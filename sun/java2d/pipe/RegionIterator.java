package sun.java2d.pipe;

public class RegionIterator
{
  Region region;
  int curIndex;
  int numXbands;
  
  RegionIterator(Region paramRegion)
  {
    region = paramRegion;
  }
  
  public RegionIterator createCopy()
  {
    RegionIterator localRegionIterator = new RegionIterator(region);
    curIndex = curIndex;
    numXbands = numXbands;
    return localRegionIterator;
  }
  
  public void copyStateFrom(RegionIterator paramRegionIterator)
  {
    if (region != region) {
      throw new InternalError("region mismatch");
    }
    curIndex = curIndex;
    numXbands = numXbands;
  }
  
  public boolean nextYRange(int[] paramArrayOfInt)
  {
    curIndex += numXbands * 2;
    numXbands = 0;
    if (curIndex >= region.endIndex) {
      return false;
    }
    paramArrayOfInt[1] = region.bands[(curIndex++)];
    paramArrayOfInt[3] = region.bands[(curIndex++)];
    numXbands = region.bands[(curIndex++)];
    return true;
  }
  
  public boolean nextXBand(int[] paramArrayOfInt)
  {
    if (numXbands <= 0) {
      return false;
    }
    numXbands -= 1;
    paramArrayOfInt[0] = region.bands[(curIndex++)];
    paramArrayOfInt[2] = region.bands[(curIndex++)];
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\RegionIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */