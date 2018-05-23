package com.sun.xml.internal.ws.message;

import com.sun.xml.internal.ws.api.message.Message;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLFilterImpl;

final class XMLReaderImpl
  extends XMLFilterImpl
{
  private final Message msg;
  private static final ContentHandler DUMMY = new DefaultHandler();
  protected static final InputSource THE_SOURCE = new InputSource();
  
  XMLReaderImpl(Message paramMessage)
  {
    msg = paramMessage;
  }
  
  public void parse(String paramString)
  {
    reportError();
  }
  
  private void reportError()
  {
    throw new IllegalStateException("This is a special XMLReader implementation that only works with the InputSource given in SAXSource.");
  }
  
  public void parse(InputSource paramInputSource)
    throws SAXException
  {
    if (paramInputSource != THE_SOURCE) {
      reportError();
    }
    msg.writeTo(this, this);
  }
  
  public ContentHandler getContentHandler()
  {
    if (super.getContentHandler() == DUMMY) {
      return null;
    }
    return super.getContentHandler();
  }
  
  public void setContentHandler(ContentHandler paramContentHandler)
  {
    if (paramContentHandler == null) {
      paramContentHandler = DUMMY;
    }
    super.setContentHandler(paramContentHandler);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\XMLReaderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */