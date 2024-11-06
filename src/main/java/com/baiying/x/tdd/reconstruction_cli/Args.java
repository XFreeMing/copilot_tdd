package com.baiying.x.tdd.reconstruction_cli;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Args {

    public static <T> T parse(Class<T> optionsClass, String... args) {
        try {
            List<String> arguments = Arrays.asList(args);
            Constructor<?> constructor = optionsClass.getDeclaredConstructors()[0];

            Object[] values = Arrays.stream(constructor.getParameters()).map(it -> parseOption(arguments, it))
                    .toArray();

            return (T) constructor.newInstance(values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Object parseOption(List<String> arguments, Parameter parmeters) {
        Option option = parmeters.getAnnotation(Option.class);
        Class<?> type = parmeters.getType();
        return PARSERS.get(type).parse(arguments, option);
    }

    private static Map<Class<?>, OptionParser> PARSERS = Map.of(
            boolean.class, new BooleanPatser(),
            int.class, new SingleValuedOptionParser<>(Integer::parseInt),
            String.class, new SingleValuedOptionParser<>(String::toString));
}
