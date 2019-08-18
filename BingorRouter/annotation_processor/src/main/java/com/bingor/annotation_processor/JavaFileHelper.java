package com.bingor.annotation_processor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by HXB on 2018/7/18.
 */
public class JavaFileHelper {
    private static List<File> dirResult = new ArrayList<>();
    private static List<File> javaFileResult = new ArrayList<>();
    private static File directory = new File("");

    /**
     * 根据类名寻找java文件
     *
     * @param classFullName
     * @param targetDir
     * @param ignorePattern
     * @param targetFileType
     * @return
     */
    public static File searchJavaFile(String classFullName, String targetDir, String ignorePattern, String targetFileType) {
        directory = new File(directory.getAbsolutePath());

        if (targetDir == null || targetDir.equals("")) {
            dirResult.add(directory);
        } else {
            searchDir(directory, targetDir, ignorePattern);
        }
//        LogUtil.log(dirResult.toString());

        String[] nameArr = classFullName.split("\\.");
        String simpleName = nameArr[nameArr.length - 1] + "." + targetFileType;
        for (File temp : dirResult) {
            search(temp, simpleName);
        }

        return findMatchFileByPackageName(classFullName.substring(0, classFullName.lastIndexOf(".")));
    }

    public static void searchDir(File dir, String targetDirName, String pattern) {
        if (!dir.isDirectory()) {
            return;
        }
        if (dir.getName().equals(targetDirName)) {
            dirResult.add(dir);
            return;
        }
        // ^((?!.).)*$
        for (File temp : dir.listFiles()) {
            if (temp != null) {
                String name = temp.getName();
                if (temp.isDirectory() && Pattern.matches(pattern, name)) {
                    searchDir(temp, targetDirName, pattern);
                }
            }
        }

    }

    public static void search(File file, String classSimpleName) {
        if (file.isDirectory()) {
            for (File temp : file.listFiles()) {
                // LogUtil.log(temp.getName());
                search(temp, classSimpleName);
            }
            // LogUtil.log("======================");
        } else if (file.getName().equals(classSimpleName)) {
            // LogUtil.log("match");
            javaFileResult.add(file);
        } else {
            // LogUtil.log(file.getName());
        }
    }

    public static void addExtends(File file, Class cls, String[] fields, String[] overrideMethods) {
        int step = 1;
        // 缓存代码，等待一起处理
        StringBuffer codeCache = new StringBuffer();
        if (file == null || !file.exists() || cls == null) {
            return;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line = "";
            do {
                line = br.readLine();
                if (line == null) {
                    break;
                }
                switch (step) {
                    case 1:
                        // 写入package
                        // 写入import cls
                        codeCache.append(line);
                        if (codeCache.toString().contains(";")) {
                            baos.write(codeCache.toString().getBytes());
                            baos.write("\r\n".getBytes());
                            line = "import " + cls.getCanonicalName() + ";";
                            baos.write(line.getBytes());
                            baos.write("\r\n".getBytes());
                            baos.flush();
                            codeCache.delete(0, codeCache.length());
                            step = 2;
                        }
                        break;
                    case 2:
                        if (line.equals("") || line.contains("import")) {
                            baos.write(line.getBytes());
                            baos.write("\r\n".getBytes());
                            continue;
                        }
                        codeCache.append(line);
                        if (codeCache.toString().contains("{")) {
                            int index = 0;
                            if (!codeCache.toString().contains("extends") && !codeCache.toString().contains("implements")) {
                                index = codeCache.indexOf("{");
                                codeCache.insert(index, "extends " + cls.getSimpleName());
                                baos.write(codeCache.toString().getBytes());
                                baos.write("\r\n".getBytes());
                            } else if (!codeCache.toString().contains("extends")
                                    && codeCache.toString().contains("implements")) {
                                index = codeCache.indexOf("implements");
                                codeCache.insert(index, "extends " + cls.getSimpleName() + " ");
                                baos.write(codeCache.toString().getBytes());
                                baos.write("\r\n".getBytes());
                                baos.flush();
                            }
                            codeCache.delete(0, codeCache.length());
                            step = 3;
                        }
                        break;
                    case 3:
                        if (fields != null && fields.length > 0) {
                            for (String field : fields) {
                                baos.write(field.getBytes());
                                baos.write("\r\n".getBytes());
                            }
                        }
                        if (overrideMethods != null && overrideMethods.length > 0) {
                            for (String method : overrideMethods) {
                                baos.write(method.getBytes());
                                baos.write("\r\n".getBytes());
                            }
                        }
                        step = 4;
                        break;
                    default:
                        baos.write(line.getBytes());
                        baos.write("\r\n".getBytes());
                        baos.flush();
                        break;
                }
            } while (line != null);

            if (br != null) {
                br.close();
                br = null;
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            FileOutputStream fos = new FileOutputStream(file);
            byte[] data = new byte[1024];
            int len = 0;
            while ((len = bais.read(data)) != -1) {
                fos.write(data, 0, len);
                fos.flush();
            }

            if (fos != null) {
                fos.flush();
                fos.close();
            }

            if (bais != null) {
                bais.close();
            }

            if (baos != null) {
                baos.flush();
                baos.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void addImplements(File file, Class cls, String[] fields, String[] overrideMethods) {
//        LogUtil.log("addImplements  start");
        int step = 1;
        // 缓存代码，等待一起处理
        StringBuffer codeCache = new StringBuffer();
        boolean alreadyImp = true;
        if (file == null || !file.exists() || cls == null) {
            if (file == null) {
                LogUtil.log("file is null");
            }
            if (!file.exists()) {
                LogUtil.log("file not exesit");
            }
            if (cls == null) {
                LogUtil.log("cls is null");
            }
            return;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line = "";
            do {
                line = br.readLine();
                if (line == null) {
                    break;
                }
//                LogUtil.log("step == " + step);
                switch (step) {
                    case 1:
                        // 写入package
                        // 写入import cls
                        codeCache.append(line);
                        if (codeCache.toString().contains(";")) {
                            baos.write(codeCache.toString().getBytes());
                            baos.write("\r\n".getBytes());
                            line = "import " + cls.getCanonicalName() + ";";
                            baos.write(line.getBytes());
                            baos.write("\r\n".getBytes());
                            baos.flush();
                            codeCache.delete(0, codeCache.length());
                            step = 2;
                        }
                        break;
                    case 2:
                        if (line.equals("") || line.contains("import")) {
                            baos.write(line.getBytes());
                            baos.write("\r\n".getBytes());
                            continue;
                        }
                        codeCache.append(line);
                        if (codeCache.toString().contains("{")) {
                            int index = 0;
                            if (!codeCache.toString().contains("implements")) {
                                index = codeCache.indexOf("{");
                                codeCache.insert(index, " implements " + cls.getSimpleName());
                                baos.write(codeCache.toString().getBytes());
                                baos.write("\r\n".getBytes());
                            } else if (codeCache.toString().contains("implements")) {
                                if (!codeCache.toString().contains(cls.getSimpleName())) {
                                    index = codeCache.indexOf("implements") + 10;
                                    codeCache.insert(index, " " + cls.getSimpleName() + ", ");
                                    alreadyImp = false;
                                }
                                baos.write(codeCache.toString().getBytes());
                                baos.write("\r\n".getBytes());
                                baos.flush();
                            }
                            codeCache.delete(0, codeCache.length());
                            step = 3;
                        }
                        break;
                    case 3:
                        if (fields != null && fields.length > 0) {
                            for (String field : fields) {
                                baos.write(field.getBytes());
                                baos.write("\r\n".getBytes());
                            }
                        }
                        if (!alreadyImp && overrideMethods != null && overrideMethods.length > 0) {
                            for (String method : overrideMethods) {
                                baos.write(method.getBytes());
                                baos.write("\r\n".getBytes());
                            }
                        }
                        step = 4;
                        break;
                    default:
                        baos.write(line.getBytes());
                        baos.write("\r\n".getBytes());
                        baos.flush();
                        break;
                }
            } while (line != null);

            if (br != null) {
                br.close();
                br = null;
            }
//            LogUtil.log("read complie");
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            FileOutputStream fos = new FileOutputStream(new File("E:\\abc", "result.txt"));
            byte[] data = new byte[1024];
            int len = 0;
            while ((len = bais.read(data)) != -1) {
                fos.write(data, 0, len);
                fos.flush();
            }

            if (fos != null) {
                fos.flush();
                fos.close();
            }

            if (bais != null) {
                bais.close();
            }

            if (baos != null) {
                baos.flush();
                baos.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtil.log(e.getCause().toString());
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.log(e.getCause().toString());
        }
    }


    public static void addKeys(File file, Map<String, String> keys) {
        if (file == null || !file.exists()) {
            return;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            br.mark((int) (file.length() + 1));
            do {
                // Key.java 里面已经有的key，不要重复写入，需要从keys里面去掉
                String delKey = null;
                line = br.readLine();
                if (line == null) {
                    break;
                }
                for (String key : keys.keySet()) {
                    if (line.contains(key)) {
                        delKey = key;
                        break;
                    }
                }
                keys.remove(delKey);
            } while (!line.contains("----------------- File end -----------------"));
            br.reset();
            do {
                line = br.readLine();
                baos.write(line.getBytes());
                baos.write("\r\n".getBytes());
                if (line.contains("----------------- Keys is under here -----------------")) {
                    for (String key : keys.keySet()) {
                        baos.write(("   public static final String " + key + " = \"" + keys.get(key) + "\";").getBytes());
                        baos.write("\r\n".getBytes());
                    }
                }
            } while (!line.contains("----------------- File end -----------------"));

            if (br != null) {
                br.close();
                br = null;
            }
            if (fr != null) {
                fr.close();
                fr = null;
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            FileOutputStream fos = new FileOutputStream(file);
            byte[] data = new byte[1024];
            int len = 0;
            while ((len = bais.read(data)) != -1) {
                fos.write(data, 0, len);
                fos.flush();
            }
            if (fos != null) {
                fos.flush();
                fos.close();
            }
            if (bais != null) {
                bais.close();
            }
            if (baos != null) {
                baos.flush();
                baos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtil.log(e.getCause().toString());
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.log(e.toString());
        } finally {
            LogUtil.log("Keys write alreadly -- " + System.currentTimeMillis());
        }
    }

    public static void addAdderName(File file, String name) {
        if (file == null || !file.exists()) {
            return;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            boolean needAdd = true;
            br.mark((int) (file.length() + 1));
            do {
                // Key.java 里面已经有的key，不要重复写入，需要从keys里面去掉
                line = br.readLine();
                if (line == null) {
                    break;
                }
                if (line.contains(name)) {
                    needAdd = false;
                    break;
                }
            } while (!line.contains("----------------- File end -----------------"));
            br.reset();
            do {
                line = br.readLine();
                baos.write(line.getBytes());
                baos.write("\r\n".getBytes());
                if (needAdd && line.contains("// ----------------- AdderName is under here -----------------")) {
                        baos.write(("   public static final String " + name.toUpperCase() + " = \"" + name + "\";").getBytes());
                        baos.write("\r\n".getBytes());
                }
            } while (!line.contains("----------------- File end -----------------"));

            if (br != null) {
                br.close();
                br = null;
            }
            if (fr != null) {
                fr.close();
                fr = null;
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            FileOutputStream fos = new FileOutputStream(file);
            byte[] data = new byte[1024];
            int len = 0;
            while ((len = bais.read(data)) != -1) {
                fos.write(data, 0, len);
                fos.flush();
            }
            if (fos != null) {
                fos.flush();
                fos.close();
            }
            if (bais != null) {
                bais.close();
            }
            if (baos != null) {
                baos.flush();
                baos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtil.log(e.getCause().toString());
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.log(e.toString());
        } finally {
            LogUtil.log("Keys write alreadly -- " + System.currentTimeMillis());
        }
    }


    private static File findMatchFileByPackageName(String packageName) {
        try {
            for (File temp : javaFileResult) {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(temp));
                String pa = "";
                do {
                    pa = bufferedReader.readLine();
                } while (!pa.contains("package"));
                if (pa.contains(packageName)) {
                    return temp;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 找寻File的对应项目根目录
     *
     * @return
     */
    public static File searchRootDir(File file) {
        boolean get = false;
        File target = new File(file.getAbsolutePath());
        do {
            boolean app = false, build = false;
            target = target.getParentFile();
            String[] filesStr = file.list();
            for (String temp : filesStr) {
                if (temp.equals("app")) {
                    app = true;
                }
                if (temp.equals("build")) {
                    build = true;
                }
            }
            if (app && build) {
                get = true;
                break;
            }
        } while (target != null);
        if (get) {
            return target;
        }
        return null;
    }

    public static void writeNodeString(String node) {
        File file = new File(new File("").getAbsolutePath(), Config.ROUTER_NODES);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String temp = "";
            boolean has = false;
            do {
                temp = br.readLine();
                if (temp.equals(node)) {
                    has = true;
                }
            } while (temp != null);
            br.close();

            if (has) {
                return;
            }
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            bw.write(node);
            bw.write("\r\n");
            bw.flush();
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> readNodeString() {
        File file = new File(new File("").getAbsolutePath(), Config.ROUTER_NODES);
        List<String> nodes = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String node = "";
            do {
                node = br.readLine();
                if (!node.equals("")) {
                    nodes.add(node);
                }
            } while (node != null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        file.delete();
        return nodes;
    }

}
