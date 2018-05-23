package java.net;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import java.util.List;
import sun.net.SocksProxy;
import sun.net.www.ParseUtil;
import sun.security.action.GetPropertyAction;

class SocksSocketImpl
  extends PlainSocketImpl
  implements SocksConsts
{
  private String server = null;
  private int serverPort = 1080;
  private InetSocketAddress external_address;
  private boolean useV4 = false;
  private Socket cmdsock = null;
  private InputStream cmdIn = null;
  private OutputStream cmdOut = null;
  private boolean applicationSetProxy;
  
  SocksSocketImpl() {}
  
  SocksSocketImpl(String paramString, int paramInt)
  {
    server = paramString;
    serverPort = (paramInt == -1 ? 1080 : paramInt);
  }
  
  SocksSocketImpl(Proxy paramProxy)
  {
    SocketAddress localSocketAddress = paramProxy.address();
    if ((localSocketAddress instanceof InetSocketAddress))
    {
      InetSocketAddress localInetSocketAddress = (InetSocketAddress)localSocketAddress;
      server = localInetSocketAddress.getHostString();
      serverPort = localInetSocketAddress.getPort();
    }
  }
  
  void setV4()
  {
    useV4 = true;
  }
  
  private synchronized void privilegedConnect(final String paramString, final int paramInt1, final int paramInt2)
    throws IOException
  {
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Void run()
          throws IOException
        {
          SocksSocketImpl.this.superConnectServer(paramString, paramInt1, paramInt2);
          cmdIn = getInputStream();
          cmdOut = getOutputStream();
          return null;
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((IOException)localPrivilegedActionException.getException());
    }
  }
  
  private void superConnectServer(String paramString, int paramInt1, int paramInt2)
    throws IOException
  {
    super.connect(new InetSocketAddress(paramString, paramInt1), paramInt2);
  }
  
  private static int remainingMillis(long paramLong)
    throws IOException
  {
    if (paramLong == 0L) {
      return 0;
    }
    long l = paramLong - System.currentTimeMillis();
    if (l > 0L) {
      return (int)l;
    }
    throw new SocketTimeoutException();
  }
  
  private int readSocksReply(InputStream paramInputStream, byte[] paramArrayOfByte)
    throws IOException
  {
    return readSocksReply(paramInputStream, paramArrayOfByte, 0L);
  }
  
  private int readSocksReply(InputStream paramInputStream, byte[] paramArrayOfByte, long paramLong)
    throws IOException
  {
    int i = paramArrayOfByte.length;
    int j = 0;
    for (int k = 0; (j < i) && (k < 3); k++)
    {
      int m;
      try
      {
        m = ((SocketInputStream)paramInputStream).read(paramArrayOfByte, j, i - j, remainingMillis(paramLong));
      }
      catch (SocketTimeoutException localSocketTimeoutException)
      {
        throw new SocketTimeoutException("Connect timed out");
      }
      if (m < 0) {
        throw new SocketException("Malformed reply from SOCKS server");
      }
      j += m;
    }
    return j;
  }
  
  private boolean authenticate(byte paramByte, InputStream paramInputStream, BufferedOutputStream paramBufferedOutputStream)
    throws IOException
  {
    return authenticate(paramByte, paramInputStream, paramBufferedOutputStream, 0L);
  }
  
  private boolean authenticate(byte paramByte, InputStream paramInputStream, BufferedOutputStream paramBufferedOutputStream, long paramLong)
    throws IOException
  {
    if (paramByte == 0) {
      return true;
    }
    if (paramByte == 2)
    {
      String str2 = null;
      final InetAddress localInetAddress = InetAddress.getByName(server);
      PasswordAuthentication localPasswordAuthentication = (PasswordAuthentication)AccessController.doPrivileged(new PrivilegedAction()
      {
        public PasswordAuthentication run()
        {
          return Authenticator.requestPasswordAuthentication(server, localInetAddress, serverPort, "SOCKS5", "SOCKS authentication", null);
        }
      });
      String str1;
      if (localPasswordAuthentication != null)
      {
        str1 = localPasswordAuthentication.getUserName();
        str2 = new String(localPasswordAuthentication.getPassword());
      }
      else
      {
        str1 = (String)AccessController.doPrivileged(new GetPropertyAction("user.name"));
      }
      if (str1 == null) {
        return false;
      }
      paramBufferedOutputStream.write(1);
      paramBufferedOutputStream.write(str1.length());
      try
      {
        paramBufferedOutputStream.write(str1.getBytes("ISO-8859-1"));
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException1)
      {
        if (!$assertionsDisabled) {
          throw new AssertionError();
        }
      }
      if (str2 != null)
      {
        paramBufferedOutputStream.write(str2.length());
        try
        {
          paramBufferedOutputStream.write(str2.getBytes("ISO-8859-1"));
        }
        catch (UnsupportedEncodingException localUnsupportedEncodingException2)
        {
          if (!$assertionsDisabled) {
            throw new AssertionError();
          }
        }
      }
      else
      {
        paramBufferedOutputStream.write(0);
      }
      paramBufferedOutputStream.flush();
      byte[] arrayOfByte = new byte[2];
      int i = readSocksReply(paramInputStream, arrayOfByte, paramLong);
      if ((i != 2) || (arrayOfByte[1] != 0))
      {
        paramBufferedOutputStream.close();
        paramInputStream.close();
        return false;
      }
      return true;
    }
    return false;
  }
  
  private void connectV4(InputStream paramInputStream, OutputStream paramOutputStream, InetSocketAddress paramInetSocketAddress, long paramLong)
    throws IOException
  {
    if (!(paramInetSocketAddress.getAddress() instanceof Inet4Address)) {
      throw new SocketException("SOCKS V4 requires IPv4 only addresses");
    }
    paramOutputStream.write(4);
    paramOutputStream.write(1);
    paramOutputStream.write(paramInetSocketAddress.getPort() >> 8 & 0xFF);
    paramOutputStream.write(paramInetSocketAddress.getPort() >> 0 & 0xFF);
    paramOutputStream.write(paramInetSocketAddress.getAddress().getAddress());
    String str = getUserName();
    try
    {
      paramOutputStream.write(str.getBytes("ISO-8859-1"));
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    paramOutputStream.write(0);
    paramOutputStream.flush();
    byte[] arrayOfByte = new byte[8];
    int i = readSocksReply(paramInputStream, arrayOfByte, paramLong);
    if (i != 8) {
      throw new SocketException("Reply from SOCKS server has bad length: " + i);
    }
    if ((arrayOfByte[0] != 0) && (arrayOfByte[0] != 4)) {
      throw new SocketException("Reply from SOCKS server has bad version");
    }
    SocketException localSocketException = null;
    switch (arrayOfByte[1])
    {
    case 90: 
      external_address = paramInetSocketAddress;
      break;
    case 91: 
      localSocketException = new SocketException("SOCKS request rejected");
      break;
    case 92: 
      localSocketException = new SocketException("SOCKS server couldn't reach destination");
      break;
    case 93: 
      localSocketException = new SocketException("SOCKS authentication failed");
      break;
    default: 
      localSocketException = new SocketException("Reply from SOCKS server contains bad status");
    }
    if (localSocketException != null)
    {
      paramInputStream.close();
      paramOutputStream.close();
      throw localSocketException;
    }
  }
  
  protected void connect(SocketAddress paramSocketAddress, int paramInt)
    throws IOException
  {
    long l1;
    if (paramInt == 0)
    {
      l1 = 0L;
    }
    else
    {
      long l2 = System.currentTimeMillis() + paramInt;
      l1 = l2 < 0L ? Long.MAX_VALUE : l2;
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if ((paramSocketAddress == null) || (!(paramSocketAddress instanceof InetSocketAddress))) {
      throw new IllegalArgumentException("Unsupported address type");
    }
    InetSocketAddress localInetSocketAddress = (InetSocketAddress)paramSocketAddress;
    if (localSecurityManager != null) {
      if (localInetSocketAddress.isUnresolved()) {
        localSecurityManager.checkConnect(localInetSocketAddress.getHostName(), localInetSocketAddress.getPort());
      } else {
        localSecurityManager.checkConnect(localInetSocketAddress.getAddress().getHostAddress(), localInetSocketAddress.getPort());
      }
    }
    if (server == null)
    {
      ProxySelector localProxySelector = (ProxySelector)AccessController.doPrivileged(new PrivilegedAction()
      {
        public ProxySelector run()
        {
          return ProxySelector.getDefault();
        }
      });
      if (localProxySelector == null)
      {
        super.connect(localInetSocketAddress, remainingMillis(l1));
        return;
      }
      localObject2 = localInetSocketAddress.getHostString();
      if (((localInetSocketAddress.getAddress() instanceof Inet6Address)) && (!((String)localObject2).startsWith("[")) && (((String)localObject2).indexOf(":") >= 0)) {
        localObject2 = "[" + (String)localObject2 + "]";
      }
      try
      {
        localObject1 = new URI("socket://" + ParseUtil.encodePath((String)localObject2) + ":" + localInetSocketAddress.getPort());
      }
      catch (URISyntaxException localURISyntaxException)
      {
        if (!$assertionsDisabled) {
          throw new AssertionError(localURISyntaxException);
        }
        localObject1 = null;
      }
      Proxy localProxy = null;
      Object localObject3 = null;
      Iterator localIterator = null;
      localIterator = localProxySelector.select((URI)localObject1).iterator();
      if ((localIterator == null) || (!localIterator.hasNext()))
      {
        super.connect(localInetSocketAddress, remainingMillis(l1));
        return;
      }
      while (localIterator.hasNext())
      {
        localProxy = (Proxy)localIterator.next();
        if ((localProxy == null) || (localProxy.type() != Proxy.Type.SOCKS))
        {
          super.connect(localInetSocketAddress, remainingMillis(l1));
          return;
        }
        if (!(localProxy.address() instanceof InetSocketAddress)) {
          throw new SocketException("Unknown address type for proxy: " + localProxy);
        }
        server = ((InetSocketAddress)localProxy.address()).getHostString();
        serverPort = ((InetSocketAddress)localProxy.address()).getPort();
        if (((localProxy instanceof SocksProxy)) && (((SocksProxy)localProxy).protocolVersion() == 4)) {
          useV4 = true;
        }
        try
        {
          privilegedConnect(server, serverPort, remainingMillis(l1));
        }
        catch (IOException localIOException2)
        {
          localProxySelector.connectFailed((URI)localObject1, localProxy.address(), localIOException2);
          server = null;
          serverPort = -1;
          localObject3 = localIOException2;
        }
      }
      if (server == null) {
        throw new SocketException("Can't connect to SOCKS proxy:" + ((IOException)localObject3).getMessage());
      }
    }
    else
    {
      try
      {
        privilegedConnect(server, serverPort, remainingMillis(l1));
      }
      catch (IOException localIOException1)
      {
        throw new SocketException(localIOException1.getMessage());
      }
    }
    BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(cmdOut, 512);
    Object localObject1 = cmdIn;
    if (useV4)
    {
      if (localInetSocketAddress.isUnresolved()) {
        throw new UnknownHostException(localInetSocketAddress.toString());
      }
      connectV4((InputStream)localObject1, localBufferedOutputStream, localInetSocketAddress, l1);
      return;
    }
    localBufferedOutputStream.write(5);
    localBufferedOutputStream.write(2);
    localBufferedOutputStream.write(0);
    localBufferedOutputStream.write(2);
    localBufferedOutputStream.flush();
    Object localObject2 = new byte[2];
    int i = readSocksReply((InputStream)localObject1, (byte[])localObject2, l1);
    if ((i != 2) || (localObject2[0] != 5))
    {
      if (localInetSocketAddress.isUnresolved()) {
        throw new UnknownHostException(localInetSocketAddress.toString());
      }
      connectV4((InputStream)localObject1, localBufferedOutputStream, localInetSocketAddress, l1);
      return;
    }
    if (localObject2[1] == -1) {
      throw new SocketException("SOCKS : No acceptable methods");
    }
    if (!authenticate(localObject2[1], (InputStream)localObject1, localBufferedOutputStream, l1)) {
      throw new SocketException("SOCKS : authentication failed");
    }
    localBufferedOutputStream.write(5);
    localBufferedOutputStream.write(1);
    localBufferedOutputStream.write(0);
    if (localInetSocketAddress.isUnresolved())
    {
      localBufferedOutputStream.write(3);
      localBufferedOutputStream.write(localInetSocketAddress.getHostName().length());
      try
      {
        localBufferedOutputStream.write(localInetSocketAddress.getHostName().getBytes("ISO-8859-1"));
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        if (!$assertionsDisabled) {
          throw new AssertionError();
        }
      }
      localBufferedOutputStream.write(localInetSocketAddress.getPort() >> 8 & 0xFF);
      localBufferedOutputStream.write(localInetSocketAddress.getPort() >> 0 & 0xFF);
    }
    else if ((localInetSocketAddress.getAddress() instanceof Inet6Address))
    {
      localBufferedOutputStream.write(4);
      localBufferedOutputStream.write(localInetSocketAddress.getAddress().getAddress());
      localBufferedOutputStream.write(localInetSocketAddress.getPort() >> 8 & 0xFF);
      localBufferedOutputStream.write(localInetSocketAddress.getPort() >> 0 & 0xFF);
    }
    else
    {
      localBufferedOutputStream.write(1);
      localBufferedOutputStream.write(localInetSocketAddress.getAddress().getAddress());
      localBufferedOutputStream.write(localInetSocketAddress.getPort() >> 8 & 0xFF);
      localBufferedOutputStream.write(localInetSocketAddress.getPort() >> 0 & 0xFF);
    }
    localBufferedOutputStream.flush();
    localObject2 = new byte[4];
    i = readSocksReply((InputStream)localObject1, (byte[])localObject2, l1);
    if (i != 4) {
      throw new SocketException("Reply from SOCKS server has bad length");
    }
    SocketException localSocketException = null;
    switch (localObject2[1])
    {
    case 0: 
      byte[] arrayOfByte1;
      int j;
      switch (localObject2[3])
      {
      case 1: 
        arrayOfByte1 = new byte[4];
        i = readSocksReply((InputStream)localObject1, arrayOfByte1, l1);
        if (i != 4) {
          throw new SocketException("Reply from SOCKS server badly formatted");
        }
        localObject2 = new byte[2];
        i = readSocksReply((InputStream)localObject1, (byte[])localObject2, l1);
        if (i != 2) {
          throw new SocketException("Reply from SOCKS server badly formatted");
        }
        break;
      case 3: 
        j = localObject2[1];
        byte[] arrayOfByte2 = new byte[j];
        i = readSocksReply((InputStream)localObject1, arrayOfByte2, l1);
        if (i != j) {
          throw new SocketException("Reply from SOCKS server badly formatted");
        }
        localObject2 = new byte[2];
        i = readSocksReply((InputStream)localObject1, (byte[])localObject2, l1);
        if (i != 2) {
          throw new SocketException("Reply from SOCKS server badly formatted");
        }
        break;
      case 4: 
        j = localObject2[1];
        arrayOfByte1 = new byte[j];
        i = readSocksReply((InputStream)localObject1, arrayOfByte1, l1);
        if (i != j) {
          throw new SocketException("Reply from SOCKS server badly formatted");
        }
        localObject2 = new byte[2];
        i = readSocksReply((InputStream)localObject1, (byte[])localObject2, l1);
        if (i != 2) {
          throw new SocketException("Reply from SOCKS server badly formatted");
        }
        break;
      case 2: 
      default: 
        localSocketException = new SocketException("Reply from SOCKS server contains wrong code");
      }
      break;
    case 1: 
      localSocketException = new SocketException("SOCKS server general failure");
      break;
    case 2: 
      localSocketException = new SocketException("SOCKS: Connection not allowed by ruleset");
      break;
    case 3: 
      localSocketException = new SocketException("SOCKS: Network unreachable");
      break;
    case 4: 
      localSocketException = new SocketException("SOCKS: Host unreachable");
      break;
    case 5: 
      localSocketException = new SocketException("SOCKS: Connection refused");
      break;
    case 6: 
      localSocketException = new SocketException("SOCKS: TTL expired");
      break;
    case 7: 
      localSocketException = new SocketException("SOCKS: Command not supported");
      break;
    case 8: 
      localSocketException = new SocketException("SOCKS: address type not supported");
    }
    if (localSocketException != null)
    {
      ((InputStream)localObject1).close();
      localBufferedOutputStream.close();
      throw localSocketException;
    }
    external_address = localInetSocketAddress;
  }
  
  private void bindV4(InputStream paramInputStream, OutputStream paramOutputStream, InetAddress paramInetAddress, int paramInt)
    throws IOException
  {
    if (!(paramInetAddress instanceof Inet4Address)) {
      throw new SocketException("SOCKS V4 requires IPv4 only addresses");
    }
    super.bind(paramInetAddress, paramInt);
    byte[] arrayOfByte1 = paramInetAddress.getAddress();
    InetAddress localInetAddress = paramInetAddress;
    if (localInetAddress.isAnyLocalAddress())
    {
      localInetAddress = (InetAddress)AccessController.doPrivileged(new PrivilegedAction()
      {
        public InetAddress run()
        {
          return cmdsock.getLocalAddress();
        }
      });
      arrayOfByte1 = localInetAddress.getAddress();
    }
    paramOutputStream.write(4);
    paramOutputStream.write(2);
    paramOutputStream.write(super.getLocalPort() >> 8 & 0xFF);
    paramOutputStream.write(super.getLocalPort() >> 0 & 0xFF);
    paramOutputStream.write(arrayOfByte1);
    String str = getUserName();
    try
    {
      paramOutputStream.write(str.getBytes("ISO-8859-1"));
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    paramOutputStream.write(0);
    paramOutputStream.flush();
    byte[] arrayOfByte2 = new byte[8];
    int i = readSocksReply(paramInputStream, arrayOfByte2);
    if (i != 8) {
      throw new SocketException("Reply from SOCKS server has bad length: " + i);
    }
    if ((arrayOfByte2[0] != 0) && (arrayOfByte2[0] != 4)) {
      throw new SocketException("Reply from SOCKS server has bad version");
    }
    SocketException localSocketException = null;
    switch (arrayOfByte2[1])
    {
    case 90: 
      external_address = new InetSocketAddress(paramInetAddress, paramInt);
      break;
    case 91: 
      localSocketException = new SocketException("SOCKS request rejected");
      break;
    case 92: 
      localSocketException = new SocketException("SOCKS server couldn't reach destination");
      break;
    case 93: 
      localSocketException = new SocketException("SOCKS authentication failed");
      break;
    default: 
      localSocketException = new SocketException("Reply from SOCKS server contains bad status");
    }
    if (localSocketException != null)
    {
      paramInputStream.close();
      paramOutputStream.close();
      throw localSocketException;
    }
  }
  
  protected synchronized void socksBind(InetSocketAddress paramInetSocketAddress)
    throws IOException
  {
    if (socket != null) {
      return;
    }
    if (server == null)
    {
      ProxySelector localProxySelector = (ProxySelector)AccessController.doPrivileged(new PrivilegedAction()
      {
        public ProxySelector run()
        {
          return ProxySelector.getDefault();
        }
      });
      if (localProxySelector == null) {
        return;
      }
      localObject2 = paramInetSocketAddress.getHostString();
      if (((paramInetSocketAddress.getAddress() instanceof Inet6Address)) && (!((String)localObject2).startsWith("[")) && (((String)localObject2).indexOf(":") >= 0)) {
        localObject2 = "[" + (String)localObject2 + "]";
      }
      try
      {
        localObject1 = new URI("serversocket://" + ParseUtil.encodePath((String)localObject2) + ":" + paramInetSocketAddress.getPort());
      }
      catch (URISyntaxException localURISyntaxException)
      {
        if (!$assertionsDisabled) {
          throw new AssertionError(localURISyntaxException);
        }
        localObject1 = null;
      }
      Proxy localProxy = null;
      Object localObject3 = null;
      Iterator localIterator = null;
      localIterator = localProxySelector.select((URI)localObject1).iterator();
      if ((localIterator == null) || (!localIterator.hasNext())) {
        return;
      }
      while (localIterator.hasNext())
      {
        localProxy = (Proxy)localIterator.next();
        if ((localProxy == null) || (localProxy.type() != Proxy.Type.SOCKS)) {
          return;
        }
        if (!(localProxy.address() instanceof InetSocketAddress)) {
          throw new SocketException("Unknown address type for proxy: " + localProxy);
        }
        server = ((InetSocketAddress)localProxy.address()).getHostString();
        serverPort = ((InetSocketAddress)localProxy.address()).getPort();
        if (((localProxy instanceof SocksProxy)) && (((SocksProxy)localProxy).protocolVersion() == 4)) {
          useV4 = true;
        }
        try
        {
          AccessController.doPrivileged(new PrivilegedExceptionAction()
          {
            public Void run()
              throws Exception
            {
              cmdsock = new Socket(new PlainSocketImpl());
              cmdsock.connect(new InetSocketAddress(server, serverPort));
              cmdIn = cmdsock.getInputStream();
              cmdOut = cmdsock.getOutputStream();
              return null;
            }
          });
        }
        catch (Exception localException2)
        {
          localProxySelector.connectFailed((URI)localObject1, localProxy.address(), new SocketException(localException2.getMessage()));
          server = null;
          serverPort = -1;
          cmdsock = null;
          localObject3 = localException2;
        }
      }
      if ((server == null) || (cmdsock == null)) {
        throw new SocketException("Can't connect to SOCKS proxy:" + ((Exception)localObject3).getMessage());
      }
    }
    else
    {
      try
      {
        AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public Void run()
            throws Exception
          {
            cmdsock = new Socket(new PlainSocketImpl());
            cmdsock.connect(new InetSocketAddress(server, serverPort));
            cmdIn = cmdsock.getInputStream();
            cmdOut = cmdsock.getOutputStream();
            return null;
          }
        });
      }
      catch (Exception localException1)
      {
        throw new SocketException(localException1.getMessage());
      }
    }
    BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(cmdOut, 512);
    Object localObject1 = cmdIn;
    if (useV4)
    {
      bindV4((InputStream)localObject1, localBufferedOutputStream, paramInetSocketAddress.getAddress(), paramInetSocketAddress.getPort());
      return;
    }
    localBufferedOutputStream.write(5);
    localBufferedOutputStream.write(2);
    localBufferedOutputStream.write(0);
    localBufferedOutputStream.write(2);
    localBufferedOutputStream.flush();
    Object localObject2 = new byte[2];
    int i = readSocksReply((InputStream)localObject1, (byte[])localObject2);
    if ((i != 2) || (localObject2[0] != 5))
    {
      bindV4((InputStream)localObject1, localBufferedOutputStream, paramInetSocketAddress.getAddress(), paramInetSocketAddress.getPort());
      return;
    }
    if (localObject2[1] == -1) {
      throw new SocketException("SOCKS : No acceptable methods");
    }
    if (!authenticate(localObject2[1], (InputStream)localObject1, localBufferedOutputStream)) {
      throw new SocketException("SOCKS : authentication failed");
    }
    localBufferedOutputStream.write(5);
    localBufferedOutputStream.write(2);
    localBufferedOutputStream.write(0);
    int j = paramInetSocketAddress.getPort();
    if (paramInetSocketAddress.isUnresolved())
    {
      localBufferedOutputStream.write(3);
      localBufferedOutputStream.write(paramInetSocketAddress.getHostName().length());
      try
      {
        localBufferedOutputStream.write(paramInetSocketAddress.getHostName().getBytes("ISO-8859-1"));
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        if (!$assertionsDisabled) {
          throw new AssertionError();
        }
      }
      localBufferedOutputStream.write(j >> 8 & 0xFF);
      localBufferedOutputStream.write(j >> 0 & 0xFF);
    }
    else if ((paramInetSocketAddress.getAddress() instanceof Inet4Address))
    {
      localObject4 = paramInetSocketAddress.getAddress().getAddress();
      localBufferedOutputStream.write(1);
      localBufferedOutputStream.write((byte[])localObject4);
      localBufferedOutputStream.write(j >> 8 & 0xFF);
      localBufferedOutputStream.write(j >> 0 & 0xFF);
      localBufferedOutputStream.flush();
    }
    else if ((paramInetSocketAddress.getAddress() instanceof Inet6Address))
    {
      localObject4 = paramInetSocketAddress.getAddress().getAddress();
      localBufferedOutputStream.write(4);
      localBufferedOutputStream.write((byte[])localObject4);
      localBufferedOutputStream.write(j >> 8 & 0xFF);
      localBufferedOutputStream.write(j >> 0 & 0xFF);
      localBufferedOutputStream.flush();
    }
    else
    {
      cmdsock.close();
      throw new SocketException("unsupported address type : " + paramInetSocketAddress);
    }
    localObject2 = new byte[4];
    i = readSocksReply((InputStream)localObject1, (byte[])localObject2);
    Object localObject4 = null;
    switch (localObject2[1])
    {
    case 0: 
      byte[] arrayOfByte1;
      int m;
      int k;
      switch (localObject2[3])
      {
      case 1: 
        arrayOfByte1 = new byte[4];
        i = readSocksReply((InputStream)localObject1, arrayOfByte1);
        if (i != 4) {
          throw new SocketException("Reply from SOCKS server badly formatted");
        }
        localObject2 = new byte[2];
        i = readSocksReply((InputStream)localObject1, (byte[])localObject2);
        if (i != 2) {
          throw new SocketException("Reply from SOCKS server badly formatted");
        }
        m = (localObject2[0] & 0xFF) << 8;
        m += (localObject2[1] & 0xFF);
        external_address = new InetSocketAddress(new Inet4Address("", arrayOfByte1), m);
        break;
      case 3: 
        k = localObject2[1];
        byte[] arrayOfByte2 = new byte[k];
        i = readSocksReply((InputStream)localObject1, arrayOfByte2);
        if (i != k) {
          throw new SocketException("Reply from SOCKS server badly formatted");
        }
        localObject2 = new byte[2];
        i = readSocksReply((InputStream)localObject1, (byte[])localObject2);
        if (i != 2) {
          throw new SocketException("Reply from SOCKS server badly formatted");
        }
        m = (localObject2[0] & 0xFF) << 8;
        m += (localObject2[1] & 0xFF);
        external_address = new InetSocketAddress(new String(arrayOfByte2), m);
        break;
      case 4: 
        k = localObject2[1];
        arrayOfByte1 = new byte[k];
        i = readSocksReply((InputStream)localObject1, arrayOfByte1);
        if (i != k) {
          throw new SocketException("Reply from SOCKS server badly formatted");
        }
        localObject2 = new byte[2];
        i = readSocksReply((InputStream)localObject1, (byte[])localObject2);
        if (i != 2) {
          throw new SocketException("Reply from SOCKS server badly formatted");
        }
        m = (localObject2[0] & 0xFF) << 8;
        m += (localObject2[1] & 0xFF);
        external_address = new InetSocketAddress(new Inet6Address("", arrayOfByte1), m);
      }
      break;
    case 1: 
      localObject4 = new SocketException("SOCKS server general failure");
      break;
    case 2: 
      localObject4 = new SocketException("SOCKS: Bind not allowed by ruleset");
      break;
    case 3: 
      localObject4 = new SocketException("SOCKS: Network unreachable");
      break;
    case 4: 
      localObject4 = new SocketException("SOCKS: Host unreachable");
      break;
    case 5: 
      localObject4 = new SocketException("SOCKS: Connection refused");
      break;
    case 6: 
      localObject4 = new SocketException("SOCKS: TTL expired");
      break;
    case 7: 
      localObject4 = new SocketException("SOCKS: Command not supported");
      break;
    case 8: 
      localObject4 = new SocketException("SOCKS: address type not supported");
    }
    if (localObject4 != null)
    {
      ((InputStream)localObject1).close();
      localBufferedOutputStream.close();
      cmdsock.close();
      cmdsock = null;
      throw ((Throwable)localObject4);
    }
    cmdIn = ((InputStream)localObject1);
    cmdOut = localBufferedOutputStream;
  }
  
  protected void acceptFrom(SocketImpl paramSocketImpl, InetSocketAddress paramInetSocketAddress)
    throws IOException
  {
    if (cmdsock == null) {
      return;
    }
    InputStream localInputStream = cmdIn;
    socksBind(paramInetSocketAddress);
    localInputStream.read();
    int i = localInputStream.read();
    localInputStream.read();
    SocketException localSocketException = null;
    InetSocketAddress localInetSocketAddress = null;
    switch (i)
    {
    case 0: 
      i = localInputStream.read();
      byte[] arrayOfByte;
      int j;
      switch (i)
      {
      case 1: 
        arrayOfByte = new byte[4];
        readSocksReply(localInputStream, arrayOfByte);
        j = localInputStream.read() << 8;
        j += localInputStream.read();
        localInetSocketAddress = new InetSocketAddress(new Inet4Address("", arrayOfByte), j);
        break;
      case 3: 
        int k = localInputStream.read();
        arrayOfByte = new byte[k];
        readSocksReply(localInputStream, arrayOfByte);
        j = localInputStream.read() << 8;
        j += localInputStream.read();
        localInetSocketAddress = new InetSocketAddress(new String(arrayOfByte), j);
        break;
      case 4: 
        arrayOfByte = new byte[16];
        readSocksReply(localInputStream, arrayOfByte);
        j = localInputStream.read() << 8;
        j += localInputStream.read();
        localInetSocketAddress = new InetSocketAddress(new Inet6Address("", arrayOfByte), j);
      }
      break;
    case 1: 
      localSocketException = new SocketException("SOCKS server general failure");
      break;
    case 2: 
      localSocketException = new SocketException("SOCKS: Accept not allowed by ruleset");
      break;
    case 3: 
      localSocketException = new SocketException("SOCKS: Network unreachable");
      break;
    case 4: 
      localSocketException = new SocketException("SOCKS: Host unreachable");
      break;
    case 5: 
      localSocketException = new SocketException("SOCKS: Connection refused");
      break;
    case 6: 
      localSocketException = new SocketException("SOCKS: TTL expired");
      break;
    case 7: 
      localSocketException = new SocketException("SOCKS: Command not supported");
      break;
    case 8: 
      localSocketException = new SocketException("SOCKS: address type not supported");
    }
    if (localSocketException != null)
    {
      cmdIn.close();
      cmdOut.close();
      cmdsock.close();
      cmdsock = null;
      throw localSocketException;
    }
    if ((paramSocketImpl instanceof SocksSocketImpl)) {
      external_address = localInetSocketAddress;
    }
    if ((paramSocketImpl instanceof PlainSocketImpl))
    {
      PlainSocketImpl localPlainSocketImpl = (PlainSocketImpl)paramSocketImpl;
      localPlainSocketImpl.setInputStream((SocketInputStream)localInputStream);
      localPlainSocketImpl.setFileDescriptor(cmdsock.getImpl().getFileDescriptor());
      localPlainSocketImpl.setAddress(cmdsock.getImpl().getInetAddress());
      localPlainSocketImpl.setPort(cmdsock.getImpl().getPort());
      localPlainSocketImpl.setLocalPort(cmdsock.getImpl().getLocalPort());
    }
    else
    {
      fd = cmdsock.getImpl().fd;
      address = cmdsock.getImpl().address;
      port = cmdsock.getImpl().port;
      localport = cmdsock.getImpl().localport;
    }
    cmdsock = null;
  }
  
  protected InetAddress getInetAddress()
  {
    if (external_address != null) {
      return external_address.getAddress();
    }
    return super.getInetAddress();
  }
  
  protected int getPort()
  {
    if (external_address != null) {
      return external_address.getPort();
    }
    return super.getPort();
  }
  
  protected int getLocalPort()
  {
    if (socket != null) {
      return super.getLocalPort();
    }
    if (external_address != null) {
      return external_address.getPort();
    }
    return super.getLocalPort();
  }
  
  protected void close()
    throws IOException
  {
    if (cmdsock != null) {
      cmdsock.close();
    }
    cmdsock = null;
    super.close();
  }
  
  private String getUserName()
  {
    String str = "";
    if (applicationSetProxy) {
      try
      {
        str = System.getProperty("user.name");
      }
      catch (SecurityException localSecurityException) {}
    } else {
      str = (String)AccessController.doPrivileged(new GetPropertyAction("user.name"));
    }
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\SocksSocketImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */