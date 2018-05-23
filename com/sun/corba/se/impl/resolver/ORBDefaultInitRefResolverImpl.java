package com.sun.corba.se.impl.resolver;

import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.resolver.Resolver;
import java.util.HashSet;
import java.util.Set;

public class ORBDefaultInitRefResolverImpl
  implements Resolver
{
  Operation urlHandler;
  String orbDefaultInitRef;
  
  public ORBDefaultInitRefResolverImpl(Operation paramOperation, String paramString)
  {
    urlHandler = paramOperation;
    orbDefaultInitRef = paramString;
  }
  
  public org.omg.CORBA.Object resolve(String paramString)
  {
    if (orbDefaultInitRef == null) {
      return null;
    }
    String str;
    if (orbDefaultInitRef.startsWith("corbaloc:")) {
      str = orbDefaultInitRef + "/" + paramString;
    } else {
      str = orbDefaultInitRef + "#" + paramString;
    }
    return (org.omg.CORBA.Object)urlHandler.operate(str);
  }
  
  public Set list()
  {
    return new HashSet();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\resolver\ORBDefaultInitRefResolverImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */