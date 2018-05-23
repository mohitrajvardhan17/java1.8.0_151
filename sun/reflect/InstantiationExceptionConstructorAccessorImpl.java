package sun.reflect;

import java.lang.reflect.InvocationTargetException;

class InstantiationExceptionConstructorAccessorImpl
  extends ConstructorAccessorImpl
{
  private final String message;
  
  InstantiationExceptionConstructorAccessorImpl(String paramString)
  {
    message = paramString;
  }
  
  public Object newInstance(Object[] paramArrayOfObject)
    throws InstantiationException, IllegalArgumentException, InvocationTargetException
  {
    if (message == null) {
      throw new InstantiationException();
    }
    throw new InstantiationException(message);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\InstantiationExceptionConstructorAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */