package javax.swing;

import java.io.Serializable;
import java.util.EventListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public abstract class AbstractListModel<E>
  implements ListModel<E>, Serializable
{
  protected EventListenerList listenerList = new EventListenerList();
  
  public AbstractListModel() {}
  
  public void addListDataListener(ListDataListener paramListDataListener)
  {
    listenerList.add(ListDataListener.class, paramListDataListener);
  }
  
  public void removeListDataListener(ListDataListener paramListDataListener)
  {
    listenerList.remove(ListDataListener.class, paramListDataListener);
  }
  
  public ListDataListener[] getListDataListeners()
  {
    return (ListDataListener[])listenerList.getListeners(ListDataListener.class);
  }
  
  protected void fireContentsChanged(Object paramObject, int paramInt1, int paramInt2)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    ListDataEvent localListDataEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ListDataListener.class)
      {
        if (localListDataEvent == null) {
          localListDataEvent = new ListDataEvent(paramObject, 0, paramInt1, paramInt2);
        }
        ((ListDataListener)arrayOfObject[(i + 1)]).contentsChanged(localListDataEvent);
      }
    }
  }
  
  protected void fireIntervalAdded(Object paramObject, int paramInt1, int paramInt2)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    ListDataEvent localListDataEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ListDataListener.class)
      {
        if (localListDataEvent == null) {
          localListDataEvent = new ListDataEvent(paramObject, 1, paramInt1, paramInt2);
        }
        ((ListDataListener)arrayOfObject[(i + 1)]).intervalAdded(localListDataEvent);
      }
    }
  }
  
  protected void fireIntervalRemoved(Object paramObject, int paramInt1, int paramInt2)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    ListDataEvent localListDataEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ListDataListener.class)
      {
        if (localListDataEvent == null) {
          localListDataEvent = new ListDataEvent(paramObject, 2, paramInt1, paramInt2);
        }
        ((ListDataListener)arrayOfObject[(i + 1)]).intervalRemoved(localListDataEvent);
      }
    }
  }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass)
  {
    return listenerList.getListeners(paramClass);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\AbstractListModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */