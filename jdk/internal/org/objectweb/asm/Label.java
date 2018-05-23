package jdk.internal.org.objectweb.asm;

public class Label
{
  static final int DEBUG = 1;
  static final int RESOLVED = 2;
  static final int RESIZED = 4;
  static final int PUSHED = 8;
  static final int TARGET = 16;
  static final int STORE = 32;
  static final int REACHABLE = 64;
  static final int JSR = 128;
  static final int RET = 256;
  static final int SUBROUTINE = 512;
  static final int VISITED = 1024;
  static final int VISITED2 = 2048;
  public Object info;
  int status;
  int line;
  int position;
  private int referenceCount;
  private int[] srcAndRefPositions;
  int inputStackTop;
  int outputStackMax;
  Frame frame;
  Label successor;
  Edge successors;
  Label next;
  
  public Label() {}
  
  public int getOffset()
  {
    if ((status & 0x2) == 0) {
      throw new IllegalStateException("Label offset position has not been resolved yet");
    }
    return position;
  }
  
  void put(MethodWriter paramMethodWriter, ByteVector paramByteVector, int paramInt, boolean paramBoolean)
  {
    if ((status & 0x2) == 0)
    {
      if (paramBoolean)
      {
        addReference(-1 - paramInt, length);
        paramByteVector.putInt(-1);
      }
      else
      {
        addReference(paramInt, length);
        paramByteVector.putShort(-1);
      }
    }
    else if (paramBoolean) {
      paramByteVector.putInt(position - paramInt);
    } else {
      paramByteVector.putShort(position - paramInt);
    }
  }
  
  private void addReference(int paramInt1, int paramInt2)
  {
    if (srcAndRefPositions == null) {
      srcAndRefPositions = new int[6];
    }
    if (referenceCount >= srcAndRefPositions.length)
    {
      int[] arrayOfInt = new int[srcAndRefPositions.length + 6];
      System.arraycopy(srcAndRefPositions, 0, arrayOfInt, 0, srcAndRefPositions.length);
      srcAndRefPositions = arrayOfInt;
    }
    srcAndRefPositions[(referenceCount++)] = paramInt1;
    srcAndRefPositions[(referenceCount++)] = paramInt2;
  }
  
  boolean resolve(MethodWriter paramMethodWriter, int paramInt, byte[] paramArrayOfByte)
  {
    boolean bool = false;
    status |= 0x2;
    position = paramInt;
    int i = 0;
    while (i < referenceCount)
    {
      int j = srcAndRefPositions[(i++)];
      int k = srcAndRefPositions[(i++)];
      int m;
      if (j >= 0)
      {
        m = paramInt - j;
        if ((m < 32768) || (m > 32767))
        {
          int n = paramArrayOfByte[(k - 1)] & 0xFF;
          if (n <= 168) {
            paramArrayOfByte[(k - 1)] = ((byte)(n + 49));
          } else {
            paramArrayOfByte[(k - 1)] = ((byte)(n + 20));
          }
          bool = true;
        }
        paramArrayOfByte[(k++)] = ((byte)(m >>> 8));
        paramArrayOfByte[k] = ((byte)m);
      }
      else
      {
        m = paramInt + j + 1;
        paramArrayOfByte[(k++)] = ((byte)(m >>> 24));
        paramArrayOfByte[(k++)] = ((byte)(m >>> 16));
        paramArrayOfByte[(k++)] = ((byte)(m >>> 8));
        paramArrayOfByte[k] = ((byte)m);
      }
    }
    return bool;
  }
  
  Label getFirst()
  {
    return frame == null ? this : frame.owner;
  }
  
  boolean inSubroutine(long paramLong)
  {
    if ((status & 0x400) != 0) {
      return (srcAndRefPositions[((int)(paramLong >>> 32))] & (int)paramLong) != 0;
    }
    return false;
  }
  
  boolean inSameSubroutine(Label paramLabel)
  {
    if (((status & 0x400) == 0) || ((status & 0x400) == 0)) {
      return false;
    }
    for (int i = 0; i < srcAndRefPositions.length; i++) {
      if ((srcAndRefPositions[i] & srcAndRefPositions[i]) != 0) {
        return true;
      }
    }
    return false;
  }
  
  void addToSubroutine(long paramLong, int paramInt)
  {
    if ((status & 0x400) == 0)
    {
      status |= 0x400;
      srcAndRefPositions = new int[paramInt / 32 + 1];
    }
    srcAndRefPositions[((int)(paramLong >>> 32))] |= (int)paramLong;
  }
  
  void visitSubroutine(Label paramLabel, long paramLong, int paramInt)
  {
    Label localLabel1 = this;
    while (localLabel1 != null)
    {
      Label localLabel2 = localLabel1;
      localLabel1 = next;
      next = null;
      if (paramLabel != null)
      {
        if ((status & 0x800) != 0) {
          continue;
        }
        status |= 0x800;
        if (((status & 0x100) != 0) && (!localLabel2.inSameSubroutine(paramLabel)))
        {
          localEdge = new Edge();
          info = inputStackTop;
          successor = successors.successor;
          next = successors;
          successors = localEdge;
        }
      }
      else
      {
        if (localLabel2.inSubroutine(paramLong)) {
          continue;
        }
        localLabel2.addToSubroutine(paramLong, paramInt);
      }
      for (Edge localEdge = successors; localEdge != null; localEdge = next) {
        if ((((status & 0x80) == 0) || (localEdge != successors.next)) && (successor.next == null))
        {
          successor.next = localLabel1;
          localLabel1 = successor;
        }
      }
    }
  }
  
  public String toString()
  {
    return "L" + System.identityHashCode(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\Label.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */