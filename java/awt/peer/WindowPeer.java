package java.awt.peer;

import java.awt.Dialog;

public abstract interface WindowPeer
  extends ContainerPeer
{
  public abstract void toFront();
  
  public abstract void toBack();
  
  public abstract void updateAlwaysOnTopState();
  
  public abstract void updateFocusableWindowState();
  
  public abstract void setModalBlocked(Dialog paramDialog, boolean paramBoolean);
  
  public abstract void updateMinimumSize();
  
  public abstract void updateIconImages();
  
  public abstract void setOpacity(float paramFloat);
  
  public abstract void setOpaque(boolean paramBoolean);
  
  public abstract void updateWindow();
  
  public abstract void repositionSecurityWarning();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\peer\WindowPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */