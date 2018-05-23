package sun.rmi.transport;

class SequenceEntry
{
  long sequenceNum;
  boolean keep;
  
  SequenceEntry(long paramLong)
  {
    sequenceNum = paramLong;
    keep = false;
  }
  
  void retain(long paramLong)
  {
    sequenceNum = paramLong;
    keep = true;
  }
  
  void update(long paramLong)
  {
    sequenceNum = paramLong;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\SequenceEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */