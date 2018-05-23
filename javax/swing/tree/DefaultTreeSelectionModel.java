package javax.swing.tree;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import javax.swing.DefaultListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public class DefaultTreeSelectionModel
  implements Cloneable, Serializable, TreeSelectionModel
{
  public static final String SELECTION_MODE_PROPERTY = "selectionMode";
  protected SwingPropertyChangeSupport changeSupport;
  protected TreePath[] selection;
  protected EventListenerList listenerList = new EventListenerList();
  protected transient RowMapper rowMapper;
  protected DefaultListSelectionModel listSelectionModel = new DefaultListSelectionModel();
  protected int selectionMode = 4;
  protected TreePath leadPath;
  protected int leadIndex = leadRow = -1;
  protected int leadRow;
  private Hashtable<TreePath, Boolean> uniquePaths = new Hashtable();
  private Hashtable<TreePath, Boolean> lastPaths = new Hashtable();
  private TreePath[] tempPaths = new TreePath[1];
  
  public DefaultTreeSelectionModel() {}
  
  public void setRowMapper(RowMapper paramRowMapper)
  {
    rowMapper = paramRowMapper;
    resetRowSelection();
  }
  
  public RowMapper getRowMapper()
  {
    return rowMapper;
  }
  
  public void setSelectionMode(int paramInt)
  {
    int i = selectionMode;
    selectionMode = paramInt;
    if ((selectionMode != 1) && (selectionMode != 2) && (selectionMode != 4)) {
      selectionMode = 4;
    }
    if ((i != selectionMode) && (changeSupport != null)) {
      changeSupport.firePropertyChange("selectionMode", Integer.valueOf(i), Integer.valueOf(selectionMode));
    }
  }
  
  public int getSelectionMode()
  {
    return selectionMode;
  }
  
  public void setSelectionPath(TreePath paramTreePath)
  {
    if (paramTreePath == null)
    {
      setSelectionPaths(null);
    }
    else
    {
      TreePath[] arrayOfTreePath = new TreePath[1];
      arrayOfTreePath[0] = paramTreePath;
      setSelectionPaths(arrayOfTreePath);
    }
  }
  
  public void setSelectionPaths(TreePath[] paramArrayOfTreePath)
  {
    TreePath[] arrayOfTreePath = paramArrayOfTreePath;
    int i;
    if (arrayOfTreePath == null) {
      i = 0;
    } else {
      i = arrayOfTreePath.length;
    }
    int k;
    if (selection == null) {
      k = 0;
    } else {
      k = selection.length;
    }
    if (i + k != 0)
    {
      if (selectionMode == 1)
      {
        if (i > 1)
        {
          arrayOfTreePath = new TreePath[1];
          arrayOfTreePath[0] = paramArrayOfTreePath[0];
          i = 1;
        }
      }
      else if ((selectionMode == 2) && (i > 0) && (!arePathsContiguous(arrayOfTreePath)))
      {
        arrayOfTreePath = new TreePath[1];
        arrayOfTreePath[0] = paramArrayOfTreePath[0];
        i = 1;
      }
      TreePath localTreePath = leadPath;
      Vector localVector = new Vector(i + k);
      ArrayList localArrayList = new ArrayList(i);
      lastPaths.clear();
      leadPath = null;
      for (int j = 0; j < i; j++)
      {
        localObject = arrayOfTreePath[j];
        if ((localObject != null) && (lastPaths.get(localObject) == null))
        {
          lastPaths.put(localObject, Boolean.TRUE);
          if (uniquePaths.get(localObject) == null) {
            localVector.addElement(new PathPlaceHolder((TreePath)localObject, true));
          }
          leadPath = ((TreePath)localObject);
          localArrayList.add(localObject);
        }
      }
      Object localObject = (TreePath[])localArrayList.toArray(new TreePath[localArrayList.size()]);
      for (int m = 0; m < k; m++) {
        if ((selection[m] != null) && (lastPaths.get(selection[m]) == null)) {
          localVector.addElement(new PathPlaceHolder(selection[m], false));
        }
      }
      selection = ((TreePath[])localObject);
      Hashtable localHashtable = uniquePaths;
      uniquePaths = lastPaths;
      lastPaths = localHashtable;
      lastPaths.clear();
      insureUniqueness();
      updateLeadIndex();
      resetRowSelection();
      if (localVector.size() > 0) {
        notifyPathChange(localVector, localTreePath);
      }
    }
  }
  
  public void addSelectionPath(TreePath paramTreePath)
  {
    if (paramTreePath != null)
    {
      TreePath[] arrayOfTreePath = new TreePath[1];
      arrayOfTreePath[0] = paramTreePath;
      addSelectionPaths(arrayOfTreePath);
    }
  }
  
  public void addSelectionPaths(TreePath[] paramArrayOfTreePath)
  {
    int i = paramArrayOfTreePath == null ? 0 : paramArrayOfTreePath.length;
    if (i > 0) {
      if (selectionMode == 1)
      {
        setSelectionPaths(paramArrayOfTreePath);
      }
      else if ((selectionMode == 2) && (!canPathsBeAdded(paramArrayOfTreePath)))
      {
        if (arePathsContiguous(paramArrayOfTreePath))
        {
          setSelectionPaths(paramArrayOfTreePath);
        }
        else
        {
          TreePath[] arrayOfTreePath1 = new TreePath[1];
          arrayOfTreePath1[0] = paramArrayOfTreePath[0];
          setSelectionPaths(arrayOfTreePath1);
        }
      }
      else
      {
        TreePath localTreePath = leadPath;
        Vector localVector = null;
        int m;
        if (selection == null) {
          m = 0;
        } else {
          m = selection.length;
        }
        lastPaths.clear();
        int j = 0;
        int k = 0;
        while (j < i)
        {
          if (paramArrayOfTreePath[j] != null)
          {
            if (uniquePaths.get(paramArrayOfTreePath[j]) == null)
            {
              k++;
              if (localVector == null) {
                localVector = new Vector();
              }
              localVector.addElement(new PathPlaceHolder(paramArrayOfTreePath[j], true));
              uniquePaths.put(paramArrayOfTreePath[j], Boolean.TRUE);
              lastPaths.put(paramArrayOfTreePath[j], Boolean.TRUE);
            }
            leadPath = paramArrayOfTreePath[j];
          }
          j++;
        }
        if (leadPath == null) {
          leadPath = localTreePath;
        }
        if (k > 0)
        {
          TreePath[] arrayOfTreePath2 = new TreePath[m + k];
          if (m > 0) {
            System.arraycopy(selection, 0, arrayOfTreePath2, 0, m);
          }
          if (k != paramArrayOfTreePath.length)
          {
            Enumeration localEnumeration = lastPaths.keys();
            j = m;
            while (localEnumeration.hasMoreElements()) {
              arrayOfTreePath2[(j++)] = ((TreePath)localEnumeration.nextElement());
            }
          }
          else
          {
            System.arraycopy(paramArrayOfTreePath, 0, arrayOfTreePath2, m, k);
          }
          selection = arrayOfTreePath2;
          insureUniqueness();
          updateLeadIndex();
          resetRowSelection();
          notifyPathChange(localVector, localTreePath);
        }
        else
        {
          leadPath = localTreePath;
        }
        lastPaths.clear();
      }
    }
  }
  
  public void removeSelectionPath(TreePath paramTreePath)
  {
    if (paramTreePath != null)
    {
      TreePath[] arrayOfTreePath = new TreePath[1];
      arrayOfTreePath[0] = paramTreePath;
      removeSelectionPaths(arrayOfTreePath);
    }
  }
  
  public void removeSelectionPaths(TreePath[] paramArrayOfTreePath)
  {
    if ((paramArrayOfTreePath != null) && (selection != null) && (paramArrayOfTreePath.length > 0)) {
      if (!canPathsBeRemoved(paramArrayOfTreePath))
      {
        clearSelection();
      }
      else
      {
        Vector localVector = null;
        for (int i = paramArrayOfTreePath.length - 1; i >= 0; i--) {
          if ((paramArrayOfTreePath[i] != null) && (uniquePaths.get(paramArrayOfTreePath[i]) != null))
          {
            if (localVector == null) {
              localVector = new Vector(paramArrayOfTreePath.length);
            }
            uniquePaths.remove(paramArrayOfTreePath[i]);
            localVector.addElement(new PathPlaceHolder(paramArrayOfTreePath[i], false));
          }
        }
        if (localVector != null)
        {
          i = localVector.size();
          TreePath localTreePath = leadPath;
          if (i == selection.length)
          {
            selection = null;
          }
          else
          {
            Enumeration localEnumeration = uniquePaths.keys();
            int j = 0;
            selection = new TreePath[selection.length - i];
            while (localEnumeration.hasMoreElements()) {
              selection[(j++)] = ((TreePath)localEnumeration.nextElement());
            }
          }
          if ((leadPath != null) && (uniquePaths.get(leadPath) == null))
          {
            if (selection != null) {
              leadPath = selection[(selection.length - 1)];
            } else {
              leadPath = null;
            }
          }
          else if (selection != null) {
            leadPath = selection[(selection.length - 1)];
          } else {
            leadPath = null;
          }
          updateLeadIndex();
          resetRowSelection();
          notifyPathChange(localVector, localTreePath);
        }
      }
    }
  }
  
  public TreePath getSelectionPath()
  {
    if ((selection != null) && (selection.length > 0)) {
      return selection[0];
    }
    return null;
  }
  
  public TreePath[] getSelectionPaths()
  {
    if (selection != null)
    {
      int i = selection.length;
      TreePath[] arrayOfTreePath = new TreePath[i];
      System.arraycopy(selection, 0, arrayOfTreePath, 0, i);
      return arrayOfTreePath;
    }
    return new TreePath[0];
  }
  
  public int getSelectionCount()
  {
    return selection == null ? 0 : selection.length;
  }
  
  public boolean isPathSelected(TreePath paramTreePath)
  {
    return uniquePaths.get(paramTreePath) != null;
  }
  
  public boolean isSelectionEmpty()
  {
    return (selection == null) || (selection.length == 0);
  }
  
  public void clearSelection()
  {
    if ((selection != null) && (selection.length > 0))
    {
      int i = selection.length;
      boolean[] arrayOfBoolean = new boolean[i];
      for (int j = 0; j < i; j++) {
        arrayOfBoolean[j] = false;
      }
      TreeSelectionEvent localTreeSelectionEvent = new TreeSelectionEvent(this, selection, arrayOfBoolean, leadPath, null);
      leadPath = null;
      leadIndex = (leadRow = -1);
      uniquePaths.clear();
      selection = null;
      resetRowSelection();
      fireValueChanged(localTreeSelectionEvent);
    }
  }
  
  public void addTreeSelectionListener(TreeSelectionListener paramTreeSelectionListener)
  {
    listenerList.add(TreeSelectionListener.class, paramTreeSelectionListener);
  }
  
  public void removeTreeSelectionListener(TreeSelectionListener paramTreeSelectionListener)
  {
    listenerList.remove(TreeSelectionListener.class, paramTreeSelectionListener);
  }
  
  public TreeSelectionListener[] getTreeSelectionListeners()
  {
    return (TreeSelectionListener[])listenerList.getListeners(TreeSelectionListener.class);
  }
  
  protected void fireValueChanged(TreeSelectionEvent paramTreeSelectionEvent)
  {
    Object[] arrayOfObject = listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TreeSelectionListener.class) {
        ((TreeSelectionListener)arrayOfObject[(i + 1)]).valueChanged(paramTreeSelectionEvent);
      }
    }
  }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass)
  {
    return listenerList.getListeners(paramClass);
  }
  
  public int[] getSelectionRows()
  {
    if ((rowMapper != null) && (selection != null) && (selection.length > 0))
    {
      Object localObject = rowMapper.getRowsForPaths(selection);
      if (localObject != null)
      {
        int i = 0;
        for (int j = localObject.length - 1; j >= 0; j--) {
          if (localObject[j] == -1) {
            i++;
          }
        }
        if (i > 0) {
          if (i == localObject.length)
          {
            localObject = null;
          }
          else
          {
            int[] arrayOfInt = new int[localObject.length - i];
            int k = localObject.length - 1;
            int m = 0;
            while (k >= 0)
            {
              if (localObject[k] != -1) {
                arrayOfInt[(m++)] = localObject[k];
              }
              k--;
            }
            localObject = arrayOfInt;
          }
        }
      }
      return (int[])localObject;
    }
    return new int[0];
  }
  
  public int getMinSelectionRow()
  {
    return listSelectionModel.getMinSelectionIndex();
  }
  
  public int getMaxSelectionRow()
  {
    return listSelectionModel.getMaxSelectionIndex();
  }
  
  public boolean isRowSelected(int paramInt)
  {
    return listSelectionModel.isSelectedIndex(paramInt);
  }
  
  public void resetRowSelection()
  {
    listSelectionModel.clearSelection();
    if ((selection != null) && (rowMapper != null))
    {
      int j = 0;
      int[] arrayOfInt = rowMapper.getRowsForPaths(selection);
      int k = 0;
      int m = selection.length;
      while (k < m)
      {
        int i = arrayOfInt[k];
        if (i != -1) {
          listSelectionModel.addSelectionInterval(i, i);
        }
        k++;
      }
      if ((leadIndex != -1) && (arrayOfInt != null))
      {
        leadRow = arrayOfInt[leadIndex];
      }
      else if (leadPath != null)
      {
        tempPaths[0] = leadPath;
        arrayOfInt = rowMapper.getRowsForPaths(tempPaths);
        leadRow = (arrayOfInt != null ? arrayOfInt[0] : -1);
      }
      else
      {
        leadRow = -1;
      }
      insureRowContinuity();
    }
    else
    {
      leadRow = -1;
    }
  }
  
  public int getLeadSelectionRow()
  {
    return leadRow;
  }
  
  public TreePath getLeadSelectionPath()
  {
    return leadPath;
  }
  
  public synchronized void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    if (changeSupport == null) {
      changeSupport = new SwingPropertyChangeSupport(this);
    }
    changeSupport.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public synchronized void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    if (changeSupport == null) {
      return;
    }
    changeSupport.removePropertyChangeListener(paramPropertyChangeListener);
  }
  
  public PropertyChangeListener[] getPropertyChangeListeners()
  {
    if (changeSupport == null) {
      return new PropertyChangeListener[0];
    }
    return changeSupport.getPropertyChangeListeners();
  }
  
  protected void insureRowContinuity()
  {
    if ((selectionMode == 2) && (selection != null) && (rowMapper != null))
    {
      DefaultListSelectionModel localDefaultListSelectionModel = listSelectionModel;
      int i = localDefaultListSelectionModel.getMinSelectionIndex();
      if (i != -1)
      {
        int j = i;
        int k = localDefaultListSelectionModel.getMaxSelectionIndex();
        while (j <= k)
        {
          if (!localDefaultListSelectionModel.isSelectedIndex(j)) {
            if (j == i)
            {
              clearSelection();
            }
            else
            {
              TreePath[] arrayOfTreePath = new TreePath[j - i];
              int[] arrayOfInt = rowMapper.getRowsForPaths(selection);
              for (int m = 0; m < arrayOfInt.length; m++) {
                if (arrayOfInt[m] < j) {
                  arrayOfTreePath[(arrayOfInt[m] - i)] = selection[m];
                }
              }
              setSelectionPaths(arrayOfTreePath);
              break;
            }
          }
          j++;
        }
      }
    }
    else if ((selectionMode == 1) && (selection != null) && (selection.length > 1))
    {
      setSelectionPath(selection[0]);
    }
  }
  
  protected boolean arePathsContiguous(TreePath[] paramArrayOfTreePath)
  {
    if ((rowMapper == null) || (paramArrayOfTreePath.length < 2)) {
      return true;
    }
    BitSet localBitSet = new BitSet(32);
    int m = paramArrayOfTreePath.length;
    int n = 0;
    TreePath[] arrayOfTreePath = new TreePath[1];
    arrayOfTreePath[0] = paramArrayOfTreePath[0];
    int k = rowMapper.getRowsForPaths(arrayOfTreePath)[0];
    for (int j = 0; j < m; j++) {
      if (paramArrayOfTreePath[j] != null)
      {
        arrayOfTreePath[0] = paramArrayOfTreePath[j];
        int[] arrayOfInt = rowMapper.getRowsForPaths(arrayOfTreePath);
        if (arrayOfInt == null) {
          return false;
        }
        int i = arrayOfInt[0];
        if ((i == -1) || (i < k - m) || (i > k + m)) {
          return false;
        }
        if (i < k) {
          k = i;
        }
        if (!localBitSet.get(i))
        {
          localBitSet.set(i);
          n++;
        }
      }
    }
    int i1 = n + k;
    for (j = k; j < i1; j++) {
      if (!localBitSet.get(j)) {
        return false;
      }
    }
    return true;
  }
  
  protected boolean canPathsBeAdded(TreePath[] paramArrayOfTreePath)
  {
    if ((paramArrayOfTreePath == null) || (paramArrayOfTreePath.length == 0) || (rowMapper == null) || (selection == null) || (selectionMode == 4)) {
      return true;
    }
    BitSet localBitSet = new BitSet();
    DefaultListSelectionModel localDefaultListSelectionModel = listSelectionModel;
    int k = localDefaultListSelectionModel.getMinSelectionIndex();
    int m = localDefaultListSelectionModel.getMaxSelectionIndex();
    TreePath[] arrayOfTreePath = new TreePath[1];
    if (k != -1) {
      for (j = k; j <= m; j++) {
        if (localDefaultListSelectionModel.isSelectedIndex(j)) {
          localBitSet.set(j);
        }
      }
    }
    arrayOfTreePath[0] = paramArrayOfTreePath[0];
    k = m = rowMapper.getRowsForPaths(arrayOfTreePath)[0];
    for (int j = paramArrayOfTreePath.length - 1; j >= 0; j--) {
      if (paramArrayOfTreePath[j] != null)
      {
        arrayOfTreePath[0] = paramArrayOfTreePath[j];
        int[] arrayOfInt = rowMapper.getRowsForPaths(arrayOfTreePath);
        if (arrayOfInt == null) {
          return false;
        }
        int i = arrayOfInt[0];
        k = Math.min(i, k);
        m = Math.max(i, m);
        if (i == -1) {
          return false;
        }
        localBitSet.set(i);
      }
    }
    for (j = k; j <= m; j++) {
      if (!localBitSet.get(j)) {
        return false;
      }
    }
    return true;
  }
  
  protected boolean canPathsBeRemoved(TreePath[] paramArrayOfTreePath)
  {
    if ((rowMapper == null) || (selection == null) || (selectionMode == 4)) {
      return true;
    }
    BitSet localBitSet = new BitSet();
    int j = paramArrayOfTreePath.length;
    int k = -1;
    int m = 0;
    TreePath[] arrayOfTreePath = new TreePath[1];
    lastPaths.clear();
    for (int i = 0; i < j; i++) {
      if (paramArrayOfTreePath[i] != null) {
        lastPaths.put(paramArrayOfTreePath[i], Boolean.TRUE);
      }
    }
    for (i = selection.length - 1; i >= 0; i--) {
      if (lastPaths.get(selection[i]) == null)
      {
        arrayOfTreePath[0] = selection[i];
        int[] arrayOfInt = rowMapper.getRowsForPaths(arrayOfTreePath);
        if ((arrayOfInt != null) && (arrayOfInt[0] != -1) && (!localBitSet.get(arrayOfInt[0])))
        {
          m++;
          if (k == -1) {
            k = arrayOfInt[0];
          } else {
            k = Math.min(k, arrayOfInt[0]);
          }
          localBitSet.set(arrayOfInt[0]);
        }
      }
    }
    lastPaths.clear();
    if (m > 1) {
      for (i = k + m - 1; i >= k; i--) {
        if (!localBitSet.get(i)) {
          return false;
        }
      }
    }
    return true;
  }
  
  @Deprecated
  protected void notifyPathChange(Vector<?> paramVector, TreePath paramTreePath)
  {
    int i = paramVector.size();
    boolean[] arrayOfBoolean = new boolean[i];
    TreePath[] arrayOfTreePath = new TreePath[i];
    for (int j = 0; j < i; j++)
    {
      PathPlaceHolder localPathPlaceHolder = (PathPlaceHolder)paramVector.elementAt(j);
      arrayOfBoolean[j] = isNew;
      arrayOfTreePath[j] = path;
    }
    TreeSelectionEvent localTreeSelectionEvent = new TreeSelectionEvent(this, arrayOfTreePath, arrayOfBoolean, paramTreePath, leadPath);
    fireValueChanged(localTreeSelectionEvent);
  }
  
  protected void updateLeadIndex()
  {
    if (leadPath != null)
    {
      if (selection == null)
      {
        leadPath = null;
        leadIndex = (leadRow = -1);
      }
      else
      {
        leadRow = (leadIndex = -1);
        for (int i = selection.length - 1; i >= 0; i--) {
          if (selection[i] == leadPath)
          {
            leadIndex = i;
            break;
          }
        }
      }
    }
    else {
      leadIndex = -1;
    }
  }
  
  protected void insureUniqueness() {}
  
  public String toString()
  {
    int i = getSelectionCount();
    StringBuffer localStringBuffer = new StringBuffer();
    int[] arrayOfInt;
    if (rowMapper != null) {
      arrayOfInt = rowMapper.getRowsForPaths(selection);
    } else {
      arrayOfInt = null;
    }
    localStringBuffer.append(getClass().getName() + " " + hashCode() + " [ ");
    for (int j = 0; j < i; j++) {
      if (arrayOfInt != null) {
        localStringBuffer.append(selection[j].toString() + "@" + Integer.toString(arrayOfInt[j]) + " ");
      } else {
        localStringBuffer.append(selection[j].toString() + " ");
      }
    }
    localStringBuffer.append("]");
    return localStringBuffer.toString();
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    DefaultTreeSelectionModel localDefaultTreeSelectionModel = (DefaultTreeSelectionModel)super.clone();
    changeSupport = null;
    if (selection != null)
    {
      int i = selection.length;
      selection = new TreePath[i];
      System.arraycopy(selection, 0, selection, 0, i);
    }
    listenerList = new EventListenerList();
    listSelectionModel = ((DefaultListSelectionModel)listSelectionModel.clone());
    uniquePaths = new Hashtable();
    lastPaths = new Hashtable();
    tempPaths = new TreePath[1];
    return localDefaultTreeSelectionModel;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    Object[] arrayOfObject;
    if ((rowMapper != null) && ((rowMapper instanceof Serializable)))
    {
      arrayOfObject = new Object[2];
      arrayOfObject[0] = "rowMapper";
      arrayOfObject[1] = rowMapper;
    }
    else
    {
      arrayOfObject = new Object[0];
    }
    paramObjectOutputStream.writeObject(arrayOfObject);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    Object[] arrayOfObject = (Object[])paramObjectInputStream.readObject();
    if ((arrayOfObject.length > 0) && (arrayOfObject[0].equals("rowMapper"))) {
      rowMapper = ((RowMapper)arrayOfObject[1]);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\tree\DefaultTreeSelectionModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */