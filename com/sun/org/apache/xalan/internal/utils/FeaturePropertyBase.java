package com.sun.org.apache.xalan.internal.utils;

public abstract class FeaturePropertyBase
{
  String[] values = null;
  State[] states = { State.DEFAULT, State.DEFAULT };
  
  public FeaturePropertyBase() {}
  
  public void setValue(Enum paramEnum, State paramState, String paramString)
  {
    if (paramState.compareTo(states[paramEnum.ordinal()]) >= 0)
    {
      values[paramEnum.ordinal()] = paramString;
      states[paramEnum.ordinal()] = paramState;
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
  
  public boolean setValue(String paramString, State paramState, boolean paramBoolean)
  {
    int i = getIndex(paramString);
    if (i > -1)
    {
      if (paramBoolean) {
        setValue(i, paramState, "true");
      } else {
        setValue(i, paramState, "false");
      }
      return true;
    }
    return false;
  }
  
  public String getValue(Enum paramEnum)
  {
    return values[paramEnum.ordinal()];
  }
  
  public String getValue(String paramString)
  {
    int i = getIndex(paramString);
    if (i > -1) {
      return getValueByIndex(i);
    }
    return null;
  }
  
  public String getValueAsString(String paramString)
  {
    int i = getIndex(paramString);
    if (i > -1) {
      return getValueByIndex(i);
    }
    return null;
  }
  
  public String getValueByIndex(int paramInt)
  {
    return values[paramInt];
  }
  
  public abstract int getIndex(String paramString);
  
  public <E extends Enum<E>> int getIndex(Class<E> paramClass, String paramString)
  {
    for (Enum localEnum : (Enum[])paramClass.getEnumConstants()) {
      if (localEnum.toString().equals(paramString)) {
        return localEnum.ordinal();
      }
    }
    return -1;
  }
  
  void getSystemProperty(Enum paramEnum, String paramString)
  {
    try
    {
      String str = SecuritySupport.getSystemProperty(paramString);
      if (str != null)
      {
        values[paramEnum.ordinal()] = str;
        states[paramEnum.ordinal()] = State.SYSTEMPROPERTY;
        return;
      }
      str = SecuritySupport.readJAXPProperty(paramString);
      if (str != null)
      {
        values[paramEnum.ordinal()] = str;
        states[paramEnum.ordinal()] = State.JAXPDOTPROPERTIES;
      }
    }
    catch (NumberFormatException localNumberFormatException) {}
  }
  
  public static enum State
  {
    DEFAULT,  FSP,  JAXPDOTPROPERTIES,  SYSTEMPROPERTY,  APIPROPERTY;
    
    private State() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\utils\FeaturePropertyBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */