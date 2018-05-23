package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;
import com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.net.URI;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.WebServiceException;

public abstract class TransportTubeFactory
{
  private static final TransportTubeFactory DEFAULT = new DefaultTransportTubeFactory(null);
  private static final Logger logger = Logger.getLogger(TransportTubeFactory.class.getName());
  
  public TransportTubeFactory() {}
  
  public abstract Tube doCreate(@NotNull ClientTubeAssemblerContext paramClientTubeAssemblerContext);
  
  public static Tube create(@Nullable ClassLoader paramClassLoader, @NotNull ClientTubeAssemblerContext paramClientTubeAssemblerContext)
  {
    Object localObject1 = ServiceFinder.find(TransportTubeFactory.class, paramClassLoader, paramClientTubeAssemblerContext.getContainer()).iterator();
    Object localObject3;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (TransportTubeFactory)((Iterator)localObject1).next();
      localObject3 = ((TransportTubeFactory)localObject2).doCreate(paramClientTubeAssemblerContext);
      if (localObject3 != null)
      {
        if (logger.isLoggable(Level.FINE)) {
          logger.log(Level.FINE, "{0} successfully created {1}", new Object[] { localObject2.getClass(), localObject3 });
        }
        return (Tube)localObject3;
      }
    }
    localObject1 = new ClientPipeAssemblerContext(paramClientTubeAssemblerContext.getAddress(), paramClientTubeAssemblerContext.getWsdlModel(), paramClientTubeAssemblerContext.getService(), paramClientTubeAssemblerContext.getBinding(), paramClientTubeAssemblerContext.getContainer());
    ((ClientPipeAssemblerContext)localObject1).setCodec(paramClientTubeAssemblerContext.getCodec());
    Object localObject2 = ServiceFinder.find(TransportPipeFactory.class, paramClassLoader).iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (TransportPipeFactory)((Iterator)localObject2).next();
      Pipe localPipe = ((TransportPipeFactory)localObject3).doCreate((ClientPipeAssemblerContext)localObject1);
      if (localPipe != null)
      {
        if (logger.isLoggable(Level.FINE)) {
          logger.log(Level.FINE, "{0} successfully created {1}", new Object[] { localObject3.getClass(), localPipe });
        }
        return PipeAdapter.adapt(localPipe);
      }
    }
    return DEFAULT.createDefault((ClientTubeAssemblerContext)localObject1);
  }
  
  protected Tube createDefault(ClientTubeAssemblerContext paramClientTubeAssemblerContext)
  {
    String str = paramClientTubeAssemblerContext.getAddress().getURI().getScheme();
    if ((str != null) && ((str.equalsIgnoreCase("http")) || (str.equalsIgnoreCase("https")))) {
      return createHttpTransport(paramClientTubeAssemblerContext);
    }
    throw new WebServiceException("Unsupported endpoint address: " + paramClientTubeAssemblerContext.getAddress());
  }
  
  protected Tube createHttpTransport(ClientTubeAssemblerContext paramClientTubeAssemblerContext)
  {
    return new HttpTransportPipe(paramClientTubeAssemblerContext.getCodec(), paramClientTubeAssemblerContext.getBinding());
  }
  
  private static class DefaultTransportTubeFactory
    extends TransportTubeFactory
  {
    private DefaultTransportTubeFactory() {}
    
    public Tube doCreate(ClientTubeAssemblerContext paramClientTubeAssemblerContext)
    {
      return createDefault(paramClientTubeAssemblerContext);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\pipe\TransportTubeFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */