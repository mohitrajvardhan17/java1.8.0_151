package javax.swing;

import java.io.Serializable;
import java.util.EventListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class DefaultBoundedRangeModel
  implements BoundedRangeModel, Serializable
{
  protected transient ChangeEvent changeEvent = null;
  protected EventListenerList listenerList = new EventListenerList();
  private int value = 0;
  private int extent = 0;
  private int min = 0;
  private int max = 100;
  private boolean isAdjusting = false;
  
  public DefaultBoundedRangeModel() {}
  
  public DefaultBoundedRangeModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramInt4 >= paramInt3) && (paramInt1 >= paramInt3) && (paramInt1 + paramInt2 >= paramInt1) && (paramInt1 + paramInt2 <= paramInt4))
    {
      value = paramInt1;
      extent = paramInt2;
      min = paramInt3;
      max = paramInt4;
    }
    else
    {
      throw new IllegalArgumentException("invalid range properties");
    }
  }
  
  public int getValue()
  {
    return value;
  }
  
  public int getExtent()
  {
    return extent;
  }
  
  public int getMinimum()
  {
    return min;
  }
  
  public int getMaximum()
  {
    return max;
  }
  
  public void setValue(int paramInt)
  {
    paramInt = Math.min(paramInt, Integer.MAX_VALUE - extent);
    int i = Math.max(paramInt, min);
    if (i + extent > max) {
      i = max - extent;
    }
    setRangeProperties(i, extent, min, max, isAdjusting);
  }
  
  public void setExtent(int paramInt)
  {
    int i = Math.max(0, paramInt);
    if (value + i > max) {
      i = max - value;
    }
    setRangeProperties(value, i, min, max, isAdjusting);
  }
  
  public void setMinimum(int paramInt)
  {
    int i = Math.max(paramInt, max);
    int j = Math.max(paramInt, value);
    int k = Math.min(i - j, extent);
    setRangeProperties(j, k, paramInt, i, isAdjusting);
  }
  
  public void setMaximum(int paramInt)
  {
    int i = Math.min(paramInt, min);
    int j = Math.min(paramInt - i, extent);
    int k = Math.min(paramInt - j, value);
    setRangeProperties(k, j, i, paramInt, isAdjusting);
  }
  
  public void setValueIsAdjusting(boolean paramBoolean)
  {
    setRangeProperties(value, extent, min, max, paramBoolean);
  }
  
  public boolean getValueIsAdjusting()
  {
    return isAdjusting;
  }
  
  public void setRangeProperties(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    if (paramInt3 > paramInt4) {
      paramInt3 = paramInt4;
    }
    if (paramInt1 > paramInt4) {
      paramInt4 = paramInt1;
    }
    if (paramInt1 < paramInt3) {
      paramInt3 = paramInt1;
    }
    if (paramInt2 + paramInt1 > paramInt4) {
      paramInt2 = paramInt4 - paramInt1;
    }
    if (paramInt2 < 0) {
      paramInt2 = 0;
    }
    int i = (paramInt1 != value) || (paramInt2 != extent) || (paramInt3 != min) || (paramInt4 != max) || (paramBoolean != isAdjusting) ? 1 : 0;
    if (i != 0)
    {
      value = paramInt1;
      extent = paramInt2;
      min = paramInt3;
      max = paramInt4;
      isAdjusting = paramBoolean;
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
  
  public String toString()
  {
    String str = "value=" + getValue() + ", extent=" + getExtent() + ", min=" + getMinimum() + ", max=" + getMaximum() + ", adj=" + getValueIsAdjusting();
    return getClass().getName() + "[" + str + "]";
  }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass)
  {
    return listenerList.getListeners(paramClass);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\DefaultBoundedRangeModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */