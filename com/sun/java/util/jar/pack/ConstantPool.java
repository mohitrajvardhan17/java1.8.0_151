package com.sun.java.util.jar.pack;

import java.io.PrintStream;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

abstract class ConstantPool
{
  protected static final Entry[] noRefs;
  protected static final ClassEntry[] noClassRefs;
  static final byte[] TAGS_IN_ORDER;
  static final byte[] TAG_ORDER;
  static final byte[] NUMBER_TAGS;
  static final byte[] EXTRA_TAGS;
  static final byte[] LOADABLE_VALUE_TAGS;
  static final byte[] ANY_MEMBER_TAGS;
  static final byte[] FIELD_SPECIFIC_TAGS;
  
  private ConstantPool() {}
  
  static int verbose()
  {
    return Utils.currentPropMap().getInteger("com.sun.java.util.jar.pack.verbose");
  }
  
  public static synchronized Utf8Entry getUtf8Entry(String paramString)
  {
    Map localMap = Utils.getTLGlobals().getUtf8Entries();
    Utf8Entry localUtf8Entry = (Utf8Entry)localMap.get(paramString);
    if (localUtf8Entry == null)
    {
      localUtf8Entry = new Utf8Entry(paramString);
      localMap.put(localUtf8Entry.stringValue(), localUtf8Entry);
    }
    return localUtf8Entry;
  }
  
  public static ClassEntry getClassEntry(String paramString)
  {
    Map localMap = Utils.getTLGlobals().getClassEntries();
    ClassEntry localClassEntry = (ClassEntry)localMap.get(paramString);
    if (localClassEntry == null)
    {
      localClassEntry = new ClassEntry(getUtf8Entry(paramString));
      assert (paramString.equals(localClassEntry.stringValue()));
      localMap.put(localClassEntry.stringValue(), localClassEntry);
    }
    return localClassEntry;
  }
  
  public static LiteralEntry getLiteralEntry(Comparable<?> paramComparable)
  {
    Map localMap = Utils.getTLGlobals().getLiteralEntries();
    Object localObject = (LiteralEntry)localMap.get(paramComparable);
    if (localObject == null)
    {
      if ((paramComparable instanceof String)) {
        localObject = new StringEntry(getUtf8Entry((String)paramComparable));
      } else {
        localObject = new NumberEntry((Number)paramComparable);
      }
      localMap.put(paramComparable, localObject);
    }
    return (LiteralEntry)localObject;
  }
  
  public static StringEntry getStringEntry(String paramString)
  {
    return (StringEntry)getLiteralEntry(paramString);
  }
  
  public static SignatureEntry getSignatureEntry(String paramString)
  {
    Map localMap = Utils.getTLGlobals().getSignatureEntries();
    SignatureEntry localSignatureEntry = (SignatureEntry)localMap.get(paramString);
    if (localSignatureEntry == null)
    {
      localSignatureEntry = new SignatureEntry(paramString);
      assert (localSignatureEntry.stringValue().equals(paramString));
      localMap.put(paramString, localSignatureEntry);
    }
    return localSignatureEntry;
  }
  
  public static SignatureEntry getSignatureEntry(Utf8Entry paramUtf8Entry, ClassEntry[] paramArrayOfClassEntry)
  {
    return getSignatureEntry(SignatureEntry.stringValueOf(paramUtf8Entry, paramArrayOfClassEntry));
  }
  
  public static DescriptorEntry getDescriptorEntry(Utf8Entry paramUtf8Entry, SignatureEntry paramSignatureEntry)
  {
    Map localMap = Utils.getTLGlobals().getDescriptorEntries();
    String str = DescriptorEntry.stringValueOf(paramUtf8Entry, paramSignatureEntry);
    DescriptorEntry localDescriptorEntry = (DescriptorEntry)localMap.get(str);
    if (localDescriptorEntry == null)
    {
      localDescriptorEntry = new DescriptorEntry(paramUtf8Entry, paramSignatureEntry);
      assert (localDescriptorEntry.stringValue().equals(str)) : (localDescriptorEntry.stringValue() + " != " + str);
      localMap.put(str, localDescriptorEntry);
    }
    return localDescriptorEntry;
  }
  
  public static DescriptorEntry getDescriptorEntry(Utf8Entry paramUtf8Entry1, Utf8Entry paramUtf8Entry2)
  {
    return getDescriptorEntry(paramUtf8Entry1, getSignatureEntry(paramUtf8Entry2.stringValue()));
  }
  
  public static MemberEntry getMemberEntry(byte paramByte, ClassEntry paramClassEntry, DescriptorEntry paramDescriptorEntry)
  {
    Map localMap = Utils.getTLGlobals().getMemberEntries();
    String str = MemberEntry.stringValueOf(paramByte, paramClassEntry, paramDescriptorEntry);
    MemberEntry localMemberEntry = (MemberEntry)localMap.get(str);
    if (localMemberEntry == null)
    {
      localMemberEntry = new MemberEntry(paramByte, paramClassEntry, paramDescriptorEntry);
      assert (localMemberEntry.stringValue().equals(str)) : (localMemberEntry.stringValue() + " != " + str);
      localMap.put(str, localMemberEntry);
    }
    return localMemberEntry;
  }
  
  public static MethodHandleEntry getMethodHandleEntry(byte paramByte, MemberEntry paramMemberEntry)
  {
    Map localMap = Utils.getTLGlobals().getMethodHandleEntries();
    String str = MethodHandleEntry.stringValueOf(paramByte, paramMemberEntry);
    MethodHandleEntry localMethodHandleEntry = (MethodHandleEntry)localMap.get(str);
    if (localMethodHandleEntry == null)
    {
      localMethodHandleEntry = new MethodHandleEntry(paramByte, paramMemberEntry);
      assert (localMethodHandleEntry.stringValue().equals(str));
      localMap.put(str, localMethodHandleEntry);
    }
    return localMethodHandleEntry;
  }
  
  public static MethodTypeEntry getMethodTypeEntry(SignatureEntry paramSignatureEntry)
  {
    Map localMap = Utils.getTLGlobals().getMethodTypeEntries();
    String str = paramSignatureEntry.stringValue();
    MethodTypeEntry localMethodTypeEntry = (MethodTypeEntry)localMap.get(str);
    if (localMethodTypeEntry == null)
    {
      localMethodTypeEntry = new MethodTypeEntry(paramSignatureEntry);
      assert (localMethodTypeEntry.stringValue().equals(str));
      localMap.put(str, localMethodTypeEntry);
    }
    return localMethodTypeEntry;
  }
  
  public static MethodTypeEntry getMethodTypeEntry(Utf8Entry paramUtf8Entry)
  {
    return getMethodTypeEntry(getSignatureEntry(paramUtf8Entry.stringValue()));
  }
  
  public static InvokeDynamicEntry getInvokeDynamicEntry(BootstrapMethodEntry paramBootstrapMethodEntry, DescriptorEntry paramDescriptorEntry)
  {
    Map localMap = Utils.getTLGlobals().getInvokeDynamicEntries();
    String str = InvokeDynamicEntry.stringValueOf(paramBootstrapMethodEntry, paramDescriptorEntry);
    InvokeDynamicEntry localInvokeDynamicEntry = (InvokeDynamicEntry)localMap.get(str);
    if (localInvokeDynamicEntry == null)
    {
      localInvokeDynamicEntry = new InvokeDynamicEntry(paramBootstrapMethodEntry, paramDescriptorEntry);
      assert (localInvokeDynamicEntry.stringValue().equals(str));
      localMap.put(str, localInvokeDynamicEntry);
    }
    return localInvokeDynamicEntry;
  }
  
  public static BootstrapMethodEntry getBootstrapMethodEntry(MethodHandleEntry paramMethodHandleEntry, Entry[] paramArrayOfEntry)
  {
    Map localMap = Utils.getTLGlobals().getBootstrapMethodEntries();
    String str = BootstrapMethodEntry.stringValueOf(paramMethodHandleEntry, paramArrayOfEntry);
    BootstrapMethodEntry localBootstrapMethodEntry = (BootstrapMethodEntry)localMap.get(str);
    if (localBootstrapMethodEntry == null)
    {
      localBootstrapMethodEntry = new BootstrapMethodEntry(paramMethodHandleEntry, paramArrayOfEntry);
      assert (localBootstrapMethodEntry.stringValue().equals(str));
      localMap.put(str, localBootstrapMethodEntry);
    }
    return localBootstrapMethodEntry;
  }
  
  static boolean isMemberTag(byte paramByte)
  {
    switch (paramByte)
    {
    case 9: 
    case 10: 
    case 11: 
      return true;
    }
    return false;
  }
  
  static byte numberTagOf(Number paramNumber)
  {
    if ((paramNumber instanceof Integer)) {
      return 3;
    }
    if ((paramNumber instanceof Float)) {
      return 4;
    }
    if ((paramNumber instanceof Long)) {
      return 5;
    }
    if ((paramNumber instanceof Double)) {
      return 6;
    }
    throw new RuntimeException("bad literal value " + paramNumber);
  }
  
  static boolean isRefKind(byte paramByte)
  {
    return (1 <= paramByte) && (paramByte <= 9);
  }
  
  static String qualifiedStringValue(Entry paramEntry1, Entry paramEntry2)
  {
    return qualifiedStringValue(paramEntry1.stringValue(), paramEntry2.stringValue());
  }
  
  static String qualifiedStringValue(String paramString1, String paramString2)
  {
    assert (paramString1.indexOf(".") < 0);
    return paramString1 + "." + paramString2;
  }
  
  static int compareSignatures(String paramString1, String paramString2)
  {
    return compareSignatures(paramString1, paramString2, null, null);
  }
  
  static int compareSignatures(String paramString1, String paramString2, String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    int i = paramString1.charAt(0);
    int j = paramString2.charAt(0);
    if ((i != 40) && (j == 40)) {
      return -1;
    }
    if ((j != 40) && (i == 40)) {
      return 1;
    }
    if (paramArrayOfString1 == null) {
      paramArrayOfString1 = structureSignature(paramString1);
    }
    if (paramArrayOfString2 == null) {
      paramArrayOfString2 = structureSignature(paramString2);
    }
    if (paramArrayOfString1.length != paramArrayOfString2.length) {
      return paramArrayOfString1.length - paramArrayOfString2.length;
    }
    int k = paramArrayOfString1.length;
    int m = k;
    for (;;)
    {
      m--;
      if (m < 0) {
        break;
      }
      int n = paramArrayOfString1[m].compareTo(paramArrayOfString2[m]);
      if (n != 0) {
        return n;
      }
    }
    assert (paramString1.equals(paramString2));
    return 0;
  }
  
  static int countClassParts(Utf8Entry paramUtf8Entry)
  {
    int i = 0;
    String str = paramUtf8Entry.stringValue();
    for (int j = 0; j < str.length(); j++) {
      if (str.charAt(j) == 'L') {
        i++;
      }
    }
    return i;
  }
  
  static String flattenSignature(String[] paramArrayOfString)
  {
    String str1 = paramArrayOfString[0];
    if (paramArrayOfString.length == 1) {
      return str1;
    }
    int i = str1.length();
    for (int j = 1; j < paramArrayOfString.length; j++) {
      i += paramArrayOfString[j].length();
    }
    char[] arrayOfChar = new char[i];
    int k = 0;
    int m = 1;
    for (int n = 0; n < str1.length(); n++)
    {
      int i1 = str1.charAt(n);
      arrayOfChar[(k++)] = i1;
      if (i1 == 76)
      {
        String str2 = paramArrayOfString[(m++)];
        str2.getChars(0, str2.length(), arrayOfChar, k);
        k += str2.length();
      }
    }
    assert (k == i);
    assert (m == paramArrayOfString.length);
    return new String(arrayOfChar);
  }
  
  private static int skipTo(char paramChar, String paramString, int paramInt)
  {
    paramInt = paramString.indexOf(paramChar, paramInt);
    return paramInt >= 0 ? paramInt : paramString.length();
  }
  
  static String[] structureSignature(String paramString)
  {
    int i = paramString.indexOf('L');
    if (i < 0)
    {
      localObject = new String[] { paramString };
      return (String[])localObject;
    }
    Object localObject = null;
    String[] arrayOfString = null;
    for (int j = 0; j <= 1; j++)
    {
      int k = 0;
      int m = 1;
      int n = 0;
      int i1 = 0;
      int i2 = 0;
      int i4;
      for (int i3 = i + 1; i3 > 0; i3 = paramString.indexOf('L', i4) + 1)
      {
        if (n < i3) {
          n = skipTo(';', paramString, i3);
        }
        if (i1 < i3) {
          i1 = skipTo('<', paramString, i3);
        }
        i4 = n < i1 ? n : i1;
        if (j != 0)
        {
          paramString.getChars(i2, i3, (char[])localObject, k);
          arrayOfString[m] = paramString.substring(i3, i4);
        }
        k += i3 - i2;
        m++;
        i2 = i4;
      }
      if (j != 0)
      {
        paramString.getChars(i2, paramString.length(), (char[])localObject, k);
        break;
      }
      k += paramString.length() - i2;
      localObject = new char[k];
      arrayOfString = new String[m];
    }
    arrayOfString[0] = new String((char[])localObject);
    return arrayOfString;
  }
  
  public static Index makeIndex(String paramString, Entry[] paramArrayOfEntry)
  {
    return new Index(paramString, paramArrayOfEntry);
  }
  
  public static Index makeIndex(String paramString, Collection<Entry> paramCollection)
  {
    return new Index(paramString, paramCollection);
  }
  
  public static void sort(Index paramIndex)
  {
    paramIndex.clearIndex();
    Arrays.sort(cpMap);
    if (verbose() > 2) {
      System.out.println("sorted " + paramIndex.dumpString());
    }
  }
  
  public static Index[] partition(Index paramIndex, int[] paramArrayOfInt)
  {
    ArrayList localArrayList = new ArrayList();
    Entry[] arrayOfEntry = cpMap;
    assert (paramArrayOfInt.length == arrayOfEntry.length);
    Object localObject;
    for (int i = 0; i < paramArrayOfInt.length; i++)
    {
      j = paramArrayOfInt[i];
      if (j >= 0)
      {
        while (j >= localArrayList.size()) {
          localArrayList.add(null);
        }
        localObject = (List)localArrayList.get(j);
        if (localObject == null) {
          localArrayList.set(j, localObject = new ArrayList());
        }
        ((List)localObject).add(arrayOfEntry[i]);
      }
    }
    Index[] arrayOfIndex = new Index[localArrayList.size()];
    for (int j = 0; j < arrayOfIndex.length; j++)
    {
      localObject = (List)localArrayList.get(j);
      if (localObject != null)
      {
        arrayOfIndex[j] = new Index(debugName + "/part#" + j, (Collection)localObject);
        assert (arrayOfIndex[j].indexOf((Entry)((List)localObject).get(0)) == 0);
      }
    }
    return arrayOfIndex;
  }
  
  public static Index[] partitionByTag(Index paramIndex)
  {
    Entry[] arrayOfEntry = cpMap;
    int[] arrayOfInt = new int[arrayOfEntry.length];
    for (int i = 0; i < arrayOfInt.length; i++)
    {
      Entry localEntry = arrayOfEntry[i];
      arrayOfInt[i] = (localEntry == null ? -1 : tag);
    }
    Object localObject = partition(paramIndex, arrayOfInt);
    for (int j = 0; j < localObject.length; j++) {
      if (localObject[j] != null) {
        debugName = tagName(j);
      }
    }
    if (localObject.length < 19)
    {
      Index[] arrayOfIndex = new Index[19];
      System.arraycopy(localObject, 0, arrayOfIndex, 0, localObject.length);
      localObject = arrayOfIndex;
    }
    return (Index[])localObject;
  }
  
  public static void completeReferencesIn(Set<Entry> paramSet, boolean paramBoolean)
  {
    completeReferencesIn(paramSet, paramBoolean, null);
  }
  
  public static void completeReferencesIn(Set<Entry> paramSet, boolean paramBoolean, List<BootstrapMethodEntry> paramList)
  {
    paramSet.remove(null);
    ListIterator localListIterator = new ArrayList(paramSet).listIterator(paramSet.size());
    while (localListIterator.hasPrevious())
    {
      Object localObject1 = (Entry)localListIterator.previous();
      localListIterator.remove();
      assert (localObject1 != null);
      Object localObject2;
      Object localObject3;
      if ((paramBoolean) && (tag == 13))
      {
        localObject2 = (SignatureEntry)localObject1;
        localObject3 = ((SignatureEntry)localObject2).asUtf8Entry();
        paramSet.remove(localObject2);
        paramSet.add(localObject3);
        localObject1 = localObject3;
      }
      if ((paramList != null) && (tag == 17))
      {
        localObject2 = (BootstrapMethodEntry)localObject1;
        paramSet.remove(localObject2);
        if (!paramList.contains(localObject2)) {
          paramList.add(localObject2);
        }
      }
      for (int i = 0;; i++)
      {
        localObject3 = ((Entry)localObject1).getRef(i);
        if (localObject3 == null) {
          break;
        }
        if (paramSet.add(localObject3)) {
          localListIterator.add(localObject3);
        }
      }
    }
  }
  
  static double percent(int paramInt1, int paramInt2)
  {
    return (int)(10000.0D * paramInt1 / paramInt2 + 0.5D) / 100.0D;
  }
  
  public static String tagName(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
      return "Utf8";
    case 3: 
      return "Integer";
    case 4: 
      return "Float";
    case 5: 
      return "Long";
    case 6: 
      return "Double";
    case 7: 
      return "Class";
    case 8: 
      return "String";
    case 9: 
      return "Fieldref";
    case 10: 
      return "Methodref";
    case 11: 
      return "InterfaceMethodref";
    case 12: 
      return "NameandType";
    case 15: 
      return "MethodHandle";
    case 16: 
      return "MethodType";
    case 18: 
      return "InvokeDynamic";
    case 50: 
      return "**All";
    case 0: 
      return "**None";
    case 51: 
      return "**LoadableValue";
    case 52: 
      return "**AnyMember";
    case 53: 
      return "*FieldSpecific";
    case 13: 
      return "*Signature";
    case 17: 
      return "*BootstrapMethod";
    }
    return "tag#" + paramInt;
  }
  
  public static String refKindName(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
      return "getField";
    case 2: 
      return "getStatic";
    case 3: 
      return "putField";
    case 4: 
      return "putStatic";
    case 5: 
      return "invokeVirtual";
    case 6: 
      return "invokeStatic";
    case 7: 
      return "invokeSpecial";
    case 8: 
      return "newInvokeSpecial";
    case 9: 
      return "invokeInterface";
    }
    return "refKind#" + paramInt;
  }
  
  private static boolean verifyTagOrder(byte[] paramArrayOfByte)
  {
    int i = -1;
    for (int m : paramArrayOfByte)
    {
      int n = TAG_ORDER[m];
      assert (n > 0) : ("tag not found: " + m);
      assert (TAGS_IN_ORDER[(n - 1)] == m) : ("tag repeated: " + m + " => " + n + " => " + TAGS_IN_ORDER[(n - 1)]);
      assert (i < n) : ("tags not in order: " + Arrays.toString(paramArrayOfByte) + " at " + m);
      i = n;
    }
    return true;
  }
  
  static
  {
    noRefs = new Entry[0];
    noClassRefs = new ClassEntry[0];
    TAGS_IN_ORDER = new byte[] { 1, 3, 4, 5, 6, 8, 7, 13, 12, 9, 10, 11, 15, 16, 17, 18 };
    TAG_ORDER = new byte[19];
    for (int i = 0; i < TAGS_IN_ORDER.length; i++) {
      TAG_ORDER[TAGS_IN_ORDER[i]] = ((byte)(i + 1));
    }
    NUMBER_TAGS = new byte[] { 3, 4, 5, 6 };
    EXTRA_TAGS = new byte[] { 15, 16, 17, 18 };
    LOADABLE_VALUE_TAGS = new byte[] { 3, 4, 5, 6, 8, 7, 15, 16 };
    ANY_MEMBER_TAGS = new byte[] { 9, 10, 11 };
    FIELD_SPECIFIC_TAGS = new byte[] { 3, 4, 5, 6, 8 };
    assert ((verifyTagOrder(TAGS_IN_ORDER)) && (verifyTagOrder(NUMBER_TAGS)) && (verifyTagOrder(EXTRA_TAGS)) && (verifyTagOrder(LOADABLE_VALUE_TAGS)) && (verifyTagOrder(ANY_MEMBER_TAGS)) && (verifyTagOrder(FIELD_SPECIFIC_TAGS)));
  }
  
  public static class BootstrapMethodEntry
    extends ConstantPool.Entry
  {
    final ConstantPool.MethodHandleEntry bsmRef;
    final ConstantPool.Entry[] argRefs;
    
    public ConstantPool.Entry getRef(int paramInt)
    {
      if (paramInt == 0) {
        return bsmRef;
      }
      if (paramInt - 1 < argRefs.length) {
        return argRefs[(paramInt - 1)];
      }
      return null;
    }
    
    protected int computeValueHash()
    {
      int i = bsmRef.hashCode();
      return Arrays.hashCode(argRefs) + (i << 8) ^ i;
    }
    
    BootstrapMethodEntry(ConstantPool.MethodHandleEntry paramMethodHandleEntry, ConstantPool.Entry[] paramArrayOfEntry)
    {
      super();
      bsmRef = paramMethodHandleEntry;
      argRefs = ((ConstantPool.Entry[])paramArrayOfEntry.clone());
      hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject == null) || (paramObject.getClass() != BootstrapMethodEntry.class)) {
        return false;
      }
      BootstrapMethodEntry localBootstrapMethodEntry = (BootstrapMethodEntry)paramObject;
      return (bsmRef.eq(bsmRef)) && (Arrays.equals(argRefs, argRefs));
    }
    
    public int compareTo(Object paramObject)
    {
      int i = superCompareTo(paramObject);
      if (i == 0)
      {
        BootstrapMethodEntry localBootstrapMethodEntry = (BootstrapMethodEntry)paramObject;
        if (Utils.SORT_BSS_BSM_MAJOR) {
          i = bsmRef.compareTo(bsmRef);
        }
        if (i == 0) {
          i = compareArgArrays(argRefs, argRefs);
        }
        if (i == 0) {
          i = bsmRef.compareTo(bsmRef);
        }
      }
      return i;
    }
    
    public String stringValue()
    {
      return stringValueOf(bsmRef, argRefs);
    }
    
    static String stringValueOf(ConstantPool.MethodHandleEntry paramMethodHandleEntry, ConstantPool.Entry[] paramArrayOfEntry)
    {
      StringBuffer localStringBuffer = new StringBuffer(paramMethodHandleEntry.stringValue());
      char c = '<';
      int i = 0;
      for (ConstantPool.Entry localEntry : paramArrayOfEntry)
      {
        localStringBuffer.append(c).append(localEntry.stringValue());
        c = ';';
      }
      if (c == '<') {
        localStringBuffer.append(c);
      }
      localStringBuffer.append('>');
      return localStringBuffer.toString();
    }
    
    static int compareArgArrays(ConstantPool.Entry[] paramArrayOfEntry1, ConstantPool.Entry[] paramArrayOfEntry2)
    {
      int i = paramArrayOfEntry1.length - paramArrayOfEntry2.length;
      if (i != 0) {
        return i;
      }
      for (int j = 0; j < paramArrayOfEntry1.length; j++)
      {
        i = paramArrayOfEntry1[j].compareTo(paramArrayOfEntry2[j]);
        if (i != 0) {
          break;
        }
      }
      return i;
    }
  }
  
  public static class ClassEntry
    extends ConstantPool.Entry
  {
    final ConstantPool.Utf8Entry ref;
    
    public ConstantPool.Entry getRef(int paramInt)
    {
      return paramInt == 0 ? ref : null;
    }
    
    protected int computeValueHash()
    {
      return ref.hashCode() + tag;
    }
    
    ClassEntry(ConstantPool.Entry paramEntry)
    {
      super();
      ref = ((ConstantPool.Utf8Entry)paramEntry);
      hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      return (paramObject != null) && (paramObject.getClass() == ClassEntry.class) && (ref.eq(ref));
    }
    
    public int compareTo(Object paramObject)
    {
      int i = superCompareTo(paramObject);
      if (i == 0) {
        i = ref.compareTo(ref);
      }
      return i;
    }
    
    public String stringValue()
    {
      return ref.stringValue();
    }
  }
  
  public static class DescriptorEntry
    extends ConstantPool.Entry
  {
    final ConstantPool.Utf8Entry nameRef;
    final ConstantPool.SignatureEntry typeRef;
    
    public ConstantPool.Entry getRef(int paramInt)
    {
      if (paramInt == 0) {
        return nameRef;
      }
      if (paramInt == 1) {
        return typeRef;
      }
      return null;
    }
    
    DescriptorEntry(ConstantPool.Entry paramEntry1, ConstantPool.Entry paramEntry2)
    {
      super();
      if ((paramEntry2 instanceof ConstantPool.Utf8Entry)) {
        paramEntry2 = ConstantPool.getSignatureEntry(paramEntry2.stringValue());
      }
      nameRef = ((ConstantPool.Utf8Entry)paramEntry1);
      typeRef = ((ConstantPool.SignatureEntry)paramEntry2);
      hashCode();
    }
    
    protected int computeValueHash()
    {
      int i = typeRef.hashCode();
      return nameRef.hashCode() + (i << 8) ^ i;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject == null) || (paramObject.getClass() != DescriptorEntry.class)) {
        return false;
      }
      DescriptorEntry localDescriptorEntry = (DescriptorEntry)paramObject;
      return (nameRef.eq(nameRef)) && (typeRef.eq(typeRef));
    }
    
    public int compareTo(Object paramObject)
    {
      int i = superCompareTo(paramObject);
      if (i == 0)
      {
        DescriptorEntry localDescriptorEntry = (DescriptorEntry)paramObject;
        i = typeRef.compareTo(typeRef);
        if (i == 0) {
          i = nameRef.compareTo(nameRef);
        }
      }
      return i;
    }
    
    public String stringValue()
    {
      return stringValueOf(nameRef, typeRef);
    }
    
    static String stringValueOf(ConstantPool.Entry paramEntry1, ConstantPool.Entry paramEntry2)
    {
      return ConstantPool.qualifiedStringValue(paramEntry2, paramEntry1);
    }
    
    public String prettyString()
    {
      return nameRef.stringValue() + typeRef.prettyString();
    }
    
    public boolean isMethod()
    {
      return typeRef.isMethod();
    }
    
    public byte getLiteralTag()
    {
      return typeRef.getLiteralTag();
    }
  }
  
  public static abstract class Entry
    implements Comparable<Object>
  {
    protected final byte tag;
    protected int valueHash;
    
    protected Entry(byte paramByte)
    {
      tag = paramByte;
    }
    
    public final byte getTag()
    {
      return tag;
    }
    
    public final boolean tagEquals(int paramInt)
    {
      return getTag() == paramInt;
    }
    
    public Entry getRef(int paramInt)
    {
      return null;
    }
    
    public boolean eq(Entry paramEntry)
    {
      assert (paramEntry != null);
      return (this == paramEntry) || (equals(paramEntry));
    }
    
    public abstract boolean equals(Object paramObject);
    
    public final int hashCode()
    {
      if (valueHash == 0)
      {
        valueHash = computeValueHash();
        if (valueHash == 0) {
          valueHash = 1;
        }
      }
      return valueHash;
    }
    
    protected abstract int computeValueHash();
    
    public abstract int compareTo(Object paramObject);
    
    protected int superCompareTo(Object paramObject)
    {
      Entry localEntry = (Entry)paramObject;
      if (tag != tag) {
        return ConstantPool.TAG_ORDER[tag] - ConstantPool.TAG_ORDER[tag];
      }
      return 0;
    }
    
    public final boolean isDoubleWord()
    {
      return (tag == 6) || (tag == 5);
    }
    
    public final boolean tagMatches(int paramInt)
    {
      if (tag == paramInt) {
        return true;
      }
      byte[] arrayOfByte1;
      switch (paramInt)
      {
      case 50: 
        return true;
      case 13: 
        return tag == 1;
      case 51: 
        arrayOfByte1 = ConstantPool.LOADABLE_VALUE_TAGS;
        break;
      case 52: 
        arrayOfByte1 = ConstantPool.ANY_MEMBER_TAGS;
        break;
      case 53: 
        arrayOfByte1 = ConstantPool.FIELD_SPECIFIC_TAGS;
        break;
      default: 
        return false;
      }
      for (int k : arrayOfByte1) {
        if (k == tag) {
          return true;
        }
      }
      return false;
    }
    
    public String toString()
    {
      String str = stringValue();
      if (ConstantPool.verbose() > 4)
      {
        if (valueHash != 0) {
          str = str + " hash=" + valueHash;
        }
        str = str + " id=" + System.identityHashCode(this);
      }
      return ConstantPool.tagName(tag) + "=" + str;
    }
    
    public abstract String stringValue();
  }
  
  public static final class Index
    extends AbstractList<ConstantPool.Entry>
  {
    protected String debugName;
    protected ConstantPool.Entry[] cpMap;
    protected boolean flattenSigs;
    protected ConstantPool.Entry[] indexKey;
    protected int[] indexValue;
    
    protected ConstantPool.Entry[] getMap()
    {
      return cpMap;
    }
    
    protected Index(String paramString)
    {
      debugName = paramString;
    }
    
    protected Index(String paramString, ConstantPool.Entry[] paramArrayOfEntry)
    {
      this(paramString);
      setMap(paramArrayOfEntry);
    }
    
    protected void setMap(ConstantPool.Entry[] paramArrayOfEntry)
    {
      clearIndex();
      cpMap = paramArrayOfEntry;
    }
    
    protected Index(String paramString, Collection<ConstantPool.Entry> paramCollection)
    {
      this(paramString);
      setMap(paramCollection);
    }
    
    protected void setMap(Collection<ConstantPool.Entry> paramCollection)
    {
      cpMap = new ConstantPool.Entry[paramCollection.size()];
      paramCollection.toArray(cpMap);
      setMap(cpMap);
    }
    
    public int size()
    {
      return cpMap.length;
    }
    
    public ConstantPool.Entry get(int paramInt)
    {
      return cpMap[paramInt];
    }
    
    public ConstantPool.Entry getEntry(int paramInt)
    {
      return cpMap[paramInt];
    }
    
    private int findIndexOf(ConstantPool.Entry paramEntry)
    {
      if (indexKey == null) {
        initializeIndex();
      }
      int i = findIndexLocation(paramEntry);
      if (indexKey[i] != paramEntry)
      {
        if ((flattenSigs) && (tag == 13))
        {
          ConstantPool.SignatureEntry localSignatureEntry = (ConstantPool.SignatureEntry)paramEntry;
          return findIndexOf(localSignatureEntry.asUtf8Entry());
        }
        return -1;
      }
      int j = indexValue[i];
      assert (paramEntry.equals(cpMap[j]));
      return j;
    }
    
    public boolean contains(ConstantPool.Entry paramEntry)
    {
      return findIndexOf(paramEntry) >= 0;
    }
    
    public int indexOf(ConstantPool.Entry paramEntry)
    {
      int i = findIndexOf(paramEntry);
      if ((i < 0) && (ConstantPool.verbose() > 0))
      {
        System.out.println("not found: " + paramEntry);
        System.out.println("       in: " + dumpString());
        Thread.dumpStack();
      }
      assert (i >= 0);
      return i;
    }
    
    public int lastIndexOf(ConstantPool.Entry paramEntry)
    {
      return indexOf(paramEntry);
    }
    
    public boolean assertIsSorted()
    {
      for (int i = 1; i < cpMap.length; i++) {
        if (cpMap[(i - 1)].compareTo(cpMap[i]) > 0)
        {
          System.out.println("Not sorted at " + (i - 1) + "/" + i + ": " + dumpString());
          return false;
        }
      }
      return true;
    }
    
    protected void clearIndex()
    {
      indexKey = null;
      indexValue = null;
    }
    
    private int findIndexLocation(ConstantPool.Entry paramEntry)
    {
      int i = indexKey.length;
      int j = paramEntry.hashCode();
      int k = j & i - 1;
      int m = (j >>> 8 | 0x1) & i - 1;
      for (;;)
      {
        ConstantPool.Entry localEntry = indexKey[k];
        if ((localEntry == paramEntry) || (localEntry == null)) {
          return k;
        }
        k += m;
        if (k >= i) {
          k -= i;
        }
      }
    }
    
    private void initializeIndex()
    {
      if (ConstantPool.verbose() > 2) {
        System.out.println("initialize Index " + debugName + " [" + size() + "]");
      }
      int i = (int)((cpMap.length + 10) * 1.5D);
      int j = 1;
      while (j < i) {
        j <<= 1;
      }
      indexKey = new ConstantPool.Entry[j];
      indexValue = new int[j];
      for (int k = 0; k < cpMap.length; k++)
      {
        ConstantPool.Entry localEntry = cpMap[k];
        if (localEntry != null)
        {
          int m = findIndexLocation(localEntry);
          assert (indexKey[m] == null);
          indexKey[m] = localEntry;
          indexValue[m] = k;
        }
      }
    }
    
    public ConstantPool.Entry[] toArray(ConstantPool.Entry[] paramArrayOfEntry)
    {
      int i = size();
      if (paramArrayOfEntry.length < i) {
        return (ConstantPool.Entry[])super.toArray(paramArrayOfEntry);
      }
      System.arraycopy(cpMap, 0, paramArrayOfEntry, 0, i);
      if (paramArrayOfEntry.length > i) {
        paramArrayOfEntry[i] = null;
      }
      return paramArrayOfEntry;
    }
    
    public ConstantPool.Entry[] toArray()
    {
      return toArray(new ConstantPool.Entry[size()]);
    }
    
    public Object clone()
    {
      return new Index(debugName, (ConstantPool.Entry[])cpMap.clone());
    }
    
    public String toString()
    {
      return "Index " + debugName + " [" + size() + "]";
    }
    
    public String dumpString()
    {
      String str = toString();
      str = str + " {\n";
      for (int i = 0; i < cpMap.length; i++) {
        str = str + "    " + i + ": " + cpMap[i] + "\n";
      }
      str = str + "}";
      return str;
    }
  }
  
  public static class IndexGroup
  {
    private ConstantPool.Index[] indexByTag = new ConstantPool.Index[19];
    private ConstantPool.Index[] indexByTagGroup;
    private int[] untypedFirstIndexByTag;
    private int totalSizeQQ;
    private ConstantPool.Index[][] indexByTagAndClass;
    
    public IndexGroup() {}
    
    private ConstantPool.Index makeTagGroupIndex(byte paramByte, byte[] paramArrayOfByte)
    {
      if (indexByTagGroup == null) {
        indexByTagGroup = new ConstantPool.Index[4];
      }
      int i = paramByte - 50;
      assert (indexByTagGroup[i] == null);
      int j = 0;
      Object localObject = null;
      for (int k = 1; k <= 2; k++)
      {
        untypedIndexOf(null);
        for (int i1 : paramArrayOfByte)
        {
          ConstantPool.Index localIndex = indexByTag[i1];
          if (localIndex != null)
          {
            int i2 = cpMap.length;
            if (i2 != 0)
            {
              assert (paramByte == 50 ? j == untypedFirstIndexByTag[i1] : j < untypedFirstIndexByTag[i1]);
              if (localObject != null)
              {
                assert (localObject[j] == null);
                assert (localObject[(j + i2 - 1)] == null);
                System.arraycopy(cpMap, 0, localObject, j, i2);
              }
              j += i2;
            }
          }
        }
        if (localObject == null)
        {
          assert (k == 1);
          localObject = new ConstantPool.Entry[j];
          j = 0;
        }
      }
      indexByTagGroup[i] = new ConstantPool.Index(ConstantPool.tagName(paramByte), (ConstantPool.Entry[])localObject);
      return indexByTagGroup[i];
    }
    
    public int untypedIndexOf(ConstantPool.Entry paramEntry)
    {
      if (untypedFirstIndexByTag == null)
      {
        untypedFirstIndexByTag = new int[20];
        i = 0;
        for (int j = 0; j < ConstantPool.TAGS_IN_ORDER.length; j++)
        {
          k = ConstantPool.TAGS_IN_ORDER[j];
          ConstantPool.Index localIndex2 = indexByTag[k];
          if (localIndex2 != null)
          {
            int m = cpMap.length;
            untypedFirstIndexByTag[k] = i;
            i += m;
          }
        }
        untypedFirstIndexByTag[19] = i;
      }
      if (paramEntry == null) {
        return -1;
      }
      int i = tag;
      ConstantPool.Index localIndex1 = indexByTag[i];
      if (localIndex1 == null) {
        return -1;
      }
      int k = localIndex1.findIndexOf(paramEntry);
      if (k >= 0) {
        k += untypedFirstIndexByTag[i];
      }
      return k;
    }
    
    public void initIndexByTag(byte paramByte, ConstantPool.Index paramIndex)
    {
      assert (indexByTag[paramByte] == null);
      ConstantPool.Entry[] arrayOfEntry = cpMap;
      for (int i = 0; i < arrayOfEntry.length; i++) {
        assert (tag == paramByte);
      }
      if ((paramByte == 1) && (!$assertionsDisabled) && (arrayOfEntry.length != 0) && (!arrayOfEntry[0].stringValue().equals(""))) {
        throw new AssertionError();
      }
      indexByTag[paramByte] = paramIndex;
      untypedFirstIndexByTag = null;
      indexByTagGroup = null;
      if (indexByTagAndClass != null) {
        indexByTagAndClass[paramByte] = null;
      }
    }
    
    public ConstantPool.Index getIndexByTag(byte paramByte)
    {
      if (paramByte >= 50) {
        return getIndexByTagGroup(paramByte);
      }
      ConstantPool.Index localIndex = indexByTag[paramByte];
      if (localIndex == null)
      {
        localIndex = new ConstantPool.Index(ConstantPool.tagName(paramByte), new ConstantPool.Entry[0]);
        indexByTag[paramByte] = localIndex;
      }
      return localIndex;
    }
    
    private ConstantPool.Index getIndexByTagGroup(byte paramByte)
    {
      if (indexByTagGroup != null)
      {
        ConstantPool.Index localIndex = indexByTagGroup[(paramByte - 50)];
        if (localIndex != null) {
          return localIndex;
        }
      }
      switch (paramByte)
      {
      case 50: 
        return makeTagGroupIndex((byte)50, ConstantPool.TAGS_IN_ORDER);
      case 51: 
        return makeTagGroupIndex((byte)51, ConstantPool.LOADABLE_VALUE_TAGS);
      case 52: 
        return makeTagGroupIndex((byte)52, ConstantPool.ANY_MEMBER_TAGS);
      case 53: 
        return null;
      }
      throw new AssertionError("bad tag group " + paramByte);
    }
    
    public ConstantPool.Index getMemberIndex(byte paramByte, ConstantPool.ClassEntry paramClassEntry)
    {
      if (paramClassEntry == null) {
        throw new RuntimeException("missing class reference for " + ConstantPool.tagName(paramByte));
      }
      if (indexByTagAndClass == null) {
        indexByTagAndClass = new ConstantPool.Index[19][];
      }
      ConstantPool.Index localIndex1 = getIndexByTag((byte)7);
      ConstantPool.Index[] arrayOfIndex = indexByTagAndClass[paramByte];
      if (arrayOfIndex == null)
      {
        ConstantPool.Index localIndex2 = getIndexByTag(paramByte);
        int[] arrayOfInt = new int[localIndex2.size()];
        for (int j = 0; j < arrayOfInt.length; j++)
        {
          ConstantPool.MemberEntry localMemberEntry = (ConstantPool.MemberEntry)localIndex2.get(j);
          int k = localIndex1.indexOf(classRef);
          arrayOfInt[j] = k;
        }
        arrayOfIndex = ConstantPool.partition(localIndex2, arrayOfInt);
        for (j = 0; j < arrayOfIndex.length; j++) {
          assert ((arrayOfIndex[j] == null) || (arrayOfIndex[j].assertIsSorted()));
        }
        indexByTagAndClass[paramByte] = arrayOfIndex;
      }
      int i = localIndex1.indexOf(paramClassEntry);
      return arrayOfIndex[i];
    }
    
    public int getOverloadingIndex(ConstantPool.MemberEntry paramMemberEntry)
    {
      ConstantPool.Index localIndex = getMemberIndex(tag, classRef);
      ConstantPool.Utf8Entry localUtf8Entry = descRef.nameRef;
      int i = 0;
      for (int j = 0; j < cpMap.length; j++)
      {
        ConstantPool.MemberEntry localMemberEntry = (ConstantPool.MemberEntry)cpMap[j];
        if (localMemberEntry.equals(paramMemberEntry)) {
          return i;
        }
        if (descRef.nameRef.equals(localUtf8Entry)) {
          i++;
        }
      }
      throw new RuntimeException("should not reach here");
    }
    
    public ConstantPool.MemberEntry getOverloadingForIndex(byte paramByte, ConstantPool.ClassEntry paramClassEntry, String paramString, int paramInt)
    {
      assert (paramString.equals(paramString.intern()));
      ConstantPool.Index localIndex = getMemberIndex(paramByte, paramClassEntry);
      int i = 0;
      for (int j = 0; j < cpMap.length; j++)
      {
        ConstantPool.MemberEntry localMemberEntry = (ConstantPool.MemberEntry)cpMap[j];
        if (descRef.nameRef.stringValue().equals(paramString))
        {
          if (i == paramInt) {
            return localMemberEntry;
          }
          i++;
        }
      }
      throw new RuntimeException("should not reach here");
    }
    
    public boolean haveNumbers()
    {
      for (byte b : ConstantPool.NUMBER_TAGS) {
        if (getIndexByTag(b).size() > 0) {
          return true;
        }
      }
      return false;
    }
    
    public boolean haveExtraTags()
    {
      for (byte b : ConstantPool.EXTRA_TAGS) {
        if (getIndexByTag(b).size() > 0) {
          return true;
        }
      }
      return false;
    }
  }
  
  public static class InvokeDynamicEntry
    extends ConstantPool.Entry
  {
    final ConstantPool.BootstrapMethodEntry bssRef;
    final ConstantPool.DescriptorEntry descRef;
    
    public ConstantPool.Entry getRef(int paramInt)
    {
      if (paramInt == 0) {
        return bssRef;
      }
      if (paramInt == 1) {
        return descRef;
      }
      return null;
    }
    
    protected int computeValueHash()
    {
      int i = descRef.hashCode();
      return bssRef.hashCode() + (i << 8) ^ i;
    }
    
    InvokeDynamicEntry(ConstantPool.BootstrapMethodEntry paramBootstrapMethodEntry, ConstantPool.DescriptorEntry paramDescriptorEntry)
    {
      super();
      bssRef = paramBootstrapMethodEntry;
      descRef = paramDescriptorEntry;
      hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject == null) || (paramObject.getClass() != InvokeDynamicEntry.class)) {
        return false;
      }
      InvokeDynamicEntry localInvokeDynamicEntry = (InvokeDynamicEntry)paramObject;
      return (bssRef.eq(bssRef)) && (descRef.eq(descRef));
    }
    
    public int compareTo(Object paramObject)
    {
      int i = superCompareTo(paramObject);
      if (i == 0)
      {
        InvokeDynamicEntry localInvokeDynamicEntry = (InvokeDynamicEntry)paramObject;
        if (Utils.SORT_INDY_BSS_MAJOR) {
          i = bssRef.compareTo(bssRef);
        }
        if (i == 0) {
          i = descRef.compareTo(descRef);
        }
        if (i == 0) {
          i = bssRef.compareTo(bssRef);
        }
      }
      return i;
    }
    
    public String stringValue()
    {
      return stringValueOf(bssRef, descRef);
    }
    
    static String stringValueOf(ConstantPool.BootstrapMethodEntry paramBootstrapMethodEntry, ConstantPool.DescriptorEntry paramDescriptorEntry)
    {
      return "Indy:" + paramBootstrapMethodEntry.stringValue() + "." + paramDescriptorEntry.stringValue();
    }
  }
  
  public static abstract class LiteralEntry
    extends ConstantPool.Entry
  {
    protected LiteralEntry(byte paramByte)
    {
      super();
    }
    
    public abstract Comparable<?> literalValue();
  }
  
  public static class MemberEntry
    extends ConstantPool.Entry
  {
    final ConstantPool.ClassEntry classRef;
    final ConstantPool.DescriptorEntry descRef;
    
    public ConstantPool.Entry getRef(int paramInt)
    {
      if (paramInt == 0) {
        return classRef;
      }
      if (paramInt == 1) {
        return descRef;
      }
      return null;
    }
    
    protected int computeValueHash()
    {
      int i = descRef.hashCode();
      return classRef.hashCode() + (i << 8) ^ i;
    }
    
    MemberEntry(byte paramByte, ConstantPool.ClassEntry paramClassEntry, ConstantPool.DescriptorEntry paramDescriptorEntry)
    {
      super();
      assert (ConstantPool.isMemberTag(paramByte));
      classRef = paramClassEntry;
      descRef = paramDescriptorEntry;
      hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject == null) || (paramObject.getClass() != MemberEntry.class)) {
        return false;
      }
      MemberEntry localMemberEntry = (MemberEntry)paramObject;
      return (classRef.eq(classRef)) && (descRef.eq(descRef));
    }
    
    public int compareTo(Object paramObject)
    {
      int i = superCompareTo(paramObject);
      if (i == 0)
      {
        MemberEntry localMemberEntry = (MemberEntry)paramObject;
        if (Utils.SORT_MEMBERS_DESCR_MAJOR) {
          i = descRef.compareTo(descRef);
        }
        if (i == 0) {
          i = classRef.compareTo(classRef);
        }
        if (i == 0) {
          i = descRef.compareTo(descRef);
        }
      }
      return i;
    }
    
    public String stringValue()
    {
      return stringValueOf(tag, classRef, descRef);
    }
    
    static String stringValueOf(byte paramByte, ConstantPool.ClassEntry paramClassEntry, ConstantPool.DescriptorEntry paramDescriptorEntry)
    {
      assert (ConstantPool.isMemberTag(paramByte));
      String str;
      switch (paramByte)
      {
      case 9: 
        str = "Field:";
        break;
      case 10: 
        str = "Method:";
        break;
      case 11: 
        str = "IMethod:";
        break;
      default: 
        str = paramByte + "???";
      }
      return str + ConstantPool.qualifiedStringValue(paramClassEntry, paramDescriptorEntry);
    }
    
    public boolean isMethod()
    {
      return descRef.isMethod();
    }
  }
  
  public static class MethodHandleEntry
    extends ConstantPool.Entry
  {
    final int refKind;
    final ConstantPool.MemberEntry memRef;
    
    public ConstantPool.Entry getRef(int paramInt)
    {
      return paramInt == 0 ? memRef : null;
    }
    
    protected int computeValueHash()
    {
      int i = refKind;
      return memRef.hashCode() + (i << 8) ^ i;
    }
    
    MethodHandleEntry(byte paramByte, ConstantPool.MemberEntry paramMemberEntry)
    {
      super();
      assert (ConstantPool.isRefKind(paramByte));
      refKind = paramByte;
      memRef = paramMemberEntry;
      hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject == null) || (paramObject.getClass() != MethodHandleEntry.class)) {
        return false;
      }
      MethodHandleEntry localMethodHandleEntry = (MethodHandleEntry)paramObject;
      return (refKind == refKind) && (memRef.eq(memRef));
    }
    
    public int compareTo(Object paramObject)
    {
      int i = superCompareTo(paramObject);
      if (i == 0)
      {
        MethodHandleEntry localMethodHandleEntry = (MethodHandleEntry)paramObject;
        if (Utils.SORT_HANDLES_KIND_MAJOR) {
          i = refKind - refKind;
        }
        if (i == 0) {
          i = memRef.compareTo(memRef);
        }
        if (i == 0) {
          i = refKind - refKind;
        }
      }
      return i;
    }
    
    public static String stringValueOf(int paramInt, ConstantPool.MemberEntry paramMemberEntry)
    {
      return ConstantPool.refKindName(paramInt) + ":" + paramMemberEntry.stringValue();
    }
    
    public String stringValue()
    {
      return stringValueOf(refKind, memRef);
    }
  }
  
  public static class MethodTypeEntry
    extends ConstantPool.Entry
  {
    final ConstantPool.SignatureEntry typeRef;
    
    public ConstantPool.Entry getRef(int paramInt)
    {
      return paramInt == 0 ? typeRef : null;
    }
    
    protected int computeValueHash()
    {
      return typeRef.hashCode() + tag;
    }
    
    MethodTypeEntry(ConstantPool.SignatureEntry paramSignatureEntry)
    {
      super();
      typeRef = paramSignatureEntry;
      hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject == null) || (paramObject.getClass() != MethodTypeEntry.class)) {
        return false;
      }
      MethodTypeEntry localMethodTypeEntry = (MethodTypeEntry)paramObject;
      return typeRef.eq(typeRef);
    }
    
    public int compareTo(Object paramObject)
    {
      int i = superCompareTo(paramObject);
      if (i == 0)
      {
        MethodTypeEntry localMethodTypeEntry = (MethodTypeEntry)paramObject;
        i = typeRef.compareTo(typeRef);
      }
      return i;
    }
    
    public String stringValue()
    {
      return typeRef.stringValue();
    }
  }
  
  public static class NumberEntry
    extends ConstantPool.LiteralEntry
  {
    final Number value;
    
    NumberEntry(Number paramNumber)
    {
      super();
      value = paramNumber;
      hashCode();
    }
    
    protected int computeValueHash()
    {
      return value.hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      return (paramObject != null) && (paramObject.getClass() == NumberEntry.class) && (value.equals(value));
    }
    
    public int compareTo(Object paramObject)
    {
      int i = superCompareTo(paramObject);
      if (i == 0)
      {
        Comparable localComparable = (Comparable)value;
        i = localComparable.compareTo(value);
      }
      return i;
    }
    
    public Number numberValue()
    {
      return value;
    }
    
    public Comparable<?> literalValue()
    {
      return (Comparable)value;
    }
    
    public String stringValue()
    {
      return value.toString();
    }
  }
  
  public static class SignatureEntry
    extends ConstantPool.Entry
  {
    final ConstantPool.Utf8Entry formRef;
    final ConstantPool.ClassEntry[] classRefs;
    String value;
    ConstantPool.Utf8Entry asUtf8Entry;
    
    public ConstantPool.Entry getRef(int paramInt)
    {
      if (paramInt == 0) {
        return formRef;
      }
      return paramInt - 1 < classRefs.length ? classRefs[(paramInt - 1)] : null;
    }
    
    SignatureEntry(String paramString)
    {
      super();
      paramString = paramString.intern();
      value = paramString;
      String[] arrayOfString = ConstantPool.structureSignature(paramString);
      formRef = ConstantPool.getUtf8Entry(arrayOfString[0]);
      classRefs = new ConstantPool.ClassEntry[arrayOfString.length - 1];
      for (int i = 1; i < arrayOfString.length; i++) {
        classRefs[(i - 1)] = ConstantPool.getClassEntry(arrayOfString[i]);
      }
      hashCode();
    }
    
    protected int computeValueHash()
    {
      stringValue();
      return value.hashCode() + tag;
    }
    
    public ConstantPool.Utf8Entry asUtf8Entry()
    {
      if (asUtf8Entry == null) {
        asUtf8Entry = ConstantPool.getUtf8Entry(stringValue());
      }
      return asUtf8Entry;
    }
    
    public boolean equals(Object paramObject)
    {
      return (paramObject != null) && (paramObject.getClass() == SignatureEntry.class) && (value.equals(value));
    }
    
    public int compareTo(Object paramObject)
    {
      int i = superCompareTo(paramObject);
      if (i == 0)
      {
        SignatureEntry localSignatureEntry = (SignatureEntry)paramObject;
        i = ConstantPool.compareSignatures(value, value);
      }
      return i;
    }
    
    public String stringValue()
    {
      if (value == null) {
        value = stringValueOf(formRef, classRefs);
      }
      return value;
    }
    
    static String stringValueOf(ConstantPool.Utf8Entry paramUtf8Entry, ConstantPool.ClassEntry[] paramArrayOfClassEntry)
    {
      String[] arrayOfString = new String[1 + paramArrayOfClassEntry.length];
      arrayOfString[0] = paramUtf8Entry.stringValue();
      for (int i = 1; i < arrayOfString.length; i++) {
        arrayOfString[i] = paramArrayOfClassEntry[(i - 1)].stringValue();
      }
      return ConstantPool.flattenSignature(arrayOfString).intern();
    }
    
    public int computeSize(boolean paramBoolean)
    {
      String str = formRef.stringValue();
      int i = 0;
      int j = 1;
      if (isMethod())
      {
        i = 1;
        j = str.indexOf(')');
      }
      int k = 0;
      label154:
      for (int m = i; m < j; m++)
      {
        switch (str.charAt(m))
        {
        case 'D': 
        case 'J': 
          if (paramBoolean) {
            k++;
          }
          break;
        case '[': 
        case ';': 
        default: 
          while (str.charAt(m) == '[')
          {
            m++;
            continue;
            break label154;
            assert (0 <= "BSCIJFDZLV([".indexOf(str.charAt(m)));
          }
        }
        k++;
      }
      return k;
    }
    
    public boolean isMethod()
    {
      return formRef.stringValue().charAt(0) == '(';
    }
    
    public byte getLiteralTag()
    {
      switch (formRef.stringValue().charAt(0))
      {
      case 'I': 
        return 3;
      case 'J': 
        return 5;
      case 'F': 
        return 4;
      case 'D': 
        return 6;
      case 'B': 
      case 'C': 
      case 'S': 
      case 'Z': 
        return 3;
      case 'L': 
        return 8;
      }
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
      return 0;
    }
    
    public String prettyString()
    {
      if (isMethod())
      {
        str = formRef.stringValue();
        str = str.substring(0, 1 + str.indexOf(')'));
      }
      int i;
      for (String str = "/" + formRef.stringValue(); (i = str.indexOf(';')) >= 0; str = str.substring(0, i) + str.substring(i + 1)) {}
      return str;
    }
  }
  
  public static class StringEntry
    extends ConstantPool.LiteralEntry
  {
    final ConstantPool.Utf8Entry ref;
    
    public ConstantPool.Entry getRef(int paramInt)
    {
      return paramInt == 0 ? ref : null;
    }
    
    StringEntry(ConstantPool.Entry paramEntry)
    {
      super();
      ref = ((ConstantPool.Utf8Entry)paramEntry);
      hashCode();
    }
    
    protected int computeValueHash()
    {
      return ref.hashCode() + tag;
    }
    
    public boolean equals(Object paramObject)
    {
      return (paramObject != null) && (paramObject.getClass() == StringEntry.class) && (ref.eq(ref));
    }
    
    public int compareTo(Object paramObject)
    {
      int i = superCompareTo(paramObject);
      if (i == 0) {
        i = ref.compareTo(ref);
      }
      return i;
    }
    
    public Comparable<?> literalValue()
    {
      return ref.stringValue();
    }
    
    public String stringValue()
    {
      return ref.stringValue();
    }
  }
  
  public static class Utf8Entry
    extends ConstantPool.Entry
  {
    final String value;
    
    Utf8Entry(String paramString)
    {
      super();
      value = paramString.intern();
      hashCode();
    }
    
    protected int computeValueHash()
    {
      return value.hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      return (paramObject != null) && (paramObject.getClass() == Utf8Entry.class) && (value.equals(value));
    }
    
    public int compareTo(Object paramObject)
    {
      int i = superCompareTo(paramObject);
      if (i == 0) {
        i = value.compareTo(value);
      }
      return i;
    }
    
    public String stringValue()
    {
      return value;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\util\jar\pack\ConstantPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */