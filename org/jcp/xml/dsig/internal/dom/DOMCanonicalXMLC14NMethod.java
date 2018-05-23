package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import java.security.InvalidAlgorithmParameterException;
import javax.xml.crypto.Data;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

public final class DOMCanonicalXMLC14NMethod
  extends ApacheCanonicalizer
{
  public DOMCanonicalXMLC14NMethod() {}
  
  public void init(TransformParameterSpec paramTransformParameterSpec)
    throws InvalidAlgorithmParameterException
  {
    if (paramTransformParameterSpec != null) {
      throw new InvalidAlgorithmParameterException("no parameters should be specified for Canonical XML C14N algorithm");
    }
  }
  
  public Data transform(Data paramData, XMLCryptoContext paramXMLCryptoContext)
    throws TransformException
  {
    if ((paramData instanceof DOMSubTreeData))
    {
      DOMSubTreeData localDOMSubTreeData = (DOMSubTreeData)paramData;
      if (localDOMSubTreeData.excludeComments()) {
        try
        {
          apacheCanonicalizer = Canonicalizer.getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
        }
        catch (InvalidCanonicalizerException localInvalidCanonicalizerException)
        {
          throw new TransformException("Couldn't find Canonicalizer for: http://www.w3.org/TR/2001/REC-xml-c14n-20010315: " + localInvalidCanonicalizerException.getMessage(), localInvalidCanonicalizerException);
        }
      }
    }
    return canonicalize(paramData, paramXMLCryptoContext);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMCanonicalXMLC14NMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */