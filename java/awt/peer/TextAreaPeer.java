package java.awt.peer;

import java.awt.Dimension;

public abstract interface TextAreaPeer
  extends TextComponentPeer
{
  public abstract void insert(String paramString, int paramInt);
  
  public abstract void replaceRange(String paramString, int paramInt1, int paramInt2);
  
  public abstract Dimension getPreferredSize(int paramInt1, int paramInt2);
  
  public abstract Dimension getMinimumSize(int paramInt1, int paramInt2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\peer\TextAreaPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */