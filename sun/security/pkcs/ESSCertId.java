package sun.security.pkcs;

import java.io.IOException;
import sun.misc.HexDumpEncoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.x509.GeneralNames;
import sun.security.x509.SerialNumber;

class ESSCertId
{
  private static volatile HexDumpEncoder hexDumper;
  private byte[] certHash;
  private GeneralNames issuer;
  private SerialNumber serialNumber;
  
  ESSCertId(DerValue paramDerValue)
    throws IOException
  {
    certHash = data.getDerValue().toByteArray();
    if (data.available() > 0)
    {
      DerValue localDerValue = data.getDerValue();
      issuer = new GeneralNames(data.getDerValue());
      serialNumber = new SerialNumber(data.getDerValue());
    }
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("[\n\tCertificate hash (SHA-1):\n");
    if (hexDumper == null) {
      hexDumper = new HexDumpEncoder();
    }
    localStringBuffer.append(hexDumper.encode(certHash));
    if ((issuer != null) && (serialNumber != null))
    {
      localStringBuffer.append("\n\tIssuer: " + issuer + "\n");
      localStringBuffer.append("\t" + serialNumber);
    }
    localStringBuffer.append("\n]");
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\pkcs\ESSCertId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */