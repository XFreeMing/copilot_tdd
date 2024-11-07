# TDD演示

## 目录

- [首页](./README.md)


接下来我将演示如何通过 TDD 的方式完成一段完整的功能，让大家对 TDD 的做法有个感性的认识，并且也可以看到Copilot在TDD驱动研发过程发挥了哪些作用。

1. 任务分解与整体流程
2. 识别坏味道与代码重构
3. 按测试策略重组测试 
4. 状态验证

我们会通过 TDD 来实现命令行参数解析的功能。这个练习源自 Robert  的 《代码简介之道》*Clean Code* 第十四章的一个例子。需求描述如下：

> 我们中的大多数人都不得不时不时地解析一下命令行参数。如果我们没有一个方便的工具，那么我们就简单地处理一下传入 main 函数的字符串数组。

> 传递给程序的参数由标志和值组成。标志应该是一个字符，前面有一个减号。每个标志都应该有零个或多个与之相关的值。例如：

```shell
-l -p 8080 -d /usr/logs
```
> - "l"（日志）没有相关的值，它是一个布尔标志，如果存在则为 true，不存在则为 false。
> - "p"（端口）有一个整数值，
> - "d"（目录）有一个字符串值。标志后面如果存在多个值，则该标志表示一个列表：

## 我们将采用 任务分解法来演示整个TDD实现流程
## 任务分解法简介

1. 大致构思软件被使用的方式，把握对外接口的方向；
2. 大致构思功能的实现方式，划分所需的组件（Component）以及组件间的关系（所谓的架构）。
3. 根据需求功能描述拆分功能点，功能点要考虑 正确路径 （Happy Path）和 边界条件 (Sad Path)
4. 依照组件以及组件间的关系，将功能拆分到对应组件
5. 针对拆分的结果编写测试， 进入 红/绿/重构循环



## 第一步：大致构思软件被使用的方式，把握对外接口的方向；
下方是模拟了下 日常思考过程 其中出现了两种不通的接口风格，本次采用的是第二种
```java
    // 第一种 
    Arguments args = Args.parse("-l:b,p:d,d:s","-l","-p","8080","-d","/usr/logs");
    args.getBool("-l");
    args.getInt("-p");
```

```java
    // 第二种 较为现代化
    static record Options(@Option("l")boolean loggin, @Option("p")int port, @Option("d")String directory) {}
    Options options = Args.parse(Options.class,"-l","-p","8080","-d","/usr/logs");
    options.logging()；
    options.port();
```

## 第二步：大致构思功能的实现方式，划分所需的组件（Component）以及组件间的关系（所谓的架构）。


- 首先可以先看下 输入 `-l -p 8080 -d /usr/logs`
- 我们可以采用 数组分割的思路 `[-l] [-p ,8080] [-d, /usr/logs]`
- 对数组进行分段处理 可以利用 index 标志位 找到每一个位置 然后往后去读
- 也可以使用 Map 模式 `{-l:[],-p:[8080],-d:[/usr/logs]}`
- 从简单的角度出发 本次采用  index 模式 这就是 我们选择的实现策略

## 第三步任务分解

### 我们可以先从最终实现的功能出发 写出两个测试
```java

    import org.junit.Test;
    import static org.junit.jupiter.api.Assertions.*;
    // example1: -l -p 8080 -d /usr/logs
    
    @Test
    public void should_example_1(){};
    // example2: -g this is a list -d 1 2 -3 5

    @Test
    public void should_example_2(){};

    static record MuitiOptions(@Option("l")boolean loggin, @Option("p")int port, @Option("d")String directory) {}
    static record ListOption(@Option("g")String[] group, @Option("d")int[] decimals) {}

```

### 个人实现与Copilot生成的对比
```java
    // example1: -l -p 8080 -d /usr/logs
    @Test
    public void should_example_1(){
        MuitiOptions options = Args.parse(Options.class, "-l", "-p", "8080", "-d", "/usr/logs");
        assertTrue(options.loggin());
        assertEquals(8080, options.port());
        assertEquals("/usr/logs", options.directory());
    };
    // example2: -g this is a list -d 1 2 -3 5

    @Test
    public void should_example_2(){
        ListOption options = Args.parse(ListOption.class, "-g", "this", "is", "a", "list", "-d", "1", "2", "-3", "5");
        assertArrayEquals(new String[]{"this", "is", "a", "list"}, options.group());
        assertArrayEquals(new int[]{1, 2, -3, 5}, options.decimals());
    };

    static record MuitiOptions(@Option("l")boolean loggin, @Option("p")int port, @Option("d")String directory) {}
    static record ListOption(@Option("g")String[] group, @Option("d")int[] decimals) {}
```

## 那接下来是不是根据两个测试开始驱动我的整个开发呢？

1. 思路是对的 但实际操作上并不是这样的原因是步子太大了

2. 因为我们可以看一看要想通过 should_example_1 我们需要处理 布尔型的参数，要处理整型的，还要处理字符串型的

3. 如果想要通过 should_example_2 我们需要处理字符串数组，整型数组

4. 这都是比较大的步骤。

5. 目前我们就到了怎么根据我们现在已经在脑海中得到的实现方式下的大致的代码结构，和我们确定的这样一个API的角度，我们如何对任务进行更细致的分解。

6. 其实我们可以先从一个最简单的例子开始，比如说我们先处理一个布尔型的参数，然后再处理一个整型的参数，再处理一个字符串型的参数，然后再处理一个字符串数组，再处理一个整型数组。当我们 写道第四个时其实所谓的正向流程就跑通了

### 根据上方思路TODO 列表如下
```java
// -l -p 8080 -d /usr/logs
    // Single Options:
        // TODO: - Bool -l
        @Test
        public void should_parse_bool() {}
        static record BooleanOption(@Option("l") boolean logging) {}
        @Test
        public void should_parse_bool_to_false_if_flag_not_present(){}
        // TODO:- Intger -p 8080
        @Test
        public void should_parse_intger() {}
        // TODO:- String -d /usr/logs
        @Test
        public void should_parse_string() {}
        // TODO:- muiti options: -l -p 8080 -d /usr/logs
        @Test
        public void should_parse_multi_options() {}
    // sed path:
        // TODO:- Bool -l t / -l t f
        // TODO:- Intger -p/ -p 8080 8081
        // TODO:- String -d/ -d /usr/logs /usr/logs
    // default value
        // TODO:- Bool :false
        // TODO:- Intger: 0
        // TODO:- String: ""
```
### 这里 我们可以 借用Copilot 生成对应的测试代码只需一路回车即可





## 红绿循环
1. 第一步先将最终的测试 Disable掉 因为暂时不用关心  @Disabled
2. 第二步通过编译 也就是构造我们大致的结构
```java
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER }) // 因为是用在 recordconstructor的 parameter上 
public @interface Option {
    String value();
}
```
```java
public class Args {
    public static <T> T parse(Class<T> optionsClass, String... args) {rerurn null}
}
```
3. 开始写第一个TODO
4. 我们有了第一个 红状态，接下来可以写生产代码了，注意这里有个原则是 尽可能快的让测试通过，不要想太多不用想边界，不要想太复杂，甚至功能不一定是正确的，只要让测试通过就好，因为这里是强迫我们去养成一种新的编程习惯。这里还特别提到了，我们可以不用担心犯下任何罪恶
5. 代码如下
```java
 public static <T> T parse(Class<T> clazz, String... args) {
        // 因为我们知道 传进来的是Booleanoption
        // 所以我们可以直接通过反射来获取到这个类的构造函数
        Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
        // 同时我知道里面有一个默认的构造函数里面有 Annotaion 修饰的参数
        // 因为我知道这个参数是Boolean类型的所以直接返回true
        try {
            return (T) constructor.newInstance(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
```

6. 大家可以看到上段代码距离我们的目标还有很远，这个呢 我们目前不用担心，我们可以看到我们的测试是通过的，这就是我们的红绿循环

7. 那大家说 那你这样写我这个代码怎么才能真正实现功能呢

8. 这个时候 我们在看第二个测试就好了，他是第一个测试的反例,这时候我们跑下
第二个测试时其实是不通过的这时候我们就可以开始写我们的生产代码了

```java

public static <T> T parse(Class<T> clazz, String... args) {
        try {
            // 因为我们知道 传进来的是Booleanoption
            // 所以我们可以直接通过反射来获取到这个类的构造函数
            Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
            // 我们知道他会有一组参数
            Parameter parameter = constructor.getParameters()[0];
            // 我们知道他会有一个注解
            Option option = parameter.getAnnotation(Option.class);
            // 我们在只需要判断 option 的值 是否在 args 中
            return (T) constructor.newInstance(Arrays.asList(args).contains("-" + option.value()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
}
```
8. 好了这时候我们两个测试就都通过了

9. 这时候我们看下现在的实现 大家可能会觉得你跟开玩笑一样，这个一点都不像我们最终可能实现功能的结构，但我们不着急哈，我们看下怎么TDD就能帮助我们一步一步的实现，最终变成一个可用的代码

10. 我们通常情况下由于想的太多，可能处理不过来，TDD恰好能帮助我们一次只做一点点，虽然现在很不像话，但是没关系我们写几个测试它可能慢慢就会变好

11. 下面我们继续写下一个测试，这里Copilot 已经帮忙我们把TODO 列表的测试都写好了 这是之前践行TDD无法做到的，所以这里从写一个测试变成了直接运行测试，我们只需要关注红绿循环就好了


```java

    public static <T> T parse(Class<T> clazz, String... args) {
        try {
            // 因为我们知道 传进来的是Booleanoption
            // 所以我们可以直接通过反射来获取到这个类的构造函数
            Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
            // 我们知道他会有一组参数
            Parameter parameter = constructor.getParameters()[0];
        
            List<String> arguments = Arrays.asList(args);
            // 首先 我们要显示的把value 体现出来 因为 value 不一定是 Boolean类型的 所以我们用 Object
            Object value = null;
            // 我们知道他会有一个注解
            Option option = parameter.getAnnotation(Option.class);
            
            // 然后我们添加各种类型的判断
            if (parameter.getType() == boolean.class) {
                value = arguments.contains("-" + option.value());
            }
            // 由于第二个测试是整型的 所以我们这里就直接写整型的
            if (parameter.getType() == int.class) {
                // 这里我们设计过实现策略是通过 index 来找到对应的值
                int index = arguments.indexOf("-" + option.value());
                value = Integer.parseInt(arguments.get(index + 1));
            }

            return (T) constructor.newInstance(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
```

12. 现在同样 大家会发现 我们目前代码里面有很多可能出现bug的地方，比如 constructor 如果有多个怎么办，它第一个不是默认的怎么办，然后参数上没有option注解怎么办，他多余一个参数怎么办，这些我们现在都不管，因为我们现在是通过测试逐步去驱动我们的实现，现在的测试上只考虑了这些情况，那我们就可以通过这样的情况去做

13. 同样的 我们在看String的情况

```java
// 这时候很简单了 我们在追加一个判断即可
if (parameter.getType() == String.class) {
    int index = arguments.indexOf("-" + option.value());
    value = arguments.get(index + 1);
}
```

14. 接下来我们来实现 最开始两大功能中的 第一个功能了 也就是 多个参数的情况

```java
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
        Object value = null;
        Option option = parameter.getAnnotation(Option.class);

        if (parameter.getType() == boolean.class) {
            value = arguments.contains("-" + option.value());
        }
        if (parameter.getType() == int.class) {
            int index = arguments.indexOf("-" + option.value());
            value = Integer.parseInt(arguments.get(index + 1));
        }
        if (parameter.getType() == String.class) {
            int index = arguments.indexOf("-" + option.value());
            value = arguments.get(index + 1);
        }
        return value;
    }

```
15. 到这里 我们的 Happy Path 已经跑通了，这时候我们回想下我们最开始实现的代码极度简陋，取第一个构造函数，取第一个参数直接返回True 到现在我们形成的这样的方法，不过过了10分钟的时间，所以呢TDD告诉我们其实就是不要焦虑，转换成小的测试，在这个过程中我们可以控制我们的进度最终我们得到我们想要的这个代码结构

## 选择

现在我们有两个选择：继续完成功能，或者开始重构。是否进入重构有两个先决条件，
- 第一是测试都是绿的，也就是当前功能正常。
- 第二是坏味道足够明显。显然我们的测试都是绿的，而且到达了一个里程碑点，完成了一大块功能。

同样，目前代码中存在明显的坏味道，就是这段代码：
```java
        if (parameter.getType() == boolean.class) {
            value = arguments.contains("-" + option.value());
        }
        if (parameter.getType() == int.class) {
            int index = arguments.indexOf("-" + option.value());
            value = Integer.parseInt(arguments.get(index + 1));
        }
        if (parameter.getType() == String.class) {
            int index = arguments.indexOf("-" + option.value());
            value = arguments.get(index + 1);
        }
```
可以看到，这段代码中存在多个分支条件。而且可以预见，随着我们要支持的类型越来越多，比如 double 类型，那么我们还需要引入更多类似的结构。
这是一个明显的面向对象误用的坏味道——分支语句（Switch Statements、Object-Oriented Abusers）。
而我们可以利用重构手法“利用多态替换条件分支”（Replacing Conditional with Polymorphism）对其进行重构。

## 需要注意的是，“利用多态替换条件分支”是一个相当大的重构，我们需要一系列的步骤才能完成这个重构。这期间，我们需要保持小步骤且稳定的节奏，逐步完成重构，而不是按照目标对代码进行重写。所以接下来的演示中，可以留心数一下，在整个重构过程中，我们运行了多少次测试

[TDD演示-消除代码坏味道](./TDD演示-消除代码坏味道.md)