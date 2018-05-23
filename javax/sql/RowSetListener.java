package javax.sql;

import java.util.EventListener;

public abstract interface RowSetListener
  extends EventListener
{
  public abstract void rowSetChanged(RowSetEvent paramRowSetEvent);
  
  public abstract void rowChanged(RowSetEvent paramRowSetEvent);
  
  public abstract void cursorMoved(RowSetEvent paramRowSetEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\RowSetListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */