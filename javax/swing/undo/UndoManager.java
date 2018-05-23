package javax.swing.undo;

import java.util.Iterator;
import java.util.Vector;
import javax.swing.UIManager;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

public class UndoManager
  extends CompoundEdit
  implements UndoableEditListener
{
  int indexOfNextAdd = 0;
  int limit = 100;
  
  public UndoManager()
  {
    edits.ensureCapacity(limit);
  }
  
  public synchronized int getLimit()
  {
    return limit;
  }
  
  public synchronized void discardAllEdits()
  {
    Iterator localIterator = edits.iterator();
    while (localIterator.hasNext())
    {
      UndoableEdit localUndoableEdit = (UndoableEdit)localIterator.next();
      localUndoableEdit.die();
    }
    edits = new Vector();
    indexOfNextAdd = 0;
  }
  
  protected void trimForLimit()
  {
    if (limit >= 0)
    {
      int i = edits.size();
      if (i > limit)
      {
        int j = limit / 2;
        int k = indexOfNextAdd - 1 - j;
        int m = indexOfNextAdd - 1 + j;
        if (m - k + 1 > limit) {
          k++;
        }
        if (k < 0)
        {
          m -= k;
          k = 0;
        }
        if (m >= i)
        {
          int n = i - m - 1;
          m += n;
          k += n;
        }
        trimEdits(m + 1, i - 1);
        trimEdits(0, k - 1);
      }
    }
  }
  
  protected void trimEdits(int paramInt1, int paramInt2)
  {
    if (paramInt1 <= paramInt2)
    {
      for (int i = paramInt2; paramInt1 <= i; i--)
      {
        UndoableEdit localUndoableEdit = (UndoableEdit)edits.elementAt(i);
        localUndoableEdit.die();
        edits.removeElementAt(i);
      }
      if (indexOfNextAdd > paramInt2) {
        indexOfNextAdd -= paramInt2 - paramInt1 + 1;
      } else if (indexOfNextAdd >= paramInt1) {
        indexOfNextAdd = paramInt1;
      }
    }
  }
  
  public synchronized void setLimit(int paramInt)
  {
    if (!inProgress) {
      throw new RuntimeException("Attempt to call UndoManager.setLimit() after UndoManager.end() has been called");
    }
    limit = paramInt;
    trimForLimit();
  }
  
  protected UndoableEdit editToBeUndone()
  {
    int i = indexOfNextAdd;
    while (i > 0)
    {
      UndoableEdit localUndoableEdit = (UndoableEdit)edits.elementAt(--i);
      if (localUndoableEdit.isSignificant()) {
        return localUndoableEdit;
      }
    }
    return null;
  }
  
  protected UndoableEdit editToBeRedone()
  {
    int i = edits.size();
    int j = indexOfNextAdd;
    while (j < i)
    {
      UndoableEdit localUndoableEdit = (UndoableEdit)edits.elementAt(j++);
      if (localUndoableEdit.isSignificant()) {
        return localUndoableEdit;
      }
    }
    return null;
  }
  
  protected void undoTo(UndoableEdit paramUndoableEdit)
    throws CannotUndoException
  {
    UndoableEdit localUndoableEdit;
    for (int i = 0; i == 0; i = localUndoableEdit == paramUndoableEdit ? 1 : 0)
    {
      localUndoableEdit = (UndoableEdit)edits.elementAt(--indexOfNextAdd);
      localUndoableEdit.undo();
    }
  }
  
  protected void redoTo(UndoableEdit paramUndoableEdit)
    throws CannotRedoException
  {
    UndoableEdit localUndoableEdit;
    for (int i = 0; i == 0; i = localUndoableEdit == paramUndoableEdit ? 1 : 0)
    {
      localUndoableEdit = (UndoableEdit)edits.elementAt(indexOfNextAdd++);
      localUndoableEdit.redo();
    }
  }
  
  public synchronized void undoOrRedo()
    throws CannotRedoException, CannotUndoException
  {
    if (indexOfNextAdd == edits.size()) {
      undo();
    } else {
      redo();
    }
  }
  
  public synchronized boolean canUndoOrRedo()
  {
    if (indexOfNextAdd == edits.size()) {
      return canUndo();
    }
    return canRedo();
  }
  
  public synchronized void undo()
    throws CannotUndoException
  {
    if (inProgress)
    {
      UndoableEdit localUndoableEdit = editToBeUndone();
      if (localUndoableEdit == null) {
        throw new CannotUndoException();
      }
      undoTo(localUndoableEdit);
    }
    else
    {
      super.undo();
    }
  }
  
  public synchronized boolean canUndo()
  {
    if (inProgress)
    {
      UndoableEdit localUndoableEdit = editToBeUndone();
      return (localUndoableEdit != null) && (localUndoableEdit.canUndo());
    }
    return super.canUndo();
  }
  
  public synchronized void redo()
    throws CannotRedoException
  {
    if (inProgress)
    {
      UndoableEdit localUndoableEdit = editToBeRedone();
      if (localUndoableEdit == null) {
        throw new CannotRedoException();
      }
      redoTo(localUndoableEdit);
    }
    else
    {
      super.redo();
    }
  }
  
  public synchronized boolean canRedo()
  {
    if (inProgress)
    {
      UndoableEdit localUndoableEdit = editToBeRedone();
      return (localUndoableEdit != null) && (localUndoableEdit.canRedo());
    }
    return super.canRedo();
  }
  
  public synchronized boolean addEdit(UndoableEdit paramUndoableEdit)
  {
    trimEdits(indexOfNextAdd, edits.size() - 1);
    boolean bool = super.addEdit(paramUndoableEdit);
    if (inProgress) {
      bool = true;
    }
    indexOfNextAdd = edits.size();
    trimForLimit();
    return bool;
  }
  
  public synchronized void end()
  {
    super.end();
    trimEdits(indexOfNextAdd, edits.size() - 1);
  }
  
  public synchronized String getUndoOrRedoPresentationName()
  {
    if (indexOfNextAdd == edits.size()) {
      return getUndoPresentationName();
    }
    return getRedoPresentationName();
  }
  
  public synchronized String getUndoPresentationName()
  {
    if (inProgress)
    {
      if (canUndo()) {
        return editToBeUndone().getUndoPresentationName();
      }
      return UIManager.getString("AbstractUndoableEdit.undoText");
    }
    return super.getUndoPresentationName();
  }
  
  public synchronized String getRedoPresentationName()
  {
    if (inProgress)
    {
      if (canRedo()) {
        return editToBeRedone().getRedoPresentationName();
      }
      return UIManager.getString("AbstractUndoableEdit.redoText");
    }
    return super.getRedoPresentationName();
  }
  
  public void undoableEditHappened(UndoableEditEvent paramUndoableEditEvent)
  {
    addEdit(paramUndoableEditEvent.getEdit());
  }
  
  public String toString()
  {
    return super.toString() + " limit: " + limit + " indexOfNextAdd: " + indexOfNextAdd;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\undo\UndoManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */