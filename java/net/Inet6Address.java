package java.net;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.util.Arrays;
import java.util.Enumeration;
import sun.misc.Unsafe;

public final class Inet6Address
  extends InetAddress
{
  static final int INADDRSZ = 16;
  private transient int cached_scope_id;
  private final transient Inet6AddressHolder holder6;
  private static final long serialVersionUID = 6880410070516793377L;
  private static final ObjectStreamField[] serialPersistentFields;
  private static final long FIELDS_OFFSET;
  private static final Unsafe UNSAFE;
  private static final int INT16SZ = 2;
  
  Inet6Address()
  {
    holder.init(null, 2);
    holder6 = new Inet6AddressHolder(null);
  }
  
  Inet6Address(String paramString, byte[] paramArrayOfByte, int paramInt)
  {
    holder.init(paramString, 2);
    holder6 = new Inet6AddressHolder(null);
    holder6.init(paramArrayOfByte, paramInt);
  }
  
  Inet6Address(String paramString, byte[] paramArrayOfByte)
  {
    holder6 = new Inet6AddressHolder(null);
    try
    {
      initif(paramString, paramArrayOfByte, null);
    }
    catch (UnknownHostException localUnknownHostException) {}
  }
  
  Inet6Address(String paramString, byte[] paramArrayOfByte, NetworkInterface paramNetworkInterface)
    throws UnknownHostException
  {
    holder6 = new Inet6AddressHolder(null);
    initif(paramString, paramArrayOfByte, paramNetworkInterface);
  }
  
  Inet6Address(String paramString1, byte[] paramArrayOfByte, String paramString2)
    throws UnknownHostException
  {
    holder6 = new Inet6AddressHolder(null);
    initstr(paramString1, paramArrayOfByte, paramString2);
  }
  
  public static Inet6Address getByAddress(String paramString, byte[] paramArrayOfByte, NetworkInterface paramNetworkInterface)
    throws UnknownHostException
  {
    if ((paramString != null) && (paramString.length() > 0) && (paramString.charAt(0) == '[') && (paramString.charAt(paramString.length() - 1) == ']')) {
      paramString = paramString.substring(1, paramString.length() - 1);
    }
    if ((paramArrayOfByte != null) && (paramArrayOfByte.length == 16)) {
      return new Inet6Address(paramString, paramArrayOfByte, paramNetworkInterface);
    }
    throw new UnknownHostException("addr is of illegal length");
  }
  
  public static Inet6Address getByAddress(String paramString, byte[] paramArrayOfByte, int paramInt)
    throws UnknownHostException
  {
    if ((paramString != null) && (paramString.length() > 0) && (paramString.charAt(0) == '[') && (paramString.charAt(paramString.length() - 1) == ']')) {
      paramString = paramString.substring(1, paramString.length() - 1);
    }
    if ((paramArrayOfByte != null) && (paramArrayOfByte.length == 16)) {
      return new Inet6Address(paramString, paramArrayOfByte, paramInt);
    }
    throw new UnknownHostException("addr is of illegal length");
  }
  
  private void initstr(String paramString1, byte[] paramArrayOfByte, String paramString2)
    throws UnknownHostException
  {
    try
    {
      NetworkInterface localNetworkInterface = NetworkInterface.getByName(paramString2);
      if (localNetworkInterface == null) {
        throw new UnknownHostException("no such interface " + paramString2);
      }
      initif(paramString1, paramArrayOfByte, localNetworkInterface);
    }
    catch (SocketException localSocketException)
    {
      throw new UnknownHostException("SocketException thrown" + paramString2);
    }
  }
  
  private void initif(String paramString, byte[] paramArrayOfByte, NetworkInterface paramNetworkInterface)
    throws UnknownHostException
  {
    int i = -1;
    holder6.init(paramArrayOfByte, paramNetworkInterface);
    if (paramArrayOfByte.length == 16) {
      i = 2;
    }
    holder.init(paramString, i);
  }
  
  private static boolean isDifferentLocalAddressType(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    if ((isLinkLocalAddress(paramArrayOfByte1)) && (!isLinkLocalAddress(paramArrayOfByte2))) {
      return false;
    }
    return (!isSiteLocalAddress(paramArrayOfByte1)) || (isSiteLocalAddress(paramArrayOfByte2));
  }
  
  private static int deriveNumericScope(byte[] paramArrayOfByte, NetworkInterface paramNetworkInterface)
    throws UnknownHostException
  {
    Enumeration localEnumeration = paramNetworkInterface.getInetAddresses();
    while (localEnumeration.hasMoreElements())
    {
      InetAddress localInetAddress = (InetAddress)localEnumeration.nextElement();
      if ((localInetAddress instanceof Inet6Address))
      {
        Inet6Address localInet6Address = (Inet6Address)localInetAddress;
        if (isDifferentLocalAddressType(paramArrayOfByte, localInet6Address.getAddress())) {
          return localInet6Address.getScopeId();
        }
      }
    }
    throw new UnknownHostException("no scope_id found");
  }
  
  private int deriveNumericScope(String paramString)
    throws UnknownHostException
  {
    Enumeration localEnumeration;
    try
    {
      localEnumeration = NetworkInterface.getNetworkInterfaces();
    }
    catch (SocketException localSocketException)
    {
      throw new UnknownHostException("could not enumerate local network interfaces");
    }
    while (localEnumeration.hasMoreElements())
    {
      NetworkInterface localNetworkInterface = (NetworkInterface)localEnumeration.nextElement();
      if (localNetworkInterface.getName().equals(paramString)) {
        return deriveNumericScope(holder6.ipaddress, localNetworkInterface);
      }
    }
    throw new UnknownHostException("No matching address found for interface : " + paramString);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    NetworkInterface localNetworkInterface = null;
    if (getClass().getClassLoader() != null) {
      throw new SecurityException("invalid address type");
    }
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    byte[] arrayOfByte = (byte[])localGetField.get("ipaddress", null);
    int i = localGetField.get("scope_id", -1);
    boolean bool1 = localGetField.get("scope_id_set", false);
    boolean bool2 = localGetField.get("scope_ifname_set", false);
    String str = (String)localGetField.get("ifname", null);
    if ((str != null) && (!"".equals(str))) {
      try
      {
        localNetworkInterface = NetworkInterface.getByName(str);
        if (localNetworkInterface == null)
        {
          bool1 = false;
          bool2 = false;
          i = 0;
        }
        else
        {
          bool2 = true;
          try
          {
            i = deriveNumericScope(arrayOfByte, localNetworkInterface);
          }
          catch (UnknownHostException localUnknownHostException) {}
        }
      }
      catch (SocketException localSocketException) {}
    }
    arrayOfByte = (byte[])arrayOfByte.clone();
    if (arrayOfByte.length != 16) {
      throw new InvalidObjectException("invalid address length: " + arrayOfByte.length);
    }
    if (holder.getFamily() != 2) {
      throw new InvalidObjectException("invalid address family type");
    }
    Inet6AddressHolder localInet6AddressHolder = new Inet6AddressHolder(arrayOfByte, i, bool1, localNetworkInterface, bool2, null);
    UNSAFE.putObject(this, FIELDS_OFFSET, localInet6AddressHolder);
  }
  
  private synchronized void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    String str = null;
    if (holder6.scope_ifname != null)
    {
      str = holder6.scope_ifname.getName();
      holder6.scope_ifname_set = true;
    }
    ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
    localPutField.put("ipaddress", holder6.ipaddress);
    localPutField.put("scope_id", holder6.scope_id);
    localPutField.put("scope_id_set", holder6.scope_id_set);
    localPutField.put("scope_ifname_set", holder6.scope_ifname_set);
    localPutField.put("ifname", str);
    paramObjectOutputStream.writeFields();
  }
  
  public boolean isMulticastAddress()
  {
    return holder6.isMulticastAddress();
  }
  
  public boolean isAnyLocalAddress()
  {
    return holder6.isAnyLocalAddress();
  }
  
  public boolean isLoopbackAddress()
  {
    return holder6.isLoopbackAddress();
  }
  
  public boolean isLinkLocalAddress()
  {
    return holder6.isLinkLocalAddress();
  }
  
  static boolean isLinkLocalAddress(byte[] paramArrayOfByte)
  {
    return ((paramArrayOfByte[0] & 0xFF) == 254) && ((paramArrayOfByte[1] & 0xC0) == 128);
  }
  
  public boolean isSiteLocalAddress()
  {
    return holder6.isSiteLocalAddress();
  }
  
  static boolean isSiteLocalAddress(byte[] paramArrayOfByte)
  {
    return ((paramArrayOfByte[0] & 0xFF) == 254) && ((paramArrayOfByte[1] & 0xC0) == 192);
  }
  
  public boolean isMCGlobal()
  {
    return holder6.isMCGlobal();
  }
  
  public boolean isMCNodeLocal()
  {
    return holder6.isMCNodeLocal();
  }
  
  public boolean isMCLinkLocal()
  {
    return holder6.isMCLinkLocal();
  }
  
  public boolean isMCSiteLocal()
  {
    return holder6.isMCSiteLocal();
  }
  
  public boolean isMCOrgLocal()
  {
    return holder6.isMCOrgLocal();
  }
  
  public byte[] getAddress()
  {
    return (byte[])holder6.ipaddress.clone();
  }
  
  public int getScopeId()
  {
    return holder6.scope_id;
  }
  
  public NetworkInterface getScopedInterface()
  {
    return holder6.scope_ifname;
  }
  
  public String getHostAddress()
  {
    return holder6.getHostAddress();
  }
  
  public int hashCode()
  {
    return holder6.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject == null) || (!(paramObject instanceof Inet6Address))) {
      return false;
    }
    Inet6Address localInet6Address = (Inet6Address)paramObject;
    return holder6.equals(holder6);
  }
  
  public boolean isIPv4CompatibleAddress()
  {
    return holder6.isIPv4CompatibleAddress();
  }
  
  static String numericToTextFormat(byte[] paramArrayOfByte)
  {
    StringBuilder localStringBuilder = new StringBuilder(39);
    for (int i = 0; i < 8; i++)
    {
      localStringBuilder.append(Integer.toHexString(paramArrayOfByte[(i << 1)] << 8 & 0xFF00 | paramArrayOfByte[((i << 1) + 1)] & 0xFF));
      if (i < 7) {
        localStringBuilder.append(":");
      }
    }
    return localStringBuilder.toString();
  }
  
  private static native void init();
  
  static
  {
    init();
    serialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("ipaddress", byte[].class), new ObjectStreamField("scope_id", Integer.TYPE), new ObjectStreamField("scope_id_set", Boolean.TYPE), new ObjectStreamField("scope_ifname_set", Boolean.TYPE), new ObjectStreamField("ifname", String.class) };
    try
    {
      Unsafe localUnsafe = Unsafe.getUnsafe();
      FIELDS_OFFSET = localUnsafe.objectFieldOffset(Inet6Address.class.getDeclaredField("holder6"));
      UNSAFE = localUnsafe;
    }
    catch (ReflectiveOperationException localReflectiveOperationException)
    {
      throw new Error(localReflectiveOperationException);
    }
  }
  
  private class Inet6AddressHolder
  {
    byte[] ipaddress;
    int scope_id;
    boolean scope_id_set;
    NetworkInterface scope_ifname;
    boolean scope_ifname_set;
    
    private Inet6AddressHolder()
    {
      ipaddress = new byte[16];
    }
    
    private Inet6AddressHolder(byte[] paramArrayOfByte, int paramInt, boolean paramBoolean1, NetworkInterface paramNetworkInterface, boolean paramBoolean2)
    {
      ipaddress = paramArrayOfByte;
      scope_id = paramInt;
      scope_id_set = paramBoolean1;
      scope_ifname_set = paramBoolean2;
      scope_ifname = paramNetworkInterface;
    }
    
    void setAddr(byte[] paramArrayOfByte)
    {
      if (paramArrayOfByte.length == 16) {
        System.arraycopy(paramArrayOfByte, 0, ipaddress, 0, 16);
      }
    }
    
    void init(byte[] paramArrayOfByte, int paramInt)
    {
      setAddr(paramArrayOfByte);
      if (paramInt >= 0)
      {
        scope_id = paramInt;
        scope_id_set = true;
      }
    }
    
    void init(byte[] paramArrayOfByte, NetworkInterface paramNetworkInterface)
      throws UnknownHostException
    {
      setAddr(paramArrayOfByte);
      if (paramNetworkInterface != null)
      {
        scope_id = Inet6Address.deriveNumericScope(ipaddress, paramNetworkInterface);
        scope_id_set = true;
        scope_ifname = paramNetworkInterface;
        scope_ifname_set = true;
      }
    }
    
    String getHostAddress()
    {
      String str = Inet6Address.numericToTextFormat(ipaddress);
      if (scope_ifname != null) {
        str = str + "%" + scope_ifname.getName();
      } else if (scope_id_set) {
        str = str + "%" + scope_id;
      }
      return str;
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof Inet6AddressHolder)) {
        return false;
      }
      Inet6AddressHolder localInet6AddressHolder = (Inet6AddressHolder)paramObject;
      return Arrays.equals(ipaddress, ipaddress);
    }
    
    public int hashCode()
    {
      if (ipaddress != null)
      {
        int i = 0;
        int j = 0;
        while (j < 16)
        {
          int k = 0;
          int m = 0;
          while ((k < 4) && (j < 16))
          {
            m = (m << 8) + ipaddress[j];
            k++;
            j++;
          }
          i += m;
        }
        return i;
      }
      return 0;
    }
    
    boolean isIPv4CompatibleAddress()
    {
      return (ipaddress[0] == 0) && (ipaddress[1] == 0) && (ipaddress[2] == 0) && (ipaddress[3] == 0) && (ipaddress[4] == 0) && (ipaddress[5] == 0) && (ipaddress[6] == 0) && (ipaddress[7] == 0) && (ipaddress[8] == 0) && (ipaddress[9] == 0) && (ipaddress[10] == 0) && (ipaddress[11] == 0);
    }
    
    boolean isMulticastAddress()
    {
      return (ipaddress[0] & 0xFF) == 255;
    }
    
    boolean isAnyLocalAddress()
    {
      int i = 0;
      for (int j = 0; j < 16; j++) {
        i = (byte)(i | ipaddress[j]);
      }
      return i == 0;
    }
    
    boolean isLoopbackAddress()
    {
      int i = 0;
      for (int j = 0; j < 15; j++) {
        i = (byte)(i | ipaddress[j]);
      }
      return (i == 0) && (ipaddress[15] == 1);
    }
    
    boolean isLinkLocalAddress()
    {
      return ((ipaddress[0] & 0xFF) == 254) && ((ipaddress[1] & 0xC0) == 128);
    }
    
    boolean isSiteLocalAddress()
    {
      return ((ipaddress[0] & 0xFF) == 254) && ((ipaddress[1] & 0xC0) == 192);
    }
    
    boolean isMCGlobal()
    {
      return ((ipaddress[0] & 0xFF) == 255) && ((ipaddress[1] & 0xF) == 14);
    }
    
    boolean isMCNodeLocal()
    {
      return ((ipaddress[0] & 0xFF) == 255) && ((ipaddress[1] & 0xF) == 1);
    }
    
    boolean isMCLinkLocal()
    {
      return ((ipaddress[0] & 0xFF) == 255) && ((ipaddress[1] & 0xF) == 2);
    }
    
    boolean isMCSiteLocal()
    {
      return ((ipaddress[0] & 0xFF) == 255) && ((ipaddress[1] & 0xF) == 5);
    }
    
    boolean isMCOrgLocal()
    {
      return ((ipaddress[0] & 0xFF) == 255) && ((ipaddress[1] & 0xF) == 8);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\Inet6Address.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */