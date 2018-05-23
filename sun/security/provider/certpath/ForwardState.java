package sun.security.provider.certpath;

import java.io.IOException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;
import sun.security.x509.GeneralName;
import sun.security.x509.GeneralNameInterface;
import sun.security.x509.GeneralNames;
import sun.security.x509.SubjectAlternativeNameExtension;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;

class ForwardState
  implements State
{
  private static final Debug debug = Debug.getInstance("certpath");
  X500Principal issuerDN;
  X509CertImpl cert;
  HashSet<GeneralNameInterface> subjectNamesTraversed;
  int traversedCACerts;
  private boolean init = true;
  UntrustedChecker untrustedChecker;
  ArrayList<PKIXCertPathChecker> forwardCheckers;
  boolean keyParamsNeededFlag = false;
  
  ForwardState() {}
  
  public boolean isInitial()
  {
    return init;
  }
  
  public boolean keyParamsNeeded()
  {
    return keyParamsNeededFlag;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("State [");
    localStringBuilder.append("\n  issuerDN of last cert: ").append(issuerDN);
    localStringBuilder.append("\n  traversedCACerts: ").append(traversedCACerts);
    localStringBuilder.append("\n  init: ").append(String.valueOf(init));
    localStringBuilder.append("\n  keyParamsNeeded: ").append(String.valueOf(keyParamsNeededFlag));
    localStringBuilder.append("\n  subjectNamesTraversed: \n").append(subjectNamesTraversed);
    localStringBuilder.append("]\n");
    return localStringBuilder.toString();
  }
  
  public void initState(List<PKIXCertPathChecker> paramList)
    throws CertPathValidatorException
  {
    subjectNamesTraversed = new HashSet();
    traversedCACerts = 0;
    forwardCheckers = new ArrayList();
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      PKIXCertPathChecker localPKIXCertPathChecker = (PKIXCertPathChecker)localIterator.next();
      if (localPKIXCertPathChecker.isForwardCheckingSupported())
      {
        localPKIXCertPathChecker.init(true);
        forwardCheckers.add(localPKIXCertPathChecker);
      }
    }
    init = true;
  }
  
  public void updateState(X509Certificate paramX509Certificate)
    throws CertificateException, IOException, CertPathValidatorException
  {
    if (paramX509Certificate == null) {
      return;
    }
    X509CertImpl localX509CertImpl = X509CertImpl.toImpl(paramX509Certificate);
    if (PKIX.isDSAPublicKeyWithoutParams(localX509CertImpl.getPublicKey())) {
      keyParamsNeededFlag = true;
    }
    cert = localX509CertImpl;
    issuerDN = paramX509Certificate.getIssuerX500Principal();
    if ((!X509CertImpl.isSelfIssued(paramX509Certificate)) && (!init) && (paramX509Certificate.getBasicConstraints() != -1)) {
      traversedCACerts += 1;
    }
    if ((init) || (!X509CertImpl.isSelfIssued(paramX509Certificate)))
    {
      X500Principal localX500Principal = paramX509Certificate.getSubjectX500Principal();
      subjectNamesTraversed.add(X500Name.asX500Name(localX500Principal));
      try
      {
        SubjectAlternativeNameExtension localSubjectAlternativeNameExtension = localX509CertImpl.getSubjectAlternativeNameExtension();
        if (localSubjectAlternativeNameExtension != null)
        {
          GeneralNames localGeneralNames = localSubjectAlternativeNameExtension.get("subject_name");
          Iterator localIterator = localGeneralNames.names().iterator();
          while (localIterator.hasNext())
          {
            GeneralName localGeneralName = (GeneralName)localIterator.next();
            subjectNamesTraversed.add(localGeneralName.getName());
          }
        }
      }
      catch (IOException localIOException)
      {
        if (debug != null)
        {
          debug.println("ForwardState.updateState() unexpected exception");
          localIOException.printStackTrace();
        }
        throw new CertPathValidatorException(localIOException);
      }
    }
    init = false;
  }
  
  public Object clone()
  {
    try
    {
      ForwardState localForwardState = (ForwardState)super.clone();
      forwardCheckers = ((ArrayList)forwardCheckers.clone());
      ListIterator localListIterator = forwardCheckers.listIterator();
      while (localListIterator.hasNext())
      {
        PKIXCertPathChecker localPKIXCertPathChecker = (PKIXCertPathChecker)localListIterator.next();
        if ((localPKIXCertPathChecker instanceof Cloneable)) {
          localListIterator.set((PKIXCertPathChecker)localPKIXCertPathChecker.clone());
        }
      }
      subjectNamesTraversed = ((HashSet)subjectNamesTraversed.clone());
      return localForwardState;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException.toString(), localCloneNotSupportedException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\certpath\ForwardState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */