package javax.swing;

import java.beans.Transient;
import java.io.Serializable;
import java.util.BitSet;
import java.util.EventListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class DefaultListSelectionModel
  implements ListSelectionModel, Cloneable, Serializable
{
  private static final int MIN = -1;
  private static final int MAX = Integer.MAX_VALUE;
  private int selectionMode = 2;
  private int minIndex = Integer.MAX_VALUE;
  private int maxIndex = -1;
  private int anchorIndex = -1;
  private int leadIndex = -1;
  private int firstAdjustedIndex = Integer.MAX_VALUE;
  private int lastAdjustedIndex = -1;
  private boolean isAdjusting = false;
  private int firstChangedIndex = Integer.MAX_VALUE;
  private int lastChangedIndex = -1;
  private BitSet value = new BitSet(32);
  protected EventListenerList listenerList = new EventListenerList();
  protected boolean leadAnchorNotificationEnabled = true;
  
  public DefaultListSelectionModel() {}
  
  public int getMinSelectionIndex()
  {
    return isSelectionEmpty() ? -1 : minIndex;
  }
  
  public int getMaxSelectionIndex()
  {
    return maxIndex;
  }
  
  public boolean getValueIsAdjusting()
  {
    return isAdjusting;
  }
  
  public int getSelectionMode()
  {
    return selectionMode;
  }
  
  public void setSelectionMode(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
    case 1: 
    case 2: 
      selectionMode = paramInt;
      break;
    default: 
      throw new IllegalArgumentException("invalid selectionMode");
    }
  }
  
  public boolean isSelectedIndex(int paramInt)
  {
    return (paramInt < minIndex) || (paramInt > maxIndex) ? false : value.get(paramInt);
  }
  
  public boolean isSelectionEmpty()
  {
    return minIndex > maxIndex;
  }
  
  public void addListSelectionListener(ListSelectionListener paramListSelectionListener)
  {
    listenerList.add(ListSelectionListener.class, paramListSelectionListener);
  }
  
  public void removeListSelectionListener(ListSelectionListener paramListSelectionListener)
  {
    listenerList.remove(ListSelectionListener.class, paramListSelectionListener);
  }
  
  public ListSelectionListener[] getListSelectionListeners()
  {
    return (ListSelectionListener[])listenerList.getListeners(ListSelectionListener.class);
  }
  
  protected void fireValueChanged(boolean paramBoolean)
  {
    if (lastChangedIndex == -1) {
      return;
    }
    int i = firstChangedIndex;
    int j = lastChangedIndex;
    firstChangedIndex = Integer.MAX_VALUE;
    lastChangedIndex = -1;
    fireValueChanged(i, j, paramBoolean);
  }
  
  protected void fireValueChanged(int paramInt1, int paramInt2)
  {
    fireValueChanged(paramInt1, paramInt2, getValueIsAdjusting());
  }
  
  protected void fireValueChanged(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    ListSelectionEvent localListSelectionEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ListSelectionListener.class)
      {
        if (localListSelectionEvent == null) {
          localListSelectionEvent = new ListSelectionEvent(this, paramInt1, paramInt2, paramBoolean);
        }
        ((ListSelectionListener)arrayOfObject[(i + 1)]).valueChanged(localListSelectionEvent);
      }
    }
  }
  
  private void fireValueChanged()
  {
    if (lastAdjustedIndex == -1) {
      return;
    }
    if (getValueIsAdjusting())
    {
      firstChangedIndex = Math.min(firstChangedIndex, firstAdjustedIndex);
      lastChangedIndex = Math.max(lastChangedIndex, lastAdjustedIndex);
    }
    int i = firstAdjustedIndex;
    int j = lastAdjustedIndex;
    firstAdjustedIndex = Integer.MAX_VALUE;
    lastAdjustedIndex = -1;
    fireValueChanged(i, j);
  }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass)
  {
    return listenerList.getListeners(paramClass);
  }
  
  private void markAsDirty(int paramInt)
  {
    if (paramInt == -1) {
      return;
    }
    firstAdjustedIndex = Math.min(firstAdjustedIndex, paramInt);
    lastAdjustedIndex = Math.max(lastAdjustedIndex, paramInt);
  }
  
  private void set(int paramInt)
  {
    if (value.get(paramInt)) {
      return;
    }
    value.set(paramInt);
    markAsDirty(paramInt);
    minIndex = Math.min(minIndex, paramInt);
    maxIndex = Math.max(maxIndex, paramInt);
  }
  
  private void clear(int paramInt)
  {
    if (!value.get(paramInt)) {
      return;
    }
    value.clear(paramInt);
    markAsDirty(paramInt);
    if (paramInt == minIndex) {
      for (minIndex += 1; (minIndex <= maxIndex) && (!value.get(minIndex)); minIndex += 1) {}
    }
    if (paramInt == maxIndex) {
      for (maxIndex -= 1; (minIndex <= maxIndex) && (!value.get(maxIndex)); maxIndex -= 1) {}
    }
    if (isSelectionEmpty())
    {
      minIndex = Integer.MAX_VALUE;
      maxIndex = -1;
    }
  }
  
  public void setLeadAnchorNotificationEnabled(boolean paramBoolean)
  {
    leadAnchorNotificationEnabled = paramBoolean;
  }
  
  public boolean isLeadAnchorNotificationEnabled()
  {
    return leadAnchorNotificationEnabled;
  }
  
  private void updateLeadAnchorIndices(int paramInt1, int paramInt2)
  {
    if (leadAnchorNotificationEnabled)
    {
      if (anchorIndex != paramInt1)
      {
        markAsDirty(anchorIndex);
        markAsDirty(paramInt1);
      }
      if (leadIndex != paramInt2)
      {
        markAsDirty(leadIndex);
        markAsDirty(paramInt2);
      }
    }
    anchorIndex = paramInt1;
    leadIndex = paramInt2;
  }
  
  private boolean contains(int paramInt1, int paramInt2, int paramInt3)
  {
    return (paramInt3 >= paramInt1) && (paramInt3 <= paramInt2);
  }
  
  private void changeSelection(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    for (int i = Math.min(paramInt3, paramInt1); i <= Math.max(paramInt4, paramInt2); i++)
    {
      boolean bool1 = contains(paramInt1, paramInt2, i);
      boolean bool2 = contains(paramInt3, paramInt4, i);
      if ((bool2) && (bool1)) {
        if (paramBoolean) {
          bool1 = false;
        } else {
          bool2 = false;
        }
      }
      if (bool2) {
        set(i);
      }
      if (bool1) {
        clear(i);
      }
    }
    fireValueChanged();
  }
  
  private void changeSelection(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    changeSelection(paramInt1, paramInt2, paramInt3, paramInt4, true);
  }
  
  public void clearSelection()
  {
    removeSelectionIntervalImpl(minIndex, maxIndex, false);
  }
  
  public void setSelectionInterval(int paramInt1, int paramInt2)
  {
    if ((paramInt1 == -1) || (paramInt2 == -1)) {
      return;
    }
    if (getSelectionMode() == 0) {
      paramInt1 = paramInt2;
    }
    updateLeadAnchorIndices(paramInt1, paramInt2);
    int i = minIndex;
    int j = maxIndex;
    int k = Math.min(paramInt1, paramInt2);
    int m = Math.max(paramInt1, paramInt2);
    changeSelection(i, j, k, m);
  }
  
  public void addSelectionInterval(int paramInt1, int paramInt2)
  {
    if ((paramInt1 == -1) || (paramInt2 == -1)) {
      return;
    }
    if (getSelectionMode() == 0)
    {
      setSelectionInterval(paramInt1, paramInt2);
      return;
    }
    updateLeadAnchorIndices(paramInt1, paramInt2);
    int i = Integer.MAX_VALUE;
    int j = -1;
    int k = Math.min(paramInt1, paramInt2);
    int m = Math.max(paramInt1, paramInt2);
    if ((getSelectionMode() == 1) && ((m < minIndex - 1) || (k > maxIndex + 1)))
    {
      setSelectionInterval(paramInt1, paramInt2);
      return;
    }
    changeSelection(i, j, k, m);
  }
  
  public void removeSelectionInterval(int paramInt1, int paramInt2)
  {
    removeSelectionIntervalImpl(paramInt1, paramInt2, true);
  }
  
  private void removeSelectionIntervalImpl(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if ((paramInt1 == -1) || (paramInt2 == -1)) {
      return;
    }
    if (paramBoolean) {
      updateLeadAnchorIndices(paramInt1, paramInt2);
    }
    int i = Math.min(paramInt1, paramInt2);
    int j = Math.max(paramInt1, paramInt2);
    int k = Integer.MAX_VALUE;
    int m = -1;
    if ((getSelectionMode() != 2) && (i > minIndex) && (j < maxIndex)) {
      j = maxIndex;
    }
    changeSelection(i, j, k, m);
  }
  
  private void setState(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean) {
      set(paramInt);
    } else {
      clear(paramInt);
    }
  }
  
  public void insertIndexInterval(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i = paramBoolean ? paramInt1 : paramInt1 + 1;
    int j = i + paramInt2 - 1;
    for (int k = maxIndex; k >= i; k--) {
      setState(k + paramInt2, value.get(k));
    }
    boolean bool = getSelectionMode() == 0 ? false : value.get(paramInt1);
    for (int m = i; m <= j; m++) {
      setState(m, bool);
    }
    m = leadIndex;
    if ((m > paramInt1) || ((paramBoolean) && (m == paramInt1))) {
      m = leadIndex + paramInt2;
    }
    int n = anchorIndex;
    if ((n > paramInt1) || ((paramBoolean) && (n == paramInt1))) {
      n = anchorIndex + paramInt2;
    }
    if ((m != leadIndex) || (n != anchorIndex)) {
      updateLeadAnchorIndices(n, m);
    }
    fireValueChanged();
  }
  
  public void removeIndexInterval(int paramInt1, int paramInt2)
  {
    int i = Math.min(paramInt1, paramInt2);
    int j = Math.max(paramInt1, paramInt2);
    int k = j - i + 1;
    for (int m = i; m <= maxIndex; m++) {
      setState(m, value.get(m + k));
    }
    m = leadIndex;
    if ((m != 0) || (i != 0)) {
      if (m > j) {
        m = leadIndex - k;
      } else if (m >= i) {
        m = i - 1;
      }
    }
    int n = anchorIndex;
    if ((n != 0) || (i != 0)) {
      if (n > j) {
        n = anchorIndex - k;
      } else if (n >= i) {
        n = i - 1;
      }
    }
    if ((m != leadIndex) || (n != anchorIndex)) {
      updateLeadAnchorIndices(n, m);
    }
    fireValueChanged();
  }
  
  public void setValueIsAdjusting(boolean paramBoolean)
  {
    if (paramBoolean != isAdjusting)
    {
      isAdjusting = paramBoolean;
      fireValueChanged(paramBoolean);
    }
  }
  
  public String toString()
  {
    String str = (getValueIsAdjusting() ? "~" : "=") + value.toString();
    return getClass().getName() + " " + Integer.toString(hashCode()) + " " + str;
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    DefaultListSelectionModel localDefaultListSelectionModel = (DefaultListSelectionModel)super.clone();
    value = ((BitSet)value.clone());
    listenerList = new EventListenerList();
    return localDefaultListSelectionModel;
  }
  
  @Transient
  public int getAnchorSelectionIndex()
  {
    return anchorIndex;
  }
  
  @Transient
  public int getLeadSelectionIndex()
  {
    return leadIndex;
  }
  
  public void setAnchorSelectionIndex(int paramInt)
  {
    updateLeadAnchorIndices(paramInt, leadIndex);
    fireValueChanged();
  }
  
  public void moveLeadSelectionIndex(int paramInt)
  {
    if ((paramInt == -1) && (anchorIndex != -1)) {
      return;
    }
    updateLeadAnchorIndices(anchorIndex, paramInt);
    fireValueChanged();
  }
  
  public void setLeadSelectionIndex(int paramInt)
  {
    int i = anchorIndex;
    if (paramInt == -1)
    {
      if (i == -1)
      {
        updateLeadAnchorIndices(i, paramInt);
        fireValueChanged();
      }
      return;
    }
    if (i == -1) {
      return;
    }
    if (leadIndex == -1) {
      leadIndex = paramInt;
    }
    boolean bool = value.get(anchorIndex);
    if (getSelectionMode() == 0)
    {
      i = paramInt;
      bool = true;
    }
    int j = Math.min(anchorIndex, leadIndex);
    int k = Math.max(anchorIndex, leadIndex);
    int m = Math.min(i, paramInt);
    int n = Math.max(i, paramInt);
    updateLeadAnchorIndices(i, paramInt);
    if (bool) {
      changeSelection(j, k, m, n);
    } else {
      changeSelection(m, n, j, k, false);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\DefaultListSelectionModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */