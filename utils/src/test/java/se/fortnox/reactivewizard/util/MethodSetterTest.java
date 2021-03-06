package se.fortnox.reactivewizard.util;

import org.junit.Before;
import org.junit.Test;
import rx.Observable;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class MethodSetterTest extends AccessorTest {
    private Setter value;
    private Setter longObservable;
    private Setter genericSuperKey;
    private Setter genericSuperValue;
    private Setter superKey;
    private Setter superValue;

    @Before
    public void setUp() {
        value = ReflectionUtil.getSetter(GenericMethodSubclass.class, "value");
        longObservable = ReflectionUtil.getSetter(GenericMethodSubclass.class, "longObservable");
        genericSuperKey = ReflectionUtil.getSetter(GenericMethodSubclass.class, "superKey");
        genericSuperValue = ReflectionUtil.getSetter(GenericMethodSubclass.class, "superValue");
        superKey = ReflectionUtil.getSetter(MethodSubclass.class, "superKey");
        superValue = ReflectionUtil.getSetter(MethodSubclass.class, "superValue");
    }

    @Test
    public void shouldCreateMethodSetters() {
        assertThat(Stream.of(value, longObservable, genericSuperKey, genericSuperValue, superKey, superValue).allMatch(MethodSetter.class::isInstance)).isTrue();
    }

    @Test
    public void shouldSetValue() throws Exception {
        GenericMethodSubclass foo = new GenericMethodSubclass(1);
        value.invoke(foo, 9);
        assertThat(foo.getValue()).isEqualTo(9);

        genericSuperKey.invoke(foo, "9");
        assertThat(foo.getSuperKey()).isEqualTo("9");

        genericSuperValue.invoke(foo, 9);
        assertThat(foo.getSuperValue());

        MethodSubclass bar = new MethodSubclass("1", 2);
        superKey.invoke(bar, "9");
        assertThat(bar.getSuperKey()).isEqualTo("9");

        superValue.invoke(bar, 9);
        assertThat(bar.getSuperValue()).isEqualTo(9);
    }

    @Test
    public void shouldGetParameterType() {
        assertThat(value.getParameterType()).isEqualTo(Integer.class);
        assertThat(genericSuperKey.getParameterType()).isEqualTo(String.class);
        assertThat(genericSuperValue.getParameterType()).isEqualTo(Integer.class);
        assertThat(superKey.getParameterType()).isEqualTo(String.class);
        assertThat(superValue.getParameterType()).isEqualTo(Integer.class);
    }

    @Test
    public void shouldGetGenericParameterType() {
        assertThat(longObservable.getGenericParameterType().toString()).isEqualTo("rx.Observable<java.lang.Long>");
        assertThat(genericSuperKey.getGenericParameterType().toString()).isEqualTo("class java.lang.String");
        assertThat(genericSuperValue.getGenericParameterType().toString()).isEqualTo("class java.lang.Integer");
        assertThat(superKey.getGenericParameterType().toString()).isEqualTo("class java.lang.String");
        assertThat(superValue.getGenericParameterType().toString()).isEqualTo("class java.lang.Integer");
    }
}
