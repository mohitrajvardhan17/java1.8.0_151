package com.sun.xml.internal.bind.v2.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.WeakHashMap;

public class EditDistance
{
  private static final WeakHashMap<AbstractMap.SimpleEntry<String, String>, Integer> CACHE = new WeakHashMap();
  private int[] cost;
  private int[] back;
  private final String a;
  private final String b;
  
  public static int editDistance(String paramString1, String paramString2)
  {
    AbstractMap.SimpleEntry localSimpleEntry = new AbstractMap.SimpleEntry(paramString1, paramString2);
    Integer localInteger = null;
    if (CACHE.containsKey(localSimpleEntry)) {
      localInteger = (Integer)CACHE.get(localSimpleEntry);
    }
    if (localInteger == null)
    {
      localInteger = Integer.valueOf(new EditDistance(paramString1, paramString2).calc());
      CACHE.put(localSimpleEntry, localInteger);
    }
    return localInteger.intValue();
  }
  
  public static String findNearest(String paramString, String[] paramArrayOfString)
  {
    return findNearest(paramString, Arrays.asList(paramArrayOfString));
  }
  
  public static String findNearest(String paramString, Collection<String> paramCollection)
  {
    int i = Integer.MAX_VALUE;
    Object localObject = null;
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      int j = editDistance(paramString, str);
      if (i > j)
      {
        i = j;
        localObject = str;
      }
    }
    return (String)localObject;
  }
  
  private EditDistance(String paramString1, String paramString2)
  {
    a = paramString1;
    b = paramString2;
    cost = new int[paramString1.length() + 1];
    back = new int[paramString1.length() + 1];
    for (int i = 0; i <= paramString1.length(); i++) {
      cost[i] = i;
    }
  }
  
  private void flip()
  {
    int[] arrayOfInt = cost;
    cost = back;
    back = arrayOfInt;
  }
  
  private int min(int paramInt1, int paramInt2, int paramInt3)
  {
    return Math.min(paramInt1, Math.min(paramInt2, paramInt3));
  }
  
  private int calc()
  {
    for (int i = 0; i < b.length(); i++)
    {
      flip();
      cost[0] = (i + 1);
      for (int j = 0; j < a.length(); j++)
      {
        int k = a.charAt(j) == b.charAt(i) ? 0 : 1;
        cost[(j + 1)] = min(back[j] + k, cost[j] + 1, back[(j + 1)] + 1);
      }
    }
    return cost[a.length()];
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\util\EditDistance.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */