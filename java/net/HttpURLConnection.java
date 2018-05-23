package java.net;

import java.io.IOException;
import java.io.InputStream;
import java.security.Permission;
import java.util.Date;

public abstract class HttpURLConnection
  extends URLConnection
{
  protected String method = "GET";
  protected int chunkLength = -1;
  protected int fixedContentLength = -1;
  protected long fixedContentLengthLong = -1L;
  private static final int DEFAULT_CHUNK_SIZE = 4096;
  protected int responseCode = -1;
  protected String responseMessage = null;
  private static boolean followRedirects = true;
  protected boolean instanceFollowRedirects = followRedirects;
  private static final String[] methods = { "GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE", "TRACE" };
  public static final int HTTP_OK = 200;
  public static final int HTTP_CREATED = 201;
  public static final int HTTP_ACCEPTED = 202;
  public static final int HTTP_NOT_AUTHORITATIVE = 203;
  public static final int HTTP_NO_CONTENT = 204;
  public static final int HTTP_RESET = 205;
  public static final int HTTP_PARTIAL = 206;
  public static final int HTTP_MULT_CHOICE = 300;
  public static final int HTTP_MOVED_PERM = 301;
  public static final int HTTP_MOVED_TEMP = 302;
  public static final int HTTP_SEE_OTHER = 303;
  public static final int HTTP_NOT_MODIFIED = 304;
  public static final int HTTP_USE_PROXY = 305;
  public static final int HTTP_BAD_REQUEST = 400;
  public static final int HTTP_UNAUTHORIZED = 401;
  public static final int HTTP_PAYMENT_REQUIRED = 402;
  public static final int HTTP_FORBIDDEN = 403;
  public static final int HTTP_NOT_FOUND = 404;
  public static final int HTTP_BAD_METHOD = 405;
  public static final int HTTP_NOT_ACCEPTABLE = 406;
  public static final int HTTP_PROXY_AUTH = 407;
  public static final int HTTP_CLIENT_TIMEOUT = 408;
  public static final int HTTP_CONFLICT = 409;
  public static final int HTTP_GONE = 410;
  public static final int HTTP_LENGTH_REQUIRED = 411;
  public static final int HTTP_PRECON_FAILED = 412;
  public static final int HTTP_ENTITY_TOO_LARGE = 413;
  public static final int HTTP_REQ_TOO_LONG = 414;
  public static final int HTTP_UNSUPPORTED_TYPE = 415;
  @Deprecated
  public static final int HTTP_SERVER_ERROR = 500;
  public static final int HTTP_INTERNAL_ERROR = 500;
  public static final int HTTP_NOT_IMPLEMENTED = 501;
  public static final int HTTP_BAD_GATEWAY = 502;
  public static final int HTTP_UNAVAILABLE = 503;
  public static final int HTTP_GATEWAY_TIMEOUT = 504;
  public static final int HTTP_VERSION = 505;
  
  public String getHeaderFieldKey(int paramInt)
  {
    return null;
  }
  
  public void setFixedLengthStreamingMode(int paramInt)
  {
    if (connected) {
      throw new IllegalStateException("Already connected");
    }
    if (chunkLength != -1) {
      throw new IllegalStateException("Chunked encoding streaming mode set");
    }
    if (paramInt < 0) {
      throw new IllegalArgumentException("invalid content length");
    }
    fixedContentLength = paramInt;
  }
  
  public void setFixedLengthStreamingMode(long paramLong)
  {
    if (connected) {
      throw new IllegalStateException("Already connected");
    }
    if (chunkLength != -1) {
      throw new IllegalStateException("Chunked encoding streaming mode set");
    }
    if (paramLong < 0L) {
      throw new IllegalArgumentException("invalid content length");
    }
    fixedContentLengthLong = paramLong;
  }
  
  public void setChunkedStreamingMode(int paramInt)
  {
    if (connected) {
      throw new IllegalStateException("Can't set streaming mode: already connected");
    }
    if ((fixedContentLength != -1) || (fixedContentLengthLong != -1L)) {
      throw new IllegalStateException("Fixed length streaming mode set");
    }
    chunkLength = (paramInt <= 0 ? 4096 : paramInt);
  }
  
  public String getHeaderField(int paramInt)
  {
    return null;
  }
  
  protected HttpURLConnection(URL paramURL)
  {
    super(paramURL);
  }
  
  public static void setFollowRedirects(boolean paramBoolean)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkSetFactory();
    }
    followRedirects = paramBoolean;
  }
  
  public static boolean getFollowRedirects()
  {
    return followRedirects;
  }
  
  public void setInstanceFollowRedirects(boolean paramBoolean)
  {
    instanceFollowRedirects = paramBoolean;
  }
  
  public boolean getInstanceFollowRedirects()
  {
    return instanceFollowRedirects;
  }
  
  public void setRequestMethod(String paramString)
    throws ProtocolException
  {
    if (connected) {
      throw new ProtocolException("Can't reset method: already connected");
    }
    for (int i = 0; i < methods.length; i++) {
      if (methods[i].equals(paramString))
      {
        if (paramString.equals("TRACE"))
        {
          SecurityManager localSecurityManager = System.getSecurityManager();
          if (localSecurityManager != null) {
            localSecurityManager.checkPermission(new NetPermission("allowHttpTrace"));
          }
        }
        method = paramString;
        return;
      }
    }
    throw new ProtocolException("Invalid HTTP method: " + paramString);
  }
  
  public String getRequestMethod()
  {
    return method;
  }
  
  public int getResponseCode()
    throws IOException
  {
    if (responseCode != -1) {
      return responseCode;
    }
    Object localObject = null;
    try
    {
      getInputStream();
    }
    catch (Exception localException)
    {
      localObject = localException;
    }
    String str = getHeaderField(0);
    if (str == null)
    {
      if (localObject != null)
      {
        if ((localObject instanceof RuntimeException)) {
          throw ((RuntimeException)localObject);
        }
        throw ((IOException)localObject);
      }
      return -1;
    }
    if (str.startsWith("HTTP/1."))
    {
      int i = str.indexOf(' ');
      if (i > 0)
      {
        int j = str.indexOf(' ', i + 1);
        if ((j > 0) && (j < str.length())) {
          responseMessage = str.substring(j + 1);
        }
        if (j < 0) {
          j = str.length();
        }
        try
        {
          responseCode = Integer.parseInt(str.substring(i + 1, j));
          return responseCode;
        }
        catch (NumberFormatException localNumberFormatException) {}
      }
    }
    return -1;
  }
  
  public String getResponseMessage()
    throws IOException
  {
    getResponseCode();
    return responseMessage;
  }
  
  public long getHeaderFieldDate(String paramString, long paramLong)
  {
    String str = getHeaderField(paramString);
    try
    {
      if (str.indexOf("GMT") == -1) {
        str = str + " GMT";
      }
      return Date.parse(str);
    }
    catch (Exception localException) {}
    return paramLong;
  }
  
  public abstract void disconnect();
  
  public abstract boolean usingProxy();
  
  public Permission getPermission()
    throws IOException
  {
    int i = url.getPort();
    i = i < 0 ? 80 : i;
    String str = url.getHost() + ":" + i;
    SocketPermission localSocketPermission = new SocketPermission(str, "connect");
    return localSocketPermission;
  }
  
  public InputStream getErrorStream()
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\HttpURLConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */