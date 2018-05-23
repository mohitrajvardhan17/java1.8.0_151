package sun.security.jgss;

import java.util.LinkedList;
import org.ietf.jgss.MessageProp;

public class TokenTracker
{
  static final int MAX_INTERVALS = 5;
  private int initNumber;
  private int windowStart;
  private int expectedNumber;
  private int windowStartIndex = 0;
  private LinkedList<Entry> list = new LinkedList();
  
  public TokenTracker(int paramInt)
  {
    initNumber = paramInt;
    windowStart = paramInt;
    expectedNumber = paramInt;
    Entry localEntry = new Entry(paramInt - 1);
    list.add(localEntry);
  }
  
  private int getIntervalIndex(int paramInt)
  {
    Entry localEntry = null;
    for (int i = list.size() - 1; i >= 0; i--)
    {
      localEntry = (Entry)list.get(i);
      if (localEntry.compareTo(paramInt) <= 0) {
        break;
      }
    }
    return i;
  }
  
  public final synchronized void getProps(int paramInt, MessageProp paramMessageProp)
  {
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    boolean bool4 = false;
    int i = getIntervalIndex(paramInt);
    Entry localEntry = null;
    if (i != -1) {
      localEntry = (Entry)list.get(i);
    }
    if (paramInt == expectedNumber) {
      expectedNumber += 1;
    } else if ((localEntry != null) && (localEntry.contains(paramInt))) {
      bool4 = true;
    } else if (expectedNumber >= initNumber)
    {
      if (paramInt > expectedNumber) {
        bool1 = true;
      } else if (paramInt >= windowStart) {
        bool3 = true;
      } else if (paramInt >= initNumber) {
        bool2 = true;
      } else {
        bool1 = true;
      }
    }
    else if (paramInt > expectedNumber)
    {
      if (paramInt < initNumber) {
        bool1 = true;
      } else if (windowStart >= initNumber)
      {
        if (paramInt >= windowStart) {
          bool3 = true;
        } else {
          bool2 = true;
        }
      }
      else {
        bool2 = true;
      }
    }
    else if (windowStart > expectedNumber) {
      bool3 = true;
    } else if (paramInt < windowStart) {
      bool2 = true;
    } else {
      bool3 = true;
    }
    if ((!bool4) && (!bool2)) {
      add(paramInt, i);
    }
    if (bool1) {
      expectedNumber = (paramInt + 1);
    }
    paramMessageProp.setSupplementaryStates(bool4, bool2, bool3, bool1, 0, null);
  }
  
  private void add(int paramInt1, int paramInt2)
  {
    Entry localEntry2 = null;
    Entry localEntry3 = null;
    int i = 0;
    int j = 0;
    if (paramInt2 != -1)
    {
      localEntry2 = (Entry)list.get(paramInt2);
      if (paramInt1 == localEntry2.getEnd() + 1)
      {
        localEntry2.setEnd(paramInt1);
        i = 1;
      }
    }
    int k = paramInt2 + 1;
    if (k < list.size())
    {
      localEntry3 = (Entry)list.get(k);
      if (paramInt1 == localEntry3.getStart() - 1)
      {
        if (i == 0)
        {
          localEntry3.setStart(paramInt1);
        }
        else
        {
          localEntry3.setStart(localEntry2.getStart());
          list.remove(paramInt2);
          if (windowStartIndex > paramInt2) {
            windowStartIndex -= 1;
          }
        }
        j = 1;
      }
    }
    if ((j != 0) || (i != 0)) {
      return;
    }
    Entry localEntry1;
    if (list.size() < 5)
    {
      localEntry1 = new Entry(paramInt1);
      if (paramInt2 < windowStartIndex) {
        windowStartIndex += 1;
      }
    }
    else
    {
      int m = windowStartIndex;
      if (windowStartIndex == list.size() - 1) {
        windowStartIndex = 0;
      }
      localEntry1 = (Entry)list.remove(m);
      windowStart = ((Entry)list.get(windowStartIndex)).getStart();
      localEntry1.setStart(paramInt1);
      localEntry1.setEnd(paramInt1);
      if (paramInt2 >= m) {
        paramInt2--;
      } else if (m != windowStartIndex)
      {
        if (paramInt2 == -1) {
          windowStart = paramInt1;
        }
      }
      else {
        windowStartIndex += 1;
      }
    }
    list.add(paramInt2 + 1, localEntry1);
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("TokenTracker: ");
    localStringBuffer.append(" initNumber=").append(initNumber);
    localStringBuffer.append(" windowStart=").append(windowStart);
    localStringBuffer.append(" expectedNumber=").append(expectedNumber);
    localStringBuffer.append(" windowStartIndex=").append(windowStartIndex);
    localStringBuffer.append("\n\tIntervals are: {");
    for (int i = 0; i < list.size(); i++)
    {
      if (i != 0) {
        localStringBuffer.append(", ");
      }
      localStringBuffer.append(((Entry)list.get(i)).toString());
    }
    localStringBuffer.append('}');
    return localStringBuffer.toString();
  }
  
  class Entry
  {
    private int start;
    private int end;
    
    Entry(int paramInt)
    {
      start = paramInt;
      end = paramInt;
    }
    
    final int compareTo(int paramInt)
    {
      if (start > paramInt) {
        return 1;
      }
      if (end < paramInt) {
        return -1;
      }
      return 0;
    }
    
    final boolean contains(int paramInt)
    {
      return (paramInt >= start) && (paramInt <= end);
    }
    
    final void append(int paramInt)
    {
      if (paramInt == end + 1) {
        end = paramInt;
      }
    }
    
    final void setInterval(int paramInt1, int paramInt2)
    {
      start = paramInt1;
      end = paramInt2;
    }
    
    final void setEnd(int paramInt)
    {
      end = paramInt;
    }
    
    final void setStart(int paramInt)
    {
      start = paramInt;
    }
    
    final int getStart()
    {
      return start;
    }
    
    final int getEnd()
    {
      return end;
    }
    
    public String toString()
    {
      return "[" + start + ", " + end + "]";
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\TokenTracker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */