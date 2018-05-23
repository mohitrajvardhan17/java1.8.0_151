package com.sun.org.apache.xml.internal.security.transforms;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public abstract class TransformSpi
{
  public TransformSpi() {}
  
  protected XMLSignatureInput enginePerformTransform(XMLSignatureInput paramXMLSignatureInput, OutputStream paramOutputStream, Transform paramTransform)
    throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException, ParserConfigurationException, SAXException
  {
    throw new UnsupportedOperationException();
  }
  
  protected XMLSignatureInput enginePerformTransform(XMLSignatureInput paramXMLSignatureInput, Transform paramTransform)
    throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException, ParserConfigurationException, SAXException
  {
    return enginePerformTransform(paramXMLSignatureInput, null, paramTransform);
  }
  
  protected XMLSignatureInput enginePerformTransform(XMLSignatureInput paramXMLSignatureInput)
    throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException, ParserConfigurationException, SAXException
  {
    return enginePerformTransform(paramXMLSignatureInput, null);
  }
  
  protected abstract String engineGetURI();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\TransformSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */