package sun.security.provider.certpath;

import java.io.IOException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXReason;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import sun.security.util.Debug;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.NameConstraintsExtension;
import sun.security.x509.PKIXExtensions;
import sun.security.x509.X509CertImpl;

class ConstraintsChecker
  extends PKIXCertPathChecker
{
  private static final Debug debug = Debug.getInstance("certpath");
  private final int certPathLength;
  private int maxPathLength;
  private int i;
  private NameConstraintsExtension prevNC;
  private Set<String> supportedExts;
  
  ConstraintsChecker(int paramInt)
  {
    certPathLength = paramInt;
  }
  
  public void init(boolean paramBoolean)
    throws CertPathValidatorException
  {
    if (!paramBoolean)
    {
      i = 0;
      maxPathLength = certPathLength;
      prevNC = null;
    }
    else
    {
      throw new CertPathValidatorException("forward checking not supported");
    }
  }
  
  public boolean isForwardCheckingSupported()
  {
    return false;
  }
  
  public Set<String> getSupportedExtensions()
  {
    if (supportedExts == null)
    {
      supportedExts = new HashSet(2);
      supportedExts.add(PKIXExtensions.BasicConstraints_Id.toString());
      supportedExts.add(PKIXExtensions.NameConstraints_Id.toString());
      supportedExts = Collections.unmodifiableSet(supportedExts);
    }
    return supportedExts;
  }
  
  public void check(Certificate paramCertificate, Collection<String> paramCollection)
    throws CertPathValidatorException
  {
    X509Certificate localX509Certificate = (X509Certificate)paramCertificate;
    i += 1;
    checkBasicConstraints(localX509Certificate);
    verifyNameConstraints(localX509Certificate);
    if ((paramCollection != null) && (!paramCollection.isEmpty()))
    {
      paramCollection.remove(PKIXExtensions.BasicConstraints_Id.toString());
      paramCollection.remove(PKIXExtensions.NameConstraints_Id.toString());
    }
  }
  
  private void verifyNameConstraints(X509Certificate paramX509Certificate)
    throws CertPathValidatorException
  {
    String str = "name constraints";
    if (debug != null) {
      debug.println("---checking " + str + "...");
    }
    if ((prevNC != null) && ((i == certPathLength) || (!X509CertImpl.isSelfIssued(paramX509Certificate))))
    {
      if (debug != null) {
        debug.println("prevNC = " + prevNC + ", currDN = " + paramX509Certificate.getSubjectX500Principal());
      }
      try
      {
        if (!prevNC.verify(paramX509Certificate)) {
          throw new CertPathValidatorException(str + " check failed", null, null, -1, PKIXReason.INVALID_NAME);
        }
      }
      catch (IOException localIOException)
      {
        throw new CertPathValidatorException(localIOException);
      }
    }
    prevNC = mergeNameConstraints(paramX509Certificate, prevNC);
    if (debug != null) {
      debug.println(str + " verified.");
    }
  }
  
  static NameConstraintsExtension mergeNameConstraints(X509Certificate paramX509Certificate, NameConstraintsExtension paramNameConstraintsExtension)
    throws CertPathValidatorException
  {
    X509CertImpl localX509CertImpl;
    try
    {
      localX509CertImpl = X509CertImpl.toImpl(paramX509Certificate);
    }
    catch (CertificateException localCertificateException)
    {
      throw new CertPathValidatorException(localCertificateException);
    }
    NameConstraintsExtension localNameConstraintsExtension = localX509CertImpl.getNameConstraintsExtension();
    if (debug != null) {
      debug.println("prevNC = " + paramNameConstraintsExtension + ", newNC = " + String.valueOf(localNameConstraintsExtension));
    }
    if (paramNameConstraintsExtension == null)
    {
      if (debug != null) {
        debug.println("mergedNC = " + String.valueOf(localNameConstraintsExtension));
      }
      if (localNameConstraintsExtension == null) {
        return localNameConstraintsExtension;
      }
      return (NameConstraintsExtension)localNameConstraintsExtension.clone();
    }
    try
    {
      paramNameConstraintsExtension.merge(localNameConstraintsExtension);
    }
    catch (IOException localIOException)
    {
      throw new CertPathValidatorException(localIOException);
    }
    if (debug != null) {
      debug.println("mergedNC = " + paramNameConstraintsExtension);
    }
    return paramNameConstraintsExtension;
  }
  
  private void checkBasicConstraints(X509Certificate paramX509Certificate)
    throws CertPathValidatorException
  {
    String str = "basic constraints";
    if (debug != null)
    {
      debug.println("---checking " + str + "...");
      debug.println("i = " + i + ", maxPathLength = " + maxPathLength);
    }
    if (i < certPathLength)
    {
      int j = -1;
      if (paramX509Certificate.getVersion() < 3)
      {
        if ((i == 1) && (X509CertImpl.isSelfIssued(paramX509Certificate))) {
          j = Integer.MAX_VALUE;
        }
      }
      else {
        j = paramX509Certificate.getBasicConstraints();
      }
      if (j == -1) {
        throw new CertPathValidatorException(str + " check failed: this is not a CA certificate", null, null, -1, PKIXReason.NOT_CA_CERT);
      }
      if (!X509CertImpl.isSelfIssued(paramX509Certificate))
      {
        if (maxPathLength <= 0) {
          throw new CertPathValidatorException(str + " check failed: pathLenConstraint violated - this cert must be the last cert in the certification path", null, null, -1, PKIXReason.PATH_TOO_LONG);
        }
        maxPathLength -= 1;
      }
      if (j < maxPathLength) {
        maxPathLength = j;
      }
    }
    if (debug != null)
    {
      debug.println("after processing, maxPathLength = " + maxPathLength);
      debug.println(str + " verified.");
    }
  }
  
  static int mergeBasicConstraints(X509Certificate paramX509Certificate, int paramInt)
  {
    int j = paramX509Certificate.getBasicConstraints();
    if (!X509CertImpl.isSelfIssued(paramX509Certificate)) {
      paramInt--;
    }
    if (j < paramInt) {
      paramInt = j;
    }
    return paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\certpath\ConstraintsChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */