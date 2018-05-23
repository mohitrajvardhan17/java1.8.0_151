package com.sun.xml.internal.fastinfoset.algorithm;

public abstract class IntegerEncodingAlgorithm
  extends BuiltInEncodingAlgorithm
{
  public static final int SHORT_SIZE = 2;
  public static final int INT_SIZE = 4;
  public static final int LONG_SIZE = 8;
  public static final int SHORT_MAX_CHARACTER_SIZE = 6;
  public static final int INT_MAX_CHARACTER_SIZE = 11;
  public static final int LONG_MAX_CHARACTER_SIZE = 20;
  
  public IntegerEncodingAlgorithm() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\algorithm\IntegerEncodingAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */