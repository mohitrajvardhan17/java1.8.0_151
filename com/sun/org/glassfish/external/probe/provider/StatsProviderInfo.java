package com.sun.org.glassfish.external.probe.provider;

public class StatsProviderInfo
{
  private String configElement;
  private PluginPoint pp;
  private String subTreeRoot;
  private Object statsProvider;
  private String configLevelStr = null;
  private final String invokerId;
  
  public StatsProviderInfo(String paramString1, PluginPoint paramPluginPoint, String paramString2, Object paramObject)
  {
    this(paramString1, paramPluginPoint, paramString2, paramObject, null);
  }
  
  public StatsProviderInfo(String paramString1, PluginPoint paramPluginPoint, String paramString2, Object paramObject, String paramString3)
  {
    configElement = paramString1;
    pp = paramPluginPoint;
    subTreeRoot = paramString2;
    statsProvider = paramObject;
    invokerId = paramString3;
  }
  
  public String getConfigElement()
  {
    return configElement;
  }
  
  public PluginPoint getPluginPoint()
  {
    return pp;
  }
  
  public String getSubTreeRoot()
  {
    return subTreeRoot;
  }
  
  public Object getStatsProvider()
  {
    return statsProvider;
  }
  
  public String getConfigLevel()
  {
    return configLevelStr;
  }
  
  public void setConfigLevel(String paramString)
  {
    configLevelStr = paramString;
  }
  
  public String getInvokerId()
  {
    return invokerId;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\glassfish\external\probe\provider\StatsProviderInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */