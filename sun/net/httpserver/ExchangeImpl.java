package sun.net.httpserver;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.nio.channels.SocketChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Logger;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;

class ExchangeImpl
{
  Headers reqHdrs;
  Headers rspHdrs;
  Request req;
  String method;
  boolean writefinished;
  URI uri;
  HttpConnection connection;
  long reqContentLen;
  long rspContentLen;
  InputStream ris;
  OutputStream ros;
  Thread thread;
  boolean close;
  boolean closed;
  boolean http10 = false;
  private static final String pattern = "EEE, dd MMM yyyy HH:mm:ss zzz";
  private static final TimeZone gmtTZ = TimeZone.getTimeZone("GMT");
  private static final ThreadLocal<DateFormat> dateFormat = new ThreadLocal()
  {
    protected DateFormat initialValue()
    {
      SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
      localSimpleDateFormat.setTimeZone(ExchangeImpl.gmtTZ);
      return localSimpleDateFormat;
    }
  };
  private static final String HEAD = "HEAD";
  InputStream uis;
  OutputStream uos;
  LeftOverInputStream uis_orig;
  PlaceholderOutputStream uos_orig;
  boolean sentHeaders;
  Map<String, Object> attributes;
  int rcode = -1;
  HttpPrincipal principal;
  ServerImpl server;
  private byte[] rspbuf = new byte[''];
  
  ExchangeImpl(String paramString, URI paramURI, Request paramRequest, long paramLong, HttpConnection paramHttpConnection)
    throws IOException
  {
    req = paramRequest;
    reqHdrs = paramRequest.headers();
    rspHdrs = new Headers();
    method = paramString;
    uri = paramURI;
    connection = paramHttpConnection;
    reqContentLen = paramLong;
    ros = paramRequest.outputStream();
    ris = paramRequest.inputStream();
    server = getServerImpl();
    server.startExchange();
  }
  
  public Headers getRequestHeaders()
  {
    return new UnmodifiableHeaders(reqHdrs);
  }
  
  public Headers getResponseHeaders()
  {
    return rspHdrs;
  }
  
  public URI getRequestURI()
  {
    return uri;
  }
  
  public String getRequestMethod()
  {
    return method;
  }
  
  public HttpContextImpl getHttpContext()
  {
    return connection.getHttpContext();
  }
  
  private boolean isHeadRequest()
  {
    return "HEAD".equals(getRequestMethod());
  }
  
  public void close()
  {
    if (closed) {
      return;
    }
    closed = true;
    try
    {
      if ((uis_orig == null) || (uos == null))
      {
        connection.close();
        return;
      }
      if (!uos_orig.isWrapped())
      {
        connection.close();
        return;
      }
      if (!uis_orig.isClosed()) {
        uis_orig.close();
      }
      uos.close();
    }
    catch (IOException localIOException)
    {
      connection.close();
    }
  }
  
  public InputStream getRequestBody()
  {
    if (uis != null) {
      return uis;
    }
    if (reqContentLen == -1L)
    {
      uis_orig = new ChunkedInputStream(this, ris);
      uis = uis_orig;
    }
    else
    {
      uis_orig = new FixedLengthInputStream(this, ris, reqContentLen);
      uis = uis_orig;
    }
    return uis;
  }
  
  LeftOverInputStream getOriginalInputStream()
  {
    return uis_orig;
  }
  
  public int getResponseCode()
  {
    return rcode;
  }
  
  public OutputStream getResponseBody()
  {
    if (uos == null)
    {
      uos_orig = new PlaceholderOutputStream(null);
      uos = uos_orig;
    }
    return uos;
  }
  
  PlaceholderOutputStream getPlaceholderResponseBody()
  {
    getResponseBody();
    return uos_orig;
  }
  
  public void sendResponseHeaders(int paramInt, long paramLong)
    throws IOException
  {
    if (sentHeaders) {
      throw new IOException("headers already sent");
    }
    rcode = paramInt;
    String str1 = "HTTP/1.1 " + paramInt + Code.msg(paramInt) + "\r\n";
    BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(ros);
    PlaceholderOutputStream localPlaceholderOutputStream = getPlaceholderResponseBody();
    localBufferedOutputStream.write(bytes(str1, 0), 0, str1.length());
    int i = 0;
    rspHdrs.set("Date", ((DateFormat)dateFormat.get()).format(new Date()));
    Object localObject;
    String str2;
    if (((paramInt >= 100) && (paramInt < 200)) || (paramInt == 204) || (paramInt == 304))
    {
      if (paramLong != -1L)
      {
        localObject = server.getLogger();
        str2 = "sendResponseHeaders: rCode = " + paramInt + ": forcing contentLen = -1";
        ((Logger)localObject).warning(str2);
      }
      paramLong = -1L;
    }
    if (isHeadRequest())
    {
      if (paramLong >= 0L)
      {
        localObject = server.getLogger();
        str2 = "sendResponseHeaders: being invoked with a content length for a HEAD request";
        ((Logger)localObject).warning(str2);
      }
      i = 1;
      paramLong = 0L;
    }
    else if (paramLong == 0L)
    {
      if (http10)
      {
        localPlaceholderOutputStream.setWrappedStream(new UndefLengthOutputStream(this, ros));
        close = true;
      }
      else
      {
        rspHdrs.set("Transfer-encoding", "chunked");
        localPlaceholderOutputStream.setWrappedStream(new ChunkedOutputStream(this, ros));
      }
    }
    else
    {
      if (paramLong == -1L)
      {
        i = 1;
        paramLong = 0L;
      }
      rspHdrs.set("Content-length", Long.toString(paramLong));
      localPlaceholderOutputStream.setWrappedStream(new FixedLengthOutputStream(this, ros, paramLong));
    }
    write(rspHdrs, localBufferedOutputStream);
    rspContentLen = paramLong;
    localBufferedOutputStream.flush();
    localBufferedOutputStream = null;
    sentHeaders = true;
    if (i != 0)
    {
      localObject = new WriteFinishedEvent(this);
      server.addEvent((Event)localObject);
      closed = true;
    }
    server.logReply(paramInt, req.requestLine(), null);
  }
  
  void write(Headers paramHeaders, OutputStream paramOutputStream)
    throws IOException
  {
    Set localSet = paramHeaders.entrySet();
    Iterator localIterator1 = localSet.iterator();
    while (localIterator1.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator1.next();
      String str1 = (String)localEntry.getKey();
      List localList = (List)localEntry.getValue();
      Iterator localIterator2 = localList.iterator();
      while (localIterator2.hasNext())
      {
        String str2 = (String)localIterator2.next();
        int i = str1.length();
        byte[] arrayOfByte = bytes(str1, 2);
        arrayOfByte[(i++)] = 58;
        arrayOfByte[(i++)] = 32;
        paramOutputStream.write(arrayOfByte, 0, i);
        arrayOfByte = bytes(str2, 2);
        i = str2.length();
        arrayOfByte[(i++)] = 13;
        arrayOfByte[(i++)] = 10;
        paramOutputStream.write(arrayOfByte, 0, i);
      }
    }
    paramOutputStream.write(13);
    paramOutputStream.write(10);
  }
  
  private byte[] bytes(String paramString, int paramInt)
  {
    int i = paramString.length();
    if (i + paramInt > rspbuf.length)
    {
      int j = i + paramInt - rspbuf.length;
      rspbuf = new byte[2 * (rspbuf.length + j)];
    }
    char[] arrayOfChar = paramString.toCharArray();
    for (int k = 0; k < arrayOfChar.length; k++) {
      rspbuf[k] = ((byte)arrayOfChar[k]);
    }
    return rspbuf;
  }
  
  public InetSocketAddress getRemoteAddress()
  {
    Socket localSocket = connection.getChannel().socket();
    InetAddress localInetAddress = localSocket.getInetAddress();
    int i = localSocket.getPort();
    return new InetSocketAddress(localInetAddress, i);
  }
  
  public InetSocketAddress getLocalAddress()
  {
    Socket localSocket = connection.getChannel().socket();
    InetAddress localInetAddress = localSocket.getLocalAddress();
    int i = localSocket.getLocalPort();
    return new InetSocketAddress(localInetAddress, i);
  }
  
  public String getProtocol()
  {
    String str = req.requestLine();
    int i = str.lastIndexOf(' ');
    return str.substring(i + 1);
  }
  
  public SSLSession getSSLSession()
  {
    SSLEngine localSSLEngine = connection.getSSLEngine();
    if (localSSLEngine == null) {
      return null;
    }
    return localSSLEngine.getSession();
  }
  
  public Object getAttribute(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("null name parameter");
    }
    if (attributes == null) {
      attributes = getHttpContext().getAttributes();
    }
    return attributes.get(paramString);
  }
  
  public void setAttribute(String paramString, Object paramObject)
  {
    if (paramString == null) {
      throw new NullPointerException("null name parameter");
    }
    if (attributes == null) {
      attributes = getHttpContext().getAttributes();
    }
    attributes.put(paramString, paramObject);
  }
  
  public void setStreams(InputStream paramInputStream, OutputStream paramOutputStream)
  {
    assert (uis != null);
    if (paramInputStream != null) {
      uis = paramInputStream;
    }
    if (paramOutputStream != null) {
      uos = paramOutputStream;
    }
  }
  
  HttpConnection getConnection()
  {
    return connection;
  }
  
  ServerImpl getServerImpl()
  {
    return getHttpContext().getServerImpl();
  }
  
  public HttpPrincipal getPrincipal()
  {
    return principal;
  }
  
  void setPrincipal(HttpPrincipal paramHttpPrincipal)
  {
    principal = paramHttpPrincipal;
  }
  
  static ExchangeImpl get(HttpExchange paramHttpExchange)
  {
    if ((paramHttpExchange instanceof HttpExchangeImpl)) {
      return ((HttpExchangeImpl)paramHttpExchange).getExchangeImpl();
    }
    assert ((paramHttpExchange instanceof HttpsExchangeImpl));
    return ((HttpsExchangeImpl)paramHttpExchange).getExchangeImpl();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\httpserver\ExchangeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */