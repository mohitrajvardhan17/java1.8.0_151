package com.sun.beans.finder;

final class SignatureException
  extends RuntimeException
{
  SignatureException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  NoSuchMethodException toNoSuchMethodException(String paramString)
  {
    Throwable localThrowable = getCause();
    if ((localThrowable instanceof NoSuchMethodException)) {
      return (NoSuchMethodException)localThrowable;
    }
    NoSuchMethodException localNoSuchMethodException = new NoSuchMethodException(paramString);
    localNoSuchMethodException.initCause(localThrowable);
    return localNoSuchMethodException;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\finder\SignatureException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */