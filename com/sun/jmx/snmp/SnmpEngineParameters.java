package com.sun.jmx.snmp;

import java.io.Serializable;

public class SnmpEngineParameters
  implements Serializable
{
  private static final long serialVersionUID = 3720556613478400808L;
  private UserAcl uacl = null;
  private String securityFile = null;
  private boolean encrypt = false;
  private SnmpEngineId engineId = null;
  
  public SnmpEngineParameters() {}
  
  public void setSecurityFile(String paramString)
  {
    securityFile = paramString;
  }
  
  public String getSecurityFile()
  {
    return securityFile;
  }
  
  public void setUserAcl(UserAcl paramUserAcl)
  {
    uacl = paramUserAcl;
  }
  
  public UserAcl getUserAcl()
  {
    return uacl;
  }
  
  public void activateEncryption()
  {
    encrypt = true;
  }
  
  public void deactivateEncryption()
  {
    encrypt = false;
  }
  
  public boolean isEncryptionEnabled()
  {
    return encrypt;
  }
  
  public void setEngineId(SnmpEngineId paramSnmpEngineId)
  {
    engineId = paramSnmpEngineId;
  }
  
  public SnmpEngineId getEngineId()
  {
    return engineId;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpEngineParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */