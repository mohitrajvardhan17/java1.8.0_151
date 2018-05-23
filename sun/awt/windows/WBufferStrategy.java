package sun.awt.windows;

import java.awt.Component;
import java.awt.Image;

public final class WBufferStrategy
{
  public WBufferStrategy() {}
  
  private static native void initIDs(Class<?> paramClass);
  
  public static native Image getDrawBuffer(Component paramComponent);
  
  static
  {
    initIDs(Component.class);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WBufferStrategy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */