package com.sun.java.swing.plaf.motif;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.MenuSelectionManager;

class MotifMenuMouseListener
  extends MouseAdapter
{
  MotifMenuMouseListener() {}
  
  public void mousePressed(MouseEvent paramMouseEvent)
  {
    MenuSelectionManager.defaultManager().processMouseEvent(paramMouseEvent);
  }
  
  public void mouseReleased(MouseEvent paramMouseEvent)
  {
    MenuSelectionManager.defaultManager().processMouseEvent(paramMouseEvent);
  }
  
  public void mouseEntered(MouseEvent paramMouseEvent)
  {
    MenuSelectionManager.defaultManager().processMouseEvent(paramMouseEvent);
  }
  
  public void mouseExited(MouseEvent paramMouseEvent)
  {
    MenuSelectionManager.defaultManager().processMouseEvent(paramMouseEvent);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifMenuMouseListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */