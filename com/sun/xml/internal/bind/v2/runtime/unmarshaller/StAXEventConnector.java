package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

final class StAXEventConnector
  extends StAXConnector
{
  private final XMLEventReader staxEventReader;
  private XMLEvent event;
  private final AttributesImpl attrs = new AttributesImpl();
  private final StringBuilder buffer = new StringBuilder();
  private boolean seenText;
  
  public StAXEventConnector(XMLEventReader paramXMLEventReader, XmlVisitor paramXmlVisitor)
  {
    super(paramXmlVisitor);
    staxEventReader = paramXMLEventReader;
  }
  
  public void bridge()
    throws XMLStreamException
  {
    try
    {
      int i = 0;
      event = staxEventReader.peek();
      if ((!event.isStartDocument()) && (!event.isStartElement())) {
        throw new IllegalStateException();
      }
      do
      {
        event = staxEventReader.nextEvent();
      } while (!event.isStartElement());
      handleStartDocument(event.asStartElement().getNamespaceContext());
      for (;;)
      {
        switch (event.getEventType())
        {
        case 1: 
          handleStartElement(event.asStartElement());
          i++;
          break;
        case 2: 
          i--;
          handleEndElement(event.asEndElement());
          if (i != 0) {
            break;
          }
          break;
        case 4: 
        case 6: 
        case 12: 
          handleCharacters(event.asCharacters());
        }
        event = staxEventReader.nextEvent();
      }
      handleEndDocument();
      event = null;
    }
    catch (SAXException localSAXException)
    {
      throw new XMLStreamException(localSAXException);
    }
  }
  
  protected Location getCurrentLocation()
  {
    return event.getLocation();
  }
  
  protected String getCurrentQName()
  {
    QName localQName;
    if (event.isEndElement()) {
      localQName = event.asEndElement().getName();
    } else {
      localQName = event.asStartElement().getName();
    }
    return getQName(localQName.getPrefix(), localQName.getLocalPart());
  }
  
  private void handleCharacters(Characters paramCharacters)
    throws SAXException, XMLStreamException
  {
    if (!predictor.expectText()) {
      return;
    }
    seenText = true;
    XMLEvent localXMLEvent;
    for (;;)
    {
      localXMLEvent = staxEventReader.peek();
      if (!isIgnorable(localXMLEvent)) {
        break;
      }
      staxEventReader.nextEvent();
    }
    if (isTag(localXMLEvent))
    {
      visitor.text(paramCharacters.getData());
      return;
    }
    buffer.append(paramCharacters.getData());
    for (;;)
    {
      localXMLEvent = staxEventReader.peek();
      if (isIgnorable(localXMLEvent))
      {
        staxEventReader.nextEvent();
      }
      else
      {
        if (isTag(localXMLEvent))
        {
          visitor.text(buffer);
          buffer.setLength(0);
          return;
        }
        buffer.append(localXMLEvent.asCharacters().getData());
        staxEventReader.nextEvent();
      }
    }
  }
  
  private boolean isTag(XMLEvent paramXMLEvent)
  {
    int i = paramXMLEvent.getEventType();
    return (i == 1) || (i == 2);
  }
  
  private boolean isIgnorable(XMLEvent paramXMLEvent)
  {
    int i = paramXMLEvent.getEventType();
    return (i == 5) || (i == 3);
  }
  
  private void handleEndElement(EndElement paramEndElement)
    throws SAXException
  {
    if ((!seenText) && (predictor.expectText())) {
      visitor.text("");
    }
    QName localQName = paramEndElement.getName();
    tagName.uri = fixNull(localQName.getNamespaceURI());
    tagName.local = localQName.getLocalPart();
    visitor.endElement(tagName);
    Iterator localIterator = paramEndElement.getNamespaces();
    while (localIterator.hasNext())
    {
      String str = fixNull(((Namespace)localIterator.next()).getPrefix());
      visitor.endPrefixMapping(str);
    }
    seenText = false;
  }
  
  private void handleStartElement(StartElement paramStartElement)
    throws SAXException
  {
    Object localObject1 = paramStartElement.getNamespaces();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Namespace)((Iterator)localObject1).next();
      visitor.startPrefixMapping(fixNull(((Namespace)localObject2).getPrefix()), fixNull(((Namespace)localObject2).getNamespaceURI()));
    }
    localObject1 = paramStartElement.getName();
    tagName.uri = fixNull(((QName)localObject1).getNamespaceURI());
    Object localObject2 = ((QName)localObject1).getLocalPart();
    tagName.uri = fixNull(((QName)localObject1).getNamespaceURI());
    tagName.local = ((String)localObject2);
    tagName.atts = getAttributes(paramStartElement);
    visitor.startElement(tagName);
    seenText = false;
  }
  
  private Attributes getAttributes(StartElement paramStartElement)
  {
    attrs.clear();
    Iterator localIterator = paramStartElement.getAttributes();
    while (localIterator.hasNext())
    {
      Attribute localAttribute = (Attribute)localIterator.next();
      QName localQName = localAttribute.getName();
      String str1 = fixNull(localQName.getNamespaceURI());
      String str2 = localQName.getLocalPart();
      String str3 = localQName.getPrefix();
      String str4;
      if ((str3 == null) || (str3.length() == 0)) {
        str4 = str2;
      } else {
        str4 = str3 + ':' + str2;
      }
      String str5 = localAttribute.getDTDType();
      String str6 = localAttribute.getValue();
      attrs.addAttribute(str1, str2, str4, str5, str6);
    }
    return attrs;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\StAXEventConnector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */