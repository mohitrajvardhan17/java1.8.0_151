package java.awt.datatransfer;

public class UnsupportedFlavorException
  extends Exception
{
  private static final long serialVersionUID = 5383814944251665601L;
  
  public UnsupportedFlavorException(DataFlavor paramDataFlavor)
  {
    super(paramDataFlavor != null ? paramDataFlavor.getHumanPresentableName() : null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\datatransfer\UnsupportedFlavorException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */