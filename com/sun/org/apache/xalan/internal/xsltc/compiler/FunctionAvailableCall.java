package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Vector;

final class FunctionAvailableCall
  extends FunctionCall
{
  private Expression _arg;
  private String _nameOfFunct = null;
  private String _namespaceOfFunct = null;
  private boolean _isFunctionAvailable = false;
  
  public FunctionAvailableCall(QName paramQName, Vector paramVector)
  {
    super(paramQName, paramVector);
    _arg = ((Expression)paramVector.elementAt(0));
    _type = null;
    if ((_arg instanceof LiteralExpr))
    {
      LiteralExpr localLiteralExpr = (LiteralExpr)_arg;
      _namespaceOfFunct = localLiteralExpr.getNamespace();
      _nameOfFunct = localLiteralExpr.getValue();
      if (!isInternalNamespace()) {
        _isFunctionAvailable = hasMethods();
      }
    }
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    if (_type != null) {
      return _type;
    }
    if ((_arg instanceof LiteralExpr)) {
      return _type = Type.Boolean;
    }
    ErrorMsg localErrorMsg = new ErrorMsg("NEED_LITERAL_ERR", "function-available", this);
    throw new TypeCheckError(localErrorMsg);
  }
  
  public Object evaluateAtCompileTime()
  {
    return getResult() ? Boolean.TRUE : Boolean.FALSE;
  }
  
  private boolean hasMethods()
  {
    LiteralExpr localLiteralExpr = (LiteralExpr)_arg;
    String str = getClassNameFromUri(_namespaceOfFunct);
    Object localObject1 = null;
    int i = _nameOfFunct.indexOf(":");
    Object localObject2;
    if (i > 0)
    {
      localObject2 = _nameOfFunct.substring(i + 1);
      int j = ((String)localObject2).lastIndexOf('.');
      if (j > 0)
      {
        localObject1 = ((String)localObject2).substring(j + 1);
        if ((str != null) && (!str.equals(""))) {
          str = str + "." + ((String)localObject2).substring(0, j);
        } else {
          str = ((String)localObject2).substring(0, j);
        }
      }
      else
      {
        localObject1 = localObject2;
      }
    }
    else
    {
      localObject1 = _nameOfFunct;
    }
    if ((str == null) || (localObject1 == null)) {
      return false;
    }
    if (((String)localObject1).indexOf('-') > 0) {
      localObject1 = replaceDash((String)localObject1);
    }
    try
    {
      localObject2 = ObjectFactory.findProviderClass(str, true);
      if (localObject2 == null) {
        return false;
      }
      Method[] arrayOfMethod = ((Class)localObject2).getMethods();
      for (int k = 0; k < arrayOfMethod.length; k++)
      {
        int m = arrayOfMethod[k].getModifiers();
        if ((Modifier.isPublic(m)) && (Modifier.isStatic(m)) && (arrayOfMethod[k].getName().equals(localObject1))) {
          return true;
        }
      }
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      return false;
    }
    return false;
  }
  
  public boolean getResult()
  {
    if (_nameOfFunct == null) {
      return false;
    }
    if (isInternalNamespace())
    {
      Parser localParser = getParser();
      _isFunctionAvailable = localParser.functionSupported(Util.getLocalName(_nameOfFunct));
    }
    return _isFunctionAvailable;
  }
  
  private boolean isInternalNamespace()
  {
    return (_namespaceOfFunct == null) || (_namespaceOfFunct.equals("")) || (_namespaceOfFunct.equals("http://xml.apache.org/xalan/xsltc"));
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    paramMethodGenerator.getInstructionList().append(new PUSH(localConstantPoolGen, getResult()));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\FunctionAvailableCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */