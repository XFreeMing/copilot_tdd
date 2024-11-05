
# Copilot+TDD

## 关于TDD的常见疑问

- 为什么要开发人员来写测试？这难道不是测试人员的工作吗？难道开发写了测试，测试人员就不用再测了嘛？
- 又要写测试，又要写生产代码，效率是不是太低了？只写生产代码效率应该更高吧？
- 不写测试我也能写出可以工作的软件，那么写测试能给我带来什么额外的好处呢？


的确，从直觉上来看，测试驱动开发相当令人困惑：它将我们通常认为的辅助性工作——测试，作为程序员编码的主要驱动力；它主张通过构造一系列自动化测试（由程序员编写），为编写生产代码（Production Code）做指引；
它甚至建议，如果不存在失败的测试，就不要编写生产代码。看起来，似乎测试驱动开发有些过分强调测试对于程序员的重要性了。

那么我们就需要仔细思考，“测试”在所谓的“正常软件开发模式”中，到底发挥着怎样的作用。当明晰了测试驱动开发的这个核心逻辑之后，我们才能讨论是不是过分强调了。


## 隐式程序员测试（Implicit Developer Testing）

直觉和经验告诉我们，在所谓的【正常软件开发模式】中，貌似测试只是最后的验收步骤，程序员很少直接参与。但事实却不是这样，就算是所谓的【正常软件开发模式】，也蕴含着非常多“程序员测试”的步骤。

只不过这些“程序员测试”并不表现为自动化测试，而是由
- **测试应用(Test Application)**
- **跑一下(Run It)**
- **调试(Debug)** 
等隐含手段体现的。


## 让我们看下具体的例子:
1. 假设我们需要将某个对象存储到数据库中，以 Java 中的 JPA（Jakarta Persistence API）为例，那么我们大概可以构造出这样的“测试应用” 详见 com.baiying.x.tdd.normal.test_app
这个测试应用符合我们对于验证测试的一切认知：有需要被测试的行为，有明确的执行结果，以及针对结果的验证。
那么我们实际上可以很容易地将它改写为自动化测试：
2. 同样，“跑一下”也不是某个技巧的正式名字。从严谨的角度出发，“跑一下”甚至不能算是它真正的名字。它真正的名字应该叫“在我本地的测试环境中跑一下”。同样，所有人也都熟知这一技巧，就真的是“在我本地的测试环境中跑一下”。因为当代应用通常都在受控环境中运行（Managed Environment），所以当验证某个功能时，需要连通其所在的受控环境一起执行。
让我们再看一个具体的例子。假设我们需要实现 REST API，以 Java 中的 JAX-RS（Jakarta Restful WebService）为例，那么我们大概会这样来跑一下：

## TDD 仍是时至今日最具有【工程效能】的研发流程，没有之一？

### 什么是工程效能？

- 开发功能的效能
- 发现问题的效能
- 定位问题的效能
- 修复问题的效能

从理论上来说，后面三个并不是根本的复杂性问题，但在实际中却大量存在，甚至占据一半以上的有效工作时间。因而高效地完成这些非根本性问题，就可以显著地提高效率。

其中发现错误，并准确定位错误，通过发现问题的测试和定位问题的测试可以高效实现。
而如果说发现问题的测试，还有后置或外包于他人的可能，
那么定位问题的测试，无论如何都没有办法了。

所以实际上高效能的研发过程，至少需要我们提供可工作的代码，以及一组可用于定位问题的测试。


> 因此 测试驱动开发最直接的收益，就是可以提高开发者的工程效能。

## 测试驱动开发的难点在哪里？

> 学习测试驱动开发是困难的，很多信服于测试驱动开发理念而自发实践的人也会被各种问题困扰：

- 测试从哪里来？
- 为什么我写了很多测试，功能却没有进展？
- 写什么样的测试既能驱动功能进展，又不会在重构中被破坏？
- 社区里很多人都非常推崇单元测试，但我就是要测一段 SQL，单元测试怎么测？

测试驱动开发从来都不是一种即插即用的技能，它是一种工作习惯和思维方式，背后还对深层的胜任力（Competency）——分析性思考有极高的要求。某种程度上讲，测试驱动开发有点像物理，定理写出来很简单，但需要我们在不同的场景下练习，才能应用得得心应手。


## TDD基本原则

TDD的创始人：Kent Beck 在他的传世大作 Tese-Driven Development: By Example 中提出了TDD的基本原则

1. 当且仅当存在失败的自动化测试时，才编写生产代码
2. 消除坏味道

## TDD的工作步骤

1. 红： 编写一个失败的小测试，甚至可以时无法编译的测试
2. 绿：让这个测试快速通过，甚至不惜犯下任何罪恶
3. 重构：消除上一步中产生的所有坏味道

然而红/绿/重构循环仅仅关注单个测试这个层面，它没有回答测试从何而来，
于是很多尝试采用TDD的人都卡在了第0步：我该写哪些测试？
于是作者徐浩总结了任务分解法，将任务列表作为TDD的核心要素。

## 任务分解法

1. 大致构思软件被使用的方式，把握对外接口的方向；
2. 大致构思功能的实现方式，划分所需的组件（Component）以及组件间的关系（所谓的架构）。
3. 根据需求功能描述拆分功能点，功能点要考虑 正确路径 （Happy Path）和 边界条件 (Sad Path)
4. 依照组件以及组件间的关系，将功能拆分到对应组件
5. 针对拆分的结果编写测试， 进入 红/绿/重构循环





## TDD演示

接下来我将演示如何通过 TDD 的方式完成一段完整的功能，让大家对 TDD 的做法有个感性的认识，并且也可以看到Copilot在TDD中的应用。

1. 任务分解与整体流程
2. 识别坏味道与代码重构
3. 按测试策略重组测试
4. 状态验证

接下来，我会通过 TDD 来实现命令行参数解析的功能。这个练习源自 Robert  的 *Clean Code* 第十四章的一个例子。需求描述如下：

> 我们中的大多数人都不得不时不时地解析一下命令行参数。如果我们没有一个方便的工具，那么我们就简单地处理一下传入 main 函数的字符串数组。有很多开源工具可以完成这个任务，但它们可能并不能完全满足我们的要求。所以我们再写一个吧。

> 传递给程序的参数由标志和值组成。标志应该是一个字符，前面有一个减号。每个标志都应该有零个或多个与之相关的值。例如：

```shell
-l -p 8080 -d /usr/logs
```
> - “l”（日志）没有相关的值，它是一个布尔标志，如果存在则为 true，不存在则为 false。
> - “p”（端口）有一个整数值，
> - “d”（目录）有一个字符串值。标志后面如果存在多个值，则该标志表示一个列表：
> 





















参考课程：https://time.geekbang.org/column/intro/100109401?tab=catalog