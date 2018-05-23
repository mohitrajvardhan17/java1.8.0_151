package com.sun.jmx.snmp;

public class SnmpOidRecord
{
  private String name;
  private String oid;
  private String type;
  
  public SnmpOidRecord(String paramString1, String paramString2, String paramString3)
  {
    name = paramString1;
    oid = paramString2;
    type = paramString3;
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getOid()
  {
    return oid;
  }
  
  public String getType()
  {
    return type;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpOidRecord.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */