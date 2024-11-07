package com.baiying.x.tdd.demo_11;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Args {
    public static <T> T parse(Class<T> clazz, String... args) {
        try {
            List<String> arguments = Arrays.asList(args);
            Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
            Object[] values = Arrays.stream(constructor.getParameters()).map(it -> parseOption(it, arguments))
                    .toArray();
            return (T) constructor.newInstance(values);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object parseOption(Parameter parameter, List<String> arguments) {
        Option option = parameter.getAnnotation(Option.class);
        Class<?> type = parameter.getType();
        return PARSERS.get(type).parse(arguments, option);
    }

    public interface OptionParser {
        Object parse(List<String> arguments, Option option);
    }

    private static Map<Class<?>, OptionParser> PARSERS = Map.of(
            boolean.class, new BooleanOptionParser(),
            int.class, new SingleValuedOptionParser<>(Integer::parseInt),
            String.class, new SingleValuedOptionParser<>(String::valueOf));

    static class SingleValuedOptionParser<T> implements OptionParser {
        Function<String, T> valueParser;

        SingleValuedOptionParser(Function<String, T> valueParser) {
            this.valueParser = valueParser;
        }

        @Override
        public T parse(List<String> arguments, Option option) {
            int index = arguments.indexOf("-" + option.value());
            return this.valueParser.apply(arguments.get(index + 1));
        }

    }

    static class BooleanOptionParser implements OptionParser {
        @Override
        public Object parse(List<String> arguments, Option option) {
            return arguments.contains("-" + option.value());
        }
    }

}
