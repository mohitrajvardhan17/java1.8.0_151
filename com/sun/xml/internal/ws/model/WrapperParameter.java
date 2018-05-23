package com.sun.xml.internal.ws.model;

import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.jws.WebParam.Mode;

public class WrapperParameter
  extends ParameterImpl
{
  protected final List<ParameterImpl> wrapperChildren = new ArrayList();
  
  public WrapperParameter(JavaMethodImpl paramJavaMethodImpl, TypeInfo paramTypeInfo, WebParam.Mode paramMode, int paramInt)
  {
    super(paramJavaMethodImpl, paramTypeInfo, paramMode, paramInt);
    paramTypeInfo.properties().put(WrapperParameter.class.getName(), this);
  }
  
  /**
   * @deprecated
   */
  public boolean isWrapperStyle()
  {
    return true;
  }
  
  public List<ParameterImpl> getWrapperChildren()
  {
    return wrapperChildren;
  }
  
  public void addWrapperChild(ParameterImpl paramParameterImpl)
  {
    wrapperChildren.add(paramParameterImpl);
    wrapper = this;
    assert (paramParameterImpl.getBinding() == ParameterBinding.BODY);
  }
  
  public void clear()
  {
    wrapperChildren.clear();
  }
  
  void fillTypes(List<TypeInfo> paramList)
  {
    super.fillTypes(paramList);
    if (WrapperComposite.class.equals(getTypeInfotype))
    {
      Iterator localIterator = wrapperChildren.iterator();
      while (localIterator.hasNext())
      {
        ParameterImpl localParameterImpl = (ParameterImpl)localIterator.next();
        localParameterImpl.fillTypes(paramList);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\WrapperParameter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */