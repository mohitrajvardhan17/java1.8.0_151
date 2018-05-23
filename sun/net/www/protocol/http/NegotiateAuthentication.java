package sun.net.www.protocol.http;

import java.io.IOException;
import java.net.Authenticator.RequestorType;
import java.net.URL;
import java.security.AccessController;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.HashMap;
import sun.net.www.HeaderParser;
import sun.security.action.GetPropertyAction;
import sun.util.logging.PlatformLogger;

class NegotiateAuthentication
  extends AuthenticationInfo
{
  private static final long serialVersionUID = 100L;
  private static final PlatformLogger logger = ;
  private final HttpCallerInfo hci;
  static HashMap<String, Boolean> supported = null;
  static ThreadLocal<HashMap<String, Negotiator>> cache = null;
  private static final boolean cacheSPNEGO;
  private Negotiator negotiator = null;
  
  public NegotiateAuthentication(HttpCallerInfo paramHttpCallerInfo)
  {
    super(Authenticator.RequestorType.PROXY == authType ? 'p' : 's', scheme.equalsIgnoreCase("Negotiate") ? AuthScheme.NEGOTIATE : AuthScheme.KERBEROS, url, "");
    hci = paramHttpCallerInfo;
  }
  
  public boolean supportsPreemptiveAuthorization()
  {
    return false;
  }
  
  /* Error */
  public static boolean isSupported(HttpCallerInfo paramHttpCallerInfo)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: invokestatic 267	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   5: invokevirtual 266	java/lang/Thread:getContextClassLoader	()Ljava/lang/ClassLoader;
    //   8: astore_1
    //   9: goto +41 -> 50
    //   12: astore_2
    //   13: getstatic 252	sun/net/www/protocol/http/NegotiateAuthentication:logger	Lsun/util/logging/PlatformLogger;
    //   16: getstatic 253	sun/util/logging/PlatformLogger$Level:FINER	Lsun/util/logging/PlatformLogger$Level;
    //   19: invokevirtual 295	sun/util/logging/PlatformLogger:isLoggable	(Lsun/util/logging/PlatformLogger$Level;)Z
    //   22: ifeq +28 -> 50
    //   25: getstatic 252	sun/net/www/protocol/http/NegotiateAuthentication:logger	Lsun/util/logging/PlatformLogger;
    //   28: new 134	java/lang/StringBuilder
    //   31: dup
    //   32: invokespecial 262	java/lang/StringBuilder:<init>	()V
    //   35: ldc 5
    //   37: invokevirtual 265	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   40: aload_2
    //   41: invokevirtual 264	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   44: invokevirtual 263	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   47: invokevirtual 294	sun/util/logging/PlatformLogger:finer	(Ljava/lang/String;)V
    //   50: aload_1
    //   51: ifnull +19 -> 70
    //   54: aload_1
    //   55: dup
    //   56: astore_2
    //   57: monitorenter
    //   58: aload_0
    //   59: invokestatic 288	sun/net/www/protocol/http/NegotiateAuthentication:isSupportedImpl	(Lsun/net/www/protocol/http/HttpCallerInfo;)Z
    //   62: aload_2
    //   63: monitorexit
    //   64: ireturn
    //   65: astore_3
    //   66: aload_2
    //   67: monitorexit
    //   68: aload_3
    //   69: athrow
    //   70: aload_0
    //   71: invokestatic 288	sun/net/www/protocol/http/NegotiateAuthentication:isSupportedImpl	(Lsun/net/www/protocol/http/HttpCallerInfo;)Z
    //   74: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	75	0	paramHttpCallerInfo	HttpCallerInfo
    //   1	54	1	localClassLoader	ClassLoader
    //   12	29	2	localSecurityException	SecurityException
    //   56	11	2	Ljava/lang/Object;	Object
    //   65	4	3	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   2	9	12	java/lang/SecurityException
    //   58	64	65	finally
    //   65	68	65	finally
  }
  
  private static synchronized boolean isSupportedImpl(HttpCallerInfo paramHttpCallerInfo)
  {
    if (supported == null) {
      supported = new HashMap();
    }
    String str = host;
    str = str.toLowerCase();
    if (supported.containsKey(str)) {
      return ((Boolean)supported.get(str)).booleanValue();
    }
    Negotiator localNegotiator = Negotiator.getNegotiator(paramHttpCallerInfo);
    if (localNegotiator != null)
    {
      supported.put(str, Boolean.valueOf(true));
      if (cache == null) {
        cache = new ThreadLocal()
        {
          protected HashMap<String, Negotiator> initialValue()
          {
            return new HashMap();
          }
        };
      }
      ((HashMap)cache.get()).put(str, localNegotiator);
      return true;
    }
    supported.put(str, Boolean.valueOf(false));
    return false;
  }
  
  private static synchronized HashMap<String, Negotiator> getCache()
  {
    if (cache == null) {
      return null;
    }
    return (HashMap)cache.get();
  }
  
  protected boolean useAuthCache()
  {
    return (super.useAuthCache()) && (cacheSPNEGO);
  }
  
  public String getHeaderValue(URL paramURL, String paramString)
  {
    throw new RuntimeException("getHeaderValue not supported");
  }
  
  public boolean isAuthorizationStale(String paramString)
  {
    return false;
  }
  
  public synchronized boolean setHeaders(HttpURLConnection paramHttpURLConnection, HeaderParser paramHeaderParser, String paramString)
  {
    try
    {
      byte[] arrayOfByte = null;
      String[] arrayOfString = paramString.split("\\s+");
      if (arrayOfString.length > 1) {
        arrayOfByte = Base64.getDecoder().decode(arrayOfString[1]);
      }
      String str = hci.scheme + " " + Base64.getEncoder().encodeToString(arrayOfByte == null ? firstToken() : nextToken(arrayOfByte));
      paramHttpURLConnection.setAuthenticationProperty(getHeaderName(), str);
      return true;
    }
    catch (IOException localIOException) {}
    return false;
  }
  
  private byte[] firstToken()
    throws IOException
  {
    negotiator = null;
    HashMap localHashMap = getCache();
    if (localHashMap != null)
    {
      negotiator = ((Negotiator)localHashMap.get(getHost()));
      if (negotiator != null) {
        localHashMap.remove(getHost());
      }
    }
    if (negotiator == null)
    {
      negotiator = Negotiator.getNegotiator(hci);
      if (negotiator == null)
      {
        IOException localIOException = new IOException("Cannot initialize Negotiator");
        throw localIOException;
      }
    }
    return negotiator.firstToken();
  }
  
  private byte[] nextToken(byte[] paramArrayOfByte)
    throws IOException
  {
    return negotiator.nextToken(paramArrayOfByte);
  }
  
  static
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("jdk.spnego.cache", "true"));
    cacheSPNEGO = Boolean.parseBoolean(str);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\protocol\http\NegotiateAuthentication.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */