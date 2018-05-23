package javax.swing;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

class ColorTracker
  implements ActionListener, Serializable
{
  JColorChooser chooser;
  Color color;
  
  public ColorTracker(JColorChooser paramJColorChooser)
  {
    chooser = paramJColorChooser;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    color = chooser.getColor();
  }
  
  public Color getColor()
  {
    return color;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\ColorTracker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */