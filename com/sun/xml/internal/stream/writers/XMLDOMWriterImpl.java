package com.sun.xml.internal.stream.writers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.helpers.NamespaceSupport;

public class XMLDOMWriterImpl
  implements XMLStreamWriter
{
  private Document ownerDoc = null;
  private Node currentNode = null;
  private Node node = null;
  private NamespaceSupport namespaceContext = null;
  private Method mXmlVersion = null;
  private boolean[] needContextPop = null;
  private StringBuffer stringBuffer = null;
  private int resizeValue = 20;
  private int depth = 0;
  
  public XMLDOMWriterImpl(DOMResult paramDOMResult)
  {
    node = paramDOMResult.getNode();
    if (node.getNodeType() == 9)
    {
      ownerDoc = ((Document)node);
      currentNode = ownerDoc;
    }
    else
    {
      ownerDoc = node.getOwnerDocument();
      currentNode = node;
    }
    getDLThreeMethods();
    stringBuffer = new StringBuffer();
    needContextPop = new boolean[resizeValue];
    namespaceContext = new NamespaceSupport();
  }
  
  private void getDLThreeMethods()
  {
    try
    {
      mXmlVersion = ownerDoc.getClass().getMethod("setXmlVersion", new Class[] { String.class });
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      mXmlVersion = null;
    }
    catch (SecurityException localSecurityException)
    {
      mXmlVersion = null;
    }
  }
  
  public void close()
    throws XMLStreamException
  {}
  
  public void flush()
    throws XMLStreamException
  {}
  
  public NamespaceContext getNamespaceContext()
  {
    return null;
  }
  
  public String getPrefix(String paramString)
    throws XMLStreamException
  {
    String str = null;
    if (namespaceContext != null) {
      str = namespaceContext.getPrefix(paramString);
    }
    return str;
  }
  
  public Object getProperty(String paramString)
    throws IllegalArgumentException
  {
    throw new UnsupportedOperationException();
  }
  
  public void setDefaultNamespace(String paramString)
    throws XMLStreamException
  {
    namespaceContext.declarePrefix("", paramString);
    if (needContextPop[depth] == 0) {
      needContextPop[depth] = true;
    }
  }
  
  public void setNamespaceContext(NamespaceContext paramNamespaceContext)
    throws XMLStreamException
  {
    throw new UnsupportedOperationException();
  }
  
  public void setPrefix(String paramString1, String paramString2)
    throws XMLStreamException
  {
    if (paramString1 == null) {
      throw new XMLStreamException("Prefix cannot be null");
    }
    namespaceContext.declarePrefix(paramString1, paramString2);
    if (needContextPop[depth] == 0) {
      needContextPop[depth] = true;
    }
  }
  
  public void writeAttribute(String paramString1, String paramString2)
    throws XMLStreamException
  {
    if (currentNode.getNodeType() == 1)
    {
      Attr localAttr = ownerDoc.createAttribute(paramString1);
      localAttr.setValue(paramString2);
      ((Element)currentNode).setAttributeNode(localAttr);
    }
    else
    {
      throw new IllegalStateException("Current DOM Node type  is " + currentNode.getNodeType() + "and does not allow attributes to be set ");
    }
  }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3)
    throws XMLStreamException
  {
    if (currentNode.getNodeType() == 1)
    {
      String str1 = null;
      if (paramString1 == null) {
        throw new XMLStreamException("NamespaceURI cannot be null");
      }
      if (paramString2 == null) {
        throw new XMLStreamException("Local name cannot be null");
      }
      if (namespaceContext != null) {
        str1 = namespaceContext.getPrefix(paramString1);
      }
      if (str1 == null) {
        throw new XMLStreamException("Namespace URI " + paramString1 + "is not bound to any prefix");
      }
      String str2 = null;
      if (str1.equals("")) {
        str2 = paramString2;
      } else {
        str2 = getQName(str1, paramString2);
      }
      Attr localAttr = ownerDoc.createAttributeNS(paramString1, str2);
      localAttr.setValue(paramString3);
      ((Element)currentNode).setAttributeNode(localAttr);
    }
    else
    {
      throw new IllegalStateException("Current DOM Node type  is " + currentNode.getNodeType() + "and does not allow attributes to be set ");
    }
  }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3, String paramString4)
    throws XMLStreamException
  {
    if (currentNode.getNodeType() == 1)
    {
      if (paramString2 == null) {
        throw new XMLStreamException("NamespaceURI cannot be null");
      }
      if (paramString3 == null) {
        throw new XMLStreamException("Local name cannot be null");
      }
      if (paramString1 == null) {
        throw new XMLStreamException("prefix cannot be null");
      }
      String str = null;
      if (paramString1.equals("")) {
        str = paramString3;
      } else {
        str = getQName(paramString1, paramString3);
      }
      Attr localAttr = ownerDoc.createAttributeNS(paramString2, str);
      localAttr.setValue(paramString4);
      ((Element)currentNode).setAttributeNodeNS(localAttr);
    }
    else
    {
      throw new IllegalStateException("Current DOM Node type  is " + currentNode.getNodeType() + "and does not allow attributes to be set ");
    }
  }
  
  public void writeCData(String paramString)
    throws XMLStreamException
  {
    if (paramString == null) {
      throw new XMLStreamException("CDATA cannot be null");
    }
    CDATASection localCDATASection = ownerDoc.createCDATASection(paramString);
    getNode().appendChild(localCDATASection);
  }
  
  public void writeCharacters(String paramString)
    throws XMLStreamException
  {
    Text localText = ownerDoc.createTextNode(paramString);
    currentNode.appendChild(localText);
  }
  
  public void writeCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws XMLStreamException
  {
    Text localText = ownerDoc.createTextNode(new String(paramArrayOfChar, paramInt1, paramInt2));
    currentNode.appendChild(localText);
  }
  
  public void writeComment(String paramString)
    throws XMLStreamException
  {
    Comment localComment = ownerDoc.createComment(paramString);
    getNode().appendChild(localComment);
  }
  
  public void writeDTD(String paramString)
    throws XMLStreamException
  {
    throw new UnsupportedOperationException();
  }
  
  public void writeDefaultNamespace(String paramString)
    throws XMLStreamException
  {
    if (currentNode.getNodeType() == 1)
    {
      String str = "xmlns";
      ((Element)currentNode).setAttributeNS("http://www.w3.org/2000/xmlns/", str, paramString);
    }
    else
    {
      throw new IllegalStateException("Current DOM Node type  is " + currentNode.getNodeType() + "and does not allow attributes to be set ");
    }
  }
  
  public void writeEmptyElement(String paramString)
    throws XMLStreamException
  {
    if (ownerDoc != null)
    {
      Element localElement = ownerDoc.createElement(paramString);
      if (currentNode != null) {
        currentNode.appendChild(localElement);
      } else {
        ownerDoc.appendChild(localElement);
      }
    }
  }
  
  public void writeEmptyElement(String paramString1, String paramString2)
    throws XMLStreamException
  {
    if (ownerDoc != null)
    {
      String str1 = null;
      String str2 = null;
      if (paramString1 == null) {
        throw new XMLStreamException("NamespaceURI cannot be null");
      }
      if (paramString2 == null) {
        throw new XMLStreamException("Local name cannot be null");
      }
      if (namespaceContext != null) {
        str2 = namespaceContext.getPrefix(paramString1);
      }
      if (str2 == null) {
        throw new XMLStreamException("Namespace URI " + paramString1 + "is not bound to any prefix");
      }
      if ("".equals(str2)) {
        str1 = paramString2;
      } else {
        str1 = getQName(str2, paramString2);
      }
      Element localElement = ownerDoc.createElementNS(paramString1, str1);
      if (currentNode != null) {
        currentNode.appendChild(localElement);
      } else {
        ownerDoc.appendChild(localElement);
      }
    }
  }
  
  public void writeEmptyElement(String paramString1, String paramString2, String paramString3)
    throws XMLStreamException
  {
    if (ownerDoc != null)
    {
      if (paramString3 == null) {
        throw new XMLStreamException("NamespaceURI cannot be null");
      }
      if (paramString2 == null) {
        throw new XMLStreamException("Local name cannot be null");
      }
      if (paramString1 == null) {
        throw new XMLStreamException("Prefix cannot be null");
      }
      String str = null;
      if ("".equals(paramString1)) {
        str = paramString2;
      } else {
        str = getQName(paramString1, paramString2);
      }
      Element localElement = ownerDoc.createElementNS(paramString3, str);
      if (currentNode != null) {
        currentNode.appendChild(localElement);
      } else {
        ownerDoc.appendChild(localElement);
      }
    }
  }
  
  public void writeEndDocument()
    throws XMLStreamException
  {
    currentNode = null;
    for (int i = 0; i < depth; i++)
    {
      if (needContextPop[depth] != 0)
      {
        needContextPop[depth] = false;
        namespaceContext.popContext();
      }
      depth -= 1;
    }
    depth = 0;
  }
  
  public void writeEndElement()
    throws XMLStreamException
  {
    Node localNode = currentNode.getParentNode();
    if (currentNode.getNodeType() == 9) {
      currentNode = null;
    } else {
      currentNode = localNode;
    }
    if (needContextPop[depth] != 0)
    {
      needContextPop[depth] = false;
      namespaceContext.popContext();
    }
    depth -= 1;
  }
  
  public void writeEntityRef(String paramString)
    throws XMLStreamException
  {
    EntityReference localEntityReference = ownerDoc.createEntityReference(paramString);
    currentNode.appendChild(localEntityReference);
  }
  
  public void writeNamespace(String paramString1, String paramString2)
    throws XMLStreamException
  {
    if (paramString1 == null) {
      throw new XMLStreamException("prefix cannot be null");
    }
    if (paramString2 == null) {
      throw new XMLStreamException("NamespaceURI cannot be null");
    }
    String str = null;
    if (paramString1.equals("")) {
      str = "xmlns";
    } else {
      str = getQName("xmlns", paramString1);
    }
    ((Element)currentNode).setAttributeNS("http://www.w3.org/2000/xmlns/", str, paramString2);
  }
  
  public void writeProcessingInstruction(String paramString)
    throws XMLStreamException
  {
    if (paramString == null) {
      throw new XMLStreamException("Target cannot be null");
    }
    ProcessingInstruction localProcessingInstruction = ownerDoc.createProcessingInstruction(paramString, "");
    currentNode.appendChild(localProcessingInstruction);
  }
  
  public void writeProcessingInstruction(String paramString1, String paramString2)
    throws XMLStreamException
  {
    if (paramString1 == null) {
      throw new XMLStreamException("Target cannot be null");
    }
    ProcessingInstruction localProcessingInstruction = ownerDoc.createProcessingInstruction(paramString1, paramString2);
    currentNode.appendChild(localProcessingInstruction);
  }
  
  public void writeStartDocument()
    throws XMLStreamException
  {
    try
    {
      if (mXmlVersion != null) {
        mXmlVersion.invoke(ownerDoc, new Object[] { "1.0" });
      }
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new XMLStreamException(localIllegalAccessException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw new XMLStreamException(localInvocationTargetException);
    }
  }
  
  public void writeStartDocument(String paramString)
    throws XMLStreamException
  {
    try
    {
      if (mXmlVersion != null) {
        mXmlVersion.invoke(ownerDoc, new Object[] { paramString });
      }
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new XMLStreamException(localIllegalAccessException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw new XMLStreamException(localInvocationTargetException);
    }
  }
  
  public void writeStartDocument(String paramString1, String paramString2)
    throws XMLStreamException
  {
    try
    {
      if (mXmlVersion != null) {
        mXmlVersion.invoke(ownerDoc, new Object[] { paramString2 });
      }
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new XMLStreamException(localIllegalAccessException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw new XMLStreamException(localInvocationTargetException);
    }
  }
  
  public void writeStartElement(String paramString)
    throws XMLStreamException
  {
    if (ownerDoc != null)
    {
      Element localElement = ownerDoc.createElement(paramString);
      if (currentNode != null) {
        currentNode.appendChild(localElement);
      } else {
        ownerDoc.appendChild(localElement);
      }
      currentNode = localElement;
    }
    if (needContextPop[depth] != 0) {
      namespaceContext.pushContext();
    }
    incDepth();
  }
  
  public void writeStartElement(String paramString1, String paramString2)
    throws XMLStreamException
  {
    if (ownerDoc != null)
    {
      String str1 = null;
      String str2 = null;
      if (paramString1 == null) {
        throw new XMLStreamException("NamespaceURI cannot be null");
      }
      if (paramString2 == null) {
        throw new XMLStreamException("Local name cannot be null");
      }
      if (namespaceContext != null) {
        str2 = namespaceContext.getPrefix(paramString1);
      }
      if (str2 == null) {
        throw new XMLStreamException("Namespace URI " + paramString1 + "is not bound to any prefix");
      }
      if ("".equals(str2)) {
        str1 = paramString2;
      } else {
        str1 = getQName(str2, paramString2);
      }
      Element localElement = ownerDoc.createElementNS(paramString1, str1);
      if (currentNode != null) {
        currentNode.appendChild(localElement);
      } else {
        ownerDoc.appendChild(localElement);
      }
      currentNode = localElement;
    }
    if (needContextPop[depth] != 0) {
      namespaceContext.pushContext();
    }
    incDepth();
  }
  
  public void writeStartElement(String paramString1, String paramString2, String paramString3)
    throws XMLStreamException
  {
    if (ownerDoc != null)
    {
      String str = null;
      if (paramString3 == null) {
        throw new XMLStreamException("NamespaceURI cannot be null");
      }
      if (paramString2 == null) {
        throw new XMLStreamException("Local name cannot be null");
      }
      if (paramString1 == null) {
        throw new XMLStreamException("Prefix cannot be null");
      }
      if (paramString1.equals("")) {
        str = paramString2;
      } else {
        str = getQName(paramString1, paramString2);
      }
      Element localElement = ownerDoc.createElementNS(paramString3, str);
      if (currentNode != null) {
        currentNode.appendChild(localElement);
      } else {
        ownerDoc.appendChild(localElement);
      }
      currentNode = localElement;
      if (needContextPop[depth] != 0) {
        namespaceContext.pushContext();
      }
      incDepth();
    }
  }
  
  private String getQName(String paramString1, String paramString2)
  {
    stringBuffer.setLength(0);
    stringBuffer.append(paramString1);
    stringBuffer.append(":");
    stringBuffer.append(paramString2);
    return stringBuffer.toString();
  }
  
  private Node getNode()
  {
    if (currentNode == null) {
      return ownerDoc;
    }
    return currentNode;
  }
  
  private void incDepth()
  {
    depth += 1;
    if (depth == needContextPop.length)
    {
      boolean[] arrayOfBoolean = new boolean[depth + resizeValue];
      System.arraycopy(needContextPop, 0, arrayOfBoolean, 0, depth);
      needContextPop = arrayOfBoolean;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\writers\XMLDOMWriterImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */