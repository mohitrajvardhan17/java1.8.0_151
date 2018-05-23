package com.sun.xml.internal.ws.assembler.dev;

import com.sun.xml.internal.ws.api.pipe.Pipe;
import com.sun.xml.internal.ws.api.pipe.Tube;

public abstract interface TubelineAssemblyContext
{
  public abstract Pipe getAdaptedTubelineHead();
  
  public abstract <T> T getImplementation(Class<T> paramClass);
  
  public abstract Tube getTubelineHead();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\assembler\dev\TubelineAssemblyContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */