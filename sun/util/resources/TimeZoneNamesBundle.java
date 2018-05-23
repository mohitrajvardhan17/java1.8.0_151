package sun.util.resources;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class TimeZoneNamesBundle
  extends OpenListResourceBundle
{
  public TimeZoneNamesBundle() {}
  
  public Object handleGetObject(String paramString)
  {
    String[] arrayOfString1 = (String[])super.handleGetObject(paramString);
    if (Objects.isNull(arrayOfString1)) {
      return null;
    }
    int i = arrayOfString1.length;
    String[] arrayOfString2 = new String[7];
    arrayOfString2[0] = paramString;
    System.arraycopy(arrayOfString1, 0, arrayOfString2, 1, i);
    return arrayOfString2;
  }
  
  protected <K, V> Map<K, V> createMap(int paramInt)
  {
    return new LinkedHashMap(paramInt);
  }
  
  protected <E> Set<E> createSet()
  {
    return new LinkedHashSet();
  }
  
  protected abstract Object[][] getContents();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\resources\TimeZoneNamesBundle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */