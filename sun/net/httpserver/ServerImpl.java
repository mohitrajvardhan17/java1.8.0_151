package sun.net.httpserver;

import com.sun.net.httpserver.Filter.Chain;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

class ServerImpl
  implements TimeSource
{
  private String protocol;
  private boolean https;
  private Executor executor;
  private HttpsConfigurator httpsConfig;
  private SSLContext sslContext;
  private ContextList contexts;
  private InetSocketAddress address;
  private ServerSocketChannel schan;
  private Selector selector;
  private SelectionKey listenerKey;
  private Set<HttpConnection> idleConnections;
  private Set<HttpConnection> allConnections;
  private Set<HttpConnection> reqConnections;
  private Set<HttpConnection> rspConnections;
  private List<Event> events;
  private Object lolock = new Object();
  private volatile boolean finished = false;
  private volatile boolean terminating = false;
  private boolean bound = false;
  private boolean started = false;
  private volatile long time;
  private volatile long subticks = 0L;
  private volatile long ticks;
  private HttpServer wrapper;
  static final int CLOCK_TICK = ServerConfig.getClockTick();
  static final long IDLE_INTERVAL = ServerConfig.getIdleInterval();
  static final int MAX_IDLE_CONNECTIONS = ServerConfig.getMaxIdleConnections();
  static final long TIMER_MILLIS = ServerConfig.getTimerMillis();
  static final long MAX_REQ_TIME = getTimeMillis(ServerConfig.getMaxReqTime());
  static final long MAX_RSP_TIME = getTimeMillis(ServerConfig.getMaxRspTime());
  static final boolean timer1Enabled = (MAX_REQ_TIME != -1L) || (MAX_RSP_TIME != -1L);
  private Timer timer;
  private Timer timer1;
  private Logger logger;
  Dispatcher dispatcher;
  static boolean debug = ServerConfig.debugEnabled();
  private int exchangeCount = 0;
  
  ServerImpl(HttpServer paramHttpServer, String paramString, InetSocketAddress paramInetSocketAddress, int paramInt)
    throws IOException
  {
    protocol = paramString;
    wrapper = paramHttpServer;
    logger = Logger.getLogger("com.sun.net.httpserver");
    ServerConfig.checkLegacyProperties(logger);
    https = paramString.equalsIgnoreCase("https");
    address = paramInetSocketAddress;
    contexts = new ContextList();
    schan = ServerSocketChannel.open();
    if (paramInetSocketAddress != null)
    {
      ServerSocket localServerSocket = schan.socket();
      localServerSocket.bind(paramInetSocketAddress, paramInt);
      bound = true;
    }
    selector = Selector.open();
    schan.configureBlocking(false);
    listenerKey = schan.register(selector, 16);
    dispatcher = new Dispatcher();
    idleConnections = Collections.synchronizedSet(new HashSet());
    allConnections = Collections.synchronizedSet(new HashSet());
    reqConnections = Collections.synchronizedSet(new HashSet());
    rspConnections = Collections.synchronizedSet(new HashSet());
    time = System.currentTimeMillis();
    timer = new Timer("server-timer", true);
    timer.schedule(new ServerTimerTask(), CLOCK_TICK, CLOCK_TICK);
    if (timer1Enabled)
    {
      timer1 = new Timer("server-timer1", true);
      timer1.schedule(new ServerTimerTask1(), TIMER_MILLIS, TIMER_MILLIS);
      logger.config("HttpServer timer1 enabled period in ms:  " + TIMER_MILLIS);
      logger.config("MAX_REQ_TIME:  " + MAX_REQ_TIME);
      logger.config("MAX_RSP_TIME:  " + MAX_RSP_TIME);
    }
    events = new LinkedList();
    logger.config("HttpServer created " + paramString + " " + paramInetSocketAddress);
  }
  
  public void bind(InetSocketAddress paramInetSocketAddress, int paramInt)
    throws IOException
  {
    if (bound) {
      throw new BindException("HttpServer already bound");
    }
    if (paramInetSocketAddress == null) {
      throw new NullPointerException("null address");
    }
    ServerSocket localServerSocket = schan.socket();
    localServerSocket.bind(paramInetSocketAddress, paramInt);
    bound = true;
  }
  
  public void start()
  {
    if ((!bound) || (started) || (finished)) {
      throw new IllegalStateException("server in wrong state");
    }
    if (executor == null) {
      executor = new DefaultExecutor(null);
    }
    Thread localThread = new Thread(dispatcher);
    started = true;
    localThread.start();
  }
  
  public void setExecutor(Executor paramExecutor)
  {
    if (started) {
      throw new IllegalStateException("server already started");
    }
    executor = paramExecutor;
  }
  
  public Executor getExecutor()
  {
    return executor;
  }
  
  public void setHttpsConfigurator(HttpsConfigurator paramHttpsConfigurator)
  {
    if (paramHttpsConfigurator == null) {
      throw new NullPointerException("null HttpsConfigurator");
    }
    if (started) {
      throw new IllegalStateException("server already started");
    }
    httpsConfig = paramHttpsConfigurator;
    sslContext = paramHttpsConfigurator.getSSLContext();
  }
  
  public HttpsConfigurator getHttpsConfigurator()
  {
    return httpsConfig;
  }
  
  public void stop(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("negative delay parameter");
    }
    terminating = true;
    try
    {
      schan.close();
    }
    catch (IOException localIOException) {}
    selector.wakeup();
    long l = System.currentTimeMillis() + paramInt * 1000;
    while (System.currentTimeMillis() < l)
    {
      delay();
      if (finished) {
        break;
      }
    }
    finished = true;
    selector.wakeup();
    synchronized (allConnections)
    {
      Iterator localIterator = allConnections.iterator();
      while (localIterator.hasNext())
      {
        HttpConnection localHttpConnection = (HttpConnection)localIterator.next();
        localHttpConnection.close();
      }
    }
    allConnections.clear();
    idleConnections.clear();
    timer.cancel();
    if (timer1Enabled) {
      timer1.cancel();
    }
  }
  
  public synchronized HttpContextImpl createContext(String paramString, HttpHandler paramHttpHandler)
  {
    if ((paramHttpHandler == null) || (paramString == null)) {
      throw new NullPointerException("null handler, or path parameter");
    }
    HttpContextImpl localHttpContextImpl = new HttpContextImpl(protocol, paramString, paramHttpHandler, this);
    contexts.add(localHttpContextImpl);
    logger.config("context created: " + paramString);
    return localHttpContextImpl;
  }
  
  public synchronized HttpContextImpl createContext(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("null path parameter");
    }
    HttpContextImpl localHttpContextImpl = new HttpContextImpl(protocol, paramString, null, this);
    contexts.add(localHttpContextImpl);
    logger.config("context created: " + paramString);
    return localHttpContextImpl;
  }
  
  public synchronized void removeContext(String paramString)
    throws IllegalArgumentException
  {
    if (paramString == null) {
      throw new NullPointerException("null path parameter");
    }
    contexts.remove(protocol, paramString);
    logger.config("context removed: " + paramString);
  }
  
  public synchronized void removeContext(HttpContext paramHttpContext)
    throws IllegalArgumentException
  {
    if (!(paramHttpContext instanceof HttpContextImpl)) {
      throw new IllegalArgumentException("wrong HttpContext type");
    }
    contexts.remove((HttpContextImpl)paramHttpContext);
    logger.config("context removed: " + paramHttpContext.getPath());
  }
  
  public InetSocketAddress getAddress()
  {
    (InetSocketAddress)AccessController.doPrivileged(new PrivilegedAction()
    {
      public InetSocketAddress run()
      {
        return (InetSocketAddress)schan.socket().getLocalSocketAddress();
      }
    });
  }
  
  Selector getSelector()
  {
    return selector;
  }
  
  void addEvent(Event paramEvent)
  {
    synchronized (lolock)
    {
      events.add(paramEvent);
      selector.wakeup();
    }
  }
  
  static synchronized void dprint(String paramString)
  {
    if (debug) {
      System.out.println(paramString);
    }
  }
  
  static synchronized void dprint(Exception paramException)
  {
    if (debug)
    {
      System.out.println(paramException);
      paramException.printStackTrace();
    }
  }
  
  Logger getLogger()
  {
    return logger;
  }
  
  private void closeConnection(HttpConnection paramHttpConnection)
  {
    paramHttpConnection.close();
    allConnections.remove(paramHttpConnection);
    switch (paramHttpConnection.getState())
    {
    case REQUEST: 
      reqConnections.remove(paramHttpConnection);
      break;
    case RESPONSE: 
      rspConnections.remove(paramHttpConnection);
      break;
    case IDLE: 
      idleConnections.remove(paramHttpConnection);
    }
    assert (!reqConnections.remove(paramHttpConnection));
    assert (!rspConnections.remove(paramHttpConnection));
    assert (!idleConnections.remove(paramHttpConnection));
  }
  
  void logReply(int paramInt, String paramString1, String paramString2)
  {
    if (!logger.isLoggable(Level.FINE)) {
      return;
    }
    if (paramString2 == null) {
      paramString2 = "";
    }
    String str1;
    if (paramString1.length() > 80) {
      str1 = paramString1.substring(0, 80) + "<TRUNCATED>";
    } else {
      str1 = paramString1;
    }
    String str2 = str1 + " [" + paramInt + " " + Code.msg(paramInt) + "] (" + paramString2 + ")";
    logger.fine(str2);
  }
  
  long getTicks()
  {
    return ticks;
  }
  
  public long getTime()
  {
    return time;
  }
  
  void delay()
  {
    
    try
    {
      Thread.sleep(200L);
    }
    catch (InterruptedException localInterruptedException) {}
  }
  
  synchronized void startExchange()
  {
    exchangeCount += 1;
  }
  
  synchronized int endExchange()
  {
    exchangeCount -= 1;
    assert (exchangeCount >= 0);
    return exchangeCount;
  }
  
  HttpServer getWrapper()
  {
    return wrapper;
  }
  
  void requestStarted(HttpConnection paramHttpConnection)
  {
    creationTime = getTime();
    paramHttpConnection.setState(HttpConnection.State.REQUEST);
    reqConnections.add(paramHttpConnection);
  }
  
  void requestCompleted(HttpConnection paramHttpConnection)
  {
    assert (paramHttpConnection.getState() == HttpConnection.State.REQUEST);
    reqConnections.remove(paramHttpConnection);
    rspStartedTime = getTime();
    rspConnections.add(paramHttpConnection);
    paramHttpConnection.setState(HttpConnection.State.RESPONSE);
  }
  
  void responseCompleted(HttpConnection paramHttpConnection)
  {
    assert (paramHttpConnection.getState() == HttpConnection.State.RESPONSE);
    rspConnections.remove(paramHttpConnection);
    paramHttpConnection.setState(HttpConnection.State.IDLE);
  }
  
  void logStackTrace(String paramString)
  {
    logger.finest(paramString);
    StringBuilder localStringBuilder = new StringBuilder();
    StackTraceElement[] arrayOfStackTraceElement = Thread.currentThread().getStackTrace();
    for (int i = 0; i < arrayOfStackTraceElement.length; i++) {
      localStringBuilder.append(arrayOfStackTraceElement[i].toString()).append("\n");
    }
    logger.finest(localStringBuilder.toString());
  }
  
  static long getTimeMillis(long paramLong)
  {
    if (paramLong == -1L) {
      return -1L;
    }
    return paramLong * 1000L;
  }
  
  private static class DefaultExecutor
    implements Executor
  {
    private DefaultExecutor() {}
    
    public void execute(Runnable paramRunnable)
    {
      paramRunnable.run();
    }
  }
  
  class Dispatcher
    implements Runnable
  {
    final LinkedList<HttpConnection> connsToRegister = new LinkedList();
    
    Dispatcher() {}
    
    private void handleEvent(Event paramEvent)
    {
      ExchangeImpl localExchangeImpl = exchange;
      HttpConnection localHttpConnection = localExchangeImpl.getConnection();
      try
      {
        if ((paramEvent instanceof WriteFinishedEvent))
        {
          int i = endExchange();
          if ((terminating) && (i == 0)) {
            finished = true;
          }
          responseCompleted(localHttpConnection);
          LeftOverInputStream localLeftOverInputStream = localExchangeImpl.getOriginalInputStream();
          if (!localLeftOverInputStream.isEOF()) {
            close = true;
          }
          if ((close) || (idleConnections.size() >= ServerImpl.MAX_IDLE_CONNECTIONS))
          {
            localHttpConnection.close();
            allConnections.remove(localHttpConnection);
          }
          else if (localLeftOverInputStream.isDataBuffered())
          {
            requestStarted(localHttpConnection);
            handle(localHttpConnection.getChannel(), localHttpConnection);
          }
          else
          {
            connsToRegister.add(localHttpConnection);
          }
        }
      }
      catch (IOException localIOException)
      {
        logger.log(Level.FINER, "Dispatcher (1)", localIOException);
        localHttpConnection.close();
      }
    }
    
    void reRegister(HttpConnection paramHttpConnection)
    {
      try
      {
        SocketChannel localSocketChannel = paramHttpConnection.getChannel();
        localSocketChannel.configureBlocking(false);
        SelectionKey localSelectionKey = localSocketChannel.register(selector, 1);
        localSelectionKey.attach(paramHttpConnection);
        selectionKey = localSelectionKey;
        time = (getTime() + ServerImpl.IDLE_INTERVAL);
        idleConnections.add(paramHttpConnection);
      }
      catch (IOException localIOException)
      {
        ServerImpl.dprint(localIOException);
        logger.log(Level.FINER, "Dispatcher(8)", localIOException);
        paramHttpConnection.close();
      }
    }
    
    public void run()
    {
      while (!finished) {
        try
        {
          List localList = null;
          synchronized (lolock)
          {
            if (events.size() > 0)
            {
              localList = events;
              events = new LinkedList();
            }
          }
          if (localList != null)
          {
            ??? = localList.iterator();
            while (((Iterator)???).hasNext())
            {
              localObject2 = (Event)((Iterator)???).next();
              handleEvent((Event)localObject2);
            }
          }
          ??? = connsToRegister.iterator();
          while (((Iterator)???).hasNext())
          {
            localObject2 = (HttpConnection)((Iterator)???).next();
            reRegister((HttpConnection)localObject2);
          }
          connsToRegister.clear();
          selector.select(1000L);
          ??? = selector.selectedKeys();
          Object localObject2 = ((Set)???).iterator();
          while (((Iterator)localObject2).hasNext())
          {
            SelectionKey localSelectionKey = (SelectionKey)((Iterator)localObject2).next();
            ((Iterator)localObject2).remove();
            Object localObject3;
            HttpConnection localHttpConnection;
            if (localSelectionKey.equals(listenerKey))
            {
              if (!terminating)
              {
                SocketChannel localSocketChannel = schan.accept();
                if (ServerConfig.noDelay()) {
                  localSocketChannel.socket().setTcpNoDelay(true);
                }
                if (localSocketChannel != null)
                {
                  localSocketChannel.configureBlocking(false);
                  localObject3 = localSocketChannel.register(selector, 1);
                  localHttpConnection = new HttpConnection();
                  selectionKey = ((SelectionKey)localObject3);
                  localHttpConnection.setChannel(localSocketChannel);
                  ((SelectionKey)localObject3).attach(localHttpConnection);
                  requestStarted(localHttpConnection);
                  allConnections.add(localHttpConnection);
                }
              }
            }
            else {
              try
              {
                if (localSelectionKey.isReadable())
                {
                  localObject3 = (SocketChannel)localSelectionKey.channel();
                  localHttpConnection = (HttpConnection)localSelectionKey.attachment();
                  localSelectionKey.cancel();
                  ((SocketChannel)localObject3).configureBlocking(true);
                  if (idleConnections.remove(localHttpConnection)) {
                    requestStarted(localHttpConnection);
                  }
                  handle((SocketChannel)localObject3, localHttpConnection);
                }
                else if (!$assertionsDisabled)
                {
                  throw new AssertionError();
                }
              }
              catch (CancelledKeyException localCancelledKeyException)
              {
                handleException(localSelectionKey, null);
              }
              catch (IOException localIOException2)
              {
                handleException(localSelectionKey, localIOException2);
              }
            }
          }
          selector.selectNow();
        }
        catch (IOException localIOException1)
        {
          logger.log(Level.FINER, "Dispatcher (4)", localIOException1);
        }
        catch (Exception localException1)
        {
          logger.log(Level.FINER, "Dispatcher (7)", localException1);
        }
      }
      try
      {
        selector.close();
      }
      catch (Exception localException2) {}
    }
    
    private void handleException(SelectionKey paramSelectionKey, Exception paramException)
    {
      HttpConnection localHttpConnection = (HttpConnection)paramSelectionKey.attachment();
      if (paramException != null) {
        logger.log(Level.FINER, "Dispatcher (2)", paramException);
      }
      ServerImpl.this.closeConnection(localHttpConnection);
    }
    
    public void handle(SocketChannel paramSocketChannel, HttpConnection paramHttpConnection)
      throws IOException
    {
      try
      {
        ServerImpl.Exchange localExchange = new ServerImpl.Exchange(ServerImpl.this, paramSocketChannel, protocol, paramHttpConnection);
        executor.execute(localExchange);
      }
      catch (HttpError localHttpError)
      {
        logger.log(Level.FINER, "Dispatcher (4)", localHttpError);
        ServerImpl.this.closeConnection(paramHttpConnection);
      }
      catch (IOException localIOException)
      {
        logger.log(Level.FINER, "Dispatcher (5)", localIOException);
        ServerImpl.this.closeConnection(paramHttpConnection);
      }
    }
  }
  
  class Exchange
    implements Runnable
  {
    SocketChannel chan;
    HttpConnection connection;
    HttpContextImpl context;
    InputStream rawin;
    OutputStream rawout;
    String protocol;
    ExchangeImpl tx;
    HttpContextImpl ctx;
    boolean rejected = false;
    
    Exchange(SocketChannel paramSocketChannel, String paramString, HttpConnection paramHttpConnection)
      throws IOException
    {
      chan = paramSocketChannel;
      connection = paramHttpConnection;
      protocol = paramString;
    }
    
    public void run()
    {
      context = connection.getHttpContext();
      SSLEngine localSSLEngine = null;
      String str1 = null;
      SSLStreams localSSLStreams = null;
      try
      {
        int i;
        if (context != null)
        {
          rawin = connection.getInputStream();
          rawout = connection.getRawOutputStream();
          i = 0;
        }
        else
        {
          i = 1;
          if (https)
          {
            if (sslContext == null)
            {
              logger.warning("SSL connection received. No https contxt created");
              throw new HttpError("No SSL context established");
            }
            localSSLStreams = new SSLStreams(ServerImpl.this, sslContext, chan);
            rawin = localSSLStreams.getInputStream();
            rawout = localSSLStreams.getOutputStream();
            localSSLEngine = localSSLStreams.getSSLEngine();
            connection.sslStreams = localSSLStreams;
          }
          else
          {
            rawin = new BufferedInputStream(new Request.ReadStream(ServerImpl.this, chan));
            rawout = new Request.WriteStream(ServerImpl.this, chan);
          }
          connection.raw = rawin;
          connection.rawout = rawout;
        }
        Request localRequest = new Request(rawin, rawout);
        str1 = localRequest.requestLine();
        if (str1 == null)
        {
          ServerImpl.this.closeConnection(connection);
          return;
        }
        int j = str1.indexOf(' ');
        if (j == -1)
        {
          reject(400, str1, "Bad request line");
          return;
        }
        String str2 = str1.substring(0, j);
        int k = j + 1;
        j = str1.indexOf(' ', k);
        if (j == -1)
        {
          reject(400, str1, "Bad request line");
          return;
        }
        String str3 = str1.substring(k, j);
        URI localURI = new URI(str3);
        k = j + 1;
        String str4 = str1.substring(k);
        Headers localHeaders1 = localRequest.headers();
        String str5 = localHeaders1.getFirst("Transfer-encoding");
        long l = 0L;
        if ((str5 != null) && (str5.equalsIgnoreCase("chunked")))
        {
          l = -1L;
        }
        else
        {
          str5 = localHeaders1.getFirst("Content-Length");
          if (str5 != null) {
            l = Long.parseLong(str5);
          }
          if (l == 0L) {
            requestCompleted(connection);
          }
        }
        ctx = contexts.findContext(protocol, localURI.getPath());
        if (ctx == null)
        {
          reject(404, str1, "No context found for request");
          return;
        }
        connection.setContext(ctx);
        if (ctx.getHandler() == null)
        {
          reject(500, str1, "No handler for context");
          return;
        }
        tx = new ExchangeImpl(str2, localURI, localRequest, l, connection);
        String str6 = localHeaders1.getFirst("Connection");
        Headers localHeaders2 = tx.getResponseHeaders();
        if ((str6 != null) && (str6.equalsIgnoreCase("close"))) {
          tx.close = true;
        }
        if (str4.equalsIgnoreCase("http/1.0"))
        {
          tx.http10 = true;
          if (str6 == null)
          {
            tx.close = true;
            localHeaders2.set("Connection", "close");
          }
          else if (str6.equalsIgnoreCase("keep-alive"))
          {
            localHeaders2.set("Connection", "keep-alive");
            int m = (int)(ServerConfig.getIdleInterval() / 1000L);
            int n = ServerConfig.getMaxIdleConnections();
            localObject = "timeout=" + m + ", max=" + n;
            localHeaders2.set("Keep-Alive", (String)localObject);
          }
        }
        if (i != 0) {
          connection.setParameters(rawin, rawout, chan, localSSLEngine, localSSLStreams, sslContext, protocol, ctx, rawin);
        }
        String str7 = localHeaders1.getFirst("Expect");
        if ((str7 != null) && (str7.equalsIgnoreCase("100-continue")))
        {
          logReply(100, str1, null);
          sendReply(100, false, null);
        }
        List localList = ctx.getSystemFilters();
        Object localObject = ctx.getFilters();
        Filter.Chain localChain1 = new Filter.Chain(localList, ctx.getHandler());
        Filter.Chain localChain2 = new Filter.Chain((List)localObject, new LinkHandler(localChain1));
        tx.getRequestBody();
        tx.getResponseBody();
        if (https) {
          localChain2.doFilter(new HttpsExchangeImpl(tx));
        } else {
          localChain2.doFilter(new HttpExchangeImpl(tx));
        }
      }
      catch (IOException localIOException)
      {
        logger.log(Level.FINER, "ServerImpl.Exchange (1)", localIOException);
        ServerImpl.this.closeConnection(connection);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        reject(400, str1, "NumberFormatException thrown");
      }
      catch (URISyntaxException localURISyntaxException)
      {
        reject(400, str1, "URISyntaxException thrown");
      }
      catch (Exception localException)
      {
        logger.log(Level.FINER, "ServerImpl.Exchange (2)", localException);
        ServerImpl.this.closeConnection(connection);
      }
    }
    
    void reject(int paramInt, String paramString1, String paramString2)
    {
      rejected = true;
      logReply(paramInt, paramString1, paramString2);
      sendReply(paramInt, false, "<h1>" + paramInt + Code.msg(paramInt) + "</h1>" + paramString2);
      ServerImpl.this.closeConnection(connection);
    }
    
    void sendReply(int paramInt, boolean paramBoolean, String paramString)
    {
      try
      {
        StringBuilder localStringBuilder = new StringBuilder(512);
        localStringBuilder.append("HTTP/1.1 ").append(paramInt).append(Code.msg(paramInt)).append("\r\n");
        if ((paramString != null) && (paramString.length() != 0))
        {
          localStringBuilder.append("Content-Length: ").append(paramString.length()).append("\r\n").append("Content-Type: text/html\r\n");
        }
        else
        {
          localStringBuilder.append("Content-Length: 0\r\n");
          paramString = "";
        }
        if (paramBoolean) {
          localStringBuilder.append("Connection: close\r\n");
        }
        localStringBuilder.append("\r\n").append(paramString);
        String str = localStringBuilder.toString();
        byte[] arrayOfByte = str.getBytes("ISO8859_1");
        rawout.write(arrayOfByte);
        rawout.flush();
        if (paramBoolean) {
          ServerImpl.this.closeConnection(connection);
        }
      }
      catch (IOException localIOException)
      {
        logger.log(Level.FINER, "ServerImpl.sendReply", localIOException);
        ServerImpl.this.closeConnection(connection);
      }
    }
    
    class LinkHandler
      implements HttpHandler
    {
      Filter.Chain nextChain;
      
      LinkHandler(Filter.Chain paramChain)
      {
        nextChain = paramChain;
      }
      
      public void handle(HttpExchange paramHttpExchange)
        throws IOException
      {
        nextChain.doFilter(paramHttpExchange);
      }
    }
  }
  
  class ServerTimerTask
    extends TimerTask
  {
    ServerTimerTask() {}
    
    public void run()
    {
      LinkedList localLinkedList = new LinkedList();
      time = System.currentTimeMillis();
      ServerImpl.access$1808(ServerImpl.this);
      synchronized (idleConnections)
      {
        Iterator localIterator = idleConnections.iterator();
        HttpConnection localHttpConnection;
        while (localIterator.hasNext())
        {
          localHttpConnection = (HttpConnection)localIterator.next();
          if (time <= time) {
            localLinkedList.add(localHttpConnection);
          }
        }
        localIterator = localLinkedList.iterator();
        while (localIterator.hasNext())
        {
          localHttpConnection = (HttpConnection)localIterator.next();
          idleConnections.remove(localHttpConnection);
          allConnections.remove(localHttpConnection);
          localHttpConnection.close();
        }
      }
    }
  }
  
  class ServerTimerTask1
    extends TimerTask
  {
    ServerTimerTask1() {}
    
    public void run()
    {
      LinkedList localLinkedList = new LinkedList();
      time = System.currentTimeMillis();
      Iterator localIterator;
      HttpConnection localHttpConnection;
      synchronized (reqConnections)
      {
        if (ServerImpl.MAX_REQ_TIME != -1L)
        {
          localIterator = reqConnections.iterator();
          while (localIterator.hasNext())
          {
            localHttpConnection = (HttpConnection)localIterator.next();
            if (creationTime + ServerImpl.TIMER_MILLIS + ServerImpl.MAX_REQ_TIME <= time) {
              localLinkedList.add(localHttpConnection);
            }
          }
          localIterator = localLinkedList.iterator();
          while (localIterator.hasNext())
          {
            localHttpConnection = (HttpConnection)localIterator.next();
            logger.log(Level.FINE, "closing: no request: " + localHttpConnection);
            reqConnections.remove(localHttpConnection);
            allConnections.remove(localHttpConnection);
            localHttpConnection.close();
          }
        }
      }
      localLinkedList = new LinkedList();
      synchronized (rspConnections)
      {
        if (ServerImpl.MAX_RSP_TIME != -1L)
        {
          localIterator = rspConnections.iterator();
          while (localIterator.hasNext())
          {
            localHttpConnection = (HttpConnection)localIterator.next();
            if (rspStartedTime + ServerImpl.TIMER_MILLIS + ServerImpl.MAX_RSP_TIME <= time) {
              localLinkedList.add(localHttpConnection);
            }
          }
          localIterator = localLinkedList.iterator();
          while (localIterator.hasNext())
          {
            localHttpConnection = (HttpConnection)localIterator.next();
            logger.log(Level.FINE, "closing: no response: " + localHttpConnection);
            rspConnections.remove(localHttpConnection);
            allConnections.remove(localHttpConnection);
            localHttpConnection.close();
          }
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\httpserver\ServerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */