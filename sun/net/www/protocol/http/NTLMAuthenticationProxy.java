package sun.net.www.protocol.http;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.PasswordAuthentication;
import java.net.URL;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

class NTLMAuthenticationProxy
{
  private static Method supportsTA;
  private static Method isTrustedSite;
  private static final String clazzStr = "sun.net.www.protocol.http.ntlm.NTLMAuthentication";
  private static final String supportsTAStr = "supportsTransparentAuth";
  private static final String isTrustedSiteStr = "isTrustedSite";
  static final NTLMAuthenticationProxy proxy = ;
  static final boolean supported = proxy != null;
  static final boolean supportsTransparentAuth = supported ? supportsTransparentAuth() : false;
  private final Constructor<? extends AuthenticationInfo> threeArgCtr;
  private final Constructor<? extends AuthenticationInfo> fiveArgCtr;
  
  private NTLMAuthenticationProxy(Constructor<? extends AuthenticationInfo> paramConstructor1, Constructor<? extends AuthenticationInfo> paramConstructor2)
  {
    threeArgCtr = paramConstructor1;
    fiveArgCtr = paramConstructor2;
  }
  
  AuthenticationInfo create(boolean paramBoolean, URL paramURL, PasswordAuthentication paramPasswordAuthentication)
  {
    try
    {
      return (AuthenticationInfo)threeArgCtr.newInstance(new Object[] { Boolean.valueOf(paramBoolean), paramURL, paramPasswordAuthentication });
    }
    catch (ReflectiveOperationException localReflectiveOperationException)
    {
      finest(localReflectiveOperationException);
    }
    return null;
  }
  
  AuthenticationInfo create(boolean paramBoolean, String paramString, int paramInt, PasswordAuthentication paramPasswordAuthentication)
  {
    try
    {
      return (AuthenticationInfo)fiveArgCtr.newInstance(new Object[] { Boolean.valueOf(paramBoolean), paramString, Integer.valueOf(paramInt), paramPasswordAuthentication });
    }
    catch (ReflectiveOperationException localReflectiveOperationException)
    {
      finest(localReflectiveOperationException);
    }
    return null;
  }
  
  private static boolean supportsTransparentAuth()
  {
    try
    {
      return ((Boolean)supportsTA.invoke(null, new Object[0])).booleanValue();
    }
    catch (ReflectiveOperationException localReflectiveOperationException)
    {
      finest(localReflectiveOperationException);
    }
    return false;
  }
  
  public static boolean isTrustedSite(URL paramURL)
  {
    try
    {
      return ((Boolean)isTrustedSite.invoke(null, new Object[] { paramURL })).booleanValue();
    }
    catch (ReflectiveOperationException localReflectiveOperationException)
    {
      finest(localReflectiveOperationException);
    }
    return false;
  }
  
  private static NTLMAuthenticationProxy tryLoadNTLMAuthentication()
  {
    try
    {
      Class localClass = Class.forName("sun.net.www.protocol.http.ntlm.NTLMAuthentication", true, null);
      if (localClass != null)
      {
        Constructor localConstructor1 = localClass.getConstructor(new Class[] { Boolean.TYPE, URL.class, PasswordAuthentication.class });
        Constructor localConstructor2 = localClass.getConstructor(new Class[] { Boolean.TYPE, String.class, Integer.TYPE, PasswordAuthentication.class });
        supportsTA = localClass.getDeclaredMethod("supportsTransparentAuth", new Class[0]);
        isTrustedSite = localClass.getDeclaredMethod("isTrustedSite", new Class[] { URL.class });
        return new NTLMAuthenticationProxy(localConstructor1, localConstructor2);
      }
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      finest(localClassNotFoundException);
    }
    catch (ReflectiveOperationException localReflectiveOperationException)
    {
      throw new AssertionError(localReflectiveOperationException);
    }
    return null;
  }
  
  static void finest(Exception paramException)
  {
    PlatformLogger localPlatformLogger = HttpURLConnection.getHttpLogger();
    if (localPlatformLogger.isLoggable(PlatformLogger.Level.FINEST)) {
      localPlatformLogger.finest("NTLMAuthenticationProxy: " + paramException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\protocol\http\NTLMAuthenticationProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */