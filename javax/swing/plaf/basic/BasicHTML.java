package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.StringReader;
import java.net.URL;
import javax.swing.JComponent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.ParagraphView;
import javax.swing.text.Position.Bias;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLEditorKit.HTMLFactory;
import javax.swing.text.html.ImageView;
import javax.swing.text.html.StyleSheet;
import sun.swing.SwingUtilities2;

public class BasicHTML
{
  private static final String htmlDisable = "html.disable";
  public static final String propertyKey = "html";
  public static final String documentBaseKey = "html.base";
  private static BasicEditorKit basicHTMLFactory;
  private static ViewFactory basicHTMLViewFactory;
  private static final String styleChanges = "p { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0 }body { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0 }";
  
  public BasicHTML() {}
  
  public static View createHTMLView(JComponent paramJComponent, String paramString)
  {
    BasicEditorKit localBasicEditorKit = getFactory();
    Document localDocument = localBasicEditorKit.createDefaultDocument(paramJComponent.getFont(), paramJComponent.getForeground());
    Object localObject = paramJComponent.getClientProperty("html.base");
    if ((localObject instanceof URL)) {
      ((HTMLDocument)localDocument).setBase((URL)localObject);
    }
    StringReader localStringReader = new StringReader(paramString);
    try
    {
      localBasicEditorKit.read(localStringReader, localDocument, 0);
    }
    catch (Throwable localThrowable) {}
    ViewFactory localViewFactory = localBasicEditorKit.getViewFactory();
    View localView = localViewFactory.create(localDocument.getDefaultRootElement());
    Renderer localRenderer = new Renderer(paramJComponent, localViewFactory, localView);
    return localRenderer;
  }
  
  public static int getHTMLBaseline(View paramView, int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0)) {
      throw new IllegalArgumentException("Width and height must be >= 0");
    }
    if ((paramView instanceof Renderer)) {
      return getBaseline(paramView.getView(0), paramInt1, paramInt2);
    }
    return -1;
  }
  
  static int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    View localView = (View)paramJComponent.getClientProperty("html");
    if (localView != null)
    {
      int i = getHTMLBaseline(localView, paramInt3, paramInt4);
      if (i < 0) {
        return i;
      }
      return paramInt1 + i;
    }
    return paramInt1 + paramInt2;
  }
  
  static int getBaseline(View paramView, int paramInt1, int paramInt2)
  {
    if (hasParagraph(paramView))
    {
      paramView.setSize(paramInt1, paramInt2);
      return getBaseline(paramView, new Rectangle(0, 0, paramInt1, paramInt2));
    }
    return -1;
  }
  
  private static int getBaseline(View paramView, Shape paramShape)
  {
    if (paramView.getViewCount() == 0) {
      return -1;
    }
    AttributeSet localAttributeSet = paramView.getElement().getAttributes();
    Object localObject = null;
    if (localAttributeSet != null) {
      localObject = localAttributeSet.getAttribute(StyleConstants.NameAttribute);
    }
    int i = 0;
    if ((localObject == HTML.Tag.HTML) && (paramView.getViewCount() > 1)) {
      i++;
    }
    paramShape = paramView.getChildAllocation(i, paramShape);
    if (paramShape == null) {
      return -1;
    }
    View localView = paramView.getView(i);
    if ((paramView instanceof ParagraphView))
    {
      Rectangle localRectangle;
      if ((paramShape instanceof Rectangle)) {
        localRectangle = (Rectangle)paramShape;
      } else {
        localRectangle = paramShape.getBounds();
      }
      return y + (int)(height * localView.getAlignment(1));
    }
    return getBaseline(localView, paramShape);
  }
  
  private static boolean hasParagraph(View paramView)
  {
    if ((paramView instanceof ParagraphView)) {
      return true;
    }
    if (paramView.getViewCount() == 0) {
      return false;
    }
    AttributeSet localAttributeSet = paramView.getElement().getAttributes();
    Object localObject = null;
    if (localAttributeSet != null) {
      localObject = localAttributeSet.getAttribute(StyleConstants.NameAttribute);
    }
    int i = 0;
    if ((localObject == HTML.Tag.HTML) && (paramView.getViewCount() > 1)) {
      i = 1;
    }
    return hasParagraph(paramView.getView(i));
  }
  
  public static boolean isHTMLString(String paramString)
  {
    if ((paramString != null) && (paramString.length() >= 6) && (paramString.charAt(0) == '<') && (paramString.charAt(5) == '>'))
    {
      String str = paramString.substring(1, 5);
      return str.equalsIgnoreCase("html");
    }
    return false;
  }
  
  public static void updateRenderer(JComponent paramJComponent, String paramString)
  {
    View localView1 = null;
    View localView2 = (View)paramJComponent.getClientProperty("html");
    Boolean localBoolean = (Boolean)paramJComponent.getClientProperty("html.disable");
    if ((localBoolean != Boolean.TRUE) && (isHTMLString(paramString))) {
      localView1 = createHTMLView(paramJComponent, paramString);
    }
    if ((localView1 != localView2) && (localView2 != null)) {
      for (int i = 0; i < localView2.getViewCount(); i++) {
        localView2.getView(i).setParent(null);
      }
    }
    paramJComponent.putClientProperty("html", localView1);
  }
  
  static BasicEditorKit getFactory()
  {
    if (basicHTMLFactory == null)
    {
      basicHTMLViewFactory = new BasicHTMLViewFactory();
      basicHTMLFactory = new BasicEditorKit();
    }
    return basicHTMLFactory;
  }
  
  static class BasicDocument
    extends HTMLDocument
  {
    BasicDocument(StyleSheet paramStyleSheet, Font paramFont, Color paramColor)
    {
      super();
      setPreservesUnknownTags(false);
      setFontAndColor(paramFont, paramColor);
    }
    
    private void setFontAndColor(Font paramFont, Color paramColor)
    {
      getStyleSheet().addRule(SwingUtilities2.displayPropertiesToCSS(paramFont, paramColor));
    }
  }
  
  static class BasicEditorKit
    extends HTMLEditorKit
  {
    private static StyleSheet defaultStyles;
    
    BasicEditorKit() {}
    
    public StyleSheet getStyleSheet()
    {
      if (defaultStyles == null)
      {
        defaultStyles = new StyleSheet();
        StringReader localStringReader = new StringReader("p { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0 }body { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0 }");
        try
        {
          defaultStyles.loadRules(localStringReader, null);
        }
        catch (Throwable localThrowable) {}
        localStringReader.close();
        defaultStyles.addStyleSheet(super.getStyleSheet());
      }
      return defaultStyles;
    }
    
    public Document createDefaultDocument(Font paramFont, Color paramColor)
    {
      StyleSheet localStyleSheet1 = getStyleSheet();
      StyleSheet localStyleSheet2 = new StyleSheet();
      localStyleSheet2.addStyleSheet(localStyleSheet1);
      BasicHTML.BasicDocument localBasicDocument = new BasicHTML.BasicDocument(localStyleSheet2, paramFont, paramColor);
      localBasicDocument.setAsynchronousLoadPriority(Integer.MAX_VALUE);
      localBasicDocument.setPreservesUnknownTags(false);
      return localBasicDocument;
    }
    
    public ViewFactory getViewFactory()
    {
      return BasicHTML.basicHTMLViewFactory;
    }
  }
  
  static class BasicHTMLViewFactory
    extends HTMLEditorKit.HTMLFactory
  {
    BasicHTMLViewFactory() {}
    
    public View create(Element paramElement)
    {
      View localView = super.create(paramElement);
      if ((localView instanceof ImageView)) {
        ((ImageView)localView).setLoadsSynchronously(true);
      }
      return localView;
    }
  }
  
  static class Renderer
    extends View
  {
    private int width;
    private View view;
    private ViewFactory factory;
    private JComponent host;
    
    Renderer(JComponent paramJComponent, ViewFactory paramViewFactory, View paramView)
    {
      super();
      host = paramJComponent;
      factory = paramViewFactory;
      view = paramView;
      view.setParent(this);
      setSize(view.getPreferredSpan(0), view.getPreferredSpan(1));
    }
    
    public AttributeSet getAttributes()
    {
      return null;
    }
    
    public float getPreferredSpan(int paramInt)
    {
      if (paramInt == 0) {
        return width;
      }
      return view.getPreferredSpan(paramInt);
    }
    
    public float getMinimumSpan(int paramInt)
    {
      return view.getMinimumSpan(paramInt);
    }
    
    public float getMaximumSpan(int paramInt)
    {
      return 2.14748365E9F;
    }
    
    public void preferenceChanged(View paramView, boolean paramBoolean1, boolean paramBoolean2)
    {
      host.revalidate();
      host.repaint();
    }
    
    public float getAlignment(int paramInt)
    {
      return view.getAlignment(paramInt);
    }
    
    public void paint(Graphics paramGraphics, Shape paramShape)
    {
      Rectangle localRectangle = paramShape.getBounds();
      view.setSize(width, height);
      view.paint(paramGraphics, paramShape);
    }
    
    public void setParent(View paramView)
    {
      throw new Error("Can't set parent on root view");
    }
    
    public int getViewCount()
    {
      return 1;
    }
    
    public View getView(int paramInt)
    {
      return view;
    }
    
    public Shape modelToView(int paramInt, Shape paramShape, Position.Bias paramBias)
      throws BadLocationException
    {
      return view.modelToView(paramInt, paramShape, paramBias);
    }
    
    public Shape modelToView(int paramInt1, Position.Bias paramBias1, int paramInt2, Position.Bias paramBias2, Shape paramShape)
      throws BadLocationException
    {
      return view.modelToView(paramInt1, paramBias1, paramInt2, paramBias2, paramShape);
    }
    
    public int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias)
    {
      return view.viewToModel(paramFloat1, paramFloat2, paramShape, paramArrayOfBias);
    }
    
    public Document getDocument()
    {
      return view.getDocument();
    }
    
    public int getStartOffset()
    {
      return view.getStartOffset();
    }
    
    public int getEndOffset()
    {
      return view.getEndOffset();
    }
    
    public Element getElement()
    {
      return view.getElement();
    }
    
    public void setSize(float paramFloat1, float paramFloat2)
    {
      width = ((int)paramFloat1);
      view.setSize(paramFloat1, paramFloat2);
    }
    
    public Container getContainer()
    {
      return host;
    }
    
    public ViewFactory getViewFactory()
    {
      return factory;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicHTML.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */