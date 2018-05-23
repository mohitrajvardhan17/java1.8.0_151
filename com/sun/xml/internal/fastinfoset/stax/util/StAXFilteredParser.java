package com.sun.xml.internal.fastinfoset.stax.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class StAXFilteredParser
  extends StAXParserWrapper
{
  private StreamFilter _filter;
  
  public StAXFilteredParser() {}
  
  public StAXFilteredParser(XMLStreamReader paramXMLStreamReader, StreamFilter paramStreamFilter)
  {
    super(paramXMLStreamReader);
    _filter = paramStreamFilter;
  }
  
  public void setFilter(StreamFilter paramStreamFilter)
  {
    _filter = paramStreamFilter;
  }
  
  public int next()
    throws XMLStreamException
  {
    if (hasNext()) {
      return super.next();
    }
    throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.noMoreItems"));
  }
  
  public boolean hasNext()
    throws XMLStreamException
  {
    while (super.hasNext())
    {
      if (_filter.accept(getReader())) {
        return true;
      }
      super.next();
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\stax\util\StAXFilteredParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */