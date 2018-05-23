package com.sun.xml.internal.ws.assembler;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.internal.ws.resources.TubelineassemblyMessages;
import com.sun.xml.internal.ws.runtime.config.TubeFactoryConfig;
import com.sun.xml.internal.ws.runtime.config.TubeFactoryList;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.xml.namespace.QName;

final class TubelineAssemblyController
{
  private final MetroConfigName metroConfigName;
  
  TubelineAssemblyController(MetroConfigName paramMetroConfigName)
  {
    metroConfigName = paramMetroConfigName;
  }
  
  Collection<TubeCreator> getTubeCreators(ClientTubelineAssemblyContext paramClientTubelineAssemblyContext)
  {
    URI localURI;
    if (paramClientTubelineAssemblyContext.getPortInfo() != null) {
      localURI = createEndpointComponentUri(paramClientTubelineAssemblyContext.getPortInfo().getServiceName(), paramClientTubelineAssemblyContext.getPortInfo().getPortName());
    } else {
      localURI = null;
    }
    MetroConfigLoader localMetroConfigLoader = new MetroConfigLoader(paramClientTubelineAssemblyContext.getContainer(), metroConfigName);
    return initializeTubeCreators(localMetroConfigLoader.getClientSideTubeFactories(localURI));
  }
  
  Collection<TubeCreator> getTubeCreators(DefaultServerTubelineAssemblyContext paramDefaultServerTubelineAssemblyContext)
  {
    URI localURI;
    if (paramDefaultServerTubelineAssemblyContext.getEndpoint() != null) {
      localURI = createEndpointComponentUri(paramDefaultServerTubelineAssemblyContext.getEndpoint().getServiceName(), paramDefaultServerTubelineAssemblyContext.getEndpoint().getPortName());
    } else {
      localURI = null;
    }
    MetroConfigLoader localMetroConfigLoader = new MetroConfigLoader(paramDefaultServerTubelineAssemblyContext.getEndpoint().getContainer(), metroConfigName);
    return initializeTubeCreators(localMetroConfigLoader.getEndpointSideTubeFactories(localURI));
  }
  
  private Collection<TubeCreator> initializeTubeCreators(TubeFactoryList paramTubeFactoryList)
  {
    ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
    LinkedList localLinkedList = new LinkedList();
    Iterator localIterator = paramTubeFactoryList.getTubeFactoryConfigs().iterator();
    while (localIterator.hasNext())
    {
      TubeFactoryConfig localTubeFactoryConfig = (TubeFactoryConfig)localIterator.next();
      localLinkedList.addFirst(new TubeCreator(localTubeFactoryConfig, localClassLoader));
    }
    return localLinkedList;
  }
  
  private URI createEndpointComponentUri(@NotNull QName paramQName1, @NotNull QName paramQName2)
  {
    StringBuilder localStringBuilder = new StringBuilder(paramQName1.getNamespaceURI()).append("#wsdl11.port(").append(paramQName1.getLocalPart()).append('/').append(paramQName2.getLocalPart()).append(')');
    try
    {
      return new URI(localStringBuilder.toString());
    }
    catch (URISyntaxException localURISyntaxException)
    {
      Logger.getLogger(TubelineAssemblyController.class).warning(TubelineassemblyMessages.MASM_0020_ERROR_CREATING_URI_FROM_GENERATED_STRING(localStringBuilder.toString()), localURISyntaxException);
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\assembler\TubelineAssemblyController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */