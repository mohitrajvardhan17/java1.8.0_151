package com.sun.xml.internal.ws.api.pipe;

import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PipeClonerImpl
  extends PipeCloner
{
  private static final Logger LOGGER = Logger.getLogger(PipeClonerImpl.class.getName());
  
  public PipeClonerImpl()
  {
    super(new HashMap());
  }
  
  protected PipeClonerImpl(Map<Object, Object> paramMap)
  {
    super(paramMap);
  }
  
  public <T extends Pipe> T copy(T paramT)
  {
    Pipe localPipe = (Pipe)master2copy.get(paramT);
    if (localPipe == null)
    {
      localPipe = paramT.copy(this);
      assert (master2copy.get(paramT) == localPipe) : ("the pipe must call the add(...) method to register itself before start copying other pipes, but " + paramT + " hasn't done so");
    }
    return localPipe;
  }
  
  public void add(Pipe paramPipe1, Pipe paramPipe2)
  {
    assert (!master2copy.containsKey(paramPipe1));
    assert ((paramPipe1 != null) && (paramPipe2 != null));
    master2copy.put(paramPipe1, paramPipe2);
  }
  
  public void add(AbstractTubeImpl paramAbstractTubeImpl1, AbstractTubeImpl paramAbstractTubeImpl2)
  {
    add(paramAbstractTubeImpl1, paramAbstractTubeImpl2);
  }
  
  public void add(Tube paramTube1, Tube paramTube2)
  {
    assert (!master2copy.containsKey(paramTube1));
    assert ((paramTube1 != null) && (paramTube2 != null));
    master2copy.put(paramTube1, paramTube2);
  }
  
  public <T extends Tube> T copy(T paramT)
  {
    Tube localTube = (Tube)master2copy.get(paramT);
    if (localTube == null) {
      if (paramT != null) {
        localTube = paramT.copy(this);
      } else if (LOGGER.isLoggable(Level.FINER)) {
        LOGGER.fine("WARNING, tube passed to 'copy' in " + this + " was null, so no copy was made");
      }
    }
    return localTube;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\pipe\PipeClonerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */