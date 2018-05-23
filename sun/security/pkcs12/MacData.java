package sun.security.pkcs12;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import sun.security.pkcs.ParsingException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;

class MacData
{
  private String digestAlgorithmName;
  private AlgorithmParameters digestAlgorithmParams;
  private byte[] digest;
  private byte[] macSalt;
  private int iterations;
  private byte[] encoded = null;
  
  MacData(DerInputStream paramDerInputStream)
    throws IOException, ParsingException
  {
    DerValue[] arrayOfDerValue1 = paramDerInputStream.getSequence(2);
    DerInputStream localDerInputStream = new DerInputStream(arrayOfDerValue1[0].toByteArray());
    DerValue[] arrayOfDerValue2 = localDerInputStream.getSequence(2);
    AlgorithmId localAlgorithmId = AlgorithmId.parse(arrayOfDerValue2[0]);
    digestAlgorithmName = localAlgorithmId.getName();
    digestAlgorithmParams = localAlgorithmId.getParameters();
    digest = arrayOfDerValue2[1].getOctetString();
    macSalt = arrayOfDerValue1[1].getOctetString();
    if (arrayOfDerValue1.length > 2) {
      iterations = arrayOfDerValue1[2].getInteger();
    } else {
      iterations = 1;
    }
  }
  
  MacData(String paramString, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt)
    throws NoSuchAlgorithmException
  {
    if (paramString == null) {
      throw new NullPointerException("the algName parameter must be non-null");
    }
    AlgorithmId localAlgorithmId = AlgorithmId.get(paramString);
    digestAlgorithmName = localAlgorithmId.getName();
    digestAlgorithmParams = localAlgorithmId.getParameters();
    if (paramArrayOfByte1 == null) {
      throw new NullPointerException("the digest parameter must be non-null");
    }
    if (paramArrayOfByte1.length == 0) {
      throw new IllegalArgumentException("the digest parameter must not be empty");
    }
    digest = ((byte[])paramArrayOfByte1.clone());
    macSalt = paramArrayOfByte2;
    iterations = paramInt;
    encoded = null;
  }
  
  MacData(AlgorithmParameters paramAlgorithmParameters, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt)
    throws NoSuchAlgorithmException
  {
    if (paramAlgorithmParameters == null) {
      throw new NullPointerException("the algParams parameter must be non-null");
    }
    AlgorithmId localAlgorithmId = AlgorithmId.get(paramAlgorithmParameters);
    digestAlgorithmName = localAlgorithmId.getName();
    digestAlgorithmParams = localAlgorithmId.getParameters();
    if (paramArrayOfByte1 == null) {
      throw new NullPointerException("the digest parameter must be non-null");
    }
    if (paramArrayOfByte1.length == 0) {
      throw new IllegalArgumentException("the digest parameter must not be empty");
    }
    digest = ((byte[])paramArrayOfByte1.clone());
    macSalt = paramArrayOfByte2;
    iterations = paramInt;
    encoded = null;
  }
  
  String getDigestAlgName()
  {
    return digestAlgorithmName;
  }
  
  byte[] getSalt()
  {
    return macSalt;
  }
  
  int getIterations()
  {
    return iterations;
  }
  
  byte[] getDigest()
  {
    return digest;
  }
  
  public byte[] getEncoded()
    throws NoSuchAlgorithmException, IOException
  {
    if (encoded != null) {
      return (byte[])encoded.clone();
    }
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    DerOutputStream localDerOutputStream3 = new DerOutputStream();
    AlgorithmId localAlgorithmId = AlgorithmId.get(digestAlgorithmName);
    localAlgorithmId.encode(localDerOutputStream3);
    localDerOutputStream3.putOctetString(digest);
    localDerOutputStream2.write((byte)48, localDerOutputStream3);
    localDerOutputStream2.putOctetString(macSalt);
    localDerOutputStream2.putInteger(iterations);
    localDerOutputStream1.write((byte)48, localDerOutputStream2);
    encoded = localDerOutputStream1.toByteArray();
    return (byte[])encoded.clone();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\pkcs12\MacData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */