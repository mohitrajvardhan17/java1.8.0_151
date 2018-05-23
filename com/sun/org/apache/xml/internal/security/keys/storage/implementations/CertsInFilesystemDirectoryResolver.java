package com.sun.org.apache.xml.internal.security.keys.storage.implementations;

import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SKI;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolverException;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolverSpi;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.x500.X500Principal;

public class CertsInFilesystemDirectoryResolver
  extends StorageResolverSpi
{
  private static Logger log = Logger.getLogger(CertsInFilesystemDirectoryResolver.class.getName());
  private String merlinsCertificatesDir = null;
  private List<X509Certificate> certs = new ArrayList();
  
  public CertsInFilesystemDirectoryResolver(String paramString)
    throws StorageResolverException
  {
    merlinsCertificatesDir = paramString;
    readCertsFromHarddrive();
  }
  
  private void readCertsFromHarddrive()
    throws StorageResolverException
  {
    File localFile1 = new File(merlinsCertificatesDir);
    ArrayList localArrayList = new ArrayList();
    String[] arrayOfString = localFile1.list();
    for (int i = 0; i < arrayOfString.length; i++)
    {
      String str1 = arrayOfString[i];
      if (str1.endsWith(".crt")) {
        localArrayList.add(arrayOfString[i]);
      }
    }
    CertificateFactory localCertificateFactory = null;
    try
    {
      localCertificateFactory = CertificateFactory.getInstance("X.509");
    }
    catch (CertificateException localCertificateException1)
    {
      throw new StorageResolverException("empty", localCertificateException1);
    }
    if (localCertificateFactory == null) {
      throw new StorageResolverException("empty");
    }
    for (int j = 0; j < localArrayList.size(); j++)
    {
      String str2 = localFile1.getAbsolutePath() + File.separator + (String)localArrayList.get(j);
      File localFile2 = new File(str2);
      int k = 0;
      String str3 = null;
      FileInputStream localFileInputStream = null;
      try
      {
        localFileInputStream = new FileInputStream(localFile2);
        X509Certificate localX509Certificate = (X509Certificate)localCertificateFactory.generateCertificate(localFileInputStream);
        localX509Certificate.checkValidity();
        certs.add(localX509Certificate);
        str3 = localX509Certificate.getSubjectX500Principal().getName();
        k = 1;
        try
        {
          if (localFileInputStream != null) {
            localFileInputStream.close();
          }
        }
        catch (IOException localIOException1)
        {
          if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Could not add certificate from file " + str2, localIOException1);
          }
        }
        if (k == 0) {
          continue;
        }
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        if (log.isLoggable(Level.FINE)) {
          log.log(Level.FINE, "Could not add certificate from file " + str2, localFileNotFoundException);
        }
      }
      catch (CertificateNotYetValidException localCertificateNotYetValidException)
      {
        if (log.isLoggable(Level.FINE)) {
          log.log(Level.FINE, "Could not add certificate from file " + str2, localCertificateNotYetValidException);
        }
      }
      catch (CertificateExpiredException localCertificateExpiredException)
      {
        if (log.isLoggable(Level.FINE)) {
          log.log(Level.FINE, "Could not add certificate from file " + str2, localCertificateExpiredException);
        }
      }
      catch (CertificateException localCertificateException2)
      {
        if (log.isLoggable(Level.FINE)) {
          log.log(Level.FINE, "Could not add certificate from file " + str2, localCertificateException2);
        }
      }
      finally
      {
        try
        {
          if (localFileInputStream != null) {
            localFileInputStream.close();
          }
        }
        catch (IOException localIOException6)
        {
          if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Could not add certificate from file " + str2, localIOException6);
          }
        }
      }
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "Added certificate: " + str3);
      }
    }
  }
  
  public Iterator<Certificate> getIterator()
  {
    return new FilesystemIterator(certs);
  }
  
  public static void main(String[] paramArrayOfString)
    throws Exception
  {
    CertsInFilesystemDirectoryResolver localCertsInFilesystemDirectoryResolver = new CertsInFilesystemDirectoryResolver("data/ie/baltimore/merlin-examples/merlin-xmldsig-eighteen/certs");
    Iterator localIterator = localCertsInFilesystemDirectoryResolver.getIterator();
    while (localIterator.hasNext())
    {
      X509Certificate localX509Certificate = (X509Certificate)localIterator.next();
      byte[] arrayOfByte = XMLX509SKI.getSKIBytesFromCert(localX509Certificate);
      System.out.println();
      System.out.println("Base64(SKI())=                 \"" + Base64.encode(arrayOfByte) + "\"");
      System.out.println("cert.getSerialNumber()=        \"" + localX509Certificate.getSerialNumber().toString() + "\"");
      System.out.println("cert.getSubjectX500Principal().getName()= \"" + localX509Certificate.getSubjectX500Principal().getName() + "\"");
      System.out.println("cert.getIssuerX500Principal().getName()=  \"" + localX509Certificate.getIssuerX500Principal().getName() + "\"");
    }
  }
  
  private static class FilesystemIterator
    implements Iterator<Certificate>
  {
    List<X509Certificate> certs = null;
    int i;
    
    public FilesystemIterator(List<X509Certificate> paramList)
    {
      certs = paramList;
      i = 0;
    }
    
    public boolean hasNext()
    {
      return i < certs.size();
    }
    
    public Certificate next()
    {
      return (Certificate)certs.get(i++);
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException("Can't remove keys from KeyStore");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\keys\storage\implementations\CertsInFilesystemDirectoryResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */