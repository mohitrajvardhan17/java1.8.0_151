package sun.security.jgss.krb5;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;
import sun.security.jgss.GSSHeader;
import sun.security.krb5.Confounder;

class WrapToken_v2
  extends MessageToken_v2
{
  byte[] confounder = null;
  private final boolean privacy;
  
  public WrapToken_v2(Krb5Context paramKrb5Context, byte[] paramArrayOfByte, int paramInt1, int paramInt2, MessageProp paramMessageProp)
    throws GSSException
  {
    super(1284, paramKrb5Context, paramArrayOfByte, paramInt1, paramInt2, paramMessageProp);
    privacy = paramMessageProp.getPrivacy();
  }
  
  public WrapToken_v2(Krb5Context paramKrb5Context, InputStream paramInputStream, MessageProp paramMessageProp)
    throws GSSException
  {
    super(1284, paramKrb5Context, paramInputStream, paramMessageProp);
    privacy = paramMessageProp.getPrivacy();
  }
  
  public byte[] getData()
    throws GSSException
  {
    byte[] arrayOfByte = new byte[tokenDataLen];
    int i = getData(arrayOfByte, 0);
    return Arrays.copyOf(arrayOfByte, i);
  }
  
  public int getData(byte[] paramArrayOfByte, int paramInt)
    throws GSSException
  {
    if (privacy)
    {
      cipherHelper.decryptData(this, tokenData, 0, tokenDataLen, paramArrayOfByte, paramInt, getKeyUsage());
      return tokenDataLen - 16 - 16 - cipherHelper.getChecksumLength();
    }
    int i = tokenDataLen - cipherHelper.getChecksumLength();
    System.arraycopy(tokenData, 0, paramArrayOfByte, paramInt, i);
    if (!verifySign(paramArrayOfByte, paramInt, i)) {
      throw new GSSException(6, -1, "Corrupt checksum in Wrap token");
    }
    return i;
  }
  
  public WrapToken_v2(Krb5Context paramKrb5Context, MessageProp paramMessageProp, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws GSSException
  {
    super(1284, paramKrb5Context);
    confounder = Confounder.bytes(16);
    genSignAndSeqNumber(paramMessageProp, paramArrayOfByte, paramInt1, paramInt2);
    if (!paramKrb5Context.getConfState()) {
      paramMessageProp.setPrivacy(false);
    }
    privacy = paramMessageProp.getPrivacy();
    if (!privacy)
    {
      tokenData = new byte[paramInt2 + checksum.length];
      System.arraycopy(paramArrayOfByte, paramInt1, tokenData, 0, paramInt2);
      System.arraycopy(checksum, 0, tokenData, paramInt2, checksum.length);
    }
    else
    {
      tokenData = cipherHelper.encryptData(this, confounder, getTokenHeader(), paramArrayOfByte, paramInt1, paramInt2, getKeyUsage());
    }
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException
  {
    encodeHeader(paramOutputStream);
    paramOutputStream.write(tokenData);
  }
  
  public byte[] encode()
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(16 + tokenData.length);
    encode(localByteArrayOutputStream);
    return localByteArrayOutputStream.toByteArray();
  }
  
  public int encode(byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    byte[] arrayOfByte = encode();
    System.arraycopy(arrayOfByte, 0, paramArrayOfByte, paramInt, arrayOfByte.length);
    return arrayOfByte.length;
  }
  
  static int getSizeLimit(int paramInt1, boolean paramBoolean, int paramInt2, CipherHelper paramCipherHelper)
    throws GSSException
  {
    return GSSHeader.getMaxMechTokenSize(OID, paramInt2) - (16 + paramCipherHelper.getChecksumLength() + 16) - 8;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\krb5\WrapToken_v2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */