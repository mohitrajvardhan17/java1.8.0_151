package com.sun.org.glassfish.external.probe.provider;

public abstract interface StatsProviderManagerDelegate
{
  public abstract void register(StatsProviderInfo paramStatsProviderInfo);
  
  public abstract void unregister(Object paramObject);
  
  public abstract boolean hasListeners(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\glassfish\external\probe\provider\StatsProviderManagerDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */