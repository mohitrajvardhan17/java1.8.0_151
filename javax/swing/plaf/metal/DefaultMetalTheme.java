package javax.swing.plaf.metal;

import java.awt.Font;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import sun.awt.AppContext;
import sun.security.action.GetPropertyAction;
import sun.swing.SwingUtilities2;

public class DefaultMetalTheme
  extends MetalTheme
{
  private static final boolean PLAIN_FONTS;
  private static final String[] fontNames = { "Dialog", "Dialog", "Dialog", "Dialog", "Dialog", "Dialog" };
  private static final int[] fontStyles = { 1, 0, 0, 1, 1, 0 };
  private static final int[] fontSizes = { 12, 12, 12, 12, 12, 10 };
  private static final String[] defaultNames = { "swing.plaf.metal.controlFont", "swing.plaf.metal.systemFont", "swing.plaf.metal.userFont", "swing.plaf.metal.controlFont", "swing.plaf.metal.controlFont", "swing.plaf.metal.smallFont" };
  private static final ColorUIResource primary1 = new ColorUIResource(102, 102, 153);
  private static final ColorUIResource primary2 = new ColorUIResource(153, 153, 204);
  private static final ColorUIResource primary3 = new ColorUIResource(204, 204, 255);
  private static final ColorUIResource secondary1 = new ColorUIResource(102, 102, 102);
  private static final ColorUIResource secondary2 = new ColorUIResource(153, 153, 153);
  private static final ColorUIResource secondary3 = new ColorUIResource(204, 204, 204);
  private FontDelegate fontDelegate;
  
  static String getDefaultFontName(int paramInt)
  {
    return fontNames[paramInt];
  }
  
  static int getDefaultFontSize(int paramInt)
  {
    return fontSizes[paramInt];
  }
  
  static int getDefaultFontStyle(int paramInt)
  {
    if (paramInt != 4)
    {
      Object localObject = null;
      if (AppContext.getAppContext().get(SwingUtilities2.LAF_STATE_KEY) != null) {
        localObject = UIManager.get("swing.boldMetal");
      }
      if (localObject != null)
      {
        if (Boolean.FALSE.equals(localObject)) {
          return 0;
        }
      }
      else if (PLAIN_FONTS) {
        return 0;
      }
    }
    return fontStyles[paramInt];
  }
  
  static String getDefaultPropertyName(int paramInt)
  {
    return defaultNames[paramInt];
  }
  
  public String getName()
  {
    return "Steel";
  }
  
  public DefaultMetalTheme()
  {
    install();
  }
  
  protected ColorUIResource getPrimary1()
  {
    return primary1;
  }
  
  protected ColorUIResource getPrimary2()
  {
    return primary2;
  }
  
  protected ColorUIResource getPrimary3()
  {
    return primary3;
  }
  
  protected ColorUIResource getSecondary1()
  {
    return secondary1;
  }
  
  protected ColorUIResource getSecondary2()
  {
    return secondary2;
  }
  
  protected ColorUIResource getSecondary3()
  {
    return secondary3;
  }
  
  public FontUIResource getControlTextFont()
  {
    return getFont(0);
  }
  
  public FontUIResource getSystemTextFont()
  {
    return getFont(1);
  }
  
  public FontUIResource getUserTextFont()
  {
    return getFont(2);
  }
  
  public FontUIResource getMenuTextFont()
  {
    return getFont(3);
  }
  
  public FontUIResource getWindowTitleFont()
  {
    return getFont(4);
  }
  
  public FontUIResource getSubTextFont()
  {
    return getFont(5);
  }
  
  private FontUIResource getFont(int paramInt)
  {
    return fontDelegate.getFont(paramInt);
  }
  
  void install()
  {
    if ((MetalLookAndFeel.isWindows()) && (MetalLookAndFeel.useSystemFonts())) {
      fontDelegate = new WindowsFontDelegate();
    } else {
      fontDelegate = new FontDelegate();
    }
  }
  
  boolean isSystemTheme()
  {
    return getClass() == DefaultMetalTheme.class;
  }
  
  static
  {
    Object localObject = AccessController.doPrivileged(new GetPropertyAction("swing.boldMetal"));
    if ((localObject == null) || (!"false".equals(localObject))) {
      PLAIN_FONTS = false;
    } else {
      PLAIN_FONTS = true;
    }
  }
  
  private static class FontDelegate
  {
    private static int[] defaultMapping = { 0, 1, 2, 0, 0, 5 };
    FontUIResource[] fonts = new FontUIResource[6];
    
    public FontDelegate() {}
    
    public FontUIResource getFont(int paramInt)
    {
      int i = defaultMapping[paramInt];
      if (fonts[paramInt] == null)
      {
        Font localFont = getPrivilegedFont(i);
        if (localFont == null) {
          localFont = new Font(DefaultMetalTheme.getDefaultFontName(paramInt), DefaultMetalTheme.getDefaultFontStyle(paramInt), DefaultMetalTheme.getDefaultFontSize(paramInt));
        }
        fonts[paramInt] = new FontUIResource(localFont);
      }
      return fonts[paramInt];
    }
    
    protected Font getPrivilegedFont(final int paramInt)
    {
      (Font)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Font run()
        {
          return Font.getFont(DefaultMetalTheme.getDefaultPropertyName(paramInt));
        }
      });
    }
  }
  
  private static class WindowsFontDelegate
    extends DefaultMetalTheme.FontDelegate
  {
    private MetalFontDesktopProperty[] props = new MetalFontDesktopProperty[6];
    private boolean[] checkedPriviledged = new boolean[6];
    
    public WindowsFontDelegate() {}
    
    public FontUIResource getFont(int paramInt)
    {
      if (fonts[paramInt] != null) {
        return fonts[paramInt];
      }
      if (checkedPriviledged[paramInt] == 0)
      {
        Font localFont = getPrivilegedFont(paramInt);
        checkedPriviledged[paramInt] = true;
        if (localFont != null)
        {
          fonts[paramInt] = new FontUIResource(localFont);
          return fonts[paramInt];
        }
      }
      if (props[paramInt] == null) {
        props[paramInt] = new MetalFontDesktopProperty(paramInt);
      }
      return (FontUIResource)props[paramInt].createValue(null);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\DefaultMetalTheme.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */