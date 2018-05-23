package javax.print;

import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import sun.awt.AppContext;

public abstract class StreamPrintServiceFactory
{
  public StreamPrintServiceFactory() {}
  
  private static Services getServices()
  {
    Services localServices = (Services)AppContext.getAppContext().get(Services.class);
    if (localServices == null)
    {
      localServices = new Services();
      AppContext.getAppContext().put(Services.class, localServices);
    }
    return localServices;
  }
  
  private static ArrayList getListOfFactories()
  {
    return getServiceslistOfFactories;
  }
  
  private static ArrayList initListOfFactories()
  {
    ArrayList localArrayList = new ArrayList();
    getServiceslistOfFactories = localArrayList;
    return localArrayList;
  }
  
  public static StreamPrintServiceFactory[] lookupStreamPrintServiceFactories(DocFlavor paramDocFlavor, String paramString)
  {
    ArrayList localArrayList = getFactories(paramDocFlavor, paramString);
    return (StreamPrintServiceFactory[])localArrayList.toArray(new StreamPrintServiceFactory[localArrayList.size()]);
  }
  
  public abstract String getOutputFormat();
  
  public abstract DocFlavor[] getSupportedDocFlavors();
  
  public abstract StreamPrintService getPrintService(OutputStream paramOutputStream);
  
  private static ArrayList getAllFactories()
  {
    synchronized (StreamPrintServiceFactory.class)
    {
      ArrayList localArrayList = getListOfFactories();
      if (localArrayList != null) {
        return localArrayList;
      }
      localArrayList = initListOfFactories();
      try
      {
        AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public Object run()
          {
            Iterator localIterator = ServiceLoader.load(StreamPrintServiceFactory.class).iterator();
            ArrayList localArrayList = StreamPrintServiceFactory.access$100();
            while (localIterator.hasNext()) {
              try
              {
                localArrayList.add(localIterator.next());
              }
              catch (ServiceConfigurationError localServiceConfigurationError)
              {
                if (System.getSecurityManager() != null) {
                  localServiceConfigurationError.printStackTrace();
                } else {
                  throw localServiceConfigurationError;
                }
              }
            }
            return null;
          }
        });
      }
      catch (PrivilegedActionException localPrivilegedActionException) {}
      return localArrayList;
    }
  }
  
  private static boolean isMember(DocFlavor paramDocFlavor, DocFlavor[] paramArrayOfDocFlavor)
  {
    for (int i = 0; i < paramArrayOfDocFlavor.length; i++) {
      if (paramDocFlavor.equals(paramArrayOfDocFlavor[i])) {
        return true;
      }
    }
    return false;
  }
  
  private static ArrayList getFactories(DocFlavor paramDocFlavor, String paramString)
  {
    if ((paramDocFlavor == null) && (paramString == null)) {
      return getAllFactories();
    }
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = getAllFactories().iterator();
    while (localIterator.hasNext())
    {
      StreamPrintServiceFactory localStreamPrintServiceFactory = (StreamPrintServiceFactory)localIterator.next();
      if (((paramString == null) || (paramString.equalsIgnoreCase(localStreamPrintServiceFactory.getOutputFormat()))) && ((paramDocFlavor == null) || (isMember(paramDocFlavor, localStreamPrintServiceFactory.getSupportedDocFlavors())))) {
        localArrayList.add(localStreamPrintServiceFactory);
      }
    }
    return localArrayList;
  }
  
  static class Services
  {
    private ArrayList listOfFactories = null;
    
    Services() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\StreamPrintServiceFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */