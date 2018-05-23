package javax.xml.soap;

import javax.xml.namespace.QName;
import org.w3c.dom.Element;

public abstract class SOAPFactory
{
  private static final String SOAP_FACTORY_PROPERTY = "javax.xml.soap.SOAPFactory";
  static final String DEFAULT_SOAP_FACTORY = "com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPFactory1_1Impl";
  
  public SOAPFactory() {}
  
  public SOAPElement createElement(Element paramElement)
    throws SOAPException
  {
    throw new UnsupportedOperationException("createElement(org.w3c.dom.Element) must be overridden by all subclasses of SOAPFactory.");
  }
  
  public abstract SOAPElement createElement(Name paramName)
    throws SOAPException;
  
  public SOAPElement createElement(QName paramQName)
    throws SOAPException
  {
    throw new UnsupportedOperationException("createElement(QName) must be overridden by all subclasses of SOAPFactory.");
  }
  
  public abstract SOAPElement createElement(String paramString)
    throws SOAPException;
  
  public abstract SOAPElement createElement(String paramString1, String paramString2, String paramString3)
    throws SOAPException;
  
  public abstract Detail createDetail()
    throws SOAPException;
  
  public abstract SOAPFault createFault(String paramString, QName paramQName)
    throws SOAPException;
  
  public abstract SOAPFault createFault()
    throws SOAPException;
  
  public abstract Name createName(String paramString1, String paramString2, String paramString3)
    throws SOAPException;
  
  public abstract Name createName(String paramString)
    throws SOAPException;
  
  public static SOAPFactory newInstance()
    throws SOAPException
  {
    try
    {
      SOAPFactory localSOAPFactory = (SOAPFactory)FactoryFinder.find("javax.xml.soap.SOAPFactory", "com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPFactory1_1Impl", false);
      if (localSOAPFactory != null) {
        return localSOAPFactory;
      }
      return newInstance("SOAP 1.1 Protocol");
    }
    catch (Exception localException)
    {
      throw new SOAPException("Unable to create SOAP Factory: " + localException.getMessage());
    }
  }
  
  public static SOAPFactory newInstance(String paramString)
    throws SOAPException
  {
    return SAAJMetaFactory.getInstance().newSOAPFactory(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\soap\SOAPFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */