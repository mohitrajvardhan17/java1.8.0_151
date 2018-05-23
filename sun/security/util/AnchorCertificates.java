package sun.security.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.AccessController;
import java.security.KeyStore;
import java.security.PrivilegedAction;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import sun.security.x509.X509CertImpl;

public class AnchorCertificates
{
  private static final Debug debug = Debug.getInstance("certpath");
  private static final String HASH = "SHA-256";
  private static Set<String> certs = Collections.emptySet();
  
  public static boolean contains(X509Certificate paramX509Certificate)
  {
    String str = X509CertImpl.getFingerprint("SHA-256", paramX509Certificate);
    boolean bool = certs.contains(str);
    if ((bool) && (debug != null)) {
      debug.println("AnchorCertificate.contains: matched " + paramX509Certificate.getSubjectDN());
    }
    return bool;
  }
  
  private AnchorCertificates() {}
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        File localFile = new File(System.getProperty("java.home"), "lib/security/cacerts");
        try
        {
          KeyStore localKeyStore = KeyStore.getInstance("JKS");
          FileInputStream localFileInputStream = new FileInputStream(localFile);
          Object localObject1 = null;
          try
          {
            localKeyStore.load(localFileInputStream, null);
            AnchorCertificates.access$002(new HashSet());
            Enumeration localEnumeration = localKeyStore.aliases();
            while (localEnumeration.hasMoreElements())
            {
              String str = (String)localEnumeration.nextElement();
              if (str.contains(" [jdk"))
              {
                X509Certificate localX509Certificate = (X509Certificate)localKeyStore.getCertificate(str);
                AnchorCertificates.certs.add(X509CertImpl.getFingerprint("SHA-256", localX509Certificate));
              }
            }
          }
          catch (Throwable localThrowable2)
          {
            localObject1 = localThrowable2;
            throw localThrowable2;
          }
          finally
          {
            if (localFileInputStream != null) {
              if (localObject1 != null) {
                try
                {
                  localFileInputStream.close();
                }
                catch (Throwable localThrowable3)
                {
                  ((Throwable)localObject1).addSuppressed(localThrowable3);
                }
              } else {
                localFileInputStream.close();
              }
            }
          }
        }
        catch (Exception localException)
        {
          if (AnchorCertificates.debug != null) {
            AnchorCertificates.debug.println("Error parsing cacerts");
          }
          localException.printStackTrace();
        }
        return null;
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\AnchorCertificates.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */