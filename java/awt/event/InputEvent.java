package java.awt.event;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.util.Arrays;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.InputEventAccessor;
import sun.security.util.SecurityConstants.AWT;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

public abstract class InputEvent
  extends ComponentEvent
{
  private static final PlatformLogger logger = PlatformLogger.getLogger("java.awt.event.InputEvent");
  public static final int SHIFT_MASK = 1;
  public static final int CTRL_MASK = 2;
  public static final int META_MASK = 4;
  public static final int ALT_MASK = 8;
  public static final int ALT_GRAPH_MASK = 32;
  public static final int BUTTON1_MASK = 16;
  public static final int BUTTON2_MASK = 8;
  public static final int BUTTON3_MASK = 4;
  public static final int SHIFT_DOWN_MASK = 64;
  public static final int CTRL_DOWN_MASK = 128;
  public static final int META_DOWN_MASK = 256;
  public static final int ALT_DOWN_MASK = 512;
  public static final int BUTTON1_DOWN_MASK = 1024;
  public static final int BUTTON2_DOWN_MASK = 2048;
  public static final int BUTTON3_DOWN_MASK = 4096;
  public static final int ALT_GRAPH_DOWN_MASK = 8192;
  private static final int[] BUTTON_DOWN_MASK = { 1024, 2048, 4096, 16384, 32768, 65536, 131072, 262144, 524288, 1048576, 2097152, 4194304, 8388608, 16777216, 33554432, 67108864, 134217728, 268435456, 536870912, 1073741824 };
  static final int FIRST_HIGH_BIT = Integer.MIN_VALUE;
  static final int JDK_1_3_MODIFIERS = 63;
  static final int HIGH_MODIFIERS = Integer.MIN_VALUE;
  long when;
  int modifiers;
  private transient boolean canAccessSystemClipboard;
  static final long serialVersionUID = -2482525981698309786L;
  
  private static int[] getButtonDownMasks()
  {
    return Arrays.copyOf(BUTTON_DOWN_MASK, BUTTON_DOWN_MASK.length);
  }
  
  public static int getMaskForButton(int paramInt)
  {
    if ((paramInt <= 0) || (paramInt > BUTTON_DOWN_MASK.length)) {
      throw new IllegalArgumentException("button doesn't exist " + paramInt);
    }
    return BUTTON_DOWN_MASK[(paramInt - 1)];
  }
  
  private static native void initIDs();
  
  InputEvent(Component paramComponent, int paramInt1, long paramLong, int paramInt2)
  {
    super(paramComponent, paramInt1);
    when = paramLong;
    modifiers = paramInt2;
    canAccessSystemClipboard = canAccessSystemClipboard();
  }
  
  private boolean canAccessSystemClipboard()
  {
    boolean bool = false;
    if (!GraphicsEnvironment.isHeadless())
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null) {
        try
        {
          localSecurityManager.checkPermission(SecurityConstants.AWT.ACCESS_CLIPBOARD_PERMISSION);
          bool = true;
        }
        catch (SecurityException localSecurityException)
        {
          if (logger.isLoggable(PlatformLogger.Level.FINE)) {
            logger.fine("InputEvent.canAccessSystemClipboard() got SecurityException ", localSecurityException);
          }
        }
      } else {
        bool = true;
      }
    }
    return bool;
  }
  
  public boolean isShiftDown()
  {
    return (modifiers & 0x1) != 0;
  }
  
  public boolean isControlDown()
  {
    return (modifiers & 0x2) != 0;
  }
  
  public boolean isMetaDown()
  {
    return (modifiers & 0x4) != 0;
  }
  
  public boolean isAltDown()
  {
    return (modifiers & 0x8) != 0;
  }
  
  public boolean isAltGraphDown()
  {
    return (modifiers & 0x20) != 0;
  }
  
  public long getWhen()
  {
    return when;
  }
  
  public int getModifiers()
  {
    return modifiers & 0x8000003F;
  }
  
  public int getModifiersEx()
  {
    return modifiers & 0xFFFFFFC0;
  }
  
  public void consume()
  {
    consumed = true;
  }
  
  public boolean isConsumed()
  {
    return consumed;
  }
  
  public static String getModifiersExText(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if ((paramInt & 0x100) != 0)
    {
      localStringBuilder.append(Toolkit.getProperty("AWT.meta", "Meta"));
      localStringBuilder.append("+");
    }
    if ((paramInt & 0x80) != 0)
    {
      localStringBuilder.append(Toolkit.getProperty("AWT.control", "Ctrl"));
      localStringBuilder.append("+");
    }
    if ((paramInt & 0x200) != 0)
    {
      localStringBuilder.append(Toolkit.getProperty("AWT.alt", "Alt"));
      localStringBuilder.append("+");
    }
    if ((paramInt & 0x40) != 0)
    {
      localStringBuilder.append(Toolkit.getProperty("AWT.shift", "Shift"));
      localStringBuilder.append("+");
    }
    if ((paramInt & 0x2000) != 0)
    {
      localStringBuilder.append(Toolkit.getProperty("AWT.altGraph", "Alt Graph"));
      localStringBuilder.append("+");
    }
    int i = 1;
    for (int m : BUTTON_DOWN_MASK)
    {
      if ((paramInt & m) != 0)
      {
        localStringBuilder.append(Toolkit.getProperty("AWT.button" + i, "Button" + i));
        localStringBuilder.append("+");
      }
      i++;
    }
    if (localStringBuilder.length() > 0) {
      localStringBuilder.setLength(localStringBuilder.length() - 1);
    }
    return localStringBuilder.toString();
  }
  
  static
  {
    NativeLibLoader.loadLibraries();
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
    AWTAccessor.setInputEventAccessor(new AWTAccessor.InputEventAccessor()
    {
      public int[] getButtonDownMasks()
      {
        return InputEvent.access$000();
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\event\InputEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */