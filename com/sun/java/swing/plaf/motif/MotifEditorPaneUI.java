package com.sun.java.swing.plaf.motif;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicEditorPaneUI;
import javax.swing.text.Caret;

public class MotifEditorPaneUI
  extends BasicEditorPaneUI
{
  public MotifEditorPaneUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MotifEditorPaneUI();
  }
  
  protected Caret createCaret()
  {
    return MotifTextUI.createCaret();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\motif\MotifEditorPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */