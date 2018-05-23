package javax.xml.soap;

import java.util.Iterator;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

public abstract interface SOAPElement
  extends Node, Element
{
  public abstract SOAPElement addChildElement(Name paramName)
    throws SOAPException;
  
  public abstract SOAPElement addChildElement(QName paramQName)
    throws SOAPException;
  
  public abstract SOAPElement addChildElement(String paramString)
    throws SOAPException;
  
  public abstract SOAPElement addChildElement(String paramString1, String paramString2)
    throws SOAPException;
  
  public abstract SOAPElement addChildElement(String paramString1, String paramString2, String paramString3)
    throws SOAPException;
  
  public abstract SOAPElement addChildElement(SOAPElement paramSOAPElement)
    throws SOAPException;
  
  public abstract void removeContents();
  
  public abstract SOAPElement addTextNode(String paramString)
    throws SOAPException;
  
  public abstract SOAPElement addAttribute(Name paramName, String paramString)
    throws SOAPException;
  
  public abstract SOAPElement addAttribute(QName paramQName, String paramString)
    throws SOAPException;
  
  public abstract SOAPElement addNamespaceDeclaration(String paramString1, String paramString2)
    throws SOAPException;
  
  public abstract String getAttributeValue(Name paramName);
  
  public abstract String getAttributeValue(QName paramQName);
  
  public abstract Iterator getAllAttributes();
  
  public abstract Iterator getAllAttributesAsQNames();
  
  public abstract String getNamespaceURI(String paramString);
  
  public abstract Iterator getNamespacePrefixes();
  
  public abstract Iterator getVisibleNamespacePrefixes();
  
  public abstract QName createQName(String paramString1, String paramString2)
    throws SOAPException;
  
  public abstract Name getElementName();
  
  public abstract QName getElementQName();
  
  public abstract SOAPElement setElementQName(QName paramQName)
    throws SOAPException;
  
  public abstract boolean removeAttribute(Name paramName);
  
  public abstract boolean removeAttribute(QName paramQName);
  
  public abstract boolean removeNamespaceDeclaration(String paramString);
  
  public abstract Iterator getChildElements();
  
  public abstract Iterator getChildElements(Name paramName);
  
  public abstract Iterator getChildElements(QName paramQName);
  
  public abstract void setEncodingStyle(String paramString)
    throws SOAPException;
  
  public abstract String getEncodingStyle();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\soap\SOAPElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */