package com.sun.org.glassfish.external.probe.provider;

public enum PluginPoint
{
  SERVER("server", "server"),  APPLICATIONS("applications", "server/applications");
  
  String name;
  String path;
  
  private PluginPoint(String paramString1, String paramString2)
  {
    name = paramString1;
    path = paramString2;
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getPath()
  {
    return path;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\glassfish\external\probe\provider\PluginPoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */