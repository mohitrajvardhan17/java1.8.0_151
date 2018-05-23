package jdk.internal.util.xml.impl;

public class Pair
{
  public String name;
  public String value;
  public int num;
  public char[] chars;
  public int id;
  public Pair list;
  public Pair next;
  
  public Pair() {}
  
  public String qname()
  {
    return new String(chars, 1, chars.length - 1);
  }
  
  public String local()
  {
    if (chars[0] != 0) {
      return new String(chars, chars[0] + '\001', chars.length - chars[0] - 1);
    }
    return new String(chars, 1, chars.length - 1);
  }
  
  public String pref()
  {
    if (chars[0] != 0) {
      return new String(chars, 1, chars[0] - '\001');
    }
    return "";
  }
  
  public boolean eqpref(char[] paramArrayOfChar)
  {
    if (chars[0] == paramArrayOfChar[0])
    {
      int i = chars[0];
      for (int j = 1; j < i; j = (char)(j + 1)) {
        if (chars[j] != paramArrayOfChar[j]) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  public boolean eqname(char[] paramArrayOfChar)
  {
    int i = (char)chars.length;
    if (i == paramArrayOfChar.length)
    {
      for (int j = 0; j < i; j = (char)(j + 1)) {
        if (chars[j] != paramArrayOfChar[j]) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\util\xml\impl\Pair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */