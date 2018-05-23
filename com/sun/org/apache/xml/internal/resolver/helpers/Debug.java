package com.sun.org.apache.xml.internal.resolver.helpers;

import java.io.PrintStream;

public class Debug
{
  protected int debug = 0;
  
  public Debug() {}
  
  public void setDebug(int paramInt)
  {
    debug = paramInt;
  }
  
  public int getDebug()
  {
    return debug;
  }
  
  public void message(int paramInt, String paramString)
  {
    if (debug >= paramInt) {
      System.out.println(paramString);
    }
  }
  
  public void message(int paramInt, String paramString1, String paramString2)
  {
    if (debug >= paramInt) {
      System.out.println(paramString1 + ": " + paramString2);
    }
  }
  
  public void message(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    if (debug >= paramInt)
    {
      System.out.println(paramString1 + ": " + paramString2);
      System.out.println("\t" + paramString3);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\resolver\helpers\Debug.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */