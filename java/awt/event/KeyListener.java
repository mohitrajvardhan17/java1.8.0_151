package java.awt.event;

import java.util.EventListener;

public abstract interface KeyListener
  extends EventListener
{
  public abstract void keyTyped(KeyEvent paramKeyEvent);
  
  public abstract void keyPressed(KeyEvent paramKeyEvent);
  
  public abstract void keyReleased(KeyEvent paramKeyEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\event\KeyListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */