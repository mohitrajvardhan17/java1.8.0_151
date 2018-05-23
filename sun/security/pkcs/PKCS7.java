package sun.security.pkcs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import sun.security.timestamp.HttpTimestamper;
import sun.security.timestamp.TSRequest;
import sun.security.timestamp.TSResponse;
import sun.security.timestamp.TimestampToken;
import sun.security.timestamp.Timestamper;
import sun.security.util.Debug;
import sun.security.util.DerEncoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;
import sun.security.x509.X500Name;
import sun.security.x509.X509CRLImpl;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

public class PKCS7
{
  private ObjectIdentifier contentType;
  private BigInteger version = null;
  private AlgorithmId[] digestAlgorithmIds = null;
  private ContentInfo contentInfo = null;
  private X509Certificate[] certificates = null;
  private X509CRL[] crls = null;
  private SignerInfo[] signerInfos = null;
  private boolean oldStyle = false;
  private Principal[] certIssuerNames;
  private static final String KP_TIMESTAMPING_OID = "1.3.6.1.5.5.7.3.8";
  private static final String EXTENDED_KEY_USAGE_OID = "2.5.29.37";
  
  public PKCS7(InputStream paramInputStream)
    throws ParsingException, IOException
  {
    DataInputStream localDataInputStream = new DataInputStream(paramInputStream);
    byte[] arrayOfByte = new byte[localDataInputStream.available()];
    localDataInputStream.readFully(arrayOfByte);
    parse(new DerInputStream(arrayOfByte));
  }
  
  public PKCS7(DerInputStream paramDerInputStream)
    throws ParsingException
  {
    parse(paramDerInputStream);
  }
  
  public PKCS7(byte[] paramArrayOfByte)
    throws ParsingException
  {
    try
    {
      DerInputStream localDerInputStream = new DerInputStream(paramArrayOfByte);
      parse(localDerInputStream);
    }
    catch (IOException localIOException)
    {
      ParsingException localParsingException = new ParsingException("Unable to parse the encoded bytes");
      localParsingException.initCause(localIOException);
      throw localParsingException;
    }
  }
  
  private void parse(DerInputStream paramDerInputStream)
    throws ParsingException
  {
    try
    {
      paramDerInputStream.mark(paramDerInputStream.available());
      parse(paramDerInputStream, false);
    }
    catch (IOException localIOException1)
    {
      try
      {
        paramDerInputStream.reset();
        parse(paramDerInputStream, true);
        oldStyle = true;
      }
      catch (IOException localIOException2)
      {
        ParsingException localParsingException = new ParsingException(localIOException2.getMessage());
        localParsingException.initCause(localIOException1);
        localParsingException.addSuppressed(localIOException2);
        throw localParsingException;
      }
    }
  }
  
  private void parse(DerInputStream paramDerInputStream, boolean paramBoolean)
    throws IOException
  {
    contentInfo = new ContentInfo(paramDerInputStream, paramBoolean);
    contentType = contentInfo.contentType;
    DerValue localDerValue = contentInfo.getContent();
    if (contentType.equals(ContentInfo.SIGNED_DATA_OID)) {
      parseSignedData(localDerValue);
    } else if (contentType.equals(ContentInfo.OLD_SIGNED_DATA_OID)) {
      parseOldSignedData(localDerValue);
    } else if (contentType.equals(ContentInfo.NETSCAPE_CERT_SEQUENCE_OID)) {
      parseNetscapeCertChain(localDerValue);
    } else {
      throw new ParsingException("content type " + contentType + " not supported.");
    }
  }
  
  public PKCS7(AlgorithmId[] paramArrayOfAlgorithmId, ContentInfo paramContentInfo, X509Certificate[] paramArrayOfX509Certificate, X509CRL[] paramArrayOfX509CRL, SignerInfo[] paramArrayOfSignerInfo)
  {
    version = BigInteger.ONE;
    digestAlgorithmIds = paramArrayOfAlgorithmId;
    contentInfo = paramContentInfo;
    certificates = paramArrayOfX509Certificate;
    crls = paramArrayOfX509CRL;
    signerInfos = paramArrayOfSignerInfo;
  }
  
  public PKCS7(AlgorithmId[] paramArrayOfAlgorithmId, ContentInfo paramContentInfo, X509Certificate[] paramArrayOfX509Certificate, SignerInfo[] paramArrayOfSignerInfo)
  {
    this(paramArrayOfAlgorithmId, paramContentInfo, paramArrayOfX509Certificate, null, paramArrayOfSignerInfo);
  }
  
  private void parseNetscapeCertChain(DerValue paramDerValue)
    throws ParsingException, IOException
  {
    DerInputStream localDerInputStream = new DerInputStream(paramDerValue.toByteArray());
    DerValue[] arrayOfDerValue = localDerInputStream.getSequence(2);
    certificates = new X509Certificate[arrayOfDerValue.length];
    CertificateFactory localCertificateFactory = null;
    try
    {
      localCertificateFactory = CertificateFactory.getInstance("X.509");
    }
    catch (CertificateException localCertificateException1) {}
    for (int i = 0; i < arrayOfDerValue.length; i++)
    {
      ByteArrayInputStream localByteArrayInputStream = null;
      try
      {
        if (localCertificateFactory == null)
        {
          certificates[i] = new X509CertImpl(arrayOfDerValue[i]);
        }
        else
        {
          byte[] arrayOfByte = arrayOfDerValue[i].toByteArray();
          localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
          certificates[i] = ((X509Certificate)localCertificateFactory.generateCertificate(localByteArrayInputStream));
          localByteArrayInputStream.close();
          localByteArrayInputStream = null;
        }
      }
      catch (CertificateException localCertificateException2)
      {
        localParsingException = new ParsingException(localCertificateException2.getMessage());
        localParsingException.initCause(localCertificateException2);
        throw localParsingException;
      }
      catch (IOException localIOException)
      {
        ParsingException localParsingException = new ParsingException(localIOException.getMessage());
        localParsingException.initCause(localIOException);
        throw localParsingException;
      }
      finally
      {
        if (localByteArrayInputStream != null) {
          localByteArrayInputStream.close();
        }
      }
    }
  }
  
  private void parseSignedData(DerValue paramDerValue)
    throws ParsingException, IOException
  {
    DerInputStream localDerInputStream = paramDerValue.toDerInputStream();
    version = localDerInputStream.getBigInteger();
    DerValue[] arrayOfDerValue1 = localDerInputStream.getSet(1);
    int i = arrayOfDerValue1.length;
    digestAlgorithmIds = new AlgorithmId[i];
    try
    {
      for (int j = 0; j < i; j++)
      {
        localObject1 = arrayOfDerValue1[j];
        digestAlgorithmIds[j] = AlgorithmId.parse((DerValue)localObject1);
      }
    }
    catch (IOException localIOException1)
    {
      Object localObject1 = new ParsingException("Error parsing digest AlgorithmId IDs: " + localIOException1.getMessage());
      ((ParsingException)localObject1).initCause(localIOException1);
      throw ((Throwable)localObject1);
    }
    contentInfo = new ContentInfo(localDerInputStream);
    CertificateFactory localCertificateFactory = null;
    try
    {
      localCertificateFactory = CertificateFactory.getInstance("X.509");
    }
    catch (CertificateException localCertificateException1) {}
    Object localObject3;
    if ((byte)localDerInputStream.peekByte() == -96)
    {
      arrayOfDerValue2 = localDerInputStream.getSet(2, true);
      i = arrayOfDerValue2.length;
      certificates = new X509Certificate[i];
      k = 0;
      for (int m = 0; m < i; m++)
      {
        localObject3 = null;
        try
        {
          int n = arrayOfDerValue2[m].getTag();
          if (n == 48)
          {
            if (localCertificateFactory == null)
            {
              certificates[k] = new X509CertImpl(arrayOfDerValue2[m]);
            }
            else
            {
              localObject4 = arrayOfDerValue2[m].toByteArray();
              localObject3 = new ByteArrayInputStream((byte[])localObject4);
              certificates[k] = ((X509Certificate)localCertificateFactory.generateCertificate((InputStream)localObject3));
              ((ByteArrayInputStream)localObject3).close();
              localObject3 = null;
            }
            k++;
          }
        }
        catch (CertificateException localCertificateException2)
        {
          localObject4 = new ParsingException(localCertificateException2.getMessage());
          ((ParsingException)localObject4).initCause(localCertificateException2);
          throw ((Throwable)localObject4);
        }
        catch (IOException localIOException2)
        {
          Object localObject4 = new ParsingException(localIOException2.getMessage());
          ((ParsingException)localObject4).initCause(localIOException2);
          throw ((Throwable)localObject4);
        }
        finally
        {
          if (localObject3 != null) {
            ((ByteArrayInputStream)localObject3).close();
          }
        }
      }
      if (k != i) {
        certificates = ((X509Certificate[])Arrays.copyOf(certificates, k));
      }
    }
    Object localObject2;
    if ((byte)localDerInputStream.peekByte() == -95)
    {
      arrayOfDerValue2 = localDerInputStream.getSet(1, true);
      i = arrayOfDerValue2.length;
      crls = new X509CRL[i];
      for (k = 0; k < i; k++)
      {
        localObject2 = null;
        try
        {
          if (localCertificateFactory == null)
          {
            crls[k] = new X509CRLImpl(arrayOfDerValue2[k]);
          }
          else
          {
            localObject3 = arrayOfDerValue2[k].toByteArray();
            localObject2 = new ByteArrayInputStream((byte[])localObject3);
            crls[k] = ((X509CRL)localCertificateFactory.generateCRL((InputStream)localObject2));
            ((ByteArrayInputStream)localObject2).close();
            localObject2 = null;
          }
        }
        catch (CRLException localCRLException)
        {
          ParsingException localParsingException = new ParsingException(localCRLException.getMessage());
          localParsingException.initCause(localCRLException);
          throw localParsingException;
        }
        finally
        {
          if (localObject2 != null) {
            ((ByteArrayInputStream)localObject2).close();
          }
        }
      }
    }
    DerValue[] arrayOfDerValue2 = localDerInputStream.getSet(1);
    i = arrayOfDerValue2.length;
    signerInfos = new SignerInfo[i];
    for (int k = 0; k < i; k++)
    {
      localObject2 = arrayOfDerValue2[k].toDerInputStream();
      signerInfos[k] = new SignerInfo((DerInputStream)localObject2);
    }
  }
  
  private void parseOldSignedData(DerValue paramDerValue)
    throws ParsingException, IOException
  {
    DerInputStream localDerInputStream1 = paramDerValue.toDerInputStream();
    version = localDerInputStream1.getBigInteger();
    DerValue[] arrayOfDerValue1 = localDerInputStream1.getSet(1);
    int i = arrayOfDerValue1.length;
    digestAlgorithmIds = new AlgorithmId[i];
    try
    {
      for (int j = 0; j < i; j++)
      {
        DerValue localDerValue = arrayOfDerValue1[j];
        digestAlgorithmIds[j] = AlgorithmId.parse(localDerValue);
      }
    }
    catch (IOException localIOException1)
    {
      throw new ParsingException("Error parsing digest AlgorithmId IDs");
    }
    contentInfo = new ContentInfo(localDerInputStream1, true);
    CertificateFactory localCertificateFactory = null;
    try
    {
      localCertificateFactory = CertificateFactory.getInstance("X.509");
    }
    catch (CertificateException localCertificateException1) {}
    DerValue[] arrayOfDerValue2 = localDerInputStream1.getSet(2);
    i = arrayOfDerValue2.length;
    certificates = new X509Certificate[i];
    for (int k = 0; k < i; k++)
    {
      ByteArrayInputStream localByteArrayInputStream = null;
      try
      {
        if (localCertificateFactory == null)
        {
          certificates[k] = new X509CertImpl(arrayOfDerValue2[k]);
        }
        else
        {
          byte[] arrayOfByte = arrayOfDerValue2[k].toByteArray();
          localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
          certificates[k] = ((X509Certificate)localCertificateFactory.generateCertificate(localByteArrayInputStream));
          localByteArrayInputStream.close();
          localByteArrayInputStream = null;
        }
      }
      catch (CertificateException localCertificateException2)
      {
        localParsingException = new ParsingException(localCertificateException2.getMessage());
        localParsingException.initCause(localCertificateException2);
        throw localParsingException;
      }
      catch (IOException localIOException2)
      {
        ParsingException localParsingException = new ParsingException(localIOException2.getMessage());
        localParsingException.initCause(localIOException2);
        throw localParsingException;
      }
      finally
      {
        if (localByteArrayInputStream != null) {
          localByteArrayInputStream.close();
        }
      }
    }
    localDerInputStream1.getSet(0);
    DerValue[] arrayOfDerValue3 = localDerInputStream1.getSet(1);
    i = arrayOfDerValue3.length;
    signerInfos = new SignerInfo[i];
    for (int m = 0; m < i; m++)
    {
      DerInputStream localDerInputStream2 = arrayOfDerValue3[m].toDerInputStream();
      signerInfos[m] = new SignerInfo(localDerInputStream2, true);
    }
  }
  
  public void encodeSignedData(OutputStream paramOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    encodeSignedData(localDerOutputStream);
    paramOutputStream.write(localDerOutputStream.toByteArray());
  }
  
  public void encodeSignedData(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    localDerOutputStream.putInteger(version);
    localDerOutputStream.putOrderedSetOf((byte)49, digestAlgorithmIds);
    contentInfo.encode(localDerOutputStream);
    if ((certificates != null) && (certificates.length != 0))
    {
      localObject1 = new X509CertImpl[certificates.length];
      for (int i = 0; i < certificates.length; i++) {
        if ((certificates[i] instanceof X509CertImpl)) {
          localObject1[i] = ((X509CertImpl)certificates[i]);
        } else {
          try
          {
            byte[] arrayOfByte1 = certificates[i].getEncoded();
            localObject1[i] = new X509CertImpl(arrayOfByte1);
          }
          catch (CertificateException localCertificateException)
          {
            throw new IOException(localCertificateException);
          }
        }
      }
      localDerOutputStream.putOrderedSetOf((byte)-96, (DerEncoder[])localObject1);
    }
    if ((crls != null) && (crls.length != 0))
    {
      localObject1 = new HashSet(crls.length);
      for (Object localObject3 : crls) {
        if ((localObject3 instanceof X509CRLImpl)) {
          ((Set)localObject1).add((X509CRLImpl)localObject3);
        } else {
          try
          {
            byte[] arrayOfByte2 = ((X509CRL)localObject3).getEncoded();
            ((Set)localObject1).add(new X509CRLImpl(arrayOfByte2));
          }
          catch (CRLException localCRLException)
          {
            throw new IOException(localCRLException);
          }
        }
      }
      localDerOutputStream.putOrderedSetOf((byte)-95, (DerEncoder[])((Set)localObject1).toArray(new X509CRLImpl[((Set)localObject1).size()]));
    }
    localDerOutputStream.putOrderedSetOf((byte)49, signerInfos);
    Object localObject1 = new DerValue((byte)48, localDerOutputStream.toByteArray());
    ??? = new ContentInfo(ContentInfo.SIGNED_DATA_OID, (DerValue)localObject1);
    ((ContentInfo)???).encode(paramDerOutputStream);
  }
  
  public SignerInfo verify(SignerInfo paramSignerInfo, byte[] paramArrayOfByte)
    throws NoSuchAlgorithmException, SignatureException
  {
    return paramSignerInfo.verify(this, paramArrayOfByte);
  }
  
  public SignerInfo[] verify(byte[] paramArrayOfByte)
    throws NoSuchAlgorithmException, SignatureException
  {
    Vector localVector = new Vector();
    for (int i = 0; i < signerInfos.length; i++)
    {
      SignerInfo localSignerInfo = verify(signerInfos[i], paramArrayOfByte);
      if (localSignerInfo != null) {
        localVector.addElement(localSignerInfo);
      }
    }
    if (!localVector.isEmpty())
    {
      SignerInfo[] arrayOfSignerInfo = new SignerInfo[localVector.size()];
      localVector.copyInto(arrayOfSignerInfo);
      return arrayOfSignerInfo;
    }
    return null;
  }
  
  public SignerInfo[] verify()
    throws NoSuchAlgorithmException, SignatureException
  {
    return verify(null);
  }
  
  public BigInteger getVersion()
  {
    return version;
  }
  
  public AlgorithmId[] getDigestAlgorithmIds()
  {
    return digestAlgorithmIds;
  }
  
  public ContentInfo getContentInfo()
  {
    return contentInfo;
  }
  
  public X509Certificate[] getCertificates()
  {
    if (certificates != null) {
      return (X509Certificate[])certificates.clone();
    }
    return null;
  }
  
  public X509CRL[] getCRLs()
  {
    if (crls != null) {
      return (X509CRL[])crls.clone();
    }
    return null;
  }
  
  public SignerInfo[] getSignerInfos()
  {
    return signerInfos;
  }
  
  public X509Certificate getCertificate(BigInteger paramBigInteger, X500Name paramX500Name)
  {
    if (certificates != null)
    {
      if (certIssuerNames == null) {
        populateCertIssuerNames();
      }
      for (int i = 0; i < certificates.length; i++)
      {
        X509Certificate localX509Certificate = certificates[i];
        BigInteger localBigInteger = localX509Certificate.getSerialNumber();
        if ((paramBigInteger.equals(localBigInteger)) && (paramX500Name.equals(certIssuerNames[i]))) {
          return localX509Certificate;
        }
      }
    }
    return null;
  }
  
  private void populateCertIssuerNames()
  {
    if (certificates == null) {
      return;
    }
    certIssuerNames = new Principal[certificates.length];
    for (int i = 0; i < certificates.length; i++)
    {
      X509Certificate localX509Certificate = certificates[i];
      Principal localPrincipal = localX509Certificate.getIssuerDN();
      if (!(localPrincipal instanceof X500Name)) {
        try
        {
          X509CertInfo localX509CertInfo = new X509CertInfo(localX509Certificate.getTBSCertificate());
          localPrincipal = (Principal)localX509CertInfo.get("issuer.dname");
        }
        catch (Exception localException) {}
      }
      certIssuerNames[i] = localPrincipal;
    }
  }
  
  public String toString()
  {
    String str = "";
    str = str + contentInfo + "\n";
    if (version != null) {
      str = str + "PKCS7 :: version: " + Debug.toHexString(version) + "\n";
    }
    int i;
    if (digestAlgorithmIds != null)
    {
      str = str + "PKCS7 :: digest AlgorithmIds: \n";
      for (i = 0; i < digestAlgorithmIds.length; i++) {
        str = str + "\t" + digestAlgorithmIds[i] + "\n";
      }
    }
    if (certificates != null)
    {
      str = str + "PKCS7 :: certificates: \n";
      for (i = 0; i < certificates.length; i++) {
        str = str + "\t" + i + ".   " + certificates[i] + "\n";
      }
    }
    if (crls != null)
    {
      str = str + "PKCS7 :: crls: \n";
      for (i = 0; i < crls.length; i++) {
        str = str + "\t" + i + ".   " + crls[i] + "\n";
      }
    }
    if (signerInfos != null)
    {
      str = str + "PKCS7 :: signer infos: \n";
      for (i = 0; i < signerInfos.length; i++) {
        str = str + "\t" + i + ".  " + signerInfos[i] + "\n";
      }
    }
    return str;
  }
  
  public boolean isOldStyle()
  {
    return oldStyle;
  }
  
  public static byte[] generateSignedData(byte[] paramArrayOfByte1, X509Certificate[] paramArrayOfX509Certificate, byte[] paramArrayOfByte2, String paramString1, URI paramURI, String paramString2, String paramString3)
    throws CertificateException, IOException, NoSuchAlgorithmException
  {
    PKCS9Attributes localPKCS9Attributes = null;
    if (paramURI != null)
    {
      localObject1 = new HttpTimestamper(paramURI);
      localObject2 = generateTimestampToken((Timestamper)localObject1, paramString2, paramString3, paramArrayOfByte1);
      localPKCS9Attributes = new PKCS9Attributes(new PKCS9Attribute[] { new PKCS9Attribute("SignatureTimestampToken", localObject2) });
    }
    Object localObject1 = X500Name.asX500Name(paramArrayOfX509Certificate[0].getIssuerX500Principal());
    Object localObject2 = paramArrayOfX509Certificate[0].getSerialNumber();
    String str1 = AlgorithmId.getEncAlgFromSigAlg(paramString1);
    String str2 = AlgorithmId.getDigAlgFromSigAlg(paramString1);
    SignerInfo localSignerInfo = new SignerInfo((X500Name)localObject1, (BigInteger)localObject2, AlgorithmId.get(str2), null, AlgorithmId.get(str1), paramArrayOfByte1, localPKCS9Attributes);
    SignerInfo[] arrayOfSignerInfo = { localSignerInfo };
    AlgorithmId[] arrayOfAlgorithmId = { localSignerInfo.getDigestAlgorithmId() };
    ContentInfo localContentInfo = paramArrayOfByte2 == null ? new ContentInfo(ContentInfo.DATA_OID, null) : new ContentInfo(paramArrayOfByte2);
    PKCS7 localPKCS7 = new PKCS7(arrayOfAlgorithmId, localContentInfo, paramArrayOfX509Certificate, arrayOfSignerInfo);
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    localPKCS7.encodeSignedData(localByteArrayOutputStream);
    return localByteArrayOutputStream.toByteArray();
  }
  
  private static byte[] generateTimestampToken(Timestamper paramTimestamper, String paramString1, String paramString2, byte[] paramArrayOfByte)
    throws IOException, CertificateException
  {
    MessageDigest localMessageDigest = null;
    TSRequest localTSRequest = null;
    try
    {
      localMessageDigest = MessageDigest.getInstance(paramString2);
      localTSRequest = new TSRequest(paramString1, paramArrayOfByte, localMessageDigest);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException1)
    {
      throw new IllegalArgumentException(localNoSuchAlgorithmException1);
    }
    BigInteger localBigInteger1 = null;
    if (SecureRandomHolder.RANDOM != null)
    {
      localBigInteger1 = new BigInteger(64, SecureRandomHolder.RANDOM);
      localTSRequest.setNonce(localBigInteger1);
    }
    localTSRequest.requestCertificate(true);
    TSResponse localTSResponse = paramTimestamper.generateTimestamp(localTSRequest);
    int i = localTSResponse.getStatusCode();
    if ((i != 0) && (i != 1)) {
      throw new IOException("Error generating timestamp: " + localTSResponse.getStatusCodeAsText() + " " + localTSResponse.getFailureCodeAsText());
    }
    if ((paramString1 != null) && (!paramString1.equals(localTSResponse.getTimestampToken().getPolicyID()))) {
      throw new IOException("TSAPolicyID changed in timestamp token");
    }
    PKCS7 localPKCS7 = localTSResponse.getToken();
    TimestampToken localTimestampToken = localTSResponse.getTimestampToken();
    try
    {
      if (!localTimestampToken.getHashAlgorithm().equals(AlgorithmId.get(paramString2))) {
        throw new IOException("Digest algorithm not " + paramString2 + " in timestamp token");
      }
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException2)
    {
      throw new IllegalArgumentException();
    }
    if (!MessageDigest.isEqual(localTimestampToken.getHashedMessage(), localTSRequest.getHashedMessage())) {
      throw new IOException("Digest octets changed in timestamp token");
    }
    BigInteger localBigInteger2 = localTimestampToken.getNonce();
    if ((localBigInteger2 == null) && (localBigInteger1 != null)) {
      throw new IOException("Nonce missing in timestamp token");
    }
    if ((localBigInteger2 != null) && (!localBigInteger2.equals(localBigInteger1))) {
      throw new IOException("Nonce changed in timestamp token");
    }
    for (SignerInfo localSignerInfo : localPKCS7.getSignerInfos())
    {
      X509Certificate localX509Certificate = localSignerInfo.getCertificate(localPKCS7);
      if (localX509Certificate == null) {
        throw new CertificateException("Certificate not included in timestamp token");
      }
      if (!localX509Certificate.getCriticalExtensionOIDs().contains("2.5.29.37")) {
        throw new CertificateException("Certificate is not valid for timestamping");
      }
      List localList = localX509Certificate.getExtendedKeyUsage();
      if ((localList == null) || (!localList.contains("1.3.6.1.5.5.7.3.8"))) {
        throw new CertificateException("Certificate is not valid for timestamping");
      }
    }
    return localTSResponse.getEncodedToken();
  }
  
  private static class SecureRandomHolder
  {
    static final SecureRandom RANDOM;
    
    private SecureRandomHolder() {}
    
    static
    {
      SecureRandom localSecureRandom = null;
      try
      {
        localSecureRandom = SecureRandom.getInstance("SHA1PRNG");
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {}
      RANDOM = localSecureRandom;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\pkcs\PKCS7.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */