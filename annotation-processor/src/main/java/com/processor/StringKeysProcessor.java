package com.processor;

import com.annotation.StringKeysGenerator;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by vladimir.angeleski on 23/05/2017.
 */

@SupportedAnnotationTypes("com.annotation.StringKeysGenerator")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class StringKeysProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        Collection<? extends Element> annotatedElements = roundEnvironment
            .getElementsAnnotatedWith(StringKeysGenerator.class);

        List<TypeElement> types = new ArrayList<>(ElementFilter.typesIn(annotatedElements));

        for (TypeElement element : types) {
            processAnnotation(element);
        }

        return true;
    }

    private void processAnnotation(TypeElement element) {
        AnnotationParams params = getAnnotationParams(element);

        TypeSpec.Builder translationKeysClass = TypeSpec.classBuilder(params.className)
            .addModifiers(Modifier.PUBLIC);

        processingEnv.getMessager()
            .printMessage(Diagnostic.Kind.WARNING,
                "packageName = " + params.packageName
                    + ", className = " + params.className
                    + ", path = " + params.resourcesPath
            );

        translationKeysClass.addJavadoc(" Generated class \n");
        translationKeysClass.addJavadoc(" Annotation params: packageName = " + params.packageName
            + ", className = " + params.className
            + ", path = " + params.resourcesPath);

        try {
            Map<String, String> keys = readStringsXML(params.resourcesPath);
            generateFields(keys, translationKeysClass);
        } catch (Exception e) {
            processingEnv.getMessager()
                .printMessage(Diagnostic.Kind.ERROR, "Error happened. Error Message : " + e.getMessage());
        }

        saveSourceFile(params, translationKeysClass);
    }

    private void saveSourceFile(AnnotationParams params, TypeSpec.Builder translationKeysClass) {
        JavaFile javaFile = JavaFile.builder(params.packageName, translationKeysClass.build())
            .build();

        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                "Could not write generated class " + "" + ": " + e);
        }
    }

    private AnnotationParams getAnnotationParams(TypeElement annotation) {
        AnnotationParams params = new AnnotationParams();
        params.packageName = annotation.getAnnotation(StringKeysGenerator.class).packageName();
        params.className = annotation.getAnnotation(StringKeysGenerator.class).className();
        params.resourcesPath = annotation.getAnnotation(StringKeysGenerator.class).stringsPath();

        return params;
    }


    private void generateFields(Map<String, String> keys, TypeSpec.Builder translationKeysClass) {
        for (Map.Entry<String, String> entry : keys.entrySet()) {
            FieldSpec filed = FieldSpec.builder(String.class, entry.getKey())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL,Modifier.STATIC)
                .initializer("\"" + entry.getKey() + "\"")
                .build();
            translationKeysClass.addField(filed);
        }
    }

    private Map<String, String> readStringsXML(String resourcePath) throws Exception {
        Map<String, String> keys = new HashMap<>();

        File file = new File(new File(resourcePath).getAbsolutePath());

        DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder();
        Document doc = dBuilder.parse(file);

        org.w3c.dom.Element resourcesElement = doc.getDocumentElement();
        if (resourcesElement.hasChildNodes()) {
            printNote(resourcesElement.getChildNodes(), keys);
        }

        return keys;
    }

    private static void printNote(NodeList nodeList, Map<String, String> keys) {
        for (int count = 0; count < nodeList.getLength(); count++) {
            Node tempNode = nodeList.item(count);
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                keys.put(tempNode.getAttributes().getNamedItem("name").getNodeValue(), tempNode.getTextContent());
            }
        }
    }

    public static class AnnotationParams {
        String className;
        String packageName;
        String resourcesPath;
    }
}
