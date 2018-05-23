package com.sun.xml.internal.messaging.saaj.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import javax.xml.transform.stream.StreamSource;

public class JAXMStreamSource
  extends StreamSource
{
  InputStream in;
  Reader reader;
  private static final boolean lazyContentLength = SAAJUtil.getSystemBoolean("saaj.lazy.contentlength");
  
  public JAXMStreamSource(InputStream paramInputStream)
    throws IOException
  {
    if (lazyContentLength)
    {
      in = paramInputStream;
    }
    else if ((paramInputStream instanceof ByteInputStream))
    {
      in = ((ByteInputStream)paramInputStream);
    }
    else
    {
      ByteOutputStream localByteOutputStream = new ByteOutputStream();
      localByteOutputStream.write(paramInputStream);
      in = localByteOutputStream.newInputStream();
    }
  }
  
  public JAXMStreamSource(Reader paramReader)
    throws IOException
  {
    if (lazyContentLength)
    {
      reader = paramReader;
      return;
    }
    CharWriter localCharWriter = new CharWriter();
    char[] arrayOfChar = new char['Ѐ'];
    int i;
    while (-1 != (i = paramReader.read(arrayOfChar))) {
      localCharWriter.write(arrayOfChar, 0, i);
    }
    reader = new CharReader(localCharWriter.getChars(), localCharWriter.getCount());
  }
  
  public InputStream getInputStream()
  {
    return in;
  }
  
  public Reader getReader()
  {
    return reader;
  }
  
  public void reset()
    throws IOException
  {
    if (in != null) {
      in.reset();
    }
    if (reader != null) {
      reader.reset();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\util\JAXMStreamSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */