package com.sun.java.swing.plaf.windows;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPasswordFieldUI;
import javax.swing.text.Caret;

public class WindowsPasswordFieldUI
  extends BasicPasswordFieldUI
{
  public WindowsPasswordFieldUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new WindowsPasswordFieldUI();
  }
  
  protected Caret createCaret()
  {
    return new WindowsTextUI.WindowsCaret();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\windows\WindowsPasswordFieldUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */