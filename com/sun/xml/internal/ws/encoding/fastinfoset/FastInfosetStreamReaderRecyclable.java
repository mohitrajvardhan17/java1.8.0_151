package com.sun.xml.internal.ws.encoding.fastinfoset;

import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory.RecycleAware;
import java.io.InputStream;

public final class FastInfosetStreamReaderRecyclable
  extends StAXDocumentParser
  implements XMLStreamReaderFactory.RecycleAware
{
  private static final FastInfosetStreamReaderFactory READER_FACTORY = ;
  
  public FastInfosetStreamReaderRecyclable() {}
  
  public FastInfosetStreamReaderRecyclable(InputStream paramInputStream)
  {
    super(paramInputStream);
  }
  
  public void onRecycled()
  {
    READER_FACTORY.doRecycle(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\encoding\fastinfoset\FastInfosetStreamReaderRecyclable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */