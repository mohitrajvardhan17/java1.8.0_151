package com.sun.xml.internal.bind.v2.model.core;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationSource;
import java.util.Collection;
import javax.activation.MimeType;
import javax.xml.namespace.QName;

public abstract interface PropertyInfo<T, C>
  extends AnnotationSource
{
  public abstract TypeInfo<T, C> parent();
  
  public abstract String getName();
  
  public abstract String displayName();
  
  public abstract boolean isCollection();
  
  public abstract Collection<? extends TypeInfo<T, C>> ref();
  
  public abstract PropertyKind kind();
  
  public abstract Adapter<T, C> getAdapter();
  
  public abstract ID id();
  
  public abstract MimeType getExpectedMimeType();
  
  public abstract boolean inlineBinaryData();
  
  @Nullable
  public abstract QName getSchemaType();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\core\PropertyInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */