package com.sun.org.apache.xml.internal.dtm.ref;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public abstract interface IncrementalSAXSource
{
  public abstract void setContentHandler(ContentHandler paramContentHandler);
  
  public abstract void setLexicalHandler(LexicalHandler paramLexicalHandler);
  
  public abstract void setDTDHandler(DTDHandler paramDTDHandler);
  
  public abstract Object deliverMoreNodes(boolean paramBoolean);
  
  public abstract void startParse(InputSource paramInputSource)
    throws SAXException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\IncrementalSAXSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */