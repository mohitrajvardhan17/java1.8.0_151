package com.sun.jmx.snmp.daemon;

final class SnmpRequestCounter
{
  int reqid = 0;
  
  public SnmpRequestCounter() {}
  
  public synchronized int getNewId()
  {
    if (++reqid < 0) {
      reqid = 1;
    }
    return reqid;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\daemon\SnmpRequestCounter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */