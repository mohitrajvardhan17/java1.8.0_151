package javax.swing.colorchooser;

import java.awt.Color;
import javax.swing.event.ChangeListener;

public abstract interface ColorSelectionModel
{
  public abstract Color getSelectedColor();
  
  public abstract void setSelectedColor(Color paramColor);
  
  public abstract void addChangeListener(ChangeListener paramChangeListener);
  
  public abstract void removeChangeListener(ChangeListener paramChangeListener);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\colorchooser\ColorSelectionModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */