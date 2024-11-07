package com.baiying.x.tdd.demo_11;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.baiying.x.tdd.demo_11.Args.OptionParser;

public class Args {
    // 思路是要将 单个测参数解析 提取出来 然后再 变量多个参数
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

    private static Map<Class<?>, OptionParser> PARSERS = Map.of(
            boolean.class, new BooleanOptionParser(),
            int.class, new IntegerOptionParser(),
            String.class, new StringOptionParser());

    public interface OptionParser {
        Object parse(List<String> arguments, Option option);
    }

    // 通过 inline 优化代码
    private static Map<Class<?>, OptionParser> PARSERS = Map.of(
            boolean.class, new BooleanOptionParser(),
            int.class, new IntegerOptionParser(Integer::parseInt),
            String.class, new IntegerOptionParser(String::valueOf));

    static class IntegerOptionParser implements OptionParser {
        Function<String, Object> valueParser;

        IntegerOptionParser(Function<String, Object> valueParser) {
            this.valueParser = valueParser;
        }

        @Override
        public Object parse(List<String> arguments, Option option) {
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
