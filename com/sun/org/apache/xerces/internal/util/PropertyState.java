package com.sun.org.apache.xerces.internal.util;

public class PropertyState
{
  public final Status status;
  public final Object state;
  public static final PropertyState UNKNOWN = new PropertyState(Status.UNKNOWN, null);
  public static final PropertyState RECOGNIZED = new PropertyState(Status.RECOGNIZED, null);
  public static final PropertyState NOT_SUPPORTED = new PropertyState(Status.NOT_SUPPORTED, null);
  public static final PropertyState NOT_RECOGNIZED = new PropertyState(Status.NOT_RECOGNIZED, null);
  public static final PropertyState NOT_ALLOWED = new PropertyState(Status.NOT_ALLOWED, null);
  
  public PropertyState(Status paramStatus, Object paramObject)
  {
    status = paramStatus;
    state = paramObject;
  }
  
  public static PropertyState of(Status paramStatus)
  {
    return new PropertyState(paramStatus, null);
  }
  
  public static PropertyState is(Object paramObject)
  {
    return new PropertyState(Status.SET, paramObject);
  }
  
  public boolean isExceptional()
  {
    return status.isExceptional();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\PropertyState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */