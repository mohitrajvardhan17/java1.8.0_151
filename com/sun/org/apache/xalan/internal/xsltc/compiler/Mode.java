package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.DUP;
import com.sun.org.apache.bcel.internal.generic.GOTO_W;
import com.sun.org.apache.bcel.internal.generic.IFLT;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.SWITCH;
import com.sun.org.apache.bcel.internal.generic.TargetLostException;
import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.bcel.internal.util.InstructionFinder;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NamedMethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

final class Mode
  implements Constants
{
  private final QName _name;
  private final Stylesheet _stylesheet;
  private final String _methodName;
  private Vector _templates;
  private Vector _childNodeGroup = null;
  private TestSeq _childNodeTestSeq = null;
  private Vector _attribNodeGroup = null;
  private TestSeq _attribNodeTestSeq = null;
  private Vector _idxGroup = null;
  private TestSeq _idxTestSeq = null;
  private Vector[] _patternGroups;
  private TestSeq[] _testSeq;
  private Map<Template, Object> _neededTemplates = new HashMap();
  private Map<Template, Mode> _namedTemplates = new HashMap();
  private Map<Template, InstructionHandle> _templateIHs = new HashMap();
  private Map<Template, InstructionList> _templateILs = new HashMap();
  private LocationPathPattern _rootPattern = null;
  private Map<Integer, Integer> _importLevels = null;
  private Map<String, Key> _keys = null;
  private int _currentIndex;
  
  public Mode(QName paramQName, Stylesheet paramStylesheet, String paramString)
  {
    _name = paramQName;
    _stylesheet = paramStylesheet;
    _methodName = ("applyTemplates" + paramString);
    _templates = new Vector();
    _patternGroups = new Vector[32];
  }
  
  public String functionName()
  {
    return _methodName;
  }
  
  public String functionName(int paramInt1, int paramInt2)
  {
    if (_importLevels == null) {
      _importLevels = new HashMap();
    }
    _importLevels.put(Integer.valueOf(paramInt2), Integer.valueOf(paramInt1));
    return _methodName + '_' + paramInt2;
  }
  
  private String getClassName()
  {
    return _stylesheet.getClassName();
  }
  
  public Stylesheet getStylesheet()
  {
    return _stylesheet;
  }
  
  public void addTemplate(Template paramTemplate)
  {
    _templates.addElement(paramTemplate);
  }
  
  private Vector quicksort(Vector paramVector, int paramInt1, int paramInt2)
  {
    if (paramInt1 < paramInt2)
    {
      int i = partition(paramVector, paramInt1, paramInt2);
      quicksort(paramVector, paramInt1, i);
      quicksort(paramVector, i + 1, paramInt2);
    }
    return paramVector;
  }
  
  private int partition(Vector paramVector, int paramInt1, int paramInt2)
  {
    Template localTemplate = (Template)paramVector.elementAt(paramInt1);
    int i = paramInt1 - 1;
    int j = paramInt2 + 1;
    for (;;)
    {
      if (localTemplate.compareTo((Template)paramVector.elementAt(--j)) <= 0)
      {
        while (localTemplate.compareTo((Template)paramVector.elementAt(++i)) < 0) {}
        if (i >= j) {
          break;
        }
        paramVector.set(j, paramVector.set(i, paramVector.elementAt(j)));
      }
    }
    return j;
  }
  
  public void processPatterns(Map<String, Key> paramMap)
  {
    _keys = paramMap;
    _templates = quicksort(_templates, 0, _templates.size() - 1);
    Enumeration localEnumeration = _templates.elements();
    while (localEnumeration.hasMoreElements())
    {
      Template localTemplate = (Template)localEnumeration.nextElement();
      if ((localTemplate.isNamed()) && (!localTemplate.disabled())) {
        _namedTemplates.put(localTemplate, this);
      }
      Pattern localPattern = localTemplate.getPattern();
      if (localPattern != null) {
        flattenAlternative(localPattern, localTemplate, paramMap);
      }
    }
    prepareTestSequences();
  }
  
  private void flattenAlternative(Pattern paramPattern, Template paramTemplate, Map<String, Key> paramMap)
  {
    Object localObject;
    if ((paramPattern instanceof IdKeyPattern))
    {
      localObject = (IdKeyPattern)paramPattern;
      ((IdKeyPattern)localObject).setTemplate(paramTemplate);
      if (_idxGroup == null) {
        _idxGroup = new Vector();
      }
      _idxGroup.add(paramPattern);
    }
    else if ((paramPattern instanceof AlternativePattern))
    {
      localObject = (AlternativePattern)paramPattern;
      flattenAlternative(((AlternativePattern)localObject).getLeft(), paramTemplate, paramMap);
      flattenAlternative(((AlternativePattern)localObject).getRight(), paramTemplate, paramMap);
    }
    else if ((paramPattern instanceof LocationPathPattern))
    {
      localObject = (LocationPathPattern)paramPattern;
      ((LocationPathPattern)localObject).setTemplate(paramTemplate);
      addPatternToGroup((LocationPathPattern)localObject);
    }
  }
  
  private void addPatternToGroup(LocationPathPattern paramLocationPathPattern)
  {
    if ((paramLocationPathPattern instanceof IdKeyPattern))
    {
      addPattern(-1, paramLocationPathPattern);
    }
    else
    {
      StepPattern localStepPattern = paramLocationPathPattern.getKernelPattern();
      if (localStepPattern != null) {
        addPattern(localStepPattern.getNodeType(), paramLocationPathPattern);
      } else if ((_rootPattern == null) || (paramLocationPathPattern.noSmallerThan(_rootPattern))) {
        _rootPattern = paramLocationPathPattern;
      }
    }
  }
  
  private void addPattern(int paramInt, LocationPathPattern paramLocationPathPattern)
  {
    int i = _patternGroups.length;
    Object localObject;
    if (paramInt >= i)
    {
      localObject = new Vector[paramInt * 2];
      System.arraycopy(_patternGroups, 0, localObject, 0, i);
      _patternGroups = ((Vector[])localObject);
    }
    if (paramInt == -1)
    {
      if (paramLocationPathPattern.getAxis() == 2) {
        localObject = _attribNodeGroup == null ? (_attribNodeGroup = new Vector(2)) : _attribNodeGroup;
      } else {
        localObject = _childNodeGroup == null ? (_childNodeGroup = new Vector(2)) : _childNodeGroup;
      }
    }
    else {
      localObject = _patternGroups[paramInt] == null ? (_patternGroups[paramInt] = new Vector(2)) : _patternGroups[paramInt];
    }
    if (((Vector)localObject).size() == 0)
    {
      ((Vector)localObject).addElement(paramLocationPathPattern);
    }
    else
    {
      int j = 0;
      for (int k = 0; k < ((Vector)localObject).size(); k++)
      {
        LocationPathPattern localLocationPathPattern = (LocationPathPattern)((Vector)localObject).elementAt(k);
        if (paramLocationPathPattern.noSmallerThan(localLocationPathPattern))
        {
          j = 1;
          ((Vector)localObject).insertElementAt(paramLocationPathPattern, k);
          break;
        }
      }
      if (j == 0) {
        ((Vector)localObject).addElement(paramLocationPathPattern);
      }
    }
  }
  
  private void completeTestSequences(int paramInt, Vector paramVector)
  {
    if (paramVector != null) {
      if (_patternGroups[paramInt] == null)
      {
        _patternGroups[paramInt] = paramVector;
      }
      else
      {
        int i = paramVector.size();
        for (int j = 0; j < i; j++) {
          addPattern(paramInt, (LocationPathPattern)paramVector.elementAt(j));
        }
      }
    }
  }
  
  private void prepareTestSequences()
  {
    Vector localVector1 = _patternGroups[1];
    Vector localVector2 = _patternGroups[2];
    completeTestSequences(3, _childNodeGroup);
    completeTestSequences(1, _childNodeGroup);
    completeTestSequences(7, _childNodeGroup);
    completeTestSequences(8, _childNodeGroup);
    completeTestSequences(2, _attribNodeGroup);
    Vector localVector3 = _stylesheet.getXSLTC().getNamesIndex();
    Object localObject;
    if ((localVector1 != null) || (localVector2 != null) || (_childNodeGroup != null) || (_attribNodeGroup != null))
    {
      i = _patternGroups.length;
      for (j = 14; j < i; j++) {
        if (_patternGroups[j] != null)
        {
          localObject = (String)localVector3.elementAt(j - 14);
          if (isAttributeName((String)localObject))
          {
            completeTestSequences(j, localVector2);
            completeTestSequences(j, _attribNodeGroup);
          }
          else
          {
            completeTestSequences(j, localVector1);
            completeTestSequences(j, _childNodeGroup);
          }
        }
      }
    }
    _testSeq = new TestSeq[14 + localVector3.size()];
    int i = _patternGroups.length;
    for (int j = 0; j < i; j++)
    {
      localObject = _patternGroups[j];
      if (localObject != null)
      {
        TestSeq localTestSeq = new TestSeq((Vector)localObject, j, this);
        localTestSeq.reduce();
        _testSeq[j] = localTestSeq;
        localTestSeq.findTemplates(_neededTemplates);
      }
    }
    if ((_childNodeGroup != null) && (_childNodeGroup.size() > 0))
    {
      _childNodeTestSeq = new TestSeq(_childNodeGroup, -1, this);
      _childNodeTestSeq.reduce();
      _childNodeTestSeq.findTemplates(_neededTemplates);
    }
    if ((_idxGroup != null) && (_idxGroup.size() > 0))
    {
      _idxTestSeq = new TestSeq(_idxGroup, this);
      _idxTestSeq.reduce();
      _idxTestSeq.findTemplates(_neededTemplates);
    }
    if (_rootPattern != null) {
      _neededTemplates.put(_rootPattern.getTemplate(), this);
    }
  }
  
  private void compileNamedTemplate(Template paramTemplate, ClassGenerator paramClassGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = new InstructionList();
    String str = Util.escape(paramTemplate.getName().toString());
    int i = 0;
    if (paramTemplate.isSimpleNamedTemplate())
    {
      localObject = paramTemplate.getParameters();
      i = ((Vector)localObject).size();
    }
    Object localObject = new Type[4 + i];
    String[] arrayOfString = new String[4 + i];
    localObject[0] = Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
    localObject[1] = Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    localObject[2] = Util.getJCRefType("Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;");
    localObject[3] = Type.INT;
    arrayOfString[0] = "document";
    arrayOfString[1] = "iterator";
    arrayOfString[2] = "handler";
    arrayOfString[3] = "node";
    for (int j = 4; j < 4 + i; j++)
    {
      localObject[j] = Util.getJCRefType("Ljava/lang/Object;");
      arrayOfString[j] = ("param" + String.valueOf(j - 4));
    }
    NamedMethodGenerator localNamedMethodGenerator = new NamedMethodGenerator(1, Type.VOID, (Type[])localObject, arrayOfString, str, getClassName(), localInstructionList, localConstantPoolGen);
    localInstructionList.append(paramTemplate.compile(paramClassGenerator, localNamedMethodGenerator));
    localInstructionList.append(RETURN);
    paramClassGenerator.addMethod(localNamedMethodGenerator);
  }
  
  private void compileTemplates(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, InstructionHandle paramInstructionHandle)
  {
    Set localSet = _namedTemplates.keySet();
    Iterator localIterator = localSet.iterator();
    Template localTemplate;
    while (localIterator.hasNext())
    {
      localTemplate = (Template)localIterator.next();
      compileNamedTemplate(localTemplate, paramClassGenerator);
    }
    localSet = _neededTemplates.keySet();
    localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      localTemplate = (Template)localIterator.next();
      if (localTemplate.hasContents())
      {
        InstructionList localInstructionList = localTemplate.compile(paramClassGenerator, paramMethodGenerator);
        localInstructionList.append(new GOTO_W(paramInstructionHandle));
        _templateILs.put(localTemplate, localInstructionList);
        _templateIHs.put(localTemplate, localInstructionList.getStart());
      }
      else
      {
        _templateIHs.put(localTemplate, paramInstructionHandle);
      }
    }
  }
  
  private void appendTemplateCode(InstructionList paramInstructionList)
  {
    Iterator localIterator = _neededTemplates.keySet().iterator();
    while (localIterator.hasNext())
    {
      Template localTemplate = (Template)localIterator.next();
      InstructionList localInstructionList = (InstructionList)_templateILs.get(localTemplate);
      if (localInstructionList != null) {
        paramInstructionList.append(localInstructionList);
      }
    }
  }
  
  private void appendTestSequences(InstructionList paramInstructionList)
  {
    int i = _testSeq.length;
    for (int j = 0; j < i; j++)
    {
      TestSeq localTestSeq = _testSeq[j];
      if (localTestSeq != null)
      {
        InstructionList localInstructionList = localTestSeq.getInstructionList();
        if (localInstructionList != null) {
          paramInstructionList.append(localInstructionList);
        }
      }
    }
  }
  
  public static void compileGetChildren(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, int paramInt)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getChildren", "(I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    localInstructionList.append(paramMethodGenerator.loadDOM());
    localInstructionList.append(new ILOAD(paramInt));
    localInstructionList.append(new INVOKEINTERFACE(i, 2));
  }
  
  private InstructionList compileDefaultRecursion(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, InstructionHandle paramInstructionHandle)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = new InstructionList();
    String str = paramClassGenerator.getApplyTemplatesSig();
    int i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getChildren", "(I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    int j = localConstantPoolGen.addMethodref(getClassName(), functionName(), str);
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(paramMethodGenerator.loadDOM());
    localInstructionList.append(paramMethodGenerator.loadDOM());
    localInstructionList.append(new ILOAD(_currentIndex));
    localInstructionList.append(new INVOKEINTERFACE(i, 2));
    localInstructionList.append(paramMethodGenerator.loadHandler());
    localInstructionList.append(new INVOKEVIRTUAL(j));
    localInstructionList.append(new GOTO_W(paramInstructionHandle));
    return localInstructionList;
  }
  
  private InstructionList compileDefaultText(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, InstructionHandle paramInstructionHandle)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = new InstructionList();
    int i = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "characters", "(ILcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
    localInstructionList.append(paramMethodGenerator.loadDOM());
    localInstructionList.append(new ILOAD(_currentIndex));
    localInstructionList.append(paramMethodGenerator.loadHandler());
    localInstructionList.append(new INVOKEINTERFACE(i, 3));
    localInstructionList.append(new GOTO_W(paramInstructionHandle));
    return localInstructionList;
  }
  
  private InstructionList compileNamespaces(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, boolean[] paramArrayOfBoolean1, boolean[] paramArrayOfBoolean2, boolean paramBoolean, InstructionHandle paramInstructionHandle)
  {
    XSLTC localXSLTC = paramClassGenerator.getParser().getXSLTC();
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    Vector localVector1 = localXSLTC.getNamespaceIndex();
    Vector localVector2 = localXSLTC.getNamesIndex();
    int i = localVector1.size() + 1;
    int j = localVector2.size();
    InstructionList localInstructionList = new InstructionList();
    int[] arrayOfInt = new int[i];
    InstructionHandle[] arrayOfInstructionHandle = new InstructionHandle[arrayOfInt.length];
    if (i > 0)
    {
      int k = 0;
      for (int m = 0; m < i; m++)
      {
        arrayOfInstructionHandle[m] = paramInstructionHandle;
        arrayOfInt[m] = m;
      }
      for (m = 14; m < 14 + j; m++) {
        if ((paramArrayOfBoolean1[m] != 0) && (paramArrayOfBoolean2[m] == paramBoolean))
        {
          String str1 = (String)localVector2.elementAt(m - 14);
          String str2 = str1.substring(0, str1.lastIndexOf(':'));
          int n = localXSLTC.registerNamespace(str2);
          if ((m < _testSeq.length) && (_testSeq[m] != null))
          {
            arrayOfInstructionHandle[n] = _testSeq[m].compile(paramClassGenerator, paramMethodGenerator, paramInstructionHandle);
            k = 1;
          }
        }
      }
      if (k == 0) {
        return null;
      }
      m = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNamespaceType", "(I)I");
      localInstructionList.append(paramMethodGenerator.loadDOM());
      localInstructionList.append(new ILOAD(_currentIndex));
      localInstructionList.append(new INVOKEINTERFACE(m, 2));
      localInstructionList.append(new SWITCH(arrayOfInt, arrayOfInstructionHandle, paramInstructionHandle));
      return localInstructionList;
    }
    return null;
  }
  
  public void compileApplyTemplates(ClassGenerator paramClassGenerator)
  {
    XSLTC localXSLTC = paramClassGenerator.getParser().getXSLTC();
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    Vector localVector = localXSLTC.getNamesIndex();
    Type[] arrayOfType = new Type[3];
    arrayOfType[0] = Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
    arrayOfType[1] = Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    arrayOfType[2] = Util.getJCRefType("Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;");
    String[] arrayOfString = new String[3];
    arrayOfString[0] = "document";
    arrayOfString[1] = "iterator";
    arrayOfString[2] = "handler";
    InstructionList localInstructionList1 = new InstructionList();
    MethodGenerator localMethodGenerator = new MethodGenerator(17, Type.VOID, arrayOfType, arrayOfString, functionName(), getClassName(), localInstructionList1, paramClassGenerator.getConstantPool());
    localMethodGenerator.addException("com.sun.org.apache.xalan.internal.xsltc.TransletException");
    localInstructionList1.append(NOP);
    LocalVariableGen localLocalVariableGen = localMethodGenerator.addLocalVariable2("current", Type.INT, null);
    _currentIndex = localLocalVariableGen.getIndex();
    InstructionList localInstructionList2 = new InstructionList();
    localInstructionList2.append(NOP);
    InstructionList localInstructionList3 = new InstructionList();
    localInstructionList3.append(localMethodGenerator.loadIterator());
    localInstructionList3.append(localMethodGenerator.nextNode());
    localInstructionList3.append(DUP);
    localInstructionList3.append(new ISTORE(_currentIndex));
    BranchHandle localBranchHandle1 = localInstructionList3.append(new IFLT(null));
    BranchHandle localBranchHandle2 = localInstructionList3.append(new GOTO_W(null));
    localBranchHandle1.setTarget(localInstructionList3.append(RETURN));
    InstructionHandle localInstructionHandle1 = localInstructionList3.getStart();
    localLocalVariableGen.setStart(localInstructionList1.append(new GOTO_W(localInstructionHandle1)));
    localLocalVariableGen.setEnd(localBranchHandle2);
    InstructionList localInstructionList4 = compileDefaultRecursion(paramClassGenerator, localMethodGenerator, localInstructionHandle1);
    InstructionHandle localInstructionHandle2 = localInstructionList4.getStart();
    InstructionList localInstructionList5 = compileDefaultText(paramClassGenerator, localMethodGenerator, localInstructionHandle1);
    InstructionHandle localInstructionHandle3 = localInstructionList5.getStart();
    int[] arrayOfInt = new int[14 + localVector.size()];
    for (int i = 0; i < arrayOfInt.length; i++) {
      arrayOfInt[i] = i;
    }
    boolean[] arrayOfBoolean1 = new boolean[arrayOfInt.length];
    boolean[] arrayOfBoolean2 = new boolean[arrayOfInt.length];
    for (int j = 0; j < localVector.size(); j++)
    {
      localObject1 = (String)localVector.elementAt(j);
      arrayOfBoolean1[(j + 14)] = isAttributeName((String)localObject1);
      arrayOfBoolean2[(j + 14)] = isNamespaceName((String)localObject1);
    }
    compileTemplates(paramClassGenerator, localMethodGenerator, localInstructionHandle1);
    TestSeq localTestSeq1 = _testSeq[1];
    Object localObject1 = localInstructionHandle2;
    if (localTestSeq1 != null) {
      localObject1 = localTestSeq1.compile(paramClassGenerator, localMethodGenerator, localInstructionHandle2);
    }
    TestSeq localTestSeq2 = _testSeq[2];
    InstructionHandle localInstructionHandle4 = localInstructionHandle3;
    if (localTestSeq2 != null) {
      localInstructionHandle4 = localTestSeq2.compile(paramClassGenerator, localMethodGenerator, localInstructionHandle4);
    }
    InstructionList localInstructionList6 = null;
    if (_idxTestSeq != null)
    {
      localBranchHandle2.setTarget(_idxTestSeq.compile(paramClassGenerator, localMethodGenerator, localInstructionList2.getStart()));
      localInstructionList6 = _idxTestSeq.getInstructionList();
    }
    else
    {
      localBranchHandle2.setTarget(localInstructionList2.getStart());
    }
    if (_childNodeTestSeq != null)
    {
      double d1 = _childNodeTestSeq.getPriority();
      int k = _childNodeTestSeq.getPosition();
      double d2 = -1.7976931348623157E308D;
      m = Integer.MIN_VALUE;
      if (localTestSeq1 != null)
      {
        d2 = localTestSeq1.getPriority();
        m = localTestSeq1.getPosition();
      }
      if ((d2 == NaN.0D) || (d2 < d1) || ((d2 == d1) && (m < k))) {
        localObject1 = _childNodeTestSeq.compile(paramClassGenerator, localMethodGenerator, localInstructionHandle1);
      }
      localObject4 = _testSeq[3];
      double d3 = -1.7976931348623157E308D;
      int i1 = Integer.MIN_VALUE;
      if (localObject4 != null)
      {
        d3 = ((TestSeq)localObject4).getPriority();
        i1 = ((TestSeq)localObject4).getPosition();
      }
      if ((d3 == NaN.0D) || (d3 < d1) || ((d3 == d1) && (i1 < k)))
      {
        localInstructionHandle3 = _childNodeTestSeq.compile(paramClassGenerator, localMethodGenerator, localInstructionHandle1);
        _testSeq[3] = _childNodeTestSeq;
      }
    }
    Object localObject2 = localObject1;
    InstructionList localInstructionList7 = compileNamespaces(paramClassGenerator, localMethodGenerator, arrayOfBoolean2, arrayOfBoolean1, false, (InstructionHandle)localObject1);
    if (localInstructionList7 != null) {
      localObject2 = localInstructionList7.getStart();
    }
    InstructionHandle localInstructionHandle5 = localInstructionHandle4;
    InstructionList localInstructionList8 = compileNamespaces(paramClassGenerator, localMethodGenerator, arrayOfBoolean2, arrayOfBoolean1, true, localInstructionHandle4);
    if (localInstructionList8 != null) {
      localInstructionHandle5 = localInstructionList8.getStart();
    }
    InstructionHandle[] arrayOfInstructionHandle = new InstructionHandle[arrayOfInt.length];
    for (int m = 14; m < arrayOfInstructionHandle.length; m++)
    {
      localObject4 = _testSeq[m];
      if (arrayOfBoolean2[m] != 0)
      {
        if (arrayOfBoolean1[m] != 0) {
          arrayOfInstructionHandle[m] = localInstructionHandle5;
        } else {
          arrayOfInstructionHandle[m] = localObject2;
        }
      }
      else if (localObject4 != null)
      {
        if (arrayOfBoolean1[m] != 0) {
          arrayOfInstructionHandle[m] = ((TestSeq)localObject4).compile(paramClassGenerator, localMethodGenerator, localInstructionHandle5);
        } else {
          arrayOfInstructionHandle[m] = ((TestSeq)localObject4).compile(paramClassGenerator, localMethodGenerator, (InstructionHandle)localObject2);
        }
      }
      else {
        arrayOfInstructionHandle[m] = localInstructionHandle1;
      }
    }
    arrayOfInstructionHandle[0] = (_rootPattern != null ? getTemplateInstructionHandle(_rootPattern.getTemplate()) : localInstructionHandle2);
    arrayOfInstructionHandle[9] = (_rootPattern != null ? getTemplateInstructionHandle(_rootPattern.getTemplate()) : localInstructionHandle2);
    arrayOfInstructionHandle[3] = (_testSeq[3] != null ? _testSeq[3].compile(paramClassGenerator, localMethodGenerator, localInstructionHandle3) : localInstructionHandle3);
    arrayOfInstructionHandle[13] = localInstructionHandle1;
    arrayOfInstructionHandle[1] = localObject2;
    arrayOfInstructionHandle[2] = localInstructionHandle5;
    Object localObject3 = localInstructionHandle1;
    if (_childNodeTestSeq != null) {
      localObject3 = localObject1;
    }
    if (_testSeq[7] != null) {
      arrayOfInstructionHandle[7] = _testSeq[7].compile(paramClassGenerator, localMethodGenerator, (InstructionHandle)localObject3);
    } else {
      arrayOfInstructionHandle[7] = localObject3;
    }
    Object localObject4 = localInstructionHandle1;
    if (_childNodeTestSeq != null) {
      localObject4 = localObject1;
    }
    arrayOfInstructionHandle[8] = (_testSeq[8] != null ? _testSeq[8].compile(paramClassGenerator, localMethodGenerator, (InstructionHandle)localObject4) : localObject4);
    arrayOfInstructionHandle[4] = localInstructionHandle1;
    arrayOfInstructionHandle[11] = localInstructionHandle1;
    arrayOfInstructionHandle[10] = localInstructionHandle1;
    arrayOfInstructionHandle[6] = localInstructionHandle1;
    arrayOfInstructionHandle[5] = localInstructionHandle1;
    arrayOfInstructionHandle[12] = localInstructionHandle1;
    for (int n = 14; n < arrayOfInstructionHandle.length; n++)
    {
      localObject5 = _testSeq[n];
      if ((localObject5 == null) || (arrayOfBoolean2[n] != 0))
      {
        if (arrayOfBoolean1[n] != 0) {
          arrayOfInstructionHandle[n] = localInstructionHandle5;
        } else {
          arrayOfInstructionHandle[n] = localObject2;
        }
      }
      else if (arrayOfBoolean1[n] != 0) {
        arrayOfInstructionHandle[n] = ((TestSeq)localObject5).compile(paramClassGenerator, localMethodGenerator, localInstructionHandle5);
      } else {
        arrayOfInstructionHandle[n] = ((TestSeq)localObject5).compile(paramClassGenerator, localMethodGenerator, (InstructionHandle)localObject2);
      }
    }
    if (localInstructionList6 != null) {
      localInstructionList2.insert(localInstructionList6);
    }
    n = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getExpandedTypeID", "(I)I");
    localInstructionList2.append(localMethodGenerator.loadDOM());
    localInstructionList2.append(new ILOAD(_currentIndex));
    localInstructionList2.append(new INVOKEINTERFACE(n, 2));
    Object localObject5 = localInstructionList2.append(new SWITCH(arrayOfInt, arrayOfInstructionHandle, localInstructionHandle1));
    appendTestSequences(localInstructionList2);
    appendTemplateCode(localInstructionList2);
    if (localInstructionList7 != null) {
      localInstructionList2.append(localInstructionList7);
    }
    if (localInstructionList8 != null) {
      localInstructionList2.append(localInstructionList8);
    }
    localInstructionList2.append(localInstructionList4);
    localInstructionList2.append(localInstructionList5);
    localInstructionList1.append(localInstructionList2);
    localInstructionList1.append(localInstructionList3);
    peepHoleOptimization(localMethodGenerator);
    paramClassGenerator.addMethod(localMethodGenerator);
    if (_importLevels != null)
    {
      Iterator localIterator = _importLevels.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        compileApplyImports(paramClassGenerator, ((Integer)localEntry.getValue()).intValue(), ((Integer)localEntry.getKey()).intValue());
      }
    }
  }
  
  private void compileTemplateCalls(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, InstructionHandle paramInstructionHandle, int paramInt1, int paramInt2)
  {
    Iterator localIterator = _neededTemplates.keySet().iterator();
    while (localIterator.hasNext())
    {
      Template localTemplate = (Template)localIterator.next();
      int i = localTemplate.getImportPrecedence();
      if ((i >= paramInt1) && (i < paramInt2)) {
        if (localTemplate.hasContents())
        {
          InstructionList localInstructionList = localTemplate.compile(paramClassGenerator, paramMethodGenerator);
          localInstructionList.append(new GOTO_W(paramInstructionHandle));
          _templateILs.put(localTemplate, localInstructionList);
          _templateIHs.put(localTemplate, localInstructionList.getStart());
        }
        else
        {
          _templateIHs.put(localTemplate, paramInstructionHandle);
        }
      }
    }
  }
  
  public void compileApplyImports(ClassGenerator paramClassGenerator, int paramInt1, int paramInt2)
  {
    XSLTC localXSLTC = paramClassGenerator.getParser().getXSLTC();
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    Vector localVector1 = localXSLTC.getNamesIndex();
    _namedTemplates = new HashMap();
    _neededTemplates = new HashMap();
    _templateIHs = new HashMap();
    _templateILs = new HashMap();
    _patternGroups = new Vector[32];
    _rootPattern = null;
    Vector localVector2 = _templates;
    _templates = new Vector();
    Enumeration localEnumeration = localVector2.elements();
    while (localEnumeration.hasMoreElements())
    {
      localObject1 = (Template)localEnumeration.nextElement();
      int i = ((Template)localObject1).getImportPrecedence();
      if ((i >= paramInt1) && (i < paramInt2)) {
        addTemplate((Template)localObject1);
      }
    }
    processPatterns(_keys);
    Object localObject1 = new Type[4];
    localObject1[0] = Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
    localObject1[1] = Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    localObject1[2] = Util.getJCRefType("Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;");
    localObject1[3] = Type.INT;
    String[] arrayOfString = new String[4];
    arrayOfString[0] = "document";
    arrayOfString[1] = "iterator";
    arrayOfString[2] = "handler";
    arrayOfString[3] = "node";
    InstructionList localInstructionList1 = new InstructionList();
    MethodGenerator localMethodGenerator = new MethodGenerator(17, Type.VOID, (Type[])localObject1, arrayOfString, functionName() + '_' + paramInt2, getClassName(), localInstructionList1, paramClassGenerator.getConstantPool());
    localMethodGenerator.addException("com.sun.org.apache.xalan.internal.xsltc.TransletException");
    LocalVariableGen localLocalVariableGen = localMethodGenerator.addLocalVariable2("current", Type.INT, null);
    _currentIndex = localLocalVariableGen.getIndex();
    localInstructionList1.append(new ILOAD(localMethodGenerator.getLocalIndex("node")));
    localLocalVariableGen.setStart(localInstructionList1.append(new ISTORE(_currentIndex)));
    InstructionList localInstructionList2 = new InstructionList();
    localInstructionList2.append(NOP);
    InstructionList localInstructionList3 = new InstructionList();
    localInstructionList3.append(RETURN);
    InstructionHandle localInstructionHandle1 = localInstructionList3.getStart();
    InstructionList localInstructionList4 = compileDefaultRecursion(paramClassGenerator, localMethodGenerator, localInstructionHandle1);
    InstructionHandle localInstructionHandle2 = localInstructionList4.getStart();
    InstructionList localInstructionList5 = compileDefaultText(paramClassGenerator, localMethodGenerator, localInstructionHandle1);
    InstructionHandle localInstructionHandle3 = localInstructionList5.getStart();
    int[] arrayOfInt = new int[14 + localVector1.size()];
    for (int j = 0; j < arrayOfInt.length; j++) {
      arrayOfInt[j] = j;
    }
    boolean[] arrayOfBoolean1 = new boolean[arrayOfInt.length];
    boolean[] arrayOfBoolean2 = new boolean[arrayOfInt.length];
    for (int k = 0; k < localVector1.size(); k++)
    {
      localObject2 = (String)localVector1.elementAt(k);
      arrayOfBoolean1[(k + 14)] = isAttributeName((String)localObject2);
      arrayOfBoolean2[(k + 14)] = isNamespaceName((String)localObject2);
    }
    compileTemplateCalls(paramClassGenerator, localMethodGenerator, localInstructionHandle1, paramInt1, paramInt2);
    TestSeq localTestSeq1 = _testSeq[1];
    Object localObject2 = localInstructionHandle2;
    if (localTestSeq1 != null) {
      localObject2 = localTestSeq1.compile(paramClassGenerator, localMethodGenerator, localInstructionHandle1);
    }
    TestSeq localTestSeq2 = _testSeq[2];
    InstructionHandle localInstructionHandle4 = localInstructionHandle1;
    if (localTestSeq2 != null) {
      localInstructionHandle4 = localTestSeq2.compile(paramClassGenerator, localMethodGenerator, localInstructionHandle4);
    }
    InstructionList localInstructionList6 = null;
    if (_idxTestSeq != null) {
      localInstructionList6 = _idxTestSeq.getInstructionList();
    }
    if (_childNodeTestSeq != null)
    {
      double d1 = _childNodeTestSeq.getPriority();
      int m = _childNodeTestSeq.getPosition();
      double d2 = -1.7976931348623157E308D;
      n = Integer.MIN_VALUE;
      if (localTestSeq1 != null)
      {
        d2 = localTestSeq1.getPriority();
        n = localTestSeq1.getPosition();
      }
      if ((d2 == NaN.0D) || (d2 < d1) || ((d2 == d1) && (n < m))) {
        localObject2 = _childNodeTestSeq.compile(paramClassGenerator, localMethodGenerator, localInstructionHandle1);
      }
      localObject5 = _testSeq[3];
      double d3 = -1.7976931348623157E308D;
      int i2 = Integer.MIN_VALUE;
      if (localObject5 != null)
      {
        d3 = ((TestSeq)localObject5).getPriority();
        i2 = ((TestSeq)localObject5).getPosition();
      }
      if ((d3 == NaN.0D) || (d3 < d1) || ((d3 == d1) && (i2 < m)))
      {
        localInstructionHandle3 = _childNodeTestSeq.compile(paramClassGenerator, localMethodGenerator, localInstructionHandle1);
        _testSeq[3] = _childNodeTestSeq;
      }
    }
    Object localObject3 = localObject2;
    InstructionList localInstructionList7 = compileNamespaces(paramClassGenerator, localMethodGenerator, arrayOfBoolean2, arrayOfBoolean1, false, (InstructionHandle)localObject2);
    if (localInstructionList7 != null) {
      localObject3 = localInstructionList7.getStart();
    }
    InstructionList localInstructionList8 = compileNamespaces(paramClassGenerator, localMethodGenerator, arrayOfBoolean2, arrayOfBoolean1, true, localInstructionHandle4);
    InstructionHandle localInstructionHandle5 = localInstructionHandle4;
    if (localInstructionList8 != null) {
      localInstructionHandle5 = localInstructionList8.getStart();
    }
    InstructionHandle[] arrayOfInstructionHandle = new InstructionHandle[arrayOfInt.length];
    for (int n = 14; n < arrayOfInstructionHandle.length; n++)
    {
      localObject5 = _testSeq[n];
      if (arrayOfBoolean2[n] != 0)
      {
        if (arrayOfBoolean1[n] != 0) {
          arrayOfInstructionHandle[n] = localInstructionHandle5;
        } else {
          arrayOfInstructionHandle[n] = localObject3;
        }
      }
      else if (localObject5 != null)
      {
        if (arrayOfBoolean1[n] != 0) {
          arrayOfInstructionHandle[n] = ((TestSeq)localObject5).compile(paramClassGenerator, localMethodGenerator, localInstructionHandle5);
        } else {
          arrayOfInstructionHandle[n] = ((TestSeq)localObject5).compile(paramClassGenerator, localMethodGenerator, (InstructionHandle)localObject3);
        }
      }
      else {
        arrayOfInstructionHandle[n] = localInstructionHandle1;
      }
    }
    arrayOfInstructionHandle[0] = (_rootPattern != null ? getTemplateInstructionHandle(_rootPattern.getTemplate()) : localInstructionHandle2);
    arrayOfInstructionHandle[9] = (_rootPattern != null ? getTemplateInstructionHandle(_rootPattern.getTemplate()) : localInstructionHandle2);
    arrayOfInstructionHandle[3] = (_testSeq[3] != null ? _testSeq[3].compile(paramClassGenerator, localMethodGenerator, localInstructionHandle3) : localInstructionHandle3);
    arrayOfInstructionHandle[13] = localInstructionHandle1;
    arrayOfInstructionHandle[1] = localObject3;
    arrayOfInstructionHandle[2] = localInstructionHandle5;
    Object localObject4 = localInstructionHandle1;
    if (_childNodeTestSeq != null) {
      localObject4 = localObject2;
    }
    if (_testSeq[7] != null) {
      arrayOfInstructionHandle[7] = _testSeq[7].compile(paramClassGenerator, localMethodGenerator, (InstructionHandle)localObject4);
    } else {
      arrayOfInstructionHandle[7] = localObject4;
    }
    Object localObject5 = localInstructionHandle1;
    if (_childNodeTestSeq != null) {
      localObject5 = localObject2;
    }
    arrayOfInstructionHandle[8] = (_testSeq[8] != null ? _testSeq[8].compile(paramClassGenerator, localMethodGenerator, (InstructionHandle)localObject5) : localObject5);
    arrayOfInstructionHandle[4] = localInstructionHandle1;
    arrayOfInstructionHandle[11] = localInstructionHandle1;
    arrayOfInstructionHandle[10] = localInstructionHandle1;
    arrayOfInstructionHandle[6] = localInstructionHandle1;
    arrayOfInstructionHandle[5] = localInstructionHandle1;
    arrayOfInstructionHandle[12] = localInstructionHandle1;
    for (int i1 = 14; i1 < arrayOfInstructionHandle.length; i1++)
    {
      localObject6 = _testSeq[i1];
      if ((localObject6 == null) || (arrayOfBoolean2[i1] != 0))
      {
        if (arrayOfBoolean1[i1] != 0) {
          arrayOfInstructionHandle[i1] = localInstructionHandle5;
        } else {
          arrayOfInstructionHandle[i1] = localObject3;
        }
      }
      else if (arrayOfBoolean1[i1] != 0) {
        arrayOfInstructionHandle[i1] = ((TestSeq)localObject6).compile(paramClassGenerator, localMethodGenerator, localInstructionHandle5);
      } else {
        arrayOfInstructionHandle[i1] = ((TestSeq)localObject6).compile(paramClassGenerator, localMethodGenerator, (InstructionHandle)localObject3);
      }
    }
    if (localInstructionList6 != null) {
      localInstructionList2.insert(localInstructionList6);
    }
    i1 = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getExpandedTypeID", "(I)I");
    localInstructionList2.append(localMethodGenerator.loadDOM());
    localInstructionList2.append(new ILOAD(_currentIndex));
    localInstructionList2.append(new INVOKEINTERFACE(i1, 2));
    Object localObject6 = localInstructionList2.append(new SWITCH(arrayOfInt, arrayOfInstructionHandle, localInstructionHandle1));
    appendTestSequences(localInstructionList2);
    appendTemplateCode(localInstructionList2);
    if (localInstructionList7 != null) {
      localInstructionList2.append(localInstructionList7);
    }
    if (localInstructionList8 != null) {
      localInstructionList2.append(localInstructionList8);
    }
    localInstructionList2.append(localInstructionList4);
    localInstructionList2.append(localInstructionList5);
    localInstructionList1.append(localInstructionList2);
    localLocalVariableGen.setEnd(localInstructionList2.getEnd());
    localInstructionList1.append(localInstructionList3);
    peepHoleOptimization(localMethodGenerator);
    paramClassGenerator.addMethod(localMethodGenerator);
    _templates = localVector2;
  }
  
  private void peepHoleOptimization(MethodGenerator paramMethodGenerator)
  {
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    InstructionFinder localInstructionFinder = new InstructionFinder(localInstructionList);
    String str = "loadinstruction pop";
    Iterator localIterator = localInstructionFinder.search(str);
    InstructionHandle[] arrayOfInstructionHandle;
    while (localIterator.hasNext())
    {
      arrayOfInstructionHandle = (InstructionHandle[])localIterator.next();
      try
      {
        if ((!arrayOfInstructionHandle[0].hasTargeters()) && (!arrayOfInstructionHandle[1].hasTargeters())) {
          localInstructionList.delete(arrayOfInstructionHandle[0], arrayOfInstructionHandle[1]);
        }
      }
      catch (TargetLostException localTargetLostException1) {}
    }
    str = "iload iload swap istore";
    localIterator = localInstructionFinder.search(str);
    Object localObject;
    while (localIterator.hasNext())
    {
      arrayOfInstructionHandle = (InstructionHandle[])localIterator.next();
      try
      {
        ILOAD localILOAD = (ILOAD)arrayOfInstructionHandle[0].getInstruction();
        localObject = (ILOAD)arrayOfInstructionHandle[1].getInstruction();
        ISTORE localISTORE = (ISTORE)arrayOfInstructionHandle[3].getInstruction();
        if ((!arrayOfInstructionHandle[1].hasTargeters()) && (!arrayOfInstructionHandle[2].hasTargeters()) && (!arrayOfInstructionHandle[3].hasTargeters()) && (localILOAD.getIndex() == ((ILOAD)localObject).getIndex()) && (((ILOAD)localObject).getIndex() == localISTORE.getIndex())) {
          localInstructionList.delete(arrayOfInstructionHandle[1], arrayOfInstructionHandle[3]);
        }
      }
      catch (TargetLostException localTargetLostException2) {}
    }
    str = "loadinstruction loadinstruction swap";
    localIterator = localInstructionFinder.search(str);
    while (localIterator.hasNext())
    {
      arrayOfInstructionHandle = (InstructionHandle[])localIterator.next();
      try
      {
        if ((!arrayOfInstructionHandle[0].hasTargeters()) && (!arrayOfInstructionHandle[1].hasTargeters()) && (!arrayOfInstructionHandle[2].hasTargeters()))
        {
          Instruction localInstruction = arrayOfInstructionHandle[1].getInstruction();
          localInstructionList.insert(arrayOfInstructionHandle[0], localInstruction);
          localInstructionList.delete(arrayOfInstructionHandle[1], arrayOfInstructionHandle[2]);
        }
      }
      catch (TargetLostException localTargetLostException3) {}
    }
    str = "aload aload";
    localIterator = localInstructionFinder.search(str);
    while (localIterator.hasNext())
    {
      arrayOfInstructionHandle = (InstructionHandle[])localIterator.next();
      try
      {
        if (!arrayOfInstructionHandle[1].hasTargeters())
        {
          ALOAD localALOAD = (ALOAD)arrayOfInstructionHandle[0].getInstruction();
          localObject = (ALOAD)arrayOfInstructionHandle[1].getInstruction();
          if (localALOAD.getIndex() == ((ALOAD)localObject).getIndex())
          {
            localInstructionList.insert(arrayOfInstructionHandle[1], new DUP());
            localInstructionList.delete(arrayOfInstructionHandle[1]);
          }
        }
      }
      catch (TargetLostException localTargetLostException4) {}
    }
  }
  
  public InstructionHandle getTemplateInstructionHandle(Template paramTemplate)
  {
    return (InstructionHandle)_templateIHs.get(paramTemplate);
  }
  
  private static boolean isAttributeName(String paramString)
  {
    int i = paramString.lastIndexOf(':') + 1;
    return paramString.charAt(i) == '@';
  }
  
  private static boolean isNamespaceName(String paramString)
  {
    int i = paramString.lastIndexOf(':');
    return (i > -1) && (paramString.charAt(paramString.length() - 1) == '*');
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Mode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */