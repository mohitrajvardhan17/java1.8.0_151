package java.awt.im.spi;

import java.awt.Window;
import java.awt.font.TextHitInfo;
import java.awt.im.InputMethodRequests;
import java.text.AttributedCharacterIterator;
import javax.swing.JFrame;

public abstract interface InputMethodContext
  extends InputMethodRequests
{
  public abstract void dispatchInputMethodEvent(int paramInt1, AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt2, TextHitInfo paramTextHitInfo1, TextHitInfo paramTextHitInfo2);
  
  public abstract Window createInputMethodWindow(String paramString, boolean paramBoolean);
  
  public abstract JFrame createInputMethodJFrame(String paramString, boolean paramBoolean);
  
  public abstract void enableClientWindowNotification(InputMethod paramInputMethod, boolean paramBoolean);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\im\spi\InputMethodContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */