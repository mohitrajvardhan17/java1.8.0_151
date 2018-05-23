package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.generic.ACONST_NULL;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.DCONST;
import com.sun.org.apache.bcel.internal.generic.ICONST;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.IntType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.RealType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class Variable
  extends VariableBase
{
  Variable() {}
  
  public int getIndex()
  {
    return _local != null ? _local.getIndex() : -1;
  }
  
  public void parseContents(Parser paramParser)
  {
    super.parseContents(paramParser);
    SyntaxTreeNode localSyntaxTreeNode = getParent();
    if ((localSyntaxTreeNode instanceof Stylesheet))
    {
      _isLocal = false;
      Variable localVariable = paramParser.getSymbolTable().lookupVariable(_name);
      if (localVariable != null)
      {
        int i = getImportPrecedence();
        int j = localVariable.getImportPrecedence();
        if (i == j)
        {
          String str = _name.toString();
          reportError(this, paramParser, "VARIABLE_REDEF_ERR", str);
        }
        else
        {
          if (j > i)
          {
            _ignore = true;
            copyReferences(localVariable);
            return;
          }
          localVariable.copyReferences(this);
          localVariable.disable();
        }
      }
      ((Stylesheet)localSyntaxTreeNode).addVariable(this);
      paramParser.getSymbolTable().addVariable(this);
    }
    else
    {
      _isLocal = true;
    }
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    if (_select != null)
    {
      _type = _select.typeCheck(paramSymbolTable);
    }
    else if (hasContents())
    {
      typeCheckContents(paramSymbolTable);
      _type = Type.ResultTree;
    }
    else
    {
      _type = Type.Reference;
    }
    return Type.Void;
  }
  
  public void initialize(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if ((isLocal()) && (!_refs.isEmpty()))
    {
      if (_local == null) {
        _local = paramMethodGenerator.addLocalVariable2(getEscapedName(), _type.toJCType(), null);
      }
      if (((_type instanceof IntType)) || ((_type instanceof NodeType)) || ((_type instanceof BooleanType))) {
        localInstructionList.append(new ICONST(0));
      } else if ((_type instanceof RealType)) {
        localInstructionList.append(new DCONST(0.0D));
      } else {
        localInstructionList.append(new ACONST_NULL());
      }
      _local.setStart(localInstructionList.append(_type.STORE(_local.getIndex())));
    }
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    if (_refs.isEmpty()) {
      _ignore = true;
    }
    if (_ignore) {
      return;
    }
    _ignore = true;
    String str1 = getEscapedName();
    if (isLocal())
    {
      translateValue(paramClassGenerator, paramMethodGenerator);
      int i = _local == null ? 1 : 0;
      if (i != 0) {
        mapRegister(paramMethodGenerator);
      }
      InstructionHandle localInstructionHandle = localInstructionList.append(_type.STORE(_local.getIndex()));
      if (i != 0) {
        _local.setStart(localInstructionHandle);
      }
    }
    else
    {
      String str2 = _type.toSignature();
      if (paramClassGenerator.containsField(str1) == null)
      {
        paramClassGenerator.addField(new Field(1, localConstantPoolGen.addUtf8(str1), localConstantPoolGen.addUtf8(str2), null, localConstantPoolGen.getConstantPool()));
        localInstructionList.append(paramClassGenerator.loadTranslet());
        translateValue(paramClassGenerator, paramMethodGenerator);
        localInstructionList.append(new PUTFIELD(localConstantPoolGen.addFieldref(paramClassGenerator.getClassName(), str1, str2)));
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Variable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */