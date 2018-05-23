package com.sun.xml.internal.ws.server.provider;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.server.AsyncProvider;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.spi.db.BindingHelper;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.activation.DataSource;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.ws.Provider;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPBinding;

final class ProviderEndpointModel<T>
{
  final boolean isAsync;
  @NotNull
  final Service.Mode mode;
  @NotNull
  final Class datatype;
  @NotNull
  final Class implClass;
  
  ProviderEndpointModel(Class<T> paramClass, WSBinding paramWSBinding)
  {
    assert (paramClass != null);
    assert (paramWSBinding != null);
    implClass = paramClass;
    mode = getServiceMode(paramClass);
    Class localClass1 = (paramWSBinding instanceof SOAPBinding) ? SOAPMessage.class : DataSource.class;
    isAsync = AsyncProvider.class.isAssignableFrom(paramClass);
    Class localClass2 = isAsync ? AsyncProvider.class : Provider.class;
    Type localType = BindingHelper.getBaseType(paramClass, localClass2);
    if (localType == null) {
      throw new WebServiceException(ServerMessages.NOT_IMPLEMENT_PROVIDER(paramClass.getName()));
    }
    if (!(localType instanceof ParameterizedType)) {
      throw new WebServiceException(ServerMessages.PROVIDER_NOT_PARAMETERIZED(paramClass.getName()));
    }
    ParameterizedType localParameterizedType = (ParameterizedType)localType;
    Type[] arrayOfType = localParameterizedType.getActualTypeArguments();
    if (!(arrayOfType[0] instanceof Class)) {
      throw new WebServiceException(ServerMessages.PROVIDER_INVALID_PARAMETER_TYPE(paramClass.getName(), arrayOfType[0]));
    }
    datatype = ((Class)arrayOfType[0]);
    if ((mode == Service.Mode.PAYLOAD) && (datatype != Source.class)) {
      throw new IllegalArgumentException("Illeagal combination - Mode.PAYLOAD and Provider<" + localClass1.getName() + ">");
    }
  }
  
  private static Service.Mode getServiceMode(Class<?> paramClass)
  {
    ServiceMode localServiceMode = (ServiceMode)paramClass.getAnnotation(ServiceMode.class);
    return localServiceMode == null ? Service.Mode.PAYLOAD : localServiceMode.value();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\provider\ProviderEndpointModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */