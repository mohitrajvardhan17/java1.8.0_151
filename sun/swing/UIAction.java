package sun.swing;

import java.beans.PropertyChangeListener;
import javax.swing.Action;

public abstract class UIAction
  implements Action
{
  private String name;
  
  public UIAction(String paramString)
  {
    name = paramString;
  }
  
  public final String getName()
  {
    return name;
  }
  
  public Object getValue(String paramString)
  {
    if (paramString == "Name") {
      return name;
    }
    return null;
  }
  
  public void putValue(String paramString, Object paramObject) {}
  
  public void setEnabled(boolean paramBoolean) {}
  
  public final boolean isEnabled()
  {
    return isEnabled(null);
  }
  
  public boolean isEnabled(Object paramObject)
  {
    return true;
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {}
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\swing\UIAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */