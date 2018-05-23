package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.DetailImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.FaultElementImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.FaultImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFaultElement;

public class Fault1_1Impl
  extends FaultImpl
{
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.ver1_1", "com.sun.xml.internal.messaging.saaj.soap.ver1_1.LocalStrings");
  
  public Fault1_1Impl(SOAPDocumentImpl paramSOAPDocumentImpl, String paramString)
  {
    super(paramSOAPDocumentImpl, NameImpl.createFault1_1Name(paramString));
  }
  
  protected NameImpl getDetailName()
  {
    return NameImpl.createDetail1_1Name();
  }
  
  protected NameImpl getFaultCodeName()
  {
    return NameImpl.createFromUnqualifiedName("faultcode");
  }
  
  protected NameImpl getFaultStringName()
  {
    return NameImpl.createFromUnqualifiedName("faultstring");
  }
  
  protected NameImpl getFaultActorName()
  {
    return NameImpl.createFromUnqualifiedName("faultactor");
  }
  
  protected DetailImpl createDetail()
  {
    return new Detail1_1Impl(((SOAPDocument)getOwnerDocument()).getDocument());
  }
  
  protected FaultElementImpl createSOAPFaultElement(String paramString)
  {
    return new FaultElement1_1Impl(((SOAPDocument)getOwnerDocument()).getDocument(), paramString);
  }
  
  protected void checkIfStandardFaultCode(String paramString1, String paramString2)
    throws SOAPException
  {}
  
  protected void finallySetFaultCode(String paramString)
    throws SOAPException
  {
    faultCodeElement.addTextNode(paramString);
  }
  
  public String getFaultCode()
  {
    if (faultCodeElement == null) {
      findFaultCodeElement();
    }
    return faultCodeElement.getValue();
  }
  
  public Name getFaultCodeAsName()
  {
    String str1 = getFaultCode();
    if (str1 == null) {
      return null;
    }
    int i = str1.indexOf(':');
    if (i == -1) {
      return NameImpl.createFromUnqualifiedName(str1);
    }
    String str2 = str1.substring(0, i);
    if (faultCodeElement == null) {
      findFaultCodeElement();
    }
    String str3 = faultCodeElement.getNamespaceURI(str2);
    return NameImpl.createFromQualifiedName(str1, str3);
  }
  
  public QName getFaultCodeAsQName()
  {
    String str = getFaultCode();
    if (str == null) {
      return null;
    }
    if (faultCodeElement == null) {
      findFaultCodeElement();
    }
    return convertCodeToQName(str, faultCodeElement);
  }
  
  public void setFaultString(String paramString)
    throws SOAPException
  {
    if (faultStringElement == null) {
      findFaultStringElement();
    }
    if (faultStringElement == null)
    {
      faultStringElement = addSOAPFaultElement("faultstring");
    }
    else
    {
      faultStringElement.removeContents();
      faultStringElement.removeAttribute("xml:lang");
    }
    faultStringElement.addTextNode(paramString);
  }
  
  public String getFaultString()
  {
    if (faultStringElement == null) {
      findFaultStringElement();
    }
    return faultStringElement.getValue();
  }
  
  public Locale getFaultStringLocale()
  {
    if (faultStringElement == null) {
      findFaultStringElement();
    }
    if (faultStringElement != null)
    {
      String str = faultStringElement.getAttributeValue(NameImpl.createFromUnqualifiedName("xml:lang"));
      if (str != null) {
        return xmlLangToLocale(str);
      }
    }
    return null;
  }
  
  public void setFaultString(String paramString, Locale paramLocale)
    throws SOAPException
  {
    setFaultString(paramString);
    faultStringElement.addAttribute(NameImpl.createFromTagName("xml:lang"), localeToXmlLang(paramLocale));
  }
  
  protected boolean isStandardFaultElement(String paramString)
  {
    return (paramString.equalsIgnoreCase("detail")) || (paramString.equalsIgnoreCase("faultcode")) || (paramString.equalsIgnoreCase("faultstring")) || (paramString.equalsIgnoreCase("faultactor"));
  }
  
  public void appendFaultSubcode(QName paramQName)
  {
    log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "appendFaultSubcode");
    throw new UnsupportedOperationException("Not supported in SOAP 1.1");
  }
  
  public void removeAllFaultSubcodes()
  {
    log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "removeAllFaultSubcodes");
    throw new UnsupportedOperationException("Not supported in SOAP 1.1");
  }
  
  public Iterator getFaultSubcodes()
  {
    log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "getFaultSubcodes");
    throw new UnsupportedOperationException("Not supported in SOAP 1.1");
  }
  
  public String getFaultReasonText(Locale paramLocale)
  {
    log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "getFaultReasonText");
    throw new UnsupportedOperationException("Not supported in SOAP 1.1");
  }
  
  public Iterator getFaultReasonTexts()
  {
    log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "getFaultReasonTexts");
    throw new UnsupportedOperationException("Not supported in SOAP 1.1");
  }
  
  public Iterator getFaultReasonLocales()
  {
    log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "getFaultReasonLocales");
    throw new UnsupportedOperationException("Not supported in SOAP 1.1");
  }
  
  public void addFaultReasonText(String paramString, Locale paramLocale)
    throws SOAPException
  {
    log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "addFaultReasonText");
    throw new UnsupportedOperationException("Not supported in SOAP 1.1");
  }
  
  public String getFaultRole()
  {
    log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "getFaultRole");
    throw new UnsupportedOperationException("Not supported in SOAP 1.1");
  }
  
  public void setFaultRole(String paramString)
  {
    log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "setFaultRole");
    throw new UnsupportedOperationException("Not supported in SOAP 1.1");
  }
  
  public String getFaultNode()
  {
    log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "getFaultNode");
    throw new UnsupportedOperationException("Not supported in SOAP 1.1");
  }
  
  public void setFaultNode(String paramString)
  {
    log.log(Level.SEVERE, "SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", "setFaultNode");
    throw new UnsupportedOperationException("Not supported in SOAP 1.1");
  }
  
  protected QName getDefaultFaultCode()
  {
    return new QName("http://schemas.xmlsoap.org/soap/envelope/", "Server");
  }
  
  public SOAPElement addChildElement(SOAPElement paramSOAPElement)
    throws SOAPException
  {
    String str = paramSOAPElement.getLocalName();
    if (("Detail".equalsIgnoreCase(str)) && (hasDetail()))
    {
      log.severe("SAAJ0305.ver1_2.detail.exists.error");
      throw new SOAPExceptionImpl("Cannot add Detail, Detail already exists");
    }
    return super.addChildElement(paramSOAPElement);
  }
  
  protected FaultElementImpl createSOAPFaultElement(QName paramQName)
  {
    return new FaultElement1_1Impl(((SOAPDocument)getOwnerDocument()).getDocument(), paramQName);
  }
  
  protected FaultElementImpl createSOAPFaultElement(Name paramName)
  {
    return new FaultElement1_1Impl(((SOAPDocument)getOwnerDocument()).getDocument(), (NameImpl)paramName);
  }
  
  public void setFaultCode(String paramString1, String paramString2, String paramString3)
    throws SOAPException
  {
    if (((paramString2 == null) || ("".equals(paramString2))) && (paramString3 != null) && (!"".equals(paramString3)))
    {
      paramString2 = getNamespacePrefix(paramString3);
      if ((paramString2 == null) || ("".equals(paramString2))) {
        paramString2 = "ns0";
      }
    }
    if (faultCodeElement == null) {
      findFaultCodeElement();
    }
    if (faultCodeElement == null) {
      faultCodeElement = addFaultCodeElement();
    } else {
      faultCodeElement.removeContents();
    }
    if (((paramString3 == null) || ("".equals(paramString3))) && (paramString2 != null) && (!"".equals("prefix"))) {
      paramString3 = faultCodeElement.getNamespaceURI(paramString2);
    }
    if ((paramString3 == null) || ("".equals(paramString3)))
    {
      if ((paramString2 != null) && (!"".equals(paramString2)))
      {
        log.log(Level.SEVERE, "SAAJ0307.impl.no.ns.URI", new Object[] { paramString2 + ":" + paramString1 });
        throw new SOAPExceptionImpl("Empty/Null NamespaceURI specified for faultCode \"" + paramString2 + ":" + paramString1 + "\"");
      }
      paramString3 = "";
    }
    checkIfStandardFaultCode(paramString1, paramString3);
    ((FaultElementImpl)faultCodeElement).ensureNamespaceIsDeclared(paramString2, paramString3);
    if ((paramString2 == null) || ("".equals(paramString2))) {
      finallySetFaultCode(paramString1);
    } else {
      finallySetFaultCode(paramString2 + ":" + paramString1);
    }
  }
  
  private boolean standardFaultCode(String paramString)
  {
    if ((paramString.equals("VersionMismatch")) || (paramString.equals("MustUnderstand")) || (paramString.equals("Client")) || (paramString.equals("Server"))) {
      return true;
    }
    return (paramString.startsWith("VersionMismatch.")) || (paramString.startsWith("MustUnderstand.")) || (paramString.startsWith("Client.")) || (paramString.startsWith("Server."));
  }
  
  public void setFaultActor(String paramString)
    throws SOAPException
  {
    if (faultActorElement == null) {
      findFaultActorElement();
    }
    if (faultActorElement != null) {
      faultActorElement.detachNode();
    }
    if (paramString == null) {
      return;
    }
    faultActorElement = createSOAPFaultElement(getFaultActorName());
    faultActorElement.addTextNode(paramString);
    if (hasDetail())
    {
      insertBefore(faultActorElement, detail);
      return;
    }
    addNode(faultActorElement);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\ver1_1\Fault1_1Impl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */