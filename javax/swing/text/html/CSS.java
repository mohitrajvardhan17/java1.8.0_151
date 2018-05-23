package javax.swing.text.html;

import java.awt.Color;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.SizeRequirements;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.View;

public class CSS
  implements Serializable
{
  private static final Hashtable<String, Attribute> attributeMap = new Hashtable();
  private static final Hashtable<String, Value> valueMap = new Hashtable();
  private static final Hashtable<HTML.Attribute, Attribute[]> htmlAttrToCssAttrMap = new Hashtable(20);
  private static final Hashtable<Object, Attribute> styleConstantToCssMap = new Hashtable(17);
  private static final Hashtable<String, Value> htmlValueToCssValueMap = new Hashtable(8);
  private static final Hashtable<String, Value> cssValueToInternalValueMap = new Hashtable(13);
  private transient Hashtable<Object, Object> valueConvertor = new Hashtable();
  private int baseFontSize = baseFontSizeIndex + 1;
  private transient StyleSheet styleSheet = null;
  static int baseFontSizeIndex = 3;
  
  public CSS()
  {
    valueConvertor.put(Attribute.FONT_SIZE, new FontSize());
    valueConvertor.put(Attribute.FONT_FAMILY, new FontFamily());
    valueConvertor.put(Attribute.FONT_WEIGHT, new FontWeight());
    BorderStyle localBorderStyle = new BorderStyle();
    valueConvertor.put(Attribute.BORDER_TOP_STYLE, localBorderStyle);
    valueConvertor.put(Attribute.BORDER_RIGHT_STYLE, localBorderStyle);
    valueConvertor.put(Attribute.BORDER_BOTTOM_STYLE, localBorderStyle);
    valueConvertor.put(Attribute.BORDER_LEFT_STYLE, localBorderStyle);
    ColorValue localColorValue = new ColorValue();
    valueConvertor.put(Attribute.COLOR, localColorValue);
    valueConvertor.put(Attribute.BACKGROUND_COLOR, localColorValue);
    valueConvertor.put(Attribute.BORDER_TOP_COLOR, localColorValue);
    valueConvertor.put(Attribute.BORDER_RIGHT_COLOR, localColorValue);
    valueConvertor.put(Attribute.BORDER_BOTTOM_COLOR, localColorValue);
    valueConvertor.put(Attribute.BORDER_LEFT_COLOR, localColorValue);
    LengthValue localLengthValue1 = new LengthValue();
    valueConvertor.put(Attribute.MARGIN_TOP, localLengthValue1);
    valueConvertor.put(Attribute.MARGIN_BOTTOM, localLengthValue1);
    valueConvertor.put(Attribute.MARGIN_LEFT, localLengthValue1);
    valueConvertor.put(Attribute.MARGIN_LEFT_LTR, localLengthValue1);
    valueConvertor.put(Attribute.MARGIN_LEFT_RTL, localLengthValue1);
    valueConvertor.put(Attribute.MARGIN_RIGHT, localLengthValue1);
    valueConvertor.put(Attribute.MARGIN_RIGHT_LTR, localLengthValue1);
    valueConvertor.put(Attribute.MARGIN_RIGHT_RTL, localLengthValue1);
    valueConvertor.put(Attribute.PADDING_TOP, localLengthValue1);
    valueConvertor.put(Attribute.PADDING_BOTTOM, localLengthValue1);
    valueConvertor.put(Attribute.PADDING_LEFT, localLengthValue1);
    valueConvertor.put(Attribute.PADDING_RIGHT, localLengthValue1);
    BorderWidthValue localBorderWidthValue = new BorderWidthValue(null, 0);
    valueConvertor.put(Attribute.BORDER_TOP_WIDTH, localBorderWidthValue);
    valueConvertor.put(Attribute.BORDER_BOTTOM_WIDTH, localBorderWidthValue);
    valueConvertor.put(Attribute.BORDER_LEFT_WIDTH, localBorderWidthValue);
    valueConvertor.put(Attribute.BORDER_RIGHT_WIDTH, localBorderWidthValue);
    LengthValue localLengthValue2 = new LengthValue(true);
    valueConvertor.put(Attribute.TEXT_INDENT, localLengthValue2);
    valueConvertor.put(Attribute.WIDTH, localLengthValue1);
    valueConvertor.put(Attribute.HEIGHT, localLengthValue1);
    valueConvertor.put(Attribute.BORDER_SPACING, localLengthValue1);
    StringValue localStringValue = new StringValue();
    valueConvertor.put(Attribute.FONT_STYLE, localStringValue);
    valueConvertor.put(Attribute.TEXT_DECORATION, localStringValue);
    valueConvertor.put(Attribute.TEXT_ALIGN, localStringValue);
    valueConvertor.put(Attribute.VERTICAL_ALIGN, localStringValue);
    CssValueMapper localCssValueMapper = new CssValueMapper();
    valueConvertor.put(Attribute.LIST_STYLE_TYPE, localCssValueMapper);
    valueConvertor.put(Attribute.BACKGROUND_IMAGE, new BackgroundImage());
    valueConvertor.put(Attribute.BACKGROUND_POSITION, new BackgroundPosition());
    valueConvertor.put(Attribute.BACKGROUND_REPEAT, localCssValueMapper);
    valueConvertor.put(Attribute.BACKGROUND_ATTACHMENT, localCssValueMapper);
    CssValue localCssValue = new CssValue();
    int i = Attribute.allAttributes.length;
    for (int j = 0; j < i; j++)
    {
      Attribute localAttribute = Attribute.allAttributes[j];
      if (valueConvertor.get(localAttribute) == null) {
        valueConvertor.put(localAttribute, localCssValue);
      }
    }
  }
  
  void setBaseFontSize(int paramInt)
  {
    if (paramInt < 1) {
      baseFontSize = 0;
    } else if (paramInt > 7) {
      baseFontSize = 7;
    } else {
      baseFontSize = paramInt;
    }
  }
  
  void setBaseFontSize(String paramString)
  {
    if (paramString != null)
    {
      int i;
      if (paramString.startsWith("+"))
      {
        i = Integer.valueOf(paramString.substring(1)).intValue();
        setBaseFontSize(baseFontSize + i);
      }
      else if (paramString.startsWith("-"))
      {
        i = -Integer.valueOf(paramString.substring(1)).intValue();
        setBaseFontSize(baseFontSize + i);
      }
      else
      {
        setBaseFontSize(Integer.valueOf(paramString).intValue());
      }
    }
  }
  
  int getBaseFontSize()
  {
    return baseFontSize;
  }
  
  void addInternalCSSValue(MutableAttributeSet paramMutableAttributeSet, Attribute paramAttribute, String paramString)
  {
    if (paramAttribute == Attribute.FONT)
    {
      ShorthandFontParser.parseShorthandFont(this, paramString, paramMutableAttributeSet);
    }
    else if (paramAttribute == Attribute.BACKGROUND)
    {
      ShorthandBackgroundParser.parseShorthandBackground(this, paramString, paramMutableAttributeSet);
    }
    else if (paramAttribute == Attribute.MARGIN)
    {
      ShorthandMarginParser.parseShorthandMargin(this, paramString, paramMutableAttributeSet, Attribute.ALL_MARGINS);
    }
    else if (paramAttribute == Attribute.PADDING)
    {
      ShorthandMarginParser.parseShorthandMargin(this, paramString, paramMutableAttributeSet, Attribute.ALL_PADDING);
    }
    else if (paramAttribute == Attribute.BORDER_WIDTH)
    {
      ShorthandMarginParser.parseShorthandMargin(this, paramString, paramMutableAttributeSet, Attribute.ALL_BORDER_WIDTHS);
    }
    else if (paramAttribute == Attribute.BORDER_COLOR)
    {
      ShorthandMarginParser.parseShorthandMargin(this, paramString, paramMutableAttributeSet, Attribute.ALL_BORDER_COLORS);
    }
    else if (paramAttribute == Attribute.BORDER_STYLE)
    {
      ShorthandMarginParser.parseShorthandMargin(this, paramString, paramMutableAttributeSet, Attribute.ALL_BORDER_STYLES);
    }
    else if ((paramAttribute == Attribute.BORDER) || (paramAttribute == Attribute.BORDER_TOP) || (paramAttribute == Attribute.BORDER_RIGHT) || (paramAttribute == Attribute.BORDER_BOTTOM) || (paramAttribute == Attribute.BORDER_LEFT))
    {
      ShorthandBorderParser.parseShorthandBorder(paramMutableAttributeSet, paramAttribute, paramString);
    }
    else
    {
      Object localObject = getInternalCSSValue(paramAttribute, paramString);
      if (localObject != null) {
        paramMutableAttributeSet.addAttribute(paramAttribute, localObject);
      }
    }
  }
  
  Object getInternalCSSValue(Attribute paramAttribute, String paramString)
  {
    CssValue localCssValue = (CssValue)valueConvertor.get(paramAttribute);
    Object localObject = localCssValue.parseCssValue(paramString);
    return localObject != null ? localObject : localCssValue.parseCssValue(paramAttribute.getDefaultValue());
  }
  
  Attribute styleConstantsKeyToCSSKey(StyleConstants paramStyleConstants)
  {
    return (Attribute)styleConstantToCssMap.get(paramStyleConstants);
  }
  
  Object styleConstantsValueToCSSValue(StyleConstants paramStyleConstants, Object paramObject)
  {
    Attribute localAttribute = styleConstantsKeyToCSSKey(paramStyleConstants);
    if (localAttribute != null)
    {
      CssValue localCssValue = (CssValue)valueConvertor.get(localAttribute);
      return localCssValue.fromStyleConstants(paramStyleConstants, paramObject);
    }
    return null;
  }
  
  Object cssValueToStyleConstantsValue(StyleConstants paramStyleConstants, Object paramObject)
  {
    if ((paramObject instanceof CssValue)) {
      return ((CssValue)paramObject).toStyleConstants(paramStyleConstants, null);
    }
    return null;
  }
  
  Font getFont(StyleContext paramStyleContext, AttributeSet paramAttributeSet, int paramInt, StyleSheet paramStyleSheet)
  {
    paramStyleSheet = getStyleSheet(paramStyleSheet);
    int i = getFontSize(paramAttributeSet, paramInt, paramStyleSheet);
    StringValue localStringValue = (StringValue)paramAttributeSet.getAttribute(Attribute.VERTICAL_ALIGN);
    if (localStringValue != null)
    {
      localObject1 = localStringValue.toString();
      if ((((String)localObject1).indexOf("sup") >= 0) || (((String)localObject1).indexOf("sub") >= 0)) {
        i -= 2;
      }
    }
    Object localObject1 = (FontFamily)paramAttributeSet.getAttribute(Attribute.FONT_FAMILY);
    String str = localObject1 != null ? ((FontFamily)localObject1).getValue() : "SansSerif";
    int j = 0;
    FontWeight localFontWeight = (FontWeight)paramAttributeSet.getAttribute(Attribute.FONT_WEIGHT);
    if ((localFontWeight != null) && (localFontWeight.getValue() > 400)) {
      j |= 0x1;
    }
    Object localObject2 = paramAttributeSet.getAttribute(Attribute.FONT_STYLE);
    if ((localObject2 != null) && (localObject2.toString().indexOf("italic") >= 0)) {
      j |= 0x2;
    }
    if (str.equalsIgnoreCase("monospace")) {
      str = "Monospaced";
    }
    Font localFont = paramStyleContext.getFont(str, j, i);
    if ((localFont == null) || ((localFont.getFamily().equals("Dialog")) && (!str.equalsIgnoreCase("Dialog"))))
    {
      str = "SansSerif";
      localFont = paramStyleContext.getFont(str, j, i);
    }
    return localFont;
  }
  
  static int getFontSize(AttributeSet paramAttributeSet, int paramInt, StyleSheet paramStyleSheet)
  {
    FontSize localFontSize = (FontSize)paramAttributeSet.getAttribute(Attribute.FONT_SIZE);
    return localFontSize != null ? localFontSize.getValue(paramAttributeSet, paramStyleSheet) : paramInt;
  }
  
  Color getColor(AttributeSet paramAttributeSet, Attribute paramAttribute)
  {
    ColorValue localColorValue = (ColorValue)paramAttributeSet.getAttribute(paramAttribute);
    if (localColorValue != null) {
      return localColorValue.getValue();
    }
    return null;
  }
  
  float getPointSize(String paramString, StyleSheet paramStyleSheet)
  {
    paramStyleSheet = getStyleSheet(paramStyleSheet);
    if (paramString != null)
    {
      int i;
      if (paramString.startsWith("+"))
      {
        i = Integer.valueOf(paramString.substring(1)).intValue();
        return getPointSize(baseFontSize + i, paramStyleSheet);
      }
      if (paramString.startsWith("-"))
      {
        i = -Integer.valueOf(paramString.substring(1)).intValue();
        return getPointSize(baseFontSize + i, paramStyleSheet);
      }
      int j = Integer.valueOf(paramString).intValue();
      return getPointSize(j, paramStyleSheet);
    }
    return 0.0F;
  }
  
  float getLength(AttributeSet paramAttributeSet, Attribute paramAttribute, StyleSheet paramStyleSheet)
  {
    paramStyleSheet = getStyleSheet(paramStyleSheet);
    LengthValue localLengthValue = (LengthValue)paramAttributeSet.getAttribute(paramAttribute);
    boolean bool = paramStyleSheet == null ? false : paramStyleSheet.isW3CLengthUnits();
    float f = localLengthValue != null ? localLengthValue.getValue(bool) : 0.0F;
    return f;
  }
  
  AttributeSet translateHTMLToCSS(AttributeSet paramAttributeSet)
  {
    SimpleAttributeSet localSimpleAttributeSet = new SimpleAttributeSet();
    Element localElement = (Element)paramAttributeSet;
    HTML.Tag localTag = getHTMLTag(paramAttributeSet);
    Object localObject;
    if ((localTag == HTML.Tag.TD) || (localTag == HTML.Tag.TH))
    {
      localObject = localElement.getParentElement().getParentElement().getAttributes();
      int i = getTableBorder((AttributeSet)localObject);
      if (i > 0) {
        translateAttribute(HTML.Attribute.BORDER, "1", localSimpleAttributeSet);
      }
      String str = (String)((AttributeSet)localObject).getAttribute(HTML.Attribute.CELLPADDING);
      if (str != null)
      {
        LengthValue localLengthValue = (LengthValue)getInternalCSSValue(Attribute.PADDING_TOP, str);
        span = (span < 0.0F ? 0.0F : span);
        localSimpleAttributeSet.addAttribute(Attribute.PADDING_TOP, localLengthValue);
        localSimpleAttributeSet.addAttribute(Attribute.PADDING_BOTTOM, localLengthValue);
        localSimpleAttributeSet.addAttribute(Attribute.PADDING_LEFT, localLengthValue);
        localSimpleAttributeSet.addAttribute(Attribute.PADDING_RIGHT, localLengthValue);
      }
    }
    if (localElement.isLeaf()) {
      translateEmbeddedAttributes(paramAttributeSet, localSimpleAttributeSet);
    } else {
      translateAttributes(localTag, paramAttributeSet, localSimpleAttributeSet);
    }
    if (localTag == HTML.Tag.CAPTION)
    {
      localObject = paramAttributeSet.getAttribute(HTML.Attribute.ALIGN);
      if ((localObject != null) && ((localObject.equals("top")) || (localObject.equals("bottom"))))
      {
        localSimpleAttributeSet.addAttribute(Attribute.CAPTION_SIDE, localObject);
        localSimpleAttributeSet.removeAttribute(Attribute.TEXT_ALIGN);
      }
      else
      {
        localObject = paramAttributeSet.getAttribute(HTML.Attribute.VALIGN);
        if (localObject != null) {
          localSimpleAttributeSet.addAttribute(Attribute.CAPTION_SIDE, localObject);
        }
      }
    }
    return localSimpleAttributeSet;
  }
  
  private static int getTableBorder(AttributeSet paramAttributeSet)
  {
    String str = (String)paramAttributeSet.getAttribute(HTML.Attribute.BORDER);
    if ((str == "#DEFAULT") || ("".equals(str))) {
      return 1;
    }
    try
    {
      return Integer.parseInt(str);
    }
    catch (NumberFormatException localNumberFormatException) {}
    return 0;
  }
  
  public static Attribute[] getAllAttributeKeys()
  {
    Attribute[] arrayOfAttribute = new Attribute[Attribute.allAttributes.length];
    System.arraycopy(Attribute.allAttributes, 0, arrayOfAttribute, 0, Attribute.allAttributes.length);
    return arrayOfAttribute;
  }
  
  public static final Attribute getAttribute(String paramString)
  {
    return (Attribute)attributeMap.get(paramString);
  }
  
  static final Value getValue(String paramString)
  {
    return (Value)valueMap.get(paramString);
  }
  
  static URL getURL(URL paramURL, String paramString)
  {
    if (paramString == null) {
      return null;
    }
    if ((paramString.startsWith("url(")) && (paramString.endsWith(")"))) {
      paramString = paramString.substring(4, paramString.length() - 1);
    }
    try
    {
      URL localURL1 = new URL(paramString);
      if (localURL1 != null) {
        return localURL1;
      }
    }
    catch (MalformedURLException localMalformedURLException1) {}
    if (paramURL != null) {
      try
      {
        URL localURL2 = new URL(paramURL, paramString);
        return localURL2;
      }
      catch (MalformedURLException localMalformedURLException2) {}
    }
    return null;
  }
  
  static String colorToHex(Color paramColor)
  {
    String str1 = "#";
    String str2 = Integer.toHexString(paramColor.getRed());
    if (str2.length() > 2) {
      str2 = str2.substring(0, 2);
    } else if (str2.length() < 2) {
      str1 = str1 + "0" + str2;
    } else {
      str1 = str1 + str2;
    }
    str2 = Integer.toHexString(paramColor.getGreen());
    if (str2.length() > 2) {
      str2 = str2.substring(0, 2);
    } else if (str2.length() < 2) {
      str1 = str1 + "0" + str2;
    } else {
      str1 = str1 + str2;
    }
    str2 = Integer.toHexString(paramColor.getBlue());
    if (str2.length() > 2) {
      str2 = str2.substring(0, 2);
    } else if (str2.length() < 2) {
      str1 = str1 + "0" + str2;
    } else {
      str1 = str1 + str2;
    }
    return str1;
  }
  
  static final Color hexToColor(String paramString)
  {
    int i = paramString.length();
    String str1;
    if (paramString.startsWith("#")) {
      str1 = paramString.substring(1, Math.min(paramString.length(), 7));
    } else {
      str1 = paramString;
    }
    String str2 = "0x" + str1;
    Color localColor;
    try
    {
      localColor = Color.decode(str2);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      localColor = null;
    }
    return localColor;
  }
  
  static Color stringToColor(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    Color localColor;
    if (paramString.length() == 0) {
      localColor = Color.black;
    } else if (paramString.startsWith("rgb(")) {
      localColor = parseRGB(paramString);
    } else if (paramString.charAt(0) == '#') {
      localColor = hexToColor(paramString);
    } else if (paramString.equalsIgnoreCase("Black")) {
      localColor = hexToColor("#000000");
    } else if (paramString.equalsIgnoreCase("Silver")) {
      localColor = hexToColor("#C0C0C0");
    } else if (paramString.equalsIgnoreCase("Gray")) {
      localColor = hexToColor("#808080");
    } else if (paramString.equalsIgnoreCase("White")) {
      localColor = hexToColor("#FFFFFF");
    } else if (paramString.equalsIgnoreCase("Maroon")) {
      localColor = hexToColor("#800000");
    } else if (paramString.equalsIgnoreCase("Red")) {
      localColor = hexToColor("#FF0000");
    } else if (paramString.equalsIgnoreCase("Purple")) {
      localColor = hexToColor("#800080");
    } else if (paramString.equalsIgnoreCase("Fuchsia")) {
      localColor = hexToColor("#FF00FF");
    } else if (paramString.equalsIgnoreCase("Green")) {
      localColor = hexToColor("#008000");
    } else if (paramString.equalsIgnoreCase("Lime")) {
      localColor = hexToColor("#00FF00");
    } else if (paramString.equalsIgnoreCase("Olive")) {
      localColor = hexToColor("#808000");
    } else if (paramString.equalsIgnoreCase("Yellow")) {
      localColor = hexToColor("#FFFF00");
    } else if (paramString.equalsIgnoreCase("Navy")) {
      localColor = hexToColor("#000080");
    } else if (paramString.equalsIgnoreCase("Blue")) {
      localColor = hexToColor("#0000FF");
    } else if (paramString.equalsIgnoreCase("Teal")) {
      localColor = hexToColor("#008080");
    } else if (paramString.equalsIgnoreCase("Aqua")) {
      localColor = hexToColor("#00FFFF");
    } else if (paramString.equalsIgnoreCase("Orange")) {
      localColor = hexToColor("#FF8000");
    } else {
      localColor = hexToColor(paramString);
    }
    return localColor;
  }
  
  private static Color parseRGB(String paramString)
  {
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = 4;
    int i = getColorComponent(paramString, arrayOfInt);
    int j = getColorComponent(paramString, arrayOfInt);
    int k = getColorComponent(paramString, arrayOfInt);
    return new Color(i, j, k);
  }
  
  private static int getColorComponent(String paramString, int[] paramArrayOfInt)
  {
    int i = paramString.length();
    char c;
    while ((paramArrayOfInt[0] < i) && ((c = paramString.charAt(paramArrayOfInt[0])) != '-') && (!Character.isDigit(c)) && (c != '.')) {
      paramArrayOfInt[0] += 1;
    }
    int j = paramArrayOfInt[0];
    if ((j < i) && (paramString.charAt(paramArrayOfInt[0]) == '-')) {
      paramArrayOfInt[0] += 1;
    }
    while ((paramArrayOfInt[0] < i) && (Character.isDigit(paramString.charAt(paramArrayOfInt[0])))) {
      paramArrayOfInt[0] += 1;
    }
    if ((paramArrayOfInt[0] < i) && (paramString.charAt(paramArrayOfInt[0]) == '.'))
    {
      paramArrayOfInt[0] += 1;
      while ((paramArrayOfInt[0] < i) && (Character.isDigit(paramString.charAt(paramArrayOfInt[0])))) {
        paramArrayOfInt[0] += 1;
      }
    }
    if (j != paramArrayOfInt[0]) {
      try
      {
        float f = Float.parseFloat(paramString.substring(j, paramArrayOfInt[0]));
        if ((paramArrayOfInt[0] < i) && (paramString.charAt(paramArrayOfInt[0]) == '%'))
        {
          paramArrayOfInt[0] += 1;
          f = f * 255.0F / 100.0F;
        }
        return Math.min(255, Math.max(0, (int)f));
      }
      catch (NumberFormatException localNumberFormatException) {}
    }
    return 0;
  }
  
  static int getIndexOfSize(float paramFloat, int[] paramArrayOfInt)
  {
    for (int i = 0; i < paramArrayOfInt.length; i++) {
      if (paramFloat <= paramArrayOfInt[i]) {
        return i + 1;
      }
    }
    return paramArrayOfInt.length;
  }
  
  static int getIndexOfSize(float paramFloat, StyleSheet paramStyleSheet)
  {
    int[] arrayOfInt = paramStyleSheet != null ? paramStyleSheet.getSizeMap() : StyleSheet.sizeMapDefault;
    return getIndexOfSize(paramFloat, arrayOfInt);
  }
  
  static String[] parseStrings(String paramString)
  {
    int k = paramString == null ? 0 : paramString.length();
    Vector localVector = new Vector(4);
    for (int i = 0; i < k; i++)
    {
      while ((i < k) && (Character.isWhitespace(paramString.charAt(i)))) {
        i++;
      }
      int j = i;
      while ((i < k) && (!Character.isWhitespace(paramString.charAt(i)))) {
        i++;
      }
      if (j != i) {
        localVector.addElement(paramString.substring(j, i));
      }
    }
    String[] arrayOfString = new String[localVector.size()];
    localVector.copyInto(arrayOfString);
    return arrayOfString;
  }
  
  float getPointSize(int paramInt, StyleSheet paramStyleSheet)
  {
    paramStyleSheet = getStyleSheet(paramStyleSheet);
    int[] arrayOfInt = paramStyleSheet != null ? paramStyleSheet.getSizeMap() : StyleSheet.sizeMapDefault;
    paramInt--;
    if (paramInt < 0) {
      return arrayOfInt[0];
    }
    if (paramInt > arrayOfInt.length - 1) {
      return arrayOfInt[(arrayOfInt.length - 1)];
    }
    return arrayOfInt[paramInt];
  }
  
  private void translateEmbeddedAttributes(AttributeSet paramAttributeSet, MutableAttributeSet paramMutableAttributeSet)
  {
    Enumeration localEnumeration = paramAttributeSet.getAttributeNames();
    if (paramAttributeSet.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.HR) {
      translateAttributes(HTML.Tag.HR, paramAttributeSet, paramMutableAttributeSet);
    }
    while (localEnumeration.hasMoreElements())
    {
      Object localObject1 = localEnumeration.nextElement();
      if ((localObject1 instanceof HTML.Tag))
      {
        HTML.Tag localTag = (HTML.Tag)localObject1;
        Object localObject2 = paramAttributeSet.getAttribute(localTag);
        if ((localObject2 != null) && ((localObject2 instanceof AttributeSet))) {
          translateAttributes(localTag, (AttributeSet)localObject2, paramMutableAttributeSet);
        }
      }
      else if ((localObject1 instanceof Attribute))
      {
        paramMutableAttributeSet.addAttribute(localObject1, paramAttributeSet.getAttribute(localObject1));
      }
    }
  }
  
  private void translateAttributes(HTML.Tag paramTag, AttributeSet paramAttributeSet, MutableAttributeSet paramMutableAttributeSet)
  {
    Enumeration localEnumeration = paramAttributeSet.getAttributeNames();
    while (localEnumeration.hasMoreElements())
    {
      Object localObject1 = localEnumeration.nextElement();
      if ((localObject1 instanceof HTML.Attribute))
      {
        HTML.Attribute localAttribute = (HTML.Attribute)localObject1;
        if (localAttribute == HTML.Attribute.ALIGN)
        {
          String str = (String)paramAttributeSet.getAttribute(HTML.Attribute.ALIGN);
          if (str != null)
          {
            Attribute localAttribute1 = getCssAlignAttribute(paramTag, paramAttributeSet);
            if (localAttribute1 != null)
            {
              Object localObject2 = getCssValue(localAttribute1, str);
              if (localObject2 != null) {
                paramMutableAttributeSet.addAttribute(localAttribute1, localObject2);
              }
            }
          }
        }
        else if ((localAttribute != HTML.Attribute.SIZE) || (isHTMLFontTag(paramTag)))
        {
          if ((paramTag == HTML.Tag.TABLE) && (localAttribute == HTML.Attribute.BORDER))
          {
            int i = getTableBorder(paramAttributeSet);
            if (i > 0) {
              translateAttribute(HTML.Attribute.BORDER, Integer.toString(i), paramMutableAttributeSet);
            }
          }
          else
          {
            translateAttribute(localAttribute, (String)paramAttributeSet.getAttribute(localAttribute), paramMutableAttributeSet);
          }
        }
      }
      else if ((localObject1 instanceof Attribute))
      {
        paramMutableAttributeSet.addAttribute(localObject1, paramAttributeSet.getAttribute(localObject1));
      }
    }
  }
  
  private void translateAttribute(HTML.Attribute paramAttribute, String paramString, MutableAttributeSet paramMutableAttributeSet)
  {
    Attribute[] arrayOfAttribute1 = getCssAttribute(paramAttribute);
    if ((arrayOfAttribute1 == null) || (paramString == null)) {
      return;
    }
    for (Attribute localAttribute : arrayOfAttribute1)
    {
      Object localObject = getCssValue(localAttribute, paramString);
      if (localObject != null) {
        paramMutableAttributeSet.addAttribute(localAttribute, localObject);
      }
    }
  }
  
  Object getCssValue(Attribute paramAttribute, String paramString)
  {
    CssValue localCssValue = (CssValue)valueConvertor.get(paramAttribute);
    Object localObject = localCssValue.parseHtmlValue(paramString);
    return localObject;
  }
  
  private Attribute[] getCssAttribute(HTML.Attribute paramAttribute)
  {
    return (Attribute[])htmlAttrToCssAttrMap.get(paramAttribute);
  }
  
  private Attribute getCssAlignAttribute(HTML.Tag paramTag, AttributeSet paramAttributeSet)
  {
    return Attribute.TEXT_ALIGN;
  }
  
  private HTML.Tag getHTMLTag(AttributeSet paramAttributeSet)
  {
    Object localObject = paramAttributeSet.getAttribute(StyleConstants.NameAttribute);
    if ((localObject instanceof HTML.Tag))
    {
      HTML.Tag localTag = (HTML.Tag)localObject;
      return localTag;
    }
    return null;
  }
  
  private boolean isHTMLFontTag(HTML.Tag paramTag)
  {
    return (paramTag != null) && ((paramTag == HTML.Tag.FONT) || (paramTag == HTML.Tag.BASEFONT));
  }
  
  private boolean isFloater(String paramString)
  {
    return (paramString.equals("left")) || (paramString.equals("right"));
  }
  
  private boolean validTextAlignValue(String paramString)
  {
    return (isFloater(paramString)) || (paramString.equals("center"));
  }
  
  static SizeRequirements calculateTiledRequirements(LayoutIterator paramLayoutIterator, SizeRequirements paramSizeRequirements)
  {
    long l1 = 0L;
    long l2 = 0L;
    long l3 = 0L;
    int i = 0;
    int j = 0;
    int k = paramLayoutIterator.getCount();
    for (int m = 0; m < k; m++)
    {
      paramLayoutIterator.setIndex(m);
      int n = i;
      int i1 = (int)paramLayoutIterator.getLeadingCollapseSpan();
      j += Math.max(n, i1);
      l3 += (int)paramLayoutIterator.getPreferredSpan(0.0F);
      l1 = ((float)l1 + paramLayoutIterator.getMinimumSpan(0.0F));
      l2 = ((float)l2 + paramLayoutIterator.getMaximumSpan(0.0F));
      i = (int)paramLayoutIterator.getTrailingCollapseSpan();
    }
    j += i;
    j = (int)(j + 2.0F * paramLayoutIterator.getBorderWidth());
    l1 += j;
    l3 += j;
    l2 += j;
    if (paramSizeRequirements == null) {
      paramSizeRequirements = new SizeRequirements();
    }
    minimum = (l1 > 2147483647L ? Integer.MAX_VALUE : (int)l1);
    preferred = (l3 > 2147483647L ? Integer.MAX_VALUE : (int)l3);
    maximum = (l2 > 2147483647L ? Integer.MAX_VALUE : (int)l2);
    return paramSizeRequirements;
  }
  
  static void calculateTiledLayout(LayoutIterator paramLayoutIterator, int paramInt)
  {
    long l1 = 0L;
    int i = 0;
    int j = 0;
    int k = paramLayoutIterator.getCount();
    int m = 3;
    long[] arrayOfLong1 = new long[m];
    long[] arrayOfLong2 = new long[m];
    for (int n = 0; n < m; n++) {
      arrayOfLong1[n] = (arrayOfLong2[n] = 0L);
    }
    for (n = 0; n < k; n++)
    {
      paramLayoutIterator.setIndex(n);
      int i1 = i;
      int i2 = (int)paramLayoutIterator.getLeadingCollapseSpan();
      paramLayoutIterator.setOffset(Math.max(i1, i2));
      j += paramLayoutIterator.getOffset();
      long l2 = paramLayoutIterator.getPreferredSpan(paramInt);
      paramLayoutIterator.setSpan((int)l2);
      l1 += l2;
      arrayOfLong1[paramLayoutIterator.getAdjustmentWeight()] += paramLayoutIterator.getMaximumSpan(paramInt) - l2;
      arrayOfLong2[paramLayoutIterator.getAdjustmentWeight()] += l2 - paramLayoutIterator.getMinimumSpan(paramInt);
      i = (int)paramLayoutIterator.getTrailingCollapseSpan();
    }
    j += i;
    j = (int)(j + 2.0F * paramLayoutIterator.getBorderWidth());
    for (n = 1; n < m; n++)
    {
      arrayOfLong1[n] += arrayOfLong1[(n - 1)];
      arrayOfLong2[n] += arrayOfLong2[(n - 1)];
    }
    n = paramInt - j;
    long l3 = n - l1;
    long[] arrayOfLong3 = l3 > 0L ? arrayOfLong1 : arrayOfLong2;
    l3 = Math.abs(l3);
    for (int i3 = 0; (i3 <= 2) && (arrayOfLong3[i3] < l3); i3++) {}
    float f1 = 0.0F;
    if (i3 <= 2)
    {
      l3 -= (i3 > 0 ? arrayOfLong3[(i3 - 1)] : 0L);
      if (l3 != 0L)
      {
        float f2 = (float)(arrayOfLong3[i3] - (i3 > 0 ? arrayOfLong3[(i3 - 1)] : 0L));
        f1 = (float)l3 / f2;
      }
    }
    int i4 = (int)paramLayoutIterator.getBorderWidth();
    for (int i5 = 0; i5 < k; i5++)
    {
      paramLayoutIterator.setIndex(i5);
      paramLayoutIterator.setOffset(paramLayoutIterator.getOffset() + i4);
      if (paramLayoutIterator.getAdjustmentWeight() < i3)
      {
        paramLayoutIterator.setSpan((int)(n > l1 ? Math.floor(paramLayoutIterator.getMaximumSpan(paramInt)) : Math.ceil(paramLayoutIterator.getMinimumSpan(paramInt))));
      }
      else if (paramLayoutIterator.getAdjustmentWeight() == i3)
      {
        i6 = n > l1 ? (int)paramLayoutIterator.getMaximumSpan(paramInt) - paramLayoutIterator.getSpan() : paramLayoutIterator.getSpan() - (int)paramLayoutIterator.getMinimumSpan(paramInt);
        i7 = (int)Math.floor(f1 * i6);
        paramLayoutIterator.setSpan(paramLayoutIterator.getSpan() + (n > l1 ? i7 : -i7));
      }
      i4 = (int)Math.min(paramLayoutIterator.getOffset() + paramLayoutIterator.getSpan(), 2147483647L);
    }
    i5 = paramInt - i4 - (int)paramLayoutIterator.getTrailingCollapseSpan() - (int)paramLayoutIterator.getBorderWidth();
    int i6 = i5 > 0 ? 1 : -1;
    i5 *= i6;
    int i7 = 1;
    while ((i5 > 0) && (i7 != 0))
    {
      i7 = 0;
      int i8 = 0;
      for (int i9 = 0; i9 < k; i9++)
      {
        paramLayoutIterator.setIndex(i9);
        paramLayoutIterator.setOffset(paramLayoutIterator.getOffset() + i8);
        int i10 = paramLayoutIterator.getSpan();
        if (i5 > 0)
        {
          int i11 = i6 > 0 ? (int)Math.floor(paramLayoutIterator.getMaximumSpan(paramInt)) - i10 : i10 - (int)Math.ceil(paramLayoutIterator.getMinimumSpan(paramInt));
          if (i11 >= 1)
          {
            i7 = 1;
            paramLayoutIterator.setSpan(i10 + i6);
            i8 += i6;
            i5--;
          }
        }
      }
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    Enumeration localEnumeration = valueConvertor.keys();
    paramObjectOutputStream.writeInt(valueConvertor.size());
    if (localEnumeration != null) {
      while (localEnumeration.hasMoreElements())
      {
        Object localObject1 = localEnumeration.nextElement();
        Object localObject2 = valueConvertor.get(localObject1);
        if ((!(localObject1 instanceof Serializable)) && ((localObject1 = StyleContext.getStaticAttributeKey(localObject1)) == null))
        {
          localObject1 = null;
          localObject2 = null;
        }
        else if ((!(localObject2 instanceof Serializable)) && ((localObject2 = StyleContext.getStaticAttributeKey(localObject2)) == null))
        {
          localObject1 = null;
          localObject2 = null;
        }
        paramObjectOutputStream.writeObject(localObject1);
        paramObjectOutputStream.writeObject(localObject2);
      }
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readInt();
    valueConvertor = new Hashtable(Math.max(1, i));
    while (i-- > 0)
    {
      Object localObject1 = paramObjectInputStream.readObject();
      Object localObject2 = paramObjectInputStream.readObject();
      Object localObject3 = StyleContext.getStaticAttribute(localObject1);
      if (localObject3 != null) {
        localObject1 = localObject3;
      }
      Object localObject4 = StyleContext.getStaticAttribute(localObject2);
      if (localObject4 != null) {
        localObject2 = localObject4;
      }
      if ((localObject1 != null) && (localObject2 != null)) {
        valueConvertor.put(localObject1, localObject2);
      }
    }
  }
  
  private StyleSheet getStyleSheet(StyleSheet paramStyleSheet)
  {
    if (paramStyleSheet != null) {
      styleSheet = paramStyleSheet;
    }
    return styleSheet;
  }
  
  static
  {
    for (int i = 0; i < Attribute.allAttributes.length; i++) {
      attributeMap.put(Attribute.allAttributes[i].toString(), Attribute.allAttributes[i]);
    }
    for (i = 0; i < Value.allValues.length; i++) {
      valueMap.put(Value.allValues[i].toString(), Value.allValues[i]);
    }
    htmlAttrToCssAttrMap.put(HTML.Attribute.COLOR, new Attribute[] { Attribute.COLOR });
    htmlAttrToCssAttrMap.put(HTML.Attribute.TEXT, new Attribute[] { Attribute.COLOR });
    htmlAttrToCssAttrMap.put(HTML.Attribute.CLEAR, new Attribute[] { Attribute.CLEAR });
    htmlAttrToCssAttrMap.put(HTML.Attribute.BACKGROUND, new Attribute[] { Attribute.BACKGROUND_IMAGE });
    htmlAttrToCssAttrMap.put(HTML.Attribute.BGCOLOR, new Attribute[] { Attribute.BACKGROUND_COLOR });
    htmlAttrToCssAttrMap.put(HTML.Attribute.WIDTH, new Attribute[] { Attribute.WIDTH });
    htmlAttrToCssAttrMap.put(HTML.Attribute.HEIGHT, new Attribute[] { Attribute.HEIGHT });
    htmlAttrToCssAttrMap.put(HTML.Attribute.BORDER, new Attribute[] { Attribute.BORDER_TOP_WIDTH, Attribute.BORDER_RIGHT_WIDTH, Attribute.BORDER_BOTTOM_WIDTH, Attribute.BORDER_LEFT_WIDTH });
    htmlAttrToCssAttrMap.put(HTML.Attribute.CELLPADDING, new Attribute[] { Attribute.PADDING });
    htmlAttrToCssAttrMap.put(HTML.Attribute.CELLSPACING, new Attribute[] { Attribute.BORDER_SPACING });
    htmlAttrToCssAttrMap.put(HTML.Attribute.MARGINWIDTH, new Attribute[] { Attribute.MARGIN_LEFT, Attribute.MARGIN_RIGHT });
    htmlAttrToCssAttrMap.put(HTML.Attribute.MARGINHEIGHT, new Attribute[] { Attribute.MARGIN_TOP, Attribute.MARGIN_BOTTOM });
    htmlAttrToCssAttrMap.put(HTML.Attribute.HSPACE, new Attribute[] { Attribute.PADDING_LEFT, Attribute.PADDING_RIGHT });
    htmlAttrToCssAttrMap.put(HTML.Attribute.VSPACE, new Attribute[] { Attribute.PADDING_BOTTOM, Attribute.PADDING_TOP });
    htmlAttrToCssAttrMap.put(HTML.Attribute.FACE, new Attribute[] { Attribute.FONT_FAMILY });
    htmlAttrToCssAttrMap.put(HTML.Attribute.SIZE, new Attribute[] { Attribute.FONT_SIZE });
    htmlAttrToCssAttrMap.put(HTML.Attribute.VALIGN, new Attribute[] { Attribute.VERTICAL_ALIGN });
    htmlAttrToCssAttrMap.put(HTML.Attribute.ALIGN, new Attribute[] { Attribute.VERTICAL_ALIGN, Attribute.TEXT_ALIGN, Attribute.FLOAT });
    htmlAttrToCssAttrMap.put(HTML.Attribute.TYPE, new Attribute[] { Attribute.LIST_STYLE_TYPE });
    htmlAttrToCssAttrMap.put(HTML.Attribute.NOWRAP, new Attribute[] { Attribute.WHITE_SPACE });
    styleConstantToCssMap.put(StyleConstants.FontFamily, Attribute.FONT_FAMILY);
    styleConstantToCssMap.put(StyleConstants.FontSize, Attribute.FONT_SIZE);
    styleConstantToCssMap.put(StyleConstants.Bold, Attribute.FONT_WEIGHT);
    styleConstantToCssMap.put(StyleConstants.Italic, Attribute.FONT_STYLE);
    styleConstantToCssMap.put(StyleConstants.Underline, Attribute.TEXT_DECORATION);
    styleConstantToCssMap.put(StyleConstants.StrikeThrough, Attribute.TEXT_DECORATION);
    styleConstantToCssMap.put(StyleConstants.Superscript, Attribute.VERTICAL_ALIGN);
    styleConstantToCssMap.put(StyleConstants.Subscript, Attribute.VERTICAL_ALIGN);
    styleConstantToCssMap.put(StyleConstants.Foreground, Attribute.COLOR);
    styleConstantToCssMap.put(StyleConstants.Background, Attribute.BACKGROUND_COLOR);
    styleConstantToCssMap.put(StyleConstants.FirstLineIndent, Attribute.TEXT_INDENT);
    styleConstantToCssMap.put(StyleConstants.LeftIndent, Attribute.MARGIN_LEFT);
    styleConstantToCssMap.put(StyleConstants.RightIndent, Attribute.MARGIN_RIGHT);
    styleConstantToCssMap.put(StyleConstants.SpaceAbove, Attribute.MARGIN_TOP);
    styleConstantToCssMap.put(StyleConstants.SpaceBelow, Attribute.MARGIN_BOTTOM);
    styleConstantToCssMap.put(StyleConstants.Alignment, Attribute.TEXT_ALIGN);
    htmlValueToCssValueMap.put("disc", Value.DISC);
    htmlValueToCssValueMap.put("square", Value.SQUARE);
    htmlValueToCssValueMap.put("circle", Value.CIRCLE);
    htmlValueToCssValueMap.put("1", Value.DECIMAL);
    htmlValueToCssValueMap.put("a", Value.LOWER_ALPHA);
    htmlValueToCssValueMap.put("A", Value.UPPER_ALPHA);
    htmlValueToCssValueMap.put("i", Value.LOWER_ROMAN);
    htmlValueToCssValueMap.put("I", Value.UPPER_ROMAN);
    cssValueToInternalValueMap.put("none", Value.NONE);
    cssValueToInternalValueMap.put("disc", Value.DISC);
    cssValueToInternalValueMap.put("square", Value.SQUARE);
    cssValueToInternalValueMap.put("circle", Value.CIRCLE);
    cssValueToInternalValueMap.put("decimal", Value.DECIMAL);
    cssValueToInternalValueMap.put("lower-roman", Value.LOWER_ROMAN);
    cssValueToInternalValueMap.put("upper-roman", Value.UPPER_ROMAN);
    cssValueToInternalValueMap.put("lower-alpha", Value.LOWER_ALPHA);
    cssValueToInternalValueMap.put("upper-alpha", Value.UPPER_ALPHA);
    cssValueToInternalValueMap.put("repeat", Value.BACKGROUND_REPEAT);
    cssValueToInternalValueMap.put("no-repeat", Value.BACKGROUND_NO_REPEAT);
    cssValueToInternalValueMap.put("repeat-x", Value.BACKGROUND_REPEAT_X);
    cssValueToInternalValueMap.put("repeat-y", Value.BACKGROUND_REPEAT_Y);
    cssValueToInternalValueMap.put("scroll", Value.BACKGROUND_SCROLL);
    cssValueToInternalValueMap.put("fixed", Value.BACKGROUND_FIXED);
    Object localObject1 = Attribute.allAttributes;
    Object localObject4;
    try
    {
      for (localObject4 : localObject1) {
        StyleContext.registerStaticAttributeKey(localObject4);
      }
    }
    catch (Throwable localThrowable1)
    {
      localThrowable1.printStackTrace();
    }
    localObject1 = Value.allValues;
    try
    {
      for (localObject4 : localObject1) {
        StyleContext.registerStaticAttributeKey(localObject4);
      }
    }
    catch (Throwable localThrowable2)
    {
      localThrowable2.printStackTrace();
    }
  }
  
  public static final class Attribute
  {
    private String name;
    private String defaultValue;
    private boolean inherited;
    public static final Attribute BACKGROUND = new Attribute("background", null, false);
    public static final Attribute BACKGROUND_ATTACHMENT = new Attribute("background-attachment", "scroll", false);
    public static final Attribute BACKGROUND_COLOR = new Attribute("background-color", "transparent", false);
    public static final Attribute BACKGROUND_IMAGE = new Attribute("background-image", "none", false);
    public static final Attribute BACKGROUND_POSITION = new Attribute("background-position", null, false);
    public static final Attribute BACKGROUND_REPEAT = new Attribute("background-repeat", "repeat", false);
    public static final Attribute BORDER = new Attribute("border", null, false);
    public static final Attribute BORDER_BOTTOM = new Attribute("border-bottom", null, false);
    public static final Attribute BORDER_BOTTOM_COLOR = new Attribute("border-bottom-color", null, false);
    public static final Attribute BORDER_BOTTOM_STYLE = new Attribute("border-bottom-style", "none", false);
    public static final Attribute BORDER_BOTTOM_WIDTH = new Attribute("border-bottom-width", "medium", false);
    public static final Attribute BORDER_COLOR = new Attribute("border-color", null, false);
    public static final Attribute BORDER_LEFT = new Attribute("border-left", null, false);
    public static final Attribute BORDER_LEFT_COLOR = new Attribute("border-left-color", null, false);
    public static final Attribute BORDER_LEFT_STYLE = new Attribute("border-left-style", "none", false);
    public static final Attribute BORDER_LEFT_WIDTH = new Attribute("border-left-width", "medium", false);
    public static final Attribute BORDER_RIGHT = new Attribute("border-right", null, false);
    public static final Attribute BORDER_RIGHT_COLOR = new Attribute("border-right-color", null, false);
    public static final Attribute BORDER_RIGHT_STYLE = new Attribute("border-right-style", "none", false);
    public static final Attribute BORDER_RIGHT_WIDTH = new Attribute("border-right-width", "medium", false);
    public static final Attribute BORDER_STYLE = new Attribute("border-style", "none", false);
    public static final Attribute BORDER_TOP = new Attribute("border-top", null, false);
    public static final Attribute BORDER_TOP_COLOR = new Attribute("border-top-color", null, false);
    public static final Attribute BORDER_TOP_STYLE = new Attribute("border-top-style", "none", false);
    public static final Attribute BORDER_TOP_WIDTH = new Attribute("border-top-width", "medium", false);
    public static final Attribute BORDER_WIDTH = new Attribute("border-width", "medium", false);
    public static final Attribute CLEAR = new Attribute("clear", "none", false);
    public static final Attribute COLOR = new Attribute("color", "black", true);
    public static final Attribute DISPLAY = new Attribute("display", "block", false);
    public static final Attribute FLOAT = new Attribute("float", "none", false);
    public static final Attribute FONT = new Attribute("font", null, true);
    public static final Attribute FONT_FAMILY = new Attribute("font-family", null, true);
    public static final Attribute FONT_SIZE = new Attribute("font-size", "medium", true);
    public static final Attribute FONT_STYLE = new Attribute("font-style", "normal", true);
    public static final Attribute FONT_VARIANT = new Attribute("font-variant", "normal", true);
    public static final Attribute FONT_WEIGHT = new Attribute("font-weight", "normal", true);
    public static final Attribute HEIGHT = new Attribute("height", "auto", false);
    public static final Attribute LETTER_SPACING = new Attribute("letter-spacing", "normal", true);
    public static final Attribute LINE_HEIGHT = new Attribute("line-height", "normal", true);
    public static final Attribute LIST_STYLE = new Attribute("list-style", null, true);
    public static final Attribute LIST_STYLE_IMAGE = new Attribute("list-style-image", "none", true);
    public static final Attribute LIST_STYLE_POSITION = new Attribute("list-style-position", "outside", true);
    public static final Attribute LIST_STYLE_TYPE = new Attribute("list-style-type", "disc", true);
    public static final Attribute MARGIN = new Attribute("margin", null, false);
    public static final Attribute MARGIN_BOTTOM = new Attribute("margin-bottom", "0", false);
    public static final Attribute MARGIN_LEFT = new Attribute("margin-left", "0", false);
    public static final Attribute MARGIN_RIGHT = new Attribute("margin-right", "0", false);
    static final Attribute MARGIN_LEFT_LTR = new Attribute("margin-left-ltr", Integer.toString(Integer.MIN_VALUE), false);
    static final Attribute MARGIN_LEFT_RTL = new Attribute("margin-left-rtl", Integer.toString(Integer.MIN_VALUE), false);
    static final Attribute MARGIN_RIGHT_LTR = new Attribute("margin-right-ltr", Integer.toString(Integer.MIN_VALUE), false);
    static final Attribute MARGIN_RIGHT_RTL = new Attribute("margin-right-rtl", Integer.toString(Integer.MIN_VALUE), false);
    public static final Attribute MARGIN_TOP = new Attribute("margin-top", "0", false);
    public static final Attribute PADDING = new Attribute("padding", null, false);
    public static final Attribute PADDING_BOTTOM = new Attribute("padding-bottom", "0", false);
    public static final Attribute PADDING_LEFT = new Attribute("padding-left", "0", false);
    public static final Attribute PADDING_RIGHT = new Attribute("padding-right", "0", false);
    public static final Attribute PADDING_TOP = new Attribute("padding-top", "0", false);
    public static final Attribute TEXT_ALIGN = new Attribute("text-align", null, true);
    public static final Attribute TEXT_DECORATION = new Attribute("text-decoration", "none", true);
    public static final Attribute TEXT_INDENT = new Attribute("text-indent", "0", true);
    public static final Attribute TEXT_TRANSFORM = new Attribute("text-transform", "none", true);
    public static final Attribute VERTICAL_ALIGN = new Attribute("vertical-align", "baseline", false);
    public static final Attribute WORD_SPACING = new Attribute("word-spacing", "normal", true);
    public static final Attribute WHITE_SPACE = new Attribute("white-space", "normal", true);
    public static final Attribute WIDTH = new Attribute("width", "auto", false);
    static final Attribute BORDER_SPACING = new Attribute("border-spacing", "0", true);
    static final Attribute CAPTION_SIDE = new Attribute("caption-side", "left", true);
    static final Attribute[] allAttributes = { BACKGROUND, BACKGROUND_ATTACHMENT, BACKGROUND_COLOR, BACKGROUND_IMAGE, BACKGROUND_POSITION, BACKGROUND_REPEAT, BORDER, BORDER_BOTTOM, BORDER_BOTTOM_WIDTH, BORDER_COLOR, BORDER_LEFT, BORDER_LEFT_WIDTH, BORDER_RIGHT, BORDER_RIGHT_WIDTH, BORDER_STYLE, BORDER_TOP, BORDER_TOP_WIDTH, BORDER_WIDTH, BORDER_TOP_STYLE, BORDER_RIGHT_STYLE, BORDER_BOTTOM_STYLE, BORDER_LEFT_STYLE, BORDER_TOP_COLOR, BORDER_RIGHT_COLOR, BORDER_BOTTOM_COLOR, BORDER_LEFT_COLOR, CLEAR, COLOR, DISPLAY, FLOAT, FONT, FONT_FAMILY, FONT_SIZE, FONT_STYLE, FONT_VARIANT, FONT_WEIGHT, HEIGHT, LETTER_SPACING, LINE_HEIGHT, LIST_STYLE, LIST_STYLE_IMAGE, LIST_STYLE_POSITION, LIST_STYLE_TYPE, MARGIN, MARGIN_BOTTOM, MARGIN_LEFT, MARGIN_RIGHT, MARGIN_TOP, PADDING, PADDING_BOTTOM, PADDING_LEFT, PADDING_RIGHT, PADDING_TOP, TEXT_ALIGN, TEXT_DECORATION, TEXT_INDENT, TEXT_TRANSFORM, VERTICAL_ALIGN, WORD_SPACING, WHITE_SPACE, WIDTH, BORDER_SPACING, CAPTION_SIDE, MARGIN_LEFT_LTR, MARGIN_LEFT_RTL, MARGIN_RIGHT_LTR, MARGIN_RIGHT_RTL };
    private static final Attribute[] ALL_MARGINS = { MARGIN_TOP, MARGIN_RIGHT, MARGIN_BOTTOM, MARGIN_LEFT };
    private static final Attribute[] ALL_PADDING = { PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM, PADDING_LEFT };
    private static final Attribute[] ALL_BORDER_WIDTHS = { BORDER_TOP_WIDTH, BORDER_RIGHT_WIDTH, BORDER_BOTTOM_WIDTH, BORDER_LEFT_WIDTH };
    private static final Attribute[] ALL_BORDER_STYLES = { BORDER_TOP_STYLE, BORDER_RIGHT_STYLE, BORDER_BOTTOM_STYLE, BORDER_LEFT_STYLE };
    private static final Attribute[] ALL_BORDER_COLORS = { BORDER_TOP_COLOR, BORDER_RIGHT_COLOR, BORDER_BOTTOM_COLOR, BORDER_LEFT_COLOR };
    
    private Attribute(String paramString1, String paramString2, boolean paramBoolean)
    {
      name = paramString1;
      defaultValue = paramString2;
      inherited = paramBoolean;
    }
    
    public String toString()
    {
      return name;
    }
    
    public String getDefaultValue()
    {
      return defaultValue;
    }
    
    public boolean isInherited()
    {
      return inherited;
    }
  }
  
  static class BackgroundImage
    extends CSS.CssValue
  {
    private boolean loadedImage;
    private ImageIcon image;
    
    BackgroundImage() {}
    
    Object parseCssValue(String paramString)
    {
      BackgroundImage localBackgroundImage = new BackgroundImage();
      svalue = paramString;
      return localBackgroundImage;
    }
    
    Object parseHtmlValue(String paramString)
    {
      return parseCssValue(paramString);
    }
    
    ImageIcon getImage(URL paramURL)
    {
      if (!loadedImage) {
        synchronized (this)
        {
          if (!loadedImage)
          {
            URL localURL = CSS.getURL(paramURL, svalue);
            loadedImage = true;
            if (localURL != null)
            {
              image = new ImageIcon();
              Image localImage = Toolkit.getDefaultToolkit().createImage(localURL);
              if (localImage != null) {
                image.setImage(localImage);
              }
            }
          }
        }
      }
      return image;
    }
  }
  
  static class BackgroundPosition
    extends CSS.CssValue
  {
    float horizontalPosition;
    float verticalPosition;
    short relative;
    
    BackgroundPosition() {}
    
    Object parseCssValue(String paramString)
    {
      String[] arrayOfString = CSS.parseStrings(paramString);
      int i = arrayOfString.length;
      BackgroundPosition localBackgroundPosition = new BackgroundPosition();
      relative = 5;
      svalue = paramString;
      if (i > 0)
      {
        int j = 0;
        int k = 0;
        Object localObject;
        while (k < i)
        {
          localObject = arrayOfString[(k++)];
          if (((String)localObject).equals("center"))
          {
            j = (short)(j | 0x4);
          }
          else
          {
            if ((j & 0x1) == 0) {
              if (((String)localObject).equals("top"))
              {
                j = (short)(j | 0x1);
              }
              else if (((String)localObject).equals("bottom"))
              {
                j = (short)(j | 0x1);
                verticalPosition = 1.0F;
                continue;
              }
            }
            if ((j & 0x2) == 0) {
              if (((String)localObject).equals("left"))
              {
                j = (short)(j | 0x2);
                horizontalPosition = 0.0F;
              }
              else if (((String)localObject).equals("right"))
              {
                j = (short)(j | 0x2);
                horizontalPosition = 1.0F;
              }
            }
          }
        }
        if (j != 0)
        {
          if ((j & 0x1) == 1)
          {
            if ((j & 0x2) == 0) {
              horizontalPosition = 0.5F;
            }
          }
          else if ((j & 0x2) == 2) {
            verticalPosition = 0.5F;
          } else {
            horizontalPosition = (verticalPosition = 0.5F);
          }
        }
        else
        {
          localObject = new CSS.LengthUnit(arrayOfString[0], (short)0, 0.0F);
          if (type == 0)
          {
            horizontalPosition = value;
            relative = ((short)(0x1 ^ relative));
          }
          else if (type == 1)
          {
            horizontalPosition = value;
          }
          else if (type == 3)
          {
            horizontalPosition = value;
            relative = ((short)(0x1 ^ relative | 0x2));
          }
          if (i > 1)
          {
            localObject = new CSS.LengthUnit(arrayOfString[1], (short)0, 0.0F);
            if (type == 0)
            {
              verticalPosition = value;
              relative = ((short)(0x4 ^ relative));
            }
            else if (type == 1)
            {
              verticalPosition = value;
            }
            else if (type == 3)
            {
              verticalPosition = value;
              relative = ((short)(0x4 ^ relative | 0x8));
            }
          }
          else
          {
            verticalPosition = 0.5F;
          }
        }
      }
      return localBackgroundPosition;
    }
    
    boolean isHorizontalPositionRelativeToSize()
    {
      return (relative & 0x1) == 1;
    }
    
    boolean isHorizontalPositionRelativeToFontSize()
    {
      return (relative & 0x2) == 2;
    }
    
    float getHorizontalPosition()
    {
      return horizontalPosition;
    }
    
    boolean isVerticalPositionRelativeToSize()
    {
      return (relative & 0x4) == 4;
    }
    
    boolean isVerticalPositionRelativeToFontSize()
    {
      return (relative & 0x8) == 8;
    }
    
    float getVerticalPosition()
    {
      return verticalPosition;
    }
  }
  
  static class BorderStyle
    extends CSS.CssValue
  {
    private transient CSS.Value style;
    
    BorderStyle() {}
    
    CSS.Value getValue()
    {
      return style;
    }
    
    Object parseCssValue(String paramString)
    {
      CSS.Value localValue = CSS.getValue(paramString);
      if ((localValue != null) && ((localValue == CSS.Value.INSET) || (localValue == CSS.Value.OUTSET) || (localValue == CSS.Value.NONE) || (localValue == CSS.Value.DOTTED) || (localValue == CSS.Value.DASHED) || (localValue == CSS.Value.SOLID) || (localValue == CSS.Value.DOUBLE) || (localValue == CSS.Value.GROOVE) || (localValue == CSS.Value.RIDGE)))
      {
        BorderStyle localBorderStyle = new BorderStyle();
        svalue = paramString;
        style = localValue;
        return localBorderStyle;
      }
      return null;
    }
    
    private void writeObject(ObjectOutputStream paramObjectOutputStream)
      throws IOException
    {
      paramObjectOutputStream.defaultWriteObject();
      if (style == null) {
        paramObjectOutputStream.writeObject(null);
      } else {
        paramObjectOutputStream.writeObject(style.toString());
      }
    }
    
    private void readObject(ObjectInputStream paramObjectInputStream)
      throws ClassNotFoundException, IOException
    {
      paramObjectInputStream.defaultReadObject();
      Object localObject = paramObjectInputStream.readObject();
      if (localObject != null) {
        style = CSS.getValue((String)localObject);
      }
    }
  }
  
  static class BorderWidthValue
    extends CSS.LengthValue
  {
    private static final float[] values = { 1.0F, 2.0F, 4.0F };
    
    BorderWidthValue(String paramString, int paramInt)
    {
      svalue = paramString;
      span = values[paramInt];
      percentage = false;
    }
    
    Object parseCssValue(String paramString)
    {
      if (paramString != null)
      {
        if (paramString.equals("thick")) {
          return new BorderWidthValue(paramString, 2);
        }
        if (paramString.equals("medium")) {
          return new BorderWidthValue(paramString, 1);
        }
        if (paramString.equals("thin")) {
          return new BorderWidthValue(paramString, 0);
        }
      }
      return super.parseCssValue(paramString);
    }
    
    Object parseHtmlValue(String paramString)
    {
      if (paramString == "#DEFAULT") {
        return parseCssValue("medium");
      }
      return parseCssValue(paramString);
    }
  }
  
  static class ColorValue
    extends CSS.CssValue
  {
    Color c;
    
    ColorValue() {}
    
    Color getValue()
    {
      return c;
    }
    
    Object parseCssValue(String paramString)
    {
      Color localColor = CSS.stringToColor(paramString);
      if (localColor != null)
      {
        ColorValue localColorValue = new ColorValue();
        svalue = paramString;
        c = localColor;
        return localColorValue;
      }
      return null;
    }
    
    Object parseHtmlValue(String paramString)
    {
      return parseCssValue(paramString);
    }
    
    Object fromStyleConstants(StyleConstants paramStyleConstants, Object paramObject)
    {
      ColorValue localColorValue = new ColorValue();
      c = ((Color)paramObject);
      svalue = CSS.colorToHex(c);
      return localColorValue;
    }
    
    Object toStyleConstants(StyleConstants paramStyleConstants, View paramView)
    {
      return c;
    }
  }
  
  static class CssValue
    implements Serializable
  {
    String svalue;
    
    CssValue() {}
    
    Object parseCssValue(String paramString)
    {
      return paramString;
    }
    
    Object parseHtmlValue(String paramString)
    {
      return parseCssValue(paramString);
    }
    
    Object fromStyleConstants(StyleConstants paramStyleConstants, Object paramObject)
    {
      return null;
    }
    
    Object toStyleConstants(StyleConstants paramStyleConstants, View paramView)
    {
      return null;
    }
    
    public String toString()
    {
      return svalue;
    }
  }
  
  static class CssValueMapper
    extends CSS.CssValue
  {
    CssValueMapper() {}
    
    Object parseCssValue(String paramString)
    {
      Object localObject = CSS.cssValueToInternalValueMap.get(paramString);
      if (localObject == null) {
        localObject = CSS.cssValueToInternalValueMap.get(paramString.toLowerCase());
      }
      return localObject;
    }
    
    Object parseHtmlValue(String paramString)
    {
      Object localObject = CSS.htmlValueToCssValueMap.get(paramString);
      if (localObject == null) {
        localObject = CSS.htmlValueToCssValueMap.get(paramString.toLowerCase());
      }
      return localObject;
    }
  }
  
  static class FontFamily
    extends CSS.CssValue
  {
    String family;
    
    FontFamily() {}
    
    String getValue()
    {
      return family;
    }
    
    Object parseCssValue(String paramString)
    {
      int i = paramString.indexOf(',');
      FontFamily localFontFamily = new FontFamily();
      svalue = paramString;
      family = null;
      if (i == -1)
      {
        setFontName(localFontFamily, paramString);
      }
      else
      {
        int j = 0;
        int m = paramString.length();
        i = 0;
        while (j == 0)
        {
          while ((i < m) && (Character.isWhitespace(paramString.charAt(i)))) {
            i++;
          }
          int k = i;
          i = paramString.indexOf(',', i);
          if (i == -1) {
            i = m;
          }
          if (k < m)
          {
            if (k != i)
            {
              int n = i;
              if ((i > 0) && (paramString.charAt(i - 1) == ' ')) {
                n--;
              }
              setFontName(localFontFamily, paramString.substring(k, n));
              j = family != null ? 1 : 0;
            }
            i++;
          }
          else
          {
            j = 1;
          }
        }
      }
      if (family == null) {
        family = "SansSerif";
      }
      return localFontFamily;
    }
    
    private void setFontName(FontFamily paramFontFamily, String paramString)
    {
      family = paramString;
    }
    
    Object parseHtmlValue(String paramString)
    {
      return parseCssValue(paramString);
    }
    
    Object fromStyleConstants(StyleConstants paramStyleConstants, Object paramObject)
    {
      return parseCssValue(paramObject.toString());
    }
    
    Object toStyleConstants(StyleConstants paramStyleConstants, View paramView)
    {
      return family;
    }
  }
  
  class FontSize
    extends CSS.CssValue
  {
    float value;
    boolean index;
    CSS.LengthUnit lu;
    
    FontSize() {}
    
    int getValue(AttributeSet paramAttributeSet, StyleSheet paramStyleSheet)
    {
      paramStyleSheet = CSS.this.getStyleSheet(paramStyleSheet);
      if (index) {
        return Math.round(getPointSize((int)value, paramStyleSheet));
      }
      if (lu == null) {
        return Math.round(value);
      }
      if (lu.type == 0)
      {
        boolean bool = paramStyleSheet == null ? false : paramStyleSheet.isW3CLengthUnits();
        return Math.round(lu.getValue(bool));
      }
      if (paramAttributeSet != null)
      {
        AttributeSet localAttributeSet = paramAttributeSet.getResolveParent();
        if (localAttributeSet != null)
        {
          int i = StyleConstants.getFontSize(localAttributeSet);
          float f;
          if ((lu.type == 1) || (lu.type == 3)) {
            f = lu.value * i;
          } else {
            f = lu.value + i;
          }
          return Math.round(f);
        }
      }
      return 12;
    }
    
    Object parseCssValue(String paramString)
    {
      FontSize localFontSize = new FontSize(CSS.this);
      svalue = paramString;
      try
      {
        if (paramString.equals("xx-small"))
        {
          value = 1.0F;
          index = true;
        }
        else if (paramString.equals("x-small"))
        {
          value = 2.0F;
          index = true;
        }
        else if (paramString.equals("small"))
        {
          value = 3.0F;
          index = true;
        }
        else if (paramString.equals("medium"))
        {
          value = 4.0F;
          index = true;
        }
        else if (paramString.equals("large"))
        {
          value = 5.0F;
          index = true;
        }
        else if (paramString.equals("x-large"))
        {
          value = 6.0F;
          index = true;
        }
        else if (paramString.equals("xx-large"))
        {
          value = 7.0F;
          index = true;
        }
        else
        {
          lu = new CSS.LengthUnit(paramString, (short)1, 1.0F);
        }
      }
      catch (NumberFormatException localNumberFormatException)
      {
        localFontSize = null;
      }
      return localFontSize;
    }
    
    Object parseHtmlValue(String paramString)
    {
      if ((paramString == null) || (paramString.length() == 0)) {
        return null;
      }
      FontSize localFontSize = new FontSize(CSS.this);
      svalue = paramString;
      try
      {
        int i = getBaseFontSize();
        int j;
        if (paramString.charAt(0) == '+')
        {
          j = Integer.valueOf(paramString.substring(1)).intValue();
          value = (i + j);
          index = true;
        }
        else if (paramString.charAt(0) == '-')
        {
          j = -Integer.valueOf(paramString.substring(1)).intValue();
          value = (i + j);
          index = true;
        }
        else
        {
          value = Integer.parseInt(paramString);
          if (value > 7.0F) {
            value = 7.0F;
          } else if (value < 0.0F) {
            value = 0.0F;
          }
          index = true;
        }
      }
      catch (NumberFormatException localNumberFormatException)
      {
        localFontSize = null;
      }
      return localFontSize;
    }
    
    Object fromStyleConstants(StyleConstants paramStyleConstants, Object paramObject)
    {
      if ((paramObject instanceof Number))
      {
        FontSize localFontSize = new FontSize(CSS.this);
        value = CSS.getIndexOfSize(((Number)paramObject).floatValue(), StyleSheet.sizeMapDefault);
        svalue = Integer.toString((int)value);
        index = true;
        return localFontSize;
      }
      return parseCssValue(paramObject.toString());
    }
    
    Object toStyleConstants(StyleConstants paramStyleConstants, View paramView)
    {
      if (paramView != null) {
        return Integer.valueOf(getValue(paramView.getAttributes(), null));
      }
      return Integer.valueOf(getValue(null, null));
    }
  }
  
  static class FontWeight
    extends CSS.CssValue
  {
    int weight;
    
    FontWeight() {}
    
    int getValue()
    {
      return weight;
    }
    
    Object parseCssValue(String paramString)
    {
      FontWeight localFontWeight = new FontWeight();
      svalue = paramString;
      if (paramString.equals("bold")) {
        weight = 700;
      } else if (paramString.equals("normal")) {
        weight = 400;
      } else {
        try
        {
          weight = Integer.parseInt(paramString);
        }
        catch (NumberFormatException localNumberFormatException)
        {
          localFontWeight = null;
        }
      }
      return localFontWeight;
    }
    
    Object fromStyleConstants(StyleConstants paramStyleConstants, Object paramObject)
    {
      if (paramObject.equals(Boolean.TRUE)) {
        return parseCssValue("bold");
      }
      return parseCssValue("normal");
    }
    
    Object toStyleConstants(StyleConstants paramStyleConstants, View paramView)
    {
      return weight > 500 ? Boolean.TRUE : Boolean.FALSE;
    }
    
    boolean isBold()
    {
      return weight > 500;
    }
  }
  
  static abstract interface LayoutIterator
  {
    public static final int WorstAdjustmentWeight = 2;
    
    public abstract void setOffset(int paramInt);
    
    public abstract int getOffset();
    
    public abstract void setSpan(int paramInt);
    
    public abstract int getSpan();
    
    public abstract int getCount();
    
    public abstract void setIndex(int paramInt);
    
    public abstract float getMinimumSpan(float paramFloat);
    
    public abstract float getPreferredSpan(float paramFloat);
    
    public abstract float getMaximumSpan(float paramFloat);
    
    public abstract int getAdjustmentWeight();
    
    public abstract float getBorderWidth();
    
    public abstract float getLeadingCollapseSpan();
    
    public abstract float getTrailingCollapseSpan();
  }
  
  static class LengthUnit
    implements Serializable
  {
    static Hashtable<String, Float> lengthMapping = new Hashtable(6);
    static Hashtable<String, Float> w3cLengthMapping = new Hashtable(6);
    short type;
    float value;
    String units = null;
    static final short UNINITALIZED_LENGTH = 10;
    
    LengthUnit(String paramString, short paramShort, float paramFloat)
    {
      parse(paramString, paramShort, paramFloat);
    }
    
    void parse(String paramString, short paramShort, float paramFloat)
    {
      type = paramShort;
      value = paramFloat;
      int i = paramString.length();
      if ((i > 0) && (paramString.charAt(i - 1) == '%')) {
        try
        {
          value = (Float.valueOf(paramString.substring(0, i - 1)).floatValue() / 100.0F);
          type = 1;
        }
        catch (NumberFormatException localNumberFormatException1) {}
      }
      if (i >= 2)
      {
        units = paramString.substring(i - 2, i);
        Float localFloat = (Float)lengthMapping.get(units);
        if (localFloat != null)
        {
          try
          {
            value = Float.valueOf(paramString.substring(0, i - 2)).floatValue();
            type = 0;
          }
          catch (NumberFormatException localNumberFormatException3) {}
        }
        else if ((units.equals("em")) || (units.equals("ex")))
        {
          try
          {
            value = Float.valueOf(paramString.substring(0, i - 2)).floatValue();
            type = 3;
          }
          catch (NumberFormatException localNumberFormatException4) {}
        }
        else if (paramString.equals("larger"))
        {
          value = 2.0F;
          type = 2;
        }
        else if (paramString.equals("smaller"))
        {
          value = -2.0F;
          type = 2;
        }
        else
        {
          try
          {
            value = Float.valueOf(paramString).floatValue();
            type = 0;
          }
          catch (NumberFormatException localNumberFormatException5) {}
        }
      }
      else if (i > 0)
      {
        try
        {
          value = Float.valueOf(paramString).floatValue();
          type = 0;
        }
        catch (NumberFormatException localNumberFormatException2) {}
      }
    }
    
    float getValue(boolean paramBoolean)
    {
      Hashtable localHashtable = paramBoolean ? w3cLengthMapping : lengthMapping;
      float f = 1.0F;
      if (units != null)
      {
        Float localFloat = (Float)localHashtable.get(units);
        if (localFloat != null) {
          f = localFloat.floatValue();
        }
      }
      return value * f;
    }
    
    static float getValue(float paramFloat, String paramString, Boolean paramBoolean)
    {
      Hashtable localHashtable = paramBoolean.booleanValue() ? w3cLengthMapping : lengthMapping;
      float f = 1.0F;
      if (paramString != null)
      {
        Float localFloat = (Float)localHashtable.get(paramString);
        if (localFloat != null) {
          f = localFloat.floatValue();
        }
      }
      return paramFloat * f;
    }
    
    public String toString()
    {
      return type + " " + value;
    }
    
    static
    {
      lengthMapping.put("pt", new Float(1.0F));
      lengthMapping.put("px", new Float(1.3F));
      lengthMapping.put("mm", new Float(2.83464F));
      lengthMapping.put("cm", new Float(28.3464F));
      lengthMapping.put("pc", new Float(12.0F));
      lengthMapping.put("in", new Float(72.0F));
      int i = 72;
      try
      {
        i = Toolkit.getDefaultToolkit().getScreenResolution();
      }
      catch (HeadlessException localHeadlessException) {}
      w3cLengthMapping.put("pt", new Float(i / 72.0F));
      w3cLengthMapping.put("px", new Float(1.0F));
      w3cLengthMapping.put("mm", new Float(i / 25.4F));
      w3cLengthMapping.put("cm", new Float(i / 2.54F));
      w3cLengthMapping.put("pc", new Float(i / 6.0F));
      w3cLengthMapping.put("in", new Float(i));
    }
  }
  
  static class LengthValue
    extends CSS.CssValue
  {
    boolean mayBeNegative;
    boolean percentage;
    float span;
    String units = null;
    
    LengthValue()
    {
      this(false);
    }
    
    LengthValue(boolean paramBoolean)
    {
      mayBeNegative = paramBoolean;
    }
    
    float getValue()
    {
      return getValue(false);
    }
    
    float getValue(boolean paramBoolean)
    {
      return getValue(0.0F, paramBoolean);
    }
    
    float getValue(float paramFloat)
    {
      return getValue(paramFloat, false);
    }
    
    float getValue(float paramFloat, boolean paramBoolean)
    {
      if (percentage) {
        return span * paramFloat;
      }
      return CSS.LengthUnit.getValue(span, units, Boolean.valueOf(paramBoolean));
    }
    
    boolean isPercentage()
    {
      return percentage;
    }
    
    Object parseCssValue(String paramString)
    {
      CSS.LengthUnit localLengthUnit;
      try
      {
        float f = Float.valueOf(paramString).floatValue();
        localLengthValue = new LengthValue();
        span = f;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        localLengthUnit = new CSS.LengthUnit(paramString, (short)10, 0.0F);
        switch (type)
        {
        case 0: 
          localLengthValue = new LengthValue();
          span = (mayBeNegative ? value : Math.max(0.0F, value));
          units = units;
        }
      }
      LengthValue localLengthValue = new LengthValue();
      span = Math.max(0.0F, Math.min(1.0F, value));
      percentage = true;
      break label151;
      return null;
      label151:
      svalue = paramString;
      return localLengthValue;
    }
    
    Object parseHtmlValue(String paramString)
    {
      if (paramString.equals("#DEFAULT")) {
        paramString = "1";
      }
      return parseCssValue(paramString);
    }
    
    Object fromStyleConstants(StyleConstants paramStyleConstants, Object paramObject)
    {
      LengthValue localLengthValue = new LengthValue();
      svalue = paramObject.toString();
      span = ((Float)paramObject).floatValue();
      return localLengthValue;
    }
    
    Object toStyleConstants(StyleConstants paramStyleConstants, View paramView)
    {
      return new Float(getValue(false));
    }
  }
  
  static class ShorthandBackgroundParser
  {
    ShorthandBackgroundParser() {}
    
    static void parseShorthandBackground(CSS paramCSS, String paramString, MutableAttributeSet paramMutableAttributeSet)
    {
      String[] arrayOfString = CSS.parseStrings(paramString);
      int i = arrayOfString.length;
      int j = 0;
      int k = 0;
      while (j < i)
      {
        String str = arrayOfString[(j++)];
        if (((k & 0x1) == 0) && (isImage(str)))
        {
          paramCSS.addInternalCSSValue(paramMutableAttributeSet, CSS.Attribute.BACKGROUND_IMAGE, str);
          k = (short)(k | 0x1);
        }
        else if (((k & 0x2) == 0) && (isRepeat(str)))
        {
          paramCSS.addInternalCSSValue(paramMutableAttributeSet, CSS.Attribute.BACKGROUND_REPEAT, str);
          k = (short)(k | 0x2);
        }
        else if (((k & 0x4) == 0) && (isAttachment(str)))
        {
          paramCSS.addInternalCSSValue(paramMutableAttributeSet, CSS.Attribute.BACKGROUND_ATTACHMENT, str);
          k = (short)(k | 0x4);
        }
        else if (((k & 0x8) == 0) && (isPosition(str)))
        {
          if ((j < i) && (isPosition(arrayOfString[j]))) {
            paramCSS.addInternalCSSValue(paramMutableAttributeSet, CSS.Attribute.BACKGROUND_POSITION, str + " " + arrayOfString[(j++)]);
          } else {
            paramCSS.addInternalCSSValue(paramMutableAttributeSet, CSS.Attribute.BACKGROUND_POSITION, str);
          }
          k = (short)(k | 0x8);
        }
        else if (((k & 0x10) == 0) && (isColor(str)))
        {
          paramCSS.addInternalCSSValue(paramMutableAttributeSet, CSS.Attribute.BACKGROUND_COLOR, str);
          k = (short)(k | 0x10);
        }
      }
      if ((k & 0x1) == 0) {
        paramCSS.addInternalCSSValue(paramMutableAttributeSet, CSS.Attribute.BACKGROUND_IMAGE, null);
      }
      if ((k & 0x2) == 0) {
        paramCSS.addInternalCSSValue(paramMutableAttributeSet, CSS.Attribute.BACKGROUND_REPEAT, "repeat");
      }
      if ((k & 0x4) == 0) {
        paramCSS.addInternalCSSValue(paramMutableAttributeSet, CSS.Attribute.BACKGROUND_ATTACHMENT, "scroll");
      }
      if ((k & 0x8) == 0) {
        paramCSS.addInternalCSSValue(paramMutableAttributeSet, CSS.Attribute.BACKGROUND_POSITION, null);
      }
    }
    
    static boolean isImage(String paramString)
    {
      return (paramString.startsWith("url(")) && (paramString.endsWith(")"));
    }
    
    static boolean isRepeat(String paramString)
    {
      return (paramString.equals("repeat-x")) || (paramString.equals("repeat-y")) || (paramString.equals("repeat")) || (paramString.equals("no-repeat"));
    }
    
    static boolean isAttachment(String paramString)
    {
      return (paramString.equals("fixed")) || (paramString.equals("scroll"));
    }
    
    static boolean isPosition(String paramString)
    {
      return (paramString.equals("top")) || (paramString.equals("bottom")) || (paramString.equals("left")) || (paramString.equals("right")) || (paramString.equals("center")) || ((paramString.length() > 0) && (Character.isDigit(paramString.charAt(0))));
    }
    
    static boolean isColor(String paramString)
    {
      return CSS.stringToColor(paramString) != null;
    }
  }
  
  static class ShorthandBorderParser
  {
    static CSS.Attribute[] keys = { CSS.Attribute.BORDER_TOP, CSS.Attribute.BORDER_RIGHT, CSS.Attribute.BORDER_BOTTOM, CSS.Attribute.BORDER_LEFT };
    
    ShorthandBorderParser() {}
    
    static void parseShorthandBorder(MutableAttributeSet paramMutableAttributeSet, CSS.Attribute paramAttribute, String paramString)
    {
      Object[] arrayOfObject = new Object[CSSBorder.PARSERS.length];
      String[] arrayOfString1 = CSS.parseStrings(paramString);
      for (String str : arrayOfString1)
      {
        int m = 0;
        for (int n = 0; n < arrayOfObject.length; n++)
        {
          Object localObject = CSSBorder.PARSERS[n].parseCssValue(str);
          if (localObject != null)
          {
            if (arrayOfObject[n] != null) {
              break;
            }
            arrayOfObject[n] = localObject;
            m = 1;
            break;
          }
        }
        if (m == 0) {
          return;
        }
      }
      for (int i = 0; i < arrayOfObject.length; i++) {
        if (arrayOfObject[i] == null) {
          arrayOfObject[i] = CSSBorder.DEFAULTS[i];
        }
      }
      for (i = 0; i < keys.length; i++) {
        if ((paramAttribute == CSS.Attribute.BORDER) || (paramAttribute == keys[i])) {
          for (??? = 0; ??? < arrayOfObject.length; ???++) {
            paramMutableAttributeSet.addAttribute(CSSBorder.ATTRIBUTES[???][i], arrayOfObject[???]);
          }
        }
      }
    }
  }
  
  static class ShorthandFontParser
  {
    ShorthandFontParser() {}
    
    static void parseShorthandFont(CSS paramCSS, String paramString, MutableAttributeSet paramMutableAttributeSet)
    {
      String[] arrayOfString = CSS.parseStrings(paramString);
      int i = arrayOfString.length;
      int j = 0;
      int k = 0;
      int m = Math.min(3, i);
      while (j < m) {
        if (((k & 0x1) == 0) && (isFontStyle(arrayOfString[j])))
        {
          paramCSS.addInternalCSSValue(paramMutableAttributeSet, CSS.Attribute.FONT_STYLE, arrayOfString[(j++)]);
          k = (short)(k | 0x1);
        }
        else if (((k & 0x2) == 0) && (isFontVariant(arrayOfString[j])))
        {
          paramCSS.addInternalCSSValue(paramMutableAttributeSet, CSS.Attribute.FONT_VARIANT, arrayOfString[(j++)]);
          k = (short)(k | 0x2);
        }
        else if (((k & 0x4) == 0) && (isFontWeight(arrayOfString[j])))
        {
          paramCSS.addInternalCSSValue(paramMutableAttributeSet, CSS.Attribute.FONT_WEIGHT, arrayOfString[(j++)]);
          k = (short)(k | 0x4);
        }
        else
        {
          if (!arrayOfString[j].equals("normal")) {
            break;
          }
          j++;
        }
      }
      if ((k & 0x1) == 0) {
        paramCSS.addInternalCSSValue(paramMutableAttributeSet, CSS.Attribute.FONT_STYLE, "normal");
      }
      if ((k & 0x2) == 0) {
        paramCSS.addInternalCSSValue(paramMutableAttributeSet, CSS.Attribute.FONT_VARIANT, "normal");
      }
      if ((k & 0x4) == 0) {
        paramCSS.addInternalCSSValue(paramMutableAttributeSet, CSS.Attribute.FONT_WEIGHT, "normal");
      }
      String str;
      if (j < i)
      {
        str = arrayOfString[j];
        int n = str.indexOf('/');
        if (n != -1)
        {
          str = str.substring(0, n);
          arrayOfString[j] = arrayOfString[j].substring(n);
        }
        else
        {
          j++;
        }
        paramCSS.addInternalCSSValue(paramMutableAttributeSet, CSS.Attribute.FONT_SIZE, str);
      }
      else
      {
        paramCSS.addInternalCSSValue(paramMutableAttributeSet, CSS.Attribute.FONT_SIZE, "medium");
      }
      if ((j < i) && (arrayOfString[j].startsWith("/")))
      {
        str = null;
        if (arrayOfString[j].equals("/"))
        {
          j++;
          if (j < i) {
            str = arrayOfString[(j++)];
          }
        }
        else
        {
          str = arrayOfString[(j++)].substring(1);
        }
        if (str != null) {
          paramCSS.addInternalCSSValue(paramMutableAttributeSet, CSS.Attribute.LINE_HEIGHT, str);
        } else {
          paramCSS.addInternalCSSValue(paramMutableAttributeSet, CSS.Attribute.LINE_HEIGHT, "normal");
        }
      }
      else
      {
        paramCSS.addInternalCSSValue(paramMutableAttributeSet, CSS.Attribute.LINE_HEIGHT, "normal");
      }
      if (j < i)
      {
        for (str = arrayOfString[(j++)]; j < i; str = str + " " + arrayOfString[(j++)]) {}
        paramCSS.addInternalCSSValue(paramMutableAttributeSet, CSS.Attribute.FONT_FAMILY, str);
      }
      else
      {
        paramCSS.addInternalCSSValue(paramMutableAttributeSet, CSS.Attribute.FONT_FAMILY, "SansSerif");
      }
    }
    
    private static boolean isFontStyle(String paramString)
    {
      return (paramString.equals("italic")) || (paramString.equals("oblique"));
    }
    
    private static boolean isFontVariant(String paramString)
    {
      return paramString.equals("small-caps");
    }
    
    private static boolean isFontWeight(String paramString)
    {
      if ((paramString.equals("bold")) || (paramString.equals("bolder")) || (paramString.equals("italic")) || (paramString.equals("lighter"))) {
        return true;
      }
      return (paramString.length() == 3) && (paramString.charAt(0) >= '1') && (paramString.charAt(0) <= '9') && (paramString.charAt(1) == '0') && (paramString.charAt(2) == '0');
    }
  }
  
  static class ShorthandMarginParser
  {
    ShorthandMarginParser() {}
    
    static void parseShorthandMargin(CSS paramCSS, String paramString, MutableAttributeSet paramMutableAttributeSet, CSS.Attribute[] paramArrayOfAttribute)
    {
      String[] arrayOfString = CSS.parseStrings(paramString);
      int i = arrayOfString.length;
      int j = 0;
      int k;
      switch (i)
      {
      case 0: 
        
      case 1: 
        for (k = 0; k < 4; k++) {
          paramCSS.addInternalCSSValue(paramMutableAttributeSet, paramArrayOfAttribute[k], arrayOfString[0]);
        }
        break;
      case 2: 
        paramCSS.addInternalCSSValue(paramMutableAttributeSet, paramArrayOfAttribute[0], arrayOfString[0]);
        paramCSS.addInternalCSSValue(paramMutableAttributeSet, paramArrayOfAttribute[2], arrayOfString[0]);
        paramCSS.addInternalCSSValue(paramMutableAttributeSet, paramArrayOfAttribute[1], arrayOfString[1]);
        paramCSS.addInternalCSSValue(paramMutableAttributeSet, paramArrayOfAttribute[3], arrayOfString[1]);
        break;
      case 3: 
        paramCSS.addInternalCSSValue(paramMutableAttributeSet, paramArrayOfAttribute[0], arrayOfString[0]);
        paramCSS.addInternalCSSValue(paramMutableAttributeSet, paramArrayOfAttribute[1], arrayOfString[1]);
        paramCSS.addInternalCSSValue(paramMutableAttributeSet, paramArrayOfAttribute[2], arrayOfString[2]);
        paramCSS.addInternalCSSValue(paramMutableAttributeSet, paramArrayOfAttribute[3], arrayOfString[1]);
        break;
      default: 
        for (k = 0; k < 4; k++) {
          paramCSS.addInternalCSSValue(paramMutableAttributeSet, paramArrayOfAttribute[k], arrayOfString[k]);
        }
      }
    }
  }
  
  static class StringValue
    extends CSS.CssValue
  {
    StringValue() {}
    
    Object parseCssValue(String paramString)
    {
      StringValue localStringValue = new StringValue();
      svalue = paramString;
      return localStringValue;
    }
    
    Object fromStyleConstants(StyleConstants paramStyleConstants, Object paramObject)
    {
      if (paramStyleConstants == StyleConstants.Italic)
      {
        if (paramObject.equals(Boolean.TRUE)) {
          return parseCssValue("italic");
        }
        return parseCssValue("");
      }
      if (paramStyleConstants == StyleConstants.Underline)
      {
        if (paramObject.equals(Boolean.TRUE)) {
          return parseCssValue("underline");
        }
        return parseCssValue("");
      }
      if (paramStyleConstants == StyleConstants.Alignment)
      {
        int i = ((Integer)paramObject).intValue();
        String str;
        switch (i)
        {
        case 0: 
          str = "left";
          break;
        case 2: 
          str = "right";
          break;
        case 1: 
          str = "center";
          break;
        case 3: 
          str = "justify";
          break;
        default: 
          str = "left";
        }
        return parseCssValue(str);
      }
      if (paramStyleConstants == StyleConstants.StrikeThrough)
      {
        if (paramObject.equals(Boolean.TRUE)) {
          return parseCssValue("line-through");
        }
        return parseCssValue("");
      }
      if (paramStyleConstants == StyleConstants.Superscript)
      {
        if (paramObject.equals(Boolean.TRUE)) {
          return parseCssValue("super");
        }
        return parseCssValue("");
      }
      if (paramStyleConstants == StyleConstants.Subscript)
      {
        if (paramObject.equals(Boolean.TRUE)) {
          return parseCssValue("sub");
        }
        return parseCssValue("");
      }
      return null;
    }
    
    Object toStyleConstants(StyleConstants paramStyleConstants, View paramView)
    {
      if (paramStyleConstants == StyleConstants.Italic)
      {
        if (svalue.indexOf("italic") >= 0) {
          return Boolean.TRUE;
        }
        return Boolean.FALSE;
      }
      if (paramStyleConstants == StyleConstants.Underline)
      {
        if (svalue.indexOf("underline") >= 0) {
          return Boolean.TRUE;
        }
        return Boolean.FALSE;
      }
      if (paramStyleConstants == StyleConstants.Alignment)
      {
        if (svalue.equals("right")) {
          return new Integer(2);
        }
        if (svalue.equals("center")) {
          return new Integer(1);
        }
        if (svalue.equals("justify")) {
          return new Integer(3);
        }
        return new Integer(0);
      }
      if (paramStyleConstants == StyleConstants.StrikeThrough)
      {
        if (svalue.indexOf("line-through") >= 0) {
          return Boolean.TRUE;
        }
        return Boolean.FALSE;
      }
      if (paramStyleConstants == StyleConstants.Superscript)
      {
        if (svalue.indexOf("super") >= 0) {
          return Boolean.TRUE;
        }
        return Boolean.FALSE;
      }
      if (paramStyleConstants == StyleConstants.Subscript)
      {
        if (svalue.indexOf("sub") >= 0) {
          return Boolean.TRUE;
        }
        return Boolean.FALSE;
      }
      return null;
    }
    
    boolean isItalic()
    {
      return svalue.indexOf("italic") != -1;
    }
    
    boolean isStrike()
    {
      return svalue.indexOf("line-through") != -1;
    }
    
    boolean isUnderline()
    {
      return svalue.indexOf("underline") != -1;
    }
    
    boolean isSub()
    {
      return svalue.indexOf("sub") != -1;
    }
    
    boolean isSup()
    {
      return svalue.indexOf("sup") != -1;
    }
  }
  
  static final class Value
  {
    static final Value INHERITED = new Value("inherited");
    static final Value NONE = new Value("none");
    static final Value HIDDEN = new Value("hidden");
    static final Value DOTTED = new Value("dotted");
    static final Value DASHED = new Value("dashed");
    static final Value SOLID = new Value("solid");
    static final Value DOUBLE = new Value("double");
    static final Value GROOVE = new Value("groove");
    static final Value RIDGE = new Value("ridge");
    static final Value INSET = new Value("inset");
    static final Value OUTSET = new Value("outset");
    static final Value DISC = new Value("disc");
    static final Value CIRCLE = new Value("circle");
    static final Value SQUARE = new Value("square");
    static final Value DECIMAL = new Value("decimal");
    static final Value LOWER_ROMAN = new Value("lower-roman");
    static final Value UPPER_ROMAN = new Value("upper-roman");
    static final Value LOWER_ALPHA = new Value("lower-alpha");
    static final Value UPPER_ALPHA = new Value("upper-alpha");
    static final Value BACKGROUND_NO_REPEAT = new Value("no-repeat");
    static final Value BACKGROUND_REPEAT = new Value("repeat");
    static final Value BACKGROUND_REPEAT_X = new Value("repeat-x");
    static final Value BACKGROUND_REPEAT_Y = new Value("repeat-y");
    static final Value BACKGROUND_SCROLL = new Value("scroll");
    static final Value BACKGROUND_FIXED = new Value("fixed");
    private String name;
    static final Value[] allValues = { INHERITED, NONE, DOTTED, DASHED, SOLID, DOUBLE, GROOVE, RIDGE, INSET, OUTSET, DISC, CIRCLE, SQUARE, DECIMAL, LOWER_ROMAN, UPPER_ROMAN, LOWER_ALPHA, UPPER_ALPHA, BACKGROUND_NO_REPEAT, BACKGROUND_REPEAT, BACKGROUND_REPEAT_X, BACKGROUND_REPEAT_Y, BACKGROUND_FIXED, BACKGROUND_FIXED };
    
    private Value(String paramString)
    {
      name = paramString;
    }
    
    public String toString()
    {
      return name;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\CSS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */