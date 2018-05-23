package sun.security.jca;

import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.Provider.Service;
import java.security.Security;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import sun.security.util.Debug;

public final class ProviderList
{
  static final Debug debug = Debug.getInstance("jca", "ProviderList");
  private static final ProviderConfig[] PC0 = new ProviderConfig[0];
  private static final Provider[] P0 = new Provider[0];
  static final ProviderList EMPTY = new ProviderList(PC0, true);
  private static final Provider EMPTY_PROVIDER = new Provider("##Empty##", 1.0D, "initialization in progress")
  {
    private static final long serialVersionUID = 1151354171352296389L;
    
    public Provider.Service getService(String paramAnonymousString1, String paramAnonymousString2)
    {
      return null;
    }
  };
  private final ProviderConfig[] configs;
  private volatile boolean allLoaded;
  private final List<Provider> userList = new AbstractList()
  {
    public int size()
    {
      return configs.length;
    }
    
    public Provider get(int paramAnonymousInt)
    {
      return getProvider(paramAnonymousInt);
    }
  };
  
  static ProviderList fromSecurityProperties()
  {
    (ProviderList)AccessController.doPrivileged(new PrivilegedAction()
    {
      public ProviderList run()
      {
        return new ProviderList(null);
      }
    });
  }
  
  public static ProviderList add(ProviderList paramProviderList, Provider paramProvider)
  {
    return insertAt(paramProviderList, paramProvider, -1);
  }
  
  public static ProviderList insertAt(ProviderList paramProviderList, Provider paramProvider, int paramInt)
  {
    if (paramProviderList.getProvider(paramProvider.getName()) != null) {
      return paramProviderList;
    }
    ArrayList localArrayList = new ArrayList(Arrays.asList(configs));
    int i = localArrayList.size();
    if ((paramInt < 0) || (paramInt > i)) {
      paramInt = i;
    }
    localArrayList.add(paramInt, new ProviderConfig(paramProvider));
    return new ProviderList((ProviderConfig[])localArrayList.toArray(PC0), true);
  }
  
  public static ProviderList remove(ProviderList paramProviderList, String paramString)
  {
    if (paramProviderList.getProvider(paramString) == null) {
      return paramProviderList;
    }
    ProviderConfig[] arrayOfProviderConfig1 = new ProviderConfig[paramProviderList.size() - 1];
    int i = 0;
    for (ProviderConfig localProviderConfig : configs) {
      if (!localProviderConfig.getProvider().getName().equals(paramString)) {
        arrayOfProviderConfig1[(i++)] = localProviderConfig;
      }
    }
    return new ProviderList(arrayOfProviderConfig1, true);
  }
  
  public static ProviderList newList(Provider... paramVarArgs)
  {
    ProviderConfig[] arrayOfProviderConfig = new ProviderConfig[paramVarArgs.length];
    for (int i = 0; i < paramVarArgs.length; i++) {
      arrayOfProviderConfig[i] = new ProviderConfig(paramVarArgs[i]);
    }
    return new ProviderList(arrayOfProviderConfig, true);
  }
  
  private ProviderList(ProviderConfig[] paramArrayOfProviderConfig, boolean paramBoolean)
  {
    configs = paramArrayOfProviderConfig;
    allLoaded = paramBoolean;
  }
  
  private ProviderList()
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 1;; i++)
    {
      String str1 = Security.getProperty("security.provider." + i);
      if (str1 == null) {
        break;
      }
      str1 = str1.trim();
      if (str1.length() == 0)
      {
        System.err.println("invalid entry for security.provider." + i);
        break;
      }
      int j = str1.indexOf(' ');
      ProviderConfig localProviderConfig;
      if (j == -1)
      {
        localProviderConfig = new ProviderConfig(str1);
      }
      else
      {
        String str2 = str1.substring(0, j);
        String str3 = str1.substring(j + 1).trim();
        localProviderConfig = new ProviderConfig(str2, str3);
      }
      if (!localArrayList.contains(localProviderConfig)) {
        localArrayList.add(localProviderConfig);
      }
    }
    configs = ((ProviderConfig[])localArrayList.toArray(PC0));
    if (debug != null) {
      debug.println("provider configuration: " + localArrayList);
    }
  }
  
  ProviderList getJarList(String[] paramArrayOfString)
  {
    ArrayList localArrayList = new ArrayList();
    for (String str : paramArrayOfString)
    {
      Object localObject2 = new ProviderConfig(str);
      for (ProviderConfig localProviderConfig : configs) {
        if (localProviderConfig.equals(localObject2))
        {
          localObject2 = localProviderConfig;
          break;
        }
      }
      localArrayList.add(localObject2);
    }
    ??? = (ProviderConfig[])localArrayList.toArray(PC0);
    return new ProviderList((ProviderConfig[])???, false);
  }
  
  public int size()
  {
    return configs.length;
  }
  
  Provider getProvider(int paramInt)
  {
    Provider localProvider = configs[paramInt].getProvider();
    return localProvider != null ? localProvider : EMPTY_PROVIDER;
  }
  
  public List<Provider> providers()
  {
    return userList;
  }
  
  private ProviderConfig getProviderConfig(String paramString)
  {
    int i = getIndex(paramString);
    return i != -1 ? configs[i] : null;
  }
  
  public Provider getProvider(String paramString)
  {
    ProviderConfig localProviderConfig = getProviderConfig(paramString);
    return localProviderConfig == null ? null : localProviderConfig.getProvider();
  }
  
  public int getIndex(String paramString)
  {
    for (int i = 0; i < configs.length; i++)
    {
      Provider localProvider = getProvider(i);
      if (localProvider.getName().equals(paramString)) {
        return i;
      }
    }
    return -1;
  }
  
  private int loadAll()
  {
    if (allLoaded) {
      return configs.length;
    }
    if (debug != null)
    {
      debug.println("Loading all providers");
      new Exception("Call trace").printStackTrace();
    }
    int i = 0;
    for (int j = 0; j < configs.length; j++)
    {
      Provider localProvider = configs[j].getProvider();
      if (localProvider != null) {
        i++;
      }
    }
    if (i == configs.length) {
      allLoaded = true;
    }
    return i;
  }
  
  ProviderList removeInvalid()
  {
    int i = loadAll();
    if (i == configs.length) {
      return this;
    }
    ProviderConfig[] arrayOfProviderConfig = new ProviderConfig[i];
    int j = 0;
    int k = 0;
    while (j < configs.length)
    {
      ProviderConfig localProviderConfig = configs[j];
      if (localProviderConfig.isLoaded()) {
        arrayOfProviderConfig[(k++)] = localProviderConfig;
      }
      j++;
    }
    return new ProviderList(arrayOfProviderConfig, true);
  }
  
  public Provider[] toArray()
  {
    return (Provider[])providers().toArray(P0);
  }
  
  public String toString()
  {
    return Arrays.asList(configs).toString();
  }
  
  public Provider.Service getService(String paramString1, String paramString2)
  {
    for (int i = 0; i < configs.length; i++)
    {
      Provider localProvider = getProvider(i);
      Provider.Service localService = localProvider.getService(paramString1, paramString2);
      if (localService != null) {
        return localService;
      }
    }
    return null;
  }
  
  public List<Provider.Service> getServices(String paramString1, String paramString2)
  {
    return new ServiceList(paramString1, paramString2);
  }
  
  @Deprecated
  public List<Provider.Service> getServices(String paramString, List<String> paramList)
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      localArrayList.add(new ServiceId(paramString, str));
    }
    return getServices(localArrayList);
  }
  
  public List<Provider.Service> getServices(List<ServiceId> paramList)
  {
    return new ServiceList(paramList);
  }
  
  private final class ServiceList
    extends AbstractList<Provider.Service>
  {
    private final String type;
    private final String algorithm;
    private final List<ServiceId> ids;
    private Provider.Service firstService;
    private List<Provider.Service> services;
    private int providerIndex;
    
    ServiceList(String paramString1, String paramString2)
    {
      type = paramString1;
      algorithm = paramString2;
      ids = null;
    }
    
    ServiceList()
    {
      type = null;
      algorithm = null;
      List localList;
      ids = localList;
    }
    
    private void addService(Provider.Service paramService)
    {
      if (firstService == null)
      {
        firstService = paramService;
      }
      else
      {
        if (services == null)
        {
          services = new ArrayList(4);
          services.add(firstService);
        }
        services.add(paramService);
      }
    }
    
    private Provider.Service tryGet(int paramInt)
    {
      for (;;)
      {
        if ((paramInt == 0) && (firstService != null)) {
          return firstService;
        }
        if ((services != null) && (services.size() > paramInt)) {
          return (Provider.Service)services.get(paramInt);
        }
        if (providerIndex >= configs.length) {
          return null;
        }
        Provider localProvider = getProvider(providerIndex++);
        Object localObject;
        if (type != null)
        {
          localObject = localProvider.getService(type, algorithm);
          if (localObject != null) {
            addService((Provider.Service)localObject);
          }
        }
        else
        {
          localObject = ids.iterator();
          while (((Iterator)localObject).hasNext())
          {
            ServiceId localServiceId = (ServiceId)((Iterator)localObject).next();
            Provider.Service localService = localProvider.getService(type, algorithm);
            if (localService != null) {
              addService(localService);
            }
          }
        }
      }
    }
    
    public Provider.Service get(int paramInt)
    {
      Provider.Service localService = tryGet(paramInt);
      if (localService == null) {
        throw new IndexOutOfBoundsException();
      }
      return localService;
    }
    
    public int size()
    {
      if (services != null) {
        i = services.size();
      }
      for (int i = firstService != null ? 1 : 0; tryGet(i) != null; i++) {}
      return i;
    }
    
    public boolean isEmpty()
    {
      return tryGet(0) == null;
    }
    
    public Iterator<Provider.Service> iterator()
    {
      new Iterator()
      {
        int index;
        
        public boolean hasNext()
        {
          return ProviderList.ServiceList.this.tryGet(index) != null;
        }
        
        public Provider.Service next()
        {
          Provider.Service localService = ProviderList.ServiceList.this.tryGet(index);
          if (localService == null) {
            throw new NoSuchElementException();
          }
          index += 1;
          return localService;
        }
        
        public void remove()
        {
          throw new UnsupportedOperationException();
        }
      };
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jca\ProviderList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */