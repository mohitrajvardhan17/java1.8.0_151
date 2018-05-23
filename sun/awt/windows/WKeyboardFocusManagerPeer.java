package sun.awt.windows;

import java.awt.Component;
import java.awt.Window;
import java.awt.peer.ComponentPeer;
import sun.awt.CausedFocusEvent.Cause;
import sun.awt.KeyboardFocusManagerPeerImpl;

final class WKeyboardFocusManagerPeer
  extends KeyboardFocusManagerPeerImpl
{
  private static final WKeyboardFocusManagerPeer inst = new WKeyboardFocusManagerPeer();
  
  static native void setNativeFocusOwner(ComponentPeer paramComponentPeer);
  
  static native Component getNativeFocusOwner();
  
  static native Window getNativeFocusedWindow();
  
  public static WKeyboardFocusManagerPeer getInstance()
  {
    return inst;
  }
  
  private WKeyboardFocusManagerPeer() {}
  
  public void setCurrentFocusOwner(Component paramComponent)
  {
    setNativeFocusOwner(paramComponent != null ? paramComponent.getPeer() : null);
  }
  
  public Component getCurrentFocusOwner()
  {
    return getNativeFocusOwner();
  }
  
  public void setCurrentFocusedWindow(Window paramWindow)
  {
    throw new RuntimeException("not implemented");
  }
  
  public Window getCurrentFocusedWindow()
  {
    return getNativeFocusedWindow();
  }
  
  public static boolean deliverFocus(Component paramComponent1, Component paramComponent2, boolean paramBoolean1, boolean paramBoolean2, long paramLong, CausedFocusEvent.Cause paramCause)
  {
    return KeyboardFocusManagerPeerImpl.deliverFocus(paramComponent1, paramComponent2, paramBoolean1, paramBoolean2, paramLong, paramCause, getNativeFocusOwner());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WKeyboardFocusManagerPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */