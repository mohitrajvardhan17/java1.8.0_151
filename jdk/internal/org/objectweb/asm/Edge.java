package jdk.internal.org.objectweb.asm;

class Edge
{
  static final int NORMAL = 0;
  static final int EXCEPTION = Integer.MAX_VALUE;
  int info;
  Label successor;
  Edge next;
  
  Edge() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\Edge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */