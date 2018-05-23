package java.rmi.server;

import java.net.MalformedURLException;
import java.net.URL;

@Deprecated
public abstract interface LoaderHandler
{
  public static final String packagePrefix = "sun.rmi.server";
  
  @Deprecated
  public abstract Class<?> loadClass(String paramString)
    throws MalformedURLException, ClassNotFoundException;
  
  @Deprecated
  public abstract Class<?> loadClass(URL paramURL, String paramString)
    throws MalformedURLException, ClassNotFoundException;
  
  @Deprecated
  public abstract Object getSecurityContext(ClassLoader paramClassLoader);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\server\LoaderHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */