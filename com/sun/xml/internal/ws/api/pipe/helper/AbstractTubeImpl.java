package com.sun.xml.internal.ws.api.pipe.helper;

import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Pipe;
import com.sun.xml.internal.ws.api.pipe.PipeCloner;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;

public abstract class AbstractTubeImpl
  implements Tube, Pipe
{
  protected AbstractTubeImpl() {}
  
  protected AbstractTubeImpl(AbstractTubeImpl paramAbstractTubeImpl, TubeCloner paramTubeCloner)
  {
    paramTubeCloner.add(paramAbstractTubeImpl, this);
  }
  
  protected final NextAction doInvoke(Tube paramTube, Packet paramPacket)
  {
    NextAction localNextAction = new NextAction();
    localNextAction.invoke(paramTube, paramPacket);
    return localNextAction;
  }
  
  protected final NextAction doInvokeAndForget(Tube paramTube, Packet paramPacket)
  {
    NextAction localNextAction = new NextAction();
    localNextAction.invokeAndForget(paramTube, paramPacket);
    return localNextAction;
  }
  
  protected final NextAction doReturnWith(Packet paramPacket)
  {
    NextAction localNextAction = new NextAction();
    localNextAction.returnWith(paramPacket);
    return localNextAction;
  }
  
  protected final NextAction doThrow(Packet paramPacket, Throwable paramThrowable)
  {
    NextAction localNextAction = new NextAction();
    localNextAction.throwException(paramPacket, paramThrowable);
    return localNextAction;
  }
  
  @Deprecated
  protected final NextAction doSuspend()
  {
    NextAction localNextAction = new NextAction();
    localNextAction.suspend();
    return localNextAction;
  }
  
  protected final NextAction doSuspend(Runnable paramRunnable)
  {
    NextAction localNextAction = new NextAction();
    localNextAction.suspend(paramRunnable);
    return localNextAction;
  }
  
  @Deprecated
  protected final NextAction doSuspend(Tube paramTube)
  {
    NextAction localNextAction = new NextAction();
    localNextAction.suspend(paramTube);
    return localNextAction;
  }
  
  protected final NextAction doSuspend(Tube paramTube, Runnable paramRunnable)
  {
    NextAction localNextAction = new NextAction();
    localNextAction.suspend(paramTube, paramRunnable);
    return localNextAction;
  }
  
  protected final NextAction doThrow(Throwable paramThrowable)
  {
    NextAction localNextAction = new NextAction();
    localNextAction.throwException(paramThrowable);
    return localNextAction;
  }
  
  public Packet process(Packet paramPacket)
  {
    return Fiber.current().runSync(this, paramPacket);
  }
  
  public final AbstractTubeImpl copy(PipeCloner paramPipeCloner)
  {
    return copy(paramPipeCloner);
  }
  
  public abstract AbstractTubeImpl copy(TubeCloner paramTubeCloner);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\pipe\helper\AbstractTubeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */