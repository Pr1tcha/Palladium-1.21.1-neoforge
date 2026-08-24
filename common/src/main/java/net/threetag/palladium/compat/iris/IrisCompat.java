package net.threetag.palladium.compat.iris;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class IrisCompat {

    private static final Method GET_INSTANCE;
    private static final Method IS_SHADER_PACK_IN_USE;

    static {
        Method getInstance = null;
        Method isShaderPackInUse = null;

        try {
            var irisApi = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            getInstance = irisApi.getMethod("getInstance");
            isShaderPackInUse = irisApi.getMethod("isShaderPackInUse");
        } catch (ReflectiveOperationException | LinkageError ignored) {
        }

        GET_INSTANCE = getInstance;
        IS_SHADER_PACK_IN_USE = isShaderPackInUse;
    }

    public static boolean isShaderPackActive() {
        if (GET_INSTANCE == null || IS_SHADER_PACK_IN_USE == null) {
            return false;
        }

        try {
            var api = GET_INSTANCE.invoke(null);
            return Boolean.TRUE.equals(IS_SHADER_PACK_IN_USE.invoke(api));
        } catch (IllegalAccessException | InvocationTargetException ignored) {
            return false;
        }
    }

}
