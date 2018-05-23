package javax.swing;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ComponentUI;

public class JToggleButton
  extends AbstractButton
  implements Accessible
{
  private static final String uiClassID = "ToggleButtonUI";
  
  public JToggleButton()
  {
    this(null, null, false);
  }
  
  public JToggleButton(Icon paramIcon)
  {
    this(null, paramIcon, false);
  }
  
  public JToggleButton(Icon paramIcon, boolean paramBoolean)
  {
    this(null, paramIcon, paramBoolean);
  }
  
  public JToggleButton(String paramString)
  {
    this(paramString, null, false);
  }
  
  public JToggleButton(String paramString, boolean paramBoolean)
  {
    this(paramString, null, paramBoolean);
  }
  
  public JToggleButton(Action paramAction)
  {
    this();
    setAction(paramAction);
  }
  
  public JToggleButton(String paramString, Icon paramIcon)
  {
    this(paramString, paramIcon, false);
  }
  
  public JToggleButton(String paramString, Icon paramIcon, boolean paramBoolean)
  {
    setModel(new ToggleButtonModel());
    model.setSelected(paramBoolean);
    init(paramString, paramIcon);
  }
  
  public void updateUI()
  {
    setUI((ButtonUI)UIManager.getUI(this));
  }
  
  public String getUIClassID()
  {
    return "ToggleButtonUI";
  }
  
  boolean shouldUpdateSelectedStateFromAction()
  {
    return true;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("ToggleButtonUI"))
    {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if ((b == 0) && (ui != null)) {
        ui.installUI(this);
      }
    }
  }
  
  protected String paramString()
  {
    return super.paramString();
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJToggleButton();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJToggleButton
    extends AbstractButton.AccessibleAbstractButton
    implements ItemListener
  {
    public AccessibleJToggleButton()
    {
      super();
      addItemListener(this);
    }
    
    public void itemStateChanged(ItemEvent paramItemEvent)
    {
      JToggleButton localJToggleButton = (JToggleButton)paramItemEvent.getSource();
      if (accessibleContext != null) {
        if (localJToggleButton.isSelected()) {
          accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.CHECKED);
        } else {
          accessibleContext.firePropertyChange("AccessibleState", AccessibleState.CHECKED, null);
        }
      }
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.TOGGLE_BUTTON;
    }
  }
  
  public static class ToggleButtonModel
    extends DefaultButtonModel
  {
    public ToggleButtonModel() {}
    
    public boolean isSelected()
    {
      return (stateMask & 0x2) != 0;
    }
    
    public void setSelected(boolean paramBoolean)
    {
      ButtonGroup localButtonGroup = getGroup();
      if (localButtonGroup != null)
      {
        localButtonGroup.setSelected(this, paramBoolean);
        paramBoolean = localButtonGroup.isSelected(this);
      }
      if (isSelected() == paramBoolean) {
        return;
      }
      if (paramBoolean) {
        stateMask |= 0x2;
      } else {
        stateMask &= 0xFFFFFFFD;
      }
      fireStateChanged();
      fireItemStateChanged(new ItemEvent(this, 701, this, isSelected() ? 1 : 2));
    }
    
    public void setPressed(boolean paramBoolean)
    {
      if ((isPressed() == paramBoolean) || (!isEnabled())) {
        return;
      }
      if ((!paramBoolean) && (isArmed())) {
        setSelected(!isSelected());
      }
      if (paramBoolean) {
        stateMask |= 0x4;
      } else {
        stateMask &= 0xFFFFFFFB;
      }
      fireStateChanged();
      if ((!isPressed()) && (isArmed()))
      {
        int i = 0;
        AWTEvent localAWTEvent = EventQueue.getCurrentEvent();
        if ((localAWTEvent instanceof InputEvent)) {
          i = ((InputEvent)localAWTEvent).getModifiers();
        } else if ((localAWTEvent instanceof ActionEvent)) {
          i = ((ActionEvent)localAWTEvent).getModifiers();
        }
        fireActionPerformed(new ActionEvent(this, 1001, getActionCommand(), EventQueue.getMostRecentEventTime(), i));
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JToggleButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */