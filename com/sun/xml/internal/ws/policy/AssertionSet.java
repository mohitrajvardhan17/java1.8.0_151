package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Comparison;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Text;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.namespace.QName;

public final class AssertionSet
  implements Iterable<PolicyAssertion>, Comparable<AssertionSet>
{
  private static final AssertionSet EMPTY_ASSERTION_SET = new AssertionSet(Collections.unmodifiableList(new LinkedList()));
  private static final Comparator<PolicyAssertion> ASSERTION_COMPARATOR = new Comparator()
  {
    public int compare(PolicyAssertion paramAnonymousPolicyAssertion1, PolicyAssertion paramAnonymousPolicyAssertion2)
    {
      if (paramAnonymousPolicyAssertion1.equals(paramAnonymousPolicyAssertion2)) {
        return 0;
      }
      int i = PolicyUtils.Comparison.QNAME_COMPARATOR.compare(paramAnonymousPolicyAssertion1.getName(), paramAnonymousPolicyAssertion2.getName());
      if (i != 0) {
        return i;
      }
      i = PolicyUtils.Comparison.compareNullableStrings(paramAnonymousPolicyAssertion1.getValue(), paramAnonymousPolicyAssertion2.getValue());
      if (i != 0) {
        return i;
      }
      i = PolicyUtils.Comparison.compareBoolean(paramAnonymousPolicyAssertion1.hasNestedAssertions(), paramAnonymousPolicyAssertion2.hasNestedAssertions());
      if (i != 0) {
        return i;
      }
      i = PolicyUtils.Comparison.compareBoolean(paramAnonymousPolicyAssertion1.hasNestedPolicy(), paramAnonymousPolicyAssertion2.hasNestedPolicy());
      if (i != 0) {
        return i;
      }
      return Math.round(Math.signum(paramAnonymousPolicyAssertion1.hashCode() - paramAnonymousPolicyAssertion2.hashCode()));
    }
  };
  private final List<PolicyAssertion> assertions;
  private final Set<QName> vocabulary = new TreeSet(PolicyUtils.Comparison.QNAME_COMPARATOR);
  private final Collection<QName> immutableVocabulary = Collections.unmodifiableCollection(vocabulary);
  
  private AssertionSet(List<PolicyAssertion> paramList)
  {
    assert (paramList != null) : LocalizationMessages.WSP_0037_PRIVATE_CONSTRUCTOR_DOES_NOT_TAKE_NULL();
    assertions = paramList;
  }
  
  private AssertionSet(Collection<AssertionSet> paramCollection)
  {
    assertions = new LinkedList();
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      AssertionSet localAssertionSet = (AssertionSet)localIterator.next();
      addAll(assertions);
    }
  }
  
  private boolean add(PolicyAssertion paramPolicyAssertion)
  {
    if (paramPolicyAssertion == null) {
      return false;
    }
    if (assertions.contains(paramPolicyAssertion)) {
      return false;
    }
    assertions.add(paramPolicyAssertion);
    vocabulary.add(paramPolicyAssertion.getName());
    return true;
  }
  
  private boolean addAll(Collection<? extends PolicyAssertion> paramCollection)
  {
    boolean bool = true;
    if (paramCollection != null)
    {
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        PolicyAssertion localPolicyAssertion = (PolicyAssertion)localIterator.next();
        bool &= add(localPolicyAssertion);
      }
    }
    return bool;
  }
  
  Collection<PolicyAssertion> getAssertions()
  {
    return assertions;
  }
  
  Collection<QName> getVocabulary()
  {
    return immutableVocabulary;
  }
  
  boolean isCompatibleWith(AssertionSet paramAssertionSet, PolicyIntersector.CompatibilityMode paramCompatibilityMode)
  {
    boolean bool = (paramCompatibilityMode == PolicyIntersector.CompatibilityMode.LAX) || (vocabulary.equals(vocabulary));
    bool = (bool) && (areAssertionsCompatible(paramAssertionSet, paramCompatibilityMode));
    bool = (bool) && (paramAssertionSet.areAssertionsCompatible(this, paramCompatibilityMode));
    return bool;
  }
  
  private boolean areAssertionsCompatible(AssertionSet paramAssertionSet, PolicyIntersector.CompatibilityMode paramCompatibilityMode)
  {
    Iterator localIterator1 = assertions.iterator();
    while (localIterator1.hasNext())
    {
      PolicyAssertion localPolicyAssertion1 = (PolicyAssertion)localIterator1.next();
      if ((paramCompatibilityMode == PolicyIntersector.CompatibilityMode.STRICT) || (!localPolicyAssertion1.isIgnorable()))
      {
        Iterator localIterator2 = assertions.iterator();
        for (;;)
        {
          if (!localIterator2.hasNext()) {
            break label95;
          }
          PolicyAssertion localPolicyAssertion2 = (PolicyAssertion)localIterator2.next();
          if (localPolicyAssertion1.isCompatibleWith(localPolicyAssertion2, paramCompatibilityMode)) {
            break;
          }
        }
        label95:
        return false;
      }
    }
    return true;
  }
  
  public static AssertionSet createMergedAssertionSet(Collection<AssertionSet> paramCollection)
  {
    if ((paramCollection == null) || (paramCollection.isEmpty())) {
      return EMPTY_ASSERTION_SET;
    }
    AssertionSet localAssertionSet = new AssertionSet(paramCollection);
    Collections.sort(assertions, ASSERTION_COMPARATOR);
    return localAssertionSet;
  }
  
  public static AssertionSet createAssertionSet(Collection<? extends PolicyAssertion> paramCollection)
  {
    if ((paramCollection == null) || (paramCollection.isEmpty())) {
      return EMPTY_ASSERTION_SET;
    }
    AssertionSet localAssertionSet = new AssertionSet(new LinkedList());
    localAssertionSet.addAll(paramCollection);
    Collections.sort(assertions, ASSERTION_COMPARATOR);
    return localAssertionSet;
  }
  
  public static AssertionSet emptyAssertionSet()
  {
    return EMPTY_ASSERTION_SET;
  }
  
  public Iterator<PolicyAssertion> iterator()
  {
    return assertions.iterator();
  }
  
  public Collection<PolicyAssertion> get(QName paramQName)
  {
    LinkedList localLinkedList = new LinkedList();
    if (vocabulary.contains(paramQName))
    {
      Iterator localIterator = assertions.iterator();
      while (localIterator.hasNext())
      {
        PolicyAssertion localPolicyAssertion = (PolicyAssertion)localIterator.next();
        if (localPolicyAssertion.getName().equals(paramQName)) {
          localLinkedList.add(localPolicyAssertion);
        }
      }
    }
    return localLinkedList;
  }
  
  public boolean isEmpty()
  {
    return assertions.isEmpty();
  }
  
  public boolean contains(QName paramQName)
  {
    return vocabulary.contains(paramQName);
  }
  
  public int compareTo(AssertionSet paramAssertionSet)
  {
    if (equals(paramAssertionSet)) {
      return 0;
    }
    Iterator localIterator1 = getVocabulary().iterator();
    Iterator localIterator2 = paramAssertionSet.getVocabulary().iterator();
    while (localIterator1.hasNext())
    {
      localObject1 = (QName)localIterator1.next();
      if (localIterator2.hasNext())
      {
        localObject2 = (QName)localIterator2.next();
        int i = PolicyUtils.Comparison.QNAME_COMPARATOR.compare(localObject1, localObject2);
        if (i != 0) {
          return i;
        }
      }
      else
      {
        return 1;
      }
    }
    if (localIterator2.hasNext()) {
      return -1;
    }
    Object localObject1 = getAssertions().iterator();
    Object localObject2 = paramAssertionSet.getAssertions().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      PolicyAssertion localPolicyAssertion1 = (PolicyAssertion)((Iterator)localObject1).next();
      if (((Iterator)localObject2).hasNext())
      {
        PolicyAssertion localPolicyAssertion2 = (PolicyAssertion)((Iterator)localObject2).next();
        int j = ASSERTION_COMPARATOR.compare(localPolicyAssertion1, localPolicyAssertion2);
        if (j != 0) {
          return j;
        }
      }
      else
      {
        return 1;
      }
    }
    if (((Iterator)localObject2).hasNext()) {
      return -1;
    }
    return 1;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof AssertionSet)) {
      return false;
    }
    AssertionSet localAssertionSet = (AssertionSet)paramObject;
    boolean bool = true;
    bool = (bool) && (vocabulary.equals(vocabulary));
    bool = (bool) && (assertions.size() == assertions.size()) && (assertions.containsAll(assertions));
    return bool;
  }
  
  public int hashCode()
  {
    int i = 17;
    i = 37 * i + vocabulary.hashCode();
    i = 37 * i + assertions.hashCode();
    return i;
  }
  
  public String toString()
  {
    return toString(0, new StringBuffer()).toString();
  }
  
  StringBuffer toString(int paramInt, StringBuffer paramStringBuffer)
  {
    String str1 = PolicyUtils.Text.createIndent(paramInt);
    String str2 = PolicyUtils.Text.createIndent(paramInt + 1);
    paramStringBuffer.append(str1).append("assertion set {").append(PolicyUtils.Text.NEW_LINE);
    if (assertions.isEmpty())
    {
      paramStringBuffer.append(str2).append("no assertions").append(PolicyUtils.Text.NEW_LINE);
    }
    else
    {
      Iterator localIterator = assertions.iterator();
      while (localIterator.hasNext())
      {
        PolicyAssertion localPolicyAssertion = (PolicyAssertion)localIterator.next();
        localPolicyAssertion.toString(paramInt + 1, paramStringBuffer).append(PolicyUtils.Text.NEW_LINE);
      }
    }
    paramStringBuffer.append(str1).append('}');
    return paramStringBuffer;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\AssertionSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */