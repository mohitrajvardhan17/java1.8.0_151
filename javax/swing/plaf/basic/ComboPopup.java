package javax.swing.plaf.basic;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JList;

public abstract interface ComboPopup
{
  public abstract void show();
  
  public abstract void hide();
  
  public abstract boolean isVisible();
  
  public abstract JList getList();
  
  public abstract MouseListener getMouseListener();
  
  public abstract MouseMotionListener getMouseMotionListener();
  
  public abstract KeyListener getKeyListener();
  
  public abstract void uninstallingUI();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\ComboPopup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */