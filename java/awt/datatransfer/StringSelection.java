package java.awt.datatransfer;

import java.io.IOException;
import java.io.StringReader;

public class StringSelection
  implements Transferable, ClipboardOwner
{
  private static final int STRING = 0;
  private static final int PLAIN_TEXT = 1;
  private static final DataFlavor[] flavors = { DataFlavor.stringFlavor, DataFlavor.plainTextFlavor };
  private String data;
  
  public StringSelection(String paramString)
  {
    data = paramString;
  }
  
  public DataFlavor[] getTransferDataFlavors()
  {
    return (DataFlavor[])flavors.clone();
  }
  
  public boolean isDataFlavorSupported(DataFlavor paramDataFlavor)
  {
    for (int i = 0; i < flavors.length; i++) {
      if (paramDataFlavor.equals(flavors[i])) {
        return true;
      }
    }
    return false;
  }
  
  public Object getTransferData(DataFlavor paramDataFlavor)
    throws UnsupportedFlavorException, IOException
  {
    if (paramDataFlavor.equals(flavors[0])) {
      return data;
    }
    if (paramDataFlavor.equals(flavors[1])) {
      return new StringReader(data == null ? "" : data);
    }
    throw new UnsupportedFlavorException(paramDataFlavor);
  }
  
  public void lostOwnership(Clipboard paramClipboard, Transferable paramTransferable) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\datatransfer\StringSelection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */