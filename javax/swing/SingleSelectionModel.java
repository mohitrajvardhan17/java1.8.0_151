package javax.swing;

import javax.swing.event.ChangeListener;

public abstract interface SingleSelectionModel
{
  public abstract int getSelectedIndex();
  
  public abstract void setSelectedIndex(int paramInt);
  
  public abstract void clearSelection();
  
  public abstract boolean isSelected();
  
  public abstract void addChangeListener(ChangeListener paramChangeListener);
  
  public abstract void removeChangeListener(ChangeListener paramChangeListener);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\SingleSelectionModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */