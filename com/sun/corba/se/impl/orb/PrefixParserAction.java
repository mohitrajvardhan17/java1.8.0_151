package com.sun.corba.se.impl.orb;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orb.StringPair;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class PrefixParserAction
  extends ParserActionBase
{
  private Class componentType;
  private ORBUtilSystemException wrapper;
  
  public PrefixParserAction(String paramString1, Operation paramOperation, String paramString2, Class paramClass)
  {
    super(paramString1, true, paramOperation, paramString2);
    componentType = paramClass;
    wrapper = ORBUtilSystemException.get("orb.lifecycle");
  }
  
  public Object apply(Properties paramProperties)
  {
    String str1 = getPropertyName();
    int i = str1.length();
    if (str1.charAt(i - 1) != '.')
    {
      str1 = str1 + '.';
      i++;
    }
    LinkedList localLinkedList = new LinkedList();
    Iterator localIterator1 = paramProperties.keySet().iterator();
    Object localObject1;
    Object localObject2;
    while (localIterator1.hasNext())
    {
      String str2 = (String)localIterator1.next();
      if (str2.startsWith(str1))
      {
        localObject1 = str2.substring(i);
        String str3 = paramProperties.getProperty(str2);
        StringPair localStringPair = new StringPair((String)localObject1, str3);
        localObject2 = getOperation().operate(localStringPair);
        localLinkedList.add(localObject2);
      }
    }
    int j = localLinkedList.size();
    if (j > 0)
    {
      localObject1 = null;
      try
      {
        localObject1 = Array.newInstance(componentType, j);
      }
      catch (Throwable localThrowable1)
      {
        throw wrapper.couldNotCreateArray(localThrowable1, getPropertyName(), componentType, new Integer(j));
      }
      Iterator localIterator2 = localLinkedList.iterator();
      for (int k = 0; localIterator2.hasNext(); k++)
      {
        localObject2 = localIterator2.next();
        try
        {
          Array.set(localObject1, k, localObject2);
        }
        catch (Throwable localThrowable2)
        {
          throw wrapper.couldNotSetArray(localThrowable2, getPropertyName(), new Integer(k), componentType, new Integer(j), localObject2.toString());
        }
      }
      return localObject1;
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orb\PrefixParserAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */