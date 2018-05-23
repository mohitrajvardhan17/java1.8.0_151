package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class UnresolvedRef
  extends VariableRefBase
{
  private QName _variableName = null;
  private VariableRefBase _ref = null;
  
  public UnresolvedRef(QName paramQName)
  {
    _variableName = paramQName;
  }
  
  public QName getName()
  {
    return _variableName;
  }
  
  private ErrorMsg reportError()
  {
    ErrorMsg localErrorMsg = new ErrorMsg("VARIABLE_UNDEF_ERR", _variableName, this);
    getParser().reportError(3, localErrorMsg);
    return localErrorMsg;
  }
  
  private VariableRefBase resolve(Parser paramParser, SymbolTable paramSymbolTable)
  {
    VariableBase localVariableBase = paramParser.lookupVariable(_variableName);
    if (localVariableBase == null) {
      localVariableBase = (VariableBase)paramSymbolTable.lookupName(_variableName);
    }
    if (localVariableBase == null)
    {
      reportError();
      return null;
    }
    _variable = localVariableBase;
    addParentDependency();
    if ((localVariableBase instanceof Variable)) {
      return new VariableRef((Variable)localVariableBase);
    }
    if ((localVariableBase instanceof Param)) {
      return new ParameterRef((Param)localVariableBase);
    }
    return null;
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    if (_ref != null)
    {
      String str = _variableName.toString();
      ErrorMsg localErrorMsg = new ErrorMsg("CIRCULAR_VARIABLE_ERR", str, this);
    }
    if ((_ref = resolve(getParser(), paramSymbolTable)) != null) {
      return _type = _ref.typeCheck(paramSymbolTable);
    }
    throw new TypeCheckError(reportError());
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    if (_ref != null) {
      _ref.translate(paramClassGenerator, paramMethodGenerator);
    } else {
      reportError();
    }
  }
  
  public String toString()
  {
    return "unresolved-ref()";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\UnresolvedRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */