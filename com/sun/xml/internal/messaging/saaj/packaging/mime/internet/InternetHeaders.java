package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.LineInputStream;
import com.sun.xml.internal.messaging.saaj.util.FinalArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractList;
import java.util.List;
import java.util.NoSuchElementException;

public final class InternetHeaders
{
  private final FinalArrayList headers = new FinalArrayList();
  private List headerValueView;
  
  public InternetHeaders() {}
  
  public InternetHeaders(InputStream paramInputStream)
    throws MessagingException
  {
    load(paramInputStream);
  }
  
  public void load(InputStream paramInputStream)
    throws MessagingException
  {
    LineInputStream localLineInputStream = new LineInputStream(paramInputStream);
    Object localObject = null;
    StringBuffer localStringBuffer = new StringBuffer();
    try
    {
      String str;
      do
      {
        str = localLineInputStream.readLine();
        if ((str != null) && ((str.startsWith(" ")) || (str.startsWith("\t"))))
        {
          if (localObject != null)
          {
            localStringBuffer.append((String)localObject);
            localObject = null;
          }
          localStringBuffer.append("\r\n");
          localStringBuffer.append(str);
        }
        else
        {
          if (localObject != null)
          {
            addHeaderLine((String)localObject);
          }
          else if (localStringBuffer.length() > 0)
          {
            addHeaderLine(localStringBuffer.toString());
            localStringBuffer.setLength(0);
          }
          localObject = str;
        }
        if (str == null) {
          break;
        }
      } while (str.length() > 0);
    }
    catch (IOException localIOException)
    {
      throw new MessagingException("Error in input stream", localIOException);
    }
  }
  
  public String[] getHeader(String paramString)
  {
    FinalArrayList localFinalArrayList = new FinalArrayList();
    int i = headers.size();
    for (int j = 0; j < i; j++)
    {
      hdr localhdr = (hdr)headers.get(j);
      if (paramString.equalsIgnoreCase(name)) {
        localFinalArrayList.add(localhdr.getValue());
      }
    }
    if (localFinalArrayList.size() == 0) {
      return null;
    }
    return (String[])localFinalArrayList.toArray(new String[localFinalArrayList.size()]);
  }
  
  public String getHeader(String paramString1, String paramString2)
  {
    String[] arrayOfString = getHeader(paramString1);
    if (arrayOfString == null) {
      return null;
    }
    if ((arrayOfString.length == 1) || (paramString2 == null)) {
      return arrayOfString[0];
    }
    StringBuffer localStringBuffer = new StringBuffer(arrayOfString[0]);
    for (int i = 1; i < arrayOfString.length; i++)
    {
      localStringBuffer.append(paramString2);
      localStringBuffer.append(arrayOfString[i]);
    }
    return localStringBuffer.toString();
  }
  
  public void setHeader(String paramString1, String paramString2)
  {
    int i = 0;
    for (int j = 0; j < headers.size(); j++)
    {
      hdr localhdr = (hdr)headers.get(j);
      if (paramString1.equalsIgnoreCase(name)) {
        if (i == 0)
        {
          int k;
          if ((line != null) && ((k = line.indexOf(':')) >= 0)) {
            line = (line.substring(0, k + 1) + " " + paramString2);
          } else {
            line = (paramString1 + ": " + paramString2);
          }
          i = 1;
        }
        else
        {
          headers.remove(j);
          j--;
        }
      }
    }
    if (i == 0) {
      addHeader(paramString1, paramString2);
    }
  }
  
  public void addHeader(String paramString1, String paramString2)
  {
    int i = headers.size();
    for (int j = headers.size() - 1; j >= 0; j--)
    {
      hdr localhdr = (hdr)headers.get(j);
      if (paramString1.equalsIgnoreCase(name))
      {
        headers.add(j + 1, new hdr(paramString1, paramString2));
        return;
      }
      if (name.equals(":")) {
        i = j;
      }
    }
    headers.add(i, new hdr(paramString1, paramString2));
  }
  
  public void removeHeader(String paramString)
  {
    for (int i = 0; i < headers.size(); i++)
    {
      hdr localhdr = (hdr)headers.get(i);
      if (paramString.equalsIgnoreCase(name))
      {
        headers.remove(i);
        i--;
      }
    }
  }
  
  public FinalArrayList getAllHeaders()
  {
    return headers;
  }
  
  public void addHeaderLine(String paramString)
  {
    try
    {
      int i = paramString.charAt(0);
      if ((i == 32) || (i == 9))
      {
        hdr localhdr = (hdr)headers.get(headers.size() - 1);
        hdr tmp46_45 = localhdr;
        4645line = (4645line + "\r\n" + paramString);
      }
      else
      {
        headers.add(new hdr(paramString));
      }
    }
    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException) {}catch (NoSuchElementException localNoSuchElementException) {}
  }
  
  public List getAllHeaderLines()
  {
    if (headerValueView == null) {
      headerValueView = new AbstractList()
      {
        public Object get(int paramAnonymousInt)
        {
          return headers.get(paramAnonymousInt)).line;
        }
        
        public int size()
        {
          return headers.size();
        }
      };
    }
    return headerValueView;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\internet\InternetHeaders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */