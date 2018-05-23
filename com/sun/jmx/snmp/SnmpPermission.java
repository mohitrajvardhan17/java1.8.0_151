package com.sun.jmx.snmp;

import java.security.BasicPermission;

public class SnmpPermission
  extends BasicPermission
{
  public SnmpPermission(String paramString)
  {
    super(paramString);
  }
  
  public SnmpPermission(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */