package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;

public abstract interface ModelBuilderI<T, C, F, M>
{
  public abstract Navigator<T, C, F, M> getNavigator();
  
  public abstract AnnotationReader<T, C, F, M> getReader();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\ModelBuilderI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */