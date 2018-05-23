package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class PolicyConstraintsExtension
  extends Extension
  implements CertAttrSet<String>
{
  public static final String IDENT = "x509.info.extensions.PolicyConstraints";
  public static final String NAME = "PolicyConstraints";
  public static final String REQUIRE = "require";
  public static final String INHIBIT = "inhibit";
  private static final byte TAG_REQUIRE = 0;
  private static final byte TAG_INHIBIT = 1;
  private int require = -1;
  private int inhibit = -1;
  
  private void encodeThis()
    throws IOException
  {
    if ((require == -1) && (inhibit == -1))
    {
      extensionValue = null;
      return;
    }
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    DerOutputStream localDerOutputStream3;
    if (require != -1)
    {
      localDerOutputStream3 = new DerOutputStream();
      localDerOutputStream3.putInteger(require);
      localDerOutputStream1.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, false, (byte)0), localDerOutputStream3);
    }
    if (inhibit != -1)
    {
      localDerOutputStream3 = new DerOutputStream();
      localDerOutputStream3.putInteger(inhibit);
      localDerOutputStream1.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, false, (byte)1), localDerOutputStream3);
    }
    localDerOutputStream2.write((byte)48, localDerOutputStream1);
    extensionValue = localDerOutputStream2.toByteArray();
  }
  
  public PolicyConstraintsExtension(int paramInt1, int paramInt2)
    throws IOException
  {
    this(Boolean.FALSE, paramInt1, paramInt2);
  }
  
  public PolicyConstraintsExtension(Boolean paramBoolean, int paramInt1, int paramInt2)
    throws IOException
  {
    require = paramInt1;
    inhibit = paramInt2;
    extensionId = PKIXExtensions.PolicyConstraints_Id;
    critical = paramBoolean.booleanValue();
    encodeThis();
  }
  
  public PolicyConstraintsExtension(Boolean paramBoolean, Object paramObject)
    throws IOException
  {
    extensionId = PKIXExtensions.PolicyConstraints_Id;
    critical = paramBoolean.booleanValue();
    extensionValue = ((byte[])paramObject);
    DerValue localDerValue1 = new DerValue(extensionValue);
    if (tag != 48) {
      throw new IOException("Sequence tag missing for PolicyConstraint.");
    }
    DerInputStream localDerInputStream = data;
    while ((localDerInputStream != null) && (localDerInputStream.available() != 0))
    {
      DerValue localDerValue2 = localDerInputStream.getDerValue();
      if ((localDerValue2.isContextSpecific((byte)0)) && (!localDerValue2.isConstructed()))
      {
        if (require != -1) {
          throw new IOException("Duplicate requireExplicitPolicyfound in the PolicyConstraintsExtension");
        }
        localDerValue2.resetTag((byte)2);
        require = localDerValue2.getInteger();
      }
      else if ((localDerValue2.isContextSpecific((byte)1)) && (!localDerValue2.isConstructed()))
      {
        if (inhibit != -1) {
          throw new IOException("Duplicate inhibitPolicyMappingfound in the PolicyConstraintsExtension");
        }
        localDerValue2.resetTag((byte)2);
        inhibit = localDerValue2.getInteger();
      }
      else
      {
        throw new IOException("Invalid encoding of PolicyConstraint");
      }
    }
  }
  
  public String toString()
  {
    String str = super.toString() + "PolicyConstraints: [  Require: ";
    if (require == -1) {
      str = str + "unspecified;";
    } else {
      str = str + require + ";";
    }
    str = str + "\tInhibit: ";
    if (inhibit == -1) {
      str = str + "unspecified";
    } else {
      str = str + inhibit;
    }
    str = str + " ]\n";
    return str;
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    if (extensionValue == null)
    {
      extensionId = PKIXExtensions.PolicyConstraints_Id;
      critical = false;
      encodeThis();
    }
    super.encode(localDerOutputStream);
    paramOutputStream.write(localDerOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject)
    throws IOException
  {
    if (!(paramObject instanceof Integer)) {
      throw new IOException("Attribute value should be of type Integer.");
    }
    if (paramString.equalsIgnoreCase("require")) {
      require = ((Integer)paramObject).intValue();
    } else if (paramString.equalsIgnoreCase("inhibit")) {
      inhibit = ((Integer)paramObject).intValue();
    } else {
      throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:PolicyConstraints.");
    }
    encodeThis();
  }
  
  public Integer get(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("require")) {
      return new Integer(require);
    }
    if (paramString.equalsIgnoreCase("inhibit")) {
      return new Integer(inhibit);
    }
    throw new IOException("Attribute name not recognized by CertAttrSet:PolicyConstraints.");
  }
  
  public void delete(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("require")) {
      require = -1;
    } else if (paramString.equalsIgnoreCase("inhibit")) {
      inhibit = -1;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:PolicyConstraints.");
    }
    encodeThis();
  }
  
  public Enumeration<String> getElements()
  {
    AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
    localAttributeNameEnumeration.addElement("require");
    localAttributeNameEnumeration.addElement("inhibit");
    return localAttributeNameEnumeration.elements();
  }
  
  public String getName()
  {
    return "PolicyConstraints";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\PolicyConstraintsExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */