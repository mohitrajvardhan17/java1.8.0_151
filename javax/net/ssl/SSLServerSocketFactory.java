package javax.net.ssl;

import java.io.PrintStream;
import java.security.NoSuchAlgorithmException;
import javax.net.ServerSocketFactory;

public abstract class SSLServerSocketFactory
  extends ServerSocketFactory
{
  private static SSLServerSocketFactory theFactory;
  private static boolean propertyChecked;
  
  private static void log(String paramString)
  {
    if (SSLSocketFactory.DEBUG) {
      System.out.println(paramString);
    }
  }
  
  protected SSLServerSocketFactory() {}
  
  public static synchronized ServerSocketFactory getDefault()
  {
    if (theFactory != null) {
      return theFactory;
    }
    if (!propertyChecked)
    {
      propertyChecked = true;
      String str = SSLSocketFactory.getSecurityProperty("ssl.ServerSocketFactory.provider");
      if (str != null)
      {
        log("setting up default SSLServerSocketFactory");
        try
        {
          Class localClass = null;
          try
          {
            localClass = Class.forName(str);
          }
          catch (ClassNotFoundException localClassNotFoundException)
          {
            ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
            if (localClassLoader != null) {
              localClass = localClassLoader.loadClass(str);
            }
          }
          log("class " + str + " is loaded");
          SSLServerSocketFactory localSSLServerSocketFactory = (SSLServerSocketFactory)localClass.newInstance();
          log("instantiated an instance of class " + str);
          theFactory = localSSLServerSocketFactory;
          return localSSLServerSocketFactory;
        }
        catch (Exception localException)
        {
          log("SSLServerSocketFactory instantiation failed: " + localException);
          theFactory = new DefaultSSLServerSocketFactory(localException);
          return theFactory;
        }
      }
    }
    try
    {
      return SSLContext.getDefault().getServerSocketFactory();
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      return new DefaultSSLServerSocketFactory(localNoSuchAlgorithmException);
    }
  }
  
  public abstract String[] getDefaultCipherSuites();
  
  public abstract String[] getSupportedCipherSuites();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\ssl\SSLServerSocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */