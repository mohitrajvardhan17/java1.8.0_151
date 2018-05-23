package com.sun.org.apache.xml.internal.security.transforms;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.exceptions.AlgorithmAlreadyRegisteredException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformBase64Decode;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformC14N;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformC14N11;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformC14N11_WithComments;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformC14NExclusive;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformC14NExclusiveWithComments;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformC14NWithComments;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformEnvelopedSignature;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformXPath;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformXPath2Filter;
import com.sun.org.apache.xml.internal.security.transforms.implementations.TransformXSLT;
import com.sun.org.apache.xml.internal.security.utils.HelperNodeList;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class Transform
  extends SignatureElementProxy
{
  private static Logger log = Logger.getLogger(Transform.class.getName());
  private static Map<String, Class<? extends TransformSpi>> transformSpiHash = new ConcurrentHashMap();
  private final TransformSpi transformSpi;
  
  public Transform(Document paramDocument, String paramString)
    throws InvalidTransformException
  {
    this(paramDocument, paramString, (NodeList)null);
  }
  
  public Transform(Document paramDocument, String paramString, Element paramElement)
    throws InvalidTransformException
  {
    super(paramDocument);
    HelperNodeList localHelperNodeList = null;
    if (paramElement != null)
    {
      localHelperNodeList = new HelperNodeList();
      XMLUtils.addReturnToElement(paramDocument, localHelperNodeList);
      localHelperNodeList.appendChild(paramElement);
      XMLUtils.addReturnToElement(paramDocument, localHelperNodeList);
    }
    transformSpi = initializeTransform(paramString, localHelperNodeList);
  }
  
  public Transform(Document paramDocument, String paramString, NodeList paramNodeList)
    throws InvalidTransformException
  {
    super(paramDocument);
    transformSpi = initializeTransform(paramString, paramNodeList);
  }
  
  public Transform(Element paramElement, String paramString)
    throws InvalidTransformException, TransformationException, XMLSecurityException
  {
    super(paramElement, paramString);
    String str = paramElement.getAttributeNS(null, "Algorithm");
    if ((str == null) || (str.length() == 0))
    {
      localObject = new Object[] { "Algorithm", "Transform" };
      throw new TransformationException("xml.WrongContent", (Object[])localObject);
    }
    Object localObject = (Class)transformSpiHash.get(str);
    if (localObject == null)
    {
      Object[] arrayOfObject1 = { str };
      throw new InvalidTransformException("signature.Transform.UnknownTransform", arrayOfObject1);
    }
    try
    {
      transformSpi = ((TransformSpi)((Class)localObject).newInstance());
    }
    catch (InstantiationException localInstantiationException)
    {
      arrayOfObject2 = new Object[] { str };
      throw new InvalidTransformException("signature.Transform.UnknownTransform", arrayOfObject2, localInstantiationException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      Object[] arrayOfObject2 = { str };
      throw new InvalidTransformException("signature.Transform.UnknownTransform", arrayOfObject2, localIllegalAccessException);
    }
  }
  
  public static void register(String paramString1, String paramString2)
    throws AlgorithmAlreadyRegisteredException, ClassNotFoundException, InvalidTransformException
  {
    JavaUtils.checkRegisterPermission();
    Class localClass = (Class)transformSpiHash.get(paramString1);
    if (localClass != null)
    {
      localObject = new Object[] { paramString1, localClass };
      throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", (Object[])localObject);
    }
    Object localObject = ClassLoaderUtils.loadClass(paramString2, Transform.class);
    transformSpiHash.put(paramString1, localObject);
  }
  
  public static void register(String paramString, Class<? extends TransformSpi> paramClass)
    throws AlgorithmAlreadyRegisteredException
  {
    JavaUtils.checkRegisterPermission();
    Class localClass = (Class)transformSpiHash.get(paramString);
    if (localClass != null)
    {
      Object[] arrayOfObject = { paramString, localClass };
      throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", arrayOfObject);
    }
    transformSpiHash.put(paramString, paramClass);
  }
  
  public static void registerDefaultAlgorithms()
  {
    transformSpiHash.put("http://www.w3.org/2000/09/xmldsig#base64", TransformBase64Decode.class);
    transformSpiHash.put("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", TransformC14N.class);
    transformSpiHash.put("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments", TransformC14NWithComments.class);
    transformSpiHash.put("http://www.w3.org/2006/12/xml-c14n11", TransformC14N11.class);
    transformSpiHash.put("http://www.w3.org/2006/12/xml-c14n11#WithComments", TransformC14N11_WithComments.class);
    transformSpiHash.put("http://www.w3.org/2001/10/xml-exc-c14n#", TransformC14NExclusive.class);
    transformSpiHash.put("http://www.w3.org/2001/10/xml-exc-c14n#WithComments", TransformC14NExclusiveWithComments.class);
    transformSpiHash.put("http://www.w3.org/TR/1999/REC-xpath-19991116", TransformXPath.class);
    transformSpiHash.put("http://www.w3.org/2000/09/xmldsig#enveloped-signature", TransformEnvelopedSignature.class);
    transformSpiHash.put("http://www.w3.org/TR/1999/REC-xslt-19991116", TransformXSLT.class);
    transformSpiHash.put("http://www.w3.org/2002/06/xmldsig-filter2", TransformXPath2Filter.class);
  }
  
  public String getURI()
  {
    return constructionElement.getAttributeNS(null, "Algorithm");
  }
  
  public XMLSignatureInput performTransform(XMLSignatureInput paramXMLSignatureInput)
    throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException
  {
    return performTransform(paramXMLSignatureInput, null);
  }
  
  public XMLSignatureInput performTransform(XMLSignatureInput paramXMLSignatureInput, OutputStream paramOutputStream)
    throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException
  {
    XMLSignatureInput localXMLSignatureInput = null;
    try
    {
      localXMLSignatureInput = transformSpi.enginePerformTransform(paramXMLSignatureInput, paramOutputStream, this);
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      arrayOfObject = new Object[] { getURI(), "ParserConfigurationException" };
      throw new CanonicalizationException("signature.Transform.ErrorDuringTransform", arrayOfObject, localParserConfigurationException);
    }
    catch (SAXException localSAXException)
    {
      Object[] arrayOfObject = { getURI(), "SAXException" };
      throw new CanonicalizationException("signature.Transform.ErrorDuringTransform", arrayOfObject, localSAXException);
    }
    return localXMLSignatureInput;
  }
  
  public String getBaseLocalName()
  {
    return "Transform";
  }
  
  private TransformSpi initializeTransform(String paramString, NodeList paramNodeList)
    throws InvalidTransformException
  {
    constructionElement.setAttributeNS(null, "Algorithm", paramString);
    Class localClass = (Class)transformSpiHash.get(paramString);
    if (localClass == null)
    {
      localObject = new Object[] { paramString };
      throw new InvalidTransformException("signature.Transform.UnknownTransform", (Object[])localObject);
    }
    Object localObject = null;
    try
    {
      localObject = (TransformSpi)localClass.newInstance();
    }
    catch (InstantiationException localInstantiationException)
    {
      arrayOfObject = new Object[] { paramString };
      throw new InvalidTransformException("signature.Transform.UnknownTransform", arrayOfObject, localInstantiationException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      Object[] arrayOfObject = { paramString };
      throw new InvalidTransformException("signature.Transform.UnknownTransform", arrayOfObject, localIllegalAccessException);
    }
    if (log.isLoggable(Level.FINE))
    {
      log.log(Level.FINE, "Create URI \"" + paramString + "\" class \"" + localObject.getClass() + "\"");
      log.log(Level.FINE, "The NodeList is " + paramNodeList);
    }
    if (paramNodeList != null) {
      for (int i = 0; i < paramNodeList.getLength(); i++) {
        constructionElement.appendChild(paramNodeList.item(i).cloneNode(true));
      }
    }
    return (TransformSpi)localObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\Transform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */