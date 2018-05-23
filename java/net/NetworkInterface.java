package java.net;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;

public final class NetworkInterface
{
  private String name;
  private String displayName;
  private int index;
  private InetAddress[] addrs;
  private InterfaceAddress[] bindings;
  private NetworkInterface[] childs;
  private NetworkInterface parent = null;
  private boolean virtual = false;
  private static final NetworkInterface defaultInterface;
  private static final int defaultIndex;
  
  NetworkInterface() {}
  
  NetworkInterface(String paramString, int paramInt, InetAddress[] paramArrayOfInetAddress)
  {
    name = paramString;
    index = paramInt;
    addrs = paramArrayOfInetAddress;
  }
  
  public String getName()
  {
    return name;
  }
  
  public Enumeration<InetAddress> getInetAddresses()
  {
    new Enumeration()
    {
      private int i = 0;
      private int count = 0;
      private InetAddress[] local_addrs = new InetAddress[addrs.length];
      
      public InetAddress nextElement()
      {
        if (i < count) {
          return local_addrs[(i++)];
        }
        throw new NoSuchElementException();
      }
      
      public boolean hasMoreElements()
      {
        return i < count;
      }
    };
  }
  
  public List<InterfaceAddress> getInterfaceAddresses()
  {
    ArrayList localArrayList = new ArrayList(1);
    SecurityManager localSecurityManager = System.getSecurityManager();
    for (int i = 0; i < bindings.length; i++) {
      try
      {
        if (localSecurityManager != null) {
          localSecurityManager.checkConnect(bindings[i].getAddress().getHostAddress(), -1);
        }
        localArrayList.add(bindings[i]);
      }
      catch (SecurityException localSecurityException) {}
    }
    return localArrayList;
  }
  
  public Enumeration<NetworkInterface> getSubInterfaces()
  {
    new Enumeration()
    {
      private int i = 0;
      
      public NetworkInterface nextElement()
      {
        if (i < childs.length) {
          return childs[(i++)];
        }
        throw new NoSuchElementException();
      }
      
      public boolean hasMoreElements()
      {
        return i < childs.length;
      }
    };
  }
  
  public NetworkInterface getParent()
  {
    return parent;
  }
  
  public int getIndex()
  {
    return index;
  }
  
  public String getDisplayName()
  {
    return "".equals(displayName) ? null : displayName;
  }
  
  public static NetworkInterface getByName(String paramString)
    throws SocketException
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    return getByName0(paramString);
  }
  
  public static NetworkInterface getByIndex(int paramInt)
    throws SocketException
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Interface index can't be negative");
    }
    return getByIndex0(paramInt);
  }
  
  public static NetworkInterface getByInetAddress(InetAddress paramInetAddress)
    throws SocketException
  {
    if (paramInetAddress == null) {
      throw new NullPointerException();
    }
    if ((!(paramInetAddress instanceof Inet4Address)) && (!(paramInetAddress instanceof Inet6Address))) {
      throw new IllegalArgumentException("invalid address type");
    }
    return getByInetAddress0(paramInetAddress);
  }
  
  public static Enumeration<NetworkInterface> getNetworkInterfaces()
    throws SocketException
  {
    NetworkInterface[] arrayOfNetworkInterface = getAll();
    if (arrayOfNetworkInterface == null) {
      return null;
    }
    new Enumeration()
    {
      private int i = 0;
      
      public NetworkInterface nextElement()
      {
        if ((val$netifs != null) && (i < val$netifs.length))
        {
          NetworkInterface localNetworkInterface = val$netifs[(i++)];
          return localNetworkInterface;
        }
        throw new NoSuchElementException();
      }
      
      public boolean hasMoreElements()
      {
        return (val$netifs != null) && (i < val$netifs.length);
      }
    };
  }
  
  private static native NetworkInterface[] getAll()
    throws SocketException;
  
  private static native NetworkInterface getByName0(String paramString)
    throws SocketException;
  
  private static native NetworkInterface getByIndex0(int paramInt)
    throws SocketException;
  
  private static native NetworkInterface getByInetAddress0(InetAddress paramInetAddress)
    throws SocketException;
  
  public boolean isUp()
    throws SocketException
  {
    return isUp0(name, index);
  }
  
  public boolean isLoopback()
    throws SocketException
  {
    return isLoopback0(name, index);
  }
  
  public boolean isPointToPoint()
    throws SocketException
  {
    return isP2P0(name, index);
  }
  
  public boolean supportsMulticast()
    throws SocketException
  {
    return supportsMulticast0(name, index);
  }
  
  public byte[] getHardwareAddress()
    throws SocketException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      try
      {
        localSecurityManager.checkPermission(new NetPermission("getNetworkInformation"));
      }
      catch (SecurityException localSecurityException)
      {
        if (!getInetAddresses().hasMoreElements()) {
          return null;
        }
      }
    }
    for (InetAddress localInetAddress : addrs) {
      if ((localInetAddress instanceof Inet4Address)) {
        return getMacAddr0(((Inet4Address)localInetAddress).getAddress(), name, index);
      }
    }
    return getMacAddr0(null, name, index);
  }
  
  public int getMTU()
    throws SocketException
  {
    return getMTU0(name, index);
  }
  
  public boolean isVirtual()
  {
    return virtual;
  }
  
  private static native boolean isUp0(String paramString, int paramInt)
    throws SocketException;
  
  private static native boolean isLoopback0(String paramString, int paramInt)
    throws SocketException;
  
  private static native boolean supportsMulticast0(String paramString, int paramInt)
    throws SocketException;
  
  private static native boolean isP2P0(String paramString, int paramInt)
    throws SocketException;
  
  private static native byte[] getMacAddr0(byte[] paramArrayOfByte, String paramString, int paramInt)
    throws SocketException;
  
  private static native int getMTU0(String paramString, int paramInt)
    throws SocketException;
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof NetworkInterface)) {
      return false;
    }
    NetworkInterface localNetworkInterface = (NetworkInterface)paramObject;
    if (name != null)
    {
      if (!name.equals(name)) {
        return false;
      }
    }
    else if (name != null) {
      return false;
    }
    if (addrs == null) {
      return addrs == null;
    }
    if (addrs == null) {
      return false;
    }
    if (addrs.length != addrs.length) {
      return false;
    }
    InetAddress[] arrayOfInetAddress = addrs;
    int i = arrayOfInetAddress.length;
    for (int j = 0; j < i; j++)
    {
      int k = 0;
      for (int m = 0; m < i; m++) {
        if (addrs[j].equals(arrayOfInetAddress[m]))
        {
          k = 1;
          break;
        }
      }
      if (k == 0) {
        return false;
      }
    }
    return true;
  }
  
  public int hashCode()
  {
    return name == null ? 0 : name.hashCode();
  }
  
  public String toString()
  {
    String str = "name:";
    str = str + (name == null ? "null" : name);
    if (displayName != null) {
      str = str + " (" + displayName + ")";
    }
    return str;
  }
  
  private static native void init();
  
  static NetworkInterface getDefault()
  {
    return defaultInterface;
  }
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        System.loadLibrary("net");
        return null;
      }
    });
    init();
    defaultInterface = DefaultInterface.getDefault();
    if (defaultInterface != null) {
      defaultIndex = defaultInterface.getIndex();
    } else {
      defaultIndex = 0;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\NetworkInterface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */