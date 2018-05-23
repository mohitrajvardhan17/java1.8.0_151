package com.sun.jmx.snmp;

import java.io.Serializable;
import java.util.Date;

public class Timestamp
  implements Serializable
{
  private static final long serialVersionUID = -242456119149401823L;
  private long sysUpTime;
  private long crtime;
  private SnmpTimeticks uptimeCache = null;
  
  public Timestamp()
  {
    crtime = System.currentTimeMillis();
  }
  
  public Timestamp(long paramLong1, long paramLong2)
  {
    sysUpTime = paramLong1;
    crtime = paramLong2;
  }
  
  public Timestamp(long paramLong)
  {
    sysUpTime = paramLong;
    crtime = System.currentTimeMillis();
  }
  
  public final synchronized SnmpTimeticks getTimeTicks()
  {
    if (uptimeCache == null) {
      uptimeCache = new SnmpTimeticks((int)sysUpTime);
    }
    return uptimeCache;
  }
  
  public final long getSysUpTime()
  {
    return sysUpTime;
  }
  
  public final synchronized Date getDate()
  {
    return new Date(crtime);
  }
  
  public final long getDateTime()
  {
    return crtime;
  }
  
  public final String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("{SysUpTime = " + SnmpTimeticks.printTimeTicks(sysUpTime));
    localStringBuffer.append("} {Timestamp = " + getDate().toString() + "}");
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\Timestamp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */