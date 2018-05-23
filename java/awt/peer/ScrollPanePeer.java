package java.awt.peer;

import java.awt.Adjustable;

public abstract interface ScrollPanePeer
  extends ContainerPeer
{
  public abstract int getHScrollbarHeight();
  
  public abstract int getVScrollbarWidth();
  
  public abstract void setScrollPosition(int paramInt1, int paramInt2);
  
  public abstract void childResized(int paramInt1, int paramInt2);
  
  public abstract void setUnitIncrement(Adjustable paramAdjustable, int paramInt);
  
  public abstract void setValue(Adjustable paramAdjustable, int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\peer\ScrollPanePeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */