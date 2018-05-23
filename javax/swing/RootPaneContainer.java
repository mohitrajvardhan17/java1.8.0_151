package javax.swing;

import java.awt.Component;
import java.awt.Container;

public abstract interface RootPaneContainer
{
  public abstract JRootPane getRootPane();
  
  public abstract void setContentPane(Container paramContainer);
  
  public abstract Container getContentPane();
  
  public abstract void setLayeredPane(JLayeredPane paramJLayeredPane);
  
  public abstract JLayeredPane getLayeredPane();
  
  public abstract void setGlassPane(Component paramComponent);
  
  public abstract Component getGlassPane();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\RootPaneContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */