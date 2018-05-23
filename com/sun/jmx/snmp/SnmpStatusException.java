package com.sun.jmx.snmp;

public class SnmpStatusException
  extends Exception
  implements SnmpDefinitions
{
  private static final long serialVersionUID = 5809485694133115675L;
  public static final int noSuchName = 2;
  public static final int badValue = 3;
  public static final int readOnly = 4;
  public static final int noAccess = 6;
  public static final int noSuchInstance = 224;
  public static final int noSuchObject = 225;
  private int errorStatus = 0;
  private int errorIndex = -1;
  
  public SnmpStatusException(int paramInt)
  {
    errorStatus = paramInt;
  }
  
  public SnmpStatusException(int paramInt1, int paramInt2)
  {
    errorStatus = paramInt1;
    errorIndex = paramInt2;
  }
  
  public SnmpStatusException(String paramString)
  {
    super(paramString);
  }
  
  public SnmpStatusException(SnmpStatusException paramSnmpStatusException, int paramInt)
  {
    super(paramSnmpStatusException.getMessage());
    errorStatus = errorStatus;
    errorIndex = paramInt;
  }
  
  public int getStatus()
  {
    return errorStatus;
  }
  
  public int getErrorIndex()
  {
    return errorIndex;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\SnmpStatusException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */