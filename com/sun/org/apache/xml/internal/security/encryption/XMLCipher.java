package com.sun.org.apache.xml.internal.security.encryption;

import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.KeyInfo;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.EncryptedKeyResolver;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.transforms.InvalidTransformException;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource.PSpecified;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XMLCipher
{
  private static Logger log = Logger.getLogger(XMLCipher.class.getName());
  public static final String TRIPLEDES = "http://www.w3.org/2001/04/xmlenc#tripledes-cbc";
  public static final String AES_128 = "http://www.w3.org/2001/04/xmlenc#aes128-cbc";
  public static final String AES_256 = "http://www.w3.org/2001/04/xmlenc#aes256-cbc";
  public static final String AES_192 = "http://www.w3.org/2001/04/xmlenc#aes192-cbc";
  public static final String AES_128_GCM = "http://www.w3.org/2009/xmlenc11#aes128-gcm";
  public static final String AES_192_GCM = "http://www.w3.org/2009/xmlenc11#aes192-gcm";
  public static final String AES_256_GCM = "http://www.w3.org/2009/xmlenc11#aes256-gcm";
  public static final String RSA_v1dot5 = "http://www.w3.org/2001/04/xmlenc#rsa-1_5";
  public static final String RSA_OAEP = "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p";
  public static final String RSA_OAEP_11 = "http://www.w3.org/2009/xmlenc11#rsa-oaep";
  public static final String DIFFIE_HELLMAN = "http://www.w3.org/2001/04/xmlenc#dh";
  public static final String TRIPLEDES_KeyWrap = "http://www.w3.org/2001/04/xmlenc#kw-tripledes";
  public static final String AES_128_KeyWrap = "http://www.w3.org/2001/04/xmlenc#kw-aes128";
  public static final String AES_256_KeyWrap = "http://www.w3.org/2001/04/xmlenc#kw-aes256";
  public static final String AES_192_KeyWrap = "http://www.w3.org/2001/04/xmlenc#kw-aes192";
  public static final String SHA1 = "http://www.w3.org/2000/09/xmldsig#sha1";
  public static final String SHA256 = "http://www.w3.org/2001/04/xmlenc#sha256";
  public static final String SHA512 = "http://www.w3.org/2001/04/xmlenc#sha512";
  public static final String RIPEMD_160 = "http://www.w3.org/2001/04/xmlenc#ripemd160";
  public static final String XML_DSIG = "http://www.w3.org/2000/09/xmldsig#";
  public static final String N14C_XML = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
  public static final String N14C_XML_WITH_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
  public static final String EXCL_XML_N14C = "http://www.w3.org/2001/10/xml-exc-c14n#";
  public static final String EXCL_XML_N14C_WITH_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
  public static final String PHYSICAL_XML_N14C = "http://santuario.apache.org/c14n/physical";
  public static final String BASE64_ENCODING = "http://www.w3.org/2000/09/xmldsig#base64";
  public static final int ENCRYPT_MODE = 1;
  public static final int DECRYPT_MODE = 2;
  public static final int UNWRAP_MODE = 4;
  public static final int WRAP_MODE = 3;
  private static final String ENC_ALGORITHMS = "http://www.w3.org/2001/04/xmlenc#tripledes-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes128-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes256-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes192-cbc\nhttp://www.w3.org/2001/04/xmlenc#rsa-1_5\nhttp://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p\nhttp://www.w3.org/2009/xmlenc11#rsa-oaep\nhttp://www.w3.org/2001/04/xmlenc#kw-tripledes\nhttp://www.w3.org/2001/04/xmlenc#kw-aes128\nhttp://www.w3.org/2001/04/xmlenc#kw-aes256\nhttp://www.w3.org/2001/04/xmlenc#kw-aes192\nhttp://www.w3.org/2009/xmlenc11#aes128-gcm\nhttp://www.w3.org/2009/xmlenc11#aes192-gcm\nhttp://www.w3.org/2009/xmlenc11#aes256-gcm\n";
  private Cipher contextCipher;
  private int cipherMode = Integer.MIN_VALUE;
  private String algorithm = null;
  private String requestedJCEProvider = null;
  private Canonicalizer canon;
  private Document contextDocument;
  private Factory factory;
  private Serializer serializer;
  private Key key;
  private Key kek;
  private EncryptedKey ek;
  private EncryptedData ed;
  private SecureRandom random;
  private boolean secureValidation;
  private String digestAlg;
  private List<KeyResolverSpi> internalKeyResolvers;
  
  public void setSerializer(Serializer paramSerializer)
  {
    serializer = paramSerializer;
    paramSerializer.setCanonicalizer(canon);
  }
  
  public Serializer getSerializer()
  {
    return serializer;
  }
  
  private XMLCipher(String paramString1, String paramString2, String paramString3, String paramString4)
    throws XMLEncryptionException
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Constructing XMLCipher...");
    }
    factory = new Factory(null);
    algorithm = paramString1;
    requestedJCEProvider = paramString2;
    digestAlg = paramString4;
    try
    {
      if (paramString3 == null) {
        canon = Canonicalizer.getInstance("http://santuario.apache.org/c14n/physical");
      } else {
        canon = Canonicalizer.getInstance(paramString3);
      }
    }
    catch (InvalidCanonicalizerException localInvalidCanonicalizerException)
    {
      throw new XMLEncryptionException("empty", localInvalidCanonicalizerException);
    }
    if (serializer == null) {
      serializer = new DocumentSerializer();
    }
    serializer.setCanonicalizer(canon);
    if (paramString1 != null) {
      contextCipher = constructCipher(paramString1, paramString4);
    }
  }
  
  private static boolean isValidEncryptionAlgorithm(String paramString)
  {
    return (paramString.equals("http://www.w3.org/2001/04/xmlenc#tripledes-cbc")) || (paramString.equals("http://www.w3.org/2001/04/xmlenc#aes128-cbc")) || (paramString.equals("http://www.w3.org/2001/04/xmlenc#aes256-cbc")) || (paramString.equals("http://www.w3.org/2001/04/xmlenc#aes192-cbc")) || (paramString.equals("http://www.w3.org/2009/xmlenc11#aes128-gcm")) || (paramString.equals("http://www.w3.org/2009/xmlenc11#aes192-gcm")) || (paramString.equals("http://www.w3.org/2009/xmlenc11#aes256-gcm")) || (paramString.equals("http://www.w3.org/2001/04/xmlenc#rsa-1_5")) || (paramString.equals("http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p")) || (paramString.equals("http://www.w3.org/2009/xmlenc11#rsa-oaep")) || (paramString.equals("http://www.w3.org/2001/04/xmlenc#kw-tripledes")) || (paramString.equals("http://www.w3.org/2001/04/xmlenc#kw-aes128")) || (paramString.equals("http://www.w3.org/2001/04/xmlenc#kw-aes256")) || (paramString.equals("http://www.w3.org/2001/04/xmlenc#kw-aes192"));
  }
  
  private static void validateTransformation(String paramString)
  {
    if (null == paramString) {
      throw new NullPointerException("Transformation unexpectedly null...");
    }
    if (!isValidEncryptionAlgorithm(paramString)) {
      log.log(Level.WARNING, "Algorithm non-standard, expected one of http://www.w3.org/2001/04/xmlenc#tripledes-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes128-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes256-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes192-cbc\nhttp://www.w3.org/2001/04/xmlenc#rsa-1_5\nhttp://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p\nhttp://www.w3.org/2009/xmlenc11#rsa-oaep\nhttp://www.w3.org/2001/04/xmlenc#kw-tripledes\nhttp://www.w3.org/2001/04/xmlenc#kw-aes128\nhttp://www.w3.org/2001/04/xmlenc#kw-aes256\nhttp://www.w3.org/2001/04/xmlenc#kw-aes192\nhttp://www.w3.org/2009/xmlenc11#aes128-gcm\nhttp://www.w3.org/2009/xmlenc11#aes192-gcm\nhttp://www.w3.org/2009/xmlenc11#aes256-gcm\n");
    }
  }
  
  public static XMLCipher getInstance(String paramString)
    throws XMLEncryptionException
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Getting XMLCipher with transformation");
    }
    validateTransformation(paramString);
    return new XMLCipher(paramString, null, null, null);
  }
  
  public static XMLCipher getInstance(String paramString1, String paramString2)
    throws XMLEncryptionException
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Getting XMLCipher with transformation and c14n algorithm");
    }
    validateTransformation(paramString1);
    return new XMLCipher(paramString1, null, paramString2, null);
  }
  
  public static XMLCipher getInstance(String paramString1, String paramString2, String paramString3)
    throws XMLEncryptionException
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Getting XMLCipher with transformation and c14n algorithm");
    }
    validateTransformation(paramString1);
    return new XMLCipher(paramString1, null, paramString2, paramString3);
  }
  
  public static XMLCipher getProviderInstance(String paramString1, String paramString2)
    throws XMLEncryptionException
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Getting XMLCipher with transformation and provider");
    }
    if (null == paramString2) {
      throw new NullPointerException("Provider unexpectedly null..");
    }
    validateTransformation(paramString1);
    return new XMLCipher(paramString1, paramString2, null, null);
  }
  
  public static XMLCipher getProviderInstance(String paramString1, String paramString2, String paramString3)
    throws XMLEncryptionException
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Getting XMLCipher with transformation, provider and c14n algorithm");
    }
    if (null == paramString2) {
      throw new NullPointerException("Provider unexpectedly null..");
    }
    validateTransformation(paramString1);
    return new XMLCipher(paramString1, paramString2, paramString3, null);
  }
  
  public static XMLCipher getProviderInstance(String paramString1, String paramString2, String paramString3, String paramString4)
    throws XMLEncryptionException
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Getting XMLCipher with transformation, provider and c14n algorithm");
    }
    if (null == paramString2) {
      throw new NullPointerException("Provider unexpectedly null..");
    }
    validateTransformation(paramString1);
    return new XMLCipher(paramString1, paramString2, paramString3, paramString4);
  }
  
  public static XMLCipher getInstance()
    throws XMLEncryptionException
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Getting XMLCipher with no arguments");
    }
    return new XMLCipher(null, null, null, null);
  }
  
  public static XMLCipher getProviderInstance(String paramString)
    throws XMLEncryptionException
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Getting XMLCipher with provider");
    }
    return new XMLCipher(null, paramString, null, null);
  }
  
  public void init(int paramInt, Key paramKey)
    throws XMLEncryptionException
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Initializing XMLCipher...");
    }
    ek = null;
    ed = null;
    switch (paramInt)
    {
    case 1: 
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "opmode = ENCRYPT_MODE");
      }
      ed = createEncryptedData(1, "NO VALUE YET");
      break;
    case 2: 
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "opmode = DECRYPT_MODE");
      }
      break;
    case 3: 
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "opmode = WRAP_MODE");
      }
      ek = createEncryptedKey(1, "NO VALUE YET");
      break;
    case 4: 
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "opmode = UNWRAP_MODE");
      }
      break;
    default: 
      log.log(Level.SEVERE, "Mode unexpectedly invalid");
      throw new XMLEncryptionException("Invalid mode in init");
    }
    cipherMode = paramInt;
    key = paramKey;
  }
  
  public void setSecureValidation(boolean paramBoolean)
  {
    secureValidation = paramBoolean;
  }
  
  public void registerInternalKeyResolver(KeyResolverSpi paramKeyResolverSpi)
  {
    if (internalKeyResolvers == null) {
      internalKeyResolvers = new ArrayList();
    }
    internalKeyResolvers.add(paramKeyResolverSpi);
  }
  
  public EncryptedData getEncryptedData()
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Returning EncryptedData");
    }
    return ed;
  }
  
  public EncryptedKey getEncryptedKey()
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Returning EncryptedKey");
    }
    return ek;
  }
  
  public void setKEK(Key paramKey)
  {
    kek = paramKey;
  }
  
  public Element martial(EncryptedData paramEncryptedData)
  {
    return factory.toElement(paramEncryptedData);
  }
  
  public Element martial(Document paramDocument, EncryptedData paramEncryptedData)
  {
    contextDocument = paramDocument;
    return factory.toElement(paramEncryptedData);
  }
  
  public Element martial(EncryptedKey paramEncryptedKey)
  {
    return factory.toElement(paramEncryptedKey);
  }
  
  public Element martial(Document paramDocument, EncryptedKey paramEncryptedKey)
  {
    contextDocument = paramDocument;
    return factory.toElement(paramEncryptedKey);
  }
  
  public Element martial(ReferenceList paramReferenceList)
  {
    return factory.toElement(paramReferenceList);
  }
  
  public Element martial(Document paramDocument, ReferenceList paramReferenceList)
  {
    contextDocument = paramDocument;
    return factory.toElement(paramReferenceList);
  }
  
  private Document encryptElement(Element paramElement)
    throws Exception
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Encrypting element...");
    }
    if (null == paramElement) {
      log.log(Level.SEVERE, "Element unexpectedly null...");
    }
    if ((cipherMode != 1) && (log.isLoggable(Level.FINE))) {
      log.log(Level.FINE, "XMLCipher unexpectedly not in ENCRYPT_MODE...");
    }
    if (algorithm == null) {
      throw new XMLEncryptionException("XMLCipher instance without transformation specified");
    }
    encryptData(contextDocument, paramElement, false);
    Element localElement = factory.toElement(ed);
    Node localNode = paramElement.getParentNode();
    localNode.replaceChild(localElement, paramElement);
    return contextDocument;
  }
  
  private Document encryptElementContent(Element paramElement)
    throws Exception
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Encrypting element content...");
    }
    if (null == paramElement) {
      log.log(Level.SEVERE, "Element unexpectedly null...");
    }
    if ((cipherMode != 1) && (log.isLoggable(Level.FINE))) {
      log.log(Level.FINE, "XMLCipher unexpectedly not in ENCRYPT_MODE...");
    }
    if (algorithm == null) {
      throw new XMLEncryptionException("XMLCipher instance without transformation specified");
    }
    encryptData(contextDocument, paramElement, true);
    Element localElement = factory.toElement(ed);
    removeContent(paramElement);
    paramElement.appendChild(localElement);
    return contextDocument;
  }
  
  public Document doFinal(Document paramDocument1, Document paramDocument2)
    throws Exception
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Processing source document...");
    }
    if (null == paramDocument1) {
      log.log(Level.SEVERE, "Context document unexpectedly null...");
    }
    if (null == paramDocument2) {
      log.log(Level.SEVERE, "Source document unexpectedly null...");
    }
    contextDocument = paramDocument1;
    Document localDocument = null;
    switch (cipherMode)
    {
    case 2: 
      localDocument = decryptElement(paramDocument2.getDocumentElement());
      break;
    case 1: 
      localDocument = encryptElement(paramDocument2.getDocumentElement());
      break;
    case 3: 
    case 4: 
      break;
    default: 
      throw new XMLEncryptionException("empty", new IllegalStateException());
    }
    return localDocument;
  }
  
  public Document doFinal(Document paramDocument, Element paramElement)
    throws Exception
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Processing source element...");
    }
    if (null == paramDocument) {
      log.log(Level.SEVERE, "Context document unexpectedly null...");
    }
    if (null == paramElement) {
      log.log(Level.SEVERE, "Source element unexpectedly null...");
    }
    contextDocument = paramDocument;
    Document localDocument = null;
    switch (cipherMode)
    {
    case 2: 
      localDocument = decryptElement(paramElement);
      break;
    case 1: 
      localDocument = encryptElement(paramElement);
      break;
    case 3: 
    case 4: 
      break;
    default: 
      throw new XMLEncryptionException("empty", new IllegalStateException());
    }
    return localDocument;
  }
  
  public Document doFinal(Document paramDocument, Element paramElement, boolean paramBoolean)
    throws Exception
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Processing source element...");
    }
    if (null == paramDocument) {
      log.log(Level.SEVERE, "Context document unexpectedly null...");
    }
    if (null == paramElement) {
      log.log(Level.SEVERE, "Source element unexpectedly null...");
    }
    contextDocument = paramDocument;
    Document localDocument = null;
    switch (cipherMode)
    {
    case 2: 
      if (paramBoolean) {
        localDocument = decryptElementContent(paramElement);
      } else {
        localDocument = decryptElement(paramElement);
      }
      break;
    case 1: 
      if (paramBoolean) {
        localDocument = encryptElementContent(paramElement);
      } else {
        localDocument = encryptElement(paramElement);
      }
      break;
    case 3: 
    case 4: 
      break;
    default: 
      throw new XMLEncryptionException("empty", new IllegalStateException());
    }
    return localDocument;
  }
  
  public EncryptedData encryptData(Document paramDocument, Element paramElement)
    throws Exception
  {
    return encryptData(paramDocument, paramElement, false);
  }
  
  public EncryptedData encryptData(Document paramDocument, String paramString, InputStream paramInputStream)
    throws Exception
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Encrypting element...");
    }
    if (null == paramDocument) {
      log.log(Level.SEVERE, "Context document unexpectedly null...");
    }
    if (null == paramInputStream) {
      log.log(Level.SEVERE, "Serialized data unexpectedly null...");
    }
    if ((cipherMode != 1) && (log.isLoggable(Level.FINE))) {
      log.log(Level.FINE, "XMLCipher unexpectedly not in ENCRYPT_MODE...");
    }
    return encryptData(paramDocument, null, paramString, paramInputStream);
  }
  
  public EncryptedData encryptData(Document paramDocument, Element paramElement, boolean paramBoolean)
    throws Exception
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Encrypting element...");
    }
    if (null == paramDocument) {
      log.log(Level.SEVERE, "Context document unexpectedly null...");
    }
    if (null == paramElement) {
      log.log(Level.SEVERE, "Element unexpectedly null...");
    }
    if ((cipherMode != 1) && (log.isLoggable(Level.FINE))) {
      log.log(Level.FINE, "XMLCipher unexpectedly not in ENCRYPT_MODE...");
    }
    if (paramBoolean) {
      return encryptData(paramDocument, paramElement, "http://www.w3.org/2001/04/xmlenc#Content", null);
    }
    return encryptData(paramDocument, paramElement, "http://www.w3.org/2001/04/xmlenc#Element", null);
  }
  
  private EncryptedData encryptData(Document paramDocument, Element paramElement, String paramString, InputStream paramInputStream)
    throws Exception
  {
    contextDocument = paramDocument;
    if (algorithm == null) {
      throw new XMLEncryptionException("XMLCipher instance without transformation specified");
    }
    byte[] arrayOfByte1 = null;
    Object localObject2;
    if (paramInputStream == null)
    {
      if (paramString.equals("http://www.w3.org/2001/04/xmlenc#Content"))
      {
        localObject1 = paramElement.getChildNodes();
        if (null != localObject1)
        {
          arrayOfByte1 = serializer.serializeToByteArray((NodeList)localObject1);
        }
        else
        {
          localObject2 = new Object[] { "Element has no content." };
          throw new XMLEncryptionException("empty", (Object[])localObject2);
        }
      }
      else
      {
        arrayOfByte1 = serializer.serializeToByteArray(paramElement);
      }
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "Serialized octets:\n" + new String(arrayOfByte1, "UTF-8"));
      }
    }
    Object localObject1 = null;
    if (contextCipher == null) {
      localObject2 = constructCipher(algorithm, null);
    } else {
      localObject2 = contextCipher;
    }
    try
    {
      if (("http://www.w3.org/2009/xmlenc11#aes128-gcm".equals(algorithm)) || ("http://www.w3.org/2009/xmlenc11#aes192-gcm".equals(algorithm)) || ("http://www.w3.org/2009/xmlenc11#aes256-gcm".equals(algorithm)))
      {
        if (random == null) {
          random = SecureRandom.getInstance("SHA1PRNG");
        }
        byte[] arrayOfByte2 = new byte[12];
        random.nextBytes(arrayOfByte2);
        localObject3 = new IvParameterSpec(arrayOfByte2);
        ((Cipher)localObject2).init(cipherMode, key, (AlgorithmParameterSpec)localObject3);
      }
      else
      {
        ((Cipher)localObject2).init(cipherMode, key);
      }
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      throw new XMLEncryptionException("empty", localInvalidKeyException);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new XMLEncryptionException("empty", localNoSuchAlgorithmException);
    }
    Object localObject5;
    try
    {
      if (paramInputStream != null)
      {
        localObject3 = new byte[' '];
        localObject4 = new ByteArrayOutputStream();
        int i;
        while ((i = paramInputStream.read((byte[])localObject3)) != -1)
        {
          localObject5 = ((Cipher)localObject2).update((byte[])localObject3, 0, i);
          ((ByteArrayOutputStream)localObject4).write((byte[])localObject5);
        }
        ((ByteArrayOutputStream)localObject4).write(((Cipher)localObject2).doFinal());
        localObject1 = ((ByteArrayOutputStream)localObject4).toByteArray();
      }
      else
      {
        localObject1 = ((Cipher)localObject2).doFinal(arrayOfByte1);
        if (log.isLoggable(Level.FINE)) {
          log.log(Level.FINE, "Expected cipher.outputSize = " + Integer.toString(((Cipher)localObject2).getOutputSize(arrayOfByte1.length)));
        }
      }
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "Actual cipher.outputSize = " + Integer.toString(localObject1.length));
      }
    }
    catch (IllegalStateException localIllegalStateException)
    {
      throw new XMLEncryptionException("empty", localIllegalStateException);
    }
    catch (IllegalBlockSizeException localIllegalBlockSizeException)
    {
      throw new XMLEncryptionException("empty", localIllegalBlockSizeException);
    }
    catch (BadPaddingException localBadPaddingException)
    {
      throw new XMLEncryptionException("empty", localBadPaddingException);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new XMLEncryptionException("empty", localUnsupportedEncodingException);
    }
    byte[] arrayOfByte3 = ((Cipher)localObject2).getIV();
    Object localObject3 = new byte[arrayOfByte3.length + localObject1.length];
    System.arraycopy(arrayOfByte3, 0, localObject3, 0, arrayOfByte3.length);
    System.arraycopy(localObject1, 0, localObject3, arrayOfByte3.length, localObject1.length);
    Object localObject4 = Base64.encode((byte[])localObject3);
    if (log.isLoggable(Level.FINE))
    {
      log.log(Level.FINE, "Encrypted octets:\n" + (String)localObject4);
      log.log(Level.FINE, "Encrypted octets length = " + ((String)localObject4).length());
    }
    try
    {
      localObject5 = ed.getCipherData();
      CipherValue localCipherValue = ((CipherData)localObject5).getCipherValue();
      localCipherValue.setValue((String)localObject4);
      if (paramString != null) {
        ed.setType(new URI(paramString).toString());
      }
      EncryptionMethod localEncryptionMethod = factory.newEncryptionMethod(new URI(algorithm).toString());
      localEncryptionMethod.setDigestAlgorithm(digestAlg);
      ed.setEncryptionMethod(localEncryptionMethod);
    }
    catch (URISyntaxException localURISyntaxException)
    {
      throw new XMLEncryptionException("empty", localURISyntaxException);
    }
    return ed;
  }
  
  public EncryptedData loadEncryptedData(Document paramDocument, Element paramElement)
    throws XMLEncryptionException
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Loading encrypted element...");
    }
    if (null == paramDocument) {
      throw new NullPointerException("Context document unexpectedly null...");
    }
    if (null == paramElement) {
      throw new NullPointerException("Element unexpectedly null...");
    }
    if (cipherMode != 2) {
      throw new XMLEncryptionException("XMLCipher unexpectedly not in DECRYPT_MODE...");
    }
    contextDocument = paramDocument;
    ed = factory.newEncryptedData(paramElement);
    return ed;
  }
  
  public EncryptedKey loadEncryptedKey(Document paramDocument, Element paramElement)
    throws XMLEncryptionException
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Loading encrypted key...");
    }
    if (null == paramDocument) {
      throw new NullPointerException("Context document unexpectedly null...");
    }
    if (null == paramElement) {
      throw new NullPointerException("Element unexpectedly null...");
    }
    if ((cipherMode != 4) && (cipherMode != 2)) {
      throw new XMLEncryptionException("XMLCipher unexpectedly not in UNWRAP_MODE or DECRYPT_MODE...");
    }
    contextDocument = paramDocument;
    ek = factory.newEncryptedKey(paramElement);
    return ek;
  }
  
  public EncryptedKey loadEncryptedKey(Element paramElement)
    throws XMLEncryptionException
  {
    return loadEncryptedKey(paramElement.getOwnerDocument(), paramElement);
  }
  
  public EncryptedKey encryptKey(Document paramDocument, Key paramKey)
    throws XMLEncryptionException
  {
    return encryptKey(paramDocument, paramKey, null, null);
  }
  
  public EncryptedKey encryptKey(Document paramDocument, Key paramKey, String paramString, byte[] paramArrayOfByte)
    throws XMLEncryptionException
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Encrypting key ...");
    }
    if (null == paramKey) {
      log.log(Level.SEVERE, "Key unexpectedly null...");
    }
    if (cipherMode != 3) {
      log.log(Level.FINE, "XMLCipher unexpectedly not in WRAP_MODE...");
    }
    if (algorithm == null) {
      throw new XMLEncryptionException("XMLCipher instance without transformation specified");
    }
    contextDocument = paramDocument;
    byte[] arrayOfByte = null;
    Cipher localCipher;
    if (contextCipher == null) {
      localCipher = constructCipher(algorithm, null);
    } else {
      localCipher = contextCipher;
    }
    try
    {
      OAEPParameterSpec localOAEPParameterSpec = constructOAEPParameters(algorithm, digestAlg, paramString, paramArrayOfByte);
      if (localOAEPParameterSpec == null) {
        localCipher.init(3, key);
      } else {
        localCipher.init(3, key, localOAEPParameterSpec);
      }
      arrayOfByte = localCipher.wrap(paramKey);
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      throw new XMLEncryptionException("empty", localInvalidKeyException);
    }
    catch (IllegalBlockSizeException localIllegalBlockSizeException)
    {
      throw new XMLEncryptionException("empty", localIllegalBlockSizeException);
    }
    catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException)
    {
      throw new XMLEncryptionException("empty", localInvalidAlgorithmParameterException);
    }
    String str = Base64.encode(arrayOfByte);
    if (log.isLoggable(Level.FINE))
    {
      log.log(Level.FINE, "Encrypted key octets:\n" + str);
      log.log(Level.FINE, "Encrypted key octets length = " + str.length());
    }
    CipherValue localCipherValue = ek.getCipherData().getCipherValue();
    localCipherValue.setValue(str);
    try
    {
      EncryptionMethod localEncryptionMethod = factory.newEncryptionMethod(new URI(algorithm).toString());
      localEncryptionMethod.setDigestAlgorithm(digestAlg);
      localEncryptionMethod.setMGFAlgorithm(paramString);
      localEncryptionMethod.setOAEPparams(paramArrayOfByte);
      ek.setEncryptionMethod(localEncryptionMethod);
    }
    catch (URISyntaxException localURISyntaxException)
    {
      throw new XMLEncryptionException("empty", localURISyntaxException);
    }
    return ek;
  }
  
  public Key decryptKey(EncryptedKey paramEncryptedKey, String paramString)
    throws XMLEncryptionException
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Decrypting key from previously loaded EncryptedKey...");
    }
    if ((cipherMode != 4) && (log.isLoggable(Level.FINE))) {
      log.log(Level.FINE, "XMLCipher unexpectedly not in UNWRAP_MODE...");
    }
    if (paramString == null) {
      throw new XMLEncryptionException("Cannot decrypt a key without knowing the algorithm");
    }
    if (key == null)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "Trying to find a KEK via key resolvers");
      }
      localObject = paramEncryptedKey.getKeyInfo();
      if (localObject != null)
      {
        ((KeyInfo)localObject).setSecureValidation(secureValidation);
        try
        {
          String str1 = paramEncryptedKey.getEncryptionMethod().getAlgorithm();
          str2 = JCEMapper.getJCEKeyAlgorithmFromURI(str1);
          if ("RSA".equals(str2)) {
            key = ((KeyInfo)localObject).getPrivateKey();
          } else {
            key = ((KeyInfo)localObject).getSecretKey();
          }
        }
        catch (Exception localException)
        {
          if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, localException.getMessage(), localException);
          }
        }
      }
      if (key == null)
      {
        log.log(Level.SEVERE, "XMLCipher::decryptKey called without a KEK and cannot resolve");
        throw new XMLEncryptionException("Unable to decrypt without a KEK");
      }
    }
    Object localObject = new XMLCipherInput(paramEncryptedKey);
    ((XMLCipherInput)localObject).setSecureValidation(secureValidation);
    byte[] arrayOfByte = ((XMLCipherInput)localObject).getBytes();
    String str2 = JCEMapper.getJCEKeyAlgorithmFromURI(paramString);
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "JCE Key Algorithm: " + str2);
    }
    Cipher localCipher;
    if (contextCipher == null) {
      localCipher = constructCipher(paramEncryptedKey.getEncryptionMethod().getAlgorithm(), paramEncryptedKey.getEncryptionMethod().getDigestAlgorithm());
    } else {
      localCipher = contextCipher;
    }
    Key localKey;
    try
    {
      EncryptionMethod localEncryptionMethod = paramEncryptedKey.getEncryptionMethod();
      OAEPParameterSpec localOAEPParameterSpec = constructOAEPParameters(localEncryptionMethod.getAlgorithm(), localEncryptionMethod.getDigestAlgorithm(), localEncryptionMethod.getMGFAlgorithm(), localEncryptionMethod.getOAEPparams());
      if (localOAEPParameterSpec == null) {
        localCipher.init(4, key);
      } else {
        localCipher.init(4, key, localOAEPParameterSpec);
      }
      localKey = localCipher.unwrap(arrayOfByte, str2, 3);
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      throw new XMLEncryptionException("empty", localInvalidKeyException);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new XMLEncryptionException("empty", localNoSuchAlgorithmException);
    }
    catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException)
    {
      throw new XMLEncryptionException("empty", localInvalidAlgorithmParameterException);
    }
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Decryption of key type " + paramString + " OK");
    }
    return localKey;
  }
  
  private OAEPParameterSpec constructOAEPParameters(String paramString1, String paramString2, String paramString3, byte[] paramArrayOfByte)
  {
    if (("http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p".equals(paramString1)) || ("http://www.w3.org/2009/xmlenc11#rsa-oaep".equals(paramString1)))
    {
      String str = "SHA-1";
      if (paramString2 != null) {
        str = JCEMapper.translateURItoJCEID(paramString2);
      }
      PSource.PSpecified localPSpecified = PSource.PSpecified.DEFAULT;
      if (paramArrayOfByte != null) {
        localPSpecified = new PSource.PSpecified(paramArrayOfByte);
      }
      MGF1ParameterSpec localMGF1ParameterSpec = new MGF1ParameterSpec("SHA-1");
      if ("http://www.w3.org/2009/xmlenc11#rsa-oaep".equals(paramString1)) {
        if ("http://www.w3.org/2009/xmlenc11#mgf1sha256".equals(paramString3)) {
          localMGF1ParameterSpec = new MGF1ParameterSpec("SHA-256");
        } else if ("http://www.w3.org/2009/xmlenc11#mgf1sha384".equals(paramString3)) {
          localMGF1ParameterSpec = new MGF1ParameterSpec("SHA-384");
        } else if ("http://www.w3.org/2009/xmlenc11#mgf1sha512".equals(paramString3)) {
          localMGF1ParameterSpec = new MGF1ParameterSpec("SHA-512");
        }
      }
      return new OAEPParameterSpec(str, "MGF1", localMGF1ParameterSpec, localPSpecified);
    }
    return null;
  }
  
  private Cipher constructCipher(String paramString1, String paramString2)
    throws XMLEncryptionException
  {
    String str = JCEMapper.translateURItoJCEID(paramString1);
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "JCE Algorithm = " + str);
    }
    Cipher localCipher;
    try
    {
      if (requestedJCEProvider == null) {
        localCipher = Cipher.getInstance(str);
      } else {
        localCipher = Cipher.getInstance(str, requestedJCEProvider);
      }
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      if (("http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p".equals(paramString1)) && ((paramString2 == null) || ("http://www.w3.org/2000/09/xmldsig#sha1".equals(paramString2)))) {
        try
        {
          if (requestedJCEProvider == null) {
            localCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
          } else {
            localCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding", requestedJCEProvider);
          }
        }
        catch (Exception localException)
        {
          throw new XMLEncryptionException("empty", localException);
        }
      } else {
        throw new XMLEncryptionException("empty", localNoSuchAlgorithmException);
      }
    }
    catch (NoSuchProviderException localNoSuchProviderException)
    {
      throw new XMLEncryptionException("empty", localNoSuchProviderException);
    }
    catch (NoSuchPaddingException localNoSuchPaddingException)
    {
      throw new XMLEncryptionException("empty", localNoSuchPaddingException);
    }
    return localCipher;
  }
  
  public Key decryptKey(EncryptedKey paramEncryptedKey)
    throws XMLEncryptionException
  {
    return decryptKey(paramEncryptedKey, ed.getEncryptionMethod().getAlgorithm());
  }
  
  private static void removeContent(Node paramNode)
  {
    while (paramNode.hasChildNodes()) {
      paramNode.removeChild(paramNode.getFirstChild());
    }
  }
  
  private Document decryptElement(Element paramElement)
    throws XMLEncryptionException
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Decrypting element...");
    }
    if (cipherMode != 2) {
      log.log(Level.SEVERE, "XMLCipher unexpectedly not in DECRYPT_MODE...");
    }
    byte[] arrayOfByte = decryptToByteArray(paramElement);
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Decrypted octets:\n" + new String(arrayOfByte));
    }
    Node localNode1 = paramElement.getParentNode();
    Node localNode2 = serializer.deserialize(arrayOfByte, localNode1);
    if ((localNode1 != null) && (9 == localNode1.getNodeType()))
    {
      contextDocument.removeChild(contextDocument.getDocumentElement());
      contextDocument.appendChild(localNode2);
    }
    else if (localNode1 != null)
    {
      localNode1.replaceChild(localNode2, paramElement);
    }
    return contextDocument;
  }
  
  private Document decryptElementContent(Element paramElement)
    throws XMLEncryptionException
  {
    Element localElement = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptedData").item(0);
    if (null == localElement) {
      throw new XMLEncryptionException("No EncryptedData child element.");
    }
    return decryptElement(localElement);
  }
  
  public byte[] decryptToByteArray(Element paramElement)
    throws XMLEncryptionException
  {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Decrypting to ByteArray...");
    }
    if (cipherMode != 2) {
      log.log(Level.SEVERE, "XMLCipher unexpectedly not in DECRYPT_MODE...");
    }
    EncryptedData localEncryptedData = factory.newEncryptedData(paramElement);
    if (key == null)
    {
      localObject1 = localEncryptedData.getKeyInfo();
      if (localObject1 != null) {
        try
        {
          String str1 = localEncryptedData.getEncryptionMethod().getAlgorithm();
          localObject2 = new EncryptedKeyResolver(str1, kek);
          if (internalKeyResolvers != null)
          {
            int i = internalKeyResolvers.size();
            for (int j = 0; j < i; j++) {
              ((EncryptedKeyResolver)localObject2).registerInternalKeyResolver((KeyResolverSpi)internalKeyResolvers.get(j));
            }
          }
          ((KeyInfo)localObject1).registerInternalKeyResolver((KeyResolverSpi)localObject2);
          ((KeyInfo)localObject1).setSecureValidation(secureValidation);
          key = ((KeyInfo)localObject1).getSecretKey();
        }
        catch (KeyResolverException localKeyResolverException)
        {
          if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, localKeyResolverException.getMessage(), localKeyResolverException);
          }
        }
      }
      if (key == null)
      {
        log.log(Level.SEVERE, "XMLCipher::decryptElement called without a key and unable to resolve");
        throw new XMLEncryptionException("encryption.nokey");
      }
    }
    Object localObject1 = new XMLCipherInput(localEncryptedData);
    ((XMLCipherInput)localObject1).setSecureValidation(secureValidation);
    byte[] arrayOfByte1 = ((XMLCipherInput)localObject1).getBytes();
    Object localObject2 = JCEMapper.translateURItoJCEID(localEncryptedData.getEncryptionMethod().getAlgorithm());
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "JCE Algorithm = " + (String)localObject2);
    }
    Cipher localCipher;
    try
    {
      if (requestedJCEProvider == null) {
        localCipher = Cipher.getInstance((String)localObject2);
      } else {
        localCipher = Cipher.getInstance((String)localObject2, requestedJCEProvider);
      }
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new XMLEncryptionException("empty", localNoSuchAlgorithmException);
    }
    catch (NoSuchProviderException localNoSuchProviderException)
    {
      throw new XMLEncryptionException("empty", localNoSuchProviderException);
    }
    catch (NoSuchPaddingException localNoSuchPaddingException)
    {
      throw new XMLEncryptionException("empty", localNoSuchPaddingException);
    }
    int k = localCipher.getBlockSize();
    String str2 = localEncryptedData.getEncryptionMethod().getAlgorithm();
    if (("http://www.w3.org/2009/xmlenc11#aes128-gcm".equals(str2)) || ("http://www.w3.org/2009/xmlenc11#aes192-gcm".equals(str2)) || ("http://www.w3.org/2009/xmlenc11#aes256-gcm".equals(str2))) {
      k = 12;
    }
    byte[] arrayOfByte2 = new byte[k];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, k);
    IvParameterSpec localIvParameterSpec = new IvParameterSpec(arrayOfByte2);
    try
    {
      localCipher.init(cipherMode, key, localIvParameterSpec);
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      throw new XMLEncryptionException("empty", localInvalidKeyException);
    }
    catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException)
    {
      throw new XMLEncryptionException("empty", localInvalidAlgorithmParameterException);
    }
    try
    {
      return localCipher.doFinal(arrayOfByte1, k, arrayOfByte1.length - k);
    }
    catch (IllegalBlockSizeException localIllegalBlockSizeException)
    {
      throw new XMLEncryptionException("empty", localIllegalBlockSizeException);
    }
    catch (BadPaddingException localBadPaddingException)
    {
      throw new XMLEncryptionException("empty", localBadPaddingException);
    }
  }
  
  public EncryptedData createEncryptedData(int paramInt, String paramString)
    throws XMLEncryptionException
  {
    EncryptedData localEncryptedData = null;
    CipherData localCipherData = null;
    switch (paramInt)
    {
    case 2: 
      CipherReference localCipherReference = factory.newCipherReference(paramString);
      localCipherData = factory.newCipherData(paramInt);
      localCipherData.setCipherReference(localCipherReference);
      localEncryptedData = factory.newEncryptedData(localCipherData);
      break;
    case 1: 
      CipherValue localCipherValue = factory.newCipherValue(paramString);
      localCipherData = factory.newCipherData(paramInt);
      localCipherData.setCipherValue(localCipherValue);
      localEncryptedData = factory.newEncryptedData(localCipherData);
    }
    return localEncryptedData;
  }
  
  public EncryptedKey createEncryptedKey(int paramInt, String paramString)
    throws XMLEncryptionException
  {
    EncryptedKey localEncryptedKey = null;
    CipherData localCipherData = null;
    switch (paramInt)
    {
    case 2: 
      CipherReference localCipherReference = factory.newCipherReference(paramString);
      localCipherData = factory.newCipherData(paramInt);
      localCipherData.setCipherReference(localCipherReference);
      localEncryptedKey = factory.newEncryptedKey(localCipherData);
      break;
    case 1: 
      CipherValue localCipherValue = factory.newCipherValue(paramString);
      localCipherData = factory.newCipherData(paramInt);
      localCipherData.setCipherValue(localCipherValue);
      localEncryptedKey = factory.newEncryptedKey(localCipherData);
    }
    return localEncryptedKey;
  }
  
  public AgreementMethod createAgreementMethod(String paramString)
  {
    return factory.newAgreementMethod(paramString);
  }
  
  public CipherData createCipherData(int paramInt)
  {
    return factory.newCipherData(paramInt);
  }
  
  public CipherReference createCipherReference(String paramString)
  {
    return factory.newCipherReference(paramString);
  }
  
  public CipherValue createCipherValue(String paramString)
  {
    return factory.newCipherValue(paramString);
  }
  
  public EncryptionMethod createEncryptionMethod(String paramString)
  {
    return factory.newEncryptionMethod(paramString);
  }
  
  public EncryptionProperties createEncryptionProperties()
  {
    return factory.newEncryptionProperties();
  }
  
  public EncryptionProperty createEncryptionProperty()
  {
    return factory.newEncryptionProperty();
  }
  
  public ReferenceList createReferenceList(int paramInt)
  {
    return factory.newReferenceList(paramInt);
  }
  
  public Transforms createTransforms()
  {
    return factory.newTransforms();
  }
  
  public Transforms createTransforms(Document paramDocument)
  {
    return factory.newTransforms(paramDocument);
  }
  
  private class Factory
  {
    private Factory() {}
    
    AgreementMethod newAgreementMethod(String paramString)
    {
      return new AgreementMethodImpl(paramString);
    }
    
    CipherData newCipherData(int paramInt)
    {
      return new CipherDataImpl(paramInt);
    }
    
    CipherReference newCipherReference(String paramString)
    {
      return new CipherReferenceImpl(paramString);
    }
    
    CipherValue newCipherValue(String paramString)
    {
      return new CipherValueImpl(paramString);
    }
    
    EncryptedData newEncryptedData(CipherData paramCipherData)
    {
      return new EncryptedDataImpl(paramCipherData);
    }
    
    EncryptedKey newEncryptedKey(CipherData paramCipherData)
    {
      return new EncryptedKeyImpl(paramCipherData);
    }
    
    EncryptionMethod newEncryptionMethod(String paramString)
    {
      return new EncryptionMethodImpl(paramString);
    }
    
    EncryptionProperties newEncryptionProperties()
    {
      return new EncryptionPropertiesImpl();
    }
    
    EncryptionProperty newEncryptionProperty()
    {
      return new EncryptionPropertyImpl();
    }
    
    ReferenceList newReferenceList(int paramInt)
    {
      return new ReferenceListImpl(paramInt);
    }
    
    Transforms newTransforms()
    {
      return new TransformsImpl();
    }
    
    Transforms newTransforms(Document paramDocument)
    {
      return new TransformsImpl(paramDocument);
    }
    
    CipherData newCipherData(Element paramElement)
      throws XMLEncryptionException
    {
      if (null == paramElement) {
        throw new NullPointerException("element is null");
      }
      int i = 0;
      Element localElement = null;
      if (paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherValue").getLength() > 0)
      {
        i = 1;
        localElement = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherValue").item(0);
      }
      else if (paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherReference").getLength() > 0)
      {
        i = 2;
        localElement = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherReference").item(0);
      }
      CipherData localCipherData = newCipherData(i);
      if (i == 1) {
        localCipherData.setCipherValue(newCipherValue(localElement));
      } else if (i == 2) {
        localCipherData.setCipherReference(newCipherReference(localElement));
      }
      return localCipherData;
    }
    
    CipherReference newCipherReference(Element paramElement)
      throws XMLEncryptionException
    {
      Attr localAttr = paramElement.getAttributeNodeNS(null, "URI");
      CipherReferenceImpl localCipherReferenceImpl = new CipherReferenceImpl(localAttr);
      NodeList localNodeList = paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "Transforms");
      Element localElement = (Element)localNodeList.item(0);
      if (localElement != null)
      {
        if (XMLCipher.log.isLoggable(Level.FINE)) {
          XMLCipher.log.log(Level.FINE, "Creating a DSIG based Transforms element");
        }
        try
        {
          localCipherReferenceImpl.setTransforms(new TransformsImpl(localElement));
        }
        catch (XMLSignatureException localXMLSignatureException)
        {
          throw new XMLEncryptionException("empty", localXMLSignatureException);
        }
        catch (InvalidTransformException localInvalidTransformException)
        {
          throw new XMLEncryptionException("empty", localInvalidTransformException);
        }
        catch (XMLSecurityException localXMLSecurityException)
        {
          throw new XMLEncryptionException("empty", localXMLSecurityException);
        }
      }
      return localCipherReferenceImpl;
    }
    
    CipherValue newCipherValue(Element paramElement)
    {
      String str = XMLUtils.getFullTextChildrenFromElement(paramElement);
      return newCipherValue(str);
    }
    
    EncryptedData newEncryptedData(Element paramElement)
      throws XMLEncryptionException
    {
      EncryptedData localEncryptedData = null;
      NodeList localNodeList = paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherData");
      Element localElement1 = (Element)localNodeList.item(localNodeList.getLength() - 1);
      CipherData localCipherData = newCipherData(localElement1);
      localEncryptedData = newEncryptedData(localCipherData);
      localEncryptedData.setId(paramElement.getAttributeNS(null, "Id"));
      localEncryptedData.setType(paramElement.getAttributeNS(null, "Type"));
      localEncryptedData.setMimeType(paramElement.getAttributeNS(null, "MimeType"));
      localEncryptedData.setEncoding(paramElement.getAttributeNS(null, "Encoding"));
      Element localElement2 = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionMethod").item(0);
      if (null != localElement2) {
        localEncryptedData.setEncryptionMethod(newEncryptionMethod(localElement2));
      }
      Element localElement3 = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "KeyInfo").item(0);
      if (null != localElement3)
      {
        localObject = newKeyInfo(localElement3);
        localEncryptedData.setKeyInfo((KeyInfo)localObject);
      }
      Object localObject = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionProperties").item(0);
      if (null != localObject) {
        localEncryptedData.setEncryptionProperties(newEncryptionProperties((Element)localObject));
      }
      return localEncryptedData;
    }
    
    EncryptedKey newEncryptedKey(Element paramElement)
      throws XMLEncryptionException
    {
      EncryptedKey localEncryptedKey = null;
      NodeList localNodeList = paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherData");
      Element localElement1 = (Element)localNodeList.item(localNodeList.getLength() - 1);
      CipherData localCipherData = newCipherData(localElement1);
      localEncryptedKey = newEncryptedKey(localCipherData);
      localEncryptedKey.setId(paramElement.getAttributeNS(null, "Id"));
      localEncryptedKey.setType(paramElement.getAttributeNS(null, "Type"));
      localEncryptedKey.setMimeType(paramElement.getAttributeNS(null, "MimeType"));
      localEncryptedKey.setEncoding(paramElement.getAttributeNS(null, "Encoding"));
      localEncryptedKey.setRecipient(paramElement.getAttributeNS(null, "Recipient"));
      Element localElement2 = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionMethod").item(0);
      if (null != localElement2) {
        localEncryptedKey.setEncryptionMethod(newEncryptionMethod(localElement2));
      }
      Element localElement3 = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "KeyInfo").item(0);
      if (null != localElement3)
      {
        localObject = newKeyInfo(localElement3);
        localEncryptedKey.setKeyInfo((KeyInfo)localObject);
      }
      Object localObject = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionProperties").item(0);
      if (null != localObject) {
        localEncryptedKey.setEncryptionProperties(newEncryptionProperties((Element)localObject));
      }
      Element localElement4 = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "ReferenceList").item(0);
      if (null != localElement4) {
        localEncryptedKey.setReferenceList(newReferenceList(localElement4));
      }
      Element localElement5 = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CarriedKeyName").item(0);
      if (null != localElement5) {
        localEncryptedKey.setCarriedName(localElement5.getFirstChild().getNodeValue());
      }
      return localEncryptedKey;
    }
    
    KeyInfo newKeyInfo(Element paramElement)
      throws XMLEncryptionException
    {
      try
      {
        KeyInfo localKeyInfo = new KeyInfo(paramElement, null);
        localKeyInfo.setSecureValidation(secureValidation);
        if (internalKeyResolvers != null)
        {
          int i = internalKeyResolvers.size();
          for (int j = 0; j < i; j++) {
            localKeyInfo.registerInternalKeyResolver((KeyResolverSpi)internalKeyResolvers.get(j));
          }
        }
        return localKeyInfo;
      }
      catch (XMLSecurityException localXMLSecurityException)
      {
        throw new XMLEncryptionException("Error loading Key Info", localXMLSecurityException);
      }
    }
    
    EncryptionMethod newEncryptionMethod(Element paramElement)
    {
      String str1 = paramElement.getAttributeNS(null, "Algorithm");
      EncryptionMethod localEncryptionMethod = newEncryptionMethod(str1);
      Element localElement1 = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "KeySize").item(0);
      if (null != localElement1) {
        localEncryptionMethod.setKeySize(Integer.valueOf(localElement1.getFirstChild().getNodeValue()).intValue());
      }
      Element localElement2 = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "OAEPparams").item(0);
      if (null != localElement2) {
        try
        {
          String str2 = localElement2.getFirstChild().getNodeValue();
          localEncryptionMethod.setOAEPparams(Base64.decode(str2.getBytes("UTF-8")));
        }
        catch (UnsupportedEncodingException localUnsupportedEncodingException)
        {
          throw new RuntimeException("UTF-8 not supported", localUnsupportedEncodingException);
        }
        catch (Base64DecodingException localBase64DecodingException)
        {
          throw new RuntimeException("BASE-64 decoding error", localBase64DecodingException);
        }
      }
      Element localElement3 = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "DigestMethod").item(0);
      if (localElement3 != null)
      {
        localObject = localElement3.getAttributeNS(null, "Algorithm");
        localEncryptionMethod.setDigestAlgorithm((String)localObject);
      }
      Object localObject = (Element)paramElement.getElementsByTagNameNS("http://www.w3.org/2009/xmlenc11#", "MGF").item(0);
      if ((localObject != null) && (!"http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p".equals(algorithm)))
      {
        String str3 = ((Element)localObject).getAttributeNS(null, "Algorithm");
        localEncryptionMethod.setMGFAlgorithm(str3);
      }
      return localEncryptionMethod;
    }
    
    EncryptionProperties newEncryptionProperties(Element paramElement)
    {
      EncryptionProperties localEncryptionProperties = newEncryptionProperties();
      localEncryptionProperties.setId(paramElement.getAttributeNS(null, "Id"));
      NodeList localNodeList = paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionProperty");
      for (int i = 0; i < localNodeList.getLength(); i++)
      {
        Node localNode = localNodeList.item(i);
        if (null != localNode) {
          localEncryptionProperties.addEncryptionProperty(newEncryptionProperty((Element)localNode));
        }
      }
      return localEncryptionProperties;
    }
    
    EncryptionProperty newEncryptionProperty(Element paramElement)
    {
      EncryptionProperty localEncryptionProperty = newEncryptionProperty();
      localEncryptionProperty.setTarget(paramElement.getAttributeNS(null, "Target"));
      localEncryptionProperty.setId(paramElement.getAttributeNS(null, "Id"));
      return localEncryptionProperty;
    }
    
    ReferenceList newReferenceList(Element paramElement)
    {
      int i = 0;
      if (null != paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "DataReference").item(0)) {
        i = 1;
      } else if (null != paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "KeyReference").item(0)) {
        i = 2;
      }
      ReferenceListImpl localReferenceListImpl = new ReferenceListImpl(i);
      NodeList localNodeList = null;
      int j;
      String str;
      switch (i)
      {
      case 1: 
        localNodeList = paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "DataReference");
        for (j = 0; j < localNodeList.getLength(); j++)
        {
          str = ((Element)localNodeList.item(j)).getAttribute("URI");
          localReferenceListImpl.add(localReferenceListImpl.newDataReference(str));
        }
        break;
      case 2: 
        localNodeList = paramElement.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "KeyReference");
        for (j = 0; j < localNodeList.getLength(); j++)
        {
          str = ((Element)localNodeList.item(j)).getAttribute("URI");
          localReferenceListImpl.add(localReferenceListImpl.newKeyReference(str));
        }
      }
      return localReferenceListImpl;
    }
    
    Element toElement(EncryptedData paramEncryptedData)
    {
      return ((EncryptedDataImpl)paramEncryptedData).toElement();
    }
    
    Element toElement(EncryptedKey paramEncryptedKey)
    {
      return ((EncryptedKeyImpl)paramEncryptedKey).toElement();
    }
    
    Element toElement(ReferenceList paramReferenceList)
    {
      return ((ReferenceListImpl)paramReferenceList).toElement();
    }
    
    private class AgreementMethodImpl
      implements AgreementMethod
    {
      private byte[] kaNonce = null;
      private List<Element> agreementMethodInformation = null;
      private KeyInfo originatorKeyInfo = null;
      private KeyInfo recipientKeyInfo = null;
      private String algorithmURI = null;
      
      public AgreementMethodImpl(String paramString)
      {
        URI localURI = null;
        try
        {
          localURI = new URI(paramString);
        }
        catch (URISyntaxException localURISyntaxException)
        {
          throw ((IllegalArgumentException)new IllegalArgumentException().initCause(localURISyntaxException));
        }
        algorithmURI = localURI.toString();
      }
      
      public byte[] getKANonce()
      {
        return kaNonce;
      }
      
      public void setKANonce(byte[] paramArrayOfByte)
      {
        kaNonce = paramArrayOfByte;
      }
      
      public Iterator<Element> getAgreementMethodInformation()
      {
        return agreementMethodInformation.iterator();
      }
      
      public void addAgreementMethodInformation(Element paramElement)
      {
        agreementMethodInformation.add(paramElement);
      }
      
      public void revoveAgreementMethodInformation(Element paramElement)
      {
        agreementMethodInformation.remove(paramElement);
      }
      
      public KeyInfo getOriginatorKeyInfo()
      {
        return originatorKeyInfo;
      }
      
      public void setOriginatorKeyInfo(KeyInfo paramKeyInfo)
      {
        originatorKeyInfo = paramKeyInfo;
      }
      
      public KeyInfo getRecipientKeyInfo()
      {
        return recipientKeyInfo;
      }
      
      public void setRecipientKeyInfo(KeyInfo paramKeyInfo)
      {
        recipientKeyInfo = paramKeyInfo;
      }
      
      public String getAlgorithm()
      {
        return algorithmURI;
      }
    }
    
    private class CipherDataImpl
      implements CipherData
    {
      private static final String valueMessage = "Data type is reference type.";
      private static final String referenceMessage = "Data type is value type.";
      private CipherValue cipherValue = null;
      private CipherReference cipherReference = null;
      private int cipherType = Integer.MIN_VALUE;
      
      public CipherDataImpl(int paramInt)
      {
        cipherType = paramInt;
      }
      
      public CipherValue getCipherValue()
      {
        return cipherValue;
      }
      
      public void setCipherValue(CipherValue paramCipherValue)
        throws XMLEncryptionException
      {
        if (cipherType == 2) {
          throw new XMLEncryptionException("empty", new UnsupportedOperationException("Data type is reference type."));
        }
        cipherValue = paramCipherValue;
      }
      
      public CipherReference getCipherReference()
      {
        return cipherReference;
      }
      
      public void setCipherReference(CipherReference paramCipherReference)
        throws XMLEncryptionException
      {
        if (cipherType == 1) {
          throw new XMLEncryptionException("empty", new UnsupportedOperationException("Data type is value type."));
        }
        cipherReference = paramCipherReference;
      }
      
      public int getDataType()
      {
        return cipherType;
      }
      
      Element toElement()
      {
        Element localElement = XMLUtils.createElementInEncryptionSpace(contextDocument, "CipherData");
        if (cipherType == 1) {
          localElement.appendChild(((XMLCipher.Factory.CipherValueImpl)cipherValue).toElement());
        } else if (cipherType == 2) {
          localElement.appendChild(((XMLCipher.Factory.CipherReferenceImpl)cipherReference).toElement());
        }
        return localElement;
      }
    }
    
    private class CipherReferenceImpl
      implements CipherReference
    {
      private String referenceURI = null;
      private Transforms referenceTransforms = null;
      private Attr referenceNode = null;
      
      public CipherReferenceImpl(String paramString)
      {
        referenceURI = paramString;
        referenceNode = null;
      }
      
      public CipherReferenceImpl(Attr paramAttr)
      {
        referenceURI = paramAttr.getNodeValue();
        referenceNode = paramAttr;
      }
      
      public String getURI()
      {
        return referenceURI;
      }
      
      public Attr getURIAsAttr()
      {
        return referenceNode;
      }
      
      public Transforms getTransforms()
      {
        return referenceTransforms;
      }
      
      public void setTransforms(Transforms paramTransforms)
      {
        referenceTransforms = paramTransforms;
      }
      
      Element toElement()
      {
        Element localElement = XMLUtils.createElementInEncryptionSpace(contextDocument, "CipherReference");
        localElement.setAttributeNS(null, "URI", referenceURI);
        if (null != referenceTransforms) {
          localElement.appendChild(((XMLCipher.Factory.TransformsImpl)referenceTransforms).toElement());
        }
        return localElement;
      }
    }
    
    private class CipherValueImpl
      implements CipherValue
    {
      private String cipherValue = null;
      
      public CipherValueImpl(String paramString)
      {
        cipherValue = paramString;
      }
      
      public String getValue()
      {
        return cipherValue;
      }
      
      public void setValue(String paramString)
      {
        cipherValue = paramString;
      }
      
      Element toElement()
      {
        Element localElement = XMLUtils.createElementInEncryptionSpace(contextDocument, "CipherValue");
        localElement.appendChild(contextDocument.createTextNode(cipherValue));
        return localElement;
      }
    }
    
    private class EncryptedDataImpl
      extends XMLCipher.Factory.EncryptedTypeImpl
      implements EncryptedData
    {
      public EncryptedDataImpl(CipherData paramCipherData)
      {
        super(paramCipherData);
      }
      
      Element toElement()
      {
        Element localElement = ElementProxy.createElementForFamily(contextDocument, "http://www.w3.org/2001/04/xmlenc#", "EncryptedData");
        if (null != super.getId()) {
          localElement.setAttributeNS(null, "Id", super.getId());
        }
        if (null != super.getType()) {
          localElement.setAttributeNS(null, "Type", super.getType());
        }
        if (null != super.getMimeType()) {
          localElement.setAttributeNS(null, "MimeType", super.getMimeType());
        }
        if (null != super.getEncoding()) {
          localElement.setAttributeNS(null, "Encoding", super.getEncoding());
        }
        if (null != super.getEncryptionMethod()) {
          localElement.appendChild(((XMLCipher.Factory.EncryptionMethodImpl)super.getEncryptionMethod()).toElement());
        }
        if (null != super.getKeyInfo()) {
          localElement.appendChild(super.getKeyInfo().getElement().cloneNode(true));
        }
        localElement.appendChild(((XMLCipher.Factory.CipherDataImpl)super.getCipherData()).toElement());
        if (null != super.getEncryptionProperties()) {
          localElement.appendChild(((XMLCipher.Factory.EncryptionPropertiesImpl)super.getEncryptionProperties()).toElement());
        }
        return localElement;
      }
    }
    
    private class EncryptedKeyImpl
      extends XMLCipher.Factory.EncryptedTypeImpl
      implements EncryptedKey
    {
      private String keyRecipient = null;
      private ReferenceList referenceList = null;
      private String carriedName = null;
      
      public EncryptedKeyImpl(CipherData paramCipherData)
      {
        super(paramCipherData);
      }
      
      public String getRecipient()
      {
        return keyRecipient;
      }
      
      public void setRecipient(String paramString)
      {
        keyRecipient = paramString;
      }
      
      public ReferenceList getReferenceList()
      {
        return referenceList;
      }
      
      public void setReferenceList(ReferenceList paramReferenceList)
      {
        referenceList = paramReferenceList;
      }
      
      public String getCarriedName()
      {
        return carriedName;
      }
      
      public void setCarriedName(String paramString)
      {
        carriedName = paramString;
      }
      
      Element toElement()
      {
        Element localElement1 = ElementProxy.createElementForFamily(contextDocument, "http://www.w3.org/2001/04/xmlenc#", "EncryptedKey");
        if (null != super.getId()) {
          localElement1.setAttributeNS(null, "Id", super.getId());
        }
        if (null != super.getType()) {
          localElement1.setAttributeNS(null, "Type", super.getType());
        }
        if (null != super.getMimeType()) {
          localElement1.setAttributeNS(null, "MimeType", super.getMimeType());
        }
        if (null != super.getEncoding()) {
          localElement1.setAttributeNS(null, "Encoding", super.getEncoding());
        }
        if (null != getRecipient()) {
          localElement1.setAttributeNS(null, "Recipient", getRecipient());
        }
        if (null != super.getEncryptionMethod()) {
          localElement1.appendChild(((XMLCipher.Factory.EncryptionMethodImpl)super.getEncryptionMethod()).toElement());
        }
        if (null != super.getKeyInfo()) {
          localElement1.appendChild(super.getKeyInfo().getElement().cloneNode(true));
        }
        localElement1.appendChild(((XMLCipher.Factory.CipherDataImpl)super.getCipherData()).toElement());
        if (null != super.getEncryptionProperties()) {
          localElement1.appendChild(((XMLCipher.Factory.EncryptionPropertiesImpl)super.getEncryptionProperties()).toElement());
        }
        if ((referenceList != null) && (!referenceList.isEmpty())) {
          localElement1.appendChild(((XMLCipher.Factory.ReferenceListImpl)getReferenceList()).toElement());
        }
        if (null != carriedName)
        {
          Element localElement2 = ElementProxy.createElementForFamily(contextDocument, "http://www.w3.org/2001/04/xmlenc#", "CarriedKeyName");
          Text localText = contextDocument.createTextNode(carriedName);
          localElement2.appendChild(localText);
          localElement1.appendChild(localElement2);
        }
        return localElement1;
      }
    }
    
    private abstract class EncryptedTypeImpl
    {
      private String id = null;
      private String type = null;
      private String mimeType = null;
      private String encoding = null;
      private EncryptionMethod encryptionMethod = null;
      private KeyInfo keyInfo = null;
      private CipherData cipherData = null;
      private EncryptionProperties encryptionProperties = null;
      
      protected EncryptedTypeImpl(CipherData paramCipherData)
      {
        cipherData = paramCipherData;
      }
      
      public String getId()
      {
        return id;
      }
      
      public void setId(String paramString)
      {
        id = paramString;
      }
      
      public String getType()
      {
        return type;
      }
      
      public void setType(String paramString)
      {
        if ((paramString == null) || (paramString.length() == 0))
        {
          type = null;
        }
        else
        {
          URI localURI = null;
          try
          {
            localURI = new URI(paramString);
          }
          catch (URISyntaxException localURISyntaxException)
          {
            throw ((IllegalArgumentException)new IllegalArgumentException().initCause(localURISyntaxException));
          }
          type = localURI.toString();
        }
      }
      
      public String getMimeType()
      {
        return mimeType;
      }
      
      public void setMimeType(String paramString)
      {
        mimeType = paramString;
      }
      
      public String getEncoding()
      {
        return encoding;
      }
      
      public void setEncoding(String paramString)
      {
        if ((paramString == null) || (paramString.length() == 0))
        {
          encoding = null;
        }
        else
        {
          URI localURI = null;
          try
          {
            localURI = new URI(paramString);
          }
          catch (URISyntaxException localURISyntaxException)
          {
            throw ((IllegalArgumentException)new IllegalArgumentException().initCause(localURISyntaxException));
          }
          encoding = localURI.toString();
        }
      }
      
      public EncryptionMethod getEncryptionMethod()
      {
        return encryptionMethod;
      }
      
      public void setEncryptionMethod(EncryptionMethod paramEncryptionMethod)
      {
        encryptionMethod = paramEncryptionMethod;
      }
      
      public KeyInfo getKeyInfo()
      {
        return keyInfo;
      }
      
      public void setKeyInfo(KeyInfo paramKeyInfo)
      {
        keyInfo = paramKeyInfo;
      }
      
      public CipherData getCipherData()
      {
        return cipherData;
      }
      
      public EncryptionProperties getEncryptionProperties()
      {
        return encryptionProperties;
      }
      
      public void setEncryptionProperties(EncryptionProperties paramEncryptionProperties)
      {
        encryptionProperties = paramEncryptionProperties;
      }
    }
    
    private class EncryptionMethodImpl
      implements EncryptionMethod
    {
      private String algorithm = null;
      private int keySize = Integer.MIN_VALUE;
      private byte[] oaepParams = null;
      private List<Element> encryptionMethodInformation = null;
      private String digestAlgorithm = null;
      private String mgfAlgorithm = null;
      
      public EncryptionMethodImpl(String paramString)
      {
        URI localURI = null;
        try
        {
          localURI = new URI(paramString);
        }
        catch (URISyntaxException localURISyntaxException)
        {
          throw ((IllegalArgumentException)new IllegalArgumentException().initCause(localURISyntaxException));
        }
        algorithm = localURI.toString();
        encryptionMethodInformation = new LinkedList();
      }
      
      public String getAlgorithm()
      {
        return algorithm;
      }
      
      public int getKeySize()
      {
        return keySize;
      }
      
      public void setKeySize(int paramInt)
      {
        keySize = paramInt;
      }
      
      public byte[] getOAEPparams()
      {
        return oaepParams;
      }
      
      public void setOAEPparams(byte[] paramArrayOfByte)
      {
        oaepParams = paramArrayOfByte;
      }
      
      public void setDigestAlgorithm(String paramString)
      {
        digestAlgorithm = paramString;
      }
      
      public String getDigestAlgorithm()
      {
        return digestAlgorithm;
      }
      
      public void setMGFAlgorithm(String paramString)
      {
        mgfAlgorithm = paramString;
      }
      
      public String getMGFAlgorithm()
      {
        return mgfAlgorithm;
      }
      
      public Iterator<Element> getEncryptionMethodInformation()
      {
        return encryptionMethodInformation.iterator();
      }
      
      public void addEncryptionMethodInformation(Element paramElement)
      {
        encryptionMethodInformation.add(paramElement);
      }
      
      public void removeEncryptionMethodInformation(Element paramElement)
      {
        encryptionMethodInformation.remove(paramElement);
      }
      
      Element toElement()
      {
        Element localElement = XMLUtils.createElementInEncryptionSpace(contextDocument, "EncryptionMethod");
        localElement.setAttributeNS(null, "Algorithm", algorithm);
        if (keySize > 0) {
          localElement.appendChild(XMLUtils.createElementInEncryptionSpace(contextDocument, "KeySize").appendChild(contextDocument.createTextNode(String.valueOf(keySize))));
        }
        if (null != oaepParams)
        {
          localObject = XMLUtils.createElementInEncryptionSpace(contextDocument, "OAEPparams");
          ((Element)localObject).appendChild(contextDocument.createTextNode(Base64.encode(oaepParams)));
          localElement.appendChild((Node)localObject);
        }
        if (digestAlgorithm != null)
        {
          localObject = XMLUtils.createElementInSignatureSpace(contextDocument, "DigestMethod");
          ((Element)localObject).setAttributeNS(null, "Algorithm", digestAlgorithm);
          localElement.appendChild((Node)localObject);
        }
        if (mgfAlgorithm != null)
        {
          localObject = XMLUtils.createElementInEncryption11Space(contextDocument, "MGF");
          ((Element)localObject).setAttributeNS(null, "Algorithm", mgfAlgorithm);
          ((Element)localObject).setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + ElementProxy.getDefaultPrefix("http://www.w3.org/2009/xmlenc11#"), "http://www.w3.org/2009/xmlenc11#");
          localElement.appendChild((Node)localObject);
        }
        Object localObject = encryptionMethodInformation.iterator();
        while (((Iterator)localObject).hasNext()) {
          localElement.appendChild((Node)((Iterator)localObject).next());
        }
        return localElement;
      }
    }
    
    private class EncryptionPropertiesImpl
      implements EncryptionProperties
    {
      private String id = null;
      private List<EncryptionProperty> encryptionProperties = null;
      
      public EncryptionPropertiesImpl() {}
      
      public String getId()
      {
        return id;
      }
      
      public void setId(String paramString)
      {
        id = paramString;
      }
      
      public Iterator<EncryptionProperty> getEncryptionProperties()
      {
        return encryptionProperties.iterator();
      }
      
      public void addEncryptionProperty(EncryptionProperty paramEncryptionProperty)
      {
        encryptionProperties.add(paramEncryptionProperty);
      }
      
      public void removeEncryptionProperty(EncryptionProperty paramEncryptionProperty)
      {
        encryptionProperties.remove(paramEncryptionProperty);
      }
      
      Element toElement()
      {
        Element localElement = XMLUtils.createElementInEncryptionSpace(contextDocument, "EncryptionProperties");
        if (null != id) {
          localElement.setAttributeNS(null, "Id", id);
        }
        Iterator localIterator = getEncryptionProperties();
        while (localIterator.hasNext()) {
          localElement.appendChild(((XMLCipher.Factory.EncryptionPropertyImpl)localIterator.next()).toElement());
        }
        return localElement;
      }
    }
    
    private class EncryptionPropertyImpl
      implements EncryptionProperty
    {
      private String target = null;
      private String id = null;
      private Map<String, String> attributeMap = new HashMap();
      private List<Element> encryptionInformation = null;
      
      public EncryptionPropertyImpl() {}
      
      public String getTarget()
      {
        return target;
      }
      
      public void setTarget(String paramString)
      {
        if ((paramString == null) || (paramString.length() == 0))
        {
          target = null;
        }
        else if (paramString.startsWith("#"))
        {
          target = paramString;
        }
        else
        {
          URI localURI = null;
          try
          {
            localURI = new URI(paramString);
          }
          catch (URISyntaxException localURISyntaxException)
          {
            throw ((IllegalArgumentException)new IllegalArgumentException().initCause(localURISyntaxException));
          }
          target = localURI.toString();
        }
      }
      
      public String getId()
      {
        return id;
      }
      
      public void setId(String paramString)
      {
        id = paramString;
      }
      
      public String getAttribute(String paramString)
      {
        return (String)attributeMap.get(paramString);
      }
      
      public void setAttribute(String paramString1, String paramString2)
      {
        attributeMap.put(paramString1, paramString2);
      }
      
      public Iterator<Element> getEncryptionInformation()
      {
        return encryptionInformation.iterator();
      }
      
      public void addEncryptionInformation(Element paramElement)
      {
        encryptionInformation.add(paramElement);
      }
      
      public void removeEncryptionInformation(Element paramElement)
      {
        encryptionInformation.remove(paramElement);
      }
      
      Element toElement()
      {
        Element localElement = XMLUtils.createElementInEncryptionSpace(contextDocument, "EncryptionProperty");
        if (null != target) {
          localElement.setAttributeNS(null, "Target", target);
        }
        if (null != id) {
          localElement.setAttributeNS(null, "Id", id);
        }
        return localElement;
      }
    }
    
    private class ReferenceListImpl
      implements ReferenceList
    {
      private Class<?> sentry;
      private List<Reference> references;
      
      public ReferenceListImpl(int paramInt)
      {
        if (paramInt == 1) {
          sentry = DataReference.class;
        } else if (paramInt == 2) {
          sentry = KeyReference.class;
        } else {
          throw new IllegalArgumentException();
        }
        references = new LinkedList();
      }
      
      public void add(Reference paramReference)
      {
        if (!paramReference.getClass().equals(sentry)) {
          throw new IllegalArgumentException();
        }
        references.add(paramReference);
      }
      
      public void remove(Reference paramReference)
      {
        if (!paramReference.getClass().equals(sentry)) {
          throw new IllegalArgumentException();
        }
        references.remove(paramReference);
      }
      
      public int size()
      {
        return references.size();
      }
      
      public boolean isEmpty()
      {
        return references.isEmpty();
      }
      
      public Iterator<Reference> getReferences()
      {
        return references.iterator();
      }
      
      Element toElement()
      {
        Element localElement = ElementProxy.createElementForFamily(contextDocument, "http://www.w3.org/2001/04/xmlenc#", "ReferenceList");
        Iterator localIterator = references.iterator();
        while (localIterator.hasNext())
        {
          Reference localReference = (Reference)localIterator.next();
          localElement.appendChild(((ReferenceImpl)localReference).toElement());
        }
        return localElement;
      }
      
      public Reference newDataReference(String paramString)
      {
        return new DataReference(paramString);
      }
      
      public Reference newKeyReference(String paramString)
      {
        return new KeyReference(paramString);
      }
      
      private class DataReference
        extends XMLCipher.Factory.ReferenceListImpl.ReferenceImpl
      {
        DataReference(String paramString)
        {
          super(paramString);
        }
        
        public String getType()
        {
          return "DataReference";
        }
      }
      
      private class KeyReference
        extends XMLCipher.Factory.ReferenceListImpl.ReferenceImpl
      {
        KeyReference(String paramString)
        {
          super(paramString);
        }
        
        public String getType()
        {
          return "KeyReference";
        }
      }
      
      private abstract class ReferenceImpl
        implements Reference
      {
        private String uri;
        private List<Element> referenceInformation;
        
        ReferenceImpl(String paramString)
        {
          uri = paramString;
          referenceInformation = new LinkedList();
        }
        
        public abstract String getType();
        
        public String getURI()
        {
          return uri;
        }
        
        public Iterator<Element> getElementRetrievalInformation()
        {
          return referenceInformation.iterator();
        }
        
        public void setURI(String paramString)
        {
          uri = paramString;
        }
        
        public void removeElementRetrievalInformation(Element paramElement)
        {
          referenceInformation.remove(paramElement);
        }
        
        public void addElementRetrievalInformation(Element paramElement)
        {
          referenceInformation.add(paramElement);
        }
        
        public Element toElement()
        {
          String str = getType();
          Element localElement = ElementProxy.createElementForFamily(contextDocument, "http://www.w3.org/2001/04/xmlenc#", str);
          localElement.setAttribute("URI", uri);
          return localElement;
        }
      }
    }
    
    private class TransformsImpl
      extends com.sun.org.apache.xml.internal.security.transforms.Transforms
      implements Transforms
    {
      public TransformsImpl()
      {
        super();
      }
      
      public TransformsImpl(Document paramDocument)
      {
        if (paramDocument == null) {
          throw new RuntimeException("Document is null");
        }
        doc = paramDocument;
        constructionElement = createElementForFamilyLocal(doc, getBaseNamespace(), getBaseLocalName());
      }
      
      public TransformsImpl(Element paramElement)
        throws XMLSignatureException, InvalidTransformException, XMLSecurityException, TransformationException
      {
        super("");
      }
      
      public Element toElement()
      {
        if (doc == null) {
          doc = contextDocument;
        }
        return getElement();
      }
      
      public com.sun.org.apache.xml.internal.security.transforms.Transforms getDSTransforms()
      {
        return this;
      }
      
      public String getBaseNamespace()
      {
        return "http://www.w3.org/2001/04/xmlenc#";
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\encryption\XMLCipher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */