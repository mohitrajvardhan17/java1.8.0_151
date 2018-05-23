package com.sun.xml.internal.ws.client.sei;

import java.lang.reflect.Method;
import javax.xml.ws.WebServiceException;

public abstract class MethodHandler
{
  protected final SEIStub owner;
  protected Method method;
  
  protected MethodHandler(SEIStub paramSEIStub, Method paramMethod)
  {
    owner = paramSEIStub;
    method = paramMethod;
  }
  
  abstract Object invoke(Object paramObject, Object[] paramArrayOfObject)
    throws WebServiceException, Throwable;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\sei\MethodHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */