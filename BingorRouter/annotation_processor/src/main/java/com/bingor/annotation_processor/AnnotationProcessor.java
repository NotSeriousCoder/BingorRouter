package com.bingor.annotation_processor;

import com.bingor.annotation.App;
import com.bingor.annotation.Module;
import com.bingor.annotation.RouterNode;
import com.google.auto.service.AutoService;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {
    private Filer filer;
    private Elements elementUtils;
    private Set<? extends Element> routerNodes, module, app;
    private Map<String, String> keys;
    private String adderClassName;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        elementUtils = processingEnvironment.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotationTypes = new HashSet<>();
        supportedAnnotationTypes.add(RouterNode.class.getCanonicalName());
        supportedAnnotationTypes.add(Module.class.getCanonicalName());
        supportedAnnotationTypes.add(App.class.getCanonicalName());
        return supportedAnnotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return processingEnv.getSourceVersion();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        routerNodes = roundEnvironment.getElementsAnnotatedWith(RouterNode.class);
        module = roundEnvironment.getElementsAnnotatedWith(Module.class);
        app = roundEnvironment.getElementsAnnotatedWith(App.class);

        keys = new HashMap<>();
        String modu = "";
        for (Element mo : module) {
            modu = (((TypeElement) mo).getAnnotation(Module.class)).module();
            break;
        }
        LogUtil.clear(modu);
        try {
            initAdder(modu);
            initKeys();
            addAdderName();
            setupRouterNoteInitializer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogUtil.close();
        return true;
    }

    private void addAdderName() {
        if (!Utils.textIsEmpty(adderClassName)) {
            File target = JavaFileHelper.searchJavaFile("com.bingor.router.Keys", "src", "^((?!\\.).)*$", "java");
            if (target != null) {
                JavaFileHelper.addAdderName(target, adderClassName);
            } else {
                LogUtil.log("target is null");
            }
        }
    }

    private void initAdder(String moduleName) throws IOException {
        if (!Utils.textIsEmpty(moduleName)) {
            JavaFileObject javaFileObject = filer.createSourceFile(Config.FILE_PREFIX + moduleName);
            javaFileObject.delete();
            PrintWriter printWriter = new PrintWriter(javaFileObject.openWriter());

            adderClassName = Config.FILE_PREFIX + moduleName;

            // 类的开头
            printWriter.println("package " + Config.PACKAGE_NAME + ";");
            printWriter.println("public class " + adderClassName + "{");
            printWriter.println("   public static void initNodes(){");
            printWriter.println("       String key;");
            for (Element element : routerNodes) {
                TypeElement typeElement = (TypeElement) element;
                RouterNode routerNode = typeElement.getAnnotation(RouterNode.class);
                int routeType = routerNode.nodeType();
                String action = routerNode.action();

                String qName = typeElement.getQualifiedName().toString();
                LogUtil.log(qName);
                String[] kv = createKey(qName, routeType);
                keys.put(kv[0], kv[1]);
                printWriter.println("       key = com.bingor.router.Keys.getKey(\"" + kv[0] + "\");");
                // 这里需要判断Key是不是为空
                printWriter.println("       if(key != null){");

                switch (routeType) {
                    case RouterNode.NODE_TYPE_ACTIVITY:
                    case RouterNode.NODE_TYPE_RECEIVER:
                    case RouterNode.NODE_TYPE_SERVICE:
                        if (Utils.textIsEmpty(action)) {
                            printWriter.println("           com.bingor.router.Router.addNode(key , new com.bingor.router.node.IntentRouterNode( " + qName + ".class" + " ));");
                        } else {
                            printWriter.println("           com.bingor.router.Router.addNode(key , new com.bingor.router.node.IntentRouterNode( " + qName + ".class, " + action + " ));");
                        }
                        break;
                    case RouterNode.NODE_TYPE_UTIL_JSON:
                    case RouterNode.NODE_TYPE_UTIL_JSON_ACTIVITY_CALLBACK:
                    case RouterNode.NODE_TYPE_UTIL_JSON_CALLBACK:
                    case RouterNode.NODE_TYPE_UTIL_JSON_CONTEXT_CALLBACK:
                        printWriter.println("           com.bingor.router.Router.addNode(key, new com.bingor.router.node.UtilRouterNode(" + qName + ".class));");
                        break;
                }

                printWriter.println("       }");
            }

            //类的结尾
            printWriter.println("   }");
            printWriter.println("}");

            printWriter.close();
        }
    }


    private void initKeys() {
        if (!keys.isEmpty()) {
            File target = JavaFileHelper.searchJavaFile("com.bingor.router.Keys", "src", "^((?!\\.).)*$", "java");
            if (target != null) {
                Map<String, String> tempKeys = new HashMap<>();
                tempKeys.putAll(keys);
                JavaFileHelper.addKeys(target, tempKeys);
            } else {
                LogUtil.log("target is null");
            }
        }
    }

    private void setupRouterNoteInitializer() throws IOException {
        if (app != null && !app.isEmpty()) {
            JavaFileObject javaFileObject = filer.createSourceFile(Config.ROUTERNOTE_INITIALIZER);
            javaFileObject.delete();
            PrintWriter printWriter = new PrintWriter(javaFileObject.openWriter());

            // 类的开头
            printWriter.println("package " + Config.PACKAGE_NAME + ";");
            printWriter.println("public class " + Config.ROUTERNOTE_INITIALIZER + "{");
            printWriter.println("   public static void init(){");

            printWriter.println("        java.util.List<String> names = com.bingor.router.Keys.getAdderNames();");
            printWriter.println("        if (names != null && !names.isEmpty()) {");

            printWriter.println("            for (String name : names) {");


            printWriter.println("                try {\n" +
                    "                    Class cls = Class.forName(\"com.bingor.router.\" + name);\n" +
                    "                    java.lang.reflect.Method method = cls.getMethod(\"initNodes\");\n" +
                    "                    method.invoke(null);\n" +
                    "                } catch (ClassNotFoundException e) {\n" +
                    "                    e.printStackTrace();\n" +
                    "                } catch (NoSuchMethodException e) {\n" +
                    "                    e.printStackTrace();\n" +
                    "                } catch (IllegalAccessException e) {\n" +
                    "                    e.printStackTrace();\n" +
                    "                } catch (java.lang.reflect.InvocationTargetException e) {\n" +
                    "                    e.printStackTrace();\n" +
                    "                }");

            printWriter.println("            }");

            printWriter.println("        }");

            //类的结尾
            printWriter.println("   }");
            printWriter.println("}");

            printWriter.close();
        }
    }


    private String[] createKey(String packageStr, int noteType) {
        String key = "key_";
        String value = packageStr.replace(".", "_").toUpperCase();
        switch (noteType) {
            case RouterNode.NODE_TYPE_ACTIVITY:
                key += "activity_" + value;
                break;
            case RouterNode.NODE_TYPE_RECEIVER:
                key += "receiver_" + value;
                break;
            case RouterNode.NODE_TYPE_SERVICE:
                key += "service_" + value;
                break;
            case RouterNode.NODE_TYPE_UTIL_JSON:
            case RouterNode.NODE_TYPE_UTIL_JSON_ACTIVITY_CALLBACK:
            case RouterNode.NODE_TYPE_UTIL_JSON_CALLBACK:
            case RouterNode.NODE_TYPE_UTIL_JSON_CONTEXT_CALLBACK:
                key += "util_json_" + value;
                break;
        }
        return new String[]{key.toUpperCase(), value};
    }

}