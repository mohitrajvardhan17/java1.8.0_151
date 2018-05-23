package javax.swing.plaf.metal;

import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.plaf.basic.BasicInternalFrameUI.BorderListener;

public class MetalInternalFrameUI
  extends BasicInternalFrameUI
{
  private static final PropertyChangeListener metalPropertyChangeListener = new MetalPropertyChangeHandler(null);
  private static final Border handyEmptyBorder = new EmptyBorder(0, 0, 0, 0);
  protected static String IS_PALETTE = "JInternalFrame.isPalette";
  private static String IS_PALETTE_KEY = "JInternalFrame.isPalette";
  private static String FRAME_TYPE = "JInternalFrame.frameType";
  private static String NORMAL_FRAME = "normal";
  private static String PALETTE_FRAME = "palette";
  private static String OPTION_DIALOG = "optionDialog";
  
  public MetalInternalFrameUI(JInternalFrame paramJInternalFrame)
  {
    super(paramJInternalFrame);
  }
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MetalInternalFrameUI((JInternalFrame)paramJComponent);
  }
  
  public void installUI(JComponent paramJComponent)
  {
    super.installUI(paramJComponent);
    Object localObject = paramJComponent.getClientProperty(IS_PALETTE_KEY);
    if (localObject != null) {
      setPalette(((Boolean)localObject).booleanValue());
    }
    Container localContainer = frame.getContentPane();
    stripContentBorder(localContainer);
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    frame = ((JInternalFrame)paramJComponent);
    Container localContainer = ((JInternalFrame)paramJComponent).getContentPane();
    if ((localContainer instanceof JComponent))
    {
      JComponent localJComponent = (JComponent)localContainer;
      if (localJComponent.getBorder() == handyEmptyBorder) {
        localJComponent.setBorder(null);
      }
    }
    super.uninstallUI(paramJComponent);
  }
  
  protected void installListeners()
  {
    super.installListeners();
    frame.addPropertyChangeListener(metalPropertyChangeListener);
  }
  
  protected void uninstallListeners()
  {
    frame.removePropertyChangeListener(metalPropertyChangeListener);
    super.uninstallListeners();
  }
  
  protected void installKeyboardActions()
  {
    super.installKeyboardActions();
    ActionMap localActionMap = SwingUtilities.getUIActionMap(frame);
    if (localActionMap != null) {
      localActionMap.remove("showSystemMenu");
    }
  }
  
  protected void uninstallKeyboardActions()
  {
    super.uninstallKeyboardActions();
  }
  
  protected void uninstallComponents()
  {
    titlePane = null;
    super.uninstallComponents();
  }
  
  private void stripContentBorder(Object paramObject)
  {
    if ((paramObject instanceof JComponent))
    {
      JComponent localJComponent = (JComponent)paramObject;
      Border localBorder = localJComponent.getBorder();
      if ((localBorder == null) || ((localBorder instanceof UIResource))) {
        localJComponent.setBorder(handyEmptyBorder);
      }
    }
  }
  
  protected JComponent createNorthPane(JInternalFrame paramJInternalFrame)
  {
    return new MetalInternalFrameTitlePane(paramJInternalFrame);
  }
  
  private void setFrameType(String paramString)
  {
    if (paramString.equals(OPTION_DIALOG))
    {
      LookAndFeel.installBorder(frame, "InternalFrame.optionDialogBorder");
      ((MetalInternalFrameTitlePane)titlePane).setPalette(false);
    }
    else if (paramString.equals(PALETTE_FRAME))
    {
      LookAndFeel.installBorder(frame, "InternalFrame.paletteBorder");
      ((MetalInternalFrameTitlePane)titlePane).setPalette(true);
    }
    else
    {
      LookAndFeel.installBorder(frame, "InternalFrame.border");
      ((MetalInternalFrameTitlePane)titlePane).setPalette(false);
    }
  }
  
  public void setPalette(boolean paramBoolean)
  {
    if (paramBoolean) {
      LookAndFeel.installBorder(frame, "InternalFrame.paletteBorder");
    } else {
      LookAndFeel.installBorder(frame, "InternalFrame.border");
    }
    ((MetalInternalFrameTitlePane)titlePane).setPalette(paramBoolean);
  }
  
  protected MouseInputAdapter createBorderListener(JInternalFrame paramJInternalFrame)
  {
    return new BorderListener1(null);
  }
  
  private class BorderListener1
    extends BasicInternalFrameUI.BorderListener
    implements SwingConstants
  {
    private BorderListener1()
    {
      super();
    }
    
    Rectangle getIconBounds()
    {
      boolean bool = MetalUtils.isLeftToRight(frame);
      int i = bool ? 5 : titlePane.getWidth() - 5;
      Rectangle localRectangle = null;
      Icon localIcon = frame.getFrameIcon();
      if (localIcon != null)
      {
        if (!bool) {
          i -= localIcon.getIconWidth();
        }
        int j = titlePane.getHeight() / 2 - localIcon.getIconHeight() / 2;
        localRectangle = new Rectangle(i, j, localIcon.getIconWidth(), localIcon.getIconHeight());
      }
      return localRectangle;
    }
    
    public void mouseClicked(MouseEvent paramMouseEvent)
    {
      if ((paramMouseEvent.getClickCount() == 2) && (paramMouseEvent.getSource() == getNorthPane()) && (frame.isClosable()) && (!frame.isIcon()))
      {
        Rectangle localRectangle = getIconBounds();
        if ((localRectangle != null) && (localRectangle.contains(paramMouseEvent.getX(), paramMouseEvent.getY()))) {
          frame.doDefaultCloseAction();
        } else {
          super.mouseClicked(paramMouseEvent);
        }
      }
      else
      {
        super.mouseClicked(paramMouseEvent);
      }
    }
  }
  
  private static class MetalPropertyChangeHandler
    implements PropertyChangeListener
  {
    private MetalPropertyChangeHandler() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      JInternalFrame localJInternalFrame = (JInternalFrame)paramPropertyChangeEvent.getSource();
      if (!(localJInternalFrame.getUI() instanceof MetalInternalFrameUI)) {
        return;
      }
      MetalInternalFrameUI localMetalInternalFrameUI = (MetalInternalFrameUI)localJInternalFrame.getUI();
      if (str.equals(MetalInternalFrameUI.FRAME_TYPE))
      {
        if ((paramPropertyChangeEvent.getNewValue() instanceof String)) {
          localMetalInternalFrameUI.setFrameType((String)paramPropertyChangeEvent.getNewValue());
        }
      }
      else if (str.equals(MetalInternalFrameUI.IS_PALETTE_KEY))
      {
        if (paramPropertyChangeEvent.getNewValue() != null) {
          localMetalInternalFrameUI.setPalette(((Boolean)paramPropertyChangeEvent.getNewValue()).booleanValue());
        } else {
          localMetalInternalFrameUI.setPalette(false);
        }
      }
      else if (str.equals("contentPane")) {
        localMetalInternalFrameUI.stripContentBorder(paramPropertyChangeEvent.getNewValue());
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalInternalFrameUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */