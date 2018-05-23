package sun.security.jgss.krb5;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;
import sun.security.jgss.GSSHeader;
import sun.security.jgss.GSSToken;
import sun.security.util.ObjectIdentifier;

abstract class MessageToken
  extends Krb5Token
{
  private static final int TOKEN_NO_CKSUM_SIZE = 16;
  private static final int FILLER = 65535;
  static final int SGN_ALG_DES_MAC_MD5 = 0;
  static final int SGN_ALG_DES_MAC = 512;
  static final int SGN_ALG_HMAC_SHA1_DES3_KD = 1024;
  static final int SEAL_ALG_NONE = 65535;
  static final int SEAL_ALG_DES = 0;
  static final int SEAL_ALG_DES3_KD = 512;
  static final int SEAL_ALG_ARCFOUR_HMAC = 4096;
  static final int SGN_ALG_HMAC_MD5_ARCFOUR = 4352;
  private static final int TOKEN_ID_POS = 0;
  private static final int SIGN_ALG_POS = 2;
  private static final int SEAL_ALG_POS = 4;
  private int seqNumber;
  private boolean confState = true;
  private boolean initiator = true;
  private int tokenId = 0;
  private GSSHeader gssHeader = null;
  private MessageTokenHeader tokenHeader = null;
  private byte[] checksum = null;
  private byte[] encSeqNumber = null;
  private byte[] seqNumberData = null;
  CipherHelper cipherHelper = null;
  
  MessageToken(int paramInt1, Krb5Context paramKrb5Context, byte[] paramArrayOfByte, int paramInt2, int paramInt3, MessageProp paramMessageProp)
    throws GSSException
  {
    this(paramInt1, paramKrb5Context, new ByteArrayInputStream(paramArrayOfByte, paramInt2, paramInt3), paramMessageProp);
  }
  
  MessageToken(int paramInt, Krb5Context paramKrb5Context, InputStream paramInputStream, MessageProp paramMessageProp)
    throws GSSException
  {
    init(paramInt, paramKrb5Context);
    try
    {
      gssHeader = new GSSHeader(paramInputStream);
      if (!gssHeader.getOid().equals(OID)) {
        throw new GSSException(10, -1, getTokenName(paramInt));
      }
      if (!confState) {
        paramMessageProp.setPrivacy(false);
      }
      tokenHeader = new MessageTokenHeader(paramInputStream, paramMessageProp);
      encSeqNumber = new byte[8];
      readFully(paramInputStream, encSeqNumber);
      checksum = new byte[cipherHelper.getChecksumLength()];
      readFully(paramInputStream, checksum);
    }
    catch (IOException localIOException)
    {
      throw new GSSException(10, -1, getTokenName(paramInt) + ":" + localIOException.getMessage());
    }
  }
  
  public final GSSHeader getGSSHeader()
  {
    return gssHeader;
  }
  
  public final int getTokenId()
  {
    return tokenId;
  }
  
  public final byte[] getEncSeqNumber()
  {
    return encSeqNumber;
  }
  
  public final byte[] getChecksum()
  {
    return checksum;
  }
  
  public final boolean getConfState()
  {
    return confState;
  }
  
  public void genSignAndSeqNumber(MessageProp paramMessageProp, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, byte[] paramArrayOfByte3)
    throws GSSException
  {
    int i = paramMessageProp.getQOP();
    if (i != 0)
    {
      i = 0;
      paramMessageProp.setQOP(i);
    }
    if (!confState) {
      paramMessageProp.setPrivacy(false);
    }
    tokenHeader = new MessageTokenHeader(tokenId, paramMessageProp.getPrivacy(), i);
    checksum = getChecksum(paramArrayOfByte1, paramArrayOfByte2, paramInt1, paramInt2, paramArrayOfByte3);
    seqNumberData = new byte[8];
    if (cipherHelper.isArcFour()) {
      writeBigEndian(seqNumber, seqNumberData);
    } else {
      writeLittleEndian(seqNumber, seqNumberData);
    }
    if (!initiator)
    {
      seqNumberData[4] = -1;
      seqNumberData[5] = -1;
      seqNumberData[6] = -1;
      seqNumberData[7] = -1;
    }
    encSeqNumber = cipherHelper.encryptSeq(checksum, seqNumberData, 0, 8);
  }
  
  public final boolean verifySignAndSeqNumber(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, byte[] paramArrayOfByte3)
    throws GSSException
  {
    byte[] arrayOfByte = getChecksum(paramArrayOfByte1, paramArrayOfByte2, paramInt1, paramInt2, paramArrayOfByte3);
    if (MessageDigest.isEqual(checksum, arrayOfByte))
    {
      seqNumberData = cipherHelper.decryptSeq(checksum, encSeqNumber, 0, 8);
      int i = 0;
      if (initiator) {
        i = -1;
      }
      if ((seqNumberData[4] == i) && (seqNumberData[5] == i) && (seqNumberData[6] == i) && (seqNumberData[7] == i)) {
        return true;
      }
    }
    return false;
  }
  
  public final int getSequenceNumber()
  {
    int i = 0;
    if (cipherHelper.isArcFour()) {
      i = readBigEndian(seqNumberData, 0, 4);
    } else {
      i = readLittleEndian(seqNumberData, 0, 4);
    }
    return i;
  }
  
  private byte[] getChecksum(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, byte[] paramArrayOfByte3)
    throws GSSException
  {
    byte[] arrayOfByte1 = tokenHeader.getBytes();
    byte[] arrayOfByte2 = paramArrayOfByte1;
    byte[] arrayOfByte3 = arrayOfByte1;
    if (arrayOfByte2 != null)
    {
      arrayOfByte3 = new byte[arrayOfByte1.length + arrayOfByte2.length];
      System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 0, arrayOfByte1.length);
      System.arraycopy(arrayOfByte2, 0, arrayOfByte3, arrayOfByte1.length, arrayOfByte2.length);
    }
    return cipherHelper.calculateChecksum(tokenHeader.getSignAlg(), arrayOfByte3, paramArrayOfByte3, paramArrayOfByte2, paramInt1, paramInt2, tokenId);
  }
  
  MessageToken(int paramInt, Krb5Context paramKrb5Context)
    throws GSSException
  {
    init(paramInt, paramKrb5Context);
    seqNumber = paramKrb5Context.incrementMySequenceNumber();
  }
  
  private void init(int paramInt, Krb5Context paramKrb5Context)
    throws GSSException
  {
    tokenId = paramInt;
    confState = paramKrb5Context.getConfState();
    initiator = paramKrb5Context.isInitiator();
    cipherHelper = paramKrb5Context.getCipherHelper(null);
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException, GSSException
  {
    gssHeader = new GSSHeader(OID, getKrb5TokenSize());
    gssHeader.encode(paramOutputStream);
    tokenHeader.encode(paramOutputStream);
    paramOutputStream.write(encSeqNumber);
    paramOutputStream.write(checksum);
  }
  
  protected int getKrb5TokenSize()
    throws GSSException
  {
    return getTokenSize();
  }
  
  protected final int getTokenSize()
    throws GSSException
  {
    return 16 + cipherHelper.getChecksumLength();
  }
  
  protected static final int getTokenSize(CipherHelper paramCipherHelper)
    throws GSSException
  {
    return 16 + paramCipherHelper.getChecksumLength();
  }
  
  protected abstract int getSealAlg(boolean paramBoolean, int paramInt)
    throws GSSException;
  
  protected int getSgnAlg(int paramInt)
    throws GSSException
  {
    return cipherHelper.getSgnAlg();
  }
  
  class MessageTokenHeader
  {
    private int tokenId;
    private int signAlg;
    private int sealAlg;
    private byte[] bytes = new byte[8];
    
    public MessageTokenHeader(int paramInt1, boolean paramBoolean, int paramInt2)
      throws GSSException
    {
      tokenId = paramInt1;
      signAlg = getSgnAlg(paramInt2);
      sealAlg = getSealAlg(paramBoolean, paramInt2);
      bytes[0] = ((byte)(paramInt1 >>> 8));
      bytes[1] = ((byte)paramInt1);
      bytes[2] = ((byte)(signAlg >>> 8));
      bytes[3] = ((byte)signAlg);
      bytes[4] = ((byte)(sealAlg >>> 8));
      bytes[5] = ((byte)sealAlg);
      bytes[6] = -1;
      bytes[7] = -1;
    }
    
    public MessageTokenHeader(InputStream paramInputStream, MessageProp paramMessageProp)
      throws IOException
    {
      GSSToken.readFully(paramInputStream, bytes);
      tokenId = GSSToken.readInt(bytes, 0);
      signAlg = GSSToken.readInt(bytes, 2);
      sealAlg = GSSToken.readInt(bytes, 4);
      int i = GSSToken.readInt(bytes, 6);
      switch (sealAlg)
      {
      case 0: 
      case 512: 
      case 4096: 
        paramMessageProp.setPrivacy(true);
        break;
      default: 
        paramMessageProp.setPrivacy(false);
      }
      paramMessageProp.setQOP(0);
    }
    
    public final void encode(OutputStream paramOutputStream)
      throws IOException
    {
      paramOutputStream.write(bytes);
    }
    
    public final int getTokenId()
    {
      return tokenId;
    }
    
    public final int getSignAlg()
    {
      return signAlg;
    }
    
    public final int getSealAlg()
    {
      return sealAlg;
    }
    
    public final byte[] getBytes()
    {
      return bytes;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\krb5\MessageToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */