package javax.swing.undo;

public abstract interface UndoableEdit
{
  public abstract void undo()
    throws CannotUndoException;
  
  public abstract boolean canUndo();
  
  public abstract void redo()
    throws CannotRedoException;
  
  public abstract boolean canRedo();
  
  public abstract void die();
  
  public abstract boolean addEdit(UndoableEdit paramUndoableEdit);
  
  public abstract boolean replaceEdit(UndoableEdit paramUndoableEdit);
  
  public abstract boolean isSignificant();
  
  public abstract String getPresentationName();
  
  public abstract String getUndoPresentationName();
  
  public abstract String getRedoPresentationName();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\undo\UndoableEdit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */