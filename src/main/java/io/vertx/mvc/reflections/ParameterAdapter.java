package io.vertx.mvc.reflections;

import io.vertx.core.MultiMap;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.beanutils.PropertyUtils;

public class ParameterAdapter {

	public static final SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	static {
		parser.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object adaptParamToType(String value, Class<?> parameterClass) throws Exception {
    	if (value == null) {
    		return null;
    	}
        if (parameterClass.equals(String.class)) {
            return value;
        } else if (parameterClass.equals(Long.class)) {
            return Long.valueOf(value);
        } else if (parameterClass.equals(Integer.class)) {
            return Integer.valueOf(value);
        } else if (parameterClass.equals(Float.class)) {
        	return Float.valueOf(value);
        } else if (parameterClass.equals(Date.class)) {
            return parser.parse(value);
        } else if (parameterClass.isEnum()) {
        	return Enum.valueOf((Class<Enum>)parameterClass, value); 
        }
        return null;
    }
    
    public static Object adaptParamsToType(MultiMap params, Class<?> parameterClass) throws Exception {
    	Object instance = parameterClass.newInstance();
    	Field[] fields = parameterClass.getDeclaredFields();
    	for (Field field : fields) {
    		String requestValue = params.get(field.getName());
    		Object value = adaptParamToType(requestValue, field.getType());
    		PropertyUtils.setProperty(instance, field.getName(), value);
    	}
    	return instance;
    }
}
