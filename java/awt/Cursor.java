package java.awt;

import java.beans.ConstructorProperties;
import java.io.File;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.CursorAccessor;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;
import sun.security.action.GetPropertyAction;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

public class Cursor
  implements Serializable
{
  public static final int DEFAULT_CURSOR = 0;
  public static final int CROSSHAIR_CURSOR = 1;
  public static final int TEXT_CURSOR = 2;
  public static final int WAIT_CURSOR = 3;
  public static final int SW_RESIZE_CURSOR = 4;
  public static final int SE_RESIZE_CURSOR = 5;
  public static final int NW_RESIZE_CURSOR = 6;
  public static final int NE_RESIZE_CURSOR = 7;
  public static final int N_RESIZE_CURSOR = 8;
  public static final int S_RESIZE_CURSOR = 9;
  public static final int W_RESIZE_CURSOR = 10;
  public static final int E_RESIZE_CURSOR = 11;
  public static final int HAND_CURSOR = 12;
  public static final int MOVE_CURSOR = 13;
  @Deprecated
  protected static Cursor[] predefined = new Cursor[14];
  private static final Cursor[] predefinedPrivate = new Cursor[14];
  static final String[][] cursorProperties = { { "AWT.DefaultCursor", "Default Cursor" }, { "AWT.CrosshairCursor", "Crosshair Cursor" }, { "AWT.TextCursor", "Text Cursor" }, { "AWT.WaitCursor", "Wait Cursor" }, { "AWT.SWResizeCursor", "Southwest Resize Cursor" }, { "AWT.SEResizeCursor", "Southeast Resize Cursor" }, { "AWT.NWResizeCursor", "Northwest Resize Cursor" }, { "AWT.NEResizeCursor", "Northeast Resize Cursor" }, { "AWT.NResizeCursor", "North Resize Cursor" }, { "AWT.SResizeCursor", "South Resize Cursor" }, { "AWT.WResizeCursor", "West Resize Cursor" }, { "AWT.EResizeCursor", "East Resize Cursor" }, { "AWT.HandCursor", "Hand Cursor" }, { "AWT.MoveCursor", "Move Cursor" } };
  int type = 0;
  public static final int CUSTOM_CURSOR = -1;
  private static final Hashtable<String, Cursor> systemCustomCursors = new Hashtable(1);
  private static final String systemCustomCursorDirPrefix = initCursorDir();
  private static final String systemCustomCursorPropertiesFile = systemCustomCursorDirPrefix + "cursors.properties";
  private static Properties systemCustomCursorProperties = null;
  private static final String CursorDotPrefix = "Cursor.";
  private static final String DotFileSuffix = ".File";
  private static final String DotHotspotSuffix = ".HotSpot";
  private static final String DotNameSuffix = ".Name";
  private static final long serialVersionUID = 8028237497568985504L;
  private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.Cursor");
  private transient long pData;
  private transient Object anchor = new Object();
  transient CursorDisposer disposer;
  protected String name;
  
  private static String initCursorDir()
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.home"));
    return str + File.separator + "lib" + File.separator + "images" + File.separator + "cursors" + File.separator;
  }
  
  private static native void initIDs();
  
  private void setPData(long paramLong)
  {
    pData = paramLong;
    if (GraphicsEnvironment.isHeadless()) {
      return;
    }
    if (disposer == null)
    {
      disposer = new CursorDisposer(paramLong);
      if (anchor == null) {
        anchor = new Object();
      }
      Disposer.addRecord(anchor, disposer);
    }
    else
    {
      disposer.pData = paramLong;
    }
  }
  
  public static Cursor getPredefinedCursor(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 13)) {
      throw new IllegalArgumentException("illegal cursor type");
    }
    Cursor localCursor = predefinedPrivate[paramInt];
    if (localCursor == null) {
      predefinedPrivate[paramInt] = (localCursor = new Cursor(paramInt));
    }
    if (predefined[paramInt] == null) {
      predefined[paramInt] = localCursor;
    }
    return localCursor;
  }
  
  public static Cursor getSystemCustomCursor(String paramString)
    throws AWTException, HeadlessException
  {
    GraphicsEnvironment.checkHeadless();
    Cursor localCursor = (Cursor)systemCustomCursors.get(paramString);
    if (localCursor == null)
    {
      synchronized (systemCustomCursors)
      {
        if (systemCustomCursorProperties == null) {
          loadSystemCustomCursorProperties();
        }
      }
      ??? = "Cursor." + paramString;
      String str1 = (String)??? + ".File";
      if (!systemCustomCursorProperties.containsKey(str1))
      {
        if (log.isLoggable(PlatformLogger.Level.FINER)) {
          log.finer("Cursor.getSystemCustomCursor(" + paramString + ") returned null");
        }
        return null;
      }
      String str2 = systemCustomCursorProperties.getProperty(str1);
      String str3 = systemCustomCursorProperties.getProperty((String)??? + ".Name");
      if (str3 == null) {
        str3 = paramString;
      }
      String str4 = systemCustomCursorProperties.getProperty((String)??? + ".HotSpot");
      if (str4 == null) {
        throw new AWTException("no hotspot property defined for cursor: " + paramString);
      }
      StringTokenizer localStringTokenizer = new StringTokenizer(str4, ",");
      if (localStringTokenizer.countTokens() != 2) {
        throw new AWTException("failed to parse hotspot property for cursor: " + paramString);
      }
      NumberFormatException localNumberFormatException1 = 0;
      int i = 0;
      try
      {
        localNumberFormatException1 = Integer.parseInt(localStringTokenizer.nextToken());
        i = Integer.parseInt(localStringTokenizer.nextToken());
      }
      catch (NumberFormatException localNumberFormatException2)
      {
        throw new AWTException("failed to parse hotspot property for cursor: " + paramString);
      }
      try
      {
        localNumberFormatException2 = localNumberFormatException1;
        final int j = i;
        final String str5 = str3;
        localCursor = (Cursor)AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public Cursor run()
            throws Exception
          {
            Toolkit localToolkit = Toolkit.getDefaultToolkit();
            Image localImage = localToolkit.getImage(Cursor.systemCustomCursorDirPrefix + val$fileName);
            return localToolkit.createCustomCursor(localImage, new Point(localNumberFormatException2, j), str5);
          }
        });
      }
      catch (Exception localException)
      {
        throw new AWTException("Exception: " + localException.getClass() + " " + localException.getMessage() + " occurred while creating cursor " + paramString);
      }
      if (localCursor == null)
      {
        if (log.isLoggable(PlatformLogger.Level.FINER)) {
          log.finer("Cursor.getSystemCustomCursor(" + paramString + ") returned null");
        }
      }
      else {
        systemCustomCursors.put(paramString, localCursor);
      }
    }
    return localCursor;
  }
  
  public static Cursor getDefaultCursor()
  {
    return getPredefinedCursor(0);
  }
  
  @ConstructorProperties({"type"})
  public Cursor(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 13)) {
      throw new IllegalArgumentException("illegal cursor type");
    }
    type = paramInt;
    name = Toolkit.getProperty(cursorProperties[paramInt][0], cursorProperties[paramInt][1]);
  }
  
  protected Cursor(String paramString)
  {
    type = -1;
    name = paramString;
  }
  
  public int getType()
  {
    return type;
  }
  
  public String getName()
  {
    return name;
  }
  
  public String toString()
  {
    return getClass().getName() + "[" + getName() + "]";
  }
  
  private static void loadSystemCustomCursorProperties()
    throws AWTException
  {
    synchronized (systemCustomCursors)
    {
      systemCustomCursorProperties = new Properties();
      try
      {
        AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          /* Error */
          public Object run()
            throws Exception
          {
            // Byte code:
            //   0: aconst_null
            //   1: astore_1
            //   2: new 28	java/io/FileInputStream
            //   5: dup
            //   6: invokestatic 44	java/awt/Cursor:access$300	()Ljava/lang/String;
            //   9: invokespecial 47	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
            //   12: astore_1
            //   13: invokestatic 45	java/awt/Cursor:access$400	()Ljava/util/Properties;
            //   16: aload_1
            //   17: invokevirtual 49	java/util/Properties:load	(Ljava/io/InputStream;)V
            //   20: aload_1
            //   21: ifnull +21 -> 42
            //   24: aload_1
            //   25: invokevirtual 46	java/io/FileInputStream:close	()V
            //   28: goto +14 -> 42
            //   31: astore_2
            //   32: aload_1
            //   33: ifnull +7 -> 40
            //   36: aload_1
            //   37: invokevirtual 46	java/io/FileInputStream:close	()V
            //   40: aload_2
            //   41: athrow
            //   42: aconst_null
            //   43: areturn
            // Local variable table:
            //   start	length	slot	name	signature
            //   0	44	0	this	3
            //   1	36	1	localFileInputStream	java.io.FileInputStream
            //   31	10	2	localObject	Object
            // Exception table:
            //   from	to	target	type
            //   2	20	31	finally
          }
        });
      }
      catch (Exception localException)
      {
        systemCustomCursorProperties = null;
        throw new AWTException("Exception: " + localException.getClass() + " " + localException.getMessage() + " occurred while loading: " + systemCustomCursorPropertiesFile);
      }
    }
  }
  
  private static native void finalizeImpl(long paramLong);
  
  static
  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
    AWTAccessor.setCursorAccessor(new AWTAccessor.CursorAccessor()
    {
      public long getPData(Cursor paramAnonymousCursor)
      {
        return pData;
      }
      
      public void setPData(Cursor paramAnonymousCursor, long paramAnonymousLong)
      {
        pData = paramAnonymousLong;
      }
      
      public int getType(Cursor paramAnonymousCursor)
      {
        return type;
      }
    });
  }
  
  static class CursorDisposer
    implements DisposerRecord
  {
    volatile long pData;
    
    public CursorDisposer(long paramLong)
    {
      pData = paramLong;
    }
    
    public void dispose()
    {
      if (pData != 0L) {
        Cursor.finalizeImpl(pData);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\Cursor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */