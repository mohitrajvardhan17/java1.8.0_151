package sun.net.www;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringJoiner;

public class MessageHeader
{
  private String[] keys;
  private String[] values;
  private int nkeys;
  
  public MessageHeader()
  {
    grow();
  }
  
  public MessageHeader(InputStream paramInputStream)
    throws IOException
  {
    parseHeader(paramInputStream);
  }
  
  public synchronized String getHeaderNamesInList()
  {
    StringJoiner localStringJoiner = new StringJoiner(",");
    for (int i = 0; i < nkeys; i++) {
      localStringJoiner.add(keys[i]);
    }
    return localStringJoiner.toString();
  }
  
  public synchronized void reset()
  {
    keys = null;
    values = null;
    nkeys = 0;
    grow();
  }
  
  public synchronized String findValue(String paramString)
  {
    int i;
    if (paramString == null)
    {
      i = nkeys;
      do
      {
        i--;
        if (i < 0) {
          break;
        }
      } while (keys[i] != null);
      return values[i];
    }
    else
    {
      i = nkeys;
      do
      {
        i--;
        if (i < 0) {
          break;
        }
      } while (!paramString.equalsIgnoreCase(keys[i]));
      return values[i];
    }
    return null;
  }
  
  public synchronized int getKey(String paramString)
  {
    int i = nkeys;
    do
    {
      i--;
      if (i < 0) {
        break;
      }
    } while ((keys[i] != paramString) && ((paramString == null) || (!paramString.equalsIgnoreCase(keys[i]))));
    return i;
    return -1;
  }
  
  public synchronized String getKey(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= nkeys)) {
      return null;
    }
    return keys[paramInt];
  }
  
  public synchronized String getValue(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= nkeys)) {
      return null;
    }
    return values[paramInt];
  }
  
  public synchronized String findNextValue(String paramString1, String paramString2)
  {
    int i = 0;
    int j;
    if (paramString1 == null)
    {
      j = nkeys;
      for (;;)
      {
        j--;
        if (j < 0) {
          break;
        }
        if (keys[j] == null)
        {
          if (i != 0) {
            return values[j];
          }
          if (values[j] == paramString2) {
            i = 1;
          }
        }
      }
    }
    else
    {
      j = nkeys;
      for (;;)
      {
        j--;
        if (j < 0) {
          break;
        }
        if (paramString1.equalsIgnoreCase(keys[j]))
        {
          if (i != 0) {
            return values[j];
          }
          if (values[j] == paramString2) {
            i = 1;
          }
        }
      }
    }
    return null;
  }
  
  public boolean filterNTLMResponses(String paramString)
  {
    int i = 0;
    for (int j = 0; j < nkeys; j++) {
      if ((paramString.equalsIgnoreCase(keys[j])) && (values[j] != null) && (values[j].length() > 5) && (values[j].substring(0, 5).equalsIgnoreCase("NTLM ")))
      {
        i = 1;
        break;
      }
    }
    if (i != 0)
    {
      j = 0;
      for (int k = 0; k < nkeys; k++) {
        if ((!paramString.equalsIgnoreCase(keys[k])) || ((!"Negotiate".equalsIgnoreCase(values[k])) && (!"Kerberos".equalsIgnoreCase(values[k]))))
        {
          if (k != j)
          {
            keys[j] = keys[k];
            values[j] = values[k];
          }
          j++;
        }
      }
      if (j != nkeys)
      {
        nkeys = j;
        return true;
      }
    }
    return false;
  }
  
  public Iterator<String> multiValueIterator(String paramString)
  {
    return new HeaderIterator(paramString, this);
  }
  
  public synchronized Map<String, List<String>> getHeaders()
  {
    return getHeaders(null);
  }
  
  public synchronized Map<String, List<String>> getHeaders(String[] paramArrayOfString)
  {
    return filterAndAddHeaders(paramArrayOfString, null);
  }
  
  public synchronized Map<String, List<String>> filterAndAddHeaders(String[] paramArrayOfString, Map<String, List<String>> paramMap)
  {
    int i = 0;
    HashMap localHashMap = new HashMap();
    int j = nkeys;
    Object localObject1;
    for (;;)
    {
      j--;
      if (j < 0) {
        break;
      }
      if (paramArrayOfString != null) {
        for (int k = 0; k < paramArrayOfString.length; k++) {
          if ((paramArrayOfString[k] != null) && (paramArrayOfString[k].equalsIgnoreCase(keys[j])))
          {
            i = 1;
            break;
          }
        }
      }
      if (i == 0)
      {
        localObject1 = (List)localHashMap.get(keys[j]);
        if (localObject1 == null)
        {
          localObject1 = new ArrayList();
          localHashMap.put(keys[j], localObject1);
        }
        ((List)localObject1).add(values[j]);
      }
      else
      {
        i = 0;
      }
    }
    if (paramMap != null)
    {
      localIterator = paramMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        localObject1 = (Map.Entry)localIterator.next();
        Object localObject2 = (List)localHashMap.get(((Map.Entry)localObject1).getKey());
        if (localObject2 == null)
        {
          localObject2 = new ArrayList();
          localHashMap.put(((Map.Entry)localObject1).getKey(), localObject2);
        }
        ((List)localObject2).addAll((Collection)((Map.Entry)localObject1).getValue());
      }
    }
    Iterator localIterator = localHashMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (String)localIterator.next();
      localHashMap.put(localObject1, Collections.unmodifiableList((List)localHashMap.get(localObject1)));
    }
    return Collections.unmodifiableMap(localHashMap);
  }
  
  public synchronized void print(PrintStream paramPrintStream)
  {
    for (int i = 0; i < nkeys; i++) {
      if (keys[i] != null) {
        paramPrintStream.print(keys[i] + (values[i] != null ? ": " + values[i] : "") + "\r\n");
      }
    }
    paramPrintStream.print("\r\n");
    paramPrintStream.flush();
  }
  
  public synchronized void add(String paramString1, String paramString2)
  {
    grow();
    keys[nkeys] = paramString1;
    values[nkeys] = paramString2;
    nkeys += 1;
  }
  
  public synchronized void prepend(String paramString1, String paramString2)
  {
    grow();
    for (int i = nkeys; i > 0; i--)
    {
      keys[i] = keys[(i - 1)];
      values[i] = values[(i - 1)];
    }
    keys[0] = paramString1;
    values[0] = paramString2;
    nkeys += 1;
  }
  
  public synchronized void set(int paramInt, String paramString1, String paramString2)
  {
    grow();
    if (paramInt < 0) {
      return;
    }
    if (paramInt >= nkeys)
    {
      add(paramString1, paramString2);
    }
    else
    {
      keys[paramInt] = paramString1;
      values[paramInt] = paramString2;
    }
  }
  
  private void grow()
  {
    if ((keys == null) || (nkeys >= keys.length))
    {
      String[] arrayOfString1 = new String[nkeys + 4];
      String[] arrayOfString2 = new String[nkeys + 4];
      if (keys != null) {
        System.arraycopy(keys, 0, arrayOfString1, 0, nkeys);
      }
      if (values != null) {
        System.arraycopy(values, 0, arrayOfString2, 0, nkeys);
      }
      keys = arrayOfString1;
      values = arrayOfString2;
    }
  }
  
  public synchronized void remove(String paramString)
  {
    int i;
    int j;
    if (paramString == null) {
      for (i = 0; i < nkeys; i++) {
        while ((keys[i] == null) && (i < nkeys))
        {
          for (j = i; j < nkeys - 1; j++)
          {
            keys[j] = keys[(j + 1)];
            values[j] = values[(j + 1)];
          }
          nkeys -= 1;
        }
      }
    } else {
      for (i = 0; i < nkeys; i++) {
        while ((paramString.equalsIgnoreCase(keys[i])) && (i < nkeys))
        {
          for (j = i; j < nkeys - 1; j++)
          {
            keys[j] = keys[(j + 1)];
            values[j] = values[(j + 1)];
          }
          nkeys -= 1;
        }
      }
    }
  }
  
  public synchronized void set(String paramString1, String paramString2)
  {
    int i = nkeys;
    do
    {
      i--;
      if (i < 0) {
        break;
      }
    } while (!paramString1.equalsIgnoreCase(keys[i]));
    values[i] = paramString2;
    return;
    add(paramString1, paramString2);
  }
  
  public synchronized void setIfNotSet(String paramString1, String paramString2)
  {
    if (findValue(paramString1) == null) {
      add(paramString1, paramString2);
    }
  }
  
  public static String canonicalID(String paramString)
  {
    if (paramString == null) {
      return "";
    }
    int i = 0;
    int j = paramString.length();
    int m;
    for (int k = 0; (i < j) && (((m = paramString.charAt(i)) == '<') || (m <= 32)); k = 1) {
      i++;
    }
    while ((i < j) && (((m = paramString.charAt(j - 1)) == '>') || (m <= 32)))
    {
      j--;
      k = 1;
    }
    return k != 0 ? paramString.substring(i, j) : paramString;
  }
  
  public void parseHeader(InputStream paramInputStream)
    throws IOException
  {
    synchronized (this)
    {
      nkeys = 0;
    }
    mergeHeader(paramInputStream);
  }
  
  public void mergeHeader(InputStream paramInputStream)
    throws IOException
  {
    if (paramInputStream == null) {
      return;
    }
    Object localObject1 = new char[10];
    int i = paramInputStream.read();
    while ((i != 10) && (i != 13) && (i >= 0))
    {
      int j = 0;
      int k = -1;
      int n = i > 32 ? 1 : 0;
      localObject1[(j++)] = ((char)i);
      int m;
      Object localObject2;
      while ((m = paramInputStream.read()) >= 0)
      {
        switch (m)
        {
        case 58: 
          if ((n != 0) && (j > 0)) {
            k = j;
          }
          n = 0;
          break;
        case 9: 
          m = 32;
        case 32: 
          n = 0;
          break;
        case 10: 
        case 13: 
          i = paramInputStream.read();
          if ((m == 13) && (i == 10))
          {
            i = paramInputStream.read();
            if (i == 13) {
              i = paramInputStream.read();
            }
          }
          if ((i == 10) || (i == 13) || (i > 32)) {
            break label252;
          }
          m = 32;
        }
        if (j >= localObject1.length)
        {
          localObject2 = new char[localObject1.length * 2];
          System.arraycopy(localObject1, 0, localObject2, 0, j);
          localObject1 = localObject2;
        }
        localObject1[(j++)] = ((char)m);
      }
      i = -1;
      label252:
      while ((j > 0) && (localObject1[(j - 1)] <= ' ')) {
        j--;
      }
      if (k <= 0)
      {
        localObject2 = null;
        k = 0;
      }
      else
      {
        localObject2 = String.copyValueOf((char[])localObject1, 0, k);
        if ((k < j) && (localObject1[k] == ':')) {
          k++;
        }
        while ((k < j) && (localObject1[k] <= ' ')) {
          k++;
        }
      }
      String str;
      if (k >= j) {
        str = new String();
      } else {
        str = String.copyValueOf((char[])localObject1, k, j - k);
      }
      add((String)localObject2, str);
    }
  }
  
  public synchronized String toString()
  {
    String str = super.toString() + nkeys + " pairs: ";
    for (int i = 0; (i < keys.length) && (i < nkeys); i++) {
      str = str + "{" + keys[i] + ": " + values[i] + "}";
    }
    return str;
  }
  
  class HeaderIterator
    implements Iterator<String>
  {
    int index = 0;
    int next = -1;
    String key;
    boolean haveNext = false;
    Object lock;
    
    public HeaderIterator(String paramString, Object paramObject)
    {
      key = paramString;
      lock = paramObject;
    }
    
    public boolean hasNext()
    {
      synchronized (lock)
      {
        if (haveNext) {
          return true;
        }
        while (index < nkeys)
        {
          if (key.equalsIgnoreCase(keys[index]))
          {
            haveNext = true;
            next = (index++);
            return true;
          }
          index += 1;
        }
        return false;
      }
    }
    
    public String next()
    {
      synchronized (lock)
      {
        if (haveNext)
        {
          haveNext = false;
          return values[next];
        }
        if (hasNext()) {
          return next();
        }
        throw new NoSuchElementException("No more elements");
      }
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException("remove not allowed");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\MessageHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */