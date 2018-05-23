package sun.security.jgss.krb5;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;
import sun.security.jgss.GSSToken;

abstract class MessageToken_v2
  extends Krb5Token
{
  protected static final int TOKEN_HEADER_SIZE = 16;
  private static final int TOKEN_ID_POS = 0;
  private static final int TOKEN_FLAG_POS = 2;
  private static final int TOKEN_EC_POS = 4;
  private static final int TOKEN_RRC_POS = 6;
  protected static final int CONFOUNDER_SIZE = 16;
  static final int KG_USAGE_ACCEPTOR_SEAL = 22;
  static final int KG_USAGE_ACCEPTOR_SIGN = 23;
  static final int KG_USAGE_INITIATOR_SEAL = 24;
  static final int KG_USAGE_INITIATOR_SIGN = 25;
  private static final int FLAG_SENDER_IS_ACCEPTOR = 1;
  private static final int FLAG_WRAP_CONFIDENTIAL = 2;
  private static final int FLAG_ACCEPTOR_SUBKEY = 4;
  private static final int FILLER = 255;
  private MessageTokenHeader tokenHeader = null;
  private int tokenId = 0;
  private int seqNumber;
  protected byte[] tokenData;
  protected int tokenDataLen;
  private int key_usage = 0;
  private int ec = 0;
  private int rrc = 0;
  byte[] checksum = null;
  private boolean confState = true;
  private boolean initiator = true;
  private boolean have_acceptor_subkey = false;
  CipherHelper cipherHelper = null;
  
  MessageToken_v2(int paramInt1, Krb5Context paramKrb5Context, byte[] paramArrayOfByte, int paramInt2, int paramInt3, MessageProp paramMessageProp)
    throws GSSException
  {
    this(paramInt1, paramKrb5Context, new ByteArrayInputStream(paramArrayOfByte, paramInt2, paramInt3), paramMessageProp);
  }
  
  MessageToken_v2(int paramInt, Krb5Context paramKrb5Context, InputStream paramInputStream, MessageProp paramMessageProp)
    throws GSSException
  {
    init(paramInt, paramKrb5Context);
    try
    {
      if (!confState) {
        paramMessageProp.setPrivacy(false);
      }
      tokenHeader = new MessageTokenHeader(paramInputStream, paramMessageProp, paramInt);
      if (paramInt == 1284) {
        key_usage = (!initiator ? 24 : 22);
      } else if (paramInt == 1028) {
        key_usage = (!initiator ? 25 : 23);
      }
      int i = 0;
      if ((paramInt == 1284) && (paramMessageProp.getPrivacy())) {
        i = 32 + cipherHelper.getChecksumLength();
      } else {
        i = cipherHelper.getChecksumLength();
      }
      if (paramInt == 1028)
      {
        tokenDataLen = i;
        tokenData = new byte[i];
        readFully(paramInputStream, tokenData);
      }
      else
      {
        tokenDataLen = paramInputStream.available();
        if (tokenDataLen >= i)
        {
          tokenData = new byte[tokenDataLen];
          readFully(paramInputStream, tokenData);
        }
        else
        {
          byte[] arrayOfByte = new byte[i];
          readFully(paramInputStream, arrayOfByte);
          int k = paramInputStream.available();
          tokenDataLen = (i + k);
          tokenData = Arrays.copyOf(arrayOfByte, tokenDataLen);
          readFully(paramInputStream, tokenData, i, k);
        }
      }
      if (paramInt == 1284) {
        rotate();
      }
      if ((paramInt == 1028) || ((paramInt == 1284) && (!paramMessageProp.getPrivacy())))
      {
        int j = cipherHelper.getChecksumLength();
        checksum = new byte[j];
        System.arraycopy(tokenData, tokenDataLen - j, checksum, 0, j);
        if ((paramInt == 1284) && (!paramMessageProp.getPrivacy()) && (j != ec)) {
          throw new GSSException(10, -1, getTokenName(paramInt) + ":EC incorrect!");
        }
      }
    }
    catch (IOException localIOException)
    {
      throw new GSSException(10, -1, getTokenName(paramInt) + ":" + localIOException.getMessage());
    }
  }
  
  public final int getTokenId()
  {
    return tokenId;
  }
  
  public final int getKeyUsage()
  {
    return key_usage;
  }
  
  public final boolean getConfState()
  {
    return confState;
  }
  
  public void genSignAndSeqNumber(MessageProp paramMessageProp, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
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
    tokenHeader = new MessageTokenHeader(tokenId, paramMessageProp.getPrivacy());
    if (tokenId == 1284) {
      key_usage = (initiator ? 24 : 22);
    } else if (tokenId == 1028) {
      key_usage = (initiator ? 25 : 23);
    }
    if ((tokenId == 1028) || ((!paramMessageProp.getPrivacy()) && (tokenId == 1284))) {
      checksum = getChecksum(paramArrayOfByte, paramInt1, paramInt2);
    }
    if ((!paramMessageProp.getPrivacy()) && (tokenId == 1284))
    {
      byte[] arrayOfByte = tokenHeader.getBytes();
      arrayOfByte[4] = ((byte)(checksum.length >>> 8));
      arrayOfByte[5] = ((byte)checksum.length);
    }
  }
  
  public final boolean verifySign(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws GSSException
  {
    byte[] arrayOfByte = getChecksum(paramArrayOfByte, paramInt1, paramInt2);
    return MessageDigest.isEqual(checksum, arrayOfByte);
  }
  
  private void rotate()
  {
    if (rrc % tokenDataLen != 0)
    {
      rrc %= tokenDataLen;
      byte[] arrayOfByte = new byte[tokenDataLen];
      System.arraycopy(tokenData, rrc, arrayOfByte, 0, tokenDataLen - rrc);
      System.arraycopy(tokenData, 0, arrayOfByte, tokenDataLen - rrc, rrc);
      tokenData = arrayOfByte;
    }
  }
  
  public final int getSequenceNumber()
  {
    return seqNumber;
  }
  
  byte[] getChecksum(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws GSSException
  {
    byte[] arrayOfByte = tokenHeader.getBytes();
    int i = arrayOfByte[2] & 0x2;
    if ((i == 0) && (tokenId == 1284))
    {
      arrayOfByte[4] = 0;
      arrayOfByte[5] = 0;
      arrayOfByte[6] = 0;
      arrayOfByte[7] = 0;
    }
    return cipherHelper.calculateChecksum(arrayOfByte, paramArrayOfByte, paramInt1, paramInt2, key_usage);
  }
  
  MessageToken_v2(int paramInt, Krb5Context paramKrb5Context)
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
    have_acceptor_subkey = (paramKrb5Context.getKeySrc() == 2);
    cipherHelper = paramKrb5Context.getCipherHelper(null);
  }
  
  protected void encodeHeader(OutputStream paramOutputStream)
    throws IOException
  {
    tokenHeader.encode(paramOutputStream);
  }
  
  public abstract void encode(OutputStream paramOutputStream)
    throws IOException;
  
  protected final byte[] getTokenHeader()
  {
    return tokenHeader.getBytes();
  }
  
  class MessageTokenHeader
  {
    private int tokenId;
    private byte[] bytes = new byte[16];
    
    public MessageTokenHeader(int paramInt, boolean paramBoolean)
      throws GSSException
    {
      tokenId = paramInt;
      bytes[0] = ((byte)(paramInt >>> 8));
      bytes[1] = ((byte)paramInt);
      int i = 0;
      i = (initiator ? 0 : 1) | ((paramBoolean) && (paramInt != 1028) ? 2 : 0) | (have_acceptor_subkey ? 4 : 0);
      bytes[2] = ((byte)i);
      bytes[3] = -1;
      if (paramInt == 1284)
      {
        bytes[4] = 0;
        bytes[5] = 0;
        bytes[6] = 0;
        bytes[7] = 0;
      }
      else if (paramInt == 1028)
      {
        for (int j = 4; j < 8; j++) {
          bytes[j] = -1;
        }
      }
      GSSToken.writeBigEndian(seqNumber, bytes, 12);
    }
    
    public MessageTokenHeader(InputStream paramInputStream, MessageProp paramMessageProp, int paramInt)
      throws IOException, GSSException
    {
      GSSToken.readFully(paramInputStream, bytes, 0, 16);
      tokenId = GSSToken.readInt(bytes, 0);
      if (tokenId != paramInt) {
        throw new GSSException(10, -1, Krb5Token.getTokenName(tokenId) + ":Defective Token ID!");
      }
      int i = initiator ? 1 : 0;
      int j = bytes[2] & 0x1;
      if (j != i) {
        throw new GSSException(10, -1, Krb5Token.getTokenName(tokenId) + ":Acceptor Flag Error!");
      }
      int k = bytes[2] & 0x2;
      if ((k == 2) && (tokenId == 1284)) {
        paramMessageProp.setPrivacy(true);
      } else {
        paramMessageProp.setPrivacy(false);
      }
      if (tokenId == 1284)
      {
        if ((bytes[3] & 0xFF) != 255) {
          throw new GSSException(10, -1, Krb5Token.getTokenName(tokenId) + ":Defective Token Filler!");
        }
        ec = GSSToken.readBigEndian(bytes, 4, 2);
        rrc = GSSToken.readBigEndian(bytes, 6, 2);
      }
      else if (tokenId == 1028)
      {
        for (int m = 3; m < 8; m++) {
          if ((bytes[m] & 0xFF) != 255) {
            throw new GSSException(10, -1, Krb5Token.getTokenName(tokenId) + ":Defective Token Filler!");
          }
        }
      }
      paramMessageProp.setQOP(0);
      seqNumber = GSSToken.readBigEndian(bytes, 0, 8);
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
    
    public final byte[] getBytes()
    {
      return bytes;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\krb5\MessageToken_v2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */