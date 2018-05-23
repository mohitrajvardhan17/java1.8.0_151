package javax.print;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import javax.print.attribute.AttributeSet;
import sun.awt.AppContext;

public abstract class PrintServiceLookup
{
  public PrintServiceLookup() {}
  
  private static Services getServicesForContext()
  {
    Services localServices = (Services)AppContext.getAppContext().get(Services.class);
    if (localServices == null)
    {
      localServices = new Services();
      AppContext.getAppContext().put(Services.class, localServices);
    }
    return localServices;
  }
  
  private static ArrayList getListOfLookupServices()
  {
    return getServicesForContextlistOfLookupServices;
  }
  
  private static ArrayList initListOfLookupServices()
  {
    ArrayList localArrayList = new ArrayList();
    getServicesForContextlistOfLookupServices = localArrayList;
    return localArrayList;
  }
  
  private static ArrayList getRegisteredServices()
  {
    return getServicesForContextregisteredServices;
  }
  
  private static ArrayList initRegisteredServices()
  {
    ArrayList localArrayList = new ArrayList();
    getServicesForContextregisteredServices = localArrayList;
    return localArrayList;
  }
  
  public static final PrintService[] lookupPrintServices(DocFlavor paramDocFlavor, AttributeSet paramAttributeSet)
  {
    ArrayList localArrayList = getServices(paramDocFlavor, paramAttributeSet);
    return (PrintService[])localArrayList.toArray(new PrintService[localArrayList.size()]);
  }
  
  public static final MultiDocPrintService[] lookupMultiDocPrintServices(DocFlavor[] paramArrayOfDocFlavor, AttributeSet paramAttributeSet)
  {
    ArrayList localArrayList = getMultiDocServices(paramArrayOfDocFlavor, paramAttributeSet);
    return (MultiDocPrintService[])localArrayList.toArray(new MultiDocPrintService[localArrayList.size()]);
  }
  
  public static final PrintService lookupDefaultPrintService()
  {
    Iterator localIterator = getAllLookupServices().iterator();
    while (localIterator.hasNext()) {
      try
      {
        PrintServiceLookup localPrintServiceLookup = (PrintServiceLookup)localIterator.next();
        PrintService localPrintService = localPrintServiceLookup.getDefaultPrintService();
        if (localPrintService != null) {
          return localPrintService;
        }
      }
      catch (Exception localException) {}
    }
    return null;
  }
  
  public static boolean registerServiceProvider(PrintServiceLookup paramPrintServiceLookup)
  {
    synchronized (PrintServiceLookup.class)
    {
      Iterator localIterator = getAllLookupServices().iterator();
      while (localIterator.hasNext()) {
        try
        {
          Object localObject1 = localIterator.next();
          if (localObject1.getClass() == paramPrintServiceLookup.getClass()) {
            return false;
          }
        }
        catch (Exception localException) {}
      }
      getListOfLookupServices().add(paramPrintServiceLookup);
      return true;
    }
  }
  
  public static boolean registerService(PrintService paramPrintService)
  {
    synchronized (PrintServiceLookup.class)
    {
      if ((paramPrintService instanceof StreamPrintService)) {
        return false;
      }
      ArrayList localArrayList = getRegisteredServices();
      if (localArrayList == null) {
        localArrayList = initRegisteredServices();
      } else if (localArrayList.contains(paramPrintService)) {
        return false;
      }
      localArrayList.add(paramPrintService);
      return true;
    }
  }
  
  public abstract PrintService[] getPrintServices(DocFlavor paramDocFlavor, AttributeSet paramAttributeSet);
  
  public abstract PrintService[] getPrintServices();
  
  public abstract MultiDocPrintService[] getMultiDocPrintServices(DocFlavor[] paramArrayOfDocFlavor, AttributeSet paramAttributeSet);
  
  public abstract PrintService getDefaultPrintService();
  
  private static ArrayList getAllLookupServices()
  {
    synchronized (PrintServiceLookup.class)
    {
      ArrayList localArrayList = getListOfLookupServices();
      if (localArrayList != null) {
        return localArrayList;
      }
      localArrayList = initListOfLookupServices();
      try
      {
        AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public Object run()
          {
            Iterator localIterator = ServiceLoader.load(PrintServiceLookup.class).iterator();
            ArrayList localArrayList = PrintServiceLookup.access$200();
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
  
  private static ArrayList getServices(DocFlavor paramDocFlavor, AttributeSet paramAttributeSet)
  {
    ArrayList localArrayList1 = new ArrayList();
    Iterator localIterator = getAllLookupServices().iterator();
    Object localObject;
    int i;
    while (localIterator.hasNext()) {
      try
      {
        PrintServiceLookup localPrintServiceLookup = (PrintServiceLookup)localIterator.next();
        localObject = null;
        if ((paramDocFlavor == null) && (paramAttributeSet == null)) {
          try
          {
            localObject = localPrintServiceLookup.getPrintServices();
          }
          catch (Throwable localThrowable) {}
        } else {
          localObject = localPrintServiceLookup.getPrintServices(paramDocFlavor, paramAttributeSet);
        }
        if (localObject != null) {
          for (i = 0; i < localObject.length; i++) {
            localArrayList1.add(localObject[i]);
          }
        }
      }
      catch (Exception localException) {}
    }
    ArrayList localArrayList2 = null;
    try
    {
      localObject = System.getSecurityManager();
      if (localObject != null) {
        ((SecurityManager)localObject).checkPrintJobAccess();
      }
      localArrayList2 = getRegisteredServices();
    }
    catch (SecurityException localSecurityException) {}
    if (localArrayList2 != null)
    {
      PrintService[] arrayOfPrintService = (PrintService[])localArrayList2.toArray(new PrintService[localArrayList2.size()]);
      for (i = 0; i < arrayOfPrintService.length; i++) {
        if (!localArrayList1.contains(arrayOfPrintService[i])) {
          if ((paramDocFlavor == null) && (paramAttributeSet == null)) {
            localArrayList1.add(arrayOfPrintService[i]);
          } else if (((paramDocFlavor != null) && (arrayOfPrintService[i].isDocFlavorSupported(paramDocFlavor))) || ((paramDocFlavor == null) && (null == arrayOfPrintService[i].getUnsupportedAttributes(paramDocFlavor, paramAttributeSet)))) {
            localArrayList1.add(arrayOfPrintService[i]);
          }
        }
      }
    }
    return localArrayList1;
  }
  
  private static ArrayList getMultiDocServices(DocFlavor[] paramArrayOfDocFlavor, AttributeSet paramAttributeSet)
  {
    ArrayList localArrayList1 = new ArrayList();
    Iterator localIterator = getAllLookupServices().iterator();
    Object localObject;
    int i;
    while (localIterator.hasNext()) {
      try
      {
        PrintServiceLookup localPrintServiceLookup = (PrintServiceLookup)localIterator.next();
        localObject = localPrintServiceLookup.getMultiDocPrintServices(paramArrayOfDocFlavor, paramAttributeSet);
        if (localObject != null) {
          for (i = 0; i < localObject.length; i++) {
            localArrayList1.add(localObject[i]);
          }
        }
      }
      catch (Exception localException1) {}
    }
    ArrayList localArrayList2 = null;
    try
    {
      localObject = System.getSecurityManager();
      if (localObject != null) {
        ((SecurityManager)localObject).checkPrintJobAccess();
      }
      localArrayList2 = getRegisteredServices();
    }
    catch (Exception localException2) {}
    if (localArrayList2 != null)
    {
      PrintService[] arrayOfPrintService = (PrintService[])localArrayList2.toArray(new PrintService[localArrayList2.size()]);
      for (i = 0; i < arrayOfPrintService.length; i++) {
        if (((arrayOfPrintService[i] instanceof MultiDocPrintService)) && (!localArrayList1.contains(arrayOfPrintService[i]))) {
          if ((paramArrayOfDocFlavor == null) || (paramArrayOfDocFlavor.length == 0))
          {
            localArrayList1.add(arrayOfPrintService[i]);
          }
          else
          {
            int j = 1;
            for (int k = 0; k < paramArrayOfDocFlavor.length; k++) {
              if (arrayOfPrintService[i].isDocFlavorSupported(paramArrayOfDocFlavor[k]))
              {
                if (arrayOfPrintService[i].getUnsupportedAttributes(paramArrayOfDocFlavor[k], paramAttributeSet) != null)
                {
                  j = 0;
                  break;
                }
              }
              else
              {
                j = 0;
                break;
              }
            }
            if (j != 0) {
              localArrayList1.add(arrayOfPrintService[i]);
            }
          }
        }
      }
    }
    return localArrayList1;
  }
  
  static class Services
  {
    private ArrayList listOfLookupServices = null;
    private ArrayList registeredServices = null;
    
    Services() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\PrintServiceLookup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */