package com.sun.xml.internal.ws.api.config.management.policy;

import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.internal.ws.policy.spi.AssertionCreationException;
import com.sun.xml.internal.ws.resources.ManagementMessages;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public class ManagedServiceAssertion
  extends ManagementAssertion
{
  public static final QName MANAGED_SERVICE_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "ManagedService");
  private static final QName COMMUNICATION_SERVER_IMPLEMENTATIONS_PARAMETER_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "CommunicationServerImplementations");
  private static final QName COMMUNICATION_SERVER_IMPLEMENTATION_PARAMETER_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "CommunicationServerImplementation");
  private static final QName CONFIGURATOR_IMPLEMENTATION_PARAMETER_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "ConfiguratorImplementation");
  private static final QName CONFIG_SAVER_IMPLEMENTATION_PARAMETER_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "ConfigSaverImplementation");
  private static final QName CONFIG_READER_IMPLEMENTATION_PARAMETER_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "ConfigReaderImplementation");
  private static final QName CLASS_NAME_ATTRIBUTE_QNAME = new QName("className");
  private static final QName ENDPOINT_DISPOSE_DELAY_ATTRIBUTE_QNAME = new QName("endpointDisposeDelay");
  private static final Logger LOGGER = Logger.getLogger(ManagedServiceAssertion.class);
  
  public static ManagedServiceAssertion getAssertion(WSEndpoint paramWSEndpoint)
    throws WebServiceException
  {
    LOGGER.entering(new Object[] { paramWSEndpoint });
    PolicyMap localPolicyMap = paramWSEndpoint.getPolicyMap();
    ManagedServiceAssertion localManagedServiceAssertion = (ManagedServiceAssertion)ManagementAssertion.getAssertion(MANAGED_SERVICE_QNAME, localPolicyMap, paramWSEndpoint.getServiceName(), paramWSEndpoint.getPortName(), ManagedServiceAssertion.class);
    LOGGER.exiting(localManagedServiceAssertion);
    return localManagedServiceAssertion;
  }
  
  public ManagedServiceAssertion(AssertionData paramAssertionData, Collection<PolicyAssertion> paramCollection)
    throws AssertionCreationException
  {
    super(MANAGED_SERVICE_QNAME, paramAssertionData, paramCollection);
  }
  
  public boolean isManagementEnabled()
  {
    String str = getAttributeValue(MANAGEMENT_ATTRIBUTE_QNAME);
    boolean bool = true;
    if (str != null) {
      if (str.trim().toLowerCase().equals("on")) {
        bool = true;
      } else {
        bool = Boolean.parseBoolean(str);
      }
    }
    return bool;
  }
  
  public long getEndpointDisposeDelay(long paramLong)
    throws WebServiceException
  {
    long l = paramLong;
    String str = getAttributeValue(ENDPOINT_DISPOSE_DELAY_ATTRIBUTE_QNAME);
    if (str != null) {
      try
      {
        l = Long.parseLong(str);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw ((WebServiceException)LOGGER.logSevereException(new WebServiceException(ManagementMessages.WSM_1008_EXPECTED_INTEGER_DISPOSE_DELAY_VALUE(str), localNumberFormatException)));
      }
    }
    return l;
  }
  
  public Collection<ImplementationRecord> getCommunicationServerImplementations()
  {
    LinkedList localLinkedList = new LinkedList();
    Iterator localIterator1 = getParametersIterator();
    while (localIterator1.hasNext())
    {
      PolicyAssertion localPolicyAssertion1 = (PolicyAssertion)localIterator1.next();
      if (COMMUNICATION_SERVER_IMPLEMENTATIONS_PARAMETER_QNAME.equals(localPolicyAssertion1.getName()))
      {
        Iterator localIterator2 = localPolicyAssertion1.getParametersIterator();
        if (!localIterator2.hasNext()) {
          throw ((WebServiceException)LOGGER.logSevereException(new WebServiceException(ManagementMessages.WSM_1005_EXPECTED_COMMUNICATION_CHILD())));
        }
        while (localIterator2.hasNext())
        {
          PolicyAssertion localPolicyAssertion2 = (PolicyAssertion)localIterator2.next();
          if (COMMUNICATION_SERVER_IMPLEMENTATION_PARAMETER_QNAME.equals(localPolicyAssertion2.getName())) {
            localLinkedList.add(getImplementation(localPolicyAssertion2));
          } else {
            throw ((WebServiceException)LOGGER.logSevereException(new WebServiceException(ManagementMessages.WSM_1004_EXPECTED_XML_TAG(COMMUNICATION_SERVER_IMPLEMENTATION_PARAMETER_QNAME, localPolicyAssertion2.getName()))));
          }
        }
      }
    }
    return localLinkedList;
  }
  
  public ImplementationRecord getConfiguratorImplementation()
  {
    return findImplementation(CONFIGURATOR_IMPLEMENTATION_PARAMETER_QNAME);
  }
  
  public ImplementationRecord getConfigSaverImplementation()
  {
    return findImplementation(CONFIG_SAVER_IMPLEMENTATION_PARAMETER_QNAME);
  }
  
  public ImplementationRecord getConfigReaderImplementation()
  {
    return findImplementation(CONFIG_READER_IMPLEMENTATION_PARAMETER_QNAME);
  }
  
  private ImplementationRecord findImplementation(QName paramQName)
  {
    Iterator localIterator = getParametersIterator();
    while (localIterator.hasNext())
    {
      PolicyAssertion localPolicyAssertion = (PolicyAssertion)localIterator.next();
      if (paramQName.equals(localPolicyAssertion.getName())) {
        return getImplementation(localPolicyAssertion);
      }
    }
    return null;
  }
  
  private ImplementationRecord getImplementation(PolicyAssertion paramPolicyAssertion)
  {
    String str1 = paramPolicyAssertion.getAttributeValue(CLASS_NAME_ATTRIBUTE_QNAME);
    HashMap localHashMap = new HashMap();
    Iterator localIterator1 = paramPolicyAssertion.getParametersIterator();
    LinkedList localLinkedList = new LinkedList();
    while (localIterator1.hasNext())
    {
      PolicyAssertion localPolicyAssertion1 = (PolicyAssertion)localIterator1.next();
      QName localQName = localPolicyAssertion1.getName();
      Object localObject;
      if (localPolicyAssertion1.hasParameters())
      {
        localObject = new HashMap();
        Iterator localIterator2 = localPolicyAssertion1.getParametersIterator();
        while (localIterator2.hasNext())
        {
          PolicyAssertion localPolicyAssertion2 = (PolicyAssertion)localIterator2.next();
          String str2 = localPolicyAssertion2.getValue();
          if (str2 != null) {
            str2 = str2.trim();
          }
          ((Map)localObject).put(localPolicyAssertion2.getName(), str2);
        }
        localLinkedList.add(new NestedParameters(localQName, (Map)localObject, null));
      }
      else
      {
        localObject = localPolicyAssertion1.getValue();
        if (localObject != null) {
          localObject = ((String)localObject).trim();
        }
        localHashMap.put(localQName, localObject);
      }
    }
    return new ImplementationRecord(str1, localHashMap, localLinkedList);
  }
  
  public static class ImplementationRecord
  {
    private final String implementation;
    private final Map<QName, String> parameters;
    private final Collection<ManagedServiceAssertion.NestedParameters> nestedParameters;
    
    protected ImplementationRecord(String paramString, Map<QName, String> paramMap, Collection<ManagedServiceAssertion.NestedParameters> paramCollection)
    {
      implementation = paramString;
      parameters = paramMap;
      nestedParameters = paramCollection;
    }
    
    public String getImplementation()
    {
      return implementation;
    }
    
    public Map<QName, String> getParameters()
    {
      return parameters;
    }
    
    public Collection<ManagedServiceAssertion.NestedParameters> getNestedParameters()
    {
      return nestedParameters;
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == null) {
        return false;
      }
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      ImplementationRecord localImplementationRecord = (ImplementationRecord)paramObject;
      if (implementation == null ? implementation != null : !implementation.equals(implementation)) {
        return false;
      }
      if ((parameters != parameters) && ((parameters == null) || (!parameters.equals(parameters)))) {
        return false;
      }
      return (nestedParameters == nestedParameters) || ((nestedParameters != null) && (nestedParameters.equals(nestedParameters)));
    }
    
    public int hashCode()
    {
      int i = 3;
      i = 53 * i + (implementation != null ? implementation.hashCode() : 0);
      i = 53 * i + (parameters != null ? parameters.hashCode() : 0);
      i = 53 * i + (nestedParameters != null ? nestedParameters.hashCode() : 0);
      return i;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder("ImplementationRecord: ");
      localStringBuilder.append("implementation = \"").append(implementation).append("\", ");
      localStringBuilder.append("parameters = \"").append(parameters).append("\", ");
      localStringBuilder.append("nested parameters = \"").append(nestedParameters).append("\"");
      return localStringBuilder.toString();
    }
  }
  
  public static class NestedParameters
  {
    private final QName name;
    private final Map<QName, String> parameters;
    
    private NestedParameters(QName paramQName, Map<QName, String> paramMap)
    {
      name = paramQName;
      parameters = paramMap;
    }
    
    public QName getName()
    {
      return name;
    }
    
    public Map<QName, String> getParameters()
    {
      return parameters;
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == null) {
        return false;
      }
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      NestedParameters localNestedParameters = (NestedParameters)paramObject;
      if (name == null ? name != null : !name.equals(name)) {
        return false;
      }
      return (parameters == parameters) || ((parameters != null) && (parameters.equals(parameters)));
    }
    
    public int hashCode()
    {
      int i = 5;
      i = 59 * i + (name != null ? name.hashCode() : 0);
      i = 59 * i + (parameters != null ? parameters.hashCode() : 0);
      return i;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder("NestedParameters: ");
      localStringBuilder.append("name = \"").append(name).append("\", ");
      localStringBuilder.append("parameters = \"").append(parameters).append("\"");
      return localStringBuilder.toString();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\config\management\policy\ManagedServiceAssertion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */