package javax.xml.crypto.dsig.keyinfo;

import java.util.List;
import javax.xml.crypto.Data;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;

public abstract interface RetrievalMethod
  extends URIReference, XMLStructure
{
  public abstract List getTransforms();
  
  public abstract String getURI();
  
  public abstract Data dereference(XMLCryptoContext paramXMLCryptoContext)
    throws URIReferenceException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\dsig\keyinfo\RetrievalMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */