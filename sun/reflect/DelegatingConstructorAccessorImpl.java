package sun.reflect;

import java.lang.reflect.InvocationTargetException;

class DelegatingConstructorAccessorImpl
  extends ConstructorAccessorImpl
{
  private ConstructorAccessorImpl delegate;
  
  DelegatingConstructorAccessorImpl(ConstructorAccessorImpl paramConstructorAccessorImpl)
  {
    setDelegate(paramConstructorAccessorImpl);
  }
  
  public Object newInstance(Object[] paramArrayOfObject)
    throws InstantiationException, IllegalArgumentException, InvocationTargetException
  {
    return delegate.newInstance(paramArrayOfObject);
  }
  
  void setDelegate(ConstructorAccessorImpl paramConstructorAccessorImpl)
  {
    delegate = paramConstructorAccessorImpl;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\DelegatingConstructorAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */