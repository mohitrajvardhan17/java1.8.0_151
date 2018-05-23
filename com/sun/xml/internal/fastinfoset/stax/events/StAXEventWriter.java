package com.sun.xml.internal.fastinfoset.stax.events;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class StAXEventWriter
  implements XMLEventWriter
{
  private XMLStreamWriter _streamWriter;
  
  public StAXEventWriter(XMLStreamWriter paramXMLStreamWriter)
  {
    _streamWriter = paramXMLStreamWriter;
  }
  
  public void flush()
    throws XMLStreamException
  {
    _streamWriter.flush();
  }
  
  public void close()
    throws XMLStreamException
  {
    _streamWriter.close();
  }
  
  public void add(XMLEventReader paramXMLEventReader)
    throws XMLStreamException
  {
    if (paramXMLEventReader == null) {
      throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.nullEventReader"));
    }
    while (paramXMLEventReader.hasNext()) {
      add(paramXMLEventReader.nextEvent());
    }
  }
  
  public void add(XMLEvent paramXMLEvent)
    throws XMLStreamException
  {
    int i = paramXMLEvent.getEventType();
    Object localObject1;
    QName localQName1;
    Object localObject2;
    switch (i)
    {
    case 11: 
      localObject1 = (DTD)paramXMLEvent;
      _streamWriter.writeDTD(((DTD)localObject1).getDocumentTypeDeclaration());
      break;
    case 7: 
      localObject1 = (StartDocument)paramXMLEvent;
      _streamWriter.writeStartDocument(((StartDocument)localObject1).getCharacterEncodingScheme(), ((StartDocument)localObject1).getVersion());
      break;
    case 1: 
      localObject1 = paramXMLEvent.asStartElement();
      localQName1 = ((StartElement)localObject1).getName();
      _streamWriter.writeStartElement(localQName1.getPrefix(), localQName1.getLocalPart(), localQName1.getNamespaceURI());
      Iterator localIterator = ((StartElement)localObject1).getNamespaces();
      while (localIterator.hasNext())
      {
        localObject2 = (Namespace)localIterator.next();
        _streamWriter.writeNamespace(((Namespace)localObject2).getPrefix(), ((Namespace)localObject2).getNamespaceURI());
      }
      localObject2 = ((StartElement)localObject1).getAttributes();
    case 13: 
    case 5: 
    case 3: 
    case 4: 
    case 9: 
    case 10: 
    case 12: 
    case 2: 
    case 8: 
    case 6: 
    default: 
      while (((Iterator)localObject2).hasNext())
      {
        Attribute localAttribute = (Attribute)((Iterator)localObject2).next();
        QName localQName2 = localAttribute.getName();
        _streamWriter.writeAttribute(localQName2.getPrefix(), localQName2.getNamespaceURI(), localQName2.getLocalPart(), localAttribute.getValue());
        continue;
        localObject1 = (Namespace)paramXMLEvent;
        _streamWriter.writeNamespace(((Namespace)localObject1).getPrefix(), ((Namespace)localObject1).getNamespaceURI());
        break;
        localObject1 = (Comment)paramXMLEvent;
        _streamWriter.writeComment(((Comment)localObject1).getText());
        break;
        localObject1 = (ProcessingInstruction)paramXMLEvent;
        _streamWriter.writeProcessingInstruction(((ProcessingInstruction)localObject1).getTarget(), ((ProcessingInstruction)localObject1).getData());
        break;
        localObject1 = paramXMLEvent.asCharacters();
        if (((Characters)localObject1).isCData())
        {
          _streamWriter.writeCData(((Characters)localObject1).getData());
        }
        else
        {
          _streamWriter.writeCharacters(((Characters)localObject1).getData());
          break;
          localObject1 = (EntityReference)paramXMLEvent;
          _streamWriter.writeEntityRef(((EntityReference)localObject1).getName());
          break;
          localObject1 = (Attribute)paramXMLEvent;
          localQName1 = ((Attribute)localObject1).getName();
          _streamWriter.writeAttribute(localQName1.getPrefix(), localQName1.getNamespaceURI(), localQName1.getLocalPart(), ((Attribute)localObject1).getValue());
          break;
          localObject1 = (Characters)paramXMLEvent;
          if (((Characters)localObject1).isCData())
          {
            _streamWriter.writeCData(((Characters)localObject1).getData());
            break;
            _streamWriter.writeEndElement();
            break;
            _streamWriter.writeEndDocument();
            break;
            throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.eventTypeNotSupported", new Object[] { Util.getEventTypeString(i) }));
          }
        }
      }
    }
  }
  
  public String getPrefix(String paramString)
    throws XMLStreamException
  {
    return _streamWriter.getPrefix(paramString);
  }
  
  public NamespaceContext getNamespaceContext()
  {
    return _streamWriter.getNamespaceContext();
  }
  
  public void setDefaultNamespace(String paramString)
    throws XMLStreamException
  {
    _streamWriter.setDefaultNamespace(paramString);
  }
  
  public void setNamespaceContext(NamespaceContext paramNamespaceContext)
    throws XMLStreamException
  {
    _streamWriter.setNamespaceContext(paramNamespaceContext);
  }
  
  public void setPrefix(String paramString1, String paramString2)
    throws XMLStreamException
  {
    _streamWriter.setPrefix(paramString1, paramString2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\StAXEventWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */