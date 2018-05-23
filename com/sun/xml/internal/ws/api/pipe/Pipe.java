package com.sun.xml.internal.ws.api.pipe;

import com.sun.xml.internal.ws.api.message.Packet;

/**
 * @deprecated
 */
public abstract interface Pipe
{
  public abstract Packet process(Packet paramPacket);
  
  public abstract void preDestroy();
  
  public abstract Pipe copy(PipeCloner paramPipeCloner);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\pipe\Pipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */