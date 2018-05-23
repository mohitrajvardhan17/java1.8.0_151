package java.security;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

@Deprecated
public abstract class Identity
  implements Principal, Serializable
{
  private static final long serialVersionUID = 3609922007826600659L;
  private String name;
  private PublicKey publicKey;
  String info = "No further information available.";
  IdentityScope scope;
  Vector<Certificate> certificates;
  
  protected Identity()
  {
    this("restoring...");
  }
  
  public Identity(String paramString, IdentityScope paramIdentityScope)
    throws KeyManagementException
  {
    this(paramString);
    if (paramIdentityScope != null) {
      paramIdentityScope.addIdentity(this);
    }
    scope = paramIdentityScope;
  }
  
  public Identity(String paramString)
  {
    name = paramString;
  }
  
  public final String getName()
  {
    return name;
  }
  
  public final IdentityScope getScope()
  {
    return scope;
  }
  
  public PublicKey getPublicKey()
  {
    return publicKey;
  }
  
  public void setPublicKey(PublicKey paramPublicKey)
    throws KeyManagementException
  {
    check("setIdentityPublicKey");
    publicKey = paramPublicKey;
    certificates = new Vector();
  }
  
  public void setInfo(String paramString)
  {
    check("setIdentityInfo");
    info = paramString;
  }
  
  public String getInfo()
  {
    return info;
  }
  
  public void addCertificate(Certificate paramCertificate)
    throws KeyManagementException
  {
    check("addIdentityCertificate");
    if (certificates == null) {
      certificates = new Vector();
    }
    if (publicKey != null)
    {
      if (!keyEquals(publicKey, paramCertificate.getPublicKey())) {
        throw new KeyManagementException("public key different from cert public key");
      }
    }
    else {
      publicKey = paramCertificate.getPublicKey();
    }
    certificates.addElement(paramCertificate);
  }
  
  private boolean keyEquals(PublicKey paramPublicKey1, PublicKey paramPublicKey2)
  {
    String str1 = paramPublicKey1.getFormat();
    String str2 = paramPublicKey2.getFormat();
    if (((str1 == null ? 1 : 0) ^ (str2 == null ? 1 : 0)) != 0) {
      return false;
    }
    if ((str1 != null) && (str2 != null) && (!str1.equalsIgnoreCase(str2))) {
      return false;
    }
    return Arrays.equals(paramPublicKey1.getEncoded(), paramPublicKey2.getEncoded());
  }
  
  public void removeCertificate(Certificate paramCertificate)
    throws KeyManagementException
  {
    check("removeIdentityCertificate");
    if (certificates != null) {
      certificates.removeElement(paramCertificate);
    }
  }
  
  public Certificate[] certificates()
  {
    if (certificates == null) {
      return new Certificate[0];
    }
    int i = certificates.size();
    Certificate[] arrayOfCertificate = new Certificate[i];
    certificates.copyInto(arrayOfCertificate);
    return arrayOfCertificate;
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof Identity))
    {
      Identity localIdentity = (Identity)paramObject;
      if (fullName().equals(localIdentity.fullName())) {
        return true;
      }
      return identityEquals(localIdentity);
    }
    return false;
  }
  
  protected boolean identityEquals(Identity paramIdentity)
  {
    if (!name.equalsIgnoreCase(name)) {
      return false;
    }
    if (((publicKey == null ? 1 : 0) ^ (publicKey == null ? 1 : 0)) != 0) {
      return false;
    }
    return (publicKey == null) || (publicKey == null) || (publicKey.equals(publicKey));
  }
  
  String fullName()
  {
    String str = name;
    if (scope != null) {
      str = str + "." + scope.getName();
    }
    return str;
  }
  
  public String toString()
  {
    check("printIdentity");
    String str = name;
    if (scope != null) {
      str = str + "[" + scope.getName() + "]";
    }
    return str;
  }
  
  public String toString(boolean paramBoolean)
  {
    String str = toString();
    if (paramBoolean)
    {
      str = str + "\n";
      str = str + printKeys();
      str = str + "\n" + printCertificates();
      if (info != null) {
        str = str + "\n\t" + info;
      } else {
        str = str + "\n\tno additional information available.";
      }
    }
    return str;
  }
  
  String printKeys()
  {
    String str = "";
    if (publicKey != null) {
      str = "\tpublic key initialized";
    } else {
      str = "\tno public key";
    }
    return str;
  }
  
  String printCertificates()
  {
    String str = "";
    if (certificates == null) {
      return "\tno certificates";
    }
    str = str + "\tcertificates: \n";
    int i = 1;
    Iterator localIterator = certificates.iterator();
    while (localIterator.hasNext())
    {
      Certificate localCertificate = (Certificate)localIterator.next();
      str = str + "\tcertificate " + i++ + "\tfor  : " + localCertificate.getPrincipal() + "\n";
      str = str + "\t\t\tfrom : " + localCertificate.getGuarantor() + "\n";
    }
    return str;
  }
  
  public int hashCode()
  {
    return name.hashCode();
  }
  
  private static void check(String paramString)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkSecurityAccess(paramString);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\Identity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */