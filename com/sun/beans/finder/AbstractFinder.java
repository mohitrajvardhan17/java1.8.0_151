package com.sun.beans.finder;

import java.lang.reflect.Executable;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

abstract class AbstractFinder<T extends Executable>
{
  private final Class<?>[] args;
  
  protected AbstractFinder(Class<?>[] paramArrayOfClass)
  {
    args = paramArrayOfClass;
  }
  
  protected boolean isValid(T paramT)
  {
    return Modifier.isPublic(paramT.getModifiers());
  }
  
  final T find(T[] paramArrayOfT)
    throws NoSuchMethodException
  {
    HashMap localHashMap = new HashMap();
    Object localObject1 = null;
    Object localObject2 = null;
    int i = 0;
    T ?;
    Class[] arrayOfClass1;
    for (? : paramArrayOfT) {
      if (isValid(?))
      {
        arrayOfClass1 = ?.getParameterTypes();
        if (arrayOfClass1.length == args.length)
        {
          PrimitiveWrapperMap.replacePrimitivesWithWrappers(arrayOfClass1);
          if (isAssignable(arrayOfClass1, args)) {
            if (localObject1 == null)
            {
              localObject1 = ?;
              localObject2 = arrayOfClass1;
            }
            else
            {
              boolean bool1 = isAssignable((Class[])localObject2, arrayOfClass1);
              boolean bool3 = isAssignable(arrayOfClass1, (Class[])localObject2);
              if ((bool3) && (bool1))
              {
                bool1 = !?.isSynthetic();
                bool3 = !((Executable)localObject1).isSynthetic();
              }
              if (bool3 == bool1)
              {
                i = 1;
              }
              else if (bool1)
              {
                localObject1 = ?;
                localObject2 = arrayOfClass1;
                i = 0;
              }
            }
          }
        }
        if (?.isVarArgs())
        {
          int m = arrayOfClass1.length - 1;
          if (m <= args.length)
          {
            Class[] arrayOfClass2 = new Class[args.length];
            System.arraycopy(arrayOfClass1, 0, arrayOfClass2, 0, m);
            if (m < args.length)
            {
              Class localClass = arrayOfClass1[m].getComponentType();
              if (localClass.isPrimitive()) {
                localClass = PrimitiveWrapperMap.getType(localClass.getName());
              }
              for (int n = m; n < args.length; n++) {
                arrayOfClass2[n] = localClass;
              }
            }
            localHashMap.put(?, arrayOfClass2);
          }
        }
      }
    }
    for (? : paramArrayOfT)
    {
      arrayOfClass1 = (Class[])localHashMap.get(?);
      if ((arrayOfClass1 != null) && (isAssignable(arrayOfClass1, args))) {
        if (localObject1 == null)
        {
          localObject1 = ?;
          localObject2 = arrayOfClass1;
        }
        else
        {
          boolean bool2 = isAssignable((Class[])localObject2, arrayOfClass1);
          boolean bool4 = isAssignable(arrayOfClass1, (Class[])localObject2);
          if ((bool4) && (bool2))
          {
            bool2 = !?.isSynthetic();
            bool4 = !((Executable)localObject1).isSynthetic();
          }
          if (bool4 == bool2)
          {
            if (localObject2 == localHashMap.get(localObject1)) {
              i = 1;
            }
          }
          else if (bool2)
          {
            localObject1 = ?;
            localObject2 = arrayOfClass1;
            i = 0;
          }
        }
      }
    }
    if (i != 0) {
      throw new NoSuchMethodException("Ambiguous methods are found");
    }
    if (localObject1 == null) {
      throw new NoSuchMethodException("Method is not found");
    }
    return (T)localObject1;
  }
  
  private boolean isAssignable(Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2)
  {
    for (int i = 0; i < args.length; i++) {
      if ((null != args[i]) && (!paramArrayOfClass1[i].isAssignableFrom(paramArrayOfClass2[i]))) {
        return false;
      }
    }
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\finder\AbstractFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */