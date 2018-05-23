package sun.awt;

import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;

public class OSInfo
{
  public static final WindowsVersion WINDOWS_UNKNOWN = new WindowsVersion(-1, -1, null);
  public static final WindowsVersion WINDOWS_95 = new WindowsVersion(4, 0, null);
  public static final WindowsVersion WINDOWS_98 = new WindowsVersion(4, 10, null);
  public static final WindowsVersion WINDOWS_ME = new WindowsVersion(4, 90, null);
  public static final WindowsVersion WINDOWS_2000 = new WindowsVersion(5, 0, null);
  public static final WindowsVersion WINDOWS_XP = new WindowsVersion(5, 1, null);
  public static final WindowsVersion WINDOWS_2003 = new WindowsVersion(5, 2, null);
  public static final WindowsVersion WINDOWS_VISTA = new WindowsVersion(6, 0, null);
  private static final String OS_NAME = "os.name";
  private static final String OS_VERSION = "os.version";
  private static final Map<String, WindowsVersion> windowsVersionMap = new HashMap();
  private static final PrivilegedAction<OSType> osTypeAction = new PrivilegedAction()
  {
    public OSInfo.OSType run()
    {
      return OSInfo.getOSType();
    }
  };
  
  private OSInfo() {}
  
  public static OSType getOSType()
    throws SecurityException
  {
    String str = System.getProperty("os.name");
    if (str != null)
    {
      if (str.contains("Windows")) {
        return OSType.WINDOWS;
      }
      if (str.contains("Linux")) {
        return OSType.LINUX;
      }
      if ((str.contains("Solaris")) || (str.contains("SunOS"))) {
        return OSType.SOLARIS;
      }
      if (str.contains("OS X")) {
        return OSType.MACOSX;
      }
    }
    return OSType.UNKNOWN;
  }
  
  public static PrivilegedAction<OSType> getOSTypeAction()
  {
    return osTypeAction;
  }
  
  public static WindowsVersion getWindowsVersion()
    throws SecurityException
  {
    String str = System.getProperty("os.version");
    if (str == null) {
      return WINDOWS_UNKNOWN;
    }
    synchronized (windowsVersionMap)
    {
      WindowsVersion localWindowsVersion = (WindowsVersion)windowsVersionMap.get(str);
      if (localWindowsVersion == null)
      {
        String[] arrayOfString = str.split("\\.");
        if (arrayOfString.length == 2) {
          try
          {
            localWindowsVersion = new WindowsVersion(Integer.parseInt(arrayOfString[0]), Integer.parseInt(arrayOfString[1]), null);
          }
          catch (NumberFormatException localNumberFormatException)
          {
            return WINDOWS_UNKNOWN;
          }
        } else {
          return WINDOWS_UNKNOWN;
        }
        windowsVersionMap.put(str, localWindowsVersion);
      }
      return localWindowsVersion;
    }
  }
  
  static
  {
    windowsVersionMap.put(WINDOWS_95.toString(), WINDOWS_95);
    windowsVersionMap.put(WINDOWS_98.toString(), WINDOWS_98);
    windowsVersionMap.put(WINDOWS_ME.toString(), WINDOWS_ME);
    windowsVersionMap.put(WINDOWS_2000.toString(), WINDOWS_2000);
    windowsVersionMap.put(WINDOWS_XP.toString(), WINDOWS_XP);
    windowsVersionMap.put(WINDOWS_2003.toString(), WINDOWS_2003);
    windowsVersionMap.put(WINDOWS_VISTA.toString(), WINDOWS_VISTA);
  }
  
  public static enum OSType
  {
    WINDOWS,  LINUX,  SOLARIS,  MACOSX,  UNKNOWN;
    
    private OSType() {}
  }
  
  public static class WindowsVersion
    implements Comparable<WindowsVersion>
  {
    private final int major;
    private final int minor;
    
    private WindowsVersion(int paramInt1, int paramInt2)
    {
      major = paramInt1;
      minor = paramInt2;
    }
    
    public int getMajor()
    {
      return major;
    }
    
    public int getMinor()
    {
      return minor;
    }
    
    public int compareTo(WindowsVersion paramWindowsVersion)
    {
      int i = major - paramWindowsVersion.getMajor();
      if (i == 0) {
        i = minor - paramWindowsVersion.getMinor();
      }
      return i;
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof WindowsVersion)) && (compareTo((WindowsVersion)paramObject) == 0);
    }
    
    public int hashCode()
    {
      return 31 * major + minor;
    }
    
    public String toString()
    {
      return major + "." + minor;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\OSInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */