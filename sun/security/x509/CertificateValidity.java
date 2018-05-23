package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.util.Date;
import java.util.Enumeration;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class CertificateValidity
  implements CertAttrSet<String>
{
  public static final String IDENT = "x509.info.validity";
  public static final String NAME = "validity";
  public static final String NOT_BEFORE = "notBefore";
  public static final String NOT_AFTER = "notAfter";
  private static final long YR_2050 = 2524636800000L;
  private Date notBefore;
  private Date notAfter;
  
  private Date getNotBefore()
  {
    return new Date(notBefore.getTime());
  }
  
  private Date getNotAfter()
  {
    return new Date(notAfter.getTime());
  }
  
  private void construct(DerValue paramDerValue)
    throws IOException
  {
    if (tag != 48) {
      throw new IOException("Invalid encoded CertificateValidity, starting sequence tag missing.");
    }
    if (data.available() == 0) {
      throw new IOException("No data encoded for CertificateValidity");
    }
    DerInputStream localDerInputStream = new DerInputStream(paramDerValue.toByteArray());
    DerValue[] arrayOfDerValue = localDerInputStream.getSequence(2);
    if (arrayOfDerValue.length != 2) {
      throw new IOException("Invalid encoding for CertificateValidity");
    }
    if (0tag == 23) {
      notBefore = data.getUTCTime();
    } else if (0tag == 24) {
      notBefore = data.getGeneralizedTime();
    } else {
      throw new IOException("Invalid encoding for CertificateValidity");
    }
    if (1tag == 23) {
      notAfter = data.getUTCTime();
    } else if (1tag == 24) {
      notAfter = data.getGeneralizedTime();
    } else {
      throw new IOException("Invalid encoding for CertificateValidity");
    }
  }
  
  public CertificateValidity() {}
  
  public CertificateValidity(Date paramDate1, Date paramDate2)
  {
    notBefore = paramDate1;
    notAfter = paramDate2;
  }
  
  public CertificateValidity(DerInputStream paramDerInputStream)
    throws IOException
  {
    DerValue localDerValue = paramDerInputStream.getDerValue();
    construct(localDerValue);
  }
  
  public String toString()
  {
    if ((notBefore == null) || (notAfter == null)) {
      return "";
    }
    return "Validity: [From: " + notBefore.toString() + ",\n               To: " + notAfter.toString() + "]";
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException
  {
    if ((notBefore == null) || (notAfter == null)) {
      throw new IOException("CertAttrSet:CertificateValidity: null values to encode.\n");
    }
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    if (notBefore.getTime() < 2524636800000L) {
      localDerOutputStream1.putUTCTime(notBefore);
    } else {
      localDerOutputStream1.putGeneralizedTime(notBefore);
    }
    if (notAfter.getTime() < 2524636800000L) {
      localDerOutputStream1.putUTCTime(notAfter);
    } else {
      localDerOutputStream1.putGeneralizedTime(notAfter);
    }
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write((byte)48, localDerOutputStream1);
    paramOutputStream.write(localDerOutputStream2.toByteArray());
  }
  
  public void set(String paramString, Object paramObject)
    throws IOException
  {
    if (!(paramObject instanceof Date)) {
      throw new IOException("Attribute must be of type Date.");
    }
    if (paramString.equalsIgnoreCase("notBefore")) {
      notBefore = ((Date)paramObject);
    } else if (paramString.equalsIgnoreCase("notAfter")) {
      notAfter = ((Date)paramObject);
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet: CertificateValidity.");
    }
  }
  
  public Date get(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("notBefore")) {
      return getNotBefore();
    }
    if (paramString.equalsIgnoreCase("notAfter")) {
      return getNotAfter();
    }
    throw new IOException("Attribute name not recognized by CertAttrSet: CertificateValidity.");
  }
  
  public void delete(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("notBefore")) {
      notBefore = null;
    } else if (paramString.equalsIgnoreCase("notAfter")) {
      notAfter = null;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet: CertificateValidity.");
    }
  }
  
  public Enumeration<String> getElements()
  {
    AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
    localAttributeNameEnumeration.addElement("notBefore");
    localAttributeNameEnumeration.addElement("notAfter");
    return localAttributeNameEnumeration.elements();
  }
  
  public String getName()
  {
    return "validity";
  }
  
  public void valid()
    throws CertificateNotYetValidException, CertificateExpiredException
  {
    Date localDate = new Date();
    valid(localDate);
  }
  
  public void valid(Date paramDate)
    throws CertificateNotYetValidException, CertificateExpiredException
  {
    if (notBefore.after(paramDate)) {
      throw new CertificateNotYetValidException("NotBefore: " + notBefore.toString());
    }
    if (notAfter.before(paramDate)) {
      throw new CertificateExpiredException("NotAfter: " + notAfter.toString());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\CertificateValidity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */