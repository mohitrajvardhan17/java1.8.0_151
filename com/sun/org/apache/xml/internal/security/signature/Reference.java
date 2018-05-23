package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.algorithms.MessageDigestAlgorithm;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.reference.ReferenceData;
import com.sun.org.apache.xml.internal.security.signature.reference.ReferenceNodeSetData;
import com.sun.org.apache.xml.internal.security.signature.reference.ReferenceOctetStreamData;
import com.sun.org.apache.xml.internal.security.signature.reference.ReferenceSubTreeData;
import com.sun.org.apache.xml.internal.security.transforms.InvalidTransformException;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.DigesterOutputStream;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.UnsyncBufferedOutputStream;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class Reference
  extends SignatureElementProxy
{
  public static final String OBJECT_URI = "http://www.w3.org/2000/09/xmldsig#Object";
  public static final String MANIFEST_URI = "http://www.w3.org/2000/09/xmldsig#Manifest";
  public static final int MAXIMUM_TRANSFORM_COUNT = 5;
  private boolean secureValidation;
  private static boolean useC14N11 = ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
  {
    public Boolean run()
    {
      return Boolean.valueOf(Boolean.getBoolean("com.sun.org.apache.xml.internal.security.useC14N11"));
    }
  })).booleanValue();
  private static final Logger log = Logger.getLogger(Reference.class.getName());
  private Manifest manifest;
  private XMLSignatureInput transformsOutput;
  private Transforms transforms;
  private Element digestMethodElem;
  private Element digestValueElement;
  private ReferenceData referenceData;
  
  protected Reference(Document paramDocument, String paramString1, String paramString2, Manifest paramManifest, Transforms paramTransforms, String paramString3)
    throws XMLSignatureException
  {
    super(paramDocument);
    XMLUtils.addReturnToElement(constructionElement);
    baseURI = paramString1;
    manifest = paramManifest;
    setURI(paramString2);
    if (paramTransforms != null)
    {
      transforms = paramTransforms;
      constructionElement.appendChild(paramTransforms.getElement());
      XMLUtils.addReturnToElement(constructionElement);
    }
    MessageDigestAlgorithm localMessageDigestAlgorithm = MessageDigestAlgorithm.getInstance(doc, paramString3);
    digestMethodElem = localMessageDigestAlgorithm.getElement();
    constructionElement.appendChild(digestMethodElem);
    XMLUtils.addReturnToElement(constructionElement);
    digestValueElement = XMLUtils.createElementInSignatureSpace(doc, "DigestValue");
    constructionElement.appendChild(digestValueElement);
    XMLUtils.addReturnToElement(constructionElement);
  }
  
  protected Reference(Element paramElement, String paramString, Manifest paramManifest)
    throws XMLSecurityException
  {
    this(paramElement, paramString, paramManifest, false);
  }
  
  protected Reference(Element paramElement, String paramString, Manifest paramManifest, boolean paramBoolean)
    throws XMLSecurityException
  {
    super(paramElement, paramString);
    secureValidation = paramBoolean;
    baseURI = paramString;
    Element localElement = XMLUtils.getNextElement(paramElement.getFirstChild());
    if (("Transforms".equals(localElement.getLocalName())) && ("http://www.w3.org/2000/09/xmldsig#".equals(localElement.getNamespaceURI())))
    {
      transforms = new Transforms(localElement, baseURI);
      transforms.setSecureValidation(paramBoolean);
      if ((paramBoolean) && (transforms.getLength() > 5))
      {
        Object[] arrayOfObject = { Integer.valueOf(transforms.getLength()), Integer.valueOf(5) };
        throw new XMLSecurityException("signature.tooManyTransforms", arrayOfObject);
      }
      localElement = XMLUtils.getNextElement(localElement.getNextSibling());
    }
    digestMethodElem = localElement;
    digestValueElement = XMLUtils.getNextElement(digestMethodElem.getNextSibling());
    manifest = paramManifest;
  }
  
  public MessageDigestAlgorithm getMessageDigestAlgorithm()
    throws XMLSignatureException
  {
    if (digestMethodElem == null) {
      return null;
    }
    String str = digestMethodElem.getAttributeNS(null, "Algorithm");
    if (str == null) {
      return null;
    }
    if ((secureValidation) && ("http://www.w3.org/2001/04/xmldsig-more#md5".equals(str)))
    {
      Object[] arrayOfObject = { str };
      throw new XMLSignatureException("signature.signatureAlgorithm", arrayOfObject);
    }
    return MessageDigestAlgorithm.getInstance(doc, str);
  }
  
  public void setURI(String paramString)
  {
    if (paramString != null) {
      constructionElement.setAttributeNS(null, "URI", paramString);
    }
  }
  
  public String getURI()
  {
    return constructionElement.getAttributeNS(null, "URI");
  }
  
  public void setId(String paramString)
  {
    if (paramString != null)
    {
      constructionElement.setAttributeNS(null, "Id", paramString);
      constructionElement.setIdAttributeNS(null, "Id", true);
    }
  }
  
  public String getId()
  {
    return constructionElement.getAttributeNS(null, "Id");
  }
  
  public void setType(String paramString)
  {
    if (paramString != null) {
      constructionElement.setAttributeNS(null, "Type", paramString);
    }
  }
  
  public String getType()
  {
    return constructionElement.getAttributeNS(null, "Type");
  }
  
  public boolean typeIsReferenceToObject()
  {
    return "http://www.w3.org/2000/09/xmldsig#Object".equals(getType());
  }
  
  public boolean typeIsReferenceToManifest()
  {
    return "http://www.w3.org/2000/09/xmldsig#Manifest".equals(getType());
  }
  
  private void setDigestValueElement(byte[] paramArrayOfByte)
  {
    for (Node localNode = digestValueElement.getFirstChild(); localNode != null; localNode = localNode.getNextSibling()) {
      digestValueElement.removeChild(localNode);
    }
    String str = Base64.encode(paramArrayOfByte);
    Text localText = doc.createTextNode(str);
    digestValueElement.appendChild(localText);
  }
  
  public void generateDigestValue()
    throws XMLSignatureException, ReferenceNotInitializedException
  {
    setDigestValueElement(calculateDigest(false));
  }
  
  public XMLSignatureInput getContentsBeforeTransformation()
    throws ReferenceNotInitializedException
  {
    try
    {
      Attr localAttr = constructionElement.getAttributeNodeNS(null, "URI");
      ResourceResolver localResourceResolver = ResourceResolver.getInstance(localAttr, baseURI, manifest.getPerManifestResolvers(), secureValidation);
      localResourceResolver.addProperties(manifest.getResolverProperties());
      return localResourceResolver.resolve(localAttr, baseURI, secureValidation);
    }
    catch (ResourceResolverException localResourceResolverException)
    {
      throw new ReferenceNotInitializedException("empty", localResourceResolverException);
    }
  }
  
  private XMLSignatureInput getContentsAfterTransformation(XMLSignatureInput paramXMLSignatureInput, OutputStream paramOutputStream)
    throws XMLSignatureException
  {
    try
    {
      Transforms localTransforms = getTransforms();
      XMLSignatureInput localXMLSignatureInput = null;
      if (localTransforms != null)
      {
        localXMLSignatureInput = localTransforms.performTransforms(paramXMLSignatureInput, paramOutputStream);
        transformsOutput = localXMLSignatureInput;
      }
      else
      {
        localXMLSignatureInput = paramXMLSignatureInput;
      }
      return localXMLSignatureInput;
    }
    catch (ResourceResolverException localResourceResolverException)
    {
      throw new XMLSignatureException("empty", localResourceResolverException);
    }
    catch (CanonicalizationException localCanonicalizationException)
    {
      throw new XMLSignatureException("empty", localCanonicalizationException);
    }
    catch (InvalidCanonicalizerException localInvalidCanonicalizerException)
    {
      throw new XMLSignatureException("empty", localInvalidCanonicalizerException);
    }
    catch (TransformationException localTransformationException)
    {
      throw new XMLSignatureException("empty", localTransformationException);
    }
    catch (XMLSecurityException localXMLSecurityException)
    {
      throw new XMLSignatureException("empty", localXMLSecurityException);
    }
  }
  
  public XMLSignatureInput getContentsAfterTransformation()
    throws XMLSignatureException
  {
    XMLSignatureInput localXMLSignatureInput = getContentsBeforeTransformation();
    cacheDereferencedElement(localXMLSignatureInput);
    return getContentsAfterTransformation(localXMLSignatureInput, null);
  }
  
  public XMLSignatureInput getNodesetBeforeFirstCanonicalization()
    throws XMLSignatureException
  {
    try
    {
      XMLSignatureInput localXMLSignatureInput1 = getContentsBeforeTransformation();
      cacheDereferencedElement(localXMLSignatureInput1);
      XMLSignatureInput localXMLSignatureInput2 = localXMLSignatureInput1;
      Transforms localTransforms = getTransforms();
      if (localTransforms != null)
      {
        for (int i = 0; i < localTransforms.getLength(); i++)
        {
          Transform localTransform = localTransforms.item(i);
          String str = localTransform.getURI();
          if ((str.equals("http://www.w3.org/2001/10/xml-exc-c14n#")) || (str.equals("http://www.w3.org/2001/10/xml-exc-c14n#WithComments")) || (str.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315")) || (str.equals("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments"))) {
            break;
          }
          localXMLSignatureInput2 = localTransform.performTransform(localXMLSignatureInput2, null);
        }
        localXMLSignatureInput2.setSourceURI(localXMLSignatureInput1.getSourceURI());
      }
      return localXMLSignatureInput2;
    }
    catch (IOException localIOException)
    {
      throw new XMLSignatureException("empty", localIOException);
    }
    catch (ResourceResolverException localResourceResolverException)
    {
      throw new XMLSignatureException("empty", localResourceResolverException);
    }
    catch (CanonicalizationException localCanonicalizationException)
    {
      throw new XMLSignatureException("empty", localCanonicalizationException);
    }
    catch (InvalidCanonicalizerException localInvalidCanonicalizerException)
    {
      throw new XMLSignatureException("empty", localInvalidCanonicalizerException);
    }
    catch (TransformationException localTransformationException)
    {
      throw new XMLSignatureException("empty", localTransformationException);
    }
    catch (XMLSecurityException localXMLSecurityException)
    {
      throw new XMLSignatureException("empty", localXMLSecurityException);
    }
  }
  
  public String getHTMLRepresentation()
    throws XMLSignatureException
  {
    try
    {
      XMLSignatureInput localXMLSignatureInput = getNodesetBeforeFirstCanonicalization();
      Transforms localTransforms = getTransforms();
      Object localObject1 = null;
      Object localObject3;
      if (localTransforms != null) {
        for (int i = 0; i < localTransforms.getLength(); i++)
        {
          localObject3 = localTransforms.item(i);
          String str = ((Transform)localObject3).getURI();
          if ((str.equals("http://www.w3.org/2001/10/xml-exc-c14n#")) || (str.equals("http://www.w3.org/2001/10/xml-exc-c14n#WithComments")))
          {
            localObject1 = localObject3;
            break;
          }
        }
      }
      Object localObject2 = new HashSet();
      if ((localObject1 != null) && (((Transform)localObject1).length("http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces") == 1))
      {
        localObject3 = new InclusiveNamespaces(XMLUtils.selectNode(((Transform)localObject1).getElement().getFirstChild(), "http://www.w3.org/2001/10/xml-exc-c14n#", "InclusiveNamespaces", 0), getBaseURI());
        localObject2 = InclusiveNamespaces.prefixStr2Set(((InclusiveNamespaces)localObject3).getInclusiveNamespaces());
      }
      return localXMLSignatureInput.getHTMLRepresentation((Set)localObject2);
    }
    catch (TransformationException localTransformationException)
    {
      throw new XMLSignatureException("empty", localTransformationException);
    }
    catch (InvalidTransformException localInvalidTransformException)
    {
      throw new XMLSignatureException("empty", localInvalidTransformException);
    }
    catch (XMLSecurityException localXMLSecurityException)
    {
      throw new XMLSignatureException("empty", localXMLSecurityException);
    }
  }
  
  public XMLSignatureInput getTransformsOutput()
  {
    return transformsOutput;
  }
  
  public ReferenceData getReferenceData()
  {
    return referenceData;
  }
  
  protected XMLSignatureInput dereferenceURIandPerformTransforms(OutputStream paramOutputStream)
    throws XMLSignatureException
  {
    try
    {
      XMLSignatureInput localXMLSignatureInput1 = getContentsBeforeTransformation();
      cacheDereferencedElement(localXMLSignatureInput1);
      XMLSignatureInput localXMLSignatureInput2 = getContentsAfterTransformation(localXMLSignatureInput1, paramOutputStream);
      transformsOutput = localXMLSignatureInput2;
      return localXMLSignatureInput2;
    }
    catch (XMLSecurityException localXMLSecurityException)
    {
      throw new ReferenceNotInitializedException("empty", localXMLSecurityException);
    }
  }
  
  private void cacheDereferencedElement(XMLSignatureInput paramXMLSignatureInput)
  {
    if (paramXMLSignatureInput.isNodeSet()) {
      try
      {
        final Set localSet = paramXMLSignatureInput.getNodeSet();
        referenceData = new ReferenceNodeSetData()
        {
          public Iterator<Node> iterator()
          {
            new Iterator()
            {
              Iterator<Node> sIterator = val$s.iterator();
              
              public boolean hasNext()
              {
                return sIterator.hasNext();
              }
              
              public Node next()
              {
                return (Node)sIterator.next();
              }
              
              public void remove()
              {
                throw new UnsupportedOperationException();
              }
            };
          }
        };
      }
      catch (Exception localException)
      {
        log.log(Level.WARNING, "cannot cache dereferenced data: " + localException);
      }
    } else if (paramXMLSignatureInput.isElement()) {
      referenceData = new ReferenceSubTreeData(paramXMLSignatureInput.getSubNode(), paramXMLSignatureInput.isExcludeComments());
    } else if ((paramXMLSignatureInput.isOctetStream()) || (paramXMLSignatureInput.isByteArray())) {
      try
      {
        referenceData = new ReferenceOctetStreamData(paramXMLSignatureInput.getOctetStream(), paramXMLSignatureInput.getSourceURI(), paramXMLSignatureInput.getMIMEType());
      }
      catch (IOException localIOException)
      {
        log.log(Level.WARNING, "cannot cache dereferenced data: " + localIOException);
      }
    }
  }
  
  public Transforms getTransforms()
    throws XMLSignatureException, InvalidTransformException, TransformationException, XMLSecurityException
  {
    return transforms;
  }
  
  public byte[] getReferencedBytes()
    throws ReferenceNotInitializedException, XMLSignatureException
  {
    try
    {
      XMLSignatureInput localXMLSignatureInput = dereferenceURIandPerformTransforms(null);
      return localXMLSignatureInput.getBytes();
    }
    catch (IOException localIOException)
    {
      throw new ReferenceNotInitializedException("empty", localIOException);
    }
    catch (CanonicalizationException localCanonicalizationException)
    {
      throw new ReferenceNotInitializedException("empty", localCanonicalizationException);
    }
  }
  
  private byte[] calculateDigest(boolean paramBoolean)
    throws ReferenceNotInitializedException, XMLSignatureException
  {
    UnsyncBufferedOutputStream localUnsyncBufferedOutputStream = null;
    try
    {
      MessageDigestAlgorithm localMessageDigestAlgorithm = getMessageDigestAlgorithm();
      localMessageDigestAlgorithm.reset();
      DigesterOutputStream localDigesterOutputStream = new DigesterOutputStream(localMessageDigestAlgorithm);
      localUnsyncBufferedOutputStream = new UnsyncBufferedOutputStream(localDigesterOutputStream);
      XMLSignatureInput localXMLSignatureInput = dereferenceURIandPerformTransforms(localUnsyncBufferedOutputStream);
      if ((useC14N11) && (!paramBoolean) && (!localXMLSignatureInput.isOutputStreamSet()) && (!localXMLSignatureInput.isOctetStream()))
      {
        if (transforms == null)
        {
          transforms = new Transforms(doc);
          transforms.setSecureValidation(secureValidation);
          constructionElement.insertBefore(transforms.getElement(), digestMethodElem);
        }
        transforms.addTransform("http://www.w3.org/2006/12/xml-c14n11");
        localXMLSignatureInput.updateOutputStream(localUnsyncBufferedOutputStream, true);
      }
      else
      {
        localXMLSignatureInput.updateOutputStream(localUnsyncBufferedOutputStream);
      }
      localUnsyncBufferedOutputStream.flush();
      if (localXMLSignatureInput.getOctetStreamReal() != null) {
        localXMLSignatureInput.getOctetStreamReal().close();
      }
      byte[] arrayOfByte = localDigesterOutputStream.getDigestValue();
      return arrayOfByte;
    }
    catch (XMLSecurityException localXMLSecurityException)
    {
      throw new ReferenceNotInitializedException("empty", localXMLSecurityException);
    }
    catch (IOException localIOException1)
    {
      throw new ReferenceNotInitializedException("empty", localIOException1);
    }
    finally
    {
      if (localUnsyncBufferedOutputStream != null) {
        try
        {
          localUnsyncBufferedOutputStream.close();
        }
        catch (IOException localIOException3)
        {
          throw new ReferenceNotInitializedException("empty", localIOException3);
        }
      }
    }
  }
  
  public byte[] getDigestValue()
    throws Base64DecodingException, XMLSecurityException
  {
    if (digestValueElement == null)
    {
      Object[] arrayOfObject = { "DigestValue", "http://www.w3.org/2000/09/xmldsig#" };
      throw new XMLSecurityException("signature.Verification.NoSignatureElement", arrayOfObject);
    }
    return Base64.decode(digestValueElement);
  }
  
  public boolean verify()
    throws ReferenceNotInitializedException, XMLSecurityException
  {
    byte[] arrayOfByte1 = getDigestValue();
    byte[] arrayOfByte2 = calculateDigest(true);
    boolean bool = MessageDigestAlgorithm.isEqual(arrayOfByte1, arrayOfByte2);
    if (!bool)
    {
      log.log(Level.WARNING, "Verification failed for URI \"" + getURI() + "\"");
      log.log(Level.WARNING, "Expected Digest: " + Base64.encode(arrayOfByte1));
      log.log(Level.WARNING, "Actual Digest: " + Base64.encode(arrayOfByte2));
    }
    else if (log.isLoggable(Level.FINE))
    {
      log.log(Level.FINE, "Verification successful for URI \"" + getURI() + "\"");
    }
    return bool;
  }
  
  public String getBaseLocalName()
  {
    return "Reference";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\signature\Reference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */