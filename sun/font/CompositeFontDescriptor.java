package sun.font;

public class CompositeFontDescriptor
{
  private String faceName;
  private int coreComponentCount;
  private String[] componentFaceNames;
  private String[] componentFileNames;
  private int[] exclusionRanges;
  private int[] exclusionRangeLimits;
  
  public CompositeFontDescriptor(String paramString, int paramInt, String[] paramArrayOfString1, String[] paramArrayOfString2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    faceName = paramString;
    coreComponentCount = paramInt;
    componentFaceNames = paramArrayOfString1;
    componentFileNames = paramArrayOfString2;
    exclusionRanges = paramArrayOfInt1;
    exclusionRangeLimits = paramArrayOfInt2;
  }
  
  public String getFaceName()
  {
    return faceName;
  }
  
  public int getCoreComponentCount()
  {
    return coreComponentCount;
  }
  
  public String[] getComponentFaceNames()
  {
    return componentFaceNames;
  }
  
  public String[] getComponentFileNames()
  {
    return componentFileNames;
  }
  
  public int[] getExclusionRanges()
  {
    return exclusionRanges;
  }
  
  public int[] getExclusionRangeLimits()
  {
    return exclusionRangeLimits;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\CompositeFontDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */