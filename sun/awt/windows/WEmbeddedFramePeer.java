package sun.awt.windows;

import java.awt.Dialog;
import java.awt.Graphics;
import java.awt.Rectangle;
import sun.awt.EmbeddedFrame;
import sun.awt.Win32GraphicsEnvironment;

public class WEmbeddedFramePeer
  extends WFramePeer
{
  public WEmbeddedFramePeer(EmbeddedFrame paramEmbeddedFrame)
  {
    super(paramEmbeddedFrame);
  }
  
  native void create(WComponentPeer paramWComponentPeer);
  
  public void print(Graphics paramGraphics) {}
  
  public void updateMinimumSize() {}
  
  public void modalDisable(Dialog paramDialog, long paramLong)
  {
    super.modalDisable(paramDialog, paramLong);
    ((EmbeddedFrame)target).notifyModalBlocked(paramDialog, true);
  }
  
  public void modalEnable(Dialog paramDialog)
  {
    super.modalEnable(paramDialog);
    ((EmbeddedFrame)target).notifyModalBlocked(paramDialog, false);
  }
  
  public void setBoundsPrivate(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    setBounds(paramInt1, paramInt2, paramInt3, paramInt4, 16387);
  }
  
  public native Rectangle getBoundsPrivate();
  
  public boolean isAccelCapable()
  {
    return !Win32GraphicsEnvironment.isDWMCompositionEnabled();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WEmbeddedFramePeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */