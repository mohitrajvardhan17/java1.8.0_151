package java.awt;

import java.awt.event.AdjustmentListener;

public abstract interface Adjustable
{
  public static final int HORIZONTAL = 0;
  public static final int VERTICAL = 1;
  public static final int NO_ORIENTATION = 2;
  
  public abstract int getOrientation();
  
  public abstract void setMinimum(int paramInt);
  
  public abstract int getMinimum();
  
  public abstract void setMaximum(int paramInt);
  
  public abstract int getMaximum();
  
  public abstract void setUnitIncrement(int paramInt);
  
  public abstract int getUnitIncrement();
  
  public abstract void setBlockIncrement(int paramInt);
  
  public abstract int getBlockIncrement();
  
  public abstract void setVisibleAmount(int paramInt);
  
  public abstract int getVisibleAmount();
  
  public abstract void setValue(int paramInt);
  
  public abstract int getValue();
  
  public abstract void addAdjustmentListener(AdjustmentListener paramAdjustmentListener);
  
  public abstract void removeAdjustmentListener(AdjustmentListener paramAdjustmentListener);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\Adjustable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */