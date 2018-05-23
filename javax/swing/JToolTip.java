package javax.swing;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Objects;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ToolTipUI;

public class JToolTip
  extends JComponent
  implements Accessible
{
  private static final String uiClassID = "ToolTipUI";
  String tipText;
  JComponent component;
  
  public JToolTip()
  {
    setOpaque(true);
    updateUI();
  }
  
  public ToolTipUI getUI()
  {
    return (ToolTipUI)ui;
  }
  
  public void updateUI()
  {
    setUI((ToolTipUI)UIManager.getUI(this));
  }
  
  public String getUIClassID()
  {
    return "ToolTipUI";
  }
  
  public void setTipText(String paramString)
  {
    String str = tipText;
    tipText = paramString;
    firePropertyChange("tiptext", str, paramString);
    if (!Objects.equals(str, paramString))
    {
      revalidate();
      repaint();
    }
  }
  
  public String getTipText()
  {
    return tipText;
  }
  
  public void setComponent(JComponent paramJComponent)
  {
    JComponent localJComponent = component;
    component = paramJComponent;
    firePropertyChange("component", localJComponent, paramJComponent);
  }
  
  public JComponent getComponent()
  {
    return component;
  }
  
  boolean alwaysOnTop()
  {
    return true;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("ToolTipUI"))
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
    String str = tipText != null ? tipText : "";
    return super.paramString() + ",tipText=" + str;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJToolTip();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJToolTip
    extends JComponent.AccessibleJComponent
  {
    protected AccessibleJToolTip()
    {
      super();
    }
    
    public String getAccessibleDescription()
    {
      String str = accessibleDescription;
      if (str == null) {
        str = (String)getClientProperty("AccessibleDescription");
      }
      if (str == null) {
        str = getTipText();
      }
      return str;
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.TOOL_TIP;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JToolTip.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */