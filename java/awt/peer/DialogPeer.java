package java.awt.peer;

import java.awt.Window;
import java.util.List;

public abstract interface DialogPeer
  extends WindowPeer
{
  public abstract void setTitle(String paramString);
  
  public abstract void setResizable(boolean paramBoolean);
  
  public abstract void blockWindows(List<Window> paramList);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\peer\DialogPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */