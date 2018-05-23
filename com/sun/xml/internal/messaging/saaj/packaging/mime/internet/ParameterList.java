package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public final class ParameterList
{
  private final HashMap list;
  
  public ParameterList()
  {
    list = new HashMap();
  }
  
  private ParameterList(HashMap paramHashMap)
  {
    list = paramHashMap;
  }
  
  public ParameterList(String paramString)
    throws ParseException
  {
    HeaderTokenizer localHeaderTokenizer = new HeaderTokenizer(paramString, "()<>@,;:\\\"\t []/?=");
    list = new HashMap();
    for (;;)
    {
      HeaderTokenizer.Token localToken = localHeaderTokenizer.next();
      int i = localToken.getType();
      if (i == -4) {
        return;
      }
      if ((char)i != ';') {
        break;
      }
      localToken = localHeaderTokenizer.next();
      if (localToken.getType() == -4) {
        return;
      }
      if (localToken.getType() != -1) {
        throw new ParseException();
      }
      String str = localToken.getValue().toLowerCase();
      localToken = localHeaderTokenizer.next();
      if ((char)localToken.getType() != '=') {
        throw new ParseException();
      }
      localToken = localHeaderTokenizer.next();
      i = localToken.getType();
      if ((i != -1) && (i != -2)) {
        throw new ParseException();
      }
      list.put(str, localToken.getValue());
    }
    throw new ParseException();
  }
  
  public int size()
  {
    return list.size();
  }
  
  public String get(String paramString)
  {
    return (String)list.get(paramString.trim().toLowerCase());
  }
  
  public void set(String paramString1, String paramString2)
  {
    list.put(paramString1.trim().toLowerCase(), paramString2);
  }
  
  public void remove(String paramString)
  {
    list.remove(paramString.trim().toLowerCase());
  }
  
  public Iterator getNames()
  {
    return list.keySet().iterator();
  }
  
  public String toString()
  {
    return toString(0);
  }
  
  public String toString(int paramInt)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    Iterator localIterator = list.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str1 = (String)localEntry.getKey();
      String str2 = quote((String)localEntry.getValue());
      localStringBuffer.append("; ");
      paramInt += 2;
      int i = str1.length() + str2.length() + 1;
      if (paramInt + i > 76)
      {
        localStringBuffer.append("\r\n\t");
        paramInt = 8;
      }
      localStringBuffer.append(str1).append('=');
      paramInt += str1.length() + 1;
      if (paramInt + str2.length() > 76)
      {
        String str3 = MimeUtility.fold(paramInt, str2);
        localStringBuffer.append(str3);
        int j = str3.lastIndexOf('\n');
        if (j >= 0) {
          paramInt += str3.length() - j - 1;
        } else {
          paramInt += str3.length();
        }
      }
      else
      {
        localStringBuffer.append(str2);
        paramInt += str2.length();
      }
    }
    return localStringBuffer.toString();
  }
  
  private String quote(String paramString)
  {
    if ("".equals(paramString)) {
      return "\"\"";
    }
    return MimeUtility.quote(paramString, "()<>@,;:\\\"\t []/?=");
  }
  
  public ParameterList copy()
  {
    return new ParameterList((HashMap)list.clone());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\internet\ParameterList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */