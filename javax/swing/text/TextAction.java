package javax.swing.text;

import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.AbstractAction;
import javax.swing.Action;

public abstract class TextAction
  extends AbstractAction
{
  public TextAction(String paramString)
  {
    super(paramString);
  }
  
  protected final JTextComponent getTextComponent(ActionEvent paramActionEvent)
  {
    if (paramActionEvent != null)
    {
      Object localObject = paramActionEvent.getSource();
      if ((localObject instanceof JTextComponent)) {
        return (JTextComponent)localObject;
      }
    }
    return getFocusedComponent();
  }
  
  public static final Action[] augmentList(Action[] paramArrayOfAction1, Action[] paramArrayOfAction2)
  {
    Hashtable localHashtable = new Hashtable();
    Action localAction;
    String str;
    for (localAction : paramArrayOfAction1)
    {
      str = (String)localAction.getValue("Name");
      localHashtable.put(str != null ? str : "", localAction);
    }
    for (localAction : paramArrayOfAction2)
    {
      str = (String)localAction.getValue("Name");
      localHashtable.put(str != null ? str : "", localAction);
    }
    ??? = new Action[localHashtable.size()];
    ??? = 0;
    Enumeration localEnumeration = localHashtable.elements();
    while (localEnumeration.hasMoreElements()) {
      ???[(???++)] = ((Action)localEnumeration.nextElement());
    }
    return (Action[])???;
  }
  
  protected final JTextComponent getFocusedComponent()
  {
    return JTextComponent.getFocusedComponent();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\TextAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */