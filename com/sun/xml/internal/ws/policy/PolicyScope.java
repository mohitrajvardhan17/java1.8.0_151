package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Text;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

final class PolicyScope
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyScope.class);
  private final List<PolicySubject> subjects = new LinkedList();
  
  PolicyScope(List<PolicySubject> paramList)
  {
    if ((paramList != null) && (!paramList.isEmpty())) {
      subjects.addAll(paramList);
    }
  }
  
  void attach(PolicySubject paramPolicySubject)
  {
    if (paramPolicySubject == null) {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0020_SUBJECT_PARAM_MUST_NOT_BE_NULL())));
    }
    subjects.add(paramPolicySubject);
  }
  
  void dettachAllSubjects()
  {
    subjects.clear();
  }
  
  Policy getEffectivePolicy(PolicyMerger paramPolicyMerger)
    throws PolicyException
  {
    LinkedList localLinkedList = new LinkedList();
    Iterator localIterator = subjects.iterator();
    while (localIterator.hasNext())
    {
      PolicySubject localPolicySubject = (PolicySubject)localIterator.next();
      localLinkedList.add(localPolicySubject.getEffectivePolicy(paramPolicyMerger));
    }
    return paramPolicyMerger.merge(localLinkedList);
  }
  
  Collection<PolicySubject> getPolicySubjects()
  {
    return subjects;
  }
  
  public String toString()
  {
    return toString(0, new StringBuffer()).toString();
  }
  
  StringBuffer toString(int paramInt, StringBuffer paramStringBuffer)
  {
    String str = PolicyUtils.Text.createIndent(paramInt);
    paramStringBuffer.append(str).append("policy scope {").append(PolicyUtils.Text.NEW_LINE);
    Iterator localIterator = subjects.iterator();
    while (localIterator.hasNext())
    {
      PolicySubject localPolicySubject = (PolicySubject)localIterator.next();
      localPolicySubject.toString(paramInt + 1, paramStringBuffer).append(PolicyUtils.Text.NEW_LINE);
    }
    paramStringBuffer.append(str).append('}');
    return paramStringBuffer;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\PolicyScope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */