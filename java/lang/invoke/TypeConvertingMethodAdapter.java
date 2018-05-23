package java.lang.invoke;

import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;
import sun.invoke.util.BytecodeDescriptor;
import sun.invoke.util.Wrapper;

class TypeConvertingMethodAdapter
  extends MethodVisitor
{
  private static final int NUM_WRAPPERS;
  private static final String NAME_OBJECT = "java/lang/Object";
  private static final String WRAPPER_PREFIX = "Ljava/lang/";
  private static final String NAME_BOX_METHOD = "valueOf";
  private static final int[][] wideningOpcodes;
  private static final Wrapper[] FROM_WRAPPER_NAME;
  private static final Wrapper[] FROM_TYPE_SORT;
  
  TypeConvertingMethodAdapter(MethodVisitor paramMethodVisitor)
  {
    super(327680, paramMethodVisitor);
  }
  
  private static void initWidening(Wrapper paramWrapper, int paramInt, Wrapper... paramVarArgs)
  {
    for (Wrapper localWrapper : paramVarArgs) {
      wideningOpcodes[localWrapper.ordinal()][paramWrapper.ordinal()] = paramInt;
    }
  }
  
  private static int hashWrapperName(String paramString)
  {
    if (paramString.length() < 3) {
      return 0;
    }
    return ('\003' * paramString.charAt(1) + paramString.charAt(2)) % 16;
  }
  
  private Wrapper wrapperOrNullFromDescriptor(String paramString)
  {
    if (!paramString.startsWith("Ljava/lang/")) {
      return null;
    }
    String str = paramString.substring("Ljava/lang/".length(), paramString.length() - 1);
    Wrapper localWrapper = FROM_WRAPPER_NAME[hashWrapperName(str)];
    if ((localWrapper == null) || (localWrapper.wrapperSimpleName().equals(str))) {
      return localWrapper;
    }
    return null;
  }
  
  private static String wrapperName(Wrapper paramWrapper)
  {
    return "java/lang/" + paramWrapper.wrapperSimpleName();
  }
  
  private static String unboxMethod(Wrapper paramWrapper)
  {
    return paramWrapper.primitiveSimpleName() + "Value";
  }
  
  private static String boxingDescriptor(Wrapper paramWrapper)
  {
    return String.format("(%s)L%s;", new Object[] { Character.valueOf(paramWrapper.basicTypeChar()), wrapperName(paramWrapper) });
  }
  
  private static String unboxingDescriptor(Wrapper paramWrapper)
  {
    return "()" + paramWrapper.basicTypeChar();
  }
  
  void boxIfTypePrimitive(Type paramType)
  {
    Wrapper localWrapper = FROM_TYPE_SORT[paramType.getSort()];
    if (localWrapper != null) {
      box(localWrapper);
    }
  }
  
  void widen(Wrapper paramWrapper1, Wrapper paramWrapper2)
  {
    if (paramWrapper1 != paramWrapper2)
    {
      int i = wideningOpcodes[paramWrapper1.ordinal()][paramWrapper2.ordinal()];
      if (i != 0) {
        visitInsn(i);
      }
    }
  }
  
  void box(Wrapper paramWrapper)
  {
    visitMethodInsn(184, wrapperName(paramWrapper), "valueOf", boxingDescriptor(paramWrapper), false);
  }
  
  void unbox(String paramString, Wrapper paramWrapper)
  {
    visitMethodInsn(182, paramString, unboxMethod(paramWrapper), unboxingDescriptor(paramWrapper), false);
  }
  
  private String descriptorToName(String paramString)
  {
    int i = paramString.length() - 1;
    if ((paramString.charAt(0) == 'L') && (paramString.charAt(i) == ';')) {
      return paramString.substring(1, i);
    }
    return paramString;
  }
  
  void cast(String paramString1, String paramString2)
  {
    String str1 = descriptorToName(paramString1);
    String str2 = descriptorToName(paramString2);
    if ((!str2.equals(str1)) && (!str2.equals("java/lang/Object"))) {
      visitTypeInsn(192, str2);
    }
  }
  
  private boolean isPrimitive(Wrapper paramWrapper)
  {
    return paramWrapper != Wrapper.OBJECT;
  }
  
  private Wrapper toWrapper(String paramString)
  {
    char c = paramString.charAt(0);
    if ((c == '[') || (c == '(')) {
      c = 'L';
    }
    return Wrapper.forBasicType(c);
  }
  
  void convertType(Class<?> paramClass1, Class<?> paramClass2, Class<?> paramClass3)
  {
    if ((paramClass1.equals(paramClass2)) && (paramClass1.equals(paramClass3))) {
      return;
    }
    if ((paramClass1 == Void.TYPE) || (paramClass2 == Void.TYPE)) {
      return;
    }
    Object localObject1;
    Object localObject2;
    Object localObject3;
    if (paramClass1.isPrimitive())
    {
      localObject1 = Wrapper.forPrimitiveType(paramClass1);
      if (paramClass2.isPrimitive())
      {
        widen((Wrapper)localObject1, Wrapper.forPrimitiveType(paramClass2));
      }
      else
      {
        localObject2 = BytecodeDescriptor.unparse(paramClass2);
        localObject3 = wrapperOrNullFromDescriptor((String)localObject2);
        if (localObject3 != null)
        {
          widen((Wrapper)localObject1, (Wrapper)localObject3);
          box((Wrapper)localObject3);
        }
        else
        {
          box((Wrapper)localObject1);
          cast(wrapperName((Wrapper)localObject1), (String)localObject2);
        }
      }
    }
    else
    {
      localObject1 = BytecodeDescriptor.unparse(paramClass1);
      if (paramClass3.isPrimitive())
      {
        localObject2 = localObject1;
      }
      else
      {
        localObject2 = BytecodeDescriptor.unparse(paramClass3);
        cast((String)localObject1, (String)localObject2);
      }
      localObject3 = BytecodeDescriptor.unparse(paramClass2);
      if (paramClass2.isPrimitive())
      {
        Wrapper localWrapper1 = toWrapper((String)localObject3);
        Wrapper localWrapper2 = wrapperOrNullFromDescriptor((String)localObject2);
        if (localWrapper2 != null)
        {
          if ((localWrapper2.isSigned()) || (localWrapper2.isFloating()))
          {
            unbox(wrapperName(localWrapper2), localWrapper1);
          }
          else
          {
            unbox(wrapperName(localWrapper2), localWrapper2);
            widen(localWrapper2, localWrapper1);
          }
        }
        else
        {
          String str;
          if ((localWrapper1.isSigned()) || (localWrapper1.isFloating())) {
            str = "java/lang/Number";
          } else {
            str = wrapperName(localWrapper1);
          }
          cast((String)localObject2, str);
          unbox(str, localWrapper1);
        }
      }
      else
      {
        cast((String)localObject2, (String)localObject3);
      }
    }
  }
  
  void iconst(int paramInt)
  {
    if ((paramInt >= -1) && (paramInt <= 5)) {
      mv.visitInsn(3 + paramInt);
    } else if ((paramInt >= -128) && (paramInt <= 127)) {
      mv.visitIntInsn(16, paramInt);
    } else if ((paramInt >= 32768) && (paramInt <= 32767)) {
      mv.visitIntInsn(17, paramInt);
    } else {
      mv.visitLdcInsn(Integer.valueOf(paramInt));
    }
  }
  
  static
  {
    NUM_WRAPPERS = Wrapper.values().length;
    wideningOpcodes = new int[NUM_WRAPPERS][NUM_WRAPPERS];
    FROM_WRAPPER_NAME = new Wrapper[16];
    FROM_TYPE_SORT = new Wrapper[16];
    for (Wrapper localWrapper : Wrapper.values()) {
      if (localWrapper.basicTypeChar() != 'L')
      {
        int m = hashWrapperName(localWrapper.wrapperSimpleName());
        assert (FROM_WRAPPER_NAME[m] == null);
        FROM_WRAPPER_NAME[m] = localWrapper;
      }
    }
    for (int i = 0; i < NUM_WRAPPERS; i++) {
      for (??? = 0; ??? < NUM_WRAPPERS; ???++) {
        wideningOpcodes[i][???] = 0;
      }
    }
    initWidening(Wrapper.LONG, 133, new Wrapper[] { Wrapper.BYTE, Wrapper.SHORT, Wrapper.INT, Wrapper.CHAR });
    initWidening(Wrapper.LONG, 140, new Wrapper[] { Wrapper.FLOAT });
    initWidening(Wrapper.FLOAT, 134, new Wrapper[] { Wrapper.BYTE, Wrapper.SHORT, Wrapper.INT, Wrapper.CHAR });
    initWidening(Wrapper.FLOAT, 137, new Wrapper[] { Wrapper.LONG });
    initWidening(Wrapper.DOUBLE, 135, new Wrapper[] { Wrapper.BYTE, Wrapper.SHORT, Wrapper.INT, Wrapper.CHAR });
    initWidening(Wrapper.DOUBLE, 141, new Wrapper[] { Wrapper.FLOAT });
    initWidening(Wrapper.DOUBLE, 138, new Wrapper[] { Wrapper.LONG });
    FROM_TYPE_SORT[3] = Wrapper.BYTE;
    FROM_TYPE_SORT[4] = Wrapper.SHORT;
    FROM_TYPE_SORT[5] = Wrapper.INT;
    FROM_TYPE_SORT[7] = Wrapper.LONG;
    FROM_TYPE_SORT[2] = Wrapper.CHAR;
    FROM_TYPE_SORT[6] = Wrapper.FLOAT;
    FROM_TYPE_SORT[8] = Wrapper.DOUBLE;
    FROM_TYPE_SORT[1] = Wrapper.BOOLEAN;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\invoke\TypeConvertingMethodAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */