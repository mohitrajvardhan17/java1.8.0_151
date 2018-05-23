package com.sun.java.swing.plaf.motif;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPasswordFieldUI;
import javax.swing.text.Caret;

public class MotifPasswordFieldUI
  extends BasicPasswordFieldUI
{
  public MotifPasswordFieldUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MotifPasswordFieldUI();
  }
  
  protected Caret createCaret()
  {
    return MotifTextUI.createCaret();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifPasswordFieldUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */