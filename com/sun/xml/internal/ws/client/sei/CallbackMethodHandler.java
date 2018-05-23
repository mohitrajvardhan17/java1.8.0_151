package com.sun.xml.internal.ws.client.sei;

import java.lang.reflect.Method;
import java.util.concurrent.Future;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.WebServiceException;

final class CallbackMethodHandler
  extends AsyncMethodHandler
{
  private final int handlerPos;
  
  CallbackMethodHandler(SEIStub paramSEIStub, Method paramMethod, int paramInt)
  {
    super(paramSEIStub, paramMethod);
    handlerPos = paramInt;
  }
  
  Future<?> invoke(Object paramObject, Object[] paramArrayOfObject)
    throws WebServiceException
  {
    AsyncHandler localAsyncHandler = (AsyncHandler)paramArrayOfObject[handlerPos];
    return doInvoke(paramObject, paramArrayOfObject, localAsyncHandler);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\sei\CallbackMethodHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */