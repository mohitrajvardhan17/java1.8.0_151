package javax.management.monitor;

import javax.management.ObjectName;

public abstract interface CounterMonitorMBean
  extends MonitorMBean
{
  @Deprecated
  public abstract Number getDerivedGauge();
  
  @Deprecated
  public abstract long getDerivedGaugeTimeStamp();
  
  @Deprecated
  public abstract Number getThreshold();
  
  @Deprecated
  public abstract void setThreshold(Number paramNumber)
    throws IllegalArgumentException;
  
  public abstract Number getDerivedGauge(ObjectName paramObjectName);
  
  public abstract long getDerivedGaugeTimeStamp(ObjectName paramObjectName);
  
  public abstract Number getThreshold(ObjectName paramObjectName);
  
  public abstract Number getInitThreshold();
  
  public abstract void setInitThreshold(Number paramNumber)
    throws IllegalArgumentException;
  
  public abstract Number getOffset();
  
  public abstract void setOffset(Number paramNumber)
    throws IllegalArgumentException;
  
  public abstract Number getModulus();
  
  public abstract void setModulus(Number paramNumber)
    throws IllegalArgumentException;
  
  public abstract boolean getNotify();
  
  public abstract void setNotify(boolean paramBoolean);
  
  public abstract boolean getDifferenceMode();
  
  public abstract void setDifferenceMode(boolean paramBoolean);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\monitor\CounterMonitorMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */