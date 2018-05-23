package jdk.internal.org.objectweb.asm;

class Context
{
  Attribute[] attrs;
  int flags;
  char[] buffer;
  int[] bootstrapMethods;
  int access;
  String name;
  String desc;
  Label[] labels;
  int typeRef;
  TypePath typePath;
  int offset;
  Label[] start;
  Label[] end;
  int[] index;
  int mode;
  int localCount;
  int localDiff;
  Object[] local;
  int stackCount;
  Object[] stack;
  
  Context() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\Context.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */