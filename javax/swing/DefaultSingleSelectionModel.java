package javax.swing;

import java.io.Serializable;
import java.util.EventListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class DefaultSingleSelectionModel
  implements SingleSelectionModel, Serializable
{
  protected transient ChangeEvent changeEvent = null;
  protected EventListenerList listenerList = new EventListenerList();
  private int index = -1;
  
  public DefaultSingleSelectionModel() {}
  
  public int getSelectedIndex()
  {
    return index;
  }
  
  public void setSelectedIndex(int paramInt)
  {
    if (index != paramInt)
    {
      index = paramInt;
      fireStateChanged();
    }
  }
  
  public void clearSelection()
  {
    setSelectedIndex(-1);
  }
  
  public boolean isSelected()
  {
    boolean bool = false;
    if (getSelectedIndex() != -1) {
      bool = true;
    }
    return bool;
  }
  
  public void addChangeListener(ChangeListener paramChangeListener)
  {
    listenerList.add(ChangeListener.class, paramChangeListener);
  }
  
  public void removeChangeListener(ChangeListener paramChangeListener)
  {
    listenerList.remove(ChangeListener.class, paramChangeListener);
  }
  
  public ChangeListener[] getChangeListeners()
  {
    return (ChangeListener[])listenerList.getListeners(ChangeListener.class);
  }
  
  protected void fireStateChanged()
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ChangeListener.class)
      {
        if (changeEvent == null) {
          changeEvent = new ChangeEvent(this);
        }
        ((ChangeListener)arrayOfObject[(i + 1)]).stateChanged(changeEvent);
      }
    }
  }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass)
  {
    return listenerList.getListeners(paramClass);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\DefaultSingleSelectionModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */