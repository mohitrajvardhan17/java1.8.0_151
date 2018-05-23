package sun.security.timestamp;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Extension;
import sun.security.util.DerOutputStream;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;

public class TSRequest
{
  private int version = 1;
  private AlgorithmId hashAlgorithmId = null;
  private byte[] hashValue;
  private String policyId = null;
  private BigInteger nonce = null;
  private boolean returnCertificate = false;
  private X509Extension[] extensions = null;
  
  public TSRequest(String paramString, byte[] paramArrayOfByte, MessageDigest paramMessageDigest)
    throws NoSuchAlgorithmException
  {
    policyId = paramString;
    hashAlgorithmId = AlgorithmId.get(paramMessageDigest.getAlgorithm());
    hashValue = paramMessageDigest.digest(paramArrayOfByte);
  }
  
  public byte[] getHashedMessage()
  {
    return (byte[])hashValue.clone();
  }
  
  public void setVersion(int paramInt)
  {
    version = paramInt;
  }
  
  public void setPolicyId(String paramString)
  {
    policyId = paramString;
  }
  
  public void setNonce(BigInteger paramBigInteger)
  {
    nonce = paramBigInteger;
  }
  
  public void requestCertificate(boolean paramBoolean)
  {
    returnCertificate = paramBoolean;
  }
  
  public void setExtensions(X509Extension[] paramArrayOfX509Extension)
  {
    extensions = paramArrayOfX509Extension;
  }
  
  public byte[] encode()
    throws IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    localDerOutputStream1.putInteger(version);
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    hashAlgorithmId.encode(localDerOutputStream2);
    localDerOutputStream2.putOctetString(hashValue);
    localDerOutputStream1.write((byte)48, localDerOutputStream2);
    if (policyId != null) {
      localDerOutputStream1.putOID(new ObjectIdentifier(policyId));
    }
    if (nonce != null) {
      localDerOutputStream1.putInteger(nonce);
    }
    if (returnCertificate) {
      localDerOutputStream1.putBoolean(true);
    }
    DerOutputStream localDerOutputStream3 = new DerOutputStream();
    localDerOutputStream3.write((byte)48, localDerOutputStream1);
    return localDerOutputStream3.toByteArray();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\timestamp\TSRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */