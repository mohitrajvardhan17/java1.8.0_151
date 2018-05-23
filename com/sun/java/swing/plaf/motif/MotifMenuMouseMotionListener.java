package com.sun.java.swing.plaf.motif;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.MenuSelectionManager;

class MotifMenuMouseMotionListener
  implements MouseMotionListener
{
  MotifMenuMouseMotionListener() {}
  
  public void mouseDragged(MouseEvent paramMouseEvent)
  {
    MenuSelectionManager.defaultManager().processMouseEvent(paramMouseEvent);
  }
  
  public void mouseMoved(MouseEvent paramMouseEvent)
  {
    MenuSelectionManager.defaultManager().processMouseEvent(paramMouseEvent);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifMenuMouseMotionListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */