package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Window;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.BorderUIResource.CompoundBorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicBorders.MarginBorder;
import javax.swing.text.JTextComponent;
import sun.swing.StringUIClientPropertyKey;
import sun.swing.SwingUtilities2;

public class MetalBorders
{
  static Object NO_BUTTON_ROLLOVER = new StringUIClientPropertyKey("NoButtonRollover");
  private static Border buttonBorder;
  private static Border textBorder;
  private static Border textFieldBorder;
  private static Border toggleButtonBorder;
  
  public MetalBorders() {}
  
  public static Border getButtonBorder()
  {
    if (buttonBorder == null) {
      buttonBorder = new BorderUIResource.CompoundBorderUIResource(new ButtonBorder(), new BasicBorders.MarginBorder());
    }
    return buttonBorder;
  }
  
  public static Border getTextBorder()
  {
    if (textBorder == null) {
      textBorder = new BorderUIResource.CompoundBorderUIResource(new Flush3DBorder(), new BasicBorders.MarginBorder());
    }
    return textBorder;
  }
  
  public static Border getTextFieldBorder()
  {
    if (textFieldBorder == null) {
      textFieldBorder = new BorderUIResource.CompoundBorderUIResource(new TextFieldBorder(), new BasicBorders.MarginBorder());
    }
    return textFieldBorder;
  }
  
  public static Border getToggleButtonBorder()
  {
    if (toggleButtonBorder == null) {
      toggleButtonBorder = new BorderUIResource.CompoundBorderUIResource(new ToggleButtonBorder(), new BasicBorders.MarginBorder());
    }
    return toggleButtonBorder;
  }
  
  public static Border getDesktopIconBorder()
  {
    return new BorderUIResource.CompoundBorderUIResource(new LineBorder(MetalLookAndFeel.getControlDarkShadow(), 1), new MatteBorder(2, 2, 1, 2, MetalLookAndFeel.getControl()));
  }
  
  static Border getToolBarRolloverBorder()
  {
    if (MetalLookAndFeel.usingOcean()) {
      return new CompoundBorder(new ButtonBorder(), new RolloverMarginBorder());
    }
    return new CompoundBorder(new RolloverButtonBorder(), new RolloverMarginBorder());
  }
  
  static Border getToolBarNonrolloverBorder()
  {
    if (MetalLookAndFeel.usingOcean()) {
      new CompoundBorder(new ButtonBorder(), new RolloverMarginBorder());
    }
    return new CompoundBorder(new ButtonBorder(), new RolloverMarginBorder());
  }
  
  public static class ButtonBorder
    extends AbstractBorder
    implements UIResource
  {
    protected static Insets borderInsets = new Insets(3, 3, 3, 3);
    
    public ButtonBorder() {}
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (!(paramComponent instanceof AbstractButton)) {
        return;
      }
      if (MetalLookAndFeel.usingOcean())
      {
        paintOceanBorder(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
        return;
      }
      AbstractButton localAbstractButton = (AbstractButton)paramComponent;
      ButtonModel localButtonModel = localAbstractButton.getModel();
      if (localButtonModel.isEnabled())
      {
        int i = (localButtonModel.isPressed()) && (localButtonModel.isArmed()) ? 1 : 0;
        int j = ((localAbstractButton instanceof JButton)) && (((JButton)localAbstractButton).isDefaultButton()) ? 1 : 0;
        if ((i != 0) && (j != 0)) {
          MetalUtils.drawDefaultButtonPressedBorder(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
        } else if (i != 0) {
          MetalUtils.drawPressed3DBorder(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
        } else if (j != 0) {
          MetalUtils.drawDefaultButtonBorder(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, false);
        } else {
          MetalUtils.drawButtonBorder(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, false);
        }
      }
      else
      {
        MetalUtils.drawDisabledBorder(paramGraphics, paramInt1, paramInt2, paramInt3 - 1, paramInt4 - 1);
      }
    }
    
    private void paintOceanBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      AbstractButton localAbstractButton = (AbstractButton)paramComponent;
      ButtonModel localButtonModel = ((AbstractButton)paramComponent).getModel();
      paramGraphics.translate(paramInt1, paramInt2);
      if (MetalUtils.isToolBarButton(localAbstractButton))
      {
        if (localButtonModel.isEnabled())
        {
          if (localButtonModel.isPressed())
          {
            paramGraphics.setColor(MetalLookAndFeel.getWhite());
            paramGraphics.fillRect(1, paramInt4 - 1, paramInt3 - 1, 1);
            paramGraphics.fillRect(paramInt3 - 1, 1, 1, paramInt4 - 1);
            paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
            paramGraphics.drawRect(0, 0, paramInt3 - 2, paramInt4 - 2);
            paramGraphics.fillRect(1, 1, paramInt3 - 3, 1);
          }
          else if ((localButtonModel.isSelected()) || (localButtonModel.isRollover()))
          {
            paramGraphics.setColor(MetalLookAndFeel.getWhite());
            paramGraphics.fillRect(1, paramInt4 - 1, paramInt3 - 1, 1);
            paramGraphics.fillRect(paramInt3 - 1, 1, 1, paramInt4 - 1);
            paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
            paramGraphics.drawRect(0, 0, paramInt3 - 2, paramInt4 - 2);
          }
          else
          {
            paramGraphics.setColor(MetalLookAndFeel.getWhite());
            paramGraphics.drawRect(1, 1, paramInt3 - 2, paramInt4 - 2);
            paramGraphics.setColor(UIManager.getColor("Button.toolBarBorderBackground"));
            paramGraphics.drawRect(0, 0, paramInt3 - 2, paramInt4 - 2);
          }
        }
        else
        {
          paramGraphics.setColor(UIManager.getColor("Button.disabledToolBarBorderBackground"));
          paramGraphics.drawRect(0, 0, paramInt3 - 2, paramInt4 - 2);
        }
      }
      else if (localButtonModel.isEnabled())
      {
        boolean bool1 = localButtonModel.isPressed();
        boolean bool2 = localButtonModel.isArmed();
        if (((paramComponent instanceof JButton)) && (((JButton)paramComponent).isDefaultButton()))
        {
          paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
          paramGraphics.drawRect(0, 0, paramInt3 - 1, paramInt4 - 1);
          paramGraphics.drawRect(1, 1, paramInt3 - 3, paramInt4 - 3);
        }
        else if (bool1)
        {
          paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
          paramGraphics.fillRect(0, 0, paramInt3, 2);
          paramGraphics.fillRect(0, 2, 2, paramInt4 - 2);
          paramGraphics.fillRect(paramInt3 - 1, 1, 1, paramInt4 - 1);
          paramGraphics.fillRect(1, paramInt4 - 1, paramInt3 - 2, 1);
        }
        else if ((localButtonModel.isRollover()) && (localAbstractButton.getClientProperty(MetalBorders.NO_BUTTON_ROLLOVER) == null))
        {
          paramGraphics.setColor(MetalLookAndFeel.getPrimaryControl());
          paramGraphics.drawRect(0, 0, paramInt3 - 1, paramInt4 - 1);
          paramGraphics.drawRect(2, 2, paramInt3 - 5, paramInt4 - 5);
          paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
          paramGraphics.drawRect(1, 1, paramInt3 - 3, paramInt4 - 3);
        }
        else
        {
          paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
          paramGraphics.drawRect(0, 0, paramInt3 - 1, paramInt4 - 1);
        }
      }
      else
      {
        paramGraphics.setColor(MetalLookAndFeel.getInactiveControlTextColor());
        paramGraphics.drawRect(0, 0, paramInt3 - 1, paramInt4 - 1);
        if (((paramComponent instanceof JButton)) && (((JButton)paramComponent).isDefaultButton())) {
          paramGraphics.drawRect(1, 1, paramInt3 - 3, paramInt4 - 3);
        }
      }
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      paramInsets.set(3, 3, 3, 3);
      return paramInsets;
    }
  }
  
  static class DialogBorder
    extends AbstractBorder
    implements UIResource
  {
    private static final int corner = 14;
    
    DialogBorder() {}
    
    protected Color getActiveBackground()
    {
      return MetalLookAndFeel.getPrimaryControlDarkShadow();
    }
    
    protected Color getActiveHighlight()
    {
      return MetalLookAndFeel.getPrimaryControlShadow();
    }
    
    protected Color getActiveShadow()
    {
      return MetalLookAndFeel.getPrimaryControlInfo();
    }
    
    protected Color getInactiveBackground()
    {
      return MetalLookAndFeel.getControlDarkShadow();
    }
    
    protected Color getInactiveHighlight()
    {
      return MetalLookAndFeel.getControlShadow();
    }
    
    protected Color getInactiveShadow()
    {
      return MetalLookAndFeel.getControlInfo();
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Window localWindow = SwingUtilities.getWindowAncestor(paramComponent);
      Color localColor1;
      Color localColor2;
      Color localColor3;
      if ((localWindow != null) && (localWindow.isActive()))
      {
        localColor1 = getActiveBackground();
        localColor2 = getActiveHighlight();
        localColor3 = getActiveShadow();
      }
      else
      {
        localColor1 = getInactiveBackground();
        localColor2 = getInactiveHighlight();
        localColor3 = getInactiveShadow();
      }
      paramGraphics.setColor(localColor1);
      paramGraphics.drawLine(paramInt1 + 1, paramInt2 + 0, paramInt1 + paramInt3 - 2, paramInt2 + 0);
      paramGraphics.drawLine(paramInt1 + 0, paramInt2 + 1, paramInt1 + 0, paramInt2 + paramInt4 - 2);
      paramGraphics.drawLine(paramInt1 + paramInt3 - 1, paramInt2 + 1, paramInt1 + paramInt3 - 1, paramInt2 + paramInt4 - 2);
      paramGraphics.drawLine(paramInt1 + 1, paramInt2 + paramInt4 - 1, paramInt1 + paramInt3 - 2, paramInt2 + paramInt4 - 1);
      for (int i = 1; i < 5; i++) {
        paramGraphics.drawRect(paramInt1 + i, paramInt2 + i, paramInt3 - i * 2 - 1, paramInt4 - i * 2 - 1);
      }
      if (((localWindow instanceof Dialog)) && (((Dialog)localWindow).isResizable()))
      {
        paramGraphics.setColor(localColor2);
        paramGraphics.drawLine(15, 3, paramInt3 - 14, 3);
        paramGraphics.drawLine(3, 15, 3, paramInt4 - 14);
        paramGraphics.drawLine(paramInt3 - 2, 15, paramInt3 - 2, paramInt4 - 14);
        paramGraphics.drawLine(15, paramInt4 - 2, paramInt3 - 14, paramInt4 - 2);
        paramGraphics.setColor(localColor3);
        paramGraphics.drawLine(14, 2, paramInt3 - 14 - 1, 2);
        paramGraphics.drawLine(2, 14, 2, paramInt4 - 14 - 1);
        paramGraphics.drawLine(paramInt3 - 3, 14, paramInt3 - 3, paramInt4 - 14 - 1);
        paramGraphics.drawLine(14, paramInt4 - 3, paramInt3 - 14 - 1, paramInt4 - 3);
      }
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      paramInsets.set(5, 5, 5, 5);
      return paramInsets;
    }
  }
  
  static class ErrorDialogBorder
    extends MetalBorders.DialogBorder
    implements UIResource
  {
    ErrorDialogBorder() {}
    
    protected Color getActiveBackground()
    {
      return UIManager.getColor("OptionPane.errorDialog.border.background");
    }
  }
  
  public static class Flush3DBorder
    extends AbstractBorder
    implements UIResource
  {
    public Flush3DBorder() {}
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (paramComponent.isEnabled()) {
        MetalUtils.drawFlush3DBorder(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      } else {
        MetalUtils.drawDisabledBorder(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      paramInsets.set(2, 2, 2, 2);
      return paramInsets;
    }
  }
  
  static class FrameBorder
    extends AbstractBorder
    implements UIResource
  {
    private static final int corner = 14;
    
    FrameBorder() {}
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Window localWindow = SwingUtilities.getWindowAncestor(paramComponent);
      ColorUIResource localColorUIResource1;
      ColorUIResource localColorUIResource2;
      ColorUIResource localColorUIResource3;
      if ((localWindow != null) && (localWindow.isActive()))
      {
        localColorUIResource1 = MetalLookAndFeel.getPrimaryControlDarkShadow();
        localColorUIResource2 = MetalLookAndFeel.getPrimaryControlShadow();
        localColorUIResource3 = MetalLookAndFeel.getPrimaryControlInfo();
      }
      else
      {
        localColorUIResource1 = MetalLookAndFeel.getControlDarkShadow();
        localColorUIResource2 = MetalLookAndFeel.getControlShadow();
        localColorUIResource3 = MetalLookAndFeel.getControlInfo();
      }
      paramGraphics.setColor(localColorUIResource1);
      paramGraphics.drawLine(paramInt1 + 1, paramInt2 + 0, paramInt1 + paramInt3 - 2, paramInt2 + 0);
      paramGraphics.drawLine(paramInt1 + 0, paramInt2 + 1, paramInt1 + 0, paramInt2 + paramInt4 - 2);
      paramGraphics.drawLine(paramInt1 + paramInt3 - 1, paramInt2 + 1, paramInt1 + paramInt3 - 1, paramInt2 + paramInt4 - 2);
      paramGraphics.drawLine(paramInt1 + 1, paramInt2 + paramInt4 - 1, paramInt1 + paramInt3 - 2, paramInt2 + paramInt4 - 1);
      for (int i = 1; i < 5; i++) {
        paramGraphics.drawRect(paramInt1 + i, paramInt2 + i, paramInt3 - i * 2 - 1, paramInt4 - i * 2 - 1);
      }
      if (((localWindow instanceof Frame)) && (((Frame)localWindow).isResizable()))
      {
        paramGraphics.setColor(localColorUIResource2);
        paramGraphics.drawLine(15, 3, paramInt3 - 14, 3);
        paramGraphics.drawLine(3, 15, 3, paramInt4 - 14);
        paramGraphics.drawLine(paramInt3 - 2, 15, paramInt3 - 2, paramInt4 - 14);
        paramGraphics.drawLine(15, paramInt4 - 2, paramInt3 - 14, paramInt4 - 2);
        paramGraphics.setColor(localColorUIResource3);
        paramGraphics.drawLine(14, 2, paramInt3 - 14 - 1, 2);
        paramGraphics.drawLine(2, 14, 2, paramInt4 - 14 - 1);
        paramGraphics.drawLine(paramInt3 - 3, 14, paramInt3 - 3, paramInt4 - 14 - 1);
        paramGraphics.drawLine(14, paramInt4 - 3, paramInt3 - 14 - 1, paramInt4 - 3);
      }
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      paramInsets.set(5, 5, 5, 5);
      return paramInsets;
    }
  }
  
  public static class InternalFrameBorder
    extends AbstractBorder
    implements UIResource
  {
    private static final int corner = 14;
    
    public InternalFrameBorder() {}
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      ColorUIResource localColorUIResource1;
      ColorUIResource localColorUIResource2;
      ColorUIResource localColorUIResource3;
      if (((paramComponent instanceof JInternalFrame)) && (((JInternalFrame)paramComponent).isSelected()))
      {
        localColorUIResource1 = MetalLookAndFeel.getPrimaryControlDarkShadow();
        localColorUIResource2 = MetalLookAndFeel.getPrimaryControlShadow();
        localColorUIResource3 = MetalLookAndFeel.getPrimaryControlInfo();
      }
      else
      {
        localColorUIResource1 = MetalLookAndFeel.getControlDarkShadow();
        localColorUIResource2 = MetalLookAndFeel.getControlShadow();
        localColorUIResource3 = MetalLookAndFeel.getControlInfo();
      }
      paramGraphics.setColor(localColorUIResource1);
      paramGraphics.drawLine(1, 0, paramInt3 - 2, 0);
      paramGraphics.drawLine(0, 1, 0, paramInt4 - 2);
      paramGraphics.drawLine(paramInt3 - 1, 1, paramInt3 - 1, paramInt4 - 2);
      paramGraphics.drawLine(1, paramInt4 - 1, paramInt3 - 2, paramInt4 - 1);
      for (int i = 1; i < 5; i++) {
        paramGraphics.drawRect(paramInt1 + i, paramInt2 + i, paramInt3 - i * 2 - 1, paramInt4 - i * 2 - 1);
      }
      if (((paramComponent instanceof JInternalFrame)) && (((JInternalFrame)paramComponent).isResizable()))
      {
        paramGraphics.setColor(localColorUIResource2);
        paramGraphics.drawLine(15, 3, paramInt3 - 14, 3);
        paramGraphics.drawLine(3, 15, 3, paramInt4 - 14);
        paramGraphics.drawLine(paramInt3 - 2, 15, paramInt3 - 2, paramInt4 - 14);
        paramGraphics.drawLine(15, paramInt4 - 2, paramInt3 - 14, paramInt4 - 2);
        paramGraphics.setColor(localColorUIResource3);
        paramGraphics.drawLine(14, 2, paramInt3 - 14 - 1, 2);
        paramGraphics.drawLine(2, 14, 2, paramInt4 - 14 - 1);
        paramGraphics.drawLine(paramInt3 - 3, 14, paramInt3 - 3, paramInt4 - 14 - 1);
        paramGraphics.drawLine(14, paramInt4 - 3, paramInt3 - 14 - 1, paramInt4 - 3);
      }
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      paramInsets.set(5, 5, 5, 5);
      return paramInsets;
    }
  }
  
  public static class MenuBarBorder
    extends AbstractBorder
    implements UIResource
  {
    protected static Insets borderInsets = new Insets(1, 0, 1, 0);
    
    public MenuBarBorder() {}
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      paramGraphics.translate(paramInt1, paramInt2);
      if (MetalLookAndFeel.usingOcean())
      {
        if (((paramComponent instanceof JMenuBar)) && (!MetalToolBarUI.doesMenuBarBorderToolBar((JMenuBar)paramComponent)))
        {
          paramGraphics.setColor(MetalLookAndFeel.getControl());
          SwingUtilities2.drawHLine(paramGraphics, 0, paramInt3 - 1, paramInt4 - 2);
          paramGraphics.setColor(UIManager.getColor("MenuBar.borderColor"));
          SwingUtilities2.drawHLine(paramGraphics, 0, paramInt3 - 1, paramInt4 - 1);
        }
      }
      else
      {
        paramGraphics.setColor(MetalLookAndFeel.getControlShadow());
        SwingUtilities2.drawHLine(paramGraphics, 0, paramInt3 - 1, paramInt4 - 1);
      }
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      if (MetalLookAndFeel.usingOcean()) {
        paramInsets.set(0, 0, 2, 0);
      } else {
        paramInsets.set(1, 0, 1, 0);
      }
      return paramInsets;
    }
  }
  
  public static class MenuItemBorder
    extends AbstractBorder
    implements UIResource
  {
    protected static Insets borderInsets = new Insets(2, 2, 2, 2);
    
    public MenuItemBorder() {}
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (!(paramComponent instanceof JMenuItem)) {
        return;
      }
      JMenuItem localJMenuItem = (JMenuItem)paramComponent;
      ButtonModel localButtonModel = localJMenuItem.getModel();
      paramGraphics.translate(paramInt1, paramInt2);
      if ((paramComponent.getParent() instanceof JMenuBar))
      {
        if ((localButtonModel.isArmed()) || (localButtonModel.isSelected()))
        {
          paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
          paramGraphics.drawLine(0, 0, paramInt3 - 2, 0);
          paramGraphics.drawLine(0, 0, 0, paramInt4 - 1);
          paramGraphics.drawLine(paramInt3 - 2, 2, paramInt3 - 2, paramInt4 - 1);
          paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
          paramGraphics.drawLine(paramInt3 - 1, 1, paramInt3 - 1, paramInt4 - 1);
          paramGraphics.setColor(MetalLookAndFeel.getMenuBackground());
          paramGraphics.drawLine(paramInt3 - 1, 0, paramInt3 - 1, 0);
        }
      }
      else if ((localButtonModel.isArmed()) || (((paramComponent instanceof JMenu)) && (localButtonModel.isSelected())))
      {
        paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
        paramGraphics.drawLine(0, 0, paramInt3 - 1, 0);
        paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
        paramGraphics.drawLine(0, paramInt4 - 1, paramInt3 - 1, paramInt4 - 1);
      }
      else
      {
        paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
        paramGraphics.drawLine(0, 0, 0, paramInt4 - 1);
      }
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      paramInsets.set(2, 2, 2, 2);
      return paramInsets;
    }
  }
  
  public static class OptionDialogBorder
    extends AbstractBorder
    implements UIResource
  {
    int titleHeight = 0;
    
    public OptionDialogBorder() {}
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      paramGraphics.translate(paramInt1, paramInt2);
      int i = -1;
      Object localObject;
      if ((paramComponent instanceof JInternalFrame))
      {
        localObject = ((JInternalFrame)paramComponent).getClientProperty("JInternalFrame.messageType");
        if ((localObject instanceof Integer)) {
          i = ((Integer)localObject).intValue();
        }
      }
      switch (i)
      {
      case 0: 
        localObject = UIManager.getColor("OptionPane.errorDialog.border.background");
        break;
      case 3: 
        localObject = UIManager.getColor("OptionPane.questionDialog.border.background");
        break;
      case 2: 
        localObject = UIManager.getColor("OptionPane.warningDialog.border.background");
        break;
      case -1: 
      case 1: 
      default: 
        localObject = MetalLookAndFeel.getPrimaryControlDarkShadow();
      }
      paramGraphics.setColor((Color)localObject);
      paramGraphics.drawLine(1, 0, paramInt3 - 2, 0);
      paramGraphics.drawLine(0, 1, 0, paramInt4 - 2);
      paramGraphics.drawLine(paramInt3 - 1, 1, paramInt3 - 1, paramInt4 - 2);
      paramGraphics.drawLine(1, paramInt4 - 1, paramInt3 - 2, paramInt4 - 1);
      for (int j = 1; j < 3; j++) {
        paramGraphics.drawRect(j, j, paramInt3 - j * 2 - 1, paramInt4 - j * 2 - 1);
      }
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      paramInsets.set(3, 3, 3, 3);
      return paramInsets;
    }
  }
  
  public static class PaletteBorder
    extends AbstractBorder
    implements UIResource
  {
    int titleHeight = 0;
    
    public PaletteBorder() {}
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      paramGraphics.translate(paramInt1, paramInt2);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
      paramGraphics.drawLine(0, 1, 0, paramInt4 - 2);
      paramGraphics.drawLine(1, paramInt4 - 1, paramInt3 - 2, paramInt4 - 1);
      paramGraphics.drawLine(paramInt3 - 1, 1, paramInt3 - 1, paramInt4 - 2);
      paramGraphics.drawLine(1, 0, paramInt3 - 2, 0);
      paramGraphics.drawRect(1, 1, paramInt3 - 3, paramInt4 - 3);
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      paramInsets.set(1, 1, 1, 1);
      return paramInsets;
    }
  }
  
  public static class PopupMenuBorder
    extends AbstractBorder
    implements UIResource
  {
    protected static Insets borderInsets = new Insets(3, 1, 2, 1);
    
    public PopupMenuBorder() {}
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      paramGraphics.translate(paramInt1, paramInt2);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
      paramGraphics.drawRect(0, 0, paramInt3 - 1, paramInt4 - 1);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
      paramGraphics.drawLine(1, 1, paramInt3 - 2, 1);
      paramGraphics.drawLine(1, 2, 1, 2);
      paramGraphics.drawLine(1, paramInt4 - 2, 1, paramInt4 - 2);
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      paramInsets.set(3, 1, 2, 1);
      return paramInsets;
    }
  }
  
  static class QuestionDialogBorder
    extends MetalBorders.DialogBorder
    implements UIResource
  {
    QuestionDialogBorder() {}
    
    protected Color getActiveBackground()
    {
      return UIManager.getColor("OptionPane.questionDialog.border.background");
    }
  }
  
  public static class RolloverButtonBorder
    extends MetalBorders.ButtonBorder
  {
    public RolloverButtonBorder() {}
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      AbstractButton localAbstractButton = (AbstractButton)paramComponent;
      ButtonModel localButtonModel = localAbstractButton.getModel();
      if ((localButtonModel.isRollover()) && ((!localButtonModel.isPressed()) || (localButtonModel.isArmed()))) {
        super.paintBorder(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
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
  
  public static class ScrollPaneBorder
    extends AbstractBorder
    implements UIResource
  {
    public ScrollPaneBorder() {}
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (!(paramComponent instanceof JScrollPane)) {
        return;
      }
      JScrollPane localJScrollPane = (JScrollPane)paramComponent;
      JViewport localJViewport1 = localJScrollPane.getColumnHeader();
      int i = 0;
      if (localJViewport1 != null) {
        i = localJViewport1.getHeight();
      }
      JViewport localJViewport2 = localJScrollPane.getRowHeader();
      int j = 0;
      if (localJViewport2 != null) {
        j = localJViewport2.getWidth();
      }
      paramGraphics.translate(paramInt1, paramInt2);
      paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
      paramGraphics.drawRect(0, 0, paramInt3 - 2, paramInt4 - 2);
      paramGraphics.setColor(MetalLookAndFeel.getControlHighlight());
      paramGraphics.drawLine(paramInt3 - 1, 1, paramInt3 - 1, paramInt4 - 1);
      paramGraphics.drawLine(1, paramInt4 - 1, paramInt3 - 1, paramInt4 - 1);
      paramGraphics.setColor(MetalLookAndFeel.getControl());
      paramGraphics.drawLine(paramInt3 - 2, 2 + i, paramInt3 - 2, 2 + i);
      paramGraphics.drawLine(1 + j, paramInt4 - 2, 1 + j, paramInt4 - 2);
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      paramInsets.set(1, 1, 2, 2);
      return paramInsets;
    }
  }
  
  public static class TableHeaderBorder
    extends AbstractBorder
  {
    protected Insets editorBorderInsets = new Insets(2, 2, 2, 0);
    
    public TableHeaderBorder() {}
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      paramGraphics.translate(paramInt1, paramInt2);
      paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
      paramGraphics.drawLine(paramInt3 - 1, 0, paramInt3 - 1, paramInt4 - 1);
      paramGraphics.drawLine(1, paramInt4 - 1, paramInt3 - 1, paramInt4 - 1);
      paramGraphics.setColor(MetalLookAndFeel.getControlHighlight());
      paramGraphics.drawLine(0, 0, paramInt3 - 2, 0);
      paramGraphics.drawLine(0, 0, 0, paramInt4 - 2);
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      paramInsets.set(2, 2, 2, 0);
      return paramInsets;
    }
  }
  
  public static class TextFieldBorder
    extends MetalBorders.Flush3DBorder
  {
    public TextFieldBorder() {}
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (!(paramComponent instanceof JTextComponent))
      {
        if (paramComponent.isEnabled()) {
          MetalUtils.drawFlush3DBorder(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
        } else {
          MetalUtils.drawDisabledBorder(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
        }
        return;
      }
      if ((paramComponent.isEnabled()) && (((JTextComponent)paramComponent).isEditable())) {
        MetalUtils.drawFlush3DBorder(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      } else {
        MetalUtils.drawDisabledBorder(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
  }
  
  public static class ToggleButtonBorder
    extends MetalBorders.ButtonBorder
  {
    public ToggleButtonBorder() {}
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      AbstractButton localAbstractButton = (AbstractButton)paramComponent;
      ButtonModel localButtonModel = localAbstractButton.getModel();
      if (MetalLookAndFeel.usingOcean())
      {
        if ((localButtonModel.isArmed()) || (!localAbstractButton.isEnabled()))
        {
          super.paintBorder(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
        }
        else
        {
          paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
          paramGraphics.drawRect(0, 0, paramInt3 - 1, paramInt4 - 1);
        }
        return;
      }
      if (!paramComponent.isEnabled()) {
        MetalUtils.drawDisabledBorder(paramGraphics, paramInt1, paramInt2, paramInt3 - 1, paramInt4 - 1);
      } else if ((localButtonModel.isPressed()) && (localButtonModel.isArmed())) {
        MetalUtils.drawPressed3DBorder(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      } else if (localButtonModel.isSelected()) {
        MetalUtils.drawDark3DBorder(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      } else {
        MetalUtils.drawFlush3DBorder(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
  }
  
  public static class ToolBarBorder
    extends AbstractBorder
    implements UIResource, SwingConstants
  {
    protected MetalBumps bumps = new MetalBumps(10, 10, MetalLookAndFeel.getControlHighlight(), MetalLookAndFeel.getControlDarkShadow(), UIManager.getColor("ToolBar.background"));
    
    public ToolBarBorder() {}
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (!(paramComponent instanceof JToolBar)) {
        return;
      }
      paramGraphics.translate(paramInt1, paramInt2);
      if (((JToolBar)paramComponent).isFloatable()) {
        if (((JToolBar)paramComponent).getOrientation() == 0)
        {
          int i = MetalLookAndFeel.usingOcean() ? -1 : 0;
          bumps.setBumpArea(10, paramInt4 - 4);
          if (MetalUtils.isLeftToRight(paramComponent)) {
            bumps.paintIcon(paramComponent, paramGraphics, 2, 2 + i);
          } else {
            bumps.paintIcon(paramComponent, paramGraphics, paramInt3 - 12, 2 + i);
          }
        }
        else
        {
          bumps.setBumpArea(paramInt3 - 4, 10);
          bumps.paintIcon(paramComponent, paramGraphics, 2, 2);
        }
      }
      if ((((JToolBar)paramComponent).getOrientation() == 0) && (MetalLookAndFeel.usingOcean()))
      {
        paramGraphics.setColor(MetalLookAndFeel.getControl());
        paramGraphics.drawLine(0, paramInt4 - 2, paramInt3, paramInt4 - 2);
        paramGraphics.setColor(UIManager.getColor("ToolBar.borderColor"));
        paramGraphics.drawLine(0, paramInt4 - 1, paramInt3, paramInt4 - 1);
      }
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      if (MetalLookAndFeel.usingOcean()) {
        paramInsets.set(1, 2, 3, 2);
      } else {
        top = (left = bottom = right = 2);
      }
      if (!(paramComponent instanceof JToolBar)) {
        return paramInsets;
      }
      if (((JToolBar)paramComponent).isFloatable()) {
        if (((JToolBar)paramComponent).getOrientation() == 0)
        {
          if (paramComponent.getComponentOrientation().isLeftToRight()) {
            left = 16;
          } else {
            right = 16;
          }
        }
        else {
          top = 16;
        }
      }
      Insets localInsets = ((JToolBar)paramComponent).getMargin();
      if (localInsets != null)
      {
        left += left;
        top += top;
        right += right;
        bottom += bottom;
      }
      return paramInsets;
    }
  }
  
  static class WarningDialogBorder
    extends MetalBorders.DialogBorder
    implements UIResource
  {
    WarningDialogBorder() {}
    
    protected Color getActiveBackground()
    {
      return UIManager.getColor("OptionPane.warningDialog.border.background");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalBorders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */