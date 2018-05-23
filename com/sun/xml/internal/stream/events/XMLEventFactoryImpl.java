package com.sun.xml.internal.stream.events;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;

public class XMLEventFactoryImpl
  extends XMLEventFactory
{
  Location location = null;
  
  public XMLEventFactoryImpl() {}
  
  public Attribute createAttribute(String paramString1, String paramString2)
  {
    AttributeImpl localAttributeImpl = new AttributeImpl(paramString1, paramString2);
    if (location != null) {
      localAttributeImpl.setLocation(location);
    }
    return localAttributeImpl;
  }
  
  public Attribute createAttribute(QName paramQName, String paramString)
  {
    return createAttribute(paramQName.getPrefix(), paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramString);
  }
  
  public Attribute createAttribute(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    AttributeImpl localAttributeImpl = new AttributeImpl(paramString1, paramString2, paramString3, paramString4, null);
    if (location != null) {
      localAttributeImpl.setLocation(location);
    }
    return localAttributeImpl;
  }
  
  public Characters createCData(String paramString)
  {
    CharacterEvent localCharacterEvent = new CharacterEvent(paramString, true);
    if (location != null) {
      localCharacterEvent.setLocation(location);
    }
    return localCharacterEvent;
  }
  
  public Characters createCharacters(String paramString)
  {
    CharacterEvent localCharacterEvent = new CharacterEvent(paramString);
    if (location != null) {
      localCharacterEvent.setLocation(location);
    }
    return localCharacterEvent;
  }
  
  public Comment createComment(String paramString)
  {
    CommentEvent localCommentEvent = new CommentEvent(paramString);
    if (location != null) {
      localCommentEvent.setLocation(location);
    }
    return localCommentEvent;
  }
  
  public DTD createDTD(String paramString)
  {
    DTDEvent localDTDEvent = new DTDEvent(paramString);
    if (location != null) {
      localDTDEvent.setLocation(location);
    }
    return localDTDEvent;
  }
  
  public EndDocument createEndDocument()
  {
    EndDocumentEvent localEndDocumentEvent = new EndDocumentEvent();
    if (location != null) {
      localEndDocumentEvent.setLocation(location);
    }
    return localEndDocumentEvent;
  }
  
  public EndElement createEndElement(QName paramQName, Iterator paramIterator)
  {
    return createEndElement(paramQName.getPrefix(), paramQName.getNamespaceURI(), paramQName.getLocalPart());
  }
  
  public EndElement createEndElement(String paramString1, String paramString2, String paramString3)
  {
    EndElementEvent localEndElementEvent = new EndElementEvent(paramString1, paramString2, paramString3);
    if (location != null) {
      localEndElementEvent.setLocation(location);
    }
    return localEndElementEvent;
  }
  
  public EndElement createEndElement(String paramString1, String paramString2, String paramString3, Iterator paramIterator)
  {
    EndElementEvent localEndElementEvent = new EndElementEvent(paramString1, paramString2, paramString3);
    if (paramIterator != null) {
      while (paramIterator.hasNext()) {
        localEndElementEvent.addNamespace((Namespace)paramIterator.next());
      }
    }
    if (location != null) {
      localEndElementEvent.setLocation(location);
    }
    return localEndElementEvent;
  }
  
  public EntityReference createEntityReference(String paramString, EntityDeclaration paramEntityDeclaration)
  {
    EntityReferenceEvent localEntityReferenceEvent = new EntityReferenceEvent(paramString, paramEntityDeclaration);
    if (location != null) {
      localEntityReferenceEvent.setLocation(location);
    }
    return localEntityReferenceEvent;
  }
  
  public Characters createIgnorableSpace(String paramString)
  {
    CharacterEvent localCharacterEvent = new CharacterEvent(paramString, false, true);
    if (location != null) {
      localCharacterEvent.setLocation(location);
    }
    return localCharacterEvent;
  }
  
  public Namespace createNamespace(String paramString)
  {
    NamespaceImpl localNamespaceImpl = new NamespaceImpl(paramString);
    if (location != null) {
      localNamespaceImpl.setLocation(location);
    }
    return localNamespaceImpl;
  }
  
  public Namespace createNamespace(String paramString1, String paramString2)
  {
    NamespaceImpl localNamespaceImpl = new NamespaceImpl(paramString1, paramString2);
    if (location != null) {
      localNamespaceImpl.setLocation(location);
    }
    return localNamespaceImpl;
  }
  
  public ProcessingInstruction createProcessingInstruction(String paramString1, String paramString2)
  {
    ProcessingInstructionEvent localProcessingInstructionEvent = new ProcessingInstructionEvent(paramString1, paramString2);
    if (location != null) {
      localProcessingInstructionEvent.setLocation(location);
    }
    return localProcessingInstructionEvent;
  }
  
  public Characters createSpace(String paramString)
  {
    CharacterEvent localCharacterEvent = new CharacterEvent(paramString);
    if (location != null) {
      localCharacterEvent.setLocation(location);
    }
    return localCharacterEvent;
  }
  
  public StartDocument createStartDocument()
  {
    StartDocumentEvent localStartDocumentEvent = new StartDocumentEvent();
    if (location != null) {
      localStartDocumentEvent.setLocation(location);
    }
    return localStartDocumentEvent;
  }
  
  public StartDocument createStartDocument(String paramString)
  {
    StartDocumentEvent localStartDocumentEvent = new StartDocumentEvent(paramString);
    if (location != null) {
      localStartDocumentEvent.setLocation(location);
    }
    return localStartDocumentEvent;
  }
  
  public StartDocument createStartDocument(String paramString1, String paramString2)
  {
    StartDocumentEvent localStartDocumentEvent = new StartDocumentEvent(paramString1, paramString2);
    if (location != null) {
      localStartDocumentEvent.setLocation(location);
    }
    return localStartDocumentEvent;
  }
  
  public StartDocument createStartDocument(String paramString1, String paramString2, boolean paramBoolean)
  {
    StartDocumentEvent localStartDocumentEvent = new StartDocumentEvent(paramString1, paramString2, paramBoolean);
    if (location != null) {
      localStartDocumentEvent.setLocation(location);
    }
    return localStartDocumentEvent;
  }
  
  public StartElement createStartElement(QName paramQName, Iterator paramIterator1, Iterator paramIterator2)
  {
    return createStartElement(paramQName.getPrefix(), paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramIterator1, paramIterator2);
  }
  
  public StartElement createStartElement(String paramString1, String paramString2, String paramString3)
  {
    StartElementEvent localStartElementEvent = new StartElementEvent(paramString1, paramString2, paramString3);
    if (location != null) {
      localStartElementEvent.setLocation(location);
    }
    return localStartElementEvent;
  }
  
  public StartElement createStartElement(String paramString1, String paramString2, String paramString3, Iterator paramIterator1, Iterator paramIterator2)
  {
    return createStartElement(paramString1, paramString2, paramString3, paramIterator1, paramIterator2, null);
  }
  
  public StartElement createStartElement(String paramString1, String paramString2, String paramString3, Iterator paramIterator1, Iterator paramIterator2, NamespaceContext paramNamespaceContext)
  {
    StartElementEvent localStartElementEvent = new StartElementEvent(paramString1, paramString2, paramString3);
    localStartElementEvent.addAttributes(paramIterator1);
    localStartElementEvent.addNamespaceAttributes(paramIterator2);
    localStartElementEvent.setNamespaceContext(paramNamespaceContext);
    if (location != null) {
      localStartElementEvent.setLocation(location);
    }
    return localStartElementEvent;
  }
  
  public void setLocation(Location paramLocation)
  {
    location = paramLocation;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\events\XMLEventFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */