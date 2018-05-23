package com.sun.org.apache.xerces.internal.impl.dv;

import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import java.util.Vector;

public class XSFacets
{
  public int length;
  public int minLength;
  public int maxLength;
  public short whiteSpace;
  public int totalDigits;
  public int fractionDigits;
  public String pattern;
  public Vector enumeration;
  public Vector enumNSDecls;
  public String maxInclusive;
  public String maxExclusive;
  public String minInclusive;
  public String minExclusive;
  public XSAnnotation lengthAnnotation;
  public XSAnnotation minLengthAnnotation;
  public XSAnnotation maxLengthAnnotation;
  public XSAnnotation whiteSpaceAnnotation;
  public XSAnnotation totalDigitsAnnotation;
  public XSAnnotation fractionDigitsAnnotation;
  public XSObjectListImpl patternAnnotations;
  public XSObjectList enumAnnotations;
  public XSAnnotation maxInclusiveAnnotation;
  public XSAnnotation maxExclusiveAnnotation;
  public XSAnnotation minInclusiveAnnotation;
  public XSAnnotation minExclusiveAnnotation;
  
  public XSFacets() {}
  
  public void reset()
  {
    lengthAnnotation = null;
    minLengthAnnotation = null;
    maxLengthAnnotation = null;
    whiteSpaceAnnotation = null;
    totalDigitsAnnotation = null;
    fractionDigitsAnnotation = null;
    patternAnnotations = null;
    enumAnnotations = null;
    maxInclusiveAnnotation = null;
    maxExclusiveAnnotation = null;
    minInclusiveAnnotation = null;
    minExclusiveAnnotation = null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\XSFacets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */