package sun.awt.windows;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.dnd.DropTarget;
import java.awt.event.ComponentEvent;
import sun.awt.LightweightFrame;
import sun.swing.JLightweightFrame;
import sun.swing.SwingAccessor;
import sun.swing.SwingAccessor.JLightweightFrameAccessor;

public class WLightweightFramePeer
  extends WFramePeer
{
  public WLightweightFramePeer(LightweightFrame paramLightweightFrame)
  {
    super(paramLightweightFrame);
  }
  
  private LightweightFrame getLwTarget()
  {
    return (LightweightFrame)target;
  }
  
  public Graphics getGraphics()
  {
    return getLwTarget().getGraphics();
  }
  
  public void show()
  {
    super.show();
    postEvent(new ComponentEvent((Component)getTarget(), 102));
  }
  
  public void hide()
  {
    super.hide();
    postEvent(new ComponentEvent((Component)getTarget(), 103));
  }
  
  public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.reshape(paramInt1, paramInt2, paramInt3, paramInt4);
    postEvent(new ComponentEvent((Component)getTarget(), 100));
    postEvent(new ComponentEvent((Component)getTarget(), 101));
  }
  
  public void handleEvent(AWTEvent paramAWTEvent)
  {
    if (paramAWTEvent.getID() == 501) {
      emulateActivation(true);
    }
    super.handleEvent(paramAWTEvent);
  }
  
  public void grab()
  {
    getLwTarget().grabFocus();
  }
  
  public void ungrab()
  {
    getLwTarget().ungrabFocus();
  }
  
  public void updateCursorImmediately()
  {
    SwingAccessor.getJLightweightFrameAccessor().updateCursor((JLightweightFrame)getLwTarget());
  }
  
  public boolean isLightweightFramePeer()
  {
    return true;
  }
  
  public void addDropTarget(DropTarget paramDropTarget)
  {
    getLwTarget().addDropTarget(paramDropTarget);
  }
  
  public void removeDropTarget(DropTarget paramDropTarget)
  {
    getLwTarget().removeDropTarget(paramDropTarget);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WLightweightFramePeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */