package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.utils.FeatureManager.Feature;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.IntType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MultiHashtable;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ObjectType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ReferenceType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

class FunctionCall
  extends Expression
{
  private QName _fname;
  private final Vector _arguments;
  private static final Vector EMPTY_ARG_LIST = new Vector(0);
  protected static final String EXT_XSLTC = "http://xml.apache.org/xalan/xsltc";
  protected static final String JAVA_EXT_XSLTC = "http://xml.apache.org/xalan/xsltc/java";
  protected static final String EXT_XALAN = "http://xml.apache.org/xalan";
  protected static final String JAVA_EXT_XALAN = "http://xml.apache.org/xalan/java";
  protected static final String JAVA_EXT_XALAN_OLD = "http://xml.apache.org/xslt/java";
  protected static final String EXSLT_COMMON = "http://exslt.org/common";
  protected static final String EXSLT_MATH = "http://exslt.org/math";
  protected static final String EXSLT_SETS = "http://exslt.org/sets";
  protected static final String EXSLT_DATETIME = "http://exslt.org/dates-and-times";
  protected static final String EXSLT_STRINGS = "http://exslt.org/strings";
  protected static final String XALAN_CLASSPACKAGE_NAMESPACE = "xalan://";
  protected static final int NAMESPACE_FORMAT_JAVA = 0;
  protected static final int NAMESPACE_FORMAT_CLASS = 1;
  protected static final int NAMESPACE_FORMAT_PACKAGE = 2;
  protected static final int NAMESPACE_FORMAT_CLASS_OR_PACKAGE = 3;
  private int _namespace_format = 0;
  Expression _thisArgument = null;
  private String _className;
  private Class _clazz;
  private Method _chosenMethod;
  private Constructor _chosenConstructor;
  private MethodType _chosenMethodType;
  private boolean unresolvedExternal;
  private boolean _isExtConstructor = false;
  private boolean _isStatic = false;
  private static final MultiHashtable<Type, JavaType> _internal2Java = new MultiHashtable();
  private static final Map<Class<?>, Type> JAVA2INTERNAL;
  private static final Map<String, String> EXTENSIONNAMESPACE;
  private static final Map<String, String> EXTENSIONFUNCTION;
  
  public FunctionCall(QName paramQName, Vector paramVector)
  {
    _fname = paramQName;
    _arguments = paramVector;
    _type = null;
  }
  
  public FunctionCall(QName paramQName)
  {
    this(paramQName, EMPTY_ARG_LIST);
  }
  
  public String getName()
  {
    return _fname.toString();
  }
  
  public void setParser(Parser paramParser)
  {
    super.setParser(paramParser);
    if (_arguments != null)
    {
      int i = _arguments.size();
      for (int j = 0; j < i; j++)
      {
        Expression localExpression = (Expression)_arguments.elementAt(j);
        localExpression.setParser(paramParser);
        localExpression.setParent(this);
      }
    }
  }
  
  public String getClassNameFromUri(String paramString)
  {
    String str = (String)EXTENSIONNAMESPACE.get(paramString);
    if (str != null) {
      return str;
    }
    if (paramString.startsWith("http://xml.apache.org/xalan/xsltc/java"))
    {
      i = "http://xml.apache.org/xalan/xsltc/java".length() + 1;
      return paramString.length() > i ? paramString.substring(i) : "";
    }
    if (paramString.startsWith("http://xml.apache.org/xalan/java"))
    {
      i = "http://xml.apache.org/xalan/java".length() + 1;
      return paramString.length() > i ? paramString.substring(i) : "";
    }
    if (paramString.startsWith("http://xml.apache.org/xslt/java"))
    {
      i = "http://xml.apache.org/xslt/java".length() + 1;
      return paramString.length() > i ? paramString.substring(i) : "";
    }
    int i = paramString.lastIndexOf('/');
    return i > 0 ? paramString.substring(i + 1) : paramString;
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    if (_type != null) {
      return _type;
    }
    String str1 = _fname.getNamespace();
    String str2 = _fname.getLocalPart();
    if (isExtension())
    {
      _fname = new QName(null, null, str2);
      return typeCheckStandard(paramSymbolTable);
    }
    if (isStandard()) {
      return typeCheckStandard(paramSymbolTable);
    }
    try
    {
      _className = getClassNameFromUri(str1);
      int i = str2.lastIndexOf('.');
      if (i > 0)
      {
        _isStatic = true;
        if ((_className != null) && (_className.length() > 0))
        {
          _namespace_format = 2;
          _className = (_className + "." + str2.substring(0, i));
        }
        else
        {
          _namespace_format = 0;
          _className = str2.substring(0, i);
        }
        _fname = new QName(str1, null, str2.substring(i + 1));
      }
      else
      {
        if ((_className != null) && (_className.length() > 0)) {
          try
          {
            _clazz = ObjectFactory.findProviderClass(_className, true);
            _namespace_format = 1;
          }
          catch (ClassNotFoundException localClassNotFoundException)
          {
            _namespace_format = 2;
          }
        } else {
          _namespace_format = 0;
        }
        if (str2.indexOf('-') > 0) {
          str2 = replaceDash(str2);
        }
        localObject = (String)EXTENSIONFUNCTION.get(str1 + ":" + str2);
        if (localObject != null)
        {
          _fname = new QName(null, null, (String)localObject);
          return typeCheckStandard(paramSymbolTable);
        }
        _fname = new QName(str1, null, str2);
      }
      return typeCheckExternal(paramSymbolTable);
    }
    catch (TypeCheckError localTypeCheckError)
    {
      Object localObject = localTypeCheckError.getErrorMsg();
      if (localObject == null)
      {
        String str3 = _fname.getLocalPart();
        localObject = new ErrorMsg("METHOD_NOT_FOUND_ERR", str3);
      }
      getParser().reportError(3, (ErrorMsg)localObject);
    }
    return _type = Type.Void;
  }
  
  public Type typeCheckStandard(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    _fname.clearNamespace();
    int i = _arguments.size();
    Vector localVector = typeCheckArgs(paramSymbolTable);
    MethodType localMethodType1 = new MethodType(Type.Void, localVector);
    MethodType localMethodType2 = lookupPrimop(paramSymbolTable, _fname.getLocalPart(), localMethodType1);
    if (localMethodType2 != null)
    {
      for (int j = 0; j < i; j++)
      {
        Type localType = (Type)localMethodType2.argsType().elementAt(j);
        Expression localExpression = (Expression)_arguments.elementAt(j);
        if (!localType.identicalTo(localExpression.getType())) {
          try
          {
            _arguments.setElementAt(new CastExpr(localExpression, localType), j);
          }
          catch (TypeCheckError localTypeCheckError)
          {
            throw new TypeCheckError(this);
          }
        }
      }
      _chosenMethodType = localMethodType2;
      return _type = localMethodType2.resultType();
    }
    throw new TypeCheckError(this);
  }
  
  public Type typeCheckConstructor(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    Vector localVector1 = findConstructors();
    if (localVector1 == null) {
      throw new TypeCheckError("CONSTRUCTOR_NOT_FOUND", _className);
    }
    int i = localVector1.size();
    int j = _arguments.size();
    Vector localVector2 = typeCheckArgs(paramSymbolTable);
    int k = Integer.MAX_VALUE;
    _type = null;
    for (int n = 0; n < i; n++)
    {
      Constructor localConstructor = (Constructor)localVector1.elementAt(n);
      Class[] arrayOfClass = localConstructor.getParameterTypes();
      int i1 = 0;
      for (int m = 0; m < j; m++)
      {
        Class localClass = arrayOfClass[m];
        Type localType = (Type)localVector2.elementAt(m);
        JavaType localJavaType = (JavaType)_internal2Java.maps(localType, new JavaType(localClass, 0));
        if (localJavaType != null)
        {
          i1 += distance;
        }
        else if ((localType instanceof ObjectType))
        {
          ObjectType localObjectType = (ObjectType)localType;
          if (localObjectType.getJavaClass() != localClass) {
            if (localClass.isAssignableFrom(localObjectType.getJavaClass()))
            {
              i1++;
            }
            else
            {
              i1 = Integer.MAX_VALUE;
              break;
            }
          }
        }
        else
        {
          i1 = Integer.MAX_VALUE;
          break;
        }
      }
      if ((m == j) && (i1 < k))
      {
        _chosenConstructor = localConstructor;
        _isExtConstructor = true;
        k = i1;
        _type = (_clazz != null ? Type.newObjectType(_clazz) : Type.newObjectType(_className));
      }
    }
    if (_type != null) {
      return _type;
    }
    throw new TypeCheckError("ARGUMENT_CONVERSION_ERR", getMethodSignature(localVector2));
  }
  
  public Type typeCheckExternal(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    int i = _arguments.size();
    String str = _fname.getLocalPart();
    if (_fname.getLocalPart().equals("new")) {
      return typeCheckConstructor(paramSymbolTable);
    }
    int j = 0;
    if (i == 0) {
      _isStatic = true;
    }
    if (!_isStatic)
    {
      if ((_namespace_format == 0) || (_namespace_format == 2)) {
        j = 1;
      }
      localObject = (Expression)_arguments.elementAt(0);
      Type localType1 = ((Expression)localObject).typeCheck(paramSymbolTable);
      if ((_namespace_format == 1) && ((localType1 instanceof ObjectType)) && (_clazz != null) && (_clazz.isAssignableFrom(((ObjectType)localType1).getJavaClass()))) {
        j = 1;
      }
      if (j != 0)
      {
        _thisArgument = ((Expression)_arguments.elementAt(0));
        _arguments.remove(0);
        i--;
        if ((localType1 instanceof ObjectType)) {
          _className = ((ObjectType)localType1).getJavaClassName();
        } else {
          throw new TypeCheckError("NO_JAVA_FUNCT_THIS_REF", str);
        }
      }
    }
    else if (_className.length() == 0)
    {
      localObject = getParser();
      if (localObject != null) {
        reportWarning(this, (Parser)localObject, "FUNCTION_RESOLVE_ERR", _fname.toString());
      }
      unresolvedExternal = true;
      return _type = Type.Int;
    }
    Vector localVector1 = findMethods();
    if (localVector1 == null) {
      throw new TypeCheckError("METHOD_NOT_FOUND_ERR", _className + "." + str);
    }
    Object localObject = null;
    int k = localVector1.size();
    Vector localVector2 = typeCheckArgs(paramSymbolTable);
    int m = Integer.MAX_VALUE;
    _type = null;
    for (int i1 = 0; i1 < k; i1++)
    {
      Method localMethod = (Method)localVector1.elementAt(i1);
      Class[] arrayOfClass = localMethod.getParameterTypes();
      int i2 = 0;
      for (int n = 0; n < i; n++)
      {
        localObject = arrayOfClass[n];
        Type localType2 = (Type)localVector2.elementAt(n);
        JavaType localJavaType = (JavaType)_internal2Java.maps(localType2, new JavaType((Class)localObject, 0));
        if (localJavaType != null)
        {
          i2 += distance;
        }
        else if ((localType2 instanceof ReferenceType))
        {
          i2++;
        }
        else if ((localType2 instanceof ObjectType))
        {
          ObjectType localObjectType = (ObjectType)localType2;
          if (((Class)localObject).getName().equals(localObjectType.getJavaClassName()))
          {
            i2 += 0;
          }
          else if (((Class)localObject).isAssignableFrom(localObjectType.getJavaClass()))
          {
            i2++;
          }
          else
          {
            i2 = Integer.MAX_VALUE;
            break;
          }
        }
        else
        {
          i2 = Integer.MAX_VALUE;
          break;
        }
      }
      if (n == i)
      {
        localObject = localMethod.getReturnType();
        _type = ((Type)JAVA2INTERNAL.get(localObject));
        if (_type == null) {
          _type = Type.newObjectType((Class)localObject);
        }
        if ((_type != null) && (i2 < m))
        {
          _chosenMethod = localMethod;
          m = i2;
        }
      }
    }
    if ((_chosenMethod != null) && (_thisArgument == null) && (!Modifier.isStatic(_chosenMethod.getModifiers()))) {
      throw new TypeCheckError("NO_JAVA_FUNCT_THIS_REF", getMethodSignature(localVector2));
    }
    if (_type != null)
    {
      if (_type == Type.NodeSet) {
        getXSLTC().setMultiDocument(true);
      }
      return _type;
    }
    throw new TypeCheckError("ARGUMENT_CONVERSION_ERR", getMethodSignature(localVector2));
  }
  
  public Vector typeCheckArgs(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    Vector localVector = new Vector();
    Enumeration localEnumeration = _arguments.elements();
    while (localEnumeration.hasMoreElements())
    {
      Expression localExpression = (Expression)localEnumeration.nextElement();
      localVector.addElement(localExpression.typeCheck(paramSymbolTable));
    }
    return localVector;
  }
  
  protected final Expression argument(int paramInt)
  {
    return (Expression)_arguments.elementAt(paramInt);
  }
  
  protected final Expression argument()
  {
    return argument(0);
  }
  
  protected final int argumentCount()
  {
    return _arguments.size();
  }
  
  protected final void setArgument(int paramInt, Expression paramExpression)
  {
    _arguments.setElementAt(paramExpression, paramInt);
  }
  
  public void translateDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    Type localType = Type.Boolean;
    if (_chosenMethodType != null) {
      localType = _chosenMethodType.resultType();
    }
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    translate(paramClassGenerator, paramMethodGenerator);
    if (((localType instanceof BooleanType)) || ((localType instanceof IntType))) {
      _falseList.add(localInstructionList.append(new IFEQ(null)));
    }
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    int i = argumentCount();
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    boolean bool1 = paramClassGenerator.getParser().getXSLTC().isSecureProcessing();
    boolean bool2 = paramClassGenerator.getParser().getXSLTC().getFeature(FeatureManager.Feature.ORACLE_ENABLE_EXTENSION_FUNCTION);
    Object localObject1;
    String str;
    int j;
    if ((isStandard()) || (isExtension()))
    {
      for (int k = 0; k < i; k++)
      {
        localObject1 = argument(k);
        ((Expression)localObject1).translate(paramClassGenerator, paramMethodGenerator);
        ((Expression)localObject1).startIterator(paramClassGenerator, paramMethodGenerator);
      }
      str = _fname.toString().replace('-', '_') + "F";
      localObject1 = "";
      if (str.equals("sumF"))
      {
        localObject1 = "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;";
        localInstructionList.append(paramMethodGenerator.loadDOM());
      }
      else if ((str.equals("normalize_spaceF")) && (_chosenMethodType.toSignature((String)localObject1).equals("()Ljava/lang/String;")))
      {
        localObject1 = "ILcom/sun/org/apache/xalan/internal/xsltc/DOM;";
        localInstructionList.append(paramMethodGenerator.loadContextNode());
        localInstructionList.append(paramMethodGenerator.loadDOM());
      }
      j = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", str, _chosenMethodType.toSignature((String)localObject1));
      localInstructionList.append(new INVOKESTATIC(j));
    }
    else if (unresolvedExternal)
    {
      j = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "unresolved_externalF", "(Ljava/lang/String;)V");
      localInstructionList.append(new PUSH(localConstantPoolGen, _fname.toString()));
      localInstructionList.append(new INVOKESTATIC(j));
    }
    else
    {
      Object localObject2;
      if (_isExtConstructor)
      {
        if ((bool1) && (!bool2)) {
          translateUnallowedExtension(localConstantPoolGen, localInstructionList);
        }
        str = _chosenConstructor.getDeclaringClass().getName();
        localObject1 = _chosenConstructor.getParameterTypes();
        LocalVariableGen[] arrayOfLocalVariableGen = new LocalVariableGen[i];
        Expression localExpression;
        for (int n = 0; n < i; n++)
        {
          localExpression = argument(n);
          Type localType = localExpression.getType();
          localExpression.translate(paramClassGenerator, paramMethodGenerator);
          localExpression.startIterator(paramClassGenerator, paramMethodGenerator);
          localType.translateTo(paramClassGenerator, paramMethodGenerator, localObject1[n]);
          arrayOfLocalVariableGen[n] = paramMethodGenerator.addLocalVariable("function_call_tmp" + n, localType.toJCType(), null, null);
          arrayOfLocalVariableGen[n].setStart(localInstructionList.append(localType.STORE(arrayOfLocalVariableGen[n].getIndex())));
        }
        localInstructionList.append(new NEW(localConstantPoolGen.addClass(_className)));
        localInstructionList.append(InstructionConstants.DUP);
        for (n = 0; n < i; n++)
        {
          localExpression = argument(n);
          arrayOfLocalVariableGen[n].setEnd(localInstructionList.append(localExpression.getType().LOAD(arrayOfLocalVariableGen[n].getIndex())));
        }
        localObject2 = new StringBuffer();
        ((StringBuffer)localObject2).append('(');
        for (int i2 = 0; i2 < localObject1.length; i2++) {
          ((StringBuffer)localObject2).append(getSignature(localObject1[i2]));
        }
        ((StringBuffer)localObject2).append(')');
        ((StringBuffer)localObject2).append("V");
        j = localConstantPoolGen.addMethodref(str, "<init>", ((StringBuffer)localObject2).toString());
        localInstructionList.append(new INVOKESPECIAL(j));
        Type.Object.translateFrom(paramClassGenerator, paramMethodGenerator, _chosenConstructor.getDeclaringClass());
      }
      else
      {
        if ((bool1) && (!bool2)) {
          translateUnallowedExtension(localConstantPoolGen, localInstructionList);
        }
        str = _chosenMethod.getDeclaringClass().getName();
        localObject1 = _chosenMethod.getParameterTypes();
        if (_thisArgument != null) {
          _thisArgument.translate(paramClassGenerator, paramMethodGenerator);
        }
        for (int m = 0; m < i; m++)
        {
          localObject2 = argument(m);
          ((Expression)localObject2).translate(paramClassGenerator, paramMethodGenerator);
          ((Expression)localObject2).startIterator(paramClassGenerator, paramMethodGenerator);
          ((Expression)localObject2).getType().translateTo(paramClassGenerator, paramMethodGenerator, localObject1[m]);
        }
        StringBuffer localStringBuffer = new StringBuffer();
        localStringBuffer.append('(');
        for (int i1 = 0; i1 < localObject1.length; i1++) {
          localStringBuffer.append(getSignature(localObject1[i1]));
        }
        localStringBuffer.append(')');
        localStringBuffer.append(getSignature(_chosenMethod.getReturnType()));
        if ((_thisArgument != null) && (_clazz.isInterface()))
        {
          j = localConstantPoolGen.addInterfaceMethodref(str, _fname.getLocalPart(), localStringBuffer.toString());
          localInstructionList.append(new INVOKEINTERFACE(j, i + 1));
        }
        else
        {
          j = localConstantPoolGen.addMethodref(str, _fname.getLocalPart(), localStringBuffer.toString());
          localInstructionList.append(_thisArgument != null ? new INVOKEVIRTUAL(j) : new INVOKESTATIC(j));
        }
        _type.translateFrom(paramClassGenerator, paramMethodGenerator, _chosenMethod.getReturnType());
      }
    }
  }
  
  public String toString()
  {
    return "funcall(" + _fname + ", " + _arguments + ')';
  }
  
  public boolean isStandard()
  {
    String str = _fname.getNamespace();
    return (str == null) || (str.equals(""));
  }
  
  public boolean isExtension()
  {
    String str = _fname.getNamespace();
    return (str != null) && (str.equals("http://xml.apache.org/xalan/xsltc"));
  }
  
  private Vector findMethods()
  {
    Vector localVector = null;
    String str1 = _fname.getNamespace();
    if ((_className != null) && (_className.length() > 0))
    {
      int i = _arguments.size();
      try
      {
        if (_clazz == null)
        {
          boolean bool1 = getXSLTC().isSecureProcessing();
          boolean bool2 = getXSLTC().getFeature(FeatureManager.Feature.ORACLE_ENABLE_EXTENSION_FUNCTION);
          if ((str1 != null) && (bool1) && (bool2) && ((str1.startsWith("http://xml.apache.org/xalan/java")) || (str1.startsWith("http://xml.apache.org/xalan/xsltc/java")) || (str1.startsWith("http://xml.apache.org/xslt/java")) || (str1.startsWith("xalan://")))) {
            _clazz = getXSLTC().loadExternalFunction(_className);
          } else {
            _clazz = ObjectFactory.findProviderClass(_className, true);
          }
          if (_clazz == null)
          {
            ErrorMsg localErrorMsg = new ErrorMsg("CLASS_NOT_FOUND_ERR", _className);
            getParser().reportError(3, localErrorMsg);
          }
        }
        String str2 = _fname.getLocalPart();
        localObject = _clazz.getMethods();
        for (int j = 0; j < localObject.length; j++)
        {
          int k = localObject[j].getModifiers();
          if ((Modifier.isPublic(k)) && (localObject[j].getName().equals(str2)) && (localObject[j].getParameterTypes().length == i))
          {
            if (localVector == null) {
              localVector = new Vector();
            }
            localVector.addElement(localObject[j]);
          }
        }
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        Object localObject = new ErrorMsg("CLASS_NOT_FOUND_ERR", _className);
        getParser().reportError(3, (ErrorMsg)localObject);
      }
    }
    return localVector;
  }
  
  private Vector findConstructors()
  {
    Vector localVector = null;
    String str = _fname.getNamespace();
    int i = _arguments.size();
    try
    {
      if (_clazz == null)
      {
        _clazz = ObjectFactory.findProviderClass(_className, true);
        if (_clazz == null)
        {
          localObject = new ErrorMsg("CLASS_NOT_FOUND_ERR", _className);
          getParser().reportError(3, (ErrorMsg)localObject);
        }
      }
      Object localObject = _clazz.getConstructors();
      for (int j = 0; j < localObject.length; j++)
      {
        int k = localObject[j].getModifiers();
        if ((Modifier.isPublic(k)) && (localObject[j].getParameterTypes().length == i))
        {
          if (localVector == null) {
            localVector = new Vector();
          }
          localVector.addElement(localObject[j]);
        }
      }
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      ErrorMsg localErrorMsg = new ErrorMsg("CLASS_NOT_FOUND_ERR", _className);
      getParser().reportError(3, localErrorMsg);
    }
    return localVector;
  }
  
  static final String getSignature(Class paramClass)
  {
    Object localObject1;
    Object localObject2;
    if (paramClass.isArray())
    {
      localObject1 = new StringBuffer();
      for (localObject2 = paramClass; ((Class)localObject2).isArray(); localObject2 = ((Class)localObject2).getComponentType()) {
        ((StringBuffer)localObject1).append("[");
      }
      ((StringBuffer)localObject1).append(getSignature((Class)localObject2));
      return ((StringBuffer)localObject1).toString();
    }
    if (paramClass.isPrimitive())
    {
      if (paramClass == Integer.TYPE) {
        return "I";
      }
      if (paramClass == Byte.TYPE) {
        return "B";
      }
      if (paramClass == Long.TYPE) {
        return "J";
      }
      if (paramClass == Float.TYPE) {
        return "F";
      }
      if (paramClass == Double.TYPE) {
        return "D";
      }
      if (paramClass == Short.TYPE) {
        return "S";
      }
      if (paramClass == Character.TYPE) {
        return "C";
      }
      if (paramClass == Boolean.TYPE) {
        return "Z";
      }
      if (paramClass == Void.TYPE) {
        return "V";
      }
      localObject1 = paramClass.toString();
      localObject2 = new ErrorMsg("UNKNOWN_SIG_TYPE_ERR", localObject1);
      throw new Error(((ErrorMsg)localObject2).toString());
    }
    return "L" + paramClass.getName().replace('.', '/') + ';';
  }
  
  static final String getSignature(Method paramMethod)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append('(');
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    for (int i = 0; i < arrayOfClass.length; i++) {
      localStringBuffer.append(getSignature(arrayOfClass[i]));
    }
    return ')' + getSignature(paramMethod.getReturnType());
  }
  
  static final String getSignature(Constructor paramConstructor)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append('(');
    Class[] arrayOfClass = paramConstructor.getParameterTypes();
    for (int i = 0; i < arrayOfClass.length; i++) {
      localStringBuffer.append(getSignature(arrayOfClass[i]));
    }
    return ")V";
  }
  
  private String getMethodSignature(Vector paramVector)
  {
    StringBuffer localStringBuffer = new StringBuffer(_className);
    localStringBuffer.append('.').append(_fname.getLocalPart()).append('(');
    int i = paramVector.size();
    for (int j = 0; j < i; j++)
    {
      Type localType = (Type)paramVector.elementAt(j);
      localStringBuffer.append(localType.toString());
      if (j < i - 1) {
        localStringBuffer.append(", ");
      }
    }
    localStringBuffer.append(')');
    return localStringBuffer.toString();
  }
  
  protected static String replaceDash(String paramString)
  {
    int i = 45;
    StringBuilder localStringBuilder = new StringBuilder("");
    for (int j = 0; j < paramString.length(); j++) {
      if ((j > 0) && (paramString.charAt(j - 1) == i)) {
        localStringBuilder.append(Character.toUpperCase(paramString.charAt(j)));
      } else if (paramString.charAt(j) != i) {
        localStringBuilder.append(paramString.charAt(j));
      }
    }
    return localStringBuilder.toString();
  }
  
  private void translateUnallowedExtension(ConstantPoolGen paramConstantPoolGen, InstructionList paramInstructionList)
  {
    int i = paramConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "unallowed_extension_functionF", "(Ljava/lang/String;)V");
    paramInstructionList.append(new PUSH(paramConstantPoolGen, _fname.toString()));
    paramInstructionList.append(new INVOKESTATIC(i));
  }
  
  static
  {
    Class localClass1;
    Class localClass2;
    try
    {
      localClass1 = Class.forName("org.w3c.dom.Node");
      localClass2 = Class.forName("org.w3c.dom.NodeList");
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      localObject = new ErrorMsg("CLASS_NOT_FOUND_ERR", "org.w3c.dom.Node or NodeList");
      throw new ExceptionInInitializerError(((ErrorMsg)localObject).toString());
    }
    _internal2Java.put(Type.Boolean, new JavaType(Boolean.TYPE, 0));
    _internal2Java.put(Type.Boolean, new JavaType(Boolean.class, 1));
    _internal2Java.put(Type.Boolean, new JavaType(Object.class, 2));
    _internal2Java.put(Type.Real, new JavaType(Double.TYPE, 0));
    _internal2Java.put(Type.Real, new JavaType(Double.class, 1));
    _internal2Java.put(Type.Real, new JavaType(Float.TYPE, 2));
    _internal2Java.put(Type.Real, new JavaType(Long.TYPE, 3));
    _internal2Java.put(Type.Real, new JavaType(Integer.TYPE, 4));
    _internal2Java.put(Type.Real, new JavaType(Short.TYPE, 5));
    _internal2Java.put(Type.Real, new JavaType(Byte.TYPE, 6));
    _internal2Java.put(Type.Real, new JavaType(Character.TYPE, 7));
    _internal2Java.put(Type.Real, new JavaType(Object.class, 8));
    _internal2Java.put(Type.Int, new JavaType(Double.TYPE, 0));
    _internal2Java.put(Type.Int, new JavaType(Double.class, 1));
    _internal2Java.put(Type.Int, new JavaType(Float.TYPE, 2));
    _internal2Java.put(Type.Int, new JavaType(Long.TYPE, 3));
    _internal2Java.put(Type.Int, new JavaType(Integer.TYPE, 4));
    _internal2Java.put(Type.Int, new JavaType(Short.TYPE, 5));
    _internal2Java.put(Type.Int, new JavaType(Byte.TYPE, 6));
    _internal2Java.put(Type.Int, new JavaType(Character.TYPE, 7));
    _internal2Java.put(Type.Int, new JavaType(Object.class, 8));
    _internal2Java.put(Type.String, new JavaType(String.class, 0));
    _internal2Java.put(Type.String, new JavaType(Object.class, 1));
    _internal2Java.put(Type.NodeSet, new JavaType(localClass2, 0));
    _internal2Java.put(Type.NodeSet, new JavaType(localClass1, 1));
    _internal2Java.put(Type.NodeSet, new JavaType(Object.class, 2));
    _internal2Java.put(Type.NodeSet, new JavaType(String.class, 3));
    _internal2Java.put(Type.Node, new JavaType(localClass2, 0));
    _internal2Java.put(Type.Node, new JavaType(localClass1, 1));
    _internal2Java.put(Type.Node, new JavaType(Object.class, 2));
    _internal2Java.put(Type.Node, new JavaType(String.class, 3));
    _internal2Java.put(Type.ResultTree, new JavaType(localClass2, 0));
    _internal2Java.put(Type.ResultTree, new JavaType(localClass1, 1));
    _internal2Java.put(Type.ResultTree, new JavaType(Object.class, 2));
    _internal2Java.put(Type.ResultTree, new JavaType(String.class, 3));
    _internal2Java.put(Type.Reference, new JavaType(Object.class, 0));
    _internal2Java.makeUnmodifiable();
    HashMap localHashMap1 = new HashMap();
    Object localObject = new HashMap();
    HashMap localHashMap2 = new HashMap();
    localHashMap1.put(Boolean.TYPE, Type.Boolean);
    localHashMap1.put(Void.TYPE, Type.Void);
    localHashMap1.put(Character.TYPE, Type.Real);
    localHashMap1.put(Byte.TYPE, Type.Real);
    localHashMap1.put(Short.TYPE, Type.Real);
    localHashMap1.put(Integer.TYPE, Type.Real);
    localHashMap1.put(Long.TYPE, Type.Real);
    localHashMap1.put(Float.TYPE, Type.Real);
    localHashMap1.put(Double.TYPE, Type.Real);
    localHashMap1.put(String.class, Type.String);
    localHashMap1.put(Object.class, Type.Reference);
    localHashMap1.put(localClass2, Type.NodeSet);
    localHashMap1.put(localClass1, Type.NodeSet);
    ((Map)localObject).put("http://xml.apache.org/xalan", "com.sun.org.apache.xalan.internal.lib.Extensions");
    ((Map)localObject).put("http://exslt.org/common", "com.sun.org.apache.xalan.internal.lib.ExsltCommon");
    ((Map)localObject).put("http://exslt.org/math", "com.sun.org.apache.xalan.internal.lib.ExsltMath");
    ((Map)localObject).put("http://exslt.org/sets", "com.sun.org.apache.xalan.internal.lib.ExsltSets");
    ((Map)localObject).put("http://exslt.org/dates-and-times", "com.sun.org.apache.xalan.internal.lib.ExsltDatetime");
    ((Map)localObject).put("http://exslt.org/strings", "com.sun.org.apache.xalan.internal.lib.ExsltStrings");
    localHashMap2.put("http://exslt.org/common:nodeSet", "nodeset");
    localHashMap2.put("http://exslt.org/common:objectType", "objectType");
    localHashMap2.put("http://xml.apache.org/xalan:nodeset", "nodeset");
    JAVA2INTERNAL = Collections.unmodifiableMap(localHashMap1);
    EXTENSIONNAMESPACE = Collections.unmodifiableMap((Map)localObject);
    EXTENSIONFUNCTION = Collections.unmodifiableMap(localHashMap2);
  }
  
  static class JavaType
  {
    public Class<?> type;
    public int distance;
    
    public JavaType(Class paramClass, int paramInt)
    {
      type = paramClass;
      distance = paramInt;
    }
    
    public int hashCode()
    {
      return Objects.hashCode(type);
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == null) {
        return false;
      }
      if (paramObject.getClass().isAssignableFrom(JavaType.class)) {
        return type.equals(type);
      }
      return paramObject.equals(type);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\FunctionCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */