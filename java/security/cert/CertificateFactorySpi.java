package java.security.cert;

import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class CertificateFactorySpi
{
  public CertificateFactorySpi() {}
  
  public abstract Certificate engineGenerateCertificate(InputStream paramInputStream)
    throws CertificateException;
  
  public CertPath engineGenerateCertPath(InputStream paramInputStream)
    throws CertificateException
  {
    throw new UnsupportedOperationException();
  }
  
  public CertPath engineGenerateCertPath(InputStream paramInputStream, String paramString)
    throws CertificateException
  {
    throw new UnsupportedOperationException();
  }
  
  public CertPath engineGenerateCertPath(List<? extends Certificate> paramList)
    throws CertificateException
  {
    throw new UnsupportedOperationException();
  }
  
  public Iterator<String> engineGetCertPathEncodings()
  {
    throw new UnsupportedOperationException();
  }
  
  public abstract Collection<? extends Certificate> engineGenerateCertificates(InputStream paramInputStream)
    throws CertificateException;
  
  public abstract CRL engineGenerateCRL(InputStream paramInputStream)
    throws CRLException;
  
  public abstract Collection<? extends CRL> engineGenerateCRLs(InputStream paramInputStream)
    throws CRLException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\CertificateFactorySpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */