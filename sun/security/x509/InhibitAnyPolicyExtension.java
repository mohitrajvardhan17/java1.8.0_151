package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import sun.security.util.Debug;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class InhibitAnyPolicyExtension
  extends Extension
  implements CertAttrSet<String>
{
  private static final Debug debug = Debug.getInstance("certpath");
  public static final String IDENT = "x509.info.extensions.InhibitAnyPolicy";
  public static ObjectIdentifier AnyPolicy_Id;
  public static final String NAME = "InhibitAnyPolicy";
  public static final String SKIP_CERTS = "skip_certs";
  private int skipCerts = Integer.MAX_VALUE;
  
  private void encodeThis()
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    localDerOutputStream.putInteger(skipCerts);
    extensionValue = localDerOutputStream.toByteArray();
  }
  
  public InhibitAnyPolicyExtension(int paramInt)
    throws IOException
  {
    if (paramInt < -1) {
      throw new IOException("Invalid value for skipCerts");
    }
    if (paramInt == -1) {
      skipCerts = Integer.MAX_VALUE;
    } else {
      skipCerts = paramInt;
    }
    extensionId = PKIXExtensions.InhibitAnyPolicy_Id;
    critical = true;
    encodeThis();
  }
  
  public InhibitAnyPolicyExtension(Boolean paramBoolean, Object paramObject)
    throws IOException
  {
    extensionId = PKIXExtensions.InhibitAnyPolicy_Id;
    if (!paramBoolean.booleanValue()) {
      throw new IOException("Criticality cannot be false for InhibitAnyPolicy");
    }
    critical = paramBoolean.booleanValue();
    extensionValue = ((byte[])paramObject);
    DerValue localDerValue = new DerValue(extensionValue);
    if (tag != 2) {
      throw new IOException("Invalid encoding of InhibitAnyPolicy: data not integer");
    }
    if (data == null) {
      throw new IOException("Invalid encoding of InhibitAnyPolicy: null data");
    }
    int i = localDerValue.getInteger();
    if (i < -1) {
      throw new IOException("Invalid value for skipCerts");
    }
    if (i == -1) {
      skipCerts = Integer.MAX_VALUE;
    } else {
      skipCerts = i;
    }
  }
  
  public String toString()
  {
    String str = super.toString() + "InhibitAnyPolicy: " + skipCerts + "\n";
    return str;
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    if (extensionValue == null)
    {
      extensionId = PKIXExtensions.InhibitAnyPolicy_Id;
      critical = true;
      encodeThis();
    }
    super.encode(localDerOutputStream);
    paramOutputStream.write(localDerOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("skip_certs"))
    {
      if (!(paramObject instanceof Integer)) {
        throw new IOException("Attribute value should be of type Integer.");
      }
      int i = ((Integer)paramObject).intValue();
      if (i < -1) {
        throw new IOException("Invalid value for skipCerts");
      }
      if (i == -1) {
        skipCerts = Integer.MAX_VALUE;
      } else {
        skipCerts = i;
      }
    }
    else
    {
      throw new IOException("Attribute name not recognized by CertAttrSet:InhibitAnyPolicy.");
    }
    encodeThis();
  }
  
  public Integer get(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("skip_certs")) {
      return new Integer(skipCerts);
    }
    throw new IOException("Attribute name not recognized by CertAttrSet:InhibitAnyPolicy.");
  }
  
  public void delete(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("skip_certs")) {
      throw new IOException("Attribute skip_certs may not be deleted.");
    }
    throw new IOException("Attribute name not recognized by CertAttrSet:InhibitAnyPolicy.");
  }
  
  public Enumeration<String> getElements()
  {
    AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
    localAttributeNameEnumeration.addElement("skip_certs");
    return localAttributeNameEnumeration.elements();
  }
  
  public String getName()
  {
    return "InhibitAnyPolicy";
  }
  
  static
  {
    try
    {
      AnyPolicy_Id = new ObjectIdentifier("2.5.29.32.0");
    }
    catch (IOException localIOException) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\InhibitAnyPolicyExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */