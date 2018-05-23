package javax.xml.soap;

public abstract interface SOAPEnvelope
  extends SOAPElement
{
  public abstract Name createName(String paramString1, String paramString2, String paramString3)
    throws SOAPException;
  
  public abstract Name createName(String paramString)
    throws SOAPException;
  
  public abstract SOAPHeader getHeader()
    throws SOAPException;
  
  public abstract SOAPBody getBody()
    throws SOAPException;
  
  public abstract SOAPHeader addHeader()
    throws SOAPException;
  
  public abstract SOAPBody addBody()
    throws SOAPException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\soap\SOAPEnvelope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */