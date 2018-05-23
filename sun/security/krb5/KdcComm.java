package sun.security.krb5;

import java.io.IOException;
import java.io.PrintStream;
import java.net.SocketTimeoutException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import sun.security.krb5.internal.KRBError;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.NetClient;

public final class KdcComm
{
  private static int defaultKdcRetryLimit;
  private static int defaultKdcTimeout;
  private static int defaultUdpPrefLimit;
  private static final boolean DEBUG = Krb5.DEBUG;
  private static final String BAD_POLICY_KEY = "krb5.kdc.bad.policy";
  private static int tryLessMaxRetries = 1;
  private static int tryLessTimeout = 5000;
  private static BpType badPolicy;
  private String realm;
  
  public static void initStatic()
  {
    String str1 = (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        return Security.getProperty("krb5.kdc.bad.policy");
      }
    });
    if (str1 != null)
    {
      str1 = str1.toLowerCase(Locale.ENGLISH);
      String[] arrayOfString1 = str1.split(":");
      if ("tryless".equals(arrayOfString1[0]))
      {
        if (arrayOfString1.length > 1)
        {
          String[] arrayOfString2 = arrayOfString1[1].split(",");
          try
          {
            int k = Integer.parseInt(arrayOfString2[0]);
            if (arrayOfString2.length > 1) {
              tryLessTimeout = Integer.parseInt(arrayOfString2[1]);
            }
            tryLessMaxRetries = k;
          }
          catch (NumberFormatException localNumberFormatException)
          {
            if (DEBUG) {
              System.out.println("Invalid krb5.kdc.bad.policy parameter for tryLess: " + str1 + ", use default");
            }
          }
        }
        badPolicy = BpType.TRY_LESS;
      }
      else if ("trylast".equals(arrayOfString1[0]))
      {
        badPolicy = BpType.TRY_LAST;
      }
      else
      {
        badPolicy = BpType.NONE;
      }
    }
    else
    {
      badPolicy = BpType.NONE;
    }
    int i = -1;
    int j = -1;
    int m = -1;
    try
    {
      Config localConfig = Config.getInstance();
      String str2 = localConfig.get(new String[] { "libdefaults", "kdc_timeout" });
      i = parseTimeString(str2);
      str2 = localConfig.get(new String[] { "libdefaults", "max_retries" });
      j = parsePositiveIntString(str2);
      str2 = localConfig.get(new String[] { "libdefaults", "udp_preference_limit" });
      m = parsePositiveIntString(str2);
    }
    catch (Exception localException)
    {
      if (DEBUG) {
        System.out.println("Exception in getting KDC communication settings, using default value " + localException.getMessage());
      }
    }
    defaultKdcTimeout = i > 0 ? i : 30000;
    defaultKdcRetryLimit = j > 0 ? j : 3;
    if (m < 0) {
      defaultUdpPrefLimit = 1465;
    } else if (m > 32700) {
      defaultUdpPrefLimit = 32700;
    } else {
      defaultUdpPrefLimit = m;
    }
    KdcAccessibility.access$000();
  }
  
  public KdcComm(String paramString)
    throws KrbException
  {
    if (paramString == null)
    {
      paramString = Config.getInstance().getDefaultRealm();
      if (paramString == null) {
        throw new KrbException(60, "Cannot find default realm");
      }
    }
    realm = paramString;
  }
  
  public byte[] send(byte[] paramArrayOfByte)
    throws IOException, KrbException
  {
    int i = getRealmSpecificValue(realm, "udp_preference_limit", defaultUdpPrefLimit);
    boolean bool = (i > 0) && (paramArrayOfByte != null) && (paramArrayOfByte.length > i);
    return send(paramArrayOfByte, bool);
  }
  
  private byte[] send(byte[] paramArrayOfByte, boolean paramBoolean)
    throws IOException, KrbException
  {
    if (paramArrayOfByte == null) {
      return null;
    }
    Config localConfig = Config.getInstance();
    if (realm == null)
    {
      realm = localConfig.getDefaultRealm();
      if (realm == null) {
        throw new KrbException(60, "Cannot find default realm");
      }
    }
    String str = localConfig.getKDCList(realm);
    if (str == null) {
      throw new KrbException("Cannot get kdc for realm " + realm);
    }
    Iterator localIterator = KdcAccessibility.list(str).iterator();
    if (!localIterator.hasNext()) {
      throw new KrbException("Cannot get kdc for realm " + realm);
    }
    byte[] arrayOfByte = null;
    try
    {
      arrayOfByte = sendIfPossible(paramArrayOfByte, (String)localIterator.next(), paramBoolean);
    }
    catch (Exception localException1)
    {
      int i = 0;
      while (localIterator.hasNext()) {
        try
        {
          arrayOfByte = sendIfPossible(paramArrayOfByte, (String)localIterator.next(), paramBoolean);
          i = 1;
        }
        catch (Exception localException2) {}
      }
      if (i == 0) {
        throw localException1;
      }
    }
    if (arrayOfByte == null) {
      throw new IOException("Cannot get a KDC reply");
    }
    return arrayOfByte;
  }
  
  private byte[] sendIfPossible(byte[] paramArrayOfByte, String paramString, boolean paramBoolean)
    throws IOException, KrbException
  {
    try
    {
      byte[] arrayOfByte = send(paramArrayOfByte, paramString, paramBoolean);
      KRBError localKRBError = null;
      try
      {
        localKRBError = new KRBError(arrayOfByte);
      }
      catch (Exception localException2) {}
      if ((localKRBError != null) && (localKRBError.getErrorCode() == 52)) {
        arrayOfByte = send(paramArrayOfByte, paramString, true);
      }
      KdcAccessibility.removeBad(paramString);
      return arrayOfByte;
    }
    catch (Exception localException1)
    {
      if (DEBUG)
      {
        System.out.println(">>> KrbKdcReq send: error trying " + paramString);
        localException1.printStackTrace(System.out);
      }
      KdcAccessibility.addBad(paramString);
      throw localException1;
    }
  }
  
  private byte[] send(byte[] paramArrayOfByte, String paramString, boolean paramBoolean)
    throws IOException, KrbException
  {
    if (paramArrayOfByte == null) {
      return null;
    }
    int i = 88;
    int j = getRealmSpecificValue(realm, "max_retries", defaultKdcRetryLimit);
    int k = getRealmSpecificValue(realm, "kdc_timeout", defaultKdcTimeout);
    if ((badPolicy == BpType.TRY_LESS) && (KdcAccessibility.isBad(paramString)))
    {
      if (j > tryLessMaxRetries) {
        j = tryLessMaxRetries;
      }
      if (k > tryLessTimeout) {
        k = tryLessTimeout;
      }
    }
    String str1 = null;
    String str2 = null;
    int m;
    if (paramString.charAt(0) == '[')
    {
      m = paramString.indexOf(']', 1);
      if (m == -1) {
        throw new IOException("Illegal KDC: " + paramString);
      }
      str1 = paramString.substring(1, m);
      if (m != paramString.length() - 1)
      {
        if (paramString.charAt(m + 1) != ':') {
          throw new IOException("Illegal KDC: " + paramString);
        }
        str2 = paramString.substring(m + 2);
      }
    }
    else
    {
      m = paramString.indexOf(':');
      if (m == -1)
      {
        str1 = paramString;
      }
      else
      {
        int n = paramString.indexOf(':', m + 1);
        if (n > 0)
        {
          str1 = paramString;
        }
        else
        {
          str1 = paramString.substring(0, m);
          str2 = paramString.substring(m + 1);
        }
      }
    }
    if (str2 != null)
    {
      m = parsePositiveIntString(str2);
      if (m > 0) {
        i = m;
      }
    }
    if (DEBUG) {
      System.out.println(">>> KrbKdcReq send: kdc=" + str1 + (paramBoolean ? " TCP:" : " UDP:") + i + ", timeout=" + k + ", number of retries =" + j + ", #bytes=" + paramArrayOfByte.length);
    }
    KdcCommunication localKdcCommunication = new KdcCommunication(str1, i, paramBoolean, k, j, paramArrayOfByte);
    try
    {
      byte[] arrayOfByte = (byte[])AccessController.doPrivileged(localKdcCommunication);
      if (DEBUG) {
        System.out.println(">>> KrbKdcReq send: #bytes read=" + (arrayOfByte != null ? arrayOfByte.length : 0));
      }
      return arrayOfByte;
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      Exception localException = localPrivilegedActionException.getException();
      if ((localException instanceof IOException)) {
        throw ((IOException)localException);
      }
      throw ((KrbException)localException);
    }
  }
  
  private static int parseTimeString(String paramString)
  {
    if (paramString == null) {
      return -1;
    }
    if (paramString.endsWith("s"))
    {
      int i = parsePositiveIntString(paramString.substring(0, paramString.length() - 1));
      return i < 0 ? -1 : i * 1000;
    }
    return parsePositiveIntString(paramString);
  }
  
  private int getRealmSpecificValue(String paramString1, String paramString2, int paramInt)
  {
    int i = paramInt;
    if (paramString1 == null) {
      return i;
    }
    int j = -1;
    try
    {
      String str = Config.getInstance().get(new String[] { "realms", paramString1, paramString2 });
      if (paramString2.equals("kdc_timeout")) {
        j = parseTimeString(str);
      } else {
        j = parsePositiveIntString(str);
      }
    }
    catch (Exception localException) {}
    if (j > 0) {
      i = j;
    }
    return i;
  }
  
  private static int parsePositiveIntString(String paramString)
  {
    if (paramString == null) {
      return -1;
    }
    int i = -1;
    try
    {
      i = Integer.parseInt(paramString);
    }
    catch (Exception localException)
    {
      return -1;
    }
    if (i >= 0) {
      return i;
    }
    return -1;
  }
  
  static
  {
    initStatic();
  }
  
  private static enum BpType
  {
    NONE,  TRY_LAST,  TRY_LESS;
    
    private BpType() {}
  }
  
  static class KdcAccessibility
  {
    private static Set<String> bads = new HashSet();
    
    KdcAccessibility() {}
    
    private static synchronized void addBad(String paramString)
    {
      if (KdcComm.DEBUG) {
        System.out.println(">>> KdcAccessibility: add " + paramString);
      }
      bads.add(paramString);
    }
    
    private static synchronized void removeBad(String paramString)
    {
      if (KdcComm.DEBUG) {
        System.out.println(">>> KdcAccessibility: remove " + paramString);
      }
      bads.remove(paramString);
    }
    
    private static synchronized boolean isBad(String paramString)
    {
      return bads.contains(paramString);
    }
    
    private static synchronized void reset()
    {
      if (KdcComm.DEBUG) {
        System.out.println(">>> KdcAccessibility: reset");
      }
      bads.clear();
    }
    
    private static synchronized List<String> list(String paramString)
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString);
      ArrayList localArrayList1 = new ArrayList();
      if (KdcComm.badPolicy == KdcComm.BpType.TRY_LAST)
      {
        ArrayList localArrayList2 = new ArrayList();
        while (localStringTokenizer.hasMoreTokens())
        {
          String str = localStringTokenizer.nextToken();
          if (bads.contains(str)) {
            localArrayList2.add(str);
          } else {
            localArrayList1.add(str);
          }
        }
        localArrayList1.addAll(localArrayList2);
      }
      else
      {
        while (localStringTokenizer.hasMoreTokens()) {
          localArrayList1.add(localStringTokenizer.nextToken());
        }
      }
      return localArrayList1;
    }
  }
  
  private static class KdcCommunication
    implements PrivilegedExceptionAction<byte[]>
  {
    private String kdc;
    private int port;
    private boolean useTCP;
    private int timeout;
    private int retries;
    private byte[] obuf;
    
    public KdcCommunication(String paramString, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, byte[] paramArrayOfByte)
    {
      kdc = paramString;
      port = paramInt1;
      useTCP = paramBoolean;
      timeout = paramInt2;
      retries = paramInt3;
      obuf = paramArrayOfByte;
    }
    
    public byte[] run()
      throws IOException, KrbException
    {
      byte[] arrayOfByte = null;
      for (int i = 1; i <= retries; i++)
      {
        String str = useTCP ? "TCP" : "UDP";
        NetClient localNetClient = NetClient.getInstance(str, kdc, port, timeout);
        Object localObject1 = null;
        try
        {
          if (KdcComm.DEBUG) {
            System.out.println(">>> KDCCommunication: kdc=" + kdc + " " + str + ":" + port + ", timeout=" + timeout + ",Attempt =" + i + ", #bytes=" + obuf.length);
          }
          try
          {
            localNetClient.send(obuf);
            arrayOfByte = localNetClient.receive();
          }
          catch (SocketTimeoutException localSocketTimeoutException)
          {
            if (KdcComm.DEBUG) {
              System.out.println("SocketTimeOutException with attempt: " + i);
            }
            if (i == retries)
            {
              arrayOfByte = null;
              throw localSocketTimeoutException;
            }
            if (localNetClient == null) {
              continue;
            }
          }
          if (localObject1 != null) {
            try
            {
              localNetClient.close();
            }
            catch (Throwable localThrowable2)
            {
              ((Throwable)localObject1).addSuppressed(localThrowable2);
            }
          } else {
            localNetClient.close();
          }
        }
        catch (Throwable localThrowable3)
        {
          localObject1 = localThrowable3;
          throw localThrowable3;
        }
        finally
        {
          if (localNetClient != null) {
            if (localObject1 != null) {
              try
              {
                localNetClient.close();
              }
              catch (Throwable localThrowable4)
              {
                ((Throwable)localObject1).addSuppressed(localThrowable4);
              }
            } else {
              localNetClient.close();
            }
          }
        }
      }
      return arrayOfByte;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\KdcComm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */