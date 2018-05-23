package com.sun.security.auth.module;

import jdk.Exported;

@Exported
public class NTSystem
{
  private String userName;
  private String domain;
  private String domainSID;
  private String userSID;
  private String[] groupIDs;
  private String primaryGroupID;
  private long impersonationToken;
  
  private native void getCurrent(boolean paramBoolean);
  
  private native long getImpersonationToken0();
  
  public NTSystem()
  {
    this(false);
  }
  
  NTSystem(boolean paramBoolean)
  {
    loadNative();
    getCurrent(paramBoolean);
  }
  
  public String getName()
  {
    return userName;
  }
  
  public String getDomain()
  {
    return domain;
  }
  
  public String getDomainSID()
  {
    return domainSID;
  }
  
  public String getUserSID()
  {
    return userSID;
  }
  
  public String getPrimaryGroupID()
  {
    return primaryGroupID;
  }
  
  public String[] getGroupIDs()
  {
    return groupIDs == null ? null : (String[])groupIDs.clone();
  }
  
  public synchronized long getImpersonationToken()
  {
    if (impersonationToken == 0L) {
      impersonationToken = getImpersonationToken0();
    }
    return impersonationToken;
  }
  
  private void loadNative()
  {
    System.loadLibrary("jaas_nt");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\auth\module\NTSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */