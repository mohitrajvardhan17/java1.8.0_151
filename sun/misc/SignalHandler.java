package sun.misc;

public abstract interface SignalHandler
{
  public static final SignalHandler SIG_DFL = new NativeSignalHandler(0L);
  public static final SignalHandler SIG_IGN = new NativeSignalHandler(1L);
  
  public abstract void handle(Signal paramSignal);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\SignalHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */