package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Text;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.internal.ws.policy.sourcemodel.ModelNode.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.namespace.QName;

public abstract class PolicyAssertion
{
  private final AssertionData data;
  private AssertionSet parameters;
  private NestedPolicy nestedPolicy;
  
  protected PolicyAssertion()
  {
    data = AssertionData.createAssertionData(null);
    parameters = AssertionSet.createAssertionSet(null);
  }
  
  @Deprecated
  protected PolicyAssertion(AssertionData paramAssertionData, Collection<? extends PolicyAssertion> paramCollection, AssertionSet paramAssertionSet)
  {
    data = paramAssertionData;
    if (paramAssertionSet != null) {
      nestedPolicy = NestedPolicy.createNestedPolicy(paramAssertionSet);
    }
    parameters = AssertionSet.createAssertionSet(paramCollection);
  }
  
  protected PolicyAssertion(AssertionData paramAssertionData, Collection<? extends PolicyAssertion> paramCollection)
  {
    if (paramAssertionData == null) {
      data = AssertionData.createAssertionData(null);
    } else {
      data = paramAssertionData;
    }
    parameters = AssertionSet.createAssertionSet(paramCollection);
  }
  
  public final QName getName()
  {
    return data.getName();
  }
  
  public final String getValue()
  {
    return data.getValue();
  }
  
  public boolean isOptional()
  {
    return data.isOptionalAttributeSet();
  }
  
  public boolean isIgnorable()
  {
    return data.isIgnorableAttributeSet();
  }
  
  public final boolean isPrivate()
  {
    return data.isPrivateAttributeSet();
  }
  
  public final Set<Map.Entry<QName, String>> getAttributesSet()
  {
    return data.getAttributesSet();
  }
  
  public final Map<QName, String> getAttributes()
  {
    return data.getAttributes();
  }
  
  public final String getAttributeValue(QName paramQName)
  {
    return data.getAttributeValue(paramQName);
  }
  
  @Deprecated
  public final boolean hasNestedAssertions()
  {
    return !parameters.isEmpty();
  }
  
  public final boolean hasParameters()
  {
    return !parameters.isEmpty();
  }
  
  @Deprecated
  public final Iterator<PolicyAssertion> getNestedAssertionsIterator()
  {
    return parameters.iterator();
  }
  
  public final Iterator<PolicyAssertion> getParametersIterator()
  {
    return parameters.iterator();
  }
  
  boolean isParameter()
  {
    return data.getNodeType() == ModelNode.Type.ASSERTION_PARAMETER_NODE;
  }
  
  public boolean hasNestedPolicy()
  {
    return getNestedPolicy() != null;
  }
  
  public NestedPolicy getNestedPolicy()
  {
    return nestedPolicy;
  }
  
  public <T extends PolicyAssertion> T getImplementation(Class<T> paramClass)
  {
    if (paramClass.isAssignableFrom(getClass())) {
      return (PolicyAssertion)paramClass.cast(this);
    }
    return null;
  }
  
  public String toString()
  {
    return toString(0, new StringBuffer()).toString();
  }
  
  protected StringBuffer toString(int paramInt, StringBuffer paramStringBuffer)
  {
    String str1 = PolicyUtils.Text.createIndent(paramInt);
    String str2 = PolicyUtils.Text.createIndent(paramInt + 1);
    paramStringBuffer.append(str1).append("Assertion[").append(getClass().getName()).append("] {").append(PolicyUtils.Text.NEW_LINE);
    data.toString(paramInt + 1, paramStringBuffer);
    paramStringBuffer.append(PolicyUtils.Text.NEW_LINE);
    if (hasParameters())
    {
      paramStringBuffer.append(str2).append("parameters {").append(PolicyUtils.Text.NEW_LINE);
      Iterator localIterator = parameters.iterator();
      while (localIterator.hasNext())
      {
        PolicyAssertion localPolicyAssertion = (PolicyAssertion)localIterator.next();
        localPolicyAssertion.toString(paramInt + 2, paramStringBuffer).append(PolicyUtils.Text.NEW_LINE);
      }
      paramStringBuffer.append(str2).append('}').append(PolicyUtils.Text.NEW_LINE);
    }
    else
    {
      paramStringBuffer.append(str2).append("no parameters").append(PolicyUtils.Text.NEW_LINE);
    }
    if (hasNestedPolicy()) {
      getNestedPolicy().toString(paramInt + 1, paramStringBuffer).append(PolicyUtils.Text.NEW_LINE);
    } else {
      paramStringBuffer.append(str2).append("no nested policy").append(PolicyUtils.Text.NEW_LINE);
    }
    paramStringBuffer.append(str1).append('}');
    return paramStringBuffer;
  }
  
  boolean isCompatibleWith(PolicyAssertion paramPolicyAssertion, PolicyIntersector.CompatibilityMode paramCompatibilityMode)
  {
    boolean bool = (data.getName().equals(data.getName())) && (hasNestedPolicy() == paramPolicyAssertion.hasNestedPolicy());
    if ((bool) && (hasNestedPolicy())) {
      bool = getNestedPolicy().getAssertionSet().isCompatibleWith(paramPolicyAssertion.getNestedPolicy().getAssertionSet(), paramCompatibilityMode);
    }
    return bool;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof PolicyAssertion)) {
      return false;
    }
    PolicyAssertion localPolicyAssertion = (PolicyAssertion)paramObject;
    boolean bool = true;
    bool = (bool) && (data.equals(data));
    bool = (bool) && (parameters.equals(parameters));
    bool = (bool) && (getNestedPolicy() == null ? localPolicyAssertion.getNestedPolicy() == null : getNestedPolicy().equals(localPolicyAssertion.getNestedPolicy()));
    return bool;
  }
  
  public int hashCode()
  {
    int i = 17;
    i = 37 * i + data.hashCode();
    i = 37 * i + (hasParameters() ? 17 : 0);
    i = 37 * i + (hasNestedPolicy() ? 17 : 0);
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\PolicyAssertion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */