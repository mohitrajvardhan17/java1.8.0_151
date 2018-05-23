package com.sun.corba.se.impl.resolver;

import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orb.StringPair;
import com.sun.corba.se.spi.resolver.Resolver;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ORBInitRefResolverImpl
  implements Resolver
{
  Operation urlHandler;
  Map orbInitRefTable;
  
  public ORBInitRefResolverImpl(Operation paramOperation, StringPair[] paramArrayOfStringPair)
  {
    urlHandler = paramOperation;
    orbInitRefTable = new HashMap();
    for (int i = 0; i < paramArrayOfStringPair.length; i++)
    {
      StringPair localStringPair = paramArrayOfStringPair[i];
      orbInitRefTable.put(localStringPair.getFirst(), localStringPair.getSecond());
    }
  }
  
  public org.omg.CORBA.Object resolve(String paramString)
  {
    String str = (String)orbInitRefTable.get(paramString);
    if (str == null) {
      return null;
    }
    org.omg.CORBA.Object localObject = (org.omg.CORBA.Object)urlHandler.operate(str);
    return localObject;
  }
  
  public Set list()
  {
    return orbInitRefTable.keySet();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\resolver\ORBInitRefResolverImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */