package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InputMapUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent.KeyBinding;
import sun.awt.SunToolkit;
import sun.swing.DefaultLayoutStyle;
import sun.swing.ImageIconUIResource;
import sun.swing.SwingUtilities2;

public abstract class LookAndFeel
{
  public LookAndFeel() {}
  
  public static void installColors(JComponent paramJComponent, String paramString1, String paramString2)
  {
    Color localColor1 = paramJComponent.getBackground();
    if ((localColor1 == null) || ((localColor1 instanceof UIResource))) {
      paramJComponent.setBackground(UIManager.getColor(paramString1));
    }
    Color localColor2 = paramJComponent.getForeground();
    if ((localColor2 == null) || ((localColor2 instanceof UIResource))) {
      paramJComponent.setForeground(UIManager.getColor(paramString2));
    }
  }
  
  public static void installColorsAndFont(JComponent paramJComponent, String paramString1, String paramString2, String paramString3)
  {
    Font localFont = paramJComponent.getFont();
    if ((localFont == null) || ((localFont instanceof UIResource))) {
      paramJComponent.setFont(UIManager.getFont(paramString3));
    }
    installColors(paramJComponent, paramString1, paramString2);
  }
  
  public static void installBorder(JComponent paramJComponent, String paramString)
  {
    Border localBorder = paramJComponent.getBorder();
    if ((localBorder == null) || ((localBorder instanceof UIResource))) {
      paramJComponent.setBorder(UIManager.getBorder(paramString));
    }
  }
  
  public static void uninstallBorder(JComponent paramJComponent)
  {
    if ((paramJComponent.getBorder() instanceof UIResource)) {
      paramJComponent.setBorder(null);
    }
  }
  
  public static void installProperty(JComponent paramJComponent, String paramString, Object paramObject)
  {
    if (SunToolkit.isInstanceOf(paramJComponent, "javax.swing.JPasswordField"))
    {
      if (!((JPasswordField)paramJComponent).customSetUIProperty(paramString, paramObject)) {
        paramJComponent.setUIProperty(paramString, paramObject);
      }
    }
    else {
      paramJComponent.setUIProperty(paramString, paramObject);
    }
  }
  
  public static JTextComponent.KeyBinding[] makeKeyBindings(Object[] paramArrayOfObject)
  {
    JTextComponent.KeyBinding[] arrayOfKeyBinding = new JTextComponent.KeyBinding[paramArrayOfObject.length / 2];
    for (int i = 0; i < arrayOfKeyBinding.length; i++)
    {
      Object localObject = paramArrayOfObject[(2 * i)];
      KeyStroke localKeyStroke = (localObject instanceof KeyStroke) ? (KeyStroke)localObject : KeyStroke.getKeyStroke((String)localObject);
      String str = (String)paramArrayOfObject[(2 * i + 1)];
      arrayOfKeyBinding[i] = new JTextComponent.KeyBinding(localKeyStroke, str);
    }
    return arrayOfKeyBinding;
  }
  
  public static InputMap makeInputMap(Object[] paramArrayOfObject)
  {
    InputMapUIResource localInputMapUIResource = new InputMapUIResource();
    loadKeyBindings(localInputMapUIResource, paramArrayOfObject);
    return localInputMapUIResource;
  }
  
  public static ComponentInputMap makeComponentInputMap(JComponent paramJComponent, Object[] paramArrayOfObject)
  {
    ComponentInputMapUIResource localComponentInputMapUIResource = new ComponentInputMapUIResource(paramJComponent);
    loadKeyBindings(localComponentInputMapUIResource, paramArrayOfObject);
    return localComponentInputMapUIResource;
  }
  
  public static void loadKeyBindings(InputMap paramInputMap, Object[] paramArrayOfObject)
  {
    if (paramArrayOfObject != null)
    {
      int i = 0;
      int j = paramArrayOfObject.length;
      while (i < j)
      {
        Object localObject = paramArrayOfObject[(i++)];
        KeyStroke localKeyStroke = (localObject instanceof KeyStroke) ? (KeyStroke)localObject : KeyStroke.getKeyStroke((String)localObject);
        paramInputMap.put(localKeyStroke, paramArrayOfObject[i]);
        i++;
      }
    }
  }
  
  public static Object makeIcon(Class<?> paramClass, String paramString)
  {
    return SwingUtilities2.makeIcon(paramClass, paramClass, paramString);
  }
  
  public LayoutStyle getLayoutStyle()
  {
    return DefaultLayoutStyle.getInstance();
  }
  
  public void provideErrorFeedback(Component paramComponent)
  {
    Toolkit localToolkit = null;
    if (paramComponent != null) {
      localToolkit = paramComponent.getToolkit();
    } else {
      localToolkit = Toolkit.getDefaultToolkit();
    }
    localToolkit.beep();
  }
  
  public static Object getDesktopPropertyValue(String paramString, Object paramObject)
  {
    Object localObject = Toolkit.getDefaultToolkit().getDesktopProperty(paramString);
    if (localObject == null) {
      return paramObject;
    }
    if ((localObject instanceof Color)) {
      return new ColorUIResource((Color)localObject);
    }
    if ((localObject instanceof Font)) {
      return new FontUIResource((Font)localObject);
    }
    return localObject;
  }
  
  public Icon getDisabledIcon(JComponent paramJComponent, Icon paramIcon)
  {
    if ((paramIcon instanceof ImageIcon)) {
      return new ImageIconUIResource(GrayFilter.createDisabledImage(((ImageIcon)paramIcon).getImage()));
    }
    return null;
  }
  
  public Icon getDisabledSelectedIcon(JComponent paramJComponent, Icon paramIcon)
  {
    return getDisabledIcon(paramJComponent, paramIcon);
  }
  
  public abstract String getName();
  
  public abstract String getID();
  
  public abstract String getDescription();
  
  public boolean getSupportsWindowDecorations()
  {
    return false;
  }
  
  public abstract boolean isNativeLookAndFeel();
  
  public abstract boolean isSupportedLookAndFeel();
  
  public void initialize() {}
  
  public void uninitialize() {}
  
  public UIDefaults getDefaults()
  {
    return null;
  }
  
  public String toString()
  {
    return "[" + getDescription() + " - " + getClass().getName() + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\LookAndFeel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */