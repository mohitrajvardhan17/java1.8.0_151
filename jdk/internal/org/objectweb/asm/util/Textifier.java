package jdk.internal.org.objectweb.asm.util;

import java.io.FileInputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jdk.internal.org.objectweb.asm.Attribute;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.Handle;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.TypePath;
import jdk.internal.org.objectweb.asm.TypeReference;
import jdk.internal.org.objectweb.asm.signature.SignatureReader;
import jdk.internal.org.objectweb.asm.signature.SignatureVisitor;

public class Textifier
  extends Printer
{
  public static final int INTERNAL_NAME = 0;
  public static final int FIELD_DESCRIPTOR = 1;
  public static final int FIELD_SIGNATURE = 2;
  public static final int METHOD_DESCRIPTOR = 3;
  public static final int METHOD_SIGNATURE = 4;
  public static final int CLASS_SIGNATURE = 5;
  public static final int TYPE_DECLARATION = 6;
  public static final int CLASS_DECLARATION = 7;
  public static final int PARAMETERS_DECLARATION = 8;
  public static final int HANDLE_DESCRIPTOR = 9;
  protected String tab = "  ";
  protected String tab2 = "    ";
  protected String tab3 = "      ";
  protected String ltab = "   ";
  protected Map<Label, String> labelNames;
  private int access;
  private int valueNumber = 0;
  
  public Textifier()
  {
    this(327680);
    if (getClass() != Textifier.class) {
      throw new IllegalStateException();
    }
  }
  
  protected Textifier(int paramInt)
  {
    super(paramInt);
  }
  
  public static void main(String[] paramArrayOfString)
    throws Exception
  {
    int i = 0;
    int j = 2;
    int k = 1;
    if ((paramArrayOfString.length < 1) || (paramArrayOfString.length > 2)) {
      k = 0;
    }
    if ((k != 0) && ("-debug".equals(paramArrayOfString[0])))
    {
      i = 1;
      j = 0;
      if (paramArrayOfString.length != 2) {
        k = 0;
      }
    }
    if (k == 0)
    {
      System.err.println("Prints a disassembled view of the given class.");
      System.err.println("Usage: Textifier [-debug] <fully qualified class name or class file name>");
      return;
    }
    ClassReader localClassReader;
    if ((paramArrayOfString[i].endsWith(".class")) || (paramArrayOfString[i].indexOf('\\') > -1) || (paramArrayOfString[i].indexOf('/') > -1)) {
      localClassReader = new ClassReader(new FileInputStream(paramArrayOfString[i]));
    } else {
      localClassReader = new ClassReader(paramArrayOfString[i]);
    }
    localClassReader.accept(new TraceClassVisitor(new PrintWriter(System.out)), j);
  }
  
  public void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    access = paramInt2;
    int i = paramInt1 & 0xFFFF;
    int j = paramInt1 >>> 16;
    buf.setLength(0);
    buf.append("// class version ").append(i).append('.').append(j).append(" (").append(paramInt1).append(")\n");
    if ((paramInt2 & 0x20000) != 0) {
      buf.append("// DEPRECATED\n");
    }
    buf.append("// access flags 0x").append(Integer.toHexString(paramInt2).toUpperCase()).append('\n');
    appendDescriptor(5, paramString2);
    if (paramString2 != null)
    {
      TraceSignatureVisitor localTraceSignatureVisitor = new TraceSignatureVisitor(paramInt2);
      SignatureReader localSignatureReader = new SignatureReader(paramString2);
      localSignatureReader.accept(localTraceSignatureVisitor);
      buf.append("// declaration: ").append(paramString1).append(localTraceSignatureVisitor.getDeclaration()).append('\n');
    }
    appendAccess(paramInt2 & 0xFFFFFFDF);
    if ((paramInt2 & 0x2000) != 0) {
      buf.append("@interface ");
    } else if ((paramInt2 & 0x200) != 0) {
      buf.append("interface ");
    } else if ((paramInt2 & 0x4000) == 0) {
      buf.append("class ");
    }
    appendDescriptor(0, paramString1);
    if ((paramString3 != null) && (!"java/lang/Object".equals(paramString3)))
    {
      buf.append(" extends ");
      appendDescriptor(0, paramString3);
      buf.append(' ');
    }
    if ((paramArrayOfString != null) && (paramArrayOfString.length > 0))
    {
      buf.append(" implements ");
      for (int k = 0; k < paramArrayOfString.length; k++)
      {
        appendDescriptor(0, paramArrayOfString[k]);
        buf.append(' ');
      }
    }
    buf.append(" {\n\n");
    text.add(buf.toString());
  }
  
  public void visitSource(String paramString1, String paramString2)
  {
    buf.setLength(0);
    if (paramString1 != null) {
      buf.append(tab).append("// compiled from: ").append(paramString1).append('\n');
    }
    if (paramString2 != null) {
      buf.append(tab).append("// debug info: ").append(paramString2).append('\n');
    }
    if (buf.length() > 0) {
      text.add(buf.toString());
    }
  }
  
  public void visitOuterClass(String paramString1, String paramString2, String paramString3)
  {
    buf.setLength(0);
    buf.append(tab).append("OUTERCLASS ");
    appendDescriptor(0, paramString1);
    buf.append(' ');
    if (paramString2 != null) {
      buf.append(paramString2).append(' ');
    }
    appendDescriptor(3, paramString3);
    buf.append('\n');
    text.add(buf.toString());
  }
  
  public Textifier visitClassAnnotation(String paramString, boolean paramBoolean)
  {
    text.add("\n");
    return visitAnnotation(paramString, paramBoolean);
  }
  
  public Printer visitClassTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    text.add("\n");
    return visitTypeAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
  }
  
  public void visitClassAttribute(Attribute paramAttribute)
  {
    text.add("\n");
    visitAttribute(paramAttribute);
  }
  
  public void visitInnerClass(String paramString1, String paramString2, String paramString3, int paramInt)
  {
    buf.setLength(0);
    buf.append(tab).append("// access flags 0x");
    buf.append(Integer.toHexString(paramInt & 0xFFFFFFDF).toUpperCase()).append('\n');
    buf.append(tab);
    appendAccess(paramInt);
    buf.append("INNERCLASS ");
    appendDescriptor(0, paramString1);
    buf.append(' ');
    appendDescriptor(0, paramString2);
    buf.append(' ');
    appendDescriptor(0, paramString3);
    buf.append('\n');
    text.add(buf.toString());
  }
  
  public Textifier visitField(int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject)
  {
    buf.setLength(0);
    buf.append('\n');
    if ((paramInt & 0x20000) != 0) {
      buf.append(tab).append("// DEPRECATED\n");
    }
    buf.append(tab).append("// access flags 0x").append(Integer.toHexString(paramInt).toUpperCase()).append('\n');
    if (paramString3 != null)
    {
      buf.append(tab);
      appendDescriptor(2, paramString3);
      localObject = new TraceSignatureVisitor(0);
      SignatureReader localSignatureReader = new SignatureReader(paramString3);
      localSignatureReader.acceptType((SignatureVisitor)localObject);
      buf.append(tab).append("// declaration: ").append(((TraceSignatureVisitor)localObject).getDeclaration()).append('\n');
    }
    buf.append(tab);
    appendAccess(paramInt);
    appendDescriptor(1, paramString2);
    buf.append(' ').append(paramString1);
    if (paramObject != null)
    {
      buf.append(" = ");
      if ((paramObject instanceof String)) {
        buf.append('"').append(paramObject).append('"');
      } else {
        buf.append(paramObject);
      }
    }
    buf.append('\n');
    text.add(buf.toString());
    Object localObject = createTextifier();
    text.add(((Textifier)localObject).getText());
    return (Textifier)localObject;
  }
  
  public Textifier visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    buf.setLength(0);
    buf.append('\n');
    if ((paramInt & 0x20000) != 0) {
      buf.append(tab).append("// DEPRECATED\n");
    }
    buf.append(tab).append("// access flags 0x").append(Integer.toHexString(paramInt).toUpperCase()).append('\n');
    if (paramString3 != null)
    {
      buf.append(tab);
      appendDescriptor(4, paramString3);
      TraceSignatureVisitor localTraceSignatureVisitor = new TraceSignatureVisitor(0);
      SignatureReader localSignatureReader = new SignatureReader(paramString3);
      localSignatureReader.accept(localTraceSignatureVisitor);
      String str1 = localTraceSignatureVisitor.getDeclaration();
      String str2 = localTraceSignatureVisitor.getReturnType();
      String str3 = localTraceSignatureVisitor.getExceptions();
      buf.append(tab).append("// declaration: ").append(str2).append(' ').append(paramString1).append(str1);
      if (str3 != null) {
        buf.append(" throws ").append(str3);
      }
      buf.append('\n');
    }
    buf.append(tab);
    appendAccess(paramInt & 0xFFFFFFBF);
    if ((paramInt & 0x100) != 0) {
      buf.append("native ");
    }
    if ((paramInt & 0x80) != 0) {
      buf.append("varargs ");
    }
    if ((paramInt & 0x40) != 0) {
      buf.append("bridge ");
    }
    if (((access & 0x200) != 0) && ((paramInt & 0x400) == 0) && ((paramInt & 0x8) == 0)) {
      buf.append("default ");
    }
    buf.append(paramString1);
    appendDescriptor(3, paramString2);
    if ((paramArrayOfString != null) && (paramArrayOfString.length > 0))
    {
      buf.append(" throws ");
      for (int i = 0; i < paramArrayOfString.length; i++)
      {
        appendDescriptor(0, paramArrayOfString[i]);
        buf.append(' ');
      }
    }
    buf.append('\n');
    text.add(buf.toString());
    Textifier localTextifier = createTextifier();
    text.add(localTextifier.getText());
    return localTextifier;
  }
  
  public void visitClassEnd()
  {
    text.add("}\n");
  }
  
  public void visit(String paramString, Object paramObject)
  {
    buf.setLength(0);
    appendComa(valueNumber++);
    if (paramString != null) {
      buf.append(paramString).append('=');
    }
    if ((paramObject instanceof String))
    {
      visitString((String)paramObject);
    }
    else if ((paramObject instanceof Type))
    {
      visitType((Type)paramObject);
    }
    else if ((paramObject instanceof Byte))
    {
      visitByte(((Byte)paramObject).byteValue());
    }
    else if ((paramObject instanceof Boolean))
    {
      visitBoolean(((Boolean)paramObject).booleanValue());
    }
    else if ((paramObject instanceof Short))
    {
      visitShort(((Short)paramObject).shortValue());
    }
    else if ((paramObject instanceof Character))
    {
      visitChar(((Character)paramObject).charValue());
    }
    else if ((paramObject instanceof Integer))
    {
      visitInt(((Integer)paramObject).intValue());
    }
    else if ((paramObject instanceof Float))
    {
      visitFloat(((Float)paramObject).floatValue());
    }
    else if ((paramObject instanceof Long))
    {
      visitLong(((Long)paramObject).longValue());
    }
    else if ((paramObject instanceof Double))
    {
      visitDouble(((Double)paramObject).doubleValue());
    }
    else if (paramObject.getClass().isArray())
    {
      buf.append('{');
      Object localObject;
      int i;
      if ((paramObject instanceof byte[]))
      {
        localObject = (byte[])paramObject;
        for (i = 0; i < localObject.length; i++)
        {
          appendComa(i);
          visitByte(localObject[i]);
        }
      }
      else if ((paramObject instanceof boolean[]))
      {
        localObject = (boolean[])paramObject;
        for (i = 0; i < localObject.length; i++)
        {
          appendComa(i);
          visitBoolean(localObject[i]);
        }
      }
      else if ((paramObject instanceof short[]))
      {
        localObject = (short[])paramObject;
        for (i = 0; i < localObject.length; i++)
        {
          appendComa(i);
          visitShort(localObject[i]);
        }
      }
      else if ((paramObject instanceof char[]))
      {
        localObject = (char[])paramObject;
        for (i = 0; i < localObject.length; i++)
        {
          appendComa(i);
          visitChar(localObject[i]);
        }
      }
      else if ((paramObject instanceof int[]))
      {
        localObject = (int[])paramObject;
        for (i = 0; i < localObject.length; i++)
        {
          appendComa(i);
          visitInt(localObject[i]);
        }
      }
      else if ((paramObject instanceof long[]))
      {
        localObject = (long[])paramObject;
        for (i = 0; i < localObject.length; i++)
        {
          appendComa(i);
          visitLong(localObject[i]);
        }
      }
      else if ((paramObject instanceof float[]))
      {
        localObject = (float[])paramObject;
        for (i = 0; i < localObject.length; i++)
        {
          appendComa(i);
          visitFloat(localObject[i]);
        }
      }
      else if ((paramObject instanceof double[]))
      {
        localObject = (double[])paramObject;
        for (i = 0; i < localObject.length; i++)
        {
          appendComa(i);
          visitDouble(localObject[i]);
        }
      }
      buf.append('}');
    }
    text.add(buf.toString());
  }
  
  private void visitInt(int paramInt)
  {
    buf.append(paramInt);
  }
  
  private void visitLong(long paramLong)
  {
    buf.append(paramLong).append('L');
  }
  
  private void visitFloat(float paramFloat)
  {
    buf.append(paramFloat).append('F');
  }
  
  private void visitDouble(double paramDouble)
  {
    buf.append(paramDouble).append('D');
  }
  
  private void visitChar(char paramChar)
  {
    buf.append("(char)").append(paramChar);
  }
  
  private void visitShort(short paramShort)
  {
    buf.append("(short)").append(paramShort);
  }
  
  private void visitByte(byte paramByte)
  {
    buf.append("(byte)").append(paramByte);
  }
  
  private void visitBoolean(boolean paramBoolean)
  {
    buf.append(paramBoolean);
  }
  
  private void visitString(String paramString)
  {
    appendString(buf, paramString);
  }
  
  private void visitType(Type paramType)
  {
    buf.append(paramType.getClassName()).append(".class");
  }
  
  public void visitEnum(String paramString1, String paramString2, String paramString3)
  {
    buf.setLength(0);
    appendComa(valueNumber++);
    if (paramString1 != null) {
      buf.append(paramString1).append('=');
    }
    appendDescriptor(1, paramString2);
    buf.append('.').append(paramString3);
    text.add(buf.toString());
  }
  
  public Textifier visitAnnotation(String paramString1, String paramString2)
  {
    buf.setLength(0);
    appendComa(valueNumber++);
    if (paramString1 != null) {
      buf.append(paramString1).append('=');
    }
    buf.append('@');
    appendDescriptor(1, paramString2);
    buf.append('(');
    text.add(buf.toString());
    Textifier localTextifier = createTextifier();
    text.add(localTextifier.getText());
    text.add(")");
    return localTextifier;
  }
  
  public Textifier visitArray(String paramString)
  {
    buf.setLength(0);
    appendComa(valueNumber++);
    if (paramString != null) {
      buf.append(paramString).append('=');
    }
    buf.append('{');
    text.add(buf.toString());
    Textifier localTextifier = createTextifier();
    text.add(localTextifier.getText());
    text.add("}");
    return localTextifier;
  }
  
  public void visitAnnotationEnd() {}
  
  public Textifier visitFieldAnnotation(String paramString, boolean paramBoolean)
  {
    return visitAnnotation(paramString, paramBoolean);
  }
  
  public Printer visitFieldTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    return visitTypeAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
  }
  
  public void visitFieldAttribute(Attribute paramAttribute)
  {
    visitAttribute(paramAttribute);
  }
  
  public void visitFieldEnd() {}
  
  public void visitParameter(String paramString, int paramInt)
  {
    buf.setLength(0);
    buf.append(tab2).append("// parameter ");
    appendAccess(paramInt);
    buf.append(' ').append(paramString == null ? "<no name>" : paramString).append('\n');
    text.add(buf.toString());
  }
  
  public Textifier visitAnnotationDefault()
  {
    text.add(tab2 + "default=");
    Textifier localTextifier = createTextifier();
    text.add(localTextifier.getText());
    text.add("\n");
    return localTextifier;
  }
  
  public Textifier visitMethodAnnotation(String paramString, boolean paramBoolean)
  {
    return visitAnnotation(paramString, paramBoolean);
  }
  
  public Printer visitMethodTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    return visitTypeAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
  }
  
  public Textifier visitParameterAnnotation(int paramInt, String paramString, boolean paramBoolean)
  {
    buf.setLength(0);
    buf.append(tab2).append('@');
    appendDescriptor(1, paramString);
    buf.append('(');
    text.add(buf.toString());
    Textifier localTextifier = createTextifier();
    text.add(localTextifier.getText());
    text.add(paramBoolean ? ") // parameter " : ") // invisible, parameter ");
    text.add(Integer.valueOf(paramInt));
    text.add("\n");
    return localTextifier;
  }
  
  public void visitMethodAttribute(Attribute paramAttribute)
  {
    buf.setLength(0);
    buf.append(tab).append("ATTRIBUTE ");
    appendDescriptor(-1, type);
    if ((paramAttribute instanceof Textifiable)) {
      ((Textifiable)paramAttribute).textify(buf, labelNames);
    } else {
      buf.append(" : unknown\n");
    }
    text.add(buf.toString());
  }
  
  public void visitCode() {}
  
  public void visitFrame(int paramInt1, int paramInt2, Object[] paramArrayOfObject1, int paramInt3, Object[] paramArrayOfObject2)
  {
    buf.setLength(0);
    buf.append(ltab);
    buf.append("FRAME ");
    switch (paramInt1)
    {
    case -1: 
    case 0: 
      buf.append("FULL [");
      appendFrameTypes(paramInt2, paramArrayOfObject1);
      buf.append("] [");
      appendFrameTypes(paramInt3, paramArrayOfObject2);
      buf.append(']');
      break;
    case 1: 
      buf.append("APPEND [");
      appendFrameTypes(paramInt2, paramArrayOfObject1);
      buf.append(']');
      break;
    case 2: 
      buf.append("CHOP ").append(paramInt2);
      break;
    case 3: 
      buf.append("SAME");
      break;
    case 4: 
      buf.append("SAME1 ");
      appendFrameTypes(1, paramArrayOfObject2);
    }
    buf.append('\n');
    text.add(buf.toString());
  }
  
  public void visitInsn(int paramInt)
  {
    buf.setLength(0);
    buf.append(tab2).append(OPCODES[paramInt]).append('\n');
    text.add(buf.toString());
  }
  
  public void visitIntInsn(int paramInt1, int paramInt2)
  {
    buf.setLength(0);
    buf.append(tab2).append(OPCODES[paramInt1]).append(' ').append(paramInt1 == 188 ? TYPES[paramInt2] : Integer.toString(paramInt2)).append('\n');
    text.add(buf.toString());
  }
  
  public void visitVarInsn(int paramInt1, int paramInt2)
  {
    buf.setLength(0);
    buf.append(tab2).append(OPCODES[paramInt1]).append(' ').append(paramInt2).append('\n');
    text.add(buf.toString());
  }
  
  public void visitTypeInsn(int paramInt, String paramString)
  {
    buf.setLength(0);
    buf.append(tab2).append(OPCODES[paramInt]).append(' ');
    appendDescriptor(0, paramString);
    buf.append('\n');
    text.add(buf.toString());
  }
  
  public void visitFieldInsn(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    buf.setLength(0);
    buf.append(tab2).append(OPCODES[paramInt]).append(' ');
    appendDescriptor(0, paramString1);
    buf.append('.').append(paramString2).append(" : ");
    appendDescriptor(1, paramString3);
    buf.append('\n');
    text.add(buf.toString());
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
    buf.setLength(0);
    buf.append(tab2).append(OPCODES[paramInt]).append(' ');
    appendDescriptor(0, paramString1);
    buf.append('.').append(paramString2).append(' ');
    appendDescriptor(3, paramString3);
    buf.append('\n');
    text.add(buf.toString());
  }
  
  public void visitInvokeDynamicInsn(String paramString1, String paramString2, Handle paramHandle, Object... paramVarArgs)
  {
    buf.setLength(0);
    buf.append(tab2).append("INVOKEDYNAMIC").append(' ');
    buf.append(paramString1);
    appendDescriptor(3, paramString2);
    buf.append(" [");
    buf.append('\n');
    buf.append(tab3);
    appendHandle(paramHandle);
    buf.append('\n');
    buf.append(tab3).append("// arguments:");
    if (paramVarArgs.length == 0)
    {
      buf.append(" none");
    }
    else
    {
      buf.append('\n');
      for (int i = 0; i < paramVarArgs.length; i++)
      {
        buf.append(tab3);
        Object localObject = paramVarArgs[i];
        if ((localObject instanceof String))
        {
          Printer.appendString(buf, (String)localObject);
        }
        else if ((localObject instanceof Type))
        {
          Type localType = (Type)localObject;
          if (localType.getSort() == 11) {
            appendDescriptor(3, localType.getDescriptor());
          } else {
            buf.append(localType.getDescriptor()).append(".class");
          }
        }
        else if ((localObject instanceof Handle))
        {
          appendHandle((Handle)localObject);
        }
        else
        {
          buf.append(localObject);
        }
        buf.append(", \n");
      }
      buf.setLength(buf.length() - 3);
    }
    buf.append('\n');
    buf.append(tab2).append("]\n");
    text.add(buf.toString());
  }
  
  public void visitJumpInsn(int paramInt, Label paramLabel)
  {
    buf.setLength(0);
    buf.append(tab2).append(OPCODES[paramInt]).append(' ');
    appendLabel(paramLabel);
    buf.append('\n');
    text.add(buf.toString());
  }
  
  public void visitLabel(Label paramLabel)
  {
    buf.setLength(0);
    buf.append(ltab);
    appendLabel(paramLabel);
    buf.append('\n');
    text.add(buf.toString());
  }
  
  public void visitLdcInsn(Object paramObject)
  {
    buf.setLength(0);
    buf.append(tab2).append("LDC ");
    if ((paramObject instanceof String)) {
      Printer.appendString(buf, (String)paramObject);
    } else if ((paramObject instanceof Type)) {
      buf.append(((Type)paramObject).getDescriptor()).append(".class");
    } else {
      buf.append(paramObject);
    }
    buf.append('\n');
    text.add(buf.toString());
  }
  
  public void visitIincInsn(int paramInt1, int paramInt2)
  {
    buf.setLength(0);
    buf.append(tab2).append("IINC ").append(paramInt1).append(' ').append(paramInt2).append('\n');
    text.add(buf.toString());
  }
  
  public void visitTableSwitchInsn(int paramInt1, int paramInt2, Label paramLabel, Label... paramVarArgs)
  {
    buf.setLength(0);
    buf.append(tab2).append("TABLESWITCH\n");
    for (int i = 0; i < paramVarArgs.length; i++)
    {
      buf.append(tab3).append(paramInt1 + i).append(": ");
      appendLabel(paramVarArgs[i]);
      buf.append('\n');
    }
    buf.append(tab3).append("default: ");
    appendLabel(paramLabel);
    buf.append('\n');
    text.add(buf.toString());
  }
  
  public void visitLookupSwitchInsn(Label paramLabel, int[] paramArrayOfInt, Label[] paramArrayOfLabel)
  {
    buf.setLength(0);
    buf.append(tab2).append("LOOKUPSWITCH\n");
    for (int i = 0; i < paramArrayOfLabel.length; i++)
    {
      buf.append(tab3).append(paramArrayOfInt[i]).append(": ");
      appendLabel(paramArrayOfLabel[i]);
      buf.append('\n');
    }
    buf.append(tab3).append("default: ");
    appendLabel(paramLabel);
    buf.append('\n');
    text.add(buf.toString());
  }
  
  public void visitMultiANewArrayInsn(String paramString, int paramInt)
  {
    buf.setLength(0);
    buf.append(tab2).append("MULTIANEWARRAY ");
    appendDescriptor(1, paramString);
    buf.append(' ').append(paramInt).append('\n');
    text.add(buf.toString());
  }
  
  public Printer visitInsnAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    return visitTypeAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
  }
  
  public void visitTryCatchBlock(Label paramLabel1, Label paramLabel2, Label paramLabel3, String paramString)
  {
    buf.setLength(0);
    buf.append(tab2).append("TRYCATCHBLOCK ");
    appendLabel(paramLabel1);
    buf.append(' ');
    appendLabel(paramLabel2);
    buf.append(' ');
    appendLabel(paramLabel3);
    buf.append(' ');
    appendDescriptor(0, paramString);
    buf.append('\n');
    text.add(buf.toString());
  }
  
  public Printer visitTryCatchAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    buf.setLength(0);
    buf.append(tab2).append("TRYCATCHBLOCK @");
    appendDescriptor(1, paramString);
    buf.append('(');
    text.add(buf.toString());
    Textifier localTextifier = createTextifier();
    text.add(localTextifier.getText());
    buf.setLength(0);
    buf.append(") : ");
    appendTypeReference(paramInt);
    buf.append(", ").append(paramTypePath);
    buf.append(paramBoolean ? "\n" : " // invisible\n");
    text.add(buf.toString());
    return localTextifier;
  }
  
  public void visitLocalVariable(String paramString1, String paramString2, String paramString3, Label paramLabel1, Label paramLabel2, int paramInt)
  {
    buf.setLength(0);
    buf.append(tab2).append("LOCALVARIABLE ").append(paramString1).append(' ');
    appendDescriptor(1, paramString2);
    buf.append(' ');
    appendLabel(paramLabel1);
    buf.append(' ');
    appendLabel(paramLabel2);
    buf.append(' ').append(paramInt).append('\n');
    if (paramString3 != null)
    {
      buf.append(tab2);
      appendDescriptor(2, paramString3);
      TraceSignatureVisitor localTraceSignatureVisitor = new TraceSignatureVisitor(0);
      SignatureReader localSignatureReader = new SignatureReader(paramString3);
      localSignatureReader.acceptType(localTraceSignatureVisitor);
      buf.append(tab2).append("// declaration: ").append(localTraceSignatureVisitor.getDeclaration()).append('\n');
    }
    text.add(buf.toString());
  }
  
  public Printer visitLocalVariableAnnotation(int paramInt, TypePath paramTypePath, Label[] paramArrayOfLabel1, Label[] paramArrayOfLabel2, int[] paramArrayOfInt, String paramString, boolean paramBoolean)
  {
    buf.setLength(0);
    buf.append(tab2).append("LOCALVARIABLE @");
    appendDescriptor(1, paramString);
    buf.append('(');
    text.add(buf.toString());
    Textifier localTextifier = createTextifier();
    text.add(localTextifier.getText());
    buf.setLength(0);
    buf.append(") : ");
    appendTypeReference(paramInt);
    buf.append(", ").append(paramTypePath);
    for (int i = 0; i < paramArrayOfLabel1.length; i++)
    {
      buf.append(" [ ");
      appendLabel(paramArrayOfLabel1[i]);
      buf.append(" - ");
      appendLabel(paramArrayOfLabel2[i]);
      buf.append(" - ").append(paramArrayOfInt[i]).append(" ]");
    }
    buf.append(paramBoolean ? "\n" : " // invisible\n");
    text.add(buf.toString());
    return localTextifier;
  }
  
  public void visitLineNumber(int paramInt, Label paramLabel)
  {
    buf.setLength(0);
    buf.append(tab2).append("LINENUMBER ").append(paramInt).append(' ');
    appendLabel(paramLabel);
    buf.append('\n');
    text.add(buf.toString());
  }
  
  public void visitMaxs(int paramInt1, int paramInt2)
  {
    buf.setLength(0);
    buf.append(tab2).append("MAXSTACK = ").append(paramInt1).append('\n');
    text.add(buf.toString());
    buf.setLength(0);
    buf.append(tab2).append("MAXLOCALS = ").append(paramInt2).append('\n');
    text.add(buf.toString());
  }
  
  public void visitMethodEnd() {}
  
  public Textifier visitAnnotation(String paramString, boolean paramBoolean)
  {
    buf.setLength(0);
    buf.append(tab).append('@');
    appendDescriptor(1, paramString);
    buf.append('(');
    text.add(buf.toString());
    Textifier localTextifier = createTextifier();
    text.add(localTextifier.getText());
    text.add(paramBoolean ? ")\n" : ") // invisible\n");
    return localTextifier;
  }
  
  public Textifier visitTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    buf.setLength(0);
    buf.append(tab).append('@');
    appendDescriptor(1, paramString);
    buf.append('(');
    text.add(buf.toString());
    Textifier localTextifier = createTextifier();
    text.add(localTextifier.getText());
    buf.setLength(0);
    buf.append(") : ");
    appendTypeReference(paramInt);
    buf.append(", ").append(paramTypePath);
    buf.append(paramBoolean ? "\n" : " // invisible\n");
    text.add(buf.toString());
    return localTextifier;
  }
  
  public void visitAttribute(Attribute paramAttribute)
  {
    buf.setLength(0);
    buf.append(tab).append("ATTRIBUTE ");
    appendDescriptor(-1, type);
    if ((paramAttribute instanceof Textifiable)) {
      ((Textifiable)paramAttribute).textify(buf, null);
    } else {
      buf.append(" : unknown\n");
    }
    text.add(buf.toString());
  }
  
  protected Textifier createTextifier()
  {
    return new Textifier();
  }
  
  protected void appendDescriptor(int paramInt, String paramString)
  {
    if ((paramInt == 5) || (paramInt == 2) || (paramInt == 4))
    {
      if (paramString != null) {
        buf.append("// signature ").append(paramString).append('\n');
      }
    }
    else {
      buf.append(paramString);
    }
  }
  
  protected void appendLabel(Label paramLabel)
  {
    if (labelNames == null) {
      labelNames = new HashMap();
    }
    String str = (String)labelNames.get(paramLabel);
    if (str == null)
    {
      str = "L" + labelNames.size();
      labelNames.put(paramLabel, str);
    }
    buf.append(str);
  }
  
  protected void appendHandle(Handle paramHandle)
  {
    int i = paramHandle.getTag();
    buf.append("// handle kind 0x").append(Integer.toHexString(i)).append(" : ");
    int j = 0;
    switch (i)
    {
    case 1: 
      buf.append("GETFIELD");
      break;
    case 2: 
      buf.append("GETSTATIC");
      break;
    case 3: 
      buf.append("PUTFIELD");
      break;
    case 4: 
      buf.append("PUTSTATIC");
      break;
    case 9: 
      buf.append("INVOKEINTERFACE");
      j = 1;
      break;
    case 7: 
      buf.append("INVOKESPECIAL");
      j = 1;
      break;
    case 6: 
      buf.append("INVOKESTATIC");
      j = 1;
      break;
    case 5: 
      buf.append("INVOKEVIRTUAL");
      j = 1;
      break;
    case 8: 
      buf.append("NEWINVOKESPECIAL");
      j = 1;
    }
    buf.append('\n');
    buf.append(tab3);
    appendDescriptor(0, paramHandle.getOwner());
    buf.append('.');
    buf.append(paramHandle.getName());
    if (j == 0) {
      buf.append('(');
    }
    appendDescriptor(9, paramHandle.getDesc());
    if (j == 0) {
      buf.append(')');
    }
  }
  
  private void appendAccess(int paramInt)
  {
    if ((paramInt & 0x1) != 0) {
      buf.append("public ");
    }
    if ((paramInt & 0x2) != 0) {
      buf.append("private ");
    }
    if ((paramInt & 0x4) != 0) {
      buf.append("protected ");
    }
    if ((paramInt & 0x10) != 0) {
      buf.append("final ");
    }
    if ((paramInt & 0x8) != 0) {
      buf.append("static ");
    }
    if ((paramInt & 0x20) != 0) {
      buf.append("synchronized ");
    }
    if ((paramInt & 0x40) != 0) {
      buf.append("volatile ");
    }
    if ((paramInt & 0x80) != 0) {
      buf.append("transient ");
    }
    if ((paramInt & 0x400) != 0) {
      buf.append("abstract ");
    }
    if ((paramInt & 0x800) != 0) {
      buf.append("strictfp ");
    }
    if ((paramInt & 0x1000) != 0) {
      buf.append("synthetic ");
    }
    if ((paramInt & 0x8000) != 0) {
      buf.append("mandated ");
    }
    if ((paramInt & 0x4000) != 0) {
      buf.append("enum ");
    }
  }
  
  private void appendComa(int paramInt)
  {
    if (paramInt != 0) {
      buf.append(", ");
    }
  }
  
  private void appendTypeReference(int paramInt)
  {
    TypeReference localTypeReference = new TypeReference(paramInt);
    switch (localTypeReference.getSort())
    {
    case 0: 
      buf.append("CLASS_TYPE_PARAMETER ").append(localTypeReference.getTypeParameterIndex());
      break;
    case 1: 
      buf.append("METHOD_TYPE_PARAMETER ").append(localTypeReference.getTypeParameterIndex());
      break;
    case 16: 
      buf.append("CLASS_EXTENDS ").append(localTypeReference.getSuperTypeIndex());
      break;
    case 17: 
      buf.append("CLASS_TYPE_PARAMETER_BOUND ").append(localTypeReference.getTypeParameterIndex()).append(", ").append(localTypeReference.getTypeParameterBoundIndex());
      break;
    case 18: 
      buf.append("METHOD_TYPE_PARAMETER_BOUND ").append(localTypeReference.getTypeParameterIndex()).append(", ").append(localTypeReference.getTypeParameterBoundIndex());
      break;
    case 19: 
      buf.append("FIELD");
      break;
    case 20: 
      buf.append("METHOD_RETURN");
      break;
    case 21: 
      buf.append("METHOD_RECEIVER");
      break;
    case 22: 
      buf.append("METHOD_FORMAL_PARAMETER ").append(localTypeReference.getFormalParameterIndex());
      break;
    case 23: 
      buf.append("THROWS ").append(localTypeReference.getExceptionIndex());
      break;
    case 64: 
      buf.append("LOCAL_VARIABLE");
      break;
    case 65: 
      buf.append("RESOURCE_VARIABLE");
      break;
    case 66: 
      buf.append("EXCEPTION_PARAMETER ").append(localTypeReference.getTryCatchBlockIndex());
      break;
    case 67: 
      buf.append("INSTANCEOF");
      break;
    case 68: 
      buf.append("NEW");
      break;
    case 69: 
      buf.append("CONSTRUCTOR_REFERENCE");
      break;
    case 70: 
      buf.append("METHOD_REFERENCE");
      break;
    case 71: 
      buf.append("CAST ").append(localTypeReference.getTypeArgumentIndex());
      break;
    case 72: 
      buf.append("CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT ").append(localTypeReference.getTypeArgumentIndex());
      break;
    case 73: 
      buf.append("METHOD_INVOCATION_TYPE_ARGUMENT ").append(localTypeReference.getTypeArgumentIndex());
      break;
    case 74: 
      buf.append("CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT ").append(localTypeReference.getTypeArgumentIndex());
      break;
    case 75: 
      buf.append("METHOD_REFERENCE_TYPE_ARGUMENT ").append(localTypeReference.getTypeArgumentIndex());
    }
  }
  
  private void appendFrameTypes(int paramInt, Object[] paramArrayOfObject)
  {
    for (int i = 0; i < paramInt; i++)
    {
      if (i > 0) {
        buf.append(' ');
      }
      if ((paramArrayOfObject[i] instanceof String))
      {
        String str = (String)paramArrayOfObject[i];
        if (str.startsWith("[")) {
          appendDescriptor(1, str);
        } else {
          appendDescriptor(0, str);
        }
      }
      else if ((paramArrayOfObject[i] instanceof Integer))
      {
        switch (((Integer)paramArrayOfObject[i]).intValue())
        {
        case 0: 
          appendDescriptor(1, "T");
          break;
        case 1: 
          appendDescriptor(1, "I");
          break;
        case 2: 
          appendDescriptor(1, "F");
          break;
        case 3: 
          appendDescriptor(1, "D");
          break;
        case 4: 
          appendDescriptor(1, "J");
          break;
        case 5: 
          appendDescriptor(1, "N");
          break;
        case 6: 
          appendDescriptor(1, "U");
        }
      }
      else
      {
        appendLabel((Label)paramArrayOfObject[i]);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\util\Textifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */