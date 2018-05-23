package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.ServiceProvider;
import com.sun.xml.internal.ws.policy.spi.AssertionCreationException;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionCreator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import javax.xml.namespace.QName;

public class PolicyModelTranslator
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyModelTranslator.class);
  private static final PolicyAssertionCreator defaultCreator = new DefaultPolicyAssertionCreator();
  private final Map<String, PolicyAssertionCreator> assertionCreators;
  
  private PolicyModelTranslator()
    throws PolicyException
  {
    this(null);
  }
  
  protected PolicyModelTranslator(Collection<PolicyAssertionCreator> paramCollection)
    throws PolicyException
  {
    LOGGER.entering(new Object[] { paramCollection });
    LinkedList localLinkedList = new LinkedList();
    PolicyAssertionCreator[] arrayOfPolicyAssertionCreator = (PolicyAssertionCreator[])PolicyUtils.ServiceProvider.load(PolicyAssertionCreator.class);
    Object localObject3;
    for (localObject3 : arrayOfPolicyAssertionCreator) {
      localLinkedList.add(localObject3);
    }
    if (paramCollection != null)
    {
      ??? = paramCollection.iterator();
      while (((Iterator)???).hasNext())
      {
        localObject2 = (PolicyAssertionCreator)((Iterator)???).next();
        localLinkedList.add(localObject2);
      }
    }
    ??? = new HashMap();
    Object localObject2 = localLinkedList.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      PolicyAssertionCreator localPolicyAssertionCreator1 = (PolicyAssertionCreator)((Iterator)localObject2).next();
      localObject3 = localPolicyAssertionCreator1.getSupportedDomainNamespaceURIs();
      String str = localPolicyAssertionCreator1.getClass().getName();
      if ((localObject3 == null) || (localObject3.length == 0)) {
        LOGGER.warning(LocalizationMessages.WSP_0077_ASSERTION_CREATOR_DOES_NOT_SUPPORT_ANY_URI(str));
      } else {
        for (Object localObject5 : localObject3)
        {
          LOGGER.config(LocalizationMessages.WSP_0078_ASSERTION_CREATOR_DISCOVERED(str, localObject5));
          if ((localObject5 == null) || (((String)localObject5).length() == 0)) {
            throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0070_ERROR_REGISTERING_ASSERTION_CREATOR(str))));
          }
          PolicyAssertionCreator localPolicyAssertionCreator2 = (PolicyAssertionCreator)((Map)???).put(localObject5, localPolicyAssertionCreator1);
          if (localPolicyAssertionCreator2 != null) {
            throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0071_ERROR_MULTIPLE_ASSERTION_CREATORS_FOR_NAMESPACE(localObject5, localPolicyAssertionCreator2.getClass().getName(), localPolicyAssertionCreator1.getClass().getName()))));
          }
        }
      }
    }
    assertionCreators = Collections.unmodifiableMap((Map)???);
    LOGGER.exiting();
  }
  
  public static PolicyModelTranslator getTranslator()
    throws PolicyException
  {
    return new PolicyModelTranslator();
  }
  
  public Policy translate(PolicySourceModel paramPolicySourceModel)
    throws PolicyException
  {
    LOGGER.entering(new Object[] { paramPolicySourceModel });
    if (paramPolicySourceModel == null) {
      throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0043_POLICY_MODEL_TRANSLATION_ERROR_INPUT_PARAM_NULL())));
    }
    PolicySourceModel localPolicySourceModel;
    try
    {
      localPolicySourceModel = paramPolicySourceModel.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0016_UNABLE_TO_CLONE_POLICY_SOURCE_MODEL(), localCloneNotSupportedException)));
    }
    String str1 = localPolicySourceModel.getPolicyId();
    String str2 = localPolicySourceModel.getPolicyName();
    Collection localCollection = createPolicyAlternatives(localPolicySourceModel);
    LOGGER.finest(LocalizationMessages.WSP_0052_NUMBER_OF_ALTERNATIVE_COMBINATIONS_CREATED(Integer.valueOf(localCollection.size())));
    Policy localPolicy = null;
    if (localCollection.size() == 0)
    {
      localPolicy = Policy.createNullPolicy(paramPolicySourceModel.getNamespaceVersion(), str2, str1);
      LOGGER.finest(LocalizationMessages.WSP_0055_NO_ALTERNATIVE_COMBINATIONS_CREATED());
    }
    else if ((localCollection.size() == 1) && (((AssertionSet)localCollection.iterator().next()).isEmpty()))
    {
      localPolicy = Policy.createEmptyPolicy(paramPolicySourceModel.getNamespaceVersion(), str2, str1);
      LOGGER.finest(LocalizationMessages.WSP_0026_SINGLE_EMPTY_ALTERNATIVE_COMBINATION_CREATED());
    }
    else
    {
      localPolicy = Policy.createPolicy(paramPolicySourceModel.getNamespaceVersion(), str2, str1, localCollection);
      LOGGER.finest(LocalizationMessages.WSP_0057_N_ALTERNATIVE_COMBINATIONS_M_POLICY_ALTERNATIVES_CREATED(Integer.valueOf(localCollection.size()), Integer.valueOf(localPolicy.getNumberOfAssertionSets())));
    }
    LOGGER.exiting(localPolicy);
    return localPolicy;
  }
  
  private Collection<AssertionSet> createPolicyAlternatives(PolicySourceModel paramPolicySourceModel)
    throws PolicyException
  {
    ContentDecomposition localContentDecomposition = new ContentDecomposition(null);
    LinkedList localLinkedList1 = new LinkedList();
    LinkedList localLinkedList2 = new LinkedList();
    RawPolicy localRawPolicy1 = new RawPolicy(paramPolicySourceModel.getRootNode(), new LinkedList());
    RawPolicy localRawPolicy2 = localRawPolicy1;
    do
    {
      localObject1 = originalContent;
      do
      {
        decompose((Collection)localObject1, localContentDecomposition);
        if (exactlyOneContents.isEmpty())
        {
          localObject2 = new RawAlternative(assertions);
          alternatives.add(localObject2);
          if (!allNestedPolicies.isEmpty()) {
            localLinkedList1.addAll(allNestedPolicies);
          }
        }
        else
        {
          localObject2 = PolicyUtils.Collections.combine(assertions, exactlyOneContents, false);
          if ((localObject2 != null) && (!((Collection)localObject2).isEmpty())) {
            localLinkedList2.addAll((Collection)localObject2);
          }
        }
      } while ((localObject1 = (Collection)localLinkedList2.poll()) != null);
    } while ((localRawPolicy2 = (RawPolicy)localLinkedList1.poll()) != null);
    Object localObject1 = new LinkedList();
    Object localObject2 = alternatives.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      RawAlternative localRawAlternative = (RawAlternative)((Iterator)localObject2).next();
      List localList = normalizeRawAlternative(localRawAlternative);
      ((Collection)localObject1).addAll(localList);
    }
    return (Collection<AssertionSet>)localObject1;
  }
  
  private void decompose(Collection<ModelNode> paramCollection, ContentDecomposition paramContentDecomposition)
    throws PolicyException
  {
    paramContentDecomposition.reset();
    LinkedList localLinkedList = new LinkedList(paramCollection);
    ModelNode localModelNode;
    while ((localModelNode = (ModelNode)localLinkedList.poll()) != null) {
      switch (localModelNode.getType())
      {
      case POLICY: 
      case ALL: 
        localLinkedList.addAll(localModelNode.getChildren());
        break;
      case POLICY_REFERENCE: 
        localLinkedList.addAll(getReferencedModelRootNode(localModelNode).getChildren());
        break;
      case EXACTLY_ONE: 
        exactlyOneContents.add(expandsExactlyOneContent(localModelNode.getChildren()));
        break;
      case ASSERTION: 
        assertions.add(localModelNode);
        break;
      default: 
        throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0007_UNEXPECTED_MODEL_NODE_TYPE_FOUND(localModelNode.getType()))));
      }
    }
  }
  
  private static ModelNode getReferencedModelRootNode(ModelNode paramModelNode)
    throws PolicyException
  {
    PolicySourceModel localPolicySourceModel = paramModelNode.getReferencedModel();
    if (localPolicySourceModel == null)
    {
      PolicyReferenceData localPolicyReferenceData = paramModelNode.getPolicyReferenceData();
      if (localPolicyReferenceData == null) {
        throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0041_POLICY_REFERENCE_NODE_FOUND_WITH_NO_POLICY_REFERENCE_IN_IT())));
      }
      throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0010_UNEXPANDED_POLICY_REFERENCE_NODE_FOUND_REFERENCING(localPolicyReferenceData.getReferencedModelUri()))));
    }
    return localPolicySourceModel.getRootNode();
  }
  
  private Collection<ModelNode> expandsExactlyOneContent(Collection<ModelNode> paramCollection)
    throws PolicyException
  {
    LinkedList localLinkedList1 = new LinkedList();
    LinkedList localLinkedList2 = new LinkedList(paramCollection);
    ModelNode localModelNode;
    while ((localModelNode = (ModelNode)localLinkedList2.poll()) != null) {
      switch (localModelNode.getType())
      {
      case POLICY: 
      case ALL: 
      case ASSERTION: 
        localLinkedList1.add(localModelNode);
        break;
      case POLICY_REFERENCE: 
        localLinkedList1.add(getReferencedModelRootNode(localModelNode));
        break;
      case EXACTLY_ONE: 
        localLinkedList2.addAll(localModelNode.getChildren());
        break;
      default: 
        throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0001_UNSUPPORTED_MODEL_NODE_TYPE(localModelNode.getType()))));
      }
    }
    return localLinkedList1;
  }
  
  private List<AssertionSet> normalizeRawAlternative(RawAlternative paramRawAlternative)
    throws AssertionCreationException, PolicyException
  {
    LinkedList localLinkedList1 = new LinkedList();
    LinkedList localLinkedList2 = new LinkedList();
    Object localObject1;
    Object localObject2;
    if (!nestedAssertions.isEmpty())
    {
      localLinkedList3 = new LinkedList(nestedAssertions);
      while ((localObject1 = (RawAssertion)localLinkedList3.poll()) != null)
      {
        localObject2 = normalizeRawAssertion((RawAssertion)localObject1);
        if (((List)localObject2).size() == 1) {
          localLinkedList1.addAll((Collection)localObject2);
        } else {
          localLinkedList2.add(localObject2);
        }
      }
    }
    LinkedList localLinkedList3 = new LinkedList();
    if (localLinkedList2.isEmpty())
    {
      localLinkedList3.add(AssertionSet.createAssertionSet(localLinkedList1));
    }
    else
    {
      localObject1 = PolicyUtils.Collections.combine(localLinkedList1, localLinkedList2, true);
      localObject2 = ((Collection)localObject1).iterator();
      while (((Iterator)localObject2).hasNext())
      {
        Collection localCollection = (Collection)((Iterator)localObject2).next();
        localLinkedList3.add(AssertionSet.createAssertionSet(localCollection));
      }
    }
    return localLinkedList3;
  }
  
  private List<PolicyAssertion> normalizeRawAssertion(RawAssertion paramRawAssertion)
    throws AssertionCreationException, PolicyException
  {
    ArrayList localArrayList;
    if (parameters.isEmpty())
    {
      localArrayList = null;
    }
    else
    {
      localArrayList = new ArrayList(parameters.size());
      localObject1 = parameters.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (ModelNode)((Iterator)localObject1).next();
        localArrayList.add(createPolicyAssertionParameter((ModelNode)localObject2));
      }
    }
    Object localObject1 = new LinkedList();
    if ((nestedAlternatives != null) && (!nestedAlternatives.isEmpty()))
    {
      localObject2 = new LinkedList(nestedAlternatives);
      RawAlternative localRawAlternative;
      while ((localRawAlternative = (RawAlternative)((Queue)localObject2).poll()) != null) {
        ((List)localObject1).addAll(normalizeRawAlternative(localRawAlternative));
      }
    }
    Object localObject2 = new LinkedList();
    int i = !((List)localObject1).isEmpty() ? 1 : 0;
    if (i != 0)
    {
      Iterator localIterator = ((List)localObject1).iterator();
      while (localIterator.hasNext())
      {
        AssertionSet localAssertionSet = (AssertionSet)localIterator.next();
        ((List)localObject2).add(createPolicyAssertion(originalNode.getNodeData(), localArrayList, localAssertionSet));
      }
    }
    else
    {
      ((List)localObject2).add(createPolicyAssertion(originalNode.getNodeData(), localArrayList, null));
    }
    return (List<PolicyAssertion>)localObject2;
  }
  
  private PolicyAssertion createPolicyAssertionParameter(ModelNode paramModelNode)
    throws AssertionCreationException, PolicyException
  {
    if (paramModelNode.getType() != ModelNode.Type.ASSERTION_PARAMETER_NODE) {
      throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0065_INCONSISTENCY_IN_POLICY_SOURCE_MODEL(paramModelNode.getType()))));
    }
    ArrayList localArrayList = null;
    if (paramModelNode.hasChildren())
    {
      localArrayList = new ArrayList(paramModelNode.childrenSize());
      Iterator localIterator = paramModelNode.iterator();
      while (localIterator.hasNext())
      {
        ModelNode localModelNode = (ModelNode)localIterator.next();
        localArrayList.add(createPolicyAssertionParameter(localModelNode));
      }
    }
    return createPolicyAssertion(paramModelNode.getNodeData(), localArrayList, null);
  }
  
  private PolicyAssertion createPolicyAssertion(AssertionData paramAssertionData, Collection<PolicyAssertion> paramCollection, AssertionSet paramAssertionSet)
    throws AssertionCreationException
  {
    String str = paramAssertionData.getName().getNamespaceURI();
    PolicyAssertionCreator localPolicyAssertionCreator = (PolicyAssertionCreator)assertionCreators.get(str);
    if (localPolicyAssertionCreator == null) {
      return defaultCreator.createAssertion(paramAssertionData, paramCollection, paramAssertionSet, null);
    }
    return localPolicyAssertionCreator.createAssertion(paramAssertionData, paramCollection, paramAssertionSet, defaultCreator);
  }
  
  private static final class ContentDecomposition
  {
    final List<Collection<ModelNode>> exactlyOneContents = new LinkedList();
    final List<ModelNode> assertions = new LinkedList();
    
    private ContentDecomposition() {}
    
    void reset()
    {
      exactlyOneContents.clear();
      assertions.clear();
    }
  }
  
  private static final class RawAlternative
  {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(RawAlternative.class);
    final List<PolicyModelTranslator.RawPolicy> allNestedPolicies = new LinkedList();
    final Collection<PolicyModelTranslator.RawAssertion> nestedAssertions = new LinkedList();
    
    RawAlternative(Collection<ModelNode> paramCollection)
      throws PolicyException
    {
      Iterator localIterator1 = paramCollection.iterator();
      while (localIterator1.hasNext())
      {
        ModelNode localModelNode1 = (ModelNode)localIterator1.next();
        PolicyModelTranslator.RawAssertion localRawAssertion = new PolicyModelTranslator.RawAssertion(localModelNode1, new LinkedList());
        nestedAssertions.add(localRawAssertion);
        Iterator localIterator2 = originalNode.getChildren().iterator();
        while (localIterator2.hasNext())
        {
          ModelNode localModelNode2 = (ModelNode)localIterator2.next();
          switch (PolicyModelTranslator.1.$SwitchMap$com$sun$xml$internal$ws$policy$sourcemodel$ModelNode$Type[localModelNode2.getType().ordinal()])
          {
          case 1: 
            parameters.add(localModelNode2);
            break;
          case 2: 
          case 3: 
            if (nestedAlternatives == null)
            {
              nestedAlternatives = new LinkedList();
              PolicyModelTranslator.RawPolicy localRawPolicy;
              if (localModelNode2.getType() == ModelNode.Type.POLICY) {
                localRawPolicy = new PolicyModelTranslator.RawPolicy(localModelNode2, nestedAlternatives);
              } else {
                localRawPolicy = new PolicyModelTranslator.RawPolicy(PolicyModelTranslator.getReferencedModelRootNode(localModelNode2), nestedAlternatives);
              }
              allNestedPolicies.add(localRawPolicy);
            }
            else
            {
              throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0006_UNEXPECTED_MULTIPLE_POLICY_NODES())));
            }
            break;
          default: 
            throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0008_UNEXPECTED_CHILD_MODEL_TYPE(localModelNode2.getType()))));
          }
        }
      }
    }
  }
  
  private static final class RawAssertion
  {
    ModelNode originalNode;
    Collection<PolicyModelTranslator.RawAlternative> nestedAlternatives = null;
    final Collection<ModelNode> parameters;
    
    RawAssertion(ModelNode paramModelNode, Collection<ModelNode> paramCollection)
    {
      parameters = paramCollection;
      originalNode = paramModelNode;
    }
  }
  
  private static final class RawPolicy
  {
    final Collection<ModelNode> originalContent;
    final Collection<PolicyModelTranslator.RawAlternative> alternatives;
    
    RawPolicy(ModelNode paramModelNode, Collection<PolicyModelTranslator.RawAlternative> paramCollection)
    {
      originalContent = paramModelNode.getChildren();
      alternatives = paramCollection;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\PolicyModelTranslator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */