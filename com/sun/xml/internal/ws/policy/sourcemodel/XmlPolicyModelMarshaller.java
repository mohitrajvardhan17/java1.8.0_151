package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.txw2.TXW;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.output.StaxSerializer;
import com.sun.xml.internal.ws.policy.PolicyConstants;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamWriter;

public final class XmlPolicyModelMarshaller
  extends PolicyModelMarshaller
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(XmlPolicyModelMarshaller.class);
  private final boolean marshallInvisible;
  
  XmlPolicyModelMarshaller(boolean paramBoolean)
  {
    marshallInvisible = paramBoolean;
  }
  
  public void marshal(PolicySourceModel paramPolicySourceModel, Object paramObject)
    throws PolicyException
  {
    if ((paramObject instanceof StaxSerializer)) {
      marshal(paramPolicySourceModel, (StaxSerializer)paramObject);
    } else if ((paramObject instanceof TypedXmlWriter)) {
      marshal(paramPolicySourceModel, (TypedXmlWriter)paramObject);
    } else if ((paramObject instanceof XMLStreamWriter)) {
      marshal(paramPolicySourceModel, (XMLStreamWriter)paramObject);
    } else {
      throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0022_STORAGE_TYPE_NOT_SUPPORTED(paramObject.getClass().getName()))));
    }
  }
  
  public void marshal(Collection<PolicySourceModel> paramCollection, Object paramObject)
    throws PolicyException
  {
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      PolicySourceModel localPolicySourceModel = (PolicySourceModel)localIterator.next();
      marshal(localPolicySourceModel, paramObject);
    }
  }
  
  private void marshal(PolicySourceModel paramPolicySourceModel, StaxSerializer paramStaxSerializer)
    throws PolicyException
  {
    TypedXmlWriter localTypedXmlWriter = TXW.create(paramPolicySourceModel.getNamespaceVersion().asQName(XmlToken.Policy), TypedXmlWriter.class, paramStaxSerializer);
    marshalDefaultPrefixes(paramPolicySourceModel, localTypedXmlWriter);
    marshalPolicyAttributes(paramPolicySourceModel, localTypedXmlWriter);
    marshal(paramPolicySourceModel.getNamespaceVersion(), paramPolicySourceModel.getRootNode(), localTypedXmlWriter);
    localTypedXmlWriter.commit();
  }
  
  private void marshal(PolicySourceModel paramPolicySourceModel, TypedXmlWriter paramTypedXmlWriter)
    throws PolicyException
  {
    TypedXmlWriter localTypedXmlWriter = paramTypedXmlWriter._element(paramPolicySourceModel.getNamespaceVersion().asQName(XmlToken.Policy), TypedXmlWriter.class);
    marshalDefaultPrefixes(paramPolicySourceModel, localTypedXmlWriter);
    marshalPolicyAttributes(paramPolicySourceModel, localTypedXmlWriter);
    marshal(paramPolicySourceModel.getNamespaceVersion(), paramPolicySourceModel.getRootNode(), localTypedXmlWriter);
  }
  
  private void marshal(PolicySourceModel paramPolicySourceModel, XMLStreamWriter paramXMLStreamWriter)
    throws PolicyException
  {
    StaxSerializer localStaxSerializer = new StaxSerializer(paramXMLStreamWriter);
    TypedXmlWriter localTypedXmlWriter = TXW.create(paramPolicySourceModel.getNamespaceVersion().asQName(XmlToken.Policy), TypedXmlWriter.class, localStaxSerializer);
    marshalDefaultPrefixes(paramPolicySourceModel, localTypedXmlWriter);
    marshalPolicyAttributes(paramPolicySourceModel, localTypedXmlWriter);
    marshal(paramPolicySourceModel.getNamespaceVersion(), paramPolicySourceModel.getRootNode(), localTypedXmlWriter);
    localTypedXmlWriter.commit();
    localStaxSerializer.flush();
  }
  
  private static void marshalPolicyAttributes(PolicySourceModel paramPolicySourceModel, TypedXmlWriter paramTypedXmlWriter)
  {
    String str1 = paramPolicySourceModel.getPolicyId();
    if (str1 != null) {
      paramTypedXmlWriter._attribute(PolicyConstants.WSU_ID, str1);
    }
    String str2 = paramPolicySourceModel.getPolicyName();
    if (str2 != null) {
      paramTypedXmlWriter._attribute(paramPolicySourceModel.getNamespaceVersion().asQName(XmlToken.Name), str2);
    }
  }
  
  private void marshal(NamespaceVersion paramNamespaceVersion, ModelNode paramModelNode, TypedXmlWriter paramTypedXmlWriter)
  {
    Iterator localIterator1 = paramModelNode.iterator();
    while (localIterator1.hasNext())
    {
      ModelNode localModelNode = (ModelNode)localIterator1.next();
      AssertionData localAssertionData = localModelNode.getNodeData();
      if ((marshallInvisible) || (localAssertionData == null) || (!localAssertionData.isPrivateAttributeSet()))
      {
        TypedXmlWriter localTypedXmlWriter = null;
        if (localAssertionData == null)
        {
          localTypedXmlWriter = paramTypedXmlWriter._element(paramNamespaceVersion.asQName(localModelNode.getType().getXmlToken()), TypedXmlWriter.class);
        }
        else
        {
          localTypedXmlWriter = paramTypedXmlWriter._element(localAssertionData.getName(), TypedXmlWriter.class);
          String str = localAssertionData.getValue();
          if (str != null) {
            localTypedXmlWriter._pcdata(str);
          }
          if (localAssertionData.isOptionalAttributeSet()) {
            localTypedXmlWriter._attribute(paramNamespaceVersion.asQName(XmlToken.Optional), Boolean.TRUE);
          }
          if (localAssertionData.isIgnorableAttributeSet()) {
            localTypedXmlWriter._attribute(paramNamespaceVersion.asQName(XmlToken.Ignorable), Boolean.TRUE);
          }
          Iterator localIterator2 = localAssertionData.getAttributesSet().iterator();
          while (localIterator2.hasNext())
          {
            Map.Entry localEntry = (Map.Entry)localIterator2.next();
            localTypedXmlWriter._attribute((QName)localEntry.getKey(), localEntry.getValue());
          }
        }
        marshal(paramNamespaceVersion, localModelNode, localTypedXmlWriter);
      }
    }
  }
  
  private void marshalDefaultPrefixes(PolicySourceModel paramPolicySourceModel, TypedXmlWriter paramTypedXmlWriter)
    throws PolicyException
  {
    Map localMap = paramPolicySourceModel.getNamespaceToPrefixMapping();
    if ((!marshallInvisible) && (localMap.containsKey("http://java.sun.com/xml/ns/wsit/policy"))) {
      localMap.remove("http://java.sun.com/xml/ns/wsit/policy");
    }
    Iterator localIterator = localMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      paramTypedXmlWriter._namespace((String)localEntry.getKey(), (String)localEntry.getValue());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\XmlPolicyModelMarshaller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */