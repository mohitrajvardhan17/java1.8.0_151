package com.sun.xml.internal.ws.policy.privateutil;

import com.sun.istack.internal.logging.Logger;
import java.lang.reflect.Field;

public final class PolicyLogger
  extends Logger
{
  private static final String POLICY_PACKAGE_ROOT = "com.sun.xml.internal.ws.policy";
  
  private PolicyLogger(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }
  
  public static PolicyLogger getLogger(Class<?> paramClass)
  {
    String str = paramClass.getName();
    if (str.startsWith("com.sun.xml.internal.ws.policy")) {
      return new PolicyLogger(getLoggingSubsystemName() + str.substring("com.sun.xml.internal.ws.policy".length()), str);
    }
    return new PolicyLogger(getLoggingSubsystemName() + "." + str, str);
  }
  
  private static String getLoggingSubsystemName()
  {
    String str = "wspolicy";
    try
    {
      Class localClass = Class.forName("com.sun.xml.internal.ws.util.Constants");
      Field localField = localClass.getField("LoggingDomain");
      Object localObject = localField.get(null);
      str = localObject.toString().concat(".wspolicy");
    }
    catch (RuntimeException localRuntimeException) {}catch (Exception localException) {}
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\privateutil\PolicyLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */