package sun.net.dns;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class ResolverConfigurationImpl
  extends ResolverConfiguration
{
  private static Object lock;
  private final ResolverConfiguration.Options opts = new OptionsImpl();
  private static boolean changed;
  private static long lastRefresh;
  private static final int TIMEOUT = 120000;
  private static String os_searchlist;
  private static String os_nameservers;
  private static LinkedList<String> searchlist;
  private static LinkedList<String> nameservers;
  
  private LinkedList<String> stringToList(String paramString)
  {
    LinkedList localLinkedList = new LinkedList();
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ", ");
    while (localStringTokenizer.hasMoreTokens())
    {
      String str = localStringTokenizer.nextToken();
      if (!localLinkedList.contains(str)) {
        localLinkedList.add(str);
      }
    }
    return localLinkedList;
  }
  
  private void loadConfig()
  {
    assert (Thread.holdsLock(lock));
    if (changed)
    {
      changed = false;
    }
    else if (lastRefresh >= 0L)
    {
      long l = System.currentTimeMillis();
      if (l - lastRefresh < 120000L) {
        return;
      }
    }
    loadDNSconfig0();
    lastRefresh = System.currentTimeMillis();
    searchlist = stringToList(os_searchlist);
    nameservers = stringToList(os_nameservers);
    os_searchlist = null;
    os_nameservers = null;
  }
  
  ResolverConfigurationImpl() {}
  
  public List<String> searchlist()
  {
    synchronized (lock)
    {
      loadConfig();
      return (List)searchlist.clone();
    }
  }
  
  public List<String> nameservers()
  {
    synchronized (lock)
    {
      loadConfig();
      return (List)nameservers.clone();
    }
  }
  
  public ResolverConfiguration.Options options()
  {
    return opts;
  }
  
  static native void init0();
  
  static native void loadDNSconfig0();
  
  static native int notifyAddrChange0();
  
  static
  {
    lock = new Object();
    changed = false;
    lastRefresh = -1L;
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        System.loadLibrary("net");
        return null;
      }
    });
    init0();
    AddressChangeListener localAddressChangeListener = new AddressChangeListener();
    localAddressChangeListener.setDaemon(true);
    localAddressChangeListener.start();
  }
  
  static class AddressChangeListener
    extends Thread
  {
    AddressChangeListener() {}
    
    public void run()
    {
      for (;;)
      {
        if (ResolverConfigurationImpl.notifyAddrChange0() != 0) {
          return;
        }
        synchronized (ResolverConfigurationImpl.lock)
        {
          ResolverConfigurationImpl.access$102(true);
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\dns\ResolverConfigurationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */