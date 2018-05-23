package com.sun.tracing.dtrace;

public enum StabilityLevel
{
  INTERNAL(0),  PRIVATE(1),  OBSOLETE(2),  EXTERNAL(3),  UNSTABLE(4),  EVOLVING(5),  STABLE(6),  STANDARD(7);
  
  private int encoding;
  
  String toDisplayString()
  {
    return toString().substring(0, 1) + toString().substring(1).toLowerCase();
  }
  
  public int getEncoding()
  {
    return encoding;
  }
  
  private StabilityLevel(int paramInt)
  {
    encoding = paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\tracing\dtrace\StabilityLevel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */