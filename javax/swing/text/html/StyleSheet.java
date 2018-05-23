package javax.swing.text.html;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.UIResource;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyleContext.SmallAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;
import sun.swing.SwingUtilities2;

public class StyleSheet
  extends StyleContext
{
  static final Border noBorder = new EmptyBorder(0, 0, 0, 0);
  static final int DEFAULT_FONT_SIZE = 3;
  private CSS css;
  private SelectorMapping selectorMapping = new SelectorMapping(0);
  private Hashtable<String, ResolvedStyle> resolvedStyles = new Hashtable();
  private Vector<StyleSheet> linkedStyleSheets;
  private URL base;
  static final int[] sizeMapDefault = { 8, 10, 12, 14, 18, 24, 36 };
  private int[] sizeMap = sizeMapDefault;
  private boolean w3cLengthUnits = false;
  
  public StyleSheet()
  {
    if (css == null) {
      css = new CSS();
    }
  }
  
  public Style getRule(HTML.Tag paramTag, Element paramElement)
  {
    SearchBuffer localSearchBuffer = SearchBuffer.obtainSearchBuffer();
    try
    {
      Vector localVector = localSearchBuffer.getVector();
      for (Element localElement = paramElement; localElement != null; localElement = localElement.getParentElement()) {
        localVector.addElement(localElement);
      }
      int i = localVector.size();
      StringBuffer localStringBuffer = localSearchBuffer.getStringBuffer();
      for (int j = i - 1; j >= 1; j--)
      {
        paramElement = (Element)localVector.elementAt(j);
        localAttributeSet = paramElement.getAttributes();
        Object localObject1 = localAttributeSet.getAttribute(StyleConstants.NameAttribute);
        String str = localObject1.toString();
        localStringBuffer.append(str);
        if (localAttributeSet != null) {
          if (localAttributeSet.isDefined(HTML.Attribute.ID))
          {
            localStringBuffer.append('#');
            localStringBuffer.append(localAttributeSet.getAttribute(HTML.Attribute.ID));
          }
          else if (localAttributeSet.isDefined(HTML.Attribute.CLASS))
          {
            localStringBuffer.append('.');
            localStringBuffer.append(localAttributeSet.getAttribute(HTML.Attribute.CLASS));
          }
        }
        localStringBuffer.append(' ');
      }
      localStringBuffer.append(paramTag.toString());
      paramElement = (Element)localVector.elementAt(0);
      AttributeSet localAttributeSet = paramElement.getAttributes();
      if (paramElement.isLeaf())
      {
        localObject2 = localAttributeSet.getAttribute(paramTag);
        if ((localObject2 instanceof AttributeSet)) {
          localAttributeSet = (AttributeSet)localObject2;
        } else {
          localAttributeSet = null;
        }
      }
      if (localAttributeSet != null) {
        if (localAttributeSet.isDefined(HTML.Attribute.ID))
        {
          localStringBuffer.append('#');
          localStringBuffer.append(localAttributeSet.getAttribute(HTML.Attribute.ID));
        }
        else if (localAttributeSet.isDefined(HTML.Attribute.CLASS))
        {
          localStringBuffer.append('.');
          localStringBuffer.append(localAttributeSet.getAttribute(HTML.Attribute.CLASS));
        }
      }
      Object localObject2 = getResolvedStyle(localStringBuffer.toString(), localVector, paramTag);
      Object localObject3 = localObject2;
      return (Style)localObject3;
    }
    finally
    {
      SearchBuffer.releaseSearchBuffer(localSearchBuffer);
    }
  }
  
  public Style getRule(String paramString)
  {
    paramString = cleanSelectorString(paramString);
    if (paramString != null)
    {
      Style localStyle = getResolvedStyle(paramString);
      return localStyle;
    }
    return null;
  }
  
  public void addRule(String paramString)
  {
    if (paramString != null) {
      if (paramString == "BASE_SIZE_DISABLE")
      {
        sizeMap = sizeMapDefault;
      }
      else if (paramString.startsWith("BASE_SIZE "))
      {
        rebaseSizeMap(Integer.parseInt(paramString.substring("BASE_SIZE ".length())));
      }
      else if (paramString == "W3C_LENGTH_UNITS_ENABLE")
      {
        w3cLengthUnits = true;
      }
      else if (paramString == "W3C_LENGTH_UNITS_DISABLE")
      {
        w3cLengthUnits = false;
      }
      else
      {
        CssParser localCssParser = new CssParser();
        try
        {
          localCssParser.parse(getBase(), new StringReader(paramString), false, false);
        }
        catch (IOException localIOException) {}
      }
    }
  }
  
  public AttributeSet getDeclaration(String paramString)
  {
    if (paramString == null) {
      return SimpleAttributeSet.EMPTY;
    }
    CssParser localCssParser = new CssParser();
    return localCssParser.parseDeclaration(paramString);
  }
  
  public void loadRules(Reader paramReader, URL paramURL)
    throws IOException
  {
    CssParser localCssParser = new CssParser();
    localCssParser.parse(paramURL, paramReader, false, false);
  }
  
  public AttributeSet getViewAttributes(View paramView)
  {
    return new ViewAttributeSet(paramView);
  }
  
  public void removeStyle(String paramString)
  {
    Style localStyle1 = getStyle(paramString);
    if (localStyle1 != null)
    {
      String str = cleanSelectorString(paramString);
      String[] arrayOfString = getSimpleSelectors(str);
      synchronized (this)
      {
        SelectorMapping localSelectorMapping = getRootSelectorMapping();
        for (int i = arrayOfString.length - 1; i >= 0; i--) {
          localSelectorMapping = localSelectorMapping.getChildSelectorMapping(arrayOfString[i], true);
        }
        Style localStyle2 = localSelectorMapping.getStyle();
        if (localStyle2 != null)
        {
          localSelectorMapping.setStyle(null);
          if (resolvedStyles.size() > 0)
          {
            Enumeration localEnumeration = resolvedStyles.elements();
            while (localEnumeration.hasMoreElements())
            {
              ResolvedStyle localResolvedStyle = (ResolvedStyle)localEnumeration.nextElement();
              localResolvedStyle.removeStyle(localStyle2);
            }
          }
        }
      }
    }
    super.removeStyle(paramString);
  }
  
  public void addStyleSheet(StyleSheet paramStyleSheet)
  {
    synchronized (this)
    {
      if (linkedStyleSheets == null) {
        linkedStyleSheets = new Vector();
      }
      if (!linkedStyleSheets.contains(paramStyleSheet))
      {
        int i = 0;
        if (((paramStyleSheet instanceof UIResource)) && (linkedStyleSheets.size() > 1)) {
          i = linkedStyleSheets.size() - 1;
        }
        linkedStyleSheets.insertElementAt(paramStyleSheet, i);
        linkStyleSheetAt(paramStyleSheet, i);
      }
    }
  }
  
  public void removeStyleSheet(StyleSheet paramStyleSheet)
  {
    synchronized (this)
    {
      if (linkedStyleSheets != null)
      {
        int i = linkedStyleSheets.indexOf(paramStyleSheet);
        if (i != -1)
        {
          linkedStyleSheets.removeElementAt(i);
          unlinkStyleSheet(paramStyleSheet, i);
          if ((i == 0) && (linkedStyleSheets.size() == 0)) {
            linkedStyleSheets = null;
          }
        }
      }
    }
  }
  
  public StyleSheet[] getStyleSheets()
  {
    StyleSheet[] arrayOfStyleSheet;
    synchronized (this)
    {
      if (linkedStyleSheets != null)
      {
        arrayOfStyleSheet = new StyleSheet[linkedStyleSheets.size()];
        linkedStyleSheets.copyInto(arrayOfStyleSheet);
      }
      else
      {
        arrayOfStyleSheet = null;
      }
    }
    return arrayOfStyleSheet;
  }
  
  public void importStyleSheet(URL paramURL)
  {
    try
    {
      InputStream localInputStream = paramURL.openStream();
      BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localInputStream));
      CssParser localCssParser = new CssParser();
      localCssParser.parse(paramURL, localBufferedReader, false, true);
      localBufferedReader.close();
      localInputStream.close();
    }
    catch (Throwable localThrowable) {}
  }
  
  public void setBase(URL paramURL)
  {
    base = paramURL;
  }
  
  public URL getBase()
  {
    return base;
  }
  
  public void addCSSAttribute(MutableAttributeSet paramMutableAttributeSet, CSS.Attribute paramAttribute, String paramString)
  {
    css.addInternalCSSValue(paramMutableAttributeSet, paramAttribute, paramString);
  }
  
  public boolean addCSSAttributeFromHTML(MutableAttributeSet paramMutableAttributeSet, CSS.Attribute paramAttribute, String paramString)
  {
    Object localObject = css.getCssValue(paramAttribute, paramString);
    if (localObject != null)
    {
      paramMutableAttributeSet.addAttribute(paramAttribute, localObject);
      return true;
    }
    return false;
  }
  
  public AttributeSet translateHTMLToCSS(AttributeSet paramAttributeSet)
  {
    AttributeSet localAttributeSet = css.translateHTMLToCSS(paramAttributeSet);
    Style localStyle = addStyle(null, null);
    localStyle.addAttributes(localAttributeSet);
    return localStyle;
  }
  
  public AttributeSet addAttribute(AttributeSet paramAttributeSet, Object paramObject1, Object paramObject2)
  {
    if (css == null) {
      css = new CSS();
    }
    if ((paramObject1 instanceof StyleConstants))
    {
      HTML.Tag localTag = HTML.getTagForStyleConstantsKey((StyleConstants)paramObject1);
      if ((localTag != null) && (paramAttributeSet.isDefined(localTag))) {
        paramAttributeSet = removeAttribute(paramAttributeSet, localTag);
      }
      Object localObject = css.styleConstantsValueToCSSValue((StyleConstants)paramObject1, paramObject2);
      if (localObject != null)
      {
        CSS.Attribute localAttribute = css.styleConstantsKeyToCSSKey((StyleConstants)paramObject1);
        if (localAttribute != null) {
          return super.addAttribute(paramAttributeSet, localAttribute, localObject);
        }
      }
    }
    return super.addAttribute(paramAttributeSet, paramObject1, paramObject2);
  }
  
  public AttributeSet addAttributes(AttributeSet paramAttributeSet1, AttributeSet paramAttributeSet2)
  {
    if (!(paramAttributeSet2 instanceof HTMLDocument.TaggedAttributeSet)) {
      paramAttributeSet1 = removeHTMLTags(paramAttributeSet1, paramAttributeSet2);
    }
    return super.addAttributes(paramAttributeSet1, convertAttributeSet(paramAttributeSet2));
  }
  
  public AttributeSet removeAttribute(AttributeSet paramAttributeSet, Object paramObject)
  {
    if ((paramObject instanceof StyleConstants))
    {
      HTML.Tag localTag = HTML.getTagForStyleConstantsKey((StyleConstants)paramObject);
      if (localTag != null) {
        paramAttributeSet = super.removeAttribute(paramAttributeSet, localTag);
      }
      CSS.Attribute localAttribute = css.styleConstantsKeyToCSSKey((StyleConstants)paramObject);
      if (localAttribute != null) {
        return super.removeAttribute(paramAttributeSet, localAttribute);
      }
    }
    return super.removeAttribute(paramAttributeSet, paramObject);
  }
  
  public AttributeSet removeAttributes(AttributeSet paramAttributeSet, Enumeration<?> paramEnumeration)
  {
    return super.removeAttributes(paramAttributeSet, paramEnumeration);
  }
  
  public AttributeSet removeAttributes(AttributeSet paramAttributeSet1, AttributeSet paramAttributeSet2)
  {
    if (paramAttributeSet1 != paramAttributeSet2) {
      paramAttributeSet1 = removeHTMLTags(paramAttributeSet1, paramAttributeSet2);
    }
    return super.removeAttributes(paramAttributeSet1, convertAttributeSet(paramAttributeSet2));
  }
  
  protected StyleContext.SmallAttributeSet createSmallAttributeSet(AttributeSet paramAttributeSet)
  {
    return new SmallConversionSet(paramAttributeSet);
  }
  
  protected MutableAttributeSet createLargeAttributeSet(AttributeSet paramAttributeSet)
  {
    return new LargeConversionSet(paramAttributeSet);
  }
  
  private AttributeSet removeHTMLTags(AttributeSet paramAttributeSet1, AttributeSet paramAttributeSet2)
  {
    if ((!(paramAttributeSet2 instanceof LargeConversionSet)) && (!(paramAttributeSet2 instanceof SmallConversionSet)))
    {
      Enumeration localEnumeration = paramAttributeSet2.getAttributeNames();
      while (localEnumeration.hasMoreElements())
      {
        Object localObject = localEnumeration.nextElement();
        if ((localObject instanceof StyleConstants))
        {
          HTML.Tag localTag = HTML.getTagForStyleConstantsKey((StyleConstants)localObject);
          if ((localTag != null) && (paramAttributeSet1.isDefined(localTag))) {
            paramAttributeSet1 = super.removeAttribute(paramAttributeSet1, localTag);
          }
        }
      }
    }
    return paramAttributeSet1;
  }
  
  AttributeSet convertAttributeSet(AttributeSet paramAttributeSet)
  {
    if (((paramAttributeSet instanceof LargeConversionSet)) || ((paramAttributeSet instanceof SmallConversionSet))) {
      return paramAttributeSet;
    }
    Enumeration localEnumeration1 = paramAttributeSet.getAttributeNames();
    while (localEnumeration1.hasMoreElements())
    {
      Object localObject1 = localEnumeration1.nextElement();
      if ((localObject1 instanceof StyleConstants))
      {
        LargeConversionSet localLargeConversionSet = new LargeConversionSet();
        Enumeration localEnumeration2 = paramAttributeSet.getAttributeNames();
        while (localEnumeration2.hasMoreElements())
        {
          Object localObject2 = localEnumeration2.nextElement();
          Object localObject3 = null;
          if ((localObject2 instanceof StyleConstants))
          {
            CSS.Attribute localAttribute = css.styleConstantsKeyToCSSKey((StyleConstants)localObject2);
            if (localAttribute != null)
            {
              Object localObject4 = paramAttributeSet.getAttribute(localObject2);
              localObject3 = css.styleConstantsValueToCSSValue((StyleConstants)localObject2, localObject4);
              if (localObject3 != null) {
                localLargeConversionSet.addAttribute(localAttribute, localObject3);
              }
            }
          }
          if (localObject3 == null) {
            localLargeConversionSet.addAttribute(localObject2, paramAttributeSet.getAttribute(localObject2));
          }
        }
        return localLargeConversionSet;
      }
    }
    return paramAttributeSet;
  }
  
  public Font getFont(AttributeSet paramAttributeSet)
  {
    return css.getFont(this, paramAttributeSet, 12, this);
  }
  
  public Color getForeground(AttributeSet paramAttributeSet)
  {
    Color localColor = css.getColor(paramAttributeSet, CSS.Attribute.COLOR);
    if (localColor == null) {
      return Color.black;
    }
    return localColor;
  }
  
  public Color getBackground(AttributeSet paramAttributeSet)
  {
    return css.getColor(paramAttributeSet, CSS.Attribute.BACKGROUND_COLOR);
  }
  
  public BoxPainter getBoxPainter(AttributeSet paramAttributeSet)
  {
    return new BoxPainter(paramAttributeSet, css, this);
  }
  
  public ListPainter getListPainter(AttributeSet paramAttributeSet)
  {
    return new ListPainter(paramAttributeSet, this);
  }
  
  public void setBaseFontSize(int paramInt)
  {
    css.setBaseFontSize(paramInt);
  }
  
  public void setBaseFontSize(String paramString)
  {
    css.setBaseFontSize(paramString);
  }
  
  public static int getIndexOfSize(float paramFloat)
  {
    return CSS.getIndexOfSize(paramFloat, sizeMapDefault);
  }
  
  public float getPointSize(int paramInt)
  {
    return css.getPointSize(paramInt, this);
  }
  
  public float getPointSize(String paramString)
  {
    return css.getPointSize(paramString, this);
  }
  
  public Color stringToColor(String paramString)
  {
    return CSS.stringToColor(paramString);
  }
  
  ImageIcon getBackgroundImage(AttributeSet paramAttributeSet)
  {
    Object localObject = paramAttributeSet.getAttribute(CSS.Attribute.BACKGROUND_IMAGE);
    if (localObject != null) {
      return ((CSS.BackgroundImage)localObject).getImage(getBase());
    }
    return null;
  }
  
  void addRule(String[] paramArrayOfString, AttributeSet paramAttributeSet, boolean paramBoolean)
  {
    int i = paramArrayOfString.length;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramArrayOfString[0]);
    for (int j = 1; j < i; j++)
    {
      localStringBuilder.append(' ');
      localStringBuilder.append(paramArrayOfString[j]);
    }
    String str = localStringBuilder.toString();
    Object localObject1 = getStyle(str);
    if (localObject1 == null)
    {
      Style localStyle = addStyle(str, null);
      synchronized (this)
      {
        SelectorMapping localSelectorMapping = getRootSelectorMapping();
        for (int k = i - 1; k >= 0; k--) {
          localSelectorMapping = localSelectorMapping.getChildSelectorMapping(paramArrayOfString[k], true);
        }
        localObject1 = localSelectorMapping.getStyle();
        if (localObject1 == null)
        {
          localObject1 = localStyle;
          localSelectorMapping.setStyle((Style)localObject1);
          refreshResolvedRules(str, paramArrayOfString, (Style)localObject1, localSelectorMapping.getSpecificity());
        }
      }
    }
    if (paramBoolean) {
      localObject1 = getLinkedStyle((Style)localObject1);
    }
    ((Style)localObject1).addAttributes(paramAttributeSet);
  }
  
  private synchronized void linkStyleSheetAt(StyleSheet paramStyleSheet, int paramInt)
  {
    if (resolvedStyles.size() > 0)
    {
      Enumeration localEnumeration = resolvedStyles.elements();
      while (localEnumeration.hasMoreElements())
      {
        ResolvedStyle localResolvedStyle = (ResolvedStyle)localEnumeration.nextElement();
        localResolvedStyle.insertExtendedStyleAt(paramStyleSheet.getRule(localResolvedStyle.getName()), paramInt);
      }
    }
  }
  
  private synchronized void unlinkStyleSheet(StyleSheet paramStyleSheet, int paramInt)
  {
    if (resolvedStyles.size() > 0)
    {
      Enumeration localEnumeration = resolvedStyles.elements();
      while (localEnumeration.hasMoreElements())
      {
        ResolvedStyle localResolvedStyle = (ResolvedStyle)localEnumeration.nextElement();
        localResolvedStyle.removeExtendedStyleAt(paramInt);
      }
    }
  }
  
  String[] getSimpleSelectors(String paramString)
  {
    paramString = cleanSelectorString(paramString);
    SearchBuffer localSearchBuffer = SearchBuffer.obtainSearchBuffer();
    Vector localVector = localSearchBuffer.getVector();
    int i = 0;
    int j = paramString.length();
    while (i != -1)
    {
      int k = paramString.indexOf(' ', i);
      if (k != -1)
      {
        localVector.addElement(paramString.substring(i, k));
        k++;
        if (k == j) {
          i = -1;
        } else {
          i = k;
        }
      }
      else
      {
        localVector.addElement(paramString.substring(i));
        i = -1;
      }
    }
    String[] arrayOfString = new String[localVector.size()];
    localVector.copyInto(arrayOfString);
    SearchBuffer.releaseSearchBuffer(localSearchBuffer);
    return arrayOfString;
  }
  
  String cleanSelectorString(String paramString)
  {
    int i = 1;
    int j = 0;
    int k = paramString.length();
    while (j < k)
    {
      switch (paramString.charAt(j))
      {
      case ' ': 
        if (i != 0) {
          return _cleanSelectorString(paramString);
        }
        i = 1;
        break;
      case '\t': 
      case '\n': 
      case '\r': 
        return _cleanSelectorString(paramString);
      default: 
        i = 0;
      }
      j++;
    }
    if (i != 0) {
      return _cleanSelectorString(paramString);
    }
    return paramString;
  }
  
  private String _cleanSelectorString(String paramString)
  {
    SearchBuffer localSearchBuffer = SearchBuffer.obtainSearchBuffer();
    StringBuffer localStringBuffer = localSearchBuffer.getStringBuffer();
    int i = 1;
    int j = 0;
    char[] arrayOfChar = paramString.toCharArray();
    int k = arrayOfChar.length;
    String str = null;
    try
    {
      for (int m = 0; m < k; m++) {
        switch (arrayOfChar[m])
        {
        case ' ': 
          if (i == 0)
          {
            i = 1;
            if (j < m) {
              localStringBuffer.append(arrayOfChar, j, 1 + m - j);
            }
          }
          j = m + 1;
          break;
        case '\t': 
        case '\n': 
        case '\r': 
          if (i == 0)
          {
            i = 1;
            if (j < m)
            {
              localStringBuffer.append(arrayOfChar, j, m - j);
              localStringBuffer.append(' ');
            }
          }
          j = m + 1;
          break;
        default: 
          i = 0;
        }
      }
      if ((i != 0) && (localStringBuffer.length() > 0)) {
        localStringBuffer.setLength(localStringBuffer.length() - 1);
      } else if (j < k) {
        localStringBuffer.append(arrayOfChar, j, k - j);
      }
      str = localStringBuffer.toString();
    }
    finally
    {
      SearchBuffer.releaseSearchBuffer(localSearchBuffer);
    }
    return str;
  }
  
  private SelectorMapping getRootSelectorMapping()
  {
    return selectorMapping;
  }
  
  static int getSpecificity(String paramString)
  {
    int i = 0;
    int j = 1;
    int k = 0;
    int m = paramString.length();
    while (k < m)
    {
      switch (paramString.charAt(k))
      {
      case '.': 
        i += 100;
        break;
      case '#': 
        i += 10000;
        break;
      case ' ': 
        j = 1;
        break;
      default: 
        if (j != 0)
        {
          j = 0;
          i++;
        }
        break;
      }
      k++;
    }
    return i;
  }
  
  private Style getLinkedStyle(Style paramStyle)
  {
    Style localStyle = (Style)paramStyle.getResolveParent();
    if (localStyle == null)
    {
      localStyle = addStyle(null, null);
      paramStyle.setResolveParent(localStyle);
    }
    return localStyle;
  }
  
  private synchronized Style getResolvedStyle(String paramString, Vector paramVector, HTML.Tag paramTag)
  {
    Style localStyle = (Style)resolvedStyles.get(paramString);
    if (localStyle == null) {
      localStyle = createResolvedStyle(paramString, paramVector, paramTag);
    }
    return localStyle;
  }
  
  private synchronized Style getResolvedStyle(String paramString)
  {
    Style localStyle = (Style)resolvedStyles.get(paramString);
    if (localStyle == null) {
      localStyle = createResolvedStyle(paramString);
    }
    return localStyle;
  }
  
  private void addSortedStyle(SelectorMapping paramSelectorMapping, Vector<SelectorMapping> paramVector)
  {
    int i = paramVector.size();
    if (i > 0)
    {
      int j = paramSelectorMapping.getSpecificity();
      for (int k = 0; k < i; k++) {
        if (j >= ((SelectorMapping)paramVector.elementAt(k)).getSpecificity())
        {
          paramVector.insertElementAt(paramSelectorMapping, k);
          return;
        }
      }
    }
    paramVector.addElement(paramSelectorMapping);
  }
  
  private synchronized void getStyles(SelectorMapping paramSelectorMapping, Vector<SelectorMapping> paramVector, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3, int paramInt1, int paramInt2, Hashtable<SelectorMapping, SelectorMapping> paramHashtable)
  {
    if (paramHashtable.contains(paramSelectorMapping)) {
      return;
    }
    paramHashtable.put(paramSelectorMapping, paramSelectorMapping);
    Style localStyle = paramSelectorMapping.getStyle();
    if (localStyle != null) {
      addSortedStyle(paramSelectorMapping, paramVector);
    }
    for (int i = paramInt1; i < paramInt2; i++)
    {
      String str1 = paramArrayOfString1[i];
      if (str1 != null)
      {
        SelectorMapping localSelectorMapping = paramSelectorMapping.getChildSelectorMapping(str1, false);
        if (localSelectorMapping != null) {
          getStyles(localSelectorMapping, paramVector, paramArrayOfString1, paramArrayOfString2, paramArrayOfString3, i + 1, paramInt2, paramHashtable);
        }
        String str2;
        if (paramArrayOfString3[i] != null)
        {
          str2 = paramArrayOfString3[i];
          localSelectorMapping = paramSelectorMapping.getChildSelectorMapping(str1 + "." + str2, false);
          if (localSelectorMapping != null) {
            getStyles(localSelectorMapping, paramVector, paramArrayOfString1, paramArrayOfString2, paramArrayOfString3, i + 1, paramInt2, paramHashtable);
          }
          localSelectorMapping = paramSelectorMapping.getChildSelectorMapping("." + str2, false);
          if (localSelectorMapping != null) {
            getStyles(localSelectorMapping, paramVector, paramArrayOfString1, paramArrayOfString2, paramArrayOfString3, i + 1, paramInt2, paramHashtable);
          }
        }
        if (paramArrayOfString2[i] != null)
        {
          str2 = paramArrayOfString2[i];
          localSelectorMapping = paramSelectorMapping.getChildSelectorMapping(str1 + "#" + str2, false);
          if (localSelectorMapping != null) {
            getStyles(localSelectorMapping, paramVector, paramArrayOfString1, paramArrayOfString2, paramArrayOfString3, i + 1, paramInt2, paramHashtable);
          }
          localSelectorMapping = paramSelectorMapping.getChildSelectorMapping("#" + str2, false);
          if (localSelectorMapping != null) {
            getStyles(localSelectorMapping, paramVector, paramArrayOfString1, paramArrayOfString2, paramArrayOfString3, i + 1, paramInt2, paramHashtable);
          }
        }
      }
    }
  }
  
  private synchronized Style createResolvedStyle(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3)
  {
    SearchBuffer localSearchBuffer = SearchBuffer.obtainSearchBuffer();
    Vector localVector = localSearchBuffer.getVector();
    Hashtable localHashtable = localSearchBuffer.getHashtable();
    try
    {
      SelectorMapping localSelectorMapping1 = getRootSelectorMapping();
      int i = paramArrayOfString1.length;
      String str1 = paramArrayOfString1[0];
      SelectorMapping localSelectorMapping2 = localSelectorMapping1.getChildSelectorMapping(str1, false);
      if (localSelectorMapping2 != null) {
        getStyles(localSelectorMapping2, localVector, paramArrayOfString1, paramArrayOfString2, paramArrayOfString3, 1, i, localHashtable);
      }
      String str2;
      if (paramArrayOfString3[0] != null)
      {
        str2 = paramArrayOfString3[0];
        localSelectorMapping2 = localSelectorMapping1.getChildSelectorMapping(str1 + "." + str2, false);
        if (localSelectorMapping2 != null) {
          getStyles(localSelectorMapping2, localVector, paramArrayOfString1, paramArrayOfString2, paramArrayOfString3, 1, i, localHashtable);
        }
        localSelectorMapping2 = localSelectorMapping1.getChildSelectorMapping("." + str2, false);
        if (localSelectorMapping2 != null) {
          getStyles(localSelectorMapping2, localVector, paramArrayOfString1, paramArrayOfString2, paramArrayOfString3, 1, i, localHashtable);
        }
      }
      if (paramArrayOfString2[0] != null)
      {
        str2 = paramArrayOfString2[0];
        localSelectorMapping2 = localSelectorMapping1.getChildSelectorMapping(str1 + "#" + str2, false);
        if (localSelectorMapping2 != null) {
          getStyles(localSelectorMapping2, localVector, paramArrayOfString1, paramArrayOfString2, paramArrayOfString3, 1, i, localHashtable);
        }
        localSelectorMapping2 = localSelectorMapping1.getChildSelectorMapping("#" + str2, false);
        if (localSelectorMapping2 != null) {
          getStyles(localSelectorMapping2, localVector, paramArrayOfString1, paramArrayOfString2, paramArrayOfString3, 1, i, localHashtable);
        }
      }
      int j = linkedStyleSheets != null ? linkedStyleSheets.size() : 0;
      int k = localVector.size();
      AttributeSet[] arrayOfAttributeSet = new AttributeSet[k + j];
      for (int m = 0; m < k; m++) {
        arrayOfAttributeSet[m] = ((SelectorMapping)localVector.elementAt(m)).getStyle();
      }
      for (m = 0; m < j; m++)
      {
        localObject1 = ((StyleSheet)linkedStyleSheets.elementAt(m)).getRule(paramString);
        if (localObject1 == null) {
          arrayOfAttributeSet[(m + k)] = SimpleAttributeSet.EMPTY;
        } else {
          arrayOfAttributeSet[(m + k)] = localObject1;
        }
      }
      ResolvedStyle localResolvedStyle = new ResolvedStyle(paramString, arrayOfAttributeSet, k);
      resolvedStyles.put(paramString, localResolvedStyle);
      Object localObject1 = localResolvedStyle;
      return (Style)localObject1;
    }
    finally
    {
      SearchBuffer.releaseSearchBuffer(localSearchBuffer);
    }
  }
  
  private Style createResolvedStyle(String paramString, Vector paramVector, HTML.Tag paramTag)
  {
    int i = paramVector.size();
    String[] arrayOfString1 = new String[i];
    String[] arrayOfString2 = new String[i];
    String[] arrayOfString3 = new String[i];
    for (int j = 0; j < i; j++)
    {
      Element localElement = (Element)paramVector.elementAt(j);
      AttributeSet localAttributeSet = localElement.getAttributes();
      Object localObject;
      if ((j == 0) && (localElement.isLeaf()))
      {
        localObject = localAttributeSet.getAttribute(paramTag);
        if ((localObject instanceof AttributeSet)) {
          localAttributeSet = (AttributeSet)localObject;
        } else {
          localAttributeSet = null;
        }
      }
      if (localAttributeSet != null)
      {
        localObject = (HTML.Tag)localAttributeSet.getAttribute(StyleConstants.NameAttribute);
        if (localObject != null) {
          arrayOfString1[j] = ((HTML.Tag)localObject).toString();
        } else {
          arrayOfString1[j] = null;
        }
        if (localAttributeSet.isDefined(HTML.Attribute.CLASS)) {
          arrayOfString3[j] = localAttributeSet.getAttribute(HTML.Attribute.CLASS).toString();
        } else {
          arrayOfString3[j] = null;
        }
        if (localAttributeSet.isDefined(HTML.Attribute.ID)) {
          arrayOfString2[j] = localAttributeSet.getAttribute(HTML.Attribute.ID).toString();
        } else {
          arrayOfString2[j] = null;
        }
      }
      else
      {
        arrayOfString1[j] = (arrayOfString2[j] = arrayOfString3[j] = null);
      }
    }
    arrayOfString1[0] = paramTag.toString();
    return createResolvedStyle(paramString, arrayOfString1, arrayOfString2, arrayOfString3);
  }
  
  private Style createResolvedStyle(String paramString)
  {
    SearchBuffer localSearchBuffer = SearchBuffer.obtainSearchBuffer();
    Vector localVector = localSearchBuffer.getVector();
    try
    {
      int i = 0;
      int k = 0;
      int m = 0;
      int n = paramString.length();
      while (m < n)
      {
        if (i == m) {
          i = paramString.indexOf('.', m);
        }
        if (k == m) {
          k = paramString.indexOf('#', m);
        }
        int j = paramString.indexOf(' ', m);
        if (j == -1) {
          j = n;
        }
        if ((i != -1) && (k != -1) && (i < j) && (k < j))
        {
          if (k < i)
          {
            if (m == k) {
              localVector.addElement("");
            } else {
              localVector.addElement(paramString.substring(m, k));
            }
            if (i + 1 < j) {
              localVector.addElement(paramString.substring(i + 1, j));
            } else {
              localVector.addElement(null);
            }
            if (k + 1 == i) {
              localVector.addElement(null);
            } else {
              localVector.addElement(paramString.substring(k + 1, i));
            }
          }
          else if (k < j)
          {
            if (m == i) {
              localVector.addElement("");
            } else {
              localVector.addElement(paramString.substring(m, i));
            }
            if (i + 1 < k) {
              localVector.addElement(paramString.substring(i + 1, k));
            } else {
              localVector.addElement(null);
            }
            if (k + 1 == j) {
              localVector.addElement(null);
            } else {
              localVector.addElement(paramString.substring(k + 1, j));
            }
          }
          i = k = j + 1;
        }
        else if ((i != -1) && (i < j))
        {
          if (i == m) {
            localVector.addElement("");
          } else {
            localVector.addElement(paramString.substring(m, i));
          }
          if (i + 1 == j) {
            localVector.addElement(null);
          } else {
            localVector.addElement(paramString.substring(i + 1, j));
          }
          localVector.addElement(null);
          i = j + 1;
        }
        else if ((k != -1) && (k < j))
        {
          if (k == m) {
            localVector.addElement("");
          } else {
            localVector.addElement(paramString.substring(m, k));
          }
          localVector.addElement(null);
          if (k + 1 == j) {
            localVector.addElement(null);
          } else {
            localVector.addElement(paramString.substring(k + 1, j));
          }
          k = j + 1;
        }
        else
        {
          localVector.addElement(paramString.substring(m, j));
          localVector.addElement(null);
          localVector.addElement(null);
        }
        m = j + 1;
      }
      int i1 = localVector.size();
      int i2 = i1 / 3;
      String[] arrayOfString1 = new String[i2];
      String[] arrayOfString2 = new String[i2];
      String[] arrayOfString3 = new String[i2];
      int i3 = 0;
      for (int i4 = i1 - 3; i3 < i2; i4 -= 3)
      {
        arrayOfString1[i3] = ((String)localVector.elementAt(i4));
        arrayOfString3[i3] = ((String)localVector.elementAt(i4 + 1));
        arrayOfString2[i3] = ((String)localVector.elementAt(i4 + 2));
        i3++;
      }
      Style localStyle = createResolvedStyle(paramString, arrayOfString1, arrayOfString2, arrayOfString3);
      return localStyle;
    }
    finally
    {
      SearchBuffer.releaseSearchBuffer(localSearchBuffer);
    }
  }
  
  private synchronized void refreshResolvedRules(String paramString, String[] paramArrayOfString, Style paramStyle, int paramInt)
  {
    if (resolvedStyles.size() > 0)
    {
      Enumeration localEnumeration = resolvedStyles.elements();
      while (localEnumeration.hasMoreElements())
      {
        ResolvedStyle localResolvedStyle = (ResolvedStyle)localEnumeration.nextElement();
        if (localResolvedStyle.matches(paramString)) {
          localResolvedStyle.insertStyle(paramStyle, paramInt);
        }
      }
    }
  }
  
  void rebaseSizeMap(int paramInt)
  {
    sizeMap = new int[sizeMapDefault.length];
    for (int i = 0; i < sizeMapDefault.length; i++) {
      sizeMap[i] = Math.max(paramInt * sizeMapDefault[i] / sizeMapDefault[CSS.baseFontSizeIndex], 4);
    }
  }
  
  int[] getSizeMap()
  {
    return sizeMap;
  }
  
  boolean isW3CLengthUnits()
  {
    return w3cLengthUnits;
  }
  
  static class BackgroundImagePainter
    implements Serializable
  {
    ImageIcon backgroundImage;
    float hPosition;
    float vPosition;
    short flags;
    private int paintX;
    private int paintY;
    private int paintMaxX;
    private int paintMaxY;
    
    BackgroundImagePainter(AttributeSet paramAttributeSet, CSS paramCSS, StyleSheet paramStyleSheet)
    {
      backgroundImage = paramStyleSheet.getBackgroundImage(paramAttributeSet);
      CSS.BackgroundPosition localBackgroundPosition = (CSS.BackgroundPosition)paramAttributeSet.getAttribute(CSS.Attribute.BACKGROUND_POSITION);
      if (localBackgroundPosition != null)
      {
        hPosition = localBackgroundPosition.getHorizontalPosition();
        vPosition = localBackgroundPosition.getVerticalPosition();
        if (localBackgroundPosition.isHorizontalPositionRelativeToSize()) {
          flags = ((short)(flags | 0x4));
        } else if (localBackgroundPosition.isHorizontalPositionRelativeToSize()) {
          hPosition *= CSS.getFontSize(paramAttributeSet, 12, paramStyleSheet);
        }
        if (localBackgroundPosition.isVerticalPositionRelativeToSize()) {
          flags = ((short)(flags | 0x8));
        } else if (localBackgroundPosition.isVerticalPositionRelativeToFontSize()) {
          vPosition *= CSS.getFontSize(paramAttributeSet, 12, paramStyleSheet);
        }
      }
      CSS.Value localValue = (CSS.Value)paramAttributeSet.getAttribute(CSS.Attribute.BACKGROUND_REPEAT);
      if ((localValue == null) || (localValue == CSS.Value.BACKGROUND_REPEAT)) {
        flags = ((short)(flags | 0x3));
      } else if (localValue == CSS.Value.BACKGROUND_REPEAT_X) {
        flags = ((short)(flags | 0x1));
      } else if (localValue == CSS.Value.BACKGROUND_REPEAT_Y) {
        flags = ((short)(flags | 0x2));
      }
    }
    
    void paint(Graphics paramGraphics, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, View paramView)
    {
      Rectangle localRectangle = paramGraphics.getClipRect();
      if (localRectangle != null) {
        paramGraphics.clipRect((int)paramFloat1, (int)paramFloat2, (int)paramFloat3, (int)paramFloat4);
      }
      int i;
      int j;
      if ((flags & 0x3) == 0)
      {
        i = backgroundImage.getIconWidth();
        j = backgroundImage.getIconWidth();
        if ((flags & 0x4) == 4) {
          paintX = ((int)(paramFloat1 + paramFloat3 * hPosition - i * hPosition));
        } else {
          paintX = ((int)paramFloat1 + (int)hPosition);
        }
        if ((flags & 0x8) == 8) {
          paintY = ((int)(paramFloat2 + paramFloat4 * vPosition - j * vPosition));
        } else {
          paintY = ((int)paramFloat2 + (int)vPosition);
        }
        if ((localRectangle == null) || ((paintX + i > x) && (paintY + j > y) && (paintX < x + width) && (paintY < y + height))) {
          backgroundImage.paintIcon(null, paramGraphics, paintX, paintY);
        }
      }
      else
      {
        i = backgroundImage.getIconWidth();
        j = backgroundImage.getIconHeight();
        if ((i > 0) && (j > 0))
        {
          paintX = ((int)paramFloat1);
          paintY = ((int)paramFloat2);
          paintMaxX = ((int)(paramFloat1 + paramFloat3));
          paintMaxY = ((int)(paramFloat2 + paramFloat4));
          if (updatePaintCoordinates(localRectangle, i, j)) {
            while (paintX < paintMaxX)
            {
              int k = paintY;
              while (k < paintMaxY)
              {
                backgroundImage.paintIcon(null, paramGraphics, paintX, k);
                k += j;
              }
              paintX += i;
            }
          }
        }
      }
      if (localRectangle != null) {
        paramGraphics.setClip(x, y, width, height);
      }
    }
    
    private boolean updatePaintCoordinates(Rectangle paramRectangle, int paramInt1, int paramInt2)
    {
      if ((flags & 0x3) == 1) {
        paintMaxY = (paintY + 1);
      } else if ((flags & 0x3) == 2) {
        paintMaxX = (paintX + 1);
      }
      if (paramRectangle != null)
      {
        if (((flags & 0x3) == 1) && ((paintY + paramInt2 <= y) || (paintY > y + height))) {
          return false;
        }
        if (((flags & 0x3) == 2) && ((paintX + paramInt1 <= x) || (paintX > x + width))) {
          return false;
        }
        if ((flags & 0x1) == 1)
        {
          if (x + width < paintMaxX) {
            if ((x + width - paintX) % paramInt1 == 0) {
              paintMaxX = (x + width);
            } else {
              paintMaxX = (((x + width - paintX) / paramInt1 + 1) * paramInt1 + paintX);
            }
          }
          if (x > paintX) {
            paintX = ((x - paintX) / paramInt1 * paramInt1 + paintX);
          }
        }
        if ((flags & 0x2) == 2)
        {
          if (y + height < paintMaxY) {
            if ((y + height - paintY) % paramInt2 == 0) {
              paintMaxY = (y + height);
            } else {
              paintMaxY = (((y + height - paintY) / paramInt2 + 1) * paramInt2 + paintY);
            }
          }
          if (y > paintY) {
            paintY = ((y - paintY) / paramInt2 * paramInt2 + paintY);
          }
        }
      }
      return true;
    }
  }
  
  public static class BoxPainter
    implements Serializable
  {
    float topMargin;
    float bottomMargin;
    float leftMargin;
    float rightMargin;
    short marginFlags;
    Border border;
    Insets binsets;
    CSS css;
    StyleSheet ss;
    Color bg;
    StyleSheet.BackgroundImagePainter bgPainter;
    
    BoxPainter(AttributeSet paramAttributeSet, CSS paramCSS, StyleSheet paramStyleSheet)
    {
      ss = paramStyleSheet;
      css = paramCSS;
      border = getBorder(paramAttributeSet);
      binsets = border.getBorderInsets(null);
      topMargin = getLength(CSS.Attribute.MARGIN_TOP, paramAttributeSet);
      bottomMargin = getLength(CSS.Attribute.MARGIN_BOTTOM, paramAttributeSet);
      leftMargin = getLength(CSS.Attribute.MARGIN_LEFT, paramAttributeSet);
      rightMargin = getLength(CSS.Attribute.MARGIN_RIGHT, paramAttributeSet);
      bg = paramStyleSheet.getBackground(paramAttributeSet);
      if (paramStyleSheet.getBackgroundImage(paramAttributeSet) != null) {
        bgPainter = new StyleSheet.BackgroundImagePainter(paramAttributeSet, paramCSS, paramStyleSheet);
      }
    }
    
    Border getBorder(AttributeSet paramAttributeSet)
    {
      return new CSSBorder(paramAttributeSet);
    }
    
    Color getBorderColor(AttributeSet paramAttributeSet)
    {
      Color localColor = css.getColor(paramAttributeSet, CSS.Attribute.BORDER_COLOR);
      if (localColor == null)
      {
        localColor = css.getColor(paramAttributeSet, CSS.Attribute.COLOR);
        if (localColor == null) {
          return Color.black;
        }
      }
      return localColor;
    }
    
    public float getInset(int paramInt, View paramView)
    {
      AttributeSet localAttributeSet = paramView.getAttributes();
      float f = 0.0F;
      switch (paramInt)
      {
      case 2: 
        f += getOrientationMargin(HorizontalMargin.LEFT, leftMargin, localAttributeSet, isLeftToRight(paramView));
        f += binsets.left;
        f += getLength(CSS.Attribute.PADDING_LEFT, localAttributeSet);
        break;
      case 4: 
        f += getOrientationMargin(HorizontalMargin.RIGHT, rightMargin, localAttributeSet, isLeftToRight(paramView));
        f += binsets.right;
        f += getLength(CSS.Attribute.PADDING_RIGHT, localAttributeSet);
        break;
      case 1: 
        f += topMargin;
        f += binsets.top;
        f += getLength(CSS.Attribute.PADDING_TOP, localAttributeSet);
        break;
      case 3: 
        f += bottomMargin;
        f += binsets.bottom;
        f += getLength(CSS.Attribute.PADDING_BOTTOM, localAttributeSet);
        break;
      default: 
        throw new IllegalArgumentException("Invalid side: " + paramInt);
      }
      return f;
    }
    
    public void paint(Graphics paramGraphics, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, View paramView)
    {
      float f1 = 0.0F;
      float f2 = 0.0F;
      float f3 = 0.0F;
      float f4 = 0.0F;
      AttributeSet localAttributeSet = paramView.getAttributes();
      boolean bool = isLeftToRight(paramView);
      float f5 = getOrientationMargin(HorizontalMargin.LEFT, leftMargin, localAttributeSet, bool);
      float f6 = getOrientationMargin(HorizontalMargin.RIGHT, rightMargin, localAttributeSet, bool);
      if (!(paramView instanceof HTMLEditorKit.HTMLFactory.BodyBlockView))
      {
        f1 = f5;
        f2 = topMargin;
        f3 = -(f5 + f6);
        f4 = -(topMargin + bottomMargin);
      }
      if (bg != null)
      {
        paramGraphics.setColor(bg);
        paramGraphics.fillRect((int)(paramFloat1 + f1), (int)(paramFloat2 + f2), (int)(paramFloat3 + f3), (int)(paramFloat4 + f4));
      }
      if (bgPainter != null) {
        bgPainter.paint(paramGraphics, paramFloat1 + f1, paramFloat2 + f2, paramFloat3 + f3, paramFloat4 + f4, paramView);
      }
      paramFloat1 += f5;
      paramFloat2 += topMargin;
      paramFloat3 -= f5 + f6;
      paramFloat4 -= topMargin + bottomMargin;
      if ((border instanceof BevelBorder))
      {
        int i = (int)getLength(CSS.Attribute.BORDER_TOP_WIDTH, localAttributeSet);
        for (int j = i - 1; j >= 0; j--) {
          border.paintBorder(null, paramGraphics, (int)paramFloat1 + j, (int)paramFloat2 + j, (int)paramFloat3 - 2 * j, (int)paramFloat4 - 2 * j);
        }
      }
      else
      {
        border.paintBorder(null, paramGraphics, (int)paramFloat1, (int)paramFloat2, (int)paramFloat3, (int)paramFloat4);
      }
    }
    
    float getLength(CSS.Attribute paramAttribute, AttributeSet paramAttributeSet)
    {
      return css.getLength(paramAttributeSet, paramAttribute, ss);
    }
    
    static boolean isLeftToRight(View paramView)
    {
      boolean bool = true;
      Container localContainer;
      if ((isOrientationAware(paramView)) && (paramView != null) && ((localContainer = paramView.getContainer()) != null)) {
        bool = localContainer.getComponentOrientation().isLeftToRight();
      }
      return bool;
    }
    
    static boolean isOrientationAware(View paramView)
    {
      boolean bool = false;
      AttributeSet localAttributeSet;
      Object localObject;
      if ((paramView != null) && ((localAttributeSet = paramView.getElement().getAttributes()) != null) && (((localObject = localAttributeSet.getAttribute(StyleConstants.NameAttribute)) instanceof HTML.Tag)) && ((localObject == HTML.Tag.DIR) || (localObject == HTML.Tag.MENU) || (localObject == HTML.Tag.UL) || (localObject == HTML.Tag.OL))) {
        bool = true;
      }
      return bool;
    }
    
    float getOrientationMargin(HorizontalMargin paramHorizontalMargin, float paramFloat, AttributeSet paramAttributeSet, boolean paramBoolean)
    {
      float f1 = paramFloat;
      float f2 = paramFloat;
      Object localObject = null;
      switch (StyleSheet.1.$SwitchMap$javax$swing$text$html$StyleSheet$BoxPainter$HorizontalMargin[paramHorizontalMargin.ordinal()])
      {
      case 1: 
        f2 = paramBoolean ? getLength(CSS.Attribute.MARGIN_RIGHT_LTR, paramAttributeSet) : getLength(CSS.Attribute.MARGIN_RIGHT_RTL, paramAttributeSet);
        localObject = paramAttributeSet.getAttribute(CSS.Attribute.MARGIN_RIGHT);
        break;
      case 2: 
        f2 = paramBoolean ? getLength(CSS.Attribute.MARGIN_LEFT_LTR, paramAttributeSet) : getLength(CSS.Attribute.MARGIN_LEFT_RTL, paramAttributeSet);
        localObject = paramAttributeSet.getAttribute(CSS.Attribute.MARGIN_LEFT);
      }
      if ((localObject == null) && (f2 != -2.14748365E9F)) {
        f1 = f2;
      }
      return f1;
    }
    
    static enum HorizontalMargin
    {
      LEFT,  RIGHT;
      
      private HorizontalMargin() {}
    }
  }
  
  class CssParser
    implements CSSParser.CSSParserCallback
  {
    Vector<String[]> selectors = new Vector();
    Vector<String> selectorTokens = new Vector();
    String propertyName;
    MutableAttributeSet declaration = new SimpleAttributeSet();
    boolean parsingDeclaration;
    boolean isLink;
    URL base;
    CSSParser parser = new CSSParser();
    
    CssParser() {}
    
    public AttributeSet parseDeclaration(String paramString)
    {
      try
      {
        return parseDeclaration(new StringReader(paramString));
      }
      catch (IOException localIOException) {}
      return null;
    }
    
    public AttributeSet parseDeclaration(Reader paramReader)
      throws IOException
    {
      parse(base, paramReader, true, false);
      return declaration.copyAttributes();
    }
    
    public void parse(URL paramURL, Reader paramReader, boolean paramBoolean1, boolean paramBoolean2)
      throws IOException
    {
      base = paramURL;
      isLink = paramBoolean2;
      parsingDeclaration = paramBoolean1;
      declaration.removeAttributes(declaration);
      selectorTokens.removeAllElements();
      selectors.removeAllElements();
      propertyName = null;
      parser.parse(paramReader, this, paramBoolean1);
    }
    
    public void handleImport(String paramString)
    {
      URL localURL = CSS.getURL(base, paramString);
      if (localURL != null) {
        importStyleSheet(localURL);
      }
    }
    
    public void handleSelector(String paramString)
    {
      if ((!paramString.startsWith(".")) && (!paramString.startsWith("#"))) {
        paramString = paramString.toLowerCase();
      }
      int i = paramString.length();
      if (paramString.endsWith(","))
      {
        if (i > 1)
        {
          paramString = paramString.substring(0, i - 1);
          selectorTokens.addElement(paramString);
        }
        addSelector();
      }
      else if (i > 0)
      {
        selectorTokens.addElement(paramString);
      }
    }
    
    public void startRule()
    {
      if (selectorTokens.size() > 0) {
        addSelector();
      }
      propertyName = null;
    }
    
    public void handleProperty(String paramString)
    {
      propertyName = paramString;
    }
    
    public void handleValue(String paramString)
    {
      if ((propertyName != null) && (paramString != null) && (paramString.length() > 0))
      {
        CSS.Attribute localAttribute = CSS.getAttribute(propertyName);
        if (localAttribute != null)
        {
          if ((localAttribute == CSS.Attribute.LIST_STYLE_IMAGE) && (paramString != null) && (!paramString.equals("none")))
          {
            URL localURL = CSS.getURL(base, paramString);
            if (localURL != null) {
              paramString = localURL.toString();
            }
          }
          addCSSAttribute(declaration, localAttribute, paramString);
        }
        propertyName = null;
      }
    }
    
    public void endRule()
    {
      int i = selectors.size();
      for (int j = 0; j < i; j++)
      {
        String[] arrayOfString = (String[])selectors.elementAt(j);
        if (arrayOfString.length > 0) {
          addRule(arrayOfString, declaration, isLink);
        }
      }
      declaration.removeAttributes(declaration);
      selectors.removeAllElements();
    }
    
    private void addSelector()
    {
      String[] arrayOfString = new String[selectorTokens.size()];
      selectorTokens.copyInto(arrayOfString);
      selectors.addElement(arrayOfString);
      selectorTokens.removeAllElements();
    }
  }
  
  class LargeConversionSet
    extends SimpleAttributeSet
  {
    public LargeConversionSet(AttributeSet paramAttributeSet)
    {
      super();
    }
    
    public LargeConversionSet() {}
    
    public boolean isDefined(Object paramObject)
    {
      if ((paramObject instanceof StyleConstants))
      {
        CSS.Attribute localAttribute = css.styleConstantsKeyToCSSKey((StyleConstants)paramObject);
        if (localAttribute != null) {
          return super.isDefined(localAttribute);
        }
      }
      return super.isDefined(paramObject);
    }
    
    public Object getAttribute(Object paramObject)
    {
      if ((paramObject instanceof StyleConstants))
      {
        CSS.Attribute localAttribute = css.styleConstantsKeyToCSSKey((StyleConstants)paramObject);
        if (localAttribute != null)
        {
          Object localObject = super.getAttribute(localAttribute);
          if (localObject != null) {
            return css.cssValueToStyleConstantsValue((StyleConstants)paramObject, localObject);
          }
        }
      }
      return super.getAttribute(paramObject);
    }
  }
  
  public static class ListPainter
    implements Serializable
  {
    static final char[][] romanChars = { { 'i', 'v' }, { 'x', 'l' }, { 'c', 'd' }, { 'm', '?' } };
    private Rectangle paintRect;
    private boolean checkedForStart;
    private int start;
    private CSS.Value type;
    URL imageurl;
    private StyleSheet ss = null;
    Icon img = null;
    private int bulletgap = 5;
    private boolean isLeftToRight;
    
    ListPainter(AttributeSet paramAttributeSet, StyleSheet paramStyleSheet)
    {
      ss = paramStyleSheet;
      String str1 = (String)paramAttributeSet.getAttribute(CSS.Attribute.LIST_STYLE_IMAGE);
      type = null;
      if ((str1 != null) && (!str1.equals("none")))
      {
        String str2 = null;
        try
        {
          StringTokenizer localStringTokenizer = new StringTokenizer(str1, "()");
          if (localStringTokenizer.hasMoreTokens()) {
            str2 = localStringTokenizer.nextToken();
          }
          if (localStringTokenizer.hasMoreTokens()) {
            str2 = localStringTokenizer.nextToken();
          }
          localURL = new URL(str2);
          img = new ImageIcon(localURL);
        }
        catch (MalformedURLException localMalformedURLException1)
        {
          URL localURL;
          if ((str2 != null) && (paramStyleSheet != null) && (paramStyleSheet.getBase() != null)) {
            try
            {
              localURL = new URL(paramStyleSheet.getBase(), str2);
              img = new ImageIcon(localURL);
            }
            catch (MalformedURLException localMalformedURLException2)
            {
              img = null;
            }
          } else {
            img = null;
          }
        }
      }
      if (img == null) {
        type = ((CSS.Value)paramAttributeSet.getAttribute(CSS.Attribute.LIST_STYLE_TYPE));
      }
      start = 1;
      paintRect = new Rectangle();
    }
    
    private CSS.Value getChildType(View paramView)
    {
      CSS.Value localValue = (CSS.Value)paramView.getAttributes().getAttribute(CSS.Attribute.LIST_STYLE_TYPE);
      if (localValue == null) {
        if (type == null)
        {
          View localView = paramView.getParent();
          HTMLDocument localHTMLDocument = (HTMLDocument)localView.getDocument();
          if (HTMLDocument.matchNameAttribute(localView.getElement().getAttributes(), HTML.Tag.OL)) {
            localValue = CSS.Value.DECIMAL;
          } else {
            localValue = CSS.Value.DISC;
          }
        }
        else
        {
          localValue = type;
        }
      }
      return localValue;
    }
    
    private void getStart(View paramView)
    {
      checkedForStart = true;
      Element localElement = paramView.getElement();
      if (localElement != null)
      {
        AttributeSet localAttributeSet = localElement.getAttributes();
        Object localObject;
        if ((localAttributeSet != null) && (localAttributeSet.isDefined(HTML.Attribute.START)) && ((localObject = localAttributeSet.getAttribute(HTML.Attribute.START)) != null) && ((localObject instanceof String))) {
          try
          {
            start = Integer.parseInt((String)localObject);
          }
          catch (NumberFormatException localNumberFormatException) {}
        }
      }
    }
    
    private int getRenderIndex(View paramView, int paramInt)
    {
      if (!checkedForStart) {
        getStart(paramView);
      }
      int i = paramInt;
      for (int j = paramInt; j >= 0; j--)
      {
        AttributeSet localAttributeSet = paramView.getElement().getElement(j).getAttributes();
        if (localAttributeSet.getAttribute(StyleConstants.NameAttribute) != HTML.Tag.LI)
        {
          i--;
        }
        else if (localAttributeSet.isDefined(HTML.Attribute.VALUE))
        {
          Object localObject = localAttributeSet.getAttribute(HTML.Attribute.VALUE);
          if ((localObject != null) && ((localObject instanceof String))) {
            try
            {
              int k = Integer.parseInt((String)localObject);
              return i - j + k;
            }
            catch (NumberFormatException localNumberFormatException) {}
          }
        }
      }
      return i + start;
    }
    
    public void paint(Graphics paramGraphics, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, View paramView, int paramInt)
    {
      View localView = paramView.getView(paramInt);
      Container localContainer = paramView.getContainer();
      Object localObject1 = localView.getElement().getAttributes().getAttribute(StyleConstants.NameAttribute);
      if ((!(localObject1 instanceof HTML.Tag)) || (localObject1 != HTML.Tag.LI)) {
        return;
      }
      isLeftToRight = localContainer.getComponentOrientation().isLeftToRight();
      float f = 0.0F;
      if (localView.getViewCount() > 0)
      {
        localObject2 = localView.getView(0);
        localObject3 = ((View)localObject2).getElement().getAttributes().getAttribute(StyleConstants.NameAttribute);
        if (((localObject3 == HTML.Tag.P) || (localObject3 == HTML.Tag.IMPLIED)) && (((View)localObject2).getViewCount() > 0))
        {
          paintRect.setBounds((int)paramFloat1, (int)paramFloat2, (int)paramFloat3, (int)paramFloat4);
          localObject4 = localView.getChildAllocation(0, paintRect);
          if ((localObject4 != null) && ((localObject4 = ((View)localObject2).getView(0).getChildAllocation(0, (Shape)localObject4)) != null))
          {
            Rectangle localRectangle = (localObject4 instanceof Rectangle) ? (Rectangle)localObject4 : ((Shape)localObject4).getBounds();
            f = ((View)localObject2).getView(0).getAlignment(1);
            paramFloat2 = y;
            paramFloat4 = height;
          }
        }
      }
      Object localObject2 = localContainer.isEnabled() ? localContainer.getForeground() : ss != null ? ss.getForeground(localView.getAttributes()) : UIManager.getColor("textInactiveText");
      paramGraphics.setColor((Color)localObject2);
      if (img != null)
      {
        drawIcon(paramGraphics, (int)paramFloat1, (int)paramFloat2, (int)paramFloat3, (int)paramFloat4, f, localContainer);
        return;
      }
      Object localObject3 = getChildType(localView);
      Object localObject4 = ((StyledDocument)localView.getDocument()).getFont(localView.getAttributes());
      if (localObject4 != null) {
        paramGraphics.setFont((Font)localObject4);
      }
      if ((localObject3 == CSS.Value.SQUARE) || (localObject3 == CSS.Value.CIRCLE) || (localObject3 == CSS.Value.DISC)) {
        drawShape(paramGraphics, (CSS.Value)localObject3, (int)paramFloat1, (int)paramFloat2, (int)paramFloat3, (int)paramFloat4, f);
      } else if (localObject3 == CSS.Value.DECIMAL) {
        drawLetter(paramGraphics, '1', (int)paramFloat1, (int)paramFloat2, (int)paramFloat3, (int)paramFloat4, f, getRenderIndex(paramView, paramInt));
      } else if (localObject3 == CSS.Value.LOWER_ALPHA) {
        drawLetter(paramGraphics, 'a', (int)paramFloat1, (int)paramFloat2, (int)paramFloat3, (int)paramFloat4, f, getRenderIndex(paramView, paramInt));
      } else if (localObject3 == CSS.Value.UPPER_ALPHA) {
        drawLetter(paramGraphics, 'A', (int)paramFloat1, (int)paramFloat2, (int)paramFloat3, (int)paramFloat4, f, getRenderIndex(paramView, paramInt));
      } else if (localObject3 == CSS.Value.LOWER_ROMAN) {
        drawLetter(paramGraphics, 'i', (int)paramFloat1, (int)paramFloat2, (int)paramFloat3, (int)paramFloat4, f, getRenderIndex(paramView, paramInt));
      } else if (localObject3 == CSS.Value.UPPER_ROMAN) {
        drawLetter(paramGraphics, 'I', (int)paramFloat1, (int)paramFloat2, (int)paramFloat3, (int)paramFloat4, f, getRenderIndex(paramView, paramInt));
      }
    }
    
    void drawIcon(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat, Component paramComponent)
    {
      int i = isLeftToRight ? -(img.getIconWidth() + bulletgap) : paramInt3 + bulletgap;
      int j = paramInt1 + i;
      int k = Math.max(paramInt2, paramInt2 + (int)(paramFloat * paramInt4) - img.getIconHeight());
      img.paintIcon(paramComponent, paramGraphics, j, k);
    }
    
    void drawShape(Graphics paramGraphics, CSS.Value paramValue, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat)
    {
      int i = isLeftToRight ? -(bulletgap + 8) : paramInt3 + bulletgap;
      int j = paramInt1 + i;
      int k = Math.max(paramInt2, paramInt2 + (int)(paramFloat * paramInt4) - 8);
      if (paramValue == CSS.Value.SQUARE) {
        paramGraphics.drawRect(j, k, 8, 8);
      } else if (paramValue == CSS.Value.CIRCLE) {
        paramGraphics.drawOval(j, k, 8, 8);
      } else {
        paramGraphics.fillOval(j, k, 8, 8);
      }
    }
    
    void drawLetter(Graphics paramGraphics, char paramChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat, int paramInt5)
    {
      String str = formatItemNum(paramInt5, paramChar);
      str = "." + str;
      FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(null, paramGraphics);
      int i = SwingUtilities2.stringWidth(null, localFontMetrics, str);
      int j = isLeftToRight ? -(i + bulletgap) : paramInt3 + bulletgap;
      int k = paramInt1 + j;
      int m = Math.max(paramInt2 + localFontMetrics.getAscent(), paramInt2 + (int)(paramInt4 * paramFloat));
      SwingUtilities2.drawString(null, paramGraphics, str, k, m);
    }
    
    String formatItemNum(int paramInt, char paramChar)
    {
      String str1 = "1";
      int i = 0;
      String str2;
      switch (paramChar)
      {
      case '1': 
      default: 
        str2 = String.valueOf(paramInt);
        break;
      case 'A': 
        i = 1;
      case 'a': 
        str2 = formatAlphaNumerals(paramInt);
        break;
      case 'I': 
        i = 1;
      case 'i': 
        str2 = formatRomanNumerals(paramInt);
      }
      if (i != 0) {
        str2 = str2.toUpperCase();
      }
      return str2;
    }
    
    String formatAlphaNumerals(int paramInt)
    {
      String str;
      if (paramInt > 26) {
        str = formatAlphaNumerals(paramInt / 26) + formatAlphaNumerals(paramInt % 26);
      } else {
        str = String.valueOf((char)(97 + paramInt - 1));
      }
      return str;
    }
    
    String formatRomanNumerals(int paramInt)
    {
      return formatRomanNumerals(0, paramInt);
    }
    
    String formatRomanNumerals(int paramInt1, int paramInt2)
    {
      if (paramInt2 < 10) {
        return formatRomanDigit(paramInt1, paramInt2);
      }
      return formatRomanNumerals(paramInt1 + 1, paramInt2 / 10) + formatRomanDigit(paramInt1, paramInt2 % 10);
    }
    
    String formatRomanDigit(int paramInt1, int paramInt2)
    {
      String str = "";
      if (paramInt2 == 9)
      {
        str = str + romanChars[paramInt1][0];
        str = str + romanChars[(paramInt1 + 1)][0];
        return str;
      }
      if (paramInt2 == 4)
      {
        str = str + romanChars[paramInt1][0];
        str = str + romanChars[paramInt1][1];
        return str;
      }
      if (paramInt2 >= 5)
      {
        str = str + romanChars[paramInt1][1];
        paramInt2 -= 5;
      }
      for (int i = 0; i < paramInt2; i++) {
        str = str + romanChars[paramInt1][0];
      }
      return str;
    }
  }
  
  static class ResolvedStyle
    extends MuxingAttributeSet
    implements Serializable, Style
  {
    String name;
    private int extendedIndex;
    
    ResolvedStyle(String paramString, AttributeSet[] paramArrayOfAttributeSet, int paramInt)
    {
      super();
      name = paramString;
      extendedIndex = paramInt;
    }
    
    synchronized void insertStyle(Style paramStyle, int paramInt)
    {
      AttributeSet[] arrayOfAttributeSet = getAttributes();
      int i = arrayOfAttributeSet.length;
      for (int j = 0; (j < extendedIndex) && (paramInt <= StyleSheet.getSpecificity(((Style)arrayOfAttributeSet[j]).getName())); j++) {}
      insertAttributeSetAt(paramStyle, j);
      extendedIndex += 1;
    }
    
    synchronized void removeStyle(Style paramStyle)
    {
      AttributeSet[] arrayOfAttributeSet = getAttributes();
      for (int i = arrayOfAttributeSet.length - 1; i >= 0; i--) {
        if (arrayOfAttributeSet[i] == paramStyle)
        {
          removeAttributeSetAt(i);
          if (i >= extendedIndex) {
            break;
          }
          extendedIndex -= 1;
          break;
        }
      }
    }
    
    synchronized void insertExtendedStyleAt(Style paramStyle, int paramInt)
    {
      insertAttributeSetAt(paramStyle, extendedIndex + paramInt);
    }
    
    synchronized void addExtendedStyle(Style paramStyle)
    {
      insertAttributeSetAt(paramStyle, getAttributes().length);
    }
    
    synchronized void removeExtendedStyleAt(int paramInt)
    {
      removeAttributeSetAt(extendedIndex + paramInt);
    }
    
    protected boolean matches(String paramString)
    {
      int i = paramString.length();
      if (i == 0) {
        return false;
      }
      int j = name.length();
      int k = paramString.lastIndexOf(' ');
      int m = name.lastIndexOf(' ');
      if (k >= 0) {
        k++;
      }
      if (m >= 0) {
        m++;
      }
      if (!matches(paramString, k, i, m, j)) {
        return false;
      }
      while (k != -1)
      {
        i = k - 1;
        k = paramString.lastIndexOf(' ', i - 1);
        if (k >= 0) {
          k++;
        }
        for (boolean bool = false; (!bool) && (m != -1); bool = matches(paramString, k, i, m, j))
        {
          j = m - 1;
          m = name.lastIndexOf(' ', j - 1);
          if (m >= 0) {
            m++;
          }
        }
        if (!bool) {
          return false;
        }
      }
      return true;
    }
    
    boolean matches(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      paramInt1 = Math.max(paramInt1, 0);
      paramInt3 = Math.max(paramInt3, 0);
      int i = boundedIndexOf(name, '.', paramInt3, paramInt4);
      int j = boundedIndexOf(name, '#', paramInt3, paramInt4);
      int k = boundedIndexOf(paramString, '.', paramInt1, paramInt2);
      int m = boundedIndexOf(paramString, '#', paramInt1, paramInt2);
      if (k != -1)
      {
        if (i == -1) {
          return false;
        }
        if (paramInt1 == k)
        {
          if ((paramInt4 - i != paramInt2 - k) || (!paramString.regionMatches(paramInt1, name, i, paramInt4 - i))) {
            return false;
          }
        }
        else if ((paramInt2 - paramInt1 != paramInt4 - paramInt3) || (!paramString.regionMatches(paramInt1, name, paramInt3, paramInt4 - paramInt3))) {
          return false;
        }
        return true;
      }
      if (m != -1)
      {
        if (j == -1) {
          return false;
        }
        if (paramInt1 == m)
        {
          if ((paramInt4 - j != paramInt2 - m) || (!paramString.regionMatches(paramInt1, name, j, paramInt4 - j))) {
            return false;
          }
        }
        else if ((paramInt2 - paramInt1 != paramInt4 - paramInt3) || (!paramString.regionMatches(paramInt1, name, paramInt3, paramInt4 - paramInt3))) {
          return false;
        }
        return true;
      }
      if (i != -1) {
        return (i - paramInt3 == paramInt2 - paramInt1) && (paramString.regionMatches(paramInt1, name, paramInt3, i - paramInt3));
      }
      if (j != -1) {
        return (j - paramInt3 == paramInt2 - paramInt1) && (paramString.regionMatches(paramInt1, name, paramInt3, j - paramInt3));
      }
      return (paramInt4 - paramInt3 == paramInt2 - paramInt1) && (paramString.regionMatches(paramInt1, name, paramInt3, paramInt4 - paramInt3));
    }
    
    int boundedIndexOf(String paramString, char paramChar, int paramInt1, int paramInt2)
    {
      int i = paramString.indexOf(paramChar, paramInt1);
      if (i >= paramInt2) {
        return -1;
      }
      return i;
    }
    
    public void addAttribute(Object paramObject1, Object paramObject2) {}
    
    public void addAttributes(AttributeSet paramAttributeSet) {}
    
    public void removeAttribute(Object paramObject) {}
    
    public void removeAttributes(Enumeration<?> paramEnumeration) {}
    
    public void removeAttributes(AttributeSet paramAttributeSet) {}
    
    public void setResolveParent(AttributeSet paramAttributeSet) {}
    
    public String getName()
    {
      return name;
    }
    
    public void addChangeListener(ChangeListener paramChangeListener) {}
    
    public void removeChangeListener(ChangeListener paramChangeListener) {}
    
    public ChangeListener[] getChangeListeners()
    {
      return new ChangeListener[0];
    }
  }
  
  private static class SearchBuffer
  {
    static Stack<SearchBuffer> searchBuffers = new Stack();
    Vector vector = null;
    StringBuffer stringBuffer = null;
    Hashtable hashtable = null;
    
    private SearchBuffer() {}
    
    static SearchBuffer obtainSearchBuffer()
    {
      SearchBuffer localSearchBuffer;
      try
      {
        if (!searchBuffers.empty()) {
          localSearchBuffer = (SearchBuffer)searchBuffers.pop();
        } else {
          localSearchBuffer = new SearchBuffer();
        }
      }
      catch (EmptyStackException localEmptyStackException)
      {
        localSearchBuffer = new SearchBuffer();
      }
      return localSearchBuffer;
    }
    
    static void releaseSearchBuffer(SearchBuffer paramSearchBuffer)
    {
      paramSearchBuffer.empty();
      searchBuffers.push(paramSearchBuffer);
    }
    
    StringBuffer getStringBuffer()
    {
      if (stringBuffer == null) {
        stringBuffer = new StringBuffer();
      }
      return stringBuffer;
    }
    
    Vector getVector()
    {
      if (vector == null) {
        vector = new Vector();
      }
      return vector;
    }
    
    Hashtable getHashtable()
    {
      if (hashtable == null) {
        hashtable = new Hashtable();
      }
      return hashtable;
    }
    
    void empty()
    {
      if (stringBuffer != null) {
        stringBuffer.setLength(0);
      }
      if (vector != null) {
        vector.removeAllElements();
      }
      if (hashtable != null) {
        hashtable.clear();
      }
    }
  }
  
  static class SelectorMapping
    implements Serializable
  {
    private int specificity;
    private Style style;
    private HashMap<String, SelectorMapping> children;
    
    public SelectorMapping(int paramInt)
    {
      specificity = paramInt;
    }
    
    public int getSpecificity()
    {
      return specificity;
    }
    
    public void setStyle(Style paramStyle)
    {
      style = paramStyle;
    }
    
    public Style getStyle()
    {
      return style;
    }
    
    public SelectorMapping getChildSelectorMapping(String paramString, boolean paramBoolean)
    {
      SelectorMapping localSelectorMapping = null;
      if (children != null) {
        localSelectorMapping = (SelectorMapping)children.get(paramString);
      } else if (paramBoolean) {
        children = new HashMap(7);
      }
      if ((localSelectorMapping == null) && (paramBoolean))
      {
        int i = getChildSpecificity(paramString);
        localSelectorMapping = createChildSelectorMapping(i);
        children.put(paramString, localSelectorMapping);
      }
      return localSelectorMapping;
    }
    
    protected SelectorMapping createChildSelectorMapping(int paramInt)
    {
      return new SelectorMapping(paramInt);
    }
    
    protected int getChildSpecificity(String paramString)
    {
      int i = paramString.charAt(0);
      int j = getSpecificity();
      if (i == 46)
      {
        j += 100;
      }
      else if (i == 35)
      {
        j += 10000;
      }
      else
      {
        j++;
        if (paramString.indexOf('.') != -1) {
          j += 100;
        }
        if (paramString.indexOf('#') != -1) {
          j += 10000;
        }
      }
      return j;
    }
  }
  
  class SmallConversionSet
    extends StyleContext.SmallAttributeSet
  {
    public SmallConversionSet(AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public boolean isDefined(Object paramObject)
    {
      if ((paramObject instanceof StyleConstants))
      {
        CSS.Attribute localAttribute = css.styleConstantsKeyToCSSKey((StyleConstants)paramObject);
        if (localAttribute != null) {
          return super.isDefined(localAttribute);
        }
      }
      return super.isDefined(paramObject);
    }
    
    public Object getAttribute(Object paramObject)
    {
      if ((paramObject instanceof StyleConstants))
      {
        CSS.Attribute localAttribute = css.styleConstantsKeyToCSSKey((StyleConstants)paramObject);
        if (localAttribute != null)
        {
          Object localObject = super.getAttribute(localAttribute);
          if (localObject != null) {
            return css.cssValueToStyleConstantsValue((StyleConstants)paramObject, localObject);
          }
        }
      }
      return super.getAttribute(paramObject);
    }
  }
  
  class ViewAttributeSet
    extends MuxingAttributeSet
  {
    View host;
    
    ViewAttributeSet(View paramView)
    {
      host = paramView;
      Document localDocument = paramView.getDocument();
      StyleSheet.SearchBuffer localSearchBuffer = StyleSheet.SearchBuffer.obtainSearchBuffer();
      Vector localVector = localSearchBuffer.getVector();
      try
      {
        if ((localDocument instanceof HTMLDocument))
        {
          localObject1 = StyleSheet.this;
          Element localElement = paramView.getElement();
          AttributeSet localAttributeSet1 = localElement.getAttributes();
          AttributeSet localAttributeSet2 = ((StyleSheet)localObject1).translateHTMLToCSS(localAttributeSet1);
          if (localAttributeSet2.getAttributeCount() != 0) {
            localVector.addElement(localAttributeSet2);
          }
          Object localObject2;
          Object localObject3;
          if (localElement.isLeaf())
          {
            localObject2 = localAttributeSet1.getAttributeNames();
            while (((Enumeration)localObject2).hasMoreElements())
            {
              localObject3 = ((Enumeration)localObject2).nextElement();
              if ((localObject3 instanceof HTML.Tag))
              {
                if (localObject3 == HTML.Tag.A)
                {
                  localObject4 = localAttributeSet1.getAttribute(localObject3);
                  if ((localObject4 != null) && ((localObject4 instanceof AttributeSet)))
                  {
                    AttributeSet localAttributeSet3 = (AttributeSet)localObject4;
                    if (localAttributeSet3.getAttribute(HTML.Attribute.HREF) == null) {
                      continue;
                    }
                  }
                }
                Object localObject4 = ((StyleSheet)localObject1).getRule((HTML.Tag)localObject3, localElement);
                if (localObject4 != null) {
                  localVector.addElement(localObject4);
                }
              }
            }
          }
          else
          {
            localObject2 = (HTML.Tag)localAttributeSet1.getAttribute(StyleConstants.NameAttribute);
            localObject3 = ((StyleSheet)localObject1).getRule((HTML.Tag)localObject2, localElement);
            if (localObject3 != null) {
              localVector.addElement(localObject3);
            }
          }
        }
        Object localObject1 = new AttributeSet[localVector.size()];
        localVector.copyInto((Object[])localObject1);
        setAttributes((AttributeSet[])localObject1);
      }
      finally
      {
        StyleSheet.SearchBuffer.releaseSearchBuffer(localSearchBuffer);
      }
    }
    
    public boolean isDefined(Object paramObject)
    {
      if ((paramObject instanceof StyleConstants))
      {
        CSS.Attribute localAttribute = css.styleConstantsKeyToCSSKey((StyleConstants)paramObject);
        if (localAttribute != null) {
          paramObject = localAttribute;
        }
      }
      return super.isDefined(paramObject);
    }
    
    public Object getAttribute(Object paramObject)
    {
      if ((paramObject instanceof StyleConstants))
      {
        CSS.Attribute localAttribute = css.styleConstantsKeyToCSSKey((StyleConstants)paramObject);
        if (localAttribute != null)
        {
          Object localObject = doGetAttribute(localAttribute);
          if ((localObject instanceof CSS.CssValue)) {
            return ((CSS.CssValue)localObject).toStyleConstants((StyleConstants)paramObject, host);
          }
        }
      }
      return doGetAttribute(paramObject);
    }
    
    Object doGetAttribute(Object paramObject)
    {
      Object localObject = super.getAttribute(paramObject);
      if (localObject != null) {
        return localObject;
      }
      if ((paramObject instanceof CSS.Attribute))
      {
        CSS.Attribute localAttribute = (CSS.Attribute)paramObject;
        if (localAttribute.isInherited())
        {
          AttributeSet localAttributeSet = getResolveParent();
          if (localAttributeSet != null) {
            return localAttributeSet.getAttribute(paramObject);
          }
        }
      }
      return null;
    }
    
    public AttributeSet getResolveParent()
    {
      if (host == null) {
        return null;
      }
      View localView = host.getParent();
      return localView != null ? localView.getAttributes() : null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\StyleSheet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */