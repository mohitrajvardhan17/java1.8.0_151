package java.net;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamException;
import java.io.ObjectStreamField;
import sun.misc.Unsafe;

public class InetSocketAddress
  extends SocketAddress
{
  private final transient InetSocketAddressHolder holder;
  private static final long serialVersionUID = 5076001401234631237L;
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("hostname", String.class), new ObjectStreamField("addr", InetAddress.class), new ObjectStreamField("port", Integer.TYPE) };
  private static final long FIELDS_OFFSET;
  private static final Unsafe UNSAFE;
  
  private static int checkPort(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 65535)) {
      throw new IllegalArgumentException("port out of range:" + paramInt);
    }
    return paramInt;
  }
  
  private static String checkHost(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("hostname can't be null");
    }
    return paramString;
  }
  
  public InetSocketAddress(int paramInt)
  {
    this(InetAddress.anyLocalAddress(), paramInt);
  }
  
  public InetSocketAddress(InetAddress paramInetAddress, int paramInt)
  {
    holder = new InetSocketAddressHolder(null, paramInetAddress == null ? InetAddress.anyLocalAddress() : paramInetAddress, checkPort(paramInt), null);
  }
  
  public InetSocketAddress(String paramString, int paramInt)
  {
    checkHost(paramString);
    InetAddress localInetAddress = null;
    String str = null;
    try
    {
      localInetAddress = InetAddress.getByName(paramString);
    }
    catch (UnknownHostException localUnknownHostException)
    {
      str = paramString;
    }
    holder = new InetSocketAddressHolder(str, localInetAddress, checkPort(paramInt), null);
  }
  
  private InetSocketAddress(int paramInt, String paramString)
  {
    holder = new InetSocketAddressHolder(paramString, null, paramInt, null);
  }
  
  public static InetSocketAddress createUnresolved(String paramString, int paramInt)
  {
    return new InetSocketAddress(checkPort(paramInt), checkHost(paramString));
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
    localPutField.put("hostname", holder.hostname);
    localPutField.put("addr", holder.addr);
    localPutField.put("port", holder.port);
    paramObjectOutputStream.writeFields();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    String str = (String)localGetField.get("hostname", null);
    InetAddress localInetAddress = (InetAddress)localGetField.get("addr", null);
    int i = localGetField.get("port", -1);
    checkPort(i);
    if ((str == null) && (localInetAddress == null)) {
      throw new InvalidObjectException("hostname and addr can't both be null");
    }
    InetSocketAddressHolder localInetSocketAddressHolder = new InetSocketAddressHolder(str, localInetAddress, i, null);
    UNSAFE.putObject(this, FIELDS_OFFSET, localInetSocketAddressHolder);
  }
  
  private void readObjectNoData()
    throws ObjectStreamException
  {
    throw new InvalidObjectException("Stream data required");
  }
  
  public final int getPort()
  {
    return holder.getPort();
  }
  
  public final InetAddress getAddress()
  {
    return holder.getAddress();
  }
  
  public final String getHostName()
  {
    return holder.getHostName();
  }
  
  public final String getHostString()
  {
    return holder.getHostString();
  }
  
  public final boolean isUnresolved()
  {
    return holder.isUnresolved();
  }
  
  public String toString()
  {
    return holder.toString();
  }
  
  public final boolean equals(Object paramObject)
  {
    if ((paramObject == null) || (!(paramObject instanceof InetSocketAddress))) {
      return false;
    }
    return holder.equals(holder);
  }
  
  public final int hashCode()
  {
    return holder.hashCode();
  }
  
  static
  {
    try
    {
      Unsafe localUnsafe = Unsafe.getUnsafe();
      FIELDS_OFFSET = localUnsafe.objectFieldOffset(InetSocketAddress.class.getDeclaredField("holder"));
      UNSAFE = localUnsafe;
    }
    catch (ReflectiveOperationException localReflectiveOperationException)
    {
      throw new Error(localReflectiveOperationException);
    }
  }
  
  private static class InetSocketAddressHolder
  {
    private String hostname;
    private InetAddress addr;
    private int port;
    
    private InetSocketAddressHolder(String paramString, InetAddress paramInetAddress, int paramInt)
    {
      hostname = paramString;
      addr = paramInetAddress;
      port = paramInt;
    }
    
    private int getPort()
    {
      return port;
    }
    
    private InetAddress getAddress()
    {
      return addr;
    }
    
    private String getHostName()
    {
      if (hostname != null) {
        return hostname;
      }
      if (addr != null) {
        return addr.getHostName();
      }
      return null;
    }
    
    private String getHostString()
    {
      if (hostname != null) {
        return hostname;
      }
      if (addr != null)
      {
        if (addr.holder().getHostName() != null) {
          return addr.holder().getHostName();
        }
        return addr.getHostAddress();
      }
      return null;
    }
    
    private boolean isUnresolved()
    {
      return addr == null;
    }
    
    public String toString()
    {
      if (isUnresolved()) {
        return hostname + ":" + port;
      }
      return addr.toString() + ":" + port;
    }
    
    public final boolean equals(Object paramObject)
    {
      if ((paramObject == null) || (!(paramObject instanceof InetSocketAddressHolder))) {
        return false;
      }
      InetSocketAddressHolder localInetSocketAddressHolder = (InetSocketAddressHolder)paramObject;
      boolean bool;
      if (addr != null) {
        bool = addr.equals(addr);
      } else if (hostname != null) {
        bool = (addr == null) && (hostname.equalsIgnoreCase(hostname));
      } else {
        bool = (addr == null) && (hostname == null);
      }
      return (bool) && (port == port);
    }
    
    public final int hashCode()
    {
      if (addr != null) {
        return addr.hashCode() + port;
      }
      if (hostname != null) {
        return hostname.toLowerCase().hashCode() + port;
      }
      return port;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\InetSocketAddress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */