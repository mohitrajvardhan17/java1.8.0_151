package com.sun.xml.internal.ws.api.server;

import com.oracle.webservices.internal.api.message.PropertySet;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.util.Pool;
import java.io.IOException;

public abstract class AbstractServerAsyncTransport<T>
{
  private final WSEndpoint endpoint;
  private final CodecPool codecPool;
  
  public AbstractServerAsyncTransport(WSEndpoint paramWSEndpoint)
  {
    endpoint = paramWSEndpoint;
    codecPool = new CodecPool(paramWSEndpoint);
  }
  
  protected Packet decodePacket(T paramT, @NotNull Codec paramCodec)
    throws IOException
  {
    Packet localPacket = new Packet();
    acceptableMimeTypes = getAcceptableMimeTypes(paramT);
    localPacket.addSatellite(getPropertySet(paramT));
    transportBackChannel = getTransportBackChannel(paramT);
    return localPacket;
  }
  
  protected abstract void encodePacket(T paramT, @NotNull Packet paramPacket, @NotNull Codec paramCodec)
    throws IOException;
  
  @Nullable
  protected abstract String getAcceptableMimeTypes(T paramT);
  
  @Nullable
  protected abstract TransportBackChannel getTransportBackChannel(T paramT);
  
  @NotNull
  protected abstract PropertySet getPropertySet(T paramT);
  
  @NotNull
  protected abstract WebServiceContextDelegate getWebServiceContextDelegate(T paramT);
  
  protected void handle(final T paramT)
    throws IOException
  {
    final Codec localCodec = (Codec)codecPool.take();
    Packet localPacket = decodePacket(paramT, localCodec);
    if (!localPacket.getMessage().isFault()) {
      endpoint.schedule(localPacket, new WSEndpoint.CompletionCallback()
      {
        public void onCompletion(@NotNull Packet paramAnonymousPacket)
        {
          try
          {
            encodePacket(paramT, paramAnonymousPacket, localCodec);
          }
          catch (IOException localIOException)
          {
            localIOException.printStackTrace();
          }
          codecPool.recycle(localCodec);
        }
      });
    }
  }
  
  private static final class CodecPool
    extends Pool<Codec>
  {
    WSEndpoint endpoint;
    
    CodecPool(WSEndpoint paramWSEndpoint)
    {
      endpoint = paramWSEndpoint;
    }
    
    protected Codec create()
    {
      return endpoint.createCodec();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\server\AbstractServerAsyncTransport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */