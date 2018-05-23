package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.serializer.ElemDesc;
import com.sun.org.apache.xml.internal.serializer.ToHTMLStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.xml.sax.helpers.AttributesImpl;

final class LiteralElement
  extends Instruction
{
  private String _name;
  private LiteralElement _literalElemParent = null;
  private List<SyntaxTreeNode> _attributeElements = null;
  private Map<String, String> _accessedPrefixes = null;
  private boolean _allAttributesUnique = false;
  private static final String XMLNS_STRING = "xmlns";
  
  LiteralElement() {}
  
  public QName getName()
  {
    return _qname;
  }
  
  public void display(int paramInt)
  {
    indent(paramInt);
    Util.println("LiteralElement name = " + _name);
    displayContents(paramInt + 4);
  }
  
  private String accessedNamespace(String paramString)
  {
    if (_literalElemParent != null)
    {
      String str = _literalElemParent.accessedNamespace(paramString);
      if (str != null) {
        return str;
      }
    }
    return _accessedPrefixes != null ? (String)_accessedPrefixes.get(paramString) : null;
  }
  
  public void registerNamespace(String paramString1, String paramString2, SymbolTable paramSymbolTable, boolean paramBoolean)
  {
    String str;
    if (_literalElemParent != null)
    {
      str = _literalElemParent.accessedNamespace(paramString1);
      if ((str != null) && (str.equals(paramString2))) {
        return;
      }
    }
    if (_accessedPrefixes == null)
    {
      _accessedPrefixes = new Hashtable();
    }
    else if (!paramBoolean)
    {
      str = (String)_accessedPrefixes.get(paramString1);
      if (str != null)
      {
        if (str.equals(paramString2)) {
          return;
        }
        paramString1 = paramSymbolTable.generateNamespacePrefix();
      }
    }
    if (!paramString1.equals("xml")) {
      _accessedPrefixes.put(paramString1, paramString2);
    }
  }
  
  private String translateQName(QName paramQName, SymbolTable paramSymbolTable)
  {
    String str1 = paramQName.getLocalPart();
    Object localObject = paramQName.getPrefix();
    if (localObject == null) {
      localObject = "";
    } else if (((String)localObject).equals("xmlns")) {
      return "xmlns";
    }
    String str2 = paramSymbolTable.lookupPrefixAlias((String)localObject);
    if (str2 != null)
    {
      paramSymbolTable.excludeNamespaces((String)localObject);
      localObject = str2;
    }
    String str3 = lookupNamespace((String)localObject);
    if (str3 == null) {
      return str1;
    }
    registerNamespace((String)localObject, str3, paramSymbolTable, false);
    if (localObject != "") {
      return (String)localObject + ":" + str1;
    }
    return str1;
  }
  
  public void addAttribute(SyntaxTreeNode paramSyntaxTreeNode)
  {
    if (_attributeElements == null) {
      _attributeElements = new ArrayList(2);
    }
    _attributeElements.add(paramSyntaxTreeNode);
  }
  
  public void setFirstAttribute(SyntaxTreeNode paramSyntaxTreeNode)
  {
    if (_attributeElements == null) {
      _attributeElements = new ArrayList(2);
    }
    _attributeElements.add(0, paramSyntaxTreeNode);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    if (_attributeElements != null)
    {
      Iterator localIterator = _attributeElements.iterator();
      while (localIterator.hasNext())
      {
        SyntaxTreeNode localSyntaxTreeNode = (SyntaxTreeNode)localIterator.next();
        localSyntaxTreeNode.typeCheck(paramSymbolTable);
      }
    }
    typeCheckContents(paramSymbolTable);
    return Type.Void;
  }
  
  public Set<Map.Entry<String, String>> getNamespaceScope(SyntaxTreeNode paramSyntaxTreeNode)
  {
    HashMap localHashMap = new HashMap();
    while (paramSyntaxTreeNode != null)
    {
      Map localMap = paramSyntaxTreeNode.getPrefixMapping();
      if (localMap != null)
      {
        Iterator localIterator = localMap.keySet().iterator();
        while (localIterator.hasNext())
        {
          String str = (String)localIterator.next();
          if (!localHashMap.containsKey(str)) {
            localHashMap.put(str, localMap.get(str));
          }
        }
      }
      paramSyntaxTreeNode = paramSyntaxTreeNode.getParent();
    }
    return localHashMap.entrySet();
  }
  
  public void parseContents(Parser paramParser)
  {
    SymbolTable localSymbolTable = paramParser.getSymbolTable();
    localSymbolTable.setCurrentNode(this);
    SyntaxTreeNode localSyntaxTreeNode = getParent();
    if ((localSyntaxTreeNode != null) && ((localSyntaxTreeNode instanceof LiteralElement))) {
      _literalElemParent = ((LiteralElement)localSyntaxTreeNode);
    }
    _name = translateQName(_qname, localSymbolTable);
    int i = _attributes.getLength();
    Object localObject2;
    String str1;
    Object localObject3;
    for (int j = 0; j < i; j++)
    {
      localObject1 = paramParser.getQName(_attributes.getQName(j));
      localObject2 = ((QName)localObject1).getNamespace();
      str1 = _attributes.getValue(j);
      if (((QName)localObject1).equals(paramParser.getUseAttributeSets()))
      {
        if (!Util.isValidQNames(str1))
        {
          localObject3 = new ErrorMsg("INVALID_QNAME_ERR", str1, this);
          paramParser.reportError(3, (ErrorMsg)localObject3);
        }
        setFirstAttribute(new UseAttributeSets(str1, paramParser));
      }
      else if (((QName)localObject1).equals(paramParser.getExtensionElementPrefixes()))
      {
        localSymbolTable.excludeNamespaces(str1);
      }
      else if (((QName)localObject1).equals(paramParser.getExcludeResultPrefixes()))
      {
        localSymbolTable.excludeNamespaces(str1);
      }
      else
      {
        localObject3 = ((QName)localObject1).getPrefix();
        if (((localObject3 == null) || (!((String)localObject3).equals("xmlns"))) && ((localObject3 != null) || (!((QName)localObject1).getLocalPart().equals("xmlns"))) && ((localObject2 == null) || (!((String)localObject2).equals("http://www.w3.org/1999/XSL/Transform"))))
        {
          String str2 = translateQName((QName)localObject1, localSymbolTable);
          LiteralAttribute localLiteralAttribute = new LiteralAttribute(str2, str1, paramParser, this);
          addAttribute(localLiteralAttribute);
          localLiteralAttribute.setParent(this);
          localLiteralAttribute.parseContents(paramParser);
        }
      }
    }
    Set localSet = getNamespaceScope(this);
    Object localObject1 = localSet.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Map.Entry)((Iterator)localObject1).next();
      str1 = (String)((Map.Entry)localObject2).getKey();
      if (!str1.equals("xml"))
      {
        localObject3 = lookupNamespace(str1);
        if ((localObject3 != null) && (!localSymbolTable.isExcludedNamespace((String)localObject3))) {
          registerNamespace(str1, (String)localObject3, localSymbolTable, true);
        }
      }
    }
    parseChildren(paramParser);
    for (int k = 0; k < i; k++)
    {
      localObject2 = paramParser.getQName(_attributes.getQName(k));
      str1 = _attributes.getValue(k);
      if (((QName)localObject2).equals(paramParser.getExtensionElementPrefixes())) {
        localSymbolTable.unExcludeNamespaces(str1);
      } else if (((QName)localObject2).equals(paramParser.getExcludeResultPrefixes())) {
        localSymbolTable.unExcludeNamespaces(str1);
      }
    }
  }
  
  protected boolean contextDependent()
  {
    return dependentContents();
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    _allAttributesUnique = checkAttributesUnique();
    localInstructionList.append(paramMethodGenerator.loadHandler());
    localInstructionList.append(new PUSH(localConstantPoolGen, _name));
    localInstructionList.append(DUP2);
    localInstructionList.append(paramMethodGenerator.startElement());
    for (int i = 0; i < elementCount(); i++)
    {
      SyntaxTreeNode localSyntaxTreeNode = (SyntaxTreeNode)elementAt(i);
      if ((localSyntaxTreeNode instanceof Variable)) {
        localSyntaxTreeNode.translate(paramClassGenerator, paramMethodGenerator);
      }
    }
    Object localObject;
    if (_accessedPrefixes != null)
    {
      int j = 0;
      localObject = _accessedPrefixes.entrySet().iterator();
      while (((Iterator)localObject).hasNext())
      {
        Map.Entry localEntry = (Map.Entry)((Iterator)localObject).next();
        String str1 = (String)localEntry.getKey();
        String str2 = (String)localEntry.getValue();
        if ((str2 != "") || (str1 != ""))
        {
          if (str1 == "") {
            j = 1;
          }
          localInstructionList.append(paramMethodGenerator.loadHandler());
          localInstructionList.append(new PUSH(localConstantPoolGen, str1));
          localInstructionList.append(new PUSH(localConstantPoolGen, str2));
          localInstructionList.append(paramMethodGenerator.namespace());
        }
      }
      if ((j == 0) && ((_parent instanceof XslElement)) && (((XslElement)_parent).declaresDefaultNS()))
      {
        localInstructionList.append(paramMethodGenerator.loadHandler());
        localInstructionList.append(new PUSH(localConstantPoolGen, ""));
        localInstructionList.append(new PUSH(localConstantPoolGen, ""));
        localInstructionList.append(paramMethodGenerator.namespace());
      }
    }
    if (_attributeElements != null)
    {
      Iterator localIterator = _attributeElements.iterator();
      while (localIterator.hasNext())
      {
        localObject = (SyntaxTreeNode)localIterator.next();
        if (!(localObject instanceof XslAttribute)) {
          ((SyntaxTreeNode)localObject).translate(paramClassGenerator, paramMethodGenerator);
        }
      }
    }
    translateContents(paramClassGenerator, paramMethodGenerator);
    localInstructionList.append(paramMethodGenerator.endElement());
  }
  
  private boolean isHTMLOutput()
  {
    return getStylesheet().getOutputMethod() == 2;
  }
  
  public ElemDesc getElemDesc()
  {
    if (isHTMLOutput()) {
      return ToHTMLStream.getElemDesc(_name);
    }
    return null;
  }
  
  public boolean allAttributesUnique()
  {
    return _allAttributesUnique;
  }
  
  private boolean checkAttributesUnique()
  {
    boolean bool = canProduceAttributeNodes(this, true);
    if (bool) {
      return false;
    }
    if (_attributeElements != null)
    {
      int i = _attributeElements.size();
      HashMap localHashMap = null;
      for (int j = 0; j < i; j++)
      {
        SyntaxTreeNode localSyntaxTreeNode = (SyntaxTreeNode)_attributeElements.get(j);
        if ((localSyntaxTreeNode instanceof UseAttributeSets)) {
          return false;
        }
        if ((localSyntaxTreeNode instanceof XslAttribute))
        {
          Object localObject2;
          if (localHashMap == null)
          {
            localHashMap = new HashMap();
            for (int k = 0; k < j; k++)
            {
              localObject1 = (SyntaxTreeNode)_attributeElements.get(k);
              if ((localObject1 instanceof LiteralAttribute))
              {
                localObject2 = (LiteralAttribute)localObject1;
                localHashMap.put(((LiteralAttribute)localObject2).getName(), localObject2);
              }
            }
          }
          XslAttribute localXslAttribute = (XslAttribute)localSyntaxTreeNode;
          Object localObject1 = localXslAttribute.getName();
          if ((localObject1 instanceof AttributeValueTemplate)) {
            return false;
          }
          if ((localObject1 instanceof SimpleAttributeValue))
          {
            localObject2 = (SimpleAttributeValue)localObject1;
            String str = ((SimpleAttributeValue)localObject2).toString();
            if ((str != null) && (localHashMap.get(str) != null)) {
              return false;
            }
            if (str != null) {
              localHashMap.put(str, localXslAttribute);
            }
          }
        }
      }
    }
    return true;
  }
  
  private boolean canProduceAttributeNodes(SyntaxTreeNode paramSyntaxTreeNode, boolean paramBoolean)
  {
    List localList = paramSyntaxTreeNode.getContents();
    Iterator localIterator1 = localList.iterator();
    while (localIterator1.hasNext())
    {
      SyntaxTreeNode localSyntaxTreeNode1 = (SyntaxTreeNode)localIterator1.next();
      Object localObject;
      if ((localSyntaxTreeNode1 instanceof Text))
      {
        localObject = (Text)localSyntaxTreeNode1;
        if (!((Text)localObject).isIgnore()) {
          return false;
        }
      }
      else
      {
        if (((localSyntaxTreeNode1 instanceof LiteralElement)) || ((localSyntaxTreeNode1 instanceof ValueOf)) || ((localSyntaxTreeNode1 instanceof XslElement)) || ((localSyntaxTreeNode1 instanceof Comment)) || ((localSyntaxTreeNode1 instanceof Number)) || ((localSyntaxTreeNode1 instanceof ProcessingInstruction))) {
          return false;
        }
        if ((localSyntaxTreeNode1 instanceof XslAttribute))
        {
          if (!paramBoolean) {
            return true;
          }
        }
        else
        {
          if (((localSyntaxTreeNode1 instanceof CallTemplate)) || ((localSyntaxTreeNode1 instanceof ApplyTemplates)) || ((localSyntaxTreeNode1 instanceof Copy)) || ((localSyntaxTreeNode1 instanceof CopyOf))) {
            return true;
          }
          if ((((localSyntaxTreeNode1 instanceof If)) || ((localSyntaxTreeNode1 instanceof ForEach))) && (canProduceAttributeNodes(localSyntaxTreeNode1, false))) {
            return true;
          }
          if ((localSyntaxTreeNode1 instanceof Choose))
          {
            localObject = localSyntaxTreeNode1.getContents();
            Iterator localIterator2 = ((List)localObject).iterator();
            while (localIterator2.hasNext())
            {
              SyntaxTreeNode localSyntaxTreeNode2 = (SyntaxTreeNode)localIterator2.next();
              if ((((localSyntaxTreeNode2 instanceof When)) || ((localSyntaxTreeNode2 instanceof Otherwise))) && (canProduceAttributeNodes(localSyntaxTreeNode2, false))) {
                return true;
              }
            }
          }
        }
      }
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\LiteralElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */