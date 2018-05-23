package com.sun.xml.internal.org.jvnet.mimepull;

import java.util.Properties;

final class PropUtil
{
  private PropUtil() {}
  
  public static boolean getBooleanSystemProperty(String paramString, boolean paramBoolean)
  {
    try
    {
      return getBoolean(getProp(System.getProperties(), paramString), paramBoolean);
    }
    catch (SecurityException localSecurityException1)
    {
      try
      {
        String str = System.getProperty(paramString);
        if (str == null) {
          return paramBoolean;
        }
        if (paramBoolean) {
          return !str.equalsIgnoreCase("false");
        }
        return str.equalsIgnoreCase("true");
      }
      catch (SecurityException localSecurityException2) {}
    }
    return paramBoolean;
  }
  
  private static Object getProp(Properties paramProperties, String paramString)
  {
    Object localObject = paramProperties.get(paramString);
    if (localObject != null) {
      return localObject;
    }
    return paramProperties.getProperty(paramString);
  }
  
  private static boolean getBoolean(Object paramObject, boolean paramBoolean)
  {
    if (paramObject == null) {
      return paramBoolean;
    }
    if ((paramObject instanceof String))
    {
      if (paramBoolean) {
        return !((String)paramObject).equalsIgnoreCase("false");
      }
      return ((String)paramObject).equalsIgnoreCase("true");
    }
    if ((paramObject instanceof Boolean)) {
      return ((Boolean)paramObject).booleanValue();
    }
    return paramBoolean;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\PropUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */