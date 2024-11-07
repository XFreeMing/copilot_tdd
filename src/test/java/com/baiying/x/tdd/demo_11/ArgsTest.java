package com.baiying.x.tdd.demo_11;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArgsTest {

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
    // sed path:
    // TODO:- Bool -l t / -l t f
    // TODO:- Intger -p/ -p 8080 8081
    // TODO:- String -d/ -d /usr/logs /usr/logs
    // default value
    // TODO:- Bool :false
    // TODO:- Intger: 0
    // TODO:- String: ""

    // @Test
    // public void should_example_1() {
    // MuitiOptions options = Args.parse("-l -p 8080 -d /usr/logs",
    // MuitiOptions.class);
    // assertTrue(options.loggin());
    // assertEquals(8080, options.port());
    // assertEquals("/usr/logs", options.directory());
    // };

    // static record MuitiOptions(@Option("l") boolean loggin, @Option("p") int
    // port, @Option("d") String directory) {
    // }

    // example2: -g this is a list -d 1 2 -3 5

}