package com.sun.corba.se.impl.orbutil;

import java.lang.reflect.Array;

public final class ObjectUtility
{
  private ObjectUtility() {}
  
  public static Object concatenateArrays(Object paramObject1, Object paramObject2)
  {
    Class localClass1 = paramObject1.getClass().getComponentType();
    Class localClass2 = paramObject2.getClass().getComponentType();
    int i = Array.getLength(paramObject1);
    int j = Array.getLength(paramObject2);
    if ((localClass1 == null) || (localClass2 == null)) {
      throw new IllegalStateException("Arguments must be arrays");
    }
    if (!localClass1.equals(localClass2)) {
      throw new IllegalStateException("Arguments must be arrays with the same component type");
    }
    Object localObject = Array.newInstance(localClass1, i + j);
    int k = 0;
    for (int m = 0; m < i; m++) {
      Array.set(localObject, k++, Array.get(paramObject1, m));
    }
    for (m = 0; m < j; m++) {
      Array.set(localObject, k++, Array.get(paramObject2, m));
    }
    return localObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\ObjectUtility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */