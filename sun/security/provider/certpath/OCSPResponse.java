package sun.security.provider.certpath;

import java.io.IOException;
import java.security.AccessController;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CRLReason;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorException.BasicReason;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.misc.HexDumpEncoder;
import sun.security.action.GetIntegerAction;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;
import sun.security.x509.KeyIdentifier;
import sun.security.x509.PKIXExtensions;
import sun.security.x509.X509CertImpl;

public final class OCSPResponse
{
  private static final ResponseStatus[] rsvalues = ;
  private static final Debug debug = Debug.getInstance("certpath");
  private static final boolean dump = (debug != null) && (Debug.isOn("ocsp"));
  private static final ObjectIdentifier OCSP_BASIC_RESPONSE_OID = ObjectIdentifier.newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 48, 1, 1 });
  private static final int CERT_STATUS_GOOD = 0;
  private static final int CERT_STATUS_REVOKED = 1;
  private static final int CERT_STATUS_UNKNOWN = 2;
  private static final int NAME_TAG = 1;
  private static final int KEY_TAG = 2;
  private static final String KP_OCSP_SIGNING_OID = "1.3.6.1.5.5.7.3.9";
  private static final int DEFAULT_MAX_CLOCK_SKEW = 900000;
  private static final int MAX_CLOCK_SKEW = initializeClockSkew();
  private static final CRLReason[] values = CRLReason.values();
  private final ResponseStatus responseStatus;
  private final Map<CertId, SingleResponse> singleResponseMap;
  private final AlgorithmId sigAlgId;
  private final byte[] signature;
  private final byte[] tbsResponseData;
  private final byte[] responseNonce;
  private List<X509CertImpl> certs;
  private X509CertImpl signerCert = null;
  private final ResponderId respId;
  private Date producedAtDate = null;
  private final Map<String, java.security.cert.Extension> responseExtensions;
  
  private static int initializeClockSkew()
  {
    Integer localInteger = (Integer)AccessController.doPrivileged(new GetIntegerAction("com.sun.security.ocsp.clockSkew"));
    if ((localInteger == null) || (localInteger.intValue() < 0)) {
      return 900000;
    }
    return localInteger.intValue() * 1000;
  }
  
  public OCSPResponse(byte[] paramArrayOfByte)
    throws IOException
  {
    if (dump)
    {
      localObject1 = new HexDumpEncoder();
      debug.println("OCSPResponse bytes...\n\n" + ((HexDumpEncoder)localObject1).encode(paramArrayOfByte) + "\n");
    }
    Object localObject1 = new DerValue(paramArrayOfByte);
    if (tag != 48) {
      throw new IOException("Bad encoding in OCSP response: expected ASN.1 SEQUENCE tag.");
    }
    DerInputStream localDerInputStream1 = ((DerValue)localObject1).getData();
    int i = localDerInputStream1.getEnumerated();
    if ((i >= 0) && (i < rsvalues.length)) {
      responseStatus = rsvalues[i];
    } else {
      throw new IOException("Unknown OCSPResponse status: " + i);
    }
    if (debug != null) {
      debug.println("OCSP response status: " + responseStatus);
    }
    if (responseStatus != ResponseStatus.SUCCESSFUL)
    {
      singleResponseMap = Collections.emptyMap();
      certs = new ArrayList();
      sigAlgId = null;
      signature = null;
      tbsResponseData = null;
      responseNonce = null;
      responseExtensions = Collections.emptyMap();
      respId = null;
      return;
    }
    localObject1 = localDerInputStream1.getDerValue();
    if (!((DerValue)localObject1).isContextSpecific((byte)0)) {
      throw new IOException("Bad encoding in responseBytes element of OCSP response: expected ASN.1 context specific tag 0.");
    }
    DerValue localDerValue1 = data.getDerValue();
    if (tag != 48) {
      throw new IOException("Bad encoding in responseBytes element of OCSP response: expected ASN.1 SEQUENCE tag.");
    }
    localDerInputStream1 = data;
    ObjectIdentifier localObjectIdentifier = localDerInputStream1.getOID();
    if (localObjectIdentifier.equals(OCSP_BASIC_RESPONSE_OID))
    {
      if (debug != null) {
        debug.println("OCSP response type: basic");
      }
    }
    else
    {
      if (debug != null) {
        debug.println("OCSP response type: " + localObjectIdentifier);
      }
      throw new IOException("Unsupported OCSP response type: " + localObjectIdentifier);
    }
    DerInputStream localDerInputStream2 = new DerInputStream(localDerInputStream1.getOctetString());
    DerValue[] arrayOfDerValue1 = localDerInputStream2.getSequence(2);
    if (arrayOfDerValue1.length < 3) {
      throw new IOException("Unexpected BasicOCSPResponse value");
    }
    DerValue localDerValue2 = arrayOfDerValue1[0];
    tbsResponseData = arrayOfDerValue1[0].toByteArray();
    if (tag != 48) {
      throw new IOException("Bad encoding in tbsResponseData element of OCSP response: expected ASN.1 SEQUENCE tag.");
    }
    DerInputStream localDerInputStream3 = data;
    DerValue localDerValue3 = localDerInputStream3.getDerValue();
    if ((localDerValue3.isContextSpecific((byte)0)) && (localDerValue3.isConstructed()) && (localDerValue3.isContextSpecific()))
    {
      localDerValue3 = data.getDerValue();
      int j = localDerValue3.getInteger();
      if (data.available() != 0) {
        throw new IOException("Bad encoding in version  element of OCSP response: bad format");
      }
      localDerValue3 = localDerInputStream3.getDerValue();
    }
    respId = new ResponderId(localDerValue3.toByteArray());
    if (debug != null) {
      debug.println("Responder ID: " + respId);
    }
    localDerValue3 = localDerInputStream3.getDerValue();
    producedAtDate = localDerValue3.getGeneralizedTime();
    if (debug != null) {
      debug.println("OCSP response produced at: " + producedAtDate);
    }
    DerValue[] arrayOfDerValue2 = localDerInputStream3.getSequence(1);
    singleResponseMap = new HashMap(arrayOfDerValue2.length);
    if (debug != null) {
      debug.println("OCSP number of SingleResponses: " + arrayOfDerValue2.length);
    }
    Object localObject3;
    for (localObject3 : arrayOfDerValue2)
    {
      SingleResponse localSingleResponse = new SingleResponse((DerValue)localObject3, null);
      singleResponseMap.put(localSingleResponse.getCertId(), localSingleResponse);
    }
    ??? = new HashMap();
    if (localDerInputStream3.available() > 0)
    {
      localDerValue3 = localDerInputStream3.getDerValue();
      if (localDerValue3.isContextSpecific((byte)1)) {
        ??? = parseExtensions(localDerValue3);
      }
    }
    responseExtensions = ((Map)???);
    sun.security.x509.Extension localExtension = (sun.security.x509.Extension)((Map)???).get(PKIXExtensions.OCSPNonce_Id.toString());
    responseNonce = (localExtension != null ? localExtension.getExtensionValue() : null);
    if ((debug != null) && (responseNonce != null)) {
      debug.println("Response nonce: " + Arrays.toString(responseNonce));
    }
    sigAlgId = AlgorithmId.parse(arrayOfDerValue1[1]);
    signature = arrayOfDerValue1[2].getBitString();
    if (arrayOfDerValue1.length > 3)
    {
      DerValue localDerValue4 = arrayOfDerValue1[3];
      if (!localDerValue4.isContextSpecific((byte)0)) {
        throw new IOException("Bad encoding in certs element of OCSP response: expected ASN.1 context specific tag 0.");
      }
      localObject3 = localDerValue4.getData().getSequence(3);
      certs = new ArrayList(localObject3.length);
      try
      {
        for (int n = 0; n < localObject3.length; n++)
        {
          X509CertImpl localX509CertImpl = new X509CertImpl(localObject3[n].toByteArray());
          certs.add(localX509CertImpl);
          if (debug != null) {
            debug.println("OCSP response cert #" + (n + 1) + ": " + localX509CertImpl.getSubjectX500Principal());
          }
        }
      }
      catch (CertificateException localCertificateException)
      {
        throw new IOException("Bad encoding in X509 Certificate", localCertificateException);
      }
    }
    else
    {
      certs = new ArrayList();
    }
  }
  
  void verify(List<CertId> paramList, IssuerInfo paramIssuerInfo, X509Certificate paramX509Certificate, Date paramDate, byte[] paramArrayOfByte, String paramString)
    throws CertPathValidatorException
  {
    switch (responseStatus)
    {
    case SUCCESSFUL: 
      break;
    case TRY_LATER: 
    case INTERNAL_ERROR: 
      throw new CertPathValidatorException("OCSP response error: " + responseStatus, null, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
    case UNAUTHORIZED: 
    default: 
      throw new CertPathValidatorException("OCSP response error: " + responseStatus);
    }
    Iterator localIterator1 = paramList.iterator();
    Object localObject2;
    Object localObject3;
    while (localIterator1.hasNext())
    {
      localObject2 = (CertId)localIterator1.next();
      localObject3 = getSingleResponse((CertId)localObject2);
      if (localObject3 == null)
      {
        if (debug != null) {
          debug.println("No response found for CertId: " + localObject2);
        }
        throw new CertPathValidatorException("OCSP response does not include a response for a certificate supplied in the OCSP request");
      }
      if (debug != null) {
        debug.println("Status of certificate (with serial number " + ((CertId)localObject2).getSerialNumber() + ") is: " + ((SingleResponse)localObject3).getCertStatus());
      }
    }
    Object localObject1;
    if (signerCert == null)
    {
      try
      {
        if (paramIssuerInfo.getCertificate() != null) {
          certs.add(X509CertImpl.toImpl(paramIssuerInfo.getCertificate()));
        }
        if (paramX509Certificate != null) {
          certs.add(X509CertImpl.toImpl(paramX509Certificate));
        }
      }
      catch (CertificateException localCertificateException1)
      {
        throw new CertPathValidatorException("Invalid issuer or trusted responder certificate", localCertificateException1);
      }
      if (respId.getType() == ResponderId.Type.BY_NAME)
      {
        localObject1 = respId.getResponderName();
        localObject2 = certs.iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localObject3 = (X509CertImpl)((Iterator)localObject2).next();
          if (((X509CertImpl)localObject3).getSubjectX500Principal().equals(localObject1))
          {
            signerCert = ((X509CertImpl)localObject3);
            break;
          }
        }
      }
      else if (respId.getType() == ResponderId.Type.BY_KEY)
      {
        localObject1 = respId.getKeyIdentifier();
        localObject2 = certs.iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localObject3 = (X509CertImpl)((Iterator)localObject2).next();
          localObject4 = ((X509CertImpl)localObject3).getSubjectKeyId();
          if ((localObject4 != null) && (((KeyIdentifier)localObject1).equals(localObject4)))
          {
            signerCert = ((X509CertImpl)localObject3);
            break;
          }
          try
          {
            localObject4 = new KeyIdentifier(((X509CertImpl)localObject3).getPublicKey());
          }
          catch (IOException localIOException) {}
          if (((KeyIdentifier)localObject1).equals(localObject4))
          {
            signerCert = ((X509CertImpl)localObject3);
            break;
          }
        }
      }
    }
    if (signerCert != null) {
      if ((signerCert.getSubjectX500Principal().equals(paramIssuerInfo.getName())) && (signerCert.getPublicKey().equals(paramIssuerInfo.getPublicKey())))
      {
        if (debug != null) {
          debug.println("OCSP response is signed by the target's Issuing CA");
        }
      }
      else if (signerCert.equals(paramX509Certificate))
      {
        if (debug != null) {
          debug.println("OCSP response is signed by a Trusted Responder");
        }
      }
      else if (signerCert.getIssuerX500Principal().equals(paramIssuerInfo.getName()))
      {
        try
        {
          localObject1 = signerCert.getExtendedKeyUsage();
          if ((localObject1 == null) || (!((List)localObject1).contains("1.3.6.1.5.5.7.3.9"))) {
            throw new CertPathValidatorException("Responder's certificate not valid for signing OCSP responses");
          }
        }
        catch (CertificateParsingException localCertificateParsingException)
        {
          throw new CertPathValidatorException("Responder's certificate not valid for signing OCSP responses", localCertificateParsingException);
        }
        AlgorithmChecker localAlgorithmChecker = new AlgorithmChecker(paramIssuerInfo.getAnchor(), paramDate, paramString);
        localAlgorithmChecker.init(false);
        localAlgorithmChecker.check(signerCert, Collections.emptySet());
        try
        {
          if (paramDate == null) {
            signerCert.checkValidity();
          } else {
            signerCert.checkValidity(paramDate);
          }
        }
        catch (CertificateException localCertificateException2)
        {
          throw new CertPathValidatorException("Responder's certificate not within the validity period", localCertificateException2);
        }
        sun.security.x509.Extension localExtension = signerCert.getExtension(PKIXExtensions.OCSPNoCheck_Id);
        if ((localExtension != null) && (debug != null)) {
          debug.println("Responder's certificate includes the extension id-pkix-ocsp-nocheck.");
        }
        try
        {
          signerCert.verify(paramIssuerInfo.getPublicKey());
          if (debug != null) {
            debug.println("OCSP response is signed by an Authorized Responder");
          }
        }
        catch (GeneralSecurityException localGeneralSecurityException)
        {
          signerCert = null;
        }
      }
      else
      {
        throw new CertPathValidatorException("Responder's certificate is not authorized to sign OCSP responses");
      }
    }
    if (signerCert != null)
    {
      AlgorithmChecker.check(signerCert.getPublicKey(), sigAlgId, paramString);
      if (!verifySignature(signerCert)) {
        throw new CertPathValidatorException("Error verifying OCSP Response's signature");
      }
    }
    else
    {
      throw new CertPathValidatorException("Unable to verify OCSP Response's signature");
    }
    if ((paramArrayOfByte != null) && (responseNonce != null) && (!Arrays.equals(paramArrayOfByte, responseNonce))) {
      throw new CertPathValidatorException("Nonces don't match");
    }
    long l = paramDate == null ? System.currentTimeMillis() : paramDate.getTime();
    Date localDate = new Date(l + MAX_CLOCK_SKEW);
    Object localObject4 = new Date(l - MAX_CLOCK_SKEW);
    Iterator localIterator2 = singleResponseMap.values().iterator();
    while (localIterator2.hasNext())
    {
      SingleResponse localSingleResponse = (SingleResponse)localIterator2.next();
      if (debug != null)
      {
        String str = "";
        if (nextUpdate != null) {
          str = " until " + nextUpdate;
        }
        debug.println("OCSP response validity interval is from " + thisUpdate + str);
        debug.println("Checking validity of OCSP response on: " + new Date(l));
      }
      if (!localDate.before(thisUpdate))
      {
        if (!((Date)localObject4).after(nextUpdate != null ? nextUpdate : thisUpdate)) {}
      }
      else {
        throw new CertPathValidatorException("Response is unreliable: its validity interval is out-of-date");
      }
    }
  }
  
  public ResponseStatus getResponseStatus()
  {
    return responseStatus;
  }
  
  private boolean verifySignature(X509Certificate paramX509Certificate)
    throws CertPathValidatorException
  {
    try
    {
      Signature localSignature = Signature.getInstance(sigAlgId.getName());
      localSignature.initVerify(paramX509Certificate.getPublicKey());
      localSignature.update(tbsResponseData);
      if (localSignature.verify(signature))
      {
        if (debug != null) {
          debug.println("Verified signature of OCSP Response");
        }
        return true;
      }
      if (debug != null) {
        debug.println("Error verifying signature of OCSP Response");
      }
      return false;
    }
    catch (InvalidKeyException|NoSuchAlgorithmException|SignatureException localInvalidKeyException)
    {
      throw new CertPathValidatorException(localInvalidKeyException);
    }
  }
  
  public SingleResponse getSingleResponse(CertId paramCertId)
  {
    return (SingleResponse)singleResponseMap.get(paramCertId);
  }
  
  public Set<CertId> getCertIds()
  {
    return Collections.unmodifiableSet(singleResponseMap.keySet());
  }
  
  X509Certificate getSignerCertificate()
  {
    return signerCert;
  }
  
  public ResponderId getResponderId()
  {
    return respId;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("OCSP Response:\n");
    localStringBuilder.append("Response Status: ").append(responseStatus).append("\n");
    localStringBuilder.append("Responder ID: ").append(respId).append("\n");
    localStringBuilder.append("Produced at: ").append(producedAtDate).append("\n");
    int i = singleResponseMap.size();
    localStringBuilder.append(i).append(i == 1 ? " response:\n" : " responses:\n");
    Iterator localIterator = singleResponseMap.values().iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (SingleResponse)localIterator.next();
      localStringBuilder.append(localObject).append("\n");
    }
    if ((responseExtensions != null) && (responseExtensions.size() > 0))
    {
      i = responseExtensions.size();
      localStringBuilder.append(i).append(i == 1 ? " extension:\n" : " extensions:\n");
      localIterator = responseExtensions.keySet().iterator();
      while (localIterator.hasNext())
      {
        localObject = (String)localIterator.next();
        localStringBuilder.append(responseExtensions.get(localObject)).append("\n");
      }
    }
    return localStringBuilder.toString();
  }
  
  private static Map<String, java.security.cert.Extension> parseExtensions(DerValue paramDerValue)
    throws IOException
  {
    DerValue[] arrayOfDerValue1 = data.getSequence(3);
    HashMap localHashMap = new HashMap(arrayOfDerValue1.length);
    for (DerValue localDerValue : arrayOfDerValue1)
    {
      sun.security.x509.Extension localExtension = new sun.security.x509.Extension(localDerValue);
      if (debug != null) {
        debug.println("Extension: " + localExtension);
      }
      if (localExtension.isCritical()) {
        throw new IOException("Unsupported OCSP critical extension: " + localExtension.getExtensionId());
      }
      localHashMap.put(localExtension.getId(), localExtension);
    }
    return localHashMap;
  }
  
  static final class IssuerInfo
  {
    private final TrustAnchor anchor;
    private final X509Certificate certificate;
    private final X500Principal name;
    private final PublicKey pubKey;
    
    IssuerInfo(TrustAnchor paramTrustAnchor)
    {
      this(paramTrustAnchor, paramTrustAnchor != null ? paramTrustAnchor.getTrustedCert() : null);
    }
    
    IssuerInfo(X509Certificate paramX509Certificate)
    {
      this(null, paramX509Certificate);
    }
    
    IssuerInfo(TrustAnchor paramTrustAnchor, X509Certificate paramX509Certificate)
    {
      if ((paramTrustAnchor == null) && (paramX509Certificate == null)) {
        throw new NullPointerException("TrustAnchor and issuerCert cannot be null");
      }
      anchor = paramTrustAnchor;
      if (paramX509Certificate != null)
      {
        name = paramX509Certificate.getSubjectX500Principal();
        pubKey = paramX509Certificate.getPublicKey();
        certificate = paramX509Certificate;
      }
      else
      {
        name = paramTrustAnchor.getCA();
        pubKey = paramTrustAnchor.getCAPublicKey();
        certificate = paramTrustAnchor.getTrustedCert();
      }
    }
    
    X509Certificate getCertificate()
    {
      return certificate;
    }
    
    X500Principal getName()
    {
      return name;
    }
    
    PublicKey getPublicKey()
    {
      return pubKey;
    }
    
    TrustAnchor getAnchor()
    {
      return anchor;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Issuer Info:\n");
      localStringBuilder.append("Name: ").append(name.toString()).append("\n");
      localStringBuilder.append("Public Key:\n").append(pubKey.toString()).append("\n");
      return localStringBuilder.toString();
    }
  }
  
  public static enum ResponseStatus
  {
    SUCCESSFUL,  MALFORMED_REQUEST,  INTERNAL_ERROR,  TRY_LATER,  UNUSED,  SIG_REQUIRED,  UNAUTHORIZED;
    
    private ResponseStatus() {}
  }
  
  public static final class SingleResponse
    implements OCSP.RevocationStatus
  {
    private final CertId certId;
    private final OCSP.RevocationStatus.CertStatus certStatus;
    private final Date thisUpdate;
    private final Date nextUpdate;
    private final Date revocationTime;
    private final CRLReason revocationReason;
    private final Map<String, java.security.cert.Extension> singleExtensions;
    
    private SingleResponse(DerValue paramDerValue)
      throws IOException
    {
      if (tag != 48) {
        throw new IOException("Bad ASN.1 encoding in SingleResponse");
      }
      DerInputStream localDerInputStream = data;
      certId = new CertId(getDerValuedata);
      DerValue localDerValue = localDerInputStream.getDerValue();
      int i = (short)(byte)(tag & 0x1F);
      if (i == 1)
      {
        certStatus = OCSP.RevocationStatus.CertStatus.REVOKED;
        revocationTime = data.getGeneralizedTime();
        if (data.available() != 0)
        {
          localObject = data.getDerValue();
          i = (short)(byte)(tag & 0x1F);
          if (i == 0)
          {
            int j = data.getEnumerated();
            if ((j >= 0) && (j < OCSPResponse.values.length)) {
              revocationReason = OCSPResponse.values[j];
            } else {
              revocationReason = CRLReason.UNSPECIFIED;
            }
          }
          else
          {
            revocationReason = CRLReason.UNSPECIFIED;
          }
        }
        else
        {
          revocationReason = CRLReason.UNSPECIFIED;
        }
        if (OCSPResponse.debug != null)
        {
          OCSPResponse.debug.println("Revocation time: " + revocationTime);
          OCSPResponse.debug.println("Revocation reason: " + revocationReason);
        }
      }
      else
      {
        revocationTime = null;
        revocationReason = null;
        if (i == 0) {
          certStatus = OCSP.RevocationStatus.CertStatus.GOOD;
        } else if (i == 2) {
          certStatus = OCSP.RevocationStatus.CertStatus.UNKNOWN;
        } else {
          throw new IOException("Invalid certificate status");
        }
      }
      thisUpdate = localDerInputStream.getGeneralizedTime();
      if (OCSPResponse.debug != null) {
        OCSPResponse.debug.println("thisUpdate: " + thisUpdate);
      }
      Object localObject = null;
      Map localMap = null;
      if (localDerInputStream.available() > 0)
      {
        localDerValue = localDerInputStream.getDerValue();
        if (localDerValue.isContextSpecific((byte)0))
        {
          localObject = data.getGeneralizedTime();
          if (OCSPResponse.debug != null) {
            OCSPResponse.debug.println("nextUpdate: " + localObject);
          }
          localDerValue = localDerInputStream.available() > 0 ? localDerInputStream.getDerValue() : null;
        }
        if (localDerValue != null) {
          if (localDerValue.isContextSpecific((byte)1))
          {
            localMap = OCSPResponse.parseExtensions(localDerValue);
            if (localDerInputStream.available() > 0) {
              throw new IOException(localDerInputStream.available() + " bytes of additional data in singleResponse");
            }
          }
          else
          {
            throw new IOException("Unsupported singleResponse item, tag = " + String.format("%02X", new Object[] { Byte.valueOf(tag) }));
          }
        }
      }
      nextUpdate = ((Date)localObject);
      singleExtensions = (localMap != null ? localMap : Collections.emptyMap());
      if (OCSPResponse.debug != null)
      {
        Iterator localIterator = singleExtensions.values().iterator();
        while (localIterator.hasNext())
        {
          java.security.cert.Extension localExtension = (java.security.cert.Extension)localIterator.next();
          OCSPResponse.debug.println("singleExtension: " + localExtension);
        }
      }
    }
    
    public OCSP.RevocationStatus.CertStatus getCertStatus()
    {
      return certStatus;
    }
    
    public CertId getCertId()
    {
      return certId;
    }
    
    public Date getThisUpdate()
    {
      return thisUpdate != null ? (Date)thisUpdate.clone() : null;
    }
    
    public Date getNextUpdate()
    {
      return nextUpdate != null ? (Date)nextUpdate.clone() : null;
    }
    
    public Date getRevocationTime()
    {
      return revocationTime != null ? (Date)revocationTime.clone() : null;
    }
    
    public CRLReason getRevocationReason()
    {
      return revocationReason;
    }
    
    public Map<String, java.security.cert.Extension> getSingleExtensions()
    {
      return Collections.unmodifiableMap(singleExtensions);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("SingleResponse:\n");
      localStringBuilder.append(certId);
      localStringBuilder.append("\nCertStatus: ").append(certStatus).append("\n");
      if (certStatus == OCSP.RevocationStatus.CertStatus.REVOKED)
      {
        localStringBuilder.append("revocationTime is ");
        localStringBuilder.append(revocationTime).append("\n");
        localStringBuilder.append("revocationReason is ");
        localStringBuilder.append(revocationReason).append("\n");
      }
      localStringBuilder.append("thisUpdate is ").append(thisUpdate).append("\n");
      if (nextUpdate != null) {
        localStringBuilder.append("nextUpdate is ").append(nextUpdate).append("\n");
      }
      Iterator localIterator = singleExtensions.values().iterator();
      while (localIterator.hasNext())
      {
        java.security.cert.Extension localExtension = (java.security.cert.Extension)localIterator.next();
        localStringBuilder.append("singleExtension: ");
        localStringBuilder.append(localExtension.toString()).append("\n");
      }
      return localStringBuilder.toString();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\certpath\OCSPResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */