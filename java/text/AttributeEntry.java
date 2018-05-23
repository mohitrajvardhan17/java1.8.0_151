package java.text;

import java.util.Map.Entry;

class AttributeEntry
  implements Map.Entry<AttributedCharacterIterator.Attribute, Object>
{
  private AttributedCharacterIterator.Attribute key;
  private Object value;
  
  AttributeEntry(AttributedCharacterIterator.Attribute paramAttribute, Object paramObject)
  {
    key = paramAttribute;
    value = paramObject;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof AttributeEntry)) {
      return false;
    }
    AttributeEntry localAttributeEntry = (AttributeEntry)paramObject;
    return (key.equals(key)) && (value == null ? value == null : value.equals(value));
  }
  
  public AttributedCharacterIterator.Attribute getKey()
  {
    return key;
  }
  
  public Object getValue()
  {
    return value;
  }
  
  public Object setValue(Object paramObject)
  {
    throw new UnsupportedOperationException();
  }
  
  public int hashCode()
  {
    return key.hashCode() ^ (value == null ? 0 : value.hashCode());
  }
  
  public String toString()
  {
    return key.toString() + "=" + value.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\AttributeEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */