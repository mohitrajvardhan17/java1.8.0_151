package javax.security.auth;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Principal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import sun.security.util.ResourcesMgr;

public final class PrivateCredentialPermission
  extends Permission
{
  private static final long serialVersionUID = 5284372143517237068L;
  private static final CredOwner[] EMPTY_PRINCIPALS = new CredOwner[0];
  private String credentialClass;
  private Set<Principal> principals;
  private transient CredOwner[] credOwners;
  private boolean testing = false;
  
  PrivateCredentialPermission(String paramString, Set<Principal> paramSet)
  {
    super(paramString);
    credentialClass = paramString;
    synchronized (paramSet)
    {
      if (paramSet.size() == 0)
      {
        credOwners = EMPTY_PRINCIPALS;
      }
      else
      {
        credOwners = new CredOwner[paramSet.size()];
        int i = 0;
        Iterator localIterator = paramSet.iterator();
        while (localIterator.hasNext())
        {
          Principal localPrincipal = (Principal)localIterator.next();
          credOwners[(i++)] = new CredOwner(localPrincipal.getClass().getName(), localPrincipal.getName());
        }
      }
    }
  }
  
  public PrivateCredentialPermission(String paramString1, String paramString2)
  {
    super(paramString1);
    if (!"read".equalsIgnoreCase(paramString2)) {
      throw new IllegalArgumentException(ResourcesMgr.getString("actions.can.only.be.read."));
    }
    init(paramString1);
  }
  
  public String getCredentialClass()
  {
    return credentialClass;
  }
  
  public String[][] getPrincipals()
  {
    if ((credOwners == null) || (credOwners.length == 0)) {
      return new String[0][0];
    }
    String[][] arrayOfString = new String[credOwners.length][2];
    for (int i = 0; i < credOwners.length; i++)
    {
      arrayOfString[i][0] = credOwners[i].principalClass;
      arrayOfString[i][1] = credOwners[i].principalName;
    }
    return arrayOfString;
  }
  
  public boolean implies(Permission paramPermission)
  {
    if ((paramPermission == null) || (!(paramPermission instanceof PrivateCredentialPermission))) {
      return false;
    }
    PrivateCredentialPermission localPrivateCredentialPermission = (PrivateCredentialPermission)paramPermission;
    if (!impliesCredentialClass(credentialClass, credentialClass)) {
      return false;
    }
    return impliesPrincipalSet(credOwners, credOwners);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof PrivateCredentialPermission)) {
      return false;
    }
    PrivateCredentialPermission localPrivateCredentialPermission = (PrivateCredentialPermission)paramObject;
    return (implies(localPrivateCredentialPermission)) && (localPrivateCredentialPermission.implies(this));
  }
  
  public int hashCode()
  {
    return credentialClass.hashCode();
  }
  
  public String getActions()
  {
    return "read";
  }
  
  public PermissionCollection newPermissionCollection()
  {
    return null;
  }
  
  private void init(String paramString)
  {
    if ((paramString == null) || (paramString.trim().length() == 0)) {
      throw new IllegalArgumentException("invalid empty name");
    }
    ArrayList localArrayList = new ArrayList();
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, " ", true);
    String str1 = null;
    String str2 = null;
    if (testing) {
      System.out.println("whole name = " + paramString);
    }
    credentialClass = localStringTokenizer.nextToken();
    if (testing) {
      System.out.println("Credential Class = " + credentialClass);
    }
    MessageFormat localMessageFormat;
    Object[] arrayOfObject;
    if (!localStringTokenizer.hasMoreTokens())
    {
      localMessageFormat = new MessageFormat(ResourcesMgr.getString("permission.name.name.syntax.invalid."));
      arrayOfObject = new Object[] { paramString };
      throw new IllegalArgumentException(localMessageFormat.format(arrayOfObject) + ResourcesMgr.getString("Credential.Class.not.followed.by.a.Principal.Class.and.Name"));
    }
    while (localStringTokenizer.hasMoreTokens())
    {
      localStringTokenizer.nextToken();
      str1 = localStringTokenizer.nextToken();
      if (testing) {
        System.out.println("    Principal Class = " + str1);
      }
      if (!localStringTokenizer.hasMoreTokens())
      {
        localMessageFormat = new MessageFormat(ResourcesMgr.getString("permission.name.name.syntax.invalid."));
        arrayOfObject = new Object[] { paramString };
        throw new IllegalArgumentException(localMessageFormat.format(arrayOfObject) + ResourcesMgr.getString("Principal.Class.not.followed.by.a.Principal.Name"));
      }
      localStringTokenizer.nextToken();
      str2 = localStringTokenizer.nextToken();
      if (!str2.startsWith("\""))
      {
        localMessageFormat = new MessageFormat(ResourcesMgr.getString("permission.name.name.syntax.invalid."));
        arrayOfObject = new Object[] { paramString };
        throw new IllegalArgumentException(localMessageFormat.format(arrayOfObject) + ResourcesMgr.getString("Principal.Name.must.be.surrounded.by.quotes"));
      }
      if (!str2.endsWith("\""))
      {
        while (localStringTokenizer.hasMoreTokens())
        {
          str2 = str2 + localStringTokenizer.nextToken();
          if (str2.endsWith("\"")) {
            break;
          }
        }
        if (!str2.endsWith("\""))
        {
          localMessageFormat = new MessageFormat(ResourcesMgr.getString("permission.name.name.syntax.invalid."));
          arrayOfObject = new Object[] { paramString };
          throw new IllegalArgumentException(localMessageFormat.format(arrayOfObject) + ResourcesMgr.getString("Principal.Name.missing.end.quote"));
        }
      }
      if (testing) {
        System.out.println("\tprincipalName = '" + str2 + "'");
      }
      str2 = str2.substring(1, str2.length() - 1);
      if ((str1.equals("*")) && (!str2.equals("*"))) {
        throw new IllegalArgumentException(ResourcesMgr.getString("PrivateCredentialPermission.Principal.Class.can.not.be.a.wildcard.value.if.Principal.Name.is.not.a.wildcard.value"));
      }
      if (testing) {
        System.out.println("\tprincipalName = '" + str2 + "'");
      }
      localArrayList.add(new CredOwner(str1, str2));
    }
    credOwners = new CredOwner[localArrayList.size()];
    localArrayList.toArray(credOwners);
  }
  
  private boolean impliesCredentialClass(String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString2 == null)) {
      return false;
    }
    if (testing) {
      System.out.println("credential class comparison: " + paramString1 + "/" + paramString2);
    }
    if (paramString1.equals("*")) {
      return true;
    }
    return paramString1.equals(paramString2);
  }
  
  private boolean impliesPrincipalSet(CredOwner[] paramArrayOfCredOwner1, CredOwner[] paramArrayOfCredOwner2)
  {
    if ((paramArrayOfCredOwner1 == null) || (paramArrayOfCredOwner2 == null)) {
      return false;
    }
    if (paramArrayOfCredOwner2.length == 0) {
      return true;
    }
    if (paramArrayOfCredOwner1.length == 0) {
      return false;
    }
    for (int i = 0; i < paramArrayOfCredOwner1.length; i++)
    {
      int j = 0;
      for (int k = 0; k < paramArrayOfCredOwner2.length; k++) {
        if (paramArrayOfCredOwner1[i].implies(paramArrayOfCredOwner2[k]))
        {
          j = 1;
          break;
        }
      }
      if (j == 0) {
        return false;
      }
    }
    return true;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    if ((getName().indexOf(" ") == -1) && (getName().indexOf("\"") == -1))
    {
      credentialClass = getName();
      credOwners = EMPTY_PRINCIPALS;
    }
    else
    {
      init(getName());
    }
  }
  
  static class CredOwner
    implements Serializable
  {
    private static final long serialVersionUID = -5607449830436408266L;
    String principalClass;
    String principalName;
    
    CredOwner(String paramString1, String paramString2)
    {
      principalClass = paramString1;
      principalName = paramString2;
    }
    
    public boolean implies(Object paramObject)
    {
      if ((paramObject == null) || (!(paramObject instanceof CredOwner))) {
        return false;
      }
      CredOwner localCredOwner = (CredOwner)paramObject;
      return ((principalClass.equals("*")) || (principalClass.equals(principalClass))) && ((principalName.equals("*")) || (principalName.equals(principalName)));
    }
    
    public String toString()
    {
      MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("CredOwner.Principal.Class.class.Principal.Name.name"));
      Object[] arrayOfObject = { principalClass, principalName };
      return localMessageFormat.format(arrayOfObject);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\PrivateCredentialPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */