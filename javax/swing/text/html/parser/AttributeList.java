package javax.swing.text.html.parser;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public final class AttributeList
  implements DTDConstants, Serializable
{
  public String name;
  public int type;
  public Vector<?> values;
  public int modifier;
  public String value;
  public AttributeList next;
  static Hashtable<Object, Object> attributeTypes = new Hashtable();
  
  AttributeList() {}
  
  public AttributeList(String paramString)
  {
    name = paramString;
  }
  
  public AttributeList(String paramString1, int paramInt1, int paramInt2, String paramString2, Vector<?> paramVector, AttributeList paramAttributeList)
  {
    name = paramString1;
    type = paramInt1;
    modifier = paramInt2;
    value = paramString2;
    values = paramVector;
    next = paramAttributeList;
  }
  
  public String getName()
  {
    return name;
  }
  
  public int getType()
  {
    return type;
  }
  
  public int getModifier()
  {
    return modifier;
  }
  
  public Enumeration<?> getValues()
  {
    return values != null ? values.elements() : null;
  }
  
  public String getValue()
  {
    return value;
  }
  
  public AttributeList getNext()
  {
    return next;
  }
  
  public String toString()
  {
    return name;
  }
  
  static void defineAttributeType(String paramString, int paramInt)
  {
    Integer localInteger = Integer.valueOf(paramInt);
    attributeTypes.put(paramString, localInteger);
    attributeTypes.put(localInteger, paramString);
  }
  
  public static int name2type(String paramString)
  {
    Integer localInteger = (Integer)attributeTypes.get(paramString);
    return localInteger == null ? 1 : localInteger.intValue();
  }
  
  public static String type2name(int paramInt)
  {
    return (String)attributeTypes.get(Integer.valueOf(paramInt));
  }
  
  static
  {
    defineAttributeType("CDATA", 1);
    defineAttributeType("ENTITY", 2);
    defineAttributeType("ENTITIES", 3);
    defineAttributeType("ID", 4);
    defineAttributeType("IDREF", 5);
    defineAttributeType("IDREFS", 6);
    defineAttributeType("NAME", 7);
    defineAttributeType("NAMES", 8);
    defineAttributeType("NMTOKEN", 9);
    defineAttributeType("NMTOKENS", 10);
    defineAttributeType("NOTATION", 11);
    defineAttributeType("NUMBER", 12);
    defineAttributeType("NUMBERS", 13);
    defineAttributeType("NUTOKEN", 14);
    defineAttributeType("NUTOKENS", 15);
    attributeTypes.put("fixed", Integer.valueOf(1));
    attributeTypes.put("required", Integer.valueOf(2));
    attributeTypes.put("current", Integer.valueOf(3));
    attributeTypes.put("conref", Integer.valueOf(4));
    attributeTypes.put("implied", Integer.valueOf(5));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\parser\AttributeList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */