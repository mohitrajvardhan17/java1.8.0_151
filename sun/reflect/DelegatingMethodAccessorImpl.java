package sun.reflect;

import java.lang.reflect.InvocationTargetException;

class DelegatingMethodAccessorImpl
  extends MethodAccessorImpl
{
  private MethodAccessorImpl delegate;
  
  DelegatingMethodAccessorImpl(MethodAccessorImpl paramMethodAccessorImpl)
  {
    setDelegate(paramMethodAccessorImpl);
  }
  
  public Object invoke(Object paramObject, Object[] paramArrayOfObject)
    throws IllegalArgumentException, InvocationTargetException
  {
    return delegate.invoke(paramObject, paramArrayOfObject);
  }
  
  void setDelegate(MethodAccessorImpl paramMethodAccessorImpl)
  {
    delegate = paramMethodAccessorImpl;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\DelegatingMethodAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */