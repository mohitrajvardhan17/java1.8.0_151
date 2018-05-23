package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.utils.I18n;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class Manifest
  extends SignatureElementProxy
{
  public static final int MAXIMUM_REFERENCE_COUNT = 30;
  private static Logger log = Logger.getLogger(Manifest.class.getName());
  private List<Reference> references;
  private Element[] referencesEl;
  private boolean[] verificationResults = null;
  private Map<String, String> resolverProperties = null;
  private List<ResourceResolver> perManifestResolvers = null;
  private boolean secureValidation;
  
  public Manifest(Document paramDocument)
  {
    super(paramDocument);
    XMLUtils.addReturnToElement(constructionElement);
    references = new ArrayList();
  }
  
  public Manifest(Element paramElement, String paramString)
    throws XMLSecurityException
  {
    this(paramElement, paramString, false);
  }
  
  public Manifest(Element paramElement, String paramString, boolean paramBoolean)
    throws XMLSecurityException
  {
    super(paramElement, paramString);
    Attr localAttr1 = paramElement.getAttributeNodeNS(null, "Id");
    if (localAttr1 != null) {
      paramElement.setIdAttributeNode(localAttr1, true);
    }
    secureValidation = paramBoolean;
    referencesEl = XMLUtils.selectDsNodes(constructionElement.getFirstChild(), "Reference");
    int i = referencesEl.length;
    Object[] arrayOfObject;
    if (i == 0)
    {
      arrayOfObject = new Object[] { "Reference", "Manifest" };
      throw new DOMException((short)4, I18n.translate("xml.WrongContent", arrayOfObject));
    }
    if ((paramBoolean) && (i > 30))
    {
      arrayOfObject = new Object[] { Integer.valueOf(i), Integer.valueOf(30) };
      throw new XMLSecurityException("signature.tooManyReferences", arrayOfObject);
    }
    references = new ArrayList(i);
    for (int j = 0; j < i; j++)
    {
      Element localElement = referencesEl[j];
      Attr localAttr2 = localElement.getAttributeNodeNS(null, "Id");
      if (localAttr2 != null) {
        localElement.setIdAttributeNode(localAttr2, true);
      }
      references.add(null);
    }
  }
  
  public void addDocument(String paramString1, String paramString2, Transforms paramTransforms, String paramString3, String paramString4, String paramString5)
    throws XMLSignatureException
  {
    Reference localReference = new Reference(doc, paramString1, paramString2, this, paramTransforms, paramString3);
    if (paramString4 != null) {
      localReference.setId(paramString4);
    }
    if (paramString5 != null) {
      localReference.setType(paramString5);
    }
    references.add(localReference);
    constructionElement.appendChild(localReference.getElement());
    XMLUtils.addReturnToElement(constructionElement);
  }
  
  public void generateDigestValues()
    throws XMLSignatureException, ReferenceNotInitializedException
  {
    for (int i = 0; i < getLength(); i++)
    {
      Reference localReference = (Reference)references.get(i);
      localReference.generateDigestValue();
    }
  }
  
  public int getLength()
  {
    return references.size();
  }
  
  public Reference item(int paramInt)
    throws XMLSecurityException
  {
    if (references.get(paramInt) == null)
    {
      Reference localReference = new Reference(referencesEl[paramInt], baseURI, this, secureValidation);
      references.set(paramInt, localReference);
    }
    return (Reference)references.get(paramInt);
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
  
  public boolean verifyReferences()
    throws MissingResourceFailureException, XMLSecurityException
  {
    return verifyReferences(false);
  }
  
  public boolean verifyReferences(boolean paramBoolean)
    throws MissingResourceFailureException, XMLSecurityException
  {
    if (referencesEl == null) {
      referencesEl = XMLUtils.selectDsNodes(constructionElement.getFirstChild(), "Reference");
    }
    if (log.isLoggable(Level.FINE))
    {
      log.log(Level.FINE, "verify " + referencesEl.length + " References");
      log.log(Level.FINE, "I am " + (paramBoolean ? "" : "not") + " requested to follow nested Manifests");
    }
    if (referencesEl.length == 0) {
      throw new XMLSecurityException("empty");
    }
    if ((secureValidation) && (referencesEl.length > 30))
    {
      Object[] arrayOfObject1 = { Integer.valueOf(referencesEl.length), Integer.valueOf(30) };
      throw new XMLSecurityException("signature.tooManyReferences", arrayOfObject1);
    }
    verificationResults = new boolean[referencesEl.length];
    boolean bool1 = true;
    for (int i = 0; i < referencesEl.length; i++)
    {
      Reference localReference = new Reference(referencesEl[i], baseURI, this, secureValidation);
      references.set(i, localReference);
      try
      {
        boolean bool2 = localReference.verify();
        setVerificationResult(i, bool2);
        if (!bool2) {
          bool1 = false;
        }
        if (log.isLoggable(Level.FINE)) {
          log.log(Level.FINE, "The Reference has Type " + localReference.getType());
        }
        if ((bool1) && (paramBoolean) && (localReference.typeIsReferenceToManifest()))
        {
          if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "We have to follow a nested Manifest");
          }
          try
          {
            XMLSignatureInput localXMLSignatureInput = localReference.dereferenceURIandPerformTransforms(null);
            Set localSet = localXMLSignatureInput.getNodeSet();
            Manifest localManifest = null;
            Iterator localIterator = localSet.iterator();
            while (localIterator.hasNext())
            {
              Node localNode = (Node)localIterator.next();
              if ((localNode.getNodeType() == 1) && (((Element)localNode).getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#")) && (((Element)localNode).getLocalName().equals("Manifest"))) {
                try
                {
                  localManifest = new Manifest((Element)localNode, localXMLSignatureInput.getSourceURI(), secureValidation);
                }
                catch (XMLSecurityException localXMLSecurityException)
                {
                  if (log.isLoggable(Level.FINE)) {
                    log.log(Level.FINE, localXMLSecurityException.getMessage(), localXMLSecurityException);
                  }
                }
              }
            }
            if (localManifest == null) {
              throw new MissingResourceFailureException("empty", localReference);
            }
            perManifestResolvers = perManifestResolvers;
            resolverProperties = resolverProperties;
            boolean bool3 = localManifest.verifyReferences(paramBoolean);
            if (!bool3)
            {
              bool1 = false;
              log.log(Level.WARNING, "The nested Manifest was invalid (bad)");
            }
            else if (log.isLoggable(Level.FINE))
            {
              log.log(Level.FINE, "The nested Manifest was valid (good)");
            }
          }
          catch (IOException localIOException)
          {
            throw new ReferenceNotInitializedException("empty", localIOException);
          }
          catch (ParserConfigurationException localParserConfigurationException)
          {
            throw new ReferenceNotInitializedException("empty", localParserConfigurationException);
          }
          catch (SAXException localSAXException)
          {
            throw new ReferenceNotInitializedException("empty", localSAXException);
          }
        }
      }
      catch (ReferenceNotInitializedException localReferenceNotInitializedException)
      {
        Object[] arrayOfObject2 = { localReference.getURI() };
        throw new MissingResourceFailureException("signature.Verification.Reference.NoInput", arrayOfObject2, localReferenceNotInitializedException, localReference);
      }
    }
    return bool1;
  }
  
  private void setVerificationResult(int paramInt, boolean paramBoolean)
  {
    if (verificationResults == null) {
      verificationResults = new boolean[getLength()];
    }
    verificationResults[paramInt] = paramBoolean;
  }
  
  public boolean getVerificationResult(int paramInt)
    throws XMLSecurityException
  {
    if ((paramInt < 0) || (paramInt > getLength() - 1))
    {
      Object[] arrayOfObject = { Integer.toString(paramInt), Integer.toString(getLength()) };
      IndexOutOfBoundsException localIndexOutOfBoundsException = new IndexOutOfBoundsException(I18n.translate("signature.Verification.IndexOutOfBounds", arrayOfObject));
      throw new XMLSecurityException("generic.EmptyMessage", localIndexOutOfBoundsException);
    }
    if (verificationResults == null) {
      try
      {
        verifyReferences();
      }
      catch (Exception localException)
      {
        throw new XMLSecurityException("generic.EmptyMessage", localException);
      }
    }
    return verificationResults[paramInt];
  }
  
  public void addResourceResolver(ResourceResolver paramResourceResolver)
  {
    if (paramResourceResolver == null) {
      return;
    }
    if (perManifestResolvers == null) {
      perManifestResolvers = new ArrayList();
    }
    perManifestResolvers.add(paramResourceResolver);
  }
  
  public void addResourceResolver(ResourceResolverSpi paramResourceResolverSpi)
  {
    if (paramResourceResolverSpi == null) {
      return;
    }
    if (perManifestResolvers == null) {
      perManifestResolvers = new ArrayList();
    }
    perManifestResolvers.add(new ResourceResolver(paramResourceResolverSpi));
  }
  
  public List<ResourceResolver> getPerManifestResolvers()
  {
    return perManifestResolvers;
  }
  
  public Map<String, String> getResolverProperties()
  {
    return resolverProperties;
  }
  
  public void setResolverProperty(String paramString1, String paramString2)
  {
    if (resolverProperties == null) {
      resolverProperties = new HashMap(10);
    }
    resolverProperties.put(paramString1, paramString2);
  }
  
  public String getResolverProperty(String paramString)
  {
    return (String)resolverProperties.get(paramString);
  }
  
  public byte[] getSignedContentItem(int paramInt)
    throws XMLSignatureException
  {
    try
    {
      return getReferencedContentAfterTransformsItem(paramInt).getBytes();
    }
    catch (IOException localIOException)
    {
      throw new XMLSignatureException("empty", localIOException);
    }
    catch (CanonicalizationException localCanonicalizationException)
    {
      throw new XMLSignatureException("empty", localCanonicalizationException);
    }
    catch (InvalidCanonicalizerException localInvalidCanonicalizerException)
    {
      throw new XMLSignatureException("empty", localInvalidCanonicalizerException);
    }
    catch (XMLSecurityException localXMLSecurityException)
    {
      throw new XMLSignatureException("empty", localXMLSecurityException);
    }
  }
  
  public XMLSignatureInput getReferencedContentBeforeTransformsItem(int paramInt)
    throws XMLSecurityException
  {
    return item(paramInt).getContentsBeforeTransformation();
  }
  
  public XMLSignatureInput getReferencedContentAfterTransformsItem(int paramInt)
    throws XMLSecurityException
  {
    return item(paramInt).getContentsAfterTransformation();
  }
  
  public int getSignedContentLength()
  {
    return getLength();
  }
  
  public String getBaseLocalName()
  {
    return "Manifest";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\signature\Manifest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */