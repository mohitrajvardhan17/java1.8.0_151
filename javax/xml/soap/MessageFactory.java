package javax.xml.soap;

import java.io.IOException;
import java.io.InputStream;

public abstract class MessageFactory
{
  static final String DEFAULT_MESSAGE_FACTORY = "com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPMessageFactory1_1Impl";
  private static final String MESSAGE_FACTORY_PROPERTY = "javax.xml.soap.MessageFactory";
  
  public MessageFactory() {}
  
  public static MessageFactory newInstance()
    throws SOAPException
  {
    try
    {
      MessageFactory localMessageFactory = (MessageFactory)FactoryFinder.find("javax.xml.soap.MessageFactory", "com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPMessageFactory1_1Impl", false);
      if (localMessageFactory != null) {
        return localMessageFactory;
      }
      return newInstance("SOAP 1.1 Protocol");
    }
    catch (Exception localException)
    {
      throw new SOAPException("Unable to create message factory for SOAP: " + localException.getMessage());
    }
  }
  
  public static MessageFactory newInstance(String paramString)
    throws SOAPException
  {
    return SAAJMetaFactory.getInstance().newMessageFactory(paramString);
  }
  
  public abstract SOAPMessage createMessage()
    throws SOAPException;
  
  public abstract SOAPMessage createMessage(MimeHeaders paramMimeHeaders, InputStream paramInputStream)
    throws IOException, SOAPException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\soap\MessageFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */