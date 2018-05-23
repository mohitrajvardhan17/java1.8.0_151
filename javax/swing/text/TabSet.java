package javax.swing.text;

import java.io.Serializable;

public class TabSet
  implements Serializable
{
  private TabStop[] tabs;
  private int hashCode = Integer.MAX_VALUE;
  
  public TabSet(TabStop[] paramArrayOfTabStop)
  {
    if (paramArrayOfTabStop != null)
    {
      int i = paramArrayOfTabStop.length;
      tabs = new TabStop[i];
      System.arraycopy(paramArrayOfTabStop, 0, tabs, 0, i);
    }
    else
    {
      tabs = null;
    }
  }
  
  public int getTabCount()
  {
    return tabs == null ? 0 : tabs.length;
  }
  
  public TabStop getTab(int paramInt)
  {
    int i = getTabCount();
    if ((paramInt < 0) || (paramInt >= i)) {
      throw new IllegalArgumentException(paramInt + " is outside the range of tabs");
    }
    return tabs[paramInt];
  }
  
  public TabStop getTabAfter(float paramFloat)
  {
    int i = getTabIndexAfter(paramFloat);
    return i == -1 ? null : tabs[i];
  }
  
  public int getTabIndex(TabStop paramTabStop)
  {
    for (int i = getTabCount() - 1; i >= 0; i--) {
      if (getTab(i) == paramTabStop) {
        return i;
      }
    }
    return -1;
  }
  
  public int getTabIndexAfter(float paramFloat)
  {
    int j = 0;
    int k = getTabCount();
    while (j != k)
    {
      int i = (k - j) / 2 + j;
      if (paramFloat > tabs[i].getPosition())
      {
        if (j == i) {
          j = k;
        } else {
          j = i;
        }
      }
      else
      {
        if ((i == 0) || (paramFloat > tabs[(i - 1)].getPosition())) {
          return i;
        }
        k = i;
      }
    }
    return -1;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof TabSet))
    {
      TabSet localTabSet = (TabSet)paramObject;
      int i = getTabCount();
      if (localTabSet.getTabCount() != i) {
        return false;
      }
      for (int j = 0; j < i; j++)
      {
        TabStop localTabStop1 = getTab(j);
        TabStop localTabStop2 = localTabSet.getTab(j);
        if (((localTabStop1 == null) && (localTabStop2 != null)) || ((localTabStop1 != null) && (!getTab(j).equals(localTabSet.getTab(j))))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  public int hashCode()
  {
    if (hashCode == Integer.MAX_VALUE)
    {
      hashCode = 0;
      int i = getTabCount();
      for (int j = 0; j < i; j++)
      {
        TabStop localTabStop = getTab(j);
        hashCode ^= (localTabStop != null ? getTab(j).hashCode() : 0);
      }
      if (hashCode == Integer.MAX_VALUE) {
        hashCode -= 1;
      }
    }
    return hashCode;
  }
  
  public String toString()
  {
    int i = getTabCount();
    StringBuilder localStringBuilder = new StringBuilder("[ ");
    for (int j = 0; j < i; j++)
    {
      if (j > 0) {
        localStringBuilder.append(" - ");
      }
      localStringBuilder.append(getTab(j).toString());
    }
    localStringBuilder.append(" ]");
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\TabSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */