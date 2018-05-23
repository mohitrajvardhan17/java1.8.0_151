package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPNE;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.SIPUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MultiHashtable;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class CastExpr
  extends Expression
{
  private final Expression _left;
  private static final MultiHashtable<Type, Type> InternalTypeMap = new MultiHashtable();
  private boolean _typeTest = false;
  
  public CastExpr(Expression paramExpression, Type paramType)
    throws TypeCheckError
  {
    _left = paramExpression;
    _type = paramType;
    if (((_left instanceof Step)) && (_type == Type.Boolean))
    {
      Step localStep = (Step)_left;
      if ((localStep.getAxis() == 13) && (localStep.getNodeType() != -1)) {
        _typeTest = true;
      }
    }
    setParser(paramExpression.getParser());
    setParent(paramExpression.getParent());
    paramExpression.setParent(this);
    typeCheck(paramExpression.getParser().getSymbolTable());
  }
  
  public Expression getExpr()
  {
    return _left;
  }
  
  public boolean hasPositionCall()
  {
    return _left.hasPositionCall();
  }
  
  public boolean hasLastCall()
  {
    return _left.hasLastCall();
  }
  
  public String toString()
  {
    return "cast(" + _left + ", " + _type + ")";
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    Type localType = _left.getType();
    if (localType == null) {
      localType = _left.typeCheck(paramSymbolTable);
    }
    if ((localType instanceof NodeType)) {
      localType = Type.Node;
    } else if ((localType instanceof ResultTreeType)) {
      localType = Type.ResultTree;
    }
    if (InternalTypeMap.maps(localType, _type) != null) {
      return _type;
    }
    throw new TypeCheckError(new ErrorMsg("DATA_CONVERSION_ERR", localType.toString(), _type.toString()));
  }
  
  public void translateDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    Type localType = _left.getType();
    if (_typeTest)
    {
      ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
      InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
      int i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getExpandedTypeID", "(I)I");
      localInstructionList.append(new SIPUSH((short)((Step)_left).getNodeType()));
      localInstructionList.append(paramMethodGenerator.loadDOM());
      localInstructionList.append(paramMethodGenerator.loadContextNode());
      localInstructionList.append(new INVOKEINTERFACE(i, 2));
      _falseList.add(localInstructionList.append(new IF_ICMPNE(null)));
    }
    else
    {
      _left.translate(paramClassGenerator, paramMethodGenerator);
      if (_type != localType)
      {
        _left.startIterator(paramClassGenerator, paramMethodGenerator);
        if ((_type instanceof BooleanType))
        {
          FlowList localFlowList = localType.translateToDesynthesized(paramClassGenerator, paramMethodGenerator, _type);
          if (localFlowList != null) {
            _falseList.append(localFlowList);
          }
        }
        else
        {
          localType.translateTo(paramClassGenerator, paramMethodGenerator, _type);
        }
      }
    }
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    Type localType = _left.getType();
    _left.translate(paramClassGenerator, paramMethodGenerator);
    if (!_type.identicalTo(localType))
    {
      _left.startIterator(paramClassGenerator, paramMethodGenerator);
      localType.translateTo(paramClassGenerator, paramMethodGenerator, _type);
    }
  }
  
  static
  {
    InternalTypeMap.put(Type.Boolean, Type.Boolean);
    InternalTypeMap.put(Type.Boolean, Type.Real);
    InternalTypeMap.put(Type.Boolean, Type.String);
    InternalTypeMap.put(Type.Boolean, Type.Reference);
    InternalTypeMap.put(Type.Boolean, Type.Object);
    InternalTypeMap.put(Type.Real, Type.Real);
    InternalTypeMap.put(Type.Real, Type.Int);
    InternalTypeMap.put(Type.Real, Type.Boolean);
    InternalTypeMap.put(Type.Real, Type.String);
    InternalTypeMap.put(Type.Real, Type.Reference);
    InternalTypeMap.put(Type.Real, Type.Object);
    InternalTypeMap.put(Type.Int, Type.Int);
    InternalTypeMap.put(Type.Int, Type.Real);
    InternalTypeMap.put(Type.Int, Type.Boolean);
    InternalTypeMap.put(Type.Int, Type.String);
    InternalTypeMap.put(Type.Int, Type.Reference);
    InternalTypeMap.put(Type.Int, Type.Object);
    InternalTypeMap.put(Type.String, Type.String);
    InternalTypeMap.put(Type.String, Type.Boolean);
    InternalTypeMap.put(Type.String, Type.Real);
    InternalTypeMap.put(Type.String, Type.Reference);
    InternalTypeMap.put(Type.String, Type.Object);
    InternalTypeMap.put(Type.NodeSet, Type.NodeSet);
    InternalTypeMap.put(Type.NodeSet, Type.Boolean);
    InternalTypeMap.put(Type.NodeSet, Type.Real);
    InternalTypeMap.put(Type.NodeSet, Type.String);
    InternalTypeMap.put(Type.NodeSet, Type.Node);
    InternalTypeMap.put(Type.NodeSet, Type.Reference);
    InternalTypeMap.put(Type.NodeSet, Type.Object);
    InternalTypeMap.put(Type.Node, Type.Node);
    InternalTypeMap.put(Type.Node, Type.Boolean);
    InternalTypeMap.put(Type.Node, Type.Real);
    InternalTypeMap.put(Type.Node, Type.String);
    InternalTypeMap.put(Type.Node, Type.NodeSet);
    InternalTypeMap.put(Type.Node, Type.Reference);
    InternalTypeMap.put(Type.Node, Type.Object);
    InternalTypeMap.put(Type.ResultTree, Type.ResultTree);
    InternalTypeMap.put(Type.ResultTree, Type.Boolean);
    InternalTypeMap.put(Type.ResultTree, Type.Real);
    InternalTypeMap.put(Type.ResultTree, Type.String);
    InternalTypeMap.put(Type.ResultTree, Type.NodeSet);
    InternalTypeMap.put(Type.ResultTree, Type.Reference);
    InternalTypeMap.put(Type.ResultTree, Type.Object);
    InternalTypeMap.put(Type.Reference, Type.Reference);
    InternalTypeMap.put(Type.Reference, Type.Boolean);
    InternalTypeMap.put(Type.Reference, Type.Int);
    InternalTypeMap.put(Type.Reference, Type.Real);
    InternalTypeMap.put(Type.Reference, Type.String);
    InternalTypeMap.put(Type.Reference, Type.Node);
    InternalTypeMap.put(Type.Reference, Type.NodeSet);
    InternalTypeMap.put(Type.Reference, Type.ResultTree);
    InternalTypeMap.put(Type.Reference, Type.Object);
    InternalTypeMap.put(Type.Object, Type.String);
    InternalTypeMap.put(Type.Void, Type.String);
    InternalTypeMap.makeUnmodifiable();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\CastExpr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */