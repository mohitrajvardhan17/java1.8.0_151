package org.jcp.xml.dsig.internal.dom;

import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.TransformService;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DOMTransform
  extends DOMStructure
  implements Transform
{
  protected TransformService spi;
  
  public DOMTransform(TransformService paramTransformService)
  {
    spi = paramTransformService;
  }
  
  public DOMTransform(Element paramElement, XMLCryptoContext paramXMLCryptoContext, Provider paramProvider)
    throws MarshalException
  {
    String str = DOMUtils.getAttributeValue(paramElement, "Algorithm");
    if (paramProvider == null) {
      try
      {
        spi = TransformService.getInstance(str, "DOM");
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException1)
      {
        throw new MarshalException(localNoSuchAlgorithmException1);
      }
    } else {
      try
      {
        spi = TransformService.getInstance(str, "DOM", paramProvider);
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException2)
      {
        try
        {
          spi = TransformService.getInstance(str, "DOM");
        }
        catch (NoSuchAlgorithmException localNoSuchAlgorithmException3)
        {
          throw new MarshalException(localNoSuchAlgorithmException3);
        }
      }
    }
    try
    {
      spi.init(new javax.xml.crypto.dom.DOMStructure(paramElement), paramXMLCryptoContext);
    }
    catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException)
    {
      throw new MarshalException(localInvalidAlgorithmParameterException);
    }
  }
  
  public final AlgorithmParameterSpec getParameterSpec()
  {
    return spi.getParameterSpec();
  }
  
  public final String getAlgorithm()
  {
    return spi.getAlgorithm();
  }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext)
    throws MarshalException
  {
    Document localDocument = DOMUtils.getOwnerDocument(paramNode);
    Element localElement = null;
    if (paramNode.getLocalName().equals("Transforms")) {
      localElement = DOMUtils.createElement(localDocument, "Transform", "http://www.w3.org/2000/09/xmldsig#", paramString);
    } else {
      localElement = DOMUtils.createElement(localDocument, "CanonicalizationMethod", "http://www.w3.org/2000/09/xmldsig#", paramString);
    }
    DOMUtils.setAttribute(localElement, "Algorithm", getAlgorithm());
    spi.marshalParams(new javax.xml.crypto.dom.DOMStructure(localElement), paramDOMCryptoContext);
    paramNode.appendChild(localElement);
  }
  
  public Data transform(Data paramData, XMLCryptoContext paramXMLCryptoContext)
    throws TransformException
  {
    return spi.transform(paramData, paramXMLCryptoContext);
  }
  
  public Data transform(Data paramData, XMLCryptoContext paramXMLCryptoContext, OutputStream paramOutputStream)
    throws TransformException
  {
    return spi.transform(paramData, paramXMLCryptoContext, paramOutputStream);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof Transform)) {
      return false;
    }
    Transform localTransform = (Transform)paramObject;
    return (getAlgorithm().equals(localTransform.getAlgorithm())) && (DOMUtils.paramsEqual(getParameterSpec(), localTransform.getParameterSpec()));
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
  
  Data transform(Data paramData, XMLCryptoContext paramXMLCryptoContext, DOMSignContext paramDOMSignContext)
    throws MarshalException, TransformException
  {
    marshal(paramDOMSignContext.getParent(), DOMUtils.getSignaturePrefix(paramDOMSignContext), paramDOMSignContext);
    return transform(paramData, paramXMLCryptoContext);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMTransform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */