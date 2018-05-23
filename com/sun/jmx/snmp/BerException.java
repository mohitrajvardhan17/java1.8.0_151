package com.sun.jmx.snmp;

public class BerException
  extends Exception
{
  private static final long serialVersionUID = 494709767137042951L;
  public static final int BAD_VERSION = 1;
  private int errorType = 0;
  
  public BerException()
  {
    errorType = 0;
  }
  
  public BerException(int paramInt)
  {
    errorType = paramInt;
  }
  
  public boolean isInvalidSnmpVersion()
  {
    return errorType == 1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\BerException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */