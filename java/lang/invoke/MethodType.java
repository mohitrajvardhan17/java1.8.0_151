package java.lang.invoke;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.invoke.util.BytecodeDescriptor;
import sun.invoke.util.VerifyType;
import sun.invoke.util.Wrapper;
import sun.misc.Unsafe;

public final class MethodType
  implements Serializable
{
  private static final long serialVersionUID = 292L;
  private final Class<?> rtype;
  private final Class<?>[] ptypes;
  @Stable
  private MethodTypeForm form;
  @Stable
  private MethodType wrapAlt;
  @Stable
  private Invokers invokers;
  @Stable
  private String methodDescriptor;
  static final int MAX_JVM_ARITY = 255;
  static final int MAX_MH_ARITY = 254;
  static final int MAX_MH_INVOKER_ARITY = 253;
  static final ConcurrentWeakInternSet<MethodType> internTable;
  static final Class<?>[] NO_PTYPES;
  private static final MethodType[] objectOnlyTypes;
  private static final ObjectStreamField[] serialPersistentFields;
  private static final long rtypeOffset;
  private static final long ptypesOffset;
  
  private MethodType(Class<?> paramClass, Class<?>[] paramArrayOfClass, boolean paramBoolean)
  {
    checkRtype(paramClass);
    checkPtypes(paramArrayOfClass);
    rtype = paramClass;
    ptypes = (paramBoolean ? paramArrayOfClass : (Class[])Arrays.copyOf(paramArrayOfClass, paramArrayOfClass.length));
  }
  
  private MethodType(Class<?>[] paramArrayOfClass, Class<?> paramClass)
  {
    rtype = paramClass;
    ptypes = paramArrayOfClass;
  }
  
  MethodTypeForm form()
  {
    return form;
  }
  
  Class<?> rtype()
  {
    return rtype;
  }
  
  Class<?>[] ptypes()
  {
    return ptypes;
  }
  
  void setForm(MethodTypeForm paramMethodTypeForm)
  {
    form = paramMethodTypeForm;
  }
  
  private static void checkRtype(Class<?> paramClass)
  {
    Objects.requireNonNull(paramClass);
  }
  
  private static void checkPtype(Class<?> paramClass)
  {
    Objects.requireNonNull(paramClass);
    if (paramClass == Void.TYPE) {
      throw MethodHandleStatics.newIllegalArgumentException("parameter type cannot be void");
    }
  }
  
  private static int checkPtypes(Class<?>[] paramArrayOfClass)
  {
    int i = 0;
    for (Class<?> localClass : paramArrayOfClass)
    {
      checkPtype(localClass);
      if ((localClass == Double.TYPE) || (localClass == Long.TYPE)) {
        i++;
      }
    }
    checkSlotCount(paramArrayOfClass.length + i);
    return i;
  }
  
  static void checkSlotCount(int paramInt)
  {
    if ((paramInt & 0xFF) != paramInt) {
      throw MethodHandleStatics.newIllegalArgumentException("bad parameter count " + paramInt);
    }
  }
  
  private static IndexOutOfBoundsException newIndexOutOfBoundsException(Object paramObject)
  {
    if ((paramObject instanceof Integer)) {
      paramObject = "bad index: " + paramObject;
    }
    return new IndexOutOfBoundsException(paramObject.toString());
  }
  
  public static MethodType methodType(Class<?> paramClass, Class<?>[] paramArrayOfClass)
  {
    return makeImpl(paramClass, paramArrayOfClass, false);
  }
  
  public static MethodType methodType(Class<?> paramClass, List<Class<?>> paramList)
  {
    boolean bool = false;
    return makeImpl(paramClass, listToArray(paramList), bool);
  }
  
  private static Class<?>[] listToArray(List<Class<?>> paramList)
  {
    checkSlotCount(paramList.size());
    return (Class[])paramList.toArray(NO_PTYPES);
  }
  
  public static MethodType methodType(Class<?> paramClass1, Class<?> paramClass2, Class<?>... paramVarArgs)
  {
    Class[] arrayOfClass = new Class[1 + paramVarArgs.length];
    arrayOfClass[0] = paramClass2;
    System.arraycopy(paramVarArgs, 0, arrayOfClass, 1, paramVarArgs.length);
    return makeImpl(paramClass1, arrayOfClass, true);
  }
  
  public static MethodType methodType(Class<?> paramClass)
  {
    return makeImpl(paramClass, NO_PTYPES, true);
  }
  
  public static MethodType methodType(Class<?> paramClass1, Class<?> paramClass2)
  {
    return makeImpl(paramClass1, new Class[] { paramClass2 }, true);
  }
  
  public static MethodType methodType(Class<?> paramClass, MethodType paramMethodType)
  {
    return makeImpl(paramClass, ptypes, true);
  }
  
  static MethodType makeImpl(Class<?> paramClass, Class<?>[] paramArrayOfClass, boolean paramBoolean)
  {
    MethodType localMethodType = (MethodType)internTable.get(new MethodType(paramArrayOfClass, paramClass));
    if (localMethodType != null) {
      return localMethodType;
    }
    if (paramArrayOfClass.length == 0)
    {
      paramArrayOfClass = NO_PTYPES;
      paramBoolean = true;
    }
    localMethodType = new MethodType(paramClass, paramArrayOfClass, paramBoolean);
    form = MethodTypeForm.findForm(localMethodType);
    return (MethodType)internTable.add(localMethodType);
  }
  
  public static MethodType genericMethodType(int paramInt, boolean paramBoolean)
  {
    checkSlotCount(paramInt);
    int i = !paramBoolean ? 0 : 1;
    int j = paramInt * 2 + i;
    if (j < objectOnlyTypes.length)
    {
      localMethodType = objectOnlyTypes[j];
      if (localMethodType != null) {
        return localMethodType;
      }
    }
    Class[] arrayOfClass = new Class[paramInt + i];
    Arrays.fill(arrayOfClass, Object.class);
    if (i != 0) {
      arrayOfClass[paramInt] = Object[].class;
    }
    MethodType localMethodType = makeImpl(Object.class, arrayOfClass, true);
    if (j < objectOnlyTypes.length) {
      objectOnlyTypes[j] = localMethodType;
    }
    return localMethodType;
  }
  
  public static MethodType genericMethodType(int paramInt)
  {
    return genericMethodType(paramInt, false);
  }
  
  public MethodType changeParameterType(int paramInt, Class<?> paramClass)
  {
    if (parameterType(paramInt) == paramClass) {
      return this;
    }
    checkPtype(paramClass);
    Class[] arrayOfClass = (Class[])ptypes.clone();
    arrayOfClass[paramInt] = paramClass;
    return makeImpl(rtype, arrayOfClass, true);
  }
  
  public MethodType insertParameterTypes(int paramInt, Class<?>... paramVarArgs)
  {
    int i = ptypes.length;
    if ((paramInt < 0) || (paramInt > i)) {
      throw newIndexOutOfBoundsException(Integer.valueOf(paramInt));
    }
    int j = checkPtypes(paramVarArgs);
    checkSlotCount(parameterSlotCount() + paramVarArgs.length + j);
    int k = paramVarArgs.length;
    if (k == 0) {
      return this;
    }
    Class[] arrayOfClass = (Class[])Arrays.copyOfRange(ptypes, 0, i + k);
    System.arraycopy(arrayOfClass, paramInt, arrayOfClass, paramInt + k, i - paramInt);
    System.arraycopy(paramVarArgs, 0, arrayOfClass, paramInt, k);
    return makeImpl(rtype, arrayOfClass, true);
  }
  
  public MethodType appendParameterTypes(Class<?>... paramVarArgs)
  {
    return insertParameterTypes(parameterCount(), paramVarArgs);
  }
  
  public MethodType insertParameterTypes(int paramInt, List<Class<?>> paramList)
  {
    return insertParameterTypes(paramInt, listToArray(paramList));
  }
  
  public MethodType appendParameterTypes(List<Class<?>> paramList)
  {
    return insertParameterTypes(parameterCount(), paramList);
  }
  
  MethodType replaceParameterTypes(int paramInt1, int paramInt2, Class<?>... paramVarArgs)
  {
    if (paramInt1 == paramInt2) {
      return insertParameterTypes(paramInt1, paramVarArgs);
    }
    int i = ptypes.length;
    if ((0 > paramInt1) || (paramInt1 > paramInt2) || (paramInt2 > i)) {
      throw newIndexOutOfBoundsException("start=" + paramInt1 + " end=" + paramInt2);
    }
    int j = paramVarArgs.length;
    if (j == 0) {
      return dropParameterTypes(paramInt1, paramInt2);
    }
    return dropParameterTypes(paramInt1, paramInt2).insertParameterTypes(paramInt1, paramVarArgs);
  }
  
  MethodType asSpreaderType(Class<?> paramClass, int paramInt)
  {
    assert (parameterCount() >= paramInt);
    int i = ptypes.length - paramInt;
    if (paramInt == 0) {
      return this;
    }
    if (paramClass == Object[].class)
    {
      if (isGeneric()) {
        return this;
      }
      if (i == 0)
      {
        localObject = genericMethodType(paramInt);
        if (rtype != Object.class) {
          localObject = ((MethodType)localObject).changeReturnType(rtype);
        }
        return (MethodType)localObject;
      }
    }
    Object localObject = paramClass.getComponentType();
    assert (localObject != null);
    for (int j = i; j < ptypes.length; j++) {
      if (ptypes[j] != localObject)
      {
        Class[] arrayOfClass = (Class[])ptypes.clone();
        Arrays.fill(arrayOfClass, j, ptypes.length, localObject);
        return methodType(rtype, arrayOfClass);
      }
    }
    return this;
  }
  
  Class<?> leadingReferenceParameter()
  {
    Class localClass;
    if ((ptypes.length == 0) || ((localClass = ptypes[0]).isPrimitive())) {
      throw MethodHandleStatics.newIllegalArgumentException("no leading reference parameter");
    }
    return localClass;
  }
  
  MethodType asCollectorType(Class<?> paramClass, int paramInt)
  {
    assert (parameterCount() >= 1);
    assert (lastParameterType().isAssignableFrom(paramClass));
    MethodType localMethodType;
    if (paramClass == Object[].class)
    {
      localMethodType = genericMethodType(paramInt);
      if (rtype != Object.class) {
        localMethodType = localMethodType.changeReturnType(rtype);
      }
    }
    else
    {
      Class localClass = paramClass.getComponentType();
      assert (localClass != null);
      localMethodType = methodType(rtype, Collections.nCopies(paramInt, localClass));
    }
    if (ptypes.length == 1) {
      return localMethodType;
    }
    return localMethodType.insertParameterTypes(0, parameterList().subList(0, ptypes.length - 1));
  }
  
  public MethodType dropParameterTypes(int paramInt1, int paramInt2)
  {
    int i = ptypes.length;
    if ((0 > paramInt1) || (paramInt1 > paramInt2) || (paramInt2 > i)) {
      throw newIndexOutOfBoundsException("start=" + paramInt1 + " end=" + paramInt2);
    }
    if (paramInt1 == paramInt2) {
      return this;
    }
    Class[] arrayOfClass;
    if (paramInt1 == 0)
    {
      if (paramInt2 == i) {
        arrayOfClass = NO_PTYPES;
      } else {
        arrayOfClass = (Class[])Arrays.copyOfRange(ptypes, paramInt2, i);
      }
    }
    else if (paramInt2 == i)
    {
      arrayOfClass = (Class[])Arrays.copyOfRange(ptypes, 0, paramInt1);
    }
    else
    {
      int j = i - paramInt2;
      arrayOfClass = (Class[])Arrays.copyOfRange(ptypes, 0, paramInt1 + j);
      System.arraycopy(ptypes, paramInt2, arrayOfClass, paramInt1, j);
    }
    return makeImpl(rtype, arrayOfClass, true);
  }
  
  public MethodType changeReturnType(Class<?> paramClass)
  {
    if (returnType() == paramClass) {
      return this;
    }
    return makeImpl(paramClass, ptypes, true);
  }
  
  public boolean hasPrimitives()
  {
    return form.hasPrimitives();
  }
  
  public boolean hasWrappers()
  {
    return unwrap() != this;
  }
  
  public MethodType erase()
  {
    return form.erasedType();
  }
  
  MethodType basicType()
  {
    return form.basicType();
  }
  
  MethodType invokerType()
  {
    return insertParameterTypes(0, new Class[] { MethodHandle.class });
  }
  
  public MethodType generic()
  {
    return genericMethodType(parameterCount());
  }
  
  boolean isGeneric()
  {
    return (this == erase()) && (!hasPrimitives());
  }
  
  public MethodType wrap()
  {
    return hasPrimitives() ? wrapWithPrims(this) : this;
  }
  
  public MethodType unwrap()
  {
    MethodType localMethodType = !hasPrimitives() ? this : wrapWithPrims(this);
    return unwrapWithNoPrims(localMethodType);
  }
  
  private static MethodType wrapWithPrims(MethodType paramMethodType)
  {
    assert (paramMethodType.hasPrimitives());
    MethodType localMethodType = wrapAlt;
    if (localMethodType == null)
    {
      localMethodType = MethodTypeForm.canonicalize(paramMethodType, 2, 2);
      assert (localMethodType != null);
      wrapAlt = localMethodType;
    }
    return localMethodType;
  }
  
  private static MethodType unwrapWithNoPrims(MethodType paramMethodType)
  {
    assert (!paramMethodType.hasPrimitives());
    MethodType localMethodType = wrapAlt;
    if (localMethodType == null)
    {
      localMethodType = MethodTypeForm.canonicalize(paramMethodType, 3, 3);
      if (localMethodType == null) {
        localMethodType = paramMethodType;
      }
      wrapAlt = localMethodType;
    }
    return localMethodType;
  }
  
  public Class<?> parameterType(int paramInt)
  {
    return ptypes[paramInt];
  }
  
  public int parameterCount()
  {
    return ptypes.length;
  }
  
  public Class<?> returnType()
  {
    return rtype;
  }
  
  public List<Class<?>> parameterList()
  {
    return Collections.unmodifiableList(Arrays.asList((Object[])ptypes.clone()));
  }
  
  Class<?> lastParameterType()
  {
    int i = ptypes.length;
    return i == 0 ? Void.TYPE : ptypes[(i - 1)];
  }
  
  public Class<?>[] parameterArray()
  {
    return (Class[])ptypes.clone();
  }
  
  public boolean equals(Object paramObject)
  {
    return (this == paramObject) || (((paramObject instanceof MethodType)) && (equals((MethodType)paramObject)));
  }
  
  private boolean equals(MethodType paramMethodType)
  {
    return (rtype == rtype) && (Arrays.equals(ptypes, ptypes));
  }
  
  public int hashCode()
  {
    int i = 31 + rtype.hashCode();
    for (Class localClass : ptypes) {
      i = 31 * i + localClass.hashCode();
    }
    return i;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("(");
    for (int i = 0; i < ptypes.length; i++)
    {
      if (i > 0) {
        localStringBuilder.append(",");
      }
      localStringBuilder.append(ptypes[i].getSimpleName());
    }
    localStringBuilder.append(")");
    localStringBuilder.append(rtype.getSimpleName());
    return localStringBuilder.toString();
  }
  
  boolean isViewableAs(MethodType paramMethodType, boolean paramBoolean)
  {
    if (!VerifyType.isNullConversion(returnType(), paramMethodType.returnType(), paramBoolean)) {
      return false;
    }
    return parametersAreViewableAs(paramMethodType, paramBoolean);
  }
  
  boolean parametersAreViewableAs(MethodType paramMethodType, boolean paramBoolean)
  {
    if ((form == form) && (form.erasedType == this)) {
      return true;
    }
    if (ptypes == ptypes) {
      return true;
    }
    int i = parameterCount();
    if (i != paramMethodType.parameterCount()) {
      return false;
    }
    for (int j = 0; j < i; j++) {
      if (!VerifyType.isNullConversion(paramMethodType.parameterType(j), parameterType(j), paramBoolean)) {
        return false;
      }
    }
    return true;
  }
  
  boolean isConvertibleTo(MethodType paramMethodType)
  {
    MethodTypeForm localMethodTypeForm1 = form();
    MethodTypeForm localMethodTypeForm2 = paramMethodType.form();
    if (localMethodTypeForm1 == localMethodTypeForm2) {
      return true;
    }
    if (!canConvert(returnType(), paramMethodType.returnType())) {
      return false;
    }
    Class[] arrayOfClass1 = ptypes;
    Class[] arrayOfClass2 = ptypes;
    if (arrayOfClass1 == arrayOfClass2) {
      return true;
    }
    int i;
    if ((i = arrayOfClass1.length) != arrayOfClass2.length) {
      return false;
    }
    if (i <= 1) {
      return (i != 1) || (canConvert(arrayOfClass1[0], arrayOfClass2[0]));
    }
    if (((localMethodTypeForm1.primitiveParameterCount() == 0) && (erasedType == this)) || ((localMethodTypeForm2.primitiveParameterCount() == 0) && (erasedType == paramMethodType)))
    {
      assert (canConvertParameters(arrayOfClass1, arrayOfClass2));
      return true;
    }
    return canConvertParameters(arrayOfClass1, arrayOfClass2);
  }
  
  boolean explicitCastEquivalentToAsType(MethodType paramMethodType)
  {
    if (this == paramMethodType) {
      return true;
    }
    if (!explicitCastEquivalentToAsType(rtype, rtype)) {
      return false;
    }
    Class[] arrayOfClass1 = ptypes;
    Class[] arrayOfClass2 = ptypes;
    if (arrayOfClass2 == arrayOfClass1) {
      return true;
    }
    assert (arrayOfClass2.length == arrayOfClass1.length);
    for (int i = 0; i < arrayOfClass2.length; i++) {
      if (!explicitCastEquivalentToAsType(arrayOfClass1[i], arrayOfClass2[i])) {
        return false;
      }
    }
    return true;
  }
  
  private static boolean explicitCastEquivalentToAsType(Class<?> paramClass1, Class<?> paramClass2)
  {
    if ((paramClass1 == paramClass2) || (paramClass2 == Object.class) || (paramClass2 == Void.TYPE)) {
      return true;
    }
    if (paramClass1.isPrimitive()) {
      return canConvert(paramClass1, paramClass2);
    }
    if (paramClass2.isPrimitive()) {
      return false;
    }
    return (!paramClass2.isInterface()) || (paramClass2.isAssignableFrom(paramClass1));
  }
  
  private boolean canConvertParameters(Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2)
  {
    for (int i = 0; i < paramArrayOfClass1.length; i++) {
      if (!canConvert(paramArrayOfClass1[i], paramArrayOfClass2[i])) {
        return false;
      }
    }
    return true;
  }
  
  static boolean canConvert(Class<?> paramClass1, Class<?> paramClass2)
  {
    if ((paramClass1 == paramClass2) || (paramClass1 == Object.class) || (paramClass2 == Object.class)) {
      return true;
    }
    Wrapper localWrapper;
    if (paramClass1.isPrimitive())
    {
      if (paramClass1 == Void.TYPE) {
        return true;
      }
      localWrapper = Wrapper.forPrimitiveType(paramClass1);
      if (paramClass2.isPrimitive()) {
        return Wrapper.forPrimitiveType(paramClass2).isConvertibleFrom(localWrapper);
      }
      return paramClass2.isAssignableFrom(localWrapper.wrapperType());
    }
    if (paramClass2.isPrimitive())
    {
      if (paramClass2 == Void.TYPE) {
        return true;
      }
      localWrapper = Wrapper.forPrimitiveType(paramClass2);
      if (paramClass1.isAssignableFrom(localWrapper.wrapperType())) {
        return true;
      }
      return (Wrapper.isWrapperType(paramClass1)) && (localWrapper.isConvertibleFrom(Wrapper.forWrapperType(paramClass1)));
    }
    return true;
  }
  
  int parameterSlotCount()
  {
    return form.parameterSlotCount();
  }
  
  Invokers invokers()
  {
    Invokers localInvokers = invokers;
    if (localInvokers != null) {
      return localInvokers;
    }
    invokers = (localInvokers = new Invokers(this));
    return localInvokers;
  }
  
  int parameterSlotDepth(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > ptypes.length)) {
      parameterType(paramInt);
    }
    return form.parameterToArgSlot(paramInt - 1);
  }
  
  int returnSlotCount()
  {
    return form.returnSlotCount();
  }
  
  public static MethodType fromMethodDescriptorString(String paramString, ClassLoader paramClassLoader)
    throws IllegalArgumentException, TypeNotPresentException
  {
    if ((!paramString.startsWith("(")) || (paramString.indexOf(')') < 0) || (paramString.indexOf('.') >= 0)) {
      throw MethodHandleStatics.newIllegalArgumentException("not a method descriptor: " + paramString);
    }
    List localList = BytecodeDescriptor.parseMethod(paramString, paramClassLoader);
    Class localClass = (Class)localList.remove(localList.size() - 1);
    checkSlotCount(localList.size());
    Class[] arrayOfClass = listToArray(localList);
    return makeImpl(localClass, arrayOfClass, true);
  }
  
  public String toMethodDescriptorString()
  {
    String str = methodDescriptor;
    if (str == null)
    {
      str = BytecodeDescriptor.unparse(this);
      methodDescriptor = str;
    }
    return str;
  }
  
  static String toFieldDescriptorString(Class<?> paramClass)
  {
    return BytecodeDescriptor.unparse(paramClass);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(returnType());
    paramObjectOutputStream.writeObject(parameterArray());
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    Class localClass = (Class)paramObjectInputStream.readObject();
    Class[] arrayOfClass = (Class[])paramObjectInputStream.readObject();
    checkRtype(localClass);
    checkPtypes(arrayOfClass);
    arrayOfClass = (Class[])arrayOfClass.clone();
    MethodType_init(localClass, arrayOfClass);
  }
  
  private MethodType()
  {
    rtype = null;
    ptypes = null;
  }
  
  private void MethodType_init(Class<?> paramClass, Class<?>[] paramArrayOfClass)
  {
    checkRtype(paramClass);
    checkPtypes(paramArrayOfClass);
    MethodHandleStatics.UNSAFE.putObject(this, rtypeOffset, paramClass);
    MethodHandleStatics.UNSAFE.putObject(this, ptypesOffset, paramArrayOfClass);
  }
  
  private Object readResolve()
  {
    return methodType(rtype, ptypes);
  }
  
  static
  {
    internTable = new ConcurrentWeakInternSet();
    NO_PTYPES = new Class[0];
    objectOnlyTypes = new MethodType[20];
    serialPersistentFields = new ObjectStreamField[0];
    try
    {
      rtypeOffset = MethodHandleStatics.UNSAFE.objectFieldOffset(MethodType.class.getDeclaredField("rtype"));
      ptypesOffset = MethodHandleStatics.UNSAFE.objectFieldOffset(MethodType.class.getDeclaredField("ptypes"));
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
  
  private static class ConcurrentWeakInternSet<T>
  {
    private final ConcurrentMap<WeakEntry<T>, WeakEntry<T>> map = new ConcurrentHashMap();
    private final ReferenceQueue<T> stale = new ReferenceQueue();
    
    public ConcurrentWeakInternSet() {}
    
    public T get(T paramT)
    {
      if (paramT == null) {
        throw new NullPointerException();
      }
      expungeStaleElements();
      WeakEntry localWeakEntry = (WeakEntry)map.get(new WeakEntry(paramT));
      if (localWeakEntry != null)
      {
        Object localObject = localWeakEntry.get();
        if (localObject != null) {
          return (T)localObject;
        }
      }
      return null;
    }
    
    public T add(T paramT)
    {
      if (paramT == null) {
        throw new NullPointerException();
      }
      WeakEntry localWeakEntry1 = new WeakEntry(paramT, stale);
      Object localObject;
      do
      {
        expungeStaleElements();
        WeakEntry localWeakEntry2 = (WeakEntry)map.putIfAbsent(localWeakEntry1, localWeakEntry1);
        localObject = localWeakEntry2 == null ? paramT : localWeakEntry2.get();
      } while (localObject == null);
      return (T)localObject;
    }
    
    private void expungeStaleElements()
    {
      Reference localReference;
      while ((localReference = stale.poll()) != null) {
        map.remove(localReference);
      }
    }
    
    private static class WeakEntry<T>
      extends WeakReference<T>
    {
      public final int hashcode;
      
      public WeakEntry(T paramT, ReferenceQueue<T> paramReferenceQueue)
      {
        super(paramReferenceQueue);
        hashcode = paramT.hashCode();
      }
      
      public WeakEntry(T paramT)
      {
        super();
        hashcode = paramT.hashCode();
      }
      
      public boolean equals(Object paramObject)
      {
        if ((paramObject instanceof WeakEntry))
        {
          Object localObject1 = ((WeakEntry)paramObject).get();
          Object localObject2 = get();
          return (localObject1 == null) || (localObject2 == null) ? false : this == paramObject ? true : localObject2.equals(localObject1);
        }
        return false;
      }
      
      public int hashCode()
      {
        return hashcode;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\invoke\MethodType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */