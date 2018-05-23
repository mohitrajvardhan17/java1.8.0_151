package com.sun.java.swing.plaf.windows;

import java.awt.Graphics;
import java.awt.Window;
import javax.swing.JWindow;

class WindowsPopupWindow
  extends JWindow
{
  static final int UNDEFINED_WINDOW_TYPE = 0;
  static final int TOOLTIP_WINDOW_TYPE = 1;
  static final int MENU_WINDOW_TYPE = 2;
  static final int SUBMENU_WINDOW_TYPE = 3;
  static final int POPUPMENU_WINDOW_TYPE = 4;
  static final int COMBOBOX_POPUP_WINDOW_TYPE = 5;
  private int windowType;
  
  WindowsPopupWindow(Window paramWindow)
  {
    super(paramWindow);
    setFocusableWindowState(false);
  }
  
  void setWindowType(int paramInt)
  {
    windowType = paramInt;
  }
  
  int getWindowType()
  {
    return windowType;
  }
  
  public void update(Graphics paramGraphics)
  {
    paint(paramGraphics);
  }
  
  public void hide()
  {
    super.hide();
    removeNotify();
  }
  
  public void show()
  {
    super.show();
    pack();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\windows\WindowsPopupWindow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */