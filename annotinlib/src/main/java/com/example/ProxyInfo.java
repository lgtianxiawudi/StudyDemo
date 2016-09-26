package com.example;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * Created by ligang967 on 16/9/23.
 */

public class ProxyInfo {
    private Elements mElementUtils;
    private TypeElement mTypeElement;
    private String mPackageName="com.lgang.demo";
    private String mProxyClassName = "";
    private final String SUFFIX = "ViewInjector";

    public ProxyInfo(Elements mElementUtils, TypeElement typeElement) {
        this.mElementUtils = mElementUtils;
        this.mTypeElement = typeElement;
        //获取类名
        mProxyClassName = mTypeElement.getSimpleName()+"$$"+SUFFIX;
        //获取Package
        mPackageName = mElementUtils.getPackageOf(typeElement).getQualifiedName().toString();
//        mPackageName = getPackageName(mElementUtils,mTypeElement.getQualifiedName().toString());
    }

    //key为id，value为对应的成员变量
    public Map<Integer, VariableElement> mInjectElements = new HashMap<Integer, VariableElement>();

    public String generateJavaCode(){
        StringBuilder builder = new StringBuilder();
        builder.append("package " + mPackageName).append(";\n\n");
        builder.append("import com.example.ligang.commonlibrary.annition.*;\n");
        builder.append("public class ").append(mProxyClassName).append(" implements " + SUFFIX + "<" + mTypeElement.getQualifiedName() + ">");
        builder.append("\n{\n");
        generateMethod(builder);
        builder.append("\n}\n");
        return builder.toString();
    }
    private void generateMethod(StringBuilder builder){
        builder.append("public void inject("+mTypeElement.getQualifiedName()+" host , Object object )");
        builder.append("\n{\n");
        for(int id : mInjectElements.keySet()){
            VariableElement variableElement = mInjectElements.get(id);
            String name = variableElement.getSimpleName().toString();
            String type = variableElement.asType().toString() ;

            builder.append(" if(object instanceof android.app.Activity)");
            builder.append("\n{\n");
            builder.append("host."+name).append(" = ");
            builder.append("("+type+")(((android.app.Activity)object).findViewById("+id+"));");
            builder.append("\n}\n").append("else").append("\n{\n");
            builder.append("host."+name).append(" = ");
            builder.append("("+type+")(((android.view.View)object).findViewById("+id+"));");
            builder.append("\n}\n");
        }
        builder.append("\n}\n");
    }

    public static String getPackageName(Elements elementUtils, String qualifiedSuperClassName ) {
        TypeElement superClassname = elementUtils.getTypeElement(qualifiedSuperClassName);
        PackageElement pkg = elementUtils.getPackageOf(superClassname);
        if (pkg.isUnnamed()) {
            return "com.ligang.demo";
        }
        return pkg.getQualifiedName().toString();
    }

    public TypeElement getmTypeElement() {
        return mTypeElement;
    }

    public String getmProxyClassName() {
        return mProxyClassName;
    }
}
