package sun.security.x509;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class SerialNumber
{
  private BigInteger serialNum;
  
  private void construct(DerValue paramDerValue)
    throws IOException
  {
    serialNum = paramDerValue.getBigInteger();
    if (data.available() != 0) {
      throw new IOException("Excess SerialNumber data");
    }
  }
  
  public SerialNumber(BigInteger paramBigInteger)
  {
    serialNum = paramBigInteger;
  }
  
  public SerialNumber(int paramInt)
  {
    serialNum = BigInteger.valueOf(paramInt);
  }
  
  public SerialNumber(DerInputStream paramDerInputStream)
    throws IOException
  {
    DerValue localDerValue = paramDerInputStream.getDerValue();
    construct(localDerValue);
  }
  
  public SerialNumber(DerValue paramDerValue)
    throws IOException
  {
    construct(paramDerValue);
  }
  
  public SerialNumber(InputStream paramInputStream)
    throws IOException
  {
    DerValue localDerValue = new DerValue(paramInputStream);
    construct(localDerValue);
  }
  
  public String toString()
  {
    return "SerialNumber: [" + Debug.toHexString(serialNum) + "]";
  }
  
  public void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    paramDerOutputStream.putInteger(serialNum);
  }
  
  public BigInteger getNumber()
  {
    return serialNum;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\SerialNumber.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */