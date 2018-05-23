package javax.swing.plaf.nimbus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.AccessController;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.UIDefaults;
import javax.swing.UIDefaults.ActiveValue;
import javax.swing.UIManager;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthLookAndFeel;
import javax.swing.plaf.synth.SynthStyle;
import javax.swing.plaf.synth.SynthStyleFactory;
import sun.security.action.GetPropertyAction;
import sun.swing.ImageIconUIResource;
import sun.swing.plaf.GTKKeybindings;
import sun.swing.plaf.WindowsKeybindings;
import sun.swing.plaf.synth.SynthIcon;

public class NimbusLookAndFeel
  extends SynthLookAndFeel
{
  private static final String[] COMPONENT_KEYS = { "ArrowButton", "Button", "CheckBox", "CheckBoxMenuItem", "ColorChooser", "ComboBox", "DesktopPane", "DesktopIcon", "EditorPane", "FileChooser", "FormattedTextField", "InternalFrame", "InternalFrameTitlePane", "Label", "List", "Menu", "MenuBar", "MenuItem", "OptionPane", "Panel", "PasswordField", "PopupMenu", "PopupMenuSeparator", "ProgressBar", "RadioButton", "RadioButtonMenuItem", "RootPane", "ScrollBar", "ScrollBarTrack", "ScrollBarThumb", "ScrollPane", "Separator", "Slider", "SliderTrack", "SliderThumb", "Spinner", "SplitPane", "TabbedPane", "Table", "TableHeader", "TextArea", "TextField", "TextPane", "ToggleButton", "ToolBar", "ToolTip", "Tree", "Viewport" };
  private NimbusDefaults defaults = new NimbusDefaults();
  private UIDefaults uiDefaults;
  private DefaultsListener defaultsListener = new DefaultsListener(null);
  private Map<String, Map<String, Object>> compiledDefaults = null;
  private boolean defaultListenerAdded = false;
  
  public NimbusLookAndFeel() {}
  
  public void initialize()
  {
    super.initialize();
    defaults.initialize();
    setStyleFactory(new SynthStyleFactory()
    {
      public SynthStyle getStyle(JComponent paramAnonymousJComponent, Region paramAnonymousRegion)
      {
        return defaults.getStyle(paramAnonymousJComponent, paramAnonymousRegion);
      }
    });
  }
  
  public void uninitialize()
  {
    super.uninitialize();
    defaults.uninitialize();
    ImageCache.getInstance().flush();
    UIManager.getDefaults().removePropertyChangeListener(defaultsListener);
  }
  
  public UIDefaults getDefaults()
  {
    if (uiDefaults == null)
    {
      String str1 = getSystemProperty("os.name");
      int i = (str1 != null) && (str1.contains("Windows")) ? 1 : 0;
      uiDefaults = super.getDefaults();
      defaults.initializeDefaults(uiDefaults);
      if (i != 0) {
        WindowsKeybindings.installKeybindings(uiDefaults);
      } else {
        GTKKeybindings.installKeybindings(uiDefaults);
      }
      uiDefaults.put("TitledBorder.titlePosition", Integer.valueOf(1));
      uiDefaults.put("TitledBorder.border", new BorderUIResource(new LoweredBorder()));
      uiDefaults.put("TitledBorder.titleColor", getDerivedColor("text", 0.0F, 0.0F, 0.23F, 0, true));
      uiDefaults.put("TitledBorder.font", new NimbusDefaults.DerivedFont("defaultFont", 1.0F, Boolean.valueOf(true), null));
      uiDefaults.put("OptionPane.isYesLast", Boolean.valueOf(i == 0));
      uiDefaults.put("Table.scrollPaneCornerComponent", new UIDefaults.ActiveValue()
      {
        public Object createValue(UIDefaults paramAnonymousUIDefaults)
        {
          return new TableScrollPaneCorner();
        }
      });
      uiDefaults.put("ToolBarSeparator[Enabled].backgroundPainter", new ToolBarSeparatorPainter());
      for (String str2 : COMPONENT_KEYS)
      {
        String str3 = str2 + ".foreground";
        if (!uiDefaults.containsKey(str3)) {
          uiDefaults.put(str3, new NimbusProperty(str2, "textForeground", null));
        }
        str3 = str2 + ".background";
        if (!uiDefaults.containsKey(str3)) {
          uiDefaults.put(str3, new NimbusProperty(str2, "background", null));
        }
        str3 = str2 + ".font";
        if (!uiDefaults.containsKey(str3)) {
          uiDefaults.put(str3, new NimbusProperty(str2, "font", null));
        }
        str3 = str2 + ".disabledText";
        if (!uiDefaults.containsKey(str3)) {
          uiDefaults.put(str3, new NimbusProperty(str2, "Disabled", "textForeground", null));
        }
        str3 = str2 + ".disabled";
        if (!uiDefaults.containsKey(str3)) {
          uiDefaults.put(str3, new NimbusProperty(str2, "Disabled", "background", null));
        }
      }
      uiDefaults.put("FileView.computerIcon", new LinkProperty("FileChooser.homeFolderIcon", null));
      uiDefaults.put("FileView.directoryIcon", new LinkProperty("FileChooser.directoryIcon", null));
      uiDefaults.put("FileView.fileIcon", new LinkProperty("FileChooser.fileIcon", null));
      uiDefaults.put("FileView.floppyDriveIcon", new LinkProperty("FileChooser.floppyDriveIcon", null));
      uiDefaults.put("FileView.hardDriveIcon", new LinkProperty("FileChooser.hardDriveIcon", null));
    }
    return uiDefaults;
  }
  
  public static NimbusStyle getStyle(JComponent paramJComponent, Region paramRegion)
  {
    return (NimbusStyle)SynthLookAndFeel.getStyle(paramJComponent, paramRegion);
  }
  
  public String getName()
  {
    return "Nimbus";
  }
  
  public String getID()
  {
    return "Nimbus";
  }
  
  public String getDescription()
  {
    return "Nimbus Look and Feel";
  }
  
  public boolean shouldUpdateStyleOnAncestorChanged()
  {
    return true;
  }
  
  protected boolean shouldUpdateStyleOnEvent(PropertyChangeEvent paramPropertyChangeEvent)
  {
    String str = paramPropertyChangeEvent.getPropertyName();
    if (("name" == str) || ("ancestor" == str) || ("Nimbus.Overrides" == str) || ("Nimbus.Overrides.InheritDefaults" == str) || ("JComponent.sizeVariant" == str))
    {
      JComponent localJComponent = (JComponent)paramPropertyChangeEvent.getSource();
      defaults.clearOverridesCache(localJComponent);
      return true;
    }
    return super.shouldUpdateStyleOnEvent(paramPropertyChangeEvent);
  }
  
  public void register(Region paramRegion, String paramString)
  {
    defaults.register(paramRegion, paramString);
  }
  
  private String getSystemProperty(String paramString)
  {
    return (String)AccessController.doPrivileged(new GetPropertyAction(paramString));
  }
  
  public Icon getDisabledIcon(JComponent paramJComponent, Icon paramIcon)
  {
    if ((paramIcon instanceof SynthIcon))
    {
      SynthIcon localSynthIcon = (SynthIcon)paramIcon;
      BufferedImage localBufferedImage = EffectUtils.createCompatibleTranslucentImage(localSynthIcon.getIconWidth(), localSynthIcon.getIconHeight());
      Graphics2D localGraphics2D = localBufferedImage.createGraphics();
      localSynthIcon.paintIcon(paramJComponent, localGraphics2D, 0, 0);
      localGraphics2D.dispose();
      return new ImageIconUIResource(GrayFilter.createDisabledImage(localBufferedImage));
    }
    return super.getDisabledIcon(paramJComponent, paramIcon);
  }
  
  public Color getDerivedColor(String paramString, float paramFloat1, float paramFloat2, float paramFloat3, int paramInt, boolean paramBoolean)
  {
    return defaults.getDerivedColor(paramString, paramFloat1, paramFloat2, paramFloat3, paramInt, paramBoolean);
  }
  
  protected final Color getDerivedColor(Color paramColor1, Color paramColor2, float paramFloat, boolean paramBoolean)
  {
    int i = deriveARGB(paramColor1, paramColor2, paramFloat);
    if (paramBoolean) {
      return new ColorUIResource(i);
    }
    return new Color(i);
  }
  
  protected final Color getDerivedColor(Color paramColor1, Color paramColor2, float paramFloat)
  {
    return getDerivedColor(paramColor1, paramColor2, paramFloat, true);
  }
  
  static Object resolveToolbarConstraint(JToolBar paramJToolBar)
  {
    if (paramJToolBar != null)
    {
      Container localContainer = paramJToolBar.getParent();
      if (localContainer != null)
      {
        LayoutManager localLayoutManager = localContainer.getLayout();
        if ((localLayoutManager instanceof BorderLayout))
        {
          BorderLayout localBorderLayout = (BorderLayout)localLayoutManager;
          Object localObject = localBorderLayout.getConstraints(paramJToolBar);
          if ((localObject == "South") || (localObject == "East") || (localObject == "West")) {
            return localObject;
          }
          return "North";
        }
      }
    }
    return "North";
  }
  
  static int deriveARGB(Color paramColor1, Color paramColor2, float paramFloat)
  {
    int i = paramColor1.getRed() + Math.round((paramColor2.getRed() - paramColor1.getRed()) * paramFloat);
    int j = paramColor1.getGreen() + Math.round((paramColor2.getGreen() - paramColor1.getGreen()) * paramFloat);
    int k = paramColor1.getBlue() + Math.round((paramColor2.getBlue() - paramColor1.getBlue()) * paramFloat);
    int m = paramColor1.getAlpha() + Math.round((paramColor2.getAlpha() - paramColor1.getAlpha()) * paramFloat);
    return (m & 0xFF) << 24 | (i & 0xFF) << 16 | (j & 0xFF) << 8 | k & 0xFF;
  }
  
  static String parsePrefix(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    int i = 0;
    for (int j = 0; j < paramString.length(); j++)
    {
      int k = paramString.charAt(j);
      if (k == 34) {
        i = i == 0 ? 1 : 0;
      } else if (((k == 91) || (k == 46)) && (i == 0)) {
        return paramString.substring(0, j);
      }
    }
    return null;
  }
  
  Map<String, Object> getDefaultsForPrefix(String paramString)
  {
    if (compiledDefaults == null)
    {
      compiledDefaults = new HashMap();
      Iterator localIterator = UIManager.getDefaults().entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        if ((localEntry.getKey() instanceof String)) {
          addDefault((String)localEntry.getKey(), localEntry.getValue());
        }
      }
      if (!defaultListenerAdded)
      {
        UIManager.getDefaults().addPropertyChangeListener(defaultsListener);
        defaultListenerAdded = true;
      }
    }
    return (Map)compiledDefaults.get(paramString);
  }
  
  private void addDefault(String paramString, Object paramObject)
  {
    if (compiledDefaults == null) {
      return;
    }
    String str = parsePrefix(paramString);
    if (str != null)
    {
      Object localObject = (Map)compiledDefaults.get(str);
      if (localObject == null)
      {
        localObject = new HashMap();
        compiledDefaults.put(str, localObject);
      }
      ((Map)localObject).put(paramString, paramObject);
    }
  }
  
  private class DefaultsListener
    implements PropertyChangeListener
  {
    private DefaultsListener() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      if ("UIDefaults".equals(str)) {
        compiledDefaults = null;
      } else {
        NimbusLookAndFeel.this.addDefault(str, paramPropertyChangeEvent.getNewValue());
      }
    }
  }
  
  private class LinkProperty
    implements UIDefaults.ActiveValue, UIResource
  {
    private String dstPropName;
    
    private LinkProperty(String paramString)
    {
      dstPropName = paramString;
    }
    
    public Object createValue(UIDefaults paramUIDefaults)
    {
      return UIManager.get(dstPropName);
    }
  }
  
  private class NimbusProperty
    implements UIDefaults.ActiveValue, UIResource
  {
    private String prefix;
    private String state = null;
    private String suffix;
    private boolean isFont;
    
    private NimbusProperty(String paramString1, String paramString2)
    {
      prefix = paramString1;
      suffix = paramString2;
      isFont = "font".equals(paramString2);
    }
    
    private NimbusProperty(String paramString1, String paramString2, String paramString3)
    {
      this(paramString1, paramString3);
    }
    
    public Object createValue(UIDefaults paramUIDefaults)
    {
      Object localObject = null;
      if (state != null) {
        localObject = uiDefaults.get(prefix + "[" + state + "]." + suffix);
      }
      if (localObject == null) {
        localObject = uiDefaults.get(prefix + "[Enabled]." + suffix);
      }
      if (localObject == null) {
        if (isFont) {
          localObject = uiDefaults.get("defaultFont");
        } else {
          localObject = uiDefaults.get(suffix);
        }
      }
      return localObject;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\NimbusLookAndFeel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */