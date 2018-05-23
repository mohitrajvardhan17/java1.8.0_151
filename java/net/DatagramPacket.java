package java.net;

import java.security.AccessController;
import java.security.PrivilegedAction;

public final class DatagramPacket
{
  byte[] buf;
  int offset;
  int length;
  int bufLength;
  InetAddress address;
  int port;
  
  public DatagramPacket(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    setData(paramArrayOfByte, paramInt1, paramInt2);
    address = null;
    port = -1;
  }
  
  public DatagramPacket(byte[] paramArrayOfByte, int paramInt)
  {
    this(paramArrayOfByte, 0, paramInt);
  }
  
  public DatagramPacket(byte[] paramArrayOfByte, int paramInt1, int paramInt2, InetAddress paramInetAddress, int paramInt3)
  {
    setData(paramArrayOfByte, paramInt1, paramInt2);
    setAddress(paramInetAddress);
    setPort(paramInt3);
  }
  
  public DatagramPacket(byte[] paramArrayOfByte, int paramInt1, int paramInt2, SocketAddress paramSocketAddress)
  {
    setData(paramArrayOfByte, paramInt1, paramInt2);
    setSocketAddress(paramSocketAddress);
  }
  
  public DatagramPacket(byte[] paramArrayOfByte, int paramInt1, InetAddress paramInetAddress, int paramInt2)
  {
    this(paramArrayOfByte, 0, paramInt1, paramInetAddress, paramInt2);
  }
  
  public DatagramPacket(byte[] paramArrayOfByte, int paramInt, SocketAddress paramSocketAddress)
  {
    this(paramArrayOfByte, 0, paramInt, paramSocketAddress);
  }
  
  public synchronized InetAddress getAddress()
  {
    return address;
  }
  
  public synchronized int getPort()
  {
    return port;
  }
  
  public synchronized byte[] getData()
  {
    return buf;
  }
  
  public synchronized int getOffset()
  {
    return offset;
  }
  
  public synchronized int getLength()
  {
    return length;
  }
  
  public synchronized void setData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if ((paramInt2 < 0) || (paramInt1 < 0) || (paramInt2 + paramInt1 < 0) || (paramInt2 + paramInt1 > paramArrayOfByte.length)) {
      throw new IllegalArgumentException("illegal length or offset");
    }
    buf = paramArrayOfByte;
    length = paramInt2;
    bufLength = paramInt2;
    offset = paramInt1;
  }
  
  public synchronized void setAddress(InetAddress paramInetAddress)
  {
    address = paramInetAddress;
  }
  
  public synchronized void setPort(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 65535)) {
      throw new IllegalArgumentException("Port out of range:" + paramInt);
    }
    port = paramInt;
  }
  
  public synchronized void setSocketAddress(SocketAddress paramSocketAddress)
  {
    if ((paramSocketAddress == null) || (!(paramSocketAddress instanceof InetSocketAddress))) {
      throw new IllegalArgumentException("unsupported address type");
    }
    InetSocketAddress localInetSocketAddress = (InetSocketAddress)paramSocketAddress;
    if (localInetSocketAddress.isUnresolved()) {
      throw new IllegalArgumentException("unresolved address");
    }
    setAddress(localInetSocketAddress.getAddress());
    setPort(localInetSocketAddress.getPort());
  }
  
  public synchronized SocketAddress getSocketAddress()
  {
    return new InetSocketAddress(getAddress(), getPort());
  }
  
  public synchronized void setData(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException("null packet buffer");
    }
    buf = paramArrayOfByte;
    offset = 0;
    length = paramArrayOfByte.length;
    bufLength = paramArrayOfByte.length;
  }
  
  public synchronized void setLength(int paramInt)
  {
    if ((paramInt + offset > buf.length) || (paramInt < 0) || (paramInt + offset < 0)) {
      throw new IllegalArgumentException("illegal length");
    }
    length = paramInt;
    bufLength = length;
  }
  
  private static native void init();
  
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
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\DatagramPacket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */