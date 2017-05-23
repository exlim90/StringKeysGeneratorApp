package com.processor;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by vladimir.angeleski on 23/05/2017.
 */

@SupportedAnnotationTypes("com.annotation.StringKeysGenerator")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class StringKeysProcessor extends AbstractProcessor {

    public static final String GENERATED_CLASS_PACKAGE_NAME = "com.vladimir.generated";
    public static final String GENERATED_CLASS_NAME = "TranslationKeys";
    public static final String STRING_RESOURCES_PATH = "app/src/main/res/values/strings.xml";

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        StringBuilder builder = new StringBuilder()
            .append("package " + GENERATED_CLASS_PACKAGE_NAME + ";\n\n")
            .append("/**\n * Generated class for string.xml keys \n **/\n")
            .append("public class " + GENERATED_CLASS_NAME + " {\n\n");

        try {
            Map<String, String> keys = readStringsXML(builder);
            generateFields(builder, keys);
        } catch (Exception e) {
            builder.append("// Error generating class \n");
            builder.append("// exception message = " + e + "\n");
        }

        builder.append("}\n"); // close class

        try {
            JavaFileObject source = processingEnv.getFiler().createSourceFile(GENERATED_CLASS_PACKAGE_NAME + "." + GENERATED_CLASS_NAME);

            Writer writer = source.openWriter();
            writer.write(builder.toString());
            writer.flush();
            writer.close();
        } catch (IOException ignored) {
        }

        return true;
    }

    private Map<String, String> readStringsXML(StringBuilder builder) throws Exception {
        Map<String, String> keys = new HashMap<>();

        File file = new File(new File
            (STRING_RESOURCES_PATH).getAbsolutePath());

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
