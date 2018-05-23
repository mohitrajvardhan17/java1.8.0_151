package javax.xml.crypto.dsig.spec;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class XPathFilterParameterSpec
  implements TransformParameterSpec
{
  private String xPath;
  private Map<String, String> nsMap;
  
  public XPathFilterParameterSpec(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    xPath = paramString;
    nsMap = Collections.emptyMap();
  }
  
  public XPathFilterParameterSpec(String paramString, Map paramMap)
  {
    if ((paramString == null) || (paramMap == null)) {
      throw new NullPointerException();
    }
    xPath = paramString;
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
  
  public String getXPath()
  {
    return xPath;
  }
  
  public Map getNamespaceMap()
  {
    return nsMap;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\dsig\spec\XPathFilterParameterSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */