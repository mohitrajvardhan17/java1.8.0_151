package com.sun.xml.internal.bind.v2.model.core;

import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class Adapter<TypeT, ClassDeclT>
{
  public final ClassDeclT adapterType;
  public final TypeT defaultType;
  public final TypeT customType;
  
  public Adapter(XmlJavaTypeAdapter paramXmlJavaTypeAdapter, AnnotationReader<TypeT, ClassDeclT, ?, ?> paramAnnotationReader, Navigator<TypeT, ClassDeclT, ?, ?> paramNavigator)
  {
    this(paramNavigator.asDecl(paramAnnotationReader.getClassValue(paramXmlJavaTypeAdapter, "value")), paramNavigator);
  }
  
  public Adapter(ClassDeclT paramClassDeclT, Navigator<TypeT, ClassDeclT, ?, ?> paramNavigator)
  {
    adapterType = paramClassDeclT;
    Object localObject = paramNavigator.getBaseClass(paramNavigator.use(paramClassDeclT), paramNavigator.asDecl(XmlAdapter.class));
    assert (localObject != null);
    if (paramNavigator.isParameterizedType(localObject)) {
      defaultType = paramNavigator.getTypeArgument(localObject, 0);
    } else {
      defaultType = paramNavigator.ref(Object.class);
    }
    if (paramNavigator.isParameterizedType(localObject)) {
      customType = paramNavigator.getTypeArgument(localObject, 1);
    } else {
      customType = paramNavigator.ref(Object.class);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\core\Adapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */