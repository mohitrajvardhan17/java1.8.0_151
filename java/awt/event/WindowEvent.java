package java.awt.event;

import java.awt.Window;
import sun.awt.AppContext;
import sun.awt.SunToolkit;

public class WindowEvent
  extends ComponentEvent
{
  public static final int WINDOW_FIRST = 200;
  public static final int WINDOW_OPENED = 200;
  public static final int WINDOW_CLOSING = 201;
  public static final int WINDOW_CLOSED = 202;
  public static final int WINDOW_ICONIFIED = 203;
  public static final int WINDOW_DEICONIFIED = 204;
  public static final int WINDOW_ACTIVATED = 205;
  public static final int WINDOW_DEACTIVATED = 206;
  public static final int WINDOW_GAINED_FOCUS = 207;
  public static final int WINDOW_LOST_FOCUS = 208;
  public static final int WINDOW_STATE_CHANGED = 209;
  public static final int WINDOW_LAST = 209;
  transient Window opposite;
  int oldState;
  int newState;
  private static final long serialVersionUID = -1567959133147912127L;
  
  public WindowEvent(Window paramWindow1, int paramInt1, Window paramWindow2, int paramInt2, int paramInt3)
  {
    super(paramWindow1, paramInt1);
    opposite = paramWindow2;
    oldState = paramInt2;
    newState = paramInt3;
  }
  
  public WindowEvent(Window paramWindow1, int paramInt, Window paramWindow2)
  {
    this(paramWindow1, paramInt, paramWindow2, 0, 0);
  }
  
  public WindowEvent(Window paramWindow, int paramInt1, int paramInt2, int paramInt3)
  {
    this(paramWindow, paramInt1, null, paramInt2, paramInt3);
  }
  
  public WindowEvent(Window paramWindow, int paramInt)
  {
    this(paramWindow, paramInt, null, 0, 0);
  }
  
  public Window getWindow()
  {
    return (source instanceof Window) ? (Window)source : null;
  }
  
  public Window getOppositeWindow()
  {
    if (opposite == null) {
      return null;
    }
    return SunToolkit.targetToAppContext(opposite) == AppContext.getAppContext() ? opposite : null;
  }
  
  public int getOldState()
  {
    return oldState;
  }
  
  public int getNewState()
  {
    return newState;
  }
  
  public String paramString()
  {
    switch (id)
    {
    case 200: 
      str = "WINDOW_OPENED";
      break;
    case 201: 
      str = "WINDOW_CLOSING";
      break;
    case 202: 
      str = "WINDOW_CLOSED";
      break;
    case 203: 
      str = "WINDOW_ICONIFIED";
      break;
    case 204: 
      str = "WINDOW_DEICONIFIED";
      break;
    case 205: 
      str = "WINDOW_ACTIVATED";
      break;
    case 206: 
      str = "WINDOW_DEACTIVATED";
      break;
    case 207: 
      str = "WINDOW_GAINED_FOCUS";
      break;
    case 208: 
      str = "WINDOW_LOST_FOCUS";
      break;
    case 209: 
      str = "WINDOW_STATE_CHANGED";
      break;
    default: 
      str = "unknown type";
    }
    String str = str + ",opposite=" + getOppositeWindow() + ",oldState=" + oldState + ",newState=" + newState;
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\event\WindowEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */