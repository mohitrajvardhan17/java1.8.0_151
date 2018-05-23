package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.ServiceProvider;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Text;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.internal.ws.policy.spi.PrefixMapper;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import javax.xml.namespace.QName;

public class PolicySourceModel
  implements Cloneable
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicySourceModel.class);
  private static final Map<String, String> DEFAULT_NAMESPACE_TO_PREFIX = new HashMap();
  private final Map<String, String> namespaceToPrefix = new HashMap(DEFAULT_NAMESPACE_TO_PREFIX);
  private ModelNode rootNode = ModelNode.createRootPolicyNode(this);
  private final String policyId;
  private final String policyName;
  private final NamespaceVersion nsVersion;
  private final List<ModelNode> references = new LinkedList();
  private boolean expanded = false;
  
  public static PolicySourceModel createPolicySourceModel(NamespaceVersion paramNamespaceVersion)
  {
    return new PolicySourceModel(paramNamespaceVersion);
  }
  
  public static PolicySourceModel createPolicySourceModel(NamespaceVersion paramNamespaceVersion, String paramString1, String paramString2)
  {
    return new PolicySourceModel(paramNamespaceVersion, paramString1, paramString2);
  }
  
  private PolicySourceModel(NamespaceVersion paramNamespaceVersion)
  {
    this(paramNamespaceVersion, null, null);
  }
  
  private PolicySourceModel(NamespaceVersion paramNamespaceVersion, String paramString1, String paramString2)
  {
    this(paramNamespaceVersion, paramString1, paramString2, null);
  }
  
  protected PolicySourceModel(NamespaceVersion paramNamespaceVersion, String paramString1, String paramString2, Collection<PrefixMapper> paramCollection)
  {
    nsVersion = paramNamespaceVersion;
    policyId = paramString1;
    policyName = paramString2;
    if (paramCollection != null)
    {
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        PrefixMapper localPrefixMapper = (PrefixMapper)localIterator.next();
        namespaceToPrefix.putAll(localPrefixMapper.getPrefixMap());
      }
    }
  }
  
  public ModelNode getRootNode()
  {
    return rootNode;
  }
  
  public String getPolicyName()
  {
    return policyName;
  }
  
  public String getPolicyId()
  {
    return policyId;
  }
  
  public NamespaceVersion getNamespaceVersion()
  {
    return nsVersion;
  }
  
  Map<String, String> getNamespaceToPrefixMapping()
    throws PolicyException
  {
    HashMap localHashMap = new HashMap();
    Collection localCollection = getUsedNamespaces();
    Iterator localIterator = localCollection.iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      String str2 = getDefaultPrefix(str1);
      if (str2 != null) {
        localHashMap.put(str1, str2);
      }
    }
    return localHashMap;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof PolicySourceModel)) {
      return false;
    }
    boolean bool = true;
    PolicySourceModel localPolicySourceModel = (PolicySourceModel)paramObject;
    bool = (bool) && (policyId == null ? policyId == null : policyId.equals(policyId));
    bool = (bool) && (policyName == null ? policyName == null : policyName.equals(policyName));
    bool = (bool) && (rootNode.equals(rootNode));
    return bool;
  }
  
  public int hashCode()
  {
    int i = 17;
    i = 37 * i + (policyId == null ? 0 : policyId.hashCode());
    i = 37 * i + (policyName == null ? 0 : policyName.hashCode());
    i = 37 * i + rootNode.hashCode();
    return i;
  }
  
  public String toString()
  {
    String str = PolicyUtils.Text.createIndent(1);
    StringBuffer localStringBuffer = new StringBuffer(60);
    localStringBuffer.append("Policy source model {").append(PolicyUtils.Text.NEW_LINE);
    localStringBuffer.append(str).append("policy id = '").append(policyId).append('\'').append(PolicyUtils.Text.NEW_LINE);
    localStringBuffer.append(str).append("policy name = '").append(policyName).append('\'').append(PolicyUtils.Text.NEW_LINE);
    rootNode.toString(1, localStringBuffer).append(PolicyUtils.Text.NEW_LINE).append('}');
    return localStringBuffer.toString();
  }
  
  protected PolicySourceModel clone()
    throws CloneNotSupportedException
  {
    PolicySourceModel localPolicySourceModel = (PolicySourceModel)super.clone();
    rootNode = rootNode.clone();
    try
    {
      rootNode.setParentModel(localPolicySourceModel);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw ((CloneNotSupportedException)LOGGER.logSevereException(new CloneNotSupportedException(LocalizationMessages.WSP_0013_UNABLE_TO_SET_PARENT_MODEL_ON_ROOT()), localIllegalAccessException));
    }
    return localPolicySourceModel;
  }
  
  public boolean containsPolicyReferences()
  {
    return !references.isEmpty();
  }
  
  private boolean isExpanded()
  {
    return (references.isEmpty()) || (expanded);
  }
  
  public synchronized void expand(PolicySourceModelContext paramPolicySourceModelContext)
    throws PolicyException
  {
    if (!isExpanded())
    {
      Iterator localIterator = references.iterator();
      while (localIterator.hasNext())
      {
        ModelNode localModelNode = (ModelNode)localIterator.next();
        PolicyReferenceData localPolicyReferenceData = localModelNode.getPolicyReferenceData();
        String str = localPolicyReferenceData.getDigest();
        PolicySourceModel localPolicySourceModel;
        if (str == null) {
          localPolicySourceModel = paramPolicySourceModelContext.retrieveModel(localPolicyReferenceData.getReferencedModelUri());
        } else {
          localPolicySourceModel = paramPolicySourceModelContext.retrieveModel(localPolicyReferenceData.getReferencedModelUri(), localPolicyReferenceData.getDigestAlgorithmUri(), str);
        }
        localModelNode.setReferencedModel(localPolicySourceModel);
      }
      expanded = true;
    }
  }
  
  void addNewPolicyReference(ModelNode paramModelNode)
  {
    if (paramModelNode.getType() != ModelNode.Type.POLICY_REFERENCE) {
      throw new IllegalArgumentException(LocalizationMessages.WSP_0042_POLICY_REFERENCE_NODE_EXPECTED_INSTEAD_OF(paramModelNode.getType()));
    }
    references.add(paramModelNode);
  }
  
  private Collection<String> getUsedNamespaces()
    throws PolicyException
  {
    HashSet localHashSet = new HashSet();
    localHashSet.add(getNamespaceVersion().toString());
    if (policyId != null) {
      localHashSet.add("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
    }
    LinkedList localLinkedList = new LinkedList();
    localLinkedList.add(rootNode);
    ModelNode localModelNode1;
    while ((localModelNode1 = (ModelNode)localLinkedList.poll()) != null)
    {
      Iterator localIterator1 = localModelNode1.getChildren().iterator();
      while (localIterator1.hasNext())
      {
        ModelNode localModelNode2 = (ModelNode)localIterator1.next();
        if ((localModelNode2.hasChildren()) && (!localLinkedList.offer(localModelNode2))) {
          throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0081_UNABLE_TO_INSERT_CHILD(localLinkedList, localModelNode2))));
        }
        if (localModelNode2.isDomainSpecific())
        {
          AssertionData localAssertionData = localModelNode2.getNodeData();
          localHashSet.add(localAssertionData.getName().getNamespaceURI());
          if (localAssertionData.isPrivateAttributeSet()) {
            localHashSet.add("http://java.sun.com/xml/ns/wsit/policy");
          }
          Iterator localIterator2 = localAssertionData.getAttributesSet().iterator();
          while (localIterator2.hasNext())
          {
            Map.Entry localEntry = (Map.Entry)localIterator2.next();
            localHashSet.add(((QName)localEntry.getKey()).getNamespaceURI());
          }
        }
      }
    }
    return localHashSet;
  }
  
  private String getDefaultPrefix(String paramString)
  {
    return (String)namespaceToPrefix.get(paramString);
  }
  
  static
  {
    PrefixMapper[] arrayOfPrefixMapper = (PrefixMapper[])PolicyUtils.ServiceProvider.load(PrefixMapper.class);
    Object localObject2;
    if (arrayOfPrefixMapper != null) {
      for (localObject2 : arrayOfPrefixMapper) {
        DEFAULT_NAMESPACE_TO_PREFIX.putAll(((PrefixMapper)localObject2).getPrefixMap());
      }
    }
    for (localObject2 : NamespaceVersion.values()) {
      DEFAULT_NAMESPACE_TO_PREFIX.put(((NamespaceVersion)localObject2).toString(), ((NamespaceVersion)localObject2).getDefaultNamespacePrefix());
    }
    DEFAULT_NAMESPACE_TO_PREFIX.put("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "wsu");
    DEFAULT_NAMESPACE_TO_PREFIX.put("http://java.sun.com/xml/ns/wsit/policy", "sunwsp");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\PolicySourceModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */