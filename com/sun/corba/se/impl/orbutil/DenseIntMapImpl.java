package com.sun.corba.se.impl.orbutil;

import java.util.ArrayList;

public class DenseIntMapImpl
{
  private ArrayList list = new ArrayList();
  
  public DenseIntMapImpl() {}
  
  private void checkKey(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Key must be >= 0.");
    }
  }
  
  public Object get(int paramInt)
  {
    checkKey(paramInt);
    Object localObject = null;
    if (paramInt < list.size()) {
      localObject = list.get(paramInt);
    }
    return localObject;
  }
  
  public void set(int paramInt, Object paramObject)
  {
    checkKey(paramInt);
    extend(paramInt);
    list.set(paramInt, paramObject);
  }
  
  private void extend(int paramInt)
  {
    if (paramInt >= list.size())
    {
      list.ensureCapacity(paramInt + 1);
      int i = list.size();
      while (i++ <= paramInt) {
        list.add(null);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\DenseIntMapImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */