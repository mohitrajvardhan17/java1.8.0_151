package com.sun.xml.internal.ws.client.sei;

import java.lang.reflect.Method;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;

final class PollingMethodHandler
  extends AsyncMethodHandler
{
  PollingMethodHandler(SEIStub paramSEIStub, Method paramMethod)
  {
    super(paramSEIStub, paramMethod);
  }
  
  Response<?> invoke(Object paramObject, Object[] paramArrayOfObject)
    throws WebServiceException
  {
    return doInvoke(paramObject, paramArrayOfObject, null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\sei\PollingMethodHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */