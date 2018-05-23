package com.sun.tracing;

import java.lang.reflect.Method;

public abstract interface Provider
{
  public abstract Probe getProbe(Method paramMethod);
  
  public abstract void dispose();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\tracing\Provider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */