package sun.security.provider.certpath;

import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXReason;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import sun.security.util.Debug;

class PKIXMasterCertPathValidator
{
  private static final Debug debug = Debug.getInstance("certpath");
  
  PKIXMasterCertPathValidator() {}
  
  static void validate(CertPath paramCertPath, List<X509Certificate> paramList, List<PKIXCertPathChecker> paramList1)
    throws CertPathValidatorException
  {
    int i = paramList.size();
    if (debug != null)
    {
      debug.println("--------------------------------------------------------------");
      debug.println("Executing PKIX certification path validation algorithm.");
    }
    for (int j = 0; j < i; j++)
    {
      X509Certificate localX509Certificate = (X509Certificate)paramList.get(j);
      if (debug != null) {
        debug.println("Checking cert" + (j + 1) + " - Subject: " + localX509Certificate.getSubjectX500Principal());
      }
      Set localSet = localX509Certificate.getCriticalExtensionOIDs();
      if (localSet == null) {
        localSet = Collections.emptySet();
      }
      Object localObject;
      if ((debug != null) && (!localSet.isEmpty()))
      {
        StringJoiner localStringJoiner = new StringJoiner(", ", "{", "}");
        localObject = localSet.iterator();
        while (((Iterator)localObject).hasNext())
        {
          String str = (String)((Iterator)localObject).next();
          localStringJoiner.add(str);
        }
        debug.println("Set of critical extensions: " + localStringJoiner.toString());
      }
      for (int k = 0; k < paramList1.size(); k++)
      {
        localObject = (PKIXCertPathChecker)paramList1.get(k);
        if (debug != null) {
          debug.println("-Using checker" + (k + 1) + " ... [" + localObject.getClass().getName() + "]");
        }
        if (j == 0) {
          ((PKIXCertPathChecker)localObject).init(false);
        }
        try
        {
          ((PKIXCertPathChecker)localObject).check(localX509Certificate, localSet);
          if (debug != null) {
            debug.println("-checker" + (k + 1) + " validation succeeded");
          }
        }
        catch (CertPathValidatorException localCertPathValidatorException)
        {
          throw new CertPathValidatorException(localCertPathValidatorException.getMessage(), localCertPathValidatorException.getCause() != null ? localCertPathValidatorException.getCause() : localCertPathValidatorException, paramCertPath, i - (j + 1), localCertPathValidatorException.getReason());
        }
      }
      if (!localSet.isEmpty()) {
        throw new CertPathValidatorException("unrecognized critical extension(s)", null, paramCertPath, i - (j + 1), PKIXReason.UNRECOGNIZED_CRIT_EXT);
      }
      if (debug != null) {
        debug.println("\ncert" + (j + 1) + " validation succeeded.\n");
      }
    }
    if (debug != null)
    {
      debug.println("Cert path validation succeeded. (PKIX validation algorithm)");
      debug.println("--------------------------------------------------------------");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\certpath\PKIXMasterCertPathValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */