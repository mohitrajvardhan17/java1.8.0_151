package com.sun.xml.internal.ws.client.sei;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.message.jaxb.JAXBMessage;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.model.WrapperParameter;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.DatabindingException;
import com.sun.xml.internal.ws.spi.db.PropertyAccessor;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

abstract class BodyBuilder
{
  static final BodyBuilder EMPTY_SOAP11 = new Empty(SOAPVersion.SOAP_11);
  static final BodyBuilder EMPTY_SOAP12 = new Empty(SOAPVersion.SOAP_12);
  
  BodyBuilder() {}
  
  abstract Message createMessage(Object[] paramArrayOfObject);
  
  static final class Bare
    extends BodyBuilder.JAXB
  {
    private final int methodPos;
    private final ValueGetter getter;
    
    Bare(ParameterImpl paramParameterImpl, SOAPVersion paramSOAPVersion, ValueGetter paramValueGetter)
    {
      super(paramSOAPVersion);
      methodPos = paramParameterImpl.getIndex();
      getter = paramValueGetter;
    }
    
    Object build(Object[] paramArrayOfObject)
    {
      return getter.get(paramArrayOfObject[methodPos]);
    }
  }
  
  static final class DocLit
    extends BodyBuilder.Wrapped
  {
    private final PropertyAccessor[] accessors;
    private final Class wrapper;
    private BindingContext bindingContext;
    private boolean dynamicWrapper;
    
    DocLit(WrapperParameter paramWrapperParameter, SOAPVersion paramSOAPVersion, ValueGetterFactory paramValueGetterFactory)
    {
      super(paramSOAPVersion, paramValueGetterFactory);
      bindingContext = paramWrapperParameter.getOwner().getBindingContext();
      wrapper = ((Class)getXMLBridgegetTypeInfotype);
      dynamicWrapper = WrapperComposite.class.equals(wrapper);
      parameterBridges = new XMLBridge[children.size()];
      accessors = new PropertyAccessor[children.size()];
      for (int i = 0; i < accessors.length; i++)
      {
        ParameterImpl localParameterImpl = (ParameterImpl)children.get(i);
        QName localQName = localParameterImpl.getName();
        if (dynamicWrapper)
        {
          parameterBridges[i] = ((ParameterImpl)children.get(i)).getInlinedRepeatedElementBridge();
          if (parameterBridges[i] == null) {
            parameterBridges[i] = ((ParameterImpl)children.get(i)).getXMLBridge();
          }
        }
        else
        {
          try
          {
            accessors[i] = localParameterImpl.getOwner().getBindingContext().getElementPropertyAccessor(wrapper, localQName.getNamespaceURI(), localQName.getLocalPart());
          }
          catch (JAXBException localJAXBException)
          {
            throw new WebServiceException(wrapper + " do not have a property of the name " + localQName, localJAXBException);
          }
        }
      }
    }
    
    Object build(Object[] paramArrayOfObject)
    {
      if (dynamicWrapper) {
        return buildWrapperComposite(paramArrayOfObject);
      }
      try
      {
        Object localObject1 = bindingContext.newWrapperInstace(wrapper);
        for (int i = indices.length - 1; i >= 0; i--) {
          accessors[i].set(localObject1, getters[i].get(paramArrayOfObject[indices[i]]));
        }
        return localObject1;
      }
      catch (InstantiationException localInstantiationException)
      {
        localObject2 = new InstantiationError(localInstantiationException.getMessage());
        ((Error)localObject2).initCause(localInstantiationException);
        throw ((Throwable)localObject2);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        Object localObject2 = new IllegalAccessError(localIllegalAccessException.getMessage());
        ((Error)localObject2).initCause(localIllegalAccessException);
        throw ((Throwable)localObject2);
      }
      catch (DatabindingException localDatabindingException)
      {
        throw new WebServiceException(localDatabindingException);
      }
    }
  }
  
  private static final class Empty
    extends BodyBuilder
  {
    private final SOAPVersion soapVersion;
    
    public Empty(SOAPVersion paramSOAPVersion)
    {
      soapVersion = paramSOAPVersion;
    }
    
    Message createMessage(Object[] paramArrayOfObject)
    {
      return Messages.createEmpty(soapVersion);
    }
  }
  
  private static abstract class JAXB
    extends BodyBuilder
  {
    private final XMLBridge bridge;
    private final SOAPVersion soapVersion;
    
    protected JAXB(XMLBridge paramXMLBridge, SOAPVersion paramSOAPVersion)
    {
      assert (paramXMLBridge != null);
      bridge = paramXMLBridge;
      soapVersion = paramSOAPVersion;
    }
    
    final Message createMessage(Object[] paramArrayOfObject)
    {
      return JAXBMessage.create(bridge, build(paramArrayOfObject), soapVersion);
    }
    
    abstract Object build(Object[] paramArrayOfObject);
  }
  
  static final class RpcLit
    extends BodyBuilder.Wrapped
  {
    RpcLit(WrapperParameter paramWrapperParameter, SOAPVersion paramSOAPVersion, ValueGetterFactory paramValueGetterFactory)
    {
      super(paramSOAPVersion, paramValueGetterFactory);
      assert (getTypeInfotype == WrapperComposite.class);
      parameterBridges = new XMLBridge[children.size()];
      for (int i = 0; i < parameterBridges.length; i++) {
        parameterBridges[i] = ((ParameterImpl)children.get(i)).getXMLBridge();
      }
    }
    
    Object build(Object[] paramArrayOfObject)
    {
      return buildWrapperComposite(paramArrayOfObject);
    }
  }
  
  static abstract class Wrapped
    extends BodyBuilder.JAXB
  {
    protected final int[] indices;
    protected final ValueGetter[] getters;
    protected XMLBridge[] parameterBridges;
    protected List<ParameterImpl> children;
    
    protected Wrapped(WrapperParameter paramWrapperParameter, SOAPVersion paramSOAPVersion, ValueGetterFactory paramValueGetterFactory)
    {
      super(paramSOAPVersion);
      children = paramWrapperParameter.getWrapperChildren();
      indices = new int[children.size()];
      getters = new ValueGetter[children.size()];
      for (int i = 0; i < indices.length; i++)
      {
        ParameterImpl localParameterImpl = (ParameterImpl)children.get(i);
        indices[i] = localParameterImpl.getIndex();
        getters[i] = paramValueGetterFactory.get(localParameterImpl);
      }
    }
    
    protected WrapperComposite buildWrapperComposite(Object[] paramArrayOfObject)
    {
      WrapperComposite localWrapperComposite = new WrapperComposite();
      bridges = parameterBridges;
      values = new Object[parameterBridges.length];
      for (int i = indices.length - 1; i >= 0; i--)
      {
        Object localObject = getters[i].get(paramArrayOfObject[indices[i]]);
        if (localObject == null) {
          throw new WebServiceException("Method Parameter: " + ((ParameterImpl)children.get(i)).getName() + " cannot be null. This is BP 1.1 R2211 violation.");
        }
        values[i] = localObject;
      }
      return localWrapperComposite;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\sei\BodyBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */