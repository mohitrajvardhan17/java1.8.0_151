package com.sun.xml.internal.ws.util;

import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public abstract class Pool<T>
{
  private volatile WeakReference<ConcurrentLinkedQueue<T>> queue;
  
  public Pool() {}
  
  public final T take()
  {
    Object localObject = getQueue().poll();
    if (localObject == null) {
      return (T)create();
    }
    return (T)localObject;
  }
  
  private ConcurrentLinkedQueue<T> getQueue()
  {
    WeakReference localWeakReference = queue;
    if (localWeakReference != null)
    {
      localConcurrentLinkedQueue = (ConcurrentLinkedQueue)localWeakReference.get();
      if (localConcurrentLinkedQueue != null) {
        return localConcurrentLinkedQueue;
      }
    }
    ConcurrentLinkedQueue localConcurrentLinkedQueue = new ConcurrentLinkedQueue();
    queue = new WeakReference(localConcurrentLinkedQueue);
    return localConcurrentLinkedQueue;
  }
  
  public final void recycle(T paramT)
  {
    getQueue().offer(paramT);
  }
  
  protected abstract T create();
  
  public static final class Marshaller
    extends Pool<Marshaller>
  {
    private final JAXBContext context;
    
    public Marshaller(JAXBContext paramJAXBContext)
    {
      context = paramJAXBContext;
    }
    
    protected Marshaller create()
    {
      try
      {
        return context.createMarshaller();
      }
      catch (JAXBException localJAXBException)
      {
        throw new AssertionError(localJAXBException);
      }
    }
  }
  
  public static final class TubePool
    extends Pool<Tube>
  {
    private final Tube master;
    
    public TubePool(Tube paramTube)
    {
      master = paramTube;
      recycle(paramTube);
    }
    
    protected Tube create()
    {
      return TubeCloner.clone(master);
    }
    
    @Deprecated
    public final Tube takeMaster()
    {
      return master;
    }
  }
  
  public static final class Unmarshaller
    extends Pool<Unmarshaller>
  {
    private final JAXBContext context;
    
    public Unmarshaller(JAXBContext paramJAXBContext)
    {
      context = paramJAXBContext;
    }
    
    protected Unmarshaller create()
    {
      try
      {
        return context.createUnmarshaller();
      }
      catch (JAXBException localJAXBException)
      {
        throw new AssertionError(localJAXBException);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\Pool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */