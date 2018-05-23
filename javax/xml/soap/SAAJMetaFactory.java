package javax.xml.soap;

public abstract class SAAJMetaFactory
{
  private static final String META_FACTORY_CLASS_PROPERTY = "javax.xml.soap.MetaFactory";
  static final String DEFAULT_META_FACTORY_CLASS = "com.sun.xml.internal.messaging.saaj.soap.SAAJMetaFactoryImpl";
  
  static SAAJMetaFactory getInstance()
    throws SOAPException
  {
    try
    {
      SAAJMetaFactory localSAAJMetaFactory = (SAAJMetaFactory)FactoryFinder.find("javax.xml.soap.MetaFactory", "com.sun.xml.internal.messaging.saaj.soap.SAAJMetaFactoryImpl");
      return localSAAJMetaFactory;
    }
    catch (Exception localException)
    {
      throw new SOAPException("Unable to create SAAJ meta-factory" + localException.getMessage());
    }
  }
  
  protected SAAJMetaFactory() {}
  
  protected abstract MessageFactory newMessageFactory(String paramString)
    throws SOAPException;
  
  protected abstract SOAPFactory newSOAPFactory(String paramString)
    throws SOAPException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\soap\SAAJMetaFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */