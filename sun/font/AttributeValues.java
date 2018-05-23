package sun.font;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Toolkit;
import java.awt.font.GraphicAttribute;
import java.awt.font.NumericShaper;
import java.awt.font.TextAttribute;
import java.awt.font.TransformAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D.Double;
import java.awt.im.InputMethodHighlight;
import java.io.Serializable;
import java.text.Annotation;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class AttributeValues
  implements Cloneable
{
  private int defined;
  private int nondefault;
  private String family = "Default";
  private float weight = 1.0F;
  private float width = 1.0F;
  private float posture;
  private float size = 12.0F;
  private float tracking;
  private NumericShaper numericShaping;
  private AffineTransform transform;
  private GraphicAttribute charReplacement;
  private Paint foreground;
  private Paint background;
  private float justification = 1.0F;
  private Object imHighlight;
  private Font font;
  private byte imUnderline = -1;
  private byte superscript;
  private byte underline = -1;
  private byte runDirection = -2;
  private byte bidiEmbedding;
  private byte kerning;
  private byte ligatures;
  private boolean strikethrough;
  private boolean swapColors;
  private AffineTransform baselineTransform;
  private AffineTransform charTransform;
  private static final AttributeValues DEFAULT = new AttributeValues();
  public static final int MASK_ALL = getMask((EAttribute[])EAttribute.class.getEnumConstants());
  private static final String DEFINED_KEY = "sun.font.attributevalues.defined_key";
  
  public AttributeValues() {}
  
  public String getFamily()
  {
    return family;
  }
  
  public void setFamily(String paramString)
  {
    family = paramString;
    update(EAttribute.EFAMILY);
  }
  
  public float getWeight()
  {
    return weight;
  }
  
  public void setWeight(float paramFloat)
  {
    weight = paramFloat;
    update(EAttribute.EWEIGHT);
  }
  
  public float getWidth()
  {
    return width;
  }
  
  public void setWidth(float paramFloat)
  {
    width = paramFloat;
    update(EAttribute.EWIDTH);
  }
  
  public float getPosture()
  {
    return posture;
  }
  
  public void setPosture(float paramFloat)
  {
    posture = paramFloat;
    update(EAttribute.EPOSTURE);
  }
  
  public float getSize()
  {
    return size;
  }
  
  public void setSize(float paramFloat)
  {
    size = paramFloat;
    update(EAttribute.ESIZE);
  }
  
  public AffineTransform getTransform()
  {
    return transform;
  }
  
  public void setTransform(AffineTransform paramAffineTransform)
  {
    transform = ((paramAffineTransform == null) || (paramAffineTransform.isIdentity()) ? DEFAULTtransform : new AffineTransform(paramAffineTransform));
    updateDerivedTransforms();
    update(EAttribute.ETRANSFORM);
  }
  
  public void setTransform(TransformAttribute paramTransformAttribute)
  {
    transform = ((paramTransformAttribute == null) || (paramTransformAttribute.isIdentity()) ? DEFAULTtransform : paramTransformAttribute.getTransform());
    updateDerivedTransforms();
    update(EAttribute.ETRANSFORM);
  }
  
  public int getSuperscript()
  {
    return superscript;
  }
  
  public void setSuperscript(int paramInt)
  {
    superscript = ((byte)paramInt);
    update(EAttribute.ESUPERSCRIPT);
  }
  
  public Font getFont()
  {
    return font;
  }
  
  public void setFont(Font paramFont)
  {
    font = paramFont;
    update(EAttribute.EFONT);
  }
  
  public GraphicAttribute getCharReplacement()
  {
    return charReplacement;
  }
  
  public void setCharReplacement(GraphicAttribute paramGraphicAttribute)
  {
    charReplacement = paramGraphicAttribute;
    update(EAttribute.ECHAR_REPLACEMENT);
  }
  
  public Paint getForeground()
  {
    return foreground;
  }
  
  public void setForeground(Paint paramPaint)
  {
    foreground = paramPaint;
    update(EAttribute.EFOREGROUND);
  }
  
  public Paint getBackground()
  {
    return background;
  }
  
  public void setBackground(Paint paramPaint)
  {
    background = paramPaint;
    update(EAttribute.EBACKGROUND);
  }
  
  public int getUnderline()
  {
    return underline;
  }
  
  public void setUnderline(int paramInt)
  {
    underline = ((byte)paramInt);
    update(EAttribute.EUNDERLINE);
  }
  
  public boolean getStrikethrough()
  {
    return strikethrough;
  }
  
  public void setStrikethrough(boolean paramBoolean)
  {
    strikethrough = paramBoolean;
    update(EAttribute.ESTRIKETHROUGH);
  }
  
  public int getRunDirection()
  {
    return runDirection;
  }
  
  public void setRunDirection(int paramInt)
  {
    runDirection = ((byte)paramInt);
    update(EAttribute.ERUN_DIRECTION);
  }
  
  public int getBidiEmbedding()
  {
    return bidiEmbedding;
  }
  
  public void setBidiEmbedding(int paramInt)
  {
    bidiEmbedding = ((byte)paramInt);
    update(EAttribute.EBIDI_EMBEDDING);
  }
  
  public float getJustification()
  {
    return justification;
  }
  
  public void setJustification(float paramFloat)
  {
    justification = paramFloat;
    update(EAttribute.EJUSTIFICATION);
  }
  
  public Object getInputMethodHighlight()
  {
    return imHighlight;
  }
  
  public void setInputMethodHighlight(Annotation paramAnnotation)
  {
    imHighlight = paramAnnotation;
    update(EAttribute.EINPUT_METHOD_HIGHLIGHT);
  }
  
  public void setInputMethodHighlight(InputMethodHighlight paramInputMethodHighlight)
  {
    imHighlight = paramInputMethodHighlight;
    update(EAttribute.EINPUT_METHOD_HIGHLIGHT);
  }
  
  public int getInputMethodUnderline()
  {
    return imUnderline;
  }
  
  public void setInputMethodUnderline(int paramInt)
  {
    imUnderline = ((byte)paramInt);
    update(EAttribute.EINPUT_METHOD_UNDERLINE);
  }
  
  public boolean getSwapColors()
  {
    return swapColors;
  }
  
  public void setSwapColors(boolean paramBoolean)
  {
    swapColors = paramBoolean;
    update(EAttribute.ESWAP_COLORS);
  }
  
  public NumericShaper getNumericShaping()
  {
    return numericShaping;
  }
  
  public void setNumericShaping(NumericShaper paramNumericShaper)
  {
    numericShaping = paramNumericShaper;
    update(EAttribute.ENUMERIC_SHAPING);
  }
  
  public int getKerning()
  {
    return kerning;
  }
  
  public void setKerning(int paramInt)
  {
    kerning = ((byte)paramInt);
    update(EAttribute.EKERNING);
  }
  
  public float getTracking()
  {
    return tracking;
  }
  
  public void setTracking(float paramFloat)
  {
    tracking = ((byte)(int)paramFloat);
    update(EAttribute.ETRACKING);
  }
  
  public int getLigatures()
  {
    return ligatures;
  }
  
  public void setLigatures(int paramInt)
  {
    ligatures = ((byte)paramInt);
    update(EAttribute.ELIGATURES);
  }
  
  public AffineTransform getBaselineTransform()
  {
    return baselineTransform;
  }
  
  public AffineTransform getCharTransform()
  {
    return charTransform;
  }
  
  public static int getMask(EAttribute paramEAttribute)
  {
    return mask;
  }
  
  public static int getMask(EAttribute... paramVarArgs)
  {
    int i = 0;
    for (EAttribute localEAttribute : paramVarArgs) {
      i |= mask;
    }
    return i;
  }
  
  public void unsetDefault()
  {
    defined &= nondefault;
  }
  
  public void defineAll(int paramInt)
  {
    defined |= paramInt;
    if ((defined & EBASELINE_TRANSFORMmask) != 0) {
      throw new InternalError("can't define derived attribute");
    }
  }
  
  public boolean allDefined(int paramInt)
  {
    return (defined & paramInt) == paramInt;
  }
  
  public boolean anyDefined(int paramInt)
  {
    return (defined & paramInt) != 0;
  }
  
  public boolean anyNonDefault(int paramInt)
  {
    return (nondefault & paramInt) != 0;
  }
  
  public boolean isDefined(EAttribute paramEAttribute)
  {
    return (defined & mask) != 0;
  }
  
  public boolean isNonDefault(EAttribute paramEAttribute)
  {
    return (nondefault & mask) != 0;
  }
  
  public void setDefault(EAttribute paramEAttribute)
  {
    if (att == null) {
      throw new InternalError("can't set default derived attribute: " + paramEAttribute);
    }
    i_set(paramEAttribute, DEFAULT);
    defined |= mask;
    nondefault &= (mask ^ 0xFFFFFFFF);
  }
  
  public void unset(EAttribute paramEAttribute)
  {
    if (att == null) {
      throw new InternalError("can't unset derived attribute: " + paramEAttribute);
    }
    i_set(paramEAttribute, DEFAULT);
    defined &= (mask ^ 0xFFFFFFFF);
    nondefault &= (mask ^ 0xFFFFFFFF);
  }
  
  public void set(EAttribute paramEAttribute, AttributeValues paramAttributeValues)
  {
    if (att == null) {
      throw new InternalError("can't set derived attribute: " + paramEAttribute);
    }
    if ((paramAttributeValues == null) || (paramAttributeValues == DEFAULT))
    {
      setDefault(paramEAttribute);
    }
    else if ((defined & mask) != 0)
    {
      i_set(paramEAttribute, paramAttributeValues);
      update(paramEAttribute);
    }
  }
  
  public void set(EAttribute paramEAttribute, Object paramObject)
  {
    if (att == null) {
      throw new InternalError("can't set derived attribute: " + paramEAttribute);
    }
    if (paramObject != null) {
      try
      {
        i_set(paramEAttribute, paramObject);
        update(paramEAttribute);
        return;
      }
      catch (Exception localException) {}
    }
    setDefault(paramEAttribute);
  }
  
  public Object get(EAttribute paramEAttribute)
  {
    if (att == null) {
      throw new InternalError("can't get derived attribute: " + paramEAttribute);
    }
    if ((nondefault & mask) != 0) {
      return i_get(paramEAttribute);
    }
    return null;
  }
  
  public AttributeValues merge(Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap)
  {
    return merge(paramMap, MASK_ALL);
  }
  
  public AttributeValues merge(Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap, int paramInt)
  {
    if (((paramMap instanceof AttributeMap)) && (((AttributeMap)paramMap).getValues() != null))
    {
      merge(((AttributeMap)paramMap).getValues(), paramInt);
    }
    else if ((paramMap != null) && (!paramMap.isEmpty()))
    {
      Iterator localIterator = paramMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        try
        {
          EAttribute localEAttribute = EAttribute.forAttribute((AttributedCharacterIterator.Attribute)localEntry.getKey());
          if ((localEAttribute != null) && ((paramInt & mask) != 0)) {
            set(localEAttribute, localEntry.getValue());
          }
        }
        catch (ClassCastException localClassCastException) {}
      }
    }
    return this;
  }
  
  public AttributeValues merge(AttributeValues paramAttributeValues)
  {
    return merge(paramAttributeValues, MASK_ALL);
  }
  
  public AttributeValues merge(AttributeValues paramAttributeValues, int paramInt)
  {
    int i = paramInt & defined;
    for (EAttribute localEAttribute : EAttribute.atts)
    {
      if (i == 0) {
        break;
      }
      if ((i & mask) != 0)
      {
        i &= (mask ^ 0xFFFFFFFF);
        i_set(localEAttribute, paramAttributeValues);
        update(localEAttribute);
      }
    }
    return this;
  }
  
  public static AttributeValues fromMap(Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap)
  {
    return fromMap(paramMap, MASK_ALL);
  }
  
  public static AttributeValues fromMap(Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap, int paramInt)
  {
    return new AttributeValues().merge(paramMap, paramInt);
  }
  
  public Map<TextAttribute, Object> toMap(Map<TextAttribute, Object> paramMap)
  {
    if (paramMap == null) {
      paramMap = new HashMap();
    }
    int i = defined;
    for (int j = 0; i != 0; j++)
    {
      EAttribute localEAttribute = EAttribute.atts[j];
      if ((i & mask) != 0)
      {
        i &= (mask ^ 0xFFFFFFFF);
        paramMap.put(att, get(localEAttribute));
      }
    }
    return paramMap;
  }
  
  public static boolean is16Hashtable(Hashtable<Object, Object> paramHashtable)
  {
    return paramHashtable.containsKey("sun.font.attributevalues.defined_key");
  }
  
  public static AttributeValues fromSerializableHashtable(Hashtable<Object, Object> paramHashtable)
  {
    AttributeValues localAttributeValues = new AttributeValues();
    if ((paramHashtable != null) && (!paramHashtable.isEmpty()))
    {
      Iterator localIterator = paramHashtable.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        Object localObject1 = localEntry.getKey();
        Object localObject2 = localEntry.getValue();
        if (localObject1.equals("sun.font.attributevalues.defined_key")) {
          localAttributeValues.defineAll(((Integer)localObject2).intValue());
        } else {
          try
          {
            EAttribute localEAttribute = EAttribute.forAttribute((AttributedCharacterIterator.Attribute)localObject1);
            if (localEAttribute != null) {
              localAttributeValues.set(localEAttribute, localObject2);
            }
          }
          catch (ClassCastException localClassCastException) {}
        }
      }
    }
    return localAttributeValues;
  }
  
  public Hashtable<Object, Object> toSerializableHashtable()
  {
    Hashtable localHashtable = new Hashtable();
    int i = defined;
    int j = defined;
    for (int k = 0; j != 0; k++)
    {
      EAttribute localEAttribute = EAttribute.atts[k];
      if ((j & mask) != 0)
      {
        j &= (mask ^ 0xFFFFFFFF);
        Object localObject = get(localEAttribute);
        if (localObject != null) {
          if ((localObject instanceof Serializable)) {
            localHashtable.put(att, localObject);
          } else {
            i &= (mask ^ 0xFFFFFFFF);
          }
        }
      }
    }
    localHashtable.put("sun.font.attributevalues.defined_key", Integer.valueOf(i));
    return localHashtable;
  }
  
  public int hashCode()
  {
    return defined << 8 ^ nondefault;
  }
  
  public boolean equals(Object paramObject)
  {
    try
    {
      return equals((AttributeValues)paramObject);
    }
    catch (ClassCastException localClassCastException) {}
    return false;
  }
  
  public boolean equals(AttributeValues paramAttributeValues)
  {
    if (paramAttributeValues == null) {
      return false;
    }
    if (paramAttributeValues == this) {
      return true;
    }
    return (defined == defined) && (nondefault == nondefault) && (underline == underline) && (strikethrough == strikethrough) && (superscript == superscript) && (width == width) && (kerning == kerning) && (tracking == tracking) && (ligatures == ligatures) && (runDirection == runDirection) && (bidiEmbedding == bidiEmbedding) && (swapColors == swapColors) && (equals(transform, transform)) && (equals(foreground, foreground)) && (equals(background, background)) && (equals(numericShaping, numericShaping)) && (equals(Float.valueOf(justification), Float.valueOf(justification))) && (equals(charReplacement, charReplacement)) && (size == size) && (weight == weight) && (posture == posture) && (equals(family, family)) && (equals(font, font)) && (imUnderline == imUnderline) && (equals(imHighlight, imHighlight));
  }
  
  public AttributeValues clone()
  {
    try
    {
      AttributeValues localAttributeValues = (AttributeValues)super.clone();
      if (transform != null)
      {
        transform = new AffineTransform(transform);
        localAttributeValues.updateDerivedTransforms();
      }
      return localAttributeValues;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return null;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append('{');
    int i = defined;
    for (int j = 0; i != 0; j++)
    {
      EAttribute localEAttribute = EAttribute.atts[j];
      if ((i & mask) != 0)
      {
        i &= (mask ^ 0xFFFFFFFF);
        if (localStringBuilder.length() > 1) {
          localStringBuilder.append(", ");
        }
        localStringBuilder.append(localEAttribute);
        localStringBuilder.append('=');
        switch (localEAttribute)
        {
        case EFAMILY: 
          localStringBuilder.append('"');
          localStringBuilder.append(family);
          localStringBuilder.append('"');
          break;
        case EWEIGHT: 
          localStringBuilder.append(weight);
          break;
        case EWIDTH: 
          localStringBuilder.append(width);
          break;
        case EPOSTURE: 
          localStringBuilder.append(posture);
          break;
        case ESIZE: 
          localStringBuilder.append(size);
          break;
        case ETRANSFORM: 
          localStringBuilder.append(transform);
          break;
        case ESUPERSCRIPT: 
          localStringBuilder.append(superscript);
          break;
        case EFONT: 
          localStringBuilder.append(font);
          break;
        case ECHAR_REPLACEMENT: 
          localStringBuilder.append(charReplacement);
          break;
        case EFOREGROUND: 
          localStringBuilder.append(foreground);
          break;
        case EBACKGROUND: 
          localStringBuilder.append(background);
          break;
        case EUNDERLINE: 
          localStringBuilder.append(underline);
          break;
        case ESTRIKETHROUGH: 
          localStringBuilder.append(strikethrough);
          break;
        case ERUN_DIRECTION: 
          localStringBuilder.append(runDirection);
          break;
        case EBIDI_EMBEDDING: 
          localStringBuilder.append(bidiEmbedding);
          break;
        case EJUSTIFICATION: 
          localStringBuilder.append(justification);
          break;
        case EINPUT_METHOD_HIGHLIGHT: 
          localStringBuilder.append(imHighlight);
          break;
        case EINPUT_METHOD_UNDERLINE: 
          localStringBuilder.append(imUnderline);
          break;
        case ESWAP_COLORS: 
          localStringBuilder.append(swapColors);
          break;
        case ENUMERIC_SHAPING: 
          localStringBuilder.append(numericShaping);
          break;
        case EKERNING: 
          localStringBuilder.append(kerning);
          break;
        case ELIGATURES: 
          localStringBuilder.append(ligatures);
          break;
        case ETRACKING: 
          localStringBuilder.append(tracking);
          break;
        default: 
          throw new InternalError();
        }
        if ((nondefault & mask) == 0) {
          localStringBuilder.append('*');
        }
      }
    }
    localStringBuilder.append("[btx=" + baselineTransform + ", ctx=" + charTransform + "]");
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  private static boolean equals(Object paramObject1, Object paramObject2)
  {
    return paramObject1 == null ? false : paramObject2 == null ? true : paramObject1.equals(paramObject2);
  }
  
  private void update(EAttribute paramEAttribute)
  {
    defined |= mask;
    if (i_validate(paramEAttribute))
    {
      if (i_equals(paramEAttribute, DEFAULT)) {
        nondefault &= (mask ^ 0xFFFFFFFF);
      } else {
        nondefault |= mask;
      }
    }
    else {
      setDefault(paramEAttribute);
    }
  }
  
  private void i_set(EAttribute paramEAttribute, AttributeValues paramAttributeValues)
  {
    switch (paramEAttribute)
    {
    case EFAMILY: 
      family = family;
      break;
    case EWEIGHT: 
      weight = weight;
      break;
    case EWIDTH: 
      width = width;
      break;
    case EPOSTURE: 
      posture = posture;
      break;
    case ESIZE: 
      size = size;
      break;
    case ETRANSFORM: 
      transform = transform;
      updateDerivedTransforms();
      break;
    case ESUPERSCRIPT: 
      superscript = superscript;
      break;
    case EFONT: 
      font = font;
      break;
    case ECHAR_REPLACEMENT: 
      charReplacement = charReplacement;
      break;
    case EFOREGROUND: 
      foreground = foreground;
      break;
    case EBACKGROUND: 
      background = background;
      break;
    case EUNDERLINE: 
      underline = underline;
      break;
    case ESTRIKETHROUGH: 
      strikethrough = strikethrough;
      break;
    case ERUN_DIRECTION: 
      runDirection = runDirection;
      break;
    case EBIDI_EMBEDDING: 
      bidiEmbedding = bidiEmbedding;
      break;
    case EJUSTIFICATION: 
      justification = justification;
      break;
    case EINPUT_METHOD_HIGHLIGHT: 
      imHighlight = imHighlight;
      break;
    case EINPUT_METHOD_UNDERLINE: 
      imUnderline = imUnderline;
      break;
    case ESWAP_COLORS: 
      swapColors = swapColors;
      break;
    case ENUMERIC_SHAPING: 
      numericShaping = numericShaping;
      break;
    case EKERNING: 
      kerning = kerning;
      break;
    case ELIGATURES: 
      ligatures = ligatures;
      break;
    case ETRACKING: 
      tracking = tracking;
      break;
    default: 
      throw new InternalError();
    }
  }
  
  private boolean i_equals(EAttribute paramEAttribute, AttributeValues paramAttributeValues)
  {
    switch (paramEAttribute)
    {
    case EFAMILY: 
      return equals(family, family);
    case EWEIGHT: 
      return weight == weight;
    case EWIDTH: 
      return width == width;
    case EPOSTURE: 
      return posture == posture;
    case ESIZE: 
      return size == size;
    case ETRANSFORM: 
      return equals(transform, transform);
    case ESUPERSCRIPT: 
      return superscript == superscript;
    case EFONT: 
      return equals(font, font);
    case ECHAR_REPLACEMENT: 
      return equals(charReplacement, charReplacement);
    case EFOREGROUND: 
      return equals(foreground, foreground);
    case EBACKGROUND: 
      return equals(background, background);
    case EUNDERLINE: 
      return underline == underline;
    case ESTRIKETHROUGH: 
      return strikethrough == strikethrough;
    case ERUN_DIRECTION: 
      return runDirection == runDirection;
    case EBIDI_EMBEDDING: 
      return bidiEmbedding == bidiEmbedding;
    case EJUSTIFICATION: 
      return justification == justification;
    case EINPUT_METHOD_HIGHLIGHT: 
      return equals(imHighlight, imHighlight);
    case EINPUT_METHOD_UNDERLINE: 
      return imUnderline == imUnderline;
    case ESWAP_COLORS: 
      return swapColors == swapColors;
    case ENUMERIC_SHAPING: 
      return equals(numericShaping, numericShaping);
    case EKERNING: 
      return kerning == kerning;
    case ELIGATURES: 
      return ligatures == ligatures;
    case ETRACKING: 
      return tracking == tracking;
    }
    throw new InternalError();
  }
  
  private void i_set(EAttribute paramEAttribute, Object paramObject)
  {
    Object localObject;
    switch (paramEAttribute)
    {
    case EFAMILY: 
      family = ((String)paramObject).trim();
      break;
    case EWEIGHT: 
      weight = ((Number)paramObject).floatValue();
      break;
    case EWIDTH: 
      width = ((Number)paramObject).floatValue();
      break;
    case EPOSTURE: 
      posture = ((Number)paramObject).floatValue();
      break;
    case ESIZE: 
      size = ((Number)paramObject).floatValue();
      break;
    case ETRANSFORM: 
      if ((paramObject instanceof TransformAttribute))
      {
        localObject = (TransformAttribute)paramObject;
        if (((TransformAttribute)localObject).isIdentity()) {
          transform = null;
        } else {
          transform = ((TransformAttribute)localObject).getTransform();
        }
      }
      else
      {
        transform = new AffineTransform((AffineTransform)paramObject);
      }
      updateDerivedTransforms();
      break;
    case ESUPERSCRIPT: 
      superscript = ((byte)((Integer)paramObject).intValue());
      break;
    case EFONT: 
      font = ((Font)paramObject);
      break;
    case ECHAR_REPLACEMENT: 
      charReplacement = ((GraphicAttribute)paramObject);
      break;
    case EFOREGROUND: 
      foreground = ((Paint)paramObject);
      break;
    case EBACKGROUND: 
      background = ((Paint)paramObject);
      break;
    case EUNDERLINE: 
      underline = ((byte)((Integer)paramObject).intValue());
      break;
    case ESTRIKETHROUGH: 
      strikethrough = ((Boolean)paramObject).booleanValue();
      break;
    case ERUN_DIRECTION: 
      if ((paramObject instanceof Boolean)) {
        runDirection = ((byte)(TextAttribute.RUN_DIRECTION_LTR.equals(paramObject) ? 0 : 1));
      } else {
        runDirection = ((byte)((Integer)paramObject).intValue());
      }
      break;
    case EBIDI_EMBEDDING: 
      bidiEmbedding = ((byte)((Integer)paramObject).intValue());
      break;
    case EJUSTIFICATION: 
      justification = ((Number)paramObject).floatValue();
      break;
    case EINPUT_METHOD_HIGHLIGHT: 
      if ((paramObject instanceof Annotation))
      {
        localObject = (Annotation)paramObject;
        imHighlight = ((InputMethodHighlight)((Annotation)localObject).getValue());
      }
      else
      {
        imHighlight = ((InputMethodHighlight)paramObject);
      }
      break;
    case EINPUT_METHOD_UNDERLINE: 
      imUnderline = ((byte)((Integer)paramObject).intValue());
      break;
    case ESWAP_COLORS: 
      swapColors = ((Boolean)paramObject).booleanValue();
      break;
    case ENUMERIC_SHAPING: 
      numericShaping = ((NumericShaper)paramObject);
      break;
    case EKERNING: 
      kerning = ((byte)((Integer)paramObject).intValue());
      break;
    case ELIGATURES: 
      ligatures = ((byte)((Integer)paramObject).intValue());
      break;
    case ETRACKING: 
      tracking = ((Number)paramObject).floatValue();
      break;
    default: 
      throw new InternalError();
    }
  }
  
  private Object i_get(EAttribute paramEAttribute)
  {
    switch (paramEAttribute)
    {
    case EFAMILY: 
      return family;
    case EWEIGHT: 
      return Float.valueOf(weight);
    case EWIDTH: 
      return Float.valueOf(width);
    case EPOSTURE: 
      return Float.valueOf(posture);
    case ESIZE: 
      return Float.valueOf(size);
    case ETRANSFORM: 
      return transform == null ? TransformAttribute.IDENTITY : new TransformAttribute(transform);
    case ESUPERSCRIPT: 
      return Integer.valueOf(superscript);
    case EFONT: 
      return font;
    case ECHAR_REPLACEMENT: 
      return charReplacement;
    case EFOREGROUND: 
      return foreground;
    case EBACKGROUND: 
      return background;
    case EUNDERLINE: 
      return Integer.valueOf(underline);
    case ESTRIKETHROUGH: 
      return Boolean.valueOf(strikethrough);
    case ERUN_DIRECTION: 
      switch (runDirection)
      {
      case 0: 
        return TextAttribute.RUN_DIRECTION_LTR;
      case 1: 
        return TextAttribute.RUN_DIRECTION_RTL;
      }
      return null;
    case EBIDI_EMBEDDING: 
      return Integer.valueOf(bidiEmbedding);
    case EJUSTIFICATION: 
      return Float.valueOf(justification);
    case EINPUT_METHOD_HIGHLIGHT: 
      return imHighlight;
    case EINPUT_METHOD_UNDERLINE: 
      return Integer.valueOf(imUnderline);
    case ESWAP_COLORS: 
      return Boolean.valueOf(swapColors);
    case ENUMERIC_SHAPING: 
      return numericShaping;
    case EKERNING: 
      return Integer.valueOf(kerning);
    case ELIGATURES: 
      return Integer.valueOf(ligatures);
    case ETRACKING: 
      return Float.valueOf(tracking);
    }
    throw new InternalError();
  }
  
  private boolean i_validate(EAttribute paramEAttribute)
  {
    switch (paramEAttribute)
    {
    case EFAMILY: 
      if ((family == null) || (family.length() == 0)) {
        family = DEFAULTfamily;
      }
      return true;
    case EWEIGHT: 
      return (weight > 0.0F) && (weight < 10.0F);
    case EWIDTH: 
      return (width >= 0.5F) && (width < 10.0F);
    case EPOSTURE: 
      return (posture >= -1.0F) && (posture <= 1.0F);
    case ESIZE: 
      return size >= 0.0F;
    case ETRANSFORM: 
      if ((transform != null) && (transform.isIdentity())) {
        transform = DEFAULTtransform;
      }
      return true;
    case ESUPERSCRIPT: 
      return (superscript >= -7) && (superscript <= 7);
    case EFONT: 
      return true;
    case ECHAR_REPLACEMENT: 
      return true;
    case EFOREGROUND: 
      return true;
    case EBACKGROUND: 
      return true;
    case EUNDERLINE: 
      return (underline >= -1) && (underline < 6);
    case ESTRIKETHROUGH: 
      return true;
    case ERUN_DIRECTION: 
      return (runDirection >= -2) && (runDirection <= 1);
    case EBIDI_EMBEDDING: 
      return (bidiEmbedding >= -61) && (bidiEmbedding < 62);
    case EJUSTIFICATION: 
      justification = Math.max(0.0F, Math.min(justification, 1.0F));
      return true;
    case EINPUT_METHOD_HIGHLIGHT: 
      return true;
    case EINPUT_METHOD_UNDERLINE: 
      return (imUnderline >= -1) && (imUnderline < 6);
    case ESWAP_COLORS: 
      return true;
    case ENUMERIC_SHAPING: 
      return true;
    case EKERNING: 
      return (kerning >= 0) && (kerning <= 1);
    case ELIGATURES: 
      return (ligatures >= 0) && (ligatures <= 1);
    case ETRACKING: 
      return (tracking >= -1.0F) && (tracking <= 10.0F);
    }
    throw new InternalError("unknown attribute: " + paramEAttribute);
  }
  
  public static float getJustification(Map<?, ?> paramMap)
  {
    if (paramMap != null)
    {
      if (((paramMap instanceof AttributeMap)) && (((AttributeMap)paramMap).getValues() != null)) {
        return getValuesjustification;
      }
      Object localObject = paramMap.get(TextAttribute.JUSTIFICATION);
      if ((localObject != null) && ((localObject instanceof Number))) {
        return Math.max(0.0F, Math.min(1.0F, ((Number)localObject).floatValue()));
      }
    }
    return DEFAULTjustification;
  }
  
  public static NumericShaper getNumericShaping(Map<?, ?> paramMap)
  {
    if (paramMap != null)
    {
      if (((paramMap instanceof AttributeMap)) && (((AttributeMap)paramMap).getValues() != null)) {
        return getValuesnumericShaping;
      }
      Object localObject = paramMap.get(TextAttribute.NUMERIC_SHAPING);
      if ((localObject != null) && ((localObject instanceof NumericShaper))) {
        return (NumericShaper)localObject;
      }
    }
    return DEFAULTnumericShaping;
  }
  
  public AttributeValues applyIMHighlight()
  {
    if (imHighlight != null)
    {
      InputMethodHighlight localInputMethodHighlight = null;
      if ((imHighlight instanceof InputMethodHighlight)) {
        localInputMethodHighlight = (InputMethodHighlight)imHighlight;
      } else {
        localInputMethodHighlight = (InputMethodHighlight)((Annotation)imHighlight).getValue();
      }
      Map localMap = localInputMethodHighlight.getStyle();
      if (localMap == null)
      {
        Toolkit localToolkit = Toolkit.getDefaultToolkit();
        localMap = localToolkit.mapInputMethodHighlight(localInputMethodHighlight);
      }
      if (localMap != null) {
        return clone().merge(localMap);
      }
    }
    return this;
  }
  
  public static AffineTransform getBaselineTransform(Map<?, ?> paramMap)
  {
    if (paramMap != null)
    {
      AttributeValues localAttributeValues = null;
      if (((paramMap instanceof AttributeMap)) && (((AttributeMap)paramMap).getValues() != null)) {
        localAttributeValues = ((AttributeMap)paramMap).getValues();
      } else if (paramMap.get(TextAttribute.TRANSFORM) != null) {
        localAttributeValues = fromMap(paramMap);
      }
      if (localAttributeValues != null) {
        return baselineTransform;
      }
    }
    return null;
  }
  
  public static AffineTransform getCharTransform(Map<?, ?> paramMap)
  {
    if (paramMap != null)
    {
      AttributeValues localAttributeValues = null;
      if (((paramMap instanceof AttributeMap)) && (((AttributeMap)paramMap).getValues() != null)) {
        localAttributeValues = ((AttributeMap)paramMap).getValues();
      } else if (paramMap.get(TextAttribute.TRANSFORM) != null) {
        localAttributeValues = fromMap(paramMap);
      }
      if (localAttributeValues != null) {
        return charTransform;
      }
    }
    return null;
  }
  
  public void updateDerivedTransforms()
  {
    if (transform == null)
    {
      baselineTransform = null;
      charTransform = null;
    }
    else
    {
      charTransform = new AffineTransform(transform);
      baselineTransform = extractXRotation(charTransform, true);
      if (charTransform.isIdentity()) {
        charTransform = null;
      }
      if (baselineTransform.isIdentity()) {
        baselineTransform = null;
      }
    }
    if (baselineTransform == null) {
      nondefault &= (EBASELINE_TRANSFORMmask ^ 0xFFFFFFFF);
    } else {
      nondefault |= EBASELINE_TRANSFORMmask;
    }
  }
  
  public static AffineTransform extractXRotation(AffineTransform paramAffineTransform, boolean paramBoolean)
  {
    return extractRotation(new Point2D.Double(1.0D, 0.0D), paramAffineTransform, paramBoolean);
  }
  
  public static AffineTransform extractYRotation(AffineTransform paramAffineTransform, boolean paramBoolean)
  {
    return extractRotation(new Point2D.Double(0.0D, 1.0D), paramAffineTransform, paramBoolean);
  }
  
  private static AffineTransform extractRotation(Point2D.Double paramDouble, AffineTransform paramAffineTransform, boolean paramBoolean)
  {
    paramAffineTransform.deltaTransform(paramDouble, paramDouble);
    AffineTransform localAffineTransform1 = AffineTransform.getRotateInstance(x, y);
    try
    {
      AffineTransform localAffineTransform2 = localAffineTransform1.createInverse();
      double d1 = paramAffineTransform.getTranslateX();
      double d2 = paramAffineTransform.getTranslateY();
      paramAffineTransform.preConcatenate(localAffineTransform2);
      if ((paramBoolean) && ((d1 != 0.0D) || (d2 != 0.0D)))
      {
        paramAffineTransform.setTransform(paramAffineTransform.getScaleX(), paramAffineTransform.getShearY(), paramAffineTransform.getShearX(), paramAffineTransform.getScaleY(), 0.0D, 0.0D);
        localAffineTransform1.setTransform(localAffineTransform1.getScaleX(), localAffineTransform1.getShearY(), localAffineTransform1.getShearX(), localAffineTransform1.getScaleY(), d1, d2);
      }
    }
    catch (NoninvertibleTransformException localNoninvertibleTransformException)
    {
      return null;
    }
    return localAffineTransform1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\AttributeValues.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */