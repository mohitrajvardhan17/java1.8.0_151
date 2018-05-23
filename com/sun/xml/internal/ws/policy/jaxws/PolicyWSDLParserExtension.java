package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLObject;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.policy.PolicyResolver.ClientContext;
import com.sun.xml.internal.ws.api.policy.PolicyResolver.ServerContext;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtensionContext;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapMutator;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.IO;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModelContext;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken;
import com.sun.xml.internal.ws.resources.PolicyMessages;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;

public final class PolicyWSDLParserExtension
  extends WSDLParserExtension
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyWSDLParserExtension.class);
  private static final StringBuffer AnonymnousPolicyIdPrefix = new StringBuffer("#__anonymousPolicy__ID");
  private int anonymousPoliciesCount;
  private final SafePolicyReader policyReader = new SafePolicyReader();
  private SafePolicyReader.PolicyRecord expandQueueHead = null;
  private Map<String, SafePolicyReader.PolicyRecord> policyRecordsPassedBy = null;
  private Map<String, PolicySourceModel> anonymousPolicyModels = null;
  private List<String> unresolvedUris = null;
  private final LinkedList<String> urisNeeded = new LinkedList();
  private final Map<String, PolicySourceModel> modelsNeeded = new HashMap();
  private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4ServiceMap = null;
  private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4PortMap = null;
  private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4PortTypeMap = null;
  private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4BindingMap = null;
  private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4BoundOperationMap = null;
  private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4OperationMap = null;
  private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4MessageMap = null;
  private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4InputMap = null;
  private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4OutputMap = null;
  private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4FaultMap = null;
  private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4BindingInputOpMap = null;
  private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4BindingOutputOpMap = null;
  private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4BindingFaultOpMap = null;
  private PolicyMapBuilder policyBuilder = new PolicyMapBuilder();
  
  private boolean isPolicyProcessed(String paramString)
  {
    return modelsNeeded.containsKey(paramString);
  }
  
  private void addNewPolicyNeeded(String paramString, PolicySourceModel paramPolicySourceModel)
  {
    if (!modelsNeeded.containsKey(paramString))
    {
      modelsNeeded.put(paramString, paramPolicySourceModel);
      urisNeeded.addFirst(paramString);
    }
  }
  
  private Map<String, PolicySourceModel> getPolicyModels()
  {
    return modelsNeeded;
  }
  
  private Map<String, SafePolicyReader.PolicyRecord> getPolicyRecordsPassedBy()
  {
    if (null == policyRecordsPassedBy) {
      policyRecordsPassedBy = new HashMap();
    }
    return policyRecordsPassedBy;
  }
  
  private Map<String, PolicySourceModel> getAnonymousPolicyModels()
  {
    if (null == anonymousPolicyModels) {
      anonymousPolicyModels = new HashMap();
    }
    return anonymousPolicyModels;
  }
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4ServiceMap()
  {
    if (null == handlers4ServiceMap) {
      handlers4ServiceMap = new HashMap();
    }
    return handlers4ServiceMap;
  }
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4PortMap()
  {
    if (null == handlers4PortMap) {
      handlers4PortMap = new HashMap();
    }
    return handlers4PortMap;
  }
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4PortTypeMap()
  {
    if (null == handlers4PortTypeMap) {
      handlers4PortTypeMap = new HashMap();
    }
    return handlers4PortTypeMap;
  }
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4BindingMap()
  {
    if (null == handlers4BindingMap) {
      handlers4BindingMap = new HashMap();
    }
    return handlers4BindingMap;
  }
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4OperationMap()
  {
    if (null == handlers4OperationMap) {
      handlers4OperationMap = new HashMap();
    }
    return handlers4OperationMap;
  }
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4BoundOperationMap()
  {
    if (null == handlers4BoundOperationMap) {
      handlers4BoundOperationMap = new HashMap();
    }
    return handlers4BoundOperationMap;
  }
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4MessageMap()
  {
    if (null == handlers4MessageMap) {
      handlers4MessageMap = new HashMap();
    }
    return handlers4MessageMap;
  }
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4InputMap()
  {
    if (null == handlers4InputMap) {
      handlers4InputMap = new HashMap();
    }
    return handlers4InputMap;
  }
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4OutputMap()
  {
    if (null == handlers4OutputMap) {
      handlers4OutputMap = new HashMap();
    }
    return handlers4OutputMap;
  }
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4FaultMap()
  {
    if (null == handlers4FaultMap) {
      handlers4FaultMap = new HashMap();
    }
    return handlers4FaultMap;
  }
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4BindingInputOpMap()
  {
    if (null == handlers4BindingInputOpMap) {
      handlers4BindingInputOpMap = new HashMap();
    }
    return handlers4BindingInputOpMap;
  }
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4BindingOutputOpMap()
  {
    if (null == handlers4BindingOutputOpMap) {
      handlers4BindingOutputOpMap = new HashMap();
    }
    return handlers4BindingOutputOpMap;
  }
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4BindingFaultOpMap()
  {
    if (null == handlers4BindingFaultOpMap) {
      handlers4BindingFaultOpMap = new HashMap();
    }
    return handlers4BindingFaultOpMap;
  }
  
  private List<String> getUnresolvedUris(boolean paramBoolean)
  {
    if ((null == unresolvedUris) || (paramBoolean)) {
      unresolvedUris = new LinkedList();
    }
    return unresolvedUris;
  }
  
  private void policyRecToExpandQueue(SafePolicyReader.PolicyRecord paramPolicyRecord)
  {
    if (null == expandQueueHead) {
      expandQueueHead = paramPolicyRecord;
    } else {
      expandQueueHead = expandQueueHead.insert(paramPolicyRecord);
    }
  }
  
  public PolicyWSDLParserExtension() {}
  
  private PolicyRecordHandler readSinglePolicy(SafePolicyReader.PolicyRecord paramPolicyRecord, boolean paramBoolean)
  {
    PolicyRecordHandler localPolicyRecordHandler = null;
    String str1 = policyModel.getPolicyId();
    if (str1 == null) {
      str1 = policyModel.getPolicyName();
    }
    if (str1 != null)
    {
      localPolicyRecordHandler = new PolicyRecordHandler(HandlerType.PolicyUri, paramPolicyRecord.getUri());
      getPolicyRecordsPassedBy().put(paramPolicyRecord.getUri(), paramPolicyRecord);
      policyRecToExpandQueue(paramPolicyRecord);
    }
    else if (paramBoolean)
    {
      String str2 = anonymousPoliciesCount++;
      localPolicyRecordHandler = new PolicyRecordHandler(HandlerType.AnonymousPolicyId, str2);
      getAnonymousPolicyModels().put(str2, policyModel);
      if (null != unresolvedURIs) {
        getUnresolvedUris(false).addAll(unresolvedURIs);
      }
    }
    return localPolicyRecordHandler;
  }
  
  private void addHandlerToMap(Map<WSDLObject, Collection<PolicyRecordHandler>> paramMap, WSDLObject paramWSDLObject, PolicyRecordHandler paramPolicyRecordHandler)
  {
    if (paramMap.containsKey(paramWSDLObject))
    {
      ((Collection)paramMap.get(paramWSDLObject)).add(paramPolicyRecordHandler);
    }
    else
    {
      LinkedList localLinkedList = new LinkedList();
      localLinkedList.add(paramPolicyRecordHandler);
      paramMap.put(paramWSDLObject, localLinkedList);
    }
  }
  
  private String getBaseUrl(String paramString)
  {
    if (null == paramString) {
      return null;
    }
    int i = paramString.indexOf('#');
    return i == -1 ? paramString : paramString.substring(0, i);
  }
  
  private void processReferenceUri(String paramString, WSDLObject paramWSDLObject, XMLStreamReader paramXMLStreamReader, Map<WSDLObject, Collection<PolicyRecordHandler>> paramMap)
  {
    if ((null == paramString) || (paramString.length() == 0)) {
      return;
    }
    if ('#' != paramString.charAt(0)) {
      getUnresolvedUris(false).add(paramString);
    }
    addHandlerToMap(paramMap, paramWSDLObject, new PolicyRecordHandler(HandlerType.PolicyUri, SafePolicyReader.relativeToAbsoluteUrl(paramString, paramXMLStreamReader.getLocation().getSystemId())));
  }
  
  private boolean processSubelement(WSDLObject paramWSDLObject, XMLStreamReader paramXMLStreamReader, Map<WSDLObject, Collection<PolicyRecordHandler>> paramMap)
  {
    if (NamespaceVersion.resolveAsToken(paramXMLStreamReader.getName()) == XmlToken.PolicyReference)
    {
      processReferenceUri(policyReader.readPolicyReferenceElement(paramXMLStreamReader), paramWSDLObject, paramXMLStreamReader, paramMap);
      return true;
    }
    if (NamespaceVersion.resolveAsToken(paramXMLStreamReader.getName()) == XmlToken.Policy)
    {
      PolicyRecordHandler localPolicyRecordHandler = readSinglePolicy(policyReader.readPolicyElement(paramXMLStreamReader, null == paramXMLStreamReader.getLocation().getSystemId() ? "" : paramXMLStreamReader.getLocation().getSystemId()), true);
      if (null != localPolicyRecordHandler) {
        addHandlerToMap(paramMap, paramWSDLObject, localPolicyRecordHandler);
      }
      return true;
    }
    return false;
  }
  
  private void processAttributes(WSDLObject paramWSDLObject, XMLStreamReader paramXMLStreamReader, Map<WSDLObject, Collection<PolicyRecordHandler>> paramMap)
  {
    String[] arrayOfString1 = getPolicyURIsFromAttr(paramXMLStreamReader);
    if (null != arrayOfString1) {
      for (String str : arrayOfString1) {
        processReferenceUri(str, paramWSDLObject, paramXMLStreamReader, paramMap);
      }
    }
  }
  
  public boolean portElements(EditableWSDLPort paramEditableWSDLPort, XMLStreamReader paramXMLStreamReader)
  {
    LOGGER.entering();
    boolean bool = processSubelement(paramEditableWSDLPort, paramXMLStreamReader, getHandlers4PortMap());
    LOGGER.exiting();
    return bool;
  }
  
  public void portAttributes(EditableWSDLPort paramEditableWSDLPort, XMLStreamReader paramXMLStreamReader)
  {
    LOGGER.entering();
    processAttributes(paramEditableWSDLPort, paramXMLStreamReader, getHandlers4PortMap());
    LOGGER.exiting();
  }
  
  public boolean serviceElements(EditableWSDLService paramEditableWSDLService, XMLStreamReader paramXMLStreamReader)
  {
    LOGGER.entering();
    boolean bool = processSubelement(paramEditableWSDLService, paramXMLStreamReader, getHandlers4ServiceMap());
    LOGGER.exiting();
    return bool;
  }
  
  public void serviceAttributes(EditableWSDLService paramEditableWSDLService, XMLStreamReader paramXMLStreamReader)
  {
    LOGGER.entering();
    processAttributes(paramEditableWSDLService, paramXMLStreamReader, getHandlers4ServiceMap());
    LOGGER.exiting();
  }
  
  public boolean definitionsElements(XMLStreamReader paramXMLStreamReader)
  {
    LOGGER.entering();
    if (NamespaceVersion.resolveAsToken(paramXMLStreamReader.getName()) == XmlToken.Policy)
    {
      readSinglePolicy(policyReader.readPolicyElement(paramXMLStreamReader, null == paramXMLStreamReader.getLocation().getSystemId() ? "" : paramXMLStreamReader.getLocation().getSystemId()), false);
      LOGGER.exiting();
      return true;
    }
    LOGGER.exiting();
    return false;
  }
  
  public boolean bindingElements(EditableWSDLBoundPortType paramEditableWSDLBoundPortType, XMLStreamReader paramXMLStreamReader)
  {
    LOGGER.entering();
    boolean bool = processSubelement(paramEditableWSDLBoundPortType, paramXMLStreamReader, getHandlers4BindingMap());
    LOGGER.exiting();
    return bool;
  }
  
  public void bindingAttributes(EditableWSDLBoundPortType paramEditableWSDLBoundPortType, XMLStreamReader paramXMLStreamReader)
  {
    LOGGER.entering();
    processAttributes(paramEditableWSDLBoundPortType, paramXMLStreamReader, getHandlers4BindingMap());
    LOGGER.exiting();
  }
  
  public boolean portTypeElements(EditableWSDLPortType paramEditableWSDLPortType, XMLStreamReader paramXMLStreamReader)
  {
    LOGGER.entering();
    boolean bool = processSubelement(paramEditableWSDLPortType, paramXMLStreamReader, getHandlers4PortTypeMap());
    LOGGER.exiting();
    return bool;
  }
  
  public void portTypeAttributes(EditableWSDLPortType paramEditableWSDLPortType, XMLStreamReader paramXMLStreamReader)
  {
    LOGGER.entering();
    processAttributes(paramEditableWSDLPortType, paramXMLStreamReader, getHandlers4PortTypeMap());
    LOGGER.exiting();
  }
  
  public boolean portTypeOperationElements(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader)
  {
    LOGGER.entering();
    boolean bool = processSubelement(paramEditableWSDLOperation, paramXMLStreamReader, getHandlers4OperationMap());
    LOGGER.exiting();
    return bool;
  }
  
  public void portTypeOperationAttributes(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader)
  {
    LOGGER.entering();
    processAttributes(paramEditableWSDLOperation, paramXMLStreamReader, getHandlers4OperationMap());
    LOGGER.exiting();
  }
  
  public boolean bindingOperationElements(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader)
  {
    LOGGER.entering();
    boolean bool = processSubelement(paramEditableWSDLBoundOperation, paramXMLStreamReader, getHandlers4BoundOperationMap());
    LOGGER.exiting();
    return bool;
  }
  
  public void bindingOperationAttributes(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader)
  {
    LOGGER.entering();
    processAttributes(paramEditableWSDLBoundOperation, paramXMLStreamReader, getHandlers4BoundOperationMap());
    LOGGER.exiting();
  }
  
  public boolean messageElements(EditableWSDLMessage paramEditableWSDLMessage, XMLStreamReader paramXMLStreamReader)
  {
    LOGGER.entering();
    boolean bool = processSubelement(paramEditableWSDLMessage, paramXMLStreamReader, getHandlers4MessageMap());
    LOGGER.exiting();
    return bool;
  }
  
  public void messageAttributes(EditableWSDLMessage paramEditableWSDLMessage, XMLStreamReader paramXMLStreamReader)
  {
    LOGGER.entering();
    processAttributes(paramEditableWSDLMessage, paramXMLStreamReader, getHandlers4MessageMap());
    LOGGER.exiting();
  }
  
  public boolean portTypeOperationInputElements(EditableWSDLInput paramEditableWSDLInput, XMLStreamReader paramXMLStreamReader)
  {
    LOGGER.entering();
    boolean bool = processSubelement(paramEditableWSDLInput, paramXMLStreamReader, getHandlers4InputMap());
    LOGGER.exiting();
    return bool;
  }
  
  public void portTypeOperationInputAttributes(EditableWSDLInput paramEditableWSDLInput, XMLStreamReader paramXMLStreamReader)
  {
    LOGGER.entering();
    processAttributes(paramEditableWSDLInput, paramXMLStreamReader, getHandlers4InputMap());
    LOGGER.exiting();
  }
  
  public boolean portTypeOperationOutputElements(EditableWSDLOutput paramEditableWSDLOutput, XMLStreamReader paramXMLStreamReader)
  {
    LOGGER.entering();
    boolean bool = processSubelement(paramEditableWSDLOutput, paramXMLStreamReader, getHandlers4OutputMap());
    LOGGER.exiting();
    return bool;
  }
  
  public void portTypeOperationOutputAttributes(EditableWSDLOutput paramEditableWSDLOutput, XMLStreamReader paramXMLStreamReader)
  {
    LOGGER.entering();
    processAttributes(paramEditableWSDLOutput, paramXMLStreamReader, getHandlers4OutputMap());
    LOGGER.exiting();
  }
  
  public boolean portTypeOperationFaultElements(EditableWSDLFault paramEditableWSDLFault, XMLStreamReader paramXMLStreamReader)
  {
    LOGGER.entering();
    boolean bool = processSubelement(paramEditableWSDLFault, paramXMLStreamReader, getHandlers4FaultMap());
    LOGGER.exiting();
    return bool;
  }
  
  public void portTypeOperationFaultAttributes(EditableWSDLFault paramEditableWSDLFault, XMLStreamReader paramXMLStreamReader)
  {
    LOGGER.entering();
    processAttributes(paramEditableWSDLFault, paramXMLStreamReader, getHandlers4FaultMap());
    LOGGER.exiting();
  }
  
  public boolean bindingOperationInputElements(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader)
  {
    LOGGER.entering();
    boolean bool = processSubelement(paramEditableWSDLBoundOperation, paramXMLStreamReader, getHandlers4BindingInputOpMap());
    LOGGER.exiting();
    return bool;
  }
  
  public void bindingOperationInputAttributes(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader)
  {
    LOGGER.entering();
    processAttributes(paramEditableWSDLBoundOperation, paramXMLStreamReader, getHandlers4BindingInputOpMap());
    LOGGER.exiting();
  }
  
  public boolean bindingOperationOutputElements(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader)
  {
    LOGGER.entering();
    boolean bool = processSubelement(paramEditableWSDLBoundOperation, paramXMLStreamReader, getHandlers4BindingOutputOpMap());
    LOGGER.exiting();
    return bool;
  }
  
  public void bindingOperationOutputAttributes(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader)
  {
    LOGGER.entering();
    processAttributes(paramEditableWSDLBoundOperation, paramXMLStreamReader, getHandlers4BindingOutputOpMap());
    LOGGER.exiting();
  }
  
  public boolean bindingOperationFaultElements(EditableWSDLBoundFault paramEditableWSDLBoundFault, XMLStreamReader paramXMLStreamReader)
  {
    LOGGER.entering();
    boolean bool = processSubelement(paramEditableWSDLBoundFault, paramXMLStreamReader, getHandlers4BindingFaultOpMap());
    LOGGER.exiting(Boolean.valueOf(bool));
    return bool;
  }
  
  public void bindingOperationFaultAttributes(EditableWSDLBoundFault paramEditableWSDLBoundFault, XMLStreamReader paramXMLStreamReader)
  {
    LOGGER.entering();
    processAttributes(paramEditableWSDLBoundFault, paramXMLStreamReader, getHandlers4BindingFaultOpMap());
    LOGGER.exiting();
  }
  
  private PolicyMapBuilder getPolicyMapBuilder()
  {
    if (null == policyBuilder) {
      policyBuilder = new PolicyMapBuilder();
    }
    return policyBuilder;
  }
  
  private Collection<String> getPolicyURIs(Collection<PolicyRecordHandler> paramCollection, PolicySourceModelContext paramPolicySourceModelContext)
    throws PolicyException
  {
    ArrayList localArrayList = new ArrayList(paramCollection.size());
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      PolicyRecordHandler localPolicyRecordHandler = (PolicyRecordHandler)localIterator.next();
      String str = handler;
      if (HandlerType.AnonymousPolicyId == type)
      {
        PolicySourceModel localPolicySourceModel = (PolicySourceModel)getAnonymousPolicyModels().get(str);
        localPolicySourceModel.expand(paramPolicySourceModelContext);
        while (getPolicyModels().containsKey(str)) {
          str = anonymousPoliciesCount++;
        }
        getPolicyModels().put(str, localPolicySourceModel);
      }
      localArrayList.add(str);
    }
    return localArrayList;
  }
  
  private boolean readExternalFile(String paramString)
  {
    InputStream localInputStream = null;
    XMLStreamReader localXMLStreamReader = null;
    try
    {
      URL localURL = new URL(paramString);
      localInputStream = localURL.openStream();
      localXMLStreamReader = XmlUtil.newXMLInputFactory(true).createXMLStreamReader(localInputStream);
      while (localXMLStreamReader.hasNext())
      {
        if ((localXMLStreamReader.isStartElement()) && (NamespaceVersion.resolveAsToken(localXMLStreamReader.getName()) == XmlToken.Policy)) {
          readSinglePolicy(policyReader.readPolicyElement(localXMLStreamReader, paramString), false);
        }
        localXMLStreamReader.next();
      }
      bool = true;
      return bool;
    }
    catch (IOException localIOException)
    {
      bool = false;
      return bool;
    }
    catch (XMLStreamException localXMLStreamException)
    {
      boolean bool = false;
      return bool;
    }
    finally
    {
      PolicyUtils.IO.closeResource(localXMLStreamReader);
      PolicyUtils.IO.closeResource(localInputStream);
    }
  }
  
  public void finished(WSDLParserExtensionContext paramWSDLParserExtensionContext)
  {
    LOGGER.entering(new Object[] { paramWSDLParserExtensionContext });
    Object localObject3;
    if (null != expandQueueHead)
    {
      localObject1 = getUnresolvedUris(false);
      getUnresolvedUris(true);
      localObject2 = new LinkedList();
      for (localObject3 = expandQueueHead; null != localObject3; localObject3 = next) {
        ((LinkedList)localObject2).addFirst(((SafePolicyReader.PolicyRecord)localObject3).getUri());
      }
      getUnresolvedUris(false).addAll((Collection)localObject2);
      expandQueueHead = null;
      getUnresolvedUris(false).addAll((Collection)localObject1);
    }
    Object localObject4;
    while (!getUnresolvedUris(false).isEmpty())
    {
      localObject1 = getUnresolvedUris(false);
      getUnresolvedUris(true);
      localObject2 = ((List)localObject1).iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (String)((Iterator)localObject2).next();
        if (!isPolicyProcessed((String)localObject3))
        {
          localObject4 = (SafePolicyReader.PolicyRecord)getPolicyRecordsPassedBy().get(localObject3);
          if (null == localObject4)
          {
            if (policyReader.getUrlsRead().contains(getBaseUrl((String)localObject3))) {
              LOGGER.logSevereException(new PolicyException(PolicyMessages.WSP_1014_CAN_NOT_FIND_POLICY(localObject3)));
            } else if (readExternalFile(getBaseUrl((String)localObject3))) {
              getUnresolvedUris(false).add(localObject3);
            }
          }
          else
          {
            if (null != unresolvedURIs) {
              getUnresolvedUris(false).addAll(unresolvedURIs);
            }
            addNewPolicyNeeded((String)localObject3, policyModel);
          }
        }
      }
    }
    Object localObject1 = PolicySourceModelContext.createContext();
    Object localObject2 = urisNeeded.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (String)((Iterator)localObject2).next();
      localObject4 = (PolicySourceModel)modelsNeeded.get(localObject3);
      try
      {
        ((PolicySourceModel)localObject4).expand((PolicySourceModelContext)localObject1);
        ((PolicySourceModelContext)localObject1).addModel(new URI((String)localObject3), (PolicySourceModel)localObject4);
      }
      catch (URISyntaxException localURISyntaxException)
      {
        LOGGER.logSevereException(localURISyntaxException);
      }
      catch (PolicyException localPolicyException2)
      {
        LOGGER.logSevereException(localPolicyException2);
      }
    }
    try
    {
      localObject2 = new HashSet();
      localObject3 = paramWSDLParserExtensionContext.getWSDLModel().getServices().values().iterator();
      while (((Iterator)localObject3).hasNext())
      {
        localObject4 = (EditableWSDLService)((Iterator)localObject3).next();
        if (getHandlers4ServiceMap().containsKey(localObject4)) {
          getPolicyMapBuilder().registerHandler(new BuilderHandlerServiceScope(getPolicyURIs((Collection)getHandlers4ServiceMap().get(localObject4), (PolicySourceModelContext)localObject1), getPolicyModels(), localObject4, ((EditableWSDLService)localObject4).getName()));
        }
        Iterator localIterator1 = ((EditableWSDLService)localObject4).getPorts().iterator();
        while (localIterator1.hasNext())
        {
          EditableWSDLPort localEditableWSDLPort = (EditableWSDLPort)localIterator1.next();
          if (getHandlers4PortMap().containsKey(localEditableWSDLPort)) {
            getPolicyMapBuilder().registerHandler(new BuilderHandlerEndpointScope(getPolicyURIs((Collection)getHandlers4PortMap().get(localEditableWSDLPort), (PolicySourceModelContext)localObject1), getPolicyModels(), localEditableWSDLPort, localEditableWSDLPort.getOwner().getName(), localEditableWSDLPort.getName()));
          }
          if (null != localEditableWSDLPort.getBinding())
          {
            if (getHandlers4BindingMap().containsKey(localEditableWSDLPort.getBinding())) {
              getPolicyMapBuilder().registerHandler(new BuilderHandlerEndpointScope(getPolicyURIs((Collection)getHandlers4BindingMap().get(localEditableWSDLPort.getBinding()), (PolicySourceModelContext)localObject1), getPolicyModels(), localEditableWSDLPort.getBinding(), ((EditableWSDLService)localObject4).getName(), localEditableWSDLPort.getName()));
            }
            if (getHandlers4PortTypeMap().containsKey(localEditableWSDLPort.getBinding().getPortType())) {
              getPolicyMapBuilder().registerHandler(new BuilderHandlerEndpointScope(getPolicyURIs((Collection)getHandlers4PortTypeMap().get(localEditableWSDLPort.getBinding().getPortType()), (PolicySourceModelContext)localObject1), getPolicyModels(), localEditableWSDLPort.getBinding().getPortType(), ((EditableWSDLService)localObject4).getName(), localEditableWSDLPort.getName()));
            }
            Iterator localIterator2 = localEditableWSDLPort.getBinding().getBindingOperations().iterator();
            while (localIterator2.hasNext())
            {
              EditableWSDLBoundOperation localEditableWSDLBoundOperation = (EditableWSDLBoundOperation)localIterator2.next();
              EditableWSDLOperation localEditableWSDLOperation = localEditableWSDLBoundOperation.getOperation();
              QName localQName1 = new QName(localEditableWSDLBoundOperation.getBoundPortType().getName().getNamespaceURI(), localEditableWSDLBoundOperation.getName().getLocalPart());
              if (getHandlers4BoundOperationMap().containsKey(localEditableWSDLBoundOperation)) {
                getPolicyMapBuilder().registerHandler(new BuilderHandlerOperationScope(getPolicyURIs((Collection)getHandlers4BoundOperationMap().get(localEditableWSDLBoundOperation), (PolicySourceModelContext)localObject1), getPolicyModels(), localEditableWSDLBoundOperation, ((EditableWSDLService)localObject4).getName(), localEditableWSDLPort.getName(), localQName1));
              }
              if (getHandlers4OperationMap().containsKey(localEditableWSDLOperation)) {
                getPolicyMapBuilder().registerHandler(new BuilderHandlerOperationScope(getPolicyURIs((Collection)getHandlers4OperationMap().get(localEditableWSDLOperation), (PolicySourceModelContext)localObject1), getPolicyModels(), localEditableWSDLOperation, ((EditableWSDLService)localObject4).getName(), localEditableWSDLPort.getName(), localQName1));
              }
              EditableWSDLInput localEditableWSDLInput = localEditableWSDLOperation.getInput();
              if (null != localEditableWSDLInput)
              {
                localObject5 = localEditableWSDLInput.getMessage();
                if ((localObject5 != null) && (getHandlers4MessageMap().containsKey(localObject5))) {
                  ((HashSet)localObject2).add(new BuilderHandlerMessageScope(getPolicyURIs((Collection)getHandlers4MessageMap().get(localObject5), (PolicySourceModelContext)localObject1), getPolicyModels(), localObject5, BuilderHandlerMessageScope.Scope.InputMessageScope, ((EditableWSDLService)localObject4).getName(), localEditableWSDLPort.getName(), localQName1, null));
                }
              }
              if (getHandlers4BindingInputOpMap().containsKey(localEditableWSDLBoundOperation)) {
                getPolicyMapBuilder().registerHandler(new BuilderHandlerMessageScope(getPolicyURIs((Collection)getHandlers4BindingInputOpMap().get(localEditableWSDLBoundOperation), (PolicySourceModelContext)localObject1), getPolicyModels(), localEditableWSDLBoundOperation, BuilderHandlerMessageScope.Scope.InputMessageScope, ((EditableWSDLService)localObject4).getName(), localEditableWSDLPort.getName(), localQName1, null));
              }
              if ((null != localEditableWSDLInput) && (getHandlers4InputMap().containsKey(localEditableWSDLInput))) {
                getPolicyMapBuilder().registerHandler(new BuilderHandlerMessageScope(getPolicyURIs((Collection)getHandlers4InputMap().get(localEditableWSDLInput), (PolicySourceModelContext)localObject1), getPolicyModels(), localEditableWSDLInput, BuilderHandlerMessageScope.Scope.InputMessageScope, ((EditableWSDLService)localObject4).getName(), localEditableWSDLPort.getName(), localQName1, null));
              }
              Object localObject5 = localEditableWSDLOperation.getOutput();
              if (null != localObject5)
              {
                localObject6 = ((EditableWSDLOutput)localObject5).getMessage();
                if ((localObject6 != null) && (getHandlers4MessageMap().containsKey(localObject6))) {
                  ((HashSet)localObject2).add(new BuilderHandlerMessageScope(getPolicyURIs((Collection)getHandlers4MessageMap().get(localObject6), (PolicySourceModelContext)localObject1), getPolicyModels(), localObject6, BuilderHandlerMessageScope.Scope.OutputMessageScope, ((EditableWSDLService)localObject4).getName(), localEditableWSDLPort.getName(), localQName1, null));
                }
              }
              if (getHandlers4BindingOutputOpMap().containsKey(localEditableWSDLBoundOperation)) {
                getPolicyMapBuilder().registerHandler(new BuilderHandlerMessageScope(getPolicyURIs((Collection)getHandlers4BindingOutputOpMap().get(localEditableWSDLBoundOperation), (PolicySourceModelContext)localObject1), getPolicyModels(), localEditableWSDLBoundOperation, BuilderHandlerMessageScope.Scope.OutputMessageScope, ((EditableWSDLService)localObject4).getName(), localEditableWSDLPort.getName(), localQName1, null));
              }
              if ((null != localObject5) && (getHandlers4OutputMap().containsKey(localObject5))) {
                getPolicyMapBuilder().registerHandler(new BuilderHandlerMessageScope(getPolicyURIs((Collection)getHandlers4OutputMap().get(localObject5), (PolicySourceModelContext)localObject1), getPolicyModels(), localObject5, BuilderHandlerMessageScope.Scope.OutputMessageScope, ((EditableWSDLService)localObject4).getName(), localEditableWSDLPort.getName(), localQName1, null));
              }
              Object localObject6 = localEditableWSDLBoundOperation.getFaults().iterator();
              while (((Iterator)localObject6).hasNext())
              {
                EditableWSDLBoundFault localEditableWSDLBoundFault = (EditableWSDLBoundFault)((Iterator)localObject6).next();
                EditableWSDLFault localEditableWSDLFault = localEditableWSDLBoundFault.getFault();
                if (localEditableWSDLFault == null)
                {
                  LOGGER.warning(PolicyMessages.WSP_1021_FAULT_NOT_BOUND(localEditableWSDLBoundFault.getName()));
                }
                else
                {
                  EditableWSDLMessage localEditableWSDLMessage = localEditableWSDLFault.getMessage();
                  QName localQName2 = new QName(localEditableWSDLBoundOperation.getBoundPortType().getName().getNamespaceURI(), localEditableWSDLBoundFault.getName());
                  if ((localEditableWSDLMessage != null) && (getHandlers4MessageMap().containsKey(localEditableWSDLMessage))) {
                    ((HashSet)localObject2).add(new BuilderHandlerMessageScope(getPolicyURIs((Collection)getHandlers4MessageMap().get(localEditableWSDLMessage), (PolicySourceModelContext)localObject1), getPolicyModels(), new WSDLBoundFaultContainer(localEditableWSDLBoundFault, localEditableWSDLBoundOperation), BuilderHandlerMessageScope.Scope.FaultMessageScope, ((EditableWSDLService)localObject4).getName(), localEditableWSDLPort.getName(), localQName1, localQName2));
                  }
                  if (getHandlers4FaultMap().containsKey(localEditableWSDLFault)) {
                    ((HashSet)localObject2).add(new BuilderHandlerMessageScope(getPolicyURIs((Collection)getHandlers4FaultMap().get(localEditableWSDLFault), (PolicySourceModelContext)localObject1), getPolicyModels(), new WSDLBoundFaultContainer(localEditableWSDLBoundFault, localEditableWSDLBoundOperation), BuilderHandlerMessageScope.Scope.FaultMessageScope, ((EditableWSDLService)localObject4).getName(), localEditableWSDLPort.getName(), localQName1, localQName2));
                  }
                  if (getHandlers4BindingFaultOpMap().containsKey(localEditableWSDLBoundFault)) {
                    ((HashSet)localObject2).add(new BuilderHandlerMessageScope(getPolicyURIs((Collection)getHandlers4BindingFaultOpMap().get(localEditableWSDLBoundFault), (PolicySourceModelContext)localObject1), getPolicyModels(), new WSDLBoundFaultContainer(localEditableWSDLBoundFault, localEditableWSDLBoundOperation), BuilderHandlerMessageScope.Scope.FaultMessageScope, ((EditableWSDLService)localObject4).getName(), localEditableWSDLPort.getName(), localQName1, localQName2));
                  }
                }
              }
            }
          }
        }
      }
      localObject3 = ((HashSet)localObject2).iterator();
      while (((Iterator)localObject3).hasNext())
      {
        localObject4 = (BuilderHandlerMessageScope)((Iterator)localObject3).next();
        getPolicyMapBuilder().registerHandler((BuilderHandler)localObject4);
      }
    }
    catch (PolicyException localPolicyException1)
    {
      LOGGER.logSevereException(localPolicyException1);
    }
    LOGGER.exiting();
  }
  
  public void postFinished(WSDLParserExtensionContext paramWSDLParserExtensionContext)
  {
    EditableWSDLModel localEditableWSDLModel = paramWSDLParserExtensionContext.getWSDLModel();
    PolicyMap localPolicyMap;
    try
    {
      if (paramWSDLParserExtensionContext.isClientSide()) {
        localPolicyMap = paramWSDLParserExtensionContext.getPolicyResolver().resolve(new PolicyResolver.ClientContext(policyBuilder.getPolicyMap(new PolicyMapMutator[0]), paramWSDLParserExtensionContext.getContainer()));
      } else {
        localPolicyMap = paramWSDLParserExtensionContext.getPolicyResolver().resolve(new PolicyResolver.ServerContext(policyBuilder.getPolicyMap(new PolicyMapMutator[0]), paramWSDLParserExtensionContext.getContainer(), null, new PolicyMapMutator[0]));
      }
      localEditableWSDLModel.setPolicyMap(localPolicyMap);
    }
    catch (PolicyException localPolicyException1)
    {
      LOGGER.logSevereException(localPolicyException1);
      throw ((WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1007_POLICY_EXCEPTION_WHILE_FINISHING_PARSING_WSDL(), localPolicyException1)));
    }
    try
    {
      PolicyUtil.configureModel(localEditableWSDLModel, localPolicyMap);
    }
    catch (PolicyException localPolicyException2)
    {
      LOGGER.logSevereException(localPolicyException2);
      throw ((WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1012_FAILED_CONFIGURE_WSDL_MODEL(), localPolicyException2)));
    }
    LOGGER.exiting();
  }
  
  private String[] getPolicyURIsFromAttr(XMLStreamReader paramXMLStreamReader)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    for (NamespaceVersion localNamespaceVersion : NamespaceVersion.values())
    {
      String str = paramXMLStreamReader.getAttributeValue(localNamespaceVersion.toString(), XmlToken.PolicyUris.toString());
      if (str != null) {
        localStringBuilder.append(str).append(" ");
      }
    }
    return localStringBuilder.length() > 0 ? localStringBuilder.toString().split("[\\n ]+") : null;
  }
  
  static enum HandlerType
  {
    PolicyUri,  AnonymousPolicyId;
    
    private HandlerType() {}
  }
  
  static final class PolicyRecordHandler
  {
    String handler;
    PolicyWSDLParserExtension.HandlerType type;
    
    PolicyRecordHandler(PolicyWSDLParserExtension.HandlerType paramHandlerType, String paramString)
    {
      type = paramHandlerType;
      handler = paramString;
    }
    
    PolicyWSDLParserExtension.HandlerType getType()
    {
      return type;
    }
    
    String getHandler()
    {
      return handler;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\jaxws\PolicyWSDLParserExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */