package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFNONNULL;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MatchGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeCounterGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.RealType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.ArrayList;
import org.xml.sax.helpers.AttributesImpl;

final class Number
  extends Instruction
  implements Closure
{
  private static final int LEVEL_SINGLE = 0;
  private static final int LEVEL_MULTIPLE = 1;
  private static final int LEVEL_ANY = 2;
  private static final String[] ClassNames = { "com.sun.org.apache.xalan.internal.xsltc.dom.SingleNodeCounter", "com.sun.org.apache.xalan.internal.xsltc.dom.MultipleNodeCounter", "com.sun.org.apache.xalan.internal.xsltc.dom.AnyNodeCounter" };
  private static final String[] FieldNames = { "___single_node_counter", "___multiple_node_counter", "___any_node_counter" };
  private Pattern _from = null;
  private Pattern _count = null;
  private Expression _value = null;
  private AttributeValueTemplate _lang = null;
  private AttributeValueTemplate _format = null;
  private AttributeValueTemplate _letterValue = null;
  private AttributeValueTemplate _groupingSeparator = null;
  private AttributeValueTemplate _groupingSize = null;
  private int _level = 0;
  private boolean _formatNeeded = false;
  private String _className = null;
  private ArrayList _closureVars = null;
  
  Number() {}
  
  public boolean inInnerClass()
  {
    return _className != null;
  }
  
  public Closure getParentClosure()
  {
    return null;
  }
  
  public String getInnerClassName()
  {
    return _className;
  }
  
  public void addVariable(VariableRefBase paramVariableRefBase)
  {
    if (_closureVars == null) {
      _closureVars = new ArrayList();
    }
    if (!_closureVars.contains(paramVariableRefBase)) {
      _closureVars.add(paramVariableRefBase);
    }
  }
  
  public void parseContents(Parser paramParser)
  {
    int i = _attributes.getLength();
    for (int j = 0; j < i; j++)
    {
      String str1 = _attributes.getQName(j);
      String str2 = _attributes.getValue(j);
      if (str1.equals("value"))
      {
        _value = paramParser.parseExpression(this, str1, null);
      }
      else if (str1.equals("count"))
      {
        _count = paramParser.parsePattern(this, str1, null);
      }
      else if (str1.equals("from"))
      {
        _from = paramParser.parsePattern(this, str1, null);
      }
      else if (str1.equals("level"))
      {
        if (str2.equals("single")) {
          _level = 0;
        } else if (str2.equals("multiple")) {
          _level = 1;
        } else if (str2.equals("any")) {
          _level = 2;
        }
      }
      else if (str1.equals("format"))
      {
        _format = new AttributeValueTemplate(str2, paramParser, this);
        _formatNeeded = true;
      }
      else if (str1.equals("lang"))
      {
        _lang = new AttributeValueTemplate(str2, paramParser, this);
        _formatNeeded = true;
      }
      else if (str1.equals("letter-value"))
      {
        _letterValue = new AttributeValueTemplate(str2, paramParser, this);
        _formatNeeded = true;
      }
      else if (str1.equals("grouping-separator"))
      {
        _groupingSeparator = new AttributeValueTemplate(str2, paramParser, this);
        _formatNeeded = true;
      }
      else if (str1.equals("grouping-size"))
      {
        _groupingSize = new AttributeValueTemplate(str2, paramParser, this);
        _formatNeeded = true;
      }
    }
  }
  
  public com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    if (_value != null)
    {
      com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type localType = _value.typeCheck(paramSymbolTable);
      if (!(localType instanceof RealType)) {
        _value = new CastExpr(_value, com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Real);
      }
    }
    if (_count != null) {
      _count.typeCheck(paramSymbolTable);
    }
    if (_from != null) {
      _from.typeCheck(paramSymbolTable);
    }
    if (_format != null) {
      _format.typeCheck(paramSymbolTable);
    }
    if (_lang != null) {
      _lang.typeCheck(paramSymbolTable);
    }
    if (_letterValue != null) {
      _letterValue.typeCheck(paramSymbolTable);
    }
    if (_groupingSeparator != null) {
      _groupingSeparator.typeCheck(paramSymbolTable);
    }
    if (_groupingSize != null) {
      _groupingSize.typeCheck(paramSymbolTable);
    }
    return com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Void;
  }
  
  public boolean hasValue()
  {
    return _value != null;
  }
  
  public boolean isDefault()
  {
    return (_from == null) && (_count == null);
  }
  
  private void compileDefault(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int[] arrayOfInt = getXSLTC().getNumberFieldIndexes();
    if (arrayOfInt[_level] == -1)
    {
      localObject = new Field(2, localConstantPoolGen.addUtf8(FieldNames[_level]), localConstantPoolGen.addUtf8("Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeCounter;"), null, localConstantPoolGen.getConstantPool());
      paramClassGenerator.addField((Field)localObject);
      arrayOfInt[_level] = localConstantPoolGen.addFieldref(paramClassGenerator.getClassName(), FieldNames[_level], "Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeCounter;");
    }
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(new GETFIELD(arrayOfInt[_level]));
    Object localObject = localInstructionList.append(new IFNONNULL(null));
    int i = localConstantPoolGen.addMethodref(ClassNames[_level], "getDefaultNodeCounter", "(Lcom/sun/org/apache/xalan/internal/xsltc/Translet;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeCounter;");
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(paramMethodGenerator.loadDOM());
    localInstructionList.append(paramMethodGenerator.loadIterator());
    localInstructionList.append(new INVOKESTATIC(i));
    localInstructionList.append(DUP);
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(SWAP);
    localInstructionList.append(new PUTFIELD(arrayOfInt[_level]));
    BranchHandle localBranchHandle = localInstructionList.append(new GOTO(null));
    ((BranchHandle)localObject).setTarget(localInstructionList.append(paramClassGenerator.loadTranslet()));
    localInstructionList.append(new GETFIELD(arrayOfInt[_level]));
    localBranchHandle.setTarget(localInstructionList.append(NOP));
  }
  
  private void compileConstructor(ClassGenerator paramClassGenerator)
  {
    InstructionList localInstructionList = new InstructionList();
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    MethodGenerator localMethodGenerator = new MethodGenerator(1, com.sun.org.apache.bcel.internal.generic.Type.VOID, new com.sun.org.apache.bcel.internal.generic.Type[] { Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/Translet;"), Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"), Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), com.sun.org.apache.bcel.internal.generic.Type.BOOLEAN }, new String[] { "dom", "translet", "iterator", "hasFrom" }, "<init>", _className, localInstructionList, localConstantPoolGen);
    localInstructionList.append(ALOAD_0);
    localInstructionList.append(ALOAD_1);
    localInstructionList.append(ALOAD_2);
    localInstructionList.append(new ALOAD(3));
    localInstructionList.append(new ILOAD(4));
    int i = localConstantPoolGen.addMethodref(ClassNames[_level], "<init>", "(Lcom/sun/org/apache/xalan/internal/xsltc/Translet;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Z)V");
    localInstructionList.append(new INVOKESPECIAL(i));
    localInstructionList.append(RETURN);
    paramClassGenerator.addMethod(localMethodGenerator);
  }
  
  private void compileLocals(NodeCounterGenerator paramNodeCounterGenerator, MatchGenerator paramMatchGenerator, InstructionList paramInstructionList)
  {
    ConstantPoolGen localConstantPoolGen = paramNodeCounterGenerator.getConstantPool();
    LocalVariableGen localLocalVariableGen = paramMatchGenerator.addLocalVariable("iterator", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
    int i = localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.dom.NodeCounter", "_iterator", "Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    paramInstructionList.append(ALOAD_0);
    paramInstructionList.append(new GETFIELD(i));
    localLocalVariableGen.setStart(paramInstructionList.append(new ASTORE(localLocalVariableGen.getIndex())));
    paramMatchGenerator.setIteratorIndex(localLocalVariableGen.getIndex());
    localLocalVariableGen = paramMatchGenerator.addLocalVariable("translet", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet;"), null, null);
    i = localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.dom.NodeCounter", "_translet", "Lcom/sun/org/apache/xalan/internal/xsltc/Translet;");
    paramInstructionList.append(ALOAD_0);
    paramInstructionList.append(new GETFIELD(i));
    paramInstructionList.append(new CHECKCAST(localConstantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet")));
    localLocalVariableGen.setStart(paramInstructionList.append(new ASTORE(localLocalVariableGen.getIndex())));
    paramNodeCounterGenerator.setTransletIndex(localLocalVariableGen.getIndex());
    localLocalVariableGen = paramMatchGenerator.addLocalVariable("document", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"), null, null);
    i = localConstantPoolGen.addFieldref(_className, "_document", "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
    paramInstructionList.append(ALOAD_0);
    paramInstructionList.append(new GETFIELD(i));
    localLocalVariableGen.setStart(paramInstructionList.append(new ASTORE(localLocalVariableGen.getIndex())));
    paramMatchGenerator.setDomIndex(localLocalVariableGen.getIndex());
  }
  
  private void compilePatterns(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    _className = getXSLTC().getHelperClassName();
    NodeCounterGenerator localNodeCounterGenerator = new NodeCounterGenerator(_className, ClassNames[_level], toString(), 33, null, paramClassGenerator.getStylesheet());
    InstructionList localInstructionList = null;
    ConstantPoolGen localConstantPoolGen = localNodeCounterGenerator.getConstantPool();
    int i = _closureVars == null ? 0 : _closureVars.size();
    for (int j = 0; j < i; j++)
    {
      VariableBase localVariableBase1 = ((VariableRefBase)_closureVars.get(j)).getVariable();
      localNodeCounterGenerator.addField(new Field(1, localConstantPoolGen.addUtf8(localVariableBase1.getEscapedName()), localConstantPoolGen.addUtf8(localVariableBase1.getType().toSignature()), null, localConstantPoolGen.getConstantPool()));
    }
    compileConstructor(localNodeCounterGenerator);
    MatchGenerator localMatchGenerator;
    if (_from != null)
    {
      localInstructionList = new InstructionList();
      localMatchGenerator = new MatchGenerator(17, com.sun.org.apache.bcel.internal.generic.Type.BOOLEAN, new com.sun.org.apache.bcel.internal.generic.Type[] { com.sun.org.apache.bcel.internal.generic.Type.INT }, new String[] { "node" }, "matchesFrom", _className, localInstructionList, localConstantPoolGen);
      compileLocals(localNodeCounterGenerator, localMatchGenerator, localInstructionList);
      localInstructionList.append(localMatchGenerator.loadContextNode());
      _from.translate(localNodeCounterGenerator, localMatchGenerator);
      _from.synthesize(localNodeCounterGenerator, localMatchGenerator);
      localInstructionList.append(IRETURN);
      localNodeCounterGenerator.addMethod(localMatchGenerator);
    }
    if (_count != null)
    {
      localInstructionList = new InstructionList();
      localMatchGenerator = new MatchGenerator(17, com.sun.org.apache.bcel.internal.generic.Type.BOOLEAN, new com.sun.org.apache.bcel.internal.generic.Type[] { com.sun.org.apache.bcel.internal.generic.Type.INT }, new String[] { "node" }, "matchesCount", _className, localInstructionList, localConstantPoolGen);
      compileLocals(localNodeCounterGenerator, localMatchGenerator, localInstructionList);
      localInstructionList.append(localMatchGenerator.loadContextNode());
      _count.translate(localNodeCounterGenerator, localMatchGenerator);
      _count.synthesize(localNodeCounterGenerator, localMatchGenerator);
      localInstructionList.append(IRETURN);
      localNodeCounterGenerator.addMethod(localMatchGenerator);
    }
    getXSLTC().dumpClass(localNodeCounterGenerator.getJavaClass());
    localConstantPoolGen = paramClassGenerator.getConstantPool();
    localInstructionList = paramMethodGenerator.getInstructionList();
    j = localConstantPoolGen.addMethodref(_className, "<init>", "(Lcom/sun/org/apache/xalan/internal/xsltc/Translet;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Z)V");
    localInstructionList.append(new NEW(localConstantPoolGen.addClass(_className)));
    localInstructionList.append(DUP);
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(paramMethodGenerator.loadDOM());
    localInstructionList.append(paramMethodGenerator.loadIterator());
    localInstructionList.append(_from != null ? ICONST_1 : ICONST_0);
    localInstructionList.append(new INVOKESPECIAL(j));
    for (int k = 0; k < i; k++)
    {
      VariableRefBase localVariableRefBase = (VariableRefBase)_closureVars.get(k);
      VariableBase localVariableBase2 = localVariableRefBase.getVariable();
      com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type localType = localVariableBase2.getType();
      localInstructionList.append(DUP);
      localInstructionList.append(localVariableBase2.loadInstruction());
      localInstructionList.append(new PUTFIELD(localConstantPoolGen.addFieldref(_className, localVariableBase2.getEscapedName(), localType.toSignature())));
    }
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(paramClassGenerator.loadTranslet());
    if (hasValue())
    {
      compileDefault(paramClassGenerator, paramMethodGenerator);
      _value.translate(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(new PUSH(localConstantPoolGen, 0.5D));
      localInstructionList.append(DADD);
      i = localConstantPoolGen.addMethodref("java.lang.Math", "floor", "(D)D");
      localInstructionList.append(new INVOKESTATIC(i));
      i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.NodeCounter", "setValue", "(D)Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeCounter;");
      localInstructionList.append(new INVOKEVIRTUAL(i));
    }
    else if (isDefault())
    {
      compileDefault(paramClassGenerator, paramMethodGenerator);
    }
    else
    {
      compilePatterns(paramClassGenerator, paramMethodGenerator);
    }
    if (!hasValue())
    {
      localInstructionList.append(paramMethodGenerator.loadContextNode());
      i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.NodeCounter", "setStartNode", "(I)Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeCounter;");
      localInstructionList.append(new INVOKEVIRTUAL(i));
    }
    if (_formatNeeded)
    {
      if (_format != null) {
        _format.translate(paramClassGenerator, paramMethodGenerator);
      } else {
        localInstructionList.append(new PUSH(localConstantPoolGen, "1"));
      }
      if (_lang != null) {
        _lang.translate(paramClassGenerator, paramMethodGenerator);
      } else {
        localInstructionList.append(new PUSH(localConstantPoolGen, "en"));
      }
      if (_letterValue != null) {
        _letterValue.translate(paramClassGenerator, paramMethodGenerator);
      } else {
        localInstructionList.append(new PUSH(localConstantPoolGen, ""));
      }
      if (_groupingSeparator != null) {
        _groupingSeparator.translate(paramClassGenerator, paramMethodGenerator);
      } else {
        localInstructionList.append(new PUSH(localConstantPoolGen, ""));
      }
      if (_groupingSize != null) {
        _groupingSize.translate(paramClassGenerator, paramMethodGenerator);
      } else {
        localInstructionList.append(new PUSH(localConstantPoolGen, "0"));
      }
      i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.NodeCounter", "getCounter", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
      localInstructionList.append(new INVOKEVIRTUAL(i));
    }
    else
    {
      i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.NodeCounter", "setDefaultFormatting", "()Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeCounter;");
      localInstructionList.append(new INVOKEVIRTUAL(i));
      i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.NodeCounter", "getCounter", "()Ljava/lang/String;");
      localInstructionList.append(new INVOKEVIRTUAL(i));
    }
    localInstructionList.append(paramMethodGenerator.loadHandler());
    int i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "characters", "(Ljava/lang/String;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
    localInstructionList.append(new INVOKEVIRTUAL(i));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Number.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */