package com.processor;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by vladimir.angeleski on 23/05/2017.
 */

@SupportedAnnotationTypes("com.annotation.StringKeysGenerator")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class StringKeysProcessorJavaPoet extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {



        TypeSpec translationKeysClass = TypeSpec.classBuilder("TranslationKeysVladimir")
            .addModifiers(Modifier.PUBLIC)
            .build();

        try {
            Map<String, String> keys = readStringsXML();
            generateFields(keys ,translationKeysClass);
        } catch (Exception e) {

        }

        JavaFile javaFile = JavaFile.builder("com.vladimir.angeleski.generated", translationKeysClass)
            .build();

        try {
            javaFile.writeTo(System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    private void generateFields(Map<String, String> keys, TypeSpec translationKeysClass ) {

        List<FieldSpec> fields = new ArrayList<>();

        for (Map.Entry<String, String> entry : keys.entrySet()) {
            FieldSpec filed = FieldSpec.builder(String.class, entry.getKey())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .initializer(entry.getKey())
                .build();
            fields.add(filed);
        }
        translationKeysClass.fieldSpecs.addAll(fields);
    }

    private Map<String, String> readStringsXML() throws Exception {
        Map<String, String> keys = new HashMap<>();

        File file = new File(new File("app/src/main/res/values/strings.xml").getAbsolutePath());

        DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder();
        Document doc = dBuilder.parse(file);

        //resources element
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
}
