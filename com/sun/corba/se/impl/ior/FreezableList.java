package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.MakeImmutable;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

public class FreezableList
  extends AbstractList
{
  private List delegate = null;
  private boolean immutable = false;
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (!(paramObject instanceof FreezableList)) {
      return false;
    }
    FreezableList localFreezableList = (FreezableList)paramObject;
    return (delegate.equals(delegate)) && (immutable == immutable);
  }
  
  public int hashCode()
  {
    return delegate.hashCode();
  }
  
  public FreezableList(List paramList, boolean paramBoolean)
  {
    delegate = paramList;
    immutable = paramBoolean;
  }
  
  public FreezableList(List paramList)
  {
    this(paramList, false);
  }
  
  public void makeImmutable()
  {
    immutable = true;
  }
  
  public boolean isImmutable()
  {
    return immutable;
  }
  
  public void makeElementsImmutable()
  {
    Iterator localIterator = iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      if ((localObject instanceof MakeImmutable))
      {
        MakeImmutable localMakeImmutable = (MakeImmutable)localObject;
        localMakeImmutable.makeImmutable();
      }
    }
  }
  
  public int size()
  {
    return delegate.size();
  }
  
  public Object get(int paramInt)
  {
    return delegate.get(paramInt);
  }
  
  public Object set(int paramInt, Object paramObject)
  {
    if (immutable) {
      throw new UnsupportedOperationException();
    }
    return delegate.set(paramInt, paramObject);
  }
  
  public void add(int paramInt, Object paramObject)
  {
    if (immutable) {
      throw new UnsupportedOperationException();
    }
    delegate.add(paramInt, paramObject);
  }
  
  public Object remove(int paramInt)
  {
    if (immutable) {
      throw new UnsupportedOperationException();
    }
    return delegate.remove(paramInt);
  }
  
  public List subList(int paramInt1, int paramInt2)
  {
    List localList = delegate.subList(paramInt1, paramInt2);
    FreezableList localFreezableList = new FreezableList(localList, immutable);
    return localFreezableList;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\FreezableList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */