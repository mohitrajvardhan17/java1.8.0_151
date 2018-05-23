package sun.security.provider.certpath;

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import sun.security.util.Debug;
import sun.security.x509.AuthorityKeyIdentifierExtension;
import sun.security.x509.KeyIdentifier;
import sun.security.x509.SubjectKeyIdentifierExtension;
import sun.security.x509.X509CertImpl;

public class Vertex
{
  private static final Debug debug = Debug.getInstance("certpath");
  private X509Certificate cert;
  private int index;
  private Throwable throwable;
  
  Vertex(X509Certificate paramX509Certificate)
  {
    cert = paramX509Certificate;
    index = -1;
  }
  
  public X509Certificate getCertificate()
  {
    return cert;
  }
  
  public int getIndex()
  {
    return index;
  }
  
  void setIndex(int paramInt)
  {
    index = paramInt;
  }
  
  public Throwable getThrowable()
  {
    return throwable;
  }
  
  void setThrowable(Throwable paramThrowable)
  {
    throwable = paramThrowable;
  }
  
  public String toString()
  {
    return certToString() + throwableToString() + indexToString();
  }
  
  public String certToString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    X509CertImpl localX509CertImpl = null;
    try
    {
      localX509CertImpl = X509CertImpl.toImpl(cert);
    }
    catch (CertificateException localCertificateException)
    {
      if (debug != null)
      {
        debug.println("Vertex.certToString() unexpected exception");
        localCertificateException.printStackTrace();
      }
      return localStringBuilder.toString();
    }
    localStringBuilder.append("Issuer:     ").append(localX509CertImpl.getIssuerX500Principal()).append("\n");
    localStringBuilder.append("Subject:    ").append(localX509CertImpl.getSubjectX500Principal()).append("\n");
    localStringBuilder.append("SerialNum:  ").append(localX509CertImpl.getSerialNumber().toString(16)).append("\n");
    localStringBuilder.append("Expires:    ").append(localX509CertImpl.getNotAfter().toString()).append("\n");
    boolean[] arrayOfBoolean1 = localX509CertImpl.getIssuerUniqueID();
    int k;
    if (arrayOfBoolean1 != null)
    {
      localStringBuilder.append("IssuerUID:  ");
      for (k : arrayOfBoolean1) {
        localStringBuilder.append(k != 0 ? 1 : 0);
      }
      localStringBuilder.append("\n");
    }
    ??? = localX509CertImpl.getSubjectUniqueID();
    if (??? != null)
    {
      localStringBuilder.append("SubjectUID: ");
      for (int m : ???) {
        localStringBuilder.append(m != 0 ? 1 : 0);
      }
      localStringBuilder.append("\n");
    }
    try
    {
      ??? = localX509CertImpl.getSubjectKeyIdentifierExtension();
      if (??? != null)
      {
        localObject2 = ((SubjectKeyIdentifierExtension)???).get("key_id");
        localStringBuilder.append("SubjKeyID:  ").append(((KeyIdentifier)localObject2).toString());
      }
      Object localObject2 = localX509CertImpl.getAuthorityKeyIdentifierExtension();
      if (localObject2 != null)
      {
        KeyIdentifier localKeyIdentifier = (KeyIdentifier)((AuthorityKeyIdentifierExtension)localObject2).get("key_id");
        localStringBuilder.append("AuthKeyID:  ").append(localKeyIdentifier.toString());
      }
    }
    catch (IOException localIOException)
    {
      if (debug != null)
      {
        debug.println("Vertex.certToString() unexpected exception");
        localIOException.printStackTrace();
      }
    }
    return localStringBuilder.toString();
  }
  
  public String throwableToString()
  {
    StringBuilder localStringBuilder = new StringBuilder("Exception:  ");
    if (throwable != null) {
      localStringBuilder.append(throwable.toString());
    } else {
      localStringBuilder.append("null");
    }
    localStringBuilder.append("\n");
    return localStringBuilder.toString();
  }
  
  public String moreToString()
  {
    StringBuilder localStringBuilder = new StringBuilder("Last cert?  ");
    localStringBuilder.append(index == -1 ? "Yes" : "No");
    localStringBuilder.append("\n");
    return localStringBuilder.toString();
  }
  
  public String indexToString()
  {
    return "Index:      " + index + "\n";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\certpath\Vertex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */