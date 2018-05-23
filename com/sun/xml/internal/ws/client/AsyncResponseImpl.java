package com.sun.xml.internal.ws.client;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.Cancelable;
import com.sun.xml.internal.ws.util.CompletedFuture;
import java.util.Map;
import java.util.concurrent.FutureTask;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;

public final class AsyncResponseImpl<T>
  extends FutureTask<T>
  implements Response<T>, ResponseContextReceiver
{
  private final AsyncHandler<T> handler;
  private ResponseContext responseContext;
  private final Runnable callable;
  private Cancelable cancelable;
  
  public AsyncResponseImpl(Runnable paramRunnable, @Nullable AsyncHandler<T> paramAsyncHandler)
  {
    super(paramRunnable, null);
    callable = paramRunnable;
    handler = paramAsyncHandler;
  }
  
  public void run()
  {
    try
    {
      callable.run();
    }
    catch (WebServiceException localWebServiceException)
    {
      set(null, localWebServiceException);
    }
    catch (Throwable localThrowable)
    {
      set(null, new WebServiceException(localThrowable));
    }
  }
  
  public ResponseContext getContext()
  {
    return responseContext;
  }
  
  public void setResponseContext(ResponseContext paramResponseContext)
  {
    responseContext = paramResponseContext;
  }
  
  public void set(T paramT, Throwable paramThrowable)
  {
    if (handler != null) {
      try
      {
        handler.handleResponse(new CompletedFuture(paramT, paramThrowable)
        {
          public Map<String, Object> getContext()
          {
            return getContext();
          }
        });
      }
      catch (Throwable localThrowable)
      {
        super.setException(localThrowable);
        return;
      }
    }
    if (paramThrowable != null) {
      super.setException(paramThrowable);
    } else {
      super.set(paramT);
    }
  }
  
  public void setCancelable(Cancelable paramCancelable)
  {
    cancelable = paramCancelable;
  }
  
  public boolean cancel(boolean paramBoolean)
  {
    if (cancelable != null) {
      cancelable.cancel(paramBoolean);
    }
    return super.cancel(paramBoolean);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\AsyncResponseImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */