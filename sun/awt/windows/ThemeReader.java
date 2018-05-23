package sun.awt.windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class ThemeReader
{
  private static final Map<String, Long> widgetToTheme = new HashMap();
  private static final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
  private static final Lock readLock = readWriteLock.readLock();
  private static final Lock writeLock = readWriteLock.writeLock();
  private static volatile boolean valid = false;
  static volatile boolean xpStyleEnabled;
  
  public ThemeReader() {}
  
  static void flush()
  {
    valid = false;
  }
  
  public static native boolean isThemed();
  
  public static boolean isXPStyleEnabled()
  {
    return xpStyleEnabled;
  }
  
  private static Long getThemeImpl(String paramString)
  {
    Long localLong = (Long)widgetToTheme.get(paramString);
    if (localLong == null)
    {
      int i = paramString.indexOf("::");
      if (i > 0)
      {
        setWindowTheme(paramString.substring(0, i));
        localLong = Long.valueOf(openTheme(paramString.substring(i + 2)));
        setWindowTheme(null);
      }
      else
      {
        localLong = Long.valueOf(openTheme(paramString));
      }
      widgetToTheme.put(paramString, localLong);
    }
    return localLong;
  }
  
  /* Error */
  private static Long getTheme(String paramString)
  {
    // Byte code:
    //   0: getstatic 160	sun/awt/windows/ThemeReader:valid	Z
    //   3: ifne +118 -> 121
    //   6: getstatic 163	sun/awt/windows/ThemeReader:readLock	Ljava/util/concurrent/locks/Lock;
    //   9: invokeinterface 200 1 0
    //   14: getstatic 164	sun/awt/windows/ThemeReader:writeLock	Ljava/util/concurrent/locks/Lock;
    //   17: invokeinterface 199 1 0
    //   22: getstatic 160	sun/awt/windows/ThemeReader:valid	Z
    //   25: ifne +58 -> 83
    //   28: getstatic 162	sun/awt/windows/ThemeReader:widgetToTheme	Ljava/util/Map;
    //   31: invokeinterface 196 1 0
    //   36: invokeinterface 192 1 0
    //   41: astore_1
    //   42: aload_1
    //   43: invokeinterface 193 1 0
    //   48: ifeq +23 -> 71
    //   51: aload_1
    //   52: invokeinterface 194 1 0
    //   57: checkcast 78	java/lang/Long
    //   60: astore_2
    //   61: aload_2
    //   62: invokevirtual 166	java/lang/Long:longValue	()J
    //   65: invokestatic 174	sun/awt/windows/ThemeReader:closeTheme	(J)V
    //   68: goto -26 -> 42
    //   71: getstatic 162	sun/awt/windows/ThemeReader:widgetToTheme	Ljava/util/Map;
    //   74: invokeinterface 195 1 0
    //   79: iconst_1
    //   80: putstatic 160	sun/awt/windows/ThemeReader:valid	Z
    //   83: getstatic 163	sun/awt/windows/ThemeReader:readLock	Ljava/util/concurrent/locks/Lock;
    //   86: invokeinterface 199 1 0
    //   91: getstatic 164	sun/awt/windows/ThemeReader:writeLock	Ljava/util/concurrent/locks/Lock;
    //   94: invokeinterface 200 1 0
    //   99: goto +22 -> 121
    //   102: astore_3
    //   103: getstatic 163	sun/awt/windows/ThemeReader:readLock	Ljava/util/concurrent/locks/Lock;
    //   106: invokeinterface 199 1 0
    //   111: getstatic 164	sun/awt/windows/ThemeReader:writeLock	Ljava/util/concurrent/locks/Lock;
    //   114: invokeinterface 200 1 0
    //   119: aload_3
    //   120: athrow
    //   121: getstatic 162	sun/awt/windows/ThemeReader:widgetToTheme	Ljava/util/Map;
    //   124: aload_0
    //   125: invokeinterface 197 2 0
    //   130: checkcast 78	java/lang/Long
    //   133: astore_1
    //   134: aload_1
    //   135: ifnonnull +64 -> 199
    //   138: getstatic 163	sun/awt/windows/ThemeReader:readLock	Ljava/util/concurrent/locks/Lock;
    //   141: invokeinterface 200 1 0
    //   146: getstatic 164	sun/awt/windows/ThemeReader:writeLock	Ljava/util/concurrent/locks/Lock;
    //   149: invokeinterface 199 1 0
    //   154: aload_0
    //   155: invokestatic 191	sun/awt/windows/ThemeReader:getThemeImpl	(Ljava/lang/String;)Ljava/lang/Long;
    //   158: astore_1
    //   159: getstatic 163	sun/awt/windows/ThemeReader:readLock	Ljava/util/concurrent/locks/Lock;
    //   162: invokeinterface 199 1 0
    //   167: getstatic 164	sun/awt/windows/ThemeReader:writeLock	Ljava/util/concurrent/locks/Lock;
    //   170: invokeinterface 200 1 0
    //   175: goto +24 -> 199
    //   178: astore 4
    //   180: getstatic 163	sun/awt/windows/ThemeReader:readLock	Ljava/util/concurrent/locks/Lock;
    //   183: invokeinterface 199 1 0
    //   188: getstatic 164	sun/awt/windows/ThemeReader:writeLock	Ljava/util/concurrent/locks/Lock;
    //   191: invokeinterface 200 1 0
    //   196: aload 4
    //   198: athrow
    //   199: aload_1
    //   200: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	201	0	paramString	String
    //   41	159	1	localObject1	Object
    //   60	2	2	localLong	Long
    //   102	18	3	localObject2	Object
    //   178	19	4	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   22	83	102	finally
    //   154	159	178	finally
    //   178	180	178	finally
  }
  
  private static native void paintBackground(int[] paramArrayOfInt, long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);
  
  public static void paintBackground(int[] paramArrayOfInt, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
  {
    readLock.lock();
    try
    {
      paintBackground(paramArrayOfInt, getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7);
    }
    finally
    {
      readLock.unlock();
    }
  }
  
  private static native Insets getThemeMargins(long paramLong, int paramInt1, int paramInt2, int paramInt3);
  
  public static Insets getThemeMargins(String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    readLock.lock();
    try
    {
      Insets localInsets = getThemeMargins(getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3);
      return localInsets;
    }
    finally
    {
      readLock.unlock();
    }
  }
  
  private static native boolean isThemePartDefined(long paramLong, int paramInt1, int paramInt2);
  
  public static boolean isThemePartDefined(String paramString, int paramInt1, int paramInt2)
  {
    readLock.lock();
    try
    {
      boolean bool = isThemePartDefined(getTheme(paramString).longValue(), paramInt1, paramInt2);
      return bool;
    }
    finally
    {
      readLock.unlock();
    }
  }
  
  private static native Color getColor(long paramLong, int paramInt1, int paramInt2, int paramInt3);
  
  public static Color getColor(String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    readLock.lock();
    try
    {
      Color localColor = getColor(getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3);
      return localColor;
    }
    finally
    {
      readLock.unlock();
    }
  }
  
  private static native int getInt(long paramLong, int paramInt1, int paramInt2, int paramInt3);
  
  public static int getInt(String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    readLock.lock();
    try
    {
      int i = getInt(getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3);
      return i;
    }
    finally
    {
      readLock.unlock();
    }
  }
  
  private static native int getEnum(long paramLong, int paramInt1, int paramInt2, int paramInt3);
  
  public static int getEnum(String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    readLock.lock();
    try
    {
      int i = getEnum(getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3);
      return i;
    }
    finally
    {
      readLock.unlock();
    }
  }
  
  private static native boolean getBoolean(long paramLong, int paramInt1, int paramInt2, int paramInt3);
  
  public static boolean getBoolean(String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    readLock.lock();
    try
    {
      boolean bool = getBoolean(getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3);
      return bool;
    }
    finally
    {
      readLock.unlock();
    }
  }
  
  private static native boolean getSysBoolean(long paramLong, int paramInt);
  
  public static boolean getSysBoolean(String paramString, int paramInt)
  {
    readLock.lock();
    try
    {
      boolean bool = getSysBoolean(getTheme(paramString).longValue(), paramInt);
      return bool;
    }
    finally
    {
      readLock.unlock();
    }
  }
  
  private static native Point getPoint(long paramLong, int paramInt1, int paramInt2, int paramInt3);
  
  public static Point getPoint(String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    readLock.lock();
    try
    {
      Point localPoint = getPoint(getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3);
      return localPoint;
    }
    finally
    {
      readLock.unlock();
    }
  }
  
  private static native Dimension getPosition(long paramLong, int paramInt1, int paramInt2, int paramInt3);
  
  public static Dimension getPosition(String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    readLock.lock();
    try
    {
      Dimension localDimension = getPosition(getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3);
      return localDimension;
    }
    finally
    {
      readLock.unlock();
    }
  }
  
  private static native Dimension getPartSize(long paramLong, int paramInt1, int paramInt2);
  
  public static Dimension getPartSize(String paramString, int paramInt1, int paramInt2)
  {
    readLock.lock();
    try
    {
      Dimension localDimension = getPartSize(getTheme(paramString).longValue(), paramInt1, paramInt2);
      return localDimension;
    }
    finally
    {
      readLock.unlock();
    }
  }
  
  private static native long openTheme(String paramString);
  
  private static native void closeTheme(long paramLong);
  
  private static native void setWindowTheme(String paramString);
  
  private static native long getThemeTransitionDuration(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public static long getThemeTransitionDuration(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    readLock.lock();
    try
    {
      long l = getThemeTransitionDuration(getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3, paramInt4);
      return l;
    }
    finally
    {
      readLock.unlock();
    }
  }
  
  public static native boolean isGetThemeTransitionDurationDefined();
  
  private static native Insets getThemeBackgroundContentMargins(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public static Insets getThemeBackgroundContentMargins(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    readLock.lock();
    try
    {
      Insets localInsets = getThemeBackgroundContentMargins(getTheme(paramString).longValue(), paramInt1, paramInt2, paramInt3, paramInt4);
      return localInsets;
    }
    finally
    {
      readLock.unlock();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\ThemeReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */