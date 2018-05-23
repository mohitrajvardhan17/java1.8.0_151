package com.sun.xml.internal.fastinfoset.alphabet;

public final class BuiltInRestrictedAlphabets
{
  public static final char[][] table = new char[2][];
  
  public BuiltInRestrictedAlphabets() {}
  
  static
  {
    table[0] = "0123456789-+.E ".toCharArray();
    table[1] = "0123456789-:TZ ".toCharArray();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\alphabet\BuiltInRestrictedAlphabets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */