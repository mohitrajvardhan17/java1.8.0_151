package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Enumeration;
import sun.security.util.Debug;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class CRLNumberExtension
  extends Extension
  implements CertAttrSet<String>
{
  public static final String NAME = "CRLNumber";
  public static final String NUMBER = "value";
  private static final String LABEL = "CRL Number";
  private BigInteger crlNumber = null;
  private String extensionName;
  private String extensionLabel;
  
  private void encodeThis()
    throws IOException
  {
    if (crlNumber == null)
    {
      extensionValue = null;
      return;
    }
    DerOutputStream localDerOutputStream = new DerOutputStream();
    localDerOutputStream.putInteger(crlNumber);
    extensionValue = localDerOutputStream.toByteArray();
  }
  
  public CRLNumberExtension(int paramInt)
    throws IOException
  {
    this(PKIXExtensions.CRLNumber_Id, false, BigInteger.valueOf(paramInt), "CRLNumber", "CRL Number");
  }
  
  public CRLNumberExtension(BigInteger paramBigInteger)
    throws IOException
  {
    this(PKIXExtensions.CRLNumber_Id, false, paramBigInteger, "CRLNumber", "CRL Number");
  }
  
  protected CRLNumberExtension(ObjectIdentifier paramObjectIdentifier, boolean paramBoolean, BigInteger paramBigInteger, String paramString1, String paramString2)
    throws IOException
  {
    extensionId = paramObjectIdentifier;
    critical = paramBoolean;
    crlNumber = paramBigInteger;
    extensionName = paramString1;
    extensionLabel = paramString2;
    encodeThis();
  }
  
  public CRLNumberExtension(Boolean paramBoolean, Object paramObject)
    throws IOException
  {
    this(PKIXExtensions.CRLNumber_Id, paramBoolean, paramObject, "CRLNumber", "CRL Number");
  }
  
  protected CRLNumberExtension(ObjectIdentifier paramObjectIdentifier, Boolean paramBoolean, Object paramObject, String paramString1, String paramString2)
    throws IOException
  {
    extensionId = paramObjectIdentifier;
    critical = paramBoolean.booleanValue();
    extensionValue = ((byte[])paramObject);
    DerValue localDerValue = new DerValue(extensionValue);
    crlNumber = localDerValue.getBigInteger();
    extensionName = paramString1;
    extensionLabel = paramString2;
  }
  
  public void set(String paramString, Object paramObject)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("value"))
    {
      if (!(paramObject instanceof BigInteger)) {
        throw new IOException("Attribute must be of type BigInteger.");
      }
      crlNumber = ((BigInteger)paramObject);
    }
    else
    {
      throw new IOException("Attribute name not recognized by CertAttrSet:" + extensionName + ".");
    }
    encodeThis();
  }
  
  public BigInteger get(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("value")) {
      return crlNumber;
    }
    throw new IOException("Attribute name not recognized by CertAttrSet:" + extensionName + '.');
  }
  
  public void delete(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("value")) {
      crlNumber = null;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:" + extensionName + ".");
    }
    encodeThis();
  }
  
  public String toString()
  {
    String str = super.toString() + extensionLabel + ": " + (crlNumber == null ? "" : Debug.toHexString(crlNumber)) + "\n";
    return str;
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    encode(paramOutputStream, PKIXExtensions.CRLNumber_Id, true);
  }
  
  protected void encode(OutputStream paramOutputStream, ObjectIdentifier paramObjectIdentifier, boolean paramBoolean)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    if (extensionValue == null)
    {
      extensionId = paramObjectIdentifier;
      critical = paramBoolean;
      encodeThis();
    }
    super.encode(localDerOutputStream);
    paramOutputStream.write(localDerOutputStream.toByteArray());
  }
  
  public Enumeration<String> getElements()
  {
    AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
    localAttributeNameEnumeration.addElement("value");
    return localAttributeNameEnumeration.elements();
  }
  
  public String getName()
  {
    return extensionName;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\CRLNumberExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */