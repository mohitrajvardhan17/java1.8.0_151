package com.sun.xml.internal.ws.api.pipe.helper;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;

public abstract class AbstractFilterTubeImpl
  extends AbstractTubeImpl
{
  protected final Tube next;
  
  protected AbstractFilterTubeImpl(Tube paramTube)
  {
    next = paramTube;
  }
  
  protected AbstractFilterTubeImpl(AbstractFilterTubeImpl paramAbstractFilterTubeImpl, TubeCloner paramTubeCloner)
  {
    super(paramAbstractFilterTubeImpl, paramTubeCloner);
    if (next != null) {
      next = paramTubeCloner.copy(next);
    } else {
      next = null;
    }
  }
  
  @NotNull
  public NextAction processRequest(Packet paramPacket)
  {
    return doInvoke(next, paramPacket);
  }
  
  @NotNull
  public NextAction processResponse(Packet paramPacket)
  {
    return doReturnWith(paramPacket);
  }
  
  @NotNull
  public NextAction processException(Throwable paramThrowable)
  {
    return doThrow(paramThrowable);
  }
  
  public void preDestroy()
  {
    if (next != null) {
      next.preDestroy();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\pipe\helper\AbstractFilterTubeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */