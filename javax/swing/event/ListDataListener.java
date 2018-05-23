package javax.swing.event;

import java.util.EventListener;

public abstract interface ListDataListener
  extends EventListener
{
  public abstract void intervalAdded(ListDataEvent paramListDataEvent);
  
  public abstract void intervalRemoved(ListDataEvent paramListDataEvent);
  
  public abstract void contentsChanged(ListDataEvent paramListDataEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\event\ListDataListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */