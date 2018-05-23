package com.sun.imageio.plugins.common;

public final class I18N
  extends I18NImpl
{
  private static final String resource_name = "iio-plugin.properties";
  
  public I18N() {}
  
  public static String getString(String paramString)
  {
    return getString("com.sun.imageio.plugins.common.I18N", "iio-plugin.properties", paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\common\I18N.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */