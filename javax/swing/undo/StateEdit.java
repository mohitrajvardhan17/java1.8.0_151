package javax.swing.undo;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class StateEdit
  extends AbstractUndoableEdit
{
  protected static final String RCSID = "$Id: StateEdit.java,v 1.6 1997/10/01 20:05:51 sandipc Exp $";
  protected StateEditable object;
  protected Hashtable<Object, Object> preState;
  protected Hashtable<Object, Object> postState;
  protected String undoRedoName;
  
  public StateEdit(StateEditable paramStateEditable)
  {
    init(paramStateEditable, null);
  }
  
  public StateEdit(StateEditable paramStateEditable, String paramString)
  {
    init(paramStateEditable, paramString);
  }
  
  protected void init(StateEditable paramStateEditable, String paramString)
  {
    object = paramStateEditable;
    preState = new Hashtable(11);
    object.storeState(preState);
    postState = null;
    undoRedoName = paramString;
  }
  
  public void end()
  {
    postState = new Hashtable(11);
    object.storeState(postState);
    removeRedundantState();
  }
  
  public void undo()
  {
    super.undo();
    object.restoreState(preState);
  }
  
  public void redo()
  {
    super.redo();
    object.restoreState(postState);
  }
  
  public String getPresentationName()
  {
    return undoRedoName;
  }
  
  protected void removeRedundantState()
  {
    Vector localVector = new Vector();
    Enumeration localEnumeration = preState.keys();
    while (localEnumeration.hasMoreElements())
    {
      Object localObject1 = localEnumeration.nextElement();
      if ((postState.containsKey(localObject1)) && (postState.get(localObject1).equals(preState.get(localObject1)))) {
        localVector.addElement(localObject1);
      }
    }
    for (int i = localVector.size() - 1; i >= 0; i--)
    {
      Object localObject2 = localVector.elementAt(i);
      preState.remove(localObject2);
      postState.remove(localObject2);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\undo\StateEdit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */