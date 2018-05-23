package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class IssuingDistributionPointExtension
  extends Extension
  implements CertAttrSet<String>
{
  public static final String IDENT = "x509.info.extensions.IssuingDistributionPoint";
  public static final String NAME = "IssuingDistributionPoint";
  public static final String POINT = "point";
  public static final String REASONS = "reasons";
  public static final String ONLY_USER_CERTS = "only_user_certs";
  public static final String ONLY_CA_CERTS = "only_ca_certs";
  public static final String ONLY_ATTRIBUTE_CERTS = "only_attribute_certs";
  public static final String INDIRECT_CRL = "indirect_crl";
  private DistributionPointName distributionPoint = null;
  private ReasonFlags revocationReasons = null;
  private boolean hasOnlyUserCerts = false;
  private boolean hasOnlyCACerts = false;
  private boolean hasOnlyAttributeCerts = false;
  private boolean isIndirectCRL = false;
  private static final byte TAG_DISTRIBUTION_POINT = 0;
  private static final byte TAG_ONLY_USER_CERTS = 1;
  private static final byte TAG_ONLY_CA_CERTS = 2;
  private static final byte TAG_ONLY_SOME_REASONS = 3;
  private static final byte TAG_INDIRECT_CRL = 4;
  private static final byte TAG_ONLY_ATTRIBUTE_CERTS = 5;
  
  public IssuingDistributionPointExtension(DistributionPointName paramDistributionPointName, ReasonFlags paramReasonFlags, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
    throws IOException
  {
    if (((paramBoolean1) && ((paramBoolean2) || (paramBoolean3))) || ((paramBoolean2) && ((paramBoolean1) || (paramBoolean3))) || ((paramBoolean3) && ((paramBoolean1) || (paramBoolean2)))) {
      throw new IllegalArgumentException("Only one of hasOnlyUserCerts, hasOnlyCACerts, hasOnlyAttributeCerts may be set to true");
    }
    extensionId = PKIXExtensions.IssuingDistributionPoint_Id;
    critical = true;
    distributionPoint = paramDistributionPointName;
    revocationReasons = paramReasonFlags;
    hasOnlyUserCerts = paramBoolean1;
    hasOnlyCACerts = paramBoolean2;
    hasOnlyAttributeCerts = paramBoolean3;
    isIndirectCRL = paramBoolean4;
    encodeThis();
  }
  
  public IssuingDistributionPointExtension(Boolean paramBoolean, Object paramObject)
    throws IOException
  {
    extensionId = PKIXExtensions.IssuingDistributionPoint_Id;
    critical = paramBoolean.booleanValue();
    if (!(paramObject instanceof byte[])) {
      throw new IOException("Illegal argument type");
    }
    extensionValue = ((byte[])paramObject);
    DerValue localDerValue1 = new DerValue(extensionValue);
    if (tag != 48) {
      throw new IOException("Invalid encoding for IssuingDistributionPointExtension.");
    }
    if ((data == null) || (data.available() == 0)) {
      return;
    }
    DerInputStream localDerInputStream = data;
    while ((localDerInputStream != null) && (localDerInputStream.available() != 0))
    {
      DerValue localDerValue2 = localDerInputStream.getDerValue();
      if ((localDerValue2.isContextSpecific((byte)0)) && (localDerValue2.isConstructed()))
      {
        distributionPoint = new DistributionPointName(data.getDerValue());
      }
      else if ((localDerValue2.isContextSpecific((byte)1)) && (!localDerValue2.isConstructed()))
      {
        localDerValue2.resetTag((byte)1);
        hasOnlyUserCerts = localDerValue2.getBoolean();
      }
      else if ((localDerValue2.isContextSpecific((byte)2)) && (!localDerValue2.isConstructed()))
      {
        localDerValue2.resetTag((byte)1);
        hasOnlyCACerts = localDerValue2.getBoolean();
      }
      else if ((localDerValue2.isContextSpecific((byte)3)) && (!localDerValue2.isConstructed()))
      {
        revocationReasons = new ReasonFlags(localDerValue2);
      }
      else if ((localDerValue2.isContextSpecific((byte)4)) && (!localDerValue2.isConstructed()))
      {
        localDerValue2.resetTag((byte)1);
        isIndirectCRL = localDerValue2.getBoolean();
      }
      else if ((localDerValue2.isContextSpecific((byte)5)) && (!localDerValue2.isConstructed()))
      {
        localDerValue2.resetTag((byte)1);
        hasOnlyAttributeCerts = localDerValue2.getBoolean();
      }
      else
      {
        throw new IOException("Invalid encoding of IssuingDistributionPoint");
      }
    }
  }
  
  public String getName()
  {
    return "IssuingDistributionPoint";
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    if (extensionValue == null)
    {
      extensionId = PKIXExtensions.IssuingDistributionPoint_Id;
      critical = false;
      encodeThis();
    }
    super.encode(localDerOutputStream);
    paramOutputStream.write(localDerOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("point"))
    {
      if (!(paramObject instanceof DistributionPointName)) {
        throw new IOException("Attribute value should be of type DistributionPointName.");
      }
      distributionPoint = ((DistributionPointName)paramObject);
    }
    else if (paramString.equalsIgnoreCase("reasons"))
    {
      if (!(paramObject instanceof ReasonFlags)) {
        throw new IOException("Attribute value should be of type ReasonFlags.");
      }
      revocationReasons = ((ReasonFlags)paramObject);
    }
    else if (paramString.equalsIgnoreCase("indirect_crl"))
    {
      if (!(paramObject instanceof Boolean)) {
        throw new IOException("Attribute value should be of type Boolean.");
      }
      isIndirectCRL = ((Boolean)paramObject).booleanValue();
    }
    else if (paramString.equalsIgnoreCase("only_user_certs"))
    {
      if (!(paramObject instanceof Boolean)) {
        throw new IOException("Attribute value should be of type Boolean.");
      }
      hasOnlyUserCerts = ((Boolean)paramObject).booleanValue();
    }
    else if (paramString.equalsIgnoreCase("only_ca_certs"))
    {
      if (!(paramObject instanceof Boolean)) {
        throw new IOException("Attribute value should be of type Boolean.");
      }
      hasOnlyCACerts = ((Boolean)paramObject).booleanValue();
    }
    else if (paramString.equalsIgnoreCase("only_attribute_certs"))
    {
      if (!(paramObject instanceof Boolean)) {
        throw new IOException("Attribute value should be of type Boolean.");
      }
      hasOnlyAttributeCerts = ((Boolean)paramObject).booleanValue();
    }
    else
    {
      throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:IssuingDistributionPointExtension.");
    }
    encodeThis();
  }
  
  public Object get(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("point")) {
      return distributionPoint;
    }
    if (paramString.equalsIgnoreCase("indirect_crl")) {
      return Boolean.valueOf(isIndirectCRL);
    }
    if (paramString.equalsIgnoreCase("reasons")) {
      return revocationReasons;
    }
    if (paramString.equalsIgnoreCase("only_user_certs")) {
      return Boolean.valueOf(hasOnlyUserCerts);
    }
    if (paramString.equalsIgnoreCase("only_ca_certs")) {
      return Boolean.valueOf(hasOnlyCACerts);
    }
    if (paramString.equalsIgnoreCase("only_attribute_certs")) {
      return Boolean.valueOf(hasOnlyAttributeCerts);
    }
    throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:IssuingDistributionPointExtension.");
  }
  
  public void delete(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("point")) {
      distributionPoint = null;
    } else if (paramString.equalsIgnoreCase("indirect_crl")) {
      isIndirectCRL = false;
    } else if (paramString.equalsIgnoreCase("reasons")) {
      revocationReasons = null;
    } else if (paramString.equalsIgnoreCase("only_user_certs")) {
      hasOnlyUserCerts = false;
    } else if (paramString.equalsIgnoreCase("only_ca_certs")) {
      hasOnlyCACerts = false;
    } else if (paramString.equalsIgnoreCase("only_attribute_certs")) {
      hasOnlyAttributeCerts = false;
    } else {
      throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:IssuingDistributionPointExtension.");
    }
    encodeThis();
  }
  
  public Enumeration<String> getElements()
  {
    AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
    localAttributeNameEnumeration.addElement("point");
    localAttributeNameEnumeration.addElement("reasons");
    localAttributeNameEnumeration.addElement("only_user_certs");
    localAttributeNameEnumeration.addElement("only_ca_certs");
    localAttributeNameEnumeration.addElement("only_attribute_certs");
    localAttributeNameEnumeration.addElement("indirect_crl");
    return localAttributeNameEnumeration.elements();
  }
  
  private void encodeThis()
    throws IOException
  {
    if ((distributionPoint == null) && (revocationReasons == null) && (!hasOnlyUserCerts) && (!hasOnlyCACerts) && (!hasOnlyAttributeCerts) && (!isIndirectCRL))
    {
      extensionValue = null;
      return;
    }
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    if (distributionPoint != null)
    {
      localDerOutputStream2 = new DerOutputStream();
      distributionPoint.encode(localDerOutputStream2);
      localDerOutputStream1.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream2);
    }
    if (hasOnlyUserCerts)
    {
      localDerOutputStream2 = new DerOutputStream();
      localDerOutputStream2.putBoolean(hasOnlyUserCerts);
      localDerOutputStream1.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, false, (byte)1), localDerOutputStream2);
    }
    if (hasOnlyCACerts)
    {
      localDerOutputStream2 = new DerOutputStream();
      localDerOutputStream2.putBoolean(hasOnlyCACerts);
      localDerOutputStream1.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, false, (byte)2), localDerOutputStream2);
    }
    if (revocationReasons != null)
    {
      localDerOutputStream2 = new DerOutputStream();
      revocationReasons.encode(localDerOutputStream2);
      localDerOutputStream1.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, false, (byte)3), localDerOutputStream2);
    }
    if (isIndirectCRL)
    {
      localDerOutputStream2 = new DerOutputStream();
      localDerOutputStream2.putBoolean(isIndirectCRL);
      localDerOutputStream1.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, false, (byte)4), localDerOutputStream2);
    }
    if (hasOnlyAttributeCerts)
    {
      localDerOutputStream2 = new DerOutputStream();
      localDerOutputStream2.putBoolean(hasOnlyAttributeCerts);
      localDerOutputStream1.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, false, (byte)5), localDerOutputStream2);
    }
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write((byte)48, localDerOutputStream1);
    extensionValue = localDerOutputStream2.toByteArray();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(super.toString());
    localStringBuilder.append("IssuingDistributionPoint [\n  ");
    if (distributionPoint != null) {
      localStringBuilder.append(distributionPoint);
    }
    if (revocationReasons != null) {
      localStringBuilder.append(revocationReasons);
    }
    localStringBuilder.append(hasOnlyUserCerts ? "  Only contains user certs: true" : "  Only contains user certs: false").append("\n");
    localStringBuilder.append(hasOnlyCACerts ? "  Only contains CA certs: true" : "  Only contains CA certs: false").append("\n");
    localStringBuilder.append(hasOnlyAttributeCerts ? "  Only contains attribute certs: true" : "  Only contains attribute certs: false").append("\n");
    localStringBuilder.append(isIndirectCRL ? "  Indirect CRL: true" : "  Indirect CRL: false").append("\n");
    localStringBuilder.append("]\n");
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\IssuingDistributionPointExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */