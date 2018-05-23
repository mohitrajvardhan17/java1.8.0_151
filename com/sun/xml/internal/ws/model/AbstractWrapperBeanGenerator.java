package com.sun.xml.internal.ws.model;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.ws.spi.db.BindingHelper;
import com.sun.xml.internal.ws.util.StringUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;
import javax.jws.WebParam;
import javax.jws.WebParam.Mode;
import javax.jws.WebResult;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.ws.WebServiceException;

public abstract class AbstractWrapperBeanGenerator<T, C, M, A extends Comparable>
{
  private static final Logger LOGGER = Logger.getLogger(AbstractWrapperBeanGenerator.class.getName());
  private static final String RETURN = "return";
  private static final String EMTPY_NAMESPACE_ID = "";
  private static final Class[] jaxbAnns = { XmlAttachmentRef.class, XmlMimeType.class, XmlJavaTypeAdapter.class, XmlList.class, XmlElement.class };
  private static final Set<String> skipProperties = new HashSet();
  private final AnnotationReader<T, C, ?, M> annReader;
  private final Navigator<T, C, ?, M> nav;
  private final BeanMemberFactory<T, A> factory;
  private static final Map<String, String> reservedWords;
  
  protected AbstractWrapperBeanGenerator(AnnotationReader<T, C, ?, M> paramAnnotationReader, Navigator<T, C, ?, M> paramNavigator, BeanMemberFactory<T, A> paramBeanMemberFactory)
  {
    annReader = paramAnnotationReader;
    nav = paramNavigator;
    factory = paramBeanMemberFactory;
  }
  
  private List<Annotation> collectJAXBAnnotations(M paramM)
  {
    ArrayList localArrayList = new ArrayList();
    for (Class localClass : jaxbAnns)
    {
      Annotation localAnnotation = annReader.getMethodAnnotation(localClass, paramM, null);
      if (localAnnotation != null) {
        localArrayList.add(localAnnotation);
      }
    }
    return localArrayList;
  }
  
  private List<Annotation> collectJAXBAnnotations(M paramM, int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    for (Class localClass : jaxbAnns)
    {
      Annotation localAnnotation = annReader.getMethodParameterAnnotation(localClass, paramM, paramInt, null);
      if (localAnnotation != null) {
        localArrayList.add(localAnnotation);
      }
    }
    return localArrayList;
  }
  
  protected abstract T getSafeType(T paramT);
  
  protected abstract T getHolderValueType(T paramT);
  
  protected abstract boolean isVoidType(T paramT);
  
  public List<A> collectRequestBeanMembers(M paramM)
  {
    ArrayList localArrayList = new ArrayList();
    int i = -1;
    for (Object localObject1 : nav.getMethodParameters(paramM))
    {
      i++;
      WebParam localWebParam = (WebParam)annReader.getMethodParameterAnnotation(WebParam.class, paramM, i, null);
      if ((localWebParam == null) || ((!localWebParam.header()) && (!localWebParam.mode().equals(WebParam.Mode.OUT))))
      {
        Object localObject2 = getHolderValueType(localObject1);
        Object localObject3 = localObject2 != null ? localObject2 : getSafeType(localObject1);
        String str1 = "arg" + i;
        String str2 = (localWebParam != null) && (localWebParam.targetNamespace().length() > 0) ? localWebParam.targetNamespace() : "";
        List localList = collectJAXBAnnotations(paramM, i);
        processXmlElement(localList, str1, str2, localObject3);
        Comparable localComparable = (Comparable)factory.createWrapperBeanMember(localObject3, getPropertyName(str1), localList);
        localArrayList.add(localComparable);
      }
    }
    return localArrayList;
  }
  
  public List<A> collectResponseBeanMembers(M paramM)
  {
    ArrayList localArrayList = new ArrayList();
    String str1 = "return";
    String str2 = "";
    boolean bool = false;
    WebResult localWebResult = (WebResult)annReader.getMethodAnnotation(WebResult.class, paramM, null);
    if (localWebResult != null)
    {
      if (localWebResult.name().length() > 0) {
        str1 = localWebResult.name();
      }
      if (localWebResult.targetNamespace().length() > 0) {
        str2 = localWebResult.targetNamespace();
      }
      bool = localWebResult.header();
    }
    Object localObject1 = getSafeType(nav.getReturnType(paramM));
    if ((!isVoidType(localObject1)) && (!bool))
    {
      List localList1 = collectJAXBAnnotations(paramM);
      processXmlElement(localList1, str1, str2, localObject1);
      localArrayList.add(factory.createWrapperBeanMember(localObject1, getPropertyName(str1), localList1));
    }
    int i = -1;
    for (Object localObject2 : nav.getMethodParameters(paramM))
    {
      i++;
      Object localObject3 = getHolderValueType(localObject2);
      WebParam localWebParam = (WebParam)annReader.getMethodParameterAnnotation(WebParam.class, paramM, i, null);
      if ((localObject3 != null) && ((localWebParam == null) || (!localWebParam.header())))
      {
        String str3 = "arg" + i;
        String str4 = (localWebParam != null) && (localWebParam.targetNamespace().length() > 0) ? localWebParam.targetNamespace() : "";
        List localList2 = collectJAXBAnnotations(paramM, i);
        processXmlElement(localList2, str3, str4, localObject3);
        Comparable localComparable = (Comparable)factory.createWrapperBeanMember(localObject3, getPropertyName(str3), localList2);
        localArrayList.add(localComparable);
      }
    }
    return localArrayList;
  }
  
  private void processXmlElement(List<Annotation> paramList, String paramString1, String paramString2, T paramT)
  {
    XmlElement localXmlElement1 = null;
    Object localObject1 = paramList.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Annotation)((Iterator)localObject1).next();
      if (((Annotation)localObject2).annotationType() == XmlElement.class)
      {
        localXmlElement1 = (XmlElement)localObject2;
        paramList.remove(localObject2);
        break;
      }
    }
    localObject1 = (localXmlElement1 != null) && (!localXmlElement1.name().equals("##default")) ? localXmlElement1.name() : paramString1;
    Object localObject2 = (localXmlElement1 != null) && (!localXmlElement1.namespace().equals("##default")) ? localXmlElement1.namespace() : paramString2;
    boolean bool1 = (nav.isArray(paramT)) || ((localXmlElement1 != null) && (localXmlElement1.nillable()));
    boolean bool2 = (localXmlElement1 != null) && (localXmlElement1.required());
    XmlElementHandler localXmlElementHandler = new XmlElementHandler((String)localObject1, (String)localObject2, bool1, bool2);
    XmlElement localXmlElement2 = (XmlElement)Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { XmlElement.class }, localXmlElementHandler);
    paramList.add(localXmlElement2);
  }
  
  public Collection<A> collectExceptionBeanMembers(C paramC)
  {
    return collectExceptionBeanMembers(paramC, true);
  }
  
  public Collection<A> collectExceptionBeanMembers(C paramC, boolean paramBoolean)
  {
    TreeMap localTreeMap = new TreeMap();
    getExceptionProperties(paramC, localTreeMap, paramBoolean);
    XmlType localXmlType = (XmlType)annReader.getClassAnnotation(XmlType.class, paramC, null);
    if (localXmlType != null)
    {
      String[] arrayOfString1 = localXmlType.propOrder();
      if ((arrayOfString1.length > 0) && (arrayOfString1[0].length() != 0))
      {
        ArrayList localArrayList = new ArrayList();
        for (String str : arrayOfString1)
        {
          Comparable localComparable = (Comparable)localTreeMap.get(str);
          if (localComparable != null) {
            localArrayList.add(localComparable);
          } else {
            throw new WebServiceException("Exception " + paramC + " has @XmlType and its propOrder contains unknown property " + str);
          }
        }
        return localArrayList;
      }
    }
    return localTreeMap.values();
  }
  
  private void getExceptionProperties(C paramC, TreeMap<String, A> paramTreeMap, boolean paramBoolean)
  {
    Object localObject1 = nav.getSuperClass(paramC);
    if (localObject1 != null) {
      getExceptionProperties(localObject1, paramTreeMap, paramBoolean);
    }
    Collection localCollection = nav.getDeclaredMethods(paramC);
    Iterator localIterator = localCollection.iterator();
    while (localIterator.hasNext())
    {
      Object localObject2 = localIterator.next();
      if ((nav.isPublicMethod(localObject2)) && ((!nav.isStaticMethod(localObject2)) || (!nav.isFinalMethod(localObject2))) && (nav.isPublicMethod(localObject2)))
      {
        String str1 = nav.getMethodName(localObject2);
        if (((str1.startsWith("get")) || (str1.startsWith("is"))) && (!skipProperties.contains(str1)) && (!str1.equals("get")) && (!str1.equals("is")))
        {
          Object localObject3 = getSafeType(nav.getReturnType(localObject2));
          if (nav.getMethodParameters(localObject2).length == 0)
          {
            String str2 = str1.startsWith("get") ? str1.substring(3) : str1.substring(2);
            if (paramBoolean) {
              str2 = StringUtils.decapitalize(str2);
            }
            paramTreeMap.put(str2, factory.createWrapperBeanMember(localObject3, str2, Collections.emptyList()));
          }
        }
      }
    }
  }
  
  private static String getPropertyName(String paramString)
  {
    String str = BindingHelper.mangleNameToVariableName(paramString);
    return getJavaReservedVarialbeName(str);
  }
  
  @NotNull
  private static String getJavaReservedVarialbeName(@NotNull String paramString)
  {
    String str = (String)reservedWords.get(paramString);
    return str == null ? paramString : str;
  }
  
  static
  {
    skipProperties.add("getCause");
    skipProperties.add("getLocalizedMessage");
    skipProperties.add("getClass");
    skipProperties.add("getStackTrace");
    skipProperties.add("getSuppressed");
    reservedWords = new HashMap();
    reservedWords.put("abstract", "_abstract");
    reservedWords.put("assert", "_assert");
    reservedWords.put("boolean", "_boolean");
    reservedWords.put("break", "_break");
    reservedWords.put("byte", "_byte");
    reservedWords.put("case", "_case");
    reservedWords.put("catch", "_catch");
    reservedWords.put("char", "_char");
    reservedWords.put("class", "_class");
    reservedWords.put("const", "_const");
    reservedWords.put("continue", "_continue");
    reservedWords.put("default", "_default");
    reservedWords.put("do", "_do");
    reservedWords.put("double", "_double");
    reservedWords.put("else", "_else");
    reservedWords.put("extends", "_extends");
    reservedWords.put("false", "_false");
    reservedWords.put("final", "_final");
    reservedWords.put("finally", "_finally");
    reservedWords.put("float", "_float");
    reservedWords.put("for", "_for");
    reservedWords.put("goto", "_goto");
    reservedWords.put("if", "_if");
    reservedWords.put("implements", "_implements");
    reservedWords.put("import", "_import");
    reservedWords.put("instanceof", "_instanceof");
    reservedWords.put("int", "_int");
    reservedWords.put("interface", "_interface");
    reservedWords.put("long", "_long");
    reservedWords.put("native", "_native");
    reservedWords.put("new", "_new");
    reservedWords.put("null", "_null");
    reservedWords.put("package", "_package");
    reservedWords.put("private", "_private");
    reservedWords.put("protected", "_protected");
    reservedWords.put("public", "_public");
    reservedWords.put("return", "_return");
    reservedWords.put("short", "_short");
    reservedWords.put("static", "_static");
    reservedWords.put("strictfp", "_strictfp");
    reservedWords.put("super", "_super");
    reservedWords.put("switch", "_switch");
    reservedWords.put("synchronized", "_synchronized");
    reservedWords.put("this", "_this");
    reservedWords.put("throw", "_throw");
    reservedWords.put("throws", "_throws");
    reservedWords.put("transient", "_transient");
    reservedWords.put("true", "_true");
    reservedWords.put("try", "_try");
    reservedWords.put("void", "_void");
    reservedWords.put("volatile", "_volatile");
    reservedWords.put("while", "_while");
    reservedWords.put("enum", "_enum");
  }
  
  public static abstract interface BeanMemberFactory<T, A>
  {
    public abstract A createWrapperBeanMember(T paramT, String paramString, List<Annotation> paramList);
  }
  
  private static class XmlElementHandler
    implements InvocationHandler
  {
    private String name;
    private String namespace;
    private boolean nillable;
    private boolean required;
    
    XmlElementHandler(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2)
    {
      name = paramString1;
      namespace = paramString2;
      nillable = paramBoolean1;
      required = paramBoolean2;
    }
    
    public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
      throws Throwable
    {
      String str = paramMethod.getName();
      if (str.equals("name")) {
        return name;
      }
      if (str.equals("namespace")) {
        return namespace;
      }
      if (str.equals("nillable")) {
        return Boolean.valueOf(nillable);
      }
      if (str.equals("required")) {
        return Boolean.valueOf(required);
      }
      throw new WebServiceException("Not handling " + str);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\AbstractWrapperBeanGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */