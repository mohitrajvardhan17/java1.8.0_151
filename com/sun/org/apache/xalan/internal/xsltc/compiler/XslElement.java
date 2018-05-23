package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import java.util.List;

final class XslElement
  extends Instruction
{
  private String _prefix;
  private boolean _ignore = false;
  private boolean _isLiteralName = true;
  private AttributeValueTemplate _name;
  private AttributeValueTemplate _namespace;
  
  XslElement() {}
  
  public void display(int paramInt)
  {
    indent(paramInt);
    Util.println("Element " + _name);
    displayContents(paramInt + 4);
  }
  
  public boolean declaresDefaultNS()
  {
    return false;
  }
  
  public void parseContents(Parser paramParser)
  {
    SymbolTable localSymbolTable = paramParser.getSymbolTable();
    String str1 = getAttribute("name");
    if (str1 == "")
    {
      localObject1 = new ErrorMsg("ILLEGAL_ELEM_NAME_ERR", str1, this);
      paramParser.reportError(4, (ErrorMsg)localObject1);
      parseChildren(paramParser);
      _ignore = true;
      return;
    }
    Object localObject1 = getAttribute("namespace");
    _isLiteralName = Util.isLiteral(str1);
    Object localObject3;
    if (_isLiteralName)
    {
      if (!XML11Char.isXML11ValidQName(str1))
      {
        localObject2 = new ErrorMsg("ILLEGAL_ELEM_NAME_ERR", str1, this);
        paramParser.reportError(4, (ErrorMsg)localObject2);
        parseChildren(paramParser);
        _ignore = true;
        return;
      }
      localObject2 = paramParser.getQNameSafe(str1);
      localObject3 = ((QName)localObject2).getPrefix();
      String str2 = ((QName)localObject2).getLocalPart();
      if (localObject3 == null) {
        localObject3 = "";
      }
      Object localObject4;
      if (!hasAttribute("namespace"))
      {
        localObject1 = lookupNamespace((String)localObject3);
        if (localObject1 == null)
        {
          localObject4 = new ErrorMsg("NAMESPACE_UNDEF_ERR", localObject3, this);
          paramParser.reportError(4, (ErrorMsg)localObject4);
          parseChildren(paramParser);
          _ignore = true;
          return;
        }
        _prefix = ((String)localObject3);
        _namespace = new AttributeValueTemplate((String)localObject1, paramParser, this);
      }
      else
      {
        if (localObject3 == "")
        {
          if (Util.isLiteral((String)localObject1))
          {
            localObject3 = lookupPrefix((String)localObject1);
            if (localObject3 == null) {
              localObject3 = localSymbolTable.generateNamespacePrefix();
            }
          }
          localObject4 = new StringBuffer((String)localObject3);
          if (localObject3 != "") {
            ((StringBuffer)localObject4).append(':');
          }
          str1 = str2;
        }
        _prefix = ((String)localObject3);
        _namespace = new AttributeValueTemplate((String)localObject1, paramParser, this);
      }
    }
    else
    {
      _namespace = (localObject1 == "" ? null : new AttributeValueTemplate((String)localObject1, paramParser, this));
    }
    _name = new AttributeValueTemplate(str1, paramParser, this);
    Object localObject2 = getAttribute("use-attribute-sets");
    if (((String)localObject2).length() > 0)
    {
      if (!Util.isValidQNames((String)localObject2))
      {
        localObject3 = new ErrorMsg("INVALID_QNAME_ERR", localObject2, this);
        paramParser.reportError(3, (ErrorMsg)localObject3);
      }
      setFirstElement(new UseAttributeSets((String)localObject2, paramParser));
    }
    parseChildren(paramParser);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    if (!_ignore)
    {
      _name.typeCheck(paramSymbolTable);
      if (_namespace != null) {
        _namespace.typeCheck(paramSymbolTable);
      }
    }
    typeCheckContents(paramSymbolTable);
    return Type.Void;
  }
  
  public void translateLiteral(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if (!_ignore)
    {
      localInstructionList.append(paramMethodGenerator.loadHandler());
      _name.translate(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(DUP2);
      localInstructionList.append(paramMethodGenerator.startElement());
      if (_namespace != null)
      {
        localInstructionList.append(paramMethodGenerator.loadHandler());
        localInstructionList.append(new PUSH(localConstantPoolGen, _prefix));
        _namespace.translate(paramClassGenerator, paramMethodGenerator);
        localInstructionList.append(paramMethodGenerator.namespace());
      }
    }
    translateContents(paramClassGenerator, paramMethodGenerator);
    if (!_ignore) {
      localInstructionList.append(paramMethodGenerator.endElement());
    }
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    Object localObject = null;
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if (_isLiteralName)
    {
      translateLiteral(paramClassGenerator, paramMethodGenerator);
      return;
    }
    if (!_ignore)
    {
      LocalVariableGen localLocalVariableGen = paramMethodGenerator.addLocalVariable2("nameValue", Util.getJCRefType("Ljava/lang/String;"), null);
      _name.translate(paramClassGenerator, paramMethodGenerator);
      localLocalVariableGen.setStart(localInstructionList.append(new ASTORE(localLocalVariableGen.getIndex())));
      localInstructionList.append(new ALOAD(localLocalVariableGen.getIndex()));
      int i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "checkQName", "(Ljava/lang/String;)V");
      localInstructionList.append(new INVOKESTATIC(i));
      localInstructionList.append(paramMethodGenerator.loadHandler());
      localLocalVariableGen.setEnd(localInstructionList.append(new ALOAD(localLocalVariableGen.getIndex())));
      if (_namespace != null) {
        _namespace.translate(paramClassGenerator, paramMethodGenerator);
      } else {
        localInstructionList.append(ACONST_NULL);
      }
      localInstructionList.append(paramMethodGenerator.loadHandler());
      localInstructionList.append(paramMethodGenerator.loadDOM());
      localInstructionList.append(paramMethodGenerator.loadCurrentNode());
      localInstructionList.append(new INVOKESTATIC(localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "startXslElement", "(Ljava/lang/String;Ljava/lang/String;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;I)Ljava/lang/String;")));
    }
    translateContents(paramClassGenerator, paramMethodGenerator);
    if (!_ignore) {
      localInstructionList.append(paramMethodGenerator.endElement());
    }
  }
  
  public void translateContents(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    int i = elementCount();
    for (int j = 0; j < i; j++)
    {
      SyntaxTreeNode localSyntaxTreeNode = (SyntaxTreeNode)getContents().get(j);
      if ((!_ignore) || (!(localSyntaxTreeNode instanceof XslAttribute))) {
        localSyntaxTreeNode.translate(paramClassGenerator, paramMethodGenerator);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\XslElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */