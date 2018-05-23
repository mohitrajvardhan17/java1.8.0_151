package com.sun.xml.internal.bind.v2.model.core;

import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.impl.ModelBuilderI;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public final class Ref<T, C>
{
  public final T type;
  public final Adapter<T, C> adapter;
  public final boolean valueList;
  
  public Ref(T paramT)
  {
    this(paramT, null, false);
  }
  
  public Ref(T paramT, Adapter<T, C> paramAdapter, boolean paramBoolean)
  {
    adapter = paramAdapter;
    if (paramAdapter != null) {
      paramT = defaultType;
    }
    type = paramT;
    valueList = paramBoolean;
  }
  
  public Ref(ModelBuilderI<T, C, ?, ?> paramModelBuilderI, T paramT, XmlJavaTypeAdapter paramXmlJavaTypeAdapter, XmlList paramXmlList)
  {
    this(paramModelBuilderI.getReader(), paramModelBuilderI.getNavigator(), paramT, paramXmlJavaTypeAdapter, paramXmlList);
  }
  
  public Ref(AnnotationReader<T, C, ?, ?> paramAnnotationReader, Navigator<T, C, ?, ?> paramNavigator, T paramT, XmlJavaTypeAdapter paramXmlJavaTypeAdapter, XmlList paramXmlList)
  {
    Adapter localAdapter = null;
    if (paramXmlJavaTypeAdapter != null)
    {
      localAdapter = new Adapter(paramXmlJavaTypeAdapter, paramAnnotationReader, paramNavigator);
      paramT = defaultType;
    }
    type = paramT;
    adapter = localAdapter;
    valueList = (paramXmlList != null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\core\Ref.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */