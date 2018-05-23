package com.sun.jndi.ldap;

import com.sun.jndi.ldap.pool.Pool;
import com.sun.jndi.ldap.pool.PoolCleaner;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import java.util.Locale;
import java.util.StringTokenizer;
import javax.naming.CommunicationException;
import javax.naming.NamingException;
import javax.naming.ldap.Control;

public final class LdapPoolManager
{
  private static final String DEBUG = "com.sun.jndi.ldap.connect.pool.debug";
  public static final boolean debug = "all".equalsIgnoreCase(getProperty("com.sun.jndi.ldap.connect.pool.debug", null));
  public static final boolean trace = (debug) || ("fine".equalsIgnoreCase(getProperty("com.sun.jndi.ldap.connect.pool.debug", null)));
  private static final String POOL_AUTH = "com.sun.jndi.ldap.connect.pool.authentication";
  private static final String POOL_PROTOCOL = "com.sun.jndi.ldap.connect.pool.protocol";
  private static final String MAX_POOL_SIZE = "com.sun.jndi.ldap.connect.pool.maxsize";
  private static final String PREF_POOL_SIZE = "com.sun.jndi.ldap.connect.pool.prefsize";
  private static final String INIT_POOL_SIZE = "com.sun.jndi.ldap.connect.pool.initsize";
  private static final String POOL_TIMEOUT = "com.sun.jndi.ldap.connect.pool.timeout";
  private static final String SASL_CALLBACK = "java.naming.security.sasl.callback";
  private static final int DEFAULT_MAX_POOL_SIZE = 0;
  private static final int DEFAULT_PREF_POOL_SIZE = 0;
  private static final int DEFAULT_INIT_POOL_SIZE = 1;
  private static final int DEFAULT_TIMEOUT = 0;
  private static final String DEFAULT_AUTH_MECHS = "none simple";
  private static final String DEFAULT_PROTOCOLS = "plain";
  private static final int NONE = 0;
  private static final int SIMPLE = 1;
  private static final int DIGEST = 2;
  private static final long idleTimeout;
  private static final int maxSize;
  private static final int prefSize;
  private static final int initSize;
  private static boolean supportPlainProtocol = false;
  private static boolean supportSslProtocol = false;
  private static final Pool[] pools = new Pool[3];
  
  private LdapPoolManager() {}
  
  private static int findPool(String paramString)
  {
    if ("none".equalsIgnoreCase(paramString)) {
      return 0;
    }
    if ("simple".equalsIgnoreCase(paramString)) {
      return 1;
    }
    if ("digest-md5".equalsIgnoreCase(paramString)) {
      return 2;
    }
    return -1;
  }
  
  static boolean isPoolingAllowed(String paramString1, OutputStream paramOutputStream, String paramString2, String paramString3, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    if (((paramOutputStream != null) && (!debug)) || ((paramString3 == null) && (!supportPlainProtocol)) || (("ssl".equalsIgnoreCase(paramString3)) && (!supportSslProtocol)))
    {
      d("Pooling disallowed due to tracing or unsupported pooling of protocol");
      return false;
    }
    String str = "java.util.Comparator";
    int i = 0;
    if ((paramString1 != null) && (!paramString1.equals("javax.net.ssl.SSLSocketFactory")))
    {
      try
      {
        Class localClass = Obj.helper.loadClass(paramString1);
        localObject = localClass.getInterfaces();
        for (int k = 0; k < localObject.length; k++) {
          if (localObject[k].getCanonicalName().equals(str)) {
            i = 1;
          }
        }
      }
      catch (Exception localException)
      {
        Object localObject = new CommunicationException("Loading the socket factory");
        ((CommunicationException)localObject).setRootCause(localException);
        throw ((Throwable)localObject);
      }
      if (i == 0) {
        return false;
      }
    }
    int j = findPool(paramString2);
    if ((j < 0) || (pools[j] == null))
    {
      d("authmech not found: ", paramString2);
      return false;
    }
    d("using authmech: ", paramString2);
    switch (j)
    {
    case 0: 
    case 1: 
      return true;
    case 2: 
      return (paramHashtable == null) || (paramHashtable.get("java.naming.security.sasl.callback") == null);
    }
    return false;
  }
  
  static LdapClient getLdapClient(String paramString1, int paramInt1, String paramString2, int paramInt2, int paramInt3, OutputStream paramOutputStream, int paramInt4, String paramString3, Control[] paramArrayOfControl, String paramString4, String paramString5, Object paramObject, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    Object localObject = null;
    int i = findPool(paramString3);
    Pool localPool;
    if ((i < 0) || ((localPool = pools[i]) == null)) {
      throw new IllegalArgumentException("Attempting to use pooling for an unsupported mechanism: " + paramString3);
    }
    switch (i)
    {
    case 0: 
      localObject = new ClientId(paramInt4, paramString1, paramInt1, paramString4, paramArrayOfControl, paramOutputStream, paramString2);
      break;
    case 1: 
      localObject = new SimpleClientId(paramInt4, paramString1, paramInt1, paramString4, paramArrayOfControl, paramOutputStream, paramString2, paramString5, paramObject);
      break;
    case 2: 
      localObject = new DigestClientId(paramInt4, paramString1, paramInt1, paramString4, paramArrayOfControl, paramOutputStream, paramString2, paramString5, paramObject, paramHashtable);
    }
    return (LdapClient)localPool.getPooledConnection(localObject, paramInt2, new LdapClientFactory(paramString1, paramInt1, paramString2, paramInt2, paramInt3, paramOutputStream));
  }
  
  public static void showStats(PrintStream paramPrintStream)
  {
    paramPrintStream.println("***** start *****");
    paramPrintStream.println("idle timeout: " + idleTimeout);
    paramPrintStream.println("maximum pool size: " + maxSize);
    paramPrintStream.println("preferred pool size: " + prefSize);
    paramPrintStream.println("initial pool size: " + initSize);
    paramPrintStream.println("protocol types: " + (supportPlainProtocol ? "plain " : "") + (supportSslProtocol ? "ssl" : ""));
    paramPrintStream.println("authentication types: " + (pools[0] != null ? "none " : "") + (pools[1] != null ? "simple " : "") + (pools[2] != null ? "DIGEST-MD5 " : ""));
    for (int i = 0; i < pools.length; i++) {
      if (pools[i] != null)
      {
        paramPrintStream.println((i == 2 ? "digest pools" : i == 1 ? "simple auth pools" : i == 0 ? "anonymous pools" : "") + ":");
        pools[i].showStats(paramPrintStream);
      }
    }
    paramPrintStream.println("***** end *****");
  }
  
  public static void expire(long paramLong)
  {
    for (int i = 0; i < pools.length; i++) {
      if (pools[i] != null) {
        pools[i].expire(paramLong);
      }
    }
  }
  
  private static void d(String paramString)
  {
    if (debug) {
      System.err.println("LdapPoolManager: " + paramString);
    }
  }
  
  private static void d(String paramString1, String paramString2)
  {
    if (debug) {
      System.err.println("LdapPoolManager: " + paramString1 + paramString2);
    }
  }
  
  private static final String getProperty(String paramString1, final String paramString2)
  {
    (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        try
        {
          return System.getProperty(val$propName, paramString2);
        }
        catch (SecurityException localSecurityException) {}
        return paramString2;
      }
    });
  }
  
  private static final int getInteger(String paramString, final int paramInt)
  {
    Integer localInteger = (Integer)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Integer run()
      {
        try
        {
          return Integer.getInteger(val$propName, paramInt);
        }
        catch (SecurityException localSecurityException) {}
        return new Integer(paramInt);
      }
    });
    return localInteger.intValue();
  }
  
  private static final long getLong(String paramString, final long paramLong)
  {
    Long localLong = (Long)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Long run()
      {
        try
        {
          return Long.getLong(val$propName, paramLong);
        }
        catch (SecurityException localSecurityException) {}
        return new Long(paramLong);
      }
    });
    return localLong.longValue();
  }
  
  static
  {
    maxSize = getInteger("com.sun.jndi.ldap.connect.pool.maxsize", 0);
    prefSize = getInteger("com.sun.jndi.ldap.connect.pool.prefsize", 0);
    initSize = getInteger("com.sun.jndi.ldap.connect.pool.initsize", 1);
    idleTimeout = getLong("com.sun.jndi.ldap.connect.pool.timeout", 0L);
    String str1 = getProperty("com.sun.jndi.ldap.connect.pool.authentication", "none simple");
    StringTokenizer localStringTokenizer = new StringTokenizer(str1);
    int i = localStringTokenizer.countTokens();
    for (int k = 0; k < i; k++)
    {
      String str2 = localStringTokenizer.nextToken().toLowerCase(Locale.ENGLISH);
      if (str2.equals("anonymous")) {
        str2 = "none";
      }
      int j = findPool(str2);
      if ((j >= 0) && (pools[j] == null)) {
        pools[j] = new Pool(initSize, prefSize, maxSize);
      }
    }
    str1 = getProperty("com.sun.jndi.ldap.connect.pool.protocol", "plain");
    localStringTokenizer = new StringTokenizer(str1);
    i = localStringTokenizer.countTokens();
    for (int m = 0; m < i; m++)
    {
      String str3 = localStringTokenizer.nextToken();
      if ("plain".equalsIgnoreCase(str3)) {
        supportPlainProtocol = true;
      } else if ("ssl".equalsIgnoreCase(str3)) {
        supportSslProtocol = true;
      }
    }
    if (idleTimeout > 0L) {
      new PoolCleaner(idleTimeout, pools).start();
    }
    if (debug) {
      showStats(System.err);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\LdapPoolManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */