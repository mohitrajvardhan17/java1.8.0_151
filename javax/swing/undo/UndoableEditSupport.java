package javax.swing.undo;

import java.util.Enumeration;
import java.util.Vector;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

public class UndoableEditSupport
{
  protected int updateLevel = 0;
  protected CompoundEdit compoundEdit = null;
  protected Vector<UndoableEditListener> listeners = new Vector();
  protected Object realSource = paramObject == null ? this : paramObject;
  
  public UndoableEditSupport()
  {
    this(null);
  }
  
  public UndoableEditSupport(Object paramObject) {}
  
  public synchronized void addUndoableEditListener(UndoableEditListener paramUndoableEditListener)
  {
    listeners.addElement(paramUndoableEditListener);
  }
  
  public synchronized void removeUndoableEditListener(UndoableEditListener paramUndoableEditListener)
  {
    listeners.removeElement(paramUndoableEditListener);
  }
  
  public synchronized UndoableEditListener[] getUndoableEditListeners()
  {
    return (UndoableEditListener[])listeners.toArray(new UndoableEditListener[0]);
  }
  
  protected void _postEdit(UndoableEdit paramUndoableEdit)
  {
    UndoableEditEvent localUndoableEditEvent = new UndoableEditEvent(realSource, paramUndoableEdit);
    Enumeration localEnumeration = ((Vector)listeners.clone()).elements();
    while (localEnumeration.hasMoreElements()) {
      ((UndoableEditListener)localEnumeration.nextElement()).undoableEditHappened(localUndoableEditEvent);
    }
  }
  
  public synchronized void postEdit(UndoableEdit paramUndoableEdit)
  {
    if (updateLevel == 0) {
      _postEdit(paramUndoableEdit);
    } else {
      compoundEdit.addEdit(paramUndoableEdit);
    }
  }
  
  public int getUpdateLevel()
  {
    return updateLevel;
  }
  
  public synchronized void beginUpdate()
  {
    if (updateLevel == 0) {
      compoundEdit = createCompoundEdit();
    }
    updateLevel += 1;
  }
  
  protected CompoundEdit createCompoundEdit()
  {
    return new CompoundEdit();
  }
  
  public synchronized void endUpdate()
  {
    updateLevel -= 1;
    if (updateLevel == 0)
    {
      compoundEdit.end();
      _postEdit(compoundEdit);
      compoundEdit = null;
    }
  }
  
  public String toString()
  {
    return super.toString() + " updateLevel: " + updateLevel + " listeners: " + listeners + " compoundEdit: " + compoundEdit;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\undo\UndoableEditSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */