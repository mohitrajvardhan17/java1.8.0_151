package com.sun.jmx.snmp.daemon;

public abstract interface CommunicatorServerMBean
{
  public abstract void start();
  
  public abstract void stop();
  
  public abstract boolean isActive();
  
  public abstract boolean waitState(int paramInt, long paramLong);
  
  public abstract int getState();
  
  public abstract String getStateString();
  
  public abstract String getHost();
  
  public abstract int getPort();
  
  public abstract void setPort(int paramInt)
    throws IllegalStateException;
  
  public abstract String getProtocol();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\daemon\CommunicatorServerMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */