package jdk.internal.org.objectweb.asm.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.Attribute;
import jdk.internal.org.objectweb.asm.Handle;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.TypePath;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import jdk.internal.org.objectweb.asm.tree.analysis.Analyzer;
import jdk.internal.org.objectweb.asm.tree.analysis.BasicVerifier;

public class CheckMethodAdapter
  extends MethodVisitor
{
  public int version;
  private int access;
  private boolean startCode;
  private boolean endCode;
  private boolean endMethod;
  private int insnCount;
  private final Map<Label, Integer> labels;
  private Set<Label> usedLabels;
  private int expandedFrames;
  private int compressedFrames;
  private int lastFrame = -1;
  private List<Label> handlers;
  private static final int[] TYPE;
  private static Field labelStatusField;
  
  public CheckMethodAdapter(MethodVisitor paramMethodVisitor)
  {
    this(paramMethodVisitor, new HashMap());
  }
  
  public CheckMethodAdapter(MethodVisitor paramMethodVisitor, Map<Label, Integer> paramMap)
  {
    this(327680, paramMethodVisitor, paramMap);
    if (getClass() != CheckMethodAdapter.class) {
      throw new IllegalStateException();
    }
  }
  
  protected CheckMethodAdapter(int paramInt, MethodVisitor paramMethodVisitor, Map<Label, Integer> paramMap)
  {
    super(paramInt, paramMethodVisitor);
    labels = paramMap;
    usedLabels = new HashSet();
    handlers = new ArrayList();
  }
  
  public CheckMethodAdapter(int paramInt, String paramString1, String paramString2, final MethodVisitor paramMethodVisitor, Map<Label, Integer> paramMap)
  {
    this(new MethodNode(327680, paramInt, paramString1, paramString2, null, null)
    {
      public void visitEnd()
      {
        Analyzer localAnalyzer = new Analyzer(new BasicVerifier());
        try
        {
          localAnalyzer.analyze("dummy", this);
        }
        catch (Exception localException)
        {
          if (((localException instanceof IndexOutOfBoundsException)) && (maxLocals == 0) && (maxStack == 0)) {
            throw new RuntimeException("Data flow checking option requires valid, non zero maxLocals and maxStack values.");
          }
          localException.printStackTrace();
          StringWriter localStringWriter = new StringWriter();
          PrintWriter localPrintWriter = new PrintWriter(localStringWriter, true);
          CheckClassAdapter.printAnalyzerResult(this, localAnalyzer, localPrintWriter);
          localPrintWriter.close();
          throw new RuntimeException(localException.getMessage() + ' ' + localStringWriter.toString());
        }
        accept(paramMethodVisitor);
      }
    }, paramMap);
    access = paramInt;
  }
  
  public void visitParameter(String paramString, int paramInt)
  {
    if (paramString != null) {
      checkUnqualifiedName(version, paramString, "name");
    }
    CheckClassAdapter.checkAccess(paramInt, 36880);
    super.visitParameter(paramString, paramInt);
  }
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean)
  {
    checkEndMethod();
    checkDesc(paramString, false);
    return new CheckAnnotationAdapter(super.visitAnnotation(paramString, paramBoolean));
  }
  
  public AnnotationVisitor visitTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    checkEndMethod();
    int i = paramInt >>> 24;
    if ((i != 1) && (i != 18) && (i != 20) && (i != 21) && (i != 22) && (i != 23)) {
      throw new IllegalArgumentException("Invalid type reference sort 0x" + Integer.toHexString(i));
    }
    CheckClassAdapter.checkTypeRefAndPath(paramInt, paramTypePath);
    checkDesc(paramString, false);
    return new CheckAnnotationAdapter(super.visitTypeAnnotation(paramInt, paramTypePath, paramString, paramBoolean));
  }
  
  public AnnotationVisitor visitAnnotationDefault()
  {
    checkEndMethod();
    return new CheckAnnotationAdapter(super.visitAnnotationDefault(), false);
  }
  
  public AnnotationVisitor visitParameterAnnotation(int paramInt, String paramString, boolean paramBoolean)
  {
    checkEndMethod();
    checkDesc(paramString, false);
    return new CheckAnnotationAdapter(super.visitParameterAnnotation(paramInt, paramString, paramBoolean));
  }
  
  public void visitAttribute(Attribute paramAttribute)
  {
    checkEndMethod();
    if (paramAttribute == null) {
      throw new IllegalArgumentException("Invalid attribute (must not be null)");
    }
    super.visitAttribute(paramAttribute);
  }
  
  public void visitCode()
  {
    if ((access & 0x400) != 0) {
      throw new RuntimeException("Abstract methods cannot have code");
    }
    startCode = true;
    super.visitCode();
  }
  
  public void visitFrame(int paramInt1, int paramInt2, Object[] paramArrayOfObject1, int paramInt3, Object[] paramArrayOfObject2)
  {
    if (insnCount == lastFrame) {
      throw new IllegalStateException("At most one frame can be visited at a given code location.");
    }
    lastFrame = insnCount;
    int i;
    int j;
    switch (paramInt1)
    {
    case -1: 
    case 0: 
      i = Integer.MAX_VALUE;
      j = Integer.MAX_VALUE;
      break;
    case 3: 
      i = 0;
      j = 0;
      break;
    case 4: 
      i = 0;
      j = 1;
      break;
    case 1: 
    case 2: 
      i = 3;
      j = 0;
      break;
    default: 
      throw new IllegalArgumentException("Invalid frame type " + paramInt1);
    }
    if (paramInt2 > i) {
      throw new IllegalArgumentException("Invalid nLocal=" + paramInt2 + " for frame type " + paramInt1);
    }
    if (paramInt3 > j) {
      throw new IllegalArgumentException("Invalid nStack=" + paramInt3 + " for frame type " + paramInt1);
    }
    if (paramInt1 != 2)
    {
      if ((paramInt2 > 0) && ((paramArrayOfObject1 == null) || (paramArrayOfObject1.length < paramInt2))) {
        throw new IllegalArgumentException("Array local[] is shorter than nLocal");
      }
      for (k = 0; k < paramInt2; k++) {
        checkFrameValue(paramArrayOfObject1[k]);
      }
    }
    if ((paramInt3 > 0) && ((paramArrayOfObject2 == null) || (paramArrayOfObject2.length < paramInt3))) {
      throw new IllegalArgumentException("Array stack[] is shorter than nStack");
    }
    for (int k = 0; k < paramInt3; k++) {
      checkFrameValue(paramArrayOfObject2[k]);
    }
    if (paramInt1 == -1) {
      expandedFrames += 1;
    } else {
      compressedFrames += 1;
    }
    if ((expandedFrames > 0) && (compressedFrames > 0)) {
      throw new RuntimeException("Expanded and compressed frames must not be mixed.");
    }
    super.visitFrame(paramInt1, paramInt2, paramArrayOfObject1, paramInt3, paramArrayOfObject2);
  }
  
  public void visitInsn(int paramInt)
  {
    checkStartCode();
    checkEndCode();
    checkOpcode(paramInt, 0);
    super.visitInsn(paramInt);
    insnCount += 1;
  }
  
  public void visitIntInsn(int paramInt1, int paramInt2)
  {
    checkStartCode();
    checkEndCode();
    checkOpcode(paramInt1, 1);
    switch (paramInt1)
    {
    case 16: 
      checkSignedByte(paramInt2, "Invalid operand");
      break;
    case 17: 
      checkSignedShort(paramInt2, "Invalid operand");
      break;
    default: 
      if ((paramInt2 < 4) || (paramInt2 > 11)) {
        throw new IllegalArgumentException("Invalid operand (must be an array type code T_...): " + paramInt2);
      }
      break;
    }
    super.visitIntInsn(paramInt1, paramInt2);
    insnCount += 1;
  }
  
  public void visitVarInsn(int paramInt1, int paramInt2)
  {
    checkStartCode();
    checkEndCode();
    checkOpcode(paramInt1, 2);
    checkUnsignedShort(paramInt2, "Invalid variable index");
    super.visitVarInsn(paramInt1, paramInt2);
    insnCount += 1;
  }
  
  public void visitTypeInsn(int paramInt, String paramString)
  {
    checkStartCode();
    checkEndCode();
    checkOpcode(paramInt, 3);
    checkInternalName(paramString, "type");
    if ((paramInt == 187) && (paramString.charAt(0) == '[')) {
      throw new IllegalArgumentException("NEW cannot be used to create arrays: " + paramString);
    }
    super.visitTypeInsn(paramInt, paramString);
    insnCount += 1;
  }
  
  public void visitFieldInsn(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    checkStartCode();
    checkEndCode();
    checkOpcode(paramInt, 4);
    checkInternalName(paramString1, "owner");
    checkUnqualifiedName(version, paramString2, "name");
    checkDesc(paramString3, false);
    super.visitFieldInsn(paramInt, paramString1, paramString2, paramString3);
    insnCount += 1;
  }
  
  @Deprecated
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    if (api >= 327680)
    {
      super.visitMethodInsn(paramInt, paramString1, paramString2, paramString3);
      return;
    }
    doVisitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramInt == 185);
  }
  
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    if (api < 327680)
    {
      super.visitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramBoolean);
      return;
    }
    doVisitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramBoolean);
  }
  
  private void doVisitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    checkStartCode();
    checkEndCode();
    checkOpcode(paramInt, 5);
    if ((paramInt != 183) || (!"<init>".equals(paramString2))) {
      checkMethodIdentifier(version, paramString2, "name");
    }
    checkInternalName(paramString1, "owner");
    checkMethodDesc(paramString3);
    if ((paramInt == 182) && (paramBoolean)) {
      throw new IllegalArgumentException("INVOKEVIRTUAL can't be used with interfaces");
    }
    if ((paramInt == 185) && (!paramBoolean)) {
      throw new IllegalArgumentException("INVOKEINTERFACE can't be used with classes");
    }
    if (mv != null) {
      mv.visitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramBoolean);
    }
    insnCount += 1;
  }
  
  public void visitInvokeDynamicInsn(String paramString1, String paramString2, Handle paramHandle, Object... paramVarArgs)
  {
    checkStartCode();
    checkEndCode();
    checkMethodIdentifier(version, paramString1, "name");
    checkMethodDesc(paramString2);
    if ((paramHandle.getTag() != 6) && (paramHandle.getTag() != 8)) {
      throw new IllegalArgumentException("invalid handle tag " + paramHandle.getTag());
    }
    for (int i = 0; i < paramVarArgs.length; i++) {
      checkLDCConstant(paramVarArgs[i]);
    }
    super.visitInvokeDynamicInsn(paramString1, paramString2, paramHandle, paramVarArgs);
    insnCount += 1;
  }
  
  public void visitJumpInsn(int paramInt, Label paramLabel)
  {
    checkStartCode();
    checkEndCode();
    checkOpcode(paramInt, 6);
    checkLabel(paramLabel, false, "label");
    checkNonDebugLabel(paramLabel);
    super.visitJumpInsn(paramInt, paramLabel);
    usedLabels.add(paramLabel);
    insnCount += 1;
  }
  
  public void visitLabel(Label paramLabel)
  {
    checkStartCode();
    checkEndCode();
    checkLabel(paramLabel, false, "label");
    if (labels.get(paramLabel) != null) {
      throw new IllegalArgumentException("Already visited label");
    }
    labels.put(paramLabel, Integer.valueOf(insnCount));
    super.visitLabel(paramLabel);
  }
  
  public void visitLdcInsn(Object paramObject)
  {
    checkStartCode();
    checkEndCode();
    checkLDCConstant(paramObject);
    super.visitLdcInsn(paramObject);
    insnCount += 1;
  }
  
  public void visitIincInsn(int paramInt1, int paramInt2)
  {
    checkStartCode();
    checkEndCode();
    checkUnsignedShort(paramInt1, "Invalid variable index");
    checkSignedShort(paramInt2, "Invalid increment");
    super.visitIincInsn(paramInt1, paramInt2);
    insnCount += 1;
  }
  
  public void visitTableSwitchInsn(int paramInt1, int paramInt2, Label paramLabel, Label... paramVarArgs)
  {
    checkStartCode();
    checkEndCode();
    if (paramInt2 < paramInt1) {
      throw new IllegalArgumentException("Max = " + paramInt2 + " must be greater than or equal to min = " + paramInt1);
    }
    checkLabel(paramLabel, false, "default label");
    checkNonDebugLabel(paramLabel);
    if ((paramVarArgs == null) || (paramVarArgs.length != paramInt2 - paramInt1 + 1)) {
      throw new IllegalArgumentException("There must be max - min + 1 labels");
    }
    for (int i = 0; i < paramVarArgs.length; i++)
    {
      checkLabel(paramVarArgs[i], false, "label at index " + i);
      checkNonDebugLabel(paramVarArgs[i]);
    }
    super.visitTableSwitchInsn(paramInt1, paramInt2, paramLabel, paramVarArgs);
    for (i = 0; i < paramVarArgs.length; i++) {
      usedLabels.add(paramVarArgs[i]);
    }
    insnCount += 1;
  }
  
  public void visitLookupSwitchInsn(Label paramLabel, int[] paramArrayOfInt, Label[] paramArrayOfLabel)
  {
    checkEndCode();
    checkStartCode();
    checkLabel(paramLabel, false, "default label");
    checkNonDebugLabel(paramLabel);
    if ((paramArrayOfInt == null) || (paramArrayOfLabel == null) || (paramArrayOfInt.length != paramArrayOfLabel.length)) {
      throw new IllegalArgumentException("There must be the same number of keys and labels");
    }
    for (int i = 0; i < paramArrayOfLabel.length; i++)
    {
      checkLabel(paramArrayOfLabel[i], false, "label at index " + i);
      checkNonDebugLabel(paramArrayOfLabel[i]);
    }
    super.visitLookupSwitchInsn(paramLabel, paramArrayOfInt, paramArrayOfLabel);
    usedLabels.add(paramLabel);
    for (i = 0; i < paramArrayOfLabel.length; i++) {
      usedLabels.add(paramArrayOfLabel[i]);
    }
    insnCount += 1;
  }
  
  public void visitMultiANewArrayInsn(String paramString, int paramInt)
  {
    checkStartCode();
    checkEndCode();
    checkDesc(paramString, false);
    if (paramString.charAt(0) != '[') {
      throw new IllegalArgumentException("Invalid descriptor (must be an array type descriptor): " + paramString);
    }
    if (paramInt < 1) {
      throw new IllegalArgumentException("Invalid dimensions (must be greater than 0): " + paramInt);
    }
    if (paramInt > paramString.lastIndexOf('[') + 1) {
      throw new IllegalArgumentException("Invalid dimensions (must not be greater than dims(desc)): " + paramInt);
    }
    super.visitMultiANewArrayInsn(paramString, paramInt);
    insnCount += 1;
  }
  
  public AnnotationVisitor visitInsnAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    checkStartCode();
    checkEndCode();
    int i = paramInt >>> 24;
    if ((i != 67) && (i != 68) && (i != 69) && (i != 70) && (i != 71) && (i != 72) && (i != 73) && (i != 74) && (i != 75)) {
      throw new IllegalArgumentException("Invalid type reference sort 0x" + Integer.toHexString(i));
    }
    CheckClassAdapter.checkTypeRefAndPath(paramInt, paramTypePath);
    checkDesc(paramString, false);
    return new CheckAnnotationAdapter(super.visitInsnAnnotation(paramInt, paramTypePath, paramString, paramBoolean));
  }
  
  public void visitTryCatchBlock(Label paramLabel1, Label paramLabel2, Label paramLabel3, String paramString)
  {
    checkStartCode();
    checkEndCode();
    checkLabel(paramLabel1, false, "start label");
    checkLabel(paramLabel2, false, "end label");
    checkLabel(paramLabel3, false, "handler label");
    checkNonDebugLabel(paramLabel1);
    checkNonDebugLabel(paramLabel2);
    checkNonDebugLabel(paramLabel3);
    if ((labels.get(paramLabel1) != null) || (labels.get(paramLabel2) != null) || (labels.get(paramLabel3) != null)) {
      throw new IllegalStateException("Try catch blocks must be visited before their labels");
    }
    if (paramString != null) {
      checkInternalName(paramString, "type");
    }
    super.visitTryCatchBlock(paramLabel1, paramLabel2, paramLabel3, paramString);
    handlers.add(paramLabel1);
    handlers.add(paramLabel2);
  }
  
  public AnnotationVisitor visitTryCatchAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    checkStartCode();
    checkEndCode();
    int i = paramInt >>> 24;
    if (i != 66) {
      throw new IllegalArgumentException("Invalid type reference sort 0x" + Integer.toHexString(i));
    }
    CheckClassAdapter.checkTypeRefAndPath(paramInt, paramTypePath);
    checkDesc(paramString, false);
    return new CheckAnnotationAdapter(super.visitTryCatchAnnotation(paramInt, paramTypePath, paramString, paramBoolean));
  }
  
  public void visitLocalVariable(String paramString1, String paramString2, String paramString3, Label paramLabel1, Label paramLabel2, int paramInt)
  {
    checkStartCode();
    checkEndCode();
    checkUnqualifiedName(version, paramString1, "name");
    checkDesc(paramString2, false);
    checkLabel(paramLabel1, true, "start label");
    checkLabel(paramLabel2, true, "end label");
    checkUnsignedShort(paramInt, "Invalid variable index");
    int i = ((Integer)labels.get(paramLabel1)).intValue();
    int j = ((Integer)labels.get(paramLabel2)).intValue();
    if (j < i) {
      throw new IllegalArgumentException("Invalid start and end labels (end must be greater than start)");
    }
    super.visitLocalVariable(paramString1, paramString2, paramString3, paramLabel1, paramLabel2, paramInt);
  }
  
  public AnnotationVisitor visitLocalVariableAnnotation(int paramInt, TypePath paramTypePath, Label[] paramArrayOfLabel1, Label[] paramArrayOfLabel2, int[] paramArrayOfInt, String paramString, boolean paramBoolean)
  {
    checkStartCode();
    checkEndCode();
    int i = paramInt >>> 24;
    if ((i != 64) && (i != 65)) {
      throw new IllegalArgumentException("Invalid type reference sort 0x" + Integer.toHexString(i));
    }
    CheckClassAdapter.checkTypeRefAndPath(paramInt, paramTypePath);
    checkDesc(paramString, false);
    if ((paramArrayOfLabel1 == null) || (paramArrayOfLabel2 == null) || (paramArrayOfInt == null) || (paramArrayOfLabel2.length != paramArrayOfLabel1.length) || (paramArrayOfInt.length != paramArrayOfLabel1.length)) {
      throw new IllegalArgumentException("Invalid start, end and index arrays (must be non null and of identical length");
    }
    for (int j = 0; j < paramArrayOfLabel1.length; j++)
    {
      checkLabel(paramArrayOfLabel1[j], true, "start label");
      checkLabel(paramArrayOfLabel2[j], true, "end label");
      checkUnsignedShort(paramArrayOfInt[j], "Invalid variable index");
      int k = ((Integer)labels.get(paramArrayOfLabel1[j])).intValue();
      int m = ((Integer)labels.get(paramArrayOfLabel2[j])).intValue();
      if (m < k) {
        throw new IllegalArgumentException("Invalid start and end labels (end must be greater than start)");
      }
    }
    return super.visitLocalVariableAnnotation(paramInt, paramTypePath, paramArrayOfLabel1, paramArrayOfLabel2, paramArrayOfInt, paramString, paramBoolean);
  }
  
  public void visitLineNumber(int paramInt, Label paramLabel)
  {
    checkStartCode();
    checkEndCode();
    checkUnsignedShort(paramInt, "Invalid line number");
    checkLabel(paramLabel, true, "start label");
    super.visitLineNumber(paramInt, paramLabel);
  }
  
  public void visitMaxs(int paramInt1, int paramInt2)
  {
    checkStartCode();
    checkEndCode();
    endCode = true;
    Iterator localIterator = usedLabels.iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (Label)localIterator.next();
      if (labels.get(localObject) == null) {
        throw new IllegalStateException("Undefined label used");
      }
    }
    int i = 0;
    while (i < handlers.size())
    {
      localObject = (Integer)labels.get(handlers.get(i++));
      Integer localInteger = (Integer)labels.get(handlers.get(i++));
      if ((localObject == null) || (localInteger == null)) {
        throw new IllegalStateException("Undefined try catch block labels");
      }
      if (localInteger.intValue() <= ((Integer)localObject).intValue()) {
        throw new IllegalStateException("Emty try catch block handler range");
      }
    }
    checkUnsignedShort(paramInt1, "Invalid max stack");
    checkUnsignedShort(paramInt2, "Invalid max locals");
    super.visitMaxs(paramInt1, paramInt2);
  }
  
  public void visitEnd()
  {
    checkEndMethod();
    endMethod = true;
    super.visitEnd();
  }
  
  void checkStartCode()
  {
    if (!startCode) {
      throw new IllegalStateException("Cannot visit instructions before visitCode has been called.");
    }
  }
  
  void checkEndCode()
  {
    if (endCode) {
      throw new IllegalStateException("Cannot visit instructions after visitMaxs has been called.");
    }
  }
  
  void checkEndMethod()
  {
    if (endMethod) {
      throw new IllegalStateException("Cannot visit elements after visitEnd has been called.");
    }
  }
  
  void checkFrameValue(Object paramObject)
  {
    if ((paramObject == Opcodes.TOP) || (paramObject == Opcodes.INTEGER) || (paramObject == Opcodes.FLOAT) || (paramObject == Opcodes.LONG) || (paramObject == Opcodes.DOUBLE) || (paramObject == Opcodes.NULL) || (paramObject == Opcodes.UNINITIALIZED_THIS)) {
      return;
    }
    if ((paramObject instanceof String))
    {
      checkInternalName((String)paramObject, "Invalid stack frame value");
      return;
    }
    if (!(paramObject instanceof Label)) {
      throw new IllegalArgumentException("Invalid stack frame value: " + paramObject);
    }
    usedLabels.add((Label)paramObject);
  }
  
  static void checkOpcode(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 > 199) || (TYPE[paramInt1] != paramInt2)) {
      throw new IllegalArgumentException("Invalid opcode: " + paramInt1);
    }
  }
  
  static void checkSignedByte(int paramInt, String paramString)
  {
    if ((paramInt < -128) || (paramInt > 127)) {
      throw new IllegalArgumentException(paramString + " (must be a signed byte): " + paramInt);
    }
  }
  
  static void checkSignedShort(int paramInt, String paramString)
  {
    if ((paramInt < 32768) || (paramInt > 32767)) {
      throw new IllegalArgumentException(paramString + " (must be a signed short): " + paramInt);
    }
  }
  
  static void checkUnsignedShort(int paramInt, String paramString)
  {
    if ((paramInt < 0) || (paramInt > 65535)) {
      throw new IllegalArgumentException(paramString + " (must be an unsigned short): " + paramInt);
    }
  }
  
  static void checkConstant(Object paramObject)
  {
    if ((!(paramObject instanceof Integer)) && (!(paramObject instanceof Float)) && (!(paramObject instanceof Long)) && (!(paramObject instanceof Double)) && (!(paramObject instanceof String))) {
      throw new IllegalArgumentException("Invalid constant: " + paramObject);
    }
  }
  
  void checkLDCConstant(Object paramObject)
  {
    int i;
    if ((paramObject instanceof Type))
    {
      i = ((Type)paramObject).getSort();
      if ((i != 10) && (i != 9) && (i != 11)) {
        throw new IllegalArgumentException("Illegal LDC constant value");
      }
      if ((i != 11) && ((version & 0xFFFF) < 49)) {
        throw new IllegalArgumentException("ldc of a constant class requires at least version 1.5");
      }
      if ((i == 11) && ((version & 0xFFFF) < 51)) {
        throw new IllegalArgumentException("ldc of a method type requires at least version 1.7");
      }
    }
    else if ((paramObject instanceof Handle))
    {
      if ((version & 0xFFFF) < 51) {
        throw new IllegalArgumentException("ldc of a handle requires at least version 1.7");
      }
      i = ((Handle)paramObject).getTag();
      if ((i < 1) || (i > 9)) {
        throw new IllegalArgumentException("invalid handle tag " + i);
      }
    }
    else
    {
      checkConstant(paramObject);
    }
  }
  
  static void checkUnqualifiedName(int paramInt, String paramString1, String paramString2)
  {
    if ((paramInt & 0xFFFF) < 49) {
      checkIdentifier(paramString1, paramString2);
    } else {
      for (int i = 0; i < paramString1.length(); i++) {
        if (".;[/".indexOf(paramString1.charAt(i)) != -1) {
          throw new IllegalArgumentException("Invalid " + paramString2 + " (must be a valid unqualified name): " + paramString1);
        }
      }
    }
  }
  
  static void checkIdentifier(String paramString1, String paramString2)
  {
    checkIdentifier(paramString1, 0, -1, paramString2);
  }
  
  static void checkIdentifier(String paramString1, int paramInt1, int paramInt2, String paramString2)
  {
    if ((paramString1 == null) || (paramInt2 == -1 ? paramString1.length() <= paramInt1 : paramInt2 <= paramInt1)) {
      throw new IllegalArgumentException("Invalid " + paramString2 + " (must not be null or empty)");
    }
    if (!Character.isJavaIdentifierStart(paramString1.charAt(paramInt1))) {
      throw new IllegalArgumentException("Invalid " + paramString2 + " (must be a valid Java identifier): " + paramString1);
    }
    int i = paramInt2 == -1 ? paramString1.length() : paramInt2;
    for (int j = paramInt1 + 1; j < i; j++) {
      if (!Character.isJavaIdentifierPart(paramString1.charAt(j))) {
        throw new IllegalArgumentException("Invalid " + paramString2 + " (must be a valid Java identifier): " + paramString1);
      }
    }
  }
  
  static void checkMethodIdentifier(int paramInt, String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString1.length() == 0)) {
      throw new IllegalArgumentException("Invalid " + paramString2 + " (must not be null or empty)");
    }
    if ((paramInt & 0xFFFF) >= 49)
    {
      for (i = 0; i < paramString1.length(); i++) {
        if (".;[/<>".indexOf(paramString1.charAt(i)) != -1) {
          throw new IllegalArgumentException("Invalid " + paramString2 + " (must be a valid unqualified name): " + paramString1);
        }
      }
      return;
    }
    if (!Character.isJavaIdentifierStart(paramString1.charAt(0))) {
      throw new IllegalArgumentException("Invalid " + paramString2 + " (must be a '<init>', '<clinit>' or a valid Java identifier): " + paramString1);
    }
    for (int i = 1; i < paramString1.length(); i++) {
      if (!Character.isJavaIdentifierPart(paramString1.charAt(i))) {
        throw new IllegalArgumentException("Invalid " + paramString2 + " (must be '<init>' or '<clinit>' or a valid Java identifier): " + paramString1);
      }
    }
  }
  
  static void checkInternalName(String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString1.length() == 0)) {
      throw new IllegalArgumentException("Invalid " + paramString2 + " (must not be null or empty)");
    }
    if (paramString1.charAt(0) == '[') {
      checkDesc(paramString1, false);
    } else {
      checkInternalName(paramString1, 0, -1, paramString2);
    }
  }
  
  static void checkInternalName(String paramString1, int paramInt1, int paramInt2, String paramString2)
  {
    int i = paramInt2 == -1 ? paramString1.length() : paramInt2;
    try
    {
      int j = paramInt1;
      int k;
      do
      {
        k = paramString1.indexOf('/', j + 1);
        if ((k == -1) || (k > i)) {
          k = i;
        }
        checkIdentifier(paramString1, j, k, null);
        j = k + 1;
      } while (k != i);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new IllegalArgumentException("Invalid " + paramString2 + " (must be a fully qualified class name in internal form): " + paramString1);
    }
  }
  
  static void checkDesc(String paramString, boolean paramBoolean)
  {
    int i = checkDesc(paramString, 0, paramBoolean);
    if (i != paramString.length()) {
      throw new IllegalArgumentException("Invalid descriptor: " + paramString);
    }
  }
  
  static int checkDesc(String paramString, int paramInt, boolean paramBoolean)
  {
    if ((paramString == null) || (paramInt >= paramString.length())) {
      throw new IllegalArgumentException("Invalid type descriptor (must not be null or empty)");
    }
    int i;
    switch (paramString.charAt(paramInt))
    {
    case 'V': 
      if (paramBoolean) {
        return paramInt + 1;
      }
      throw new IllegalArgumentException("Invalid descriptor: " + paramString);
    case 'B': 
    case 'C': 
    case 'D': 
    case 'F': 
    case 'I': 
    case 'J': 
    case 'S': 
    case 'Z': 
      return paramInt + 1;
    case '[': 
      for (i = paramInt + 1; (i < paramString.length()) && (paramString.charAt(i) == '['); i++) {}
      if (i < paramString.length()) {
        return checkDesc(paramString, i, false);
      }
      throw new IllegalArgumentException("Invalid descriptor: " + paramString);
    case 'L': 
      i = paramString.indexOf(';', paramInt);
      if ((i == -1) || (i - paramInt < 2)) {
        throw new IllegalArgumentException("Invalid descriptor: " + paramString);
      }
      try
      {
        checkInternalName(paramString, paramInt + 1, i, null);
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        throw new IllegalArgumentException("Invalid descriptor: " + paramString);
      }
      return i + 1;
    }
    throw new IllegalArgumentException("Invalid descriptor: " + paramString);
  }
  
  static void checkMethodDesc(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      throw new IllegalArgumentException("Invalid method descriptor (must not be null or empty)");
    }
    if ((paramString.charAt(0) != '(') || (paramString.length() < 3)) {
      throw new IllegalArgumentException("Invalid descriptor: " + paramString);
    }
    int i = 1;
    if (paramString.charAt(i) != ')') {
      do
      {
        if (paramString.charAt(i) == 'V') {
          throw new IllegalArgumentException("Invalid descriptor: " + paramString);
        }
        i = checkDesc(paramString, i, false);
      } while ((i < paramString.length()) && (paramString.charAt(i) != ')'));
    }
    i = checkDesc(paramString, i + 1, true);
    if (i != paramString.length()) {
      throw new IllegalArgumentException("Invalid descriptor: " + paramString);
    }
  }
  
  void checkLabel(Label paramLabel, boolean paramBoolean, String paramString)
  {
    if (paramLabel == null) {
      throw new IllegalArgumentException("Invalid " + paramString + " (must not be null)");
    }
    if ((paramBoolean) && (labels.get(paramLabel) == null)) {
      throw new IllegalArgumentException("Invalid " + paramString + " (must be visited first)");
    }
  }
  
  private static void checkNonDebugLabel(Label paramLabel)
  {
    Field localField = getLabelStatusField();
    int i = 0;
    try
    {
      i = localField == null ? 0 : ((Integer)localField.get(paramLabel)).intValue();
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new Error("Internal error");
    }
    if ((i & 0x1) != 0) {
      throw new IllegalArgumentException("Labels used for debug info cannot be reused for control flow");
    }
  }
  
  private static Field getLabelStatusField()
  {
    if (labelStatusField == null)
    {
      labelStatusField = getLabelField("a");
      if (labelStatusField == null) {
        labelStatusField = getLabelField("status");
      }
    }
    return labelStatusField;
  }
  
  private static Field getLabelField(String paramString)
  {
    try
    {
      Field localField = Label.class.getDeclaredField(paramString);
      localField.setAccessible(true);
      return localField;
    }
    catch (NoSuchFieldException localNoSuchFieldException) {}
    return null;
  }
  
  static
  {
    String str = "BBBBBBBBBBBBBBBBCCIAADDDDDAAAAAAAAAAAAAAAAAAAABBBBBBBBDDDDDAAAAAAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBJBBBBBBBBBBBBBBBBBBBBHHHHHHHHHHHHHHHHDKLBBBBBBFFFFGGGGAECEBBEEBBAMHHAA";
    TYPE = new int[str.length()];
    for (int i = 0; i < TYPE.length; i++) {
      TYPE[i] = (str.charAt(i) - 'A' - 1);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\util\CheckMethodAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */