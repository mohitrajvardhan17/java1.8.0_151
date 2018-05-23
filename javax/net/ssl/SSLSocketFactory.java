package javax.net.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.security.AccessController;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.security.Security;
import java.util.Locale;
import javax.net.SocketFactory;
import sun.security.action.GetPropertyAction;

public abstract class SSLSocketFactory
  extends SocketFactory
{
  private static SSLSocketFactory theFactory;
  private static boolean propertyChecked;
  static final boolean DEBUG;
  
  private static void log(String paramString)
  {
    if (DEBUG) {
      System.out.println(paramString);
    }
  }
  
  public SSLSocketFactory() {}
  
  public static synchronized SocketFactory getDefault()
  {
    if (theFactory != null) {
      return theFactory;
    }
    if (!propertyChecked)
    {
      propertyChecked = true;
      String str = getSecurityProperty("ssl.SocketFactory.provider");
      if (str != null)
      {
        log("setting up default SSLSocketFactory");
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
          SSLSocketFactory localSSLSocketFactory = (SSLSocketFactory)localClass.newInstance();
          log("instantiated an instance of class " + str);
          theFactory = localSSLSocketFactory;
          return localSSLSocketFactory;
        }
        catch (Exception localException)
        {
          log("SSLSocketFactory instantiation failed: " + localException.toString());
          theFactory = new DefaultSSLSocketFactory(localException);
          return theFactory;
        }
      }
    }
    try
    {
      return SSLContext.getDefault().getSocketFactory();
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      return new DefaultSSLSocketFactory(localNoSuchAlgorithmException);
    }
  }
  
  static String getSecurityProperty(String paramString)
  {
    (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        String str = Security.getProperty(val$name);
        if (str != null)
        {
          str = str.trim();
          if (str.length() == 0) {
            str = null;
          }
        }
        return str;
      }
    });
  }
  
  public abstract String[] getDefaultCipherSuites();
  
  public abstract String[] getSupportedCipherSuites();
  
  public abstract Socket createSocket(Socket paramSocket, String paramString, int paramInt, boolean paramBoolean)
    throws IOException;
  
  public Socket createSocket(Socket paramSocket, InputStream paramInputStream, boolean paramBoolean)
    throws IOException
  {
    throw new UnsupportedOperationException();
  }
  
  static
  {
    String str = ((String)AccessController.doPrivileged(new GetPropertyAction("javax.net.debug", ""))).toLowerCase(Locale.ENGLISH);
    DEBUG = (str.contains("all")) || (str.contains("ssl"));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\net\ssl\SSLSocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */