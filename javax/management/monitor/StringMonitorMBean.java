package javax.management.monitor;

import javax.management.ObjectName;

public abstract interface StringMonitorMBean
  extends MonitorMBean
{
  @Deprecated
  public abstract String getDerivedGauge();
  
  @Deprecated
  public abstract long getDerivedGaugeTimeStamp();
  
  public abstract String getDerivedGauge(ObjectName paramObjectName);
  
  public abstract long getDerivedGaugeTimeStamp(ObjectName paramObjectName);
  
  public abstract String getStringToCompare();
  
  public abstract void setStringToCompare(String paramString)
    throws IllegalArgumentException;
  
  public abstract boolean getNotifyMatch();
  
  public abstract void setNotifyMatch(boolean paramBoolean);
  
  public abstract boolean getNotifyDiffer();
  
  public abstract void setNotifyDiffer(boolean paramBoolean);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\monitor\StringMonitorMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */