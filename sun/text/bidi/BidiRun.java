package sun.text.bidi;

public class BidiRun
{
  int start;
  int limit;
  int insertRemove;
  byte level;
  
  BidiRun()
  {
    this(0, 0, (byte)0);
  }
  
  BidiRun(int paramInt1, int paramInt2, byte paramByte)
  {
    start = paramInt1;
    limit = paramInt2;
    level = paramByte;
  }
  
  void copyFrom(BidiRun paramBidiRun)
  {
    start = start;
    limit = limit;
    level = level;
    insertRemove = insertRemove;
  }
  
  public byte getEmbeddingLevel()
  {
    return level;
  }
  
  boolean isEvenRun()
  {
    return (level & 0x1) == 0;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\bidi\BidiRun.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */