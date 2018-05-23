package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.IntType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.RealType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Operators;
import java.util.Vector;

final class RelationalExpr
  extends Expression
{
  private int _op;
  private Expression _left;
  private Expression _right;
  
  public RelationalExpr(int paramInt, Expression paramExpression1, Expression paramExpression2)
  {
    _op = paramInt;
    (_left = paramExpression1).setParent(this);
    (_right = paramExpression2).setParent(this);
  }
  
  public void setParser(Parser paramParser)
  {
    super.setParser(paramParser);
    _left.setParser(paramParser);
    _right.setParser(paramParser);
  }
  
  public boolean hasPositionCall()
  {
    if (_left.hasPositionCall()) {
      return true;
    }
    return _right.hasPositionCall();
  }
  
  public boolean hasLastCall()
  {
    return (_left.hasLastCall()) || (_right.hasLastCall());
  }
  
  public boolean hasReferenceArgs()
  {
    return ((_left.getType() instanceof ReferenceType)) || ((_right.getType() instanceof ReferenceType));
  }
  
  public boolean hasNodeArgs()
  {
    return ((_left.getType() instanceof NodeType)) || ((_right.getType() instanceof NodeType));
  }
  
  public boolean hasNodeSetArgs()
  {
    return ((_left.getType() instanceof NodeSetType)) || ((_right.getType() instanceof NodeSetType));
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    Type localType1 = _left.typeCheck(paramSymbolTable);
    Type localType2 = _right.typeCheck(paramSymbolTable);
    if (((localType1 instanceof ResultTreeType)) && ((localType2 instanceof ResultTreeType)))
    {
      _right = new CastExpr(_right, Type.Real);
      _left = new CastExpr(_left, Type.Real);
      return _type = Type.Boolean;
    }
    Type localType3;
    Type localType4;
    if (hasReferenceArgs())
    {
      localObject = null;
      localType3 = null;
      localType4 = null;
      VariableRefBase localVariableRefBase;
      VariableBase localVariableBase;
      if (((localType1 instanceof ReferenceType)) && ((_left instanceof VariableRefBase)))
      {
        localVariableRefBase = (VariableRefBase)_left;
        localVariableBase = localVariableRefBase.getVariable();
        localType3 = localVariableBase.getType();
      }
      if (((localType2 instanceof ReferenceType)) && ((_right instanceof VariableRefBase)))
      {
        localVariableRefBase = (VariableRefBase)_right;
        localVariableBase = localVariableRefBase.getVariable();
        localType4 = localVariableBase.getType();
      }
      if (localType3 == null) {
        localObject = localType4;
      } else if (localType4 == null) {
        localObject = localType3;
      } else {
        localObject = Type.Real;
      }
      if (localObject == null) {
        localObject = Type.Real;
      }
      _right = new CastExpr(_right, (Type)localObject);
      _left = new CastExpr(_left, (Type)localObject);
      return _type = Type.Boolean;
    }
    if (hasNodeSetArgs())
    {
      if ((localType2 instanceof NodeSetType))
      {
        localObject = _right;
        _right = _left;
        _left = ((Expression)localObject);
        _op = (_op == 4 ? 5 : _op == 3 ? 2 : _op == 2 ? 3 : 4);
        localType2 = _right.getType();
      }
      if ((localType2 instanceof NodeType)) {
        _right = new CastExpr(_right, Type.NodeSet);
      }
      if ((localType2 instanceof IntType)) {
        _right = new CastExpr(_right, Type.Real);
      }
      if ((localType2 instanceof ResultTreeType)) {
        _right = new CastExpr(_right, Type.String);
      }
      return _type = Type.Boolean;
    }
    if (hasNodeArgs())
    {
      if ((localType1 instanceof BooleanType))
      {
        _right = new CastExpr(_right, Type.Boolean);
        localType2 = Type.Boolean;
      }
      if ((localType2 instanceof BooleanType))
      {
        _left = new CastExpr(_left, Type.Boolean);
        localType1 = Type.Boolean;
      }
    }
    Object localObject = lookupPrimop(paramSymbolTable, Operators.getOpNames(_op), new MethodType(Type.Void, localType1, localType2));
    if (localObject != null)
    {
      localType3 = (Type)((MethodType)localObject).argsType().elementAt(0);
      if (!localType3.identicalTo(localType1)) {
        _left = new CastExpr(_left, localType3);
      }
      localType4 = (Type)((MethodType)localObject).argsType().elementAt(1);
      if (!localType4.identicalTo(localType2)) {
        _right = new CastExpr(_right, localType3);
      }
      return _type = ((MethodType)localObject).resultType();
    }
    throw new TypeCheckError(this);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    if ((hasNodeSetArgs()) || (hasReferenceArgs()))
    {
      ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
      InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
      _left.translate(paramClassGenerator, paramMethodGenerator);
      _left.startIterator(paramClassGenerator, paramMethodGenerator);
      _right.translate(paramClassGenerator, paramMethodGenerator);
      _right.startIterator(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(new PUSH(localConstantPoolGen, _op));
      localInstructionList.append(paramMethodGenerator.loadDOM());
      int i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "compare", "(" + _left.getType().toSignature() + _right.getType().toSignature() + "I" + "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;" + ")Z");
      localInstructionList.append(new INVOKESTATIC(i));
    }
    else
    {
      translateDesynthesized(paramClassGenerator, paramMethodGenerator);
      synthesize(paramClassGenerator, paramMethodGenerator);
    }
  }
  
  public void translateDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    if ((hasNodeSetArgs()) || (hasReferenceArgs()))
    {
      translate(paramClassGenerator, paramMethodGenerator);
      desynthesize(paramClassGenerator, paramMethodGenerator);
    }
    else
    {
      BranchInstruction localBranchInstruction = null;
      InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
      _left.translate(paramClassGenerator, paramMethodGenerator);
      _right.translate(paramClassGenerator, paramMethodGenerator);
      boolean bool = false;
      Type localType = _left.getType();
      if ((localType instanceof RealType))
      {
        localInstructionList.append(localType.CMP((_op == 3) || (_op == 5)));
        localType = Type.Int;
        bool = true;
      }
      switch (_op)
      {
      case 3: 
        localBranchInstruction = localType.GE(bool);
        break;
      case 2: 
        localBranchInstruction = localType.LE(bool);
        break;
      case 5: 
        localBranchInstruction = localType.GT(bool);
        break;
      case 4: 
        localBranchInstruction = localType.LT(bool);
        break;
      default: 
        ErrorMsg localErrorMsg = new ErrorMsg("ILLEGAL_RELAT_OP_ERR", this);
        getParser().reportError(2, localErrorMsg);
      }
      _falseList.add(localInstructionList.append(localBranchInstruction));
    }
  }
  
  public String toString()
  {
    return Operators.getOpNames(_op) + '(' + _left + ", " + _right + ')';
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\RelationalExpr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */