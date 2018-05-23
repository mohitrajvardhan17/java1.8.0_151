package javax.swing.text.html.parser;

import java.util.Hashtable;

public final class Entity
  implements DTDConstants
{
  public String name;
  public int type;
  public char[] data;
  static Hashtable<String, Integer> entityTypes = new Hashtable();
  
  public Entity(String paramString, int paramInt, char[] paramArrayOfChar)
  {
    name = paramString;
    type = paramInt;
    data = paramArrayOfChar;
  }
  
  public String getName()
  {
    return name;
  }
  
  public int getType()
  {
    return type & 0xFFFF;
  }
  
  public boolean isParameter()
  {
    return (type & 0x40000) != 0;
  }
  
  public boolean isGeneral()
  {
    return (type & 0x10000) != 0;
  }
  
  public char[] getData()
  {
    return data;
  }
  
  public String getString()
  {
    return new String(data, 0, data.length);
  }
  
  public static int name2type(String paramString)
  {
    Integer localInteger = (Integer)entityTypes.get(paramString);
    return localInteger == null ? 1 : localInteger.intValue();
  }
  
  static
  {
    entityTypes.put("PUBLIC", Integer.valueOf(10));
    entityTypes.put("CDATA", Integer.valueOf(1));
    entityTypes.put("SDATA", Integer.valueOf(11));
    entityTypes.put("PI", Integer.valueOf(12));
    entityTypes.put("STARTTAG", Integer.valueOf(13));
    entityTypes.put("ENDTAG", Integer.valueOf(14));
    entityTypes.put("MS", Integer.valueOf(15));
    entityTypes.put("MD", Integer.valueOf(16));
    entityTypes.put("SYSTEM", Integer.valueOf(17));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\parser\Entity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */