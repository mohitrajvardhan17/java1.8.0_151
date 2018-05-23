package com.sun.corba.se.impl.resolver;

import com.sun.corba.se.spi.orbutil.closure.Closure;
import com.sun.corba.se.spi.resolver.LocalResolver;
import com.sun.corba.se.spi.resolver.Resolver;
import java.util.Set;

public class SplitLocalResolverImpl
  implements LocalResolver
{
  private Resolver resolver;
  private LocalResolver localResolver;
  
  public SplitLocalResolverImpl(Resolver paramResolver, LocalResolver paramLocalResolver)
  {
    resolver = paramResolver;
    localResolver = paramLocalResolver;
  }
  
  public void register(String paramString, Closure paramClosure)
  {
    localResolver.register(paramString, paramClosure);
  }
  
  public org.omg.CORBA.Object resolve(String paramString)
  {
    return resolver.resolve(paramString);
  }
  
  public Set list()
  {
    return resolver.list();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\resolver\SplitLocalResolverImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */