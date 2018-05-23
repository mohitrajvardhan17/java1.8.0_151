package sun.security.krb5.internal.ccache;

import java.io.File;
import java.io.IOException;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;

public abstract class MemoryCredentialsCache
  extends CredentialsCache
{
  public MemoryCredentialsCache() {}
  
  private static CredentialsCache getCCacheInstance(PrincipalName paramPrincipalName)
  {
    return null;
  }
  
  private static CredentialsCache getCCacheInstance(PrincipalName paramPrincipalName, File paramFile)
  {
    return null;
  }
  
  public abstract boolean exists(String paramString);
  
  public abstract void update(Credentials paramCredentials);
  
  public abstract void save()
    throws IOException, KrbException;
  
  public abstract Credentials[] getCredsList();
  
  public abstract Credentials getCreds(PrincipalName paramPrincipalName);
  
  public abstract PrincipalName getPrimaryPrincipal();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\ccache\MemoryCredentialsCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */