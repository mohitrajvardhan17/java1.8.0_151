package com.sun.xml.internal.ws.transport;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.TransportTubeFactory;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.developer.HttpConfigFeature;
import javax.xml.ws.WebServiceFeature;

public final class DeferredTransportPipe
  extends AbstractTubeImpl
{
  private Tube transport;
  private EndpointAddress address;
  private final ClassLoader classLoader;
  private final ClientTubeAssemblerContext context;
  
  public DeferredTransportPipe(ClassLoader paramClassLoader, ClientTubeAssemblerContext paramClientTubeAssemblerContext)
  {
    classLoader = paramClassLoader;
    context = paramClientTubeAssemblerContext;
    if (paramClientTubeAssemblerContext.getBinding().getFeature(HttpConfigFeature.class) == null) {
      paramClientTubeAssemblerContext.getBinding().getFeatures().mergeFeatures(new WebServiceFeature[] { new HttpConfigFeature() }, false);
    }
    try
    {
      transport = TransportTubeFactory.create(paramClassLoader, paramClientTubeAssemblerContext);
      address = paramClientTubeAssemblerContext.getAddress();
    }
    catch (Exception localException) {}
  }
  
  public DeferredTransportPipe(DeferredTransportPipe paramDeferredTransportPipe, TubeCloner paramTubeCloner)
  {
    super(paramDeferredTransportPipe, paramTubeCloner);
    classLoader = classLoader;
    context = context;
    if (transport != null)
    {
      transport = paramTubeCloner.copy(transport);
      address = address;
    }
  }
  
  public NextAction processException(@NotNull Throwable paramThrowable)
  {
    return transport.processException(paramThrowable);
  }
  
  public NextAction processRequest(@NotNull Packet paramPacket)
  {
    if (endpointAddress == address) {
      return transport.processRequest(paramPacket);
    }
    if (transport != null)
    {
      transport.preDestroy();
      transport = null;
      address = null;
    }
    ClientTubeAssemblerContext localClientTubeAssemblerContext = new ClientTubeAssemblerContext(endpointAddress, context.getWsdlModel(), context.getBindingProvider(), context.getBinding(), context.getContainer(), context.getCodec().copy(), context.getSEIModel(), context.getSEI());
    address = endpointAddress;
    transport = TransportTubeFactory.create(classLoader, localClientTubeAssemblerContext);
    assert (transport != null);
    return transport.processRequest(paramPacket);
  }
  
  public NextAction processResponse(@NotNull Packet paramPacket)
  {
    if (transport != null) {
      return transport.processResponse(paramPacket);
    }
    return doReturnWith(paramPacket);
  }
  
  public void preDestroy()
  {
    if (transport != null)
    {
      transport.preDestroy();
      transport = null;
      address = null;
    }
  }
  
  public DeferredTransportPipe copy(TubeCloner paramTubeCloner)
  {
    return new DeferredTransportPipe(this, paramTubeCloner);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\transport\DeferredTransportPipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */