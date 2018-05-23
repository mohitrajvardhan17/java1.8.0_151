package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.spi.extension.CopyObjectPolicy;
import com.sun.corba.se.spi.extension.ServantCachingPolicy;
import com.sun.corba.se.spi.extension.ZeroPortPolicy;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.IdAssignmentPolicy;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.IdUniquenessPolicy;
import org.omg.PortableServer.IdUniquenessPolicyValue;
import org.omg.PortableServer.ImplicitActivationPolicy;
import org.omg.PortableServer.ImplicitActivationPolicyValue;
import org.omg.PortableServer.LifespanPolicy;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POAPackage.InvalidPolicy;
import org.omg.PortableServer.RequestProcessingPolicy;
import org.omg.PortableServer.RequestProcessingPolicyValue;
import org.omg.PortableServer.ServantRetentionPolicy;
import org.omg.PortableServer.ServantRetentionPolicyValue;
import org.omg.PortableServer.ThreadPolicy;
import org.omg.PortableServer.ThreadPolicyValue;

public final class Policies
{
  private static final int MIN_POA_POLICY_ID = 16;
  private static final int MAX_POA_POLICY_ID = 22;
  private static final int POLICY_TABLE_SIZE = 7;
  int defaultObjectCopierFactoryId;
  private HashMap policyMap = new HashMap();
  public static final Policies defaultPolicies = new Policies();
  public static final Policies rootPOAPolicies = new Policies(0, 0, 0, 1, 0, 0, 0);
  private int[] poaPolicyValues;
  
  private int getPolicyValue(int paramInt)
  {
    return poaPolicyValues[(paramInt - 16)];
  }
  
  private void setPolicyValue(int paramInt1, int paramInt2)
  {
    poaPolicyValues[(paramInt1 - 16)] = paramInt2;
  }
  
  private Policies(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
  {
    poaPolicyValues = new int[] { paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7 };
  }
  
  private Policies()
  {
    this(0, 0, 0, 1, 1, 0, 0);
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("Policies[");
    int i = 1;
    Iterator localIterator = policyMap.values().iterator();
    while (localIterator.hasNext())
    {
      if (i != 0) {
        i = 0;
      } else {
        localStringBuffer.append(",");
      }
      localStringBuffer.append(localIterator.next().toString());
    }
    localStringBuffer.append("]");
    return localStringBuffer.toString();
  }
  
  private int getPOAPolicyValue(Policy paramPolicy)
  {
    if ((paramPolicy instanceof ThreadPolicy)) {
      return ((ThreadPolicy)paramPolicy).value().value();
    }
    if ((paramPolicy instanceof LifespanPolicy)) {
      return ((LifespanPolicy)paramPolicy).value().value();
    }
    if ((paramPolicy instanceof IdUniquenessPolicy)) {
      return ((IdUniquenessPolicy)paramPolicy).value().value();
    }
    if ((paramPolicy instanceof IdAssignmentPolicy)) {
      return ((IdAssignmentPolicy)paramPolicy).value().value();
    }
    if ((paramPolicy instanceof ServantRetentionPolicy)) {
      return ((ServantRetentionPolicy)paramPolicy).value().value();
    }
    if ((paramPolicy instanceof RequestProcessingPolicy)) {
      return ((RequestProcessingPolicy)paramPolicy).value().value();
    }
    if ((paramPolicy instanceof ImplicitActivationPolicy)) {
      return ((ImplicitActivationPolicy)paramPolicy).value().value();
    }
    return -1;
  }
  
  private void checkForPolicyError(BitSet paramBitSet)
    throws InvalidPolicy
  {
    int j;
    for (int i = 0; i < paramBitSet.length(); j = (short)(i + 1)) {
      if (paramBitSet.get(i)) {
        throw new InvalidPolicy(i);
      }
    }
  }
  
  private void addToErrorSet(Policy[] paramArrayOfPolicy, int paramInt, BitSet paramBitSet)
  {
    for (int i = 0; i < paramArrayOfPolicy.length; i++) {
      if (paramArrayOfPolicy[i].policy_type() == paramInt)
      {
        paramBitSet.set(i);
        return;
      }
    }
  }
  
  Policies(Policy[] paramArrayOfPolicy, int paramInt)
    throws InvalidPolicy
  {
    this();
    defaultObjectCopierFactoryId = paramInt;
    if (paramArrayOfPolicy == null) {
      return;
    }
    BitSet localBitSet = new BitSet(paramArrayOfPolicy.length);
    for (int i = 0; i < paramArrayOfPolicy.length; i = (short)(i + 1))
    {
      Policy localPolicy1 = paramArrayOfPolicy[i];
      int j = getPOAPolicyValue(localPolicy1);
      Integer localInteger = new Integer(localPolicy1.policy_type());
      Policy localPolicy2 = (Policy)policyMap.get(localInteger);
      if (localPolicy2 == null) {
        policyMap.put(localInteger, localPolicy1);
      }
      if (j >= 0)
      {
        setPolicyValue(localInteger.intValue(), j);
        if ((localPolicy2 != null) && (getPOAPolicyValue(localPolicy2) != j)) {
          localBitSet.set(i);
        }
      }
    }
    if ((!retainServants()) && (useActiveMapOnly()))
    {
      addToErrorSet(paramArrayOfPolicy, 21, localBitSet);
      addToErrorSet(paramArrayOfPolicy, 22, localBitSet);
    }
    if (isImplicitlyActivated())
    {
      if (!retainServants())
      {
        addToErrorSet(paramArrayOfPolicy, 20, localBitSet);
        addToErrorSet(paramArrayOfPolicy, 21, localBitSet);
      }
      if (!isSystemAssignedIds())
      {
        addToErrorSet(paramArrayOfPolicy, 20, localBitSet);
        addToErrorSet(paramArrayOfPolicy, 19, localBitSet);
      }
    }
    checkForPolicyError(localBitSet);
  }
  
  public Policy get_effective_policy(int paramInt)
  {
    Integer localInteger = new Integer(paramInt);
    Policy localPolicy = (Policy)policyMap.get(localInteger);
    return localPolicy;
  }
  
  public final boolean isOrbControlledThreads()
  {
    return getPolicyValue(16) == 0;
  }
  
  public final boolean isSingleThreaded()
  {
    return getPolicyValue(16) == 1;
  }
  
  public final boolean isTransient()
  {
    return getPolicyValue(17) == 0;
  }
  
  public final boolean isPersistent()
  {
    return getPolicyValue(17) == 1;
  }
  
  public final boolean isUniqueIds()
  {
    return getPolicyValue(18) == 0;
  }
  
  public final boolean isMultipleIds()
  {
    return getPolicyValue(18) == 1;
  }
  
  public final boolean isUserAssignedIds()
  {
    return getPolicyValue(19) == 0;
  }
  
  public final boolean isSystemAssignedIds()
  {
    return getPolicyValue(19) == 1;
  }
  
  public final boolean retainServants()
  {
    return getPolicyValue(21) == 0;
  }
  
  public final boolean useActiveMapOnly()
  {
    return getPolicyValue(22) == 0;
  }
  
  public final boolean useDefaultServant()
  {
    return getPolicyValue(22) == 1;
  }
  
  public final boolean useServantManager()
  {
    return getPolicyValue(22) == 2;
  }
  
  public final boolean isImplicitlyActivated()
  {
    return getPolicyValue(20) == 0;
  }
  
  public final int servantCachingLevel()
  {
    Integer localInteger = new Integer(1398079488);
    ServantCachingPolicy localServantCachingPolicy = (ServantCachingPolicy)policyMap.get(localInteger);
    if (localServantCachingPolicy == null) {
      return 0;
    }
    return localServantCachingPolicy.getType();
  }
  
  public final boolean forceZeroPort()
  {
    Integer localInteger = new Integer(1398079489);
    ZeroPortPolicy localZeroPortPolicy = (ZeroPortPolicy)policyMap.get(localInteger);
    if (localZeroPortPolicy == null) {
      return false;
    }
    return localZeroPortPolicy.forceZeroPort();
  }
  
  public final int getCopierId()
  {
    Integer localInteger = new Integer(1398079490);
    CopyObjectPolicy localCopyObjectPolicy = (CopyObjectPolicy)policyMap.get(localInteger);
    if (localCopyObjectPolicy != null) {
      return localCopyObjectPolicy.getValue();
    }
    return defaultObjectCopierFactoryId;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\oa\poa\Policies.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */