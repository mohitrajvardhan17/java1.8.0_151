package org.jcp.xml.dsig.internal.dom;

import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.Provider;
import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.TransformService;
import org.w3c.dom.Element;

public class DOMCanonicalizationMethod
  extends DOMTransform
  implements CanonicalizationMethod
{
  public DOMCanonicalizationMethod(TransformService paramTransformService)
    throws InvalidAlgorithmParameterException
  {
    super(paramTransformService);
    if ((!(paramTransformService instanceof ApacheCanonicalizer)) && (!isC14Nalg(paramTransformService.getAlgorithm()))) {
      throw new InvalidAlgorithmParameterException("Illegal CanonicalizationMethod");
    }
  }
  
  public DOMCanonicalizationMethod(Element paramElement, XMLCryptoContext paramXMLCryptoContext, Provider paramProvider)
    throws MarshalException
  {
    super(paramElement, paramXMLCryptoContext, paramProvider);
    if ((!(spi instanceof ApacheCanonicalizer)) && (!isC14Nalg(spi.getAlgorithm()))) {
      throw new MarshalException("Illegal CanonicalizationMethod");
    }
  }
  
  public Data canonicalize(Data paramData, XMLCryptoContext paramXMLCryptoContext)
    throws TransformException
  {
    return transform(paramData, paramXMLCryptoContext);
  }
  
  public Data canonicalize(Data paramData, XMLCryptoContext paramXMLCryptoContext, OutputStream paramOutputStream)
    throws TransformException
  {
    return transform(paramData, paramXMLCryptoContext, paramOutputStream);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof CanonicalizationMethod)) {
      return false;
    }
    CanonicalizationMethod localCanonicalizationMethod = (CanonicalizationMethod)paramObject;
    return (getAlgorithm().equals(localCanonicalizationMethod.getAlgorithm())) && (DOMUtils.paramsEqual(getParameterSpec(), localCanonicalizationMethod.getParameterSpec()));
  }
  
  public int hashCode()
  {
    int i = 17;
    i = 31 * i + getAlgorithm().hashCode();
    AlgorithmParameterSpec localAlgorithmParameterSpec = getParameterSpec();
    if (localAlgorithmParameterSpec != null) {
      i = 31 * i + localAlgorithmParameterSpec.hashCode();
    }
    return i;
  }
  
  private static boolean isC14Nalg(String paramString)
  {
    return (paramString.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315")) || (paramString.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments")) || (paramString.equals("http://www.w3.org/2001/10/xml-exc-c14n#")) || (paramString.equals("http://www.w3.org/2001/10/xml-exc-c14n#WithComments")) || (paramString.equals("http://www.w3.org/2006/12/xml-c14n11")) || (paramString.equals("http://www.w3.org/2006/12/xml-c14n11#WithComments"));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMCanonicalizationMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */