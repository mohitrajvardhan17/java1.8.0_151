package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Text;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public final class ModelNode
  implements Iterable<ModelNode>, Cloneable
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(ModelNode.class);
  private LinkedList<ModelNode> children;
  private Collection<ModelNode> unmodifiableViewOnContent;
  private final Type type;
  private ModelNode parentNode;
  private PolicySourceModel parentModel;
  private PolicyReferenceData referenceData;
  private PolicySourceModel referencedModel;
  private AssertionData nodeData;
  
  static ModelNode createRootPolicyNode(PolicySourceModel paramPolicySourceModel)
    throws IllegalArgumentException
  {
    if (paramPolicySourceModel == null) {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0039_POLICY_SRC_MODEL_INPUT_PARAMETER_MUST_NOT_BE_NULL())));
    }
    return new ModelNode(Type.POLICY, paramPolicySourceModel);
  }
  
  private ModelNode(Type paramType, PolicySourceModel paramPolicySourceModel)
  {
    type = paramType;
    parentModel = paramPolicySourceModel;
    children = new LinkedList();
    unmodifiableViewOnContent = Collections.unmodifiableCollection(children);
  }
  
  private ModelNode(Type paramType, PolicySourceModel paramPolicySourceModel, AssertionData paramAssertionData)
  {
    this(paramType, paramPolicySourceModel);
    nodeData = paramAssertionData;
  }
  
  private ModelNode(PolicySourceModel paramPolicySourceModel, PolicyReferenceData paramPolicyReferenceData)
  {
    this(Type.POLICY_REFERENCE, paramPolicySourceModel);
    referenceData = paramPolicyReferenceData;
  }
  
  private void checkCreateChildOperationSupportForType(Type paramType)
    throws UnsupportedOperationException
  {
    if (!type.isChildTypeSupported(paramType)) {
      throw ((UnsupportedOperationException)LOGGER.logSevereException(new UnsupportedOperationException(LocalizationMessages.WSP_0073_CREATE_CHILD_NODE_OPERATION_NOT_SUPPORTED(paramType, type))));
    }
  }
  
  public ModelNode createChildPolicyNode()
  {
    checkCreateChildOperationSupportForType(Type.POLICY);
    ModelNode localModelNode = new ModelNode(Type.POLICY, parentModel);
    addChild(localModelNode);
    return localModelNode;
  }
  
  public ModelNode createChildAllNode()
  {
    checkCreateChildOperationSupportForType(Type.ALL);
    ModelNode localModelNode = new ModelNode(Type.ALL, parentModel);
    addChild(localModelNode);
    return localModelNode;
  }
  
  public ModelNode createChildExactlyOneNode()
  {
    checkCreateChildOperationSupportForType(Type.EXACTLY_ONE);
    ModelNode localModelNode = new ModelNode(Type.EXACTLY_ONE, parentModel);
    addChild(localModelNode);
    return localModelNode;
  }
  
  public ModelNode createChildAssertionNode()
  {
    checkCreateChildOperationSupportForType(Type.ASSERTION);
    ModelNode localModelNode = new ModelNode(Type.ASSERTION, parentModel);
    addChild(localModelNode);
    return localModelNode;
  }
  
  public ModelNode createChildAssertionNode(AssertionData paramAssertionData)
  {
    checkCreateChildOperationSupportForType(Type.ASSERTION);
    ModelNode localModelNode = new ModelNode(Type.ASSERTION, parentModel, paramAssertionData);
    addChild(localModelNode);
    return localModelNode;
  }
  
  public ModelNode createChildAssertionParameterNode()
  {
    checkCreateChildOperationSupportForType(Type.ASSERTION_PARAMETER_NODE);
    ModelNode localModelNode = new ModelNode(Type.ASSERTION_PARAMETER_NODE, parentModel);
    addChild(localModelNode);
    return localModelNode;
  }
  
  ModelNode createChildAssertionParameterNode(AssertionData paramAssertionData)
  {
    checkCreateChildOperationSupportForType(Type.ASSERTION_PARAMETER_NODE);
    ModelNode localModelNode = new ModelNode(Type.ASSERTION_PARAMETER_NODE, parentModel, paramAssertionData);
    addChild(localModelNode);
    return localModelNode;
  }
  
  ModelNode createChildPolicyReferenceNode(PolicyReferenceData paramPolicyReferenceData)
  {
    checkCreateChildOperationSupportForType(Type.POLICY_REFERENCE);
    ModelNode localModelNode = new ModelNode(parentModel, paramPolicyReferenceData);
    parentModel.addNewPolicyReference(localModelNode);
    addChild(localModelNode);
    return localModelNode;
  }
  
  Collection<ModelNode> getChildren()
  {
    return unmodifiableViewOnContent;
  }
  
  void setParentModel(PolicySourceModel paramPolicySourceModel)
    throws IllegalAccessException
  {
    if (parentNode != null) {
      throw ((IllegalAccessException)LOGGER.logSevereException(new IllegalAccessException(LocalizationMessages.WSP_0049_PARENT_MODEL_CAN_NOT_BE_CHANGED())));
    }
    updateParentModelReference(paramPolicySourceModel);
  }
  
  private void updateParentModelReference(PolicySourceModel paramPolicySourceModel)
  {
    parentModel = paramPolicySourceModel;
    Iterator localIterator = children.iterator();
    while (localIterator.hasNext())
    {
      ModelNode localModelNode = (ModelNode)localIterator.next();
      localModelNode.updateParentModelReference(paramPolicySourceModel);
    }
  }
  
  public PolicySourceModel getParentModel()
  {
    return parentModel;
  }
  
  public Type getType()
  {
    return type;
  }
  
  public ModelNode getParentNode()
  {
    return parentNode;
  }
  
  public AssertionData getNodeData()
  {
    return nodeData;
  }
  
  PolicyReferenceData getPolicyReferenceData()
  {
    return referenceData;
  }
  
  public AssertionData setOrReplaceNodeData(AssertionData paramAssertionData)
  {
    if (!isDomainSpecific()) {
      throw ((UnsupportedOperationException)LOGGER.logSevereException(new UnsupportedOperationException(LocalizationMessages.WSP_0051_OPERATION_NOT_SUPPORTED_FOR_THIS_BUT_ASSERTION_RELATED_NODE_TYPE(type))));
    }
    AssertionData localAssertionData = nodeData;
    nodeData = paramAssertionData;
    return localAssertionData;
  }
  
  boolean isDomainSpecific()
  {
    return (type == Type.ASSERTION) || (type == Type.ASSERTION_PARAMETER_NODE);
  }
  
  private boolean addChild(ModelNode paramModelNode)
  {
    children.add(paramModelNode);
    parentNode = this;
    return true;
  }
  
  void setReferencedModel(PolicySourceModel paramPolicySourceModel)
  {
    if (type != Type.POLICY_REFERENCE) {
      throw ((UnsupportedOperationException)LOGGER.logSevereException(new UnsupportedOperationException(LocalizationMessages.WSP_0050_OPERATION_NOT_SUPPORTED_FOR_THIS_BUT_POLICY_REFERENCE_NODE_TYPE(type))));
    }
    referencedModel = paramPolicySourceModel;
  }
  
  PolicySourceModel getReferencedModel()
  {
    return referencedModel;
  }
  
  public int childrenSize()
  {
    return children.size();
  }
  
  public boolean hasChildren()
  {
    return !children.isEmpty();
  }
  
  public Iterator<ModelNode> iterator()
  {
    return children.iterator();
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof ModelNode)) {
      return false;
    }
    boolean bool = true;
    ModelNode localModelNode = (ModelNode)paramObject;
    bool = (bool) && (type.equals(type));
    bool = (bool) && (nodeData == null ? nodeData == null : nodeData.equals(nodeData));
    bool = (bool) && (children == null ? children == null : children.equals(children));
    return bool;
  }
  
  public int hashCode()
  {
    int i = 17;
    i = 37 * i + type.hashCode();
    i = 37 * i + (parentNode == null ? 0 : parentNode.hashCode());
    i = 37 * i + (nodeData == null ? 0 : nodeData.hashCode());
    i = 37 * i + children.hashCode();
    return i;
  }
  
  public String toString()
  {
    return toString(0, new StringBuffer()).toString();
  }
  
  public StringBuffer toString(int paramInt, StringBuffer paramStringBuffer)
  {
    String str1 = PolicyUtils.Text.createIndent(paramInt);
    String str2 = PolicyUtils.Text.createIndent(paramInt + 1);
    paramStringBuffer.append(str1).append(type).append(" {").append(PolicyUtils.Text.NEW_LINE);
    if (type == Type.ASSERTION)
    {
      if (nodeData == null) {
        paramStringBuffer.append(str2).append("no assertion data set");
      } else {
        nodeData.toString(paramInt + 1, paramStringBuffer);
      }
      paramStringBuffer.append(PolicyUtils.Text.NEW_LINE);
    }
    else if (type == Type.POLICY_REFERENCE)
    {
      if (referenceData == null) {
        paramStringBuffer.append(str2).append("no policy reference data set");
      } else {
        referenceData.toString(paramInt + 1, paramStringBuffer);
      }
      paramStringBuffer.append(PolicyUtils.Text.NEW_LINE);
    }
    else if (type == Type.ASSERTION_PARAMETER_NODE)
    {
      if (nodeData == null) {
        paramStringBuffer.append(str2).append("no parameter data set");
      } else {
        nodeData.toString(paramInt + 1, paramStringBuffer);
      }
      paramStringBuffer.append(PolicyUtils.Text.NEW_LINE);
    }
    if (children.size() > 0)
    {
      Iterator localIterator = children.iterator();
      while (localIterator.hasNext())
      {
        ModelNode localModelNode = (ModelNode)localIterator.next();
        localModelNode.toString(paramInt + 1, paramStringBuffer).append(PolicyUtils.Text.NEW_LINE);
      }
    }
    else
    {
      paramStringBuffer.append(str2).append("no child nodes").append(PolicyUtils.Text.NEW_LINE);
    }
    paramStringBuffer.append(str1).append('}');
    return paramStringBuffer;
  }
  
  protected ModelNode clone()
    throws CloneNotSupportedException
  {
    ModelNode localModelNode1 = (ModelNode)super.clone();
    if (nodeData != null) {
      nodeData = nodeData.clone();
    }
    if (referencedModel != null) {
      referencedModel = referencedModel.clone();
    }
    children = new LinkedList();
    unmodifiableViewOnContent = Collections.unmodifiableCollection(children);
    Iterator localIterator = children.iterator();
    while (localIterator.hasNext())
    {
      ModelNode localModelNode2 = (ModelNode)localIterator.next();
      localModelNode1.addChild(localModelNode2.clone());
    }
    return localModelNode1;
  }
  
  PolicyReferenceData getReferenceData()
  {
    return referenceData;
  }
  
  public static enum Type
  {
    POLICY(XmlToken.Policy),  ALL(XmlToken.All),  EXACTLY_ONE(XmlToken.ExactlyOne),  POLICY_REFERENCE(XmlToken.PolicyReference),  ASSERTION(XmlToken.UNKNOWN),  ASSERTION_PARAMETER_NODE(XmlToken.UNKNOWN);
    
    private XmlToken token;
    
    private Type(XmlToken paramXmlToken)
    {
      token = paramXmlToken;
    }
    
    public XmlToken getXmlToken()
    {
      return token;
    }
    
    private boolean isChildTypeSupported(Type paramType)
    {
      switch (ModelNode.1.$SwitchMap$com$sun$xml$internal$ws$policy$sourcemodel$ModelNode$Type[ordinal()])
      {
      case 2: 
      case 4: 
      case 5: 
        switch (ModelNode.1.$SwitchMap$com$sun$xml$internal$ws$policy$sourcemodel$ModelNode$Type[paramType.ordinal()])
        {
        case 1: 
          return false;
        }
        return true;
      case 3: 
        return false;
      case 6: 
        switch (ModelNode.1.$SwitchMap$com$sun$xml$internal$ws$policy$sourcemodel$ModelNode$Type[paramType.ordinal()])
        {
        case 1: 
        case 2: 
        case 3: 
          return true;
        }
        return false;
      case 1: 
        switch (ModelNode.1.$SwitchMap$com$sun$xml$internal$ws$policy$sourcemodel$ModelNode$Type[paramType.ordinal()])
        {
        case 1: 
          return true;
        }
        return false;
      }
      throw ((IllegalStateException)ModelNode.LOGGER.logSevereException(new IllegalStateException(LocalizationMessages.WSP_0060_POLICY_ELEMENT_TYPE_UNKNOWN(this))));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\ModelNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */