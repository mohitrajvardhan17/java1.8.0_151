package com.sun.istack.internal.localization;

public final class NullLocalizable
  implements Localizable
{
  private final String msg;
  
  public NullLocalizable(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException();
    }
    msg = paramString;
  }
  
  public String getKey()
  {
    return "\000";
  }
  
  public Object[] getArguments()
  {
    return new Object[] { msg };
  }
  
  public String getResourceBundleName()
  {
    return "";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\istack\internal\localization\NullLocalizable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */