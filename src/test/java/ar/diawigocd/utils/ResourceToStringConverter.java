

package ar.diawigocd.utils;

import ar.diawigocd.plugin.Util;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

public class ResourceToStringConverter implements ArgumentConverter {
    @Override
    public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
        if (source instanceof String) {
            String resourceName = (String) source;
            return Util.readResource(resourceName);
        } else {
            throw new IllegalArgumentException(String.format("Source is not string (%s)", source.getClass().getName()));
        }
    }
}
