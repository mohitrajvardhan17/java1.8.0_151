package javax.swing;

import java.awt.ItemSelectable;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import javax.swing.event.ChangeListener;

public abstract interface ButtonModel
  extends ItemSelectable
{
  public abstract boolean isArmed();
  
  public abstract boolean isSelected();
  
  public abstract boolean isEnabled();
  
  public abstract boolean isPressed();
  
  public abstract boolean isRollover();
  
  public abstract void setArmed(boolean paramBoolean);
  
  public abstract void setSelected(boolean paramBoolean);
  
  public abstract void setEnabled(boolean paramBoolean);
  
  public abstract void setPressed(boolean paramBoolean);
  
  public abstract void setRollover(boolean paramBoolean);
  
  public abstract void setMnemonic(int paramInt);
  
  public abstract int getMnemonic();
  
  public abstract void setActionCommand(String paramString);
  
  public abstract String getActionCommand();
  
  public abstract void setGroup(ButtonGroup paramButtonGroup);
  
  public abstract void addActionListener(ActionListener paramActionListener);
  
  public abstract void removeActionListener(ActionListener paramActionListener);
  
  public abstract void addItemListener(ItemListener paramItemListener);
  
  public abstract void removeItemListener(ItemListener paramItemListener);
  
  public abstract void addChangeListener(ChangeListener paramChangeListener);
  
  public abstract void removeChangeListener(ChangeListener paramChangeListener);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\ButtonModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */