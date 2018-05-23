package com.sun.org.apache.xerces.internal.util;

import java.io.PrintStream;

public final class IntStack
{
  private int fDepth;
  private int[] fData;
  
  public IntStack() {}
  
  public int size()
  {
    return fDepth;
  }
  
  public void push(int paramInt)
  {
    ensureCapacity(fDepth + 1);
    fData[(fDepth++)] = paramInt;
  }
  
  public int peek()
  {
    return fData[(fDepth - 1)];
  }
  
  public int elementAt(int paramInt)
  {
    return fData[paramInt];
  }
  
  public int pop()
  {
    return fData[(--fDepth)];
  }
  
  public void clear()
  {
    fDepth = 0;
  }
  
  public void print()
  {
    System.out.print('(');
    System.out.print(fDepth);
    System.out.print(") {");
    for (int i = 0; i < fDepth; i++)
    {
      if (i == 3)
      {
        System.out.print(" ...");
        break;
      }
      System.out.print(' ');
      System.out.print(fData[i]);
      if (i < fDepth - 1) {
        System.out.print(',');
      }
    }
    System.out.print(" }");
    System.out.println();
  }
  
  private void ensureCapacity(int paramInt)
  {
    if (fData == null)
    {
      fData = new int[32];
    }
    else if (fData.length <= paramInt)
    {
      int[] arrayOfInt = new int[fData.length * 2];
      System.arraycopy(fData, 0, arrayOfInt, 0, fData.length);
      fData = arrayOfInt;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\IntStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */