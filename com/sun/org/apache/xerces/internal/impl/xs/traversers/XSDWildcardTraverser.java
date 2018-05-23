package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XSAnnotationImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSDeclarationPool;
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSWildcardDecl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import org.w3c.dom.Element;

class XSDWildcardTraverser
  extends XSDAbstractTraverser
{
  XSDWildcardTraverser(XSDHandler paramXSDHandler, XSAttributeChecker paramXSAttributeChecker)
  {
    super(paramXSDHandler, paramXSAttributeChecker);
  }
  
  XSParticleDecl traverseAny(Element paramElement, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar)
  {
    Object[] arrayOfObject = fAttrChecker.checkAttributes(paramElement, false, paramXSDocumentInfo);
    XSWildcardDecl localXSWildcardDecl = traverseWildcardDecl(paramElement, arrayOfObject, paramXSDocumentInfo, paramSchemaGrammar);
    XSParticleDecl localXSParticleDecl = null;
    if (localXSWildcardDecl != null)
    {
      int i = ((XInt)arrayOfObject[XSAttributeChecker.ATTIDX_MINOCCURS]).intValue();
      int j = ((XInt)arrayOfObject[XSAttributeChecker.ATTIDX_MAXOCCURS]).intValue();
      if (j != 0)
      {
        if (fSchemaHandler.fDeclPool != null) {
          localXSParticleDecl = fSchemaHandler.fDeclPool.getParticleDecl();
        } else {
          localXSParticleDecl = new XSParticleDecl();
        }
        fType = 2;
        fValue = localXSWildcardDecl;
        fMinOccurs = i;
        fMaxOccurs = j;
        fAnnotations = fAnnotations;
      }
    }
    fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
    return localXSParticleDecl;
  }
  
  XSWildcardDecl traverseAnyAttribute(Element paramElement, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar)
  {
    Object[] arrayOfObject = fAttrChecker.checkAttributes(paramElement, false, paramXSDocumentInfo);
    XSWildcardDecl localXSWildcardDecl = traverseWildcardDecl(paramElement, arrayOfObject, paramXSDocumentInfo, paramSchemaGrammar);
    fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
    return localXSWildcardDecl;
  }
  
  XSWildcardDecl traverseWildcardDecl(Element paramElement, Object[] paramArrayOfObject, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar)
  {
    XSWildcardDecl localXSWildcardDecl = new XSWildcardDecl();
    XInt localXInt1 = (XInt)paramArrayOfObject[XSAttributeChecker.ATTIDX_NAMESPACE];
    fType = localXInt1.shortValue();
    fNamespaceList = ((String[])paramArrayOfObject[XSAttributeChecker.ATTIDX_NAMESPACE_LIST]);
    XInt localXInt2 = (XInt)paramArrayOfObject[XSAttributeChecker.ATTIDX_PROCESSCONTENTS];
    fProcessContents = localXInt2.shortValue();
    Element localElement = DOMUtil.getFirstChildElement(paramElement);
    XSAnnotationImpl localXSAnnotationImpl = null;
    Object localObject;
    if (localElement != null)
    {
      if (DOMUtil.getLocalName(localElement).equals(SchemaSymbols.ELT_ANNOTATION))
      {
        localXSAnnotationImpl = traverseAnnotationDecl(localElement, paramArrayOfObject, false, paramXSDocumentInfo);
        localElement = DOMUtil.getNextSiblingElement(localElement);
      }
      else
      {
        localObject = DOMUtil.getSyntheticAnnotation(paramElement);
        if (localObject != null) {
          localXSAnnotationImpl = traverseSyntheticAnnotation(paramElement, (String)localObject, paramArrayOfObject, false, paramXSDocumentInfo);
        }
      }
      if (localElement != null) {
        reportSchemaError("s4s-elt-must-match.1", new Object[] { "wildcard", "(annotation?)", DOMUtil.getLocalName(localElement) }, paramElement);
      }
    }
    else
    {
      localObject = DOMUtil.getSyntheticAnnotation(paramElement);
      if (localObject != null) {
        localXSAnnotationImpl = traverseSyntheticAnnotation(paramElement, (String)localObject, paramArrayOfObject, false, paramXSDocumentInfo);
      }
    }
    if (localXSAnnotationImpl != null)
    {
      localObject = new XSObjectListImpl();
      ((XSObjectListImpl)localObject).addXSObject(localXSAnnotationImpl);
    }
    else
    {
      localObject = XSObjectListImpl.EMPTY_LIST;
    }
    fAnnotations = ((XSObjectList)localObject);
    return localXSWildcardDecl;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\traversers\XSDWildcardTraverser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */