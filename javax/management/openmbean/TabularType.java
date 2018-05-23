package javax.management.openmbean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class TabularType
  extends OpenType<TabularData>
{
  static final long serialVersionUID = 6554071860220659261L;
  private CompositeType rowType;
  private List<String> indexNames;
  private transient Integer myHashCode = null;
  private transient String myToString = null;
  
  public TabularType(String paramString1, String paramString2, CompositeType paramCompositeType, String[] paramArrayOfString)
    throws OpenDataException
  {
    super(TabularData.class.getName(), paramString1, paramString2, false);
    if (paramCompositeType == null) {
      throw new IllegalArgumentException("Argument rowType cannot be null.");
    }
    checkForNullElement(paramArrayOfString, "indexNames");
    checkForEmptyString(paramArrayOfString, "indexNames");
    for (int i = 0; i < paramArrayOfString.length; i++) {
      if (!paramCompositeType.containsKey(paramArrayOfString[i])) {
        throw new OpenDataException("Argument's element value indexNames[" + i + "]=\"" + paramArrayOfString[i] + "\" is not a valid item name for rowType.");
      }
    }
    rowType = paramCompositeType;
    ArrayList localArrayList = new ArrayList(paramArrayOfString.length + 1);
    for (int j = 0; j < paramArrayOfString.length; j++) {
      localArrayList.add(paramArrayOfString[j]);
    }
    indexNames = Collections.unmodifiableList(localArrayList);
  }
  
  private static void checkForNullElement(Object[] paramArrayOfObject, String paramString)
  {
    if ((paramArrayOfObject == null) || (paramArrayOfObject.length == 0)) {
      throw new IllegalArgumentException("Argument " + paramString + "[] cannot be null or empty.");
    }
    for (int i = 0; i < paramArrayOfObject.length; i++) {
      if (paramArrayOfObject[i] == null) {
        throw new IllegalArgumentException("Argument's element " + paramString + "[" + i + "] cannot be null.");
      }
    }
  }
  
  private static void checkForEmptyString(String[] paramArrayOfString, String paramString)
  {
    for (int i = 0; i < paramArrayOfString.length; i++) {
      if (paramArrayOfString[i].trim().equals("")) {
        throw new IllegalArgumentException("Argument's element " + paramString + "[" + i + "] cannot be an empty string.");
      }
    }
  }
  
  public CompositeType getRowType()
  {
    return rowType;
  }
  
  public List<String> getIndexNames()
  {
    return indexNames;
  }
  
  public boolean isValue(Object paramObject)
  {
    if (!(paramObject instanceof TabularData)) {
      return false;
    }
    TabularData localTabularData = (TabularData)paramObject;
    TabularType localTabularType = localTabularData.getTabularType();
    return isAssignableFrom(localTabularType);
  }
  
  boolean isAssignableFrom(OpenType<?> paramOpenType)
  {
    if (!(paramOpenType instanceof TabularType)) {
      return false;
    }
    TabularType localTabularType = (TabularType)paramOpenType;
    if ((!getTypeName().equals(localTabularType.getTypeName())) || (!getIndexNames().equals(localTabularType.getIndexNames()))) {
      return false;
    }
    return getRowType().isAssignableFrom(localTabularType.getRowType());
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    TabularType localTabularType;
    try
    {
      localTabularType = (TabularType)paramObject;
    }
    catch (ClassCastException localClassCastException)
    {
      return false;
    }
    if (!getTypeName().equals(localTabularType.getTypeName())) {
      return false;
    }
    if (!rowType.equals(rowType)) {
      return false;
    }
    return indexNames.equals(indexNames);
  }
  
  public int hashCode()
  {
    if (myHashCode == null)
    {
      int i = 0;
      i += getTypeName().hashCode();
      i += rowType.hashCode();
      Iterator localIterator = indexNames.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        i += str.hashCode();
      }
      myHashCode = Integer.valueOf(i);
    }
    return myHashCode.intValue();
  }
  
  public String toString()
  {
    if (myToString == null)
    {
      StringBuilder localStringBuilder = new StringBuilder().append(getClass().getName()).append("(name=").append(getTypeName()).append(",rowType=").append(rowType.toString()).append(",indexNames=(");
      String str1 = "";
      Iterator localIterator = indexNames.iterator();
      while (localIterator.hasNext())
      {
        String str2 = (String)localIterator.next();
        localStringBuilder.append(str1).append(str2);
        str1 = ",";
      }
      localStringBuilder.append("))");
      myToString = localStringBuilder.toString();
    }
    return myToString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\openmbean\TabularType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */