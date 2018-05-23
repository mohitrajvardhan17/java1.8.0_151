package com.sun.org.apache.xerces.internal.utils;

public final class XMLSecurityPropertyManager
{
  private final String[] values = new String[Property.values().length];
  private State[] states = { State.DEFAULT, State.DEFAULT };
  
  public XMLSecurityPropertyManager()
  {
    for (Property localProperty : Property.values()) {
      values[localProperty.ordinal()] = localProperty.defaultValue();
    }
    readSystemProperties();
  }
  
  public boolean setValue(String paramString, State paramState, Object paramObject)
  {
    int i = getIndex(paramString);
    if (i > -1)
    {
      setValue(i, paramState, (String)paramObject);
      return true;
    }
    return false;
  }
  
  public void setValue(Property paramProperty, State paramState, String paramString)
  {
    if (paramState.compareTo(states[paramProperty.ordinal()]) >= 0)
    {
      values[paramProperty.ordinal()] = paramString;
      states[paramProperty.ordinal()] = paramState;
    }
  }
  
  public void setValue(int paramInt, State paramState, String paramString)
  {
    if (paramState.compareTo(states[paramInt]) >= 0)
    {
      values[paramInt] = paramString;
      states[paramInt] = paramState;
    }
  }
  
  public String getValue(String paramString)
  {
    int i = getIndex(paramString);
    if (i > -1) {
      return getValueByIndex(i);
    }
    return null;
  }
  
  public String getValue(Property paramProperty)
  {
    return values[paramProperty.ordinal()];
  }
  
  public String getValueByIndex(int paramInt)
  {
    return values[paramInt];
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
    getSystemProperty(Property.ACCESS_EXTERNAL_SCHEMA, "javax.xml.accessExternalSchema");
  }
  
  private void getSystemProperty(Property paramProperty, String paramString)
  {
    try
    {
      String str = SecuritySupport.getSystemProperty(paramString);
      if (str != null)
      {
        values[paramProperty.ordinal()] = str;
        states[paramProperty.ordinal()] = State.SYSTEMPROPERTY;
        return;
      }
      str = SecuritySupport.readJAXPProperty(paramString);
      if (str != null)
      {
        values[paramProperty.ordinal()] = str;
        states[paramProperty.ordinal()] = State.JAXPDOTPROPERTIES;
      }
    }
    catch (NumberFormatException localNumberFormatException) {}
  }
  
  public static enum Property
  {
    ACCESS_EXTERNAL_DTD("http://javax.xml.XMLConstants/property/accessExternalDTD", "all"),  ACCESS_EXTERNAL_SCHEMA("http://javax.xml.XMLConstants/property/accessExternalSchema", "all");
    
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
  
  public static enum State
  {
    DEFAULT,  FSP,  JAXPDOTPROPERTIES,  SYSTEMPROPERTY,  APIPROPERTY;
    
    private State() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\utils\XMLSecurityPropertyManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */