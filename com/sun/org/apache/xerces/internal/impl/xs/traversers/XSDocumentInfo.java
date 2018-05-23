package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import com.sun.org.apache.xerces.internal.impl.validation.ValidationState;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaNamespaceSupport;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaException;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import java.util.Stack;
import java.util.Vector;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class XSDocumentInfo
{
  protected SchemaNamespaceSupport fNamespaceSupport;
  protected SchemaNamespaceSupport fNamespaceSupportRoot;
  protected Stack SchemaNamespaceSupportStack = new Stack();
  protected boolean fAreLocalAttributesQualified;
  protected boolean fAreLocalElementsQualified;
  protected short fBlockDefault;
  protected short fFinalDefault;
  String fTargetNamespace;
  protected boolean fIsChameleonSchema;
  protected Element fSchemaElement;
  Vector fImportedNS = new Vector();
  protected ValidationState fValidationContext = new ValidationState();
  SymbolTable fSymbolTable = null;
  protected XSAttributeChecker fAttrChecker;
  protected Object[] fSchemaAttrs;
  protected XSAnnotationInfo fAnnotations = null;
  private Vector fReportedTNS = null;
  
  XSDocumentInfo(Element paramElement, XSAttributeChecker paramXSAttributeChecker, SymbolTable paramSymbolTable)
    throws XMLSchemaException
  {
    fSchemaElement = paramElement;
    initNamespaceSupport(paramElement);
    fIsChameleonSchema = false;
    fSymbolTable = paramSymbolTable;
    fAttrChecker = paramXSAttributeChecker;
    if (paramElement != null)
    {
      Element localElement = paramElement;
      fSchemaAttrs = paramXSAttributeChecker.checkAttributes(localElement, true, this);
      if (fSchemaAttrs == null) {
        throw new XMLSchemaException(null, null);
      }
      fAreLocalAttributesQualified = (((XInt)fSchemaAttrs[XSAttributeChecker.ATTIDX_AFORMDEFAULT]).intValue() == 1);
      fAreLocalElementsQualified = (((XInt)fSchemaAttrs[XSAttributeChecker.ATTIDX_EFORMDEFAULT]).intValue() == 1);
      fBlockDefault = ((XInt)fSchemaAttrs[XSAttributeChecker.ATTIDX_BLOCKDEFAULT]).shortValue();
      fFinalDefault = ((XInt)fSchemaAttrs[XSAttributeChecker.ATTIDX_FINALDEFAULT]).shortValue();
      fTargetNamespace = ((String)fSchemaAttrs[XSAttributeChecker.ATTIDX_TARGETNAMESPACE]);
      if (fTargetNamespace != null) {
        fTargetNamespace = paramSymbolTable.addSymbol(fTargetNamespace);
      }
      fNamespaceSupportRoot = new SchemaNamespaceSupport(fNamespaceSupport);
      fValidationContext.setNamespaceSupport(fNamespaceSupport);
      fValidationContext.setSymbolTable(paramSymbolTable);
    }
  }
  
  private void initNamespaceSupport(Element paramElement)
  {
    fNamespaceSupport = new SchemaNamespaceSupport();
    fNamespaceSupport.reset();
    for (Node localNode = paramElement.getParentNode(); (localNode != null) && (localNode.getNodeType() == 1) && (!localNode.getNodeName().equals("DOCUMENT_NODE")); localNode = localNode.getParentNode())
    {
      Element localElement = (Element)localNode;
      NamedNodeMap localNamedNodeMap = localElement.getAttributes();
      int i = localNamedNodeMap != null ? localNamedNodeMap.getLength() : 0;
      for (int j = 0; j < i; j++)
      {
        Attr localAttr = (Attr)localNamedNodeMap.item(j);
        String str1 = localAttr.getNamespaceURI();
        if ((str1 != null) && (str1.equals("http://www.w3.org/2000/xmlns/")))
        {
          String str2 = localAttr.getLocalName().intern();
          if (str2 == "xmlns") {
            str2 = "";
          }
          if (fNamespaceSupport.getURI(str2) == null) {
            fNamespaceSupport.declarePrefix(str2, localAttr.getValue().intern());
          }
        }
      }
    }
  }
  
  void backupNSSupport(SchemaNamespaceSupport paramSchemaNamespaceSupport)
  {
    SchemaNamespaceSupportStack.push(fNamespaceSupport);
    if (paramSchemaNamespaceSupport == null) {
      paramSchemaNamespaceSupport = fNamespaceSupportRoot;
    }
    fNamespaceSupport = new SchemaNamespaceSupport(paramSchemaNamespaceSupport);
    fValidationContext.setNamespaceSupport(fNamespaceSupport);
  }
  
  void restoreNSSupport()
  {
    fNamespaceSupport = ((SchemaNamespaceSupport)SchemaNamespaceSupportStack.pop());
    fValidationContext.setNamespaceSupport(fNamespaceSupport);
  }
  
  public String toString()
  {
    return "targetNamespace is " + fTargetNamespace;
  }
  
  public void addAllowedNS(String paramString)
  {
    fImportedNS.addElement(paramString == null ? "" : paramString);
  }
  
  public boolean isAllowedNS(String paramString)
  {
    return fImportedNS.contains(paramString == null ? "" : paramString);
  }
  
  final boolean needReportTNSError(String paramString)
  {
    if (fReportedTNS == null) {
      fReportedTNS = new Vector();
    } else if (fReportedTNS.contains(paramString)) {
      return false;
    }
    fReportedTNS.addElement(paramString);
    return true;
  }
  
  Object[] getSchemaAttrs()
  {
    return fSchemaAttrs;
  }
  
  void returnSchemaAttrs()
  {
    fAttrChecker.returnAttrArray(fSchemaAttrs, null);
    fSchemaAttrs = null;
  }
  
  void addAnnotation(XSAnnotationInfo paramXSAnnotationInfo)
  {
    next = fAnnotations;
    fAnnotations = paramXSAnnotationInfo;
  }
  
  XSAnnotationInfo getAnnotations()
  {
    return fAnnotations;
  }
  
  void removeAnnotations()
  {
    fAnnotations = null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\traversers\XSDocumentInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */