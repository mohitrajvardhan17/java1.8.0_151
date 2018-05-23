package com.sun.xml.internal.stream.writers;

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

public class XMLEventWriterImpl
  implements XMLEventWriter
{
  private XMLStreamWriter fStreamWriter;
  private static final boolean DEBUG = false;
  
  public XMLEventWriterImpl(XMLStreamWriter paramXMLStreamWriter)
  {
    fStreamWriter = paramXMLStreamWriter;
  }
  
  public void add(XMLEventReader paramXMLEventReader)
    throws XMLStreamException
  {
    if (paramXMLEventReader == null) {
      throw new XMLStreamException("Event reader shouldn't be null");
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
      fStreamWriter.writeDTD(((DTD)localObject1).getDocumentTypeDeclaration());
      break;
    case 7: 
      localObject1 = (StartDocument)paramXMLEvent;
      try
      {
        fStreamWriter.writeStartDocument(((StartDocument)localObject1).getCharacterEncodingScheme(), ((StartDocument)localObject1).getVersion());
      }
      catch (XMLStreamException localXMLStreamException)
      {
        fStreamWriter.writeStartDocument(((StartDocument)localObject1).getVersion());
      }
    case 1: 
      localObject1 = paramXMLEvent.asStartElement();
      localQName1 = ((StartElement)localObject1).getName();
      fStreamWriter.writeStartElement(localQName1.getPrefix(), localQName1.getLocalPart(), localQName1.getNamespaceURI());
      Iterator localIterator = ((StartElement)localObject1).getNamespaces();
      while (localIterator.hasNext())
      {
        localObject2 = (Namespace)localIterator.next();
        fStreamWriter.writeNamespace(((Namespace)localObject2).getPrefix(), ((Namespace)localObject2).getNamespaceURI());
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
      while (((Iterator)localObject2).hasNext())
      {
        Attribute localAttribute = (Attribute)((Iterator)localObject2).next();
        QName localQName2 = localAttribute.getName();
        fStreamWriter.writeAttribute(localQName2.getPrefix(), localQName2.getNamespaceURI(), localQName2.getLocalPart(), localAttribute.getValue());
        continue;
        localObject1 = (Namespace)paramXMLEvent;
        fStreamWriter.writeNamespace(((Namespace)localObject1).getPrefix(), ((Namespace)localObject1).getNamespaceURI());
        break;
        localObject1 = (Comment)paramXMLEvent;
        fStreamWriter.writeComment(((Comment)localObject1).getText());
        break;
        localObject1 = (ProcessingInstruction)paramXMLEvent;
        fStreamWriter.writeProcessingInstruction(((ProcessingInstruction)localObject1).getTarget(), ((ProcessingInstruction)localObject1).getData());
        break;
        localObject1 = paramXMLEvent.asCharacters();
        if (((Characters)localObject1).isCData())
        {
          fStreamWriter.writeCData(((Characters)localObject1).getData());
        }
        else
        {
          fStreamWriter.writeCharacters(((Characters)localObject1).getData());
          break;
          localObject1 = (EntityReference)paramXMLEvent;
          fStreamWriter.writeEntityRef(((EntityReference)localObject1).getName());
          break;
          localObject1 = (Attribute)paramXMLEvent;
          localQName1 = ((Attribute)localObject1).getName();
          fStreamWriter.writeAttribute(localQName1.getPrefix(), localQName1.getNamespaceURI(), localQName1.getLocalPart(), ((Attribute)localObject1).getValue());
          break;
          localObject1 = (Characters)paramXMLEvent;
          if (((Characters)localObject1).isCData())
          {
            fStreamWriter.writeCData(((Characters)localObject1).getData());
            break;
            fStreamWriter.writeEndElement();
            break;
            fStreamWriter.writeEndDocument();
          }
        }
      }
    }
  }
  
  public void close()
    throws XMLStreamException
  {
    fStreamWriter.close();
  }
  
  public void flush()
    throws XMLStreamException
  {
    fStreamWriter.flush();
  }
  
  public NamespaceContext getNamespaceContext()
  {
    return fStreamWriter.getNamespaceContext();
  }
  
  public String getPrefix(String paramString)
    throws XMLStreamException
  {
    return fStreamWriter.getPrefix(paramString);
  }
  
  public void setDefaultNamespace(String paramString)
    throws XMLStreamException
  {
    fStreamWriter.setDefaultNamespace(paramString);
  }
  
  public void setNamespaceContext(NamespaceContext paramNamespaceContext)
    throws XMLStreamException
  {
    fStreamWriter.setNamespaceContext(paramNamespaceContext);
  }
  
  public void setPrefix(String paramString1, String paramString2)
    throws XMLStreamException
  {
    fStreamWriter.setPrefix(paramString1, paramString2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\writers\XMLEventWriterImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */