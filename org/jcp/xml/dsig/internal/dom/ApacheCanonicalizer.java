package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.Init;
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class ApacheCanonicalizer
  extends TransformService
{
  private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
  protected Canonicalizer apacheCanonicalizer;
  private Transform apacheTransform;
  protected String inclusiveNamespaces;
  protected C14NMethodParameterSpec params;
  protected Document ownerDoc;
  protected Element transformElem;
  
  public ApacheCanonicalizer() {}
  
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
  
  public Data canonicalize(Data paramData, XMLCryptoContext paramXMLCryptoContext)
    throws TransformException
  {
    return canonicalize(paramData, paramXMLCryptoContext, null);
  }
  
  public Data canonicalize(Data paramData, XMLCryptoContext paramXMLCryptoContext, OutputStream paramOutputStream)
    throws TransformException
  {
    if (apacheCanonicalizer == null) {
      try
      {
        apacheCanonicalizer = Canonicalizer.getInstance(getAlgorithm());
        if (log.isLoggable(Level.FINE)) {
          log.log(Level.FINE, "Created canonicalizer for algorithm: " + getAlgorithm());
        }
      }
      catch (InvalidCanonicalizerException localInvalidCanonicalizerException)
      {
        throw new TransformException("Couldn't find Canonicalizer for: " + getAlgorithm() + ": " + localInvalidCanonicalizerException.getMessage(), localInvalidCanonicalizerException);
      }
    }
    if (paramOutputStream != null) {
      apacheCanonicalizer.setWriter(paramOutputStream);
    } else {
      apacheCanonicalizer.setWriter(new ByteArrayOutputStream());
    }
    try
    {
      Object localObject1 = null;
      Object localObject2;
      if ((paramData instanceof ApacheData))
      {
        localObject2 = ((ApacheData)paramData).getXMLSignatureInput();
        if (((XMLSignatureInput)localObject2).isElement())
        {
          if (inclusiveNamespaces != null) {
            return new OctetStreamData(new ByteArrayInputStream(apacheCanonicalizer.canonicalizeSubtree(((XMLSignatureInput)localObject2).getSubNode(), inclusiveNamespaces)));
          }
          return new OctetStreamData(new ByteArrayInputStream(apacheCanonicalizer.canonicalizeSubtree(((XMLSignatureInput)localObject2).getSubNode())));
        }
        if (((XMLSignatureInput)localObject2).isNodeSet()) {
          localObject1 = ((XMLSignatureInput)localObject2).getNodeSet();
        } else {
          return new OctetStreamData(new ByteArrayInputStream(apacheCanonicalizer.canonicalize(Utils.readBytesFromStream(((XMLSignatureInput)localObject2).getOctetStream()))));
        }
      }
      else
      {
        if ((paramData instanceof DOMSubTreeData))
        {
          localObject2 = (DOMSubTreeData)paramData;
          if (inclusiveNamespaces != null) {
            return new OctetStreamData(new ByteArrayInputStream(apacheCanonicalizer.canonicalizeSubtree(((DOMSubTreeData)localObject2).getRoot(), inclusiveNamespaces)));
          }
          return new OctetStreamData(new ByteArrayInputStream(apacheCanonicalizer.canonicalizeSubtree(((DOMSubTreeData)localObject2).getRoot())));
        }
        if ((paramData instanceof NodeSetData))
        {
          localObject2 = (NodeSetData)paramData;
          Set localSet = Utils.toNodeSet(((NodeSetData)localObject2).iterator());
          localObject1 = localSet;
          if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Canonicalizing " + ((Set)localObject1).size() + " nodes");
          }
        }
        else
        {
          return new OctetStreamData(new ByteArrayInputStream(apacheCanonicalizer.canonicalize(Utils.readBytesFromStream(((OctetStreamData)paramData).getOctetStream()))));
        }
      }
      if (inclusiveNamespaces != null) {
        return new OctetStreamData(new ByteArrayInputStream(apacheCanonicalizer.canonicalizeXPathNodeSet((Set)localObject1, inclusiveNamespaces)));
      }
      return new OctetStreamData(new ByteArrayInputStream(apacheCanonicalizer.canonicalizeXPathNodeSet((Set)localObject1)));
    }
    catch (Exception localException)
    {
      throw new TransformException(localException);
    }
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
    XMLSignatureInput localXMLSignatureInput;
    if ((paramData instanceof ApacheData))
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "ApacheData = true");
      }
      localXMLSignatureInput = ((ApacheData)paramData).getXMLSignatureInput();
    }
    else if ((paramData instanceof NodeSetData))
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "isNodeSet() = true");
      }
      Object localObject;
      if ((paramData instanceof DOMSubTreeData))
      {
        localObject = (DOMSubTreeData)paramData;
        localXMLSignatureInput = new XMLSignatureInput(((DOMSubTreeData)localObject).getRoot());
        localXMLSignatureInput.setExcludeComments(((DOMSubTreeData)localObject).excludeComments());
      }
      else
      {
        localObject = Utils.toNodeSet(((NodeSetData)paramData).iterator());
        localXMLSignatureInput = new XMLSignatureInput((Set)localObject);
      }
    }
    else
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "isNodeSet() = false");
      }
      try
      {
        localXMLSignatureInput = new XMLSignatureInput(((OctetStreamData)paramData).getOctetStream());
      }
      catch (Exception localException2)
      {
        throw new TransformException(localException2);
      }
    }
    try
    {
      localXMLSignatureInput = apacheTransform.performTransform(localXMLSignatureInput, paramOutputStream);
      if ((!localXMLSignatureInput.isNodeSet()) && (!localXMLSignatureInput.isElement())) {
        return null;
      }
      if (localXMLSignatureInput.isOctetStream()) {
        return new ApacheOctetStreamData(localXMLSignatureInput);
      }
      return new ApacheNodeSetData(localXMLSignatureInput);
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\ApacheCanonicalizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */