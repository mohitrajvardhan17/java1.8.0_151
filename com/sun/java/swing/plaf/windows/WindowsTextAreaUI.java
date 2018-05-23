package com.sun.java.swing.plaf.windows;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.Caret;

public class WindowsTextAreaUI
  extends BasicTextAreaUI
{
  public WindowsTextAreaUI() {}
  
  protected Caret createCaret()
  {
    return new WindowsTextUI.WindowsCaret();
  }
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new WindowsTextAreaUI();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\windows\WindowsTextAreaUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */