package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

final class SymbolTable
{
  private final Map<String, Stylesheet> _stylesheets = new HashMap();
  private final Map<String, Vector> _primops = new HashMap();
  private Map<String, VariableBase> _variables = null;
  private Map<String, Template> _templates = null;
  private Map<String, AttributeSet> _attributeSets = null;
  private Map<String, String> _aliases = null;
  private Map<String, Integer> _excludedURI = null;
  private Stack<Map<String, Integer>> _excludedURIStack = null;
  private Map<String, DecimalFormatting> _decimalFormats = null;
  private Map<String, Key> _keys = null;
  private int _nsCounter = 0;
  private SyntaxTreeNode _current = null;
  
  SymbolTable() {}
  
  public DecimalFormatting getDecimalFormatting(QName paramQName)
  {
    if (_decimalFormats == null) {
      return null;
    }
    return (DecimalFormatting)_decimalFormats.get(paramQName.getStringRep());
  }
  
  public void addDecimalFormatting(QName paramQName, DecimalFormatting paramDecimalFormatting)
  {
    if (_decimalFormats == null) {
      _decimalFormats = new HashMap();
    }
    _decimalFormats.put(paramQName.getStringRep(), paramDecimalFormatting);
  }
  
  public Key getKey(QName paramQName)
  {
    if (_keys == null) {
      return null;
    }
    return (Key)_keys.get(paramQName.getStringRep());
  }
  
  public void addKey(QName paramQName, Key paramKey)
  {
    if (_keys == null) {
      _keys = new HashMap();
    }
    _keys.put(paramQName.getStringRep(), paramKey);
  }
  
  public Stylesheet addStylesheet(QName paramQName, Stylesheet paramStylesheet)
  {
    return (Stylesheet)_stylesheets.put(paramQName.getStringRep(), paramStylesheet);
  }
  
  public Stylesheet lookupStylesheet(QName paramQName)
  {
    return (Stylesheet)_stylesheets.get(paramQName.getStringRep());
  }
  
  public Template addTemplate(Template paramTemplate)
  {
    QName localQName = paramTemplate.getName();
    if (_templates == null) {
      _templates = new HashMap();
    }
    return (Template)_templates.put(localQName.getStringRep(), paramTemplate);
  }
  
  public Template lookupTemplate(QName paramQName)
  {
    if (_templates == null) {
      return null;
    }
    return (Template)_templates.get(paramQName.getStringRep());
  }
  
  public Variable addVariable(Variable paramVariable)
  {
    if (_variables == null) {
      _variables = new HashMap();
    }
    String str = paramVariable.getName().getStringRep();
    return (Variable)_variables.put(str, paramVariable);
  }
  
  public Param addParam(Param paramParam)
  {
    if (_variables == null) {
      _variables = new HashMap();
    }
    String str = paramParam.getName().getStringRep();
    return (Param)_variables.put(str, paramParam);
  }
  
  public Variable lookupVariable(QName paramQName)
  {
    if (_variables == null) {
      return null;
    }
    String str = paramQName.getStringRep();
    VariableBase localVariableBase = (VariableBase)_variables.get(str);
    return (localVariableBase instanceof Variable) ? (Variable)localVariableBase : null;
  }
  
  public Param lookupParam(QName paramQName)
  {
    if (_variables == null) {
      return null;
    }
    String str = paramQName.getStringRep();
    VariableBase localVariableBase = (VariableBase)_variables.get(str);
    return (localVariableBase instanceof Param) ? (Param)localVariableBase : null;
  }
  
  public SyntaxTreeNode lookupName(QName paramQName)
  {
    if (_variables == null) {
      return null;
    }
    String str = paramQName.getStringRep();
    return (SyntaxTreeNode)_variables.get(str);
  }
  
  public AttributeSet addAttributeSet(AttributeSet paramAttributeSet)
  {
    if (_attributeSets == null) {
      _attributeSets = new HashMap();
    }
    return (AttributeSet)_attributeSets.put(paramAttributeSet.getName().getStringRep(), paramAttributeSet);
  }
  
  public AttributeSet lookupAttributeSet(QName paramQName)
  {
    if (_attributeSets == null) {
      return null;
    }
    return (AttributeSet)_attributeSets.get(paramQName.getStringRep());
  }
  
  public void addPrimop(String paramString, MethodType paramMethodType)
  {
    Vector localVector = (Vector)_primops.get(paramString);
    if (localVector == null) {
      _primops.put(paramString, localVector = new Vector());
    }
    localVector.addElement(paramMethodType);
  }
  
  public Vector lookupPrimop(String paramString)
  {
    return (Vector)_primops.get(paramString);
  }
  
  public String generateNamespacePrefix()
  {
    return "ns" + _nsCounter++;
  }
  
  public void setCurrentNode(SyntaxTreeNode paramSyntaxTreeNode)
  {
    _current = paramSyntaxTreeNode;
  }
  
  public String lookupNamespace(String paramString)
  {
    if (_current == null) {
      return "";
    }
    return _current.lookupNamespace(paramString);
  }
  
  public void addPrefixAlias(String paramString1, String paramString2)
  {
    if (_aliases == null) {
      _aliases = new HashMap();
    }
    _aliases.put(paramString1, paramString2);
  }
  
  public String lookupPrefixAlias(String paramString)
  {
    if (_aliases == null) {
      return null;
    }
    return (String)_aliases.get(paramString);
  }
  
  public void excludeURI(String paramString)
  {
    if (paramString == null) {
      return;
    }
    if (_excludedURI == null) {
      _excludedURI = new HashMap();
    }
    Integer localInteger = (Integer)_excludedURI.get(paramString);
    if (localInteger == null) {
      localInteger = Integer.valueOf(1);
    } else {
      localInteger = Integer.valueOf(localInteger.intValue() + 1);
    }
    _excludedURI.put(paramString, localInteger);
  }
  
  public void excludeNamespaces(String paramString)
  {
    if (paramString != null)
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString);
      while (localStringTokenizer.hasMoreTokens())
      {
        String str1 = localStringTokenizer.nextToken();
        String str2;
        if (str1.equals("#default")) {
          str2 = lookupNamespace("");
        } else {
          str2 = lookupNamespace(str1);
        }
        if (str2 != null) {
          excludeURI(str2);
        }
      }
    }
  }
  
  public boolean isExcludedNamespace(String paramString)
  {
    if ((paramString != null) && (_excludedURI != null))
    {
      Integer localInteger = (Integer)_excludedURI.get(paramString);
      return (localInteger != null) && (localInteger.intValue() > 0);
    }
    return false;
  }
  
  public void unExcludeNamespaces(String paramString)
  {
    if (_excludedURI == null) {
      return;
    }
    if (paramString != null)
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString);
      while (localStringTokenizer.hasMoreTokens())
      {
        String str1 = localStringTokenizer.nextToken();
        String str2;
        if (str1.equals("#default")) {
          str2 = lookupNamespace("");
        } else {
          str2 = lookupNamespace(str1);
        }
        Integer localInteger = (Integer)_excludedURI.get(str2);
        if (localInteger != null) {
          _excludedURI.put(str2, Integer.valueOf(localInteger.intValue() - 1));
        }
      }
    }
  }
  
  public void pushExcludedNamespacesContext()
  {
    if (_excludedURIStack == null) {
      _excludedURIStack = new Stack();
    }
    _excludedURIStack.push(_excludedURI);
    _excludedURI = null;
  }
  
  public void popExcludedNamespacesContext()
  {
    _excludedURI = ((Map)_excludedURIStack.pop());
    if (_excludedURIStack.isEmpty()) {
      _excludedURIStack = null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\SymbolTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */