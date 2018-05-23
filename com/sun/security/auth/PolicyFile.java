package com.sun.security.auth;

import java.security.CodeSource;
import java.security.PermissionCollection;
import javax.security.auth.Policy;
import javax.security.auth.Subject;
import jdk.Exported;
import sun.security.provider.AuthPolicyFile;

@Exported(false)
@Deprecated
public class PolicyFile
  extends Policy
{
  private final AuthPolicyFile apf = new AuthPolicyFile();
  
  public PolicyFile() {}
  
  public void refresh()
  {
    apf.refresh();
  }
  
  public PermissionCollection getPermissions(Subject paramSubject, CodeSource paramCodeSource)
  {
    return apf.getPermissions(paramSubject, paramCodeSource);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\auth\PolicyFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */