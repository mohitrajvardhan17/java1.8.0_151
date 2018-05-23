package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class TransformXSLT
  extends TransformSpi
{
  public static final String implementedTransformURI = "http://www.w3.org/TR/1999/REC-xslt-19991116";
  static final String XSLTSpecNS = "http://www.w3.org/1999/XSL/Transform";
  static final String defaultXSLTSpecNSprefix = "xslt";
  static final String XSLTSTYLESHEET = "stylesheet";
  private static Logger log = Logger.getLogger(TransformXSLT.class.getName());
  
  public TransformXSLT() {}
  
  protected String engineGetURI()
  {
    return "http://www.w3.org/TR/1999/REC-xslt-19991116";
  }
  
  protected XMLSignatureInput enginePerformTransform(XMLSignatureInput paramXMLSignatureInput, OutputStream paramOutputStream, Transform paramTransform)
    throws IOException, TransformationException
  {
    try
    {
      Element localElement = paramTransform.getElement();
      localObject1 = XMLUtils.selectNode(localElement.getFirstChild(), "http://www.w3.org/1999/XSL/Transform", "stylesheet", 0);
      if (localObject1 == null)
      {
        localObject2 = new Object[] { "xslt:stylesheet", "Transform" };
        throw new TransformationException("xml.WrongContent", (Object[])localObject2);
      }
      Object localObject2 = TransformerFactory.newInstance();
      ((TransformerFactory)localObject2).setFeature("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE.booleanValue());
      StreamSource localStreamSource1 = new StreamSource(new ByteArrayInputStream(paramXMLSignatureInput.getBytes()));
      Object localObject3 = new ByteArrayOutputStream();
      Transformer localTransformer = ((TransformerFactory)localObject2).newTransformer();
      Object localObject5 = new DOMSource((Node)localObject1);
      StreamResult localStreamResult = new StreamResult((OutputStream)localObject3);
      localTransformer.transform((Source)localObject5, localStreamResult);
      StreamSource localStreamSource2 = new StreamSource(new ByteArrayInputStream(((ByteArrayOutputStream)localObject3).toByteArray()));
      localObject3 = ((TransformerFactory)localObject2).newTransformer(localStreamSource2);
      try
      {
        ((Transformer)localObject3).setOutputProperty("{http://xml.apache.org/xalan}line-separator", "\n");
      }
      catch (Exception localException)
      {
        log.log(Level.WARNING, "Unable to set Xalan line-separator property: " + localException.getMessage());
      }
      if (paramOutputStream == null)
      {
        localObject4 = new ByteArrayOutputStream();
        localObject5 = new StreamResult((OutputStream)localObject4);
        ((Transformer)localObject3).transform(localStreamSource1, (Result)localObject5);
        return new XMLSignatureInput(((ByteArrayOutputStream)localObject4).toByteArray());
      }
      Object localObject4 = new StreamResult(paramOutputStream);
      ((Transformer)localObject3).transform(localStreamSource1, (Result)localObject4);
      localObject5 = new XMLSignatureInput((byte[])null);
      ((XMLSignatureInput)localObject5).setOutputStream(paramOutputStream);
      return (XMLSignatureInput)localObject5;
    }
    catch (XMLSecurityException localXMLSecurityException)
    {
      localObject1 = new Object[] { localXMLSecurityException.getMessage() };
      throw new TransformationException("generic.EmptyMessage", (Object[])localObject1, localXMLSecurityException);
    }
    catch (TransformerConfigurationException localTransformerConfigurationException)
    {
      localObject1 = new Object[] { localTransformerConfigurationException.getMessage() };
      throw new TransformationException("generic.EmptyMessage", (Object[])localObject1, localTransformerConfigurationException);
    }
    catch (TransformerException localTransformerException)
    {
      Object localObject1 = { localTransformerException.getMessage() };
      throw new TransformationException("generic.EmptyMessage", (Object[])localObject1, localTransformerException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\implementations\TransformXSLT.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */