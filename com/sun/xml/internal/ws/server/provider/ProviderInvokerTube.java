package com.sun.xml.internal.ws.server.provider;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.Invoker;
import com.sun.xml.internal.ws.api.server.ProviderInvokerTubeFactory;
import com.sun.xml.internal.ws.binding.SOAPBindingImpl;
import com.sun.xml.internal.ws.server.InvokerTube;
import javax.xml.ws.Provider;

public abstract class ProviderInvokerTube<T>
  extends InvokerTube<Provider<T>>
{
  protected ProviderArgumentsBuilder<T> argsBuilder;
  
  ProviderInvokerTube(Invoker paramInvoker, ProviderArgumentsBuilder<T> paramProviderArgumentsBuilder)
  {
    super(paramInvoker);
    argsBuilder = paramProviderArgumentsBuilder;
  }
  
  public static <T> ProviderInvokerTube<T> create(Class<T> paramClass, WSBinding paramWSBinding, Invoker paramInvoker, Container paramContainer)
  {
    ProviderEndpointModel localProviderEndpointModel = new ProviderEndpointModel(paramClass, paramWSBinding);
    ProviderArgumentsBuilder localProviderArgumentsBuilder = ProviderArgumentsBuilder.create(localProviderEndpointModel, paramWSBinding);
    if ((paramWSBinding instanceof SOAPBindingImpl)) {
      ((SOAPBindingImpl)paramWSBinding).setMode(mode);
    }
    return ProviderInvokerTubeFactory.create(null, paramContainer, paramClass, paramInvoker, localProviderArgumentsBuilder, isAsync);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\provider\ProviderInvokerTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */