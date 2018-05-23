package sun.security.provider.certpath;

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.x509.AuthorityKeyIdentifierExtension;
import sun.security.x509.SerialNumber;

class AdaptableX509CertSelector
  extends X509CertSelector
{
  private static final Debug debug = Debug.getInstance("certpath");
  private Date startDate;
  private Date endDate;
  private byte[] ski;
  private BigInteger serial;
  
  AdaptableX509CertSelector() {}
  
  void setValidityPeriod(Date paramDate1, Date paramDate2)
  {
    startDate = paramDate1;
    endDate = paramDate2;
  }
  
  public void setSubjectKeyIdentifier(byte[] paramArrayOfByte)
  {
    throw new IllegalArgumentException();
  }
  
  public void setSerialNumber(BigInteger paramBigInteger)
  {
    throw new IllegalArgumentException();
  }
  
  void setSkiAndSerialNumber(AuthorityKeyIdentifierExtension paramAuthorityKeyIdentifierExtension)
    throws IOException
  {
    ski = null;
    serial = null;
    if (paramAuthorityKeyIdentifierExtension != null)
    {
      ski = paramAuthorityKeyIdentifierExtension.getEncodedKeyIdentifier();
      SerialNumber localSerialNumber = (SerialNumber)paramAuthorityKeyIdentifierExtension.get("serial_number");
      if (localSerialNumber != null) {
        serial = localSerialNumber.getNumber();
      }
    }
  }
  
  public boolean match(Certificate paramCertificate)
  {
    X509Certificate localX509Certificate = (X509Certificate)paramCertificate;
    if (!matchSubjectKeyID(localX509Certificate)) {
      return false;
    }
    int i = localX509Certificate.getVersion();
    if ((serial != null) && (i > 2) && (!serial.equals(localX509Certificate.getSerialNumber()))) {
      return false;
    }
    if (i < 3)
    {
      if (startDate != null) {
        try
        {
          localX509Certificate.checkValidity(startDate);
        }
        catch (CertificateException localCertificateException1)
        {
          return false;
        }
      }
      if (endDate != null) {
        try
        {
          localX509Certificate.checkValidity(endDate);
        }
        catch (CertificateException localCertificateException2)
        {
          return false;
        }
      }
    }
    return super.match(paramCertificate);
  }
  
  private boolean matchSubjectKeyID(X509Certificate paramX509Certificate)
  {
    if (ski == null) {
      return true;
    }
    try
    {
      byte[] arrayOfByte1 = paramX509Certificate.getExtensionValue("2.5.29.14");
      if (arrayOfByte1 == null)
      {
        if (debug != null) {
          debug.println("AdaptableX509CertSelector.match: no subject key ID extension. Subject: " + paramX509Certificate.getSubjectX500Principal());
        }
        return true;
      }
      DerInputStream localDerInputStream = new DerInputStream(arrayOfByte1);
      byte[] arrayOfByte2 = localDerInputStream.getOctetString();
      if ((arrayOfByte2 == null) || (!Arrays.equals(ski, arrayOfByte2)))
      {
        if (debug != null) {
          debug.println("AdaptableX509CertSelector.match: subject key IDs don't match. Expected: " + Arrays.toString(ski) + " Cert's: " + Arrays.toString(arrayOfByte2));
        }
        return false;
      }
    }
    catch (IOException localIOException)
    {
      if (debug != null) {
        debug.println("AdaptableX509CertSelector.match: exception in subject key ID check");
      }
      return false;
    }
    return true;
  }
  
  public Object clone()
  {
    AdaptableX509CertSelector localAdaptableX509CertSelector = (AdaptableX509CertSelector)super.clone();
    if (startDate != null) {
      startDate = ((Date)startDate.clone());
    }
    if (endDate != null) {
      endDate = ((Date)endDate.clone());
    }
    if (ski != null) {
      ski = ((byte[])ski.clone());
    }
    return localAdaptableX509CertSelector;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\certpath\AdaptableX509CertSelector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */