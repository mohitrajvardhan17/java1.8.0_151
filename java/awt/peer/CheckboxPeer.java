package java.awt.peer;

import java.awt.CheckboxGroup;

public abstract interface CheckboxPeer
  extends ComponentPeer
{
  public abstract void setState(boolean paramBoolean);
  
  public abstract void setCheckboxGroup(CheckboxGroup paramCheckboxGroup);
  
  public abstract void setLabel(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\peer\CheckboxPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */