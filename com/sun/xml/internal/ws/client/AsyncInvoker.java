package com.sun.xml.internal.ws.client;

import javax.xml.ws.WebServiceException;

public abstract class AsyncInvoker
  implements Runnable
{
  protected AsyncResponseImpl responseImpl;
  protected boolean nonNullAsyncHandlerGiven;
  
  public AsyncInvoker() {}
  
  public void setReceiver(AsyncResponseImpl paramAsyncResponseImpl)
  {
    responseImpl = paramAsyncResponseImpl;
  }
  
  public AsyncResponseImpl getResponseImpl()
  {
    return responseImpl;
  }
  
  public void setResponseImpl(AsyncResponseImpl paramAsyncResponseImpl)
  {
    responseImpl = paramAsyncResponseImpl;
  }
  
  public boolean isNonNullAsyncHandlerGiven()
  {
    return nonNullAsyncHandlerGiven;
  }
  
  public void setNonNullAsyncHandlerGiven(boolean paramBoolean)
  {
    nonNullAsyncHandlerGiven = paramBoolean;
  }
  
  public void run()
  {
    try
    {
      do_run();
    }
    catch (WebServiceException localWebServiceException)
    {
      throw localWebServiceException;
    }
    catch (Throwable localThrowable)
    {
      throw new WebServiceException(localThrowable);
    }
  }
  
  public abstract void do_run();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\AsyncInvoker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */