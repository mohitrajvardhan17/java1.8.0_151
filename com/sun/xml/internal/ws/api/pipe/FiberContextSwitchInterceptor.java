package com.sun.xml.internal.ws.api.pipe;

public abstract interface FiberContextSwitchInterceptor
{
  public abstract <R, P> R execute(Fiber paramFiber, P paramP, Work<R, P> paramWork);
  
  public static abstract interface Work<R, P>
  {
    public abstract R execute(P paramP);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\pipe\FiberContextSwitchInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */