package sun.security.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import sun.security.x509.X509CertImpl;

public final class UntrustedCertificates
{
  private static final Debug debug = Debug.getInstance("certpath");
  private static final String ALGORITHM_KEY = "Algorithm";
  private static final Properties props = new Properties();
  private static final String algorithm = props.getProperty("Algorithm");
  
  private static String stripColons(Object paramObject)
  {
    String str = (String)paramObject;
    char[] arrayOfChar = str.toCharArray();
    int i = 0;
    for (int j = 0; j < arrayOfChar.length; j++) {
      if (arrayOfChar[j] != ':')
      {
        if (j != i) {
          arrayOfChar[i] = arrayOfChar[j];
        }
        i++;
      }
    }
    if (i == arrayOfChar.length) {
      return str;
    }
    return new String(arrayOfChar, 0, i);
  }
  
  public static boolean isUntrusted(X509Certificate paramX509Certificate)
  {
    if (algorithm == null) {
      return false;
    }
    String str;
    if ((paramX509Certificate instanceof X509CertImpl)) {
      str = ((X509CertImpl)paramX509Certificate).getFingerprint(algorithm);
    } else {
      try
      {
        str = new X509CertImpl(paramX509Certificate.getEncoded()).getFingerprint(algorithm);
      }
      catch (CertificateException localCertificateException)
      {
        return false;
      }
    }
    return props.containsKey(str);
  }
  
  private UntrustedCertificates() {}
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        File localFile = new File(System.getProperty("java.home"), "lib/security/blacklisted.certs");
        try
        {
          FileInputStream localFileInputStream = new FileInputStream(localFile);
          Object localObject1 = null;
          try
          {
            UntrustedCertificates.props.load(localFileInputStream);
            Iterator localIterator = UntrustedCertificates.props.entrySet().iterator();
            while (localIterator.hasNext())
            {
              Map.Entry localEntry = (Map.Entry)localIterator.next();
              localEntry.setValue(UntrustedCertificates.stripColons(localEntry.getValue()));
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
        catch (IOException localIOException)
        {
          if (UntrustedCertificates.debug != null) {
            UntrustedCertificates.debug.println("Error parsing blacklisted.certs");
          }
        }
        return null;
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\UntrustedCertificates.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */