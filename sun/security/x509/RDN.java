package sun.security.x509;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class RDN
{
  final AVA[] assertion;
  private volatile List<AVA> avaList;
  private volatile String canonicalString;
  
  public RDN(String paramString)
    throws IOException
  {
    this(paramString, Collections.emptyMap());
  }
  
  public RDN(String paramString, Map<String, String> paramMap)
    throws IOException
  {
    int i = 0;
    int j = 0;
    int k = 0;
    ArrayList localArrayList = new ArrayList(3);
    for (int m = paramString.indexOf('+'); m >= 0; m = paramString.indexOf('+', j))
    {
      i += X500Name.countQuotes(paramString, j, m);
      if ((m > 0) && (paramString.charAt(m - 1) != '\\') && (i != 1))
      {
        str = paramString.substring(k, m);
        if (str.length() == 0) {
          throw new IOException("empty AVA in RDN \"" + paramString + "\"");
        }
        localAVA = new AVA(new StringReader(str), paramMap);
        localArrayList.add(localAVA);
        k = m + 1;
        i = 0;
      }
      j = m + 1;
    }
    String str = paramString.substring(k);
    if (str.length() == 0) {
      throw new IOException("empty AVA in RDN \"" + paramString + "\"");
    }
    AVA localAVA = new AVA(new StringReader(str), paramMap);
    localArrayList.add(localAVA);
    assertion = ((AVA[])localArrayList.toArray(new AVA[localArrayList.size()]));
  }
  
  RDN(String paramString1, String paramString2)
    throws IOException
  {
    this(paramString1, paramString2, Collections.emptyMap());
  }
  
  RDN(String paramString1, String paramString2, Map<String, String> paramMap)
    throws IOException
  {
    if (!paramString2.equalsIgnoreCase("RFC2253")) {
      throw new IOException("Unsupported format " + paramString2);
    }
    int i = 0;
    int j = 0;
    ArrayList localArrayList = new ArrayList(3);
    for (int k = paramString1.indexOf('+'); k >= 0; k = paramString1.indexOf('+', i))
    {
      if ((k > 0) && (paramString1.charAt(k - 1) != '\\'))
      {
        str = paramString1.substring(j, k);
        if (str.length() == 0) {
          throw new IOException("empty AVA in RDN \"" + paramString1 + "\"");
        }
        localAVA = new AVA(new StringReader(str), 3, paramMap);
        localArrayList.add(localAVA);
        j = k + 1;
      }
      i = k + 1;
    }
    String str = paramString1.substring(j);
    if (str.length() == 0) {
      throw new IOException("empty AVA in RDN \"" + paramString1 + "\"");
    }
    AVA localAVA = new AVA(new StringReader(str), 3, paramMap);
    localArrayList.add(localAVA);
    assertion = ((AVA[])localArrayList.toArray(new AVA[localArrayList.size()]));
  }
  
  RDN(DerValue paramDerValue)
    throws IOException
  {
    if (tag != 49) {
      throw new IOException("X500 RDN");
    }
    DerInputStream localDerInputStream = new DerInputStream(paramDerValue.toByteArray());
    DerValue[] arrayOfDerValue = localDerInputStream.getSet(5);
    assertion = new AVA[arrayOfDerValue.length];
    for (int i = 0; i < arrayOfDerValue.length; i++) {
      assertion[i] = new AVA(arrayOfDerValue[i]);
    }
  }
  
  RDN(int paramInt)
  {
    assertion = new AVA[paramInt];
  }
  
  public RDN(AVA paramAVA)
  {
    if (paramAVA == null) {
      throw new NullPointerException();
    }
    assertion = new AVA[] { paramAVA };
  }
  
  public RDN(AVA[] paramArrayOfAVA)
  {
    assertion = ((AVA[])paramArrayOfAVA.clone());
    for (int i = 0; i < assertion.length; i++) {
      if (assertion[i] == null) {
        throw new NullPointerException();
      }
    }
  }
  
  public List<AVA> avas()
  {
    List localList = avaList;
    if (localList == null)
    {
      localList = Collections.unmodifiableList(Arrays.asList(assertion));
      avaList = localList;
    }
    return localList;
  }
  
  public int size()
  {
    return assertion.length;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof RDN)) {
      return false;
    }
    RDN localRDN = (RDN)paramObject;
    if (assertion.length != assertion.length) {
      return false;
    }
    String str1 = toRFC2253String(true);
    String str2 = localRDN.toRFC2253String(true);
    return str1.equals(str2);
  }
  
  public int hashCode()
  {
    return toRFC2253String(true).hashCode();
  }
  
  DerValue findAttribute(ObjectIdentifier paramObjectIdentifier)
  {
    for (int i = 0; i < assertion.length; i++) {
      if (assertion[i].oid.equals(paramObjectIdentifier)) {
        return assertion[i].value;
      }
    }
    return null;
  }
  
  void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    paramDerOutputStream.putOrderedSetOf((byte)49, assertion);
  }
  
  public String toString()
  {
    if (assertion.length == 1) {
      return assertion[0].toString();
    }
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < assertion.length; i++)
    {
      if (i != 0) {
        localStringBuilder.append(" + ");
      }
      localStringBuilder.append(assertion[i].toString());
    }
    return localStringBuilder.toString();
  }
  
  public String toRFC1779String()
  {
    return toRFC1779String(Collections.emptyMap());
  }
  
  public String toRFC1779String(Map<String, String> paramMap)
  {
    if (assertion.length == 1) {
      return assertion[0].toRFC1779String(paramMap);
    }
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < assertion.length; i++)
    {
      if (i != 0) {
        localStringBuilder.append(" + ");
      }
      localStringBuilder.append(assertion[i].toRFC1779String(paramMap));
    }
    return localStringBuilder.toString();
  }
  
  public String toRFC2253String()
  {
    return toRFC2253StringInternal(false, Collections.emptyMap());
  }
  
  public String toRFC2253String(Map<String, String> paramMap)
  {
    return toRFC2253StringInternal(false, paramMap);
  }
  
  public String toRFC2253String(boolean paramBoolean)
  {
    if (!paramBoolean) {
      return toRFC2253StringInternal(false, Collections.emptyMap());
    }
    String str = canonicalString;
    if (str == null)
    {
      str = toRFC2253StringInternal(true, Collections.emptyMap());
      canonicalString = str;
    }
    return str;
  }
  
  private String toRFC2253StringInternal(boolean paramBoolean, Map<String, String> paramMap)
  {
    if (assertion.length == 1) {
      return paramBoolean ? assertion[0].toRFC2253CanonicalString() : assertion[0].toRFC2253String(paramMap);
    }
    AVA[] arrayOfAVA1 = assertion;
    if (paramBoolean)
    {
      arrayOfAVA1 = (AVA[])assertion.clone();
      Arrays.sort(arrayOfAVA1, AVAComparator.getInstance());
    }
    StringJoiner localStringJoiner = new StringJoiner("+");
    for (AVA localAVA : arrayOfAVA1) {
      localStringJoiner.add(paramBoolean ? localAVA.toRFC2253CanonicalString() : localAVA.toRFC2253String(paramMap));
    }
    return localStringJoiner.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\RDN.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */