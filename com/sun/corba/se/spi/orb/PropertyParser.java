package com.sun.corba.se.spi.orb;

import com.sun.corba.se.impl.orb.ParserAction;
import com.sun.corba.se.impl.orb.ParserActionFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertyParser
{
  private List actions = new LinkedList();
  
  public PropertyParser() {}
  
  public PropertyParser add(String paramString1, Operation paramOperation, String paramString2)
  {
    actions.add(ParserActionFactory.makeNormalAction(paramString1, paramOperation, paramString2));
    return this;
  }
  
  public PropertyParser addPrefix(String paramString1, Operation paramOperation, String paramString2, Class paramClass)
  {
    actions.add(ParserActionFactory.makePrefixAction(paramString1, paramOperation, paramString2, paramClass));
    return this;
  }
  
  public Map parse(Properties paramProperties)
  {
    HashMap localHashMap = new HashMap();
    Iterator localIterator = actions.iterator();
    while (localIterator.hasNext())
    {
      ParserAction localParserAction = (ParserAction)localIterator.next();
      Object localObject = localParserAction.apply(paramProperties);
      if (localObject != null) {
        localHashMap.put(localParserAction.getFieldName(), localObject);
      }
    }
    return localHashMap;
  }
  
  public Iterator iterator()
  {
    return actions.iterator();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orb\PropertyParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */