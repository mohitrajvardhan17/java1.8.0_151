package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignerOutputStream
  extends ByteArrayOutputStream
{
  private static Logger log = Logger.getLogger(SignerOutputStream.class.getName());
  final SignatureAlgorithm sa;
  
  public SignerOutputStream(SignatureAlgorithm paramSignatureAlgorithm)
  {
    sa = paramSignatureAlgorithm;
  }
  
  public void write(byte[] paramArrayOfByte)
  {
    try
    {
      sa.update(paramArrayOfByte);
    }
    catch (XMLSignatureException localXMLSignatureException)
    {
      throw new RuntimeException("" + localXMLSignatureException);
    }
  }
  
  public void write(int paramInt)
  {
    try
    {
      sa.update((byte)paramInt);
    }
    catch (XMLSignatureException localXMLSignatureException)
    {
      throw new RuntimeException("" + localXMLSignatureException);
    }
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (log.isLoggable(Level.FINE))
    {
      log.log(Level.FINE, "Canonicalized SignedInfo:");
      StringBuilder localStringBuilder = new StringBuilder(paramInt2);
      for (int i = paramInt1; i < paramInt1 + paramInt2; i++) {
        localStringBuilder.append((char)paramArrayOfByte[i]);
      }
      log.log(Level.FINE, localStringBuilder.toString());
    }
    try
    {
      sa.update(paramArrayOfByte, paramInt1, paramInt2);
    }
    catch (XMLSignatureException localXMLSignatureException)
    {
      throw new RuntimeException("" + localXMLSignatureException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\utils\SignerOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */