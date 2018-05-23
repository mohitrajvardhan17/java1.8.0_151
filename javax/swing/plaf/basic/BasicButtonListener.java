package javax.swing.plaf.basic;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentInputMapUIResource;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicButtonListener
  implements MouseListener, MouseMotionListener, FocusListener, ChangeListener, PropertyChangeListener
{
  private long lastPressedTimestamp = -1L;
  private boolean shouldDiscardRelease = false;
  
  static void loadActionMap(LazyActionMap paramLazyActionMap)
  {
    paramLazyActionMap.put(new Actions("pressed"));
    paramLazyActionMap.put(new Actions("released"));
  }
  
  public BasicButtonListener(AbstractButton paramAbstractButton) {}
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    String str = paramPropertyChangeEvent.getPropertyName();
    if (str == "mnemonic")
    {
      updateMnemonicBinding((AbstractButton)paramPropertyChangeEvent.getSource());
    }
    else if (str == "contentAreaFilled")
    {
      checkOpacity((AbstractButton)paramPropertyChangeEvent.getSource());
    }
    else if ((str == "text") || ("font" == str) || ("foreground" == str))
    {
      AbstractButton localAbstractButton = (AbstractButton)paramPropertyChangeEvent.getSource();
      BasicHTML.updateRenderer(localAbstractButton, localAbstractButton.getText());
    }
  }
  
  protected void checkOpacity(AbstractButton paramAbstractButton)
  {
    paramAbstractButton.setOpaque(paramAbstractButton.isContentAreaFilled());
  }
  
  public void installKeyboardActions(JComponent paramJComponent)
  {
    AbstractButton localAbstractButton = (AbstractButton)paramJComponent;
    updateMnemonicBinding(localAbstractButton);
    LazyActionMap.installLazyActionMap(paramJComponent, BasicButtonListener.class, "Button.actionMap");
    InputMap localInputMap = getInputMap(0, paramJComponent);
    SwingUtilities.replaceUIInputMap(paramJComponent, 0, localInputMap);
  }
  
  public void uninstallKeyboardActions(JComponent paramJComponent)
  {
    SwingUtilities.replaceUIInputMap(paramJComponent, 2, null);
    SwingUtilities.replaceUIInputMap(paramJComponent, 0, null);
    SwingUtilities.replaceUIActionMap(paramJComponent, null);
  }
  
  InputMap getInputMap(int paramInt, JComponent paramJComponent)
  {
    if (paramInt == 0)
    {
      BasicButtonUI localBasicButtonUI = (BasicButtonUI)BasicLookAndFeel.getUIOfType(((AbstractButton)paramJComponent).getUI(), BasicButtonUI.class);
      if (localBasicButtonUI != null) {
        return (InputMap)DefaultLookup.get(paramJComponent, localBasicButtonUI, localBasicButtonUI.getPropertyPrefix() + "focusInputMap");
      }
    }
    return null;
  }
  
  void updateMnemonicBinding(AbstractButton paramAbstractButton)
  {
    int i = paramAbstractButton.getMnemonic();
    Object localObject;
    if (i != 0)
    {
      localObject = SwingUtilities.getUIInputMap(paramAbstractButton, 2);
      if (localObject == null)
      {
        localObject = new ComponentInputMapUIResource(paramAbstractButton);
        SwingUtilities.replaceUIInputMap(paramAbstractButton, 2, (InputMap)localObject);
      }
      ((InputMap)localObject).clear();
      ((InputMap)localObject).put(KeyStroke.getKeyStroke(i, BasicLookAndFeel.getFocusAcceleratorKeyMask(), false), "pressed");
      ((InputMap)localObject).put(KeyStroke.getKeyStroke(i, BasicLookAndFeel.getFocusAcceleratorKeyMask(), true), "released");
      ((InputMap)localObject).put(KeyStroke.getKeyStroke(i, 0, true), "released");
    }
    else
    {
      localObject = SwingUtilities.getUIInputMap(paramAbstractButton, 2);
      if (localObject != null) {
        ((InputMap)localObject).clear();
      }
    }
  }
  
  public void stateChanged(ChangeEvent paramChangeEvent)
  {
    AbstractButton localAbstractButton = (AbstractButton)paramChangeEvent.getSource();
    localAbstractButton.repaint();
  }
  
  public void focusGained(FocusEvent paramFocusEvent)
  {
    AbstractButton localAbstractButton = (AbstractButton)paramFocusEvent.getSource();
    if (((localAbstractButton instanceof JButton)) && (((JButton)localAbstractButton).isDefaultCapable()))
    {
      JRootPane localJRootPane = localAbstractButton.getRootPane();
      if (localJRootPane != null)
      {
        BasicButtonUI localBasicButtonUI = (BasicButtonUI)BasicLookAndFeel.getUIOfType(localAbstractButton.getUI(), BasicButtonUI.class);
        if ((localBasicButtonUI != null) && (DefaultLookup.getBoolean(localAbstractButton, localBasicButtonUI, localBasicButtonUI.getPropertyPrefix() + "defaultButtonFollowsFocus", true)))
        {
          localJRootPane.putClientProperty("temporaryDefaultButton", localAbstractButton);
          localJRootPane.setDefaultButton((JButton)localAbstractButton);
          localJRootPane.putClientProperty("temporaryDefaultButton", null);
        }
      }
    }
    localAbstractButton.repaint();
  }
  
  public void focusLost(FocusEvent paramFocusEvent)
  {
    AbstractButton localAbstractButton = (AbstractButton)paramFocusEvent.getSource();
    JRootPane localJRootPane = localAbstractButton.getRootPane();
    if (localJRootPane != null)
    {
      localObject = (JButton)localJRootPane.getClientProperty("initialDefaultButton");
      if (localAbstractButton != localObject)
      {
        BasicButtonUI localBasicButtonUI = (BasicButtonUI)BasicLookAndFeel.getUIOfType(localAbstractButton.getUI(), BasicButtonUI.class);
        if ((localBasicButtonUI != null) && (DefaultLookup.getBoolean(localAbstractButton, localBasicButtonUI, localBasicButtonUI.getPropertyPrefix() + "defaultButtonFollowsFocus", true))) {
          localJRootPane.setDefaultButton((JButton)localObject);
        }
      }
    }
    Object localObject = localAbstractButton.getModel();
    ((ButtonModel)localObject).setPressed(false);
    ((ButtonModel)localObject).setArmed(false);
    localAbstractButton.repaint();
  }
  
  public void mouseMoved(MouseEvent paramMouseEvent) {}
  
  public void mouseDragged(MouseEvent paramMouseEvent) {}
  
  public void mouseClicked(MouseEvent paramMouseEvent) {}
  
  public void mousePressed(MouseEvent paramMouseEvent)
  {
    if (SwingUtilities.isLeftMouseButton(paramMouseEvent))
    {
      AbstractButton localAbstractButton = (AbstractButton)paramMouseEvent.getSource();
      if (localAbstractButton.contains(paramMouseEvent.getX(), paramMouseEvent.getY()))
      {
        long l1 = localAbstractButton.getMultiClickThreshhold();
        long l2 = lastPressedTimestamp;
        long l3 = lastPressedTimestamp = paramMouseEvent.getWhen();
        if ((l2 != -1L) && (l3 - l2 < l1))
        {
          shouldDiscardRelease = true;
          return;
        }
        ButtonModel localButtonModel = localAbstractButton.getModel();
        if (!localButtonModel.isEnabled()) {
          return;
        }
        if (!localButtonModel.isArmed()) {
          localButtonModel.setArmed(true);
        }
        localButtonModel.setPressed(true);
        if ((!localAbstractButton.hasFocus()) && (localAbstractButton.isRequestFocusEnabled())) {
          localAbstractButton.requestFocus();
        }
      }
    }
  }
  
  public void mouseReleased(MouseEvent paramMouseEvent)
  {
    if (SwingUtilities.isLeftMouseButton(paramMouseEvent))
    {
      if (shouldDiscardRelease)
      {
        shouldDiscardRelease = false;
        return;
      }
      AbstractButton localAbstractButton = (AbstractButton)paramMouseEvent.getSource();
      ButtonModel localButtonModel = localAbstractButton.getModel();
      localButtonModel.setPressed(false);
      localButtonModel.setArmed(false);
    }
  }
  
  public void mouseEntered(MouseEvent paramMouseEvent)
  {
    AbstractButton localAbstractButton = (AbstractButton)paramMouseEvent.getSource();
    ButtonModel localButtonModel = localAbstractButton.getModel();
    if ((localAbstractButton.isRolloverEnabled()) && (!SwingUtilities.isLeftMouseButton(paramMouseEvent))) {
      localButtonModel.setRollover(true);
    }
    if (localButtonModel.isPressed()) {
      localButtonModel.setArmed(true);
    }
  }
  
  public void mouseExited(MouseEvent paramMouseEvent)
  {
    AbstractButton localAbstractButton = (AbstractButton)paramMouseEvent.getSource();
    ButtonModel localButtonModel = localAbstractButton.getModel();
    if (localAbstractButton.isRolloverEnabled()) {
      localButtonModel.setRollover(false);
    }
    localButtonModel.setArmed(false);
  }
  
  private static class Actions
    extends UIAction
  {
    private static final String PRESS = "pressed";
    private static final String RELEASE = "released";
    
    Actions(String paramString)
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      AbstractButton localAbstractButton = (AbstractButton)paramActionEvent.getSource();
      String str = getName();
      ButtonModel localButtonModel;
      if (str == "pressed")
      {
        localButtonModel = localAbstractButton.getModel();
        localButtonModel.setArmed(true);
        localButtonModel.setPressed(true);
        if (!localAbstractButton.hasFocus()) {
          localAbstractButton.requestFocus();
        }
      }
      else if (str == "released")
      {
        localButtonModel = localAbstractButton.getModel();
        localButtonModel.setPressed(false);
        localButtonModel.setArmed(false);
      }
    }
    
    public boolean isEnabled(Object paramObject)
    {
      return (paramObject == null) || (!(paramObject instanceof AbstractButton)) || (((AbstractButton)paramObject).getModel().isEnabled());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */