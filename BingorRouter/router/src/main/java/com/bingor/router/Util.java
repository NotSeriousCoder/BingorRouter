package com.bingor.router;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by HXB on 2018/7/25.
 */
public class Util {
    List<Class<?>> interfaces;

    public Util() {
        this.interfaces = new ArrayList();
    }

    public boolean isMatchInterface(Class<?> fount, Class<?> target) {
        findAllInterface(target);
        if (!interfaces.isEmpty()) {
            for (Class cls : interfaces) {
                if (cls.getName().equals(fount.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void findAllInterface(Class<?> target) {
        Class[] ins = target.getInterfaces();
        if (ins != null && ins.length > 0) {
            for (Class cls : ins) {
                interfaces.add(cls);
                findAllInterface(cls);
            }
        }
    }


    public boolean isMatchClass(Class<?> fount, Class<?> target) {
        List<Class<?>> listSuperClass = findAllSuperClass(target);
        if (!listSuperClass.isEmpty()) {
            for (Class cls : listSuperClass) {
                if (cls.getName().equals(fount.getName())) {
                    return true;
                }
            }
        }
        return false;
    }


    public static List<Class<?>> findAllSuperClass(Class<?> target) {
        List<Class<?>> listSuperClass = new ArrayList();
        do {
            Class<?> superclass = target.getSuperclass();
            if (superclass == null) {
                break;
            }
            listSuperClass.add(superclass);
        } while (true);
        return listSuperClass;
    }

}
