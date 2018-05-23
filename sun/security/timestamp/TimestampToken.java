package sun.security.timestamp;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;

public class TimestampToken
{
  private int version;
  private ObjectIdentifier policy;
  private BigInteger serialNumber;
  private AlgorithmId hashAlgorithm;
  private byte[] hashedMessage;
  private Date genTime;
  private BigInteger nonce;
  
  public TimestampToken(byte[] paramArrayOfByte)
    throws IOException
  {
    if (paramArrayOfByte == null) {
      throw new IOException("No timestamp token info");
    }
    parse(paramArrayOfByte);
  }
  
  public Date getDate()
  {
    return genTime;
  }
  
  public AlgorithmId getHashAlgorithm()
  {
    return hashAlgorithm;
  }
  
  public byte[] getHashedMessage()
  {
    return hashedMessage;
  }
  
  public BigInteger getNonce()
  {
    return nonce;
  }
  
  public String getPolicyID()
  {
    return policy.toString();
  }
  
  public BigInteger getSerialNumber()
  {
    return serialNumber;
  }
  
  private void parse(byte[] paramArrayOfByte)
    throws IOException
  {
    DerValue localDerValue1 = new DerValue(paramArrayOfByte);
    if (tag != 48) {
      throw new IOException("Bad encoding for timestamp token info");
    }
    version = data.getInteger();
    policy = data.getOID();
    DerValue localDerValue2 = data.getDerValue();
    hashAlgorithm = AlgorithmId.parse(data.getDerValue());
    hashedMessage = data.getOctetString();
    serialNumber = data.getBigInteger();
    genTime = data.getGeneralizedTime();
    while (data.available() > 0)
    {
      DerValue localDerValue3 = data.getDerValue();
      if (tag == 2)
      {
        nonce = localDerValue3.getBigInteger();
        break;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\timestamp\TimestampToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */