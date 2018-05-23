package com.sun.xml.internal.ws.message;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

public final class RootElementSniffer
  extends DefaultHandler
{
  private String nsUri = "##error";
  private String localName = "##error";
  private Attributes atts;
  private final boolean parseAttributes;
  private static final SAXException aSAXException = new SAXException();
  private static final Attributes EMPTY_ATTRIBUTES = new AttributesImpl();
  
  public RootElementSniffer(boolean paramBoolean)
  {
    parseAttributes = paramBoolean;
  }
  
  public RootElementSniffer()
  {
    this(true);
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    nsUri = paramString1;
    localName = paramString2;
    if (parseAttributes) {
      if (paramAttributes.getLength() == 0) {
        atts = EMPTY_ATTRIBUTES;
      } else {
        atts = new AttributesImpl(paramAttributes);
      }
    }
    throw aSAXException;
  }
  
  public String getNsUri()
  {
    return nsUri;
  }
  
  public String getLocalName()
  {
    return localName;
  }
  
  public Attributes getAttributes()
  {
    return atts;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\RootElementSniffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */