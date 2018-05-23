package javax.swing;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ComponentUI;

public class JButton
  extends AbstractButton
  implements Accessible
{
  private static final String uiClassID = "ButtonUI";
  
  public JButton()
  {
    this(null, null);
  }
  
  public JButton(Icon paramIcon)
  {
    this(null, paramIcon);
  }
  
  @ConstructorProperties({"text"})
  public JButton(String paramString)
  {
    this(paramString, null);
  }
  
  public JButton(Action paramAction)
  {
    this();
    setAction(paramAction);
  }
  
  public JButton(String paramString, Icon paramIcon)
  {
    setModel(new DefaultButtonModel());
    init(paramString, paramIcon);
  }
  
  public void updateUI()
  {
    setUI((ButtonUI)UIManager.getUI(this));
  }
  
  public String getUIClassID()
  {
    return "ButtonUI";
  }
  
  public boolean isDefaultButton()
  {
    JRootPane localJRootPane = SwingUtilities.getRootPane(this);
    if (localJRootPane != null) {
      return localJRootPane.getDefaultButton() == this;
    }
    return false;
  }
  
  public boolean isDefaultCapable()
  {
    return defaultCapable;
  }
  
  public void setDefaultCapable(boolean paramBoolean)
  {
    boolean bool = defaultCapable;
    defaultCapable = paramBoolean;
    firePropertyChange("defaultCapable", bool, paramBoolean);
  }
  
  public void removeNotify()
  {
    JRootPane localJRootPane = SwingUtilities.getRootPane(this);
    if ((localJRootPane != null) && (localJRootPane.getDefaultButton() == this)) {
      localJRootPane.setDefaultButton(null);
    }
    super.removeNotify();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("ButtonUI"))
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
    String str = defaultCapable ? "true" : "false";
    return super.paramString() + ",defaultCapable=" + str;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJButton();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJButton
    extends AbstractButton.AccessibleAbstractButton
  {
    protected AccessibleJButton()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.PUSH_BUTTON;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */