package com.sun.xml.internal.ws.assembler;

import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.api.pipe.Pipe;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;
import com.sun.xml.internal.ws.assembler.dev.TubelineAssemblyContext;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

class TubelineAssemblyContextImpl
  implements TubelineAssemblyContext
{
  private static final Logger LOGGER = Logger.getLogger(TubelineAssemblyContextImpl.class);
  private Tube head;
  private Pipe adaptedHead;
  private List<Tube> tubes = new LinkedList();
  
  TubelineAssemblyContextImpl() {}
  
  public Tube getTubelineHead()
  {
    return head;
  }
  
  public Pipe getAdaptedTubelineHead()
  {
    if (adaptedHead == null) {
      adaptedHead = PipeAdapter.adapt(head);
    }
    return adaptedHead;
  }
  
  boolean setTubelineHead(Tube paramTube)
  {
    if ((paramTube == head) || (paramTube == adaptedHead)) {
      return false;
    }
    head = paramTube;
    tubes.add(head);
    adaptedHead = null;
    if (LOGGER.isLoggable(Level.FINER)) {
      LOGGER.finer(MessageFormat.format("Added '{0}' tube instance to the tubeline.", new Object[] { paramTube == null ? null : paramTube.getClass().getName() }));
    }
    return true;
  }
  
  public <T> T getImplementation(Class<T> paramClass)
  {
    Iterator localIterator = tubes.iterator();
    while (localIterator.hasNext())
    {
      Tube localTube = (Tube)localIterator.next();
      if (paramClass.isInstance(localTube)) {
        return (T)paramClass.cast(localTube);
      }
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\assembler\TubelineAssemblyContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */