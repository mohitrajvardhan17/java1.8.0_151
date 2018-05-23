package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;

public abstract class BodyImpl
  extends ElementImpl
  implements SOAPBody
{
  private SOAPFault fault;
  
  protected BodyImpl(SOAPDocumentImpl paramSOAPDocumentImpl, NameImpl paramNameImpl)
  {
    super(paramSOAPDocumentImpl, paramNameImpl);
  }
  
  protected abstract NameImpl getFaultName(String paramString);
  
  protected abstract boolean isFault(SOAPElement paramSOAPElement);
  
  protected abstract SOAPBodyElement createBodyElement(Name paramName);
  
  protected abstract SOAPBodyElement createBodyElement(QName paramQName);
  
  protected abstract SOAPFault createFaultElement();
  
  protected abstract QName getDefaultFaultCode();
  
  public SOAPFault addFault()
    throws SOAPException
  {
    if (hasFault())
    {
      log.severe("SAAJ0110.impl.fault.already.exists");
      throw new SOAPExceptionImpl("Error: Fault already exists");
    }
    fault = createFaultElement();
    addNode(fault);
    fault.setFaultCode(getDefaultFaultCode());
    fault.setFaultString("Fault string, and possibly fault code, not set");
    return fault;
  }
  
  public SOAPFault addFault(Name paramName, String paramString, Locale paramLocale)
    throws SOAPException
  {
    SOAPFault localSOAPFault = addFault();
    localSOAPFault.setFaultCode(paramName);
    localSOAPFault.setFaultString(paramString, paramLocale);
    return localSOAPFault;
  }
  
  public SOAPFault addFault(QName paramQName, String paramString, Locale paramLocale)
    throws SOAPException
  {
    SOAPFault localSOAPFault = addFault();
    localSOAPFault.setFaultCode(paramQName);
    localSOAPFault.setFaultString(paramString, paramLocale);
    return localSOAPFault;
  }
  
  public SOAPFault addFault(Name paramName, String paramString)
    throws SOAPException
  {
    SOAPFault localSOAPFault = addFault();
    localSOAPFault.setFaultCode(paramName);
    localSOAPFault.setFaultString(paramString);
    return localSOAPFault;
  }
  
  public SOAPFault addFault(QName paramQName, String paramString)
    throws SOAPException
  {
    SOAPFault localSOAPFault = addFault();
    localSOAPFault.setFaultCode(paramQName);
    localSOAPFault.setFaultString(paramString);
    return localSOAPFault;
  }
  
  void initializeFault()
  {
    FaultImpl localFaultImpl = (FaultImpl)findFault();
    fault = localFaultImpl;
  }
  
  protected SOAPElement findFault()
  {
    Iterator localIterator = getChildElementNodes();
    while (localIterator.hasNext())
    {
      SOAPElement localSOAPElement = (SOAPElement)localIterator.next();
      if (isFault(localSOAPElement)) {
        return localSOAPElement;
      }
    }
    return null;
  }
  
  public boolean hasFault()
  {
    initializeFault();
    return fault != null;
  }
  
  public SOAPFault getFault()
  {
    if (hasFault()) {
      return fault;
    }
    return null;
  }
  
  public SOAPBodyElement addBodyElement(Name paramName)
    throws SOAPException
  {
    SOAPBodyElement localSOAPBodyElement = (SOAPBodyElement)ElementFactory.createNamedElement(((SOAPDocument)getOwnerDocument()).getDocument(), paramName.getLocalName(), paramName.getPrefix(), paramName.getURI());
    if (localSOAPBodyElement == null) {
      localSOAPBodyElement = createBodyElement(paramName);
    }
    addNode(localSOAPBodyElement);
    return localSOAPBodyElement;
  }
  
  public SOAPBodyElement addBodyElement(QName paramQName)
    throws SOAPException
  {
    SOAPBodyElement localSOAPBodyElement = (SOAPBodyElement)ElementFactory.createNamedElement(((SOAPDocument)getOwnerDocument()).getDocument(), paramQName.getLocalPart(), paramQName.getPrefix(), paramQName.getNamespaceURI());
    if (localSOAPBodyElement == null) {
      localSOAPBodyElement = createBodyElement(paramQName);
    }
    addNode(localSOAPBodyElement);
    return localSOAPBodyElement;
  }
  
  public void setParentElement(SOAPElement paramSOAPElement)
    throws SOAPException
  {
    if (!(paramSOAPElement instanceof SOAPEnvelope))
    {
      log.severe("SAAJ0111.impl.body.parent.must.be.envelope");
      throw new SOAPException("Parent of SOAPBody has to be a SOAPEnvelope");
    }
    super.setParentElement(paramSOAPElement);
  }
  
  protected SOAPElement addElement(Name paramName)
    throws SOAPException
  {
    return addBodyElement(paramName);
  }
  
  protected SOAPElement addElement(QName paramQName)
    throws SOAPException
  {
    return addBodyElement(paramQName);
  }
  
  public SOAPBodyElement addDocument(Document paramDocument)
    throws SOAPException
  {
    SOAPBodyElement localSOAPBodyElement = null;
    DocumentFragment localDocumentFragment = paramDocument.createDocumentFragment();
    Element localElement = paramDocument.getDocumentElement();
    if (localElement != null)
    {
      localDocumentFragment.appendChild(localElement);
      Document localDocument = getOwnerDocument();
      org.w3c.dom.Node localNode = localDocument.importNode(localDocumentFragment, true);
      addNode(localNode);
      Iterator localIterator = getChildElements(NameImpl.copyElementName(localElement));
      while (localIterator.hasNext()) {
        localSOAPBodyElement = (SOAPBodyElement)localIterator.next();
      }
    }
    return localSOAPBodyElement;
  }
  
  protected SOAPElement convertToSoapElement(Element paramElement)
  {
    if (((paramElement instanceof SOAPBodyElement)) && (!paramElement.getClass().equals(ElementImpl.class))) {
      return (SOAPElement)paramElement;
    }
    return replaceElementWithSOAPElement(paramElement, (ElementImpl)createBodyElement(NameImpl.copyElementName(paramElement)));
  }
  
  public SOAPElement setElementQName(QName paramQName)
    throws SOAPException
  {
    log.log(Level.SEVERE, "SAAJ0146.impl.invalid.name.change.requested", new Object[] { elementQName.getLocalPart(), paramQName.getLocalPart() });
    throw new SOAPException("Cannot change name for " + elementQName.getLocalPart() + " to " + paramQName.getLocalPart());
  }
  
  public Document extractContentAsDocument()
    throws SOAPException
  {
    Iterator localIterator = getChildElements();
    for (javax.xml.soap.Node localNode = null; (localIterator.hasNext()) && (!(localNode instanceof SOAPElement)); localNode = (javax.xml.soap.Node)localIterator.next()) {}
    int i = 1;
    if (localNode == null) {
      i = 0;
    } else {
      for (localObject = localNode.getNextSibling(); localObject != null; localObject = ((org.w3c.dom.Node)localObject).getNextSibling()) {
        if ((localObject instanceof Element))
        {
          i = 0;
          break;
        }
      }
    }
    if (i == 0)
    {
      log.log(Level.SEVERE, "SAAJ0250.impl.body.should.have.exactly.one.child");
      throw new SOAPException("Cannot extract Document from body");
    }
    Object localObject = null;
    try
    {
      DocumentBuilderFactoryImpl localDocumentBuilderFactoryImpl = new DocumentBuilderFactoryImpl();
      localDocumentBuilderFactoryImpl.setNamespaceAware(true);
      DocumentBuilder localDocumentBuilder = localDocumentBuilderFactoryImpl.newDocumentBuilder();
      localObject = localDocumentBuilder.newDocument();
      Element localElement = (Element)((Document)localObject).importNode(localNode, true);
      ((Document)localObject).appendChild(localElement);
    }
    catch (Exception localException)
    {
      log.log(Level.SEVERE, "SAAJ0251.impl.cannot.extract.document.from.body");
      throw new SOAPExceptionImpl("Unable to extract Document from body", localException);
    }
    localNode.detachNode();
    return (Document)localObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\impl\BodyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */