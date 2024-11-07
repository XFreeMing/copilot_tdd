# TDD演示

## 目录

- [首页](./README.md)



接下来我将演示如何通过 TDD 的方式完成一段完整的功能，让大家对 TDD 的做法有个感性的认识，并且也可以看到Copilot在TDD驱动研发过程发挥了哪些作用。

1. 任务分解与整体流程
2. 识别坏味道与代码重构
3. 按测试策略重组测试
4. 状态验证

接下来，我会通过 TDD 来实现命令行参数解析的功能。这个练习源自 Robert  的 *Clean Code* 第十四章的一个例子。需求描述如下：

> 我们中的大多数人都不得不时不时地解析一下命令行参数。如果我们没有一个方便的工具，那么我们就简单地处理一下传入 main 函数的字符串数组。

> 传递给程序的参数由标志和值组成。标志应该是一个字符，前面有一个减号。每个标志都应该有零个或多个与之相关的值。例如：

```shell
-l -p 8080 -d /usr/logs
```
> - "l"（日志）没有相关的值，它是一个布尔标志，如果存在则为 true，不存在则为 false。
> - "p"（端口）有一个整数值，
> - "d"（目录）有一个字符串值。标志后面如果存在多个值，则该标志表示一个列表：


## 大致构思软件被使用的方式，把握对外接口的方向；
```java
    Arguments args = Args.parse("-l:b,p:d,d:s","-l","-p","8080","-d","/usr/logs");
    args.getBool("-l");
    args.getInt("-p");
```
```java
static record Options(@Option("l")boolean loggin, @Option("p")int port, @Option("d")String directory) {}
Options options = Args.parse(Options.class,"-l","-p","8080","-d","/usr/logs");
options.logging()；
options.port();
```

## 大致构思功能的实现方式，划分所需的组件（Component）以及组件间的关系（所谓的架构）。

```java
// 数组分割 [-l] [-p ,8080] [-d, /usr/logs]

```

## 任务分解
```java
// -l -p 8080 -d /usr/logs
    // Single Options:
        // - Bool -l
        // - Intger -p 8080
        // - String -d /usr/logs
        // muiti options: -l -p 8080 -d /usr/logs
    // sed path:
        // - Bool -l t / -l t f
        // - Intger -p/ -p 8080 8081
        // - String -d/ -d /usr/logs /usr/logs
    // default value
        // - Bool :false
        // - Intger: 0
        // - String: ""
```

## Copilot 根据任务 创建测试代码

## 识别坏味道与代码重构

详情见代码：com.baiying.x.tdd.reconstruction_cli