package java.awt.dnd;

import java.util.EventListener;

public abstract interface DropTargetListener
  extends EventListener
{
  public abstract void dragEnter(DropTargetDragEvent paramDropTargetDragEvent);
  
  public abstract void dragOver(DropTargetDragEvent paramDropTargetDragEvent);
  
  public abstract void dropActionChanged(DropTargetDragEvent paramDropTargetDragEvent);
  
  public abstract void dragExit(DropTargetEvent paramDropTargetEvent);
  
  public abstract void drop(DropTargetDropEvent paramDropTargetDropEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\dnd\DropTargetListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */