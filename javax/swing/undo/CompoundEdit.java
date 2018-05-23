package javax.swing.undo;

import java.util.Enumeration;
import java.util.Vector;

public class CompoundEdit
  extends AbstractUndoableEdit
{
  boolean inProgress = true;
  protected Vector<UndoableEdit> edits = new Vector();
  
  public CompoundEdit() {}
  
  public void undo()
    throws CannotUndoException
  {
    super.undo();
    int i = edits.size();
    while (i-- > 0)
    {
      UndoableEdit localUndoableEdit = (UndoableEdit)edits.elementAt(i);
      localUndoableEdit.undo();
    }
  }
  
  public void redo()
    throws CannotRedoException
  {
    super.redo();
    Enumeration localEnumeration = edits.elements();
    while (localEnumeration.hasMoreElements()) {
      ((UndoableEdit)localEnumeration.nextElement()).redo();
    }
  }
  
  protected UndoableEdit lastEdit()
  {
    int i = edits.size();
    if (i > 0) {
      return (UndoableEdit)edits.elementAt(i - 1);
    }
    return null;
  }
  
  public void die()
  {
    int i = edits.size();
    for (int j = i - 1; j >= 0; j--)
    {
      UndoableEdit localUndoableEdit = (UndoableEdit)edits.elementAt(j);
      localUndoableEdit.die();
    }
    super.die();
  }
  
  public boolean addEdit(UndoableEdit paramUndoableEdit)
  {
    if (!inProgress) {
      return false;
    }
    UndoableEdit localUndoableEdit = lastEdit();
    if (localUndoableEdit == null)
    {
      edits.addElement(paramUndoableEdit);
    }
    else if (!localUndoableEdit.addEdit(paramUndoableEdit))
    {
      if (paramUndoableEdit.replaceEdit(localUndoableEdit)) {
        edits.removeElementAt(edits.size() - 1);
      }
      edits.addElement(paramUndoableEdit);
    }
    return true;
  }
  
  public void end()
  {
    inProgress = false;
  }
  
  public boolean canUndo()
  {
    return (!isInProgress()) && (super.canUndo());
  }
  
  public boolean canRedo()
  {
    return (!isInProgress()) && (super.canRedo());
  }
  
  public boolean isInProgress()
  {
    return inProgress;
  }
  
  public boolean isSignificant()
  {
    Enumeration localEnumeration = edits.elements();
    while (localEnumeration.hasMoreElements()) {
      if (((UndoableEdit)localEnumeration.nextElement()).isSignificant()) {
        return true;
      }
    }
    return false;
  }
  
  public String getPresentationName()
  {
    UndoableEdit localUndoableEdit = lastEdit();
    if (localUndoableEdit != null) {
      return localUndoableEdit.getPresentationName();
    }
    return super.getPresentationName();
  }
  
  public String getUndoPresentationName()
  {
    UndoableEdit localUndoableEdit = lastEdit();
    if (localUndoableEdit != null) {
      return localUndoableEdit.getUndoPresentationName();
    }
    return super.getUndoPresentationName();
  }
  
  public String getRedoPresentationName()
  {
    UndoableEdit localUndoableEdit = lastEdit();
    if (localUndoableEdit != null) {
      return localUndoableEdit.getRedoPresentationName();
    }
    return super.getRedoPresentationName();
  }
  
  public String toString()
  {
    return super.toString() + " inProgress: " + inProgress + " edits: " + edits;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\undo\CompoundEdit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */