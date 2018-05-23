package com.sun.istack.internal.localization;

import java.util.Arrays;

public final class LocalizableMessage
  implements Localizable
{
  private final String _bundlename;
  private final String _key;
  private final Object[] _args;
  
  public LocalizableMessage(String paramString1, String paramString2, Object... paramVarArgs)
  {
    _bundlename = paramString1;
    _key = paramString2;
    if (paramVarArgs == null) {
      paramVarArgs = new Object[0];
    }
    _args = paramVarArgs;
  }
  
  public String getKey()
  {
    return _key;
  }
  
  public Object[] getArguments()
  {
    return Arrays.copyOf(_args, _args.length);
  }
  
  public String getResourceBundleName()
  {
    return _bundlename;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\istack\internal\localization\LocalizableMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */