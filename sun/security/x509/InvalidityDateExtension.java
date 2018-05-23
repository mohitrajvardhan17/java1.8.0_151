package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class InvalidityDateExtension
  extends Extension
  implements CertAttrSet<String>
{
  public static final String NAME = "InvalidityDate";
  public static final String DATE = "date";
  private Date date;
  
  private void encodeThis()
    throws IOException
  {
    if (date == null)
    {
      extensionValue = null;
      return;
    }
    DerOutputStream localDerOutputStream = new DerOutputStream();
    localDerOutputStream.putGeneralizedTime(date);
    extensionValue = localDerOutputStream.toByteArray();
  }
  
  public InvalidityDateExtension(Date paramDate)
    throws IOException
  {
    this(false, paramDate);
  }
  
  public InvalidityDateExtension(boolean paramBoolean, Date paramDate)
    throws IOException
  {
    extensionId = PKIXExtensions.InvalidityDate_Id;
    critical = paramBoolean;
    date = paramDate;
    encodeThis();
  }
  
  public InvalidityDateExtension(Boolean paramBoolean, Object paramObject)
    throws IOException
  {
    extensionId = PKIXExtensions.InvalidityDate_Id;
    critical = paramBoolean.booleanValue();
    extensionValue = ((byte[])paramObject);
    DerValue localDerValue = new DerValue(extensionValue);
    date = localDerValue.getGeneralizedTime();
  }
  
  public void set(String paramString, Object paramObject)
    throws IOException
  {
    if (!(paramObject instanceof Date)) {
      throw new IOException("Attribute must be of type Date.");
    }
    if (paramString.equalsIgnoreCase("date")) {
      date = ((Date)paramObject);
    } else {
      throw new IOException("Name not supported by InvalidityDateExtension");
    }
    encodeThis();
  }
  
  public Date get(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("date"))
    {
      if (date == null) {
        return null;
      }
      return new Date(date.getTime());
    }
    throw new IOException("Name not supported by InvalidityDateExtension");
  }
  
  public void delete(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("date")) {
      date = null;
    } else {
      throw new IOException("Name not supported by InvalidityDateExtension");
    }
    encodeThis();
  }
  
  public String toString()
  {
    return super.toString() + "    Invalidity Date: " + String.valueOf(date);
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    if (extensionValue == null)
    {
      extensionId = PKIXExtensions.InvalidityDate_Id;
      critical = false;
      encodeThis();
    }
    super.encode(localDerOutputStream);
    paramOutputStream.write(localDerOutputStream.toByteArray());
  }
  
  public Enumeration<String> getElements()
  {
    AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
    localAttributeNameEnumeration.addElement("date");
    return localAttributeNameEnumeration.elements();
  }
  
  public String getName()
  {
    return "InvalidityDate";
  }
  
  public static InvalidityDateExtension toImpl(java.security.cert.Extension paramExtension)
    throws IOException
  {
    if ((paramExtension instanceof InvalidityDateExtension)) {
      return (InvalidityDateExtension)paramExtension;
    }
    return new InvalidityDateExtension(Boolean.valueOf(paramExtension.isCritical()), paramExtension.getValue());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\InvalidityDateExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */