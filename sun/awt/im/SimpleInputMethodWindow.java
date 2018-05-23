package sun.awt.im;

import java.awt.Frame;

public class SimpleInputMethodWindow
  extends Frame
  implements InputMethodWindow
{
  InputContext inputContext = null;
  private static final long serialVersionUID = 5093376647036461555L;
  
  public SimpleInputMethodWindow(String paramString, InputContext paramInputContext)
  {
    super(paramString);
    if (paramInputContext != null) {
      inputContext = paramInputContext;
    }
    setFocusableWindowState(false);
  }
  
  public void setInputContext(InputContext paramInputContext)
  {
    inputContext = paramInputContext;
  }
  
  public java.awt.im.InputContext getInputContext()
  {
    if (inputContext != null) {
      return inputContext;
    }
    return super.getInputContext();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\im\SimpleInputMethodWindow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */