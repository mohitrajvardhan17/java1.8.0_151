package sun.swing;

import java.util.ArrayList;
import java.util.List;

public class BakedArrayList
  extends ArrayList
{
  private int _hashCode;
  
  public BakedArrayList(int paramInt)
  {
    super(paramInt);
  }
  
  public BakedArrayList(List paramList)
  {
    this(paramList.size());
    int i = 0;
    int j = paramList.size();
    while (i < j)
    {
      add(paramList.get(i));
      i++;
    }
    cacheHashCode();
  }
  
  public void cacheHashCode()
  {
    _hashCode = 1;
    for (int i = size() - 1; i >= 0; i--) {
      _hashCode = (31 * _hashCode + get(i).hashCode());
    }
  }
  
  public int hashCode()
  {
    return _hashCode;
  }
  
  public boolean equals(Object paramObject)
  {
    BakedArrayList localBakedArrayList = (BakedArrayList)paramObject;
    int i = size();
    if (localBakedArrayList.size() != i) {
      return false;
    }
    while (i-- > 0) {
      if (!get(i).equals(localBakedArrayList.get(i))) {
        return false;
      }
    }
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\swing\BakedArrayList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */