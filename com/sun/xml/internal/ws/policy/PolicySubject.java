package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Text;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class PolicySubject
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicySubject.class);
  private final List<Policy> policies = new LinkedList();
  private final Object subject;
  
  public PolicySubject(Object paramObject, Policy paramPolicy)
    throws IllegalArgumentException
  {
    if ((paramObject == null) || (paramPolicy == null)) {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0021_SUBJECT_AND_POLICY_PARAM_MUST_NOT_BE_NULL(paramObject, paramPolicy))));
    }
    subject = paramObject;
    attach(paramPolicy);
  }
  
  public PolicySubject(Object paramObject, Collection<Policy> paramCollection)
    throws IllegalArgumentException
  {
    if ((paramObject == null) || (paramCollection == null)) {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0062_INPUT_PARAMS_MUST_NOT_BE_NULL())));
    }
    if (paramCollection.isEmpty()) {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0064_INITIAL_POLICY_COLLECTION_MUST_NOT_BE_EMPTY())));
    }
    subject = paramObject;
    policies.addAll(paramCollection);
  }
  
  public void attach(Policy paramPolicy)
  {
    if (paramPolicy == null) {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0038_POLICY_TO_ATTACH_MUST_NOT_BE_NULL())));
    }
    policies.add(paramPolicy);
  }
  
  public Policy getEffectivePolicy(PolicyMerger paramPolicyMerger)
    throws PolicyException
  {
    return paramPolicyMerger.merge(policies);
  }
  
  public Object getSubject()
  {
    return subject;
  }
  
  public String toString()
  {
    return toString(0, new StringBuffer()).toString();
  }
  
  StringBuffer toString(int paramInt, StringBuffer paramStringBuffer)
  {
    String str1 = PolicyUtils.Text.createIndent(paramInt);
    String str2 = PolicyUtils.Text.createIndent(paramInt + 1);
    paramStringBuffer.append(str1).append("policy subject {").append(PolicyUtils.Text.NEW_LINE);
    paramStringBuffer.append(str2).append("subject = '").append(subject).append('\'').append(PolicyUtils.Text.NEW_LINE);
    Iterator localIterator = policies.iterator();
    while (localIterator.hasNext())
    {
      Policy localPolicy = (Policy)localIterator.next();
      localPolicy.toString(paramInt + 1, paramStringBuffer).append(PolicyUtils.Text.NEW_LINE);
    }
    paramStringBuffer.append(str1).append('}');
    return paramStringBuffer;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\PolicySubject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */