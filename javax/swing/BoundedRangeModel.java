package javax.swing;

import javax.swing.event.ChangeListener;

public abstract interface BoundedRangeModel
{
  public abstract int getMinimum();
  
  public abstract void setMinimum(int paramInt);
  
  public abstract int getMaximum();
  
  public abstract void setMaximum(int paramInt);
  
  public abstract int getValue();
  
  public abstract void setValue(int paramInt);
  
  public abstract void setValueIsAdjusting(boolean paramBoolean);
  
  public abstract boolean getValueIsAdjusting();
  
  public abstract int getExtent();
  
  public abstract void setExtent(int paramInt);
  
  public abstract void setRangeProperties(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean);
  
  public abstract void addChangeListener(ChangeListener paramChangeListener);
  
  public abstract void removeChangeListener(ChangeListener paramChangeListener);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\BoundedRangeModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */