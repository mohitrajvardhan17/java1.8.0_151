package com.sun.tracing.dtrace;

public enum DependencyClass
{
  UNKNOWN(0),  CPU(1),  PLATFORM(2),  GROUP(3),  ISA(4),  COMMON(5);
  
  private int encoding;
  
  public String toDisplayString()
  {
    return toString().substring(0, 1) + toString().substring(1).toLowerCase();
  }
  
  public int getEncoding()
  {
    return encoding;
  }
  
  private DependencyClass(int paramInt)
  {
    encoding = paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\tracing\dtrace\DependencyClass.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */