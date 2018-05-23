package com.sun.xml.internal.ws.fault;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.util.DOMUtil;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder={"faultcode", "faultstring", "faultactor", "detail"})
@XmlRootElement(name="Fault", namespace="http://schemas.xmlsoap.org/soap/envelope/")
class SOAP11Fault
  extends SOAPFaultBuilder
{
  @XmlElement(namespace="")
  private QName faultcode;
  @XmlElement(namespace="")
  private String faultstring;
  @XmlElement(namespace="")
  private String faultactor;
  @XmlElement(namespace="")
  private DetailType detail;
  
  SOAP11Fault() {}
  
  SOAP11Fault(QName paramQName, String paramString1, String paramString2, Element paramElement)
  {
    faultcode = paramQName;
    faultstring = paramString1;
    faultactor = paramString2;
    if (paramElement != null) {
      if (((paramElement.getNamespaceURI() == null) || ("".equals(paramElement.getNamespaceURI()))) && ("detail".equals(paramElement.getLocalName())))
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
  
  SOAP11Fault(SOAPFault paramSOAPFault)
  {
    faultcode = paramSOAPFault.getFaultCodeAsQName();
    faultstring = paramSOAPFault.getFaultString();
    faultactor = paramSOAPFault.getFaultActor();
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
  
  QName getFaultcode()
  {
    return faultcode;
  }
  
  void setFaultcode(QName paramQName)
  {
    faultcode = paramQName;
  }
  
  String getFaultString()
  {
    return faultstring;
  }
  
  void setFaultstring(String paramString)
  {
    faultstring = paramString;
  }
  
  String getFaultactor()
  {
    return faultactor;
  }
  
  void setFaultactor(String paramString)
  {
    faultactor = paramString;
  }
  
  DetailType getDetail()
  {
    return detail;
  }
  
  void setDetail(DetailType paramDetailType)
  {
    detail = paramDetailType;
  }
  
  protected Throwable getProtocolException()
  {
    try
    {
      SOAPFault localSOAPFault = SOAPVersion.SOAP_11.getSOAPFactory().createFault(faultstring, faultcode);
      localSOAPFault.setFaultActor(faultactor);
      if (detail != null)
      {
        Detail localDetail = localSOAPFault.addDetail();
        Iterator localIterator = detail.getDetails().iterator();
        while (localIterator.hasNext())
        {
          Element localElement = (Element)localIterator.next();
          Node localNode = localSOAPFault.getOwnerDocument().importNode(localElement, true);
          localDetail.appendChild(localNode);
        }
      }
      return new ServerSOAPFaultException(localSOAPFault);
    }
    catch (SOAPException localSOAPException)
    {
      throw new WebServiceException(localSOAPException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\fault\SOAP11Fault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */