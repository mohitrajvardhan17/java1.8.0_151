package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.BorderUIResource.CompoundBorderUIResource;
import javax.swing.plaf.BorderUIResource.LineBorderUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;
import sun.swing.SwingUtilities2;

public class BasicBorders
{
  public BasicBorders() {}
  
  public static Border getButtonBorder()
  {
    UIDefaults localUIDefaults = UIManager.getLookAndFeelDefaults();
    BorderUIResource.CompoundBorderUIResource localCompoundBorderUIResource = new BorderUIResource.CompoundBorderUIResource(new ButtonBorder(localUIDefaults.getColor("Button.shadow"), localUIDefaults.getColor("Button.darkShadow"), localUIDefaults.getColor("Button.light"), localUIDefaults.getColor("Button.highlight")), new MarginBorder());
    return localCompoundBorderUIResource;
  }
  
  public static Border getRadioButtonBorder()
  {
    UIDefaults localUIDefaults = UIManager.getLookAndFeelDefaults();
    BorderUIResource.CompoundBorderUIResource localCompoundBorderUIResource = new BorderUIResource.CompoundBorderUIResource(new RadioButtonBorder(localUIDefaults.getColor("RadioButton.shadow"), localUIDefaults.getColor("RadioButton.darkShadow"), localUIDefaults.getColor("RadioButton.light"), localUIDefaults.getColor("RadioButton.highlight")), new MarginBorder());
    return localCompoundBorderUIResource;
  }
  
  public static Border getToggleButtonBorder()
  {
    UIDefaults localUIDefaults = UIManager.getLookAndFeelDefaults();
    BorderUIResource.CompoundBorderUIResource localCompoundBorderUIResource = new BorderUIResource.CompoundBorderUIResource(new ToggleButtonBorder(localUIDefaults.getColor("ToggleButton.shadow"), localUIDefaults.getColor("ToggleButton.darkShadow"), localUIDefaults.getColor("ToggleButton.light"), localUIDefaults.getColor("ToggleButton.highlight")), new MarginBorder());
    return localCompoundBorderUIResource;
  }
  
  public static Border getMenuBarBorder()
  {
    UIDefaults localUIDefaults = UIManager.getLookAndFeelDefaults();
    MenuBarBorder localMenuBarBorder = new MenuBarBorder(localUIDefaults.getColor("MenuBar.shadow"), localUIDefaults.getColor("MenuBar.highlight"));
    return localMenuBarBorder;
  }
  
  public static Border getSplitPaneBorder()
  {
    UIDefaults localUIDefaults = UIManager.getLookAndFeelDefaults();
    SplitPaneBorder localSplitPaneBorder = new SplitPaneBorder(localUIDefaults.getColor("SplitPane.highlight"), localUIDefaults.getColor("SplitPane.darkShadow"));
    return localSplitPaneBorder;
  }
  
  public static Border getSplitPaneDividerBorder()
  {
    UIDefaults localUIDefaults = UIManager.getLookAndFeelDefaults();
    SplitPaneDividerBorder localSplitPaneDividerBorder = new SplitPaneDividerBorder(localUIDefaults.getColor("SplitPane.highlight"), localUIDefaults.getColor("SplitPane.darkShadow"));
    return localSplitPaneDividerBorder;
  }
  
  public static Border getTextFieldBorder()
  {
    UIDefaults localUIDefaults = UIManager.getLookAndFeelDefaults();
    FieldBorder localFieldBorder = new FieldBorder(localUIDefaults.getColor("TextField.shadow"), localUIDefaults.getColor("TextField.darkShadow"), localUIDefaults.getColor("TextField.light"), localUIDefaults.getColor("TextField.highlight"));
    return localFieldBorder;
  }
  
  public static Border getProgressBarBorder()
  {
    UIDefaults localUIDefaults = UIManager.getLookAndFeelDefaults();
    BorderUIResource.LineBorderUIResource localLineBorderUIResource = new BorderUIResource.LineBorderUIResource(Color.green, 2);
    return localLineBorderUIResource;
  }
  
  public static Border getInternalFrameBorder()
  {
    UIDefaults localUIDefaults = UIManager.getLookAndFeelDefaults();
    BorderUIResource.CompoundBorderUIResource localCompoundBorderUIResource = new BorderUIResource.CompoundBorderUIResource(new BevelBorder(0, localUIDefaults.getColor("InternalFrame.borderLight"), localUIDefaults.getColor("InternalFrame.borderHighlight"), localUIDefaults.getColor("InternalFrame.borderDarkShadow"), localUIDefaults.getColor("InternalFrame.borderShadow")), BorderFactory.createLineBorder(localUIDefaults.getColor("InternalFrame.borderColor"), 1));
    return localCompoundBorderUIResource;
  }
  
  public static class ButtonBorder
    extends AbstractBorder
    implements UIResource
  {
    protected Color shadow;
    protected Color darkShadow;
    protected Color highlight;
    protected Color lightHighlight;
    
    public ButtonBorder(Color paramColor1, Color paramColor2, Color paramColor3, Color paramColor4)
    {
      shadow = paramColor1;
      darkShadow = paramColor2;
      highlight = paramColor3;
      lightHighlight = paramColor4;
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      boolean bool1 = false;
      boolean bool2 = false;
      if ((paramComponent instanceof AbstractButton))
      {
        AbstractButton localAbstractButton = (AbstractButton)paramComponent;
        ButtonModel localButtonModel = localAbstractButton.getModel();
        bool1 = (localButtonModel.isPressed()) && (localButtonModel.isArmed());
        if ((paramComponent instanceof JButton)) {
          bool2 = ((JButton)paramComponent).isDefaultButton();
        }
      }
      BasicGraphicsUtils.drawBezel(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, bool1, bool2, shadow, darkShadow, highlight, lightHighlight);
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      paramInsets.set(2, 3, 3, 3);
      return paramInsets;
    }
  }
  
  public static class FieldBorder
    extends AbstractBorder
    implements UIResource
  {
    protected Color shadow;
    protected Color darkShadow;
    protected Color highlight;
    protected Color lightHighlight;
    
    public FieldBorder(Color paramColor1, Color paramColor2, Color paramColor3, Color paramColor4)
    {
      shadow = paramColor1;
      highlight = paramColor3;
      darkShadow = paramColor2;
      lightHighlight = paramColor4;
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      BasicGraphicsUtils.drawEtchedRect(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, shadow, darkShadow, highlight, lightHighlight);
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      Insets localInsets = null;
      if ((paramComponent instanceof JTextComponent)) {
        localInsets = ((JTextComponent)paramComponent).getMargin();
      }
      top = (localInsets != null ? 2 + top : 2);
      left = (localInsets != null ? 2 + left : 2);
      bottom = (localInsets != null ? 2 + bottom : 2);
      right = (localInsets != null ? 2 + right : 2);
      return paramInsets;
    }
  }
  
  public static class MarginBorder
    extends AbstractBorder
    implements UIResource
  {
    public MarginBorder() {}
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      Insets localInsets = null;
      Object localObject;
      if ((paramComponent instanceof AbstractButton))
      {
        localObject = (AbstractButton)paramComponent;
        localInsets = ((AbstractButton)localObject).getMargin();
      }
      else if ((paramComponent instanceof JToolBar))
      {
        localObject = (JToolBar)paramComponent;
        localInsets = ((JToolBar)localObject).getMargin();
      }
      else if ((paramComponent instanceof JTextComponent))
      {
        localObject = (JTextComponent)paramComponent;
        localInsets = ((JTextComponent)localObject).getMargin();
      }
      top = (localInsets != null ? top : 0);
      left = (localInsets != null ? left : 0);
      bottom = (localInsets != null ? bottom : 0);
      right = (localInsets != null ? right : 0);
      return paramInsets;
    }
  }
  
  public static class MenuBarBorder
    extends AbstractBorder
    implements UIResource
  {
    private Color shadow;
    private Color highlight;
    
    public MenuBarBorder(Color paramColor1, Color paramColor2)
    {
      shadow = paramColor1;
      highlight = paramColor2;
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Color localColor = paramGraphics.getColor();
      paramGraphics.translate(paramInt1, paramInt2);
      paramGraphics.setColor(shadow);
      SwingUtilities2.drawHLine(paramGraphics, 0, paramInt3 - 1, paramInt4 - 2);
      paramGraphics.setColor(highlight);
      SwingUtilities2.drawHLine(paramGraphics, 0, paramInt3 - 1, paramInt4 - 1);
      paramGraphics.translate(-paramInt1, -paramInt2);
      paramGraphics.setColor(localColor);
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      paramInsets.set(0, 0, 2, 0);
      return paramInsets;
    }
  }
  
  public static class RadioButtonBorder
    extends BasicBorders.ButtonBorder
  {
    public RadioButtonBorder(Color paramColor1, Color paramColor2, Color paramColor3, Color paramColor4)
    {
      super(paramColor2, paramColor3, paramColor4);
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if ((paramComponent instanceof AbstractButton))
      {
        AbstractButton localAbstractButton = (AbstractButton)paramComponent;
        ButtonModel localButtonModel = localAbstractButton.getModel();
        if (((localButtonModel.isArmed()) && (localButtonModel.isPressed())) || (localButtonModel.isSelected())) {
          BasicGraphicsUtils.drawLoweredBezel(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, shadow, darkShadow, highlight, lightHighlight);
        } else {
          BasicGraphicsUtils.drawBezel(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, false, (localAbstractButton.isFocusPainted()) && (localAbstractButton.hasFocus()), shadow, darkShadow, highlight, lightHighlight);
        }
      }
      else
      {
        BasicGraphicsUtils.drawBezel(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, false, false, shadow, darkShadow, highlight, lightHighlight);
      }
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      paramInsets.set(2, 2, 2, 2);
      return paramInsets;
    }
  }
  
  public static class RolloverButtonBorder
    extends BasicBorders.ButtonBorder
  {
    public RolloverButtonBorder(Color paramColor1, Color paramColor2, Color paramColor3, Color paramColor4)
    {
      super(paramColor2, paramColor3, paramColor4);
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      AbstractButton localAbstractButton = (AbstractButton)paramComponent;
      ButtonModel localButtonModel = localAbstractButton.getModel();
      Color localColor1 = shadow;
      Container localContainer = localAbstractButton.getParent();
      if ((localContainer != null) && (localContainer.getBackground().equals(shadow))) {
        localColor1 = darkShadow;
      }
      if (((localButtonModel.isRollover()) && ((!localButtonModel.isPressed()) || (localButtonModel.isArmed()))) || (localButtonModel.isSelected()))
      {
        Color localColor2 = paramGraphics.getColor();
        paramGraphics.translate(paramInt1, paramInt2);
        if (((localButtonModel.isPressed()) && (localButtonModel.isArmed())) || (localButtonModel.isSelected()))
        {
          paramGraphics.setColor(localColor1);
          paramGraphics.drawRect(0, 0, paramInt3 - 1, paramInt4 - 1);
          paramGraphics.setColor(lightHighlight);
          paramGraphics.drawLine(paramInt3 - 1, 0, paramInt3 - 1, paramInt4 - 1);
          paramGraphics.drawLine(0, paramInt4 - 1, paramInt3 - 1, paramInt4 - 1);
        }
        else
        {
          paramGraphics.setColor(lightHighlight);
          paramGraphics.drawRect(0, 0, paramInt3 - 1, paramInt4 - 1);
          paramGraphics.setColor(localColor1);
          paramGraphics.drawLine(paramInt3 - 1, 0, paramInt3 - 1, paramInt4 - 1);
          paramGraphics.drawLine(0, paramInt4 - 1, paramInt3 - 1, paramInt4 - 1);
        }
        paramGraphics.translate(-paramInt1, -paramInt2);
        paramGraphics.setColor(localColor2);
      }
    }
  }
  
  static class RolloverMarginBorder
    extends EmptyBorder
  {
    public RolloverMarginBorder()
    {
      super(3, 3, 3);
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      Insets localInsets = null;
      if ((paramComponent instanceof AbstractButton)) {
        localInsets = ((AbstractButton)paramComponent).getMargin();
      }
      if ((localInsets == null) || ((localInsets instanceof UIResource)))
      {
        left = left;
        top = top;
        right = right;
        bottom = bottom;
      }
      else
      {
        left = left;
        top = top;
        right = right;
        bottom = bottom;
      }
      return paramInsets;
    }
  }
  
  public static class SplitPaneBorder
    implements Border, UIResource
  {
    protected Color highlight;
    protected Color shadow;
    
    public SplitPaneBorder(Color paramColor1, Color paramColor2)
    {
      highlight = paramColor1;
      shadow = paramColor2;
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (!(paramComponent instanceof JSplitPane)) {
        return;
      }
      JSplitPane localJSplitPane = (JSplitPane)paramComponent;
      Component localComponent = localJSplitPane.getLeftComponent();
      paramGraphics.setColor(paramComponent.getBackground());
      paramGraphics.drawRect(paramInt1, paramInt2, paramInt3 - 1, paramInt4 - 1);
      Rectangle localRectangle;
      int i;
      int j;
      if (localJSplitPane.getOrientation() == 1)
      {
        if (localComponent != null)
        {
          localRectangle = localComponent.getBounds();
          paramGraphics.setColor(shadow);
          paramGraphics.drawLine(0, 0, width + 1, 0);
          paramGraphics.drawLine(0, 1, 0, height + 1);
          paramGraphics.setColor(highlight);
          paramGraphics.drawLine(0, height + 1, width + 1, height + 1);
        }
        localComponent = localJSplitPane.getRightComponent();
        if (localComponent != null)
        {
          localRectangle = localComponent.getBounds();
          i = x + width;
          j = y + height;
          paramGraphics.setColor(shadow);
          paramGraphics.drawLine(x - 1, 0, i, 0);
          paramGraphics.setColor(highlight);
          paramGraphics.drawLine(x - 1, j, i, j);
          paramGraphics.drawLine(i, 0, i, j + 1);
        }
      }
      else
      {
        if (localComponent != null)
        {
          localRectangle = localComponent.getBounds();
          paramGraphics.setColor(shadow);
          paramGraphics.drawLine(0, 0, width + 1, 0);
          paramGraphics.drawLine(0, 1, 0, height);
          paramGraphics.setColor(highlight);
          paramGraphics.drawLine(1 + width, 0, 1 + width, height + 1);
          paramGraphics.drawLine(0, height + 1, 0, height + 1);
        }
        localComponent = localJSplitPane.getRightComponent();
        if (localComponent != null)
        {
          localRectangle = localComponent.getBounds();
          i = x + width;
          j = y + height;
          paramGraphics.setColor(shadow);
          paramGraphics.drawLine(0, y - 1, 0, j);
          paramGraphics.drawLine(i, y - 1, i, y - 1);
          paramGraphics.setColor(highlight);
          paramGraphics.drawLine(0, j, width + 1, j);
          paramGraphics.drawLine(i, y, i, j);
        }
      }
    }
    
    public Insets getBorderInsets(Component paramComponent)
    {
      return new Insets(1, 1, 1, 1);
    }
    
    public boolean isBorderOpaque()
    {
      return true;
    }
  }
  
  static class SplitPaneDividerBorder
    implements Border, UIResource
  {
    Color highlight;
    Color shadow;
    
    SplitPaneDividerBorder(Color paramColor1, Color paramColor2)
    {
      highlight = paramColor1;
      shadow = paramColor2;
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (!(paramComponent instanceof BasicSplitPaneDivider)) {
        return;
      }
      JSplitPane localJSplitPane = ((BasicSplitPaneDivider)paramComponent).getBasicSplitPaneUI().getSplitPane();
      Dimension localDimension = paramComponent.getSize();
      Component localComponent = localJSplitPane.getLeftComponent();
      paramGraphics.setColor(paramComponent.getBackground());
      paramGraphics.drawRect(paramInt1, paramInt2, paramInt3 - 1, paramInt4 - 1);
      if (localJSplitPane.getOrientation() == 1)
      {
        if (localComponent != null)
        {
          paramGraphics.setColor(highlight);
          paramGraphics.drawLine(0, 0, 0, height);
        }
        localComponent = localJSplitPane.getRightComponent();
        if (localComponent != null)
        {
          paramGraphics.setColor(shadow);
          paramGraphics.drawLine(width - 1, 0, width - 1, height);
        }
      }
      else
      {
        if (localComponent != null)
        {
          paramGraphics.setColor(highlight);
          paramGraphics.drawLine(0, 0, width, 0);
        }
        localComponent = localJSplitPane.getRightComponent();
        if (localComponent != null)
        {
          paramGraphics.setColor(shadow);
          paramGraphics.drawLine(0, height - 1, width, height - 1);
        }
      }
    }
    
    public Insets getBorderInsets(Component paramComponent)
    {
      Insets localInsets = new Insets(0, 0, 0, 0);
      if ((paramComponent instanceof BasicSplitPaneDivider))
      {
        BasicSplitPaneUI localBasicSplitPaneUI = ((BasicSplitPaneDivider)paramComponent).getBasicSplitPaneUI();
        if (localBasicSplitPaneUI != null)
        {
          JSplitPane localJSplitPane = localBasicSplitPaneUI.getSplitPane();
          if (localJSplitPane != null)
          {
            if (localJSplitPane.getOrientation() == 1)
            {
              top = (bottom = 0);
              left = (right = 1);
              return localInsets;
            }
            top = (bottom = 1);
            left = (right = 0);
            return localInsets;
          }
        }
      }
      top = (bottom = left = right = 1);
      return localInsets;
    }
    
    public boolean isBorderOpaque()
    {
      return true;
    }
  }
  
  public static class ToggleButtonBorder
    extends BasicBorders.ButtonBorder
  {
    public ToggleButtonBorder(Color paramColor1, Color paramColor2, Color paramColor3, Color paramColor4)
    {
      super(paramColor2, paramColor3, paramColor4);
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      BasicGraphicsUtils.drawBezel(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, false, false, shadow, darkShadow, highlight, lightHighlight);
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      paramInsets.set(2, 2, 2, 2);
      return paramInsets;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicBorders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */