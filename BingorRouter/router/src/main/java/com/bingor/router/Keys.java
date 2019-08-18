package com.bingor.router;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Bingor on 2019/7/27.
 */
public class Keys {
    public static String getKey(String key) {
        Class clsKeys = Keys.class;
        try {
            Field field = clsKeys.getField(key);
            return (String) field.get(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getAdderNames() {
        List<String> names = new LinkedList<>();
        Class clsKeys = Keys.class;
        Field[] fields = clsKeys.getFields();
        for (Field field : fields) {
            if (field.getName().toString().contains("ROUTERADDER_")) {
                try {
                    names.add((String) field.get(null));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return names;
    }

    // ----------------- Keys is under here -----------------
   public static final String KEY_ACTIVITY_COM_BINGOR_TESTLIB_AVTIVITY2222 = "COM_BINGOR_TESTLIB_AVTIVITY2222";
   public static final String KEY_ACTIVITY_COM_BINGOR_TEST2LIB_AVTIVITY4TEST2LIB = "COM_BINGOR_TEST2LIB_AVTIVITY4TEST2LIB";
   public static final String KEY_ACTIVITY_COM_BINGOR_TESTLIB_AVTIVITY4TESTLIB = "COM_BINGOR_TESTLIB_AVTIVITY4TESTLIB";
    public static final String KEY_ACTIVITY_COM_BINGOR_BINGORROUTER_MAIN2ACTIVITY = "COM_BINGOR_BINGORROUTER_MAIN2ACTIVITY";
    public static final String KEY_ACTIVITY_COM_BINGOR_BINGORROUTER_TESTACTIVITY = "COM_BINGOR_BINGORROUTER_TESTACTIVITY";
    public static final String KEY_ACTIVITY_COM_BINGOR_BINGORROUTER_MAINACTIVITY = "COM_BINGOR_BINGORROUTER_MAINACTIVITY";
    public static final String KEY_SERVICE_COM_BINGOR_TESTLIB_TESTSERVICE = "COM_BINGOR_TESTLIB_TESTSERVICE";


    // ----------------- AdderName is under here -----------------
   public static final String ROUTERADDER_TEST2LIB = "RouterAdder_test2Lib";
    public static final String ROUTERADDER_APP = "RouterAdder_app";
    public static final String ROUTERADDER_TESTLIB = "RouterAdder_testLib";
}


// ----------------- File end -----------------
