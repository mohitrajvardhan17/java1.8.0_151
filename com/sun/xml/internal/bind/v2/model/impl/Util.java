package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationSource;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSchemaTypes;
import javax.xml.namespace.QName;

final class Util
{
  Util() {}
  
  static <T, C, F, M> QName calcSchemaType(AnnotationReader<T, C, F, M> paramAnnotationReader, AnnotationSource paramAnnotationSource, C paramC, T paramT, Locatable paramLocatable)
  {
    XmlSchemaType localXmlSchemaType1 = (XmlSchemaType)paramAnnotationSource.readAnnotation(XmlSchemaType.class);
    if (localXmlSchemaType1 != null) {
      return new QName(localXmlSchemaType1.namespace(), localXmlSchemaType1.name());
    }
    XmlSchemaTypes localXmlSchemaTypes = (XmlSchemaTypes)paramAnnotationReader.getPackageAnnotation(XmlSchemaTypes.class, paramC, paramLocatable);
    XmlSchemaType[] arrayOfXmlSchemaType1 = null;
    if (localXmlSchemaTypes != null)
    {
      arrayOfXmlSchemaType1 = localXmlSchemaTypes.value();
    }
    else
    {
      localXmlSchemaType1 = (XmlSchemaType)paramAnnotationReader.getPackageAnnotation(XmlSchemaType.class, paramC, paramLocatable);
      if (localXmlSchemaType1 != null)
      {
        arrayOfXmlSchemaType1 = new XmlSchemaType[1];
        arrayOfXmlSchemaType1[0] = localXmlSchemaType1;
      }
    }
    if (arrayOfXmlSchemaType1 != null) {
      for (XmlSchemaType localXmlSchemaType2 : arrayOfXmlSchemaType1) {
        if (paramAnnotationReader.getClassValue(localXmlSchemaType2, "type").equals(paramT)) {
          return new QName(localXmlSchemaType2.namespace(), localXmlSchemaType2.name());
        }
      }
    }
    return null;
  }
  
  static MimeType calcExpectedMediaType(AnnotationSource paramAnnotationSource, ModelBuilder paramModelBuilder)
  {
    XmlMimeType localXmlMimeType = (XmlMimeType)paramAnnotationSource.readAnnotation(XmlMimeType.class);
    if (localXmlMimeType == null) {
      return null;
    }
    try
    {
      return new MimeType(localXmlMimeType.value());
    }
    catch (MimeTypeParseException localMimeTypeParseException)
    {
      paramModelBuilder.reportError(new IllegalAnnotationException(Messages.ILLEGAL_MIME_TYPE.format(new Object[] { localXmlMimeType.value(), localMimeTypeParseException.getMessage() }), localXmlMimeType));
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */