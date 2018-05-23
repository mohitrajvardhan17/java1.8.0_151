package com.sun.corba.se.spi.protocol;

public enum RetryType
{
  NONE(false),  BEFORE_RESPONSE(true),  AFTER_RESPONSE(true);
  
  private final boolean isRetry;
  
  private RetryType(boolean paramBoolean)
  {
    isRetry = paramBoolean;
  }
  
  public boolean isRetry()
  {
    return isRetry;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\protocol\RetryType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */