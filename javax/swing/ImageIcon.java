package javax.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.IllegalComponentStateException;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.beans.ConstructorProperties;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleIcon;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleStateSet;
import sun.awt.AppContext;

public class ImageIcon
  implements Icon, Serializable, Accessible
{
  private transient String filename;
  private transient URL location;
  transient Image image;
  transient int loadStatus = 0;
  ImageObserver imageObserver;
  String description = null;
  @Deprecated
  protected static final Component component = (Component)AccessController.doPrivileged(new PrivilegedAction()
  {
    public Component run()
    {
      try
      {
        Component localComponent = ImageIcon.access$000();
        Field localField = Component.class.getDeclaredField("appContext");
        localField.setAccessible(true);
        localField.set(localComponent, null);
        return localComponent;
      }
      catch (Throwable localThrowable)
      {
        localThrowable.printStackTrace();
      }
      return null;
    }
  });
  @Deprecated
  protected static final MediaTracker tracker = new MediaTracker(component);
  private static int mediaTrackerID;
  private static final Object TRACKER_KEY = new StringBuilder("TRACKER_KEY");
  int width = -1;
  int height = -1;
  private AccessibleImageIcon accessibleContext = null;
  
  private static Component createNoPermsComponent()
  {
    (Component)AccessController.doPrivileged(new PrivilegedAction()new AccessControlContextnew ProtectionDomain
    {
      public Component run()
      {
        new Component() {};
      }
    }, new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, null) }));
  }
  
  public ImageIcon(String paramString1, String paramString2)
  {
    image = Toolkit.getDefaultToolkit().getImage(paramString1);
    if (image == null) {
      return;
    }
    filename = paramString1;
    description = paramString2;
    loadImage(image);
  }
  
  @ConstructorProperties({"description"})
  public ImageIcon(String paramString)
  {
    this(paramString, paramString);
  }
  
  public ImageIcon(URL paramURL, String paramString)
  {
    image = Toolkit.getDefaultToolkit().getImage(paramURL);
    if (image == null) {
      return;
    }
    location = paramURL;
    description = paramString;
    loadImage(image);
  }
  
  public ImageIcon(URL paramURL)
  {
    this(paramURL, paramURL.toExternalForm());
  }
  
  public ImageIcon(Image paramImage, String paramString)
  {
    this(paramImage);
    description = paramString;
  }
  
  public ImageIcon(Image paramImage)
  {
    image = paramImage;
    Object localObject = paramImage.getProperty("comment", imageObserver);
    if ((localObject instanceof String)) {
      description = ((String)localObject);
    }
    loadImage(paramImage);
  }
  
  public ImageIcon(byte[] paramArrayOfByte, String paramString)
  {
    image = Toolkit.getDefaultToolkit().createImage(paramArrayOfByte);
    if (image == null) {
      return;
    }
    description = paramString;
    loadImage(image);
  }
  
  public ImageIcon(byte[] paramArrayOfByte)
  {
    image = Toolkit.getDefaultToolkit().createImage(paramArrayOfByte);
    if (image == null) {
      return;
    }
    Object localObject = image.getProperty("comment", imageObserver);
    if ((localObject instanceof String)) {
      description = ((String)localObject);
    }
    loadImage(image);
  }
  
  public ImageIcon() {}
  
  protected void loadImage(Image paramImage)
  {
    MediaTracker localMediaTracker = getTracker();
    synchronized (localMediaTracker)
    {
      int i = getNextID();
      localMediaTracker.addImage(paramImage, i);
      try
      {
        localMediaTracker.waitForID(i, 0L);
      }
      catch (InterruptedException localInterruptedException)
      {
        System.out.println("INTERRUPTED while loading Image");
      }
      loadStatus = localMediaTracker.statusID(i, false);
      localMediaTracker.removeImage(paramImage, i);
      width = paramImage.getWidth(imageObserver);
      height = paramImage.getHeight(imageObserver);
    }
  }
  
  /* Error */
  private int getNextID()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 319	javax/swing/ImageIcon:getTracker	()Ljava/awt/MediaTracker;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: getstatic 267	javax/swing/ImageIcon:mediaTrackerID	I
    //   10: iconst_1
    //   11: iadd
    //   12: dup
    //   13: putstatic 267	javax/swing/ImageIcon:mediaTrackerID	I
    //   16: aload_1
    //   17: monitorexit
    //   18: ireturn
    //   19: astore_2
    //   20: aload_1
    //   21: monitorexit
    //   22: aload_2
    //   23: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	24	0	this	ImageIcon
    //   5	16	1	Ljava/lang/Object;	Object
    //   19	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	18	19	finally
    //   19	22	19	finally
  }
  
  private MediaTracker getTracker()
  {
    AppContext localAppContext = AppContext.getAppContext();
    Object localObject1;
    synchronized (localAppContext)
    {
      localObject1 = localAppContext.get(TRACKER_KEY);
      if (localObject1 == null)
      {
        Component local3 = new Component() {};
        localObject1 = new MediaTracker(local3);
        localAppContext.put(TRACKER_KEY, localObject1);
      }
    }
    return (MediaTracker)localObject1;
  }
  
  public int getImageLoadStatus()
  {
    return loadStatus;
  }
  
  @Transient
  public Image getImage()
  {
    return image;
  }
  
  public void setImage(Image paramImage)
  {
    image = paramImage;
    loadImage(paramImage);
  }
  
  public String getDescription()
  {
    return description;
  }
  
  public void setDescription(String paramString)
  {
    description = paramString;
  }
  
  public synchronized void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    if (imageObserver == null) {
      paramGraphics.drawImage(image, paramInt1, paramInt2, paramComponent);
    } else {
      paramGraphics.drawImage(image, paramInt1, paramInt2, imageObserver);
    }
  }
  
  public int getIconWidth()
  {
    return width;
  }
  
  public int getIconHeight()
  {
    return height;
  }
  
  public void setImageObserver(ImageObserver paramImageObserver)
  {
    imageObserver = paramImageObserver;
  }
  
  @Transient
  public ImageObserver getImageObserver()
  {
    return imageObserver;
  }
  
  public String toString()
  {
    if (description != null) {
      return description;
    }
    return super.toString();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readInt();
    int j = paramObjectInputStream.readInt();
    int[] arrayOfInt = (int[])paramObjectInputStream.readObject();
    if (arrayOfInt != null)
    {
      Toolkit localToolkit = Toolkit.getDefaultToolkit();
      ColorModel localColorModel = ColorModel.getRGBdefault();
      image = localToolkit.createImage(new MemoryImageSource(i, j, localColorModel, arrayOfInt, 0, i));
      loadImage(image);
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    int i = getIconWidth();
    int j = getIconHeight();
    int[] arrayOfInt = image != null ? new int[i * j] : null;
    if (image != null) {
      try
      {
        PixelGrabber localPixelGrabber = new PixelGrabber(image, 0, 0, i, j, arrayOfInt, 0, i);
        localPixelGrabber.grabPixels();
        if ((localPixelGrabber.getStatus() & 0x80) != 0) {
          throw new IOException("failed to load image contents");
        }
      }
      catch (InterruptedException localInterruptedException)
      {
        throw new IOException("image load interrupted");
      }
    }
    paramObjectOutputStream.writeInt(i);
    paramObjectOutputStream.writeInt(j);
    paramObjectOutputStream.writeObject(arrayOfInt);
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleImageIcon();
    }
    return accessibleContext;
  }
  
  protected class AccessibleImageIcon
    extends AccessibleContext
    implements AccessibleIcon, Serializable
  {
    protected AccessibleImageIcon() {}
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.ICON;
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      return null;
    }
    
    public Accessible getAccessibleParent()
    {
      return null;
    }
    
    public int getAccessibleIndexInParent()
    {
      return -1;
    }
    
    public int getAccessibleChildrenCount()
    {
      return 0;
    }
    
    public Accessible getAccessibleChild(int paramInt)
    {
      return null;
    }
    
    public Locale getLocale()
      throws IllegalComponentStateException
    {
      return null;
    }
    
    public String getAccessibleIconDescription()
    {
      return getDescription();
    }
    
    public void setAccessibleIconDescription(String paramString)
    {
      setDescription(paramString);
    }
    
    public int getAccessibleIconHeight()
    {
      return height;
    }
    
    public int getAccessibleIconWidth()
    {
      return width;
    }
    
    private void readObject(ObjectInputStream paramObjectInputStream)
      throws ClassNotFoundException, IOException
    {
      paramObjectInputStream.defaultReadObject();
    }
    
    private void writeObject(ObjectOutputStream paramObjectOutputStream)
      throws IOException
    {
      paramObjectOutputStream.defaultWriteObject();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\ImageIcon.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */