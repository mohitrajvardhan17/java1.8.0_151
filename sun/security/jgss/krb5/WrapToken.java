package sun.security.jgss.krb5;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;
import sun.security.jgss.GSSHeader;
import sun.security.krb5.Confounder;

class WrapToken
  extends MessageToken
{
  static final int CONFOUNDER_SIZE = 8;
  static final byte[][] pads = { null, { 1 }, { 2, 2 }, { 3, 3, 3 }, { 4, 4, 4, 4 }, { 5, 5, 5, 5, 5 }, { 6, 6, 6, 6, 6, 6 }, { 7, 7, 7, 7, 7, 7, 7 }, { 8, 8, 8, 8, 8, 8, 8, 8 } };
  private boolean readTokenFromInputStream = true;
  private InputStream is = null;
  private byte[] tokenBytes = null;
  private int tokenOffset = 0;
  private int tokenLen = 0;
  private byte[] dataBytes = null;
  private int dataOffset = 0;
  private int dataLen = 0;
  private int dataSize = 0;
  byte[] confounder = null;
  byte[] padding = null;
  private boolean privacy = false;
  
  public WrapToken(Krb5Context paramKrb5Context, byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp)
    throws GSSException
  {
    super(513, paramKrb5Context, paramArrayOfByte, paramInt1, paramInt2, paramMessageProp);
    readTokenFromInputStream = false;
    tokenBytes = paramArrayOfByte;
    tokenOffset = paramInt1;
    tokenLen = paramInt2;
    privacy = paramMessageProp.getPrivacy();
    dataSize = (getGSSHeader().getMechTokenLength() - getKrb5TokenSize());
  }
  
  public WrapToken(Krb5Context paramKrb5Context, InputStream paramInputStream, MessageProp paramMessageProp)
    throws GSSException
  {
    super(513, paramKrb5Context, paramInputStream, paramMessageProp);
    is = paramInputStream;
    privacy = paramMessageProp.getPrivacy();
    dataSize = (getGSSHeader().getMechTokenLength() - getTokenSize());
  }
  
  public byte[] getData()
    throws GSSException
  {
    byte[] arrayOfByte1 = new byte[dataSize];
    getData(arrayOfByte1, 0);
    byte[] arrayOfByte2 = new byte[dataSize - confounder.length - padding.length];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, arrayOfByte2.length);
    return arrayOfByte2;
  }
  
  public int getData(byte[] paramArrayOfByte, int paramInt)
    throws GSSException
  {
    if (readTokenFromInputStream) {
      getDataFromStream(paramArrayOfByte, paramInt);
    } else {
      getDataFromBuffer(paramArrayOfByte, paramInt);
    }
    return dataSize - confounder.length - padding.length;
  }
  
  private void getDataFromBuffer(byte[] paramArrayOfByte, int paramInt)
    throws GSSException
  {
    GSSHeader localGSSHeader = getGSSHeader();
    int i = tokenOffset + localGSSHeader.getLength() + getTokenSize();
    if (i + dataSize > tokenOffset + tokenLen) {
      throw new GSSException(10, -1, "Insufficient data in " + getTokenName(getTokenId()));
    }
    confounder = new byte[8];
    if (privacy)
    {
      cipherHelper.decryptData(this, tokenBytes, i, dataSize, paramArrayOfByte, paramInt);
    }
    else
    {
      System.arraycopy(tokenBytes, i, confounder, 0, 8);
      int j = tokenBytes[(i + dataSize - 1)];
      if (j < 0) {
        j = 0;
      }
      if (j > 8) {
        j %= 8;
      }
      padding = pads[j];
      System.arraycopy(tokenBytes, i + 8, paramArrayOfByte, paramInt, dataSize - 8 - j);
    }
    if (!verifySignAndSeqNumber(confounder, paramArrayOfByte, paramInt, dataSize - 8 - padding.length, padding)) {
      throw new GSSException(6, -1, "Corrupt checksum or sequence number in Wrap token");
    }
  }
  
  private void getDataFromStream(byte[] paramArrayOfByte, int paramInt)
    throws GSSException
  {
    GSSHeader localGSSHeader = getGSSHeader();
    confounder = new byte[8];
    try
    {
      if (privacy)
      {
        cipherHelper.decryptData(this, is, dataSize, paramArrayOfByte, paramInt);
      }
      else
      {
        readFully(is, confounder);
        if (cipherHelper.isArcFour())
        {
          padding = pads[1];
          readFully(is, paramArrayOfByte, paramInt, dataSize - 8 - 1);
        }
        else
        {
          int i = (dataSize - 8) / 8 - 1;
          int j = paramInt;
          for (int k = 0; k < i; k++)
          {
            readFully(is, paramArrayOfByte, j, 8);
            j += 8;
          }
          byte[] arrayOfByte = new byte[8];
          readFully(is, arrayOfByte);
          int m = arrayOfByte[7];
          padding = pads[m];
          System.arraycopy(arrayOfByte, 0, paramArrayOfByte, j, arrayOfByte.length - m);
        }
      }
    }
    catch (IOException localIOException)
    {
      throw new GSSException(10, -1, getTokenName(getTokenId()) + ": " + localIOException.getMessage());
    }
    if (!verifySignAndSeqNumber(confounder, paramArrayOfByte, paramInt, dataSize - 8 - padding.length, padding)) {
      throw new GSSException(6, -1, "Corrupt checksum or sequence number in Wrap token");
    }
  }
  
  private byte[] getPadding(int paramInt)
  {
    int i = 0;
    if (cipherHelper.isArcFour())
    {
      i = 1;
    }
    else
    {
      i = paramInt % 8;
      i = 8 - i;
    }
    return pads[i];
  }
  
  public WrapToken(Krb5Context paramKrb5Context, MessageProp paramMessageProp, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws GSSException
  {
    super(513, paramKrb5Context);
    confounder = Confounder.bytes(8);
    padding = getPadding(paramInt2);
    dataSize = (confounder.length + paramInt2 + padding.length);
    dataBytes = paramArrayOfByte;
    dataOffset = paramInt1;
    dataLen = paramInt2;
    genSignAndSeqNumber(paramMessageProp, confounder, paramArrayOfByte, paramInt1, paramInt2, padding);
    if (!paramKrb5Context.getConfState()) {
      paramMessageProp.setPrivacy(false);
    }
    privacy = paramMessageProp.getPrivacy();
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException, GSSException
  {
    super.encode(paramOutputStream);
    if (!privacy)
    {
      paramOutputStream.write(confounder);
      paramOutputStream.write(dataBytes, dataOffset, dataLen);
      paramOutputStream.write(padding);
    }
    else
    {
      cipherHelper.encryptData(this, confounder, dataBytes, dataOffset, dataLen, padding, paramOutputStream);
    }
  }
  
  public byte[] encode()
    throws IOException, GSSException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(dataSize + 50);
    encode(localByteArrayOutputStream);
    return localByteArrayOutputStream.toByteArray();
  }
  
  public int encode(byte[] paramArrayOfByte, int paramInt)
    throws IOException, GSSException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    super.encode(localByteArrayOutputStream);
    byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
    System.arraycopy(arrayOfByte, 0, paramArrayOfByte, paramInt, arrayOfByte.length);
    paramInt += arrayOfByte.length;
    if (!privacy)
    {
      System.arraycopy(confounder, 0, paramArrayOfByte, paramInt, confounder.length);
      paramInt += confounder.length;
      System.arraycopy(dataBytes, dataOffset, paramArrayOfByte, paramInt, dataLen);
      paramInt += dataLen;
      System.arraycopy(padding, 0, paramArrayOfByte, paramInt, padding.length);
    }
    else
    {
      cipherHelper.encryptData(this, confounder, dataBytes, dataOffset, dataLen, padding, paramArrayOfByte, paramInt);
    }
    return arrayOfByte.length + confounder.length + dataLen + padding.length;
  }
  
  protected int getKrb5TokenSize()
    throws GSSException
  {
    return getTokenSize() + dataSize;
  }
  
  protected int getSealAlg(boolean paramBoolean, int paramInt)
    throws GSSException
  {
    if (!paramBoolean) {
      return 65535;
    }
    return cipherHelper.getSealAlg();
  }
  
  static int getSizeLimit(int paramInt1, boolean paramBoolean, int paramInt2, CipherHelper paramCipherHelper)
    throws GSSException
  {
    return GSSHeader.getMaxMechTokenSize(OID, paramInt2) - (getTokenSize(paramCipherHelper) + 8) - 8;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\krb5\WrapToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */