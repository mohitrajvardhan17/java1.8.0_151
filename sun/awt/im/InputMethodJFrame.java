package sun.awt.im;

import javax.swing.JFrame;
import javax.swing.JRootPane;

public class InputMethodJFrame
  extends JFrame
  implements InputMethodWindow
{
  InputContext inputContext = null;
  private static final long serialVersionUID = -4705856747771842549L;
  
  public InputMethodJFrame(String paramString, InputContext paramInputContext)
  {
    super(paramString);
    if (JFrame.isDefaultLookAndFeelDecorated())
    {
      setUndecorated(true);
      getRootPane().setWindowDecorationStyle(0);
    }
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\im\InputMethodJFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */