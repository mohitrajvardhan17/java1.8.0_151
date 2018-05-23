package com.sun.org.apache.xalan.internal.xsltc.runtime;

public final class Operators
{
  public static final int EQ = 0;
  public static final int NE = 1;
  public static final int GT = 2;
  public static final int LT = 3;
  public static final int GE = 4;
  public static final int LE = 5;
  private static final String[] names = { "=", "!=", ">", "<", ">=", "<=" };
  private static final int[] swapOpArray = { 0, 1, 3, 2, 5, 4 };
  
  public Operators() {}
  
  public static final String getOpNames(int paramInt)
  {
    return names[paramInt];
  }
  
  public static final int swapOp(int paramInt)
  {
    return swapOpArray[paramInt];
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\runtime\Operators.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */