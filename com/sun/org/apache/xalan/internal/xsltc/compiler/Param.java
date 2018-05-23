package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.IFNONNULL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ObjectType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import java.io.PrintStream;
import java.util.Vector;

final class Param
  extends VariableBase
{
  private boolean _isInSimpleNamedTemplate = false;
  
  Param() {}
  
  public String toString()
  {
    return "param(" + _name + ")";
  }
  
  public Instruction setLoadInstruction(Instruction paramInstruction)
  {
    Instruction localInstruction = _loadInstruction;
    _loadInstruction = paramInstruction;
    return localInstruction;
  }
  
  public Instruction setStoreInstruction(Instruction paramInstruction)
  {
    Instruction localInstruction = _storeInstruction;
    _storeInstruction = paramInstruction;
    return localInstruction;
  }
  
  public void display(int paramInt)
  {
    indent(paramInt);
    System.out.println("param " + _name);
    if (_select != null)
    {
      indent(paramInt + 4);
      System.out.println("select " + _select.toString());
    }
    displayContents(paramInt + 4);
  }
  
  public void parseContents(Parser paramParser)
  {
    super.parseContents(paramParser);
    SyntaxTreeNode localSyntaxTreeNode = getParent();
    Object localObject;
    if ((localSyntaxTreeNode instanceof Stylesheet))
    {
      _isLocal = false;
      localObject = paramParser.getSymbolTable().lookupParam(_name);
      if (localObject != null)
      {
        int i = getImportPrecedence();
        int j = ((Param)localObject).getImportPrecedence();
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
            copyReferences((VariableBase)localObject);
            return;
          }
          ((Param)localObject).copyReferences(this);
          ((Param)localObject).disable();
        }
      }
      ((Stylesheet)localSyntaxTreeNode).addParam(this);
      paramParser.getSymbolTable().addParam(this);
    }
    else if ((localSyntaxTreeNode instanceof Template))
    {
      localObject = (Template)localSyntaxTreeNode;
      _isLocal = true;
      ((Template)localObject).addParameter(this);
      if (((Template)localObject).isSimpleNamedTemplate()) {
        _isInSimpleNamedTemplate = true;
      }
    }
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    if (_select != null)
    {
      _type = _select.typeCheck(paramSymbolTable);
      if ((!(_type instanceof ReferenceType)) && (!(_type instanceof ObjectType))) {
        _select = new CastExpr(_select, Type.Reference);
      }
    }
    else if (hasContents())
    {
      typeCheckContents(paramSymbolTable);
    }
    _type = Type.Reference;
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
    String str1 = BasisLibrary.mapQNameToJavaName(_name.toString());
    String str2 = _type.toSignature();
    String str3 = _type.getClassName();
    if (isLocal())
    {
      if (_isInSimpleNamedTemplate)
      {
        localInstructionList.append(loadInstruction());
        BranchHandle localBranchHandle = localInstructionList.append(new IFNONNULL(null));
        translateValue(paramClassGenerator, paramMethodGenerator);
        localInstructionList.append(storeInstruction());
        localBranchHandle.setTarget(localInstructionList.append(NOP));
        return;
      }
      localInstructionList.append(paramClassGenerator.loadTranslet());
      localInstructionList.append(new PUSH(localConstantPoolGen, str1));
      translateValue(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(new PUSH(localConstantPoolGen, true));
      localInstructionList.append(new INVOKEVIRTUAL(localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "addParameter", "(Ljava/lang/String;Ljava/lang/Object;Z)Ljava/lang/Object;")));
      if (str3 != "") {
        localInstructionList.append(new CHECKCAST(localConstantPoolGen.addClass(str3)));
      }
      _type.translateUnBox(paramClassGenerator, paramMethodGenerator);
      if (_refs.isEmpty())
      {
        localInstructionList.append(_type.POP());
        _local = null;
      }
      else
      {
        _local = paramMethodGenerator.addLocalVariable2(str1, _type.toJCType(), localInstructionList.getEnd());
        localInstructionList.append(_type.STORE(_local.getIndex()));
      }
    }
    else if (paramClassGenerator.containsField(str1) == null)
    {
      paramClassGenerator.addField(new Field(1, localConstantPoolGen.addUtf8(str1), localConstantPoolGen.addUtf8(str2), null, localConstantPoolGen.getConstantPool()));
      localInstructionList.append(paramClassGenerator.loadTranslet());
      localInstructionList.append(DUP);
      localInstructionList.append(new PUSH(localConstantPoolGen, str1));
      translateValue(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(new PUSH(localConstantPoolGen, true));
      localInstructionList.append(new INVOKEVIRTUAL(localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "addParameter", "(Ljava/lang/String;Ljava/lang/Object;Z)Ljava/lang/Object;")));
      _type.translateUnBox(paramClassGenerator, paramMethodGenerator);
      if (str3 != "") {
        localInstructionList.append(new CHECKCAST(localConstantPoolGen.addClass(str3)));
      }
      localInstructionList.append(new PUTFIELD(localConstantPoolGen.addFieldref(paramClassGenerator.getClassName(), str1, str2)));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Param.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */