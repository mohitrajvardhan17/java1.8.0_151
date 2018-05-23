package com.sun.org.apache.xml.internal.security.algorithms.implementations;

import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import com.sun.org.apache.xml.internal.security.algorithms.MessageDigestAlgorithm;
import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithmSpi;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public abstract class IntegrityHmac
  extends SignatureAlgorithmSpi
{
  private static Logger log = Logger.getLogger(IntegrityHmac.class.getName());
  private Mac macAlgorithm = null;
  private int HMACOutputLength = 0;
  private boolean HMACOutputLengthSet = false;
  
  public abstract String engineGetURI();
  
  abstract int getDigestLength();
  
  public IntegrityHmac()
    throws XMLSignatureException
  {
    String str = JCEMapper.translateURItoJCEID(engineGetURI());
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Created IntegrityHmacSHA1 using " + str);
    }
    try
    {
      macAlgorithm = Mac.getInstance(str);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      Object[] arrayOfObject = { str, localNoSuchAlgorithmException.getLocalizedMessage() };
      throw new XMLSignatureException("algorithms.NoSuchAlgorithm", arrayOfObject);
    }
  }
  
  protected void engineSetParameter(AlgorithmParameterSpec paramAlgorithmParameterSpec)
    throws XMLSignatureException
  {
    throw new XMLSignatureException("empty");
  }
  
  public void reset()
  {
    HMACOutputLength = 0;
    HMACOutputLengthSet = false;
    macAlgorithm.reset();
  }
  
  protected boolean engineVerify(byte[] paramArrayOfByte)
    throws XMLSignatureException
  {
    try
    {
      if ((HMACOutputLengthSet) && (HMACOutputLength < getDigestLength()))
      {
        if (log.isLoggable(Level.FINE)) {
          log.log(Level.FINE, "HMACOutputLength must not be less than " + getDigestLength());
        }
        localObject = new Object[] { String.valueOf(getDigestLength()) };
        throw new XMLSignatureException("algorithms.HMACOutputLengthMin", (Object[])localObject);
      }
      Object localObject = macAlgorithm.doFinal();
      return MessageDigestAlgorithm.isEqual((byte[])localObject, paramArrayOfByte);
    }
    catch (IllegalStateException localIllegalStateException)
    {
      throw new XMLSignatureException("empty", localIllegalStateException);
    }
  }
  
  protected void engineInitVerify(Key paramKey)
    throws XMLSignatureException
  {
    Object localObject;
    if (!(paramKey instanceof SecretKey))
    {
      String str = paramKey.getClass().getName();
      localObject = SecretKey.class.getName();
      Object[] arrayOfObject = { str, localObject };
      throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", arrayOfObject);
    }
    try
    {
      macAlgorithm.init(paramKey);
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      localObject = macAlgorithm;
      try
      {
        macAlgorithm = Mac.getInstance(macAlgorithm.getAlgorithm());
      }
      catch (Exception localException)
      {
        if (log.isLoggable(Level.FINE)) {
          log.log(Level.FINE, "Exception when reinstantiating Mac:" + localException);
        }
        macAlgorithm = ((Mac)localObject);
      }
      throw new XMLSignatureException("empty", localInvalidKeyException);
    }
  }
  
  protected byte[] engineSign()
    throws XMLSignatureException
  {
    try
    {
      if ((HMACOutputLengthSet) && (HMACOutputLength < getDigestLength()))
      {
        if (log.isLoggable(Level.FINE)) {
          log.log(Level.FINE, "HMACOutputLength must not be less than " + getDigestLength());
        }
        Object[] arrayOfObject = { String.valueOf(getDigestLength()) };
        throw new XMLSignatureException("algorithms.HMACOutputLengthMin", arrayOfObject);
      }
      return macAlgorithm.doFinal();
    }
    catch (IllegalStateException localIllegalStateException)
    {
      throw new XMLSignatureException("empty", localIllegalStateException);
    }
  }
  
  protected void engineInitSign(Key paramKey)
    throws XMLSignatureException
  {
    if (!(paramKey instanceof SecretKey))
    {
      String str1 = paramKey.getClass().getName();
      String str2 = SecretKey.class.getName();
      Object[] arrayOfObject = { str1, str2 };
      throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", arrayOfObject);
    }
    try
    {
      macAlgorithm.init(paramKey);
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      throw new XMLSignatureException("empty", localInvalidKeyException);
    }
  }
  
  protected void engineInitSign(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec)
    throws XMLSignatureException
  {
    if (!(paramKey instanceof SecretKey))
    {
      String str1 = paramKey.getClass().getName();
      String str2 = SecretKey.class.getName();
      Object[] arrayOfObject = { str1, str2 };
      throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", arrayOfObject);
    }
    try
    {
      macAlgorithm.init(paramKey, paramAlgorithmParameterSpec);
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      throw new XMLSignatureException("empty", localInvalidKeyException);
    }
    catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException)
    {
      throw new XMLSignatureException("empty", localInvalidAlgorithmParameterException);
    }
  }
  
  protected void engineInitSign(Key paramKey, SecureRandom paramSecureRandom)
    throws XMLSignatureException
  {
    throw new XMLSignatureException("algorithms.CannotUseSecureRandomOnMAC");
  }
  
  protected void engineUpdate(byte[] paramArrayOfByte)
    throws XMLSignatureException
  {
    try
    {
      macAlgorithm.update(paramArrayOfByte);
    }
    catch (IllegalStateException localIllegalStateException)
    {
      throw new XMLSignatureException("empty", localIllegalStateException);
    }
  }
  
  protected void engineUpdate(byte paramByte)
    throws XMLSignatureException
  {
    try
    {
      macAlgorithm.update(paramByte);
    }
    catch (IllegalStateException localIllegalStateException)
    {
      throw new XMLSignatureException("empty", localIllegalStateException);
    }
  }
  
  protected void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws XMLSignatureException
  {
    try
    {
      macAlgorithm.update(paramArrayOfByte, paramInt1, paramInt2);
    }
    catch (IllegalStateException localIllegalStateException)
    {
      throw new XMLSignatureException("empty", localIllegalStateException);
    }
  }
  
  protected String engineGetJCEAlgorithmString()
  {
    return macAlgorithm.getAlgorithm();
  }
  
  protected String engineGetJCEProviderName()
  {
    return macAlgorithm.getProvider().getName();
  }
  
  protected void engineSetHMACOutputLength(int paramInt)
  {
    HMACOutputLength = paramInt;
    HMACOutputLengthSet = true;
  }
  
  protected void engineGetContextFromElement(Element paramElement)
  {
    super.engineGetContextFromElement(paramElement);
    if (paramElement == null) {
      throw new IllegalArgumentException("element null");
    }
    Text localText = XMLUtils.selectDsNodeText(paramElement.getFirstChild(), "HMACOutputLength", 0);
    if (localText != null)
    {
      HMACOutputLength = Integer.parseInt(localText.getData());
      HMACOutputLengthSet = true;
    }
  }
  
  public void engineAddContextToElement(Element paramElement)
  {
    if (paramElement == null) {
      throw new IllegalArgumentException("null element");
    }
    if (HMACOutputLengthSet)
    {
      Document localDocument = paramElement.getOwnerDocument();
      Element localElement = XMLUtils.createElementInSignatureSpace(localDocument, "HMACOutputLength");
      Text localText = localDocument.createTextNode(Integer.valueOf(HMACOutputLength).toString());
      localElement.appendChild(localText);
      XMLUtils.addReturnToElement(paramElement);
      paramElement.appendChild(localElement);
      XMLUtils.addReturnToElement(paramElement);
    }
  }
  
  public static class IntegrityHmacMD5
    extends IntegrityHmac
  {
    public IntegrityHmacMD5()
      throws XMLSignatureException
    {}
    
    public String engineGetURI()
    {
      return "http://www.w3.org/2001/04/xmldsig-more#hmac-md5";
    }
    
    int getDigestLength()
    {
      return 128;
    }
  }
  
  public static class IntegrityHmacRIPEMD160
    extends IntegrityHmac
  {
    public IntegrityHmacRIPEMD160()
      throws XMLSignatureException
    {}
    
    public String engineGetURI()
    {
      return "http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160";
    }
    
    int getDigestLength()
    {
      return 160;
    }
  }
  
  public static class IntegrityHmacSHA1
    extends IntegrityHmac
  {
    public IntegrityHmacSHA1()
      throws XMLSignatureException
    {}
    
    public String engineGetURI()
    {
      return "http://www.w3.org/2000/09/xmldsig#hmac-sha1";
    }
    
    int getDigestLength()
    {
      return 160;
    }
  }
  
  public static class IntegrityHmacSHA256
    extends IntegrityHmac
  {
    public IntegrityHmacSHA256()
      throws XMLSignatureException
    {}
    
    public String engineGetURI()
    {
      return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256";
    }
    
    int getDigestLength()
    {
      return 256;
    }
  }
  
  public static class IntegrityHmacSHA384
    extends IntegrityHmac
  {
    public IntegrityHmacSHA384()
      throws XMLSignatureException
    {}
    
    public String engineGetURI()
    {
      return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384";
    }
    
    int getDigestLength()
    {
      return 384;
    }
  }
  
  public static class IntegrityHmacSHA512
    extends IntegrityHmac
  {
    public IntegrityHmacSHA512()
      throws XMLSignatureException
    {}
    
    public String engineGetURI()
    {
      return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512";
    }
    
    int getDigestLength()
    {
      return 512;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\algorithms\implementations\IntegrityHmac.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */