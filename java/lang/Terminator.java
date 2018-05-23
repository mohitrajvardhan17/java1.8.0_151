package java.lang;

import sun.misc.Signal;
import sun.misc.SignalHandler;

class Terminator
{
  private static SignalHandler handler = null;
  
  Terminator() {}
  
  static void setup()
  {
    if (handler != null) {
      return;
    }
    SignalHandler local1 = new SignalHandler()
    {
      public void handle(Signal paramAnonymousSignal)
      {
        Shutdown.exit(paramAnonymousSignal.getNumber() + 128);
      }
    };
    handler = local1;
    try
    {
      Signal.handle(new Signal("INT"), local1);
    }
    catch (IllegalArgumentException localIllegalArgumentException1) {}
    try
    {
      Signal.handle(new Signal("TERM"), local1);
    }
    catch (IllegalArgumentException localIllegalArgumentException2) {}
  }
  
  static void teardown() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\Terminator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */