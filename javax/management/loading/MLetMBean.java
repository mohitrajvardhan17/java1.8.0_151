package javax.management.loading;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import javax.management.ServiceNotFoundException;

public abstract interface MLetMBean
{
  public abstract Set<Object> getMBeansFromURL(String paramString)
    throws ServiceNotFoundException;
  
  public abstract Set<Object> getMBeansFromURL(URL paramURL)
    throws ServiceNotFoundException;
  
  public abstract void addURL(URL paramURL);
  
  public abstract void addURL(String paramString)
    throws ServiceNotFoundException;
  
  public abstract URL[] getURLs();
  
  public abstract URL getResource(String paramString);
  
  public abstract InputStream getResourceAsStream(String paramString);
  
  public abstract Enumeration<URL> getResources(String paramString)
    throws IOException;
  
  public abstract String getLibraryDirectory();
  
  public abstract void setLibraryDirectory(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\loading\MLetMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */