package javax.swing;

import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SeparatorUI;

public class JSeparator
  extends JComponent
  implements SwingConstants, Accessible
{
  private static final String uiClassID = "SeparatorUI";
  private int orientation = 0;
  
  public JSeparator()
  {
    this(0);
  }
  
  public JSeparator(int paramInt)
  {
    checkOrientation(paramInt);
    orientation = paramInt;
    setFocusable(false);
    updateUI();
  }
  
  public SeparatorUI getUI()
  {
    return (SeparatorUI)ui;
  }
  
  public void setUI(SeparatorUI paramSeparatorUI)
  {
    super.setUI(paramSeparatorUI);
  }
  
  public void updateUI()
  {
    setUI((SeparatorUI)UIManager.getUI(this));
  }
  
  public String getUIClassID()
  {
    return "SeparatorUI";
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("SeparatorUI"))
    {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if ((b == 0) && (ui != null)) {
        ui.installUI(this);
      }
    }
  }
  
  public int getOrientation()
  {
    return orientation;
  }
  
  public void setOrientation(int paramInt)
  {
    if (orientation == paramInt) {
      return;
    }
    int i = orientation;
    checkOrientation(paramInt);
    orientation = paramInt;
    firePropertyChange("orientation", i, paramInt);
    revalidate();
    repaint();
  }
  
  private void checkOrientation(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
    case 1: 
      break;
    default: 
      throw new IllegalArgumentException("orientation must be one of: VERTICAL, HORIZONTAL");
    }
  }
  
  protected String paramString()
  {
    String str = orientation == 0 ? "HORIZONTAL" : "VERTICAL";
    return super.paramString() + ",orientation=" + str;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJSeparator();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJSeparator
    extends JComponent.AccessibleJComponent
  {
    protected AccessibleJSeparator()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.SEPARATOR;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JSeparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */