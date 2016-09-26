package com.example;

import com.google.auto.service.AutoService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

/**
 * Created by ligang967 on 16/9/23.
 */
@AutoService(Processor.class)
public class MyProcessor extends AbstractProcessor {
    private Filer mFileUtils;
    private Elements mElementUtils;
    private Messager mMessager;
    private Map<String, ProxyInfo> mProxyMap = new HashMap<String, ProxyInfo>();
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv){
        super.init(processingEnv);
        mFileUtils = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
    }
    @Override
    public Set<String> getSupportedAnnotationTypes(){//支持的注解类型
        Set<String> annotationTypes = new LinkedHashSet<String>();
        annotationTypes.add(BindView.class.getCanonicalName());
        return annotationTypes;
    }
    @Override
    public SourceVersion getSupportedSourceVersion(){//支持的源码版本
        return SourceVersion.latestSupported();
    }
    /**
     * {@inheritDoc}
     *
     * @param annotations
     * @param roundEnv
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mProxyMap.clear();
        //获取所有BindVIew的类
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(BindView.class);
        //一、收集信息
        for (Element element : elements){
            //检查element类型
            if (!checkAnnotationUseValid(element)){
                return false;
            }
            //field type 获取使用的成员变量
            VariableElement variableElement = (VariableElement) element;
            //class type 获取使用的类
            TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();//TypeElement
            String qualifiedName = typeElement.getQualifiedName().toString();

            ProxyInfo proxyInfo = mProxyMap.get(qualifiedName);
            if (proxyInfo == null){
                proxyInfo = new ProxyInfo(mElementUtils, typeElement);
                mProxyMap.put(qualifiedName, proxyInfo);
            }
            BindView annotation = variableElement.getAnnotation(BindView.class);
            int id = annotation.value();
            proxyInfo.mInjectElements.put(id, variableElement);
        }
        for(String key : mProxyMap.keySet()){
            ProxyInfo proxyInfo = mProxyMap.get(key);
            JavaFileObject sourceFile = null;
            Writer writer = null;
            try {
                sourceFile = mFileUtils.createSourceFile(
                        proxyInfo.getmProxyClassName(), proxyInfo.getmTypeElement());
                writer = sourceFile.openWriter();
                writer.write(proxyInfo.generateJavaCode());
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (writer!=null){
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return true;
    }
    private boolean checkAnnotationUseValid(Element element) {
        return element.getKind()== ElementKind.FIELD;
    }
}
