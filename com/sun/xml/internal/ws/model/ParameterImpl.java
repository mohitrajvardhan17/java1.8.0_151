package com.sun.xml.internal.ws.model;

import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.Parameter;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.soap.SOAPBinding;
import com.sun.xml.internal.ws.spi.db.RepeatedElementBridge;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.util.List;
import javax.jws.WebParam.Mode;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;

public class ParameterImpl
  implements Parameter
{
  private ParameterBinding binding;
  private ParameterBinding outBinding;
  private String partName;
  private final int index;
  private final WebParam.Mode mode;
  /**
   * @deprecated
   */
  private TypeReference typeReference;
  private TypeInfo typeInfo;
  private QName name;
  private final JavaMethodImpl parent;
  WrapperParameter wrapper;
  TypeInfo itemTypeInfo;
  
  public ParameterImpl(JavaMethodImpl paramJavaMethodImpl, TypeInfo paramTypeInfo, WebParam.Mode paramMode, int paramInt)
  {
    assert (paramTypeInfo != null);
    typeInfo = paramTypeInfo;
    name = tagName;
    mode = paramMode;
    index = paramInt;
    parent = paramJavaMethodImpl;
  }
  
  public AbstractSEIModelImpl getOwner()
  {
    return parent.owner;
  }
  
  public JavaMethod getParent()
  {
    return parent;
  }
  
  public QName getName()
  {
    return name;
  }
  
  public XMLBridge getXMLBridge()
  {
    return getOwner().getXMLBridge(typeInfo);
  }
  
  public XMLBridge getInlinedRepeatedElementBridge()
  {
    TypeInfo localTypeInfo = getItemType();
    if (localTypeInfo != null)
    {
      XMLBridge localXMLBridge = getOwner().getXMLBridge(localTypeInfo);
      if (localXMLBridge != null) {
        return new RepeatedElementBridge(typeInfo, localXMLBridge);
      }
    }
    return null;
  }
  
  public TypeInfo getItemType()
  {
    if (itemTypeInfo != null) {
      return itemTypeInfo;
    }
    if ((parent.getBinding().isRpcLit()) || (wrapper == null)) {
      return null;
    }
    if (!WrapperComposite.class.equals(wrapper.getTypeInfo().type)) {
      return null;
    }
    if (!getBinding().isBody()) {
      return null;
    }
    itemTypeInfo = typeInfo.getItemType();
    return itemTypeInfo;
  }
  
  /**
   * @deprecated
   */
  public Bridge getBridge()
  {
    return getOwner().getBridge(typeReference);
  }
  
  /**
   * @deprecated
   */
  protected Bridge getBridge(TypeReference paramTypeReference)
  {
    return getOwner().getBridge(paramTypeReference);
  }
  
  /**
   * @deprecated
   */
  public TypeReference getTypeReference()
  {
    return typeReference;
  }
  
  public TypeInfo getTypeInfo()
  {
    return typeInfo;
  }
  
  /**
   * @deprecated
   */
  void setTypeReference(TypeReference paramTypeReference)
  {
    typeReference = paramTypeReference;
    name = tagName;
  }
  
  public WebParam.Mode getMode()
  {
    return mode;
  }
  
  public int getIndex()
  {
    return index;
  }
  
  public boolean isWrapperStyle()
  {
    return false;
  }
  
  public boolean isReturnValue()
  {
    return index == -1;
  }
  
  public ParameterBinding getBinding()
  {
    if (binding == null) {
      return ParameterBinding.BODY;
    }
    return binding;
  }
  
  public void setBinding(ParameterBinding paramParameterBinding)
  {
    binding = paramParameterBinding;
  }
  
  public void setInBinding(ParameterBinding paramParameterBinding)
  {
    binding = paramParameterBinding;
  }
  
  public void setOutBinding(ParameterBinding paramParameterBinding)
  {
    outBinding = paramParameterBinding;
  }
  
  public ParameterBinding getInBinding()
  {
    return binding;
  }
  
  public ParameterBinding getOutBinding()
  {
    if (outBinding == null) {
      return binding;
    }
    return outBinding;
  }
  
  public boolean isIN()
  {
    return mode == WebParam.Mode.IN;
  }
  
  public boolean isOUT()
  {
    return mode == WebParam.Mode.OUT;
  }
  
  public boolean isINOUT()
  {
    return mode == WebParam.Mode.INOUT;
  }
  
  public boolean isResponse()
  {
    return index == -1;
  }
  
  public Object getHolderValue(Object paramObject)
  {
    if ((paramObject != null) && ((paramObject instanceof Holder))) {
      return value;
    }
    return paramObject;
  }
  
  public String getPartName()
  {
    if (partName == null) {
      return name.getLocalPart();
    }
    return partName;
  }
  
  public void setPartName(String paramString)
  {
    partName = paramString;
  }
  
  void fillTypes(List<TypeInfo> paramList)
  {
    TypeInfo localTypeInfo = getItemType();
    paramList.add(localTypeInfo != null ? localTypeInfo : getTypeInfo());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\ParameterImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */