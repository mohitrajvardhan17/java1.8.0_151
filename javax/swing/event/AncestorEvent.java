package javax.swing.event;

import java.awt.AWTEvent;
import java.awt.Container;
import javax.swing.JComponent;

public class AncestorEvent
  extends AWTEvent
{
  public static final int ANCESTOR_ADDED = 1;
  public static final int ANCESTOR_REMOVED = 2;
  public static final int ANCESTOR_MOVED = 3;
  Container ancestor;
  Container ancestorParent;
  
  public AncestorEvent(JComponent paramJComponent, int paramInt, Container paramContainer1, Container paramContainer2)
  {
    super(paramJComponent, paramInt);
    ancestor = paramContainer1;
    ancestorParent = paramContainer2;
  }
  
  public Container getAncestor()
  {
    return ancestor;
  }
  
  public Container getAncestorParent()
  {
    return ancestorParent;
  }
  
  public JComponent getComponent()
  {
    return (JComponent)getSource();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\event\AncestorEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */