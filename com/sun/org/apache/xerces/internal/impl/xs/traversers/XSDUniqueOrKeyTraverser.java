package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XSElementDecl;
import com.sun.org.apache.xerces.internal.impl.xs.identity.IdentityConstraint;
import com.sun.org.apache.xerces.internal.impl.xs.identity.UniqueOrKey;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import org.w3c.dom.Element;

class XSDUniqueOrKeyTraverser
  extends XSDAbstractIDConstraintTraverser
{
  public XSDUniqueOrKeyTraverser(XSDHandler paramXSDHandler, XSAttributeChecker paramXSAttributeChecker)
  {
    super(paramXSDHandler, paramXSAttributeChecker);
  }
  
  void traverse(Element paramElement, XSElementDecl paramXSElementDecl, XSDocumentInfo paramXSDocumentInfo, SchemaGrammar paramSchemaGrammar)
  {
    Object[] arrayOfObject = fAttrChecker.checkAttributes(paramElement, false, paramXSDocumentInfo);
    String str1 = (String)arrayOfObject[XSAttributeChecker.ATTIDX_NAME];
    if (str1 == null)
    {
      reportSchemaError("s4s-att-must-appear", new Object[] { DOMUtil.getLocalName(paramElement), SchemaSymbols.ATT_NAME }, paramElement);
      fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
      return;
    }
    UniqueOrKey localUniqueOrKey = null;
    if (DOMUtil.getLocalName(paramElement).equals(SchemaSymbols.ELT_UNIQUE)) {
      localUniqueOrKey = new UniqueOrKey(fTargetNamespace, str1, fName, (short)3);
    } else {
      localUniqueOrKey = new UniqueOrKey(fTargetNamespace, str1, fName, (short)1);
    }
    if (traverseIdentityConstraint(localUniqueOrKey, paramElement, paramXSDocumentInfo, arrayOfObject))
    {
      if (paramSchemaGrammar.getIDConstraintDecl(localUniqueOrKey.getIdentityConstraintName()) == null) {
        paramSchemaGrammar.addIDConstraintDecl(paramXSElementDecl, localUniqueOrKey);
      }
      String str2 = fSchemaHandler.schemaDocument2SystemId(paramXSDocumentInfo);
      IdentityConstraint localIdentityConstraint = paramSchemaGrammar.getIDConstraintDecl(localUniqueOrKey.getIdentityConstraintName(), str2);
      if (localIdentityConstraint == null) {
        paramSchemaGrammar.addIDConstraintDecl(paramXSElementDecl, localUniqueOrKey, str2);
      }
      if (fSchemaHandler.fTolerateDuplicates)
      {
        if ((localIdentityConstraint != null) && ((localIdentityConstraint instanceof UniqueOrKey))) {
          localUniqueOrKey = localUniqueOrKey;
        }
        fSchemaHandler.addIDConstraintDecl(localUniqueOrKey);
      }
    }
    fAttrChecker.returnAttrArray(arrayOfObject, paramXSDocumentInfo);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\traversers\XSDUniqueOrKeyTraverser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */