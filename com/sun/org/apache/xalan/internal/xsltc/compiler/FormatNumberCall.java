package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.RealType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Vector;

final class FormatNumberCall
  extends FunctionCall
{
  private Expression _value = argument(0);
  private Expression _format = argument(1);
  private Expression _name = argumentCount() == 3 ? argument(2) : null;
  private QName _resolvedQName = null;
  
  public FormatNumberCall(QName paramQName, Vector paramVector)
  {
    super(paramQName, paramVector);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    getStylesheet().numberFormattingUsed();
    Type localType1 = _value.typeCheck(paramSymbolTable);
    if (!(localType1 instanceof RealType)) {
      _value = new CastExpr(_value, Type.Real);
    }
    Type localType2 = _format.typeCheck(paramSymbolTable);
    if (!(localType2 instanceof StringType)) {
      _format = new CastExpr(_format, Type.String);
    }
    if (argumentCount() == 3)
    {
      Type localType3 = _name.typeCheck(paramSymbolTable);
      if ((_name instanceof LiteralExpr))
      {
        LiteralExpr localLiteralExpr = (LiteralExpr)_name;
        _resolvedQName = getParser().getQNameIgnoreDefaultNs(localLiteralExpr.getValue());
      }
      else if (!(localType3 instanceof StringType))
      {
        _name = new CastExpr(_name, Type.String);
      }
    }
    return _type = Type.String;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    _value.translate(paramClassGenerator, paramMethodGenerator);
    _format.translate(paramClassGenerator, paramMethodGenerator);
    int i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "formatNumber", "(DLjava/lang/String;Ljava/text/DecimalFormat;)Ljava/lang/String;");
    int j = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "getDecimalFormat", "(Ljava/lang/String;)Ljava/text/DecimalFormat;");
    localInstructionList.append(paramClassGenerator.loadTranslet());
    if (_name == null) {
      localInstructionList.append(new PUSH(localConstantPoolGen, ""));
    } else if (_resolvedQName != null) {
      localInstructionList.append(new PUSH(localConstantPoolGen, _resolvedQName.toString()));
    } else {
      _name.translate(paramClassGenerator, paramMethodGenerator);
    }
    localInstructionList.append(new INVOKEVIRTUAL(j));
    localInstructionList.append(new INVOKESTATIC(i));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\FormatNumberCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */