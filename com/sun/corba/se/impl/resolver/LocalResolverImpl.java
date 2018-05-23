package com.sun.corba.se.impl.resolver;

import com.sun.corba.se.spi.orbutil.closure.Closure;
import com.sun.corba.se.spi.resolver.LocalResolver;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LocalResolverImpl
  implements LocalResolver
{
  Map nameToClosure = new HashMap();
  
  public LocalResolverImpl() {}
  
  public synchronized org.omg.CORBA.Object resolve(String paramString)
  {
    Closure localClosure = (Closure)nameToClosure.get(paramString);
    if (localClosure == null) {
      return null;
    }
    return (org.omg.CORBA.Object)localClosure.evaluate();
  }
  
  public synchronized Set list()
  {
    return nameToClosure.keySet();
  }
  
  public synchronized void register(String paramString, Closure paramClosure)
  {
    nameToClosure.put(paramString, paramClosure);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\resolver\LocalResolverImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */