package com.sun.xml.internal.ws.api.pipe.helper;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Pipe;
import com.sun.xml.internal.ws.api.pipe.PipeCloner;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;

public class PipeAdapter
  extends AbstractTubeImpl
{
  private final Pipe next;
  
  public static Tube adapt(Pipe paramPipe)
  {
    if ((paramPipe instanceof Tube)) {
      return (Tube)paramPipe;
    }
    return new PipeAdapter(paramPipe);
  }
  
  public static Pipe adapt(Tube paramTube)
  {
    if ((paramTube instanceof Pipe)) {
      return (Pipe)paramTube;
    }
    new AbstractPipeImpl()
    {
      private final Tube t;
      
      public Packet process(Packet paramAnonymousPacket)
      {
        return Fiber.current().runSync(t, paramAnonymousPacket);
      }
      
      public Pipe copy(PipeCloner paramAnonymousPipeCloner)
      {
        return new 1TubeAdapter(this, paramAnonymousPipeCloner);
      }
    };
  }
  
  private PipeAdapter(Pipe paramPipe)
  {
    next = paramPipe;
  }
  
  private PipeAdapter(PipeAdapter paramPipeAdapter, TubeCloner paramTubeCloner)
  {
    super(paramPipeAdapter, paramTubeCloner);
    next = ((PipeCloner)paramTubeCloner).copy(next);
  }
  
  @NotNull
  public NextAction processRequest(@NotNull Packet paramPacket)
  {
    return doReturnWith(next.process(paramPacket));
  }
  
  @NotNull
  public NextAction processResponse(@NotNull Packet paramPacket)
  {
    throw new IllegalStateException();
  }
  
  @NotNull
  public NextAction processException(@NotNull Throwable paramThrowable)
  {
    throw new IllegalStateException();
  }
  
  public void preDestroy()
  {
    next.preDestroy();
  }
  
  public PipeAdapter copy(TubeCloner paramTubeCloner)
  {
    return new PipeAdapter(this, paramTubeCloner);
  }
  
  public String toString()
  {
    return super.toString() + "[" + next.toString() + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\pipe\helper\PipeAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */