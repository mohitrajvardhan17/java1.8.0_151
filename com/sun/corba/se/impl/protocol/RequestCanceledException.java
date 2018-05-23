package com.sun.corba.se.impl.protocol;

public class RequestCanceledException
  extends RuntimeException
{
  private int requestId = 0;
  
  public RequestCanceledException(int paramInt)
  {
    requestId = paramInt;
  }
  
  public int getRequestId()
  {
    return requestId;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\RequestCanceledException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */