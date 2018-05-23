package javax.swing;

import java.io.Serializable;
import java.util.Vector;

public class DefaultComboBoxModel<E>
  extends AbstractListModel<E>
  implements MutableComboBoxModel<E>, Serializable
{
  Vector<E> objects;
  Object selectedObject;
  
  public DefaultComboBoxModel()
  {
    objects = new Vector();
  }
  
  public DefaultComboBoxModel(E[] paramArrayOfE)
  {
    objects = new Vector(paramArrayOfE.length);
    int i = 0;
    int j = paramArrayOfE.length;
    while (i < j)
    {
      objects.addElement(paramArrayOfE[i]);
      i++;
    }
    if (getSize() > 0) {
      selectedObject = getElementAt(0);
    }
  }
  
  public DefaultComboBoxModel(Vector<E> paramVector)
  {
    objects = paramVector;
    if (getSize() > 0) {
      selectedObject = getElementAt(0);
    }
  }
  
  public void setSelectedItem(Object paramObject)
  {
    if (((selectedObject != null) && (!selectedObject.equals(paramObject))) || ((selectedObject == null) && (paramObject != null)))
    {
      selectedObject = paramObject;
      fireContentsChanged(this, -1, -1);
    }
  }
  
  public Object getSelectedItem()
  {
    return selectedObject;
  }
  
  public int getSize()
  {
    return objects.size();
  }
  
  public E getElementAt(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < objects.size())) {
      return (E)objects.elementAt(paramInt);
    }
    return null;
  }
  
  public int getIndexOf(Object paramObject)
  {
    return objects.indexOf(paramObject);
  }
  
  public void addElement(E paramE)
  {
    objects.addElement(paramE);
    fireIntervalAdded(this, objects.size() - 1, objects.size() - 1);
    if ((objects.size() == 1) && (selectedObject == null) && (paramE != null)) {
      setSelectedItem(paramE);
    }
  }
  
  public void insertElementAt(E paramE, int paramInt)
  {
    objects.insertElementAt(paramE, paramInt);
    fireIntervalAdded(this, paramInt, paramInt);
  }
  
  public void removeElementAt(int paramInt)
  {
    if (getElementAt(paramInt) == selectedObject) {
      if (paramInt == 0) {
        setSelectedItem(getSize() == 1 ? null : getElementAt(paramInt + 1));
      } else {
        setSelectedItem(getElementAt(paramInt - 1));
      }
    }
    objects.removeElementAt(paramInt);
    fireIntervalRemoved(this, paramInt, paramInt);
  }
  
  public void removeElement(Object paramObject)
  {
    int i = objects.indexOf(paramObject);
    if (i != -1) {
      removeElementAt(i);
    }
  }
  
  public void removeAllElements()
  {
    if (objects.size() > 0)
    {
      int i = 0;
      int j = objects.size() - 1;
      objects.removeAllElements();
      selectedObject = null;
      fireIntervalRemoved(this, i, j);
    }
    else
    {
      selectedObject = null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\DefaultComboBoxModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */