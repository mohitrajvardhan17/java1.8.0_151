package sun.security.pkcs;

import java.io.IOException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;

public class EncryptedPrivateKeyInfo
{
  private AlgorithmId algid;
  private byte[] encryptedData;
  private byte[] encoded;
  
  public EncryptedPrivateKeyInfo(byte[] paramArrayOfByte)
    throws IOException
  {
    if (paramArrayOfByte == null) {
      throw new IllegalArgumentException("encoding must not be null");
    }
    DerValue localDerValue = new DerValue(paramArrayOfByte);
    DerValue[] arrayOfDerValue = new DerValue[2];
    arrayOfDerValue[0] = data.getDerValue();
    arrayOfDerValue[1] = data.getDerValue();
    if (data.available() != 0) {
      throw new IOException("overrun, bytes = " + data.available());
    }
    algid = AlgorithmId.parse(arrayOfDerValue[0]);
    if (0data.available() != 0) {
      throw new IOException("encryptionAlgorithm field overrun");
    }
    encryptedData = arrayOfDerValue[1].getOctetString();
    if (1data.available() != 0) {
      throw new IOException("encryptedData field overrun");
    }
    encoded = ((byte[])paramArrayOfByte.clone());
  }
  
  public EncryptedPrivateKeyInfo(AlgorithmId paramAlgorithmId, byte[] paramArrayOfByte)
  {
    algid = paramAlgorithmId;
    encryptedData = ((byte[])paramArrayOfByte.clone());
  }
  
  public AlgorithmId getAlgorithm()
  {
    return algid;
  }
  
  public byte[] getEncryptedData()
  {
    return (byte[])encryptedData.clone();
  }
  
  public byte[] getEncoded()
    throws IOException
  {
    if (encoded != null) {
      return (byte[])encoded.clone();
    }
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    algid.encode(localDerOutputStream2);
    localDerOutputStream2.putOctetString(encryptedData);
    localDerOutputStream1.write((byte)48, localDerOutputStream2);
    encoded = localDerOutputStream1.toByteArray();
    return (byte[])encoded.clone();
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof EncryptedPrivateKeyInfo)) {
      return false;
    }
    try
    {
      byte[] arrayOfByte1 = getEncoded();
      byte[] arrayOfByte2 = ((EncryptedPrivateKeyInfo)paramObject).getEncoded();
      if (arrayOfByte1.length != arrayOfByte2.length) {
        return false;
      }
      for (int i = 0; i < arrayOfByte1.length; i++) {
        if (arrayOfByte1[i] != arrayOfByte2[i]) {
          return false;
        }
      }
      return true;
    }
    catch (IOException localIOException) {}
    return false;
  }
  
  public int hashCode()
  {
    int i = 0;
    for (int j = 0; j < encryptedData.length; j++) {
      i += encryptedData[j] * j;
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\pkcs\EncryptedPrivateKeyInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */