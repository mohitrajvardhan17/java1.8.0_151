package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315ExclOmitComments;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.OutputStream;
import org.w3c.dom.Element;

public class TransformC14NExclusive
  extends TransformSpi
{
  public static final String implementedTransformURI = "http://www.w3.org/2001/10/xml-exc-c14n#";
  
  public TransformC14NExclusive() {}
  
  protected String engineGetURI()
  {
    return "http://www.w3.org/2001/10/xml-exc-c14n#";
  }
  
  protected XMLSignatureInput enginePerformTransform(XMLSignatureInput paramXMLSignatureInput, OutputStream paramOutputStream, Transform paramTransform)
    throws CanonicalizationException
  {
    try
    {
      String str = null;
      if (paramTransform.length("http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces") == 1)
      {
        localObject = XMLUtils.selectNode(paramTransform.getElement().getFirstChild(), "http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces", 0);
        str = new InclusiveNamespaces((Element)localObject, paramTransform.getBaseURI()).getInclusiveNamespaces();
      }
      Object localObject = new Canonicalizer20010315ExclOmitComments();
      if (paramOutputStream != null) {
        ((Canonicalizer20010315ExclOmitComments)localObject).setWriter(paramOutputStream);
      }
      byte[] arrayOfByte = ((Canonicalizer20010315ExclOmitComments)localObject).engineCanonicalize(paramXMLSignatureInput, str);
      XMLSignatureInput localXMLSignatureInput = new XMLSignatureInput(arrayOfByte);
      if (paramOutputStream != null) {
        localXMLSignatureInput.setOutputStream(paramOutputStream);
      }
      return localXMLSignatureInput;
    }
    catch (XMLSecurityException localXMLSecurityException)
    {
      throw new CanonicalizationException("empty", localXMLSecurityException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\implementations\TransformC14NExclusive.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */