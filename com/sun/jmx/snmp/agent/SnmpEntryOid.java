package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpOid;

class SnmpEntryOid
  extends SnmpOid
{
  private static final long serialVersionUID = 9212653887791059564L;
  
  public SnmpEntryOid(long[] paramArrayOfLong, int paramInt)
  {
    int i = paramArrayOfLong.length - paramInt;
    long[] arrayOfLong = new long[i];
    System.arraycopy(paramArrayOfLong, paramInt, arrayOfLong, 0, i);
    components = arrayOfLong;
    componentCount = i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\agent\SnmpEntryOid.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */