package com.xuzp.apidoc.utils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Set;

import static com.xuzp.apidoc.utils.Constants.DOT;
import static com.xuzp.apidoc.utils.Constants.JAVA;

/**
 * @author za-xuzhiping
 * @Date 2017/12/7
 * @Time 17:38
 */
@Slf4j
public class ClassHelper {

    private static Method addURL = initAddMethod();
    private static URLClassLoader classloader = (URLClassLoader) ClassLoader.getSystemClassLoader();

    public synchronized static void loadClassPath(String classPath) {
        if (StringUtils.isNotEmpty(classPath)) {
            String[] pathNames = classPath.split(Constants.SPLIT_MARK);
            for (String pathName : pathNames) {
                if (StringUtils.isNoneEmpty(pathName)) {
                    File file = FileUtils.getFile(pathName);
                    if (!file.exists()) {
                        log.warn("文件不存在, 路径={}", pathName);
                        continue;
                    }
                    loadClass(file);
                }
            }
        }
    }

    private static void loadClass(File file) {
        if(isJarFolder(file)) {
            for(File child: file.listFiles()) {
                loadClass(child);
            }
        } else {
            try {
                addURL.invoke(classloader, new Object[]{
                        file.toURI().toURL()
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isJarFolder(File file){
        if(!file.exists() || file.isFile()){
            return false;
        }
        for (File child : file.listFiles()) {
            if(!child.isFile() || !child.getName().endsWith(".jar")) {
                return false;
            }
        }
        return true;
    }

    private static Method initAddMethod() {
        try {
            Method add = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
            add.setAccessible(true);
            return add;
        } catch (Exception e) {
            log.error("加载方法出错, exception: {}", e);
        }
        return null;
    }

    public synchronized static Set<Class> loadServiceClasses(String servicePath) {
        Set<Class> ret = Sets.newHashSet();
        if (StringUtils.isNotEmpty(servicePath)) {
            String[] pathNames = servicePath.split(Constants.SPLIT_MARK);
            for (String pathName : pathNames) {
                if (StringUtils.isEmpty(pathName)) {
                    continue;
                }
                File file = FileUtils.getFile(servicePath);
                if (!file.exists()) {
                    log.error("servicePath指定的地址不存在, servicePath: {}", servicePath);
                    return Sets.newHashSet();
                }
                loadClassByFile(file, ret);
            }
        }
        return ret;
    }

    private static void loadClassByFile(File file, Set<Class> classSet) {
        if (file.isDirectory()) {
            Arrays.stream(file.listFiles()).forEach(f -> loadClassByFile(f, classSet));
        } else if (isJavaFile(file)) {
            try {
                CompilationUnit compilationUnit = JavaParser.parse(file);
                String className = getPackageName(compilationUnit) + file.getName().replaceFirst(".java", "");
                Class cls = classloader.loadClass(className);
                log.info("通过文件名加载类成功，class: {}", className);
                classSet.add(cls);
            } catch (Exception e) {
                log.error("通过文件名加载类出错，请检查它的class文件是否存在：file: {}， exception: {}", file.getAbsolutePath(), e);
            }
        }
    }

    public static boolean isJavaFile(File file) {
        return file.isFile() && file.getName().endsWith(DOT + JAVA);
    }

    public static String getPackageName(CompilationUnit compilationUnit) {
        String packageName = "";
        if (compilationUnit.getPackageDeclaration().isPresent()) {
            packageName = compilationUnit.getPackageDeclaration().get().getNameAsString() + DOT;
        }
        return packageName;
    }
}
