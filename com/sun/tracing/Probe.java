package com.sun.tracing;

public abstract interface Probe
{
  public abstract boolean isEnabled();
  
  public abstract void trigger(Object... paramVarArgs);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\tracing\Probe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */