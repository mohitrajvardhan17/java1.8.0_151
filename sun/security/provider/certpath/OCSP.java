package sun.security.provider.certpath;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.cert.CRLReason;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorException.BasicReason;
import java.security.cert.CertificateException;
import java.security.cert.Extension;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import sun.security.action.GetIntegerAction;
import sun.security.util.Debug;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AccessDescription;
import sun.security.x509.AuthorityInfoAccessExtension;
import sun.security.x509.GeneralName;
import sun.security.x509.PKIXExtensions;
import sun.security.x509.URIName;
import sun.security.x509.X509CertImpl;

public final class OCSP
{
  private static final Debug debug = Debug.getInstance("certpath");
  private static final int DEFAULT_CONNECT_TIMEOUT = 15000;
  private static final int CONNECT_TIMEOUT = initializeTimeout();
  
  private static int initializeTimeout()
  {
    Integer localInteger = (Integer)AccessController.doPrivileged(new GetIntegerAction("com.sun.security.ocsp.timeout"));
    if ((localInteger == null) || (localInteger.intValue() < 0)) {
      return 15000;
    }
    return localInteger.intValue() * 1000;
  }
  
  private OCSP() {}
  
  public static RevocationStatus check(X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2, URI paramURI, X509Certificate paramX509Certificate3, Date paramDate)
    throws IOException, CertPathValidatorException
  {
    return check(paramX509Certificate1, paramX509Certificate2, paramURI, paramX509Certificate3, paramDate, Collections.emptyList(), "generic");
  }
  
  public static RevocationStatus check(X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2, URI paramURI, X509Certificate paramX509Certificate3, Date paramDate, List<Extension> paramList, String paramString)
    throws IOException, CertPathValidatorException
  {
    return check(paramX509Certificate1, paramURI, null, paramX509Certificate2, paramX509Certificate3, paramDate, paramList, paramString);
  }
  
  public static RevocationStatus check(X509Certificate paramX509Certificate1, URI paramURI, TrustAnchor paramTrustAnchor, X509Certificate paramX509Certificate2, X509Certificate paramX509Certificate3, Date paramDate, List<Extension> paramList, String paramString)
    throws IOException, CertPathValidatorException
  {
    CertId localCertId;
    try
    {
      X509CertImpl localX509CertImpl = X509CertImpl.toImpl(paramX509Certificate1);
      localCertId = new CertId(paramX509Certificate2, localX509CertImpl.getSerialNumberObject());
    }
    catch (CertificateException|IOException localCertificateException)
    {
      throw new CertPathValidatorException("Exception while encoding OCSPRequest", localCertificateException);
    }
    OCSPResponse localOCSPResponse = check(Collections.singletonList(localCertId), paramURI, new OCSPResponse.IssuerInfo(paramTrustAnchor, paramX509Certificate2), paramX509Certificate3, paramDate, paramList, paramString);
    return localOCSPResponse.getSingleResponse(localCertId);
  }
  
  static OCSPResponse check(List<CertId> paramList, URI paramURI, OCSPResponse.IssuerInfo paramIssuerInfo, X509Certificate paramX509Certificate, Date paramDate, List<Extension> paramList1, String paramString)
    throws IOException, CertPathValidatorException
  {
    byte[] arrayOfByte = null;
    Object localObject1 = paramList1.iterator();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Extension)((Iterator)localObject1).next();
      if (((Extension)localObject2).getId().equals(PKIXExtensions.OCSPNonce_Id.toString())) {
        arrayOfByte = ((Extension)localObject2).getValue();
      }
    }
    localObject1 = null;
    try
    {
      localObject2 = getOCSPBytes(paramList, paramURI, paramList1);
      localObject1 = new OCSPResponse((byte[])localObject2);
      ((OCSPResponse)localObject1).verify(paramList, paramIssuerInfo, paramX509Certificate, paramDate, arrayOfByte, paramString);
    }
    catch (IOException localIOException)
    {
      throw new CertPathValidatorException("Unable to determine revocation status due to network error", localIOException, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
    }
    return (OCSPResponse)localObject1;
  }
  
  public static byte[] getOCSPBytes(List<CertId> paramList, URI paramURI, List<Extension> paramList1)
    throws IOException
  {
    OCSPRequest localOCSPRequest = new OCSPRequest(paramList, paramList1);
    byte[] arrayOfByte1 = localOCSPRequest.encodeBytes();
    InputStream localInputStream = null;
    OutputStream localOutputStream = null;
    arrayOfByte2 = null;
    try
    {
      URL localURL = paramURI.toURL();
      if (debug != null) {
        debug.println("connecting to OCSP service at: " + localURL);
      }
      HttpURLConnection localHttpURLConnection = (HttpURLConnection)localURL.openConnection();
      localHttpURLConnection.setConnectTimeout(CONNECT_TIMEOUT);
      localHttpURLConnection.setReadTimeout(CONNECT_TIMEOUT);
      localHttpURLConnection.setDoOutput(true);
      localHttpURLConnection.setDoInput(true);
      localHttpURLConnection.setRequestMethod("POST");
      localHttpURLConnection.setRequestProperty("Content-type", "application/ocsp-request");
      localHttpURLConnection.setRequestProperty("Content-length", String.valueOf(arrayOfByte1.length));
      localOutputStream = localHttpURLConnection.getOutputStream();
      localOutputStream.write(arrayOfByte1);
      localOutputStream.flush();
      if ((debug != null) && (localHttpURLConnection.getResponseCode() != 200)) {
        debug.println("Received HTTP error: " + localHttpURLConnection.getResponseCode() + " - " + localHttpURLConnection.getResponseMessage());
      }
      localInputStream = localHttpURLConnection.getInputStream();
      int i = localHttpURLConnection.getContentLength();
      if (i == -1) {
        i = Integer.MAX_VALUE;
      }
      arrayOfByte2 = new byte[i > 2048 ? 2048 : i];
      int j = 0;
      while (j < i)
      {
        int k = localInputStream.read(arrayOfByte2, j, arrayOfByte2.length - j);
        if (k < 0) {
          break;
        }
        j += k;
        if ((j >= arrayOfByte2.length) && (j < i)) {
          arrayOfByte2 = Arrays.copyOf(arrayOfByte2, j * 2);
        }
      }
      arrayOfByte2 = Arrays.copyOf(arrayOfByte2, j);
      return arrayOfByte2;
    }
    finally
    {
      if (localInputStream != null) {
        try
        {
          localInputStream.close();
        }
        catch (IOException localIOException3)
        {
          throw localIOException3;
        }
      }
      if (localOutputStream != null) {
        try
        {
          localOutputStream.close();
        }
        catch (IOException localIOException4)
        {
          throw localIOException4;
        }
      }
    }
  }
  
  public static URI getResponderURI(X509Certificate paramX509Certificate)
  {
    try
    {
      return getResponderURI(X509CertImpl.toImpl(paramX509Certificate));
    }
    catch (CertificateException localCertificateException) {}
    return null;
  }
  
  static URI getResponderURI(X509CertImpl paramX509CertImpl)
  {
    AuthorityInfoAccessExtension localAuthorityInfoAccessExtension = paramX509CertImpl.getAuthorityInfoAccessExtension();
    if (localAuthorityInfoAccessExtension == null) {
      return null;
    }
    List localList = localAuthorityInfoAccessExtension.getAccessDescriptions();
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      AccessDescription localAccessDescription = (AccessDescription)localIterator.next();
      if (localAccessDescription.getAccessMethod().equals(AccessDescription.Ad_OCSP_Id))
      {
        GeneralName localGeneralName = localAccessDescription.getAccessLocation();
        if (localGeneralName.getType() == 6)
        {
          URIName localURIName = (URIName)localGeneralName.getName();
          return localURIName.getURI();
        }
      }
    }
    return null;
  }
  
  public static abstract interface RevocationStatus
  {
    public abstract CertStatus getCertStatus();
    
    public abstract Date getRevocationTime();
    
    public abstract CRLReason getRevocationReason();
    
    public abstract Map<String, Extension> getSingleExtensions();
    
    public static enum CertStatus
    {
      GOOD,  REVOKED,  UNKNOWN;
      
      private CertStatus() {}
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\certpath\OCSP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */