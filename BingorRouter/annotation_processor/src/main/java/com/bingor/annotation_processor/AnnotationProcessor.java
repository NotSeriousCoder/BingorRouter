package com.bingor.annotation_processor;

import com.bingor.annotation.Module;
import com.bingor.annotation.RouterNode;
import com.google.auto.service.AutoService;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.ElementType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Completion;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {
    private Filer filer;
    private Elements elementUtils;
    private Set<? extends Element> routerNodes, module;
    private Map<String, String> keys;

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
        String modu = "";
        for (Element mo : module) {
            modu = (((TypeElement) mo).getAnnotation(Module.class)).module();
            break;
        }
        LogUtil.clear(modu);
        initKeys();
        initAdder();
        LogUtil.close();
        return false;
    }

    private void initAdder() {
        if (module != null && !module.isEmpty()) {
            for (Element element : module) {
                TypeElement typeElement = (TypeElement) element;
                Module module = typeElement.getAnnotation(Module.class);
                RouterNode routerNode = typeElement.getAnnotation(RouterNode.class);
                String moduleName = module.module();
                int routeType = routerNode.nodeType();
                String action = routerNode.action();

                String qName = typeElement.getQualifiedName().toString();
                try {
                    JavaFileObject javaFileObject = filer.createSourceFile(Config.FILE_PREFIX + moduleName);
                    javaFileObject.delete();
                    PrintWriter printWriter = new PrintWriter(javaFileObject.openWriter());
                    printWriter.println("package " + Config.PACKAGE_NAME + ";");
                    printWriter.println("public class " + Config.FILE_PREFIX + moduleName + "{");
                    printWriter.println("   public static void initNodes(){");
                    for (String key : keys.keySet()) {
                        switch (routeType) {
                            case RouterNode.NODE_TYPE_ACTIVITY:
                            case RouterNode.NODE_TYPE_RECEIVER:
                            case RouterNode.NODE_TYPE_SERVICE:
                                if (Utils.textIsEmpty(action)) {
                                    printWriter.println("       com.bingor.router.Router.addNode(com.bingor.router.Keys." + key + ", new com.bingor.router.node.IntentRouterNode( " + qName + ".class" + " ));");
                                } else {
                                    printWriter.println("       com.bingor.router.Router.addNode(com.bingor.router.Keys." + key + ", new com.bingor.router.node.IntentRouterNode( " + qName + ".class, " + action + " ));");
                                }
                                break;
                            case RouterNode.NODE_TYPE_UTIL_JSON:
                            case RouterNode.NODE_TYPE_UTIL_JSON_ACTIVITY_CALLBACK:
                            case RouterNode.NODE_TYPE_UTIL_JSON_CALLBACK:
                            case RouterNode.NODE_TYPE_UTIL_JSON_CONTEXT_CALLBACK:
                                printWriter.println("       com.bingor.router.Router.addNode(\"" + key + "\" ," + "new com.bingor.router.node.UtilRouterNode(" + qName + ".class));");
                                break;
                        }
                    }
                    printWriter.println("   }");
                    printWriter.println("}");


                    printWriter.close();


//                    JavaFileObject javaFileObject2 = filer.createSourceFile("kapt/"+Config.FILE_PREFIX + moduleName+"2");
//                    javaFileObject2.delete();
//                    PrintWriter printWriter2 = new PrintWriter(javaFileObject2.openWriter());
//                    printWriter2.println("//aaaaaaaa");
//                    printWriter2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void initKeys() {
        LogUtil.log("initKeys used=====");
        keys = new HashMap<>();
        if (routerNodes != null && !routerNodes.isEmpty()) {
            for (Element element : routerNodes) {
                TypeElement typeElement = (TypeElement) element;
                RouterNode routerNode = typeElement.getAnnotation(RouterNode.class);
                String key = "key_";
                String value = typeElement.getQualifiedName().toString().replace(".", "_").toUpperCase();
                switch (routerNode.nodeType()) {
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
                key = key.toUpperCase();
                keys.put(key, value);
            }

            LogUtil.log("keys == \n" + keys.toString());


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

}