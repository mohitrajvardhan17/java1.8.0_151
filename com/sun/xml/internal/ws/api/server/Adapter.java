package com.sun.xml.internal.ws.api.server;

import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.config.management.Reconfigurable;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.util.Pool;
import java.util.Set;

public abstract class Adapter<TK extends Toolkit>
  implements Reconfigurable, Component
{
  protected final WSEndpoint<?> endpoint;
  protected volatile Pool<TK> pool = new Pool()
  {
    protected TK create()
    {
      return createToolkit();
    }
  };
  
  protected Adapter(WSEndpoint paramWSEndpoint)
  {
    assert (paramWSEndpoint != null);
    endpoint = paramWSEndpoint;
    paramWSEndpoint.getComponents().add(getEndpointComponent());
  }
  
  protected Component getEndpointComponent()
  {
    new Component()
    {
      public <S> S getSPI(Class<S> paramAnonymousClass)
      {
        if (paramAnonymousClass.isAssignableFrom(Reconfigurable.class)) {
          return (S)paramAnonymousClass.cast(Adapter.this);
        }
        return null;
      }
    };
  }
  
  public void reconfigure()
  {
    pool = new Pool()
    {
      protected TK create()
      {
        return createToolkit();
      }
    };
  }
  
  public <S> S getSPI(Class<S> paramClass)
  {
    if (paramClass.isAssignableFrom(Reconfigurable.class)) {
      return (S)paramClass.cast(this);
    }
    if (endpoint != null) {
      return (S)endpoint.getSPI(paramClass);
    }
    return null;
  }
  
  public WSEndpoint<?> getEndpoint()
  {
    return endpoint;
  }
  
  protected Pool<TK> getPool()
  {
    return pool;
  }
  
  protected abstract TK createToolkit();
  
  public class Toolkit
  {
    public final Codec codec = endpoint.createCodec();
    public final WSEndpoint.PipeHead head = endpoint.createPipeHead();
    
    public Toolkit() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\server\Adapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */