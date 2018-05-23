package java.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessController;
import java.util.Hashtable;
import java.util.StringTokenizer;
import sun.net.ApplicationProxy;
import sun.security.action.GetPropertyAction;
import sun.security.util.SecurityConstants;

public final class URL
  implements Serializable
{
  static final String BUILTIN_HANDLERS_PREFIX = "sun.net.www.protocol";
  static final long serialVersionUID = -7627629688361524110L;
  private static final String protocolPathProp = "java.protocol.handler.pkgs";
  private String protocol;
  private String host;
  private int port = -1;
  private String file;
  private transient String query;
  private String authority;
  private transient String path;
  private transient String userInfo;
  private String ref;
  transient InetAddress hostAddress;
  transient URLStreamHandler handler;
  private int hashCode = -1;
  private transient UrlDeserializedState tempState;
  static URLStreamHandlerFactory factory;
  static Hashtable<String, URLStreamHandler> handlers = new Hashtable();
  private static Object streamHandlerLock = new Object();
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("protocol", String.class), new ObjectStreamField("host", String.class), new ObjectStreamField("port", Integer.TYPE), new ObjectStreamField("authority", String.class), new ObjectStreamField("file", String.class), new ObjectStreamField("ref", String.class), new ObjectStreamField("hashCode", Integer.TYPE) };
  
  public URL(String paramString1, String paramString2, int paramInt, String paramString3)
    throws MalformedURLException
  {
    this(paramString1, paramString2, paramInt, paramString3, null);
  }
  
  public URL(String paramString1, String paramString2, String paramString3)
    throws MalformedURLException
  {
    this(paramString1, paramString2, -1, paramString3);
  }
  
  public URL(String paramString1, String paramString2, int paramInt, String paramString3, URLStreamHandler paramURLStreamHandler)
    throws MalformedURLException
  {
    if (paramURLStreamHandler != null)
    {
      localObject = System.getSecurityManager();
      if (localObject != null) {
        checkSpecifyHandler((SecurityManager)localObject);
      }
    }
    paramString1 = paramString1.toLowerCase();
    protocol = paramString1;
    if (paramString2 != null)
    {
      if ((paramString2.indexOf(':') >= 0) && (!paramString2.startsWith("["))) {
        paramString2 = "[" + paramString2 + "]";
      }
      host = paramString2;
      if (paramInt < -1) {
        throw new MalformedURLException("Invalid port number :" + paramInt);
      }
      port = paramInt;
      authority = (paramString2 + ":" + paramInt);
    }
    Object localObject = new Parts(paramString3);
    path = ((Parts)localObject).getPath();
    query = ((Parts)localObject).getQuery();
    if (query != null) {
      file = (path + "?" + query);
    } else {
      file = path;
    }
    ref = ((Parts)localObject).getRef();
    if ((paramURLStreamHandler == null) && ((paramURLStreamHandler = getURLStreamHandler(paramString1)) == null)) {
      throw new MalformedURLException("unknown protocol: " + paramString1);
    }
    handler = paramURLStreamHandler;
  }
  
  public URL(String paramString)
    throws MalformedURLException
  {
    this(null, paramString);
  }
  
  public URL(URL paramURL, String paramString)
    throws MalformedURLException
  {
    this(paramURL, paramString, null);
  }
  
  public URL(URL paramURL, String paramString, URLStreamHandler paramURLStreamHandler)
    throws MalformedURLException
  {
    String str = paramString;
    int m = 0;
    Object localObject1 = null;
    int n = 0;
    int i1 = 0;
    Object localObject2;
    if (paramURLStreamHandler != null)
    {
      localObject2 = System.getSecurityManager();
      if (localObject2 != null) {
        checkSpecifyHandler((SecurityManager)localObject2);
      }
    }
    try
    {
      for (int j = paramString.length(); (j > 0) && (paramString.charAt(j - 1) <= ' '); j--) {}
      while ((m < j) && (paramString.charAt(m) <= ' ')) {
        m++;
      }
      if (paramString.regionMatches(true, m, "url:", 0, 4)) {
        m += 4;
      }
      if ((m < paramString.length()) && (paramString.charAt(m) == '#')) {
        n = 1;
      }
      int k;
      for (int i = m; (n == 0) && (i < j) && ((k = paramString.charAt(i)) != '/'); i++) {
        if (k == 58)
        {
          localObject2 = paramString.substring(m, i).toLowerCase();
          if (!isValidProtocol((String)localObject2)) {
            break;
          }
          localObject1 = localObject2;
          m = i + 1;
          break;
        }
      }
      protocol = ((String)localObject1);
      if ((paramURL != null) && ((localObject1 == null) || (((String)localObject1).equalsIgnoreCase(protocol))))
      {
        if (paramURLStreamHandler == null) {
          paramURLStreamHandler = handler;
        }
        if ((path != null) && (path.startsWith("/"))) {
          localObject1 = null;
        }
        if (localObject1 == null)
        {
          protocol = protocol;
          authority = authority;
          userInfo = userInfo;
          host = host;
          port = port;
          file = file;
          path = path;
          i1 = 1;
        }
      }
      if (protocol == null) {
        throw new MalformedURLException("no protocol: " + str);
      }
      if ((paramURLStreamHandler == null) && ((paramURLStreamHandler = getURLStreamHandler(protocol)) == null)) {
        throw new MalformedURLException("unknown protocol: " + protocol);
      }
      handler = paramURLStreamHandler;
      i = paramString.indexOf('#', m);
      if (i >= 0)
      {
        ref = paramString.substring(i + 1, j);
        j = i;
      }
      if ((i1 != 0) && (m == j))
      {
        query = query;
        if (ref == null) {
          ref = ref;
        }
      }
      paramURLStreamHandler.parseURL(this, paramString, m, j);
    }
    catch (MalformedURLException localMalformedURLException1)
    {
      throw localMalformedURLException1;
    }
    catch (Exception localException)
    {
      MalformedURLException localMalformedURLException2 = new MalformedURLException(localException.getMessage());
      localMalformedURLException2.initCause(localException);
      throw localMalformedURLException2;
    }
  }
  
  private boolean isValidProtocol(String paramString)
  {
    int i = paramString.length();
    if (i < 1) {
      return false;
    }
    char c = paramString.charAt(0);
    if (!Character.isLetter(c)) {
      return false;
    }
    for (int j = 1; j < i; j++)
    {
      c = paramString.charAt(j);
      if ((!Character.isLetterOrDigit(c)) && (c != '.') && (c != '+') && (c != '-')) {
        return false;
      }
    }
    return true;
  }
  
  private void checkSpecifyHandler(SecurityManager paramSecurityManager)
  {
    paramSecurityManager.checkPermission(SecurityConstants.SPECIFY_HANDLER_PERMISSION);
  }
  
  void set(String paramString1, String paramString2, int paramInt, String paramString3, String paramString4)
  {
    synchronized (this)
    {
      protocol = paramString1;
      host = paramString2;
      authority = (paramString2 + ":" + paramInt);
      port = paramInt;
      file = paramString3;
      ref = paramString4;
      hashCode = -1;
      hostAddress = null;
      int i = paramString3.lastIndexOf('?');
      if (i != -1)
      {
        query = paramString3.substring(i + 1);
        path = paramString3.substring(0, i);
      }
      else
      {
        path = paramString3;
      }
    }
  }
  
  void set(String paramString1, String paramString2, int paramInt, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7)
  {
    synchronized (this)
    {
      protocol = paramString1;
      host = paramString2;
      port = paramInt;
      file = (paramString5 + "?" + paramString6);
      userInfo = paramString4;
      path = paramString5;
      ref = paramString7;
      hashCode = -1;
      hostAddress = null;
      query = paramString6;
      authority = paramString3;
    }
  }
  
  public String getQuery()
  {
    return query;
  }
  
  public String getPath()
  {
    return path;
  }
  
  public String getUserInfo()
  {
    return userInfo;
  }
  
  public String getAuthority()
  {
    return authority;
  }
  
  public int getPort()
  {
    return port;
  }
  
  public int getDefaultPort()
  {
    return handler.getDefaultPort();
  }
  
  public String getProtocol()
  {
    return protocol;
  }
  
  public String getHost()
  {
    return host;
  }
  
  public String getFile()
  {
    return file;
  }
  
  public String getRef()
  {
    return ref;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof URL)) {
      return false;
    }
    URL localURL = (URL)paramObject;
    return handler.equals(this, localURL);
  }
  
  public synchronized int hashCode()
  {
    if (hashCode != -1) {
      return hashCode;
    }
    hashCode = handler.hashCode(this);
    return hashCode;
  }
  
  public boolean sameFile(URL paramURL)
  {
    return handler.sameFile(this, paramURL);
  }
  
  public String toString()
  {
    return toExternalForm();
  }
  
  public String toExternalForm()
  {
    return handler.toExternalForm(this);
  }
  
  public URI toURI()
    throws URISyntaxException
  {
    return new URI(toString());
  }
  
  public URLConnection openConnection()
    throws IOException
  {
    return handler.openConnection(this);
  }
  
  public URLConnection openConnection(Proxy paramProxy)
    throws IOException
  {
    if (paramProxy == null) {
      throw new IllegalArgumentException("proxy can not be null");
    }
    ApplicationProxy localApplicationProxy = paramProxy == Proxy.NO_PROXY ? Proxy.NO_PROXY : ApplicationProxy.create(paramProxy);
    SecurityManager localSecurityManager = System.getSecurityManager();
    if ((localApplicationProxy.type() != Proxy.Type.DIRECT) && (localSecurityManager != null))
    {
      InetSocketAddress localInetSocketAddress = (InetSocketAddress)localApplicationProxy.address();
      if (localInetSocketAddress.isUnresolved()) {
        localSecurityManager.checkConnect(localInetSocketAddress.getHostName(), localInetSocketAddress.getPort());
      } else {
        localSecurityManager.checkConnect(localInetSocketAddress.getAddress().getHostAddress(), localInetSocketAddress.getPort());
      }
    }
    return handler.openConnection(this, localApplicationProxy);
  }
  
  public final InputStream openStream()
    throws IOException
  {
    return openConnection().getInputStream();
  }
  
  public final Object getContent()
    throws IOException
  {
    return openConnection().getContent();
  }
  
  public final Object getContent(Class[] paramArrayOfClass)
    throws IOException
  {
    return openConnection().getContent(paramArrayOfClass);
  }
  
  public static void setURLStreamHandlerFactory(URLStreamHandlerFactory paramURLStreamHandlerFactory)
  {
    synchronized (streamHandlerLock)
    {
      if (factory != null) {
        throw new Error("factory already defined");
      }
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null) {
        localSecurityManager.checkSetFactory();
      }
      handlers.clear();
      factory = paramURLStreamHandlerFactory;
    }
  }
  
  static URLStreamHandler getURLStreamHandler(String paramString)
  {
    Object localObject1 = (URLStreamHandler)handlers.get(paramString);
    if (localObject1 == null)
    {
      int i = 0;
      if (factory != null)
      {
        localObject1 = factory.createURLStreamHandler(paramString);
        i = 1;
      }
      Object localObject2;
      if (localObject1 == null)
      {
        String str1 = null;
        str1 = (String)AccessController.doPrivileged(new GetPropertyAction("java.protocol.handler.pkgs", ""));
        if (str1 != "") {
          str1 = str1 + "|";
        }
        str1 = str1 + "sun.net.www.protocol";
        localObject2 = new StringTokenizer(str1, "|");
        while ((localObject1 == null) && (((StringTokenizer)localObject2).hasMoreTokens()))
        {
          String str2 = ((StringTokenizer)localObject2).nextToken().trim();
          try
          {
            String str3 = str2 + "." + paramString + ".Handler";
            Class localClass = null;
            try
            {
              localClass = Class.forName(str3);
            }
            catch (ClassNotFoundException localClassNotFoundException)
            {
              ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
              if (localClassLoader != null) {
                localClass = localClassLoader.loadClass(str3);
              }
            }
            if (localClass != null) {
              localObject1 = (URLStreamHandler)localClass.newInstance();
            }
          }
          catch (Exception localException) {}
        }
      }
      synchronized (streamHandlerLock)
      {
        localObject2 = null;
        localObject2 = (URLStreamHandler)handlers.get(paramString);
        if (localObject2 != null) {
          return (URLStreamHandler)localObject2;
        }
        if ((i == 0) && (factory != null)) {
          localObject2 = factory.createURLStreamHandler(paramString);
        }
        if (localObject2 != null) {
          localObject1 = localObject2;
        }
        if (localObject1 != null) {
          handlers.put(paramString, localObject1);
        }
      }
    }
    return (URLStreamHandler)localObject1;
  }
  
  private synchronized void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private synchronized void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    String str1 = (String)localGetField.get("protocol", null);
    if (getURLStreamHandler(str1) == null) {
      throw new IOException("unknown protocol: " + str1);
    }
    String str2 = (String)localGetField.get("host", null);
    int i = localGetField.get("port", -1);
    String str3 = (String)localGetField.get("authority", null);
    String str4 = (String)localGetField.get("file", null);
    String str5 = (String)localGetField.get("ref", null);
    int j = localGetField.get("hashCode", -1);
    if ((str3 == null) && (((str2 != null) && (str2.length() > 0)) || (i != -1)))
    {
      if (str2 == null) {
        str2 = "";
      }
      str3 = str2 + ":" + i;
    }
    tempState = new UrlDeserializedState(str1, str2, i, str3, str4, str5, j);
  }
  
  private Object readResolve()
    throws ObjectStreamException
  {
    URLStreamHandler localURLStreamHandler = null;
    localURLStreamHandler = getURLStreamHandler(tempState.getProtocol());
    URL localURL = null;
    if (isBuiltinStreamHandler(localURLStreamHandler.getClass().getName())) {
      localURL = fabricateNewURL();
    } else {
      localURL = setDeserializedFields(localURLStreamHandler);
    }
    return localURL;
  }
  
  private URL setDeserializedFields(URLStreamHandler paramURLStreamHandler)
  {
    String str1 = null;
    String str2 = tempState.getProtocol();
    String str3 = tempState.getHost();
    int i = tempState.getPort();
    String str4 = tempState.getAuthority();
    String str5 = tempState.getFile();
    String str6 = tempState.getRef();
    int j = tempState.getHashCode();
    int k;
    if ((str4 == null) && (((str3 != null) && (str3.length() > 0)) || (i != -1)))
    {
      if (str3 == null) {
        str3 = "";
      }
      str4 = str3 + ":" + i;
      k = str3.lastIndexOf('@');
      if (k != -1)
      {
        str1 = str3.substring(0, k);
        str3 = str3.substring(k + 1);
      }
    }
    else if (str4 != null)
    {
      k = str4.indexOf('@');
      if (k != -1) {
        str1 = str4.substring(0, k);
      }
    }
    String str7 = null;
    String str8 = null;
    if (str5 != null)
    {
      int m = str5.lastIndexOf('?');
      if (m != -1)
      {
        str8 = str5.substring(m + 1);
        str7 = str5.substring(0, m);
      }
      else
      {
        str7 = str5;
      }
    }
    protocol = str2;
    host = str3;
    port = i;
    file = str5;
    authority = str4;
    ref = str6;
    hashCode = j;
    handler = paramURLStreamHandler;
    query = str8;
    path = str7;
    userInfo = str1;
    URL localURL = this;
    return localURL;
  }
  
  private URL fabricateNewURL()
    throws InvalidObjectException
  {
    URL localURL = null;
    String str = tempState.reconstituteUrlString();
    try
    {
      localURL = new URL(str);
    }
    catch (MalformedURLException localMalformedURLException)
    {
      resetState();
      InvalidObjectException localInvalidObjectException = new InvalidObjectException("Malformed URL: " + str);
      localInvalidObjectException.initCause(localMalformedURLException);
      throw localInvalidObjectException;
    }
    localURL.setSerializedHashCode(tempState.getHashCode());
    resetState();
    return localURL;
  }
  
  private boolean isBuiltinStreamHandler(String paramString)
  {
    return paramString.startsWith("sun.net.www.protocol");
  }
  
  private void resetState()
  {
    protocol = null;
    host = null;
    port = -1;
    file = null;
    authority = null;
    ref = null;
    hashCode = -1;
    handler = null;
    query = null;
    path = null;
    userInfo = null;
    tempState = null;
  }
  
  private void setSerializedHashCode(int paramInt)
  {
    hashCode = paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\URL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */