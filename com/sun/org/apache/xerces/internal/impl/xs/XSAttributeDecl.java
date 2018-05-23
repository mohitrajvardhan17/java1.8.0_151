package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.XSAttributeDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSComplexTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;

public class XSAttributeDecl
  implements XSAttributeDeclaration
{
  public static final short SCOPE_ABSENT = 0;
  public static final short SCOPE_GLOBAL = 1;
  public static final short SCOPE_LOCAL = 2;
  String fName = null;
  String fTargetNamespace = null;
  XSSimpleType fType = null;
  public QName fUnresolvedTypeName = null;
  short fConstraintType = 0;
  short fScope = 0;
  XSComplexTypeDecl fEnclosingCT = null;
  XSObjectList fAnnotations = null;
  ValidatedInfo fDefault = null;
  private XSNamespaceItem fNamespaceItem = null;
  
  public XSAttributeDecl() {}
  
  public void setValues(String paramString1, String paramString2, XSSimpleType paramXSSimpleType, short paramShort1, short paramShort2, ValidatedInfo paramValidatedInfo, XSComplexTypeDecl paramXSComplexTypeDecl, XSObjectList paramXSObjectList)
  {
    fName = paramString1;
    fTargetNamespace = paramString2;
    fType = paramXSSimpleType;
    fConstraintType = paramShort1;
    fScope = paramShort2;
    fDefault = paramValidatedInfo;
    fEnclosingCT = paramXSComplexTypeDecl;
    fAnnotations = paramXSObjectList;
  }
  
  public void reset()
  {
    fName = null;
    fTargetNamespace = null;
    fType = null;
    fUnresolvedTypeName = null;
    fConstraintType = 0;
    fScope = 0;
    fDefault = null;
    fAnnotations = null;
  }
  
  public short getType()
  {
    return 1;
  }
  
  public String getName()
  {
    return fName;
  }
  
  public String getNamespace()
  {
    return fTargetNamespace;
  }
  
  public XSSimpleTypeDefinition getTypeDefinition()
  {
    return fType;
  }
  
  public short getScope()
  {
    return fScope;
  }
  
  public XSComplexTypeDefinition getEnclosingCTDefinition()
  {
    return fEnclosingCT;
  }
  
  public short getConstraintType()
  {
    return fConstraintType;
  }
  
  public String getConstraintValue()
  {
    return getConstraintType() == 0 ? null : fDefault.stringValue();
  }
  
  public XSAnnotation getAnnotation()
  {
    return fAnnotations != null ? (XSAnnotation)fAnnotations.item(0) : null;
  }
  
  public XSObjectList getAnnotations()
  {
    return fAnnotations != null ? fAnnotations : XSObjectListImpl.EMPTY_LIST;
  }
  
  public ValidatedInfo getValInfo()
  {
    return fDefault;
  }
  
  public XSNamespaceItem getNamespaceItem()
  {
    return fNamespaceItem;
  }
  
  void setNamespaceItem(XSNamespaceItem paramXSNamespaceItem)
  {
    fNamespaceItem = paramXSNamespaceItem;
  }
  
  public Object getActualVC()
  {
    return getConstraintType() == 0 ? null : fDefault.actualValue;
  }
  
  public short getActualVCType()
  {
    return getConstraintType() == 0 ? 45 : fDefault.actualValueType;
  }
  
  public ShortList getItemValueTypes()
  {
    return getConstraintType() == 0 ? null : fDefault.itemValueTypes;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\XSAttributeDecl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */