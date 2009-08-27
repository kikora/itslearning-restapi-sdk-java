/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package itslearning.platform.restApi.sdk.common;

import com.sun.org.apache.xml.internal.utils.StringComparable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.reflect.Reflection;

/**
 * Handles page parameters.
 * @author Amund Trovåg
 */
public class RequestParamsHandler<TRequestParams extends IRequestParams>
{
    // TODO: it seems that this reflection bit might be hard in java

    /**
     * Gets the parameters from the query string.
     * @param <TRequestParams>
     * @param queryStringParams
     * @param clazz
     * @return
     */
    public static <TRequestParams> TRequestParams getParams(Map queryStringParams, Class<TRequestParams> clazz)
    {

        TRequestParams reqParams = null;
        try
        {
            reqParams = clazz.newInstance();
        } catch (InstantiationException ex)
        {
            Logger.getLogger(RequestParamsHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex)
        {
            Logger.getLogger(RequestParamsHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        Method[] methods = reqParams.getClass().getMethods();
        for (Method m : methods)
        {
            // 1. Only use the setters
            if (m.getName().startsWith("set"))
            {
                // 2. If setter, get the fieldname in uppercase and see if it's in queryStringParams map
                String fieldName = m.getName().replaceFirst("set", "").toUpperCase();

                for (Object s : queryStringParams.keySet())
                {
                    boolean found = false;
                    String upperString = ((String) s).toUpperCase();
                    if (upperString.equals(fieldName))
                    {
                        found = true;
                        // 3. If match, get value from reqParams as string with case-insensitive comparison(!)

                        Object value = queryStringParams.get(s);
                        try
                        {
                            // 4. cast to correct class or primitive, and
                            // 5. invoke setter with value
                            m.invoke(reqParams, value);
                        } catch (IllegalAccessException ex)
                        {
                            Logger.getLogger(RequestParamsHandler.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IllegalArgumentException ex)
                        {
                            Logger.getLogger(RequestParamsHandler.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InvocationTargetException ex)
                        {
                            Logger.getLogger(RequestParamsHandler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if(found)
                        break;
                }


            }

        }

        return reqParams;

    /*var requestParams = Activator.CreateInstance<TRequestParams>();

    foreach (var property in GetProperties())
    {
    string value = queryStringParams[property.Name];
    if (!string.IsNullOrEmpty(value))
    {
    Type propertyType = property.PropertyType;

    // Nullable<T> => T
    if (propertyType.IsGenericType && propertyType.GetGenericTypeDefinition() == typeof (Nullable<>))
    {
    propertyType = propertyType.GetGenericArguments()[0];
    }

    bool castSuccessed;
    object castedValue = CastToPrimitiveType(value, propertyType, out castSuccessed);

    // do not set value if convertion failed
    if (castSuccessed)
    {
    property.SetValue(requestParams, castedValue, null);
    }
    }
    }
    return requestParams;*/

    }
}