package javax.swing.text.html;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Shape;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.DocumentEvent;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.ComponentView;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import sun.swing.text.html.FrameEditorPaneTag;

class FrameView
  extends ComponentView
  implements HyperlinkListener
{
  JEditorPane htmlPane;
  JScrollPane scroller;
  boolean editable;
  float width;
  float height;
  URL src;
  private boolean createdComponent;
  
  public FrameView(Element paramElement)
  {
    super(paramElement);
  }
  
  protected Component createComponent()
  {
    Element localElement = getElement();
    AttributeSet localAttributeSet = localElement.getAttributes();
    String str = (String)localAttributeSet.getAttribute(HTML.Attribute.SRC);
    if ((str != null) && (!str.equals(""))) {
      try
      {
        URL localURL = ((HTMLDocument)localElement.getDocument()).getBase();
        src = new URL(localURL, str);
        htmlPane = new FrameEditorPane();
        htmlPane.addHyperlinkListener(this);
        JEditorPane localJEditorPane = getHostPane();
        boolean bool = true;
        if (localJEditorPane != null)
        {
          htmlPane.setEditable(localJEditorPane.isEditable());
          localObject1 = (String)localJEditorPane.getClientProperty("charset");
          if (localObject1 != null) {
            htmlPane.putClientProperty("charset", localObject1);
          }
          localObject2 = (HTMLEditorKit)localJEditorPane.getEditorKit();
          if (localObject2 != null) {
            bool = ((HTMLEditorKit)localObject2).isAutoFormSubmission();
          }
        }
        htmlPane.setPage(src);
        Object localObject1 = (HTMLEditorKit)htmlPane.getEditorKit();
        if (localObject1 != null) {
          ((HTMLEditorKit)localObject1).setAutoFormSubmission(bool);
        }
        Object localObject2 = htmlPane.getDocument();
        if ((localObject2 instanceof HTMLDocument)) {
          ((HTMLDocument)localObject2).setFrameDocumentState(true);
        }
        setMargin();
        createScrollPane();
        setBorder();
      }
      catch (MalformedURLException localMalformedURLException)
      {
        localMalformedURLException.printStackTrace();
      }
      catch (IOException localIOException)
      {
        localIOException.printStackTrace();
      }
    }
    createdComponent = true;
    return scroller;
  }
  
  JEditorPane getHostPane()
  {
    for (Container localContainer = getContainer(); (localContainer != null) && (!(localContainer instanceof JEditorPane)); localContainer = localContainer.getParent()) {}
    return (JEditorPane)localContainer;
  }
  
  public void setParent(View paramView)
  {
    if (paramView != null)
    {
      JTextComponent localJTextComponent = (JTextComponent)paramView.getContainer();
      editable = localJTextComponent.isEditable();
    }
    super.setParent(paramView);
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape)
  {
    Container localContainer = getContainer();
    if ((localContainer != null) && (htmlPane != null) && (htmlPane.isEditable() != ((JTextComponent)localContainer).isEditable()))
    {
      editable = ((JTextComponent)localContainer).isEditable();
      htmlPane.setEditable(editable);
    }
    super.paint(paramGraphics, paramShape);
  }
  
  private void setMargin()
  {
    int i = 0;
    Insets localInsets1 = htmlPane.getMargin();
    int j = 0;
    AttributeSet localAttributeSet = getElement().getAttributes();
    String str = (String)localAttributeSet.getAttribute(HTML.Attribute.MARGINWIDTH);
    Insets localInsets2;
    if (localInsets1 != null) {
      localInsets2 = new Insets(top, left, right, bottom);
    } else {
      localInsets2 = new Insets(0, 0, 0, 0);
    }
    if (str != null)
    {
      i = Integer.parseInt(str);
      if (i > 0)
      {
        left = i;
        right = i;
        j = 1;
      }
    }
    str = (String)localAttributeSet.getAttribute(HTML.Attribute.MARGINHEIGHT);
    if (str != null)
    {
      i = Integer.parseInt(str);
      if (i > 0)
      {
        top = i;
        bottom = i;
        j = 1;
      }
    }
    if (j != 0) {
      htmlPane.setMargin(localInsets2);
    }
  }
  
  private void setBorder()
  {
    AttributeSet localAttributeSet = getElement().getAttributes();
    String str = (String)localAttributeSet.getAttribute(HTML.Attribute.FRAMEBORDER);
    if ((str != null) && ((str.equals("no")) || (str.equals("0")))) {
      scroller.setBorder(null);
    }
  }
  
  private void createScrollPane()
  {
    AttributeSet localAttributeSet = getElement().getAttributes();
    String str = (String)localAttributeSet.getAttribute(HTML.Attribute.SCROLLING);
    if (str == null) {
      str = "auto";
    }
    if (!str.equals("no"))
    {
      if (str.equals("yes")) {
        scroller = new JScrollPane(22, 32);
      } else {
        scroller = new JScrollPane();
      }
    }
    else {
      scroller = new JScrollPane(21, 31);
    }
    JViewport localJViewport = scroller.getViewport();
    localJViewport.add(htmlPane);
    localJViewport.setBackingStoreEnabled(true);
    scroller.setMinimumSize(new Dimension(5, 5));
    scroller.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
  }
  
  JEditorPane getOutermostJEditorPane()
  {
    View localView = getParent();
    FrameSetView localFrameSetView = null;
    while (localView != null)
    {
      if ((localView instanceof FrameSetView)) {
        localFrameSetView = (FrameSetView)localView;
      }
      localView = localView.getParent();
    }
    if (localFrameSetView != null) {
      return (JEditorPane)localFrameSetView.getContainer();
    }
    return null;
  }
  
  private boolean inNestedFrameSet()
  {
    FrameSetView localFrameSetView = (FrameSetView)getParent();
    return localFrameSetView.getParent() instanceof FrameSetView;
  }
  
  public void hyperlinkUpdate(HyperlinkEvent paramHyperlinkEvent)
  {
    JEditorPane localJEditorPane = getOutermostJEditorPane();
    if (localJEditorPane == null) {
      return;
    }
    if (!(paramHyperlinkEvent instanceof HTMLFrameHyperlinkEvent))
    {
      localJEditorPane.fireHyperlinkUpdate(paramHyperlinkEvent);
      return;
    }
    HTMLFrameHyperlinkEvent localHTMLFrameHyperlinkEvent = (HTMLFrameHyperlinkEvent)paramHyperlinkEvent;
    if (localHTMLFrameHyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
    {
      String str1 = localHTMLFrameHyperlinkEvent.getTarget();
      String str2 = str1;
      if ((str1.equals("_parent")) && (!inNestedFrameSet())) {
        str1 = "_top";
      }
      if ((paramHyperlinkEvent instanceof FormSubmitEvent))
      {
        HTMLEditorKit localHTMLEditorKit = (HTMLEditorKit)localJEditorPane.getEditorKit();
        if ((localHTMLEditorKit != null) && (localHTMLEditorKit.isAutoFormSubmission()))
        {
          if (str1.equals("_top"))
          {
            try
            {
              movePostData(localJEditorPane, str2);
              localJEditorPane.setPage(localHTMLFrameHyperlinkEvent.getURL());
            }
            catch (IOException localIOException2) {}
          }
          else
          {
            HTMLDocument localHTMLDocument = (HTMLDocument)localJEditorPane.getDocument();
            localHTMLDocument.processHTMLFrameHyperlinkEvent(localHTMLFrameHyperlinkEvent);
          }
        }
        else {
          localJEditorPane.fireHyperlinkUpdate(paramHyperlinkEvent);
        }
        return;
      }
      if (str1.equals("_top")) {
        try
        {
          localJEditorPane.setPage(localHTMLFrameHyperlinkEvent.getURL());
        }
        catch (IOException localIOException1) {}
      }
      if (!localJEditorPane.isEditable()) {
        localJEditorPane.fireHyperlinkUpdate(new HTMLFrameHyperlinkEvent(localJEditorPane, localHTMLFrameHyperlinkEvent.getEventType(), localHTMLFrameHyperlinkEvent.getURL(), localHTMLFrameHyperlinkEvent.getDescription(), getElement(), localHTMLFrameHyperlinkEvent.getInputEvent(), str1));
      }
    }
  }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
  {
    Element localElement = getElement();
    AttributeSet localAttributeSet = localElement.getAttributes();
    URL localURL1 = src;
    String str = (String)localAttributeSet.getAttribute(HTML.Attribute.SRC);
    URL localURL2 = ((HTMLDocument)localElement.getDocument()).getBase();
    try
    {
      if (!createdComponent) {
        return;
      }
      Object localObject = movePostData(htmlPane, null);
      src = new URL(localURL2, str);
      if ((localURL1.equals(src)) && (src.getRef() == null) && (localObject == null)) {
        return;
      }
      htmlPane.setPage(src);
      Document localDocument = htmlPane.getDocument();
      if ((localDocument instanceof HTMLDocument)) {
        ((HTMLDocument)localDocument).setFrameDocumentState(true);
      }
    }
    catch (MalformedURLException localMalformedURLException) {}catch (IOException localIOException) {}
  }
  
  private Object movePostData(JEditorPane paramJEditorPane, String paramString)
  {
    Object localObject = null;
    JEditorPane localJEditorPane = getOutermostJEditorPane();
    if (localJEditorPane != null)
    {
      if (paramString == null) {
        paramString = (String)getElement().getAttributes().getAttribute(HTML.Attribute.NAME);
      }
      if (paramString != null)
      {
        String str = "javax.swing.JEditorPane.postdata." + paramString;
        Document localDocument = localJEditorPane.getDocument();
        localObject = localDocument.getProperty(str);
        if (localObject != null)
        {
          paramJEditorPane.getDocument().putProperty("javax.swing.JEditorPane.postdata", localObject);
          localDocument.putProperty(str, null);
        }
      }
    }
    return localObject;
  }
  
  public float getMinimumSpan(int paramInt)
  {
    return 5.0F;
  }
  
  public float getMaximumSpan(int paramInt)
  {
    return 2.14748365E9F;
  }
  
  class FrameEditorPane
    extends JEditorPane
    implements FrameEditorPaneTag
  {
    FrameEditorPane() {}
    
    public EditorKit getEditorKitForContentType(String paramString)
    {
      EditorKit localEditorKit1 = super.getEditorKitForContentType(paramString);
      JEditorPane localJEditorPane = null;
      if ((localJEditorPane = getOutermostJEditorPane()) != null)
      {
        EditorKit localEditorKit2 = localJEditorPane.getEditorKitForContentType(paramString);
        if (!localEditorKit1.getClass().equals(localEditorKit2.getClass()))
        {
          localEditorKit1 = (EditorKit)localEditorKit2.clone();
          setEditorKitForContentType(paramString, localEditorKit1);
        }
      }
      return localEditorKit1;
    }
    
    FrameView getFrameView()
    {
      return FrameView.this;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\FrameView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */