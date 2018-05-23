package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;

public abstract interface Tube
{
  @NotNull
  public abstract NextAction processRequest(@NotNull Packet paramPacket);
  
  @NotNull
  public abstract NextAction processResponse(@NotNull Packet paramPacket);
  
  @NotNull
  public abstract NextAction processException(@NotNull Throwable paramThrowable);
  
  public abstract void preDestroy();
  
  public abstract Tube copy(TubeCloner paramTubeCloner);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\pipe\Tube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */