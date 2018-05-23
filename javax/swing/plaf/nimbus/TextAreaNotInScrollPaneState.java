package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import javax.swing.JViewport;

class TextAreaNotInScrollPaneState
  extends State
{
  TextAreaNotInScrollPaneState()
  {
    super("NotInScrollPane");
  }
  
  protected boolean isInState(JComponent paramJComponent)
  {
    return !(paramJComponent.getParent() instanceof JViewport);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\TextAreaNotInScrollPaneState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */