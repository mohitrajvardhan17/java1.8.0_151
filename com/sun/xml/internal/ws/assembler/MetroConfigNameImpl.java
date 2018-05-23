package com.sun.xml.internal.ws.assembler;

public class MetroConfigNameImpl
  implements MetroConfigName
{
  private final String defaultFileName;
  private final String appFileName;
  
  public MetroConfigNameImpl(String paramString1, String paramString2)
  {
    defaultFileName = paramString1;
    appFileName = paramString2;
  }
  
  public String getDefaultFileName()
  {
    return defaultFileName;
  }
  
  public String getAppFileName()
  {
    return appFileName;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\assembler\MetroConfigNameImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */