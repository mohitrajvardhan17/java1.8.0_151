package java.util.prefs;

import java.io.ByteArrayOutputStream;
import java.util.StringTokenizer;
import sun.util.logging.PlatformLogger;

class WindowsPreferences
  extends AbstractPreferences
{
  private static PlatformLogger logger;
  private static final byte[] WINDOWS_ROOT_PATH = stringToByteArray("Software\\JavaSoft\\Prefs");
  private static final int HKEY_CURRENT_USER = -2147483647;
  private static final int HKEY_LOCAL_MACHINE = -2147483646;
  private static final int USER_ROOT_NATIVE_HANDLE = -2147483647;
  private static final int SYSTEM_ROOT_NATIVE_HANDLE = -2147483646;
  private static final int MAX_WINDOWS_PATH_LENGTH = 256;
  static final Preferences userRoot = new WindowsPreferences(-2147483647, WINDOWS_ROOT_PATH);
  static final Preferences systemRoot = new WindowsPreferences(-2147483646, WINDOWS_ROOT_PATH);
  private static final int ERROR_SUCCESS = 0;
  private static final int ERROR_FILE_NOT_FOUND = 2;
  private static final int ERROR_ACCESS_DENIED = 5;
  private static final int NATIVE_HANDLE = 0;
  private static final int ERROR_CODE = 1;
  private static final int SUBKEYS_NUMBER = 0;
  private static final int VALUES_NUMBER = 2;
  private static final int MAX_KEY_LENGTH = 3;
  private static final int MAX_VALUE_NAME_LENGTH = 4;
  private static final int DISPOSITION = 2;
  private static final int REG_CREATED_NEW_KEY = 1;
  private static final int REG_OPENED_EXISTING_KEY = 2;
  private static final int NULL_NATIVE_HANDLE = 0;
  private static final int DELETE = 65536;
  private static final int KEY_QUERY_VALUE = 1;
  private static final int KEY_SET_VALUE = 2;
  private static final int KEY_CREATE_SUB_KEY = 4;
  private static final int KEY_ENUMERATE_SUB_KEYS = 8;
  private static final int KEY_READ = 131097;
  private static final int KEY_WRITE = 131078;
  private static final int KEY_ALL_ACCESS = 983103;
  private static int INIT_SLEEP_TIME = 50;
  private static int MAX_ATTEMPTS = 5;
  private boolean isBackingStoreAvailable = true;
  
  private static native int[] WindowsRegOpenKey(int paramInt1, byte[] paramArrayOfByte, int paramInt2);
  
  private static int[] WindowsRegOpenKey1(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
  {
    int[] arrayOfInt = WindowsRegOpenKey(paramInt1, paramArrayOfByte, paramInt2);
    if (arrayOfInt[1] == 0) {
      return arrayOfInt;
    }
    if (arrayOfInt[1] == 2)
    {
      logger().warning("Trying to recreate Windows registry node " + byteArrayToString(paramArrayOfByte) + " at root 0x" + Integer.toHexString(paramInt1) + ".");
      int i = WindowsRegCreateKeyEx(paramInt1, paramArrayOfByte)[0];
      WindowsRegCloseKey(i);
      return WindowsRegOpenKey(paramInt1, paramArrayOfByte, paramInt2);
    }
    if (arrayOfInt[1] != 5)
    {
      long l = INIT_SLEEP_TIME;
      for (int j = 0; j < MAX_ATTEMPTS; j++)
      {
        try
        {
          Thread.sleep(l);
        }
        catch (InterruptedException localInterruptedException)
        {
          return arrayOfInt;
        }
        l *= 2L;
        arrayOfInt = WindowsRegOpenKey(paramInt1, paramArrayOfByte, paramInt2);
        if (arrayOfInt[1] == 0) {
          return arrayOfInt;
        }
      }
    }
    return arrayOfInt;
  }
  
  private static native int WindowsRegCloseKey(int paramInt);
  
  private static native int[] WindowsRegCreateKeyEx(int paramInt, byte[] paramArrayOfByte);
  
  private static int[] WindowsRegCreateKeyEx1(int paramInt, byte[] paramArrayOfByte)
  {
    int[] arrayOfInt = WindowsRegCreateKeyEx(paramInt, paramArrayOfByte);
    if (arrayOfInt[1] == 0) {
      return arrayOfInt;
    }
    long l = INIT_SLEEP_TIME;
    for (int i = 0; i < MAX_ATTEMPTS; i++)
    {
      try
      {
        Thread.sleep(l);
      }
      catch (InterruptedException localInterruptedException)
      {
        return arrayOfInt;
      }
      l *= 2L;
      arrayOfInt = WindowsRegCreateKeyEx(paramInt, paramArrayOfByte);
      if (arrayOfInt[1] == 0) {
        return arrayOfInt;
      }
    }
    return arrayOfInt;
  }
  
  private static native int WindowsRegDeleteKey(int paramInt, byte[] paramArrayOfByte);
  
  private static native int WindowsRegFlushKey(int paramInt);
  
  private static int WindowsRegFlushKey1(int paramInt)
  {
    int i = WindowsRegFlushKey(paramInt);
    if (i == 0) {
      return i;
    }
    long l = INIT_SLEEP_TIME;
    for (int j = 0; j < MAX_ATTEMPTS; j++)
    {
      try
      {
        Thread.sleep(l);
      }
      catch (InterruptedException localInterruptedException)
      {
        return i;
      }
      l *= 2L;
      i = WindowsRegFlushKey(paramInt);
      if (i == 0) {
        return i;
      }
    }
    return i;
  }
  
  private static native byte[] WindowsRegQueryValueEx(int paramInt, byte[] paramArrayOfByte);
  
  private static native int WindowsRegSetValueEx(int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
  
  private static int WindowsRegSetValueEx1(int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    int i = WindowsRegSetValueEx(paramInt, paramArrayOfByte1, paramArrayOfByte2);
    if (i == 0) {
      return i;
    }
    long l = INIT_SLEEP_TIME;
    for (int j = 0; j < MAX_ATTEMPTS; j++)
    {
      try
      {
        Thread.sleep(l);
      }
      catch (InterruptedException localInterruptedException)
      {
        return i;
      }
      l *= 2L;
      i = WindowsRegSetValueEx(paramInt, paramArrayOfByte1, paramArrayOfByte2);
      if (i == 0) {
        return i;
      }
    }
    return i;
  }
  
  private static native int WindowsRegDeleteValue(int paramInt, byte[] paramArrayOfByte);
  
  private static native int[] WindowsRegQueryInfoKey(int paramInt);
  
  private static int[] WindowsRegQueryInfoKey1(int paramInt)
  {
    int[] arrayOfInt = WindowsRegQueryInfoKey(paramInt);
    if (arrayOfInt[1] == 0) {
      return arrayOfInt;
    }
    long l = INIT_SLEEP_TIME;
    for (int i = 0; i < MAX_ATTEMPTS; i++)
    {
      try
      {
        Thread.sleep(l);
      }
      catch (InterruptedException localInterruptedException)
      {
        return arrayOfInt;
      }
      l *= 2L;
      arrayOfInt = WindowsRegQueryInfoKey(paramInt);
      if (arrayOfInt[1] == 0) {
        return arrayOfInt;
      }
    }
    return arrayOfInt;
  }
  
  private static native byte[] WindowsRegEnumKeyEx(int paramInt1, int paramInt2, int paramInt3);
  
  private static byte[] WindowsRegEnumKeyEx1(int paramInt1, int paramInt2, int paramInt3)
  {
    byte[] arrayOfByte = WindowsRegEnumKeyEx(paramInt1, paramInt2, paramInt3);
    if (arrayOfByte != null) {
      return arrayOfByte;
    }
    long l = INIT_SLEEP_TIME;
    for (int i = 0; i < MAX_ATTEMPTS; i++)
    {
      try
      {
        Thread.sleep(l);
      }
      catch (InterruptedException localInterruptedException)
      {
        return arrayOfByte;
      }
      l *= 2L;
      arrayOfByte = WindowsRegEnumKeyEx(paramInt1, paramInt2, paramInt3);
      if (arrayOfByte != null) {
        return arrayOfByte;
      }
    }
    return arrayOfByte;
  }
  
  private static native byte[] WindowsRegEnumValue(int paramInt1, int paramInt2, int paramInt3);
  
  private static byte[] WindowsRegEnumValue1(int paramInt1, int paramInt2, int paramInt3)
  {
    byte[] arrayOfByte = WindowsRegEnumValue(paramInt1, paramInt2, paramInt3);
    if (arrayOfByte != null) {
      return arrayOfByte;
    }
    long l = INIT_SLEEP_TIME;
    for (int i = 0; i < MAX_ATTEMPTS; i++)
    {
      try
      {
        Thread.sleep(l);
      }
      catch (InterruptedException localInterruptedException)
      {
        return arrayOfByte;
      }
      l *= 2L;
      arrayOfByte = WindowsRegEnumValue(paramInt1, paramInt2, paramInt3);
      if (arrayOfByte != null) {
        return arrayOfByte;
      }
    }
    return arrayOfByte;
  }
  
  private WindowsPreferences(WindowsPreferences paramWindowsPreferences, String paramString)
  {
    super(paramWindowsPreferences, paramString);
    int i = paramWindowsPreferences.openKey(4, 131097);
    if (i == 0)
    {
      isBackingStoreAvailable = false;
      return;
    }
    int[] arrayOfInt = WindowsRegCreateKeyEx1(i, toWindowsName(paramString));
    if (arrayOfInt[1] != 0)
    {
      logger().warning("Could not create windows registry node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ". Windows RegCreateKeyEx(...) returned error code " + arrayOfInt[1] + ".");
      isBackingStoreAvailable = false;
      return;
    }
    newNode = (arrayOfInt[2] == 1);
    closeKey(i);
    closeKey(arrayOfInt[0]);
  }
  
  private WindowsPreferences(int paramInt, byte[] paramArrayOfByte)
  {
    super(null, "");
    int[] arrayOfInt = WindowsRegCreateKeyEx1(paramInt, paramArrayOfByte);
    if (arrayOfInt[1] != 0)
    {
      logger().warning("Could not open/create prefs root node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ". Windows RegCreateKeyEx(...) returned error code " + arrayOfInt[1] + ".");
      isBackingStoreAvailable = false;
      return;
    }
    newNode = (arrayOfInt[2] == 1);
    closeKey(arrayOfInt[0]);
  }
  
  private byte[] windowsAbsolutePath()
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    localByteArrayOutputStream.write(WINDOWS_ROOT_PATH, 0, WINDOWS_ROOT_PATH.length - 1);
    StringTokenizer localStringTokenizer = new StringTokenizer(absolutePath(), "/");
    while (localStringTokenizer.hasMoreTokens())
    {
      localByteArrayOutputStream.write(92);
      String str = localStringTokenizer.nextToken();
      byte[] arrayOfByte = toWindowsName(str);
      localByteArrayOutputStream.write(arrayOfByte, 0, arrayOfByte.length - 1);
    }
    localByteArrayOutputStream.write(0);
    return localByteArrayOutputStream.toByteArray();
  }
  
  private int openKey(int paramInt)
  {
    return openKey(paramInt, paramInt);
  }
  
  private int openKey(int paramInt1, int paramInt2)
  {
    return openKey(windowsAbsolutePath(), paramInt1, paramInt2);
  }
  
  private int openKey(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramArrayOfByte.length <= 257)
    {
      int[] arrayOfInt = WindowsRegOpenKey1(rootNativeHandle(), paramArrayOfByte, paramInt1);
      if ((arrayOfInt[1] == 5) && (paramInt2 != paramInt1)) {
        arrayOfInt = WindowsRegOpenKey1(rootNativeHandle(), paramArrayOfByte, paramInt2);
      }
      if (arrayOfInt[1] != 0)
      {
        logger().warning("Could not open windows registry node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ". Windows RegOpenKey(...) returned error code " + arrayOfInt[1] + ".");
        arrayOfInt[0] = 0;
        if (arrayOfInt[1] == 5) {
          throw new SecurityException("Could not open windows registry node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ": Access denied");
        }
      }
      return arrayOfInt[0];
    }
    return openKey(rootNativeHandle(), paramArrayOfByte, paramInt1, paramInt2);
  }
  
  private int openKey(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
  {
    if (paramArrayOfByte.length <= 257)
    {
      int[] arrayOfInt = WindowsRegOpenKey1(paramInt1, paramArrayOfByte, paramInt2);
      if ((arrayOfInt[1] == 5) && (paramInt3 != paramInt2)) {
        arrayOfInt = WindowsRegOpenKey1(paramInt1, paramArrayOfByte, paramInt3);
      }
      if (arrayOfInt[1] != 0)
      {
        logger().warning("Could not open windows registry node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(paramInt1) + ". Windows RegOpenKey(...) returned error code " + arrayOfInt[1] + ".");
        arrayOfInt[0] = 0;
      }
      return arrayOfInt[0];
    }
    int i = -1;
    for (int j = 256; j > 0; j--) {
      if (paramArrayOfByte[j] == 92)
      {
        i = j;
        break;
      }
    }
    byte[] arrayOfByte1 = new byte[i + 1];
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte1, 0, i);
    arrayOfByte1[i] = 0;
    byte[] arrayOfByte2 = new byte[paramArrayOfByte.length - i - 1];
    System.arraycopy(paramArrayOfByte, i + 1, arrayOfByte2, 0, arrayOfByte2.length);
    int k = openKey(paramInt1, arrayOfByte1, paramInt2, paramInt3);
    if (k == 0) {
      return 0;
    }
    int m = openKey(k, arrayOfByte2, paramInt2, paramInt3);
    closeKey(k);
    return m;
  }
  
  private void closeKey(int paramInt)
  {
    int i = WindowsRegCloseKey(paramInt);
    if (i != 0) {
      logger().warning("Could not close windows registry node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ". Windows RegCloseKey(...) returned error code " + i + ".");
    }
  }
  
  protected void putSpi(String paramString1, String paramString2)
  {
    int i = openKey(2);
    if (i == 0)
    {
      isBackingStoreAvailable = false;
      return;
    }
    int j = WindowsRegSetValueEx1(i, toWindowsName(paramString1), toWindowsValueString(paramString2));
    if (j != 0)
    {
      logger().warning("Could not assign value to key " + byteArrayToString(toWindowsName(paramString1)) + " at Windows registry node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ". Windows RegSetValueEx(...) returned error code " + j + ".");
      isBackingStoreAvailable = false;
    }
    closeKey(i);
  }
  
  protected String getSpi(String paramString)
  {
    int i = openKey(1);
    if (i == 0) {
      return null;
    }
    byte[] arrayOfByte = WindowsRegQueryValueEx(i, toWindowsName(paramString));
    if (arrayOfByte == null)
    {
      closeKey(i);
      return null;
    }
    closeKey(i);
    return toJavaValueString((byte[])arrayOfByte);
  }
  
  protected void removeSpi(String paramString)
  {
    int i = openKey(2);
    if (i == 0) {
      return;
    }
    int j = WindowsRegDeleteValue(i, toWindowsName(paramString));
    if ((j != 0) && (j != 2))
    {
      logger().warning("Could not delete windows registry value " + byteArrayToString(windowsAbsolutePath()) + "\\" + toWindowsName(paramString) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ". Windows RegDeleteValue(...) returned error code " + j + ".");
      isBackingStoreAvailable = false;
    }
    closeKey(i);
  }
  
  protected String[] keysSpi()
    throws BackingStoreException
  {
    int i = openKey(1);
    if (i == 0) {
      throw new BackingStoreException("Could not open windows registry node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ".");
    }
    int[] arrayOfInt = WindowsRegQueryInfoKey1(i);
    if (arrayOfInt[1] != 0)
    {
      String str1 = "Could not query windows registry node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ". Windows RegQueryInfoKeyEx(...) returned error code " + arrayOfInt[1] + ".";
      logger().warning(str1);
      throw new BackingStoreException(str1);
    }
    int j = arrayOfInt[4];
    int k = arrayOfInt[2];
    if (k == 0)
    {
      closeKey(i);
      return new String[0];
    }
    String[] arrayOfString = new String[k];
    for (int m = 0; m < k; m++)
    {
      byte[] arrayOfByte = WindowsRegEnumValue1(i, m, j + 1);
      if (arrayOfByte == null)
      {
        String str2 = "Could not enumerate value #" + m + "  of windows node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ".";
        logger().warning(str2);
        throw new BackingStoreException(str2);
      }
      arrayOfString[m] = toJavaName(arrayOfByte);
    }
    closeKey(i);
    return arrayOfString;
  }
  
  protected String[] childrenNamesSpi()
    throws BackingStoreException
  {
    int i = openKey(9);
    if (i == 0) {
      throw new BackingStoreException("Could not open windows registry node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ".");
    }
    int[] arrayOfInt = WindowsRegQueryInfoKey1(i);
    if (arrayOfInt[1] != 0)
    {
      String str1 = "Could not query windows registry node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ". Windows RegQueryInfoKeyEx(...) returned error code " + arrayOfInt[1] + ".";
      logger().warning(str1);
      throw new BackingStoreException(str1);
    }
    int j = arrayOfInt[3];
    int k = arrayOfInt[0];
    if (k == 0)
    {
      closeKey(i);
      return new String[0];
    }
    String[] arrayOfString1 = new String[k];
    String[] arrayOfString2 = new String[k];
    for (int m = 0; m < k; m++)
    {
      byte[] arrayOfByte = WindowsRegEnumKeyEx1(i, m, j + 1);
      if (arrayOfByte == null)
      {
        str2 = "Could not enumerate key #" + m + "  of windows node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ". ";
        logger().warning(str2);
        throw new BackingStoreException(str2);
      }
      String str2 = toJavaName(arrayOfByte);
      arrayOfString2[m] = str2;
    }
    closeKey(i);
    return arrayOfString2;
  }
  
  public void flush()
    throws BackingStoreException
  {
    if (isRemoved())
    {
      parent.flush();
      return;
    }
    if (!isBackingStoreAvailable) {
      throw new BackingStoreException("flush(): Backing store not available.");
    }
    int i = openKey(131097);
    if (i == 0) {
      throw new BackingStoreException("Could not open windows registry node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ".");
    }
    int j = WindowsRegFlushKey1(i);
    if (j != 0)
    {
      String str = "Could not flush windows registry node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ". Windows RegFlushKey(...) returned error code " + j + ".";
      logger().warning(str);
      throw new BackingStoreException(str);
    }
    closeKey(i);
  }
  
  public void sync()
    throws BackingStoreException
  {
    if (isRemoved()) {
      throw new IllegalStateException("Node has been removed");
    }
    flush();
  }
  
  protected AbstractPreferences childSpi(String paramString)
  {
    return new WindowsPreferences(this, paramString);
  }
  
  public void removeNodeSpi()
    throws BackingStoreException
  {
    int i = ((WindowsPreferences)parent()).openKey(65536);
    if (i == 0) {
      throw new BackingStoreException("Could not open parent windows registry node of " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ".");
    }
    int j = WindowsRegDeleteKey(i, toWindowsName(name()));
    if (j != 0)
    {
      String str = "Could not delete windows registry node " + byteArrayToString(windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(rootNativeHandle()) + ". Windows RegDeleteKeyEx(...) returned error code " + j + ".";
      logger().warning(str);
      throw new BackingStoreException(str);
    }
    closeKey(i);
  }
  
  private static String toJavaName(byte[] paramArrayOfByte)
  {
    String str = byteArrayToString(paramArrayOfByte);
    if ((str.length() > 1) && (str.substring(0, 2).equals("/!"))) {
      return toJavaAlt64Name(str);
    }
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < str.length(); i++)
    {
      char c1;
      if ((c1 = str.charAt(i)) == '/')
      {
        char c2 = ' ';
        if ((str.length() > i + 1) && ((c2 = str.charAt(i + 1)) >= 'A') && (c2 <= 'Z'))
        {
          c1 = c2;
          i++;
        }
        else if ((str.length() > i + 1) && (c2 == '/'))
        {
          c1 = '\\';
          i++;
        }
      }
      else if (c1 == '\\')
      {
        c1 = '/';
      }
      localStringBuilder.append(c1);
    }
    return localStringBuilder.toString();
  }
  
  private static String toJavaAlt64Name(String paramString)
  {
    byte[] arrayOfByte = Base64.altBase64ToByteArray(paramString.substring(2));
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < arrayOfByte.length; i++)
    {
      int j = arrayOfByte[(i++)] & 0xFF;
      int k = arrayOfByte[i] & 0xFF;
      localStringBuilder.append((char)((j << 8) + k));
    }
    return localStringBuilder.toString();
  }
  
  private static byte[] toWindowsName(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < paramString.length(); i++)
    {
      char c = paramString.charAt(i);
      if ((c < ' ') || (c > '')) {
        return toWindowsAlt64Name(paramString);
      }
      if (c == '\\') {
        localStringBuilder.append("//");
      } else if (c == '/') {
        localStringBuilder.append('\\');
      } else if ((c >= 'A') && (c <= 'Z')) {
        localStringBuilder.append('/').append(c);
      } else {
        localStringBuilder.append(c);
      }
    }
    return stringToByteArray(localStringBuilder.toString());
  }
  
  private static byte[] toWindowsAlt64Name(String paramString)
  {
    byte[] arrayOfByte = new byte[2 * paramString.length()];
    int i = 0;
    for (int j = 0; j < paramString.length(); j++)
    {
      int k = paramString.charAt(j);
      arrayOfByte[(i++)] = ((byte)(k >>> 8));
      arrayOfByte[(i++)] = ((byte)k);
    }
    return stringToByteArray("/!" + Base64.byteArrayToAltBase64(arrayOfByte));
  }
  
  private static String toJavaValueString(byte[] paramArrayOfByte)
  {
    String str = byteArrayToString(paramArrayOfByte);
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < str.length(); i++)
    {
      char c1;
      if ((c1 = str.charAt(i)) == '/')
      {
        char c2 = ' ';
        if ((str.length() > i + 1) && ((c2 = str.charAt(i + 1)) == 'u'))
        {
          if (str.length() < i + 6) {
            break;
          }
          c1 = (char)Integer.parseInt(str.substring(i + 2, i + 6), 16);
          i += 5;
        }
        else if ((str.length() > i + 1) && (str.charAt(i + 1) >= 'A') && (c2 <= 'Z'))
        {
          c1 = c2;
          i++;
        }
        else if ((str.length() > i + 1) && (c2 == '/'))
        {
          c1 = '\\';
          i++;
        }
      }
      else if (c1 == '\\')
      {
        c1 = '/';
      }
      localStringBuilder.append(c1);
    }
    return localStringBuilder.toString();
  }
  
  private static byte[] toWindowsValueString(String paramString)
  {
    StringBuilder localStringBuilder1 = new StringBuilder();
    for (int i = 0; i < paramString.length(); i++)
    {
      char c = paramString.charAt(i);
      if ((c < ' ') || (c > ''))
      {
        localStringBuilder1.append("/u");
        String str = Integer.toHexString(paramString.charAt(i));
        StringBuilder localStringBuilder2 = new StringBuilder(str);
        localStringBuilder2.reverse();
        int j = 4 - localStringBuilder2.length();
        for (int k = 0; k < j; k++) {
          localStringBuilder2.append('0');
        }
        for (k = 0; k < 4; k++) {
          localStringBuilder1.append(localStringBuilder2.charAt(3 - k));
        }
      }
      else if (c == '\\')
      {
        localStringBuilder1.append("//");
      }
      else if (c == '/')
      {
        localStringBuilder1.append('\\');
      }
      else if ((c >= 'A') && (c <= 'Z'))
      {
        localStringBuilder1.append('/').append(c);
      }
      else
      {
        localStringBuilder1.append(c);
      }
    }
    return stringToByteArray(localStringBuilder1.toString());
  }
  
  private int rootNativeHandle()
  {
    return isUserNode() ? -2147483647 : -2147483646;
  }
  
  private static byte[] stringToByteArray(String paramString)
  {
    byte[] arrayOfByte = new byte[paramString.length() + 1];
    for (int i = 0; i < paramString.length(); i++) {
      arrayOfByte[i] = ((byte)paramString.charAt(i));
    }
    arrayOfByte[paramString.length()] = 0;
    return arrayOfByte;
  }
  
  private static String byteArrayToString(byte[] paramArrayOfByte)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < paramArrayOfByte.length - 1; i++) {
      localStringBuilder.append((char)paramArrayOfByte[i]);
    }
    return localStringBuilder.toString();
  }
  
  protected void flushSpi()
    throws BackingStoreException
  {}
  
  protected void syncSpi()
    throws BackingStoreException
  {}
  
  private static synchronized PlatformLogger logger()
  {
    if (logger == null) {
      logger = PlatformLogger.getLogger("java.util.prefs");
    }
    return logger;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\prefs\WindowsPreferences.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */