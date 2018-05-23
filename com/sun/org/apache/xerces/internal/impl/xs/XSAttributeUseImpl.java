package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.XSAttributeDeclaration;
import com.sun.org.apache.xerces.internal.xs.XSAttributeUse;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;

public class XSAttributeUseImpl
  implements XSAttributeUse
{
  public XSAttributeDecl fAttrDecl = null;
  public short fUse = 0;
  public short fConstraintType = 0;
  public ValidatedInfo fDefault = null;
  public XSObjectList fAnnotations = null;
  
  public XSAttributeUseImpl() {}
  
  public void reset()
  {
    fDefault = null;
    fAttrDecl = null;
    fUse = 0;
    fConstraintType = 0;
    fAnnotations = null;
  }
  
  public short getType()
  {
    return 4;
  }
  
  public String getName()
  {
    return null;
  }
  
  public String getNamespace()
  {
    return null;
  }
  
  public boolean getRequired()
  {
    return fUse == 1;
  }
  
  public XSAttributeDeclaration getAttrDeclaration()
  {
    return fAttrDecl;
  }
  
  public short getConstraintType()
  {
    return fConstraintType;
  }
  
  public String getConstraintValue()
  {
    return getConstraintType() == 0 ? null : fDefault.stringValue();
  }
  
  public XSNamespaceItem getNamespaceItem()
  {
    return null;
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
  
  public XSObjectList getAnnotations()
  {
    return fAnnotations != null ? fAnnotations : XSObjectListImpl.EMPTY_LIST;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\XSAttributeUseImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */