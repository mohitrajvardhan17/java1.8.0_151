package sun.awt;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.InvocationEvent;

public abstract class GlobalCursorManager
{
  private final NativeUpdater nativeUpdater = new NativeUpdater();
  private long lastUpdateMillis;
  private final Object lastUpdateLock = new Object();
  
  public void updateCursorImmediately()
  {
    synchronized (nativeUpdater)
    {
      nativeUpdater.pending = false;
    }
    _updateCursor(false);
  }
  
  public void updateCursorImmediately(InputEvent paramInputEvent)
  {
    int i;
    synchronized (lastUpdateLock)
    {
      i = paramInputEvent.getWhen() >= lastUpdateMillis ? 1 : 0;
    }
    if (i != 0) {
      _updateCursor(true);
    }
  }
  
  public void updateCursorLater(Component paramComponent)
  {
    nativeUpdater.postIfNotPending(paramComponent, new InvocationEvent(Toolkit.getDefaultToolkit(), nativeUpdater));
  }
  
  protected GlobalCursorManager() {}
  
  protected abstract void setCursor(Component paramComponent, Cursor paramCursor, boolean paramBoolean);
  
  protected abstract void getCursorPos(Point paramPoint);
  
  protected abstract Point getLocationOnScreen(Component paramComponent);
  
  protected abstract Component findHeavyweightUnderCursor(boolean paramBoolean);
  
  private void _updateCursor(boolean paramBoolean)
  {
    synchronized (lastUpdateLock)
    {
      lastUpdateMillis = System.currentTimeMillis();
    }
    ??? = null;
    Point localPoint = null;
    try
    {
      Object localObject2 = findHeavyweightUnderCursor(paramBoolean);
      if (localObject2 == null)
      {
        updateCursorOutOfJava();
        return;
      }
      if ((localObject2 instanceof Window)) {
        localPoint = AWTAccessor.getComponentAccessor().getLocation((Component)localObject2);
      } else if ((localObject2 instanceof Container)) {
        localPoint = getLocationOnScreen((Component)localObject2);
      }
      if (localPoint != null)
      {
        ??? = new Point();
        getCursorPos((Point)???);
        Component localComponent = AWTAccessor.getContainerAccessor().findComponentAt((Container)localObject2, x - x, y - y, false);
        if (localComponent != null) {
          localObject2 = localComponent;
        }
      }
      setCursor((Component)localObject2, AWTAccessor.getComponentAccessor().getCursor((Component)localObject2), paramBoolean);
    }
    catch (IllegalComponentStateException localIllegalComponentStateException) {}
  }
  
  protected void updateCursorOutOfJava() {}
  
  class NativeUpdater
    implements Runnable
  {
    boolean pending = false;
    
    NativeUpdater() {}
    
    public void run()
    {
      int i = 0;
      synchronized (this)
      {
        if (pending)
        {
          pending = false;
          i = 1;
        }
      }
      if (i != 0) {
        GlobalCursorManager.this._updateCursor(false);
      }
    }
    
    public void postIfNotPending(Component paramComponent, InvocationEvent paramInvocationEvent)
    {
      int i = 0;
      synchronized (this)
      {
        if (!pending) {
          pending = (i = 1);
        }
      }
      if (i != 0) {
        SunToolkit.postEvent(SunToolkit.targetToAppContext(paramComponent), paramInvocationEvent);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\GlobalCursorManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */