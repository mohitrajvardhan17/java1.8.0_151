package sun.misc;

final class NativeSignalHandler
  implements SignalHandler
{
  private final long handler;
  
  long getHandler()
  {
    return handler;
  }
  
  NativeSignalHandler(long paramLong)
  {
    handler = paramLong;
  }
  
  public void handle(Signal paramSignal)
  {
    handle0(paramSignal.getNumber(), handler);
  }
  
  private static native void handle0(int paramInt, long paramLong);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\NativeSignalHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */