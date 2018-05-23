package com.sun.jmx.snmp;

public abstract interface UserAcl
{
  public abstract String getName();
  
  public abstract boolean checkReadPermission(String paramString);
  
  public abstract boolean checkReadPermission(String paramString1, String paramString2, int paramInt);
  
  public abstract boolean checkContextName(String paramString);
  
  public abstract boolean checkWritePermission(String paramString);
  
  public abstract boolean checkWritePermission(String paramString1, String paramString2, int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\UserAcl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */