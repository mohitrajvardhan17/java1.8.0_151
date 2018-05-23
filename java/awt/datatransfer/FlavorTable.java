package java.awt.datatransfer;

import java.util.List;

public abstract interface FlavorTable
  extends FlavorMap
{
  public abstract List<String> getNativesForFlavor(DataFlavor paramDataFlavor);
  
  public abstract List<DataFlavor> getFlavorsForNative(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\datatransfer\FlavorTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */