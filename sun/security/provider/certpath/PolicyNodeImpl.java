package sun.security.provider.certpath;

import java.security.cert.PolicyNode;
import java.security.cert.PolicyQualifierInfo;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

final class PolicyNodeImpl
  implements PolicyNode
{
  private static final String ANY_POLICY = "2.5.29.32.0";
  private PolicyNodeImpl mParent;
  private HashSet<PolicyNodeImpl> mChildren;
  private String mValidPolicy;
  private HashSet<PolicyQualifierInfo> mQualifierSet;
  private boolean mCriticalityIndicator;
  private HashSet<String> mExpectedPolicySet;
  private boolean mOriginalExpectedPolicySet;
  private int mDepth;
  private boolean isImmutable = false;
  
  PolicyNodeImpl(PolicyNodeImpl paramPolicyNodeImpl, String paramString, Set<PolicyQualifierInfo> paramSet, boolean paramBoolean1, Set<String> paramSet1, boolean paramBoolean2)
  {
    mParent = paramPolicyNodeImpl;
    mChildren = new HashSet();
    if (paramString != null) {
      mValidPolicy = paramString;
    } else {
      mValidPolicy = "";
    }
    if (paramSet != null) {
      mQualifierSet = new HashSet(paramSet);
    } else {
      mQualifierSet = new HashSet();
    }
    mCriticalityIndicator = paramBoolean1;
    if (paramSet1 != null) {
      mExpectedPolicySet = new HashSet(paramSet1);
    } else {
      mExpectedPolicySet = new HashSet();
    }
    mOriginalExpectedPolicySet = (!paramBoolean2);
    if (mParent != null)
    {
      mDepth = (mParent.getDepth() + 1);
      mParent.addChild(this);
    }
    else
    {
      mDepth = 0;
    }
  }
  
  PolicyNodeImpl(PolicyNodeImpl paramPolicyNodeImpl1, PolicyNodeImpl paramPolicyNodeImpl2)
  {
    this(paramPolicyNodeImpl1, mValidPolicy, mQualifierSet, mCriticalityIndicator, mExpectedPolicySet, false);
  }
  
  public PolicyNode getParent()
  {
    return mParent;
  }
  
  public Iterator<PolicyNodeImpl> getChildren()
  {
    return Collections.unmodifiableSet(mChildren).iterator();
  }
  
  public int getDepth()
  {
    return mDepth;
  }
  
  public String getValidPolicy()
  {
    return mValidPolicy;
  }
  
  public Set<PolicyQualifierInfo> getPolicyQualifiers()
  {
    return Collections.unmodifiableSet(mQualifierSet);
  }
  
  public Set<String> getExpectedPolicies()
  {
    return Collections.unmodifiableSet(mExpectedPolicySet);
  }
  
  public boolean isCritical()
  {
    return mCriticalityIndicator;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(asString());
    Iterator localIterator = mChildren.iterator();
    while (localIterator.hasNext())
    {
      PolicyNodeImpl localPolicyNodeImpl = (PolicyNodeImpl)localIterator.next();
      localStringBuilder.append(localPolicyNodeImpl);
    }
    return localStringBuilder.toString();
  }
  
  boolean isImmutable()
  {
    return isImmutable;
  }
  
  void setImmutable()
  {
    if (isImmutable) {
      return;
    }
    Iterator localIterator = mChildren.iterator();
    while (localIterator.hasNext())
    {
      PolicyNodeImpl localPolicyNodeImpl = (PolicyNodeImpl)localIterator.next();
      localPolicyNodeImpl.setImmutable();
    }
    isImmutable = true;
  }
  
  private void addChild(PolicyNodeImpl paramPolicyNodeImpl)
  {
    if (isImmutable) {
      throw new IllegalStateException("PolicyNode is immutable");
    }
    mChildren.add(paramPolicyNodeImpl);
  }
  
  void addExpectedPolicy(String paramString)
  {
    if (isImmutable) {
      throw new IllegalStateException("PolicyNode is immutable");
    }
    if (mOriginalExpectedPolicySet)
    {
      mExpectedPolicySet.clear();
      mOriginalExpectedPolicySet = false;
    }
    mExpectedPolicySet.add(paramString);
  }
  
  void prune(int paramInt)
  {
    if (isImmutable) {
      throw new IllegalStateException("PolicyNode is immutable");
    }
    if (mChildren.size() == 0) {
      return;
    }
    Iterator localIterator = mChildren.iterator();
    while (localIterator.hasNext())
    {
      PolicyNodeImpl localPolicyNodeImpl = (PolicyNodeImpl)localIterator.next();
      localPolicyNodeImpl.prune(paramInt);
      if ((mChildren.size() == 0) && (paramInt > mDepth + 1)) {
        localIterator.remove();
      }
    }
  }
  
  void deleteChild(PolicyNode paramPolicyNode)
  {
    if (isImmutable) {
      throw new IllegalStateException("PolicyNode is immutable");
    }
    mChildren.remove(paramPolicyNode);
  }
  
  PolicyNodeImpl copyTree()
  {
    return copyTree(null);
  }
  
  private PolicyNodeImpl copyTree(PolicyNodeImpl paramPolicyNodeImpl)
  {
    PolicyNodeImpl localPolicyNodeImpl1 = new PolicyNodeImpl(paramPolicyNodeImpl, this);
    Iterator localIterator = mChildren.iterator();
    while (localIterator.hasNext())
    {
      PolicyNodeImpl localPolicyNodeImpl2 = (PolicyNodeImpl)localIterator.next();
      localPolicyNodeImpl2.copyTree(localPolicyNodeImpl1);
    }
    return localPolicyNodeImpl1;
  }
  
  Set<PolicyNodeImpl> getPolicyNodes(int paramInt)
  {
    HashSet localHashSet = new HashSet();
    getPolicyNodes(paramInt, localHashSet);
    return localHashSet;
  }
  
  private void getPolicyNodes(int paramInt, Set<PolicyNodeImpl> paramSet)
  {
    if (mDepth == paramInt)
    {
      paramSet.add(this);
    }
    else
    {
      Iterator localIterator = mChildren.iterator();
      while (localIterator.hasNext())
      {
        PolicyNodeImpl localPolicyNodeImpl = (PolicyNodeImpl)localIterator.next();
        localPolicyNodeImpl.getPolicyNodes(paramInt, paramSet);
      }
    }
  }
  
  Set<PolicyNodeImpl> getPolicyNodesExpected(int paramInt, String paramString, boolean paramBoolean)
  {
    if (paramString.equals("2.5.29.32.0")) {
      return getPolicyNodes(paramInt);
    }
    return getPolicyNodesExpectedHelper(paramInt, paramString, paramBoolean);
  }
  
  private Set<PolicyNodeImpl> getPolicyNodesExpectedHelper(int paramInt, String paramString, boolean paramBoolean)
  {
    HashSet localHashSet = new HashSet();
    if (mDepth < paramInt)
    {
      Iterator localIterator = mChildren.iterator();
      while (localIterator.hasNext())
      {
        PolicyNodeImpl localPolicyNodeImpl = (PolicyNodeImpl)localIterator.next();
        localHashSet.addAll(localPolicyNodeImpl.getPolicyNodesExpectedHelper(paramInt, paramString, paramBoolean));
      }
    }
    else if (paramBoolean)
    {
      if (mExpectedPolicySet.contains("2.5.29.32.0")) {
        localHashSet.add(this);
      }
    }
    else if (mExpectedPolicySet.contains(paramString))
    {
      localHashSet.add(this);
    }
    return localHashSet;
  }
  
  Set<PolicyNodeImpl> getPolicyNodesValid(int paramInt, String paramString)
  {
    HashSet localHashSet = new HashSet();
    if (mDepth < paramInt)
    {
      Iterator localIterator = mChildren.iterator();
      while (localIterator.hasNext())
      {
        PolicyNodeImpl localPolicyNodeImpl = (PolicyNodeImpl)localIterator.next();
        localHashSet.addAll(localPolicyNodeImpl.getPolicyNodesValid(paramInt, paramString));
      }
    }
    else if (mValidPolicy.equals(paramString))
    {
      localHashSet.add(this);
    }
    return localHashSet;
  }
  
  private static String policyToString(String paramString)
  {
    if (paramString.equals("2.5.29.32.0")) {
      return "anyPolicy";
    }
    return paramString;
  }
  
  String asString()
  {
    if (mParent == null) {
      return "anyPolicy  ROOT\n";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    int j = getDepth();
    while (i < j)
    {
      localStringBuilder.append("  ");
      i++;
    }
    localStringBuilder.append(policyToString(getValidPolicy()));
    localStringBuilder.append("  CRIT: ");
    localStringBuilder.append(isCritical());
    localStringBuilder.append("  EP: ");
    Iterator localIterator = getExpectedPolicies().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      localStringBuilder.append(policyToString(str));
      localStringBuilder.append(" ");
    }
    localStringBuilder.append(" (");
    localStringBuilder.append(getDepth());
    localStringBuilder.append(")\n");
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\certpath\PolicyNodeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */