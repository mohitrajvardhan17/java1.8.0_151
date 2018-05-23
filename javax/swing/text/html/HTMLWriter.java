package javax.swing.text.html;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;
import javax.swing.text.AbstractWriter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Segment;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

public class HTMLWriter
  extends AbstractWriter
{
  private Stack<Element> blockElementStack = new Stack();
  private boolean inContent = false;
  private boolean inPre = false;
  private int preEndOffset;
  private boolean inTextArea = false;
  private boolean newlineOutputed = false;
  private boolean completeDoc;
  private Vector<HTML.Tag> tags = new Vector(10);
  private Vector<Object> tagValues = new Vector(10);
  private Segment segment;
  private Vector<HTML.Tag> tagsToRemove = new Vector(10);
  private boolean wroteHead;
  private boolean replaceEntities;
  private char[] tempChars;
  private boolean indentNext = false;
  private boolean writeCSS = false;
  private MutableAttributeSet convAttr = new SimpleAttributeSet();
  private MutableAttributeSet oConvAttr = new SimpleAttributeSet();
  private boolean indented = false;
  
  public HTMLWriter(Writer paramWriter, HTMLDocument paramHTMLDocument)
  {
    this(paramWriter, paramHTMLDocument, 0, paramHTMLDocument.getLength());
  }
  
  public HTMLWriter(Writer paramWriter, HTMLDocument paramHTMLDocument, int paramInt1, int paramInt2)
  {
    super(paramWriter, paramHTMLDocument, paramInt1, paramInt2);
    completeDoc = ((paramInt1 == 0) && (paramInt2 == paramHTMLDocument.getLength()));
    setLineLength(80);
  }
  
  public void write()
    throws IOException, BadLocationException
  {
    ElementIterator localElementIterator = getElementIterator();
    Object localObject1 = null;
    wroteHead = false;
    setCurrentLineLength(0);
    replaceEntities = false;
    setCanWrapLines(false);
    if (segment == null) {
      segment = new Segment();
    }
    inPre = false;
    int i = 0;
    Element localElement;
    Object localObject2;
    while ((localElement = localElementIterator.next()) != null) {
      if (!inRange(localElement))
      {
        if ((completeDoc) && (localElement.getAttributes().getAttribute(StyleConstants.NameAttribute) == HTML.Tag.BODY)) {
          i = 1;
        }
      }
      else
      {
        if (localObject1 != null) {
          if (indentNeedsIncrementing((Element)localObject1, localElement))
          {
            incrIndent();
          }
          else if (((Element)localObject1).getParentElement() != localElement.getParentElement())
          {
            for (localObject2 = (Element)blockElementStack.peek(); localObject2 != localElement.getParentElement(); localObject2 = (Element)blockElementStack.peek())
            {
              blockElementStack.pop();
              if (!synthesizedElement((Element)localObject2))
              {
                AttributeSet localAttributeSet = ((Element)localObject2).getAttributes();
                if ((!matchNameAttribute(localAttributeSet, HTML.Tag.PRE)) && (!isFormElementWithContent(localAttributeSet))) {
                  decrIndent();
                }
                endTag((Element)localObject2);
              }
            }
          }
          else if (((Element)localObject1).getParentElement() == localElement.getParentElement())
          {
            localObject2 = (Element)blockElementStack.peek();
            if (localObject2 == localObject1)
            {
              blockElementStack.pop();
              endTag((Element)localObject2);
            }
          }
        }
        if ((!localElement.isLeaf()) || (isFormElementWithContent(localElement.getAttributes())))
        {
          blockElementStack.push(localElement);
          startTag(localElement);
        }
        else
        {
          emptyTag(localElement);
        }
        localObject1 = localElement;
      }
    }
    closeOutUnwantedEmbeddedTags(null);
    if (i != 0)
    {
      blockElementStack.pop();
      endTag((Element)localObject1);
    }
    while (!blockElementStack.empty())
    {
      localObject1 = (Element)blockElementStack.pop();
      if (!synthesizedElement((Element)localObject1))
      {
        localObject2 = ((Element)localObject1).getAttributes();
        if ((!matchNameAttribute((AttributeSet)localObject2, HTML.Tag.PRE)) && (!isFormElementWithContent((AttributeSet)localObject2))) {
          decrIndent();
        }
        endTag((Element)localObject1);
      }
    }
    if (completeDoc) {
      writeAdditionalComments();
    }
    segment.array = null;
  }
  
  protected void writeAttributes(AttributeSet paramAttributeSet)
    throws IOException
  {
    convAttr.removeAttributes(convAttr);
    convertToHTML32(paramAttributeSet, convAttr);
    Enumeration localEnumeration = convAttr.getAttributeNames();
    while (localEnumeration.hasMoreElements())
    {
      Object localObject = localEnumeration.nextElement();
      if ((!(localObject instanceof HTML.Tag)) && (!(localObject instanceof StyleConstants)) && (localObject != HTML.Attribute.ENDTAG)) {
        write(" " + localObject + "=\"" + convAttr.getAttribute(localObject) + "\"");
      }
    }
  }
  
  protected void emptyTag(Element paramElement)
    throws BadLocationException, IOException
  {
    if ((!inContent) && (!inPre)) {
      indentSmart();
    }
    AttributeSet localAttributeSet = paramElement.getAttributes();
    closeOutUnwantedEmbeddedTags(localAttributeSet);
    writeEmbeddedTags(localAttributeSet);
    if (matchNameAttribute(localAttributeSet, HTML.Tag.CONTENT))
    {
      inContent = true;
      text(paramElement);
    }
    else if (matchNameAttribute(localAttributeSet, HTML.Tag.COMMENT))
    {
      comment(paramElement);
    }
    else
    {
      boolean bool = isBlockTag(paramElement.getAttributes());
      if ((inContent) && (bool))
      {
        writeLineSeparator();
        indentSmart();
      }
      Object localObject1 = localAttributeSet != null ? localAttributeSet.getAttribute(StyleConstants.NameAttribute) : null;
      Object localObject2 = localAttributeSet != null ? localAttributeSet.getAttribute(HTML.Attribute.ENDTAG) : null;
      int i = 0;
      if ((localObject1 != null) && (localObject2 != null) && ((localObject2 instanceof String)) && (localObject2.equals("true"))) {
        i = 1;
      }
      if ((completeDoc) && (matchNameAttribute(localAttributeSet, HTML.Tag.HEAD)))
      {
        if (i != 0) {
          writeStyles(((HTMLDocument)getDocument()).getStyleSheet());
        }
        wroteHead = true;
      }
      write('<');
      if (i != 0) {
        write('/');
      }
      write(paramElement.getName());
      writeAttributes(localAttributeSet);
      write('>');
      if ((matchNameAttribute(localAttributeSet, HTML.Tag.TITLE)) && (i == 0))
      {
        Document localDocument = paramElement.getDocument();
        String str = (String)localDocument.getProperty("title");
        write(str);
      }
      else if ((!inContent) || (bool))
      {
        writeLineSeparator();
        if ((bool) && (inContent)) {
          indentSmart();
        }
      }
    }
  }
  
  protected boolean isBlockTag(AttributeSet paramAttributeSet)
  {
    Object localObject = paramAttributeSet.getAttribute(StyleConstants.NameAttribute);
    if ((localObject instanceof HTML.Tag))
    {
      HTML.Tag localTag = (HTML.Tag)localObject;
      return localTag.isBlock();
    }
    return false;
  }
  
  protected void startTag(Element paramElement)
    throws IOException, BadLocationException
  {
    if (synthesizedElement(paramElement)) {
      return;
    }
    AttributeSet localAttributeSet = paramElement.getAttributes();
    Object localObject = localAttributeSet.getAttribute(StyleConstants.NameAttribute);
    HTML.Tag localTag;
    if ((localObject instanceof HTML.Tag)) {
      localTag = (HTML.Tag)localObject;
    } else {
      localTag = null;
    }
    if (localTag == HTML.Tag.PRE)
    {
      inPre = true;
      preEndOffset = paramElement.getEndOffset();
    }
    closeOutUnwantedEmbeddedTags(localAttributeSet);
    if (inContent)
    {
      writeLineSeparator();
      inContent = false;
      newlineOutputed = false;
    }
    if ((completeDoc) && (localTag == HTML.Tag.BODY) && (!wroteHead))
    {
      wroteHead = true;
      indentSmart();
      write("<head>");
      writeLineSeparator();
      incrIndent();
      writeStyles(((HTMLDocument)getDocument()).getStyleSheet());
      decrIndent();
      writeLineSeparator();
      indentSmart();
      write("</head>");
      writeLineSeparator();
    }
    indentSmart();
    write('<');
    write(paramElement.getName());
    writeAttributes(localAttributeSet);
    write('>');
    if (localTag != HTML.Tag.PRE) {
      writeLineSeparator();
    }
    if (localTag == HTML.Tag.TEXTAREA)
    {
      textAreaContent(paramElement.getAttributes());
    }
    else if (localTag == HTML.Tag.SELECT)
    {
      selectContent(paramElement.getAttributes());
    }
    else if ((completeDoc) && (localTag == HTML.Tag.BODY))
    {
      writeMaps(((HTMLDocument)getDocument()).getMaps());
    }
    else if (localTag == HTML.Tag.HEAD)
    {
      HTMLDocument localHTMLDocument = (HTMLDocument)getDocument();
      wroteHead = true;
      incrIndent();
      writeStyles(localHTMLDocument.getStyleSheet());
      if (localHTMLDocument.hasBaseTag())
      {
        indentSmart();
        write("<base href=\"" + localHTMLDocument.getBase() + "\">");
        writeLineSeparator();
      }
      decrIndent();
    }
  }
  
  protected void textAreaContent(AttributeSet paramAttributeSet)
    throws BadLocationException, IOException
  {
    Document localDocument = (Document)paramAttributeSet.getAttribute(StyleConstants.ModelAttribute);
    if ((localDocument != null) && (localDocument.getLength() > 0))
    {
      if (segment == null) {
        segment = new Segment();
      }
      localDocument.getText(0, localDocument.getLength(), segment);
      if (segment.count > 0)
      {
        inTextArea = true;
        incrIndent();
        indentSmart();
        setCanWrapLines(true);
        replaceEntities = true;
        write(segment.array, segment.offset, segment.count);
        replaceEntities = false;
        setCanWrapLines(false);
        writeLineSeparator();
        inTextArea = false;
        decrIndent();
      }
    }
  }
  
  protected void text(Element paramElement)
    throws BadLocationException, IOException
  {
    int i = Math.max(getStartOffset(), paramElement.getStartOffset());
    int j = Math.min(getEndOffset(), paramElement.getEndOffset());
    if (i < j)
    {
      if (segment == null) {
        segment = new Segment();
      }
      getDocument().getText(i, j - i, segment);
      newlineOutputed = false;
      if (segment.count > 0)
      {
        if (segment.array[(segment.offset + segment.count - 1)] == '\n') {
          newlineOutputed = true;
        }
        if ((inPre) && (j == preEndOffset)) {
          if (segment.count > 1) {
            segment.count -= 1;
          } else {
            return;
          }
        }
        replaceEntities = true;
        setCanWrapLines(!inPre);
        write(segment.array, segment.offset, segment.count);
        setCanWrapLines(false);
        replaceEntities = false;
      }
    }
  }
  
  protected void selectContent(AttributeSet paramAttributeSet)
    throws IOException
  {
    Object localObject1 = paramAttributeSet.getAttribute(StyleConstants.ModelAttribute);
    incrIndent();
    Object localObject2;
    int i;
    int j;
    Option localOption;
    if ((localObject1 instanceof OptionListModel))
    {
      localObject2 = (OptionListModel)localObject1;
      i = ((OptionListModel)localObject2).getSize();
      for (j = 0; j < i; j++)
      {
        localOption = (Option)((OptionListModel)localObject2).getElementAt(j);
        writeOption(localOption);
      }
    }
    else if ((localObject1 instanceof OptionComboBoxModel))
    {
      localObject2 = (OptionComboBoxModel)localObject1;
      i = ((OptionComboBoxModel)localObject2).getSize();
      for (j = 0; j < i; j++)
      {
        localOption = (Option)((OptionComboBoxModel)localObject2).getElementAt(j);
        writeOption(localOption);
      }
    }
    decrIndent();
  }
  
  protected void writeOption(Option paramOption)
    throws IOException
  {
    indentSmart();
    write('<');
    write("option");
    Object localObject = paramOption.getAttributes().getAttribute(HTML.Attribute.VALUE);
    if (localObject != null) {
      write(" value=" + localObject);
    }
    if (paramOption.isSelected()) {
      write(" selected");
    }
    write('>');
    if (paramOption.getLabel() != null) {
      write(paramOption.getLabel());
    }
    writeLineSeparator();
  }
  
  protected void endTag(Element paramElement)
    throws IOException
  {
    if (synthesizedElement(paramElement)) {
      return;
    }
    closeOutUnwantedEmbeddedTags(paramElement.getAttributes());
    if (inContent)
    {
      if ((!newlineOutputed) && (!inPre)) {
        writeLineSeparator();
      }
      newlineOutputed = false;
      inContent = false;
    }
    if (!inPre) {
      indentSmart();
    }
    if (matchNameAttribute(paramElement.getAttributes(), HTML.Tag.PRE)) {
      inPre = false;
    }
    write('<');
    write('/');
    write(paramElement.getName());
    write('>');
    writeLineSeparator();
  }
  
  protected void comment(Element paramElement)
    throws BadLocationException, IOException
  {
    AttributeSet localAttributeSet = paramElement.getAttributes();
    if (matchNameAttribute(localAttributeSet, HTML.Tag.COMMENT))
    {
      Object localObject = localAttributeSet.getAttribute(HTML.Attribute.COMMENT);
      if ((localObject instanceof String)) {
        writeComment((String)localObject);
      } else {
        writeComment(null);
      }
    }
  }
  
  void writeComment(String paramString)
    throws IOException
  {
    write("<!--");
    if (paramString != null) {
      write(paramString);
    }
    write("-->");
    writeLineSeparator();
    indentSmart();
  }
  
  void writeAdditionalComments()
    throws IOException
  {
    Object localObject = getDocument().getProperty("AdditionalComments");
    if ((localObject instanceof Vector))
    {
      Vector localVector = (Vector)localObject;
      int i = 0;
      int j = localVector.size();
      while (i < j)
      {
        writeComment(localVector.elementAt(i).toString());
        i++;
      }
    }
  }
  
  protected boolean synthesizedElement(Element paramElement)
  {
    return matchNameAttribute(paramElement.getAttributes(), HTML.Tag.IMPLIED);
  }
  
  protected boolean matchNameAttribute(AttributeSet paramAttributeSet, HTML.Tag paramTag)
  {
    Object localObject = paramAttributeSet.getAttribute(StyleConstants.NameAttribute);
    if ((localObject instanceof HTML.Tag))
    {
      HTML.Tag localTag = (HTML.Tag)localObject;
      if (localTag == paramTag) {
        return true;
      }
    }
    return false;
  }
  
  protected void writeEmbeddedTags(AttributeSet paramAttributeSet)
    throws IOException
  {
    paramAttributeSet = convertToHTML(paramAttributeSet, oConvAttr);
    Enumeration localEnumeration = paramAttributeSet.getAttributeNames();
    while (localEnumeration.hasMoreElements())
    {
      Object localObject1 = localEnumeration.nextElement();
      if ((localObject1 instanceof HTML.Tag))
      {
        HTML.Tag localTag = (HTML.Tag)localObject1;
        if ((localTag != HTML.Tag.FORM) && (!tags.contains(localTag)))
        {
          write('<');
          write(localTag.toString());
          Object localObject2 = paramAttributeSet.getAttribute(localTag);
          if ((localObject2 != null) && ((localObject2 instanceof AttributeSet))) {
            writeAttributes((AttributeSet)localObject2);
          }
          write('>');
          tags.addElement(localTag);
          tagValues.addElement(localObject2);
        }
      }
    }
  }
  
  private boolean noMatchForTagInAttributes(AttributeSet paramAttributeSet, HTML.Tag paramTag, Object paramObject)
  {
    if ((paramAttributeSet != null) && (paramAttributeSet.isDefined(paramTag)))
    {
      Object localObject = paramAttributeSet.getAttribute(paramTag);
      if (paramObject == null ? localObject == null : (localObject != null) && (paramObject.equals(localObject))) {
        return false;
      }
    }
    return true;
  }
  
  protected void closeOutUnwantedEmbeddedTags(AttributeSet paramAttributeSet)
    throws IOException
  {
    tagsToRemove.removeAllElements();
    paramAttributeSet = convertToHTML(paramAttributeSet, null);
    int i = -1;
    int j = tags.size();
    HTML.Tag localTag;
    for (int k = j - 1; k >= 0; k--)
    {
      localTag = (HTML.Tag)tags.elementAt(k);
      Object localObject1 = tagValues.elementAt(k);
      if ((paramAttributeSet == null) || (noMatchForTagInAttributes(paramAttributeSet, localTag, localObject1)))
      {
        i = k;
        tagsToRemove.addElement(localTag);
      }
    }
    if (i != -1)
    {
      k = j - i == tagsToRemove.size() ? 1 : 0;
      for (int m = j - 1; m >= i; m--)
      {
        localTag = (HTML.Tag)tags.elementAt(m);
        if ((k != 0) || (tagsToRemove.contains(localTag)))
        {
          tags.removeElementAt(m);
          tagValues.removeElementAt(m);
        }
        write('<');
        write('/');
        write(localTag.toString());
        write('>');
      }
      j = tags.size();
      for (m = i; m < j; m++)
      {
        localTag = (HTML.Tag)tags.elementAt(m);
        write('<');
        write(localTag.toString());
        Object localObject2 = tagValues.elementAt(m);
        if ((localObject2 != null) && ((localObject2 instanceof AttributeSet))) {
          writeAttributes((AttributeSet)localObject2);
        }
        write('>');
      }
    }
  }
  
  private boolean isFormElementWithContent(AttributeSet paramAttributeSet)
  {
    return (matchNameAttribute(paramAttributeSet, HTML.Tag.TEXTAREA)) || (matchNameAttribute(paramAttributeSet, HTML.Tag.SELECT));
  }
  
  private boolean indentNeedsIncrementing(Element paramElement1, Element paramElement2)
  {
    if ((paramElement2.getParentElement() == paramElement1) && (!inPre))
    {
      if (indentNext)
      {
        indentNext = false;
        return true;
      }
      if (synthesizedElement(paramElement2)) {
        indentNext = true;
      } else if (!synthesizedElement(paramElement1)) {
        return true;
      }
    }
    return false;
  }
  
  void writeMaps(Enumeration paramEnumeration)
    throws IOException
  {
    if (paramEnumeration != null) {
      while (paramEnumeration.hasMoreElements())
      {
        Map localMap = (Map)paramEnumeration.nextElement();
        String str = localMap.getName();
        incrIndent();
        indentSmart();
        write("<map");
        if (str != null)
        {
          write(" name=\"");
          write(str);
          write("\">");
        }
        else
        {
          write('>');
        }
        writeLineSeparator();
        incrIndent();
        AttributeSet[] arrayOfAttributeSet = localMap.getAreas();
        if (arrayOfAttributeSet != null)
        {
          int i = 0;
          int j = arrayOfAttributeSet.length;
          while (i < j)
          {
            indentSmart();
            write("<area");
            writeAttributes(arrayOfAttributeSet[i]);
            write("></area>");
            writeLineSeparator();
            i++;
          }
        }
        decrIndent();
        indentSmart();
        write("</map>");
        writeLineSeparator();
        decrIndent();
      }
    }
  }
  
  void writeStyles(StyleSheet paramStyleSheet)
    throws IOException
  {
    if (paramStyleSheet != null)
    {
      Enumeration localEnumeration = paramStyleSheet.getStyleNames();
      if (localEnumeration != null)
      {
        for (boolean bool = false; localEnumeration.hasMoreElements(); bool = true)
        {
          String str = (String)localEnumeration.nextElement();
          if (("default".equals(str)) || (!writeStyle(str, paramStyleSheet.getStyle(str), bool))) {}
        }
        if (bool) {
          writeStyleEndTag();
        }
      }
    }
  }
  
  boolean writeStyle(String paramString, Style paramStyle, boolean paramBoolean)
    throws IOException
  {
    boolean bool = false;
    Enumeration localEnumeration = paramStyle.getAttributeNames();
    if (localEnumeration != null) {
      while (localEnumeration.hasMoreElements())
      {
        Object localObject = localEnumeration.nextElement();
        if ((localObject instanceof CSS.Attribute))
        {
          String str = paramStyle.getAttribute(localObject).toString();
          if (str != null)
          {
            if (!paramBoolean)
            {
              writeStyleStartTag();
              paramBoolean = true;
            }
            if (!bool)
            {
              bool = true;
              indentSmart();
              write(paramString);
              write(" {");
            }
            else
            {
              write(";");
            }
            write(' ');
            write(localObject.toString());
            write(": ");
            write(str);
          }
        }
      }
    }
    if (bool)
    {
      write(" }");
      writeLineSeparator();
    }
    return bool;
  }
  
  void writeStyleStartTag()
    throws IOException
  {
    indentSmart();
    write("<style type=\"text/css\">");
    incrIndent();
    writeLineSeparator();
    indentSmart();
    write("<!--");
    incrIndent();
    writeLineSeparator();
  }
  
  void writeStyleEndTag()
    throws IOException
  {
    decrIndent();
    indentSmart();
    write("-->");
    writeLineSeparator();
    decrIndent();
    indentSmart();
    write("</style>");
    writeLineSeparator();
    indentSmart();
  }
  
  AttributeSet convertToHTML(AttributeSet paramAttributeSet, MutableAttributeSet paramMutableAttributeSet)
  {
    if (paramMutableAttributeSet == null) {
      paramMutableAttributeSet = convAttr;
    }
    paramMutableAttributeSet.removeAttributes(paramMutableAttributeSet);
    if (writeCSS) {
      convertToHTML40(paramAttributeSet, paramMutableAttributeSet);
    } else {
      convertToHTML32(paramAttributeSet, paramMutableAttributeSet);
    }
    return paramMutableAttributeSet;
  }
  
  private static void convertToHTML32(AttributeSet paramAttributeSet, MutableAttributeSet paramMutableAttributeSet)
  {
    if (paramAttributeSet == null) {
      return;
    }
    Enumeration localEnumeration = paramAttributeSet.getAttributeNames();
    String str = "";
    while (localEnumeration.hasMoreElements())
    {
      Object localObject1 = localEnumeration.nextElement();
      Object localObject2;
      if ((localObject1 instanceof CSS.Attribute))
      {
        if ((localObject1 == CSS.Attribute.FONT_FAMILY) || (localObject1 == CSS.Attribute.FONT_SIZE) || (localObject1 == CSS.Attribute.COLOR))
        {
          createFontAttribute((CSS.Attribute)localObject1, paramAttributeSet, paramMutableAttributeSet);
        }
        else if (localObject1 == CSS.Attribute.FONT_WEIGHT)
        {
          localObject2 = (CSS.FontWeight)paramAttributeSet.getAttribute(CSS.Attribute.FONT_WEIGHT);
          if ((localObject2 != null) && (((CSS.FontWeight)localObject2).getValue() > 400)) {
            addAttribute(paramMutableAttributeSet, HTML.Tag.B, SimpleAttributeSet.EMPTY);
          }
        }
        else if (localObject1 == CSS.Attribute.FONT_STYLE)
        {
          localObject2 = paramAttributeSet.getAttribute(localObject1).toString();
          if (((String)localObject2).indexOf("italic") >= 0) {
            addAttribute(paramMutableAttributeSet, HTML.Tag.I, SimpleAttributeSet.EMPTY);
          }
        }
        else if (localObject1 == CSS.Attribute.TEXT_DECORATION)
        {
          localObject2 = paramAttributeSet.getAttribute(localObject1).toString();
          if (((String)localObject2).indexOf("underline") >= 0) {
            addAttribute(paramMutableAttributeSet, HTML.Tag.U, SimpleAttributeSet.EMPTY);
          }
          if (((String)localObject2).indexOf("line-through") >= 0) {
            addAttribute(paramMutableAttributeSet, HTML.Tag.STRIKE, SimpleAttributeSet.EMPTY);
          }
        }
        else if (localObject1 == CSS.Attribute.VERTICAL_ALIGN)
        {
          localObject2 = paramAttributeSet.getAttribute(localObject1).toString();
          if (((String)localObject2).indexOf("sup") >= 0) {
            addAttribute(paramMutableAttributeSet, HTML.Tag.SUP, SimpleAttributeSet.EMPTY);
          }
          if (((String)localObject2).indexOf("sub") >= 0) {
            addAttribute(paramMutableAttributeSet, HTML.Tag.SUB, SimpleAttributeSet.EMPTY);
          }
        }
        else if (localObject1 == CSS.Attribute.TEXT_ALIGN)
        {
          addAttribute(paramMutableAttributeSet, HTML.Attribute.ALIGN, paramAttributeSet.getAttribute(localObject1).toString());
        }
        else
        {
          if (str.length() > 0) {
            str = str + "; ";
          }
          str = str + localObject1 + ": " + paramAttributeSet.getAttribute(localObject1);
        }
      }
      else
      {
        localObject2 = paramAttributeSet.getAttribute(localObject1);
        if ((localObject2 instanceof AttributeSet)) {
          localObject2 = ((AttributeSet)localObject2).copyAttributes();
        }
        addAttribute(paramMutableAttributeSet, localObject1, localObject2);
      }
    }
    if (str.length() > 0) {
      paramMutableAttributeSet.addAttribute(HTML.Attribute.STYLE, str);
    }
  }
  
  private static void addAttribute(MutableAttributeSet paramMutableAttributeSet, Object paramObject1, Object paramObject2)
  {
    Object localObject = paramMutableAttributeSet.getAttribute(paramObject1);
    if ((localObject == null) || (localObject == SimpleAttributeSet.EMPTY)) {
      paramMutableAttributeSet.addAttribute(paramObject1, paramObject2);
    } else if (((localObject instanceof MutableAttributeSet)) && ((paramObject2 instanceof AttributeSet))) {
      ((MutableAttributeSet)localObject).addAttributes((AttributeSet)paramObject2);
    }
  }
  
  private static void createFontAttribute(CSS.Attribute paramAttribute, AttributeSet paramAttributeSet, MutableAttributeSet paramMutableAttributeSet)
  {
    Object localObject = (MutableAttributeSet)paramMutableAttributeSet.getAttribute(HTML.Tag.FONT);
    if (localObject == null)
    {
      localObject = new SimpleAttributeSet();
      paramMutableAttributeSet.addAttribute(HTML.Tag.FONT, localObject);
    }
    String str = paramAttributeSet.getAttribute(paramAttribute).toString();
    if (paramAttribute == CSS.Attribute.FONT_FAMILY) {
      ((MutableAttributeSet)localObject).addAttribute(HTML.Attribute.FACE, str);
    } else if (paramAttribute == CSS.Attribute.FONT_SIZE) {
      ((MutableAttributeSet)localObject).addAttribute(HTML.Attribute.SIZE, str);
    } else if (paramAttribute == CSS.Attribute.COLOR) {
      ((MutableAttributeSet)localObject).addAttribute(HTML.Attribute.COLOR, str);
    }
  }
  
  private static void convertToHTML40(AttributeSet paramAttributeSet, MutableAttributeSet paramMutableAttributeSet)
  {
    Enumeration localEnumeration = paramAttributeSet.getAttributeNames();
    String str = "";
    while (localEnumeration.hasMoreElements())
    {
      Object localObject = localEnumeration.nextElement();
      if ((localObject instanceof CSS.Attribute)) {
        str = str + " " + localObject + "=" + paramAttributeSet.getAttribute(localObject) + ";";
      } else {
        paramMutableAttributeSet.addAttribute(localObject, paramAttributeSet.getAttribute(localObject));
      }
    }
    if (str.length() > 0) {
      paramMutableAttributeSet.addAttribute(HTML.Attribute.STYLE, str);
    }
  }
  
  protected void writeLineSeparator()
    throws IOException
  {
    boolean bool = replaceEntities;
    replaceEntities = false;
    super.writeLineSeparator();
    replaceEntities = bool;
    indented = false;
  }
  
  protected void output(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    if (!replaceEntities)
    {
      super.output(paramArrayOfChar, paramInt1, paramInt2);
      return;
    }
    int i = paramInt1;
    paramInt2 += paramInt1;
    for (int j = paramInt1; j < paramInt2; j++) {
      switch (paramArrayOfChar[j])
      {
      case '<': 
        if (j > i) {
          super.output(paramArrayOfChar, i, j - i);
        }
        i = j + 1;
        output("&lt;");
        break;
      case '>': 
        if (j > i) {
          super.output(paramArrayOfChar, i, j - i);
        }
        i = j + 1;
        output("&gt;");
        break;
      case '&': 
        if (j > i) {
          super.output(paramArrayOfChar, i, j - i);
        }
        i = j + 1;
        output("&amp;");
        break;
      case '"': 
        if (j > i) {
          super.output(paramArrayOfChar, i, j - i);
        }
        i = j + 1;
        output("&quot;");
        break;
      case '\t': 
      case '\n': 
      case '\r': 
        break;
      default: 
        if ((paramArrayOfChar[j] < ' ') || (paramArrayOfChar[j] > ''))
        {
          if (j > i) {
            super.output(paramArrayOfChar, i, j - i);
          }
          i = j + 1;
          output("&#");
          output(String.valueOf(paramArrayOfChar[j]));
          output(";");
        }
        break;
      }
    }
    if (i < paramInt2) {
      super.output(paramArrayOfChar, i, paramInt2 - i);
    }
  }
  
  private void output(String paramString)
    throws IOException
  {
    int i = paramString.length();
    if ((tempChars == null) || (tempChars.length < i)) {
      tempChars = new char[i];
    }
    paramString.getChars(0, i, tempChars, 0);
    super.output(tempChars, 0, i);
  }
  
  private void indentSmart()
    throws IOException
  {
    if (!indented)
    {
      indent();
      indented = true;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\HTMLWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */