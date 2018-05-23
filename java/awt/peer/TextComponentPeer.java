package java.awt.peer;

import java.awt.im.InputMethodRequests;

public abstract interface TextComponentPeer
  extends ComponentPeer
{
  public abstract void setEditable(boolean paramBoolean);
  
  public abstract String getText();
  
  public abstract void setText(String paramString);
  
  public abstract int getSelectionStart();
  
  public abstract int getSelectionEnd();
  
  public abstract void select(int paramInt1, int paramInt2);
  
  public abstract void setCaretPosition(int paramInt);
  
  public abstract int getCaretPosition();
  
  public abstract InputMethodRequests getInputMethodRequests();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\peer\TextComponentPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */