package sun.awt.windows;

import java.awt.Color;
import java.awt.Font;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import sun.awt.SunToolkit;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

final class WDesktopProperties
{
  private static final PlatformLogger log;
  private static final String PREFIX = "win.";
  private static final String FILE_PREFIX = "awt.file.";
  private static final String PROP_NAMES = "win.propNames";
  private long pData;
  private WToolkit wToolkit;
  private HashMap<String, Object> map = new HashMap();
  static HashMap<String, String> fontNameMap;
  
  private static native void initIDs();
  
  static boolean isWindowsProperty(String paramString)
  {
    return (paramString.startsWith("win.")) || (paramString.startsWith("awt.file.")) || (paramString.equals("awt.font.desktophints"));
  }
  
  WDesktopProperties(WToolkit paramWToolkit)
  {
    wToolkit = paramWToolkit;
    init();
  }
  
  private native void init();
  
  private String[] getKeyNames()
  {
    Object[] arrayOfObject = map.keySet().toArray();
    String[] arrayOfString = new String[arrayOfObject.length];
    for (int i = 0; i < arrayOfObject.length; i++) {
      arrayOfString[i] = arrayOfObject[i].toString();
    }
    Arrays.sort(arrayOfString);
    return arrayOfString;
  }
  
  private native void getWindowsParameters();
  
  private synchronized void setBooleanProperty(String paramString, boolean paramBoolean)
  {
    assert (paramString != null);
    if (log.isLoggable(PlatformLogger.Level.FINE)) {
      log.fine(paramString + "=" + String.valueOf(paramBoolean));
    }
    map.put(paramString, Boolean.valueOf(paramBoolean));
  }
  
  private synchronized void setIntegerProperty(String paramString, int paramInt)
  {
    assert (paramString != null);
    if (log.isLoggable(PlatformLogger.Level.FINE)) {
      log.fine(paramString + "=" + String.valueOf(paramInt));
    }
    map.put(paramString, Integer.valueOf(paramInt));
  }
  
  private synchronized void setStringProperty(String paramString1, String paramString2)
  {
    assert (paramString1 != null);
    if (log.isLoggable(PlatformLogger.Level.FINE)) {
      log.fine(paramString1 + "=" + paramString2);
    }
    map.put(paramString1, paramString2);
  }
  
  private synchronized void setColorProperty(String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    assert ((paramString != null) && (paramInt1 <= 255) && (paramInt2 <= 255) && (paramInt3 <= 255));
    Color localColor = new Color(paramInt1, paramInt2, paramInt3);
    if (log.isLoggable(PlatformLogger.Level.FINE)) {
      log.fine(paramString + "=" + localColor);
    }
    map.put(paramString, localColor);
  }
  
  private synchronized void setFontProperty(String paramString1, String paramString2, int paramInt1, int paramInt2)
  {
    assert ((paramString1 != null) && (paramInt1 <= 3) && (paramInt2 >= 0));
    String str1 = (String)fontNameMap.get(paramString2);
    if (str1 != null) {
      paramString2 = str1;
    }
    Font localFont = new Font(paramString2, paramInt1, paramInt2);
    if (log.isLoggable(PlatformLogger.Level.FINE)) {
      log.fine(paramString1 + "=" + localFont);
    }
    map.put(paramString1, localFont);
    String str2 = paramString1 + ".height";
    Integer localInteger = Integer.valueOf(paramInt2);
    if (log.isLoggable(PlatformLogger.Level.FINE)) {
      log.fine(str2 + "=" + localInteger);
    }
    map.put(str2, localInteger);
  }
  
  private synchronized void setSoundProperty(String paramString1, String paramString2)
  {
    assert ((paramString1 != null) && (paramString2 != null));
    WinPlaySound localWinPlaySound = new WinPlaySound(paramString2);
    if (log.isLoggable(PlatformLogger.Level.FINE)) {
      log.fine(paramString1 + "=" + localWinPlaySound);
    }
    map.put(paramString1, localWinPlaySound);
  }
  
  private native void playWindowsSound(String paramString);
  
  synchronized Map<String, Object> getProperties()
  {
    ThemeReader.flush();
    map = new HashMap();
    getWindowsParameters();
    map.put("awt.font.desktophints", SunToolkit.getDesktopFontHints());
    map.put("win.propNames", getKeyNames());
    map.put("DnD.Autoscroll.cursorHysteresis", map.get("win.drag.x"));
    return (Map)map.clone();
  }
  
  synchronized RenderingHints getDesktopAAHints()
  {
    Object localObject1 = RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
    Integer localInteger1 = null;
    Boolean localBoolean = (Boolean)map.get("win.text.fontSmoothingOn");
    if ((localBoolean != null) && (localBoolean.equals(Boolean.TRUE)))
    {
      localObject2 = (Integer)map.get("win.text.fontSmoothingType");
      if ((localObject2 == null) || (((Integer)localObject2).intValue() <= 1) || (((Integer)localObject2).intValue() > 2))
      {
        localObject1 = RenderingHints.VALUE_TEXT_ANTIALIAS_GASP;
      }
      else
      {
        Integer localInteger2 = (Integer)map.get("win.text.fontSmoothingOrientation");
        if ((localInteger2 == null) || (localInteger2.intValue() != 0)) {
          localObject1 = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB;
        } else {
          localObject1 = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR;
        }
        localInteger1 = (Integer)map.get("win.text.fontSmoothingContrast");
        if (localInteger1 == null) {
          localInteger1 = Integer.valueOf(140);
        } else {
          localInteger1 = Integer.valueOf(localInteger1.intValue() / 10);
        }
      }
    }
    Object localObject2 = new RenderingHints(null);
    ((RenderingHints)localObject2).put(RenderingHints.KEY_TEXT_ANTIALIASING, localObject1);
    if (localInteger1 != null) {
      ((RenderingHints)localObject2).put(RenderingHints.KEY_TEXT_LCD_CONTRAST, localInteger1);
    }
    return (RenderingHints)localObject2;
  }
  
  static
  {
    log = PlatformLogger.getLogger("sun.awt.windows.WDesktopProperties");
    initIDs();
    fontNameMap = new HashMap();
    fontNameMap.put("Courier", "Monospaced");
    fontNameMap.put("MS Serif", "Microsoft Serif");
    fontNameMap.put("MS Sans Serif", "Microsoft Sans Serif");
    fontNameMap.put("Terminal", "Dialog");
    fontNameMap.put("FixedSys", "Monospaced");
    fontNameMap.put("System", "Dialog");
  }
  
  class WinPlaySound
    implements Runnable
  {
    String winEventName;
    
    WinPlaySound(String paramString)
    {
      winEventName = paramString;
    }
    
    public void run()
    {
      WDesktopProperties.this.playWindowsSound(winEventName);
    }
    
    public String toString()
    {
      return "WinPlaySound(" + winEventName + ")";
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      try
      {
        return winEventName.equals(winEventName);
      }
      catch (Exception localException) {}
      return false;
    }
    
    public int hashCode()
    {
      return winEventName.hashCode();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WDesktopProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */