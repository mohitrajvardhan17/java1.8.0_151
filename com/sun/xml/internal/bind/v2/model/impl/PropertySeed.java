package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.AnnotationSource;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;

abstract interface PropertySeed<T, C, F, M>
  extends Locatable, AnnotationSource
{
  public abstract String getName();
  
  public abstract T getRawType();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\PropertySeed.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */