package javax.security.auth.login;

import java.util.Collections;
import java.util.Map;
import sun.security.util.ResourcesMgr;

public class AppConfigurationEntry
{
  private String loginModuleName;
  private LoginModuleControlFlag controlFlag;
  private Map<String, ?> options;
  
  public AppConfigurationEntry(String paramString, LoginModuleControlFlag paramLoginModuleControlFlag, Map<String, ?> paramMap)
  {
    if ((paramString == null) || (paramString.length() == 0) || ((paramLoginModuleControlFlag != LoginModuleControlFlag.REQUIRED) && (paramLoginModuleControlFlag != LoginModuleControlFlag.REQUISITE) && (paramLoginModuleControlFlag != LoginModuleControlFlag.SUFFICIENT) && (paramLoginModuleControlFlag != LoginModuleControlFlag.OPTIONAL)) || (paramMap == null)) {
      throw new IllegalArgumentException();
    }
    loginModuleName = paramString;
    controlFlag = paramLoginModuleControlFlag;
    options = Collections.unmodifiableMap(paramMap);
  }
  
  public String getLoginModuleName()
  {
    return loginModuleName;
  }
  
  public LoginModuleControlFlag getControlFlag()
  {
    return controlFlag;
  }
  
  public Map<String, ?> getOptions()
  {
    return options;
  }
  
  public static class LoginModuleControlFlag
  {
    private String controlFlag;
    public static final LoginModuleControlFlag REQUIRED = new LoginModuleControlFlag("required");
    public static final LoginModuleControlFlag REQUISITE = new LoginModuleControlFlag("requisite");
    public static final LoginModuleControlFlag SUFFICIENT = new LoginModuleControlFlag("sufficient");
    public static final LoginModuleControlFlag OPTIONAL = new LoginModuleControlFlag("optional");
    
    private LoginModuleControlFlag(String paramString)
    {
      controlFlag = paramString;
    }
    
    public String toString()
    {
      return ResourcesMgr.getString("LoginModuleControlFlag.") + controlFlag;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\login\AppConfigurationEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */