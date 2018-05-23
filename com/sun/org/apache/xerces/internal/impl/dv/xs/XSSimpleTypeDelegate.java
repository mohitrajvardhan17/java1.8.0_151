package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.DatatypeException;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeFacetException;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.impl.dv.XSFacets;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;

public class XSSimpleTypeDelegate
  implements XSSimpleType
{
  protected final XSSimpleType type;
  
  public XSSimpleTypeDelegate(XSSimpleType paramXSSimpleType)
  {
    if (paramXSSimpleType == null) {
      throw new NullPointerException();
    }
    type = paramXSSimpleType;
  }
  
  public XSSimpleType getWrappedXSSimpleType()
  {
    return type;
  }
  
  public XSObjectList getAnnotations()
  {
    return type.getAnnotations();
  }
  
  public boolean getBounded()
  {
    return type.getBounded();
  }
  
  public short getBuiltInKind()
  {
    return type.getBuiltInKind();
  }
  
  public short getDefinedFacets()
  {
    return type.getDefinedFacets();
  }
  
  public XSObjectList getFacets()
  {
    return type.getFacets();
  }
  
  public boolean getFinite()
  {
    return type.getFinite();
  }
  
  public short getFixedFacets()
  {
    return type.getFixedFacets();
  }
  
  public XSSimpleTypeDefinition getItemType()
  {
    return type.getItemType();
  }
  
  public StringList getLexicalEnumeration()
  {
    return type.getLexicalEnumeration();
  }
  
  public String getLexicalFacetValue(short paramShort)
  {
    return type.getLexicalFacetValue(paramShort);
  }
  
  public StringList getLexicalPattern()
  {
    return type.getLexicalPattern();
  }
  
  public XSObjectList getMemberTypes()
  {
    return type.getMemberTypes();
  }
  
  public XSObjectList getMultiValueFacets()
  {
    return type.getMultiValueFacets();
  }
  
  public boolean getNumeric()
  {
    return type.getNumeric();
  }
  
  public short getOrdered()
  {
    return type.getOrdered();
  }
  
  public XSSimpleTypeDefinition getPrimitiveType()
  {
    return type.getPrimitiveType();
  }
  
  public short getVariety()
  {
    return type.getVariety();
  }
  
  public boolean isDefinedFacet(short paramShort)
  {
    return type.isDefinedFacet(paramShort);
  }
  
  public boolean isFixedFacet(short paramShort)
  {
    return type.isFixedFacet(paramShort);
  }
  
  public boolean derivedFrom(String paramString1, String paramString2, short paramShort)
  {
    return type.derivedFrom(paramString1, paramString2, paramShort);
  }
  
  public boolean derivedFromType(XSTypeDefinition paramXSTypeDefinition, short paramShort)
  {
    return type.derivedFromType(paramXSTypeDefinition, paramShort);
  }
  
  public boolean getAnonymous()
  {
    return type.getAnonymous();
  }
  
  public XSTypeDefinition getBaseType()
  {
    return type.getBaseType();
  }
  
  public short getFinal()
  {
    return type.getFinal();
  }
  
  public short getTypeCategory()
  {
    return type.getTypeCategory();
  }
  
  public boolean isFinal(short paramShort)
  {
    return type.isFinal(paramShort);
  }
  
  public String getName()
  {
    return type.getName();
  }
  
  public String getNamespace()
  {
    return type.getNamespace();
  }
  
  public XSNamespaceItem getNamespaceItem()
  {
    return type.getNamespaceItem();
  }
  
  public short getType()
  {
    return type.getType();
  }
  
  public void applyFacets(XSFacets paramXSFacets, short paramShort1, short paramShort2, ValidationContext paramValidationContext)
    throws InvalidDatatypeFacetException
  {
    type.applyFacets(paramXSFacets, paramShort1, paramShort2, paramValidationContext);
  }
  
  public short getPrimitiveKind()
  {
    return type.getPrimitiveKind();
  }
  
  public short getWhitespace()
    throws DatatypeException
  {
    return type.getWhitespace();
  }
  
  public boolean isEqual(Object paramObject1, Object paramObject2)
  {
    return type.isEqual(paramObject1, paramObject2);
  }
  
  public boolean isIDType()
  {
    return type.isIDType();
  }
  
  public void validate(ValidationContext paramValidationContext, ValidatedInfo paramValidatedInfo)
    throws InvalidDatatypeValueException
  {
    type.validate(paramValidationContext, paramValidatedInfo);
  }
  
  public Object validate(String paramString, ValidationContext paramValidationContext, ValidatedInfo paramValidatedInfo)
    throws InvalidDatatypeValueException
  {
    return type.validate(paramString, paramValidationContext, paramValidatedInfo);
  }
  
  public Object validate(Object paramObject, ValidationContext paramValidationContext, ValidatedInfo paramValidatedInfo)
    throws InvalidDatatypeValueException
  {
    return type.validate(paramObject, paramValidationContext, paramValidatedInfo);
  }
  
  public String toString()
  {
    return type.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\XSSimpleTypeDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */