package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.annotation.MethodLocatable;
import com.sun.xml.internal.bind.v2.model.core.RegistryInfo;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.runtime.Location;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.annotation.XmlElementDecl;

final class RegistryInfoImpl<T, C, F, M>
  implements Locatable, RegistryInfo<T, C>
{
  final C registryClass;
  private final Locatable upstream;
  private final Navigator<T, C, F, M> nav;
  private final Set<TypeInfo<T, C>> references = new LinkedHashSet();
  
  RegistryInfoImpl(ModelBuilder<T, C, F, M> paramModelBuilder, Locatable paramLocatable, C paramC)
  {
    nav = nav;
    registryClass = paramC;
    upstream = paramLocatable;
    registries.put(getPackageName(), this);
    if (nav.getDeclaredField(paramC, "_useJAXBProperties") != null)
    {
      paramModelBuilder.reportError(new IllegalAnnotationException(Messages.MISSING_JAXB_PROPERTIES.format(new Object[] { getPackageName() }), this));
      return;
    }
    Iterator localIterator = nav.getDeclaredMethods(paramC).iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      XmlElementDecl localXmlElementDecl = (XmlElementDecl)reader.getMethodAnnotation(XmlElementDecl.class, localObject, this);
      if (localXmlElementDecl == null)
      {
        if (nav.getMethodName(localObject).startsWith("create")) {
          references.add(paramModelBuilder.getTypeInfo(nav.getReturnType(localObject), new MethodLocatable(this, localObject, nav)));
        }
      }
      else
      {
        ElementInfoImpl localElementInfoImpl;
        try
        {
          localElementInfoImpl = paramModelBuilder.createElementInfo(this, localObject);
        }
        catch (IllegalAnnotationException localIllegalAnnotationException)
        {
          paramModelBuilder.reportError(localIllegalAnnotationException);
        }
        continue;
        typeInfoSet.add(localElementInfoImpl, paramModelBuilder);
        references.add(localElementInfoImpl);
      }
    }
  }
  
  public Locatable getUpstream()
  {
    return upstream;
  }
  
  public Location getLocation()
  {
    return nav.getClassLocation(registryClass);
  }
  
  public Set<TypeInfo<T, C>> getReferences()
  {
    return references;
  }
  
  public String getPackageName()
  {
    return nav.getPackageName(registryClass);
  }
  
  public C getClazz()
  {
    return (C)registryClass;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\RegistryInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */