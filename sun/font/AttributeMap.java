package sun.font;

import java.awt.font.TextAttribute;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class AttributeMap
  extends AbstractMap<TextAttribute, Object>
{
  private AttributeValues values;
  private Map<TextAttribute, Object> delegateMap;
  private static boolean first = false;
  
  public AttributeMap(AttributeValues paramAttributeValues)
  {
    values = paramAttributeValues;
  }
  
  public Set<Map.Entry<TextAttribute, Object>> entrySet()
  {
    return delegate().entrySet();
  }
  
  public Object put(TextAttribute paramTextAttribute, Object paramObject)
  {
    return delegate().put(paramTextAttribute, paramObject);
  }
  
  public AttributeValues getValues()
  {
    return values;
  }
  
  private Map<TextAttribute, Object> delegate()
  {
    if (delegateMap == null)
    {
      if (first)
      {
        first = false;
        Thread.dumpStack();
      }
      delegateMap = values.toMap(new HashMap(27));
      values = null;
    }
    return delegateMap;
  }
  
  public String toString()
  {
    if (values != null) {
      return "map of " + values.toString();
    }
    return super.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\AttributeMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */