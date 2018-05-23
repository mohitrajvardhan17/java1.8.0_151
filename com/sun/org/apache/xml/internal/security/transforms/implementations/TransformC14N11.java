package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer11_OmitComments;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import java.io.OutputStream;

public class TransformC14N11
  extends TransformSpi
{
  public TransformC14N11() {}
  
  protected String engineGetURI()
  {
    return "http://www.w3.org/2006/12/xml-c14n11";
  }
  
  protected XMLSignatureInput enginePerformTransform(XMLSignatureInput paramXMLSignatureInput, OutputStream paramOutputStream, Transform paramTransform)
    throws CanonicalizationException
  {
    Canonicalizer11_OmitComments localCanonicalizer11_OmitComments = new Canonicalizer11_OmitComments();
    if (paramOutputStream != null) {
      localCanonicalizer11_OmitComments.setWriter(paramOutputStream);
    }
    byte[] arrayOfByte = null;
    arrayOfByte = localCanonicalizer11_OmitComments.engineCanonicalize(paramXMLSignatureInput);
    XMLSignatureInput localXMLSignatureInput = new XMLSignatureInput(arrayOfByte);
    if (paramOutputStream != null) {
      localXMLSignatureInput.setOutputStream(paramOutputStream);
    }
    return localXMLSignatureInput;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\implementations\TransformC14N11.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */