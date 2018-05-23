package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.Init;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.NodeSetData;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.TransformService;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class ApacheTransform
  extends TransformService
{
  private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
  private Transform apacheTransform;
  protected Document ownerDoc;
  protected Element transformElem;
  protected TransformParameterSpec params;
  
  public ApacheTransform() {}
  
  public final AlgorithmParameterSpec getParameterSpec()
  {
    return params;
  }
  
  public void init(XMLStructure paramXMLStructure, XMLCryptoContext paramXMLCryptoContext)
    throws InvalidAlgorithmParameterException
  {
    if ((paramXMLCryptoContext != null) && (!(paramXMLCryptoContext instanceof DOMCryptoContext))) {
      throw new ClassCastException("context must be of type DOMCryptoContext");
    }
    if (paramXMLStructure == null) {
      throw new NullPointerException();
    }
    if (!(paramXMLStructure instanceof DOMStructure)) {
      throw new ClassCastException("parent must be of type DOMStructure");
    }
    transformElem = ((Element)((DOMStructure)paramXMLStructure).getNode());
    ownerDoc = DOMUtils.getOwnerDocument(transformElem);
  }
  
  public void marshalParams(XMLStructure paramXMLStructure, XMLCryptoContext paramXMLCryptoContext)
    throws MarshalException
  {
    if ((paramXMLCryptoContext != null) && (!(paramXMLCryptoContext instanceof DOMCryptoContext))) {
      throw new ClassCastException("context must be of type DOMCryptoContext");
    }
    if (paramXMLStructure == null) {
      throw new NullPointerException();
    }
    if (!(paramXMLStructure instanceof DOMStructure)) {
      throw new ClassCastException("parent must be of type DOMStructure");
    }
    transformElem = ((Element)((DOMStructure)paramXMLStructure).getNode());
    ownerDoc = DOMUtils.getOwnerDocument(transformElem);
  }
  
  public Data transform(Data paramData, XMLCryptoContext paramXMLCryptoContext)
    throws TransformException
  {
    if (paramData == null) {
      throw new NullPointerException("data must not be null");
    }
    return transformIt(paramData, paramXMLCryptoContext, (OutputStream)null);
  }
  
  public Data transform(Data paramData, XMLCryptoContext paramXMLCryptoContext, OutputStream paramOutputStream)
    throws TransformException
  {
    if (paramData == null) {
      throw new NullPointerException("data must not be null");
    }
    if (paramOutputStream == null) {
      throw new NullPointerException("output stream must not be null");
    }
    return transformIt(paramData, paramXMLCryptoContext, paramOutputStream);
  }
  
  private Data transformIt(Data paramData, XMLCryptoContext paramXMLCryptoContext, OutputStream paramOutputStream)
    throws TransformException
  {
    if (ownerDoc == null) {
      throw new TransformException("transform must be marshalled");
    }
    if (apacheTransform == null) {
      try
      {
        apacheTransform = new Transform(ownerDoc, getAlgorithm(), transformElem.getChildNodes());
        apacheTransform.setElement(transformElem, paramXMLCryptoContext.getBaseURI());
        if (log.isLoggable(Level.FINE)) {
          log.log(Level.FINE, "Created transform for algorithm: " + getAlgorithm());
        }
      }
      catch (Exception localException1)
      {
        throw new TransformException("Couldn't find Transform for: " + getAlgorithm(), localException1);
      }
    }
    Object localObject1;
    if (Utils.secureValidation(paramXMLCryptoContext))
    {
      localObject1 = getAlgorithm();
      if (Policy.restrictAlg((String)localObject1)) {
        throw new TransformException("Transform " + (String)localObject1 + " is forbidden when secure validation is enabled");
      }
    }
    if ((paramData instanceof ApacheData))
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "ApacheData = true");
      }
      localObject1 = ((ApacheData)paramData).getXMLSignatureInput();
    }
    else if ((paramData instanceof NodeSetData))
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "isNodeSet() = true");
      }
      Object localObject2;
      if ((paramData instanceof DOMSubTreeData))
      {
        if (log.isLoggable(Level.FINE)) {
          log.log(Level.FINE, "DOMSubTreeData = true");
        }
        localObject2 = (DOMSubTreeData)paramData;
        localObject1 = new XMLSignatureInput(((DOMSubTreeData)localObject2).getRoot());
        ((XMLSignatureInput)localObject1).setExcludeComments(((DOMSubTreeData)localObject2).excludeComments());
      }
      else
      {
        localObject2 = Utils.toNodeSet(((NodeSetData)paramData).iterator());
        localObject1 = new XMLSignatureInput((Set)localObject2);
      }
    }
    else
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "isNodeSet() = false");
      }
      try
      {
        localObject1 = new XMLSignatureInput(((OctetStreamData)paramData).getOctetStream());
      }
      catch (Exception localException2)
      {
        throw new TransformException(localException2);
      }
    }
    try
    {
      if (paramOutputStream != null)
      {
        localObject1 = apacheTransform.performTransform((XMLSignatureInput)localObject1, paramOutputStream);
        if ((!((XMLSignatureInput)localObject1).isNodeSet()) && (!((XMLSignatureInput)localObject1).isElement())) {
          return null;
        }
      }
      else
      {
        localObject1 = apacheTransform.performTransform((XMLSignatureInput)localObject1);
      }
      if (((XMLSignatureInput)localObject1).isOctetStream()) {
        return new ApacheOctetStreamData((XMLSignatureInput)localObject1);
      }
      return new ApacheNodeSetData((XMLSignatureInput)localObject1);
    }
    catch (Exception localException3)
    {
      throw new TransformException(localException3);
    }
  }
  
  public final boolean isFeatureSupported(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    return false;
  }
  
  static {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\ApacheTransform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */