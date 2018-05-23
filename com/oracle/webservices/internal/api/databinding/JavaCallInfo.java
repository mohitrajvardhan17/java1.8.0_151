package com.oracle.webservices.internal.api.databinding;

import java.lang.reflect.Method;

public abstract interface JavaCallInfo
{
  public abstract Method getMethod();
  
  public abstract Object[] getParameters();
  
  public abstract Object getReturnValue();
  
  public abstract void setReturnValue(Object paramObject);
  
  public abstract Throwable getException();
  
  public abstract void setException(Throwable paramThrowable);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\webservices\internal\api\databinding\JavaCallInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */