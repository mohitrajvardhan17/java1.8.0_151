package javax.swing.undo;

import java.io.Serializable;
import javax.swing.UIManager;

public class AbstractUndoableEdit
  implements UndoableEdit, Serializable
{
  protected static final String UndoName = "Undo";
  protected static final String RedoName = "Redo";
  boolean hasBeenDone = true;
  boolean alive = true;
  
  public AbstractUndoableEdit() {}
  
  public void die()
  {
    alive = false;
  }
  
  public void undo()
    throws CannotUndoException
  {
    if (!canUndo()) {
      throw new CannotUndoException();
    }
    hasBeenDone = false;
  }
  
  public boolean canUndo()
  {
    return (alive) && (hasBeenDone);
  }
  
  public void redo()
    throws CannotRedoException
  {
    if (!canRedo()) {
      throw new CannotRedoException();
    }
    hasBeenDone = true;
  }
  
  public boolean canRedo()
  {
    return (alive) && (!hasBeenDone);
  }
  
  public boolean addEdit(UndoableEdit paramUndoableEdit)
  {
    return false;
  }
  
  public boolean replaceEdit(UndoableEdit paramUndoableEdit)
  {
    return false;
  }
  
  public boolean isSignificant()
  {
    return true;
  }
  
  public String getPresentationName()
  {
    return "";
  }
  
  public String getUndoPresentationName()
  {
    String str = getPresentationName();
    if (!"".equals(str)) {
      str = UIManager.getString("AbstractUndoableEdit.undoText") + " " + str;
    } else {
      str = UIManager.getString("AbstractUndoableEdit.undoText");
    }
    return str;
  }
  
  public String getRedoPresentationName()
  {
    String str = getPresentationName();
    if (!"".equals(str)) {
      str = UIManager.getString("AbstractUndoableEdit.redoText") + " " + str;
    } else {
      str = UIManager.getString("AbstractUndoableEdit.redoText");
    }
    return str;
  }
  
  public String toString()
  {
    return super.toString() + " hasBeenDone: " + hasBeenDone + " alive: " + alive;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\undo\AbstractUndoableEdit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */