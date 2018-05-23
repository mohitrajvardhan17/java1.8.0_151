package java.awt;

import java.io.Serializable;

public class CheckboxGroup
  implements Serializable
{
  Checkbox selectedCheckbox = null;
  private static final long serialVersionUID = 3729780091441768983L;
  
  public CheckboxGroup() {}
  
  public Checkbox getSelectedCheckbox()
  {
    return getCurrent();
  }
  
  @Deprecated
  public Checkbox getCurrent()
  {
    return selectedCheckbox;
  }
  
  public void setSelectedCheckbox(Checkbox paramCheckbox)
  {
    setCurrent(paramCheckbox);
  }
  
  @Deprecated
  public synchronized void setCurrent(Checkbox paramCheckbox)
  {
    if ((paramCheckbox != null) && (group != this)) {
      return;
    }
    Checkbox localCheckbox = selectedCheckbox;
    selectedCheckbox = paramCheckbox;
    if ((localCheckbox != null) && (localCheckbox != paramCheckbox) && (group == this)) {
      localCheckbox.setState(false);
    }
    if ((paramCheckbox != null) && (localCheckbox != paramCheckbox) && (!paramCheckbox.getState())) {
      paramCheckbox.setStateInternal(true);
    }
  }
  
  public String toString()
  {
    return getClass().getName() + "[selectedCheckbox=" + selectedCheckbox + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\CheckboxGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */