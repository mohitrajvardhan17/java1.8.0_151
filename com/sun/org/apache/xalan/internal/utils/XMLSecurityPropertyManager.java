package com.sun.org.apache.xalan.internal.utils;

public final class XMLSecurityPropertyManager
  extends FeaturePropertyBase
{
  public XMLSecurityPropertyManager()
  {
    values = new String[Property.values().length];
    for (Property localProperty : Property.values()) {
      values[localProperty.ordinal()] = localProperty.defaultValue();
    }
    readSystemProperties();
  }
  
  public int getIndex(String paramString)
  {
    for (Property localProperty : ) {
      if (localProperty.equalsName(paramString)) {
        return localProperty.ordinal();
      }
    }
    return -1;
  }
  
  private void readSystemProperties()
  {
    getSystemProperty(Property.ACCESS_EXTERNAL_DTD, "javax.xml.accessExternalDTD");
    getSystemProperty(Property.ACCESS_EXTERNAL_STYLESHEET, "javax.xml.accessExternalStylesheet");
  }
  
  public static enum Property
  {
    ACCESS_EXTERNAL_DTD("http://javax.xml.XMLConstants/property/accessExternalDTD", "all"),  ACCESS_EXTERNAL_STYLESHEET("http://javax.xml.XMLConstants/property/accessExternalStylesheet", "all");
    
    final String name;
    final String defaultValue;
    
    private Property(String paramString1, String paramString2)
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
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\utils\XMLSecurityPropertyManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */