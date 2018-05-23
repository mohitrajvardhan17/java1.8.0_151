package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="existing-annotations-type")
@XmlEnum
public enum ExistingAnnotationsType
{
  MERGE("merge"),  IGNORE("ignore");
  
  private final String value;
  
  private ExistingAnnotationsType(String paramString)
  {
    value = paramString;
  }
  
  public String value()
  {
    return value;
  }
  
  public static ExistingAnnotationsType fromValue(String paramString)
  {
    for (ExistingAnnotationsType localExistingAnnotationsType : ) {
      if (value.equals(paramString)) {
        return localExistingAnnotationsType;
      }
    }
    throw new IllegalArgumentException(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\ExistingAnnotationsType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */