package javax.swing.event;

import java.util.EventObject;

public class ListDataEvent
  extends EventObject
{
  public static final int CONTENTS_CHANGED = 0;
  public static final int INTERVAL_ADDED = 1;
  public static final int INTERVAL_REMOVED = 2;
  private int type;
  private int index0;
  private int index1;
  
  public int getType()
  {
    return type;
  }
  
  public int getIndex0()
  {
    return index0;
  }
  
  public int getIndex1()
  {
    return index1;
  }
  
  public ListDataEvent(Object paramObject, int paramInt1, int paramInt2, int paramInt3)
  {
    super(paramObject);
    type = paramInt1;
    index0 = Math.min(paramInt2, paramInt3);
    index1 = Math.max(paramInt2, paramInt3);
  }
  
  public String toString()
  {
    return getClass().getName() + "[type=" + type + ",index0=" + index0 + ",index1=" + index1 + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\event\ListDataEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */