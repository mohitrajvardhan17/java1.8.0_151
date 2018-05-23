package javax.xml.soap;

public abstract class SOAPConnectionFactory
{
  static final String DEFAULT_SOAP_CONNECTION_FACTORY = "com.sun.xml.internal.messaging.saaj.client.p2p.HttpSOAPConnectionFactory";
  private static final String SF_PROPERTY = "javax.xml.soap.SOAPConnectionFactory";
  
  public SOAPConnectionFactory() {}
  
  public static SOAPConnectionFactory newInstance()
    throws SOAPException, UnsupportedOperationException
  {
    try
    {
      return (SOAPConnectionFactory)FactoryFinder.find("javax.xml.soap.SOAPConnectionFactory", "com.sun.xml.internal.messaging.saaj.client.p2p.HttpSOAPConnectionFactory");
    }
    catch (Exception localException)
    {
      throw new SOAPException("Unable to create SOAP connection factory: " + localException.getMessage());
    }
  }
  
  public abstract SOAPConnection createConnection()
    throws SOAPException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\soap\SOAPConnectionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */