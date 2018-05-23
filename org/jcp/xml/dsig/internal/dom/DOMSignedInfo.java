package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.UnsyncBufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.XMLSignatureException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMSignedInfo
  extends DOMStructure
  implements SignedInfo
{
  private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
  private List<Reference> references;
  private CanonicalizationMethod canonicalizationMethod;
  private SignatureMethod signatureMethod;
  private String id;
  private Document ownerDoc;
  private Element localSiElem;
  private InputStream canonData;
  
  public DOMSignedInfo(CanonicalizationMethod paramCanonicalizationMethod, SignatureMethod paramSignatureMethod, List<? extends Reference> paramList)
  {
    if ((paramCanonicalizationMethod == null) || (paramSignatureMethod == null) || (paramList == null)) {
      throw new NullPointerException();
    }
    canonicalizationMethod = paramCanonicalizationMethod;
    signatureMethod = paramSignatureMethod;
    references = Collections.unmodifiableList(new ArrayList(paramList));
    if (references.isEmpty()) {
      throw new IllegalArgumentException("list of references must contain at least one entry");
    }
    int i = 0;
    int j = references.size();
    while (i < j)
    {
      Object localObject = references.get(i);
      if (!(localObject instanceof Reference)) {
        throw new ClassCastException("list of references contains an illegal type");
      }
      i++;
    }
  }
  
  public DOMSignedInfo(CanonicalizationMethod paramCanonicalizationMethod, SignatureMethod paramSignatureMethod, List<? extends Reference> paramList, String paramString)
  {
    this(paramCanonicalizationMethod, paramSignatureMethod, paramList);
    id = paramString;
  }
  
  public DOMSignedInfo(Element paramElement, XMLCryptoContext paramXMLCryptoContext, Provider paramProvider)
    throws MarshalException
  {
    localSiElem = paramElement;
    ownerDoc = paramElement.getOwnerDocument();
    id = DOMUtils.getAttributeValue(paramElement, "Id");
    Element localElement1 = DOMUtils.getFirstChildElement(paramElement, "CanonicalizationMethod");
    canonicalizationMethod = new DOMCanonicalizationMethod(localElement1, paramXMLCryptoContext, paramProvider);
    Element localElement2 = DOMUtils.getNextSiblingElement(localElement1, "SignatureMethod");
    signatureMethod = DOMSignatureMethod.unmarshal(localElement2);
    boolean bool = Utils.secureValidation(paramXMLCryptoContext);
    String str1 = signatureMethod.getAlgorithm();
    if ((bool) && (Policy.restrictAlg(str1))) {
      throw new MarshalException("It is forbidden to use algorithm " + str1 + " when secure validation is enabled");
    }
    ArrayList localArrayList = new ArrayList(5);
    Element localElement3 = DOMUtils.getNextSiblingElement(localElement2, "Reference");
    localArrayList.add(new DOMReference(localElement3, paramXMLCryptoContext, paramProvider));
    for (localElement3 = DOMUtils.getNextSiblingElement(localElement3); localElement3 != null; localElement3 = DOMUtils.getNextSiblingElement(localElement3))
    {
      String str2 = localElement3.getLocalName();
      if (!str2.equals("Reference")) {
        throw new MarshalException("Invalid element name: " + str2 + ", expected Reference");
      }
      localArrayList.add(new DOMReference(localElement3, paramXMLCryptoContext, paramProvider));
      if ((bool) && (Policy.restrictNumReferences(localArrayList.size())))
      {
        String str3 = "A maximum of " + Policy.maxReferences() + " references per Manifest are allowed when secure validation is enabled";
        throw new MarshalException(str3);
      }
    }
    references = Collections.unmodifiableList(localArrayList);
  }
  
  public CanonicalizationMethod getCanonicalizationMethod()
  {
    return canonicalizationMethod;
  }
  
  public SignatureMethod getSignatureMethod()
  {
    return signatureMethod;
  }
  
  public String getId()
  {
    return id;
  }
  
  public List getReferences()
  {
    return references;
  }
  
  public InputStream getCanonicalizedData()
  {
    return canonData;
  }
  
  public void canonicalize(XMLCryptoContext paramXMLCryptoContext, ByteArrayOutputStream paramByteArrayOutputStream)
    throws XMLSignatureException
  {
    if (paramXMLCryptoContext == null) {
      throw new NullPointerException("context cannot be null");
    }
    UnsyncBufferedOutputStream localUnsyncBufferedOutputStream = new UnsyncBufferedOutputStream(paramByteArrayOutputStream);
    DOMSubTreeData localDOMSubTreeData = new DOMSubTreeData(localSiElem, true);
    try
    {
      ((DOMCanonicalizationMethod)canonicalizationMethod).canonicalize(localDOMSubTreeData, paramXMLCryptoContext, localUnsyncBufferedOutputStream);
    }
    catch (TransformException localTransformException)
    {
      throw new XMLSignatureException(localTransformException);
    }
    try
    {
      localUnsyncBufferedOutputStream.flush();
    }
    catch (IOException localIOException1)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, localIOException1.getMessage(), localIOException1);
      }
    }
    byte[] arrayOfByte = paramByteArrayOutputStream.toByteArray();
    if (log.isLoggable(Level.FINE))
    {
      log.log(Level.FINE, "Canonicalized SignedInfo:");
      StringBuilder localStringBuilder = new StringBuilder(arrayOfByte.length);
      for (int i = 0; i < arrayOfByte.length; i++) {
        localStringBuilder.append((char)arrayOfByte[i]);
      }
      log.log(Level.FINE, localStringBuilder.toString());
      log.log(Level.FINE, "Data to be signed/verified:" + Base64.encode(arrayOfByte));
    }
    canonData = new ByteArrayInputStream(arrayOfByte);
    try
    {
      localUnsyncBufferedOutputStream.close();
    }
    catch (IOException localIOException2)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, localIOException2.getMessage(), localIOException2);
      }
    }
  }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext)
    throws MarshalException
  {
    ownerDoc = DOMUtils.getOwnerDocument(paramNode);
    Element localElement = DOMUtils.createElement(ownerDoc, "SignedInfo", "http://www.w3.org/2000/09/xmldsig#", paramString);
    DOMCanonicalizationMethod localDOMCanonicalizationMethod = (DOMCanonicalizationMethod)canonicalizationMethod;
    localDOMCanonicalizationMethod.marshal(localElement, paramString, paramDOMCryptoContext);
    ((DOMStructure)signatureMethod).marshal(localElement, paramString, paramDOMCryptoContext);
    Iterator localIterator = references.iterator();
    while (localIterator.hasNext())
    {
      Reference localReference = (Reference)localIterator.next();
      ((DOMReference)localReference).marshal(localElement, paramString, paramDOMCryptoContext);
    }
    DOMUtils.setAttributeID(localElement, "Id", id);
    paramNode.appendChild(localElement);
    localSiElem = localElement;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof SignedInfo)) {
      return false;
    }
    SignedInfo localSignedInfo = (SignedInfo)paramObject;
    boolean bool = id == null ? false : localSignedInfo.getId() == null ? true : id.equals(localSignedInfo.getId());
    return (canonicalizationMethod.equals(localSignedInfo.getCanonicalizationMethod())) && (signatureMethod.equals(localSignedInfo.getSignatureMethod())) && (references.equals(localSignedInfo.getReferences())) && (bool);
  }
  
  public int hashCode()
  {
    int i = 17;
    if (id != null) {
      i = 31 * i + id.hashCode();
    }
    i = 31 * i + canonicalizationMethod.hashCode();
    i = 31 * i + signatureMethod.hashCode();
    i = 31 * i + references.hashCode();
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMSignedInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */