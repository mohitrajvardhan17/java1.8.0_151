package java.awt;

import java.awt.event.ItemListener;

public abstract interface ItemSelectable
{
  public abstract Object[] getSelectedObjects();
  
  public abstract void addItemListener(ItemListener paramItemListener);
  
  public abstract void removeItemListener(ItemListener paramItemListener);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\ItemSelectable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */