package javax.swing;

import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ComponentUI;

public class JRadioButton
  extends JToggleButton
  implements Accessible
{
  private static final String uiClassID = "RadioButtonUI";
  
  public JRadioButton()
  {
    this(null, null, false);
  }
  
  public JRadioButton(Icon paramIcon)
  {
    this(null, paramIcon, false);
  }
  
  public JRadioButton(Action paramAction)
  {
    this();
    setAction(paramAction);
  }
  
  public JRadioButton(Icon paramIcon, boolean paramBoolean)
  {
    this(null, paramIcon, paramBoolean);
  }
  
  public JRadioButton(String paramString)
  {
    this(paramString, null, false);
  }
  
  public JRadioButton(String paramString, boolean paramBoolean)
  {
    this(paramString, null, paramBoolean);
  }
  
  public JRadioButton(String paramString, Icon paramIcon)
  {
    this(paramString, paramIcon, false);
  }
  
  public JRadioButton(String paramString, Icon paramIcon, boolean paramBoolean)
  {
    super(paramString, paramIcon, paramBoolean);
    setBorderPainted(false);
    setHorizontalAlignment(10);
  }
  
  public void updateUI()
  {
    setUI((ButtonUI)UIManager.getUI(this));
  }
  
  public String getUIClassID()
  {
    return "RadioButtonUI";
  }
  
  void setIconFromAction(Action paramAction) {}
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("RadioButtonUI"))
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
      accessibleContext = new AccessibleJRadioButton();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJRadioButton
    extends JToggleButton.AccessibleJToggleButton
  {
    protected AccessibleJRadioButton()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.RADIO_BUTTON;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JRadioButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */