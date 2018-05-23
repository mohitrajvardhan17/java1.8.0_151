package sun.awt.windows;

import java.awt.Desktop.Action;
import java.awt.peer.DesktopPeer;
import java.io.File;
import java.io.IOException;
import java.net.URI;

final class WDesktopPeer
  implements DesktopPeer
{
  private static String ACTION_OPEN_VERB = "open";
  private static String ACTION_EDIT_VERB = "edit";
  private static String ACTION_PRINT_VERB = "print";
  
  WDesktopPeer() {}
  
  public boolean isSupported(Desktop.Action paramAction)
  {
    return true;
  }
  
  public void open(File paramFile)
    throws IOException
  {
    ShellExecute(paramFile, ACTION_OPEN_VERB);
  }
  
  public void edit(File paramFile)
    throws IOException
  {
    ShellExecute(paramFile, ACTION_EDIT_VERB);
  }
  
  public void print(File paramFile)
    throws IOException
  {
    ShellExecute(paramFile, ACTION_PRINT_VERB);
  }
  
  public void mail(URI paramURI)
    throws IOException
  {
    ShellExecute(paramURI, ACTION_OPEN_VERB);
  }
  
  public void browse(URI paramURI)
    throws IOException
  {
    ShellExecute(paramURI, ACTION_OPEN_VERB);
  }
  
  private void ShellExecute(File paramFile, String paramString)
    throws IOException
  {
    String str = ShellExecute(paramFile.getAbsolutePath(), paramString);
    if (str != null) {
      throw new IOException("Failed to " + paramString + " " + paramFile + ". Error message: " + str);
    }
  }
  
  private void ShellExecute(URI paramURI, String paramString)
    throws IOException
  {
    String str = ShellExecute(paramURI.toString(), paramString);
    if (str != null) {
      throw new IOException("Failed to " + paramString + " " + paramURI + ". Error message: " + str);
    }
  }
  
  private static native String ShellExecute(String paramString1, String paramString2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WDesktopPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */