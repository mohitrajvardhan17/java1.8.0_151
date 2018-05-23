package com.sun.xml.internal.ws.api.pipe.helper;

import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Pipe;
import com.sun.xml.internal.ws.api.pipe.PipeCloner;

public abstract class AbstractFilterPipeImpl
  extends AbstractPipeImpl
{
  protected final Pipe next;
  
  protected AbstractFilterPipeImpl(Pipe paramPipe)
  {
    next = paramPipe;
    assert (paramPipe != null);
  }
  
  protected AbstractFilterPipeImpl(AbstractFilterPipeImpl paramAbstractFilterPipeImpl, PipeCloner paramPipeCloner)
  {
    super(paramAbstractFilterPipeImpl, paramPipeCloner);
    next = paramPipeCloner.copy(next);
    assert (next != null);
  }
  
  public Packet process(Packet paramPacket)
  {
    return next.process(paramPacket);
  }
  
  public void preDestroy()
  {
    next.preDestroy();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\pipe\helper\AbstractFilterPipeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */