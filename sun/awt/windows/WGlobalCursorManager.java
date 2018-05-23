package sun.awt.windows;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import sun.awt.GlobalCursorManager;

final class WGlobalCursorManager
  extends GlobalCursorManager
{
  private static WGlobalCursorManager manager;
  
  WGlobalCursorManager() {}
  
  public static GlobalCursorManager getCursorManager()
  {
    if (manager == null) {
      manager = new WGlobalCursorManager();
    }
    return manager;
  }
  
  public static void nativeUpdateCursor(Component paramComponent)
  {
    getCursorManager().updateCursorLater(paramComponent);
  }
  
  protected native void setCursor(Component paramComponent, Cursor paramCursor, boolean paramBoolean);
  
  protected native void getCursorPos(Point paramPoint);
  
  protected native Component findHeavyweightUnderCursor(boolean paramBoolean);
  
  protected native Point getLocationOnScreen(Component paramComponent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WGlobalCursorManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */