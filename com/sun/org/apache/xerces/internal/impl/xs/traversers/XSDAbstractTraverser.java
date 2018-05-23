package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.XSFacets;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationState;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar.BuiltinSchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaNamespaceSupport;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeGroupDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSAttributeUseImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSComplexTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSElementDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSWildcardDecl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.XSAttributeUse;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import java.util.Locale;
import java.util.Vector;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

abstract class XSDAbstractTraverser
{
  protected static final String NO_NAME = "(no name)";
  protected static final int NOT_ALL_CONTEXT = 0;
  protected static final int PROCESSING_ALL_EL = 1;
  protected static final int GROUP_REF_WITH_ALL = 2;
  protected static final int CHILD_OF_GROUP = 4;
  protected static final int PROCESSING_ALL_GP = 8;
  protected XSDHandler fSchemaHandler = null;
  protected SymbolTable fSymbolTable = null;
  protected XSAttributeChecker fAttrChecker = null;
  protected boolean fValidateAnnotations = false;
  ValidationState fValidationState = new ValidationState();
  private static final XSSimpleType fQNameDV = (XSSimpleType)SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl("QName");
  private StringBuffer fPattern = new StringBuffer();
  private final XSFacets xsFacets = new XSFacets();
  
  XSDAbstractTraverser(XSDHandler paramXSDHandler, XSAttributeChecker paramXSAttributeChecker)
  {
    fSchemaHandler = paramXSDHandler;
    fAttrChecker = paramXSAttributeChecker;
  }
  
  void reset(SymbolTable paramSymbolTable, boolean paramBoolean, Locale paramLocale)
  {
    fSymbolTable = paramSymbolTable;
    fValidateAnnotations = paramBoolean;
    fValidationState.setExtraChecking(false);
    fValidationState.setSymbolTable(paramSymbolTable);
    fValidationState.setLocale(paramLocale);
  }
  
  XSAnnotationImpl traverseAnnotationDecl(Element paramElement, Object[] paramArrayOfObject, boolean paramBoolean, XSDocumentInfo paramXSDocumentInfo)
  {
    Object[] arrayOfObject = fAttrChecker.checkAttributes(paramElement, paramBoolean, paramXSDocumentInfo);
    fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
    String str1 = DOMUtil.getAnnotation(paramElement);
    Element localElement = DOMUtil.getFirstChildElement(paramElement);
    if (localElement != null) {
      do
      {
        localObject1 = DOMUtil.getLocalName(localElement);
        if ((!((String)localObject1).equals(SchemaSymbols.ELT_APPINFO)) && (!((String)localObject1).equals(SchemaSymbols.ELT_DOCUMENTATION)))
        {
          reportSchemaError("src-annotation", new Object[] { localObject1 }, localElement);
        }
        else
        {
          arrayOfObject = fAttrChecker.checkAttributes(localElement, true, paramXSDocumentInfo);
          fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
        }
        localElement = DOMUtil.getNextSiblingElement(localElement);
      } while (localElement != null);
    }
    if (str1 == null) {
      return null;
    }
    Object localObject1 = fSchemaHandler.getGrammar(fTargetNamespace);
    Vector localVector = (Vector)paramArrayOfObject[XSAttributeChecker.ATTIDX_NONSCHEMA];
    if ((localVector != null) && (!localVector.isEmpty()))
    {
      StringBuffer localStringBuffer = new StringBuffer(64);
      localStringBuffer.append(" ");
      int i = 0;
      while (i < localVector.size())
      {
        localObject2 = (String)localVector.elementAt(i++);
        j = ((String)localObject2).indexOf(':');
        Object localObject3;
        if (j == -1)
        {
          str2 = "";
          localObject3 = localObject2;
        }
        else
        {
          str2 = ((String)localObject2).substring(0, j);
          localObject3 = ((String)localObject2).substring(j + 1);
        }
        String str3 = fNamespaceSupport.getURI(fSymbolTable.addSymbol(str2));
        if (paramElement.getAttributeNS(str3, (String)localObject3).length() != 0)
        {
          i++;
        }
        else
        {
          localStringBuffer.append((String)localObject2).append("=\"");
          String str4 = (String)localVector.elementAt(i++);
          str4 = processAttValue(str4);
          localStringBuffer.append(str4).append("\" ");
        }
      }
      Object localObject2 = new StringBuffer(str1.length() + localStringBuffer.length());
      int j = str1.indexOf(SchemaSymbols.ELT_ANNOTATION);
      if (j == -1) {
        return null;
      }
      j += SchemaSymbols.ELT_ANNOTATION.length();
      ((StringBuffer)localObject2).append(str1.substring(0, j));
      ((StringBuffer)localObject2).append(localStringBuffer.toString());
      ((StringBuffer)localObject2).append(str1.substring(j, str1.length()));
      String str2 = ((StringBuffer)localObject2).toString();
      if (fValidateAnnotations) {
        paramXSDocumentInfo.addAnnotation(new XSAnnotationInfo(str2, paramElement));
      }
      return new XSAnnotationImpl(str2, (SchemaGrammar)localObject1);
    }
    if (fValidateAnnotations) {
      paramXSDocumentInfo.addAnnotation(new XSAnnotationInfo(str1, paramElement));
    }
    return new XSAnnotationImpl(str1, (SchemaGrammar)localObject1);
  }
  
  XSAnnotationImpl traverseSyntheticAnnotation(Element paramElement, String paramString, Object[] paramArrayOfObject, boolean paramBoolean, XSDocumentInfo paramXSDocumentInfo)
  {
    String str1 = paramString;
    SchemaGrammar localSchemaGrammar = fSchemaHandler.getGrammar(fTargetNamespace);
    Vector localVector = (Vector)paramArrayOfObject[XSAttributeChecker.ATTIDX_NONSCHEMA];
    if ((localVector != null) && (!localVector.isEmpty()))
    {
      StringBuffer localStringBuffer = new StringBuffer(64);
      localStringBuffer.append(" ");
      int i = 0;
      while (i < localVector.size())
      {
        localObject1 = (String)localVector.elementAt(i++);
        j = ((String)localObject1).indexOf(':');
        Object localObject2;
        if (j == -1)
        {
          str2 = "";
          localObject2 = localObject1;
        }
        else
        {
          str2 = ((String)localObject1).substring(0, j);
          localObject2 = ((String)localObject1).substring(j + 1);
        }
        String str3 = fNamespaceSupport.getURI(fSymbolTable.addSymbol(str2));
        localStringBuffer.append((String)localObject1).append("=\"");
        String str4 = (String)localVector.elementAt(i++);
        str4 = processAttValue(str4);
        localStringBuffer.append(str4).append("\" ");
      }
      Object localObject1 = new StringBuffer(str1.length() + localStringBuffer.length());
      int j = str1.indexOf(SchemaSymbols.ELT_ANNOTATION);
      if (j == -1) {
        return null;
      }
      j += SchemaSymbols.ELT_ANNOTATION.length();
      ((StringBuffer)localObject1).append(str1.substring(0, j));
      ((StringBuffer)localObject1).append(localStringBuffer.toString());
      ((StringBuffer)localObject1).append(str1.substring(j, str1.length()));
      String str2 = ((StringBuffer)localObject1).toString();
      if (fValidateAnnotations) {
        paramXSDocumentInfo.addAnnotation(new XSAnnotationInfo(str2, paramElement));
      }
      return new XSAnnotationImpl(str2, localSchemaGrammar);
    }
    if (fValidateAnnotations) {
      paramXSDocumentInfo.addAnnotation(new XSAnnotationInfo(str1, paramElement));
    }
    return new XSAnnotationImpl(str1, localSchemaGrammar);
  }
  
  FacetInfo traverseFacets(Element paramElement, XSSimpleType paramXSSimpleType, XSDocumentInfo paramXSDocumentInfo)
  {
    short s1 = 0;
    short s2 = 0;
    boolean bool = containsQName(paramXSSimpleType);
    Vector localVector1 = null;
    XSObjectListImpl localXSObjectListImpl1 = null;
    XSObjectListImpl localXSObjectListImpl2 = null;
    Vector localVector2 = bool ? new Vector() : null;
    short s3 = 0;
    xsFacets.reset();
    while (paramElement != null)
    {
      Object[] arrayOfObject = null;
      String str1 = DOMUtil.getLocalName(paramElement);
      Object localObject1;
      Object localObject2;
      Object localObject3;
      if (str1.equals(SchemaSymbols.ELT_ENUMERATION))
      {
        arrayOfObject = fAttrChecker.checkAttributes(paramElement, false, paramXSDocumentInfo, bool);
        localObject1 = (String)arrayOfObject[XSAttributeChecker.ATTIDX_VALUE];
        if (localObject1 == null)
        {
          reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_ENUMERATION, SchemaSymbols.ATT_VALUE }, paramElement);
          fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
          paramElement = DOMUtil.getNextSiblingElement(paramElement);
          continue;
        }
        localObject2 = (NamespaceSupport)arrayOfObject[XSAttributeChecker.ATTIDX_ENUMNSDECLS];
        if ((paramXSSimpleType.getVariety() == 1) && (paramXSSimpleType.getPrimitiveKind() == 20))
        {
          fValidationContext.setNamespaceSupport((NamespaceContext)localObject2);
          localObject3 = null;
          try
          {
            QName localQName = (QName)fQNameDV.validate((String)localObject1, fValidationContext, null);
            localObject3 = fSchemaHandler.getGlobalDecl(paramXSDocumentInfo, 6, localQName, paramElement);
          }
          catch (InvalidDatatypeValueException localInvalidDatatypeValueException)
          {
            reportSchemaError(localInvalidDatatypeValueException.getKey(), localInvalidDatatypeValueException.getArgs(), paramElement);
          }
          if (localObject3 == null)
          {
            fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
            paramElement = DOMUtil.getNextSiblingElement(paramElement);
            continue;
          }
          fValidationContext.setNamespaceSupport(fNamespaceSupport);
        }
        if (localVector1 == null)
        {
          localVector1 = new Vector();
          localXSObjectListImpl1 = new XSObjectListImpl();
        }
        localVector1.addElement(localObject1);
        localXSObjectListImpl1.addXSObject(null);
        if (bool) {
          localVector2.addElement(localObject2);
        }
        localObject3 = DOMUtil.getFirstChildElement(paramElement);
        if ((localObject3 != null) && (DOMUtil.getLocalName((Node)localObject3).equals(SchemaSymbols.ELT_ANNOTATION)))
        {
          localXSObjectListImpl1.addXSObject(localXSObjectListImpl1.getLength() - 1, traverseAnnotationDecl((Element)localObject3, arrayOfObject, false, paramXSDocumentInfo));
          localObject3 = DOMUtil.getNextSiblingElement((Node)localObject3);
        }
        else
        {
          String str2 = DOMUtil.getSyntheticAnnotation(paramElement);
          if (str2 != null) {
            localXSObjectListImpl1.addXSObject(localXSObjectListImpl1.getLength() - 1, traverseSyntheticAnnotation(paramElement, str2, arrayOfObject, false, paramXSDocumentInfo));
          }
        }
        if (localObject3 != null) {
          reportSchemaError("s4s-elt-must-match.1", new Object[] { "enumeration", "(annotation?)", DOMUtil.getLocalName((Node)localObject3) }, (Element)localObject3);
        }
      }
      else if (str1.equals(SchemaSymbols.ELT_PATTERN))
      {
        s1 = (short)(s1 | 0x8);
        arrayOfObject = fAttrChecker.checkAttributes(paramElement, false, paramXSDocumentInfo);
        localObject1 = (String)arrayOfObject[XSAttributeChecker.ATTIDX_VALUE];
        if (localObject1 == null)
        {
          reportSchemaError("s4s-att-must-appear", new Object[] { SchemaSymbols.ELT_PATTERN, SchemaSymbols.ATT_VALUE }, paramElement);
          fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
          paramElement = DOMUtil.getNextSiblingElement(paramElement);
          continue;
        }
        if (fPattern.length() == 0)
        {
          fPattern.append((String)localObject1);
        }
        else
        {
          fPattern.append("|");
          fPattern.append((String)localObject1);
        }
        localObject2 = DOMUtil.getFirstChildElement(paramElement);
        if ((localObject2 != null) && (DOMUtil.getLocalName((Node)localObject2).equals(SchemaSymbols.ELT_ANNOTATION)))
        {
          if (localXSObjectListImpl2 == null) {
            localXSObjectListImpl2 = new XSObjectListImpl();
          }
          localXSObjectListImpl2.addXSObject(traverseAnnotationDecl((Element)localObject2, arrayOfObject, false, paramXSDocumentInfo));
          localObject2 = DOMUtil.getNextSiblingElement((Node)localObject2);
        }
        else
        {
          localObject3 = DOMUtil.getSyntheticAnnotation(paramElement);
          if (localObject3 != null)
          {
            if (localXSObjectListImpl2 == null) {
              localXSObjectListImpl2 = new XSObjectListImpl();
            }
            localXSObjectListImpl2.addXSObject(traverseSyntheticAnnotation(paramElement, (String)localObject3, arrayOfObject, false, paramXSDocumentInfo));
          }
        }
        if (localObject2 != null) {
          reportSchemaError("s4s-elt-must-match.1", new Object[] { "pattern", "(annotation?)", DOMUtil.getLocalName((Node)localObject2) }, (Element)localObject2);
        }
      }
      else
      {
        if (str1.equals(SchemaSymbols.ELT_MINLENGTH))
        {
          s3 = 2;
        }
        else if (str1.equals(SchemaSymbols.ELT_MAXLENGTH))
        {
          s3 = 4;
        }
        else if (str1.equals(SchemaSymbols.ELT_MAXEXCLUSIVE))
        {
          s3 = 64;
        }
        else if (str1.equals(SchemaSymbols.ELT_MAXINCLUSIVE))
        {
          s3 = 32;
        }
        else if (str1.equals(SchemaSymbols.ELT_MINEXCLUSIVE))
        {
          s3 = 128;
        }
        else if (str1.equals(SchemaSymbols.ELT_MININCLUSIVE))
        {
          s3 = 256;
        }
        else if (str1.equals(SchemaSymbols.ELT_TOTALDIGITS))
        {
          s3 = 512;
        }
        else if (str1.equals(SchemaSymbols.ELT_FRACTIONDIGITS))
        {
          s3 = 1024;
        }
        else if (str1.equals(SchemaSymbols.ELT_WHITESPACE))
        {
          s3 = 16;
        }
        else
        {
          if (!str1.equals(SchemaSymbols.ELT_LENGTH)) {
            break;
          }
          s3 = 1;
        }
        arrayOfObject = fAttrChecker.checkAttributes(paramElement, false, paramXSDocumentInfo);
        if ((s1 & s3) != 0)
        {
          reportSchemaError("src-single-facet-value", new Object[] { str1 }, paramElement);
          fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
          paramElement = DOMUtil.getNextSiblingElement(paramElement);
          continue;
        }
        if (arrayOfObject[XSAttributeChecker.ATTIDX_VALUE] == null)
        {
          if (paramElement.getAttributeNodeNS(null, "value") == null) {
            reportSchemaError("s4s-att-must-appear", new Object[] { paramElement.getLocalName(), SchemaSymbols.ATT_VALUE }, paramElement);
          }
          fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
          paramElement = DOMUtil.getNextSiblingElement(paramElement);
          continue;
        }
        s1 = (short)(s1 | s3);
        if (((Boolean)arrayOfObject[XSAttributeChecker.ATTIDX_FIXED]).booleanValue()) {
          s2 = (short)(s2 | s3);
        }
        switch (s3)
        {
        case 2: 
          xsFacets.minLength = ((XInt)arrayOfObject[XSAttributeChecker.ATTIDX_VALUE]).intValue();
          break;
        case 4: 
          xsFacets.maxLength = ((XInt)arrayOfObject[XSAttributeChecker.ATTIDX_VALUE]).intValue();
          break;
        case 64: 
          xsFacets.maxExclusive = ((String)arrayOfObject[XSAttributeChecker.ATTIDX_VALUE]);
          break;
        case 32: 
          xsFacets.maxInclusive = ((String)arrayOfObject[XSAttributeChecker.ATTIDX_VALUE]);
          break;
        case 128: 
          xsFacets.minExclusive = ((String)arrayOfObject[XSAttributeChecker.ATTIDX_VALUE]);
          break;
        case 256: 
          xsFacets.minInclusive = ((String)arrayOfObject[XSAttributeChecker.ATTIDX_VALUE]);
          break;
        case 512: 
          xsFacets.totalDigits = ((XInt)arrayOfObject[XSAttributeChecker.ATTIDX_VALUE]).intValue();
          break;
        case 1024: 
          xsFacets.fractionDigits = ((XInt)arrayOfObject[XSAttributeChecker.ATTIDX_VALUE]).intValue();
          break;
        case 16: 
          xsFacets.whiteSpace = ((XInt)arrayOfObject[XSAttributeChecker.ATTIDX_VALUE]).shortValue();
          break;
        case 1: 
          xsFacets.length = ((XInt)arrayOfObject[XSAttributeChecker.ATTIDX_VALUE]).intValue();
        }
        localObject1 = DOMUtil.getFirstChildElement(paramElement);
        localObject2 = null;
        if ((localObject1 != null) && (DOMUtil.getLocalName((Node)localObject1).equals(SchemaSymbols.ELT_ANNOTATION)))
        {
          localObject2 = traverseAnnotationDecl((Element)localObject1, arrayOfObject, false, paramXSDocumentInfo);
          localObject1 = DOMUtil.getNextSiblingElement((Node)localObject1);
        }
        else
        {
          localObject3 = DOMUtil.getSyntheticAnnotation(paramElement);
          if (localObject3 != null) {
            localObject2 = traverseSyntheticAnnotation(paramElement, (String)localObject3, arrayOfObject, false, paramXSDocumentInfo);
          }
        }
        switch (s3)
        {
        case 2: 
          xsFacets.minLengthAnnotation = ((XSAnnotation)localObject2);
          break;
        case 4: 
          xsFacets.maxLengthAnnotation = ((XSAnnotation)localObject2);
          break;
        case 64: 
          xsFacets.maxExclusiveAnnotation = ((XSAnnotation)localObject2);
          break;
        case 32: 
          xsFacets.maxInclusiveAnnotation = ((XSAnnotation)localObject2);
          break;
        case 128: 
          xsFacets.minExclusiveAnnotation = ((XSAnnotation)localObject2);
          break;
        case 256: 
          xsFacets.minInclusiveAnnotation = ((XSAnnotation)localObject2);
          break;
        case 512: 
          xsFacets.totalDigitsAnnotation = ((XSAnnotation)localObject2);
          break;
        case 1024: 
          xsFacets.fractionDigitsAnnotation = ((XSAnnotation)localObject2);
          break;
        case 16: 
          xsFacets.whiteSpaceAnnotation = ((XSAnnotation)localObject2);
          break;
        case 1: 
          xsFacets.lengthAnnotation = ((XSAnnotation)localObject2);
        }
        if (localObject1 != null) {
          reportSchemaError("s4s-elt-must-match.1", new Object[] { str1, "(annotation?)", DOMUtil.getLocalName((Node)localObject1) }, (Element)localObject1);
        }
      }
      fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
      paramElement = DOMUtil.getNextSiblingElement(paramElement);
    }
    if (localVector1 != null)
    {
      s1 = (short)(s1 | 0x800);
      xsFacets.enumeration = localVector1;
      xsFacets.enumNSDecls = localVector2;
      xsFacets.enumAnnotations = localXSObjectListImpl1;
    }
    if ((s1 & 0x8) != 0)
    {
      xsFacets.pattern = fPattern.toString();
      xsFacets.patternAnnotations = localXSObjectListImpl2;
    }
    fPattern.setLength(0);
    return new FacetInfo(xsFacets, paramElement, s1, s2);
  }
  
  private boolean containsQName(XSSimpleType paramXSSimpleType)
  {
    if (paramXSSimpleType.getVariety() == 1)
    {
      int i = paramXSSimpleType.getPrimitiveKind();
      return (i == 18) || (i == 20);
    }
    if (paramXSSimpleType.getVariety() == 2) {
      return containsQName((XSSimpleType)paramXSSimpleType.getItemType());
    }
    if (paramXSSimpleType.getVariety() == 3)
    {
      XSObjectList localXSObjectList = paramXSSimpleType.getMemberTypes();
      for (int j = 0; j < localXSObjectList.getLength(); j++) {
        if (containsQName((XSSimpleType)localXSObjectList.item(j))) {
          return true;
        }
      }
    }
    return false;
  }
  
  Element traverseAttrsAndAttrGrps(Element paramElement, XSAttributeGroupDecl paramXSAttributeGroupDecl, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar, XSComplexTypeDecl paramXSComplexTypeDecl)
  {
    Element localElement = null;
    XSAttributeGroupDecl localXSAttributeGroupDecl = null;
    XSAttributeUseImpl localXSAttributeUseImpl = null;
    XSAttributeUse localXSAttributeUse = null;
    String str1;
    Object localObject1;
    Object localObject2;
    for (localElement = paramElement; localElement != null; localElement = DOMUtil.getNextSiblingElement(localElement))
    {
      str1 = DOMUtil.getLocalName(localElement);
      if (str1.equals(SchemaSymbols.ELT_ATTRIBUTE))
      {
        localXSAttributeUseImpl = fSchemaHandler.fAttributeTraverser.traverseLocal(localElement, paramXSDocumentInfo, paramSchemaGrammar, paramXSComplexTypeDecl);
        if (localXSAttributeUseImpl != null) {
          if (fUse == 2)
          {
            paramXSAttributeGroupDecl.addAttributeUse(localXSAttributeUseImpl);
          }
          else
          {
            localXSAttributeUse = paramXSAttributeGroupDecl.getAttributeUseNoProhibited(fAttrDecl.getNamespace(), fAttrDecl.getName());
            if (localXSAttributeUse == null)
            {
              localObject1 = paramXSAttributeGroupDecl.addAttributeUse(localXSAttributeUseImpl);
              if (localObject1 != null)
              {
                localObject2 = paramXSComplexTypeDecl == null ? "ag-props-correct.3" : "ct-props-correct.5";
                String str2 = paramXSComplexTypeDecl == null ? fName : paramXSComplexTypeDecl.getName();
                reportSchemaError((String)localObject2, new Object[] { str2, fAttrDecl.getName(), localObject1 }, localElement);
              }
            }
            else if (localXSAttributeUse != localXSAttributeUseImpl)
            {
              localObject1 = paramXSComplexTypeDecl == null ? "ag-props-correct.2" : "ct-props-correct.4";
              localObject2 = paramXSComplexTypeDecl == null ? fName : paramXSComplexTypeDecl.getName();
              reportSchemaError((String)localObject1, new Object[] { localObject2, fAttrDecl.getName() }, localElement);
            }
          }
        }
      }
      else
      {
        if (!str1.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP)) {
          break;
        }
        localXSAttributeGroupDecl = fSchemaHandler.fAttributeGroupTraverser.traverseLocal(localElement, paramXSDocumentInfo, paramSchemaGrammar);
        if (localXSAttributeGroupDecl != null)
        {
          localObject1 = localXSAttributeGroupDecl.getAttributeUses();
          int i = ((XSObjectList)localObject1).getLength();
          String str5;
          for (int j = 0; j < i; j++)
          {
            localObject2 = (XSAttributeUseImpl)((XSObjectList)localObject1).item(j);
            if (fUse == 2)
            {
              paramXSAttributeGroupDecl.addAttributeUse((XSAttributeUseImpl)localObject2);
            }
            else
            {
              localXSAttributeUse = paramXSAttributeGroupDecl.getAttributeUseNoProhibited(fAttrDecl.getNamespace(), fAttrDecl.getName());
              String str6;
              if (localXSAttributeUse == null)
              {
                str5 = paramXSAttributeGroupDecl.addAttributeUse((XSAttributeUseImpl)localObject2);
                if (str5 != null)
                {
                  str6 = paramXSComplexTypeDecl == null ? "ag-props-correct.3" : "ct-props-correct.5";
                  String str7 = paramXSComplexTypeDecl == null ? fName : paramXSComplexTypeDecl.getName();
                  reportSchemaError(str6, new Object[] { str7, fAttrDecl.getName(), str5 }, localElement);
                }
              }
              else if (localObject2 != localXSAttributeUse)
              {
                str5 = paramXSComplexTypeDecl == null ? "ag-props-correct.2" : "ct-props-correct.4";
                str6 = paramXSComplexTypeDecl == null ? fName : paramXSComplexTypeDecl.getName();
                reportSchemaError(str5, new Object[] { str6, fAttrDecl.getName() }, localElement);
              }
            }
          }
          if (fAttributeWC != null) {
            if (fAttributeWC == null)
            {
              fAttributeWC = fAttributeWC;
            }
            else
            {
              fAttributeWC = fAttributeWC.performIntersectionWith(fAttributeWC, fAttributeWC.fProcessContents);
              if (fAttributeWC == null)
              {
                String str4 = paramXSComplexTypeDecl == null ? "src-attribute_group.2" : "src-ct.4";
                str5 = paramXSComplexTypeDecl == null ? fName : paramXSComplexTypeDecl.getName();
                reportSchemaError(str4, new Object[] { str5 }, localElement);
              }
            }
          }
        }
      }
    }
    if (localElement != null)
    {
      str1 = DOMUtil.getLocalName(localElement);
      if (str1.equals(SchemaSymbols.ELT_ANYATTRIBUTE))
      {
        localObject1 = fSchemaHandler.fWildCardTraverser.traverseAnyAttribute(localElement, paramXSDocumentInfo, paramSchemaGrammar);
        if (fAttributeWC == null)
        {
          fAttributeWC = ((XSWildcardDecl)localObject1);
        }
        else
        {
          fAttributeWC = ((XSWildcardDecl)localObject1).performIntersectionWith(fAttributeWC, fProcessContents);
          if (fAttributeWC == null)
          {
            localObject2 = paramXSComplexTypeDecl == null ? "src-attribute_group.2" : "src-ct.4";
            String str3 = paramXSComplexTypeDecl == null ? fName : paramXSComplexTypeDecl.getName();
            reportSchemaError((String)localObject2, new Object[] { str3 }, localElement);
          }
        }
        localElement = DOMUtil.getNextSiblingElement(localElement);
      }
    }
    return localElement;
  }
  
  void reportSchemaError(String paramString, Object[] paramArrayOfObject, Element paramElement)
  {
    fSchemaHandler.reportSchemaError(paramString, paramArrayOfObject, paramElement);
  }
  
  void checkNotationType(String paramString, XSTypeDefinition paramXSTypeDefinition, Element paramElement)
  {
    if ((paramXSTypeDefinition.getTypeCategory() == 16) && (((XSSimpleType)paramXSTypeDefinition).getVariety() == 1) && (((XSSimpleType)paramXSTypeDefinition).getPrimitiveKind() == 20) && ((((XSSimpleType)paramXSTypeDefinition).getDefinedFacets() & 0x800) == 0)) {
      reportSchemaError("enumeration-required-notation", new Object[] { paramXSTypeDefinition.getName(), paramString, DOMUtil.getLocalName(paramElement) }, paramElement);
    }
  }
  
  protected XSParticleDecl checkOccurrences(XSParticleDecl paramXSParticleDecl, String paramString, Element paramElement, int paramInt, long paramLong)
  {
    int i = fMinOccurs;
    int j = fMaxOccurs;
    int k = (paramLong & 1 << XSAttributeChecker.ATTIDX_MINOCCURS) != 0L ? 1 : 0;
    int m = (paramLong & 1 << XSAttributeChecker.ATTIDX_MAXOCCURS) != 0L ? 1 : 0;
    int n = (paramInt & 0x1) != 0 ? 1 : 0;
    int i1 = (paramInt & 0x8) != 0 ? 1 : 0;
    int i2 = (paramInt & 0x2) != 0 ? 1 : 0;
    int i3 = (paramInt & 0x4) != 0 ? 1 : 0;
    if (i3 != 0)
    {
      Object[] arrayOfObject;
      if (k == 0)
      {
        arrayOfObject = new Object[] { paramString, "minOccurs" };
        reportSchemaError("s4s-att-not-allowed", arrayOfObject, paramElement);
        i = 1;
      }
      if (m == 0)
      {
        arrayOfObject = new Object[] { paramString, "maxOccurs" };
        reportSchemaError("s4s-att-not-allowed", arrayOfObject, paramElement);
        j = 1;
      }
    }
    if ((i == 0) && (j == 0))
    {
      fType = 0;
      return null;
    }
    if (n != 0)
    {
      if (j != 1)
      {
        reportSchemaError("cos-all-limited.2", new Object[] { j == -1 ? "unbounded" : Integer.toString(j), ((XSElementDecl)fValue).getName() }, paramElement);
        j = 1;
        if (i > 1) {
          i = 1;
        }
      }
    }
    else if (((i1 != 0) || (i2 != 0)) && (j != 1))
    {
      reportSchemaError("cos-all-limited.1.2", null, paramElement);
      if (i > 1) {
        i = 1;
      }
      j = 1;
    }
    fMinOccurs = i;
    fMaxOccurs = j;
    return paramXSParticleDecl;
  }
  
  private static String processAttValue(String paramString)
  {
    int i = paramString.length();
    for (int j = 0; j < i; j++)
    {
      int k = paramString.charAt(j);
      if ((k == 34) || (k == 60) || (k == 38) || (k == 9) || (k == 10) || (k == 13)) {
        return escapeAttValue(paramString, j);
      }
    }
    return paramString;
  }
  
  private static String escapeAttValue(String paramString, int paramInt)
  {
    int j = paramString.length();
    StringBuffer localStringBuffer = new StringBuffer(j);
    localStringBuffer.append(paramString.substring(0, paramInt));
    for (int i = paramInt; i < j; i++)
    {
      char c = paramString.charAt(i);
      if (c == '"') {
        localStringBuffer.append("&quot;");
      } else if (c == '<') {
        localStringBuffer.append("&lt;");
      } else if (c == '&') {
        localStringBuffer.append("&amp;");
      } else if (c == '\t') {
        localStringBuffer.append("&#x9;");
      } else if (c == '\n') {
        localStringBuffer.append("&#xA;");
      } else if (c == '\r') {
        localStringBuffer.append("&#xD;");
      } else {
        localStringBuffer.append(c);
      }
    }
    return localStringBuffer.toString();
  }
  
  static final class FacetInfo
  {
    final XSFacets facetdata;
    final Element nodeAfterFacets;
    final short fPresentFacets;
    final short fFixedFacets;
    
    FacetInfo(XSFacets paramXSFacets, Element paramElement, short paramShort1, short paramShort2)
    {
      facetdata = paramXSFacets;
      nodeAfterFacets = paramElement;
      fPresentFacets = paramShort1;
      fFixedFacets = paramShort2;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\traversers\XSDAbstractTraverser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */