package java.awt.dnd.peer;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.InvalidDnDOperationException;

public abstract interface DragSourceContextPeer
{
  public abstract void startDrag(DragSourceContext paramDragSourceContext, Cursor paramCursor, Image paramImage, Point paramPoint)
    throws InvalidDnDOperationException;
  
  public abstract Cursor getCursor();
  
  public abstract void setCursor(Cursor paramCursor)
    throws InvalidDnDOperationException;
  
  public abstract void transferablesFlavorsChanged();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\dnd\peer\DragSourceContextPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */