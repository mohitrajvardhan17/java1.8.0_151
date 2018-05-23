package javax.management.monitor;

import javax.management.ObjectName;

public abstract interface MonitorMBean
{
  public abstract void start();
  
  public abstract void stop();
  
  public abstract void addObservedObject(ObjectName paramObjectName)
    throws IllegalArgumentException;
  
  public abstract void removeObservedObject(ObjectName paramObjectName);
  
  public abstract boolean containsObservedObject(ObjectName paramObjectName);
  
  public abstract ObjectName[] getObservedObjects();
  
  @Deprecated
  public abstract ObjectName getObservedObject();
  
  @Deprecated
  public abstract void setObservedObject(ObjectName paramObjectName);
  
  public abstract String getObservedAttribute();
  
  public abstract void setObservedAttribute(String paramString);
  
  public abstract long getGranularityPeriod();
  
  public abstract void setGranularityPeriod(long paramLong)
    throws IllegalArgumentException;
  
  public abstract boolean isActive();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\monitor\MonitorMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */