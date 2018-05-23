package sun.security.provider;

import java.net.MalformedURLException;
import java.net.URI;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy.Parameters;
import java.security.PolicySpi;
import java.security.ProtectionDomain;
import java.security.URIParameter;

public final class PolicySpiFile
  extends PolicySpi
{
  private PolicyFile pf;
  
  public PolicySpiFile(Policy.Parameters paramParameters)
  {
    if (paramParameters == null)
    {
      pf = new PolicyFile();
    }
    else
    {
      if (!(paramParameters instanceof URIParameter)) {
        throw new IllegalArgumentException("Unrecognized policy parameter: " + paramParameters);
      }
      URIParameter localURIParameter = (URIParameter)paramParameters;
      try
      {
        pf = new PolicyFile(localURIParameter.getURI().toURL());
      }
      catch (MalformedURLException localMalformedURLException)
      {
        throw new IllegalArgumentException("Invalid URIParameter", localMalformedURLException);
      }
    }
  }
  
  protected PermissionCollection engineGetPermissions(CodeSource paramCodeSource)
  {
    return pf.getPermissions(paramCodeSource);
  }
  
  protected PermissionCollection engineGetPermissions(ProtectionDomain paramProtectionDomain)
  {
    return pf.getPermissions(paramProtectionDomain);
  }
  
  protected boolean engineImplies(ProtectionDomain paramProtectionDomain, Permission paramPermission)
  {
    return pf.implies(paramProtectionDomain, paramPermission);
  }
  
  protected void engineRefresh()
  {
    pf.refresh();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\PolicySpiFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */