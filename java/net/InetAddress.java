package java.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamException;
import java.io.ObjectStreamField;
import java.io.PrintStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import sun.misc.Unsafe;
import sun.net.InetAddressCachePolicy;
import sun.net.spi.nameservice.NameService;
import sun.net.spi.nameservice.NameServiceDescriptor;
import sun.net.util.IPAddressUtil;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetPropertyAction;

public class InetAddress
  implements Serializable
{
  static final int IPv4 = 1;
  static final int IPv6 = 2;
  static transient boolean preferIPv6Address;
  final transient InetAddressHolder holder = new InetAddressHolder();
  private static List<NameService> nameServices;
  private transient String canonicalHostName = null;
  private static final long serialVersionUID = 3286316764910316507L;
  private static Cache addressCache;
  private static Cache negativeCache;
  private static boolean addressCacheInit;
  static InetAddress[] unknown_array;
  static InetAddressImpl impl;
  private static final HashMap<String, Void> lookupTable;
  private static InetAddress cachedLocalHost;
  private static long cacheTime;
  private static final long maxCacheTime = 5000L;
  private static final Object cacheLock;
  private static final long FIELDS_OFFSET;
  private static final Unsafe UNSAFE;
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("hostName", String.class), new ObjectStreamField("address", Integer.TYPE), new ObjectStreamField("family", Integer.TYPE) };
  
  InetAddressHolder holder()
  {
    return holder;
  }
  
  InetAddress() {}
  
  private Object readResolve()
    throws ObjectStreamException
  {
    return new Inet4Address(holder().getHostName(), holder().getAddress());
  }
  
  public boolean isMulticastAddress()
  {
    return false;
  }
  
  public boolean isAnyLocalAddress()
  {
    return false;
  }
  
  public boolean isLoopbackAddress()
  {
    return false;
  }
  
  public boolean isLinkLocalAddress()
  {
    return false;
  }
  
  public boolean isSiteLocalAddress()
  {
    return false;
  }
  
  public boolean isMCGlobal()
  {
    return false;
  }
  
  public boolean isMCNodeLocal()
  {
    return false;
  }
  
  public boolean isMCLinkLocal()
  {
    return false;
  }
  
  public boolean isMCSiteLocal()
  {
    return false;
  }
  
  public boolean isMCOrgLocal()
  {
    return false;
  }
  
  public boolean isReachable(int paramInt)
    throws IOException
  {
    return isReachable(null, 0, paramInt);
  }
  
  public boolean isReachable(NetworkInterface paramNetworkInterface, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt1 < 0) {
      throw new IllegalArgumentException("ttl can't be negative");
    }
    if (paramInt2 < 0) {
      throw new IllegalArgumentException("timeout can't be negative");
    }
    return impl.isReachable(this, paramInt2, paramNetworkInterface, paramInt1);
  }
  
  public String getHostName()
  {
    return getHostName(true);
  }
  
  String getHostName(boolean paramBoolean)
  {
    if (holder().getHostName() == null) {
      holderhostName = getHostFromNameService(this, paramBoolean);
    }
    return holder().getHostName();
  }
  
  public String getCanonicalHostName()
  {
    if (canonicalHostName == null) {
      canonicalHostName = getHostFromNameService(this, true);
    }
    return canonicalHostName;
  }
  
  private static String getHostFromNameService(InetAddress paramInetAddress, boolean paramBoolean)
  {
    String str = null;
    Iterator localIterator = nameServices.iterator();
    while (localIterator.hasNext())
    {
      NameService localNameService = (NameService)localIterator.next();
      try
      {
        str = localNameService.getHostByAddr(paramInetAddress.getAddress());
        if (paramBoolean)
        {
          localObject = System.getSecurityManager();
          if (localObject != null) {
            ((SecurityManager)localObject).checkConnect(str, -1);
          }
        }
        Object localObject = getAllByName0(str, paramBoolean);
        boolean bool = false;
        if (localObject != null) {
          for (int i = 0; (!bool) && (i < localObject.length); i++) {
            bool = paramInetAddress.equals(localObject[i]);
          }
        }
        if (!bool)
        {
          str = paramInetAddress.getHostAddress();
          return str;
        }
      }
      catch (SecurityException localSecurityException)
      {
        str = paramInetAddress.getHostAddress();
      }
      catch (UnknownHostException localUnknownHostException)
      {
        str = paramInetAddress.getHostAddress();
      }
    }
    return str;
  }
  
  public byte[] getAddress()
  {
    return null;
  }
  
  public String getHostAddress()
  {
    return null;
  }
  
  public int hashCode()
  {
    return -1;
  }
  
  public boolean equals(Object paramObject)
  {
    return false;
  }
  
  public String toString()
  {
    String str = holder().getHostName();
    return (str != null ? str : "") + "/" + getHostAddress();
  }
  
  private static void cacheInitIfNeeded()
  {
    assert (Thread.holdsLock(addressCache));
    if (addressCacheInit) {
      return;
    }
    unknown_array = new InetAddress[1];
    unknown_array[0] = impl.anyLocalAddress();
    addressCache.put(impl.anyLocalAddress().getHostName(), unknown_array);
    addressCacheInit = true;
  }
  
  private static void cacheAddresses(String paramString, InetAddress[] paramArrayOfInetAddress, boolean paramBoolean)
  {
    paramString = paramString.toLowerCase();
    synchronized (addressCache)
    {
      cacheInitIfNeeded();
      if (paramBoolean) {
        addressCache.put(paramString, paramArrayOfInetAddress);
      } else {
        negativeCache.put(paramString, paramArrayOfInetAddress);
      }
    }
  }
  
  private static InetAddress[] getCachedAddresses(String paramString)
  {
    paramString = paramString.toLowerCase();
    synchronized (addressCache)
    {
      cacheInitIfNeeded();
      CacheEntry localCacheEntry = addressCache.get(paramString);
      if (localCacheEntry == null) {
        localCacheEntry = negativeCache.get(paramString);
      }
      if (localCacheEntry != null) {
        return addresses;
      }
    }
    return null;
  }
  
  private static NameService createNSProvider(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    Object localObject = null;
    if (paramString.equals("default"))
    {
      localObject = new NameService()
      {
        public InetAddress[] lookupAllHostAddr(String paramAnonymousString)
          throws UnknownHostException
        {
          return InetAddress.impl.lookupAllHostAddr(paramAnonymousString);
        }
        
        public String getHostByAddr(byte[] paramAnonymousArrayOfByte)
          throws UnknownHostException
        {
          return InetAddress.impl.getHostByAddr(paramAnonymousArrayOfByte);
        }
      };
    }
    else
    {
      String str = paramString;
      try
      {
        localObject = (NameService)AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public NameService run()
          {
            Iterator localIterator = ServiceLoader.load(NameServiceDescriptor.class).iterator();
            while (localIterator.hasNext())
            {
              NameServiceDescriptor localNameServiceDescriptor = (NameServiceDescriptor)localIterator.next();
              if (val$providerName.equalsIgnoreCase(localNameServiceDescriptor.getType() + "," + localNameServiceDescriptor.getProviderName())) {
                try
                {
                  return localNameServiceDescriptor.createNameService();
                }
                catch (Exception localException)
                {
                  localException.printStackTrace();
                  System.err.println("Cannot create name service:" + val$providerName + ": " + localException);
                }
              }
            }
            return null;
          }
        });
      }
      catch (PrivilegedActionException localPrivilegedActionException) {}
    }
    return (NameService)localObject;
  }
  
  public static InetAddress getByAddress(String paramString, byte[] paramArrayOfByte)
    throws UnknownHostException
  {
    if ((paramString != null) && (paramString.length() > 0) && (paramString.charAt(0) == '[') && (paramString.charAt(paramString.length() - 1) == ']')) {
      paramString = paramString.substring(1, paramString.length() - 1);
    }
    if (paramArrayOfByte != null)
    {
      if (paramArrayOfByte.length == 4) {
        return new Inet4Address(paramString, paramArrayOfByte);
      }
      if (paramArrayOfByte.length == 16)
      {
        byte[] arrayOfByte = IPAddressUtil.convertFromIPv4MappedAddress(paramArrayOfByte);
        if (arrayOfByte != null) {
          return new Inet4Address(paramString, arrayOfByte);
        }
        return new Inet6Address(paramString, paramArrayOfByte);
      }
    }
    throw new UnknownHostException("addr is of illegal length");
  }
  
  public static InetAddress getByName(String paramString)
    throws UnknownHostException
  {
    return getAllByName(paramString)[0];
  }
  
  private static InetAddress getByName(String paramString, InetAddress paramInetAddress)
    throws UnknownHostException
  {
    return getAllByName(paramString, paramInetAddress)[0];
  }
  
  public static InetAddress[] getAllByName(String paramString)
    throws UnknownHostException
  {
    return getAllByName(paramString, null);
  }
  
  private static InetAddress[] getAllByName(String paramString, InetAddress paramInetAddress)
    throws UnknownHostException
  {
    if ((paramString == null) || (paramString.length() == 0))
    {
      InetAddress[] arrayOfInetAddress1 = new InetAddress[1];
      arrayOfInetAddress1[0] = impl.loopbackAddress();
      return arrayOfInetAddress1;
    }
    int i = 0;
    if (paramString.charAt(0) == '[') {
      if ((paramString.length() > 2) && (paramString.charAt(paramString.length() - 1) == ']'))
      {
        paramString = paramString.substring(1, paramString.length() - 1);
        i = 1;
      }
      else
      {
        throw new UnknownHostException(paramString + ": invalid IPv6 address");
      }
    }
    if ((Character.digit(paramString.charAt(0), 16) != -1) || (paramString.charAt(0) == ':'))
    {
      byte[] arrayOfByte = null;
      int j = -1;
      String str = null;
      arrayOfByte = IPAddressUtil.textToNumericFormatV4(paramString);
      if (arrayOfByte == null)
      {
        int k;
        if ((k = paramString.indexOf("%")) != -1)
        {
          j = checkNumericZone(paramString);
          if (j == -1) {
            str = paramString.substring(k + 1);
          }
        }
        if (((arrayOfByte = IPAddressUtil.textToNumericFormatV6(paramString)) == null) && (paramString.contains(":"))) {
          throw new UnknownHostException(paramString + ": invalid IPv6 address");
        }
      }
      else if (i != 0)
      {
        throw new UnknownHostException("[" + paramString + "]");
      }
      InetAddress[] arrayOfInetAddress2 = new InetAddress[1];
      if (arrayOfByte != null)
      {
        if (arrayOfByte.length == 4) {
          arrayOfInetAddress2[0] = new Inet4Address(null, arrayOfByte);
        } else if (str != null) {
          arrayOfInetAddress2[0] = new Inet6Address(null, arrayOfByte, str);
        } else {
          arrayOfInetAddress2[0] = new Inet6Address(null, arrayOfByte, j);
        }
        return arrayOfInetAddress2;
      }
    }
    else if (i != 0)
    {
      throw new UnknownHostException("[" + paramString + "]");
    }
    return getAllByName0(paramString, paramInetAddress, true);
  }
  
  public static InetAddress getLoopbackAddress()
  {
    return impl.loopbackAddress();
  }
  
  private static int checkNumericZone(String paramString)
    throws UnknownHostException
  {
    int i = paramString.indexOf('%');
    int j = paramString.length();
    int m = 0;
    if (i == -1) {
      return -1;
    }
    for (int n = i + 1; n < j; n++)
    {
      char c = paramString.charAt(n);
      if (c == ']')
      {
        if (n != i + 1) {
          break;
        }
        return -1;
      }
      int k;
      if ((k = Character.digit(c, 10)) < 0) {
        return -1;
      }
      m = m * 10 + k;
    }
    return m;
  }
  
  private static InetAddress[] getAllByName0(String paramString)
    throws UnknownHostException
  {
    return getAllByName0(paramString, true);
  }
  
  static InetAddress[] getAllByName0(String paramString, boolean paramBoolean)
    throws UnknownHostException
  {
    return getAllByName0(paramString, null, paramBoolean);
  }
  
  private static InetAddress[] getAllByName0(String paramString, InetAddress paramInetAddress, boolean paramBoolean)
    throws UnknownHostException
  {
    if (paramBoolean)
    {
      localObject = System.getSecurityManager();
      if (localObject != null) {
        ((SecurityManager)localObject).checkConnect(paramString, -1);
      }
    }
    Object localObject = getCachedAddresses(paramString);
    if (localObject == null) {
      localObject = getAddressesFromNameService(paramString, paramInetAddress);
    }
    if (localObject == unknown_array) {
      throw new UnknownHostException(paramString);
    }
    return (InetAddress[])((InetAddress[])localObject).clone();
  }
  
  private static InetAddress[] getAddressesFromNameService(String paramString, InetAddress paramInetAddress)
    throws UnknownHostException
  {
    Object localObject1 = null;
    boolean bool = false;
    Object localObject2 = null;
    if ((localObject1 = checkLookupTable(paramString)) == null) {
      try
      {
        Iterator localIterator = nameServices.iterator();
        NameService localNameService;
        while (localIterator.hasNext())
        {
          localNameService = (NameService)localIterator.next();
          try
          {
            localObject1 = localNameService.lookupAllHostAddr(paramString);
            bool = true;
          }
          catch (UnknownHostException localUnknownHostException)
          {
            if (paramString.equalsIgnoreCase("localhost"))
            {
              InetAddress[] arrayOfInetAddress = { impl.loopbackAddress() };
              localObject1 = arrayOfInetAddress;
              bool = true;
              break;
            }
            localObject1 = unknown_array;
            bool = false;
            localObject2 = localUnknownHostException;
          }
        }
        if ((paramInetAddress != null) && (localObject1.length > 1) && (!localObject1[0].equals(paramInetAddress)))
        {
          for (int i = 1; (i < localObject1.length) && (!localObject1[i].equals(paramInetAddress)); i++) {}
          if (i < localObject1.length)
          {
            Object localObject3 = paramInetAddress;
            for (int j = 0; j < i; j++)
            {
              localNameService = localObject1[j];
              localObject1[j] = localObject3;
              localObject3 = localNameService;
            }
            localObject1[i] = localObject3;
          }
        }
        cacheAddresses(paramString, (InetAddress[])localObject1, bool);
        if ((!bool) && (localObject2 != null)) {
          throw ((Throwable)localObject2);
        }
      }
      finally
      {
        updateLookupTable(paramString);
      }
    }
    return (InetAddress[])localObject1;
  }
  
  private static InetAddress[] checkLookupTable(String paramString)
  {
    synchronized (lookupTable)
    {
      if (!lookupTable.containsKey(paramString))
      {
        lookupTable.put(paramString, null);
        return null;
      }
      while (lookupTable.containsKey(paramString)) {
        try
        {
          lookupTable.wait();
        }
        catch (InterruptedException localInterruptedException) {}
      }
    }
    ??? = getCachedAddresses(paramString);
    if (??? == null) {
      synchronized (lookupTable)
      {
        lookupTable.put(paramString, null);
        return null;
      }
    }
    return (InetAddress[])???;
  }
  
  private static void updateLookupTable(String paramString)
  {
    synchronized (lookupTable)
    {
      lookupTable.remove(paramString);
      lookupTable.notifyAll();
    }
  }
  
  public static InetAddress getByAddress(byte[] paramArrayOfByte)
    throws UnknownHostException
  {
    return getByAddress(null, paramArrayOfByte);
  }
  
  public static InetAddress getLocalHost()
    throws UnknownHostException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    try
    {
      String str = impl.getLocalHostName();
      if (localSecurityManager != null) {
        localSecurityManager.checkConnect(str, -1);
      }
      if (str.equals("localhost")) {
        return impl.loopbackAddress();
      }
      InetAddress localInetAddress = null;
      synchronized (cacheLock)
      {
        long l = System.currentTimeMillis();
        if (cachedLocalHost != null) {
          if (l - cacheTime < 5000L) {
            localInetAddress = cachedLocalHost;
          } else {
            cachedLocalHost = null;
          }
        }
        if (localInetAddress == null)
        {
          InetAddress[] arrayOfInetAddress;
          try
          {
            arrayOfInetAddress = getAddressesFromNameService(str, null);
          }
          catch (UnknownHostException localUnknownHostException1)
          {
            UnknownHostException localUnknownHostException2 = new UnknownHostException(str + ": " + localUnknownHostException1.getMessage());
            localUnknownHostException2.initCause(localUnknownHostException1);
            throw localUnknownHostException2;
          }
          cachedLocalHost = arrayOfInetAddress[0];
          cacheTime = l;
          localInetAddress = arrayOfInetAddress[0];
        }
      }
      return localInetAddress;
    }
    catch (SecurityException localSecurityException) {}
    return impl.loopbackAddress();
  }
  
  private static native void init();
  
  static InetAddress anyLocalAddress()
  {
    return impl.anyLocalAddress();
  }
  
  static InetAddressImpl loadImpl(String paramString)
  {
    Object localObject = null;
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("impl.prefix", ""));
    try
    {
      localObject = Class.forName("java.net." + str + paramString).newInstance();
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      System.err.println("Class not found: java.net." + str + paramString + ":\ncheck impl.prefix property in your properties file.");
    }
    catch (InstantiationException localInstantiationException)
    {
      System.err.println("Could not instantiate: java.net." + str + paramString + ":\ncheck impl.prefix property in your properties file.");
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      System.err.println("Cannot access class: java.net." + str + paramString + ":\ncheck impl.prefix property in your properties file.");
    }
    if (localObject == null) {
      try
      {
        localObject = Class.forName(paramString).newInstance();
      }
      catch (Exception localException)
      {
        throw new Error("System property impl.prefix incorrect");
      }
    }
    return (InetAddressImpl)localObject;
  }
  
  private void readObjectNoData(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    if (getClass().getClassLoader() != null) {
      throw new SecurityException("invalid address type");
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    if (getClass().getClassLoader() != null) {
      throw new SecurityException("invalid address type");
    }
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    String str = (String)localGetField.get("hostName", null);
    int i = localGetField.get("address", 0);
    int j = localGetField.get("family", 0);
    InetAddressHolder localInetAddressHolder = new InetAddressHolder(str, i, j);
    UNSAFE.putObject(this, FIELDS_OFFSET, localInetAddressHolder);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (getClass().getClassLoader() != null) {
      throw new SecurityException("invalid address type");
    }
    ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
    localPutField.put("hostName", holder().getHostName());
    localPutField.put("address", holder().getAddress());
    localPutField.put("family", holder().getFamily());
    paramObjectOutputStream.writeFields();
  }
  
  static
  {
    preferIPv6Address = false;
    nameServices = null;
    preferIPv6Address = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("java.net.preferIPv6Addresses"))).booleanValue();
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        System.loadLibrary("net");
        return null;
      }
    });
    init();
    addressCache = new Cache(InetAddress.Cache.Type.Positive);
    negativeCache = new Cache(InetAddress.Cache.Type.Negative);
    addressCacheInit = false;
    lookupTable = new HashMap();
    impl = InetAddressImplFactory.create();
    Object localObject = null;
    String str = "sun.net.spi.nameservice.provider.";
    int i = 1;
    nameServices = new ArrayList();
    NameService localNameService;
    for (localObject = (String)AccessController.doPrivileged(new GetPropertyAction(str + i)); localObject != null; localObject = (String)AccessController.doPrivileged(new GetPropertyAction(str + i)))
    {
      localNameService = createNSProvider((String)localObject);
      if (localNameService != null) {
        nameServices.add(localNameService);
      }
      i++;
    }
    if (nameServices.size() == 0)
    {
      localNameService = createNSProvider("default");
      nameServices.add(localNameService);
    }
    cachedLocalHost = null;
    cacheTime = 0L;
    cacheLock = new Object();
    try
    {
      localObject = Unsafe.getUnsafe();
      FIELDS_OFFSET = ((Unsafe)localObject).objectFieldOffset(InetAddress.class.getDeclaredField("holder"));
      UNSAFE = (Unsafe)localObject;
    }
    catch (ReflectiveOperationException localReflectiveOperationException)
    {
      throw new Error(localReflectiveOperationException);
    }
  }
  
  static final class Cache
  {
    private LinkedHashMap<String, InetAddress.CacheEntry> cache;
    private Type type;
    
    public Cache(Type paramType)
    {
      type = paramType;
      cache = new LinkedHashMap();
    }
    
    private int getPolicy()
    {
      if (type == Type.Positive) {
        return InetAddressCachePolicy.get();
      }
      return InetAddressCachePolicy.getNegative();
    }
    
    public Cache put(String paramString, InetAddress[] paramArrayOfInetAddress)
    {
      int i = getPolicy();
      if (i == 0) {
        return this;
      }
      if (i != -1)
      {
        LinkedList localLinkedList = new LinkedList();
        long l2 = System.currentTimeMillis();
        Iterator localIterator = cache.keySet().iterator();
        String str;
        while (localIterator.hasNext())
        {
          str = (String)localIterator.next();
          InetAddress.CacheEntry localCacheEntry2 = (InetAddress.CacheEntry)cache.get(str);
          if ((expiration < 0L) || (expiration >= l2)) {
            break;
          }
          localLinkedList.add(str);
        }
        localIterator = localLinkedList.iterator();
        while (localIterator.hasNext())
        {
          str = (String)localIterator.next();
          cache.remove(str);
        }
      }
      long l1;
      if (i == -1) {
        l1 = -1L;
      } else {
        l1 = System.currentTimeMillis() + i * 1000;
      }
      InetAddress.CacheEntry localCacheEntry1 = new InetAddress.CacheEntry(paramArrayOfInetAddress, l1);
      cache.put(paramString, localCacheEntry1);
      return this;
    }
    
    public InetAddress.CacheEntry get(String paramString)
    {
      int i = getPolicy();
      if (i == 0) {
        return null;
      }
      InetAddress.CacheEntry localCacheEntry = (InetAddress.CacheEntry)cache.get(paramString);
      if ((localCacheEntry != null) && (i != -1) && (expiration >= 0L) && (expiration < System.currentTimeMillis()))
      {
        cache.remove(paramString);
        localCacheEntry = null;
      }
      return localCacheEntry;
    }
    
    static enum Type
    {
      Positive,  Negative;
      
      private Type() {}
    }
  }
  
  static final class CacheEntry
  {
    InetAddress[] addresses;
    long expiration;
    
    CacheEntry(InetAddress[] paramArrayOfInetAddress, long paramLong)
    {
      addresses = paramArrayOfInetAddress;
      expiration = paramLong;
    }
  }
  
  static class InetAddressHolder
  {
    String originalHostName;
    String hostName;
    int address;
    int family;
    
    InetAddressHolder() {}
    
    InetAddressHolder(String paramString, int paramInt1, int paramInt2)
    {
      originalHostName = paramString;
      hostName = paramString;
      address = paramInt1;
      family = paramInt2;
    }
    
    void init(String paramString, int paramInt)
    {
      originalHostName = paramString;
      hostName = paramString;
      if (paramInt != -1) {
        family = paramInt;
      }
    }
    
    String getHostName()
    {
      return hostName;
    }
    
    String getOriginalHostName()
    {
      return originalHostName;
    }
    
    int getAddress()
    {
      return address;
    }
    
    int getFamily()
    {
      return family;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\InetAddress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */