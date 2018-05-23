package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.UnsyncBufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.NodeSetData;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dom.DOMURIReference;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.TransformService;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLValidateContext;
import org.jcp.xml.dsig.internal.DigesterOutputStream;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMReference
  extends DOMStructure
  implements Reference, DOMURIReference
{
  private static boolean useC14N11 = ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
  {
    public Boolean run()
    {
      return Boolean.valueOf(Boolean.getBoolean("com.sun.org.apache.xml.internal.security.useC14N11"));
    }
  })).booleanValue();
  private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
  private final DigestMethod digestMethod;
  private final String id;
  private final List<Transform> transforms;
  private List<Transform> allTransforms;
  private final Data appliedTransformData;
  private Attr here;
  private final String uri;
  private final String type;
  private byte[] digestValue;
  private byte[] calcDigestValue;
  private Element refElem;
  private boolean digested = false;
  private boolean validated = false;
  private boolean validationStatus;
  private Data derefData;
  private InputStream dis;
  private MessageDigest md;
  private Provider provider;
  
  public DOMReference(String paramString1, String paramString2, DigestMethod paramDigestMethod, List<? extends Transform> paramList, String paramString3, Provider paramProvider)
  {
    this(paramString1, paramString2, paramDigestMethod, null, null, paramList, paramString3, null, paramProvider);
  }
  
  public DOMReference(String paramString1, String paramString2, DigestMethod paramDigestMethod, List<? extends Transform> paramList1, Data paramData, List<? extends Transform> paramList2, String paramString3, Provider paramProvider)
  {
    this(paramString1, paramString2, paramDigestMethod, paramList1, paramData, paramList2, paramString3, null, paramProvider);
  }
  
  public DOMReference(String paramString1, String paramString2, DigestMethod paramDigestMethod, List<? extends Transform> paramList1, Data paramData, List<? extends Transform> paramList2, String paramString3, byte[] paramArrayOfByte, Provider paramProvider)
  {
    if (paramDigestMethod == null) {
      throw new NullPointerException("DigestMethod must be non-null");
    }
    int i;
    int j;
    if (paramList1 == null)
    {
      allTransforms = new ArrayList();
    }
    else
    {
      allTransforms = new ArrayList(paramList1);
      i = 0;
      j = allTransforms.size();
      while (i < j)
      {
        if (!(allTransforms.get(i) instanceof Transform)) {
          throw new ClassCastException("appliedTransforms[" + i + "] is not a valid type");
        }
        i++;
      }
    }
    if (paramList2 == null)
    {
      transforms = Collections.emptyList();
    }
    else
    {
      transforms = new ArrayList(paramList2);
      i = 0;
      j = transforms.size();
      while (i < j)
      {
        if (!(transforms.get(i) instanceof Transform)) {
          throw new ClassCastException("transforms[" + i + "] is not a valid type");
        }
        i++;
      }
      allTransforms.addAll(transforms);
    }
    digestMethod = paramDigestMethod;
    uri = paramString1;
    if ((paramString1 != null) && (!paramString1.equals(""))) {
      try
      {
        new URI(paramString1);
      }
      catch (URISyntaxException localURISyntaxException)
      {
        throw new IllegalArgumentException(localURISyntaxException.getMessage());
      }
    }
    type = paramString2;
    id = paramString3;
    if (paramArrayOfByte != null)
    {
      digestValue = ((byte[])paramArrayOfByte.clone());
      digested = true;
    }
    appliedTransformData = paramData;
    provider = paramProvider;
  }
  
  public DOMReference(Element paramElement, XMLCryptoContext paramXMLCryptoContext, Provider paramProvider)
    throws MarshalException
  {
    boolean bool = Utils.secureValidation(paramXMLCryptoContext);
    Element localElement1 = DOMUtils.getFirstChildElement(paramElement);
    ArrayList localArrayList = new ArrayList(5);
    if (localElement1.getLocalName().equals("Transforms"))
    {
      localElement2 = DOMUtils.getFirstChildElement(localElement1, "Transform");
      localArrayList.add(new DOMTransform(localElement2, paramXMLCryptoContext, paramProvider));
      for (localElement2 = DOMUtils.getNextSiblingElement(localElement2); localElement2 != null; localElement2 = DOMUtils.getNextSiblingElement(localElement2))
      {
        str = localElement2.getLocalName();
        if (!str.equals("Transform")) {
          throw new MarshalException("Invalid element name: " + str + ", expected Transform");
        }
        localArrayList.add(new DOMTransform(localElement2, paramXMLCryptoContext, paramProvider));
        if ((bool) && (Policy.restrictNumTransforms(localArrayList.size())))
        {
          localObject = "A maximum of " + Policy.maxTransforms() + " transforms per Reference are allowed when secure validation is enabled";
          throw new MarshalException((String)localObject);
        }
      }
      localElement1 = DOMUtils.getNextSiblingElement(localElement1);
    }
    if (!localElement1.getLocalName().equals("DigestMethod")) {
      throw new MarshalException("Invalid element name: " + localElement1.getLocalName() + ", expected DigestMethod");
    }
    Element localElement2 = localElement1;
    digestMethod = DOMDigestMethod.unmarshal(localElement2);
    String str = digestMethod.getAlgorithm();
    if ((bool) && (Policy.restrictAlg(str))) {
      throw new MarshalException("It is forbidden to use algorithm " + str + " when secure validation is enabled");
    }
    Object localObject = DOMUtils.getNextSiblingElement(localElement2, "DigestValue");
    try
    {
      digestValue = Base64.decode((Element)localObject);
    }
    catch (Base64DecodingException localBase64DecodingException)
    {
      throw new MarshalException(localBase64DecodingException);
    }
    if (DOMUtils.getNextSiblingElement((Node)localObject) != null) {
      throw new MarshalException("Unexpected element after DigestValue element");
    }
    uri = DOMUtils.getAttributeValue(paramElement, "URI");
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
    type = DOMUtils.getAttributeValue(paramElement, "Type");
    here = paramElement.getAttributeNodeNS(null, "URI");
    refElem = paramElement;
    transforms = localArrayList;
    allTransforms = localArrayList;
    appliedTransformData = null;
    provider = paramProvider;
  }
  
  public DigestMethod getDigestMethod()
  {
    return digestMethod;
  }
  
  public String getId()
  {
    return id;
  }
  
  public String getURI()
  {
    return uri;
  }
  
  public String getType()
  {
    return type;
  }
  
  public List getTransforms()
  {
    return Collections.unmodifiableList(allTransforms);
  }
  
  public byte[] getDigestValue()
  {
    return digestValue == null ? null : (byte[])digestValue.clone();
  }
  
  public byte[] getCalculatedDigestValue()
  {
    return calcDigestValue == null ? null : (byte[])calcDigestValue.clone();
  }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext)
    throws MarshalException
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Marshalling Reference");
    }
    Document localDocument = DOMUtils.getOwnerDocument(paramNode);
    refElem = DOMUtils.createElement(localDocument, "Reference", "http://www.w3.org/2000/09/xmldsig#", paramString);
    DOMUtils.setAttributeID(refElem, "Id", id);
    DOMUtils.setAttribute(refElem, "URI", uri);
    DOMUtils.setAttribute(refElem, "Type", type);
    if (!allTransforms.isEmpty())
    {
      localElement = DOMUtils.createElement(localDocument, "Transforms", "http://www.w3.org/2000/09/xmldsig#", paramString);
      refElem.appendChild(localElement);
      Iterator localIterator = allTransforms.iterator();
      while (localIterator.hasNext())
      {
        Transform localTransform = (Transform)localIterator.next();
        ((DOMStructure)localTransform).marshal(localElement, paramString, paramDOMCryptoContext);
      }
    }
    ((DOMDigestMethod)digestMethod).marshal(refElem, paramString, paramDOMCryptoContext);
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Adding digestValueElem");
    }
    Element localElement = DOMUtils.createElement(localDocument, "DigestValue", "http://www.w3.org/2000/09/xmldsig#", paramString);
    if (digestValue != null) {
      localElement.appendChild(localDocument.createTextNode(Base64.encode(digestValue)));
    }
    refElem.appendChild(localElement);
    paramNode.appendChild(refElem);
    here = refElem.getAttributeNodeNS(null, "URI");
  }
  
  public void digest(XMLSignContext paramXMLSignContext)
    throws XMLSignatureException
  {
    Data localData = null;
    if (appliedTransformData == null) {
      localData = dereference(paramXMLSignContext);
    } else {
      localData = appliedTransformData;
    }
    digestValue = transform(localData, paramXMLSignContext);
    String str = Base64.encode(digestValue);
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Reference object uri = " + uri);
    }
    Element localElement = DOMUtils.getLastChildElement(refElem);
    if (localElement == null) {
      throw new XMLSignatureException("DigestValue element expected");
    }
    DOMUtils.removeAllChildren(localElement);
    localElement.appendChild(refElem.getOwnerDocument().createTextNode(str));
    digested = true;
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Reference digesting completed");
    }
  }
  
  public boolean validate(XMLValidateContext paramXMLValidateContext)
    throws XMLSignatureException
  {
    if (paramXMLValidateContext == null) {
      throw new NullPointerException("validateContext cannot be null");
    }
    if (validated) {
      return validationStatus;
    }
    Data localData = dereference(paramXMLValidateContext);
    calcDigestValue = transform(localData, paramXMLValidateContext);
    if (log.isLoggable(Level.FINE))
    {
      log.log(Level.FINE, "Expected digest: " + Base64.encode(digestValue));
      log.log(Level.FINE, "Actual digest: " + Base64.encode(calcDigestValue));
    }
    validationStatus = Arrays.equals(digestValue, calcDigestValue);
    validated = true;
    return validationStatus;
  }
  
  public Data getDereferencedData()
  {
    return derefData;
  }
  
  public InputStream getDigestInputStream()
  {
    return dis;
  }
  
  private Data dereference(XMLCryptoContext paramXMLCryptoContext)
    throws XMLSignatureException
  {
    Data localData = null;
    URIDereferencer localURIDereferencer = paramXMLCryptoContext.getURIDereferencer();
    if (localURIDereferencer == null) {
      localURIDereferencer = DOMURIDereferencer.INSTANCE;
    }
    try
    {
      localData = localURIDereferencer.dereference(this, paramXMLCryptoContext);
      if (log.isLoggable(Level.FINE))
      {
        log.log(Level.FINE, "URIDereferencer class name: " + localURIDereferencer.getClass().getName());
        log.log(Level.FINE, "Data class name: " + localData.getClass().getName());
      }
    }
    catch (URIReferenceException localURIReferenceException)
    {
      throw new XMLSignatureException(localURIReferenceException);
    }
    return localData;
  }
  
  private byte[] transform(Data paramData, XMLCryptoContext paramXMLCryptoContext)
    throws XMLSignatureException
  {
    if (md == null) {
      try
      {
        md = MessageDigest.getInstance(((DOMDigestMethod)digestMethod).getMessageDigestAlgorithm());
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException1)
      {
        throw new XMLSignatureException(localNoSuchAlgorithmException1);
      }
    }
    md.reset();
    Boolean localBoolean = (Boolean)paramXMLCryptoContext.getProperty("javax.xml.crypto.dsig.cacheReference");
    DigesterOutputStream localDigesterOutputStream;
    if ((localBoolean != null) && (localBoolean.booleanValue()))
    {
      derefData = copyDerefData(paramData);
      localDigesterOutputStream = new DigesterOutputStream(md, true);
    }
    else
    {
      localDigesterOutputStream = new DigesterOutputStream(md);
    }
    UnsyncBufferedOutputStream localUnsyncBufferedOutputStream = null;
    Data localData = paramData;
    try
    {
      localUnsyncBufferedOutputStream = new UnsyncBufferedOutputStream(localDigesterOutputStream);
      int i = 0;
      int j = transforms.size();
      Object localObject2;
      while (i < j)
      {
        localObject2 = (DOMTransform)transforms.get(i);
        if (i < j - 1) {
          localData = ((DOMTransform)localObject2).transform(localData, paramXMLCryptoContext);
        } else {
          localData = ((DOMTransform)localObject2).transform(localData, paramXMLCryptoContext, localUnsyncBufferedOutputStream);
        }
        i++;
      }
      if (localData != null)
      {
        boolean bool = useC14N11;
        localObject2 = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
        Object localObject3;
        if ((paramXMLCryptoContext instanceof XMLSignContext)) {
          if (!bool)
          {
            localObject3 = (Boolean)paramXMLCryptoContext.getProperty("com.sun.org.apache.xml.internal.security.useC14N11");
            bool = (localObject3 != null) && (((Boolean)localObject3).booleanValue());
            if (bool) {
              localObject2 = "http://www.w3.org/2006/12/xml-c14n11";
            }
          }
          else
          {
            localObject2 = "http://www.w3.org/2006/12/xml-c14n11";
          }
        }
        if ((localData instanceof ApacheData))
        {
          localObject1 = ((ApacheData)localData).getXMLSignatureInput();
        }
        else if ((localData instanceof OctetStreamData))
        {
          localObject1 = new XMLSignatureInput(((OctetStreamData)localData).getOctetStream());
        }
        else if ((localData instanceof NodeSetData))
        {
          localObject3 = null;
          if (provider == null) {
            localObject3 = TransformService.getInstance((String)localObject2, "DOM");
          } else {
            try
            {
              localObject3 = TransformService.getInstance((String)localObject2, "DOM", provider);
            }
            catch (NoSuchAlgorithmException localNoSuchAlgorithmException3)
            {
              localObject3 = TransformService.getInstance((String)localObject2, "DOM");
            }
          }
          localData = ((TransformService)localObject3).transform(localData, paramXMLCryptoContext);
          localObject1 = new XMLSignatureInput(((OctetStreamData)localData).getOctetStream());
        }
        else
        {
          throw new XMLSignatureException("unrecognized Data type");
        }
        if (((paramXMLCryptoContext instanceof XMLSignContext)) && (bool) && (!((XMLSignatureInput)localObject1).isOctetStream()) && (!((XMLSignatureInput)localObject1).isOutputStreamSet()))
        {
          localObject3 = null;
          if (provider == null) {
            localObject3 = TransformService.getInstance((String)localObject2, "DOM");
          } else {
            try
            {
              localObject3 = TransformService.getInstance((String)localObject2, "DOM", provider);
            }
            catch (NoSuchAlgorithmException localNoSuchAlgorithmException4)
            {
              localObject3 = TransformService.getInstance((String)localObject2, "DOM");
            }
          }
          DOMTransform localDOMTransform = new DOMTransform((TransformService)localObject3);
          Element localElement = null;
          String str = DOMUtils.getSignaturePrefix(paramXMLCryptoContext);
          if (allTransforms.isEmpty())
          {
            localElement = DOMUtils.createElement(refElem.getOwnerDocument(), "Transforms", "http://www.w3.org/2000/09/xmldsig#", str);
            refElem.insertBefore(localElement, DOMUtils.getFirstChildElement(refElem));
          }
          else
          {
            localElement = DOMUtils.getFirstChildElement(refElem);
          }
          localDOMTransform.marshal(localElement, str, (DOMCryptoContext)paramXMLCryptoContext);
          allTransforms.add(localDOMTransform);
          ((XMLSignatureInput)localObject1).updateOutputStream(localUnsyncBufferedOutputStream, true);
        }
        else
        {
          ((XMLSignatureInput)localObject1).updateOutputStream(localUnsyncBufferedOutputStream);
        }
      }
      localUnsyncBufferedOutputStream.flush();
      if ((localBoolean != null) && (localBoolean.booleanValue())) {
        dis = localDigesterOutputStream.getInputStream();
      }
      Object localObject1 = localDigesterOutputStream.getDigestValue();
      return (byte[])localObject1;
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException2)
    {
      throw new XMLSignatureException(localNoSuchAlgorithmException2);
    }
    catch (TransformException localTransformException)
    {
      throw new XMLSignatureException(localTransformException);
    }
    catch (MarshalException localMarshalException)
    {
      throw new XMLSignatureException(localMarshalException);
    }
    catch (IOException localIOException1)
    {
      throw new XMLSignatureException(localIOException1);
    }
    catch (CanonicalizationException localCanonicalizationException)
    {
      throw new XMLSignatureException(localCanonicalizationException);
    }
    finally
    {
      if (localUnsyncBufferedOutputStream != null) {
        try
        {
          localUnsyncBufferedOutputStream.close();
        }
        catch (IOException localIOException4)
        {
          throw new XMLSignatureException(localIOException4);
        }
      }
      if (localDigesterOutputStream != null) {
        try
        {
          localDigesterOutputStream.close();
        }
        catch (IOException localIOException5)
        {
          throw new XMLSignatureException(localIOException5);
        }
      }
    }
  }
  
  public Node getHere()
  {
    return here;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof Reference)) {
      return false;
    }
    Reference localReference = (Reference)paramObject;
    boolean bool1 = id == null ? false : localReference.getId() == null ? true : id.equals(localReference.getId());
    boolean bool2 = uri == null ? false : localReference.getURI() == null ? true : uri.equals(localReference.getURI());
    boolean bool3 = type == null ? false : localReference.getType() == null ? true : type.equals(localReference.getType());
    boolean bool4 = Arrays.equals(digestValue, localReference.getDigestValue());
    return (digestMethod.equals(localReference.getDigestMethod())) && (bool1) && (bool2) && (bool3) && (allTransforms.equals(localReference.getTransforms())) && (bool4);
  }
  
  public int hashCode()
  {
    int i = 17;
    if (id != null) {
      i = 31 * i + id.hashCode();
    }
    if (uri != null) {
      i = 31 * i + uri.hashCode();
    }
    if (type != null) {
      i = 31 * i + type.hashCode();
    }
    if (digestValue != null) {
      i = 31 * i + Arrays.hashCode(digestValue);
    }
    i = 31 * i + digestMethod.hashCode();
    i = 31 * i + allTransforms.hashCode();
    return i;
  }
  
  boolean isDigested()
  {
    return digested;
  }
  
  private static Data copyDerefData(Data paramData)
  {
    if ((paramData instanceof ApacheData))
    {
      ApacheData localApacheData = (ApacheData)paramData;
      XMLSignatureInput localXMLSignatureInput = localApacheData.getXMLSignatureInput();
      if (localXMLSignatureInput.isNodeSet()) {
        try
        {
          Set localSet = localXMLSignatureInput.getNodeSet();
          new NodeSetData()
          {
            public Iterator iterator()
            {
              return val$s.iterator();
            }
          };
        }
        catch (Exception localException)
        {
          log.log(Level.WARNING, "cannot cache dereferenced data: " + localException);
          return null;
        }
      }
      if (localXMLSignatureInput.isElement()) {
        return new DOMSubTreeData(localXMLSignatureInput.getSubNode(), localXMLSignatureInput.isExcludeComments());
      }
      if ((localXMLSignatureInput.isOctetStream()) || (localXMLSignatureInput.isByteArray())) {
        try
        {
          return new OctetStreamData(localXMLSignatureInput.getOctetStream(), localXMLSignatureInput.getSourceURI(), localXMLSignatureInput.getMIMEType());
        }
        catch (IOException localIOException)
        {
          log.log(Level.WARNING, "cannot cache dereferenced data: " + localIOException);
          return null;
        }
      }
    }
    return paramData;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */