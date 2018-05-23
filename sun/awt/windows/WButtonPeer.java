package sun.awt.windows;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.peer.ButtonPeer;

final class WButtonPeer
  extends WComponentPeer
  implements ButtonPeer
{
  public Dimension getMinimumSize()
  {
    FontMetrics localFontMetrics = getFontMetrics(((Button)target).getFont());
    String str = ((Button)target).getLabel();
    if (str == null) {
      str = "";
    }
    return new Dimension(localFontMetrics.stringWidth(str) + 14, localFontMetrics.getHeight() + 8);
  }
  
  public boolean isFocusable()
  {
    return true;
  }
  
  public native void setLabel(String paramString);
  
  WButtonPeer(Button paramButton)
  {
    super(paramButton);
  }
  
  native void create(WComponentPeer paramWComponentPeer);
  
  public void handleAction(final long paramLong, int paramInt)
  {
    WToolkit.executeOnEventHandlerThread(target, new Runnable()
    {
      public void run()
      {
        postEvent(new ActionEvent(target, 1001, ((Button)target).getActionCommand(), paramLong, val$modifiers));
      }
    }, paramLong);
  }
  
  public boolean shouldClearRectBeforePaint()
  {
    return false;
  }
  
  private static native void initIDs();
  
  public boolean handleJavaKeyEvent(KeyEvent paramKeyEvent)
  {
    switch (paramKeyEvent.getID())
    {
    case 402: 
      if (paramKeyEvent.getKeyCode() == 32) {
        handleAction(paramKeyEvent.getWhen(), paramKeyEvent.getModifiers());
      }
      break;
    }
    return false;
  }
  
  static {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WButtonPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */