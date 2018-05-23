package com.sun.naming.internal;

import java.lang.ref.WeakReference;

class NamedWeakReference<T>
  extends WeakReference<T>
{
  private final String name;
  
  NamedWeakReference(T paramT, String paramString)
  {
    super(paramT);
    name = paramString;
  }
  
  String getName()
  {
    return name;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\naming\internal\NamedWeakReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */