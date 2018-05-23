package javax.swing.text;

import javax.swing.event.ChangeListener;

public abstract interface Style
  extends MutableAttributeSet
{
  public abstract String getName();
  
  public abstract void addChangeListener(ChangeListener paramChangeListener);
  
  public abstract void removeChangeListener(ChangeListener paramChangeListener);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\Style.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */