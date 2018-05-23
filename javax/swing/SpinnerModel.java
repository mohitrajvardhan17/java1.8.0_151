package javax.swing;

import javax.swing.event.ChangeListener;

public abstract interface SpinnerModel
{
  public abstract Object getValue();
  
  public abstract void setValue(Object paramObject);
  
  public abstract Object getNextValue();
  
  public abstract Object getPreviousValue();
  
  public abstract void addChangeListener(ChangeListener paramChangeListener);
  
  public abstract void removeChangeListener(ChangeListener paramChangeListener);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\SpinnerModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */