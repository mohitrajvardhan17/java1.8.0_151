package com.sun.xml.internal.ws.fault;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.util.DOMUtil;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@XmlRootElement(name="Fault", namespace="http://www.w3.org/2003/05/soap-envelope")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder={"code", "reason", "node", "role", "detail"})
class SOAP12Fault
  extends SOAPFaultBuilder
{
  @XmlTransient
  private static final String ns = "http://www.w3.org/2003/05/soap-envelope";
  @XmlElement(namespace="http://www.w3.org/2003/05/soap-envelope", name="Code")
  private CodeType code;
  @XmlElement(namespace="http://www.w3.org/2003/05/soap-envelope", name="Reason")
  private ReasonType reason;
  @XmlElement(namespace="http://www.w3.org/2003/05/soap-envelope", name="Node")
  private String node;
  @XmlElement(namespace="http://www.w3.org/2003/05/soap-envelope", name="Role")
  private String role;
  @XmlElement(namespace="http://www.w3.org/2003/05/soap-envelope", name="Detail")
  private DetailType detail;
  
  SOAP12Fault() {}
  
  SOAP12Fault(CodeType paramCodeType, ReasonType paramReasonType, String paramString1, String paramString2, DetailType paramDetailType)
  {
    code = paramCodeType;
    reason = paramReasonType;
    node = paramString1;
    role = paramString2;
    detail = paramDetailType;
  }
  
  SOAP12Fault(CodeType paramCodeType, ReasonType paramReasonType, String paramString1, String paramString2, Element paramElement)
  {
    code = paramCodeType;
    reason = paramReasonType;
    node = paramString1;
    role = paramString2;
    if (paramElement != null) {
      if ((paramElement.getNamespaceURI().equals("http://www.w3.org/2003/05/soap-envelope")) && (paramElement.getLocalName().equals("Detail")))
      {
        detail = new DetailType();
        Iterator localIterator = DOMUtil.getChildElements(paramElement).iterator();
        while (localIterator.hasNext())
        {
          Element localElement = (Element)localIterator.next();
          detail.getDetails().add(localElement);
        }
      }
      else
      {
        detail = new DetailType(paramElement);
      }
    }
  }
  
  SOAP12Fault(SOAPFault paramSOAPFault)
  {
    code = new CodeType(paramSOAPFault.getFaultCodeAsQName());
    try
    {
      fillFaultSubCodes(paramSOAPFault);
    }
    catch (SOAPException localSOAPException)
    {
      throw new WebServiceException(localSOAPException);
    }
    reason = new ReasonType(paramSOAPFault.getFaultString());
    role = paramSOAPFault.getFaultRole();
    node = paramSOAPFault.getFaultNode();
    if (paramSOAPFault.getDetail() != null)
    {
      detail = new DetailType();
      Iterator localIterator = paramSOAPFault.getDetail().getDetailEntries();
      while (localIterator.hasNext())
      {
        Element localElement = (Element)localIterator.next();
        detail.getDetails().add(localElement);
      }
    }
  }
  
  SOAP12Fault(QName paramQName, String paramString, Element paramElement)
  {
    this(new CodeType(paramQName), new ReasonType(paramString), null, null, paramElement);
  }
  
  CodeType getCode()
  {
    return code;
  }
  
  ReasonType getReason()
  {
    return reason;
  }
  
  String getNode()
  {
    return node;
  }
  
  String getRole()
  {
    return role;
  }
  
  DetailType getDetail()
  {
    return detail;
  }
  
  void setDetail(DetailType paramDetailType)
  {
    detail = paramDetailType;
  }
  
  String getFaultString()
  {
    return ((TextType)reason.texts().get(0)).getText();
  }
  
  protected Throwable getProtocolException()
  {
    try
    {
      SOAPFault localSOAPFault = SOAPVersion.SOAP_12.getSOAPFactory().createFault();
      Object localObject1;
      Object localObject2;
      if (reason != null)
      {
        localObject1 = reason.texts().iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (TextType)((Iterator)localObject1).next();
          localSOAPFault.setFaultString(((TextType)localObject2).getText());
        }
      }
      if (code != null)
      {
        localSOAPFault.setFaultCode(code.getValue());
        fillFaultSubCodes(localSOAPFault, code.getSubcode());
      }
      if ((detail != null) && (detail.getDetail(0) != null))
      {
        localObject1 = localSOAPFault.addDetail();
        localObject2 = detail.getDetails().iterator();
        while (((Iterator)localObject2).hasNext())
        {
          Node localNode1 = (Node)((Iterator)localObject2).next();
          Node localNode2 = localSOAPFault.getOwnerDocument().importNode(localNode1, true);
          ((Detail)localObject1).appendChild(localNode2);
        }
      }
      if (node != null) {
        localSOAPFault.setFaultNode(node);
      }
      return new ServerSOAPFaultException(localSOAPFault);
    }
    catch (SOAPException localSOAPException)
    {
      throw new WebServiceException(localSOAPException);
    }
  }
  
  private void fillFaultSubCodes(SOAPFault paramSOAPFault, SubcodeType paramSubcodeType)
    throws SOAPException
  {
    if (paramSubcodeType != null)
    {
      paramSOAPFault.appendFaultSubcode(paramSubcodeType.getValue());
      fillFaultSubCodes(paramSOAPFault, paramSubcodeType.getSubcode());
    }
  }
  
  private void fillFaultSubCodes(SOAPFault paramSOAPFault)
    throws SOAPException
  {
    Iterator localIterator = paramSOAPFault.getFaultSubcodes();
    Object localObject = null;
    while (localIterator.hasNext())
    {
      QName localQName = (QName)localIterator.next();
      if (localObject == null)
      {
        localObject = new SubcodeType(localQName);
        code.setSubcode((SubcodeType)localObject);
      }
      else
      {
        SubcodeType localSubcodeType = new SubcodeType(localQName);
        ((SubcodeType)localObject).setSubcode(localSubcodeType);
        localObject = localSubcodeType;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\fault\SOAP12Fault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */