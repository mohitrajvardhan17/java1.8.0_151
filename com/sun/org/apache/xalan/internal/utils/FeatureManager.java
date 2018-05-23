package com.sun.org.apache.xalan.internal.utils;

public final class FeatureManager
  extends FeaturePropertyBase
{
  public FeatureManager()
  {
    values = new String[Feature.values().length];
    for (Feature localFeature : Feature.values()) {
      values[localFeature.ordinal()] = localFeature.defaultValue();
    }
    readSystemProperties();
  }
  
  public boolean isFeatureEnabled(Feature paramFeature)
  {
    return Boolean.parseBoolean(values[paramFeature.ordinal()]);
  }
  
  public boolean isFeatureEnabled(String paramString)
  {
    return Boolean.parseBoolean(values[getIndex(paramString)]);
  }
  
  public int getIndex(String paramString)
  {
    for (Feature localFeature : ) {
      if (localFeature.equalsName(paramString)) {
        return localFeature.ordinal();
      }
    }
    return -1;
  }
  
  private void readSystemProperties()
  {
    getSystemProperty(Feature.ORACLE_ENABLE_EXTENSION_FUNCTION, "javax.xml.enableExtensionFunctions");
  }
  
  public static enum Feature
  {
    ORACLE_ENABLE_EXTENSION_FUNCTION("http://www.oracle.com/xml/jaxp/properties/enableExtensionFunctions", "true");
    
    final String name;
    final String defaultValue;
    
    private Feature(String paramString1, String paramString2)
    {
      name = paramString1;
      defaultValue = paramString2;
    }
    
    public boolean equalsName(String paramString)
    {
      return paramString == null ? false : name.equals(paramString);
    }
    
    String defaultValue()
    {
      return defaultValue;
    }
  }
  
  public static enum State
  {
    DEFAULT,  FSP,  JAXPDOTPROPERTIES,  SYSTEMPROPERTY,  APIPROPERTY;
    
    private State() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\utils\FeatureManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */