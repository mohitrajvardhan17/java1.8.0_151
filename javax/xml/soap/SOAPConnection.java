package javax.xml.soap;

public abstract class SOAPConnection
{
  public SOAPConnection() {}
  
  public abstract SOAPMessage call(SOAPMessage paramSOAPMessage, Object paramObject)
    throws SOAPException;
  
  public SOAPMessage get(Object paramObject)
    throws SOAPException
  {
    throw new UnsupportedOperationException("All subclasses of SOAPConnection must override get()");
  }
  
  public abstract void close()
    throws SOAPException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\soap\SOAPConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */