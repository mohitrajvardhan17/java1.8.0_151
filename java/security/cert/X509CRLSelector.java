package java.security.cert;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.x509.CRLNumberExtension;
import sun.security.x509.X500Name;

public class X509CRLSelector
  implements CRLSelector
{
  private static final Debug debug = Debug.getInstance("certpath");
  private HashSet<Object> issuerNames;
  private HashSet<X500Principal> issuerX500Principals;
  private BigInteger minCRL;
  private BigInteger maxCRL;
  private Date dateAndTime;
  private X509Certificate certChecking;
  private long skew = 0L;
  
  public X509CRLSelector() {}
  
  public void setIssuers(Collection<X500Principal> paramCollection)
  {
    if ((paramCollection == null) || (paramCollection.isEmpty()))
    {
      issuerNames = null;
      issuerX500Principals = null;
    }
    else
    {
      issuerX500Principals = new HashSet(paramCollection);
      issuerNames = new HashSet();
      Iterator localIterator = issuerX500Principals.iterator();
      while (localIterator.hasNext())
      {
        X500Principal localX500Principal = (X500Principal)localIterator.next();
        issuerNames.add(localX500Principal.getEncoded());
      }
    }
  }
  
  public void setIssuerNames(Collection<?> paramCollection)
    throws IOException
  {
    if ((paramCollection == null) || (paramCollection.size() == 0))
    {
      issuerNames = null;
      issuerX500Principals = null;
    }
    else
    {
      HashSet localHashSet = cloneAndCheckIssuerNames(paramCollection);
      issuerX500Principals = parseIssuerNames(localHashSet);
      issuerNames = localHashSet;
    }
  }
  
  public void addIssuer(X500Principal paramX500Principal)
  {
    addIssuerNameInternal(paramX500Principal.getEncoded(), paramX500Principal);
  }
  
  public void addIssuerName(String paramString)
    throws IOException
  {
    addIssuerNameInternal(paramString, new X500Name(paramString).asX500Principal());
  }
  
  public void addIssuerName(byte[] paramArrayOfByte)
    throws IOException
  {
    addIssuerNameInternal(paramArrayOfByte.clone(), new X500Name(paramArrayOfByte).asX500Principal());
  }
  
  private void addIssuerNameInternal(Object paramObject, X500Principal paramX500Principal)
  {
    if (issuerNames == null) {
      issuerNames = new HashSet();
    }
    if (issuerX500Principals == null) {
      issuerX500Principals = new HashSet();
    }
    issuerNames.add(paramObject);
    issuerX500Principals.add(paramX500Principal);
  }
  
  private static HashSet<Object> cloneAndCheckIssuerNames(Collection<?> paramCollection)
    throws IOException
  {
    HashSet localHashSet = new HashSet();
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      if ((!(localObject instanceof byte[])) && (!(localObject instanceof String))) {
        throw new IOException("name not byte array or String");
      }
      if ((localObject instanceof byte[])) {
        localHashSet.add(((byte[])localObject).clone());
      } else {
        localHashSet.add(localObject);
      }
    }
    return localHashSet;
  }
  
  private static HashSet<Object> cloneIssuerNames(Collection<Object> paramCollection)
  {
    try
    {
      return cloneAndCheckIssuerNames(paramCollection);
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
  }
  
  private static HashSet<X500Principal> parseIssuerNames(Collection<Object> paramCollection)
    throws IOException
  {
    HashSet localHashSet = new HashSet();
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      if ((localObject instanceof String)) {
        localHashSet.add(new X500Name((String)localObject).asX500Principal());
      } else {
        try
        {
          localHashSet.add(new X500Principal((byte[])localObject));
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          throw ((IOException)new IOException("Invalid name").initCause(localIllegalArgumentException));
        }
      }
    }
    return localHashSet;
  }
  
  public void setMinCRLNumber(BigInteger paramBigInteger)
  {
    minCRL = paramBigInteger;
  }
  
  public void setMaxCRLNumber(BigInteger paramBigInteger)
  {
    maxCRL = paramBigInteger;
  }
  
  public void setDateAndTime(Date paramDate)
  {
    if (paramDate == null) {
      dateAndTime = null;
    } else {
      dateAndTime = new Date(paramDate.getTime());
    }
    skew = 0L;
  }
  
  void setDateAndTime(Date paramDate, long paramLong)
  {
    dateAndTime = (paramDate == null ? null : new Date(paramDate.getTime()));
    skew = paramLong;
  }
  
  public void setCertificateChecking(X509Certificate paramX509Certificate)
  {
    certChecking = paramX509Certificate;
  }
  
  public Collection<X500Principal> getIssuers()
  {
    if (issuerX500Principals == null) {
      return null;
    }
    return Collections.unmodifiableCollection(issuerX500Principals);
  }
  
  public Collection<Object> getIssuerNames()
  {
    if (issuerNames == null) {
      return null;
    }
    return cloneIssuerNames(issuerNames);
  }
  
  public BigInteger getMinCRL()
  {
    return minCRL;
  }
  
  public BigInteger getMaxCRL()
  {
    return maxCRL;
  }
  
  public Date getDateAndTime()
  {
    if (dateAndTime == null) {
      return null;
    }
    return (Date)dateAndTime.clone();
  }
  
  public X509Certificate getCertificateChecking()
  {
    return certChecking;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("X509CRLSelector: [\n");
    if (issuerNames != null)
    {
      localStringBuffer.append("  IssuerNames:\n");
      Iterator localIterator = issuerNames.iterator();
      while (localIterator.hasNext()) {
        localStringBuffer.append("    " + localIterator.next() + "\n");
      }
    }
    if (minCRL != null) {
      localStringBuffer.append("  minCRLNumber: " + minCRL + "\n");
    }
    if (maxCRL != null) {
      localStringBuffer.append("  maxCRLNumber: " + maxCRL + "\n");
    }
    if (dateAndTime != null) {
      localStringBuffer.append("  dateAndTime: " + dateAndTime + "\n");
    }
    if (certChecking != null) {
      localStringBuffer.append("  Certificate being checked: " + certChecking + "\n");
    }
    localStringBuffer.append("]");
    return localStringBuffer.toString();
  }
  
  public boolean match(CRL paramCRL)
  {
    if (!(paramCRL instanceof X509CRL)) {
      return false;
    }
    X509CRL localX509CRL = (X509CRL)paramCRL;
    Object localObject1;
    Object localObject2;
    if (issuerNames != null)
    {
      localObject1 = localX509CRL.getIssuerX500Principal();
      localObject2 = issuerX500Principals.iterator();
      int i = 0;
      while ((i == 0) && (((Iterator)localObject2).hasNext())) {
        if (((X500Principal)((Iterator)localObject2).next()).equals(localObject1)) {
          i = 1;
        }
      }
      if (i == 0)
      {
        if (debug != null) {
          debug.println("X509CRLSelector.match: issuer DNs don't match");
        }
        return false;
      }
    }
    Object localObject3;
    if ((minCRL != null) || (maxCRL != null))
    {
      localObject1 = localX509CRL.getExtensionValue("2.5.29.20");
      if ((localObject1 == null) && (debug != null)) {
        debug.println("X509CRLSelector.match: no CRLNumber");
      }
      try
      {
        DerInputStream localDerInputStream = new DerInputStream((byte[])localObject1);
        localObject3 = localDerInputStream.getOctetString();
        CRLNumberExtension localCRLNumberExtension = new CRLNumberExtension(Boolean.FALSE, localObject3);
        localObject2 = localCRLNumberExtension.get("value");
      }
      catch (IOException localIOException)
      {
        if (debug != null) {
          debug.println("X509CRLSelector.match: exception in decoding CRL number");
        }
        return false;
      }
      if ((minCRL != null) && (((BigInteger)localObject2).compareTo(minCRL) < 0))
      {
        if (debug != null) {
          debug.println("X509CRLSelector.match: CRLNumber too small");
        }
        return false;
      }
      if ((maxCRL != null) && (((BigInteger)localObject2).compareTo(maxCRL) > 0))
      {
        if (debug != null) {
          debug.println("X509CRLSelector.match: CRLNumber too large");
        }
        return false;
      }
    }
    if (dateAndTime != null)
    {
      localObject1 = localX509CRL.getThisUpdate();
      localObject2 = localX509CRL.getNextUpdate();
      if (localObject2 == null)
      {
        if (debug != null) {
          debug.println("X509CRLSelector.match: nextUpdate null");
        }
        return false;
      }
      Date localDate = dateAndTime;
      localObject3 = dateAndTime;
      if (skew > 0L)
      {
        localDate = new Date(dateAndTime.getTime() + skew);
        localObject3 = new Date(dateAndTime.getTime() - skew);
      }
      if ((((Date)localObject3).after((Date)localObject2)) || (localDate.before((Date)localObject1)))
      {
        if (debug != null) {
          debug.println("X509CRLSelector.match: update out-of-range");
        }
        return false;
      }
    }
    return true;
  }
  
  public Object clone()
  {
    try
    {
      X509CRLSelector localX509CRLSelector = (X509CRLSelector)super.clone();
      if (issuerNames != null)
      {
        issuerNames = new HashSet(issuerNames);
        issuerX500Principals = new HashSet(issuerX500Principals);
      }
      return localX509CRLSelector;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException.toString(), localCloneNotSupportedException);
    }
  }
  
  static {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\X509CRLSelector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */