package com.sun.org.apache.xerces.internal.utils;

public final class ConfigurationError
  extends Error
{
  private Exception exception;
  
  ConfigurationError(String paramString, Exception paramException)
  {
    super(paramString);
    exception = paramException;
  }
  
  public Exception getException()
  {
    return exception;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\utils\ConfigurationError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */