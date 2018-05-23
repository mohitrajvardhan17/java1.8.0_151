package com.sun.xml.internal.ws.api.pipe;

import java.util.Map;

/**
 * @deprecated
 */
public abstract class PipeCloner
  extends TubeCloner
{
  public static Pipe clone(Pipe paramPipe)
  {
    return new PipeClonerImpl().copy(paramPipe);
  }
  
  PipeCloner(Map<Object, Object> paramMap)
  {
    super(paramMap);
  }
  
  public abstract <T extends Pipe> T copy(T paramT);
  
  public abstract void add(Pipe paramPipe1, Pipe paramPipe2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\pipe\PipeCloner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */