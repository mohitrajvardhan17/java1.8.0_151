package javax.swing;

import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.plaf.ComponentUI;

public class JCheckBoxMenuItem
  extends JMenuItem
  implements SwingConstants, Accessible
{
  private static final String uiClassID = "CheckBoxMenuItemUI";
  
  public JCheckBoxMenuItem()
  {
    this(null, null, false);
  }
  
  public JCheckBoxMenuItem(Icon paramIcon)
  {
    this(null, paramIcon, false);
  }
  
  public JCheckBoxMenuItem(String paramString)
  {
    this(paramString, null, false);
  }
  
  public JCheckBoxMenuItem(Action paramAction)
  {
    this();
    setAction(paramAction);
  }
  
  public JCheckBoxMenuItem(String paramString, Icon paramIcon)
  {
    this(paramString, paramIcon, false);
  }
  
  public JCheckBoxMenuItem(String paramString, boolean paramBoolean)
  {
    this(paramString, null, paramBoolean);
  }
  
  public JCheckBoxMenuItem(String paramString, Icon paramIcon, boolean paramBoolean)
  {
    super(paramString, paramIcon);
    setModel(new JToggleButton.ToggleButtonModel());
    setSelected(paramBoolean);
    setFocusable(false);
  }
  
  public String getUIClassID()
  {
    return "CheckBoxMenuItemUI";
  }
  
  public boolean getState()
  {
    return isSelected();
  }
  
  public synchronized void setState(boolean paramBoolean)
  {
    setSelected(paramBoolean);
  }
  
  public Object[] getSelectedObjects()
  {
    if (!isSelected()) {
      return null;
    }
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = getText();
    return arrayOfObject;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("CheckBoxMenuItemUI"))
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
  
  boolean shouldUpdateSelectedStateFromAction()
  {
    return true;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJCheckBoxMenuItem();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJCheckBoxMenuItem
    extends JMenuItem.AccessibleJMenuItem
  {
    protected AccessibleJCheckBoxMenuItem()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.CHECK_BOX;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JCheckBoxMenuItem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */