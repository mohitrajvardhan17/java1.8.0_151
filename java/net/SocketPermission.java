package java.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.util.StringTokenizer;
import sun.net.PortConfig;
import sun.net.RegisteredDomain;
import sun.net.util.IPAddressUtil;
import sun.net.www.URLConnection;
import sun.security.action.GetBooleanAction;
import sun.security.util.Debug;

public final class SocketPermission
  extends Permission
  implements Serializable
{
  private static final long serialVersionUID = -7204263841984476862L;
  private static final int CONNECT = 1;
  private static final int LISTEN = 2;
  private static final int ACCEPT = 4;
  private static final int RESOLVE = 8;
  private static final int NONE = 0;
  private static final int ALL = 15;
  private static final int PORT_MIN = 0;
  private static final int PORT_MAX = 65535;
  private static final int PRIV_PORT_MAX = 1023;
  private static final int DEF_EPH_LOW = 49152;
  private transient int mask;
  private String actions;
  private transient String hostname;
  private transient String cname;
  private transient InetAddress[] addresses;
  private transient boolean wildcard;
  private transient boolean init_with_ip;
  private transient boolean invalid;
  private transient int[] portrange;
  private transient boolean defaultDeny = false;
  private transient boolean untrusted;
  private transient boolean trusted;
  private static boolean trustNameService;
  private static Debug debug = null;
  private static boolean debugInit = false;
  private transient String cdomain;
  private transient String hdomain;
  
  private static synchronized Debug getDebug()
  {
    if (!debugInit)
    {
      debug = Debug.getInstance("access");
      debugInit = true;
    }
    return debug;
  }
  
  public SocketPermission(String paramString1, String paramString2)
  {
    super(getHost(paramString1));
    init(getName(), getMask(paramString2));
  }
  
  SocketPermission(String paramString, int paramInt)
  {
    super(getHost(paramString));
    init(getName(), paramInt);
  }
  
  private void setDeny()
  {
    defaultDeny = true;
  }
  
  private static String getHost(String paramString)
  {
    if (paramString.equals("")) {
      return "localhost";
    }
    int i;
    if ((paramString.charAt(0) != '[') && ((i = paramString.indexOf(':')) != paramString.lastIndexOf(':')))
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ":");
      int j = localStringTokenizer.countTokens();
      if (j == 9)
      {
        i = paramString.lastIndexOf(':');
        paramString = "[" + paramString.substring(0, i) + "]" + paramString.substring(i);
      }
      else if ((j == 8) && (paramString.indexOf("::") == -1))
      {
        paramString = "[" + paramString + "]";
      }
      else
      {
        throw new IllegalArgumentException("Ambiguous hostport part");
      }
    }
    return paramString;
  }
  
  private int[] parsePort(String paramString)
    throws Exception
  {
    if ((paramString == null) || (paramString.equals("")) || (paramString.equals("*"))) {
      return new int[] { 0, 65535 };
    }
    int i = paramString.indexOf('-');
    if (i == -1)
    {
      int j = Integer.parseInt(paramString);
      return new int[] { j, j };
    }
    String str1 = paramString.substring(0, i);
    String str2 = paramString.substring(i + 1);
    int k;
    if (str1.equals("")) {
      k = 0;
    } else {
      k = Integer.parseInt(str1);
    }
    int m;
    if (str2.equals("")) {
      m = 65535;
    } else {
      m = Integer.parseInt(str2);
    }
    if ((k < 0) || (m < 0) || (m < k)) {
      throw new IllegalArgumentException("invalid port range");
    }
    return new int[] { k, m };
  }
  
  private boolean includesEphemerals()
  {
    return portrange[0] == 0;
  }
  
  private void init(String paramString, int paramInt)
  {
    if ((paramInt & 0xF) != paramInt) {
      throw new IllegalArgumentException("invalid actions mask");
    }
    mask = (paramInt | 0x8);
    int i = 0;
    int j = 0;
    int k = 0;
    int m = -1;
    String str1 = paramString;
    if (paramString.charAt(0) == '[')
    {
      j = 1;
      i = paramString.indexOf(']');
      if (i != -1) {
        paramString = paramString.substring(j, i);
      } else {
        throw new IllegalArgumentException("invalid host/port: " + paramString);
      }
      m = str1.indexOf(':', i + 1);
    }
    else
    {
      j = 0;
      m = paramString.indexOf(':', i);
      k = m;
      if (m != -1) {
        paramString = paramString.substring(j, k);
      }
    }
    if (m != -1)
    {
      String str2 = str1.substring(m + 1);
      try
      {
        portrange = parsePort(str2);
      }
      catch (Exception localException)
      {
        throw new IllegalArgumentException("invalid port range: " + str2);
      }
    }
    else
    {
      portrange = new int[] { 0, 65535 };
    }
    hostname = paramString;
    if (paramString.lastIndexOf('*') > 0) {
      throw new IllegalArgumentException("invalid host wildcard specification");
    }
    if (paramString.startsWith("*"))
    {
      wildcard = true;
      if (paramString.equals("*")) {
        cname = "";
      } else if (paramString.startsWith("*.")) {
        cname = paramString.substring(1).toLowerCase();
      } else {
        throw new IllegalArgumentException("invalid host wildcard specification");
      }
      return;
    }
    if (paramString.length() > 0)
    {
      char c = paramString.charAt(0);
      if ((c == ':') || (Character.digit(c, 16) != -1))
      {
        byte[] arrayOfByte = IPAddressUtil.textToNumericFormatV4(paramString);
        if (arrayOfByte == null) {
          arrayOfByte = IPAddressUtil.textToNumericFormatV6(paramString);
        }
        if (arrayOfByte != null) {
          try
          {
            addresses = new InetAddress[] { InetAddress.getByAddress(arrayOfByte) };
            init_with_ip = true;
          }
          catch (UnknownHostException localUnknownHostException)
          {
            invalid = true;
          }
        }
      }
    }
  }
  
  private static int getMask(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("action can't be null");
    }
    if (paramString.equals("")) {
      throw new IllegalArgumentException("action can't be empty");
    }
    int i = 0;
    if (paramString == "resolve") {
      return 8;
    }
    if (paramString == "connect") {
      return 1;
    }
    if (paramString == "listen") {
      return 2;
    }
    if (paramString == "accept") {
      return 4;
    }
    if (paramString == "connect,accept") {
      return 5;
    }
    char[] arrayOfChar = paramString.toCharArray();
    int j = arrayOfChar.length - 1;
    if (j < 0) {
      return i;
    }
    while (j != -1)
    {
      int k;
      while ((j != -1) && (((k = arrayOfChar[j]) == ' ') || (k == 13) || (k == 10) || (k == 12) || (k == 9))) {
        j--;
      }
      int m;
      if ((j >= 6) && ((arrayOfChar[(j - 6)] == 'c') || (arrayOfChar[(j - 6)] == 'C')) && ((arrayOfChar[(j - 5)] == 'o') || (arrayOfChar[(j - 5)] == 'O')) && ((arrayOfChar[(j - 4)] == 'n') || (arrayOfChar[(j - 4)] == 'N')) && ((arrayOfChar[(j - 3)] == 'n') || (arrayOfChar[(j - 3)] == 'N')) && ((arrayOfChar[(j - 2)] == 'e') || (arrayOfChar[(j - 2)] == 'E')) && ((arrayOfChar[(j - 1)] == 'c') || (arrayOfChar[(j - 1)] == 'C')) && ((arrayOfChar[j] == 't') || (arrayOfChar[j] == 'T')))
      {
        m = 7;
        i |= 0x1;
      }
      else if ((j >= 6) && ((arrayOfChar[(j - 6)] == 'r') || (arrayOfChar[(j - 6)] == 'R')) && ((arrayOfChar[(j - 5)] == 'e') || (arrayOfChar[(j - 5)] == 'E')) && ((arrayOfChar[(j - 4)] == 's') || (arrayOfChar[(j - 4)] == 'S')) && ((arrayOfChar[(j - 3)] == 'o') || (arrayOfChar[(j - 3)] == 'O')) && ((arrayOfChar[(j - 2)] == 'l') || (arrayOfChar[(j - 2)] == 'L')) && ((arrayOfChar[(j - 1)] == 'v') || (arrayOfChar[(j - 1)] == 'V')) && ((arrayOfChar[j] == 'e') || (arrayOfChar[j] == 'E')))
      {
        m = 7;
        i |= 0x8;
      }
      else if ((j >= 5) && ((arrayOfChar[(j - 5)] == 'l') || (arrayOfChar[(j - 5)] == 'L')) && ((arrayOfChar[(j - 4)] == 'i') || (arrayOfChar[(j - 4)] == 'I')) && ((arrayOfChar[(j - 3)] == 's') || (arrayOfChar[(j - 3)] == 'S')) && ((arrayOfChar[(j - 2)] == 't') || (arrayOfChar[(j - 2)] == 'T')) && ((arrayOfChar[(j - 1)] == 'e') || (arrayOfChar[(j - 1)] == 'E')) && ((arrayOfChar[j] == 'n') || (arrayOfChar[j] == 'N')))
      {
        m = 6;
        i |= 0x2;
      }
      else if ((j >= 5) && ((arrayOfChar[(j - 5)] == 'a') || (arrayOfChar[(j - 5)] == 'A')) && ((arrayOfChar[(j - 4)] == 'c') || (arrayOfChar[(j - 4)] == 'C')) && ((arrayOfChar[(j - 3)] == 'c') || (arrayOfChar[(j - 3)] == 'C')) && ((arrayOfChar[(j - 2)] == 'e') || (arrayOfChar[(j - 2)] == 'E')) && ((arrayOfChar[(j - 1)] == 'p') || (arrayOfChar[(j - 1)] == 'P')) && ((arrayOfChar[j] == 't') || (arrayOfChar[j] == 'T')))
      {
        m = 6;
        i |= 0x4;
      }
      else
      {
        throw new IllegalArgumentException("invalid permission: " + paramString);
      }
      int n = 0;
      while ((j >= m) && (n == 0))
      {
        switch (arrayOfChar[(j - m)])
        {
        case ',': 
          n = 1;
          break;
        case '\t': 
        case '\n': 
        case '\f': 
        case '\r': 
        case ' ': 
          break;
        default: 
          throw new IllegalArgumentException("invalid permission: " + paramString);
        }
        j--;
      }
      j -= m;
    }
    return i;
  }
  
  private boolean isUntrusted()
    throws UnknownHostException
  {
    if (trusted) {
      return false;
    }
    if ((invalid) || (untrusted)) {
      return true;
    }
    try
    {
      if ((!trustNameService) && ((defaultDeny) || (URLConnection.isProxiedHost(hostname))))
      {
        if (cname == null) {
          getCanonName();
        }
        if ((!match(cname, hostname)) && (!authorized(hostname, addresses[0].getAddress())))
        {
          untrusted = true;
          Debug localDebug = getDebug();
          if ((localDebug != null) && (Debug.isOn("failure"))) {
            localDebug.println("socket access restriction: proxied host (" + addresses[0] + ") does not match " + cname + " from reverse lookup");
          }
          return true;
        }
        trusted = true;
      }
    }
    catch (UnknownHostException localUnknownHostException)
    {
      invalid = true;
      throw localUnknownHostException;
    }
    return false;
  }
  
  void getCanonName()
    throws UnknownHostException
  {
    if ((cname != null) || (invalid) || (untrusted)) {
      return;
    }
    try
    {
      if (addresses == null) {
        getIP();
      }
      if (init_with_ip) {
        cname = addresses[0].getHostName(false).toLowerCase();
      } else {
        cname = InetAddress.getByName(addresses[0].getHostAddress()).getHostName(false).toLowerCase();
      }
    }
    catch (UnknownHostException localUnknownHostException)
    {
      invalid = true;
      throw localUnknownHostException;
    }
  }
  
  private boolean match(String paramString1, String paramString2)
  {
    String str1 = paramString1.toLowerCase();
    String str2 = paramString2.toLowerCase();
    if ((str1.startsWith(str2)) && ((str1.length() == str2.length()) || (str1.charAt(str2.length()) == '.'))) {
      return true;
    }
    if (cdomain == null) {
      cdomain = RegisteredDomain.getRegisteredDomain(str1);
    }
    if (hdomain == null) {
      hdomain = RegisteredDomain.getRegisteredDomain(str2);
    }
    return (cdomain.length() != 0) && (hdomain.length() != 0) && (cdomain.equals(hdomain));
  }
  
  private boolean authorized(String paramString, byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte.length == 4) {
      return authorizedIPv4(paramString, paramArrayOfByte);
    }
    if (paramArrayOfByte.length == 16) {
      return authorizedIPv6(paramString, paramArrayOfByte);
    }
    return false;
  }
  
  private boolean authorizedIPv4(String paramString, byte[] paramArrayOfByte)
  {
    String str = "";
    try
    {
      str = "auth." + (paramArrayOfByte[3] & 0xFF) + "." + (paramArrayOfByte[2] & 0xFF) + "." + (paramArrayOfByte[1] & 0xFF) + "." + (paramArrayOfByte[0] & 0xFF) + ".in-addr.arpa";
      str = hostname + '.' + str;
      InetAddress localInetAddress = InetAddress.getAllByName0(str, false)[0];
      if (localInetAddress.equals(InetAddress.getByAddress(paramArrayOfByte))) {
        return true;
      }
      Debug localDebug1 = getDebug();
      if ((localDebug1 != null) && (Debug.isOn("failure"))) {
        localDebug1.println("socket access restriction: IP address of " + localInetAddress + " != " + InetAddress.getByAddress(paramArrayOfByte));
      }
    }
    catch (UnknownHostException localUnknownHostException)
    {
      Debug localDebug2 = getDebug();
      if ((localDebug2 != null) && (Debug.isOn("failure"))) {
        localDebug2.println("socket access restriction: forward lookup failed for " + str);
      }
    }
    return false;
  }
  
  private boolean authorizedIPv6(String paramString, byte[] paramArrayOfByte)
  {
    String str = "";
    try
    {
      StringBuffer localStringBuffer = new StringBuffer(39);
      for (int i = 15; i >= 0; i--)
      {
        localStringBuffer.append(Integer.toHexString(paramArrayOfByte[i] & 0xF));
        localStringBuffer.append('.');
        localStringBuffer.append(Integer.toHexString(paramArrayOfByte[i] >> 4 & 0xF));
        localStringBuffer.append('.');
      }
      str = "auth." + localStringBuffer.toString() + "IP6.ARPA";
      str = hostname + '.' + str;
      InetAddress localInetAddress = InetAddress.getAllByName0(str, false)[0];
      if (localInetAddress.equals(InetAddress.getByAddress(paramArrayOfByte))) {
        return true;
      }
      localDebug = getDebug();
      if ((localDebug != null) && (Debug.isOn("failure"))) {
        localDebug.println("socket access restriction: IP address of " + localInetAddress + " != " + InetAddress.getByAddress(paramArrayOfByte));
      }
    }
    catch (UnknownHostException localUnknownHostException)
    {
      Debug localDebug = getDebug();
      if ((localDebug != null) && (Debug.isOn("failure"))) {
        localDebug.println("socket access restriction: forward lookup failed for " + str);
      }
    }
    return false;
  }
  
  void getIP()
    throws UnknownHostException
  {
    if ((addresses != null) || (wildcard) || (invalid)) {
      return;
    }
    try
    {
      String str;
      if (getName().charAt(0) == '[')
      {
        str = getName().substring(1, getName().indexOf(']'));
      }
      else
      {
        int i = getName().indexOf(":");
        if (i == -1) {
          str = getName();
        } else {
          str = getName().substring(0, i);
        }
      }
      addresses = new InetAddress[] { InetAddress.getAllByName0(str, false)[0] };
    }
    catch (UnknownHostException localUnknownHostException)
    {
      invalid = true;
      throw localUnknownHostException;
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      invalid = true;
      throw new UnknownHostException(getName());
    }
  }
  
  public boolean implies(Permission paramPermission)
  {
    if (!(paramPermission instanceof SocketPermission)) {
      return false;
    }
    if (paramPermission == this) {
      return true;
    }
    SocketPermission localSocketPermission = (SocketPermission)paramPermission;
    return ((mask & mask) == mask) && (impliesIgnoreMask(localSocketPermission));
  }
  
  boolean impliesIgnoreMask(SocketPermission paramSocketPermission)
  {
    if (((mask & 0x8) != mask) && ((portrange[0] < portrange[0]) || (portrange[1] > portrange[1]))) {
      if ((includesEphemerals()) || (paramSocketPermission.includesEphemerals()))
      {
        if (!inRange(portrange[0], portrange[1], portrange[0], portrange[1])) {
          return false;
        }
      }
      else {
        return false;
      }
    }
    if ((wildcard) && ("".equals(cname))) {
      return true;
    }
    if ((invalid) || (invalid)) {
      return compareHostnames(paramSocketPermission);
    }
    try
    {
      int i;
      if (init_with_ip)
      {
        if (wildcard) {
          return false;
        }
        if (init_with_ip) {
          return addresses[0].equals(addresses[0]);
        }
        if (addresses == null) {
          paramSocketPermission.getIP();
        }
        for (i = 0; i < addresses.length; i++) {
          if (addresses[0].equals(addresses[i])) {
            return true;
          }
        }
        return false;
      }
      if ((wildcard) || (wildcard))
      {
        if ((wildcard) && (wildcard)) {
          return cname.endsWith(cname);
        }
        if (wildcard) {
          return false;
        }
        if (cname == null) {
          paramSocketPermission.getCanonName();
        }
        return cname.endsWith(cname);
      }
      if (addresses == null) {
        getIP();
      }
      if (addresses == null) {
        paramSocketPermission.getIP();
      }
      if ((!init_with_ip) || (!isUntrusted()))
      {
        for (int j = 0; j < addresses.length; j++) {
          for (i = 0; i < addresses.length; i++) {
            if (addresses[j].equals(addresses[i])) {
              return true;
            }
          }
        }
        if (cname == null) {
          getCanonName();
        }
        if (cname == null) {
          paramSocketPermission.getCanonName();
        }
        return cname.equalsIgnoreCase(cname);
      }
    }
    catch (UnknownHostException localUnknownHostException)
    {
      return compareHostnames(paramSocketPermission);
    }
    return false;
  }
  
  private boolean compareHostnames(SocketPermission paramSocketPermission)
  {
    String str1 = hostname;
    String str2 = hostname;
    if (str1 == null) {
      return false;
    }
    if (wildcard)
    {
      int i = cname.length();
      return str2.regionMatches(true, str2.length() - i, cname, 0, i);
    }
    return str1.equalsIgnoreCase(str2);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof SocketPermission)) {
      return false;
    }
    SocketPermission localSocketPermission = (SocketPermission)paramObject;
    if (mask != mask) {
      return false;
    }
    if (((mask & 0x8) != mask) && ((portrange[0] != portrange[0]) || (portrange[1] != portrange[1]))) {
      return false;
    }
    if (getName().equalsIgnoreCase(localSocketPermission.getName())) {
      return true;
    }
    try
    {
      getCanonName();
      localSocketPermission.getCanonName();
    }
    catch (UnknownHostException localUnknownHostException)
    {
      return false;
    }
    if ((invalid) || (invalid)) {
      return false;
    }
    if (cname != null) {
      return cname.equalsIgnoreCase(cname);
    }
    return false;
  }
  
  public int hashCode()
  {
    if ((init_with_ip) || (wildcard)) {
      return getName().hashCode();
    }
    try
    {
      getCanonName();
    }
    catch (UnknownHostException localUnknownHostException) {}
    if ((invalid) || (cname == null)) {
      return getName().hashCode();
    }
    return cname.hashCode();
  }
  
  int getMask()
  {
    return mask;
  }
  
  private static String getActions(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    if ((paramInt & 0x1) == 1)
    {
      i = 1;
      localStringBuilder.append("connect");
    }
    if ((paramInt & 0x2) == 2)
    {
      if (i != 0) {
        localStringBuilder.append(',');
      } else {
        i = 1;
      }
      localStringBuilder.append("listen");
    }
    if ((paramInt & 0x4) == 4)
    {
      if (i != 0) {
        localStringBuilder.append(',');
      } else {
        i = 1;
      }
      localStringBuilder.append("accept");
    }
    if ((paramInt & 0x8) == 8)
    {
      if (i != 0) {
        localStringBuilder.append(',');
      } else {
        i = 1;
      }
      localStringBuilder.append("resolve");
    }
    return localStringBuilder.toString();
  }
  
  public String getActions()
  {
    if (actions == null) {
      actions = getActions(mask);
    }
    return actions;
  }
  
  public PermissionCollection newPermissionCollection()
  {
    return new SocketPermissionCollection();
  }
  
  private synchronized void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (actions == null) {
      getActions();
    }
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private synchronized void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    init(getName(), getMask(actions));
  }
  
  private static int initEphemeralPorts(String paramString, int paramInt)
  {
    ((Integer)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Integer run()
      {
        int i = Integer.getInteger("jdk.net.ephemeralPortRange." + val$suffix, -1).intValue();
        if (i != -1) {
          return Integer.valueOf(i);
        }
        return Integer.valueOf(val$suffix.equals("low") ? PortConfig.getLower() : PortConfig.getUpper());
      }
    })).intValue();
  }
  
  private static boolean inRange(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = EphemeralRange.low;
    int j = EphemeralRange.high;
    if (paramInt3 == 0)
    {
      if (!inRange(paramInt1, paramInt2, i, j)) {
        return false;
      }
      if (paramInt4 == 0) {
        return true;
      }
      paramInt3 = 1;
    }
    if ((paramInt1 == 0) && (paramInt2 == 0)) {
      return (paramInt3 >= i) && (paramInt4 <= j);
    }
    if (paramInt1 != 0) {
      return (paramInt3 >= paramInt1) && (paramInt4 <= paramInt2);
    }
    if (paramInt2 >= i - 1) {
      return paramInt4 <= j;
    }
    return ((paramInt3 <= paramInt2) && (paramInt4 <= paramInt2)) || ((paramInt3 >= i) && (paramInt4 <= j));
  }
  
  static
  {
    Boolean localBoolean = (Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.net.trustNameService"));
    trustNameService = localBoolean.booleanValue();
  }
  
  private static class EphemeralRange
  {
    static final int low = SocketPermission.initEphemeralPorts("low", 49152);
    static final int high = SocketPermission.initEphemeralPorts("high", 65535);
    
    private EphemeralRange() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\SocketPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */