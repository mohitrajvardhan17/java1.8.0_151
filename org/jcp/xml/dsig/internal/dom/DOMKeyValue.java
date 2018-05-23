package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.KeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.PublicKey;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class DOMKeyValue
  extends DOMStructure
  implements KeyValue
{
  private static final String XMLDSIG_11_XMLNS = "http://www.w3.org/2009/xmldsig11#";
  private final PublicKey publicKey;
  
  public DOMKeyValue(PublicKey paramPublicKey)
    throws KeyException
  {
    if (paramPublicKey == null) {
      throw new NullPointerException("key cannot be null");
    }
    publicKey = paramPublicKey;
  }
  
  public DOMKeyValue(Element paramElement)
    throws MarshalException
  {
    publicKey = unmarshalKeyValue(paramElement);
  }
  
  static KeyValue unmarshal(Element paramElement)
    throws MarshalException
  {
    Element localElement = DOMUtils.getFirstChildElement(paramElement);
    if (localElement.getLocalName().equals("DSAKeyValue")) {
      return new DSA(localElement);
    }
    if (localElement.getLocalName().equals("RSAKeyValue")) {
      return new RSA(localElement);
    }
    if (localElement.getLocalName().equals("ECKeyValue")) {
      return new EC(localElement);
    }
    return new Unknown(localElement);
  }
  
  public PublicKey getPublicKey()
    throws KeyException
  {
    if (publicKey == null) {
      throw new KeyException("can't convert KeyValue to PublicKey");
    }
    return publicKey;
  }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext)
    throws MarshalException
  {
    Document localDocument = DOMUtils.getOwnerDocument(paramNode);
    Element localElement = DOMUtils.createElement(localDocument, "KeyValue", "http://www.w3.org/2000/09/xmldsig#", paramString);
    marshalPublicKey(localElement, localDocument, paramString, paramDOMCryptoContext);
    paramNode.appendChild(localElement);
  }
  
  abstract void marshalPublicKey(Node paramNode, Document paramDocument, String paramString, DOMCryptoContext paramDOMCryptoContext)
    throws MarshalException;
  
  abstract PublicKey unmarshalKeyValue(Element paramElement)
    throws MarshalException;
  
  private static PublicKey generatePublicKey(KeyFactory paramKeyFactory, KeySpec paramKeySpec)
  {
    try
    {
      return paramKeyFactory.generatePublic(paramKeySpec);
    }
    catch (InvalidKeySpecException localInvalidKeySpecException) {}
    return null;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof KeyValue)) {
      return false;
    }
    try
    {
      KeyValue localKeyValue = (KeyValue)paramObject;
      if (publicKey == null)
      {
        if (localKeyValue.getPublicKey() != null) {
          return false;
        }
      }
      else if (!publicKey.equals(localKeyValue.getPublicKey())) {
        return false;
      }
    }
    catch (KeyException localKeyException)
    {
      return false;
    }
    return true;
  }
  
  public int hashCode()
  {
    int i = 17;
    if (publicKey != null) {
      i = 31 * i + publicKey.hashCode();
    }
    return i;
  }
  
  static final class DSA
    extends DOMKeyValue
  {
    private DOMCryptoBinary p;
    private DOMCryptoBinary q;
    private DOMCryptoBinary g;
    private DOMCryptoBinary y;
    private DOMCryptoBinary j;
    private KeyFactory dsakf;
    
    DSA(PublicKey paramPublicKey)
      throws KeyException
    {
      super();
      DSAPublicKey localDSAPublicKey = (DSAPublicKey)paramPublicKey;
      DSAParams localDSAParams = localDSAPublicKey.getParams();
      p = new DOMCryptoBinary(localDSAParams.getP());
      q = new DOMCryptoBinary(localDSAParams.getQ());
      g = new DOMCryptoBinary(localDSAParams.getG());
      y = new DOMCryptoBinary(localDSAPublicKey.getY());
    }
    
    DSA(Element paramElement)
      throws MarshalException
    {
      super();
    }
    
    void marshalPublicKey(Node paramNode, Document paramDocument, String paramString, DOMCryptoContext paramDOMCryptoContext)
      throws MarshalException
    {
      Element localElement1 = DOMUtils.createElement(paramDocument, "DSAKeyValue", "http://www.w3.org/2000/09/xmldsig#", paramString);
      Element localElement2 = DOMUtils.createElement(paramDocument, "P", "http://www.w3.org/2000/09/xmldsig#", paramString);
      Element localElement3 = DOMUtils.createElement(paramDocument, "Q", "http://www.w3.org/2000/09/xmldsig#", paramString);
      Element localElement4 = DOMUtils.createElement(paramDocument, "G", "http://www.w3.org/2000/09/xmldsig#", paramString);
      Element localElement5 = DOMUtils.createElement(paramDocument, "Y", "http://www.w3.org/2000/09/xmldsig#", paramString);
      p.marshal(localElement2, paramString, paramDOMCryptoContext);
      q.marshal(localElement3, paramString, paramDOMCryptoContext);
      g.marshal(localElement4, paramString, paramDOMCryptoContext);
      y.marshal(localElement5, paramString, paramDOMCryptoContext);
      localElement1.appendChild(localElement2);
      localElement1.appendChild(localElement3);
      localElement1.appendChild(localElement4);
      localElement1.appendChild(localElement5);
      paramNode.appendChild(localElement1);
    }
    
    PublicKey unmarshalKeyValue(Element paramElement)
      throws MarshalException
    {
      if (dsakf == null) {
        try
        {
          dsakf = KeyFactory.getInstance("DSA");
        }
        catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
        {
          throw new RuntimeException("unable to create DSA KeyFactory: " + localNoSuchAlgorithmException.getMessage());
        }
      }
      Element localElement = DOMUtils.getFirstChildElement(paramElement);
      if (localElement.getLocalName().equals("P"))
      {
        p = new DOMCryptoBinary(localElement.getFirstChild());
        localElement = DOMUtils.getNextSiblingElement(localElement, "Q");
        q = new DOMCryptoBinary(localElement.getFirstChild());
        localElement = DOMUtils.getNextSiblingElement(localElement);
      }
      if (localElement.getLocalName().equals("G"))
      {
        g = new DOMCryptoBinary(localElement.getFirstChild());
        localElement = DOMUtils.getNextSiblingElement(localElement, "Y");
      }
      y = new DOMCryptoBinary(localElement.getFirstChild());
      localElement = DOMUtils.getNextSiblingElement(localElement);
      if ((localElement != null) && (localElement.getLocalName().equals("J"))) {
        j = new DOMCryptoBinary(localElement.getFirstChild());
      }
      DSAPublicKeySpec localDSAPublicKeySpec = new DSAPublicKeySpec(y.getBigNum(), p.getBigNum(), q.getBigNum(), g.getBigNum());
      return DOMKeyValue.generatePublicKey(dsakf, localDSAPublicKeySpec);
    }
  }
  
  static final class EC
    extends DOMKeyValue
  {
    private byte[] ecPublicKey;
    private KeyFactory eckf;
    private ECParameterSpec ecParams;
    private Method encodePoint;
    private Method decodePoint;
    private Method getCurveName;
    private Method getECParameterSpec;
    
    EC(PublicKey paramPublicKey)
      throws KeyException
    {
      super();
      ECPublicKey localECPublicKey = (ECPublicKey)paramPublicKey;
      ECPoint localECPoint = localECPublicKey.getW();
      ecParams = localECPublicKey.getParams();
      try
      {
        AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public Void run()
            throws ClassNotFoundException, NoSuchMethodException
          {
            getMethods();
            return null;
          }
        });
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        throw new KeyException("ECKeyValue not supported", localPrivilegedActionException.getException());
      }
      Object[] arrayOfObject = { localECPoint, ecParams.getCurve() };
      try
      {
        ecPublicKey = ((byte[])encodePoint.invoke(null, arrayOfObject));
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new KeyException(localIllegalAccessException);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        throw new KeyException(localInvocationTargetException);
      }
    }
    
    EC(Element paramElement)
      throws MarshalException
    {
      super();
    }
    
    void getMethods()
      throws ClassNotFoundException, NoSuchMethodException
    {
      Class localClass = Class.forName("sun.security.ec.ECParameters");
      Class[] arrayOfClass = { ECPoint.class, EllipticCurve.class };
      encodePoint = localClass.getMethod("encodePoint", arrayOfClass);
      arrayOfClass = new Class[] { ECParameterSpec.class };
      getCurveName = localClass.getMethod("getCurveName", arrayOfClass);
      arrayOfClass = new Class[] { byte[].class, EllipticCurve.class };
      decodePoint = localClass.getMethod("decodePoint", arrayOfClass);
      localClass = Class.forName("sun.security.ec.NamedCurve");
      arrayOfClass = new Class[] { String.class };
      getECParameterSpec = localClass.getMethod("getECParameterSpec", arrayOfClass);
    }
    
    void marshalPublicKey(Node paramNode, Document paramDocument, String paramString, DOMCryptoContext paramDOMCryptoContext)
      throws MarshalException
    {
      String str1 = DOMUtils.getNSPrefix(paramDOMCryptoContext, "http://www.w3.org/2009/xmldsig11#");
      Element localElement1 = DOMUtils.createElement(paramDocument, "ECKeyValue", "http://www.w3.org/2009/xmldsig11#", str1);
      Element localElement2 = DOMUtils.createElement(paramDocument, "NamedCurve", "http://www.w3.org/2009/xmldsig11#", str1);
      Element localElement3 = DOMUtils.createElement(paramDocument, "PublicKey", "http://www.w3.org/2009/xmldsig11#", str1);
      Object[] arrayOfObject = { ecParams };
      try
      {
        String str2 = (String)getCurveName.invoke(null, arrayOfObject);
        DOMUtils.setAttribute(localElement2, "URI", "urn:oid:" + str2);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new MarshalException(localIllegalAccessException);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        throw new MarshalException(localInvocationTargetException);
      }
      String str3 = "xmlns:" + str1;
      localElement2.setAttributeNS("http://www.w3.org/2000/xmlns/", str3, "http://www.w3.org/2009/xmldsig11#");
      localElement1.appendChild(localElement2);
      String str4 = Base64.encode(ecPublicKey);
      localElement3.appendChild(DOMUtils.getOwnerDocument(localElement3).createTextNode(str4));
      localElement1.appendChild(localElement3);
      paramNode.appendChild(localElement1);
    }
    
    PublicKey unmarshalKeyValue(Element paramElement)
      throws MarshalException
    {
      if (eckf == null) {
        try
        {
          eckf = KeyFactory.getInstance("EC");
        }
        catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
        {
          throw new RuntimeException("unable to create EC KeyFactory: " + localNoSuchAlgorithmException.getMessage());
        }
      }
      try
      {
        AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public Void run()
            throws ClassNotFoundException, NoSuchMethodException
          {
            getMethods();
            return null;
          }
        });
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        throw new MarshalException("ECKeyValue not supported", localPrivilegedActionException.getException());
      }
      ECParameterSpec localECParameterSpec = null;
      Element localElement = DOMUtils.getFirstChildElement(paramElement);
      if (localElement.getLocalName().equals("ECParameters")) {
        throw new UnsupportedOperationException("ECParameters not supported");
      }
      Object localObject2;
      if (localElement.getLocalName().equals("NamedCurve"))
      {
        localObject1 = DOMUtils.getAttributeValue(localElement, "URI");
        if (((String)localObject1).startsWith("urn:oid:"))
        {
          localObject2 = ((String)localObject1).substring(8);
          try
          {
            Object[] arrayOfObject = { localObject2 };
            localECParameterSpec = (ECParameterSpec)getECParameterSpec.invoke(null, arrayOfObject);
          }
          catch (IllegalAccessException localIllegalAccessException2)
          {
            throw new MarshalException(localIllegalAccessException2);
          }
          catch (InvocationTargetException localInvocationTargetException2)
          {
            throw new MarshalException(localInvocationTargetException2);
          }
        }
        else
        {
          throw new MarshalException("Invalid NamedCurve URI");
        }
      }
      else
      {
        throw new MarshalException("Invalid ECKeyValue");
      }
      localElement = DOMUtils.getNextSiblingElement(localElement, "PublicKey");
      Object localObject1 = null;
      try
      {
        localObject2 = new Object[] { Base64.decode(localElement), localECParameterSpec.getCurve() };
        localObject1 = (ECPoint)decodePoint.invoke(null, (Object[])localObject2);
      }
      catch (Base64DecodingException localBase64DecodingException)
      {
        throw new MarshalException("Invalid EC PublicKey", localBase64DecodingException);
      }
      catch (IllegalAccessException localIllegalAccessException1)
      {
        throw new MarshalException(localIllegalAccessException1);
      }
      catch (InvocationTargetException localInvocationTargetException1)
      {
        throw new MarshalException(localInvocationTargetException1);
      }
      ECPublicKeySpec localECPublicKeySpec = new ECPublicKeySpec((ECPoint)localObject1, localECParameterSpec);
      return DOMKeyValue.generatePublicKey(eckf, localECPublicKeySpec);
    }
  }
  
  static final class RSA
    extends DOMKeyValue
  {
    private DOMCryptoBinary modulus;
    private DOMCryptoBinary exponent;
    private KeyFactory rsakf;
    
    RSA(PublicKey paramPublicKey)
      throws KeyException
    {
      super();
      RSAPublicKey localRSAPublicKey = (RSAPublicKey)paramPublicKey;
      exponent = new DOMCryptoBinary(localRSAPublicKey.getPublicExponent());
      modulus = new DOMCryptoBinary(localRSAPublicKey.getModulus());
    }
    
    RSA(Element paramElement)
      throws MarshalException
    {
      super();
    }
    
    void marshalPublicKey(Node paramNode, Document paramDocument, String paramString, DOMCryptoContext paramDOMCryptoContext)
      throws MarshalException
    {
      Element localElement1 = DOMUtils.createElement(paramDocument, "RSAKeyValue", "http://www.w3.org/2000/09/xmldsig#", paramString);
      Element localElement2 = DOMUtils.createElement(paramDocument, "Modulus", "http://www.w3.org/2000/09/xmldsig#", paramString);
      Element localElement3 = DOMUtils.createElement(paramDocument, "Exponent", "http://www.w3.org/2000/09/xmldsig#", paramString);
      modulus.marshal(localElement2, paramString, paramDOMCryptoContext);
      exponent.marshal(localElement3, paramString, paramDOMCryptoContext);
      localElement1.appendChild(localElement2);
      localElement1.appendChild(localElement3);
      paramNode.appendChild(localElement1);
    }
    
    PublicKey unmarshalKeyValue(Element paramElement)
      throws MarshalException
    {
      if (rsakf == null) {
        try
        {
          rsakf = KeyFactory.getInstance("RSA");
        }
        catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
        {
          throw new RuntimeException("unable to create RSA KeyFactory: " + localNoSuchAlgorithmException.getMessage());
        }
      }
      Element localElement1 = DOMUtils.getFirstChildElement(paramElement, "Modulus");
      modulus = new DOMCryptoBinary(localElement1.getFirstChild());
      Element localElement2 = DOMUtils.getNextSiblingElement(localElement1, "Exponent");
      exponent = new DOMCryptoBinary(localElement2.getFirstChild());
      RSAPublicKeySpec localRSAPublicKeySpec = new RSAPublicKeySpec(modulus.getBigNum(), exponent.getBigNum());
      return DOMKeyValue.generatePublicKey(rsakf, localRSAPublicKeySpec);
    }
  }
  
  static final class Unknown
    extends DOMKeyValue
  {
    private javax.xml.crypto.dom.DOMStructure externalPublicKey;
    
    Unknown(Element paramElement)
      throws MarshalException
    {
      super();
    }
    
    PublicKey unmarshalKeyValue(Element paramElement)
      throws MarshalException
    {
      externalPublicKey = new javax.xml.crypto.dom.DOMStructure(paramElement);
      return null;
    }
    
    void marshalPublicKey(Node paramNode, Document paramDocument, String paramString, DOMCryptoContext paramDOMCryptoContext)
      throws MarshalException
    {
      paramNode.appendChild(externalPublicKey.getNode());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMKeyValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */