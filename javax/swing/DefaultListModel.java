package javax.swing;

import java.util.Enumeration;
import java.util.Vector;

public class DefaultListModel<E>
  extends AbstractListModel<E>
{
  private Vector<E> delegate = new Vector();
  
  public DefaultListModel() {}
  
  public int getSize()
  {
    return delegate.size();
  }
  
  public E getElementAt(int paramInt)
  {
    return (E)delegate.elementAt(paramInt);
  }
  
  public void copyInto(Object[] paramArrayOfObject)
  {
    delegate.copyInto(paramArrayOfObject);
  }
  
  public void trimToSize()
  {
    delegate.trimToSize();
  }
  
  public void ensureCapacity(int paramInt)
  {
    delegate.ensureCapacity(paramInt);
  }
  
  public void setSize(int paramInt)
  {
    int i = delegate.size();
    delegate.setSize(paramInt);
    if (i > paramInt) {
      fireIntervalRemoved(this, paramInt, i - 1);
    } else if (i < paramInt) {
      fireIntervalAdded(this, i, paramInt - 1);
    }
  }
  
  public int capacity()
  {
    return delegate.capacity();
  }
  
  public int size()
  {
    return delegate.size();
  }
  
  public boolean isEmpty()
  {
    return delegate.isEmpty();
  }
  
  public Enumeration<E> elements()
  {
    return delegate.elements();
  }
  
  public boolean contains(Object paramObject)
  {
    return delegate.contains(paramObject);
  }
  
  public int indexOf(Object paramObject)
  {
    return delegate.indexOf(paramObject);
  }
  
  public int indexOf(Object paramObject, int paramInt)
  {
    return delegate.indexOf(paramObject, paramInt);
  }
  
  public int lastIndexOf(Object paramObject)
  {
    return delegate.lastIndexOf(paramObject);
  }
  
  public int lastIndexOf(Object paramObject, int paramInt)
  {
    return delegate.lastIndexOf(paramObject, paramInt);
  }
  
  public E elementAt(int paramInt)
  {
    return (E)delegate.elementAt(paramInt);
  }
  
  public E firstElement()
  {
    return (E)delegate.firstElement();
  }
  
  public E lastElement()
  {
    return (E)delegate.lastElement();
  }
  
  public void setElementAt(E paramE, int paramInt)
  {
    delegate.setElementAt(paramE, paramInt);
    fireContentsChanged(this, paramInt, paramInt);
  }
  
  public void removeElementAt(int paramInt)
  {
    delegate.removeElementAt(paramInt);
    fireIntervalRemoved(this, paramInt, paramInt);
  }
  
  public void insertElementAt(E paramE, int paramInt)
  {
    delegate.insertElementAt(paramE, paramInt);
    fireIntervalAdded(this, paramInt, paramInt);
  }
  
  public void addElement(E paramE)
  {
    int i = delegate.size();
    delegate.addElement(paramE);
    fireIntervalAdded(this, i, i);
  }
  
  public boolean removeElement(Object paramObject)
  {
    int i = indexOf(paramObject);
    boolean bool = delegate.removeElement(paramObject);
    if (i >= 0) {
      fireIntervalRemoved(this, i, i);
    }
    return bool;
  }
  
  public void removeAllElements()
  {
    int i = delegate.size() - 1;
    delegate.removeAllElements();
    if (i >= 0) {
      fireIntervalRemoved(this, 0, i);
    }
  }
  
  public String toString()
  {
    return delegate.toString();
  }
  
  public Object[] toArray()
  {
    Object[] arrayOfObject = new Object[delegate.size()];
    delegate.copyInto(arrayOfObject);
    return arrayOfObject;
  }
  
  public E get(int paramInt)
  {
    return (E)delegate.elementAt(paramInt);
  }
  
  public E set(int paramInt, E paramE)
  {
    Object localObject = delegate.elementAt(paramInt);
    delegate.setElementAt(paramE, paramInt);
    fireContentsChanged(this, paramInt, paramInt);
    return (E)localObject;
  }
  
  public void add(int paramInt, E paramE)
  {
    delegate.insertElementAt(paramE, paramInt);
    fireIntervalAdded(this, paramInt, paramInt);
  }
  
  public E remove(int paramInt)
  {
    Object localObject = delegate.elementAt(paramInt);
    delegate.removeElementAt(paramInt);
    fireIntervalRemoved(this, paramInt, paramInt);
    return (E)localObject;
  }
  
  public void clear()
  {
    int i = delegate.size() - 1;
    delegate.removeAllElements();
    if (i >= 0) {
      fireIntervalRemoved(this, 0, i);
    }
  }
  
  public void removeRange(int paramInt1, int paramInt2)
  {
    if (paramInt1 > paramInt2) {
      throw new IllegalArgumentException("fromIndex must be <= toIndex");
    }
    for (int i = paramInt2; i >= paramInt1; i--) {
      delegate.removeElementAt(i);
    }
    fireIntervalRemoved(this, paramInt1, paramInt2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\DefaultListModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */