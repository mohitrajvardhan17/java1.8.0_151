package com.sun.org.apache.xml.internal.security.transforms;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Transforms
  extends SignatureElementProxy
{
  public static final String TRANSFORM_C14N_OMIT_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
  public static final String TRANSFORM_C14N_WITH_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
  public static final String TRANSFORM_C14N11_OMIT_COMMENTS = "http://www.w3.org/2006/12/xml-c14n11";
  public static final String TRANSFORM_C14N11_WITH_COMMENTS = "http://www.w3.org/2006/12/xml-c14n11#WithComments";
  public static final String TRANSFORM_C14N_EXCL_OMIT_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#";
  public static final String TRANSFORM_C14N_EXCL_WITH_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
  public static final String TRANSFORM_XSLT = "http://www.w3.org/TR/1999/REC-xslt-19991116";
  public static final String TRANSFORM_BASE64_DECODE = "http://www.w3.org/2000/09/xmldsig#base64";
  public static final String TRANSFORM_XPATH = "http://www.w3.org/TR/1999/REC-xpath-19991116";
  public static final String TRANSFORM_ENVELOPED_SIGNATURE = "http://www.w3.org/2000/09/xmldsig#enveloped-signature";
  public static final String TRANSFORM_XPOINTER = "http://www.w3.org/TR/2001/WD-xptr-20010108";
  public static final String TRANSFORM_XPATH2FILTER = "http://www.w3.org/2002/06/xmldsig-filter2";
  private static Logger log = Logger.getLogger(Transforms.class.getName());
  private Element[] transforms;
  private boolean secureValidation;
  
  protected Transforms() {}
  
  public Transforms(Document paramDocument)
  {
    super(paramDocument);
    XMLUtils.addReturnToElement(constructionElement);
  }
  
  public Transforms(Element paramElement, String paramString)
    throws DOMException, XMLSignatureException, InvalidTransformException, TransformationException, XMLSecurityException
  {
    super(paramElement, paramString);
    int i = getLength();
    if (i == 0)
    {
      Object[] arrayOfObject = { "Transform", "Transforms" };
      throw new TransformationException("xml.WrongContent", arrayOfObject);
    }
  }
  
  public void setSecureValidation(boolean paramBoolean)
  {
    secureValidation = paramBoolean;
  }
  
  public void addTransform(String paramString)
    throws TransformationException
  {
    try
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "Transforms.addTransform(" + paramString + ")");
      }
      Transform localTransform = new Transform(doc, paramString);
      addTransform(localTransform);
    }
    catch (InvalidTransformException localInvalidTransformException)
    {
      throw new TransformationException("empty", localInvalidTransformException);
    }
  }
  
  public void addTransform(String paramString, Element paramElement)
    throws TransformationException
  {
    try
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "Transforms.addTransform(" + paramString + ")");
      }
      Transform localTransform = new Transform(doc, paramString, paramElement);
      addTransform(localTransform);
    }
    catch (InvalidTransformException localInvalidTransformException)
    {
      throw new TransformationException("empty", localInvalidTransformException);
    }
  }
  
  public void addTransform(String paramString, NodeList paramNodeList)
    throws TransformationException
  {
    try
    {
      Transform localTransform = new Transform(doc, paramString, paramNodeList);
      addTransform(localTransform);
    }
    catch (InvalidTransformException localInvalidTransformException)
    {
      throw new TransformationException("empty", localInvalidTransformException);
    }
  }
  
  private void addTransform(Transform paramTransform)
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Transforms.addTransform(" + paramTransform.getURI() + ")");
    }
    Element localElement = paramTransform.getElement();
    constructionElement.appendChild(localElement);
    XMLUtils.addReturnToElement(constructionElement);
  }
  
  public XMLSignatureInput performTransforms(XMLSignatureInput paramXMLSignatureInput)
    throws TransformationException
  {
    return performTransforms(paramXMLSignatureInput, null);
  }
  
  public XMLSignatureInput performTransforms(XMLSignatureInput paramXMLSignatureInput, OutputStream paramOutputStream)
    throws TransformationException
  {
    try
    {
      int i = getLength() - 1;
      for (int j = 0; j < i; j++)
      {
        Transform localTransform2 = item(j);
        String str = localTransform2.getURI();
        if (log.isLoggable(Level.FINE)) {
          log.log(Level.FINE, "Perform the (" + j + ")th " + str + " transform");
        }
        checkSecureValidation(localTransform2);
        paramXMLSignatureInput = localTransform2.performTransform(paramXMLSignatureInput);
      }
      if (i >= 0)
      {
        Transform localTransform1 = item(i);
        checkSecureValidation(localTransform1);
        paramXMLSignatureInput = localTransform1.performTransform(paramXMLSignatureInput, paramOutputStream);
      }
      return paramXMLSignatureInput;
    }
    catch (IOException localIOException)
    {
      throw new TransformationException("empty", localIOException);
    }
    catch (CanonicalizationException localCanonicalizationException)
    {
      throw new TransformationException("empty", localCanonicalizationException);
    }
    catch (InvalidCanonicalizerException localInvalidCanonicalizerException)
    {
      throw new TransformationException("empty", localInvalidCanonicalizerException);
    }
  }
  
  private void checkSecureValidation(Transform paramTransform)
    throws TransformationException
  {
    String str = paramTransform.getURI();
    if ((secureValidation) && ("http://www.w3.org/TR/1999/REC-xslt-19991116".equals(str)))
    {
      Object[] arrayOfObject = { str };
      throw new TransformationException("signature.Transform.ForbiddenTransform", arrayOfObject);
    }
  }
  
  public int getLength()
  {
    if (transforms == null) {
      transforms = XMLUtils.selectDsNodes(constructionElement.getFirstChild(), "Transform");
    }
    return transforms.length;
  }
  
  public Transform item(int paramInt)
    throws TransformationException
  {
    try
    {
      if (transforms == null) {
        transforms = XMLUtils.selectDsNodes(constructionElement.getFirstChild(), "Transform");
      }
      return new Transform(transforms[paramInt], baseURI);
    }
    catch (XMLSecurityException localXMLSecurityException)
    {
      throw new TransformationException("empty", localXMLSecurityException);
    }
  }
  
  public String getBaseLocalName()
  {
    return "Transforms";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\Transforms.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */