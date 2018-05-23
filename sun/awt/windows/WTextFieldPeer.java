package sun.awt.windows;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.im.InputMethodRequests;
import java.awt.peer.TextFieldPeer;

final class WTextFieldPeer
  extends WTextComponentPeer
  implements TextFieldPeer
{
  public Dimension getMinimumSize()
  {
    FontMetrics localFontMetrics = getFontMetrics(((TextField)target).getFont());
    return new Dimension(localFontMetrics.stringWidth(getText()) + 24, localFontMetrics.getHeight() + 8);
  }
  
  public boolean handleJavaKeyEvent(KeyEvent paramKeyEvent)
  {
    switch (paramKeyEvent.getID())
    {
    case 400: 
      if ((paramKeyEvent.getKeyChar() == '\n') && (!paramKeyEvent.isAltDown()) && (!paramKeyEvent.isControlDown()))
      {
        postEvent(new ActionEvent(target, 1001, getText(), paramKeyEvent.getWhen(), paramKeyEvent.getModifiers()));
        return true;
      }
      break;
    }
    return false;
  }
  
  public native void setEchoChar(char paramChar);
  
  public Dimension getPreferredSize(int paramInt)
  {
    return getMinimumSize(paramInt);
  }
  
  public Dimension getMinimumSize(int paramInt)
  {
    FontMetrics localFontMetrics = getFontMetrics(((TextField)target).getFont());
    return new Dimension(localFontMetrics.charWidth('0') * paramInt + 24, localFontMetrics.getHeight() + 8);
  }
  
  public InputMethodRequests getInputMethodRequests()
  {
    return null;
  }
  
  WTextFieldPeer(TextField paramTextField)
  {
    super(paramTextField);
  }
  
  native void create(WComponentPeer paramWComponentPeer);
  
  void initialize()
  {
    TextField localTextField = (TextField)target;
    if (localTextField.echoCharIsSet()) {
      setEchoChar(localTextField.getEchoChar());
    }
    super.initialize();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WTextFieldPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */