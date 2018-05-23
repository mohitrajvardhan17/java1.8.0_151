package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.DetailImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.ElementImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.FaultElementImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.FaultImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFaultElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Fault1_2Impl
  extends FaultImpl
{
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.ver1_2", "com.sun.xml.internal.messaging.saaj.soap.ver1_2.LocalStrings");
  private static final QName textName = new QName("http://www.w3.org/2003/05/soap-envelope", "Text");
  private final QName valueName = new QName("http://www.w3.org/2003/05/soap-envelope", "Value", getPrefix());
  private final QName subcodeName = new QName("http://www.w3.org/2003/05/soap-envelope", "Subcode", getPrefix());
  private SOAPElement innermostSubCodeElement = null;
  
  public Fault1_2Impl(SOAPDocumentImpl paramSOAPDocumentImpl, String paramString1, String paramString2)
  {
    super(paramSOAPDocumentImpl, NameImpl.createFault1_2Name(paramString1, paramString2));
  }
  
  public Fault1_2Impl(SOAPDocumentImpl paramSOAPDocumentImpl, String paramString)
  {
    super(paramSOAPDocumentImpl, NameImpl.createFault1_2Name(null, paramString));
  }
  
  protected NameImpl getDetailName()
  {
    return NameImpl.createSOAP12Name("Detail", getPrefix());
  }
  
  protected NameImpl getFaultCodeName()
  {
    return NameImpl.createSOAP12Name("Code", getPrefix());
  }
  
  protected NameImpl getFaultStringName()
  {
    return getFaultReasonName();
  }
  
  protected NameImpl getFaultActorName()
  {
    return getFaultRoleName();
  }
  
  private NameImpl getFaultRoleName()
  {
    return NameImpl.createSOAP12Name("Role", getPrefix());
  }
  
  private NameImpl getFaultReasonName()
  {
    return NameImpl.createSOAP12Name("Reason", getPrefix());
  }
  
  private NameImpl getFaultReasonTextName()
  {
    return NameImpl.createSOAP12Name("Text", getPrefix());
  }
  
  private NameImpl getFaultNodeName()
  {
    return NameImpl.createSOAP12Name("Node", getPrefix());
  }
  
  private static NameImpl getXmlLangName()
  {
    return NameImpl.createXmlName("lang");
  }
  
  protected DetailImpl createDetail()
  {
    return new Detail1_2Impl(((SOAPDocument)getOwnerDocument()).getDocument());
  }
  
  protected FaultElementImpl createSOAPFaultElement(String paramString)
  {
    return new FaultElement1_2Impl(((SOAPDocument)getOwnerDocument()).getDocument(), paramString);
  }
  
  protected void checkIfStandardFaultCode(String paramString1, String paramString2)
    throws SOAPException
  {
    QName localQName = new QName(paramString2, paramString1);
    if ((SOAPConstants.SOAP_DATAENCODINGUNKNOWN_FAULT.equals(localQName)) || (SOAPConstants.SOAP_MUSTUNDERSTAND_FAULT.equals(localQName)) || (SOAPConstants.SOAP_RECEIVER_FAULT.equals(localQName)) || (SOAPConstants.SOAP_SENDER_FAULT.equals(localQName)) || (SOAPConstants.SOAP_VERSIONMISMATCH_FAULT.equals(localQName))) {
      return;
    }
    log.log(Level.SEVERE, "SAAJ0435.ver1_2.code.not.standard", localQName);
    throw new SOAPExceptionImpl(localQName + " is not a standard Code value");
  }
  
  protected void finallySetFaultCode(String paramString)
    throws SOAPException
  {
    SOAPElement localSOAPElement = faultCodeElement.addChildElement(valueName);
    localSOAPElement.addTextNode(paramString);
  }
  
  private void findReasonElement()
  {
    findFaultStringElement();
  }
  
  public Iterator getFaultReasonTexts()
    throws SOAPException
  {
    if (faultStringElement == null) {
      findReasonElement();
    }
    Iterator localIterator = faultStringElement.getChildElements(textName);
    ArrayList localArrayList = new ArrayList();
    while (localIterator.hasNext())
    {
      SOAPElement localSOAPElement = (SOAPElement)localIterator.next();
      Locale localLocale = getLocale(localSOAPElement);
      if (localLocale == null)
      {
        log.severe("SAAJ0431.ver1_2.xml.lang.missing");
        throw new SOAPExceptionImpl("\"xml:lang\" attribute is not present on the Text element");
      }
      localArrayList.add(localSOAPElement.getValue());
    }
    if (localArrayList.isEmpty())
    {
      log.severe("SAAJ0434.ver1_2.text.element.not.present");
      throw new SOAPExceptionImpl("env:Text must be present inside env:Reason");
    }
    return localArrayList.iterator();
  }
  
  public void addFaultReasonText(String paramString, Locale paramLocale)
    throws SOAPException
  {
    if (paramLocale == null)
    {
      log.severe("SAAJ0430.ver1_2.locale.required");
      throw new SOAPException("locale is required and must not be null");
    }
    if (faultStringElement == null) {
      findReasonElement();
    }
    SOAPElement localSOAPElement;
    if (faultStringElement == null)
    {
      faultStringElement = addSOAPFaultElement("Reason");
      localSOAPElement = faultStringElement.addChildElement(getFaultReasonTextName());
    }
    else
    {
      removeDefaultFaultString();
      localSOAPElement = getFaultReasonTextElement(paramLocale);
      if (localSOAPElement != null) {
        localSOAPElement.removeContents();
      } else {
        localSOAPElement = faultStringElement.addChildElement(getFaultReasonTextName());
      }
    }
    String str = localeToXmlLang(paramLocale);
    localSOAPElement.addAttribute(getXmlLangName(), str);
    localSOAPElement.addTextNode(paramString);
  }
  
  private void removeDefaultFaultString()
    throws SOAPException
  {
    SOAPElement localSOAPElement = getFaultReasonTextElement(Locale.getDefault());
    if (localSOAPElement != null)
    {
      String str = "Fault string, and possibly fault code, not set";
      if (str.equals(localSOAPElement.getValue())) {
        localSOAPElement.detachNode();
      }
    }
  }
  
  public String getFaultReasonText(Locale paramLocale)
    throws SOAPException
  {
    if (paramLocale == null) {
      return null;
    }
    if (faultStringElement == null) {
      findReasonElement();
    }
    if (faultStringElement != null)
    {
      SOAPElement localSOAPElement = getFaultReasonTextElement(paramLocale);
      if (localSOAPElement != null)
      {
        localSOAPElement.normalize();
        return localSOAPElement.getFirstChild().getNodeValue();
      }
    }
    return null;
  }
  
  public Iterator getFaultReasonLocales()
    throws SOAPException
  {
    if (faultStringElement == null) {
      findReasonElement();
    }
    Iterator localIterator = faultStringElement.getChildElements(textName);
    ArrayList localArrayList = new ArrayList();
    while (localIterator.hasNext())
    {
      SOAPElement localSOAPElement = (SOAPElement)localIterator.next();
      Locale localLocale = getLocale(localSOAPElement);
      if (localLocale == null)
      {
        log.severe("SAAJ0431.ver1_2.xml.lang.missing");
        throw new SOAPExceptionImpl("\"xml:lang\" attribute is not present on the Text element");
      }
      localArrayList.add(localLocale);
    }
    if (localArrayList.isEmpty())
    {
      log.severe("SAAJ0434.ver1_2.text.element.not.present");
      throw new SOAPExceptionImpl("env:Text elements with mandatory xml:lang attributes must be present inside env:Reason");
    }
    return localArrayList.iterator();
  }
  
  public Locale getFaultStringLocale()
  {
    Locale localLocale = null;
    try
    {
      localLocale = (Locale)getFaultReasonLocales().next();
    }
    catch (SOAPException localSOAPException) {}
    return localLocale;
  }
  
  private SOAPElement getFaultReasonTextElement(Locale paramLocale)
    throws SOAPException
  {
    Iterator localIterator = faultStringElement.getChildElements(textName);
    while (localIterator.hasNext())
    {
      SOAPElement localSOAPElement = (SOAPElement)localIterator.next();
      Locale localLocale = getLocale(localSOAPElement);
      if (localLocale == null)
      {
        log.severe("SAAJ0431.ver1_2.xml.lang.missing");
        throw new SOAPExceptionImpl("\"xml:lang\" attribute is not present on the Text element");
      }
      if (localLocale.equals(paramLocale)) {
        return localSOAPElement;
      }
    }
    return null;
  }
  
  public String getFaultNode()
  {
    SOAPElement localSOAPElement = findChild(getFaultNodeName());
    if (localSOAPElement == null) {
      return null;
    }
    return localSOAPElement.getValue();
  }
  
  public void setFaultNode(String paramString)
    throws SOAPException
  {
    Object localObject = findChild(getFaultNodeName());
    if (localObject != null) {
      ((SOAPElement)localObject).detachNode();
    }
    localObject = createSOAPFaultElement(getFaultNodeName());
    localObject = ((SOAPElement)localObject).addTextNode(paramString);
    if (getFaultRole() != null)
    {
      insertBefore((Node)localObject, faultActorElement);
      return;
    }
    if (hasDetail())
    {
      insertBefore((Node)localObject, detail);
      return;
    }
    addNode((Node)localObject);
  }
  
  public String getFaultRole()
  {
    return getFaultActor();
  }
  
  public void setFaultRole(String paramString)
    throws SOAPException
  {
    if (faultActorElement == null) {
      findFaultActorElement();
    }
    if (faultActorElement != null) {
      faultActorElement.detachNode();
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
  
  public String getFaultCode()
  {
    if (faultCodeElement == null) {
      findFaultCodeElement();
    }
    Iterator localIterator = faultCodeElement.getChildElements(valueName);
    return ((SOAPElement)localIterator.next()).getValue();
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
    Iterator localIterator = faultCodeElement.getChildElements(valueName);
    return convertCodeToQName(str, (SOAPElement)localIterator.next());
  }
  
  public Name getFaultCodeAsName()
  {
    String str = getFaultCode();
    if (str == null) {
      return null;
    }
    if (faultCodeElement == null) {
      findFaultCodeElement();
    }
    Iterator localIterator = faultCodeElement.getChildElements(valueName);
    return NameImpl.convertToName(convertCodeToQName(str, (SOAPElement)localIterator.next()));
  }
  
  public String getFaultString()
  {
    String str = null;
    try
    {
      str = (String)getFaultReasonTexts().next();
    }
    catch (SOAPException localSOAPException) {}
    return str;
  }
  
  public void setFaultString(String paramString)
    throws SOAPException
  {
    addFaultReasonText(paramString, Locale.getDefault());
  }
  
  public void setFaultString(String paramString, Locale paramLocale)
    throws SOAPException
  {
    addFaultReasonText(paramString, paramLocale);
  }
  
  public void appendFaultSubcode(QName paramQName)
    throws SOAPException
  {
    if (paramQName == null) {
      return;
    }
    if ((paramQName.getNamespaceURI() == null) || ("".equals(paramQName.getNamespaceURI())))
    {
      log.severe("SAAJ0432.ver1_2.subcode.not.ns.qualified");
      throw new SOAPExceptionImpl("A Subcode must be namespace-qualified");
    }
    if (innermostSubCodeElement == null)
    {
      if (faultCodeElement == null) {
        findFaultCodeElement();
      }
      innermostSubCodeElement = faultCodeElement;
    }
    String str = null;
    if ((paramQName.getPrefix() == null) || ("".equals(paramQName.getPrefix()))) {
      str = ((ElementImpl)innermostSubCodeElement).getNamespacePrefix(paramQName.getNamespaceURI());
    } else {
      str = paramQName.getPrefix();
    }
    if ((str == null) || ("".equals(str))) {
      str = "ns1";
    }
    innermostSubCodeElement = innermostSubCodeElement.addChildElement(subcodeName);
    SOAPElement localSOAPElement = innermostSubCodeElement.addChildElement(valueName);
    ((ElementImpl)localSOAPElement).ensureNamespaceIsDeclared(str, paramQName.getNamespaceURI());
    localSOAPElement.addTextNode(str + ":" + paramQName.getLocalPart());
  }
  
  public void removeAllFaultSubcodes()
  {
    if (faultCodeElement == null) {
      findFaultCodeElement();
    }
    Iterator localIterator = faultCodeElement.getChildElements(subcodeName);
    if (localIterator.hasNext())
    {
      SOAPElement localSOAPElement = (SOAPElement)localIterator.next();
      localSOAPElement.detachNode();
    }
  }
  
  public Iterator getFaultSubcodes()
  {
    if (faultCodeElement == null) {
      findFaultCodeElement();
    }
    final ArrayList localArrayList = new ArrayList();
    Object localObject = faultCodeElement;
    for (Iterator localIterator1 = ((SOAPElement)localObject).getChildElements(subcodeName); localIterator1.hasNext(); localIterator1 = ((SOAPElement)localObject).getChildElements(subcodeName))
    {
      localObject = (ElementImpl)localIterator1.next();
      Iterator localIterator2 = ((SOAPElement)localObject).getChildElements(valueName);
      SOAPElement localSOAPElement = (SOAPElement)localIterator2.next();
      String str = localSOAPElement.getValue();
      localArrayList.add(convertCodeToQName(str, localSOAPElement));
    }
    new Iterator()
    {
      Iterator subCodeIter = localArrayList.iterator();
      
      public boolean hasNext()
      {
        return subCodeIter.hasNext();
      }
      
      public Object next()
      {
        return subCodeIter.next();
      }
      
      public void remove()
      {
        throw new UnsupportedOperationException("Method remove() not supported on SubCodes Iterator");
      }
    };
  }
  
  private static Locale getLocale(SOAPElement paramSOAPElement)
  {
    return xmlLangToLocale(paramSOAPElement.getAttributeValue(getXmlLangName()));
  }
  
  public void setEncodingStyle(String paramString)
    throws SOAPException
  {
    log.severe("SAAJ0407.ver1_2.no.encodingStyle.in.fault");
    throw new SOAPExceptionImpl("encodingStyle attribute cannot appear on Fault");
  }
  
  public SOAPElement addAttribute(Name paramName, String paramString)
    throws SOAPException
  {
    if ((paramName.getLocalName().equals("encodingStyle")) && (paramName.getURI().equals("http://www.w3.org/2003/05/soap-envelope"))) {
      setEncodingStyle(paramString);
    }
    return super.addAttribute(paramName, paramString);
  }
  
  public SOAPElement addAttribute(QName paramQName, String paramString)
    throws SOAPException
  {
    if ((paramQName.getLocalPart().equals("encodingStyle")) && (paramQName.getNamespaceURI().equals("http://www.w3.org/2003/05/soap-envelope"))) {
      setEncodingStyle(paramString);
    }
    return super.addAttribute(paramQName, paramString);
  }
  
  public SOAPElement addTextNode(String paramString)
    throws SOAPException
  {
    log.log(Level.SEVERE, "SAAJ0416.ver1_2.adding.text.not.legal", getElementQName());
    throw new SOAPExceptionImpl("Adding text to SOAP 1.2 Fault is not legal");
  }
  
  public SOAPElement addChildElement(SOAPElement paramSOAPElement)
    throws SOAPException
  {
    String str = paramSOAPElement.getLocalName();
    Object localObject;
    if ("Detail".equalsIgnoreCase(str))
    {
      if (hasDetail())
      {
        log.severe("SAAJ0436.ver1_2.detail.exists.error");
        throw new SOAPExceptionImpl("Cannot add Detail, Detail already exists");
      }
      localObject = paramSOAPElement.getElementQName().getNamespaceURI();
      if (!((String)localObject).equals("http://www.w3.org/2003/05/soap-envelope"))
      {
        log.severe("SAAJ0437.ver1_2.version.mismatch.error");
        throw new SOAPExceptionImpl("Cannot add Detail, Incorrect SOAP version specified for Detail element");
      }
    }
    if ((paramSOAPElement instanceof Detail1_2Impl))
    {
      localObject = (ElementImpl)importElement(paramSOAPElement);
      addNode((Node)localObject);
      return convertToSoapElement((Element)localObject);
    }
    return super.addChildElement(paramSOAPElement);
  }
  
  protected boolean isStandardFaultElement(String paramString)
  {
    return (paramString.equalsIgnoreCase("code")) || (paramString.equalsIgnoreCase("reason")) || (paramString.equalsIgnoreCase("node")) || (paramString.equalsIgnoreCase("role")) || (paramString.equalsIgnoreCase("detail"));
  }
  
  protected QName getDefaultFaultCode()
  {
    return SOAPConstants.SOAP_SENDER_FAULT;
  }
  
  protected FaultElementImpl createSOAPFaultElement(QName paramQName)
  {
    return new FaultElement1_2Impl(((SOAPDocument)getOwnerDocument()).getDocument(), paramQName);
  }
  
  protected FaultElementImpl createSOAPFaultElement(Name paramName)
  {
    return new FaultElement1_2Impl(((SOAPDocument)getOwnerDocument()).getDocument(), (NameImpl)paramName);
  }
  
  public void setFaultActor(String paramString)
    throws SOAPException
  {
    setFaultRole(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\ver1_2\Fault1_2Impl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */