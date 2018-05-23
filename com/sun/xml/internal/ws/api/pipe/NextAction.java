package com.sun.xml.internal.ws.api.pipe;

import com.sun.xml.internal.ws.api.message.Packet;

public final class NextAction
{
  int kind;
  Tube next;
  Packet packet;
  Throwable throwable;
  Runnable onExitRunnable;
  static final int INVOKE = 0;
  static final int INVOKE_AND_FORGET = 1;
  static final int RETURN = 2;
  static final int THROW = 3;
  static final int SUSPEND = 4;
  static final int THROW_ABORT_RESPONSE = 5;
  static final int ABORT_RESPONSE = 6;
  static final int INVOKE_ASYNC = 7;
  
  public NextAction() {}
  
  private void set(int paramInt, Tube paramTube, Packet paramPacket, Throwable paramThrowable)
  {
    kind = paramInt;
    next = paramTube;
    packet = paramPacket;
    throwable = paramThrowable;
  }
  
  public void invoke(Tube paramTube, Packet paramPacket)
  {
    set(0, paramTube, paramPacket, null);
  }
  
  public void invokeAndForget(Tube paramTube, Packet paramPacket)
  {
    set(1, paramTube, paramPacket, null);
  }
  
  public void returnWith(Packet paramPacket)
  {
    set(2, null, paramPacket, null);
  }
  
  public void throwException(Packet paramPacket, Throwable paramThrowable)
  {
    set(2, null, paramPacket, paramThrowable);
  }
  
  public void throwException(Throwable paramThrowable)
  {
    assert (((paramThrowable instanceof RuntimeException)) || ((paramThrowable instanceof Error)));
    set(3, null, null, paramThrowable);
  }
  
  public void throwExceptionAbortResponse(Throwable paramThrowable)
  {
    set(5, null, null, paramThrowable);
  }
  
  public void abortResponse(Packet paramPacket)
  {
    set(6, null, paramPacket, null);
  }
  
  public void invokeAsync(Tube paramTube, Packet paramPacket)
  {
    set(7, paramTube, paramPacket, null);
  }
  
  /**
   * @deprecated
   */
  public void suspend()
  {
    suspend(null, null);
  }
  
  public void suspend(Runnable paramRunnable)
  {
    suspend(null, paramRunnable);
  }
  
  /**
   * @deprecated
   */
  public void suspend(Tube paramTube)
  {
    suspend(paramTube, null);
  }
  
  public void suspend(Tube paramTube, Runnable paramRunnable)
  {
    set(4, paramTube, null, null);
    onExitRunnable = paramRunnable;
  }
  
  public Tube getNext()
  {
    return next;
  }
  
  public void setNext(Tube paramTube)
  {
    next = paramTube;
  }
  
  public Packet getPacket()
  {
    return packet;
  }
  
  public Throwable getThrowable()
  {
    return throwable;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(super.toString()).append(" [");
    localStringBuilder.append("kind=").append(getKindString()).append(',');
    localStringBuilder.append("next=").append(next).append(',');
    localStringBuilder.append("packet=").append(packet != null ? packet.toShortString() : null).append(',');
    localStringBuilder.append("throwable=").append(throwable).append(']');
    return localStringBuilder.toString();
  }
  
  public String getKindString()
  {
    switch (kind)
    {
    case 0: 
      return "INVOKE";
    case 1: 
      return "INVOKE_AND_FORGET";
    case 2: 
      return "RETURN";
    case 3: 
      return "THROW";
    case 4: 
      return "SUSPEND";
    case 5: 
      return "THROW_ABORT_RESPONSE";
    case 6: 
      return "ABORT_RESPONSE";
    case 7: 
      return "INVOKE_ASYNC";
    }
    throw new AssertionError(kind);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\pipe\NextAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */