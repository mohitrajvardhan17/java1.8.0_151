package com.sun.xml.internal.ws.api.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Engine;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.Fiber.CompletionCallback;
import com.sun.xml.internal.ws.api.pipe.TransportTubeFactory;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.binding.BindingImpl;

public class NonAnonymousResponseProcessor
{
  private static final NonAnonymousResponseProcessor DEFAULT = new NonAnonymousResponseProcessor();
  
  public static NonAnonymousResponseProcessor getDefault()
  {
    return DEFAULT;
  }
  
  protected NonAnonymousResponseProcessor() {}
  
  public Packet process(Packet paramPacket)
  {
    Fiber.CompletionCallback local1 = null;
    Fiber localFiber1 = Fiber.getCurrentIfSet();
    if (localFiber1 != null)
    {
      localObject = localFiber1.getCompletionCallback();
      if (localObject != null)
      {
        local1 = new Fiber.CompletionCallback()
        {
          public void onCompletion(@NotNull Packet paramAnonymousPacket)
          {
            localObject.onCompletion(paramAnonymousPacket);
          }
          
          public void onCompletion(@NotNull Throwable paramAnonymousThrowable)
          {
            localObject.onCompletion(paramAnonymousThrowable);
          }
        };
        localFiber1.setCompletionCallback(null);
      }
    }
    final Object localObject = endpoint;
    WSBinding localWSBinding = ((WSEndpoint)localObject).getBinding();
    Tube localTube = TransportTubeFactory.create(Thread.currentThread().getContextClassLoader(), new ClientTubeAssemblerContext(endpointAddress, ((WSEndpoint)localObject).getPort(), (WSService)null, localWSBinding, ((WSEndpoint)localObject).getContainer(), ((BindingImpl)localWSBinding).createCodec(), null, null));
    Fiber localFiber2 = ((WSEndpoint)localObject).getEngine().createFiber();
    localFiber2.start(localTube, paramPacket, local1);
    Packet localPacket = paramPacket.copy(false);
    endpointAddress = null;
    return localPacket;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\addressing\NonAnonymousResponseProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */