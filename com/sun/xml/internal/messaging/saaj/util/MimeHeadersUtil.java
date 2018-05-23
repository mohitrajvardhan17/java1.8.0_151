package com.sun.xml.internal.messaging.saaj.util;

import java.util.Iterator;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;

public class MimeHeadersUtil
{
  public MimeHeadersUtil() {}
  
  public static MimeHeaders copy(MimeHeaders paramMimeHeaders)
  {
    MimeHeaders localMimeHeaders = new MimeHeaders();
    Iterator localIterator = paramMimeHeaders.getAllHeaders();
    while (localIterator.hasNext())
    {
      MimeHeader localMimeHeader = (MimeHeader)localIterator.next();
      localMimeHeaders.addHeader(localMimeHeader.getName(), localMimeHeader.getValue());
    }
    return localMimeHeaders;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\util\MimeHeadersUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */