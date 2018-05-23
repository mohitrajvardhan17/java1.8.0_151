package javax.swing.colorchooser;

import java.awt.Color;
import java.io.Serializable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class DefaultColorSelectionModel
  implements ColorSelectionModel, Serializable
{
  protected transient ChangeEvent changeEvent = null;
  protected EventListenerList listenerList = new EventListenerList();
  private Color selectedColor;
  
  public DefaultColorSelectionModel()
  {
    selectedColor = Color.white;
  }
  
  public DefaultColorSelectionModel(Color paramColor)
  {
    selectedColor = paramColor;
  }
  
  public Color getSelectedColor()
  {
    return selectedColor;
  }
  
  public void setSelectedColor(Color paramColor)
  {
    if ((paramColor != null) && (!selectedColor.equals(paramColor)))
    {
      selectedColor = paramColor;
      fireStateChanged();
    }
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
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\colorchooser\DefaultColorSelectionModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */