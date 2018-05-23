package javax.security.auth.kerberos;

import java.io.File;
import java.security.AccessControlException;
import java.util.Objects;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KerberosSecrets;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.RealmException;

public final class KeyTab
{
  private final File file;
  private final KerberosPrincipal princ;
  private final boolean bound;
  
  private KeyTab(KerberosPrincipal paramKerberosPrincipal, File paramFile, boolean paramBoolean)
  {
    princ = paramKerberosPrincipal;
    file = paramFile;
    bound = paramBoolean;
  }
  
  public static KeyTab getInstance(File paramFile)
  {
    if (paramFile == null) {
      throw new NullPointerException("file must be non null");
    }
    return new KeyTab(null, paramFile, true);
  }
  
  public static KeyTab getUnboundInstance(File paramFile)
  {
    if (paramFile == null) {
      throw new NullPointerException("file must be non null");
    }
    return new KeyTab(null, paramFile, false);
  }
  
  public static KeyTab getInstance(KerberosPrincipal paramKerberosPrincipal, File paramFile)
  {
    if (paramKerberosPrincipal == null) {
      throw new NullPointerException("princ must be non null");
    }
    if (paramFile == null) {
      throw new NullPointerException("file must be non null");
    }
    return new KeyTab(paramKerberosPrincipal, paramFile, true);
  }
  
  public static KeyTab getInstance()
  {
    return new KeyTab(null, null, true);
  }
  
  public static KeyTab getUnboundInstance()
  {
    return new KeyTab(null, null, false);
  }
  
  public static KeyTab getInstance(KerberosPrincipal paramKerberosPrincipal)
  {
    if (paramKerberosPrincipal == null) {
      throw new NullPointerException("princ must be non null");
    }
    return new KeyTab(paramKerberosPrincipal, null, true);
  }
  
  sun.security.krb5.internal.ktab.KeyTab takeSnapshot()
  {
    try
    {
      return sun.security.krb5.internal.ktab.KeyTab.getInstance(file);
    }
    catch (AccessControlException localAccessControlException1)
    {
      if (file != null) {
        throw localAccessControlException1;
      }
      AccessControlException localAccessControlException2 = new AccessControlException("Access to default keytab denied (modified exception)");
      localAccessControlException2.setStackTrace(localAccessControlException1.getStackTrace());
      throw localAccessControlException2;
    }
  }
  
  public KerberosKey[] getKeys(KerberosPrincipal paramKerberosPrincipal)
  {
    try
    {
      if ((princ != null) && (!paramKerberosPrincipal.equals(princ))) {
        return new KerberosKey[0];
      }
      PrincipalName localPrincipalName = new PrincipalName(paramKerberosPrincipal.getName());
      EncryptionKey[] arrayOfEncryptionKey = takeSnapshot().readServiceKeys(localPrincipalName);
      KerberosKey[] arrayOfKerberosKey = new KerberosKey[arrayOfEncryptionKey.length];
      for (int i = 0; i < arrayOfKerberosKey.length; i++)
      {
        Integer localInteger = arrayOfEncryptionKey[i].getKeyVersionNumber();
        arrayOfKerberosKey[i] = new KerberosKey(paramKerberosPrincipal, arrayOfEncryptionKey[i].getBytes(), arrayOfEncryptionKey[i].getEType(), localInteger == null ? 0 : localInteger.intValue());
        arrayOfEncryptionKey[i].destroy();
      }
      return arrayOfKerberosKey;
    }
    catch (RealmException localRealmException) {}
    return new KerberosKey[0];
  }
  
  EncryptionKey[] getEncryptionKeys(PrincipalName paramPrincipalName)
  {
    return takeSnapshot().readServiceKeys(paramPrincipalName);
  }
  
  public boolean exists()
  {
    return !takeSnapshot().isMissing();
  }
  
  public String toString()
  {
    String str = file == null ? "Default keytab" : file.toString();
    if (!bound) {
      return str;
    }
    if (princ == null) {
      return str + " for someone";
    }
    return str + " for " + princ;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { file, princ, Boolean.valueOf(bound) });
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof KeyTab)) {
      return false;
    }
    KeyTab localKeyTab = (KeyTab)paramObject;
    return (Objects.equals(princ, princ)) && (Objects.equals(file, file)) && (bound == bound);
  }
  
  public KerberosPrincipal getPrincipal()
  {
    return princ;
  }
  
  public boolean isBound()
  {
    return bound;
  }
  
  static
  {
    KerberosSecrets.setJavaxSecurityAuthKerberosAccess(new JavaxSecurityAuthKerberosAccessImpl());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\kerberos\KeyTab.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */