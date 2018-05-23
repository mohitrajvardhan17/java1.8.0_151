package com.sun.xml.internal.stream.dtd;

import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.xml.internal.stream.dtd.nonvalidating.DTDGrammar;
import com.sun.xml.internal.stream.dtd.nonvalidating.XMLAttributeDecl;
import com.sun.xml.internal.stream.dtd.nonvalidating.XMLSimpleType;

public class DTDGrammarUtil
{
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
  private static final boolean DEBUG_ATTRIBUTES = false;
  private static final boolean DEBUG_ELEMENT_CHILDREN = false;
  protected DTDGrammar fDTDGrammar = null;
  protected boolean fNamespaces;
  protected SymbolTable fSymbolTable = null;
  private int fCurrentElementIndex = -1;
  private int fCurrentContentSpecType = -1;
  private boolean[] fElementContentState = new boolean[8];
  private int fElementDepth = -1;
  private boolean fInElementContent = false;
  private XMLAttributeDecl fTempAttDecl = new XMLAttributeDecl();
  private QName fTempQName = new QName();
  private StringBuffer fBuffer = new StringBuffer();
  private NamespaceContext fNamespaceContext = null;
  
  public DTDGrammarUtil(SymbolTable paramSymbolTable)
  {
    fSymbolTable = paramSymbolTable;
  }
  
  public DTDGrammarUtil(DTDGrammar paramDTDGrammar, SymbolTable paramSymbolTable)
  {
    fDTDGrammar = paramDTDGrammar;
    fSymbolTable = paramSymbolTable;
  }
  
  public DTDGrammarUtil(DTDGrammar paramDTDGrammar, SymbolTable paramSymbolTable, NamespaceContext paramNamespaceContext)
  {
    fDTDGrammar = paramDTDGrammar;
    fSymbolTable = paramSymbolTable;
    fNamespaceContext = paramNamespaceContext;
  }
  
  public void reset(XMLComponentManager paramXMLComponentManager)
    throws XMLConfigurationException
  {
    fDTDGrammar = null;
    fInElementContent = false;
    fCurrentElementIndex = -1;
    fCurrentContentSpecType = -1;
    fNamespaces = paramXMLComponentManager.getFeature("http://xml.org/sax/features/namespaces", true);
    fSymbolTable = ((SymbolTable)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table"));
    fElementDepth = -1;
  }
  
  public void startElement(QName paramQName, XMLAttributes paramXMLAttributes)
    throws XNIException
  {
    handleStartElement(paramQName, paramXMLAttributes);
  }
  
  public void endElement(QName paramQName)
    throws XNIException
  {
    handleEndElement(paramQName);
  }
  
  public void startCDATA(Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void endCDATA(Augmentations paramAugmentations)
    throws XNIException
  {}
  
  public void addDTDDefaultAttrs(QName paramQName, XMLAttributes paramXMLAttributes)
    throws XNIException
  {
    int i = fDTDGrammar.getElementDeclIndex(paramQName);
    if ((i == -1) || (fDTDGrammar == null)) {
      return;
    }
    String str3;
    int i1;
    String str5;
    boolean bool;
    for (int j = fDTDGrammar.getFirstAttributeDeclIndex(i); j != -1; j = fDTDGrammar.getNextAttributeDeclIndex(j))
    {
      fDTDGrammar.getAttributeDecl(j, fTempAttDecl);
      String str1 = fTempAttDecl.name.prefix;
      String str2 = fTempAttDecl.name.localpart;
      str3 = fTempAttDecl.name.rawname;
      String str4 = getAttributeTypeName(fTempAttDecl);
      i1 = fTempAttDecl.simpleType.defaultType;
      str5 = null;
      if (fTempAttDecl.simpleType.defaultValue != null) {
        str5 = fTempAttDecl.simpleType.defaultValue;
      }
      bool = false;
      int i2 = i1 == 2 ? 1 : 0;
      int i3 = str4 == XMLSymbols.fCDATASymbol ? 1 : 0;
      int i4;
      if ((i3 == 0) || (i2 != 0) || (str5 != null))
      {
        int i5;
        if ((fNamespaceContext != null) && (str3.startsWith("xmlns")))
        {
          String str6 = "";
          i5 = str3.indexOf(':');
          if (i5 != -1) {
            str6 = str3.substring(0, i5);
          } else {
            str6 = str3;
          }
          str6 = fSymbolTable.addSymbol(str6);
          if (!((NamespaceSupport)fNamespaceContext).containsPrefixInCurrentContext(str6)) {
            fNamespaceContext.declarePrefix(str6, str5);
          }
          bool = true;
        }
        else
        {
          i4 = paramXMLAttributes.getLength();
          for (i5 = 0; i5 < i4; i5++) {
            if (paramXMLAttributes.getQName(i5) == str3)
            {
              bool = true;
              break;
            }
          }
        }
      }
      if ((!bool) && (str5 != null))
      {
        if (fNamespaces)
        {
          i4 = str3.indexOf(':');
          if (i4 != -1)
          {
            str1 = str3.substring(0, i4);
            str1 = fSymbolTable.addSymbol(str1);
            str2 = str3.substring(i4 + 1);
            str2 = fSymbolTable.addSymbol(str2);
          }
        }
        fTempQName.setValues(str1, str2, str3, fTempAttDecl.name.uri);
        i4 = paramXMLAttributes.addAttribute(fTempQName, str4, str5);
      }
    }
    int k = paramXMLAttributes.getLength();
    for (int m = 0; m < k; m++)
    {
      str3 = paramXMLAttributes.getQName(m);
      int n = 0;
      for (i1 = fDTDGrammar.getFirstAttributeDeclIndex(i); i1 != -1; i1 = fDTDGrammar.getNextAttributeDeclIndex(i1))
      {
        fDTDGrammar.getAttributeDecl(i1, fTempAttDecl);
        if (fTempAttDecl.name.rawname == str3)
        {
          n = 1;
          break;
        }
      }
      if (n != 0)
      {
        str5 = getAttributeTypeName(fTempAttDecl);
        paramXMLAttributes.setType(m, str5);
        bool = false;
        if ((paramXMLAttributes.isSpecified(m)) && (str5 != XMLSymbols.fCDATASymbol)) {
          bool = normalizeAttrValue(paramXMLAttributes, m);
        }
      }
    }
  }
  
  private boolean normalizeAttrValue(XMLAttributes paramXMLAttributes, int paramInt)
  {
    int i = 1;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    String str1 = paramXMLAttributes.getValue(paramInt);
    char[] arrayOfChar = new char[str1.length()];
    fBuffer.setLength(0);
    str1.getChars(0, str1.length(), arrayOfChar, 0);
    for (int i1 = 0; i1 < arrayOfChar.length; i1++) {
      if (arrayOfChar[i1] == ' ')
      {
        if (k != 0)
        {
          j = 1;
          k = 0;
        }
        if ((j != 0) && (i == 0))
        {
          j = 0;
          fBuffer.append(arrayOfChar[i1]);
          m++;
        }
        else if ((i != 0) || (j == 0))
        {
          n++;
        }
      }
      else
      {
        k = 1;
        j = 0;
        i = 0;
        fBuffer.append(arrayOfChar[i1]);
        m++;
      }
    }
    if ((m > 0) && (fBuffer.charAt(m - 1) == ' ')) {
      fBuffer.setLength(m - 1);
    }
    String str2 = fBuffer.toString();
    paramXMLAttributes.setValue(paramInt, str2);
    return !str1.equals(str2);
  }
  
  private String getAttributeTypeName(XMLAttributeDecl paramXMLAttributeDecl)
  {
    switch (simpleType.type)
    {
    case 1: 
      return simpleType.list ? XMLSymbols.fENTITIESSymbol : XMLSymbols.fENTITYSymbol;
    case 2: 
      StringBuffer localStringBuffer = new StringBuffer();
      localStringBuffer.append('(');
      for (int i = 0; i < simpleType.enumeration.length; i++)
      {
        if (i > 0) {
          localStringBuffer.append("|");
        }
        localStringBuffer.append(simpleType.enumeration[i]);
      }
      localStringBuffer.append(')');
      return fSymbolTable.addSymbol(localStringBuffer.toString());
    case 3: 
      return XMLSymbols.fIDSymbol;
    case 4: 
      return simpleType.list ? XMLSymbols.fIDREFSSymbol : XMLSymbols.fIDREFSymbol;
    case 5: 
      return simpleType.list ? XMLSymbols.fNMTOKENSSymbol : XMLSymbols.fNMTOKENSymbol;
    case 6: 
      return XMLSymbols.fNOTATIONSymbol;
    }
    return XMLSymbols.fCDATASymbol;
  }
  
  private void ensureStackCapacity(int paramInt)
  {
    if (paramInt == fElementContentState.length)
    {
      boolean[] arrayOfBoolean = new boolean[paramInt * 2];
      System.arraycopy(fElementContentState, 0, arrayOfBoolean, 0, paramInt);
      fElementContentState = arrayOfBoolean;
    }
  }
  
  protected void handleStartElement(QName paramQName, XMLAttributes paramXMLAttributes)
    throws XNIException
  {
    if (fDTDGrammar == null)
    {
      fCurrentElementIndex = -1;
      fCurrentContentSpecType = -1;
      fInElementContent = false;
      return;
    }
    fCurrentElementIndex = fDTDGrammar.getElementDeclIndex(paramQName);
    fCurrentContentSpecType = fDTDGrammar.getContentSpecType(fCurrentElementIndex);
    addDTDDefaultAttrs(paramQName, paramXMLAttributes);
    fInElementContent = (fCurrentContentSpecType == 3);
    fElementDepth += 1;
    ensureStackCapacity(fElementDepth);
    fElementContentState[fElementDepth] = fInElementContent;
  }
  
  protected void handleEndElement(QName paramQName)
    throws XNIException
  {
    if (fDTDGrammar == null) {
      return;
    }
    fElementDepth -= 1;
    if (fElementDepth < -1) {
      throw new RuntimeException("FWK008 Element stack underflow");
    }
    if (fElementDepth < 0)
    {
      fCurrentElementIndex = -1;
      fCurrentContentSpecType = -1;
      fInElementContent = false;
      return;
    }
    fInElementContent = fElementContentState[fElementDepth];
  }
  
  public boolean isInElementContent()
  {
    return fInElementContent;
  }
  
  public boolean isIgnorableWhiteSpace(XMLString paramXMLString)
  {
    if (isInElementContent())
    {
      for (int i = offset; i < offset + length; i++) {
        if (!XMLChar.isSpace(ch[i])) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\dtd\DTDGrammarUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */