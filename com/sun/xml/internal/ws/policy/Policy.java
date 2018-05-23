package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Comparison;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Text;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.namespace.QName;

public class Policy
  implements Iterable<AssertionSet>
{
  private static final String POLICY_TOSTRING_NAME = "policy";
  private static final List<AssertionSet> NULL_POLICY_ASSERTION_SETS = Collections.unmodifiableList(new LinkedList());
  private static final List<AssertionSet> EMPTY_POLICY_ASSERTION_SETS = Collections.unmodifiableList(new LinkedList(Arrays.asList(new AssertionSet[] { AssertionSet.emptyAssertionSet() })));
  private static final Set<QName> EMPTY_VOCABULARY = Collections.unmodifiableSet(new TreeSet(PolicyUtils.Comparison.QNAME_COMPARATOR));
  private static final Policy ANONYMOUS_NULL_POLICY = new Policy(null, null, NULL_POLICY_ASSERTION_SETS, EMPTY_VOCABULARY);
  private static final Policy ANONYMOUS_EMPTY_POLICY = new Policy(null, null, EMPTY_POLICY_ASSERTION_SETS, EMPTY_VOCABULARY);
  private String policyId;
  private String name;
  private NamespaceVersion nsVersion;
  private final List<AssertionSet> assertionSets;
  private final Set<QName> vocabulary;
  private final Collection<QName> immutableVocabulary;
  private final String toStringName;
  
  public static Policy createNullPolicy()
  {
    return ANONYMOUS_NULL_POLICY;
  }
  
  public static Policy createEmptyPolicy()
  {
    return ANONYMOUS_EMPTY_POLICY;
  }
  
  public static Policy createNullPolicy(String paramString1, String paramString2)
  {
    if ((paramString1 == null) && (paramString2 == null)) {
      return ANONYMOUS_NULL_POLICY;
    }
    return new Policy(paramString1, paramString2, NULL_POLICY_ASSERTION_SETS, EMPTY_VOCABULARY);
  }
  
  public static Policy createNullPolicy(NamespaceVersion paramNamespaceVersion, String paramString1, String paramString2)
  {
    if (((paramNamespaceVersion == null) || (paramNamespaceVersion == NamespaceVersion.getLatestVersion())) && (paramString1 == null) && (paramString2 == null)) {
      return ANONYMOUS_NULL_POLICY;
    }
    return new Policy(paramNamespaceVersion, paramString1, paramString2, NULL_POLICY_ASSERTION_SETS, EMPTY_VOCABULARY);
  }
  
  public static Policy createEmptyPolicy(String paramString1, String paramString2)
  {
    if ((paramString1 == null) && (paramString2 == null)) {
      return ANONYMOUS_EMPTY_POLICY;
    }
    return new Policy(paramString1, paramString2, EMPTY_POLICY_ASSERTION_SETS, EMPTY_VOCABULARY);
  }
  
  public static Policy createEmptyPolicy(NamespaceVersion paramNamespaceVersion, String paramString1, String paramString2)
  {
    if (((paramNamespaceVersion == null) || (paramNamespaceVersion == NamespaceVersion.getLatestVersion())) && (paramString1 == null) && (paramString2 == null)) {
      return ANONYMOUS_EMPTY_POLICY;
    }
    return new Policy(paramNamespaceVersion, paramString1, paramString2, EMPTY_POLICY_ASSERTION_SETS, EMPTY_VOCABULARY);
  }
  
  public static Policy createPolicy(Collection<AssertionSet> paramCollection)
  {
    if ((paramCollection == null) || (paramCollection.isEmpty())) {
      return createNullPolicy();
    }
    return new Policy("policy", paramCollection);
  }
  
  public static Policy createPolicy(String paramString1, String paramString2, Collection<AssertionSet> paramCollection)
  {
    if ((paramCollection == null) || (paramCollection.isEmpty())) {
      return createNullPolicy(paramString1, paramString2);
    }
    return new Policy("policy", paramString1, paramString2, paramCollection);
  }
  
  public static Policy createPolicy(NamespaceVersion paramNamespaceVersion, String paramString1, String paramString2, Collection<AssertionSet> paramCollection)
  {
    if ((paramCollection == null) || (paramCollection.isEmpty())) {
      return createNullPolicy(paramNamespaceVersion, paramString1, paramString2);
    }
    return new Policy(paramNamespaceVersion, "policy", paramString1, paramString2, paramCollection);
  }
  
  private Policy(String paramString1, String paramString2, List<AssertionSet> paramList, Set<QName> paramSet)
  {
    nsVersion = NamespaceVersion.getLatestVersion();
    toStringName = "policy";
    name = paramString1;
    policyId = paramString2;
    assertionSets = paramList;
    vocabulary = paramSet;
    immutableVocabulary = Collections.unmodifiableCollection(vocabulary);
  }
  
  Policy(String paramString, Collection<AssertionSet> paramCollection)
  {
    nsVersion = NamespaceVersion.getLatestVersion();
    toStringName = paramString;
    if ((paramCollection == null) || (paramCollection.isEmpty()))
    {
      assertionSets = NULL_POLICY_ASSERTION_SETS;
      vocabulary = EMPTY_VOCABULARY;
      immutableVocabulary = EMPTY_VOCABULARY;
    }
    else
    {
      assertionSets = new LinkedList();
      vocabulary = new TreeSet(PolicyUtils.Comparison.QNAME_COMPARATOR);
      immutableVocabulary = Collections.unmodifiableCollection(vocabulary);
      addAll(paramCollection);
    }
  }
  
  Policy(String paramString1, String paramString2, String paramString3, Collection<AssertionSet> paramCollection)
  {
    this(paramString1, paramCollection);
    name = paramString2;
    policyId = paramString3;
  }
  
  private Policy(NamespaceVersion paramNamespaceVersion, String paramString1, String paramString2, List<AssertionSet> paramList, Set<QName> paramSet)
  {
    nsVersion = paramNamespaceVersion;
    toStringName = "policy";
    name = paramString1;
    policyId = paramString2;
    assertionSets = paramList;
    vocabulary = paramSet;
    immutableVocabulary = Collections.unmodifiableCollection(vocabulary);
  }
  
  Policy(NamespaceVersion paramNamespaceVersion, String paramString, Collection<AssertionSet> paramCollection)
  {
    nsVersion = paramNamespaceVersion;
    toStringName = paramString;
    if ((paramCollection == null) || (paramCollection.isEmpty()))
    {
      assertionSets = NULL_POLICY_ASSERTION_SETS;
      vocabulary = EMPTY_VOCABULARY;
      immutableVocabulary = EMPTY_VOCABULARY;
    }
    else
    {
      assertionSets = new LinkedList();
      vocabulary = new TreeSet(PolicyUtils.Comparison.QNAME_COMPARATOR);
      immutableVocabulary = Collections.unmodifiableCollection(vocabulary);
      addAll(paramCollection);
    }
  }
  
  Policy(NamespaceVersion paramNamespaceVersion, String paramString1, String paramString2, String paramString3, Collection<AssertionSet> paramCollection)
  {
    this(paramNamespaceVersion, paramString1, paramCollection);
    name = paramString2;
    policyId = paramString3;
  }
  
  private boolean add(AssertionSet paramAssertionSet)
  {
    if (paramAssertionSet == null) {
      return false;
    }
    if (assertionSets.contains(paramAssertionSet)) {
      return false;
    }
    assertionSets.add(paramAssertionSet);
    vocabulary.addAll(paramAssertionSet.getVocabulary());
    return true;
  }
  
  private boolean addAll(Collection<AssertionSet> paramCollection)
  {
    assert ((paramCollection != null) && (!paramCollection.isEmpty())) : LocalizationMessages.WSP_0036_PRIVATE_METHOD_DOES_NOT_ACCEPT_NULL_OR_EMPTY_COLLECTION();
    boolean bool = true;
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      AssertionSet localAssertionSet = (AssertionSet)localIterator.next();
      bool &= add(localAssertionSet);
    }
    Collections.sort(assertionSets);
    return bool;
  }
  
  Collection<AssertionSet> getContent()
  {
    return assertionSets;
  }
  
  public String getId()
  {
    return policyId;
  }
  
  public String getName()
  {
    return name;
  }
  
  public NamespaceVersion getNamespaceVersion()
  {
    return nsVersion;
  }
  
  public String getIdOrName()
  {
    if (policyId != null) {
      return policyId;
    }
    return name;
  }
  
  public int getNumberOfAssertionSets()
  {
    return assertionSets.size();
  }
  
  public Iterator<AssertionSet> iterator()
  {
    return assertionSets.iterator();
  }
  
  public boolean isNull()
  {
    return assertionSets.size() == 0;
  }
  
  public boolean isEmpty()
  {
    return (assertionSets.size() == 1) && (((AssertionSet)assertionSets.get(0)).isEmpty());
  }
  
  public boolean contains(String paramString)
  {
    Iterator localIterator = vocabulary.iterator();
    while (localIterator.hasNext())
    {
      QName localQName = (QName)localIterator.next();
      if (localQName.getNamespaceURI().equals(paramString)) {
        return true;
      }
    }
    return false;
  }
  
  public Collection<QName> getVocabulary()
  {
    return immutableVocabulary;
  }
  
  public boolean contains(QName paramQName)
  {
    return vocabulary.contains(paramQName);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof Policy)) {
      return false;
    }
    Policy localPolicy = (Policy)paramObject;
    boolean bool = true;
    bool = (bool) && (vocabulary.equals(vocabulary));
    bool = (bool) && (assertionSets.size() == assertionSets.size()) && (assertionSets.containsAll(assertionSets));
    return bool;
  }
  
  public int hashCode()
  {
    int i = 17;
    i = 37 * i + vocabulary.hashCode();
    i = 37 * i + assertionSets.hashCode();
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
    String str3 = PolicyUtils.Text.createIndent(paramInt + 2);
    paramStringBuffer.append(str1).append(toStringName).append(" {").append(PolicyUtils.Text.NEW_LINE);
    paramStringBuffer.append(str2).append("namespace version = '").append(nsVersion.name()).append('\'').append(PolicyUtils.Text.NEW_LINE);
    paramStringBuffer.append(str2).append("id = '").append(policyId).append('\'').append(PolicyUtils.Text.NEW_LINE);
    paramStringBuffer.append(str2).append("name = '").append(name).append('\'').append(PolicyUtils.Text.NEW_LINE);
    paramStringBuffer.append(str2).append("vocabulary {").append(PolicyUtils.Text.NEW_LINE);
    Object localObject;
    if (vocabulary.isEmpty())
    {
      paramStringBuffer.append(str3).append("no entries").append(PolicyUtils.Text.NEW_LINE);
    }
    else
    {
      int i = 1;
      localObject = vocabulary.iterator();
      while (((Iterator)localObject).hasNext())
      {
        QName localQName = (QName)((Iterator)localObject).next();
        paramStringBuffer.append(str3).append(i++).append(". entry = '").append(localQName.getNamespaceURI()).append(':').append(localQName.getLocalPart()).append('\'').append(PolicyUtils.Text.NEW_LINE);
      }
    }
    paramStringBuffer.append(str2).append('}').append(PolicyUtils.Text.NEW_LINE);
    if (assertionSets.isEmpty())
    {
      paramStringBuffer.append(str2).append("no assertion sets").append(PolicyUtils.Text.NEW_LINE);
    }
    else
    {
      Iterator localIterator = assertionSets.iterator();
      while (localIterator.hasNext())
      {
        localObject = (AssertionSet)localIterator.next();
        ((AssertionSet)localObject).toString(paramInt + 1, paramStringBuffer).append(PolicyUtils.Text.NEW_LINE);
      }
    }
    paramStringBuffer.append(str1).append('}');
    return paramStringBuffer;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\Policy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */