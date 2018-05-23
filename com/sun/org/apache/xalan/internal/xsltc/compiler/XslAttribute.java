package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.serializer.ElemDesc;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import java.util.List;

final class XslAttribute
  extends Instruction
{
  private String _prefix;
  private AttributeValue _name;
  private AttributeValueTemplate _namespace = null;
  private boolean _ignore = false;
  private boolean _isLiteral = false;
  
  XslAttribute() {}
  
  public AttributeValue getName()
  {
    return _name;
  }
  
  public void display(int paramInt)
  {
    indent(paramInt);
    Util.println("Attribute " + _name);
    displayContents(paramInt + 4);
  }
  
  public void parseContents(Parser paramParser)
  {
    int i = 0;
    SymbolTable localSymbolTable = paramParser.getSymbolTable();
    String str1 = getAttribute("name");
    String str2 = getAttribute("namespace");
    QName localQName = paramParser.getQName(str1, false);
    String str3 = localQName.getPrefix();
    if (((str3 != null) && (str3.equals("xmlns"))) || (str1.equals("xmlns")))
    {
      reportError(this, paramParser, "ILLEGAL_ATTR_NAME_ERR", str1);
      return;
    }
    _isLiteral = Util.isLiteral(str1);
    if ((_isLiteral) && (!XML11Char.isXML11ValidQName(str1)))
    {
      reportError(this, paramParser, "ILLEGAL_ATTR_NAME_ERR", str1);
      return;
    }
    SyntaxTreeNode localSyntaxTreeNode1 = getParent();
    List localList = localSyntaxTreeNode1.getContents();
    for (int j = 0; j < localSyntaxTreeNode1.elementCount(); j++)
    {
      SyntaxTreeNode localSyntaxTreeNode2 = (SyntaxTreeNode)localList.get(j);
      if (localSyntaxTreeNode2 == this) {
        break;
      }
      if ((!(localSyntaxTreeNode2 instanceof XslAttribute)) && (!(localSyntaxTreeNode2 instanceof UseAttributeSets)) && (!(localSyntaxTreeNode2 instanceof LiteralAttribute)) && (!(localSyntaxTreeNode2 instanceof Text)) && (!(localSyntaxTreeNode2 instanceof If)) && (!(localSyntaxTreeNode2 instanceof Choose)) && (!(localSyntaxTreeNode2 instanceof CopyOf)) && (!(localSyntaxTreeNode2 instanceof VariableBase))) {
        reportWarning(this, paramParser, "STRAY_ATTRIBUTE_ERR", str1);
      }
    }
    if ((str2 != null) && (str2 != ""))
    {
      _prefix = lookupPrefix(str2);
      _namespace = new AttributeValueTemplate(str2, paramParser, this);
    }
    else if ((str3 != null) && (str3 != ""))
    {
      _prefix = str3;
      str2 = lookupNamespace(str3);
      if (str2 != null) {
        _namespace = new AttributeValueTemplate(str2, paramParser, this);
      }
    }
    if (_namespace != null)
    {
      if ((_prefix == null) || (_prefix == ""))
      {
        if (str3 != null)
        {
          _prefix = str3;
        }
        else
        {
          _prefix = localSymbolTable.generateNamespacePrefix();
          i = 1;
        }
      }
      else if ((str3 != null) && (!str3.equals(_prefix))) {
        _prefix = str3;
      }
      str1 = _prefix + ":" + localQName.getLocalPart();
      if (((localSyntaxTreeNode1 instanceof LiteralElement)) && (i == 0)) {
        ((LiteralElement)localSyntaxTreeNode1).registerNamespace(_prefix, str2, localSymbolTable, false);
      }
    }
    if ((localSyntaxTreeNode1 instanceof LiteralElement)) {
      ((LiteralElement)localSyntaxTreeNode1).addAttribute(this);
    }
    _name = AttributeValue.create(this, str1, paramParser);
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
      typeCheckContents(paramSymbolTable);
    }
    return Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if (_ignore) {
      return;
    }
    _ignore = true;
    if (_namespace != null)
    {
      localInstructionList.append(paramMethodGenerator.loadHandler());
      localInstructionList.append(new PUSH(localConstantPoolGen, _prefix));
      _namespace.translate(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(paramMethodGenerator.namespace());
    }
    int i;
    if (!_isLiteral)
    {
      localObject = paramMethodGenerator.addLocalVariable2("nameValue", Util.getJCRefType("Ljava/lang/String;"), null);
      _name.translate(paramClassGenerator, paramMethodGenerator);
      ((LocalVariableGen)localObject).setStart(localInstructionList.append(new ASTORE(((LocalVariableGen)localObject).getIndex())));
      localInstructionList.append(new ALOAD(((LocalVariableGen)localObject).getIndex()));
      i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "checkAttribQName", "(Ljava/lang/String;)V");
      localInstructionList.append(new INVOKESTATIC(i));
      localInstructionList.append(paramMethodGenerator.loadHandler());
      localInstructionList.append(DUP);
      ((LocalVariableGen)localObject).setEnd(localInstructionList.append(new ALOAD(((LocalVariableGen)localObject).getIndex())));
    }
    else
    {
      localInstructionList.append(paramMethodGenerator.loadHandler());
      localInstructionList.append(DUP);
      _name.translate(paramClassGenerator, paramMethodGenerator);
    }
    if ((elementCount() == 1) && ((elementAt(0) instanceof Text)))
    {
      localInstructionList.append(new PUSH(localConstantPoolGen, ((Text)elementAt(0)).getText()));
    }
    else
    {
      localInstructionList.append(paramClassGenerator.loadTranslet());
      localInstructionList.append(new GETFIELD(localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "stringValueHandler", "Lcom/sun/org/apache/xalan/internal/xsltc/runtime/StringValueHandler;")));
      localInstructionList.append(DUP);
      localInstructionList.append(paramMethodGenerator.storeHandler());
      translateContents(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(new INVOKEVIRTUAL(localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.StringValueHandler", "getValue", "()Ljava/lang/String;")));
    }
    Object localObject = getParent();
    if (((localObject instanceof LiteralElement)) && (((LiteralElement)localObject).allAttributesUnique()))
    {
      i = 0;
      ElemDesc localElemDesc = ((LiteralElement)localObject).getElemDesc();
      if ((localElemDesc != null) && ((_name instanceof SimpleAttributeValue)))
      {
        String str = ((SimpleAttributeValue)_name).toString();
        if (localElemDesc.isAttrFlagSet(str, 4)) {
          i |= 0x2;
        } else if (localElemDesc.isAttrFlagSet(str, 2)) {
          i |= 0x4;
        }
      }
      localInstructionList.append(new PUSH(localConstantPoolGen, i));
      localInstructionList.append(paramMethodGenerator.uniqueAttribute());
    }
    else
    {
      localInstructionList.append(paramMethodGenerator.attribute());
    }
    localInstructionList.append(paramMethodGenerator.storeHandler());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\XslAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */