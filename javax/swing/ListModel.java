package javax.swing;

import javax.swing.event.ListDataListener;

public abstract interface ListModel<E>
{
  public abstract int getSize();
  
  public abstract E getElementAt(int paramInt);
  
  public abstract void addListDataListener(ListDataListener paramListDataListener);
  
  public abstract void removeListDataListener(ListDataListener paramListDataListener);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\ListModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */