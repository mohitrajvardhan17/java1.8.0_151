package com.sun.java.swing.plaf.motif;

import javax.swing.AbstractButton;
import javax.swing.plaf.basic.BasicButtonListener;

public class MotifButtonListener
  extends BasicButtonListener
{
  public MotifButtonListener(AbstractButton paramAbstractButton)
  {
    super(paramAbstractButton);
  }
  
  protected void checkOpacity(AbstractButton paramAbstractButton)
  {
    paramAbstractButton.setOpaque(false);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */