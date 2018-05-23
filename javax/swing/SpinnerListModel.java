package javax.swing;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class SpinnerListModel
  extends AbstractSpinnerModel
  implements Serializable
{
  private List list;
  private int index;
  
  public SpinnerListModel(List<?> paramList)
  {
    if ((paramList == null) || (paramList.size() == 0)) {
      throw new IllegalArgumentException("SpinnerListModel(List) expects non-null non-empty List");
    }
    list = paramList;
    index = 0;
  }
  
  public SpinnerListModel(Object[] paramArrayOfObject)
  {
    if ((paramArrayOfObject == null) || (paramArrayOfObject.length == 0)) {
      throw new IllegalArgumentException("SpinnerListModel(Object[]) expects non-null non-empty Object[]");
    }
    list = Arrays.asList(paramArrayOfObject);
    index = 0;
  }
  
  public SpinnerListModel()
  {
    this(new Object[] { "empty" });
  }
  
  public List<?> getList()
  {
    return list;
  }
  
  public void setList(List<?> paramList)
  {
    if ((paramList == null) || (paramList.size() == 0)) {
      throw new IllegalArgumentException("invalid list");
    }
    if (!paramList.equals(list))
    {
      list = paramList;
      index = 0;
      fireStateChanged();
    }
  }
  
  public Object getValue()
  {
    return list.get(index);
  }
  
  public void setValue(Object paramObject)
  {
    int i = list.indexOf(paramObject);
    if (i == -1) {
      throw new IllegalArgumentException("invalid sequence element");
    }
    if (i != index)
    {
      index = i;
      fireStateChanged();
    }
  }
  
  public Object getNextValue()
  {
    return index >= list.size() - 1 ? null : list.get(index + 1);
  }
  
  public Object getPreviousValue()
  {
    return index <= 0 ? null : list.get(index - 1);
  }
  
  Object findNextMatch(String paramString)
  {
    int i = list.size();
    if (i == 0) {
      return null;
    }
    int j = index;
    do
    {
      Object localObject = list.get(j);
      String str = localObject.toString();
      if ((str != null) && (str.startsWith(paramString))) {
        return localObject;
      }
      j = (j + 1) % i;
    } while (j != index);
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\SpinnerListModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */