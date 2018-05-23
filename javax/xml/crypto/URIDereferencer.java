package javax.xml.crypto;

public abstract interface URIDereferencer
{
  public abstract Data dereference(URIReference paramURIReference, XMLCryptoContext paramXMLCryptoContext)
    throws URIReferenceException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\URIDereferencer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */