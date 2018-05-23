package sun.security.x509;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.security.auth.x500.X500Principal;
import sun.misc.HexDumpEncoder;
import sun.security.provider.X509Factory;
import sun.security.util.DerEncoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class X509CRLImpl
  extends X509CRL
  implements DerEncoder
{
  private byte[] signedCRL = null;
  private byte[] signature = null;
  private byte[] tbsCertList = null;
  private AlgorithmId sigAlgId = null;
  private int version;
  private AlgorithmId infoSigAlgId;
  private X500Name issuer = null;
  private X500Principal issuerPrincipal = null;
  private Date thisUpdate = null;
  private Date nextUpdate = null;
  private Map<X509IssuerSerial, X509CRLEntry> revokedMap = new TreeMap();
  private List<X509CRLEntry> revokedList = new LinkedList();
  private CRLExtensions extensions = null;
  private static final boolean isExplicit = true;
  private static final long YR_2050 = 2524636800000L;
  private boolean readOnly = false;
  private PublicKey verifiedPublicKey;
  private String verifiedProvider;
  
  private X509CRLImpl() {}
  
  public X509CRLImpl(byte[] paramArrayOfByte)
    throws CRLException
  {
    try
    {
      parse(new DerValue(paramArrayOfByte));
    }
    catch (IOException localIOException)
    {
      signedCRL = null;
      throw new CRLException("Parsing error: " + localIOException.getMessage());
    }
  }
  
  public X509CRLImpl(DerValue paramDerValue)
    throws CRLException
  {
    try
    {
      parse(paramDerValue);
    }
    catch (IOException localIOException)
    {
      signedCRL = null;
      throw new CRLException("Parsing error: " + localIOException.getMessage());
    }
  }
  
  public X509CRLImpl(InputStream paramInputStream)
    throws CRLException
  {
    try
    {
      parse(new DerValue(paramInputStream));
    }
    catch (IOException localIOException)
    {
      signedCRL = null;
      throw new CRLException("Parsing error: " + localIOException.getMessage());
    }
  }
  
  public X509CRLImpl(X500Name paramX500Name, Date paramDate1, Date paramDate2)
  {
    issuer = paramX500Name;
    thisUpdate = paramDate1;
    nextUpdate = paramDate2;
  }
  
  public X509CRLImpl(X500Name paramX500Name, Date paramDate1, Date paramDate2, X509CRLEntry[] paramArrayOfX509CRLEntry)
    throws CRLException
  {
    issuer = paramX500Name;
    thisUpdate = paramDate1;
    nextUpdate = paramDate2;
    if (paramArrayOfX509CRLEntry != null)
    {
      X500Principal localX500Principal1 = getIssuerX500Principal();
      X500Principal localX500Principal2 = localX500Principal1;
      for (int i = 0; i < paramArrayOfX509CRLEntry.length; i++)
      {
        X509CRLEntryImpl localX509CRLEntryImpl = (X509CRLEntryImpl)paramArrayOfX509CRLEntry[i];
        try
        {
          localX500Principal2 = getCertIssuer(localX509CRLEntryImpl, localX500Principal2);
        }
        catch (IOException localIOException)
        {
          throw new CRLException(localIOException);
        }
        localX509CRLEntryImpl.setCertificateIssuer(localX500Principal1, localX500Principal2);
        X509IssuerSerial localX509IssuerSerial = new X509IssuerSerial(localX500Principal2, localX509CRLEntryImpl.getSerialNumber());
        revokedMap.put(localX509IssuerSerial, localX509CRLEntryImpl);
        revokedList.add(localX509CRLEntryImpl);
        if (localX509CRLEntryImpl.hasExtensions()) {
          version = 1;
        }
      }
    }
  }
  
  public X509CRLImpl(X500Name paramX500Name, Date paramDate1, Date paramDate2, X509CRLEntry[] paramArrayOfX509CRLEntry, CRLExtensions paramCRLExtensions)
    throws CRLException
  {
    this(paramX500Name, paramDate1, paramDate2, paramArrayOfX509CRLEntry);
    if (paramCRLExtensions != null)
    {
      extensions = paramCRLExtensions;
      version = 1;
    }
  }
  
  public byte[] getEncodedInternal()
    throws CRLException
  {
    if (signedCRL == null) {
      throw new CRLException("Null CRL to encode");
    }
    return signedCRL;
  }
  
  public byte[] getEncoded()
    throws CRLException
  {
    return (byte[])getEncodedInternal().clone();
  }
  
  public void encodeInfo(OutputStream paramOutputStream)
    throws CRLException
  {
    try
    {
      DerOutputStream localDerOutputStream1 = new DerOutputStream();
      DerOutputStream localDerOutputStream2 = new DerOutputStream();
      DerOutputStream localDerOutputStream3 = new DerOutputStream();
      if (version != 0) {
        localDerOutputStream1.putInteger(version);
      }
      infoSigAlgId.encode(localDerOutputStream1);
      if ((version == 0) && (issuer.toString() == null)) {
        throw new CRLException("Null Issuer DN not allowed in v1 CRL");
      }
      issuer.encode(localDerOutputStream1);
      if (thisUpdate.getTime() < 2524636800000L) {
        localDerOutputStream1.putUTCTime(thisUpdate);
      } else {
        localDerOutputStream1.putGeneralizedTime(thisUpdate);
      }
      if (nextUpdate != null) {
        if (nextUpdate.getTime() < 2524636800000L) {
          localDerOutputStream1.putUTCTime(nextUpdate);
        } else {
          localDerOutputStream1.putGeneralizedTime(nextUpdate);
        }
      }
      if (!revokedList.isEmpty())
      {
        Iterator localIterator = revokedList.iterator();
        while (localIterator.hasNext())
        {
          X509CRLEntry localX509CRLEntry = (X509CRLEntry)localIterator.next();
          ((X509CRLEntryImpl)localX509CRLEntry).encode(localDerOutputStream2);
        }
        localDerOutputStream1.write((byte)48, localDerOutputStream2);
      }
      if (extensions != null) {
        extensions.encode(localDerOutputStream1, true);
      }
      localDerOutputStream3.write((byte)48, localDerOutputStream1);
      tbsCertList = localDerOutputStream3.toByteArray();
      paramOutputStream.write(tbsCertList);
    }
    catch (IOException localIOException)
    {
      throw new CRLException("Encoding error: " + localIOException.getMessage());
    }
  }
  
  public void verify(PublicKey paramPublicKey)
    throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException
  {
    verify(paramPublicKey, "");
  }
  
  public synchronized void verify(PublicKey paramPublicKey, String paramString)
    throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException
  {
    if (paramString == null) {
      paramString = "";
    }
    if ((verifiedPublicKey != null) && (verifiedPublicKey.equals(paramPublicKey)) && (paramString.equals(verifiedProvider))) {
      return;
    }
    if (signedCRL == null) {
      throw new CRLException("Uninitialized CRL");
    }
    Signature localSignature = null;
    if (paramString.length() == 0) {
      localSignature = Signature.getInstance(sigAlgId.getName());
    } else {
      localSignature = Signature.getInstance(sigAlgId.getName(), paramString);
    }
    localSignature.initVerify(paramPublicKey);
    if (tbsCertList == null) {
      throw new CRLException("Uninitialized CRL");
    }
    localSignature.update(tbsCertList, 0, tbsCertList.length);
    if (!localSignature.verify(signature)) {
      throw new SignatureException("Signature does not match.");
    }
    verifiedPublicKey = paramPublicKey;
    verifiedProvider = paramString;
  }
  
  public synchronized void verify(PublicKey paramPublicKey, Provider paramProvider)
    throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
  {
    if (signedCRL == null) {
      throw new CRLException("Uninitialized CRL");
    }
    Signature localSignature = null;
    if (paramProvider == null) {
      localSignature = Signature.getInstance(sigAlgId.getName());
    } else {
      localSignature = Signature.getInstance(sigAlgId.getName(), paramProvider);
    }
    localSignature.initVerify(paramPublicKey);
    if (tbsCertList == null) {
      throw new CRLException("Uninitialized CRL");
    }
    localSignature.update(tbsCertList, 0, tbsCertList.length);
    if (!localSignature.verify(signature)) {
      throw new SignatureException("Signature does not match.");
    }
    verifiedPublicKey = paramPublicKey;
  }
  
  public static void verify(X509CRL paramX509CRL, PublicKey paramPublicKey, Provider paramProvider)
    throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException
  {
    paramX509CRL.verify(paramPublicKey, paramProvider);
  }
  
  public void sign(PrivateKey paramPrivateKey, String paramString)
    throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException
  {
    sign(paramPrivateKey, paramString, null);
  }
  
  public void sign(PrivateKey paramPrivateKey, String paramString1, String paramString2)
    throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException
  {
    try
    {
      if (readOnly) {
        throw new CRLException("cannot over-write existing CRL");
      }
      Signature localSignature = null;
      if ((paramString2 == null) || (paramString2.length() == 0)) {
        localSignature = Signature.getInstance(paramString1);
      } else {
        localSignature = Signature.getInstance(paramString1, paramString2);
      }
      localSignature.initSign(paramPrivateKey);
      sigAlgId = AlgorithmId.get(localSignature.getAlgorithm());
      infoSigAlgId = sigAlgId;
      DerOutputStream localDerOutputStream1 = new DerOutputStream();
      DerOutputStream localDerOutputStream2 = new DerOutputStream();
      encodeInfo(localDerOutputStream2);
      sigAlgId.encode(localDerOutputStream2);
      localSignature.update(tbsCertList, 0, tbsCertList.length);
      signature = localSignature.sign();
      localDerOutputStream2.putBitString(signature);
      localDerOutputStream1.write((byte)48, localDerOutputStream2);
      signedCRL = localDerOutputStream1.toByteArray();
      readOnly = true;
    }
    catch (IOException localIOException)
    {
      throw new CRLException("Error while encoding data: " + localIOException.getMessage());
    }
  }
  
  public String toString()
  {
    return toStringWithAlgName("" + sigAlgId);
  }
  
  public String toStringWithAlgName(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("X.509 CRL v" + (version + 1) + "\n");
    if (sigAlgId != null) {
      localStringBuffer.append("Signature Algorithm: " + paramString.toString() + ", OID=" + sigAlgId.getOID().toString() + "\n");
    }
    if (issuer != null) {
      localStringBuffer.append("Issuer: " + issuer.toString() + "\n");
    }
    if (thisUpdate != null) {
      localStringBuffer.append("\nThis Update: " + thisUpdate.toString() + "\n");
    }
    if (nextUpdate != null) {
      localStringBuffer.append("Next Update: " + nextUpdate.toString() + "\n");
    }
    Object localObject2;
    if (revokedList.isEmpty())
    {
      localStringBuffer.append("\nNO certificates have been revoked\n");
    }
    else
    {
      localStringBuffer.append("\nRevoked Certificates: " + revokedList.size());
      int i = 1;
      localObject2 = revokedList.iterator();
      while (((Iterator)localObject2).hasNext())
      {
        X509CRLEntry localX509CRLEntry = (X509CRLEntry)((Iterator)localObject2).next();
        localStringBuffer.append("\n[" + i++ + "] " + localX509CRLEntry.toString());
      }
    }
    Object localObject1;
    if (extensions != null)
    {
      localObject1 = extensions.getAllExtensions();
      localObject2 = ((Collection)localObject1).toArray();
      localStringBuffer.append("\nCRL Extensions: " + localObject2.length);
      for (int j = 0; j < localObject2.length; j++)
      {
        localStringBuffer.append("\n[" + (j + 1) + "]: ");
        Extension localExtension = (Extension)localObject2[j];
        try
        {
          if (OIDMap.getClass(localExtension.getExtensionId()) == null)
          {
            localStringBuffer.append(localExtension.toString());
            byte[] arrayOfByte = localExtension.getExtensionValue();
            if (arrayOfByte != null)
            {
              DerOutputStream localDerOutputStream = new DerOutputStream();
              localDerOutputStream.putOctetString(arrayOfByte);
              arrayOfByte = localDerOutputStream.toByteArray();
              HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
              localStringBuffer.append("Extension unknown: DER encoded OCTET string =\n" + localHexDumpEncoder.encodeBuffer(arrayOfByte) + "\n");
            }
          }
          else
          {
            localStringBuffer.append(localExtension.toString());
          }
        }
        catch (Exception localException)
        {
          localStringBuffer.append(", Error parsing this extension");
        }
      }
    }
    if (signature != null)
    {
      localObject1 = new HexDumpEncoder();
      localStringBuffer.append("\nSignature:\n" + ((HexDumpEncoder)localObject1).encodeBuffer(signature) + "\n");
    }
    else
    {
      localStringBuffer.append("NOT signed yet\n");
    }
    return localStringBuffer.toString();
  }
  
  public boolean isRevoked(Certificate paramCertificate)
  {
    if ((revokedMap.isEmpty()) || (!(paramCertificate instanceof X509Certificate))) {
      return false;
    }
    X509Certificate localX509Certificate = (X509Certificate)paramCertificate;
    X509IssuerSerial localX509IssuerSerial = new X509IssuerSerial(localX509Certificate);
    return revokedMap.containsKey(localX509IssuerSerial);
  }
  
  public int getVersion()
  {
    return version + 1;
  }
  
  public Principal getIssuerDN()
  {
    return issuer;
  }
  
  public X500Principal getIssuerX500Principal()
  {
    if (issuerPrincipal == null) {
      issuerPrincipal = issuer.asX500Principal();
    }
    return issuerPrincipal;
  }
  
  public Date getThisUpdate()
  {
    return new Date(thisUpdate.getTime());
  }
  
  public Date getNextUpdate()
  {
    if (nextUpdate == null) {
      return null;
    }
    return new Date(nextUpdate.getTime());
  }
  
  public X509CRLEntry getRevokedCertificate(BigInteger paramBigInteger)
  {
    if (revokedMap.isEmpty()) {
      return null;
    }
    X509IssuerSerial localX509IssuerSerial = new X509IssuerSerial(getIssuerX500Principal(), paramBigInteger);
    return (X509CRLEntry)revokedMap.get(localX509IssuerSerial);
  }
  
  public X509CRLEntry getRevokedCertificate(X509Certificate paramX509Certificate)
  {
    if (revokedMap.isEmpty()) {
      return null;
    }
    X509IssuerSerial localX509IssuerSerial = new X509IssuerSerial(paramX509Certificate);
    return (X509CRLEntry)revokedMap.get(localX509IssuerSerial);
  }
  
  public Set<X509CRLEntry> getRevokedCertificates()
  {
    if (revokedList.isEmpty()) {
      return null;
    }
    return new TreeSet(revokedList);
  }
  
  public byte[] getTBSCertList()
    throws CRLException
  {
    if (tbsCertList == null) {
      throw new CRLException("Uninitialized CRL");
    }
    return (byte[])tbsCertList.clone();
  }
  
  public byte[] getSignature()
  {
    if (signature == null) {
      return null;
    }
    return (byte[])signature.clone();
  }
  
  public String getSigAlgName()
  {
    if (sigAlgId == null) {
      return null;
    }
    return sigAlgId.getName();
  }
  
  public String getSigAlgOID()
  {
    if (sigAlgId == null) {
      return null;
    }
    ObjectIdentifier localObjectIdentifier = sigAlgId.getOID();
    return localObjectIdentifier.toString();
  }
  
  public byte[] getSigAlgParams()
  {
    if (sigAlgId == null) {
      return null;
    }
    try
    {
      return sigAlgId.getEncodedParams();
    }
    catch (IOException localIOException) {}
    return null;
  }
  
  public AlgorithmId getSigAlgId()
  {
    return sigAlgId;
  }
  
  public KeyIdentifier getAuthKeyId()
    throws IOException
  {
    AuthorityKeyIdentifierExtension localAuthorityKeyIdentifierExtension = getAuthKeyIdExtension();
    if (localAuthorityKeyIdentifierExtension != null)
    {
      KeyIdentifier localKeyIdentifier = (KeyIdentifier)localAuthorityKeyIdentifierExtension.get("key_id");
      return localKeyIdentifier;
    }
    return null;
  }
  
  public AuthorityKeyIdentifierExtension getAuthKeyIdExtension()
    throws IOException
  {
    Object localObject = getExtension(PKIXExtensions.AuthorityKey_Id);
    return (AuthorityKeyIdentifierExtension)localObject;
  }
  
  public CRLNumberExtension getCRLNumberExtension()
    throws IOException
  {
    Object localObject = getExtension(PKIXExtensions.CRLNumber_Id);
    return (CRLNumberExtension)localObject;
  }
  
  public BigInteger getCRLNumber()
    throws IOException
  {
    CRLNumberExtension localCRLNumberExtension = getCRLNumberExtension();
    if (localCRLNumberExtension != null)
    {
      BigInteger localBigInteger = localCRLNumberExtension.get("value");
      return localBigInteger;
    }
    return null;
  }
  
  public DeltaCRLIndicatorExtension getDeltaCRLIndicatorExtension()
    throws IOException
  {
    Object localObject = getExtension(PKIXExtensions.DeltaCRLIndicator_Id);
    return (DeltaCRLIndicatorExtension)localObject;
  }
  
  public BigInteger getBaseCRLNumber()
    throws IOException
  {
    DeltaCRLIndicatorExtension localDeltaCRLIndicatorExtension = getDeltaCRLIndicatorExtension();
    if (localDeltaCRLIndicatorExtension != null)
    {
      BigInteger localBigInteger = localDeltaCRLIndicatorExtension.get("value");
      return localBigInteger;
    }
    return null;
  }
  
  public IssuerAlternativeNameExtension getIssuerAltNameExtension()
    throws IOException
  {
    Object localObject = getExtension(PKIXExtensions.IssuerAlternativeName_Id);
    return (IssuerAlternativeNameExtension)localObject;
  }
  
  public IssuingDistributionPointExtension getIssuingDistributionPointExtension()
    throws IOException
  {
    Object localObject = getExtension(PKIXExtensions.IssuingDistributionPoint_Id);
    return (IssuingDistributionPointExtension)localObject;
  }
  
  public boolean hasUnsupportedCriticalExtension()
  {
    if (extensions == null) {
      return false;
    }
    return extensions.hasUnsupportedCriticalExtension();
  }
  
  public Set<String> getCriticalExtensionOIDs()
  {
    if (extensions == null) {
      return null;
    }
    TreeSet localTreeSet = new TreeSet();
    Iterator localIterator = extensions.getAllExtensions().iterator();
    while (localIterator.hasNext())
    {
      Extension localExtension = (Extension)localIterator.next();
      if (localExtension.isCritical()) {
        localTreeSet.add(localExtension.getExtensionId().toString());
      }
    }
    return localTreeSet;
  }
  
  public Set<String> getNonCriticalExtensionOIDs()
  {
    if (extensions == null) {
      return null;
    }
    TreeSet localTreeSet = new TreeSet();
    Iterator localIterator = extensions.getAllExtensions().iterator();
    while (localIterator.hasNext())
    {
      Extension localExtension = (Extension)localIterator.next();
      if (!localExtension.isCritical()) {
        localTreeSet.add(localExtension.getExtensionId().toString());
      }
    }
    return localTreeSet;
  }
  
  public byte[] getExtensionValue(String paramString)
  {
    if (extensions == null) {
      return null;
    }
    try
    {
      String str = OIDMap.getName(new ObjectIdentifier(paramString));
      Object localObject1 = null;
      if (str == null)
      {
        localObject2 = new ObjectIdentifier(paramString);
        localObject3 = null;
        Enumeration localEnumeration = extensions.getElements();
        while (localEnumeration.hasMoreElements())
        {
          localObject3 = (Extension)localEnumeration.nextElement();
          ObjectIdentifier localObjectIdentifier = ((Extension)localObject3).getExtensionId();
          if (localObjectIdentifier.equals(localObject2)) {
            localObject1 = localObject3;
          }
        }
      }
      else
      {
        localObject1 = extensions.get(str);
      }
      if (localObject1 == null) {
        return null;
      }
      Object localObject2 = ((Extension)localObject1).getExtensionValue();
      if (localObject2 == null) {
        return null;
      }
      Object localObject3 = new DerOutputStream();
      ((DerOutputStream)localObject3).putOctetString((byte[])localObject2);
      return ((DerOutputStream)localObject3).toByteArray();
    }
    catch (Exception localException) {}
    return null;
  }
  
  public Object getExtension(ObjectIdentifier paramObjectIdentifier)
  {
    if (extensions == null) {
      return null;
    }
    return extensions.get(OIDMap.getName(paramObjectIdentifier));
  }
  
  private void parse(DerValue paramDerValue)
    throws CRLException, IOException
  {
    if (readOnly) {
      throw new CRLException("cannot over-write existing CRL");
    }
    if ((paramDerValue.getData() == null) || (tag != 48)) {
      throw new CRLException("Invalid DER-encoded CRL data");
    }
    signedCRL = paramDerValue.toByteArray();
    DerValue[] arrayOfDerValue1 = new DerValue[3];
    arrayOfDerValue1[0] = data.getDerValue();
    arrayOfDerValue1[1] = data.getDerValue();
    arrayOfDerValue1[2] = data.getDerValue();
    if (data.available() != 0) {
      throw new CRLException("signed overrun, bytes = " + data.available());
    }
    if (0tag != 48) {
      throw new CRLException("signed CRL fields invalid");
    }
    sigAlgId = AlgorithmId.parse(arrayOfDerValue1[1]);
    signature = arrayOfDerValue1[2].getBitString();
    if (1data.available() != 0) {
      throw new CRLException("AlgorithmId field overrun");
    }
    if (2data.available() != 0) {
      throw new CRLException("Signature field overrun");
    }
    tbsCertList = arrayOfDerValue1[0].toByteArray();
    DerInputStream localDerInputStream = 0data;
    version = 0;
    int i = (byte)localDerInputStream.peekByte();
    if (i == 2)
    {
      version = localDerInputStream.getInteger();
      if (version != 1) {
        throw new CRLException("Invalid version");
      }
    }
    DerValue localDerValue = localDerInputStream.getDerValue();
    AlgorithmId localAlgorithmId = AlgorithmId.parse(localDerValue);
    if (!localAlgorithmId.equals(sigAlgId)) {
      throw new CRLException("Signature algorithm mismatch");
    }
    infoSigAlgId = localAlgorithmId;
    issuer = new X500Name(localDerInputStream);
    if (issuer.isEmpty()) {
      throw new CRLException("Empty issuer DN not allowed in X509CRLs");
    }
    i = (byte)localDerInputStream.peekByte();
    if (i == 23) {
      thisUpdate = localDerInputStream.getUTCTime();
    } else if (i == 24) {
      thisUpdate = localDerInputStream.getGeneralizedTime();
    } else {
      throw new CRLException("Invalid encoding for thisUpdate (tag=" + i + ")");
    }
    if (localDerInputStream.available() == 0) {
      return;
    }
    i = (byte)localDerInputStream.peekByte();
    if (i == 23) {
      nextUpdate = localDerInputStream.getUTCTime();
    } else if (i == 24) {
      nextUpdate = localDerInputStream.getGeneralizedTime();
    }
    if (localDerInputStream.available() == 0) {
      return;
    }
    i = (byte)localDerInputStream.peekByte();
    if ((i == 48) && ((i & 0xC0) != 128))
    {
      DerValue[] arrayOfDerValue2 = localDerInputStream.getSequence(4);
      X500Principal localX500Principal1 = getIssuerX500Principal();
      X500Principal localX500Principal2 = localX500Principal1;
      for (int j = 0; j < arrayOfDerValue2.length; j++)
      {
        X509CRLEntryImpl localX509CRLEntryImpl = new X509CRLEntryImpl(arrayOfDerValue2[j]);
        localX500Principal2 = getCertIssuer(localX509CRLEntryImpl, localX500Principal2);
        localX509CRLEntryImpl.setCertificateIssuer(localX500Principal1, localX500Principal2);
        X509IssuerSerial localX509IssuerSerial = new X509IssuerSerial(localX500Principal2, localX509CRLEntryImpl.getSerialNumber());
        revokedMap.put(localX509IssuerSerial, localX509CRLEntryImpl);
        revokedList.add(localX509CRLEntryImpl);
      }
    }
    if (localDerInputStream.available() == 0) {
      return;
    }
    localDerValue = localDerInputStream.getDerValue();
    if ((localDerValue.isConstructed()) && (localDerValue.isContextSpecific((byte)0))) {
      extensions = new CRLExtensions(data);
    }
    readOnly = true;
  }
  
  public static X500Principal getIssuerX500Principal(X509CRL paramX509CRL)
  {
    try
    {
      byte[] arrayOfByte1 = paramX509CRL.getEncoded();
      DerInputStream localDerInputStream1 = new DerInputStream(arrayOfByte1);
      DerValue localDerValue1 = localDerInputStream1.getSequence(3)[0];
      DerInputStream localDerInputStream2 = data;
      int i = (byte)localDerInputStream2.peekByte();
      if (i == 2) {
        localDerValue2 = localDerInputStream2.getDerValue();
      }
      DerValue localDerValue2 = localDerInputStream2.getDerValue();
      localDerValue2 = localDerInputStream2.getDerValue();
      byte[] arrayOfByte2 = localDerValue2.toByteArray();
      return new X500Principal(arrayOfByte2);
    }
    catch (Exception localException)
    {
      throw new RuntimeException("Could not parse issuer", localException);
    }
  }
  
  public static byte[] getEncodedInternal(X509CRL paramX509CRL)
    throws CRLException
  {
    if ((paramX509CRL instanceof X509CRLImpl)) {
      return ((X509CRLImpl)paramX509CRL).getEncodedInternal();
    }
    return paramX509CRL.getEncoded();
  }
  
  public static X509CRLImpl toImpl(X509CRL paramX509CRL)
    throws CRLException
  {
    if ((paramX509CRL instanceof X509CRLImpl)) {
      return (X509CRLImpl)paramX509CRL;
    }
    return X509Factory.intern(paramX509CRL);
  }
  
  private X500Principal getCertIssuer(X509CRLEntryImpl paramX509CRLEntryImpl, X500Principal paramX500Principal)
    throws IOException
  {
    CertificateIssuerExtension localCertificateIssuerExtension = paramX509CRLEntryImpl.getCertificateIssuerExtension();
    if (localCertificateIssuerExtension != null)
    {
      GeneralNames localGeneralNames = localCertificateIssuerExtension.get("issuer");
      X500Name localX500Name = (X500Name)localGeneralNames.get(0).getName();
      return localX500Name.asX500Principal();
    }
    return paramX500Principal;
  }
  
  public void derEncode(OutputStream paramOutputStream)
    throws IOException
  {
    if (signedCRL == null) {
      throw new IOException("Null CRL to encode");
    }
    paramOutputStream.write((byte[])signedCRL.clone());
  }
  
  private static final class X509IssuerSerial
    implements Comparable<X509IssuerSerial>
  {
    final X500Principal issuer;
    final BigInteger serial;
    volatile int hashcode = 0;
    
    X509IssuerSerial(X500Principal paramX500Principal, BigInteger paramBigInteger)
    {
      issuer = paramX500Principal;
      serial = paramBigInteger;
    }
    
    X509IssuerSerial(X509Certificate paramX509Certificate)
    {
      this(paramX509Certificate.getIssuerX500Principal(), paramX509Certificate.getSerialNumber());
    }
    
    X500Principal getIssuer()
    {
      return issuer;
    }
    
    BigInteger getSerial()
    {
      return serial;
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if (!(paramObject instanceof X509IssuerSerial)) {
        return false;
      }
      X509IssuerSerial localX509IssuerSerial = (X509IssuerSerial)paramObject;
      return (serial.equals(localX509IssuerSerial.getSerial())) && (issuer.equals(localX509IssuerSerial.getIssuer()));
    }
    
    public int hashCode()
    {
      if (hashcode == 0)
      {
        int i = 17;
        i = 37 * i + issuer.hashCode();
        i = 37 * i + serial.hashCode();
        hashcode = i;
      }
      return hashcode;
    }
    
    public int compareTo(X509IssuerSerial paramX509IssuerSerial)
    {
      int i = issuer.toString().compareTo(issuer.toString());
      if (i != 0) {
        return i;
      }
      return serial.compareTo(serial);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\X509CRLImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */