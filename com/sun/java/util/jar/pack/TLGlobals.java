package com.sun.java.util.jar.pack;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

class TLGlobals
{
  final PropMap props = new PropMap();
  private final Map<String, ConstantPool.Utf8Entry> utf8Entries = new HashMap();
  private final Map<String, ConstantPool.ClassEntry> classEntries = new HashMap();
  private final Map<Object, ConstantPool.LiteralEntry> literalEntries = new HashMap();
  private final Map<String, ConstantPool.SignatureEntry> signatureEntries = new HashMap();
  private final Map<String, ConstantPool.DescriptorEntry> descriptorEntries = new HashMap();
  private final Map<String, ConstantPool.MemberEntry> memberEntries = new HashMap();
  private final Map<String, ConstantPool.MethodHandleEntry> methodHandleEntries = new HashMap();
  private final Map<String, ConstantPool.MethodTypeEntry> methodTypeEntries = new HashMap();
  private final Map<String, ConstantPool.InvokeDynamicEntry> invokeDynamicEntries = new HashMap();
  private final Map<String, ConstantPool.BootstrapMethodEntry> bootstrapMethodEntries = new HashMap();
  
  TLGlobals() {}
  
  SortedMap<String, String> getPropMap()
  {
    return props;
  }
  
  Map<String, ConstantPool.Utf8Entry> getUtf8Entries()
  {
    return utf8Entries;
  }
  
  Map<String, ConstantPool.ClassEntry> getClassEntries()
  {
    return classEntries;
  }
  
  Map<Object, ConstantPool.LiteralEntry> getLiteralEntries()
  {
    return literalEntries;
  }
  
  Map<String, ConstantPool.DescriptorEntry> getDescriptorEntries()
  {
    return descriptorEntries;
  }
  
  Map<String, ConstantPool.SignatureEntry> getSignatureEntries()
  {
    return signatureEntries;
  }
  
  Map<String, ConstantPool.MemberEntry> getMemberEntries()
  {
    return memberEntries;
  }
  
  Map<String, ConstantPool.MethodHandleEntry> getMethodHandleEntries()
  {
    return methodHandleEntries;
  }
  
  Map<String, ConstantPool.MethodTypeEntry> getMethodTypeEntries()
  {
    return methodTypeEntries;
  }
  
  Map<String, ConstantPool.InvokeDynamicEntry> getInvokeDynamicEntries()
  {
    return invokeDynamicEntries;
  }
  
  Map<String, ConstantPool.BootstrapMethodEntry> getBootstrapMethodEntries()
  {
    return bootstrapMethodEntries;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\util\jar\pack\TLGlobals.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */