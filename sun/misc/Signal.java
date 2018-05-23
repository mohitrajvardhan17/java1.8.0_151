package sun.misc;

import java.util.Hashtable;

public final class Signal
{
  private static Hashtable<Signal, SignalHandler> handlers = new Hashtable(4);
  private static Hashtable<Integer, Signal> signals = new Hashtable(4);
  private int number;
  private String name;
  
  public int getNumber()
  {
    return number;
  }
  
  public String getName()
  {
    return name;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (!(paramObject instanceof Signal))) {
      return false;
    }
    Signal localSignal = (Signal)paramObject;
    return (name.equals(name)) && (number == number);
  }
  
  public int hashCode()
  {
    return number;
  }
  
  public String toString()
  {
    return "SIG" + name;
  }
  
  public Signal(String paramString)
  {
    number = findSignal(paramString);
    name = paramString;
    if (number < 0) {
      throw new IllegalArgumentException("Unknown signal: " + paramString);
    }
  }
  
  public static synchronized SignalHandler handle(Signal paramSignal, SignalHandler paramSignalHandler)
    throws IllegalArgumentException
  {
    long l1 = (paramSignalHandler instanceof NativeSignalHandler) ? ((NativeSignalHandler)paramSignalHandler).getHandler() : 2L;
    long l2 = handle0(number, l1);
    if (l2 == -1L) {
      throw new IllegalArgumentException("Signal already used by VM or OS: " + paramSignal);
    }
    signals.put(Integer.valueOf(number), paramSignal);
    synchronized (handlers)
    {
      SignalHandler localSignalHandler = (SignalHandler)handlers.get(paramSignal);
      handlers.remove(paramSignal);
      if (l1 == 2L) {
        handlers.put(paramSignal, paramSignalHandler);
      }
      if (l2 == 0L) {
        return SignalHandler.SIG_DFL;
      }
      if (l2 == 1L) {
        return SignalHandler.SIG_IGN;
      }
      if (l2 == 2L) {
        return localSignalHandler;
      }
      return new NativeSignalHandler(l2);
    }
  }
  
  public static void raise(Signal paramSignal)
    throws IllegalArgumentException
  {
    if (handlers.get(paramSignal) == null) {
      throw new IllegalArgumentException("Unhandled signal: " + paramSignal);
    }
    raise0(number);
  }
  
  private static void dispatch(int paramInt)
  {
    final Signal localSignal = (Signal)signals.get(Integer.valueOf(paramInt));
    SignalHandler localSignalHandler = (SignalHandler)handlers.get(localSignal);
    Runnable local1 = new Runnable()
    {
      public void run()
      {
        val$handler.handle(localSignal);
      }
    };
    if (localSignalHandler != null) {
      new Thread(local1, localSignal + " handler").start();
    }
  }
  
  private static native int findSignal(String paramString);
  
  private static native long handle0(int paramInt, long paramLong);
  
  private static native void raise0(int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\Signal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */