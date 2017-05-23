package com.processor;

import com.annotation.StringKeysGenerator;
import com.google.common.collect.ImmutableList;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
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
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.JavaFileObject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by vladimir.angeleski on 23/05/2017.
 */

@SupportedAnnotationTypes("com.annotation.StringKeysGenerator")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class StringKeysProcessor extends AbstractProcessor {

    private String generatedClassPackageName;
    private String generatedClassName;
    private String stringResourcesPath;

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        StringBuilder builder = new StringBuilder();

        Collection<? extends Element> annotatedElements = roundEnvironment.getElementsAnnotatedWith(StringKeysGenerator.class);
        List<TypeElement> types = new ImmutableList.Builder<TypeElement>()
            .addAll(ElementFilter.typesIn(annotatedElements))
            .build();



        if (types != null && !types.isEmpty()) {
            TypeElement annotation = types.get(0);
            generatedClassPackageName = annotation.getAnnotation(StringKeysGenerator.class).packageName();
            generatedClassName = annotation.getAnnotation(StringKeysGenerator.class).className();
            stringResourcesPath = annotation.getAnnotation(StringKeysGenerator.class).stringsPath();

            builder.append("// Generated from annotation params ( packageName = " + generatedClassPackageName + ", className = " + generatedClassName + ", path = " + stringResourcesPath + " )\n");
        }

        builder.append("package " + generatedClassPackageName + ";\n\n");

        builder.append("/**\n * Generated class for string.xml keys \n **/\n")
            .append("public class " + generatedClassName + " {\n\n");
        try {
            Map<String, String> keys = readStringsXML();
            generateFields(builder, keys);
        } catch (Exception e) {
            builder.append("// Error generating class \n");
            builder.append("// exception message = " + e + "\n");
        }

        builder.append("}\n"); // close class

        try {
            JavaFileObject source = processingEnv.getFiler().createSourceFile(generatedClassPackageName + "." + generatedClassName);
            Writer writer = source.openWriter();
            writer.write(builder.toString());
            writer.flush();
            writer.close();
        } catch (IOException ignored) {
        }

        return true;
    }

    private Map<String, String> readStringsXML() throws Exception {
        Map<String, String> keys = new HashMap<>();

        File file = new File(new File
            (stringResourcesPath).getAbsolutePath());

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

    private void generateFields(StringBuilder builder, Map<String, String> keys) {
        for (Map.Entry<String, String> entry : keys.entrySet()) {
            builder.append("\tpublic static final String " + entry.getKey() + " = \"" + entry.getKey() + "\";\n");
        }
    }
}
