package java.awt.image;

public abstract class LookupTable
{
  int numComponents;
  int offset;
  int numEntries;
  
  protected LookupTable(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0) {
      throw new IllegalArgumentException("Offset must be greater than 0");
    }
    if (paramInt2 < 1) {
      throw new IllegalArgumentException("Number of components must  be at least 1");
    }
    numComponents = paramInt2;
    offset = paramInt1;
  }
  
  public int getNumComponents()
  {
    return numComponents;
  }
  
  public int getOffset()
  {
    return offset;
  }
  
  public abstract int[] lookupPixel(int[] paramArrayOfInt1, int[] paramArrayOfInt2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\LookupTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */