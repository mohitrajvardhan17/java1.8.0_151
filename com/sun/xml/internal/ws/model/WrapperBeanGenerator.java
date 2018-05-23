package com.sun.xml.internal.ws.model;

import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.annotation.RuntimeInlineAnnotationReader;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.ws.org.objectweb.asm.AnnotationVisitor;
import com.sun.xml.internal.ws.org.objectweb.asm.ClassWriter;
import com.sun.xml.internal.ws.org.objectweb.asm.FieldVisitor;
import com.sun.xml.internal.ws.org.objectweb.asm.MethodVisitor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;

public class WrapperBeanGenerator
{
  private static final Logger LOGGER = Logger.getLogger(WrapperBeanGenerator.class.getName());
  private static final FieldFactory FIELD_FACTORY = new FieldFactory(null);
  private static final AbstractWrapperBeanGenerator RUNTIME_GENERATOR = new RuntimeWrapperBeanGenerator(new RuntimeInlineAnnotationReader(), Utils.REFLECTION_NAVIGATOR, FIELD_FACTORY);
  
  public WrapperBeanGenerator() {}
  
  private static byte[] createBeanImage(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, Collection<Field> paramCollection)
    throws Exception
  {
    ClassWriter localClassWriter = new ClassWriter(0);
    localClassWriter.visit(49, 33, replaceDotWithSlash(paramString1), null, "java/lang/Object", null);
    AnnotationVisitor localAnnotationVisitor1 = localClassWriter.visitAnnotation("Ljavax/xml/bind/annotation/XmlRootElement;", true);
    localAnnotationVisitor1.visit("name", paramString2);
    localAnnotationVisitor1.visit("namespace", paramString3);
    localAnnotationVisitor1.visitEnd();
    AnnotationVisitor localAnnotationVisitor2 = localClassWriter.visitAnnotation("Ljavax/xml/bind/annotation/XmlType;", true);
    localAnnotationVisitor2.visit("name", paramString4);
    localAnnotationVisitor2.visit("namespace", paramString5);
    Object localObject2;
    Object localObject3;
    if (paramCollection.size() > 1)
    {
      localObject1 = localAnnotationVisitor2.visitArray("propOrder");
      localObject2 = paramCollection.iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (Field)((Iterator)localObject2).next();
        ((AnnotationVisitor)localObject1).visit("propOrder", fieldName);
      }
      ((AnnotationVisitor)localObject1).visitEnd();
    }
    localAnnotationVisitor2.visitEnd();
    Object localObject1 = paramCollection.iterator();
    Object localObject4;
    Object localObject5;
    Object localObject6;
    XmlElement localXmlElement;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Field)((Iterator)localObject1).next();
      localObject3 = localClassWriter.visitField(1, fieldName, asmType.getDescriptor(), ((Field)localObject2).getSignature(), null);
      localObject4 = jaxbAnnotations.iterator();
      while (((Iterator)localObject4).hasNext())
      {
        localObject5 = (Annotation)((Iterator)localObject4).next();
        if ((localObject5 instanceof XmlMimeType))
        {
          localObject6 = ((FieldVisitor)localObject3).visitAnnotation("Ljavax/xml/bind/annotation/XmlMimeType;", true);
          ((AnnotationVisitor)localObject6).visit("value", ((XmlMimeType)localObject5).value());
          ((AnnotationVisitor)localObject6).visitEnd();
        }
        else if ((localObject5 instanceof XmlJavaTypeAdapter))
        {
          localObject6 = ((FieldVisitor)localObject3).visitAnnotation("Ljavax/xml/bind/annotation/adapters/XmlJavaTypeAdapter;", true);
          ((AnnotationVisitor)localObject6).visit("value", getASMType(((XmlJavaTypeAdapter)localObject5).value()));
          ((AnnotationVisitor)localObject6).visitEnd();
        }
        else if ((localObject5 instanceof XmlAttachmentRef))
        {
          localObject6 = ((FieldVisitor)localObject3).visitAnnotation("Ljavax/xml/bind/annotation/XmlAttachmentRef;", true);
          ((AnnotationVisitor)localObject6).visitEnd();
        }
        else if ((localObject5 instanceof XmlList))
        {
          localObject6 = ((FieldVisitor)localObject3).visitAnnotation("Ljavax/xml/bind/annotation/XmlList;", true);
          ((AnnotationVisitor)localObject6).visitEnd();
        }
        else if ((localObject5 instanceof XmlElement))
        {
          localObject6 = ((FieldVisitor)localObject3).visitAnnotation("Ljavax/xml/bind/annotation/XmlElement;", true);
          localXmlElement = (XmlElement)localObject5;
          ((AnnotationVisitor)localObject6).visit("name", localXmlElement.name());
          ((AnnotationVisitor)localObject6).visit("namespace", localXmlElement.namespace());
          if (localXmlElement.nillable()) {
            ((AnnotationVisitor)localObject6).visit("nillable", Boolean.valueOf(true));
          }
          if (localXmlElement.required()) {
            ((AnnotationVisitor)localObject6).visit("required", Boolean.valueOf(true));
          }
          ((AnnotationVisitor)localObject6).visitEnd();
        }
        else
        {
          throw new WebServiceException("Unknown JAXB annotation " + localObject5);
        }
      }
      ((FieldVisitor)localObject3).visitEnd();
    }
    localObject1 = localClassWriter.visitMethod(1, "<init>", "()V", null, null);
    ((MethodVisitor)localObject1).visitCode();
    ((MethodVisitor)localObject1).visitVarInsn(25, 0);
    ((MethodVisitor)localObject1).visitMethodInsn(183, "java/lang/Object", "<init>", "()V");
    ((MethodVisitor)localObject1).visitInsn(177);
    ((MethodVisitor)localObject1).visitMaxs(1, 1);
    ((MethodVisitor)localObject1).visitEnd();
    localClassWriter.visitEnd();
    if (LOGGER.isLoggable(Level.FINE))
    {
      localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append("\n");
      ((StringBuilder)localObject2).append("@XmlRootElement(name=").append(paramString2).append(", namespace=").append(paramString3).append(")");
      ((StringBuilder)localObject2).append("\n");
      ((StringBuilder)localObject2).append("@XmlType(name=").append(paramString4).append(", namespace=").append(paramString5);
      if (paramCollection.size() > 1)
      {
        ((StringBuilder)localObject2).append(", propOrder={");
        localObject3 = paramCollection.iterator();
        while (((Iterator)localObject3).hasNext())
        {
          localObject4 = (Field)((Iterator)localObject3).next();
          ((StringBuilder)localObject2).append(" ");
          ((StringBuilder)localObject2).append(fieldName);
        }
        ((StringBuilder)localObject2).append(" }");
      }
      ((StringBuilder)localObject2).append(")");
      ((StringBuilder)localObject2).append("\n");
      ((StringBuilder)localObject2).append("public class ").append(paramString1).append(" {");
      localObject3 = paramCollection.iterator();
      while (((Iterator)localObject3).hasNext())
      {
        localObject4 = (Field)((Iterator)localObject3).next();
        ((StringBuilder)localObject2).append("\n");
        localObject5 = jaxbAnnotations.iterator();
        while (((Iterator)localObject5).hasNext())
        {
          localObject6 = (Annotation)((Iterator)localObject5).next();
          ((StringBuilder)localObject2).append("\n    ");
          if ((localObject6 instanceof XmlMimeType))
          {
            ((StringBuilder)localObject2).append("@XmlMimeType(value=").append(((XmlMimeType)localObject6).value()).append(")");
          }
          else if ((localObject6 instanceof XmlJavaTypeAdapter))
          {
            ((StringBuilder)localObject2).append("@XmlJavaTypeAdapter(value=").append(getASMType(((XmlJavaTypeAdapter)localObject6).value())).append(")");
          }
          else if ((localObject6 instanceof XmlAttachmentRef))
          {
            ((StringBuilder)localObject2).append("@XmlAttachmentRef");
          }
          else if ((localObject6 instanceof XmlList))
          {
            ((StringBuilder)localObject2).append("@XmlList");
          }
          else if ((localObject6 instanceof XmlElement))
          {
            localXmlElement = (XmlElement)localObject6;
            ((StringBuilder)localObject2).append("\n    ");
            ((StringBuilder)localObject2).append("@XmlElement(name=").append(localXmlElement.name()).append(", namespace=").append(localXmlElement.namespace());
            if (localXmlElement.nillable()) {
              ((StringBuilder)localObject2).append(", nillable=true");
            }
            if (localXmlElement.required()) {
              ((StringBuilder)localObject2).append(", required=true");
            }
            ((StringBuilder)localObject2).append(")");
          }
          else
          {
            throw new WebServiceException("Unknown JAXB annotation " + localObject6);
          }
        }
        ((StringBuilder)localObject2).append("\n    ");
        ((StringBuilder)localObject2).append("public ");
        if (((Field)localObject4).getSignature() == null) {
          ((StringBuilder)localObject2).append(asmType.getDescriptor());
        } else {
          ((StringBuilder)localObject2).append(((Field)localObject4).getSignature());
        }
        ((StringBuilder)localObject2).append(" ");
        ((StringBuilder)localObject2).append(fieldName);
      }
      ((StringBuilder)localObject2).append("\n\n}");
      LOGGER.fine(((StringBuilder)localObject2).toString());
    }
    return localClassWriter.toByteArray();
  }
  
  private static String replaceDotWithSlash(String paramString)
  {
    return paramString.replace('.', '/');
  }
  
  static Class createRequestWrapperBean(String paramString, Method paramMethod, QName paramQName, ClassLoader paramClassLoader)
  {
    if (LOGGER.isLoggable(Level.FINE)) {
      LOGGER.log(Level.FINE, "Request Wrapper Class : {0}", paramString);
    }
    List localList = RUNTIME_GENERATOR.collectRequestBeanMembers(paramMethod);
    byte[] arrayOfByte;
    try
    {
      arrayOfByte = createBeanImage(paramString, paramQName.getLocalPart(), paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramQName.getNamespaceURI(), localList);
    }
    catch (Exception localException)
    {
      throw new WebServiceException(localException);
    }
    return Injector.inject(paramClassLoader, paramString, arrayOfByte);
  }
  
  static Class createResponseWrapperBean(String paramString, Method paramMethod, QName paramQName, ClassLoader paramClassLoader)
  {
    if (LOGGER.isLoggable(Level.FINE)) {
      LOGGER.log(Level.FINE, "Response Wrapper Class : {0}", paramString);
    }
    List localList = RUNTIME_GENERATOR.collectResponseBeanMembers(paramMethod);
    byte[] arrayOfByte;
    try
    {
      arrayOfByte = createBeanImage(paramString, paramQName.getLocalPart(), paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramQName.getNamespaceURI(), localList);
    }
    catch (Exception localException)
    {
      throw new WebServiceException(localException);
    }
    return Injector.inject(paramClassLoader, paramString, arrayOfByte);
  }
  
  private static com.sun.xml.internal.ws.org.objectweb.asm.Type getASMType(java.lang.reflect.Type paramType)
  {
    assert (paramType != null);
    if ((paramType instanceof Class)) {
      return com.sun.xml.internal.ws.org.objectweb.asm.Type.getType((Class)paramType);
    }
    Object localObject;
    if ((paramType instanceof ParameterizedType))
    {
      localObject = (ParameterizedType)paramType;
      if ((((ParameterizedType)localObject).getRawType() instanceof Class)) {
        return com.sun.xml.internal.ws.org.objectweb.asm.Type.getType((Class)((ParameterizedType)localObject).getRawType());
      }
    }
    if ((paramType instanceof GenericArrayType)) {
      return com.sun.xml.internal.ws.org.objectweb.asm.Type.getType(FieldSignature.vms(paramType));
    }
    if ((paramType instanceof WildcardType)) {
      return com.sun.xml.internal.ws.org.objectweb.asm.Type.getType(FieldSignature.vms(paramType));
    }
    if ((paramType instanceof TypeVariable))
    {
      localObject = (TypeVariable)paramType;
      if ((localObject.getBounds()[0] instanceof Class)) {
        return com.sun.xml.internal.ws.org.objectweb.asm.Type.getType((Class)localObject.getBounds()[0]);
      }
    }
    throw new IllegalArgumentException("Not creating ASM Type for type = " + paramType);
  }
  
  static Class createExceptionBean(String paramString1, Class paramClass, String paramString2, String paramString3, String paramString4, ClassLoader paramClassLoader)
  {
    return createExceptionBean(paramString1, paramClass, paramString2, paramString3, paramString4, paramClassLoader, true);
  }
  
  static Class createExceptionBean(String paramString1, Class paramClass, String paramString2, String paramString3, String paramString4, ClassLoader paramClassLoader, boolean paramBoolean)
  {
    Collection localCollection = RUNTIME_GENERATOR.collectExceptionBeanMembers(paramClass, paramBoolean);
    byte[] arrayOfByte;
    try
    {
      arrayOfByte = createBeanImage(paramString1, paramString3, paramString4, paramClass.getSimpleName(), paramString2, localCollection);
    }
    catch (Exception localException)
    {
      throw new WebServiceException(localException);
    }
    return Injector.inject(paramClassLoader, paramString1, arrayOfByte);
  }
  
  static void write(byte[] paramArrayOfByte, String paramString)
  {
    paramString = paramString.substring(paramString.lastIndexOf(".") + 1);
    try
    {
      FileOutputStream localFileOutputStream = new FileOutputStream(paramString + ".class");
      localFileOutputStream.write(paramArrayOfByte);
      localFileOutputStream.flush();
      localFileOutputStream.close();
    }
    catch (IOException localIOException)
    {
      LOGGER.log(Level.INFO, "Error Writing class", localIOException);
    }
  }
  
  private static class Field
    implements Comparable<Field>
  {
    private final java.lang.reflect.Type reflectType;
    private final com.sun.xml.internal.ws.org.objectweb.asm.Type asmType;
    private final String fieldName;
    private final List<Annotation> jaxbAnnotations;
    
    Field(String paramString, java.lang.reflect.Type paramType, com.sun.xml.internal.ws.org.objectweb.asm.Type paramType1, List<Annotation> paramList)
    {
      reflectType = paramType;
      asmType = paramType1;
      fieldName = paramString;
      jaxbAnnotations = paramList;
    }
    
    String getSignature()
    {
      if ((reflectType instanceof Class)) {
        return null;
      }
      if ((reflectType instanceof TypeVariable)) {
        return null;
      }
      return FieldSignature.vms(reflectType);
    }
    
    public int compareTo(Field paramField)
    {
      return fieldName.compareTo(fieldName);
    }
  }
  
  private static final class FieldFactory
    implements AbstractWrapperBeanGenerator.BeanMemberFactory<java.lang.reflect.Type, WrapperBeanGenerator.Field>
  {
    private FieldFactory() {}
    
    public WrapperBeanGenerator.Field createWrapperBeanMember(java.lang.reflect.Type paramType, String paramString, List<Annotation> paramList)
    {
      return new WrapperBeanGenerator.Field(paramString, paramType, WrapperBeanGenerator.getASMType(paramType), paramList);
    }
  }
  
  private static final class RuntimeWrapperBeanGenerator
    extends AbstractWrapperBeanGenerator<java.lang.reflect.Type, Class, Method, WrapperBeanGenerator.Field>
  {
    protected RuntimeWrapperBeanGenerator(AnnotationReader<java.lang.reflect.Type, Class, ?, Method> paramAnnotationReader, Navigator<java.lang.reflect.Type, Class, ?, Method> paramNavigator, AbstractWrapperBeanGenerator.BeanMemberFactory<java.lang.reflect.Type, WrapperBeanGenerator.Field> paramBeanMemberFactory)
    {
      super(paramNavigator, paramBeanMemberFactory);
    }
    
    protected java.lang.reflect.Type getSafeType(java.lang.reflect.Type paramType)
    {
      return paramType;
    }
    
    protected java.lang.reflect.Type getHolderValueType(java.lang.reflect.Type paramType)
    {
      if ((paramType instanceof ParameterizedType))
      {
        ParameterizedType localParameterizedType = (ParameterizedType)paramType;
        if (localParameterizedType.getRawType().equals(Holder.class)) {
          return localParameterizedType.getActualTypeArguments()[0];
        }
      }
      return null;
    }
    
    protected boolean isVoidType(java.lang.reflect.Type paramType)
    {
      return paramType == Void.TYPE;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\WrapperBeanGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */