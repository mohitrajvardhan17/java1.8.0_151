package javax.sql;

import java.util.EventObject;

public class RowSetEvent
  extends EventObject
{
  static final long serialVersionUID = -1875450876546332005L;
  
  public RowSetEvent(RowSet paramRowSet)
  {
    super(paramRowSet);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\RowSetEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */