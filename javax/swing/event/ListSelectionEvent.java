package javax.swing.event;

import java.util.EventObject;

public class ListSelectionEvent
  extends EventObject
{
  private int firstIndex;
  private int lastIndex;
  private boolean isAdjusting;
  
  public ListSelectionEvent(Object paramObject, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    super(paramObject);
    firstIndex = paramInt1;
    lastIndex = paramInt2;
    isAdjusting = paramBoolean;
  }
  
  public int getFirstIndex()
  {
    return firstIndex;
  }
  
  public int getLastIndex()
  {
    return lastIndex;
  }
  
  public boolean getValueIsAdjusting()
  {
    return isAdjusting;
  }
  
  public String toString()
  {
    String str = " source=" + getSource() + " firstIndex= " + firstIndex + " lastIndex= " + lastIndex + " isAdjusting= " + isAdjusting + " ";
    return getClass().getName() + "[" + str + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\event\ListSelectionEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */