package javax.swing;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ComponentUI;

public class JCheckBox
  extends JToggleButton
  implements Accessible
{
  public static final String BORDER_PAINTED_FLAT_CHANGED_PROPERTY = "borderPaintedFlat";
  private boolean flat = false;
  private static final String uiClassID = "CheckBoxUI";
  
  public JCheckBox()
  {
    this(null, null, false);
  }
  
  public JCheckBox(Icon paramIcon)
  {
    this(null, paramIcon, false);
  }
  
  public JCheckBox(Icon paramIcon, boolean paramBoolean)
  {
    this(null, paramIcon, paramBoolean);
  }
  
  public JCheckBox(String paramString)
  {
    this(paramString, null, false);
  }
  
  public JCheckBox(Action paramAction)
  {
    this();
    setAction(paramAction);
  }
  
  public JCheckBox(String paramString, boolean paramBoolean)
  {
    this(paramString, null, paramBoolean);
  }
  
  public JCheckBox(String paramString, Icon paramIcon)
  {
    this(paramString, paramIcon, false);
  }
  
  public JCheckBox(String paramString, Icon paramIcon, boolean paramBoolean)
  {
    super(paramString, paramIcon, paramBoolean);
    setUIProperty("borderPainted", Boolean.FALSE);
    setHorizontalAlignment(10);
  }
  
  public void setBorderPaintedFlat(boolean paramBoolean)
  {
    boolean bool = flat;
    flat = paramBoolean;
    firePropertyChange("borderPaintedFlat", bool, flat);
    if (paramBoolean != bool)
    {
      revalidate();
      repaint();
    }
  }
  
  public boolean isBorderPaintedFlat()
  {
    return flat;
  }
  
  public void updateUI()
  {
    setUI((ButtonUI)UIManager.getUI(this));
  }
  
  public String getUIClassID()
  {
    return "CheckBoxUI";
  }
  
  void setIconFromAction(Action paramAction) {}
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("CheckBoxUI"))
    {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if ((b == 0) && (ui != null)) {
        ui.installUI(this);
      }
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    if (getUIClassID().equals("CheckBoxUI")) {
      updateUI();
    }
  }
  
  protected String paramString()
  {
    return super.paramString();
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJCheckBox();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJCheckBox
    extends JToggleButton.AccessibleJToggleButton
  {
    protected AccessibleJCheckBox()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.CHECK_BOX;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JCheckBox.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */