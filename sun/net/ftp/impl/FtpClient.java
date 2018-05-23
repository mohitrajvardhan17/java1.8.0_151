package sun.net.ftp.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sun.net.TelnetInputStream;
import sun.net.TelnetOutputStream;
import sun.net.ftp.FtpClient.TransferType;
import sun.net.ftp.FtpDirEntry;
import sun.net.ftp.FtpDirEntry.Type;
import sun.net.ftp.FtpDirParser;
import sun.net.ftp.FtpProtocolException;
import sun.net.ftp.FtpReplyCode;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

public class FtpClient
  extends sun.net.ftp.FtpClient
{
  private static int defaultSoTimeout;
  private static int defaultConnectTimeout;
  private static final PlatformLogger logger = PlatformLogger.getLogger("sun.net.ftp.FtpClient");
  private Proxy proxy;
  private Socket server;
  private PrintStream out;
  private InputStream in;
  private int readTimeout = -1;
  private int connectTimeout = -1;
  private static String encoding = "ISO8859_1";
  private InetSocketAddress serverAddr;
  private boolean replyPending = false;
  private boolean loggedIn = false;
  private boolean useCrypto = false;
  private SSLSocketFactory sslFact;
  private Socket oldSocket;
  private Vector<String> serverResponse = new Vector(1);
  private FtpReplyCode lastReplyCode = null;
  private String welcomeMsg;
  private final boolean passiveMode = true;
  private FtpClient.TransferType type = FtpClient.TransferType.BINARY;
  private long restartOffset = 0L;
  private long lastTransSize = -1L;
  private String lastFileName;
  private static String[] patStrings = { "([\\-ld](?:[r\\-][w\\-][x\\-]){3})\\s*\\d+ (\\w+)\\s*(\\w+)\\s*(\\d+)\\s*([A-Z][a-z][a-z]\\s*\\d+)\\s*(\\d\\d:\\d\\d)\\s*(\\p{Print}*)", "([\\-ld](?:[r\\-][w\\-][x\\-]){3})\\s*\\d+ (\\w+)\\s*(\\w+)\\s*(\\d+)\\s*([A-Z][a-z][a-z]\\s*\\d+)\\s*(\\d{4})\\s*(\\p{Print}*)", "(\\d{2}/\\d{2}/\\d{4})\\s*(\\d{2}:\\d{2}[ap])\\s*((?:[0-9,]+)|(?:<DIR>))\\s*(\\p{Graph}*)", "(\\d{2}-\\d{2}-\\d{2})\\s*(\\d{2}:\\d{2}[AP]M)\\s*((?:[0-9,]+)|(?:<DIR>))\\s*(\\p{Graph}*)" };
  private static int[][] patternGroups = { { 7, 4, 5, 6, 0, 1, 2, 3 }, { 7, 4, 5, 0, 6, 1, 2, 3 }, { 4, 3, 1, 2, 0, 0, 0, 0 }, { 4, 3, 1, 2, 0, 0, 0, 0 } };
  private static Pattern[] patterns;
  private static Pattern linkp = Pattern.compile("(\\p{Print}+) \\-\\> (\\p{Print}+)$");
  private DateFormat df = DateFormat.getDateInstance(2, Locale.US);
  private FtpDirParser parser = new DefaultParser(null);
  private FtpDirParser mlsxParser = new MLSxParser(null);
  private static Pattern transPat;
  private static Pattern epsvPat;
  private static Pattern pasvPat;
  private static String[] MDTMformats;
  private static SimpleDateFormat[] dateFormats;
  
  private static boolean isASCIISuperset(String paramString)
    throws Exception
  {
    String str = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_.!~*'();/?:@&=+$,";
    byte[] arrayOfByte1 = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 45, 95, 46, 33, 126, 42, 39, 40, 41, 59, 47, 63, 58, 64, 38, 61, 43, 36, 44 };
    byte[] arrayOfByte2 = str.getBytes(paramString);
    return Arrays.equals(arrayOfByte2, arrayOfByte1);
  }
  
  private void getTransferSize()
  {
    lastTransSize = -1L;
    String str1 = getLastResponseString();
    if (transPat == null) {
      transPat = Pattern.compile("150 Opening .*\\((\\d+) bytes\\).");
    }
    Matcher localMatcher = transPat.matcher(str1);
    if (localMatcher.find())
    {
      String str2 = localMatcher.group(1);
      lastTransSize = Long.parseLong(str2);
    }
  }
  
  private void getTransferName()
  {
    lastFileName = null;
    String str = getLastResponseString();
    int i = str.indexOf("unique file name:");
    int j = str.lastIndexOf(')');
    if (i >= 0)
    {
      i += 17;
      lastFileName = str.substring(i, j);
    }
  }
  
  private int readServerResponse()
    throws IOException
  {
    StringBuffer localStringBuffer = new StringBuffer(32);
    int j = -1;
    serverResponse.setSize(0);
    int k;
    for (;;)
    {
      int i;
      if ((i = in.read()) != -1)
      {
        if ((i == 13) && ((i = in.read()) != 10)) {
          localStringBuffer.append('\r');
        }
        localStringBuffer.append((char)i);
        if (i != 10) {
          continue;
        }
      }
      String str = localStringBuffer.toString();
      localStringBuffer.setLength(0);
      if (logger.isLoggable(PlatformLogger.Level.FINEST)) {
        logger.finest("Server [" + serverAddr + "] --> " + str);
      }
      if (str.length() == 0)
      {
        k = -1;
      }
      else
      {
        try
        {
          k = Integer.parseInt(str.substring(0, 3));
        }
        catch (NumberFormatException localNumberFormatException)
        {
          k = -1;
        }
        catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException) {}
        continue;
      }
      serverResponse.addElement(str);
      if (j != -1)
      {
        if ((k == j) && ((str.length() < 4) || (str.charAt(3) != '-')))
        {
          j = -1;
          break;
        }
      }
      else
      {
        if ((str.length() < 4) || (str.charAt(3) != '-')) {
          break;
        }
        j = k;
      }
    }
    return k;
  }
  
  private void sendServer(String paramString)
  {
    out.print(paramString);
    if (logger.isLoggable(PlatformLogger.Level.FINEST)) {
      logger.finest("Server [" + serverAddr + "] <-- " + paramString);
    }
  }
  
  private String getResponseString()
  {
    return (String)serverResponse.elementAt(0);
  }
  
  private Vector<String> getResponseStrings()
  {
    return serverResponse;
  }
  
  private boolean readReply()
    throws IOException
  {
    lastReplyCode = FtpReplyCode.find(readServerResponse());
    if (lastReplyCode.isPositivePreliminary())
    {
      replyPending = true;
      return true;
    }
    if ((lastReplyCode.isPositiveCompletion()) || (lastReplyCode.isPositiveIntermediate()))
    {
      if (lastReplyCode == FtpReplyCode.CLOSING_DATA_CONNECTION) {
        getTransferName();
      }
      return true;
    }
    return false;
  }
  
  private boolean issueCommand(String paramString)
    throws IOException, FtpProtocolException
  {
    if (!isConnected()) {
      throw new IllegalStateException("Not connected");
    }
    if (replyPending) {
      try
      {
        completePending();
      }
      catch (FtpProtocolException localFtpProtocolException1) {}
    }
    if (paramString.indexOf('\n') != -1)
    {
      FtpProtocolException localFtpProtocolException2 = new FtpProtocolException("Illegal FTP command");
      localFtpProtocolException2.initCause(new IllegalArgumentException("Illegal carriage return"));
      throw localFtpProtocolException2;
    }
    sendServer(paramString + "\r\n");
    return readReply();
  }
  
  private void issueCommandCheck(String paramString)
    throws FtpProtocolException, IOException
  {
    if (!issueCommand(paramString)) {
      throw new FtpProtocolException(paramString + ":" + getResponseString(), getLastReplyCode());
    }
  }
  
  private Socket openPassiveDataConnection(String paramString)
    throws FtpProtocolException, IOException
  {
    InetSocketAddress localInetSocketAddress = null;
    String str;
    Object localObject1;
    int i;
    if (issueCommand("EPSV ALL"))
    {
      issueCommandCheck("EPSV");
      str = getResponseString();
      if (epsvPat == null) {
        epsvPat = Pattern.compile("^229 .* \\(\\|\\|\\|(\\d+)\\|\\)");
      }
      localObject1 = epsvPat.matcher(str);
      if (!((Matcher)localObject1).find()) {
        throw new FtpProtocolException("EPSV failed : " + str);
      }
      localObject2 = ((Matcher)localObject1).group(1);
      i = Integer.parseInt((String)localObject2);
      InetAddress localInetAddress = server.getInetAddress();
      if (localInetAddress != null) {
        localInetSocketAddress = new InetSocketAddress(localInetAddress, i);
      } else {
        localInetSocketAddress = InetSocketAddress.createUnresolved(serverAddr.getHostName(), i);
      }
    }
    else
    {
      issueCommandCheck("PASV");
      str = getResponseString();
      if (pasvPat == null) {
        pasvPat = Pattern.compile("227 .* \\(?(\\d{1,3},\\d{1,3},\\d{1,3},\\d{1,3}),(\\d{1,3}),(\\d{1,3})\\)?");
      }
      localObject1 = pasvPat.matcher(str);
      if (!((Matcher)localObject1).find()) {
        throw new FtpProtocolException("PASV failed : " + str);
      }
      i = Integer.parseInt(((Matcher)localObject1).group(3)) + (Integer.parseInt(((Matcher)localObject1).group(2)) << 8);
      localObject2 = ((Matcher)localObject1).group(1).replace(',', '.');
      localInetSocketAddress = new InetSocketAddress((String)localObject2, i);
    }
    if (proxy != null)
    {
      if (proxy.type() == Proxy.Type.SOCKS) {
        localObject1 = (Socket)AccessController.doPrivileged(new PrivilegedAction()
        {
          public Socket run()
          {
            return new Socket(proxy);
          }
        });
      } else {
        localObject1 = new Socket(Proxy.NO_PROXY);
      }
    }
    else {
      localObject1 = new Socket();
    }
    Object localObject2 = (InetAddress)AccessController.doPrivileged(new PrivilegedAction()
    {
      public InetAddress run()
      {
        return server.getLocalAddress();
      }
    });
    ((Socket)localObject1).bind(new InetSocketAddress((InetAddress)localObject2, 0));
    if (connectTimeout >= 0) {
      ((Socket)localObject1).connect(localInetSocketAddress, connectTimeout);
    } else if (defaultConnectTimeout > 0) {
      ((Socket)localObject1).connect(localInetSocketAddress, defaultConnectTimeout);
    } else {
      ((Socket)localObject1).connect(localInetSocketAddress);
    }
    if (readTimeout >= 0) {
      ((Socket)localObject1).setSoTimeout(readTimeout);
    } else if (defaultSoTimeout > 0) {
      ((Socket)localObject1).setSoTimeout(defaultSoTimeout);
    }
    if (useCrypto) {
      try
      {
        localObject1 = sslFact.createSocket((Socket)localObject1, localInetSocketAddress.getHostName(), localInetSocketAddress.getPort(), true);
      }
      catch (Exception localException)
      {
        throw new FtpProtocolException("Can't open secure data channel: " + localException);
      }
    }
    if (!issueCommand(paramString))
    {
      ((Socket)localObject1).close();
      if (getLastReplyCode() == FtpReplyCode.FILE_UNAVAILABLE) {
        throw new FileNotFoundException(paramString);
      }
      throw new FtpProtocolException(paramString + ":" + getResponseString(), getLastReplyCode());
    }
    return (Socket)localObject1;
  }
  
  private Socket openDataConnection(String paramString)
    throws FtpProtocolException, IOException
  {
    try
    {
      return openPassiveDataConnection(paramString);
    }
    catch (FtpProtocolException localFtpProtocolException)
    {
      Object localObject1 = localFtpProtocolException.getMessage();
      if ((!((String)localObject1).startsWith("PASV")) && (!((String)localObject1).startsWith("EPSV"))) {
        throw localFtpProtocolException;
      }
      if ((proxy != null) && (proxy.type() == Proxy.Type.SOCKS)) {
        throw new FtpProtocolException("Passive mode failed");
      }
      ServerSocket localServerSocket = new ServerSocket(0, 1, server.getLocalAddress());
      Socket localSocket;
      try
      {
        localObject1 = localServerSocket.getInetAddress();
        if (((InetAddress)localObject1).isAnyLocalAddress()) {
          localObject1 = server.getLocalAddress();
        }
        String str = "EPRT |" + ((localObject1 instanceof Inet6Address) ? "2" : "1") + "|" + ((InetAddress)localObject1).getHostAddress() + "|" + localServerSocket.getLocalPort() + "|";
        if ((!issueCommand(str)) || (!issueCommand(paramString)))
        {
          str = "PORT ";
          byte[] arrayOfByte = ((InetAddress)localObject1).getAddress();
          for (int i = 0; i < arrayOfByte.length; i++) {
            str = str + (arrayOfByte[i] & 0xFF) + ",";
          }
          str = str + (localServerSocket.getLocalPort() >>> 8 & 0xFF) + "," + (localServerSocket.getLocalPort() & 0xFF);
          issueCommandCheck(str);
          issueCommandCheck(paramString);
        }
        if (connectTimeout >= 0) {
          localServerSocket.setSoTimeout(connectTimeout);
        } else if (defaultConnectTimeout > 0) {
          localServerSocket.setSoTimeout(defaultConnectTimeout);
        }
        localSocket = localServerSocket.accept();
        if (readTimeout >= 0) {
          localSocket.setSoTimeout(readTimeout);
        } else if (defaultSoTimeout > 0) {
          localSocket.setSoTimeout(defaultSoTimeout);
        }
      }
      finally
      {
        localServerSocket.close();
      }
      if (useCrypto) {
        try
        {
          localSocket = sslFact.createSocket(localSocket, serverAddr.getHostName(), serverAddr.getPort(), true);
        }
        catch (Exception localException)
        {
          throw new IOException(localException.getLocalizedMessage());
        }
      }
      return localSocket;
    }
  }
  
  private InputStream createInputStream(InputStream paramInputStream)
  {
    if (type == FtpClient.TransferType.ASCII) {
      return new TelnetInputStream(paramInputStream, false);
    }
    return paramInputStream;
  }
  
  private OutputStream createOutputStream(OutputStream paramOutputStream)
  {
    if (type == FtpClient.TransferType.ASCII) {
      return new TelnetOutputStream(paramOutputStream, false);
    }
    return paramOutputStream;
  }
  
  protected FtpClient() {}
  
  public static sun.net.ftp.FtpClient create()
  {
    return new FtpClient();
  }
  
  public sun.net.ftp.FtpClient enablePassiveMode(boolean paramBoolean)
  {
    return this;
  }
  
  public boolean isPassiveModeEnabled()
  {
    return true;
  }
  
  public sun.net.ftp.FtpClient setConnectTimeout(int paramInt)
  {
    connectTimeout = paramInt;
    return this;
  }
  
  public int getConnectTimeout()
  {
    return connectTimeout;
  }
  
  public sun.net.ftp.FtpClient setReadTimeout(int paramInt)
  {
    readTimeout = paramInt;
    return this;
  }
  
  public int getReadTimeout()
  {
    return readTimeout;
  }
  
  public sun.net.ftp.FtpClient setProxy(Proxy paramProxy)
  {
    proxy = paramProxy;
    return this;
  }
  
  public Proxy getProxy()
  {
    return proxy;
  }
  
  private void tryConnect(InetSocketAddress paramInetSocketAddress, int paramInt)
    throws IOException
  {
    if (isConnected()) {
      disconnect();
    }
    server = doConnect(paramInetSocketAddress, paramInt);
    try
    {
      out = new PrintStream(new BufferedOutputStream(server.getOutputStream()), true, encoding);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new InternalError(encoding + "encoding not found", localUnsupportedEncodingException);
    }
    in = new BufferedInputStream(server.getInputStream());
  }
  
  private Socket doConnect(InetSocketAddress paramInetSocketAddress, int paramInt)
    throws IOException
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
      } else {
        localSocket = new Socket(Proxy.NO_PROXY);
      }
    }
    else {
      localSocket = new Socket();
    }
    if (paramInt >= 0) {
      localSocket.connect(paramInetSocketAddress, paramInt);
    } else if (connectTimeout >= 0) {
      localSocket.connect(paramInetSocketAddress, connectTimeout);
    } else if (defaultConnectTimeout > 0) {
      localSocket.connect(paramInetSocketAddress, defaultConnectTimeout);
    } else {
      localSocket.connect(paramInetSocketAddress);
    }
    if (readTimeout >= 0) {
      localSocket.setSoTimeout(readTimeout);
    } else if (defaultSoTimeout > 0) {
      localSocket.setSoTimeout(defaultSoTimeout);
    }
    return localSocket;
  }
  
  private void disconnect()
    throws IOException
  {
    if (isConnected()) {
      server.close();
    }
    server = null;
    in = null;
    out = null;
    lastTransSize = -1L;
    lastFileName = null;
    restartOffset = 0L;
    welcomeMsg = null;
    lastReplyCode = null;
    serverResponse.setSize(0);
  }
  
  public boolean isConnected()
  {
    return server != null;
  }
  
  public SocketAddress getServerAddress()
  {
    return server == null ? null : server.getRemoteSocketAddress();
  }
  
  public sun.net.ftp.FtpClient connect(SocketAddress paramSocketAddress)
    throws FtpProtocolException, IOException
  {
    return connect(paramSocketAddress, -1);
  }
  
  public sun.net.ftp.FtpClient connect(SocketAddress paramSocketAddress, int paramInt)
    throws FtpProtocolException, IOException
  {
    if (!(paramSocketAddress instanceof InetSocketAddress)) {
      throw new IllegalArgumentException("Wrong address type");
    }
    serverAddr = ((InetSocketAddress)paramSocketAddress);
    tryConnect(serverAddr, paramInt);
    if (!readReply()) {
      throw new FtpProtocolException("Welcome message: " + getResponseString(), lastReplyCode);
    }
    welcomeMsg = getResponseString().substring(4);
    return this;
  }
  
  private void tryLogin(String paramString, char[] paramArrayOfChar)
    throws FtpProtocolException, IOException
  {
    issueCommandCheck("USER " + paramString);
    if ((lastReplyCode == FtpReplyCode.NEED_PASSWORD) && (paramArrayOfChar != null) && (paramArrayOfChar.length > 0)) {
      issueCommandCheck("PASS " + String.valueOf(paramArrayOfChar));
    }
  }
  
  public sun.net.ftp.FtpClient login(String paramString, char[] paramArrayOfChar)
    throws FtpProtocolException, IOException
  {
    if (!isConnected()) {
      throw new FtpProtocolException("Not connected yet", FtpReplyCode.BAD_SEQUENCE);
    }
    if ((paramString == null) || (paramString.length() == 0)) {
      throw new IllegalArgumentException("User name can't be null or empty");
    }
    tryLogin(paramString, paramArrayOfChar);
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < serverResponse.size(); i++)
    {
      String str = (String)serverResponse.elementAt(i);
      if (str != null)
      {
        if ((str.length() >= 4) && (str.startsWith("230"))) {
          str = str.substring(4);
        }
        localStringBuffer.append(str);
      }
    }
    welcomeMsg = localStringBuffer.toString();
    loggedIn = true;
    return this;
  }
  
  public sun.net.ftp.FtpClient login(String paramString1, char[] paramArrayOfChar, String paramString2)
    throws FtpProtocolException, IOException
  {
    if (!isConnected()) {
      throw new FtpProtocolException("Not connected yet", FtpReplyCode.BAD_SEQUENCE);
    }
    if ((paramString1 == null) || (paramString1.length() == 0)) {
      throw new IllegalArgumentException("User name can't be null or empty");
    }
    tryLogin(paramString1, paramArrayOfChar);
    if (lastReplyCode == FtpReplyCode.NEED_ACCOUNT) {
      issueCommandCheck("ACCT " + paramString2);
    }
    StringBuffer localStringBuffer = new StringBuffer();
    if (serverResponse != null)
    {
      Iterator localIterator = serverResponse.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if (str != null)
        {
          if ((str.length() >= 4) && (str.startsWith("230"))) {
            str = str.substring(4);
          }
          localStringBuffer.append(str);
        }
      }
    }
    welcomeMsg = localStringBuffer.toString();
    loggedIn = true;
    return this;
  }
  
  public void close()
    throws IOException
  {
    if (isConnected())
    {
      try
      {
        issueCommand("QUIT");
      }
      catch (FtpProtocolException localFtpProtocolException) {}
      loggedIn = false;
    }
    disconnect();
  }
  
  public boolean isLoggedIn()
  {
    return loggedIn;
  }
  
  public sun.net.ftp.FtpClient changeDirectory(String paramString)
    throws FtpProtocolException, IOException
  {
    if ((paramString == null) || ("".equals(paramString))) {
      throw new IllegalArgumentException("directory can't be null or empty");
    }
    issueCommandCheck("CWD " + paramString);
    return this;
  }
  
  public sun.net.ftp.FtpClient changeToParentDirectory()
    throws FtpProtocolException, IOException
  {
    issueCommandCheck("CDUP");
    return this;
  }
  
  public String getWorkingDirectory()
    throws FtpProtocolException, IOException
  {
    issueCommandCheck("PWD");
    String str = getResponseString();
    if (!str.startsWith("257")) {
      return null;
    }
    return str.substring(5, str.lastIndexOf('"'));
  }
  
  public sun.net.ftp.FtpClient setRestartOffset(long paramLong)
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("offset can't be negative");
    }
    restartOffset = paramLong;
    return this;
  }
  
  public sun.net.ftp.FtpClient getFile(String paramString, OutputStream paramOutputStream)
    throws FtpProtocolException, IOException
  {
    int i = 1500;
    Socket localSocket;
    InputStream localInputStream;
    byte[] arrayOfByte;
    int j;
    if (restartOffset > 0L)
    {
      try
      {
        localSocket = openDataConnection("REST " + restartOffset);
      }
      finally
      {
        restartOffset = 0L;
      }
      issueCommandCheck("RETR " + paramString);
      getTransferSize();
      localInputStream = createInputStream(localSocket.getInputStream());
      arrayOfByte = new byte[i * 10];
      while ((j = localInputStream.read(arrayOfByte)) >= 0) {
        if (j > 0) {
          paramOutputStream.write(arrayOfByte, 0, j);
        }
      }
      localInputStream.close();
    }
    else
    {
      localSocket = openDataConnection("RETR " + paramString);
      getTransferSize();
      localInputStream = createInputStream(localSocket.getInputStream());
      arrayOfByte = new byte[i * 10];
      while ((j = localInputStream.read(arrayOfByte)) >= 0) {
        if (j > 0) {
          paramOutputStream.write(arrayOfByte, 0, j);
        }
      }
      localInputStream.close();
    }
    return completePending();
  }
  
  /* Error */
  public InputStream getFileStream(String paramString)
    throws FtpProtocolException, IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 857	sun/net/ftp/impl/FtpClient:restartOffset	J
    //   4: lconst_0
    //   5: lcmp
    //   6: ifle +88 -> 94
    //   9: aload_0
    //   10: new 517	java/lang/StringBuilder
    //   13: dup
    //   14: invokespecial 927	java/lang/StringBuilder:<init>	()V
    //   17: ldc 35
    //   19: invokevirtual 932	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   22: aload_0
    //   23: getfield 857	sun/net/ftp/impl/FtpClient:restartOffset	J
    //   26: invokevirtual 930	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   29: invokevirtual 928	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   32: invokespecial 1018	sun/net/ftp/impl/FtpClient:openDataConnection	(Ljava/lang/String;)Ljava/net/Socket;
    //   35: astore_2
    //   36: aload_0
    //   37: lconst_0
    //   38: putfield 857	sun/net/ftp/impl/FtpClient:restartOffset	J
    //   41: goto +11 -> 52
    //   44: astore_3
    //   45: aload_0
    //   46: lconst_0
    //   47: putfield 857	sun/net/ftp/impl/FtpClient:restartOffset	J
    //   50: aload_3
    //   51: athrow
    //   52: aload_2
    //   53: ifnonnull +5 -> 58
    //   56: aconst_null
    //   57: areturn
    //   58: aload_0
    //   59: new 517	java/lang/StringBuilder
    //   62: dup
    //   63: invokespecial 927	java/lang/StringBuilder:<init>	()V
    //   66: ldc 36
    //   68: invokevirtual 932	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   71: aload_1
    //   72: invokevirtual 932	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   75: invokevirtual 928	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   78: invokespecial 1007	sun/net/ftp/impl/FtpClient:issueCommandCheck	(Ljava/lang/String;)V
    //   81: aload_0
    //   82: invokespecial 1002	sun/net/ftp/impl/FtpClient:getTransferSize	()V
    //   85: aload_0
    //   86: aload_2
    //   87: invokevirtual 951	java/net/Socket:getInputStream	()Ljava/io/InputStream;
    //   90: invokespecial 1016	sun/net/ftp/impl/FtpClient:createInputStream	(Ljava/io/InputStream;)Ljava/io/InputStream;
    //   93: areturn
    //   94: aload_0
    //   95: new 517	java/lang/StringBuilder
    //   98: dup
    //   99: invokespecial 927	java/lang/StringBuilder:<init>	()V
    //   102: ldc 36
    //   104: invokevirtual 932	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   107: aload_1
    //   108: invokevirtual 932	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   111: invokevirtual 928	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   114: invokespecial 1018	sun/net/ftp/impl/FtpClient:openDataConnection	(Ljava/lang/String;)Ljava/net/Socket;
    //   117: astore_2
    //   118: aload_2
    //   119: ifnonnull +5 -> 124
    //   122: aconst_null
    //   123: areturn
    //   124: aload_0
    //   125: invokespecial 1002	sun/net/ftp/impl/FtpClient:getTransferSize	()V
    //   128: aload_0
    //   129: aload_2
    //   130: invokevirtual 951	java/net/Socket:getInputStream	()Ljava/io/InputStream;
    //   133: invokespecial 1016	sun/net/ftp/impl/FtpClient:createInputStream	(Ljava/io/InputStream;)Ljava/io/InputStream;
    //   136: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	137	0	this	FtpClient
    //   0	137	1	paramString	String
    //   35	95	2	localSocket	Socket
    //   44	7	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	36	44	finally
  }
  
  public OutputStream putFileStream(String paramString, boolean paramBoolean)
    throws FtpProtocolException, IOException
  {
    String str = paramBoolean ? "STOU " : "STOR ";
    Socket localSocket = openDataConnection(str + paramString);
    if (localSocket == null) {
      return null;
    }
    boolean bool = type == FtpClient.TransferType.BINARY;
    return new TelnetOutputStream(localSocket.getOutputStream(), bool);
  }
  
  public sun.net.ftp.FtpClient putFile(String paramString, InputStream paramInputStream, boolean paramBoolean)
    throws FtpProtocolException, IOException
  {
    String str = paramBoolean ? "STOU " : "STOR ";
    int i = 1500;
    if (type == FtpClient.TransferType.BINARY)
    {
      Socket localSocket = openDataConnection(str + paramString);
      OutputStream localOutputStream = createOutputStream(localSocket.getOutputStream());
      byte[] arrayOfByte = new byte[i * 10];
      int j;
      while ((j = paramInputStream.read(arrayOfByte)) >= 0) {
        if (j > 0) {
          localOutputStream.write(arrayOfByte, 0, j);
        }
      }
      localOutputStream.close();
    }
    return completePending();
  }
  
  public sun.net.ftp.FtpClient appendFile(String paramString, InputStream paramInputStream)
    throws FtpProtocolException, IOException
  {
    int i = 1500;
    Socket localSocket = openDataConnection("APPE " + paramString);
    OutputStream localOutputStream = createOutputStream(localSocket.getOutputStream());
    byte[] arrayOfByte = new byte[i * 10];
    int j;
    while ((j = paramInputStream.read(arrayOfByte)) >= 0) {
      if (j > 0) {
        localOutputStream.write(arrayOfByte, 0, j);
      }
    }
    localOutputStream.close();
    return completePending();
  }
  
  public sun.net.ftp.FtpClient rename(String paramString1, String paramString2)
    throws FtpProtocolException, IOException
  {
    issueCommandCheck("RNFR " + paramString1);
    issueCommandCheck("RNTO " + paramString2);
    return this;
  }
  
  public sun.net.ftp.FtpClient deleteFile(String paramString)
    throws FtpProtocolException, IOException
  {
    issueCommandCheck("DELE " + paramString);
    return this;
  }
  
  public sun.net.ftp.FtpClient makeDirectory(String paramString)
    throws FtpProtocolException, IOException
  {
    issueCommandCheck("MKD " + paramString);
    return this;
  }
  
  public sun.net.ftp.FtpClient removeDirectory(String paramString)
    throws FtpProtocolException, IOException
  {
    issueCommandCheck("RMD " + paramString);
    return this;
  }
  
  public sun.net.ftp.FtpClient noop()
    throws FtpProtocolException, IOException
  {
    issueCommandCheck("NOOP");
    return this;
  }
  
  public String getStatus(String paramString)
    throws FtpProtocolException, IOException
  {
    issueCommandCheck("STAT " + paramString);
    Vector localVector = getResponseStrings();
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 1; i < localVector.size() - 1; i++) {
      localStringBuffer.append((String)localVector.get(i));
    }
    return localStringBuffer.toString();
  }
  
  public List<String> getFeatures()
    throws FtpProtocolException, IOException
  {
    ArrayList localArrayList = new ArrayList();
    issueCommandCheck("FEAT");
    Vector localVector = getResponseStrings();
    for (int i = 1; i < localVector.size() - 1; i++)
    {
      String str = (String)localVector.get(i);
      localArrayList.add(str.substring(1, str.length() - 1));
    }
    return localArrayList;
  }
  
  public sun.net.ftp.FtpClient abort()
    throws FtpProtocolException, IOException
  {
    issueCommandCheck("ABOR");
    return this;
  }
  
  public sun.net.ftp.FtpClient completePending()
    throws FtpProtocolException, IOException
  {
    while (replyPending)
    {
      replyPending = false;
      if (!readReply()) {
        throw new FtpProtocolException(getLastResponseString(), lastReplyCode);
      }
    }
    return this;
  }
  
  public sun.net.ftp.FtpClient reInit()
    throws FtpProtocolException, IOException
  {
    issueCommandCheck("REIN");
    loggedIn = false;
    if ((useCrypto) && ((server instanceof SSLSocket)))
    {
      SSLSession localSSLSession = ((SSLSocket)server).getSession();
      localSSLSession.invalidate();
      server = oldSocket;
      oldSocket = null;
      try
      {
        out = new PrintStream(new BufferedOutputStream(server.getOutputStream()), true, encoding);
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        throw new InternalError(encoding + "encoding not found", localUnsupportedEncodingException);
      }
      in = new BufferedInputStream(server.getInputStream());
    }
    useCrypto = false;
    return this;
  }
  
  public sun.net.ftp.FtpClient setType(FtpClient.TransferType paramTransferType)
    throws FtpProtocolException, IOException
  {
    String str = "NOOP";
    type = paramTransferType;
    if (paramTransferType == FtpClient.TransferType.ASCII) {
      str = "TYPE A";
    }
    if (paramTransferType == FtpClient.TransferType.BINARY) {
      str = "TYPE I";
    }
    if (paramTransferType == FtpClient.TransferType.EBCDIC) {
      str = "TYPE E";
    }
    issueCommandCheck(str);
    return this;
  }
  
  public InputStream list(String paramString)
    throws FtpProtocolException, IOException
  {
    Socket localSocket = openDataConnection("LIST " + paramString);
    if (localSocket != null) {
      return createInputStream(localSocket.getInputStream());
    }
    return null;
  }
  
  public InputStream nameList(String paramString)
    throws FtpProtocolException, IOException
  {
    Socket localSocket = openDataConnection("NLST " + paramString);
    if (localSocket != null) {
      return createInputStream(localSocket.getInputStream());
    }
    return null;
  }
  
  public long getSize(String paramString)
    throws FtpProtocolException, IOException
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      throw new IllegalArgumentException("path can't be null or empty");
    }
    issueCommandCheck("SIZE " + paramString);
    if (lastReplyCode == FtpReplyCode.FILE_STATUS)
    {
      String str = getResponseString();
      str = str.substring(4, str.length() - 1);
      return Long.parseLong(str);
    }
    return -1L;
  }
  
  public Date getLastModified(String paramString)
    throws FtpProtocolException, IOException
  {
    issueCommandCheck("MDTM " + paramString);
    if (lastReplyCode == FtpReplyCode.FILE_STATUS)
    {
      String str = getResponseString().substring(4);
      Date localDate = null;
      for (SimpleDateFormat localSimpleDateFormat : dateFormats)
      {
        try
        {
          localDate = localSimpleDateFormat.parse(str);
        }
        catch (ParseException localParseException) {}
        if (localDate != null) {
          return localDate;
        }
      }
    }
    return null;
  }
  
  public sun.net.ftp.FtpClient setDirParser(FtpDirParser paramFtpDirParser)
  {
    parser = paramFtpDirParser;
    return this;
  }
  
  public Iterator<FtpDirEntry> listFiles(String paramString)
    throws FtpProtocolException, IOException
  {
    Socket localSocket = null;
    BufferedReader localBufferedReader = null;
    try
    {
      localSocket = openDataConnection("MLSD " + paramString);
    }
    catch (FtpProtocolException localFtpProtocolException) {}
    if (localSocket != null)
    {
      localBufferedReader = new BufferedReader(new InputStreamReader(localSocket.getInputStream()));
      return new FtpFileIterator(mlsxParser, localBufferedReader);
    }
    localSocket = openDataConnection("LIST " + paramString);
    if (localSocket != null)
    {
      localBufferedReader = new BufferedReader(new InputStreamReader(localSocket.getInputStream()));
      return new FtpFileIterator(parser, localBufferedReader);
    }
    return null;
  }
  
  private boolean sendSecurityData(byte[] paramArrayOfByte)
    throws IOException, FtpProtocolException
  {
    BASE64Encoder localBASE64Encoder = new BASE64Encoder();
    String str = localBASE64Encoder.encode(paramArrayOfByte);
    return issueCommand("ADAT " + str);
  }
  
  private byte[] getSecurityData()
  {
    String str = getLastResponseString();
    if (str.substring(4, 9).equalsIgnoreCase("ADAT="))
    {
      BASE64Decoder localBASE64Decoder = new BASE64Decoder();
      try
      {
        return localBASE64Decoder.decodeBuffer(str.substring(9, str.length() - 1));
      }
      catch (IOException localIOException) {}
    }
    return null;
  }
  
  public sun.net.ftp.FtpClient useKerberos()
    throws FtpProtocolException, IOException
  {
    return this;
  }
  
  public String getWelcomeMsg()
  {
    return welcomeMsg;
  }
  
  public FtpReplyCode getLastReplyCode()
  {
    return lastReplyCode;
  }
  
  public String getLastResponseString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if (serverResponse != null)
    {
      Iterator localIterator = serverResponse.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if (str != null) {
          localStringBuffer.append(str);
        }
      }
    }
    return localStringBuffer.toString();
  }
  
  public long getLastTransferSize()
  {
    return lastTransSize;
  }
  
  public String getLastFileName()
  {
    return lastFileName;
  }
  
  public sun.net.ftp.FtpClient startSecureSession()
    throws FtpProtocolException, IOException
  {
    if (!isConnected()) {
      throw new FtpProtocolException("Not connected yet", FtpReplyCode.BAD_SEQUENCE);
    }
    if (sslFact == null) {
      try
      {
        sslFact = ((SSLSocketFactory)SSLSocketFactory.getDefault());
      }
      catch (Exception localException1)
      {
        throw new IOException(localException1.getLocalizedMessage());
      }
    }
    issueCommandCheck("AUTH TLS");
    Socket localSocket = null;
    try
    {
      localSocket = sslFact.createSocket(server, serverAddr.getHostName(), serverAddr.getPort(), true);
    }
    catch (SSLException localSSLException)
    {
      try
      {
        disconnect();
      }
      catch (Exception localException2) {}
      throw localSSLException;
    }
    oldSocket = server;
    server = localSocket;
    try
    {
      out = new PrintStream(new BufferedOutputStream(server.getOutputStream()), true, encoding);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new InternalError(encoding + "encoding not found", localUnsupportedEncodingException);
    }
    in = new BufferedInputStream(server.getInputStream());
    issueCommandCheck("PBSZ 0");
    issueCommandCheck("PROT P");
    useCrypto = true;
    return this;
  }
  
  public sun.net.ftp.FtpClient endSecureSession()
    throws FtpProtocolException, IOException
  {
    if (!useCrypto) {
      return this;
    }
    issueCommandCheck("CCC");
    issueCommandCheck("PROT C");
    useCrypto = false;
    server = oldSocket;
    oldSocket = null;
    try
    {
      out = new PrintStream(new BufferedOutputStream(server.getOutputStream()), true, encoding);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new InternalError(encoding + "encoding not found", localUnsupportedEncodingException);
    }
    in = new BufferedInputStream(server.getInputStream());
    return this;
  }
  
  public sun.net.ftp.FtpClient allocate(long paramLong)
    throws FtpProtocolException, IOException
  {
    issueCommandCheck("ALLO " + paramLong);
    return this;
  }
  
  public sun.net.ftp.FtpClient structureMount(String paramString)
    throws FtpProtocolException, IOException
  {
    issueCommandCheck("SMNT " + paramString);
    return this;
  }
  
  public String getSystem()
    throws FtpProtocolException, IOException
  {
    issueCommandCheck("SYST");
    String str = getResponseString();
    return str.substring(4);
  }
  
  public String getHelp(String paramString)
    throws FtpProtocolException, IOException
  {
    issueCommandCheck("HELP " + paramString);
    Vector localVector = getResponseStrings();
    if (localVector.size() == 1) {
      return ((String)localVector.get(0)).substring(4);
    }
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 1; i < localVector.size() - 1; i++) {
      localStringBuffer.append(((String)localVector.get(i)).substring(3));
    }
    return localStringBuffer.toString();
  }
  
  public sun.net.ftp.FtpClient siteCmd(String paramString)
    throws FtpProtocolException, IOException
  {
    issueCommandCheck("SITE " + paramString);
    return this;
  }
  
  static
  {
    int[] arrayOfInt = { 0, 0 };
    final String[] arrayOfString = { null };
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        val$vals[0] = Integer.getInteger("sun.net.client.defaultReadTimeout", 300000).intValue();
        val$vals[1] = Integer.getInteger("sun.net.client.defaultConnectTimeout", 300000).intValue();
        arrayOfString[0] = System.getProperty("file.encoding", "ISO8859_1");
        return null;
      }
    });
    if (arrayOfInt[0] == 0) {
      defaultSoTimeout = -1;
    } else {
      defaultSoTimeout = arrayOfInt[0];
    }
    if (arrayOfInt[1] == 0) {
      defaultConnectTimeout = -1;
    } else {
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
    patterns = new Pattern[patStrings.length];
    for (int j = 0; j < patStrings.length; j++) {
      patterns[j] = Pattern.compile(patStrings[j]);
    }
    transPat = null;
    epsvPat = null;
    pasvPat = null;
    MDTMformats = new String[] { "yyyyMMddHHmmss.SSS", "yyyyMMddHHmmss" };
    dateFormats = new SimpleDateFormat[MDTMformats.length];
    for (int i = 0; i < MDTMformats.length; i++)
    {
      dateFormats[i] = new SimpleDateFormat(MDTMformats[i]);
      dateFormats[i].setTimeZone(TimeZone.getTimeZone("GMT"));
    }
  }
  
  private class DefaultParser
    implements FtpDirParser
  {
    private DefaultParser() {}
    
    public FtpDirEntry parseLine(String paramString)
    {
      String str1 = null;
      String str2 = null;
      String str3 = null;
      String str4 = null;
      String str5 = null;
      String str6 = null;
      String str7 = null;
      boolean bool = false;
      Calendar localCalendar = Calendar.getInstance();
      int i = localCalendar.get(1);
      Matcher localMatcher1 = null;
      for (int j = 0; j < FtpClient.patterns.length; j++)
      {
        localMatcher1 = FtpClient.patterns[j].matcher(paramString);
        if (localMatcher1.find())
        {
          str4 = localMatcher1.group(FtpClient.patternGroups[j][0]);
          str2 = localMatcher1.group(FtpClient.patternGroups[j][1]);
          str1 = localMatcher1.group(FtpClient.patternGroups[j][2]);
          if (FtpClient.patternGroups[j][4] > 0) {
            str1 = str1 + ", " + localMatcher1.group(FtpClient.patternGroups[j][4]);
          } else if (FtpClient.patternGroups[j][3] > 0) {
            str1 = str1 + ", " + String.valueOf(i);
          }
          if (FtpClient.patternGroups[j][3] > 0) {
            str3 = localMatcher1.group(FtpClient.patternGroups[j][3]);
          }
          if (FtpClient.patternGroups[j][5] > 0)
          {
            str5 = localMatcher1.group(FtpClient.patternGroups[j][5]);
            bool = str5.startsWith("d");
          }
          if (FtpClient.patternGroups[j][6] > 0) {
            str6 = localMatcher1.group(FtpClient.patternGroups[j][6]);
          }
          if (FtpClient.patternGroups[j][7] > 0) {
            str7 = localMatcher1.group(FtpClient.patternGroups[j][7]);
          }
          if ("<DIR>".equals(str2))
          {
            bool = true;
            str2 = null;
          }
        }
      }
      if (str4 != null)
      {
        Date localDate;
        try
        {
          localDate = df.parse(str1);
        }
        catch (Exception localException)
        {
          localDate = null;
        }
        if ((localDate != null) && (str3 != null))
        {
          int k = str3.indexOf(":");
          localCalendar.setTime(localDate);
          localCalendar.set(10, Integer.parseInt(str3.substring(0, k)));
          localCalendar.set(12, Integer.parseInt(str3.substring(k + 1)));
          localDate = localCalendar.getTime();
        }
        Matcher localMatcher2 = FtpClient.linkp.matcher(str4);
        if (localMatcher2.find()) {
          str4 = localMatcher2.group(1);
        }
        boolean[][] arrayOfBoolean = new boolean[3][3];
        for (int m = 0; m < 3; m++) {
          for (int n = 0; n < 3; n++) {
            arrayOfBoolean[m][n] = (str5.charAt(m * 3 + n) != '-' ? 1 : 0);
          }
        }
        FtpDirEntry localFtpDirEntry = new FtpDirEntry(str4);
        localFtpDirEntry.setUser(str6).setGroup(str7);
        localFtpDirEntry.setSize(Long.parseLong(str2)).setLastModified(localDate);
        localFtpDirEntry.setPermissions(arrayOfBoolean);
        localFtpDirEntry.setType(paramString.charAt(0) == 'l' ? FtpDirEntry.Type.LINK : bool ? FtpDirEntry.Type.DIR : FtpDirEntry.Type.FILE);
        return localFtpDirEntry;
      }
      return null;
    }
  }
  
  private class FtpFileIterator
    implements Iterator<FtpDirEntry>, Closeable
  {
    private BufferedReader in = null;
    private FtpDirEntry nextFile = null;
    private FtpDirParser fparser = null;
    private boolean eof = false;
    
    public FtpFileIterator(FtpDirParser paramFtpDirParser, BufferedReader paramBufferedReader)
    {
      in = paramBufferedReader;
      fparser = paramFtpDirParser;
      readNext();
    }
    
    private void readNext()
    {
      nextFile = null;
      if (eof) {
        return;
      }
      String str = null;
      try
      {
        do
        {
          str = in.readLine();
          if (str != null)
          {
            nextFile = fparser.parseLine(str);
            if (nextFile != null) {
              return;
            }
          }
        } while (str != null);
        in.close();
      }
      catch (IOException localIOException) {}
      eof = true;
    }
    
    public boolean hasNext()
    {
      return nextFile != null;
    }
    
    public FtpDirEntry next()
    {
      FtpDirEntry localFtpDirEntry = nextFile;
      readNext();
      return localFtpDirEntry;
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void close()
      throws IOException
    {
      if ((in != null) && (!eof)) {
        in.close();
      }
      eof = true;
      nextFile = null;
    }
  }
  
  private class MLSxParser
    implements FtpDirParser
  {
    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
    
    private MLSxParser() {}
    
    public FtpDirEntry parseLine(String paramString)
    {
      String str1 = null;
      int i = paramString.lastIndexOf(";");
      if (i > 0)
      {
        str1 = paramString.substring(i + 1).trim();
        paramString = paramString.substring(0, i);
      }
      else
      {
        str1 = paramString.trim();
        paramString = "";
      }
      FtpDirEntry localFtpDirEntry = new FtpDirEntry(str1);
      Object localObject;
      while (!paramString.isEmpty())
      {
        i = paramString.indexOf(";");
        if (i > 0)
        {
          str2 = paramString.substring(0, i);
          paramString = paramString.substring(i + 1);
        }
        else
        {
          str2 = paramString;
          paramString = "";
        }
        i = str2.indexOf("=");
        if (i > 0)
        {
          localObject = str2.substring(0, i);
          String str3 = str2.substring(i + 1);
          localFtpDirEntry.addFact((String)localObject, str3);
        }
      }
      String str2 = localFtpDirEntry.getFact("Size");
      if (str2 != null) {
        localFtpDirEntry.setSize(Long.parseLong(str2));
      }
      str2 = localFtpDirEntry.getFact("Modify");
      if (str2 != null)
      {
        localObject = null;
        try
        {
          localObject = df.parse(str2);
        }
        catch (ParseException localParseException1) {}
        if (localObject != null) {
          localFtpDirEntry.setLastModified((Date)localObject);
        }
      }
      str2 = localFtpDirEntry.getFact("Create");
      if (str2 != null)
      {
        localObject = null;
        try
        {
          localObject = df.parse(str2);
        }
        catch (ParseException localParseException2) {}
        if (localObject != null) {
          localFtpDirEntry.setCreated((Date)localObject);
        }
      }
      str2 = localFtpDirEntry.getFact("Type");
      if (str2 != null)
      {
        if (str2.equalsIgnoreCase("file")) {
          localFtpDirEntry.setType(FtpDirEntry.Type.FILE);
        }
        if (str2.equalsIgnoreCase("dir")) {
          localFtpDirEntry.setType(FtpDirEntry.Type.DIR);
        }
        if (str2.equalsIgnoreCase("cdir")) {
          localFtpDirEntry.setType(FtpDirEntry.Type.CDIR);
        }
        if (str2.equalsIgnoreCase("pdir")) {
          localFtpDirEntry.setType(FtpDirEntry.Type.PDIR);
        }
      }
      return localFtpDirEntry;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\ftp\impl\FtpClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */