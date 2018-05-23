package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;

public class NullSafeAccessor<B, V, P>
  extends Accessor<B, V>
{
  private final Accessor<B, V> core;
  private final Lister<B, V, ?, P> lister;
  
  public NullSafeAccessor(Accessor<B, V> paramAccessor, Lister<B, V, ?, P> paramLister)
  {
    super(paramAccessor.getValueType());
    core = paramAccessor;
    lister = paramLister;
  }
  
  public V get(B paramB)
    throws AccessorException
  {
    Object localObject1 = core.get(paramB);
    if (localObject1 == null)
    {
      Object localObject2 = lister.startPacking(paramB, core);
      lister.endPacking(localObject2, paramB, core);
      localObject1 = core.get(paramB);
    }
    return (V)localObject1;
  }
  
  public void set(B paramB, V paramV)
    throws AccessorException
  {
    core.set(paramB, paramV);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\NullSafeAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */