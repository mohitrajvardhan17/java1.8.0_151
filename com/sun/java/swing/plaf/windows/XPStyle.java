package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.security.AccessController;
import java.util.HashMap;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.CellRendererPane;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;
import sun.awt.image.SunWritableRaster;
import sun.awt.windows.ThemeReader;
import sun.security.action.GetPropertyAction;
import sun.swing.CachedPainter;

class XPStyle
{
  private static XPStyle xp;
  private static SkinPainter skinPainter;
  private static Boolean themeActive;
  private HashMap<String, Border> borderMap = new HashMap();
  private HashMap<String, Color> colorMap = new HashMap();
  private boolean flatMenus = getSysBoolean(TMSchema.Prop.FLATMENUS);
  
  static synchronized void invalidateStyle()
  {
    xp = null;
    themeActive = null;
    skinPainter.flush();
  }
  
  static synchronized XPStyle getXP()
  {
    if (themeActive == null)
    {
      Toolkit localToolkit = Toolkit.getDefaultToolkit();
      themeActive = (Boolean)localToolkit.getDesktopProperty("win.xpstyle.themeActive");
      if (themeActive == null) {
        themeActive = Boolean.FALSE;
      }
      if (themeActive.booleanValue())
      {
        GetPropertyAction localGetPropertyAction = new GetPropertyAction("swing.noxp");
        if ((AccessController.doPrivileged(localGetPropertyAction) == null) && (ThemeReader.isThemed()) && (!(UIManager.getLookAndFeel() instanceof WindowsClassicLookAndFeel))) {
          xp = new XPStyle();
        }
      }
    }
    return ThemeReader.isXPStyleEnabled() ? xp : null;
  }
  
  static boolean isVista()
  {
    XPStyle localXPStyle = getXP();
    return (localXPStyle != null) && (localXPStyle.isSkinDefined(null, TMSchema.Part.CP_DROPDOWNBUTTONRIGHT));
  }
  
  String getString(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp)
  {
    return getTypeEnumName(paramComponent, paramPart, paramState, paramProp);
  }
  
  TMSchema.TypeEnum getTypeEnum(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp)
  {
    int i = ThemeReader.getEnum(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState), paramProp.getValue());
    return TMSchema.TypeEnum.getTypeEnum(paramProp, i);
  }
  
  private static String getTypeEnumName(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp)
  {
    int i = ThemeReader.getEnum(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState), paramProp.getValue());
    if (i == -1) {
      return null;
    }
    return TMSchema.TypeEnum.getTypeEnum(paramProp, i).getName();
  }
  
  int getInt(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp, int paramInt)
  {
    return ThemeReader.getInt(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState), paramProp.getValue());
  }
  
  Dimension getDimension(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp)
  {
    Dimension localDimension = ThemeReader.getPosition(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState), paramProp.getValue());
    return localDimension != null ? localDimension : new Dimension();
  }
  
  Point getPoint(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp)
  {
    Dimension localDimension = ThemeReader.getPosition(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState), paramProp.getValue());
    return localDimension != null ? new Point(width, height) : new Point();
  }
  
  Insets getMargin(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp)
  {
    Insets localInsets = ThemeReader.getThemeMargins(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState), paramProp.getValue());
    return localInsets != null ? localInsets : new Insets(0, 0, 0, 0);
  }
  
  synchronized Color getColor(Skin paramSkin, TMSchema.Prop paramProp, Color paramColor)
  {
    String str = paramSkin.toString() + "." + paramProp.name();
    TMSchema.Part localPart = part;
    Object localObject = (Color)colorMap.get(str);
    if (localObject == null)
    {
      localObject = ThemeReader.getColor(localPart.getControlName(null), localPart.getValue(), TMSchema.State.getValue(localPart, state), paramProp.getValue());
      if (localObject != null)
      {
        localObject = new ColorUIResource((Color)localObject);
        colorMap.put(str, localObject);
      }
    }
    return (Color)(localObject != null ? localObject : paramColor);
  }
  
  Color getColor(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp, Color paramColor)
  {
    return getColor(new Skin(paramComponent, paramPart, paramState), paramProp, paramColor);
  }
  
  synchronized Border getBorder(Component paramComponent, TMSchema.Part paramPart)
  {
    if (paramPart == TMSchema.Part.MENU)
    {
      if (flatMenus) {
        return new XPFillBorder(UIManager.getColor("InternalFrame.borderShadow"), 1);
      }
      return null;
    }
    Skin localSkin = new Skin(paramComponent, paramPart, null);
    Object localObject = (Border)borderMap.get(string);
    if (localObject == null)
    {
      String str = getTypeEnumName(paramComponent, paramPart, null, TMSchema.Prop.BGTYPE);
      if ("borderfill".equalsIgnoreCase(str))
      {
        int i = getInt(paramComponent, paramPart, null, TMSchema.Prop.BORDERSIZE, 1);
        Color localColor = getColor(localSkin, TMSchema.Prop.BORDERCOLOR, Color.black);
        localObject = new XPFillBorder(localColor, i);
        if (paramPart == TMSchema.Part.CP_COMBOBOX) {
          localObject = new XPStatefulFillBorder(localColor, i, paramPart, TMSchema.Prop.BORDERCOLOR);
        }
      }
      else if ("imagefile".equalsIgnoreCase(str))
      {
        Insets localInsets = getMargin(paramComponent, paramPart, null, TMSchema.Prop.SIZINGMARGINS);
        if (localInsets != null) {
          if (getBoolean(paramComponent, paramPart, null, TMSchema.Prop.BORDERONLY)) {
            localObject = new XPImageBorder(paramComponent, paramPart);
          } else if (paramPart == TMSchema.Part.CP_COMBOBOX) {
            localObject = new EmptyBorder(1, 1, 1, 1);
          } else if (paramPart == TMSchema.Part.TP_BUTTON) {
            localObject = new XPEmptyBorder(new Insets(3, 3, 3, 3));
          } else {
            localObject = new XPEmptyBorder(localInsets);
          }
        }
      }
      if (localObject != null) {
        borderMap.put(string, localObject);
      }
    }
    return (Border)localObject;
  }
  
  boolean isSkinDefined(Component paramComponent, TMSchema.Part paramPart)
  {
    return (paramPart.getValue() == 0) || (ThemeReader.isThemePartDefined(paramPart.getControlName(paramComponent), paramPart.getValue(), 0));
  }
  
  synchronized Skin getSkin(Component paramComponent, TMSchema.Part paramPart)
  {
    assert (isSkinDefined(paramComponent, paramPart)) : ("part " + paramPart + " is not defined");
    return new Skin(paramComponent, paramPart, null);
  }
  
  long getThemeTransitionDuration(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState1, TMSchema.State paramState2, TMSchema.Prop paramProp)
  {
    return ThemeReader.getThemeTransitionDuration(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState1), TMSchema.State.getValue(paramPart, paramState2), paramProp != null ? paramProp.getValue() : 0);
  }
  
  private XPStyle() {}
  
  private boolean getBoolean(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState, TMSchema.Prop paramProp)
  {
    return ThemeReader.getBoolean(paramPart.getControlName(paramComponent), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState), paramProp.getValue());
  }
  
  static Dimension getPartSize(TMSchema.Part paramPart, TMSchema.State paramState)
  {
    return ThemeReader.getPartSize(paramPart.getControlName(null), paramPart.getValue(), TMSchema.State.getValue(paramPart, paramState));
  }
  
  private static boolean getSysBoolean(TMSchema.Prop paramProp)
  {
    return ThemeReader.getSysBoolean("window", paramProp.getValue());
  }
  
  static
  {
    skinPainter = new SkinPainter();
    themeActive = null;
    invalidateStyle();
  }
  
  static class GlyphButton
    extends JButton
  {
    private XPStyle.Skin skin;
    
    public GlyphButton(Component paramComponent, TMSchema.Part paramPart)
    {
      XPStyle localXPStyle = XPStyle.getXP();
      skin = (localXPStyle != null ? localXPStyle.getSkin(paramComponent, paramPart) : null);
      setBorder(null);
      setContentAreaFilled(false);
      setMinimumSize(new Dimension(5, 5));
      setPreferredSize(new Dimension(16, 16));
      setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }
    
    public boolean isFocusTraversable()
    {
      return false;
    }
    
    protected TMSchema.State getState()
    {
      TMSchema.State localState = TMSchema.State.NORMAL;
      if (!isEnabled()) {
        localState = TMSchema.State.DISABLED;
      } else if (getModel().isPressed()) {
        localState = TMSchema.State.PRESSED;
      } else if (getModel().isRollover()) {
        localState = TMSchema.State.HOT;
      }
      return localState;
    }
    
    public void paintComponent(Graphics paramGraphics)
    {
      if ((XPStyle.getXP() == null) || (skin == null)) {
        return;
      }
      Dimension localDimension = getSize();
      skin.paintSkin(paramGraphics, 0, 0, width, height, getState());
    }
    
    public void setPart(Component paramComponent, TMSchema.Part paramPart)
    {
      XPStyle localXPStyle = XPStyle.getXP();
      skin = (localXPStyle != null ? localXPStyle.getSkin(paramComponent, paramPart) : null);
      revalidate();
      repaint();
    }
    
    protected void paintBorder(Graphics paramGraphics) {}
  }
  
  static class Skin
  {
    final Component component;
    final TMSchema.Part part;
    final TMSchema.State state;
    private final String string;
    private Dimension size = null;
    
    Skin(Component paramComponent, TMSchema.Part paramPart)
    {
      this(paramComponent, paramPart, null);
    }
    
    Skin(TMSchema.Part paramPart, TMSchema.State paramState)
    {
      this(null, paramPart, paramState);
    }
    
    Skin(Component paramComponent, TMSchema.Part paramPart, TMSchema.State paramState)
    {
      component = paramComponent;
      part = paramPart;
      state = paramState;
      String str = paramPart.getControlName(paramComponent) + "." + paramPart.name();
      if (paramState != null) {
        str = str + "(" + paramState.name() + ")";
      }
      string = str;
    }
    
    Insets getContentMargin()
    {
      int i = 100;
      int j = 100;
      Insets localInsets = ThemeReader.getThemeBackgroundContentMargins(part.getControlName(null), part.getValue(), 0, i, j);
      return localInsets != null ? localInsets : new Insets(0, 0, 0, 0);
    }
    
    private int getWidth(TMSchema.State paramState)
    {
      if (size == null) {
        size = XPStyle.getPartSize(part, paramState);
      }
      return size != null ? size.width : 0;
    }
    
    int getWidth()
    {
      return getWidth(state != null ? state : TMSchema.State.NORMAL);
    }
    
    private int getHeight(TMSchema.State paramState)
    {
      if (size == null) {
        size = XPStyle.getPartSize(part, paramState);
      }
      return size != null ? size.height : 0;
    }
    
    int getHeight()
    {
      return getHeight(state != null ? state : TMSchema.State.NORMAL);
    }
    
    public String toString()
    {
      return string;
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof Skin)) && (string.equals(string));
    }
    
    public int hashCode()
    {
      return string.hashCode();
    }
    
    void paintSkin(Graphics paramGraphics, int paramInt1, int paramInt2, TMSchema.State paramState)
    {
      if (paramState == null) {
        paramState = state;
      }
      paintSkin(paramGraphics, paramInt1, paramInt2, getWidth(paramState), getHeight(paramState), paramState);
    }
    
    void paintSkin(Graphics paramGraphics, Rectangle paramRectangle, TMSchema.State paramState)
    {
      paintSkin(paramGraphics, x, y, width, height, paramState);
    }
    
    void paintSkin(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, TMSchema.State paramState)
    {
      if (XPStyle.getXP() == null) {
        return;
      }
      if ((ThemeReader.isGetThemeTransitionDurationDefined()) && ((component instanceof JComponent)) && (SwingUtilities.getAncestorOfClass(CellRendererPane.class, component) == null)) {
        AnimationController.paintSkin((JComponent)component, this, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramState);
      } else {
        paintSkinRaw(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramState);
      }
    }
    
    void paintSkinRaw(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, TMSchema.State paramState)
    {
      if (XPStyle.getXP() == null) {
        return;
      }
      XPStyle.skinPainter.paint(null, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { this, paramState });
    }
    
    void paintSkin(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, TMSchema.State paramState, boolean paramBoolean)
    {
      if (XPStyle.getXP() == null) {
        return;
      }
      if ((paramBoolean) && ("borderfill".equals(XPStyle.getTypeEnumName(component, part, paramState, TMSchema.Prop.BGTYPE)))) {
        return;
      }
      XPStyle.skinPainter.paint(null, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { this, paramState });
    }
  }
  
  private static class SkinPainter
    extends CachedPainter
  {
    SkinPainter()
    {
      super();
      flush();
    }
    
    public void flush()
    {
      super.flush();
    }
    
    protected void paintToImage(Component paramComponent, Image paramImage, Graphics paramGraphics, int paramInt1, int paramInt2, Object[] paramArrayOfObject)
    {
      int i = 0;
      XPStyle.Skin localSkin = (XPStyle.Skin)paramArrayOfObject[0];
      TMSchema.Part localPart = part;
      TMSchema.State localState = (TMSchema.State)paramArrayOfObject[1];
      if (localState == null) {
        localState = state;
      }
      if (paramComponent == null) {
        paramComponent = component;
      }
      BufferedImage localBufferedImage = (BufferedImage)paramImage;
      WritableRaster localWritableRaster = localBufferedImage.getRaster();
      DataBufferInt localDataBufferInt = (DataBufferInt)localWritableRaster.getDataBuffer();
      ThemeReader.paintBackground(SunWritableRaster.stealData(localDataBufferInt, 0), localPart.getControlName(paramComponent), localPart.getValue(), TMSchema.State.getValue(localPart, localState), 0, 0, paramInt1, paramInt2, paramInt1);
      SunWritableRaster.markDirty(localDataBufferInt);
    }
    
    protected Image createImage(Component paramComponent, int paramInt1, int paramInt2, GraphicsConfiguration paramGraphicsConfiguration, Object[] paramArrayOfObject)
    {
      return new BufferedImage(paramInt1, paramInt2, 2);
    }
  }
  
  private class XPEmptyBorder
    extends EmptyBorder
    implements UIResource
  {
    XPEmptyBorder(Insets paramInsets)
    {
      super(left + 2, bottom + 2, right + 2);
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      paramInsets = super.getBorderInsets(paramComponent, paramInsets);
      Object localObject = null;
      if ((paramComponent instanceof AbstractButton))
      {
        Insets localInsets = ((AbstractButton)paramComponent).getMargin();
        if (((paramComponent.getParent() instanceof JToolBar)) && (!(paramComponent instanceof JRadioButton)) && (!(paramComponent instanceof JCheckBox)) && ((localInsets instanceof InsetsUIResource)))
        {
          top -= 2;
          left -= 2;
          bottom -= 2;
          right -= 2;
        }
        else
        {
          localObject = localInsets;
        }
      }
      else if ((paramComponent instanceof JToolBar))
      {
        localObject = ((JToolBar)paramComponent).getMargin();
      }
      else if ((paramComponent instanceof JTextComponent))
      {
        localObject = ((JTextComponent)paramComponent).getMargin();
      }
      if (localObject != null)
      {
        top = (top + 2);
        left = (left + 2);
        bottom = (bottom + 2);
        right = (right + 2);
      }
      return paramInsets;
    }
  }
  
  private class XPFillBorder
    extends LineBorder
    implements UIResource
  {
    XPFillBorder(Color paramColor, int paramInt)
    {
      super(paramInt);
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      Insets localInsets = null;
      if ((paramComponent instanceof AbstractButton)) {
        localInsets = ((AbstractButton)paramComponent).getMargin();
      } else if ((paramComponent instanceof JToolBar)) {
        localInsets = ((JToolBar)paramComponent).getMargin();
      } else if ((paramComponent instanceof JTextComponent)) {
        localInsets = ((JTextComponent)paramComponent).getMargin();
      }
      top = ((localInsets != null ? top : 0) + thickness);
      left = ((localInsets != null ? left : 0) + thickness);
      bottom = ((localInsets != null ? bottom : 0) + thickness);
      right = ((localInsets != null ? right : 0) + thickness);
      return paramInsets;
    }
  }
  
  private class XPImageBorder
    extends AbstractBorder
    implements UIResource
  {
    XPStyle.Skin skin;
    
    XPImageBorder(Component paramComponent, TMSchema.Part paramPart)
    {
      skin = getSkin(paramComponent, paramPart);
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      skin.paintSkin(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      Insets localInsets1 = null;
      Insets localInsets2 = skin.getContentMargin();
      if (localInsets2 == null) {
        localInsets2 = new Insets(0, 0, 0, 0);
      }
      if ((paramComponent instanceof AbstractButton)) {
        localInsets1 = ((AbstractButton)paramComponent).getMargin();
      } else if ((paramComponent instanceof JToolBar)) {
        localInsets1 = ((JToolBar)paramComponent).getMargin();
      } else if ((paramComponent instanceof JTextComponent)) {
        localInsets1 = ((JTextComponent)paramComponent).getMargin();
      }
      top = ((localInsets1 != null ? top : 0) + top);
      left = ((localInsets1 != null ? left : 0) + left);
      bottom = ((localInsets1 != null ? bottom : 0) + bottom);
      right = ((localInsets1 != null ? right : 0) + right);
      return paramInsets;
    }
  }
  
  private class XPStatefulFillBorder
    extends XPStyle.XPFillBorder
  {
    private final TMSchema.Part part;
    private final TMSchema.Prop prop;
    
    XPStatefulFillBorder(Color paramColor, int paramInt, TMSchema.Part paramPart, TMSchema.Prop paramProp)
    {
      super(paramColor, paramInt);
      part = paramPart;
      prop = paramProp;
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      TMSchema.State localState = TMSchema.State.NORMAL;
      if ((paramComponent instanceof JComboBox))
      {
        JComboBox localJComboBox = (JComboBox)paramComponent;
        if ((localJComboBox.getUI() instanceof WindowsComboBoxUI))
        {
          WindowsComboBoxUI localWindowsComboBoxUI = (WindowsComboBoxUI)localJComboBox.getUI();
          localState = localWindowsComboBoxUI.getXPComboBoxState(localJComboBox);
        }
      }
      lineColor = getColor(paramComponent, part, localState, prop, Color.black);
      super.paintBorder(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\windows\XPStyle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */