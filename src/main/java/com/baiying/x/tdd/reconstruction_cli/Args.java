package com.baiying.x.tdd.cli_demo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

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
        Object value = null;
        Option option = parmeters.getAnnotation(Option.class);

        if (parmeters.getType() == boolean.class) {
            value = arguments.contains("-" + option.value());
        }
        if (parmeters.getType() == int.class) {
            int index = arguments.indexOf("-" + option.value());
            value = Integer.parseInt(arguments.get(index + 1));
        }
        if (parmeters.getType() == String.class) {
            value = arguments.get(arguments.indexOf("-" + option.value()) + 1);
        }
        return value;
    }

}
