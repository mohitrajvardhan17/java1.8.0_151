package com.sun.xml.internal.ws.api.pipe;

import java.util.Map;

public abstract class TubeCloner
{
  public final Map<Object, Object> master2copy;
  
  public static Tube clone(Tube paramTube)
  {
    return new PipeClonerImpl().copy(paramTube);
  }
  
  TubeCloner(Map<Object, Object> paramMap)
  {
    master2copy = paramMap;
  }
  
  public abstract <T extends Tube> T copy(T paramT);
  
  public abstract void add(Tube paramTube1, Tube paramTube2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\pipe\TubeCloner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */