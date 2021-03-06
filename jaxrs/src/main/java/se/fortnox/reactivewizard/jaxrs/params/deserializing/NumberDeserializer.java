package se.fortnox.reactivewizard.jaxrs.params.deserializing;

import java.util.function.Function;

/**
 * Generic number deserializer.
 */
public abstract class NumberDeserializer<T> implements Deserializer<T> {

    protected String              errorCode;
    private   Function<String, T> numberParser;

    public NumberDeserializer(Function<String, T> numberParser, String errorCode) {
        this.numberParser = numberParser;
        this.errorCode = errorCode;
    }

    @Override
    public T deserialize(String value) throws DeserializerException {
        if (value == null) {
            return getNullValue();
        }
        try {
            return numberParser.apply(value);
        } catch (NumberFormatException e) {
            throw new DeserializerException(errorCode);
        }
    }

    protected T getNullValue() throws DeserializerException {
        return null;
    }
}
