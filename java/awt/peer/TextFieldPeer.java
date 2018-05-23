package java.awt.peer;

import java.awt.Dimension;

public abstract interface TextFieldPeer
  extends TextComponentPeer
{
  public abstract void setEchoChar(char paramChar);
  
  public abstract Dimension getPreferredSize(int paramInt);
  
  public abstract Dimension getMinimumSize(int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\peer\TextFieldPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */