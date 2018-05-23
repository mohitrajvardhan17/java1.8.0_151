package javax.swing.text.html.parser;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.Vector;
import sun.awt.AppContext;

public final class Element
  implements DTDConstants, Serializable
{
  public int index;
  public String name;
  public boolean oStart;
  public boolean oEnd;
  public BitSet inclusions;
  public BitSet exclusions;
  public int type = 19;
  public ContentModel content;
  public AttributeList atts;
  public Object data;
  private static final Object MAX_INDEX_KEY = new Object();
  static Hashtable<String, Integer> contentTypes = new Hashtable();
  
  Element() {}
  
  Element(String paramString, int paramInt)
  {
    name = paramString;
    index = paramInt;
    if (paramInt > getMaxIndex()) {
      AppContext.getAppContext().put(MAX_INDEX_KEY, Integer.valueOf(paramInt));
    }
  }
  
  static int getMaxIndex()
  {
    Integer localInteger = (Integer)AppContext.getAppContext().get(MAX_INDEX_KEY);
    return localInteger != null ? localInteger.intValue() : 0;
  }
  
  public String getName()
  {
    return name;
  }
  
  public boolean omitStart()
  {
    return oStart;
  }
  
  public boolean omitEnd()
  {
    return oEnd;
  }
  
  public int getType()
  {
    return type;
  }
  
  public ContentModel getContent()
  {
    return content;
  }
  
  public AttributeList getAttributes()
  {
    return atts;
  }
  
  public int getIndex()
  {
    return index;
  }
  
  public boolean isEmpty()
  {
    return type == 17;
  }
  
  public String toString()
  {
    return name;
  }
  
  public AttributeList getAttribute(String paramString)
  {
    for (AttributeList localAttributeList = atts; localAttributeList != null; localAttributeList = next) {
      if (name.equals(paramString)) {
        return localAttributeList;
      }
    }
    return null;
  }
  
  public AttributeList getAttributeByValue(String paramString)
  {
    for (AttributeList localAttributeList = atts; localAttributeList != null; localAttributeList = next) {
      if ((values != null) && (values.contains(paramString))) {
        return localAttributeList;
      }
    }
    return null;
  }
  
  public static int name2type(String paramString)
  {
    Integer localInteger = (Integer)contentTypes.get(paramString);
    return localInteger != null ? localInteger.intValue() : 0;
  }
  
  static
  {
    contentTypes.put("CDATA", Integer.valueOf(1));
    contentTypes.put("RCDATA", Integer.valueOf(16));
    contentTypes.put("EMPTY", Integer.valueOf(17));
    contentTypes.put("ANY", Integer.valueOf(19));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\parser\Element.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */