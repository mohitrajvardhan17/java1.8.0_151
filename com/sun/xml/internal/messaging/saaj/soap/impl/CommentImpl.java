package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public class CommentImpl
  extends com.sun.org.apache.xerces.internal.dom.CommentImpl
  implements javax.xml.soap.Text, Comment
{
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.impl", "com.sun.xml.internal.messaging.saaj.soap.impl.LocalStrings");
  protected static ResourceBundle rb = log.getResourceBundle();
  
  public CommentImpl(SOAPDocumentImpl paramSOAPDocumentImpl, String paramString)
  {
    super(paramSOAPDocumentImpl, paramString);
  }
  
  public String getValue()
  {
    String str = getNodeValue();
    return str.equals("") ? null : str;
  }
  
  public void setValue(String paramString)
  {
    setNodeValue(paramString);
  }
  
  public void setParentElement(SOAPElement paramSOAPElement)
    throws SOAPException
  {
    if (paramSOAPElement == null)
    {
      log.severe("SAAJ0112.impl.no.null.to.parent.elem");
      throw new SOAPException("Cannot pass NULL to setParentElement");
    }
    ((ElementImpl)paramSOAPElement).addNode(this);
  }
  
  public SOAPElement getParentElement()
  {
    return (SOAPElement)getParentNode();
  }
  
  public void detachNode()
  {
    Node localNode = getParentNode();
    if (localNode != null) {
      localNode.removeChild(this);
    }
  }
  
  public void recycleNode()
  {
    detachNode();
  }
  
  public boolean isComment()
  {
    return true;
  }
  
  public org.w3c.dom.Text splitText(int paramInt)
    throws DOMException
  {
    log.severe("SAAJ0113.impl.cannot.split.text.from.comment");
    throw new UnsupportedOperationException("Cannot split text from a Comment Node.");
  }
  
  public org.w3c.dom.Text replaceWholeText(String paramString)
    throws DOMException
  {
    log.severe("SAAJ0114.impl.cannot.replace.wholetext.from.comment");
    throw new UnsupportedOperationException("Cannot replace Whole Text from a Comment Node.");
  }
  
  public String getWholeText()
  {
    throw new UnsupportedOperationException("Not Supported");
  }
  
  public boolean isElementContentWhitespace()
  {
    throw new UnsupportedOperationException("Not Supported");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\impl\CommentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */