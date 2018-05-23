package com.sun.istack.internal.localization;

public abstract interface Localizable
{
  public static final String NOT_LOCALIZABLE = "\000";
  
  public abstract String getKey();
  
  public abstract Object[] getArguments();
  
  public abstract String getResourceBundleName();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\istack\internal\localization\Localizable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */