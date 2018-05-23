package java.awt.font;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.im.InputMethodHighlight;
import java.text.Annotation;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import sun.font.Decoration;
import sun.font.FontResolver;
import sun.text.CodePointIterator;

final class StyledParagraph
{
  private int length;
  private Decoration decoration;
  private Object font;
  private Vector<Decoration> decorations;
  int[] decorationStarts;
  private Vector<Object> fonts;
  int[] fontStarts;
  private static int INITIAL_SIZE = 8;
  
  public StyledParagraph(AttributedCharacterIterator paramAttributedCharacterIterator, char[] paramArrayOfChar)
  {
    int i = paramAttributedCharacterIterator.getBeginIndex();
    int j = paramAttributedCharacterIterator.getEndIndex();
    length = (j - i);
    int k = i;
    paramAttributedCharacterIterator.first();
    do
    {
      int m = paramAttributedCharacterIterator.getRunLimit();
      int n = k - i;
      Map localMap = paramAttributedCharacterIterator.getAttributes();
      localMap = addInputMethodAttrs(localMap);
      Decoration localDecoration = Decoration.getDecoration(localMap);
      addDecoration(localDecoration, n);
      Object localObject = getGraphicOrFont(localMap);
      if (localObject == null) {
        addFonts(paramArrayOfChar, localMap, n, m - i);
      } else {
        addFont(localObject, n);
      }
      paramAttributedCharacterIterator.setIndex(m);
      k = m;
    } while (k < j);
    if (decorations != null) {
      decorationStarts = addToVector(this, length, decorations, decorationStarts);
    }
    if (fonts != null) {
      fontStarts = addToVector(this, length, fonts, fontStarts);
    }
  }
  
  private static void insertInto(int paramInt1, int[] paramArrayOfInt, int paramInt2)
  {
    while (paramArrayOfInt[(--paramInt2)] > paramInt1) {
      paramArrayOfInt[paramInt2] += 1;
    }
  }
  
  public static StyledParagraph insertChar(AttributedCharacterIterator paramAttributedCharacterIterator, char[] paramArrayOfChar, int paramInt, StyledParagraph paramStyledParagraph)
  {
    char c = paramAttributedCharacterIterator.setIndex(paramInt);
    int i = Math.max(paramInt - paramAttributedCharacterIterator.getBeginIndex() - 1, 0);
    Map localMap = addInputMethodAttrs(paramAttributedCharacterIterator.getAttributes());
    Decoration localDecoration = Decoration.getDecoration(localMap);
    if (!paramStyledParagraph.getDecorationAt(i).equals(localDecoration)) {
      return new StyledParagraph(paramAttributedCharacterIterator, paramArrayOfChar);
    }
    Object localObject = getGraphicOrFont(localMap);
    if (localObject == null)
    {
      FontResolver localFontResolver = FontResolver.getInstance();
      int j = localFontResolver.getFontIndex(c);
      localObject = localFontResolver.getFont(j, localMap);
    }
    if (!paramStyledParagraph.getFontOrGraphicAt(i).equals(localObject)) {
      return new StyledParagraph(paramAttributedCharacterIterator, paramArrayOfChar);
    }
    length += 1;
    if (decorations != null) {
      insertInto(i, decorationStarts, decorations.size());
    }
    if (fonts != null) {
      insertInto(i, fontStarts, fonts.size());
    }
    return paramStyledParagraph;
  }
  
  private static void deleteFrom(int paramInt1, int[] paramArrayOfInt, int paramInt2)
  {
    while (paramArrayOfInt[(--paramInt2)] > paramInt1) {
      paramArrayOfInt[paramInt2] -= 1;
    }
  }
  
  public static StyledParagraph deleteChar(AttributedCharacterIterator paramAttributedCharacterIterator, char[] paramArrayOfChar, int paramInt, StyledParagraph paramStyledParagraph)
  {
    paramInt -= paramAttributedCharacterIterator.getBeginIndex();
    if ((decorations == null) && (fonts == null))
    {
      length -= 1;
      return paramStyledParagraph;
    }
    if ((paramStyledParagraph.getRunLimit(paramInt) == paramInt + 1) && ((paramInt == 0) || (paramStyledParagraph.getRunLimit(paramInt - 1) == paramInt))) {
      return new StyledParagraph(paramAttributedCharacterIterator, paramArrayOfChar);
    }
    length -= 1;
    if (decorations != null) {
      deleteFrom(paramInt, decorationStarts, decorations.size());
    }
    if (fonts != null) {
      deleteFrom(paramInt, fontStarts, fonts.size());
    }
    return paramStyledParagraph;
  }
  
  public int getRunLimit(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= length)) {
      throw new IllegalArgumentException("index out of range");
    }
    int i = length;
    if (decorations != null)
    {
      j = findRunContaining(paramInt, decorationStarts);
      i = decorationStarts[(j + 1)];
    }
    int j = length;
    if (fonts != null)
    {
      int k = findRunContaining(paramInt, fontStarts);
      j = fontStarts[(k + 1)];
    }
    return Math.min(i, j);
  }
  
  public Decoration getDecorationAt(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= length)) {
      throw new IllegalArgumentException("index out of range");
    }
    if (decorations == null) {
      return decoration;
    }
    int i = findRunContaining(paramInt, decorationStarts);
    return (Decoration)decorations.elementAt(i);
  }
  
  public Object getFontOrGraphicAt(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= length)) {
      throw new IllegalArgumentException("index out of range");
    }
    if (fonts == null) {
      return font;
    }
    int i = findRunContaining(paramInt, fontStarts);
    return fonts.elementAt(i);
  }
  
  private static int findRunContaining(int paramInt, int[] paramArrayOfInt)
  {
    for (int i = 1;; i++) {
      if (paramArrayOfInt[i] > paramInt) {
        return i - 1;
      }
    }
  }
  
  private static int[] addToVector(Object paramObject, int paramInt, Vector paramVector, int[] paramArrayOfInt)
  {
    if (!paramVector.lastElement().equals(paramObject))
    {
      paramVector.addElement(paramObject);
      int i = paramVector.size();
      if (paramArrayOfInt.length == i)
      {
        int[] arrayOfInt = new int[paramArrayOfInt.length * 2];
        System.arraycopy(paramArrayOfInt, 0, arrayOfInt, 0, paramArrayOfInt.length);
        paramArrayOfInt = arrayOfInt;
      }
      paramArrayOfInt[(i - 1)] = paramInt;
    }
    return paramArrayOfInt;
  }
  
  private void addDecoration(Decoration paramDecoration, int paramInt)
  {
    if (decorations != null)
    {
      decorationStarts = addToVector(paramDecoration, paramInt, decorations, decorationStarts);
    }
    else if (decoration == null)
    {
      decoration = paramDecoration;
    }
    else if (!decoration.equals(paramDecoration))
    {
      decorations = new Vector(INITIAL_SIZE);
      decorations.addElement(decoration);
      decorations.addElement(paramDecoration);
      decorationStarts = new int[INITIAL_SIZE];
      decorationStarts[0] = 0;
      decorationStarts[1] = paramInt;
    }
  }
  
  private void addFont(Object paramObject, int paramInt)
  {
    if (fonts != null)
    {
      fontStarts = addToVector(paramObject, paramInt, fonts, fontStarts);
    }
    else if (font == null)
    {
      font = paramObject;
    }
    else if (!font.equals(paramObject))
    {
      fonts = new Vector(INITIAL_SIZE);
      fonts.addElement(font);
      fonts.addElement(paramObject);
      fontStarts = new int[INITIAL_SIZE];
      fontStarts[0] = 0;
      fontStarts[1] = paramInt;
    }
  }
  
  private void addFonts(char[] paramArrayOfChar, Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap, int paramInt1, int paramInt2)
  {
    FontResolver localFontResolver = FontResolver.getInstance();
    CodePointIterator localCodePointIterator = CodePointIterator.create(paramArrayOfChar, paramInt1, paramInt2);
    for (int i = localCodePointIterator.charIndex(); i < paramInt2; i = localCodePointIterator.charIndex())
    {
      int j = localFontResolver.nextFontRunIndex(localCodePointIterator);
      addFont(localFontResolver.getFont(j, paramMap), i);
    }
  }
  
  static Map<? extends AttributedCharacterIterator.Attribute, ?> addInputMethodAttrs(Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap)
  {
    Object localObject1 = paramMap.get(TextAttribute.INPUT_METHOD_HIGHLIGHT);
    try
    {
      if (localObject1 != null)
      {
        if ((localObject1 instanceof Annotation)) {
          localObject1 = ((Annotation)localObject1).getValue();
        }
        InputMethodHighlight localInputMethodHighlight = (InputMethodHighlight)localObject1;
        Map localMap = null;
        try
        {
          localMap = localInputMethodHighlight.getStyle();
        }
        catch (NoSuchMethodError localNoSuchMethodError) {}
        Object localObject2;
        if (localMap == null)
        {
          localObject2 = Toolkit.getDefaultToolkit();
          localMap = ((Toolkit)localObject2).mapInputMethodHighlight(localInputMethodHighlight);
        }
        if (localMap != null)
        {
          localObject2 = new HashMap(5, 0.9F);
          ((HashMap)localObject2).putAll(paramMap);
          ((HashMap)localObject2).putAll(localMap);
          return (Map<? extends AttributedCharacterIterator.Attribute, ?>)localObject2;
        }
      }
    }
    catch (ClassCastException localClassCastException) {}
    return paramMap;
  }
  
  private static Object getGraphicOrFont(Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap)
  {
    Object localObject = paramMap.get(TextAttribute.CHAR_REPLACEMENT);
    if (localObject != null) {
      return localObject;
    }
    localObject = paramMap.get(TextAttribute.FONT);
    if (localObject != null) {
      return localObject;
    }
    if (paramMap.get(TextAttribute.FAMILY) != null) {
      return Font.getFont(paramMap);
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\font\StyledParagraph.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */