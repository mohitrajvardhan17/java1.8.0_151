package com.sun.org.apache.xerces.internal.xs.datatypes;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

public abstract interface XSDateTime
{
  public abstract int getYears();
  
  public abstract int getMonths();
  
  public abstract int getDays();
  
  public abstract int getHours();
  
  public abstract int getMinutes();
  
  public abstract double getSeconds();
  
  public abstract boolean hasTimeZone();
  
  public abstract int getTimeZoneHours();
  
  public abstract int getTimeZoneMinutes();
  
  public abstract String getLexicalValue();
  
  public abstract XSDateTime normalize();
  
  public abstract boolean isNormalized();
  
  public abstract XMLGregorianCalendar getXMLGregorianCalendar();
  
  public abstract Duration getDuration();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xs\datatypes\XSDateTime.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */