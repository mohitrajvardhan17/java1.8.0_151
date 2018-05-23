package sun.security.x509;

import java.io.IOException;
import java.util.Objects;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class DistributionPointName
{
  private static final byte TAG_FULL_NAME = 0;
  private static final byte TAG_RELATIVE_NAME = 1;
  private GeneralNames fullName = null;
  private RDN relativeName = null;
  private volatile int hashCode;
  
  public DistributionPointName(GeneralNames paramGeneralNames)
  {
    if (paramGeneralNames == null) {
      throw new IllegalArgumentException("fullName must not be null");
    }
    fullName = paramGeneralNames;
  }
  
  public DistributionPointName(RDN paramRDN)
  {
    if (paramRDN == null) {
      throw new IllegalArgumentException("relativeName must not be null");
    }
    relativeName = paramRDN;
  }
  
  public DistributionPointName(DerValue paramDerValue)
    throws IOException
  {
    if ((paramDerValue.isContextSpecific((byte)0)) && (paramDerValue.isConstructed()))
    {
      paramDerValue.resetTag((byte)48);
      fullName = new GeneralNames(paramDerValue);
    }
    else if ((paramDerValue.isContextSpecific((byte)1)) && (paramDerValue.isConstructed()))
    {
      paramDerValue.resetTag((byte)49);
      relativeName = new RDN(paramDerValue);
    }
    else
    {
      throw new IOException("Invalid encoding for DistributionPointName");
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
  
  public void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    if (fullName != null)
    {
      fullName.encode(localDerOutputStream);
      paramDerOutputStream.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream);
    }
    else
    {
      relativeName.encode(localDerOutputStream);
      paramDerOutputStream.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream);
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof DistributionPointName)) {
      return false;
    }
    DistributionPointName localDistributionPointName = (DistributionPointName)paramObject;
    return (Objects.equals(fullName, fullName)) && (Objects.equals(relativeName, relativeName));
  }
  
  public int hashCode()
  {
    int i = hashCode;
    if (i == 0)
    {
      i = 1;
      if (fullName != null) {
        i += fullName.hashCode();
      } else {
        i += relativeName.hashCode();
      }
      hashCode = i;
    }
    return i;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (fullName != null) {
      localStringBuilder.append("DistributionPointName:\n     " + fullName + "\n");
    } else {
      localStringBuilder.append("DistributionPointName:\n     " + relativeName + "\n");
    }
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\DistributionPointName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */