package com.sun.corba.se.impl.resolver;

import com.sun.corba.se.spi.resolver.Resolver;
import java.util.HashSet;
import java.util.Set;

public class CompositeResolverImpl
  implements Resolver
{
  private Resolver first;
  private Resolver second;
  
  public CompositeResolverImpl(Resolver paramResolver1, Resolver paramResolver2)
  {
    first = paramResolver1;
    second = paramResolver2;
  }
  
  public org.omg.CORBA.Object resolve(String paramString)
  {
    org.omg.CORBA.Object localObject = first.resolve(paramString);
    if (localObject == null) {
      localObject = second.resolve(paramString);
    }
    return localObject;
  }
  
  public Set list()
  {
    HashSet localHashSet = new HashSet();
    localHashSet.addAll(first.list());
    localHashSet.addAll(second.list());
    return localHashSet;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\resolver\CompositeResolverImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */