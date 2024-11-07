package com.baiying.x.tdd.demo_11;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArgsTest {

    /**
     * 
     * 大致构思软件被使用的方式，把握对外接口的方向；
     * static record Options(@Option("l")boolean loggin, @Option("p")int
     * port, @Option("d")String directory) {}
     * Options options =
     * Args.parse(Options.class,"-l","-p","8080","-d","/usr/logs");
     * options.logging()；
     * options.port();
     */

    /**
     * 大致构思功能的实现方式，划分所需的组件（Component）以及组件间的关系（所谓的架构）。
     * 
     * 首先可以先看下 输入 -l -p 8080 -d /usr/logs
     * 数组分割 [-l] [-p ,8080] [-d, /usr/logs]
     * 对数组进行分段处理 可以利用 index 标志位 找到每一个位置 然后往后去读
     * Map 模式
     * {-l:[],-p:[8080],-d:[/usr/logs]}
     * 从简单的角度出发 本次采用 index 模式 这就是 我们选择的实现策略
     */
    // example1: -l -p 8080 -d /usr/logs

    // -l -p 8080 -d /usr/logs
    // Single Options:
    // TODO: - Bool -l
    @Test
    public void should_parse_bool() {
        BooleanOption options = Args.parse(BooleanOption.class, "-l");
        assertTrue(options.logging());
    }

    static record BooleanOption(@Option("l") boolean logging) {
    }

    @Test
    public void should_parse_bool_to_false_if_flag_not_present() {
        BooleanOption options = Args.parse(BooleanOption.class);
        assertFalse(options.logging());
    }

    // TODO:- Intger -p 8080
    @Test
    public void should_parse_intger() {
        IntegerOption options = Args.parse(IntegerOption.class, "-p", "8080");
        assertEquals(8080, options.port());
    }

    static record IntegerOption(@Option("p") int port) {
    }

    // TODO:- String -d /usr/logs
    @Test
    public void should_parse_string() {
        StringOption options = Args.parse(StringOption.class, "-d", "/usr/logs");
        assertEquals("/usr/logs", options.directory());
    }

    static record StringOption(@Option("d") String directory) {
    }

    // TODO:- muiti options: -l -p 8080 -d /usr/logs
    @Test
    public void should_parse_multi_options() {
        MuitiOptions options = Args.parse(MuitiOptions.class, "-l", "-p", "8080", "-d", "/usr/logs");
        assertTrue(options.loggin());
        assertEquals(8080, options.port());
        assertEquals("/usr/logs", options.directory());
    }

    static record MuitiOptions(@Option("l") boolean loggin, @Option("p") int port, @Option("d") String directory) {
    }
    // example2: -g this is a list -d 1 2 -3 5

    // @Test
    // @Disabled
    // public void should_example_2() {
    // ListOption options = Args.parse(ListOption.class, "-g", "this", "is", "a",
    // "list", "-d", "1", "2", "-3", "5");
    // assertArrayEquals(new String[] { "this", "is", "a", "list" },
    // options.group());
    // assertArrayEquals(new int[] { 1, 2, -3, 5 }, options.decimals());
    // };

    // static record ListOption(@Option("g") String[] group, @Option("d") int[]
    // decimals) {
    // }

}