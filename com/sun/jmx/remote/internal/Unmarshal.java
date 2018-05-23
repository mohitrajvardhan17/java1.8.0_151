package com.sun.jmx.remote.internal;

import java.io.IOException;
import java.rmi.MarshalledObject;

public abstract interface Unmarshal
{
  public abstract Object get(MarshalledObject<?> paramMarshalledObject)
    throws IOException, ClassNotFoundException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\remote\internal\Unmarshal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */