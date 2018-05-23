package sun.net;

import java.security.AccessController;
import java.security.PrivilegedAction;

public final class PortConfig
{
  private static int defaultUpper;
  private static int defaultLower;
  private static final int upper;
  private static final int lower;
  
  public PortConfig() {}
  
  static native int getLower0();
  
  static native int getUpper0();
  
  public static int getLower()
  {
    return lower;
  }
  
  public static int getUpper()
  {
    return upper;
  }
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        System.loadLibrary("net");
        return null;
      }
    });
    int i = getLower0();
    if (i == -1) {
      i = defaultLower;
    }
    lower = i;
    i = getUpper0();
    if (i == -1) {
      i = defaultUpper;
    }
    upper = i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\PortConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */