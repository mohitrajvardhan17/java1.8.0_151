package sun.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;

public class NetworkClient
{
  public static final int DEFAULT_READ_TIMEOUT = -1;
  public static final int DEFAULT_CONNECT_TIMEOUT = -1;
  protected Proxy proxy = Proxy.NO_PROXY;
  protected Socket serverSocket = null;
  public PrintStream serverOutput;
  public InputStream serverInput;
  protected static int defaultSoTimeout;
  protected static int defaultConnectTimeout;
  protected int readTimeout = -1;
  protected int connectTimeout = -1;
  protected static String encoding;
  
  private static boolean isASCIISuperset(String paramString)
    throws Exception
  {
    String str = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_.!~*'();/?:@&=+$,";
    byte[] arrayOfByte1 = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 45, 95, 46, 33, 126, 42, 39, 40, 41, 59, 47, 63, 58, 64, 38, 61, 43, 36, 44 };
    byte[] arrayOfByte2 = str.getBytes(paramString);
    return Arrays.equals(arrayOfByte2, arrayOfByte1);
  }
  
  public void openServer(String paramString, int paramInt)
    throws IOException, UnknownHostException
  {
    if (serverSocket != null) {
      closeServer();
    }
    serverSocket = doConnect(paramString, paramInt);
    try
    {
      serverOutput = new PrintStream(new BufferedOutputStream(serverSocket.getOutputStream()), true, encoding);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new InternalError(encoding + "encoding not found", localUnsupportedEncodingException);
    }
    serverInput = new BufferedInputStream(serverSocket.getInputStream());
  }
  
  protected Socket doConnect(String paramString, int paramInt)
    throws IOException, UnknownHostException
  {
    Socket localSocket;
    if (proxy != null)
    {
      if (proxy.type() == Proxy.Type.SOCKS) {
        localSocket = (Socket)AccessController.doPrivileged(new PrivilegedAction()
        {
          public Socket run()
          {
            return new Socket(proxy);
          }
        });
      } else if (proxy.type() == Proxy.Type.DIRECT) {
        localSocket = createSocket();
      } else {
        localSocket = new Socket(Proxy.NO_PROXY);
      }
    }
    else {
      localSocket = createSocket();
    }
    if (connectTimeout >= 0) {
      localSocket.connect(new InetSocketAddress(paramString, paramInt), connectTimeout);
    } else if (defaultConnectTimeout > 0) {
      localSocket.connect(new InetSocketAddress(paramString, paramInt), defaultConnectTimeout);
    } else {
      localSocket.connect(new InetSocketAddress(paramString, paramInt));
    }
    if (readTimeout >= 0) {
      localSocket.setSoTimeout(readTimeout);
    } else if (defaultSoTimeout > 0) {
      localSocket.setSoTimeout(defaultSoTimeout);
    }
    return localSocket;
  }
  
  protected Socket createSocket()
    throws IOException
  {
    return new Socket();
  }
  
  protected InetAddress getLocalAddress()
    throws IOException
  {
    if (serverSocket == null) {
      throw new IOException("not connected");
    }
    (InetAddress)AccessController.doPrivileged(new PrivilegedAction()
    {
      public InetAddress run()
      {
        return serverSocket.getLocalAddress();
      }
    });
  }
  
  public void closeServer()
    throws IOException
  {
    if (!serverIsOpen()) {
      return;
    }
    serverSocket.close();
    serverSocket = null;
    serverInput = null;
    serverOutput = null;
  }
  
  public boolean serverIsOpen()
  {
    return serverSocket != null;
  }
  
  public NetworkClient(String paramString, int paramInt)
    throws IOException
  {
    openServer(paramString, paramInt);
  }
  
  public NetworkClient() {}
  
  public void setConnectTimeout(int paramInt)
  {
    connectTimeout = paramInt;
  }
  
  public int getConnectTimeout()
  {
    return connectTimeout;
  }
  
  public void setReadTimeout(int paramInt)
  {
    if (paramInt == -1) {
      paramInt = defaultSoTimeout;
    }
    if ((serverSocket != null) && (paramInt >= 0)) {
      try
      {
        serverSocket.setSoTimeout(paramInt);
      }
      catch (IOException localIOException) {}
    }
    readTimeout = paramInt;
  }
  
  public int getReadTimeout()
  {
    return readTimeout;
  }
  
  static
  {
    int[] arrayOfInt = { 0, 0 };
    final String[] arrayOfString = { null };
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        val$vals[0] = Integer.getInteger("sun.net.client.defaultReadTimeout", 0).intValue();
        val$vals[1] = Integer.getInteger("sun.net.client.defaultConnectTimeout", 0).intValue();
        arrayOfString[0] = System.getProperty("file.encoding", "ISO8859_1");
        return null;
      }
    });
    if (arrayOfInt[0] != 0) {
      defaultSoTimeout = arrayOfInt[0];
    }
    if (arrayOfInt[1] != 0) {
      defaultConnectTimeout = arrayOfInt[1];
    }
    encoding = arrayOfString[0];
    try
    {
      if (!isASCIISuperset(encoding)) {
        encoding = "ISO8859_1";
      }
    }
    catch (Exception localException)
    {
      encoding = "ISO8859_1";
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\NetworkClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */