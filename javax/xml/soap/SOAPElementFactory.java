package javax.xml.soap;

/**
 * @deprecated
 */
public class SOAPElementFactory
{
  private SOAPFactory soapFactory;
  
  private SOAPElementFactory(SOAPFactory paramSOAPFactory)
  {
    soapFactory = paramSOAPFactory;
  }
  
  /**
   * @deprecated
   */
  public SOAPElement create(Name paramName)
    throws SOAPException
  {
    return soapFactory.createElement(paramName);
  }
  
  /**
   * @deprecated
   */
  public SOAPElement create(String paramString)
    throws SOAPException
  {
    return soapFactory.createElement(paramString);
  }
  
  /**
   * @deprecated
   */
  public SOAPElement create(String paramString1, String paramString2, String paramString3)
    throws SOAPException
  {
    return soapFactory.createElement(paramString1, paramString2, paramString3);
  }
  
  public static SOAPElementFactory newInstance()
    throws SOAPException
  {
    try
    {
      return new SOAPElementFactory(SOAPFactory.newInstance());
    }
    catch (Exception localException)
    {
      throw new SOAPException("Unable to create SOAP Element Factory: " + localException.getMessage());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\soap\SOAPElementFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */