package com.sun.istack.internal.localization;

public class LocalizableMessageFactory
{
  private final String _bundlename;
  
  public LocalizableMessageFactory(String paramString)
  {
    _bundlename = paramString;
  }
  
  public Localizable getMessage(String paramString, Object... paramVarArgs)
  {
    return new LocalizableMessage(_bundlename, paramString, paramVarArgs);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\istack\internal\localization\LocalizableMessageFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */