package javax.management.monitor;

import javax.management.ObjectName;

public abstract interface GaugeMonitorMBean
  extends MonitorMBean
{
  @Deprecated
  public abstract Number getDerivedGauge();
  
  @Deprecated
  public abstract long getDerivedGaugeTimeStamp();
  
  public abstract Number getDerivedGauge(ObjectName paramObjectName);
  
  public abstract long getDerivedGaugeTimeStamp(ObjectName paramObjectName);
  
  public abstract Number getHighThreshold();
  
  public abstract Number getLowThreshold();
  
  public abstract void setThresholds(Number paramNumber1, Number paramNumber2)
    throws IllegalArgumentException;
  
  public abstract boolean getNotifyHigh();
  
  public abstract void setNotifyHigh(boolean paramBoolean);
  
  public abstract boolean getNotifyLow();
  
  public abstract void setNotifyLow(boolean paramBoolean);
  
  public abstract boolean getDifferenceMode();
  
  public abstract void setDifferenceMode(boolean paramBoolean);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\monitor\GaugeMonitorMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */