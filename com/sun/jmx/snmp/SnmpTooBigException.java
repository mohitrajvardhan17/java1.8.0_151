package com.sun.jmx.snmp;

public class SnmpTooBigException
  extends Exception
{
  private static final long serialVersionUID = 4754796246674803969L;
  private int varBindCount;
  
  public SnmpTooBigException()
  {
    varBindCount = 0;
  }
  
  public SnmpTooBigException(int paramInt)
  {
    varBindCount = paramInt;
  }
  
  public int getVarBindCount()
  {
    return varBindCount;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpTooBigException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */