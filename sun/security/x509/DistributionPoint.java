package sun.security.x509;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import sun.security.util.BitArray;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class DistributionPoint
{
  public static final int KEY_COMPROMISE = 1;
  public static final int CA_COMPROMISE = 2;
  public static final int AFFILIATION_CHANGED = 3;
  public static final int SUPERSEDED = 4;
  public static final int CESSATION_OF_OPERATION = 5;
  public static final int CERTIFICATE_HOLD = 6;
  public static final int PRIVILEGE_WITHDRAWN = 7;
  public static final int AA_COMPROMISE = 8;
  private static final String[] REASON_STRINGS = { null, "key compromise", "CA compromise", "affiliation changed", "superseded", "cessation of operation", "certificate hold", "privilege withdrawn", "AA compromise" };
  private static final byte TAG_DIST_PT = 0;
  private static final byte TAG_REASONS = 1;
  private static final byte TAG_ISSUER = 2;
  private static final byte TAG_FULL_NAME = 0;
  private static final byte TAG_REL_NAME = 1;
  private GeneralNames fullName;
  private RDN relativeName;
  private boolean[] reasonFlags;
  private GeneralNames crlIssuer;
  private volatile int hashCode;
  
  public DistributionPoint(GeneralNames paramGeneralNames1, boolean[] paramArrayOfBoolean, GeneralNames paramGeneralNames2)
  {
    if ((paramGeneralNames1 == null) && (paramGeneralNames2 == null)) {
      throw new IllegalArgumentException("fullName and crlIssuer may not both be null");
    }
    fullName = paramGeneralNames1;
    reasonFlags = paramArrayOfBoolean;
    crlIssuer = paramGeneralNames2;
  }
  
  public DistributionPoint(RDN paramRDN, boolean[] paramArrayOfBoolean, GeneralNames paramGeneralNames)
  {
    if ((paramRDN == null) && (paramGeneralNames == null)) {
      throw new IllegalArgumentException("relativeName and crlIssuer may not both be null");
    }
    relativeName = paramRDN;
    reasonFlags = paramArrayOfBoolean;
    crlIssuer = paramGeneralNames;
  }
  
  public DistributionPoint(DerValue paramDerValue)
    throws IOException
  {
    if (tag != 48) {
      throw new IOException("Invalid encoding of DistributionPoint.");
    }
    while ((data != null) && (data.available() != 0))
    {
      DerValue localDerValue1 = data.getDerValue();
      if ((localDerValue1.isContextSpecific((byte)0)) && (localDerValue1.isConstructed()))
      {
        if ((fullName != null) || (relativeName != null)) {
          throw new IOException("Duplicate DistributionPointName in DistributionPoint.");
        }
        DerValue localDerValue2 = data.getDerValue();
        if ((localDerValue2.isContextSpecific((byte)0)) && (localDerValue2.isConstructed()))
        {
          localDerValue2.resetTag((byte)48);
          fullName = new GeneralNames(localDerValue2);
        }
        else if ((localDerValue2.isContextSpecific((byte)1)) && (localDerValue2.isConstructed()))
        {
          localDerValue2.resetTag((byte)49);
          relativeName = new RDN(localDerValue2);
        }
        else
        {
          throw new IOException("Invalid DistributionPointName in DistributionPoint");
        }
      }
      else if ((localDerValue1.isContextSpecific((byte)1)) && (!localDerValue1.isConstructed()))
      {
        if (reasonFlags != null) {
          throw new IOException("Duplicate Reasons in DistributionPoint.");
        }
        localDerValue1.resetTag((byte)3);
        reasonFlags = localDerValue1.getUnalignedBitString().toBooleanArray();
      }
      else if ((localDerValue1.isContextSpecific((byte)2)) && (localDerValue1.isConstructed()))
      {
        if (crlIssuer != null) {
          throw new IOException("Duplicate CRLIssuer in DistributionPoint.");
        }
        localDerValue1.resetTag((byte)48);
        crlIssuer = new GeneralNames(localDerValue1);
      }
      else
      {
        throw new IOException("Invalid encoding of DistributionPoint.");
      }
    }
    if ((crlIssuer == null) && (fullName == null) && (relativeName == null)) {
      throw new IOException("One of fullName, relativeName,  and crlIssuer has to be set");
    }
  }
  
  public GeneralNames getFullName()
  {
    return fullName;
  }
  
  public RDN getRelativeName()
  {
    return relativeName;
  }
  
  public boolean[] getReasonFlags()
  {
    return reasonFlags;
  }
  
  public GeneralNames getCRLIssuer()
  {
    return crlIssuer;
  }
  
  public void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2;
    Object localObject;
    if ((fullName != null) || (relativeName != null))
    {
      localDerOutputStream2 = new DerOutputStream();
      if (fullName != null)
      {
        localObject = new DerOutputStream();
        fullName.encode((DerOutputStream)localObject);
        localDerOutputStream2.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), (DerOutputStream)localObject);
      }
      else if (relativeName != null)
      {
        localObject = new DerOutputStream();
        relativeName.encode((DerOutputStream)localObject);
        localDerOutputStream2.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), (DerOutputStream)localObject);
      }
      localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream2);
    }
    if (reasonFlags != null)
    {
      localDerOutputStream2 = new DerOutputStream();
      localObject = new BitArray(reasonFlags);
      localDerOutputStream2.putTruncatedUnalignedBitString((BitArray)localObject);
      localDerOutputStream1.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, false, (byte)1), localDerOutputStream2);
    }
    if (crlIssuer != null)
    {
      localDerOutputStream2 = new DerOutputStream();
      crlIssuer.encode(localDerOutputStream2);
      localDerOutputStream1.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), localDerOutputStream2);
    }
    paramDerOutputStream.write((byte)48, localDerOutputStream1);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof DistributionPoint)) {
      return false;
    }
    DistributionPoint localDistributionPoint = (DistributionPoint)paramObject;
    boolean bool = (Objects.equals(fullName, fullName)) && (Objects.equals(relativeName, relativeName)) && (Objects.equals(crlIssuer, crlIssuer)) && (Arrays.equals(reasonFlags, reasonFlags));
    return bool;
  }
  
  public int hashCode()
  {
    int i = hashCode;
    if (i == 0)
    {
      i = 1;
      if (fullName != null) {
        i += fullName.hashCode();
      }
      if (relativeName != null) {
        i += relativeName.hashCode();
      }
      if (crlIssuer != null) {
        i += crlIssuer.hashCode();
      }
      if (reasonFlags != null) {
        for (int j = 0; j < reasonFlags.length; j++) {
          if (reasonFlags[j] != 0) {
            i += j;
          }
        }
      }
      hashCode = i;
    }
    return i;
  }
  
  private static String reasonToString(int paramInt)
  {
    if ((paramInt > 0) && (paramInt < REASON_STRINGS.length)) {
      return REASON_STRINGS[paramInt];
    }
    return "Unknown reason " + paramInt;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (fullName != null) {
      localStringBuilder.append("DistributionPoint:\n     " + fullName + "\n");
    }
    if (relativeName != null) {
      localStringBuilder.append("DistributionPoint:\n     " + relativeName + "\n");
    }
    if (reasonFlags != null)
    {
      localStringBuilder.append("   ReasonFlags:\n");
      for (int i = 0; i < reasonFlags.length; i++) {
        if (reasonFlags[i] != 0) {
          localStringBuilder.append("    " + reasonToString(i) + "\n");
        }
      }
    }
    if (crlIssuer != null) {
      localStringBuilder.append("   CRLIssuer:" + crlIssuer + "\n");
    }
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\DistributionPoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */