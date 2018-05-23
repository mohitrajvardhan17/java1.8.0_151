package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.models.CMBuilder;
import com.sun.org.apache.xerces.internal.impl.xs.models.XSCMValidator;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xs.XSAttributeUse;
import com.sun.org.apache.xerces.internal.xs.XSComplexTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSParticle;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSWildcard;
import org.w3c.dom.TypeInfo;

public class XSComplexTypeDecl
  implements XSComplexTypeDefinition, TypeInfo
{
  String fName = null;
  String fTargetNamespace = null;
  XSTypeDefinition fBaseType = null;
  short fDerivedBy = 2;
  short fFinal = 0;
  short fBlock = 0;
  short fMiscFlags = 0;
  XSAttributeGroupDecl fAttrGrp = null;
  short fContentType = 0;
  XSSimpleType fXSSimpleType = null;
  XSParticleDecl fParticle = null;
  volatile XSCMValidator fCMValidator = null;
  XSCMValidator fUPACMValidator = null;
  XSObjectListImpl fAnnotations = null;
  private XSNamespaceItem fNamespaceItem = null;
  static final int DERIVATION_ANY = 0;
  static final int DERIVATION_RESTRICTION = 1;
  static final int DERIVATION_EXTENSION = 2;
  static final int DERIVATION_UNION = 4;
  static final int DERIVATION_LIST = 8;
  private static final short CT_IS_ABSTRACT = 1;
  private static final short CT_HAS_TYPE_ID = 2;
  private static final short CT_IS_ANONYMOUS = 4;
  
  public XSComplexTypeDecl() {}
  
  public void setValues(String paramString1, String paramString2, XSTypeDefinition paramXSTypeDefinition, short paramShort1, short paramShort2, short paramShort3, short paramShort4, boolean paramBoolean, XSAttributeGroupDecl paramXSAttributeGroupDecl, XSSimpleType paramXSSimpleType, XSParticleDecl paramXSParticleDecl, XSObjectListImpl paramXSObjectListImpl)
  {
    fTargetNamespace = paramString2;
    fBaseType = paramXSTypeDefinition;
    fDerivedBy = paramShort1;
    fFinal = paramShort2;
    fBlock = paramShort3;
    fContentType = paramShort4;
    if (paramBoolean) {
      fMiscFlags = ((short)(fMiscFlags | 0x1));
    }
    fAttrGrp = paramXSAttributeGroupDecl;
    fXSSimpleType = paramXSSimpleType;
    fParticle = paramXSParticleDecl;
    fAnnotations = paramXSObjectListImpl;
  }
  
  public void setName(String paramString)
  {
    fName = paramString;
  }
  
  public short getTypeCategory()
  {
    return 15;
  }
  
  public String getTypeName()
  {
    return fName;
  }
  
  public short getFinalSet()
  {
    return fFinal;
  }
  
  public String getTargetNamespace()
  {
    return fTargetNamespace;
  }
  
  public boolean containsTypeID()
  {
    return (fMiscFlags & 0x2) != 0;
  }
  
  public void setIsAbstractType()
  {
    fMiscFlags = ((short)(fMiscFlags | 0x1));
  }
  
  public void setContainsTypeID()
  {
    fMiscFlags = ((short)(fMiscFlags | 0x2));
  }
  
  public void setIsAnonymous()
  {
    fMiscFlags = ((short)(fMiscFlags | 0x4));
  }
  
  public XSCMValidator getContentModel(CMBuilder paramCMBuilder)
  {
    if ((fContentType == 1) || (fContentType == 0)) {
      return null;
    }
    if (fCMValidator == null) {
      synchronized (this)
      {
        if (fCMValidator == null) {
          fCMValidator = paramCMBuilder.getContentModel(this);
        }
      }
    }
    return fCMValidator;
  }
  
  public XSAttributeGroupDecl getAttrGrp()
  {
    return fAttrGrp;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(192);
    appendTypeInfo(localStringBuilder);
    return localStringBuilder.toString();
  }
  
  void appendTypeInfo(StringBuilder paramStringBuilder)
  {
    String[] arrayOfString1 = { "EMPTY", "SIMPLE", "ELEMENT", "MIXED" };
    String[] arrayOfString2 = { "EMPTY", "EXTENSION", "RESTRICTION" };
    paramStringBuilder.append("Complex type name='").append(fTargetNamespace).append(',').append(getTypeName()).append("', ");
    if (fBaseType != null) {
      paramStringBuilder.append(" base type name='").append(fBaseType.getName()).append("', ");
    }
    paramStringBuilder.append(" content type='").append(arrayOfString1[fContentType]).append("', ");
    paramStringBuilder.append(" isAbstract='").append(getAbstract()).append("', ");
    paramStringBuilder.append(" hasTypeId='").append(containsTypeID()).append("', ");
    paramStringBuilder.append(" final='").append(fFinal).append("', ");
    paramStringBuilder.append(" block='").append(fBlock).append("', ");
    if (fParticle != null) {
      paramStringBuilder.append(" particle='").append(fParticle.toString()).append("', ");
    }
    paramStringBuilder.append(" derivedBy='").append(arrayOfString2[fDerivedBy]).append("'. ");
  }
  
  public boolean derivedFromType(XSTypeDefinition paramXSTypeDefinition, short paramShort)
  {
    if (paramXSTypeDefinition == null) {
      return false;
    }
    if (paramXSTypeDefinition == SchemaGrammar.fAnyType) {
      return true;
    }
    for (Object localObject = this; (localObject != paramXSTypeDefinition) && (localObject != SchemaGrammar.fAnySimpleType) && (localObject != SchemaGrammar.fAnyType); localObject = ((XSTypeDefinition)localObject).getBaseType()) {}
    return localObject == paramXSTypeDefinition;
  }
  
  public boolean derivedFrom(String paramString1, String paramString2, short paramShort)
  {
    if (paramString2 == null) {
      return false;
    }
    if ((paramString1 != null) && (paramString1.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA)) && (paramString2.equals("anyType"))) {
      return true;
    }
    for (Object localObject = this; ((!paramString2.equals(((XSTypeDefinition)localObject).getName())) || (((paramString1 != null) || (((XSTypeDefinition)localObject).getNamespace() != null)) && ((paramString1 == null) || (!paramString1.equals(((XSTypeDefinition)localObject).getNamespace()))))) && (localObject != SchemaGrammar.fAnySimpleType) && (localObject != SchemaGrammar.fAnyType); localObject = ((XSTypeDefinition)localObject).getBaseType()) {}
    return (localObject != SchemaGrammar.fAnySimpleType) && (localObject != SchemaGrammar.fAnyType);
  }
  
  public boolean isDOMDerivedFrom(String paramString1, String paramString2, int paramInt)
  {
    if (paramString2 == null) {
      return false;
    }
    if ((paramString1 != null) && (paramString1.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA)) && (paramString2.equals("anyType")) && (paramInt == 1) && (paramInt == 2)) {
      return true;
    }
    if (((paramInt & 0x1) != 0) && (isDerivedByRestriction(paramString1, paramString2, paramInt, this))) {
      return true;
    }
    if (((paramInt & 0x2) != 0) && (isDerivedByExtension(paramString1, paramString2, paramInt, this))) {
      return true;
    }
    if ((((paramInt & 0x8) != 0) || ((paramInt & 0x4) != 0)) && ((paramInt & 0x1) == 0) && ((paramInt & 0x2) == 0))
    {
      if ((paramString1.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA)) && (paramString2.equals("anyType"))) {
        paramString2 = "anySimpleType";
      }
      if ((!fName.equals("anyType")) || (!fTargetNamespace.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA)))
      {
        if ((fBaseType != null) && ((fBaseType instanceof XSSimpleTypeDecl))) {
          return ((XSSimpleTypeDecl)fBaseType).isDOMDerivedFrom(paramString1, paramString2, paramInt);
        }
        if ((fBaseType != null) && ((fBaseType instanceof XSComplexTypeDecl))) {
          return ((XSComplexTypeDecl)fBaseType).isDOMDerivedFrom(paramString1, paramString2, paramInt);
        }
      }
    }
    if (((paramInt & 0x2) == 0) && ((paramInt & 0x1) == 0) && ((paramInt & 0x8) == 0) && ((paramInt & 0x4) == 0)) {
      return isDerivedByAny(paramString1, paramString2, paramInt, this);
    }
    return false;
  }
  
  private boolean isDerivedByAny(String paramString1, String paramString2, int paramInt, XSTypeDefinition paramXSTypeDefinition)
  {
    XSTypeDefinition localXSTypeDefinition = null;
    boolean bool = false;
    while ((paramXSTypeDefinition != null) && (paramXSTypeDefinition != localXSTypeDefinition))
    {
      if ((paramString2.equals(paramXSTypeDefinition.getName())) && (((paramString1 == null) && (paramXSTypeDefinition.getNamespace() == null)) || ((paramString1 != null) && (paramString1.equals(paramXSTypeDefinition.getNamespace())))))
      {
        bool = true;
        break;
      }
      if (isDerivedByRestriction(paramString1, paramString2, paramInt, paramXSTypeDefinition)) {
        return true;
      }
      if (!isDerivedByExtension(paramString1, paramString2, paramInt, paramXSTypeDefinition)) {
        return true;
      }
      localXSTypeDefinition = paramXSTypeDefinition;
      paramXSTypeDefinition = paramXSTypeDefinition.getBaseType();
    }
    return bool;
  }
  
  private boolean isDerivedByRestriction(String paramString1, String paramString2, int paramInt, XSTypeDefinition paramXSTypeDefinition)
  {
    XSTypeDefinition localXSTypeDefinition = null;
    while ((paramXSTypeDefinition != null) && (paramXSTypeDefinition != localXSTypeDefinition))
    {
      if ((paramString1 != null) && (paramString1.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA)) && (paramString2.equals("anySimpleType"))) {
        return false;
      }
      if (((paramString2.equals(paramXSTypeDefinition.getName())) && (paramString1 != null) && (paramString1.equals(paramXSTypeDefinition.getNamespace()))) || ((paramXSTypeDefinition.getNamespace() == null) && (paramString1 == null))) {
        return true;
      }
      if ((paramXSTypeDefinition instanceof XSSimpleTypeDecl))
      {
        if ((paramString1.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA)) && (paramString2.equals("anyType"))) {
          paramString2 = "anySimpleType";
        }
        return ((XSSimpleTypeDecl)paramXSTypeDefinition).isDOMDerivedFrom(paramString1, paramString2, paramInt);
      }
      if (((XSComplexTypeDecl)paramXSTypeDefinition).getDerivationMethod() != 2) {
        return false;
      }
      localXSTypeDefinition = paramXSTypeDefinition;
      paramXSTypeDefinition = paramXSTypeDefinition.getBaseType();
    }
    return false;
  }
  
  private boolean isDerivedByExtension(String paramString1, String paramString2, int paramInt, XSTypeDefinition paramXSTypeDefinition)
  {
    boolean bool = false;
    XSTypeDefinition localXSTypeDefinition = null;
    while ((paramXSTypeDefinition != null) && (paramXSTypeDefinition != localXSTypeDefinition) && ((paramString1 == null) || (!paramString1.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA)) || (!paramString2.equals("anySimpleType")) || (!SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(paramXSTypeDefinition.getNamespace())) || (!"anyType".equals(paramXSTypeDefinition.getName()))))
    {
      if ((paramString2.equals(paramXSTypeDefinition.getName())) && (((paramString1 == null) && (paramXSTypeDefinition.getNamespace() == null)) || ((paramString1 != null) && (paramString1.equals(paramXSTypeDefinition.getNamespace()))))) {
        return bool;
      }
      if ((paramXSTypeDefinition instanceof XSSimpleTypeDecl))
      {
        if ((paramString1.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA)) && (paramString2.equals("anyType"))) {
          paramString2 = "anySimpleType";
        }
        if ((paramInt & 0x2) != 0) {
          return bool & ((XSSimpleTypeDecl)paramXSTypeDefinition).isDOMDerivedFrom(paramString1, paramString2, paramInt & 0x1);
        }
        return bool & ((XSSimpleTypeDecl)paramXSTypeDefinition).isDOMDerivedFrom(paramString1, paramString2, paramInt);
      }
      if (((XSComplexTypeDecl)paramXSTypeDefinition).getDerivationMethod() == 1) {
        bool |= true;
      }
      localXSTypeDefinition = paramXSTypeDefinition;
      paramXSTypeDefinition = paramXSTypeDefinition.getBaseType();
    }
    return false;
  }
  
  public void reset()
  {
    fName = null;
    fTargetNamespace = null;
    fBaseType = null;
    fDerivedBy = 2;
    fFinal = 0;
    fBlock = 0;
    fMiscFlags = 0;
    fAttrGrp.reset();
    fContentType = 0;
    fXSSimpleType = null;
    fParticle = null;
    fCMValidator = null;
    fUPACMValidator = null;
    if (fAnnotations != null) {
      fAnnotations.clearXSObjectList();
    }
    fAnnotations = null;
  }
  
  public short getType()
  {
    return 3;
  }
  
  public String getName()
  {
    return getAnonymous() ? null : fName;
  }
  
  public boolean getAnonymous()
  {
    return (fMiscFlags & 0x4) != 0;
  }
  
  public String getNamespace()
  {
    return fTargetNamespace;
  }
  
  public XSTypeDefinition getBaseType()
  {
    return fBaseType;
  }
  
  public short getDerivationMethod()
  {
    return fDerivedBy;
  }
  
  public boolean isFinal(short paramShort)
  {
    return (fFinal & paramShort) != 0;
  }
  
  public short getFinal()
  {
    return fFinal;
  }
  
  public boolean getAbstract()
  {
    return (fMiscFlags & 0x1) != 0;
  }
  
  public XSObjectList getAttributeUses()
  {
    return fAttrGrp.getAttributeUses();
  }
  
  public XSWildcard getAttributeWildcard()
  {
    return fAttrGrp.getAttributeWildcard();
  }
  
  public short getContentType()
  {
    return fContentType;
  }
  
  public XSSimpleTypeDefinition getSimpleType()
  {
    return fXSSimpleType;
  }
  
  public XSParticle getParticle()
  {
    return fParticle;
  }
  
  public boolean isProhibitedSubstitution(short paramShort)
  {
    return (fBlock & paramShort) != 0;
  }
  
  public short getProhibitedSubstitutions()
  {
    return fBlock;
  }
  
  public XSObjectList getAnnotations()
  {
    return fAnnotations != null ? fAnnotations : XSObjectListImpl.EMPTY_LIST;
  }
  
  public XSNamespaceItem getNamespaceItem()
  {
    return fNamespaceItem;
  }
  
  void setNamespaceItem(XSNamespaceItem paramXSNamespaceItem)
  {
    fNamespaceItem = paramXSNamespaceItem;
  }
  
  public XSAttributeUse getAttributeUse(String paramString1, String paramString2)
  {
    return fAttrGrp.getAttributeUse(paramString1, paramString2);
  }
  
  public String getTypeNamespace()
  {
    return getNamespace();
  }
  
  public boolean isDerivedFrom(String paramString1, String paramString2, int paramInt)
  {
    return isDOMDerivedFrom(paramString1, paramString2, paramInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\XSComplexTypeDecl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */