package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.Init;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelector.Purpose;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.Manifest;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignature.SignatureValue;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMXMLSignature
  extends DOMStructure
  implements XMLSignature
{
  private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
  private String id;
  private XMLSignature.SignatureValue sv;
  private KeyInfo ki;
  private List<XMLObject> objects;
  private SignedInfo si;
  private Document ownerDoc = null;
  private Element localSigElem = null;
  private Element sigElem = null;
  private boolean validationStatus;
  private boolean validated = false;
  private KeySelectorResult ksr;
  private HashMap<String, XMLStructure> signatureIdMap;
  
  public DOMXMLSignature(SignedInfo paramSignedInfo, KeyInfo paramKeyInfo, List<? extends XMLObject> paramList, String paramString1, String paramString2)
  {
    if (paramSignedInfo == null) {
      throw new NullPointerException("signedInfo cannot be null");
    }
    si = paramSignedInfo;
    id = paramString1;
    sv = new DOMSignatureValue(paramString2);
    if (paramList == null)
    {
      objects = Collections.emptyList();
    }
    else
    {
      objects = Collections.unmodifiableList(new ArrayList(paramList));
      int i = 0;
      int j = objects.size();
      while (i < j)
      {
        if (!(objects.get(i) instanceof XMLObject)) {
          throw new ClassCastException("objs[" + i + "] is not an XMLObject");
        }
        i++;
      }
    }
    ki = paramKeyInfo;
  }
  
  public DOMXMLSignature(Element paramElement, XMLCryptoContext paramXMLCryptoContext, Provider paramProvider)
    throws MarshalException
  {
    localSigElem = paramElement;
    ownerDoc = localSigElem.getOwnerDocument();
    id = DOMUtils.getAttributeValue(localSigElem, "Id");
    Element localElement1 = DOMUtils.getFirstChildElement(localSigElem, "SignedInfo");
    si = new DOMSignedInfo(localElement1, paramXMLCryptoContext, paramProvider);
    Element localElement2 = DOMUtils.getNextSiblingElement(localElement1, "SignatureValue");
    sv = new DOMSignatureValue(localElement2, paramXMLCryptoContext);
    Element localElement3 = DOMUtils.getNextSiblingElement(localElement2);
    if ((localElement3 != null) && (localElement3.getLocalName().equals("KeyInfo")))
    {
      ki = new DOMKeyInfo(localElement3, paramXMLCryptoContext, paramProvider);
      localElement3 = DOMUtils.getNextSiblingElement(localElement3);
    }
    if (localElement3 == null)
    {
      objects = Collections.emptyList();
    }
    else
    {
      ArrayList localArrayList = new ArrayList();
      while (localElement3 != null)
      {
        String str = localElement3.getLocalName();
        if (!str.equals("Object")) {
          throw new MarshalException("Invalid element name: " + str + ", expected KeyInfo or Object");
        }
        localArrayList.add(new DOMXMLObject(localElement3, paramXMLCryptoContext, paramProvider));
        localElement3 = DOMUtils.getNextSiblingElement(localElement3);
      }
      objects = Collections.unmodifiableList(localArrayList);
    }
  }
  
  public String getId()
  {
    return id;
  }
  
  public KeyInfo getKeyInfo()
  {
    return ki;
  }
  
  public SignedInfo getSignedInfo()
  {
    return si;
  }
  
  public List getObjects()
  {
    return objects;
  }
  
  public XMLSignature.SignatureValue getSignatureValue()
  {
    return sv;
  }
  
  public KeySelectorResult getKeySelectorResult()
  {
    return ksr;
  }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext)
    throws MarshalException
  {
    marshal(paramNode, null, paramString, paramDOMCryptoContext);
  }
  
  public void marshal(Node paramNode1, Node paramNode2, String paramString, DOMCryptoContext paramDOMCryptoContext)
    throws MarshalException
  {
    ownerDoc = DOMUtils.getOwnerDocument(paramNode1);
    sigElem = DOMUtils.createElement(ownerDoc, "Signature", "http://www.w3.org/2000/09/xmldsig#", paramString);
    if ((paramString == null) || (paramString.length() == 0)) {
      sigElem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2000/09/xmldsig#");
    } else {
      sigElem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + paramString, "http://www.w3.org/2000/09/xmldsig#");
    }
    ((DOMSignedInfo)si).marshal(sigElem, paramString, paramDOMCryptoContext);
    ((DOMSignatureValue)sv).marshal(sigElem, paramString, paramDOMCryptoContext);
    if (ki != null) {
      ((DOMKeyInfo)ki).marshal(sigElem, null, paramString, paramDOMCryptoContext);
    }
    int i = 0;
    int j = objects.size();
    while (i < j)
    {
      ((DOMXMLObject)objects.get(i)).marshal(sigElem, paramString, paramDOMCryptoContext);
      i++;
    }
    DOMUtils.setAttributeID(sigElem, "Id", id);
    paramNode1.insertBefore(sigElem, paramNode2);
  }
  
  public boolean validate(XMLValidateContext paramXMLValidateContext)
    throws XMLSignatureException
  {
    if (paramXMLValidateContext == null) {
      throw new NullPointerException("validateContext is null");
    }
    if (!(paramXMLValidateContext instanceof DOMValidateContext)) {
      throw new ClassCastException("validateContext must be of type DOMValidateContext");
    }
    if (validated) {
      return validationStatus;
    }
    boolean bool1 = sv.validate(paramXMLValidateContext);
    if (!bool1)
    {
      validationStatus = false;
      validated = true;
      return validationStatus;
    }
    List localList1 = si.getReferences();
    boolean bool2 = true;
    int i = 0;
    int j = localList1.size();
    while ((bool2) && (i < j))
    {
      Reference localReference1 = (Reference)localList1.get(i);
      boolean bool3 = localReference1.validate(paramXMLValidateContext);
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "Reference[" + localReference1.getURI() + "] is valid: " + bool3);
      }
      bool2 &= bool3;
      i++;
    }
    if (!bool2)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "Couldn't validate the References");
      }
      validationStatus = false;
      validated = true;
      return validationStatus;
    }
    i = 1;
    if (Boolean.TRUE.equals(paramXMLValidateContext.getProperty("org.jcp.xml.dsig.validateManifests")))
    {
      j = 0;
      int k = objects.size();
      while ((i != 0) && (j < k))
      {
        XMLObject localXMLObject = (XMLObject)objects.get(j);
        List localList2 = localXMLObject.getContent();
        int m = localList2.size();
        for (int n = 0; (i != 0) && (n < m); n++)
        {
          XMLStructure localXMLStructure = (XMLStructure)localList2.get(n);
          if ((localXMLStructure instanceof Manifest))
          {
            if (log.isLoggable(Level.FINE)) {
              log.log(Level.FINE, "validating manifest");
            }
            Manifest localManifest = (Manifest)localXMLStructure;
            List localList3 = localManifest.getReferences();
            int i1 = localList3.size();
            for (int i2 = 0; (i != 0) && (i2 < i1); i2++)
            {
              Reference localReference2 = (Reference)localList3.get(i2);
              int i3 = localReference2.validate(paramXMLValidateContext);
              if (log.isLoggable(Level.FINE)) {
                log.log(Level.FINE, "Manifest ref[" + localReference2.getURI() + "] is valid: " + i3);
              }
              i &= i3;
            }
          }
        }
        j++;
      }
    }
    validationStatus = i;
    validated = true;
    return validationStatus;
  }
  
  public void sign(XMLSignContext paramXMLSignContext)
    throws MarshalException, XMLSignatureException
  {
    if (paramXMLSignContext == null) {
      throw new NullPointerException("signContext cannot be null");
    }
    DOMSignContext localDOMSignContext = (DOMSignContext)paramXMLSignContext;
    marshal(localDOMSignContext.getParent(), localDOMSignContext.getNextSibling(), DOMUtils.getSignaturePrefix(localDOMSignContext), localDOMSignContext);
    ArrayList localArrayList = new ArrayList();
    signatureIdMap = new HashMap();
    signatureIdMap.put(id, this);
    signatureIdMap.put(si.getId(), si);
    List localList1 = si.getReferences();
    Object localObject1 = localList1.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Reference)((Iterator)localObject1).next();
      signatureIdMap.put(((Reference)localObject2).getId(), localObject2);
    }
    localObject1 = objects.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (XMLObject)((Iterator)localObject1).next();
      signatureIdMap.put(((XMLObject)localObject2).getId(), localObject2);
      List localList2 = ((XMLObject)localObject2).getContent();
      Iterator localIterator1 = localList2.iterator();
      while (localIterator1.hasNext())
      {
        XMLStructure localXMLStructure = (XMLStructure)localIterator1.next();
        if ((localXMLStructure instanceof Manifest))
        {
          Manifest localManifest = (Manifest)localXMLStructure;
          signatureIdMap.put(localManifest.getId(), localManifest);
          List localList3 = localManifest.getReferences();
          Iterator localIterator2 = localList3.iterator();
          while (localIterator2.hasNext())
          {
            Reference localReference = (Reference)localIterator2.next();
            localArrayList.add(localReference);
            signatureIdMap.put(localReference.getId(), localReference);
          }
        }
      }
    }
    localArrayList.addAll(localList1);
    localObject1 = localArrayList.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Reference)((Iterator)localObject1).next();
      digestReference((DOMReference)localObject2, paramXMLSignContext);
    }
    localObject1 = localArrayList.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Reference)((Iterator)localObject1).next();
      if (!((DOMReference)localObject2).isDigested()) {
        ((DOMReference)localObject2).digest(paramXMLSignContext);
      }
    }
    localObject1 = null;
    Object localObject2 = null;
    try
    {
      localObject2 = paramXMLSignContext.getKeySelector().select(ki, KeySelector.Purpose.SIGN, si.getSignatureMethod(), paramXMLSignContext);
      localObject1 = ((KeySelectorResult)localObject2).getKey();
      if (localObject1 == null) {
        throw new XMLSignatureException("the keySelector did not find a signing key");
      }
    }
    catch (KeySelectorException localKeySelectorException)
    {
      throw new XMLSignatureException("cannot find signing key", localKeySelectorException);
    }
    try
    {
      byte[] arrayOfByte = ((AbstractDOMSignatureMethod)si.getSignatureMethod()).sign((Key)localObject1, si, paramXMLSignContext);
      ((DOMSignatureValue)sv).setValue(arrayOfByte);
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      throw new XMLSignatureException(localInvalidKeyException);
    }
    localSigElem = sigElem;
    ksr = ((KeySelectorResult)localObject2);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof XMLSignature)) {
      return false;
    }
    XMLSignature localXMLSignature = (XMLSignature)paramObject;
    boolean bool1 = id == null ? false : localXMLSignature.getId() == null ? true : id.equals(localXMLSignature.getId());
    boolean bool2 = ki == null ? false : localXMLSignature.getKeyInfo() == null ? true : ki.equals(localXMLSignature.getKeyInfo());
    return (bool1) && (bool2) && (sv.equals(localXMLSignature.getSignatureValue())) && (si.equals(localXMLSignature.getSignedInfo())) && (objects.equals(localXMLSignature.getObjects()));
  }
  
  public int hashCode()
  {
    int i = 17;
    if (id != null) {
      i = 31 * i + id.hashCode();
    }
    if (ki != null) {
      i = 31 * i + ki.hashCode();
    }
    i = 31 * i + sv.hashCode();
    i = 31 * i + si.hashCode();
    i = 31 * i + objects.hashCode();
    return i;
  }
  
  private void digestReference(DOMReference paramDOMReference, XMLSignContext paramXMLSignContext)
    throws XMLSignatureException
  {
    if (paramDOMReference.isDigested()) {
      return;
    }
    String str1 = paramDOMReference.getURI();
    if (Utils.sameDocumentURI(str1))
    {
      String str2 = Utils.parseIdFromSameDocumentURI(str1);
      Object localObject1;
      Object localObject2;
      Object localObject3;
      if ((str2 != null) && (signatureIdMap.containsKey(str2)))
      {
        localObject1 = (XMLStructure)signatureIdMap.get(str2);
        if ((localObject1 instanceof DOMReference))
        {
          digestReference((DOMReference)localObject1, paramXMLSignContext);
        }
        else if ((localObject1 instanceof Manifest))
        {
          localObject2 = (Manifest)localObject1;
          localObject3 = ((Manifest)localObject2).getReferences();
          int i = 0;
          int j = ((List)localObject3).size();
          while (i < j)
          {
            digestReference((DOMReference)((List)localObject3).get(i), paramXMLSignContext);
            i++;
          }
        }
      }
      if (str1.length() == 0)
      {
        localObject1 = paramDOMReference.getTransforms();
        localObject2 = ((List)localObject1).iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localObject3 = (Transform)((Iterator)localObject2).next();
          String str3 = ((Transform)localObject3).getAlgorithm();
          if ((str3.equals("http://www.w3.org/TR/1999/REC-xpath-19991116")) || (str3.equals("http://www.w3.org/2002/06/xmldsig-filter2"))) {
            return;
          }
        }
      }
    }
    paramDOMReference.digest(paramXMLSignContext);
  }
  
  static
  {
    Init.init();
  }
  
  public class DOMSignatureValue
    extends DOMStructure
    implements XMLSignature.SignatureValue
  {
    private String id;
    private byte[] value;
    private String valueBase64;
    private Element sigValueElem;
    private boolean validated = false;
    private boolean validationStatus;
    
    DOMSignatureValue(String paramString)
    {
      id = paramString;
    }
    
    DOMSignatureValue(Element paramElement, XMLCryptoContext paramXMLCryptoContext)
      throws MarshalException
    {
      try
      {
        value = Base64.decode(paramElement);
      }
      catch (Base64DecodingException localBase64DecodingException)
      {
        throw new MarshalException(localBase64DecodingException);
      }
      Attr localAttr = paramElement.getAttributeNodeNS(null, "Id");
      if (localAttr != null)
      {
        id = localAttr.getValue();
        paramElement.setIdAttributeNode(localAttr, true);
      }
      else
      {
        id = null;
      }
      sigValueElem = paramElement;
    }
    
    public String getId()
    {
      return id;
    }
    
    public byte[] getValue()
    {
      return value == null ? null : (byte[])value.clone();
    }
    
    public boolean validate(XMLValidateContext paramXMLValidateContext)
      throws XMLSignatureException
    {
      if (paramXMLValidateContext == null) {
        throw new NullPointerException("context cannot be null");
      }
      if (validated) {
        return validationStatus;
      }
      SignatureMethod localSignatureMethod = si.getSignatureMethod();
      Key localKey = null;
      KeySelectorResult localKeySelectorResult;
      try
      {
        localKeySelectorResult = paramXMLValidateContext.getKeySelector().select(ki, KeySelector.Purpose.VERIFY, localSignatureMethod, paramXMLValidateContext);
        localKey = localKeySelectorResult.getKey();
        if (localKey == null) {
          throw new XMLSignatureException("the keyselector did not find a validation key");
        }
      }
      catch (KeySelectorException localKeySelectorException)
      {
        throw new XMLSignatureException("cannot find validation key", localKeySelectorException);
      }
      try
      {
        validationStatus = ((AbstractDOMSignatureMethod)localSignatureMethod).verify(localKey, si, value, paramXMLValidateContext);
      }
      catch (Exception localException)
      {
        throw new XMLSignatureException(localException);
      }
      validated = true;
      ksr = localKeySelectorResult;
      return validationStatus;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (!(paramObject instanceof XMLSignature.SignatureValue)) {
        return false;
      }
      XMLSignature.SignatureValue localSignatureValue = (XMLSignature.SignatureValue)paramObject;
      boolean bool = id == null ? false : localSignatureValue.getId() == null ? true : id.equals(localSignatureValue.getId());
      return bool;
    }
    
    public int hashCode()
    {
      int i = 17;
      if (id != null) {
        i = 31 * i + id.hashCode();
      }
      return i;
    }
    
    public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext)
      throws MarshalException
    {
      sigValueElem = DOMUtils.createElement(ownerDoc, "SignatureValue", "http://www.w3.org/2000/09/xmldsig#", paramString);
      if (valueBase64 != null) {
        sigValueElem.appendChild(ownerDoc.createTextNode(valueBase64));
      }
      DOMUtils.setAttributeID(sigValueElem, "Id", id);
      paramNode.appendChild(sigValueElem);
    }
    
    void setValue(byte[] paramArrayOfByte)
    {
      value = paramArrayOfByte;
      valueBase64 = Base64.encode(paramArrayOfByte);
      sigValueElem.appendChild(ownerDoc.createTextNode(valueBase64));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMXMLSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */