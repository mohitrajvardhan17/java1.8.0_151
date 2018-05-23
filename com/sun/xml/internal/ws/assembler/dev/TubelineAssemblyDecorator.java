package com.sun.xml.internal.ws.assembler.dev;

import com.sun.xml.internal.ws.api.pipe.Tube;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class TubelineAssemblyDecorator
{
  public TubelineAssemblyDecorator() {}
  
  public static TubelineAssemblyDecorator composite(Iterable<TubelineAssemblyDecorator> paramIterable)
  {
    return new CompositeTubelineAssemblyDecorator(paramIterable);
  }
  
  public Tube decorateClient(Tube paramTube, ClientTubelineAssemblyContext paramClientTubelineAssemblyContext)
  {
    return paramTube;
  }
  
  public Tube decorateClientHead(Tube paramTube, ClientTubelineAssemblyContext paramClientTubelineAssemblyContext)
  {
    return paramTube;
  }
  
  public Tube decorateClientTail(Tube paramTube, ClientTubelineAssemblyContext paramClientTubelineAssemblyContext)
  {
    return paramTube;
  }
  
  public Tube decorateServer(Tube paramTube, ServerTubelineAssemblyContext paramServerTubelineAssemblyContext)
  {
    return paramTube;
  }
  
  public Tube decorateServerTail(Tube paramTube, ServerTubelineAssemblyContext paramServerTubelineAssemblyContext)
  {
    return paramTube;
  }
  
  public Tube decorateServerHead(Tube paramTube, ServerTubelineAssemblyContext paramServerTubelineAssemblyContext)
  {
    return paramTube;
  }
  
  private static class CompositeTubelineAssemblyDecorator
    extends TubelineAssemblyDecorator
  {
    private Collection<TubelineAssemblyDecorator> decorators = new ArrayList();
    
    public CompositeTubelineAssemblyDecorator(Iterable<TubelineAssemblyDecorator> paramIterable)
    {
      Iterator localIterator = paramIterable.iterator();
      while (localIterator.hasNext())
      {
        TubelineAssemblyDecorator localTubelineAssemblyDecorator = (TubelineAssemblyDecorator)localIterator.next();
        decorators.add(localTubelineAssemblyDecorator);
      }
    }
    
    public Tube decorateClient(Tube paramTube, ClientTubelineAssemblyContext paramClientTubelineAssemblyContext)
    {
      Iterator localIterator = decorators.iterator();
      while (localIterator.hasNext())
      {
        TubelineAssemblyDecorator localTubelineAssemblyDecorator = (TubelineAssemblyDecorator)localIterator.next();
        paramTube = localTubelineAssemblyDecorator.decorateClient(paramTube, paramClientTubelineAssemblyContext);
      }
      return paramTube;
    }
    
    public Tube decorateClientHead(Tube paramTube, ClientTubelineAssemblyContext paramClientTubelineAssemblyContext)
    {
      Iterator localIterator = decorators.iterator();
      while (localIterator.hasNext())
      {
        TubelineAssemblyDecorator localTubelineAssemblyDecorator = (TubelineAssemblyDecorator)localIterator.next();
        paramTube = localTubelineAssemblyDecorator.decorateClientHead(paramTube, paramClientTubelineAssemblyContext);
      }
      return paramTube;
    }
    
    public Tube decorateClientTail(Tube paramTube, ClientTubelineAssemblyContext paramClientTubelineAssemblyContext)
    {
      Iterator localIterator = decorators.iterator();
      while (localIterator.hasNext())
      {
        TubelineAssemblyDecorator localTubelineAssemblyDecorator = (TubelineAssemblyDecorator)localIterator.next();
        paramTube = localTubelineAssemblyDecorator.decorateClientTail(paramTube, paramClientTubelineAssemblyContext);
      }
      return paramTube;
    }
    
    public Tube decorateServer(Tube paramTube, ServerTubelineAssemblyContext paramServerTubelineAssemblyContext)
    {
      Iterator localIterator = decorators.iterator();
      while (localIterator.hasNext())
      {
        TubelineAssemblyDecorator localTubelineAssemblyDecorator = (TubelineAssemblyDecorator)localIterator.next();
        paramTube = localTubelineAssemblyDecorator.decorateServer(paramTube, paramServerTubelineAssemblyContext);
      }
      return paramTube;
    }
    
    public Tube decorateServerTail(Tube paramTube, ServerTubelineAssemblyContext paramServerTubelineAssemblyContext)
    {
      Iterator localIterator = decorators.iterator();
      while (localIterator.hasNext())
      {
        TubelineAssemblyDecorator localTubelineAssemblyDecorator = (TubelineAssemblyDecorator)localIterator.next();
        paramTube = localTubelineAssemblyDecorator.decorateServerTail(paramTube, paramServerTubelineAssemblyContext);
      }
      return paramTube;
    }
    
    public Tube decorateServerHead(Tube paramTube, ServerTubelineAssemblyContext paramServerTubelineAssemblyContext)
    {
      Iterator localIterator = decorators.iterator();
      while (localIterator.hasNext())
      {
        TubelineAssemblyDecorator localTubelineAssemblyDecorator = (TubelineAssemblyDecorator)localIterator.next();
        paramTube = localTubelineAssemblyDecorator.decorateServerHead(paramTube, paramServerTubelineAssemblyContext);
      }
      return paramTube;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\assembler\dev\TubelineAssemblyDecorator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */