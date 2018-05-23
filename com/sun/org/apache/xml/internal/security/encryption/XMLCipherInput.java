package com.sun.org.apache.xml.internal.security.encryption;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Attr;

public class XMLCipherInput
{
  private static Logger logger = Logger.getLogger(XMLCipherInput.class.getName());
  private CipherData cipherData;
  private int mode;
  private boolean secureValidation;
  
  public XMLCipherInput(CipherData paramCipherData)
    throws XMLEncryptionException
  {
    cipherData = paramCipherData;
    mode = 2;
    if (cipherData == null) {
      throw new XMLEncryptionException("CipherData is null");
    }
  }
  
  public XMLCipherInput(EncryptedType paramEncryptedType)
    throws XMLEncryptionException
  {
    cipherData = (paramEncryptedType == null ? null : paramEncryptedType.getCipherData());
    mode = 2;
    if (cipherData == null) {
      throw new XMLEncryptionException("CipherData is null");
    }
  }
  
  public void setSecureValidation(boolean paramBoolean)
  {
    secureValidation = paramBoolean;
  }
  
  public byte[] getBytes()
    throws XMLEncryptionException
  {
    if (mode == 2) {
      return getDecryptBytes();
    }
    return null;
  }
  
  private byte[] getDecryptBytes()
    throws XMLEncryptionException
  {
    String str = null;
    if (cipherData.getDataType() == 2)
    {
      if (logger.isLoggable(Level.FINE)) {
        logger.log(Level.FINE, "Found a reference type CipherData");
      }
      CipherReference localCipherReference = cipherData.getCipherReference();
      Attr localAttr = localCipherReference.getURIAsAttr();
      XMLSignatureInput localXMLSignatureInput = null;
      try
      {
        ResourceResolver localResourceResolver = ResourceResolver.getInstance(localAttr, null, secureValidation);
        localXMLSignatureInput = localResourceResolver.resolve(localAttr, null, secureValidation);
      }
      catch (ResourceResolverException localResourceResolverException)
      {
        throw new XMLEncryptionException("empty", localResourceResolverException);
      }
      if (localXMLSignatureInput != null)
      {
        if (logger.isLoggable(Level.FINE)) {
          logger.log(Level.FINE, "Managed to resolve URI \"" + localCipherReference.getURI() + "\"");
        }
      }
      else if (logger.isLoggable(Level.FINE)) {
        logger.log(Level.FINE, "Failed to resolve URI \"" + localCipherReference.getURI() + "\"");
      }
      Transforms localTransforms = localCipherReference.getTransforms();
      if (localTransforms != null)
      {
        if (logger.isLoggable(Level.FINE)) {
          logger.log(Level.FINE, "Have transforms in cipher reference");
        }
        try
        {
          com.sun.org.apache.xml.internal.security.transforms.Transforms localTransforms1 = localTransforms.getDSTransforms();
          localTransforms1.setSecureValidation(secureValidation);
          localXMLSignatureInput = localTransforms1.performTransforms(localXMLSignatureInput);
        }
        catch (TransformationException localTransformationException)
        {
          throw new XMLEncryptionException("empty", localTransformationException);
        }
      }
      try
      {
        return localXMLSignatureInput.getBytes();
      }
      catch (IOException localIOException)
      {
        throw new XMLEncryptionException("empty", localIOException);
      }
      catch (CanonicalizationException localCanonicalizationException)
      {
        throw new XMLEncryptionException("empty", localCanonicalizationException);
      }
    }
    if (cipherData.getDataType() == 1) {
      str = cipherData.getCipherValue().getValue();
    } else {
      throw new XMLEncryptionException("CipherData.getDataType() returned unexpected value");
    }
    if (logger.isLoggable(Level.FINE)) {
      logger.log(Level.FINE, "Encrypted octets:\n" + str);
    }
    try
    {
      return Base64.decode(str);
    }
    catch (Base64DecodingException localBase64DecodingException)
    {
      throw new XMLEncryptionException("empty", localBase64DecodingException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\encryption\XMLCipherInput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */