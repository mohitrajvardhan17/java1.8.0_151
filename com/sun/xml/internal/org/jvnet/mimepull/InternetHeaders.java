package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

final class InternetHeaders
{
  private final FinalArrayList<Hdr> headers = new FinalArrayList();
  
  InternetHeaders(MIMEParser.LineInputStream paramLineInputStream)
  {
    Object localObject = null;
    StringBuilder localStringBuilder = new StringBuilder();
    try
    {
      String str;
      do
      {
        str = paramLineInputStream.readLine();
        if ((str != null) && ((str.startsWith(" ")) || (str.startsWith("\t"))))
        {
          if (localObject != null)
          {
            localStringBuilder.append((String)localObject);
            localObject = null;
          }
          localStringBuilder.append("\r\n");
          localStringBuilder.append(str);
        }
        else
        {
          if (localObject != null)
          {
            addHeaderLine((String)localObject);
          }
          else if (localStringBuilder.length() > 0)
          {
            addHeaderLine(localStringBuilder.toString());
            localStringBuilder.setLength(0);
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
      throw new MIMEParsingException("Error in input stream", localIOException);
    }
  }
  
  List<String> getHeader(String paramString)
  {
    FinalArrayList localFinalArrayList = new FinalArrayList();
    int i = headers.size();
    for (int j = 0; j < i; j++)
    {
      Hdr localHdr = (Hdr)headers.get(j);
      if (paramString.equalsIgnoreCase(name)) {
        localFinalArrayList.add(localHdr.getValue());
      }
    }
    return localFinalArrayList.size() == 0 ? null : localFinalArrayList;
  }
  
  FinalArrayList<? extends Header> getAllHeaders()
  {
    return headers;
  }
  
  void addHeaderLine(String paramString)
  {
    try
    {
      int i = paramString.charAt(0);
      if ((i == 32) || (i == 9))
      {
        Hdr localHdr = (Hdr)headers.get(headers.size() - 1);
        Hdr tmp46_45 = localHdr;
        4645line = (4645line + "\r\n" + paramString);
      }
      else
      {
        headers.add(new Hdr(paramString));
      }
    }
    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException) {}catch (NoSuchElementException localNoSuchElementException) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\InternetHeaders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */