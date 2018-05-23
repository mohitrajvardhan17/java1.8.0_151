package com.sun.xml.internal.ws.api.message.saaj;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.EntityReference;
import org.w3c.dom.ProcessingInstruction;

public class SaajStaxWriter
  implements XMLStreamWriter
{
  protected SOAPMessage soap;
  protected String envURI;
  protected SOAPElement currentElement;
  protected DeferredElement deferredElement;
  protected static final String Envelope = "Envelope";
  protected static final String Header = "Header";
  protected static final String Body = "Body";
  protected static final String xmlns = "xmlns";
  
  public SaajStaxWriter(SOAPMessage paramSOAPMessage)
    throws SOAPException
  {
    soap = paramSOAPMessage;
    currentElement = soap.getSOAPPart().getEnvelope();
    envURI = currentElement.getNamespaceURI();
    deferredElement = new DeferredElement();
  }
  
  public SOAPMessage getSOAPMessage()
  {
    return soap;
  }
  
  public void writeStartElement(String paramString)
    throws XMLStreamException
  {
    currentElement = deferredElement.flushTo(currentElement);
    deferredElement.setLocalName(paramString);
  }
  
  public void writeStartElement(String paramString1, String paramString2)
    throws XMLStreamException
  {
    writeStartElement(null, paramString2, paramString1);
  }
  
  public void writeStartElement(String paramString1, String paramString2, String paramString3)
    throws XMLStreamException
  {
    currentElement = deferredElement.flushTo(currentElement);
    if (envURI.equals(paramString3)) {
      try
      {
        if ("Envelope".equals(paramString2))
        {
          currentElement = soap.getSOAPPart().getEnvelope();
          fixPrefix(paramString1);
          return;
        }
        if ("Header".equals(paramString2))
        {
          currentElement = soap.getSOAPHeader();
          fixPrefix(paramString1);
          return;
        }
        if ("Body".equals(paramString2))
        {
          currentElement = soap.getSOAPBody();
          fixPrefix(paramString1);
          return;
        }
      }
      catch (SOAPException localSOAPException)
      {
        throw new XMLStreamException(localSOAPException);
      }
    }
    deferredElement.setLocalName(paramString2);
    deferredElement.setNamespaceUri(paramString3);
    deferredElement.setPrefix(paramString1);
  }
  
  private void fixPrefix(String paramString)
    throws XMLStreamException
  {
    String str = currentElement.getPrefix();
    if ((paramString != null) && (!paramString.equals(str))) {
      currentElement.setPrefix(paramString);
    }
  }
  
  public void writeEmptyElement(String paramString1, String paramString2)
    throws XMLStreamException
  {
    writeStartElement(null, paramString2, paramString1);
  }
  
  public void writeEmptyElement(String paramString1, String paramString2, String paramString3)
    throws XMLStreamException
  {
    writeStartElement(paramString1, paramString2, paramString3);
  }
  
  public void writeEmptyElement(String paramString)
    throws XMLStreamException
  {
    writeStartElement(null, paramString, null);
  }
  
  public void writeEndElement()
    throws XMLStreamException
  {
    currentElement = deferredElement.flushTo(currentElement);
    if (currentElement != null) {
      currentElement = currentElement.getParentElement();
    }
  }
  
  public void writeEndDocument()
    throws XMLStreamException
  {
    currentElement = deferredElement.flushTo(currentElement);
  }
  
  public void close()
    throws XMLStreamException
  {}
  
  public void flush()
    throws XMLStreamException
  {}
  
  public void writeAttribute(String paramString1, String paramString2)
    throws XMLStreamException
  {
    writeAttribute(null, null, paramString1, paramString2);
  }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3, String paramString4)
    throws XMLStreamException
  {
    if ((paramString2 == null) && (paramString1 == null) && ("xmlns".equals(paramString3))) {
      writeNamespace("", paramString4);
    } else if (deferredElement.isInitialized()) {
      deferredElement.addAttribute(paramString1, paramString2, paramString3, paramString4);
    } else {
      addAttibuteToElement(currentElement, paramString1, paramString2, paramString3, paramString4);
    }
  }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3)
    throws XMLStreamException
  {
    writeAttribute(null, paramString1, paramString2, paramString3);
  }
  
  public void writeNamespace(String paramString1, String paramString2)
    throws XMLStreamException
  {
    String str = (paramString1 == null) || ("xmlns".equals(paramString1)) ? "" : paramString1;
    if (deferredElement.isInitialized()) {
      deferredElement.addNamespaceDeclaration(str, paramString2);
    } else {
      try
      {
        currentElement.addNamespaceDeclaration(str, paramString2);
      }
      catch (SOAPException localSOAPException)
      {
        throw new XMLStreamException(localSOAPException);
      }
    }
  }
  
  public void writeDefaultNamespace(String paramString)
    throws XMLStreamException
  {
    writeNamespace("", paramString);
  }
  
  public void writeComment(String paramString)
    throws XMLStreamException
  {
    currentElement = deferredElement.flushTo(currentElement);
    Comment localComment = soap.getSOAPPart().createComment(paramString);
    currentElement.appendChild(localComment);
  }
  
  public void writeProcessingInstruction(String paramString)
    throws XMLStreamException
  {
    currentElement = deferredElement.flushTo(currentElement);
    ProcessingInstruction localProcessingInstruction = soap.getSOAPPart().createProcessingInstruction(paramString, "");
    currentElement.appendChild(localProcessingInstruction);
  }
  
  public void writeProcessingInstruction(String paramString1, String paramString2)
    throws XMLStreamException
  {
    currentElement = deferredElement.flushTo(currentElement);
    ProcessingInstruction localProcessingInstruction = soap.getSOAPPart().createProcessingInstruction(paramString1, paramString2);
    currentElement.appendChild(localProcessingInstruction);
  }
  
  public void writeCData(String paramString)
    throws XMLStreamException
  {
    currentElement = deferredElement.flushTo(currentElement);
    CDATASection localCDATASection = soap.getSOAPPart().createCDATASection(paramString);
    currentElement.appendChild(localCDATASection);
  }
  
  public void writeDTD(String paramString)
    throws XMLStreamException
  {
    currentElement = deferredElement.flushTo(currentElement);
  }
  
  public void writeEntityRef(String paramString)
    throws XMLStreamException
  {
    currentElement = deferredElement.flushTo(currentElement);
    EntityReference localEntityReference = soap.getSOAPPart().createEntityReference(paramString);
    currentElement.appendChild(localEntityReference);
  }
  
  public void writeStartDocument()
    throws XMLStreamException
  {}
  
  public void writeStartDocument(String paramString)
    throws XMLStreamException
  {
    if (paramString != null) {
      soap.getSOAPPart().setXmlVersion(paramString);
    }
  }
  
  public void writeStartDocument(String paramString1, String paramString2)
    throws XMLStreamException
  {
    if (paramString2 != null) {
      soap.getSOAPPart().setXmlVersion(paramString2);
    }
    if (paramString1 != null) {
      try
      {
        soap.setProperty("javax.xml.soap.character-set-encoding", paramString1);
      }
      catch (SOAPException localSOAPException)
      {
        throw new XMLStreamException(localSOAPException);
      }
    }
  }
  
  public void writeCharacters(String paramString)
    throws XMLStreamException
  {
    currentElement = deferredElement.flushTo(currentElement);
    try
    {
      currentElement.addTextNode(paramString);
    }
    catch (SOAPException localSOAPException)
    {
      throw new XMLStreamException(localSOAPException);
    }
  }
  
  public void writeCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws XMLStreamException
  {
    currentElement = deferredElement.flushTo(currentElement);
    char[] arrayOfChar = (paramInt1 == 0) && (paramInt2 == paramArrayOfChar.length) ? paramArrayOfChar : Arrays.copyOfRange(paramArrayOfChar, paramInt1, paramInt1 + paramInt2);
    try
    {
      currentElement.addTextNode(new String(arrayOfChar));
    }
    catch (SOAPException localSOAPException)
    {
      throw new XMLStreamException(localSOAPException);
    }
  }
  
  public String getPrefix(String paramString)
    throws XMLStreamException
  {
    return currentElement.lookupPrefix(paramString);
  }
  
  public void setPrefix(String paramString1, String paramString2)
    throws XMLStreamException
  {
    if (deferredElement.isInitialized()) {
      deferredElement.addNamespaceDeclaration(paramString1, paramString2);
    } else {
      throw new XMLStreamException("Namespace not associated with any element");
    }
  }
  
  public void setDefaultNamespace(String paramString)
    throws XMLStreamException
  {
    setPrefix("", paramString);
  }
  
  public void setNamespaceContext(NamespaceContext paramNamespaceContext)
    throws XMLStreamException
  {
    throw new UnsupportedOperationException();
  }
  
  public Object getProperty(String paramString)
    throws IllegalArgumentException
  {
    if ("javax.xml.stream.isRepairingNamespaces".equals(paramString)) {
      return Boolean.FALSE;
    }
    return null;
  }
  
  public NamespaceContext getNamespaceContext()
  {
    new NamespaceContext()
    {
      public String getNamespaceURI(String paramAnonymousString)
      {
        return currentElement.getNamespaceURI(paramAnonymousString);
      }
      
      public String getPrefix(String paramAnonymousString)
      {
        return currentElement.lookupPrefix(paramAnonymousString);
      }
      
      public Iterator getPrefixes(final String paramAnonymousString)
      {
        new Iterator()
        {
          String prefix = getPrefix(paramAnonymousString);
          
          public boolean hasNext()
          {
            return prefix != null;
          }
          
          public String next()
          {
            if (!hasNext()) {
              throw new NoSuchElementException();
            }
            String str = prefix;
            prefix = null;
            return str;
          }
          
          public void remove() {}
        };
      }
    };
  }
  
  static void addAttibuteToElement(SOAPElement paramSOAPElement, String paramString1, String paramString2, String paramString3, String paramString4)
    throws XMLStreamException
  {
    try
    {
      if (paramString2 == null)
      {
        paramSOAPElement.setAttributeNS("", paramString3, paramString4);
      }
      else
      {
        QName localQName = paramString1 == null ? new QName(paramString2, paramString3) : new QName(paramString2, paramString3, paramString1);
        paramSOAPElement.addAttribute(localQName, paramString4);
      }
    }
    catch (SOAPException localSOAPException)
    {
      throw new XMLStreamException(localSOAPException);
    }
  }
  
  static class AttributeDeclaration
  {
    final String prefix;
    final String namespaceUri;
    final String localName;
    final String value;
    
    AttributeDeclaration(String paramString1, String paramString2, String paramString3, String paramString4)
    {
      prefix = paramString1;
      namespaceUri = paramString2;
      localName = paramString3;
      value = paramString4;
    }
  }
  
  static class DeferredElement
  {
    private String prefix;
    private String localName;
    private String namespaceUri;
    private final List<SaajStaxWriter.NamespaceDeclaration> namespaceDeclarations = new LinkedList();
    private final List<SaajStaxWriter.AttributeDeclaration> attributeDeclarations = new LinkedList();
    
    DeferredElement()
    {
      reset();
    }
    
    public void setPrefix(String paramString)
    {
      prefix = paramString;
    }
    
    public void setLocalName(String paramString)
    {
      if (paramString == null) {
        throw new IllegalArgumentException("localName can not be null");
      }
      localName = paramString;
    }
    
    public void setNamespaceUri(String paramString)
    {
      namespaceUri = paramString;
    }
    
    public void addNamespaceDeclaration(String paramString1, String paramString2)
    {
      if ((null == namespaceUri) && (null != paramString2) && (paramString1.equals(emptyIfNull(prefix)))) {
        namespaceUri = paramString2;
      }
      namespaceDeclarations.add(new SaajStaxWriter.NamespaceDeclaration(paramString1, paramString2));
    }
    
    public void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4)
    {
      if ((paramString2 == null) && (paramString1 == null) && ("xmlns".equals(paramString3))) {
        addNamespaceDeclaration(paramString1, paramString4);
      } else {
        attributeDeclarations.add(new SaajStaxWriter.AttributeDeclaration(paramString1, paramString2, paramString3, paramString4));
      }
    }
    
    public SOAPElement flushTo(SOAPElement paramSOAPElement)
      throws XMLStreamException
    {
      try
      {
        if (localName != null)
        {
          SOAPElement localSOAPElement;
          if (namespaceUri == null) {
            localSOAPElement = paramSOAPElement.addChildElement(localName);
          } else if (prefix == null) {
            localSOAPElement = paramSOAPElement.addChildElement(new QName(namespaceUri, localName));
          } else {
            localSOAPElement = paramSOAPElement.addChildElement(localName, prefix, namespaceUri);
          }
          Iterator localIterator = namespaceDeclarations.iterator();
          Object localObject;
          while (localIterator.hasNext())
          {
            localObject = (SaajStaxWriter.NamespaceDeclaration)localIterator.next();
            paramSOAPElement.addNamespaceDeclaration(prefix, namespaceUri);
          }
          localIterator = attributeDeclarations.iterator();
          while (localIterator.hasNext())
          {
            localObject = (SaajStaxWriter.AttributeDeclaration)localIterator.next();
            SaajStaxWriter.addAttibuteToElement(localSOAPElement, prefix, namespaceUri, localName, value);
          }
          reset();
          return localSOAPElement;
        }
        return paramSOAPElement;
      }
      catch (SOAPException localSOAPException)
      {
        throw new XMLStreamException(localSOAPException);
      }
    }
    
    public boolean isInitialized()
    {
      return localName != null;
    }
    
    private void reset()
    {
      localName = null;
      prefix = null;
      namespaceUri = null;
      namespaceDeclarations.clear();
      attributeDeclarations.clear();
    }
    
    private static String emptyIfNull(String paramString)
    {
      return paramString == null ? "" : paramString;
    }
  }
  
  static class NamespaceDeclaration
  {
    final String prefix;
    final String namespaceUri;
    
    NamespaceDeclaration(String paramString1, String paramString2)
    {
      prefix = paramString1;
      namespaceUri = paramString2;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\message\saaj\SaajStaxWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */