package com.sun.xml.internal.fastinfoset.stax.factory;

import com.sun.xml.internal.fastinfoset.stax.events.AttributeBase;
import com.sun.xml.internal.fastinfoset.stax.events.CharactersEvent;
import com.sun.xml.internal.fastinfoset.stax.events.CommentEvent;
import com.sun.xml.internal.fastinfoset.stax.events.DTDEvent;
import com.sun.xml.internal.fastinfoset.stax.events.EndDocumentEvent;
import com.sun.xml.internal.fastinfoset.stax.events.EndElementEvent;
import com.sun.xml.internal.fastinfoset.stax.events.EntityReferenceEvent;
import com.sun.xml.internal.fastinfoset.stax.events.NamespaceBase;
import com.sun.xml.internal.fastinfoset.stax.events.ProcessingInstructionEvent;
import com.sun.xml.internal.fastinfoset.stax.events.StartDocumentEvent;
import com.sun.xml.internal.fastinfoset.stax.events.StartElementEvent;
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

public class StAXEventFactory
  extends XMLEventFactory
{
  Location location = null;
  
  public StAXEventFactory() {}
  
  public void setLocation(Location paramLocation)
  {
    location = paramLocation;
  }
  
  public Attribute createAttribute(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    AttributeBase localAttributeBase = new AttributeBase(paramString1, paramString2, paramString3, paramString4, null);
    if (location != null) {
      localAttributeBase.setLocation(location);
    }
    return localAttributeBase;
  }
  
  public Attribute createAttribute(String paramString1, String paramString2)
  {
    AttributeBase localAttributeBase = new AttributeBase(paramString1, paramString2);
    if (location != null) {
      localAttributeBase.setLocation(location);
    }
    return localAttributeBase;
  }
  
  public Attribute createAttribute(QName paramQName, String paramString)
  {
    AttributeBase localAttributeBase = new AttributeBase(paramQName, paramString);
    if (location != null) {
      localAttributeBase.setLocation(location);
    }
    return localAttributeBase;
  }
  
  public Namespace createNamespace(String paramString)
  {
    NamespaceBase localNamespaceBase = new NamespaceBase(paramString);
    if (location != null) {
      localNamespaceBase.setLocation(location);
    }
    return localNamespaceBase;
  }
  
  public Namespace createNamespace(String paramString1, String paramString2)
  {
    NamespaceBase localNamespaceBase = new NamespaceBase(paramString1, paramString2);
    if (location != null) {
      localNamespaceBase.setLocation(location);
    }
    return localNamespaceBase;
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
    localStartElementEvent.addNamespaces(paramIterator2);
    localStartElementEvent.setNamespaceContext(paramNamespaceContext);
    if (location != null) {
      localStartElementEvent.setLocation(location);
    }
    return localStartElementEvent;
  }
  
  public EndElement createEndElement(QName paramQName, Iterator paramIterator)
  {
    return createEndElement(paramQName.getPrefix(), paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramIterator);
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
  
  public Characters createCharacters(String paramString)
  {
    CharactersEvent localCharactersEvent = new CharactersEvent(paramString);
    if (location != null) {
      localCharactersEvent.setLocation(location);
    }
    return localCharactersEvent;
  }
  
  public Characters createCData(String paramString)
  {
    CharactersEvent localCharactersEvent = new CharactersEvent(paramString, true);
    if (location != null) {
      localCharactersEvent.setLocation(location);
    }
    return localCharactersEvent;
  }
  
  public Characters createSpace(String paramString)
  {
    CharactersEvent localCharactersEvent = new CharactersEvent(paramString);
    localCharactersEvent.setSpace(true);
    if (location != null) {
      localCharactersEvent.setLocation(location);
    }
    return localCharactersEvent;
  }
  
  public Characters createIgnorableSpace(String paramString)
  {
    CharactersEvent localCharactersEvent = new CharactersEvent(paramString, false);
    localCharactersEvent.setSpace(true);
    localCharactersEvent.setIgnorable(true);
    if (location != null) {
      localCharactersEvent.setLocation(location);
    }
    return localCharactersEvent;
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
    StartDocumentEvent localStartDocumentEvent = new StartDocumentEvent(paramString1, paramString2);
    localStartDocumentEvent.setStandalone(paramBoolean);
    if (location != null) {
      localStartDocumentEvent.setLocation(location);
    }
    return localStartDocumentEvent;
  }
  
  public EndDocument createEndDocument()
  {
    EndDocumentEvent localEndDocumentEvent = new EndDocumentEvent();
    if (location != null) {
      localEndDocumentEvent.setLocation(location);
    }
    return localEndDocumentEvent;
  }
  
  public EntityReference createEntityReference(String paramString, EntityDeclaration paramEntityDeclaration)
  {
    EntityReferenceEvent localEntityReferenceEvent = new EntityReferenceEvent(paramString, paramEntityDeclaration);
    if (location != null) {
      localEntityReferenceEvent.setLocation(location);
    }
    return localEntityReferenceEvent;
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
  
  public ProcessingInstruction createProcessingInstruction(String paramString1, String paramString2)
  {
    ProcessingInstructionEvent localProcessingInstructionEvent = new ProcessingInstructionEvent(paramString1, paramString2);
    if (location != null) {
      localProcessingInstructionEvent.setLocation(location);
    }
    return localProcessingInstructionEvent;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\stax\factory\StAXEventFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */