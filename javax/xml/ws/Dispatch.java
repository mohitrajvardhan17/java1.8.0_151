package javax.xml.ws;

import java.util.concurrent.Future;

public abstract interface Dispatch<T>
  extends BindingProvider
{
  public abstract T invoke(T paramT);
  
  public abstract Response<T> invokeAsync(T paramT);
  
  public abstract Future<?> invokeAsync(T paramT, AsyncHandler<T> paramAsyncHandler);
  
  public abstract void invokeOneWay(T paramT);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\Dispatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */