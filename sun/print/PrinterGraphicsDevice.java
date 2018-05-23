package sun.print;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Window;

public final class PrinterGraphicsDevice
  extends GraphicsDevice
{
  String printerID;
  GraphicsConfiguration graphicsConf;
  
  protected PrinterGraphicsDevice(GraphicsConfiguration paramGraphicsConfiguration, String paramString)
  {
    printerID = paramString;
    graphicsConf = paramGraphicsConfiguration;
  }
  
  public int getType()
  {
    return 1;
  }
  
  public String getIDstring()
  {
    return printerID;
  }
  
  public GraphicsConfiguration[] getConfigurations()
  {
    GraphicsConfiguration[] arrayOfGraphicsConfiguration = new GraphicsConfiguration[1];
    arrayOfGraphicsConfiguration[0] = graphicsConf;
    return arrayOfGraphicsConfiguration;
  }
  
  public GraphicsConfiguration getDefaultConfiguration()
  {
    return graphicsConf;
  }
  
  public void setFullScreenWindow(Window paramWindow) {}
  
  public Window getFullScreenWindow()
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\print\PrinterGraphicsDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */