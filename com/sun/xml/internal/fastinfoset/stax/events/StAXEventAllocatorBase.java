package com.sun.xml.internal.fastinfoset.stax.events;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.stream.util.XMLEventConsumer;

public class StAXEventAllocatorBase
  implements XMLEventAllocator
{
  XMLEventFactory factory;
  
  public StAXEventAllocatorBase()
  {
    if (System.getProperty("javax.xml.stream.XMLEventFactory") == null) {
      System.setProperty("javax.xml.stream.XMLEventFactory", "com.sun.xml.internal.fastinfoset.stax.factory.StAXEventFactory");
    }
    factory = XMLEventFactory.newInstance();
  }
  
  public XMLEventAllocator newInstance()
  {
    return new StAXEventAllocatorBase();
  }
  
  public XMLEvent allocate(XMLStreamReader paramXMLStreamReader)
    throws XMLStreamException
  {
    if (paramXMLStreamReader == null) {
      throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.nullReader"));
    }
    return getXMLEvent(paramXMLStreamReader);
  }
  
  public void allocate(XMLStreamReader paramXMLStreamReader, XMLEventConsumer paramXMLEventConsumer)
    throws XMLStreamException
  {
    paramXMLEventConsumer.add(getXMLEvent(paramXMLStreamReader));
  }
  
  XMLEvent getXMLEvent(XMLStreamReader paramXMLStreamReader)
  {
    Object localObject1 = null;
    int i = paramXMLStreamReader.getEventType();
    factory.setLocation(paramXMLStreamReader.getLocation());
    Object localObject2;
    switch (i)
    {
    case 1: 
      localObject2 = (StartElementEvent)factory.createStartElement(paramXMLStreamReader.getPrefix(), paramXMLStreamReader.getNamespaceURI(), paramXMLStreamReader.getLocalName());
      addAttributes((StartElementEvent)localObject2, paramXMLStreamReader);
      addNamespaces((StartElementEvent)localObject2, paramXMLStreamReader);
      localObject1 = localObject2;
      break;
    case 2: 
      localObject2 = (EndElementEvent)factory.createEndElement(paramXMLStreamReader.getPrefix(), paramXMLStreamReader.getNamespaceURI(), paramXMLStreamReader.getLocalName());
      addNamespaces((EndElementEvent)localObject2, paramXMLStreamReader);
      localObject1 = localObject2;
      break;
    case 3: 
      localObject1 = factory.createProcessingInstruction(paramXMLStreamReader.getPITarget(), paramXMLStreamReader.getPIData());
      break;
    case 4: 
      if (paramXMLStreamReader.isWhiteSpace()) {
        localObject1 = factory.createSpace(paramXMLStreamReader.getText());
      } else {
        localObject1 = factory.createCharacters(paramXMLStreamReader.getText());
      }
      break;
    case 5: 
      localObject1 = factory.createComment(paramXMLStreamReader.getText());
      break;
    case 7: 
      localObject2 = (StartDocumentEvent)factory.createStartDocument(paramXMLStreamReader.getVersion(), paramXMLStreamReader.getEncoding(), paramXMLStreamReader.isStandalone());
      if (paramXMLStreamReader.getCharacterEncodingScheme() != null) {
        ((StartDocumentEvent)localObject2).setDeclaredEncoding(true);
      } else {
        ((StartDocumentEvent)localObject2).setDeclaredEncoding(false);
      }
      localObject1 = localObject2;
      break;
    case 8: 
      localObject2 = new EndDocumentEvent();
      localObject1 = localObject2;
      break;
    case 9: 
      localObject1 = factory.createEntityReference(paramXMLStreamReader.getLocalName(), new EntityDeclarationImpl(paramXMLStreamReader.getLocalName(), paramXMLStreamReader.getText()));
      break;
    case 10: 
      localObject1 = null;
      break;
    case 11: 
      localObject1 = factory.createDTD(paramXMLStreamReader.getText());
      break;
    case 12: 
      localObject1 = factory.createCData(paramXMLStreamReader.getText());
      break;
    case 6: 
      localObject1 = factory.createSpace(paramXMLStreamReader.getText());
    }
    return (XMLEvent)localObject1;
  }
  
  protected void addAttributes(StartElementEvent paramStartElementEvent, XMLStreamReader paramXMLStreamReader)
  {
    AttributeBase localAttributeBase = null;
    for (int i = 0; i < paramXMLStreamReader.getAttributeCount(); i++)
    {
      localAttributeBase = (AttributeBase)factory.createAttribute(paramXMLStreamReader.getAttributeName(i), paramXMLStreamReader.getAttributeValue(i));
      localAttributeBase.setAttributeType(paramXMLStreamReader.getAttributeType(i));
      localAttributeBase.setSpecified(paramXMLStreamReader.isAttributeSpecified(i));
      paramStartElementEvent.addAttribute(localAttributeBase);
    }
  }
  
  protected void addNamespaces(StartElementEvent paramStartElementEvent, XMLStreamReader paramXMLStreamReader)
  {
    Namespace localNamespace = null;
    for (int i = 0; i < paramXMLStreamReader.getNamespaceCount(); i++)
    {
      localNamespace = factory.createNamespace(paramXMLStreamReader.getNamespacePrefix(i), paramXMLStreamReader.getNamespaceURI(i));
      paramStartElementEvent.addNamespace(localNamespace);
    }
  }
  
  protected void addNamespaces(EndElementEvent paramEndElementEvent, XMLStreamReader paramXMLStreamReader)
  {
    Namespace localNamespace = null;
    for (int i = 0; i < paramXMLStreamReader.getNamespaceCount(); i++)
    {
      localNamespace = factory.createNamespace(paramXMLStreamReader.getNamespacePrefix(i), paramXMLStreamReader.getNamespaceURI(i));
      paramEndElementEvent.addNamespace(localNamespace);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\StAXEventAllocatorBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */