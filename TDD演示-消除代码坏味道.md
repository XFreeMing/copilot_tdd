## 识别坏味道与代码重构
详情见代码：com.baiying.x.tdd.reconstruction_cli
## 重构流程
1. 首先我们要进行抽取方法 把它和不变的地方进行一个隔离帮助我们看下，我们现在要抽出的接口到底长什么样子。
```java
    private static Object parseOption(Parameter parameter, List<String> arguments) {
        Object value = null;
        Option option = parameter.getAnnotation(Option.class);

        if (parameter.getType() == boolean.class) {
            value = parseBoolean(arguments, option);
        }
        if (parameter.getType() == int.class) {
            value = parseInt(arguments, option);
        }
        if (parameter.getType() == String.class) {
            value = parseString(arguments, option);
        }
        return value;
    }

    private static Object parseString(List<String> arguments, Option option) {
        Object value;
        int index = arguments.indexOf("-" + option.value());
        value = arguments.get(index + 1);
        return value;
    }

    private static Object parseInt(List<String> arguments, Option option) {
        Object value;
        int index = arguments.indexOf("-" + option.value());
        value = Integer.parseInt(arguments.get(index + 1));
        return value;
    }

    private static boolean parseBoolean(List<String> arguments, Option option) {
        return arguments.contains("-" + option.value());
    }

```

2. 这时候我们看到抽出的方法具有同样的签名，这样呢 我们其实可以转成一个 Interface

```java
public interface OptionParser {
    Object parse(List<String> arguments, Option option);
}
```

3. 抽出Interface 后我们就需要将刚抽出的方法转成具体的类实现这个接口

```java

    private static Object parseBoolean(List<String> arguments, Option option) {
        return new BooleanParser().parse(arguments, option);
    }

    static class BooleanParser implements OptionParser {
        @Override
        public Object parse(List<String> arguments, Option option) {
            return arguments.contains("-" + option.value());
        }
    }


```

4. 因为这个时候虽然实现只是换了个地方，但实际上我们对代码链路的实现方式进行了修改所以我么需要跑一下测试的。来保障我们的代码没有问题。虽然这个步骤可能非常小，但是这个是让我们能够更稳健的一种方法

5. 重复上面的步骤，抽取出其他的方法，然后实现接口，最后跑测试

```java
    private static Object parseOption(Parameter parameter, List<String> arguments) {
        Option option = parameter.getAnnotation(Option.class);
        OptionParser parser = null;
        if (parameter.getType() == boolean.class) {
            parser = new BooleanOptionParser();
        }
        if (parameter.getType() == int.class) {
            parser = new IntegerOptionParser();
        }
        if (parameter.getType() == String.class) {
            parser = new StringOptionParser();
        }
        return parser.parse(arguments, option);
    }


    public interface OptionParser {
        Object parse(List<String> arguments, Option option);
    }

    static class IntegerOptionParser implements OptionParser {
        @Override
        public Object parse(List<String> arguments, Option option) {
            int index = arguments.indexOf("-" + option.value());
            return Integer.parseInt(arguments.get(index + 1));
        }
    }

    static class StringOptionParser implements OptionParser {
        @Override
        public Object parse(List<String> arguments, Option option) {
            int index = arguments.indexOf("-" + option.value());
            return arguments.get(index + 1);
        }
    }

    static class BooleanOptionParser implements OptionParser {
        @Override
        public Object parse(List<String> arguments, Option option) {
            return arguments.contains("-" + option.value());
        }
    }


```

6. 接下来我们想将 if 判断抽取出去 使其更明确，同时我们发现getOptionParser 其实是一个 Factory

```java
    private static Object parseOption(Parameter parameter, List<String> arguments) {
        Option option = parameter.getAnnotation(Option.class);
        Class<?> type = parameter.getType();
        OptionParser parser = getOptionParser(type);
        return parser.parse(arguments, option);
    }

    private static OptionParser getOptionParser(Class<?> type) {
        OptionParser parser = null;

        if (type == boolean.class) {
            parser = new BooleanOptionParser();
        }
        if (type == int.class) {
            parser = new IntegerOptionParser();
        }
        if (type == String.class) {
            parser = new StringOptionParser();
        }
        return parser;
    }


```


7. 这时候我们就可以 利用多态进行重构

```java

    private static Map<Class<?>, OptionParser> PARSERS = Map.of(
            boolean.class, new BooleanOptionParser(),
            int.class, new IntegerOptionParser(),
            String.class, new StringOptionParser());

    private static OptionParser getOptionParser(Class<?> type) {
        return PARSERS.get(type);
    }

    // 进一步 inline

    private static Object parseOption(Parameter parameter, List<String> arguments) {
        Option option = parameter.getAnnotation(Option.class);
        Class<?> type = parameter.getType();
        return PARSERS.get(type).parse(arguments, option);
    }

    private static Map<Class<?>, OptionParser> PARSERS = Map.of(
            boolean.class, new BooleanOptionParser(),
            int.class, new IntegerOptionParser(),
            String.class, new StringOptionParser());

```

8. 到此我们就将if 删除掉了，如果再看一下现在的代码，会发现还有另一个坏味道：IntegerOptionParser 和 StringOptionParser 代码重复（Duplication）。当我们发现 代码中大部分代码都一样只有一行不一样的时候，我们通常使用的是策略模式 ，同时我们也可以采用 Java 的 Function 来进行重构
我们先看一种中间状态
```java

    static class IntegerOptionParser implements OptionParser {
        @Override
        public Object parse(List<String> arguments, Option option) {
            int index = arguments.indexOf("-" + option.value());
            return parseValue(arguments.get(index + 1));
        }

        protected Object parseValue(String value) {
            return Integer.parseInt(value);
        }

    }

    static class StringOptionParser extends IntegerOptionParser {
        @Override
        public Object parseValue(String value) {
            return String.valueOf(value);
        }
    }

```

9. 转换成 Function

```java
    static class IntegerOptionParser implements OptionParser {
        Function<String, Object> valueParser = Integer::parseInt;

        IntegerOptionParser() {
        }

        IntegerOptionParser(Function<String, Object> valueParser) {
            this.valueParser = valueParser;
        }

        @Override
        public Object parse(List<String> arguments, Option option) {
            int index = arguments.indexOf("-" + option.value());
            return this.valueParser.apply(arguments.get(index + 1));
        }

    }

    static class StringOptionParser extends IntegerOptionParser {
        StringOptionParser() {
            super(String::valueOf);
        }
    }

```

10. 工厂方法转换

```java
    private static Map<Class<?>, OptionParser> PARSERS = Map.of(
            boolean.class, new BooleanOptionParser(),
            int.class, new IntegerOptionParser(),
            String.class, StringOptionParser.createStringOptionParser());
    
    static class StringOptionParser extends IntegerOptionParser {

        public static OptionParser createStringOptionParser() {
            return new IntegerOptionParser(String::valueOf);
        }
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

```

11. 重命名一下 IntegerOptionParser 为 SingleValuedOptionParser 并泛型化处理

```java

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

```

12. 到此位置 我们完成了代码的重构 消除了两种坏味道，一个是代码重复，一个是 if 判断，同时我们也将代码进行了优化，使其更加清晰，同时我们也通过测试来保障我们的代码没有问题。