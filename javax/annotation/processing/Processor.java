package javax.annotation.processing;

import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

public abstract interface Processor
{
  public abstract Set<String> getSupportedOptions();
  
  public abstract Set<String> getSupportedAnnotationTypes();
  
  public abstract SourceVersion getSupportedSourceVersion();
  
  public abstract void init(ProcessingEnvironment paramProcessingEnvironment);
  
  public abstract boolean process(Set<? extends TypeElement> paramSet, RoundEnvironment paramRoundEnvironment);
  
  public abstract Iterable<? extends Completion> getCompletions(Element paramElement, AnnotationMirror paramAnnotationMirror, ExecutableElement paramExecutableElement, String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\annotation\processing\Processor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */