package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateParsingException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Objects;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class PrivateKeyUsageExtension
  extends Extension
  implements CertAttrSet<String>
{
  public static final String IDENT = "x509.info.extensions.PrivateKeyUsage";
  public static final String NAME = "PrivateKeyUsage";
  public static final String NOT_BEFORE = "not_before";
  public static final String NOT_AFTER = "not_after";
  private static final byte TAG_BEFORE = 0;
  private static final byte TAG_AFTER = 1;
  private Date notBefore = null;
  private Date notAfter = null;
  
  private void encodeThis()
    throws IOException
  {
    if ((notBefore == null) && (notAfter == null))
    {
      extensionValue = null;
      return;
    }
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    DerOutputStream localDerOutputStream3;
    if (notBefore != null)
    {
      localDerOutputStream3 = new DerOutputStream();
      localDerOutputStream3.putGeneralizedTime(notBefore);
      localDerOutputStream2.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, false, (byte)0), localDerOutputStream3);
    }
    if (notAfter != null)
    {
      localDerOutputStream3 = new DerOutputStream();
      localDerOutputStream3.putGeneralizedTime(notAfter);
      localDerOutputStream2.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, false, (byte)1), localDerOutputStream3);
    }
    localDerOutputStream1.write((byte)48, localDerOutputStream2);
    extensionValue = localDerOutputStream1.toByteArray();
  }
  
  public PrivateKeyUsageExtension(Date paramDate1, Date paramDate2)
    throws IOException
  {
    notBefore = paramDate1;
    notAfter = paramDate2;
    extensionId = PKIXExtensions.PrivateKeyUsage_Id;
    critical = false;
    encodeThis();
  }
  
  public PrivateKeyUsageExtension(Boolean paramBoolean, Object paramObject)
    throws CertificateException, IOException
  {
    extensionId = PKIXExtensions.PrivateKeyUsage_Id;
    critical = paramBoolean.booleanValue();
    extensionValue = ((byte[])paramObject);
    DerInputStream localDerInputStream = new DerInputStream(extensionValue);
    DerValue[] arrayOfDerValue = localDerInputStream.getSequence(2);
    for (int i = 0; i < arrayOfDerValue.length; i++)
    {
      DerValue localDerValue = arrayOfDerValue[i];
      if ((localDerValue.isContextSpecific((byte)0)) && (!localDerValue.isConstructed()))
      {
        if (notBefore != null) {
          throw new CertificateParsingException("Duplicate notBefore in PrivateKeyUsage.");
        }
        localDerValue.resetTag((byte)24);
        localDerInputStream = new DerInputStream(localDerValue.toByteArray());
        notBefore = localDerInputStream.getGeneralizedTime();
      }
      else if ((localDerValue.isContextSpecific((byte)1)) && (!localDerValue.isConstructed()))
      {
        if (notAfter != null) {
          throw new CertificateParsingException("Duplicate notAfter in PrivateKeyUsage.");
        }
        localDerValue.resetTag((byte)24);
        localDerInputStream = new DerInputStream(localDerValue.toByteArray());
        notAfter = localDerInputStream.getGeneralizedTime();
      }
      else
      {
        throw new IOException("Invalid encoding of PrivateKeyUsageExtension");
      }
    }
  }
  
  public String toString()
  {
    return super.toString() + "PrivateKeyUsage: [\n" + (notBefore == null ? "" : new StringBuilder().append("From: ").append(notBefore.toString()).append(", ").toString()) + (notAfter == null ? "" : new StringBuilder().append("To: ").append(notAfter.toString()).toString()) + "]\n";
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
    Objects.requireNonNull(paramDate);
    if ((notBefore != null) && (notBefore.after(paramDate))) {
      throw new CertificateNotYetValidException("NotBefore: " + notBefore.toString());
    }
    if ((notAfter != null) && (notAfter.before(paramDate))) {
      throw new CertificateExpiredException("NotAfter: " + notAfter.toString());
    }
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    if (extensionValue == null)
    {
      extensionId = PKIXExtensions.PrivateKeyUsage_Id;
      critical = false;
      encodeThis();
    }
    super.encode(localDerOutputStream);
    paramOutputStream.write(localDerOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject)
    throws CertificateException, IOException
  {
    if (!(paramObject instanceof Date)) {
      throw new CertificateException("Attribute must be of type Date.");
    }
    if (paramString.equalsIgnoreCase("not_before")) {
      notBefore = ((Date)paramObject);
    } else if (paramString.equalsIgnoreCase("not_after")) {
      notAfter = ((Date)paramObject);
    } else {
      throw new CertificateException("Attribute name not recognized by CertAttrSet:PrivateKeyUsage.");
    }
    encodeThis();
  }
  
  public Date get(String paramString)
    throws CertificateException
  {
    if (paramString.equalsIgnoreCase("not_before")) {
      return new Date(notBefore.getTime());
    }
    if (paramString.equalsIgnoreCase("not_after")) {
      return new Date(notAfter.getTime());
    }
    throw new CertificateException("Attribute name not recognized by CertAttrSet:PrivateKeyUsage.");
  }
  
  public void delete(String paramString)
    throws CertificateException, IOException
  {
    if (paramString.equalsIgnoreCase("not_before")) {
      notBefore = null;
    } else if (paramString.equalsIgnoreCase("not_after")) {
      notAfter = null;
    } else {
      throw new CertificateException("Attribute name not recognized by CertAttrSet:PrivateKeyUsage.");
    }
    encodeThis();
  }
  
  public Enumeration<String> getElements()
  {
    AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
    localAttributeNameEnumeration.addElement("not_before");
    localAttributeNameEnumeration.addElement("not_after");
    return localAttributeNameEnumeration.elements();
  }
  
  public String getName()
  {
    return "PrivateKeyUsage";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\PrivateKeyUsageExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */