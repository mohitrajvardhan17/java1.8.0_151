package com.sun.xml.internal.ws.api.model;

public enum ExceptionType
{
  WSDLException(0),  UserDefined(1);
  
  private final int exceptionType;
  
  private ExceptionType(int paramInt)
  {
    exceptionType = paramInt;
  }
  
  public int value()
  {
    return exceptionType;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\ExceptionType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */