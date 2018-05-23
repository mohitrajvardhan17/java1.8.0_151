package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.security.auth.x500.X500Principal;
import sun.net.util.IPAddressUtil;
import sun.security.pkcs.PKCS9Attribute;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class NameConstraintsExtension
  extends Extension
  implements CertAttrSet<String>, Cloneable
{
  public static final String IDENT = "x509.info.extensions.NameConstraints";
  public static final String NAME = "NameConstraints";
  public static final String PERMITTED_SUBTREES = "permitted_subtrees";
  public static final String EXCLUDED_SUBTREES = "excluded_subtrees";
  private static final byte TAG_PERMITTED = 0;
  private static final byte TAG_EXCLUDED = 1;
  private GeneralSubtrees permitted = null;
  private GeneralSubtrees excluded = null;
  private boolean hasMin;
  private boolean hasMax;
  private boolean minMaxValid = false;
  
  private void calcMinMax()
    throws IOException
  {
    hasMin = false;
    hasMax = false;
    int i;
    GeneralSubtree localGeneralSubtree;
    if (excluded != null) {
      for (i = 0; i < excluded.size(); i++)
      {
        localGeneralSubtree = excluded.get(i);
        if (localGeneralSubtree.getMinimum() != 0) {
          hasMin = true;
        }
        if (localGeneralSubtree.getMaximum() != -1) {
          hasMax = true;
        }
      }
    }
    if (permitted != null) {
      for (i = 0; i < permitted.size(); i++)
      {
        localGeneralSubtree = permitted.get(i);
        if (localGeneralSubtree.getMinimum() != 0) {
          hasMin = true;
        }
        if (localGeneralSubtree.getMaximum() != -1) {
          hasMax = true;
        }
      }
    }
    minMaxValid = true;
  }
  
  private void encodeThis()
    throws IOException
  {
    minMaxValid = false;
    if ((permitted == null) && (excluded == null))
    {
      extensionValue = null;
      return;
    }
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    DerOutputStream localDerOutputStream3;
    if (permitted != null)
    {
      localDerOutputStream3 = new DerOutputStream();
      permitted.encode(localDerOutputStream3);
      localDerOutputStream2.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream3);
    }
    if (excluded != null)
    {
      localDerOutputStream3 = new DerOutputStream();
      excluded.encode(localDerOutputStream3);
      localDerOutputStream2.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream3);
    }
    localDerOutputStream1.write((byte)48, localDerOutputStream2);
    extensionValue = localDerOutputStream1.toByteArray();
  }
  
  public NameConstraintsExtension(GeneralSubtrees paramGeneralSubtrees1, GeneralSubtrees paramGeneralSubtrees2)
    throws IOException
  {
    permitted = paramGeneralSubtrees1;
    excluded = paramGeneralSubtrees2;
    extensionId = PKIXExtensions.NameConstraints_Id;
    critical = true;
    encodeThis();
  }
  
  public NameConstraintsExtension(Boolean paramBoolean, Object paramObject)
    throws IOException
  {
    extensionId = PKIXExtensions.NameConstraints_Id;
    critical = paramBoolean.booleanValue();
    extensionValue = ((byte[])paramObject);
    DerValue localDerValue1 = new DerValue(extensionValue);
    if (tag != 48) {
      throw new IOException("Invalid encoding for NameConstraintsExtension.");
    }
    if (data == null) {
      return;
    }
    while (data.available() != 0)
    {
      DerValue localDerValue2 = data.getDerValue();
      if ((localDerValue2.isContextSpecific((byte)0)) && (localDerValue2.isConstructed()))
      {
        if (permitted != null) {
          throw new IOException("Duplicate permitted GeneralSubtrees in NameConstraintsExtension.");
        }
        localDerValue2.resetTag((byte)48);
        permitted = new GeneralSubtrees(localDerValue2);
      }
      else if ((localDerValue2.isContextSpecific((byte)1)) && (localDerValue2.isConstructed()))
      {
        if (excluded != null) {
          throw new IOException("Duplicate excluded GeneralSubtrees in NameConstraintsExtension.");
        }
        localDerValue2.resetTag((byte)48);
        excluded = new GeneralSubtrees(localDerValue2);
      }
      else
      {
        throw new IOException("Invalid encoding of NameConstraintsExtension.");
      }
    }
    minMaxValid = false;
  }
  
  public String toString()
  {
    return super.toString() + "NameConstraints: [" + (permitted == null ? "" : new StringBuilder().append("\n    Permitted:").append(permitted.toString()).toString()) + (excluded == null ? "" : new StringBuilder().append("\n    Excluded:").append(excluded.toString()).toString()) + "   ]\n";
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    if (extensionValue == null)
    {
      extensionId = PKIXExtensions.NameConstraints_Id;
      critical = true;
      encodeThis();
    }
    super.encode(localDerOutputStream);
    paramOutputStream.write(localDerOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("permitted_subtrees"))
    {
      if (!(paramObject instanceof GeneralSubtrees)) {
        throw new IOException("Attribute value should be of type GeneralSubtrees.");
      }
      permitted = ((GeneralSubtrees)paramObject);
    }
    else if (paramString.equalsIgnoreCase("excluded_subtrees"))
    {
      if (!(paramObject instanceof GeneralSubtrees)) {
        throw new IOException("Attribute value should be of type GeneralSubtrees.");
      }
      excluded = ((GeneralSubtrees)paramObject);
    }
    else
    {
      throw new IOException("Attribute name not recognized by CertAttrSet:NameConstraintsExtension.");
    }
    encodeThis();
  }
  
  public GeneralSubtrees get(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("permitted_subtrees")) {
      return permitted;
    }
    if (paramString.equalsIgnoreCase("excluded_subtrees")) {
      return excluded;
    }
    throw new IOException("Attribute name not recognized by CertAttrSet:NameConstraintsExtension.");
  }
  
  public void delete(String paramString)
    throws IOException
  {
    if (paramString.equalsIgnoreCase("permitted_subtrees")) {
      permitted = null;
    } else if (paramString.equalsIgnoreCase("excluded_subtrees")) {
      excluded = null;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:NameConstraintsExtension.");
    }
    encodeThis();
  }
  
  public Enumeration<String> getElements()
  {
    AttributeNameEnumeration localAttributeNameEnumeration = new AttributeNameEnumeration();
    localAttributeNameEnumeration.addElement("permitted_subtrees");
    localAttributeNameEnumeration.addElement("excluded_subtrees");
    return localAttributeNameEnumeration.elements();
  }
  
  public String getName()
  {
    return "NameConstraints";
  }
  
  public void merge(NameConstraintsExtension paramNameConstraintsExtension)
    throws IOException
  {
    if (paramNameConstraintsExtension == null) {
      return;
    }
    GeneralSubtrees localGeneralSubtrees1 = paramNameConstraintsExtension.get("excluded_subtrees");
    if (excluded == null) {
      excluded = (localGeneralSubtrees1 != null ? (GeneralSubtrees)localGeneralSubtrees1.clone() : null);
    } else if (localGeneralSubtrees1 != null) {
      excluded.union(localGeneralSubtrees1);
    }
    GeneralSubtrees localGeneralSubtrees2 = paramNameConstraintsExtension.get("permitted_subtrees");
    if (permitted == null)
    {
      permitted = (localGeneralSubtrees2 != null ? (GeneralSubtrees)localGeneralSubtrees2.clone() : null);
    }
    else if (localGeneralSubtrees2 != null)
    {
      localGeneralSubtrees1 = permitted.intersect(localGeneralSubtrees2);
      if (localGeneralSubtrees1 != null) {
        if (excluded != null) {
          excluded.union(localGeneralSubtrees1);
        } else {
          excluded = ((GeneralSubtrees)localGeneralSubtrees1.clone());
        }
      }
    }
    if (permitted != null) {
      permitted.reduce(excluded);
    }
    encodeThis();
  }
  
  public boolean verify(X509Certificate paramX509Certificate)
    throws IOException
  {
    if (paramX509Certificate == null) {
      throw new IOException("Certificate is null");
    }
    if (!minMaxValid) {
      calcMinMax();
    }
    if (hasMin) {
      throw new IOException("Non-zero minimum BaseDistance in name constraints not supported");
    }
    if (hasMax) {
      throw new IOException("Maximum BaseDistance in name constraints not supported");
    }
    X500Principal localX500Principal = paramX509Certificate.getSubjectX500Principal();
    X500Name localX500Name = X500Name.asX500Name(localX500Principal);
    if ((!localX500Name.isEmpty()) && (!verify(localX500Name))) {
      return false;
    }
    GeneralNames localGeneralNames = null;
    try
    {
      X509CertImpl localX509CertImpl = X509CertImpl.toImpl(paramX509Certificate);
      localObject2 = localX509CertImpl.getSubjectAlternativeNameExtension();
      if (localObject2 != null) {
        localGeneralNames = ((SubjectAlternativeNameExtension)localObject2).get("subject_name");
      }
    }
    catch (CertificateException localCertificateException)
    {
      throw new IOException("Unable to extract extensions from certificate: " + localCertificateException.getMessage());
    }
    Object localObject3;
    if (localGeneralNames == null)
    {
      localGeneralNames = new GeneralNames();
      localObject1 = localX500Name.allAvas().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (AVA)((Iterator)localObject1).next();
        ObjectIdentifier localObjectIdentifier = ((AVA)localObject2).getObjectIdentifier();
        if (localObjectIdentifier.equals(PKCS9Attribute.EMAIL_ADDRESS_OID))
        {
          localObject3 = ((AVA)localObject2).getValueString();
          if (localObject3 != null) {
            try
            {
              localGeneralNames.add(new GeneralName(new RFC822Name((String)localObject3)));
            }
            catch (IOException localIOException2) {}
          }
        }
      }
    }
    Object localObject1 = localX500Name.findMostSpecificAttribute(X500Name.commonName_oid);
    Object localObject2 = localObject1 == null ? null : ((DerValue)localObject1).getAsString();
    if (localObject2 != null) {
      try
      {
        if ((IPAddressUtil.isIPv4LiteralAddress((String)localObject2)) || (IPAddressUtil.isIPv6LiteralAddress((String)localObject2)))
        {
          if (!hasNameType(localGeneralNames, 7)) {
            localGeneralNames.add(new GeneralName(new IPAddressName((String)localObject2)));
          }
        }
        else if (!hasNameType(localGeneralNames, 2)) {
          localGeneralNames.add(new GeneralName(new DNSName((String)localObject2)));
        }
      }
      catch (IOException localIOException1) {}
    }
    for (int i = 0; i < localGeneralNames.size(); i++)
    {
      localObject3 = localGeneralNames.get(i).getName();
      if (!verify((GeneralNameInterface)localObject3)) {
        return false;
      }
    }
    return true;
  }
  
  private static boolean hasNameType(GeneralNames paramGeneralNames, int paramInt)
  {
    Iterator localIterator = paramGeneralNames.names().iterator();
    while (localIterator.hasNext())
    {
      GeneralName localGeneralName = (GeneralName)localIterator.next();
      if (localGeneralName.getType() == paramInt) {
        return true;
      }
    }
    return false;
  }
  
  public boolean verify(GeneralNameInterface paramGeneralNameInterface)
    throws IOException
  {
    if (paramGeneralNameInterface == null) {
      throw new IOException("name is null");
    }
    int i;
    Object localObject1;
    Object localObject2;
    if ((excluded != null) && (excluded.size() > 0)) {
      for (i = 0; i < excluded.size(); i++)
      {
        GeneralSubtree localGeneralSubtree = excluded.get(i);
        if (localGeneralSubtree != null)
        {
          localObject1 = localGeneralSubtree.getName();
          if (localObject1 != null)
          {
            localObject2 = ((GeneralName)localObject1).getName();
            if (localObject2 != null) {
              switch (((GeneralNameInterface)localObject2).constrains(paramGeneralNameInterface))
              {
              case -1: 
              case 2: 
              case 3: 
                break;
              case 0: 
              case 1: 
                return false;
              }
            }
          }
        }
      }
    }
    if ((permitted != null) && (permitted.size() > 0))
    {
      i = 0;
      for (int j = 0; j < permitted.size(); j++)
      {
        localObject1 = permitted.get(j);
        if (localObject1 != null)
        {
          localObject2 = ((GeneralSubtree)localObject1).getName();
          if (localObject2 != null)
          {
            GeneralNameInterface localGeneralNameInterface = ((GeneralName)localObject2).getName();
            if (localGeneralNameInterface != null) {
              switch (localGeneralNameInterface.constrains(paramGeneralNameInterface))
              {
              case -1: 
                break;
              case 2: 
              case 3: 
                i = 1;
                break;
              case 0: 
              case 1: 
                return true;
              }
            }
          }
        }
      }
      if (i != 0) {
        return false;
      }
    }
    return true;
  }
  
  public Object clone()
  {
    try
    {
      NameConstraintsExtension localNameConstraintsExtension = (NameConstraintsExtension)super.clone();
      if (permitted != null) {
        permitted = ((GeneralSubtrees)permitted.clone());
      }
      if (excluded != null) {
        excluded = ((GeneralSubtrees)excluded.clone());
      }
      return localNameConstraintsExtension;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new RuntimeException("CloneNotSupportedException while cloning NameConstraintsException. This should never happen.");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\NameConstraintsExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */