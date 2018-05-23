package javax.swing.text.html;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.GlyphView.GlyphPainter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.LayeredHighlighter;
import javax.swing.text.Position.Bias;
import javax.swing.text.Segment;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

public class ImageView
  extends View
{
  private static boolean sIsInc = false;
  private static int sIncRate = 100;
  private static final String PENDING_IMAGE = "html.pendingImage";
  private static final String MISSING_IMAGE = "html.missingImage";
  private static final String IMAGE_CACHE_PROPERTY = "imageCache";
  private static final int DEFAULT_WIDTH = 38;
  private static final int DEFAULT_HEIGHT = 38;
  private static final int DEFAULT_BORDER = 2;
  private static final int LOADING_FLAG = 1;
  private static final int LINK_FLAG = 2;
  private static final int WIDTH_FLAG = 4;
  private static final int HEIGHT_FLAG = 8;
  private static final int RELOAD_FLAG = 16;
  private static final int RELOAD_IMAGE_FLAG = 32;
  private static final int SYNC_LOAD_FLAG = 64;
  private AttributeSet attr;
  private Image image;
  private Image disabledImage;
  private int width;
  private int height;
  private int state = 48;
  private Container container;
  private Rectangle fBounds = new Rectangle();
  private Color borderColor;
  private short borderSize;
  private short leftInset;
  private short rightInset;
  private short topInset;
  private short bottomInset;
  private ImageObserver imageObserver = new ImageHandler(null);
  private View altView;
  private float vAlign;
  
  public ImageView(Element paramElement)
  {
    super(paramElement);
  }
  
  public String getAltText()
  {
    return (String)getElement().getAttributes().getAttribute(HTML.Attribute.ALT);
  }
  
  public URL getImageURL()
  {
    String str = (String)getElement().getAttributes().getAttribute(HTML.Attribute.SRC);
    if (str == null) {
      return null;
    }
    URL localURL1 = ((HTMLDocument)getDocument()).getBase();
    try
    {
      URL localURL2 = new URL(localURL1, str);
      return localURL2;
    }
    catch (MalformedURLException localMalformedURLException) {}
    return null;
  }
  
  public Icon getNoImageIcon()
  {
    return (Icon)UIManager.getLookAndFeelDefaults().get("html.missingImage");
  }
  
  public Icon getLoadingImageIcon()
  {
    return (Icon)UIManager.getLookAndFeelDefaults().get("html.pendingImage");
  }
  
  public Image getImage()
  {
    sync();
    return image;
  }
  
  private Image getImage(boolean paramBoolean)
  {
    Image localImage = getImage();
    if (!paramBoolean)
    {
      if (disabledImage == null) {
        disabledImage = GrayFilter.createDisabledImage(localImage);
      }
      localImage = disabledImage;
    }
    return localImage;
  }
  
  public void setLoadsSynchronously(boolean paramBoolean)
  {
    synchronized (this)
    {
      if (paramBoolean) {
        state |= 0x40;
      } else {
        state = ((state | 0x40) ^ 0x40);
      }
    }
  }
  
  public boolean getLoadsSynchronously()
  {
    return (state & 0x40) != 0;
  }
  
  protected StyleSheet getStyleSheet()
  {
    HTMLDocument localHTMLDocument = (HTMLDocument)getDocument();
    return localHTMLDocument.getStyleSheet();
  }
  
  public AttributeSet getAttributes()
  {
    sync();
    return attr;
  }
  
  public String getToolTipText(float paramFloat1, float paramFloat2, Shape paramShape)
  {
    return getAltText();
  }
  
  protected void setPropertiesFromAttributes()
  {
    StyleSheet localStyleSheet = getStyleSheet();
    attr = localStyleSheet.getViewAttributes(this);
    borderSize = ((short)getIntAttr(HTML.Attribute.BORDER, isLink() ? 2 : 0));
    leftInset = (rightInset = (short)(getIntAttr(HTML.Attribute.HSPACE, 0) + borderSize));
    topInset = (bottomInset = (short)(getIntAttr(HTML.Attribute.VSPACE, 0) + borderSize));
    borderColor = ((StyledDocument)getDocument()).getForeground(getAttributes());
    AttributeSet localAttributeSet1 = getElement().getAttributes();
    Object localObject1 = localAttributeSet1.getAttribute(HTML.Attribute.ALIGN);
    vAlign = 1.0F;
    if (localObject1 != null)
    {
      localObject1 = localObject1.toString();
      if ("top".equals(localObject1)) {
        vAlign = 0.0F;
      } else if ("middle".equals(localObject1)) {
        vAlign = 0.5F;
      }
    }
    AttributeSet localAttributeSet2 = (AttributeSet)localAttributeSet1.getAttribute(HTML.Tag.A);
    if ((localAttributeSet2 != null) && (localAttributeSet2.isDefined(HTML.Attribute.HREF))) {
      synchronized (this)
      {
        state |= 0x2;
      }
    } else {
      synchronized (this)
      {
        state = ((state | 0x2) ^ 0x2);
      }
    }
  }
  
  public void setParent(View paramView)
  {
    View localView = getParent();
    super.setParent(paramView);
    container = (paramView != null ? getContainer() : null);
    if (localView != paramView) {
      synchronized (this)
      {
        state |= 0x10;
      }
    }
  }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    super.changedUpdate(paramDocumentEvent, paramShape, paramViewFactory);
    synchronized (this)
    {
      state |= 0x30;
    }
    preferenceChanged(null, true, true);
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape)
  {
    sync();
    Rectangle localRectangle1 = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
    Rectangle localRectangle2 = paramGraphics.getClipBounds();
    fBounds.setBounds(localRectangle1);
    paintHighlights(paramGraphics, paramShape);
    paintBorder(paramGraphics, localRectangle1);
    if (localRectangle2 != null) {
      paramGraphics.clipRect(x + leftInset, y + topInset, width - leftInset - rightInset, height - topInset - bottomInset);
    }
    Container localContainer = getContainer();
    Image localImage = getImage((localContainer == null) || (localContainer.isEnabled()));
    Icon localIcon;
    if (localImage != null)
    {
      if (!hasPixels(localImage))
      {
        localIcon = getLoadingImageIcon();
        if (localIcon != null) {
          localIcon.paintIcon(localContainer, paramGraphics, x + leftInset, y + topInset);
        }
      }
      else
      {
        paramGraphics.drawImage(localImage, x + leftInset, y + topInset, width, height, imageObserver);
      }
    }
    else
    {
      localIcon = getNoImageIcon();
      if (localIcon != null) {
        localIcon.paintIcon(localContainer, paramGraphics, x + leftInset, y + topInset);
      }
      View localView = getAltView();
      if ((localView != null) && (((state & 0x4) == 0) || (width > 38)))
      {
        Rectangle localRectangle3 = new Rectangle(x + leftInset + 38, y + topInset, width - leftInset - rightInset - 38, height - topInset - bottomInset);
        localView.paint(paramGraphics, localRectangle3);
      }
    }
    if (localRectangle2 != null) {
      paramGraphics.setClip(x, y, width, height);
    }
  }
  
  private void paintHighlights(Graphics paramGraphics, Shape paramShape)
  {
    if ((container instanceof JTextComponent))
    {
      JTextComponent localJTextComponent = (JTextComponent)container;
      Highlighter localHighlighter = localJTextComponent.getHighlighter();
      if ((localHighlighter instanceof LayeredHighlighter)) {
        ((LayeredHighlighter)localHighlighter).paintLayeredHighlights(paramGraphics, getStartOffset(), getEndOffset(), paramShape, localJTextComponent, this);
      }
    }
  }
  
  private void paintBorder(Graphics paramGraphics, Rectangle paramRectangle)
  {
    Color localColor = borderColor;
    if (((borderSize > 0) || (image == null)) && (localColor != null))
    {
      int i = leftInset - borderSize;
      int j = topInset - borderSize;
      paramGraphics.setColor(localColor);
      int k = image == null ? 1 : borderSize;
      for (int m = 0; m < k; m++) {
        paramGraphics.drawRect(x + i + m, y + j + m, width - m - m - i - i - 1, height - m - m - j - j - 1);
      }
    }
  }
  
  public float getPreferredSpan(int paramInt)
  {
    sync();
    if ((paramInt == 0) && ((state & 0x4) == 4))
    {
      getPreferredSpanFromAltView(paramInt);
      return width + leftInset + rightInset;
    }
    if ((paramInt == 1) && ((state & 0x8) == 8))
    {
      getPreferredSpanFromAltView(paramInt);
      return height + topInset + bottomInset;
    }
    Image localImage = getImage();
    if (localImage != null)
    {
      switch (paramInt)
      {
      case 0: 
        return width + leftInset + rightInset;
      case 1: 
        return height + topInset + bottomInset;
      }
      throw new IllegalArgumentException("Invalid axis: " + paramInt);
    }
    View localView = getAltView();
    float f = 0.0F;
    if (localView != null) {
      f = localView.getPreferredSpan(paramInt);
    }
    switch (paramInt)
    {
    case 0: 
      return f + (width + leftInset + rightInset);
    case 1: 
      return f + (height + topInset + bottomInset);
    }
    throw new IllegalArgumentException("Invalid axis: " + paramInt);
  }
  
  public float getAlignment(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
      return vAlign;
    }
    return super.getAlignment(paramInt);
  }
  
  public Shape modelToView(int paramInt, Shape paramShape, Position.Bias paramBias)
    throws BadLocationException
  {
    int i = getStartOffset();
    int j = getEndOffset();
    if ((paramInt >= i) && (paramInt <= j))
    {
      Rectangle localRectangle = paramShape.getBounds();
      if (paramInt == j) {
        x += width;
      }
      width = 0;
      return localRectangle;
    }
    return null;
  }
  
  public int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias)
  {
    Rectangle localRectangle = (Rectangle)paramShape;
    if (paramFloat1 < x + width)
    {
      paramArrayOfBias[0] = Position.Bias.Forward;
      return getStartOffset();
    }
    paramArrayOfBias[0] = Position.Bias.Backward;
    return getEndOffset();
  }
  
  public void setSize(float paramFloat1, float paramFloat2)
  {
    sync();
    if (getImage() == null)
    {
      View localView = getAltView();
      if (localView != null) {
        localView.setSize(Math.max(0.0F, paramFloat1 - (38 + leftInset + rightInset)), Math.max(0.0F, paramFloat2 - (topInset + bottomInset)));
      }
    }
  }
  
  private boolean isLink()
  {
    return (state & 0x2) == 2;
  }
  
  private boolean hasPixels(Image paramImage)
  {
    return (paramImage != null) && (paramImage.getHeight(imageObserver) > 0) && (paramImage.getWidth(imageObserver) > 0);
  }
  
  private float getPreferredSpanFromAltView(int paramInt)
  {
    if (getImage() == null)
    {
      View localView = getAltView();
      if (localView != null) {
        return localView.getPreferredSpan(paramInt);
      }
    }
    return 0.0F;
  }
  
  private void repaint(long paramLong)
  {
    if ((container != null) && (fBounds != null)) {
      container.repaint(paramLong, fBounds.x, fBounds.y, fBounds.width, fBounds.height);
    }
  }
  
  private int getIntAttr(HTML.Attribute paramAttribute, int paramInt)
  {
    AttributeSet localAttributeSet = getElement().getAttributes();
    if (localAttributeSet.isDefined(paramAttribute))
    {
      String str = (String)localAttributeSet.getAttribute(paramAttribute);
      int i;
      if (str == null) {
        i = paramInt;
      } else {
        try
        {
          i = Math.max(0, Integer.parseInt(str));
        }
        catch (NumberFormatException localNumberFormatException)
        {
          i = paramInt;
        }
      }
      return i;
    }
    return paramInt;
  }
  
  private void sync()
  {
    int i = state;
    if ((i & 0x20) != 0) {
      refreshImage();
    }
    i = state;
    if ((i & 0x10) != 0)
    {
      synchronized (this)
      {
        state = ((state | 0x10) ^ 0x10);
      }
      setPropertiesFromAttributes();
    }
  }
  
  /* Error */
  private void refreshImage()
  {
    // Byte code:
    //   0: aload_0
    //   1: dup
    //   2: astore_1
    //   3: monitorenter
    //   4: aload_0
    //   5: aload_0
    //   6: getfield 472	javax/swing/text/html/ImageView:state	I
    //   9: iconst_1
    //   10: ior
    //   11: bipush 32
    //   13: ior
    //   14: iconst_4
    //   15: ior
    //   16: bipush 8
    //   18: ior
    //   19: bipush 44
    //   21: ixor
    //   22: putfield 472	javax/swing/text/html/ImageView:state	I
    //   25: aload_0
    //   26: aconst_null
    //   27: putfield 483	javax/swing/text/html/ImageView:image	Ljava/awt/Image;
    //   30: aload_0
    //   31: aload_0
    //   32: iconst_0
    //   33: dup_x1
    //   34: putfield 470	javax/swing/text/html/ImageView:height	I
    //   37: putfield 473	javax/swing/text/html/ImageView:width	I
    //   40: aload_1
    //   41: monitorexit
    //   42: goto +8 -> 50
    //   45: astore_2
    //   46: aload_1
    //   47: monitorexit
    //   48: aload_2
    //   49: athrow
    //   50: aload_0
    //   51: invokespecial 539	javax/swing/text/html/ImageView:loadImage	()V
    //   54: aload_0
    //   55: invokespecial 545	javax/swing/text/html/ImageView:updateImageSize	()V
    //   58: aload_0
    //   59: dup
    //   60: astore_1
    //   61: monitorenter
    //   62: aload_0
    //   63: aload_0
    //   64: getfield 472	javax/swing/text/html/ImageView:state	I
    //   67: iconst_1
    //   68: ior
    //   69: iconst_1
    //   70: ixor
    //   71: putfield 472	javax/swing/text/html/ImageView:state	I
    //   74: aload_1
    //   75: monitorexit
    //   76: goto +8 -> 84
    //   79: astore_3
    //   80: aload_1
    //   81: monitorexit
    //   82: aload_3
    //   83: athrow
    //   84: goto +39 -> 123
    //   87: astore 4
    //   89: aload_0
    //   90: dup
    //   91: astore 5
    //   93: monitorenter
    //   94: aload_0
    //   95: aload_0
    //   96: getfield 472	javax/swing/text/html/ImageView:state	I
    //   99: iconst_1
    //   100: ior
    //   101: iconst_1
    //   102: ixor
    //   103: putfield 472	javax/swing/text/html/ImageView:state	I
    //   106: aload 5
    //   108: monitorexit
    //   109: goto +11 -> 120
    //   112: astore 6
    //   114: aload 5
    //   116: monitorexit
    //   117: aload 6
    //   119: athrow
    //   120: aload 4
    //   122: athrow
    //   123: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	124	0	this	ImageView
    //   45	4	2	localObject1	Object
    //   79	4	3	localObject2	Object
    //   87	34	4	localObject3	Object
    //   112	6	6	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   4	42	45	finally
    //   45	48	45	finally
    //   62	76	79	finally
    //   79	82	79	finally
    //   50	58	87	finally
    //   87	89	87	finally
    //   94	109	112	finally
    //   112	117	112	finally
  }
  
  private void loadImage()
  {
    URL localURL = getImageURL();
    Image localImage = null;
    if (localURL != null)
    {
      Dictionary localDictionary = (Dictionary)getDocument().getProperty("imageCache");
      if (localDictionary != null)
      {
        localImage = (Image)localDictionary.get(localURL);
      }
      else
      {
        localImage = Toolkit.getDefaultToolkit().createImage(localURL);
        if ((localImage != null) && (getLoadsSynchronously()))
        {
          ImageIcon localImageIcon = new ImageIcon();
          localImageIcon.setImage(localImage);
        }
      }
    }
    image = localImage;
  }
  
  private void updateImageSize()
  {
    int i = 0;
    int j = 0;
    int k = 0;
    Image localImage = getImage();
    if (localImage != null)
    {
      Element localElement = getElement();
      AttributeSet localAttributeSet = localElement.getAttributes();
      i = getIntAttr(HTML.Attribute.WIDTH, -1);
      if (i > 0) {
        k |= 0x4;
      }
      j = getIntAttr(HTML.Attribute.HEIGHT, -1);
      if (j > 0) {
        k |= 0x8;
      }
      if (i <= 0)
      {
        i = localImage.getWidth(imageObserver);
        if (i <= 0) {
          i = 38;
        }
      }
      if (j <= 0)
      {
        j = localImage.getHeight(imageObserver);
        if (j <= 0) {
          j = 38;
        }
      }
      if ((k & 0xC) != 0) {
        Toolkit.getDefaultToolkit().prepareImage(localImage, i, j, imageObserver);
      } else {
        Toolkit.getDefaultToolkit().prepareImage(localImage, -1, -1, imageObserver);
      }
      int m = 0;
      synchronized (this)
      {
        if (image != null)
        {
          if (((k & 0x4) == 4) || (width == 0)) {
            width = i;
          }
          if (((k & 0x8) == 8) || (height == 0)) {
            height = j;
          }
        }
        else
        {
          m = 1;
          if ((k & 0x4) == 4) {
            width = i;
          }
          if ((k & 0x8) == 8) {
            height = j;
          }
        }
        state |= k;
        state = ((state | 0x1) ^ 0x1);
      }
      if (m != 0) {
        updateAltTextView();
      }
    }
    else
    {
      width = (height = 38);
      updateAltTextView();
    }
  }
  
  private void updateAltTextView()
  {
    String str = getAltText();
    if (str != null)
    {
      ImageLabelView localImageLabelView = new ImageLabelView(getElement(), str);
      synchronized (this)
      {
        altView = localImageLabelView;
      }
    }
  }
  
  private View getAltView()
  {
    View localView;
    synchronized (this)
    {
      localView = altView;
    }
    if ((localView != null) && (localView.getParent() == null)) {
      localView.setParent(getParent());
    }
    return localView;
  }
  
  private void safePreferenceChanged()
  {
    if (SwingUtilities.isEventDispatchThread())
    {
      Document localDocument = getDocument();
      if ((localDocument instanceof AbstractDocument)) {
        ((AbstractDocument)localDocument).readLock();
      }
      preferenceChanged(null, true, true);
      if ((localDocument instanceof AbstractDocument)) {
        ((AbstractDocument)localDocument).readUnlock();
      }
    }
    else
    {
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          ImageView.this.safePreferenceChanged();
        }
      });
    }
  }
  
  private class ImageHandler
    implements ImageObserver
  {
    private ImageHandler() {}
    
    public boolean imageUpdate(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      if (((paramImage != image) && (paramImage != disabledImage)) || (image == null) || (getParent() == null)) {
        return false;
      }
      if ((paramInt1 & 0xC0) != 0)
      {
        ImageView.this.repaint(0L);
        synchronized (ImageView.this)
        {
          if (image == paramImage)
          {
            image = null;
            if ((state & 0x4) != 4) {
              width = 38;
            }
            if ((state & 0x8) != 8) {
              height = 38;
            }
          }
          else
          {
            disabledImage = null;
          }
          if ((state & 0x1) == 1) {
            return false;
          }
        }
        ImageView.this.updateAltTextView();
        ImageView.this.safePreferenceChanged();
        return false;
      }
      if (image == paramImage)
      {
        int i = 0;
        if (((paramInt1 & 0x2) != 0) && (!getElement().getAttributes().isDefined(HTML.Attribute.HEIGHT))) {
          i = (short)(i | 0x1);
        }
        if (((paramInt1 & 0x1) != 0) && (!getElement().getAttributes().isDefined(HTML.Attribute.WIDTH))) {
          i = (short)(i | 0x2);
        }
        synchronized (ImageView.this)
        {
          if (((i & 0x1) == 1) && ((state & 0x4) == 0)) {
            width = paramInt4;
          }
          if (((i & 0x2) == 2) && ((state & 0x8) == 0)) {
            height = paramInt5;
          }
          if ((state & 0x1) == 1) {
            return true;
          }
        }
        if (i != 0)
        {
          ImageView.this.safePreferenceChanged();
          return true;
        }
      }
      if ((paramInt1 & 0x30) != 0) {
        ImageView.this.repaint(0L);
      } else if (((paramInt1 & 0x8) != 0) && (ImageView.sIsInc)) {
        ImageView.this.repaint(ImageView.sIncRate);
      }
      return (paramInt1 & 0x20) == 0;
    }
  }
  
  private class ImageLabelView
    extends InlineView
  {
    private Segment segment;
    private Color fg;
    
    ImageLabelView(Element paramElement, String paramString)
    {
      super();
      reset(paramString);
    }
    
    public void reset(String paramString)
    {
      segment = new Segment(paramString.toCharArray(), 0, paramString.length());
    }
    
    public void paint(Graphics paramGraphics, Shape paramShape)
    {
      GlyphView.GlyphPainter localGlyphPainter = getGlyphPainter();
      if (localGlyphPainter != null)
      {
        paramGraphics.setColor(getForeground());
        localGlyphPainter.paint(this, paramGraphics, paramShape, getStartOffset(), getEndOffset());
      }
    }
    
    public Segment getText(int paramInt1, int paramInt2)
    {
      if ((paramInt1 < 0) || (paramInt2 > segment.array.length)) {
        throw new RuntimeException("ImageLabelView: Stale view");
      }
      segment.offset = paramInt1;
      segment.count = (paramInt2 - paramInt1);
      return segment;
    }
    
    public int getStartOffset()
    {
      return 0;
    }
    
    public int getEndOffset()
    {
      return segment.array.length;
    }
    
    public View breakView(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2)
    {
      return this;
    }
    
    public Color getForeground()
    {
      View localView;
      if ((fg == null) && ((localView = getParent()) != null))
      {
        Document localDocument = getDocument();
        AttributeSet localAttributeSet = localView.getAttributes();
        if ((localAttributeSet != null) && ((localDocument instanceof StyledDocument))) {
          fg = ((StyledDocument)localDocument).getForeground(localAttributeSet);
        }
      }
      return fg;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\ImageView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */