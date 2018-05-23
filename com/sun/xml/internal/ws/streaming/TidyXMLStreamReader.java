package com.sun.xml.internal.ws.streaming;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.util.xml.XMLStreamReaderFilter;
import java.io.Closeable;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;

public class TidyXMLStreamReader
  extends XMLStreamReaderFilter
{
  private final Closeable closeableSource;
  
  public TidyXMLStreamReader(@NotNull XMLStreamReader paramXMLStreamReader, @Nullable Closeable paramCloseable)
  {
    super(paramXMLStreamReader);
    closeableSource = paramCloseable;
  }
  
  public void close()
    throws XMLStreamException
  {
    super.close();
    try
    {
      if (closeableSource != null) {
        closeableSource.close();
      }
    }
    catch (IOException localIOException)
    {
      throw new WebServiceException(localIOException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\streaming\TidyXMLStreamReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */