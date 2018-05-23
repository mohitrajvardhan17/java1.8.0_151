package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.List;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.ExcC14NParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import org.w3c.dom.Element;

public final class DOMExcC14NMethod
  extends ApacheCanonicalizer
{
  public DOMExcC14NMethod() {}
  
  public void init(TransformParameterSpec paramTransformParameterSpec)
    throws InvalidAlgorithmParameterException
  {
    if (paramTransformParameterSpec != null)
    {
      if (!(paramTransformParameterSpec instanceof ExcC14NParameterSpec)) {
        throw new InvalidAlgorithmParameterException("params must be of type ExcC14NParameterSpec");
      }
      params = ((C14NMethodParameterSpec)paramTransformParameterSpec);
    }
  }
  
  public void init(XMLStructure paramXMLStructure, XMLCryptoContext paramXMLCryptoContext)
    throws InvalidAlgorithmParameterException
  {
    super.init(paramXMLStructure, paramXMLCryptoContext);
    Element localElement = DOMUtils.getFirstChildElement(transformElem);
    if (localElement == null)
    {
      params = null;
      inclusiveNamespaces = null;
      return;
    }
    unmarshalParams(localElement);
  }
  
  private void unmarshalParams(Element paramElement)
  {
    String str = paramElement.getAttributeNS(null, "PrefixList");
    inclusiveNamespaces = str;
    int i = 0;
    int j = str.indexOf(' ');
    ArrayList localArrayList = new ArrayList();
    while (j != -1)
    {
      localArrayList.add(str.substring(i, j));
      i = j + 1;
      j = str.indexOf(' ', i);
    }
    if (i <= str.length()) {
      localArrayList.add(str.substring(i));
    }
    params = new ExcC14NParameterSpec(localArrayList);
  }
  
  public void marshalParams(XMLStructure paramXMLStructure, XMLCryptoContext paramXMLCryptoContext)
    throws MarshalException
  {
    super.marshalParams(paramXMLStructure, paramXMLCryptoContext);
    AlgorithmParameterSpec localAlgorithmParameterSpec = getParameterSpec();
    if (localAlgorithmParameterSpec == null) {
      return;
    }
    String str = DOMUtils.getNSPrefix(paramXMLCryptoContext, "http://www.w3.org/2001/10/xml-exc-c14n#");
    Element localElement = DOMUtils.createElement(ownerDoc, "InclusiveNamespaces", "http://www.w3.org/2001/10/xml-exc-c14n#", str);
    if ((str == null) || (str.length() == 0)) {
      localElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2001/10/xml-exc-c14n#");
    } else {
      localElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + str, "http://www.w3.org/2001/10/xml-exc-c14n#");
    }
    ExcC14NParameterSpec localExcC14NParameterSpec = (ExcC14NParameterSpec)localAlgorithmParameterSpec;
    StringBuffer localStringBuffer = new StringBuffer("");
    List localList = localExcC14NParameterSpec.getPrefixList();
    int i = 0;
    int j = localList.size();
    while (i < j)
    {
      localStringBuffer.append((String)localList.get(i));
      if (i < j - 1) {
        localStringBuffer.append(" ");
      }
      i++;
    }
    DOMUtils.setAttribute(localElement, "PrefixList", localStringBuffer.toString());
    inclusiveNamespaces = localStringBuffer.toString();
    transformElem.appendChild(localElement);
  }
  
  public String getParamsNSURI()
  {
    return "http://www.w3.org/2001/10/xml-exc-c14n#";
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
          apacheCanonicalizer = Canonicalizer.getInstance("http://www.w3.org/2001/10/xml-exc-c14n#");
        }
        catch (InvalidCanonicalizerException localInvalidCanonicalizerException)
        {
          throw new TransformException("Couldn't find Canonicalizer for: http://www.w3.org/2001/10/xml-exc-c14n#: " + localInvalidCanonicalizerException.getMessage(), localInvalidCanonicalizerException);
        }
      }
    }
    return canonicalize(paramData, paramXMLCryptoContext);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMExcC14NMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */