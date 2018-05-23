package com.sun.org.apache.xml.internal.utils;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @deprecated
 */
public class DOM2Helper
  extends DOMHelper
{
  private Document m_doc;
  
  public DOM2Helper() {}
  
  public void checkNode(Node paramNode)
    throws TransformerException
  {}
  
  public boolean supportsSAX()
  {
    return true;
  }
  
  public void setDocument(Document paramDocument)
  {
    m_doc = paramDocument;
  }
  
  public Document getDocument()
  {
    return m_doc;
  }
  
  public void parse(InputSource paramInputSource)
    throws TransformerException
  {
    try
    {
      DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
      localDocumentBuilderFactory.setNamespaceAware(true);
      localDocumentBuilderFactory.setValidating(true);
      DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
      localDocumentBuilder.setErrorHandler(new DefaultErrorHandler());
      setDocument(localDocumentBuilder.parse(paramInputSource));
    }
    catch (SAXException localSAXException)
    {
      throw new TransformerException(localSAXException);
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      throw new TransformerException(localParserConfigurationException);
    }
    catch (IOException localIOException)
    {
      throw new TransformerException(localIOException);
    }
  }
  
  public Element getElementByID(String paramString, Document paramDocument)
  {
    return paramDocument.getElementById(paramString);
  }
  
  public static boolean isNodeAfter(Node paramNode1, Node paramNode2)
  {
    if (((paramNode1 instanceof DOMOrder)) && ((paramNode2 instanceof DOMOrder)))
    {
      int i = ((DOMOrder)paramNode1).getUid();
      int j = ((DOMOrder)paramNode2).getUid();
      return i <= j;
    }
    return DOMHelper.isNodeAfter(paramNode1, paramNode2);
  }
  
  public static Node getParentOfNode(Node paramNode)
  {
    Object localObject = paramNode.getParentNode();
    if ((localObject == null) && (2 == paramNode.getNodeType())) {
      localObject = ((Attr)paramNode).getOwnerElement();
    }
    return (Node)localObject;
  }
  
  public String getLocalNameOfNode(Node paramNode)
  {
    String str = paramNode.getLocalName();
    return null == str ? super.getLocalNameOfNode(paramNode) : str;
  }
  
  public String getNamespaceOfNode(Node paramNode)
  {
    return paramNode.getNamespaceURI();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\DOM2Helper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */