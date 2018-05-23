package javax.xml.crypto.dsig.spec;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class XPathType
{
  private final String expression;
  private final Filter filter;
  private Map<String, String> nsMap;
  
  public XPathType(String paramString, Filter paramFilter)
  {
    if (paramString == null) {
      throw new NullPointerException("expression cannot be null");
    }
    if (paramFilter == null) {
      throw new NullPointerException("filter cannot be null");
    }
    expression = paramString;
    filter = paramFilter;
    nsMap = Collections.emptyMap();
  }
  
  public XPathType(String paramString, Filter paramFilter, Map paramMap)
  {
    this(paramString, paramFilter);
    if (paramMap == null) {
      throw new NullPointerException("namespaceMap cannot be null");
    }
    HashMap localHashMap = new HashMap(paramMap);
    Iterator localIterator = localHashMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      localObject = (Map.Entry)localIterator.next();
      if ((!(((Map.Entry)localObject).getKey() instanceof String)) || (!(((Map.Entry)localObject).getValue() instanceof String))) {
        throw new ClassCastException("not a String");
      }
    }
    Object localObject = localHashMap;
    nsMap = Collections.unmodifiableMap((Map)localObject);
  }
  
  public String getExpression()
  {
    return expression;
  }
  
  public Filter getFilter()
  {
    return filter;
  }
  
  public Map getNamespaceMap()
  {
    return nsMap;
  }
  
  public static class Filter
  {
    private final String operation;
    public static final Filter INTERSECT = new Filter("intersect");
    public static final Filter SUBTRACT = new Filter("subtract");
    public static final Filter UNION = new Filter("union");
    
    private Filter(String paramString)
    {
      operation = paramString;
    }
    
    public String toString()
    {
      return operation;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\dsig\spec\XPathType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */