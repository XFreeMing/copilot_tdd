package com.baiying.x.tdd.cli_test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.baiying.x.tdd.cli_demo.Args;
import com.baiying.x.tdd.cli_demo.Option;

public class ArgsTest {
    // -l -p 8080 -d /usr/logs
    // Single Options:
    // - Bool -l
    @Test
    public void should_parse_bool_to_true_if_flag_present() {
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

    // - Intger -p 8080
    @Test
    public void should_parse_intger() {
        IntegerOption options = Args.parse(IntegerOption.class, "-p", "8080");
        assertEquals(8080, options.port());
    }

    static record IntegerOption(@Option("p") int port) {
    }

    // - String -d /usr/logs
    @Test
    public void should_parse_string() {
        StringOption options = Args.parse(StringOption.class, "-d", "/usr/logs");
        assertEquals("/usr/logs", options.directory());
    }

    static record StringOption(@Option("d") String directory) {
    }

    // muiti options: -l -p 8080 -d /usr/logs
    @Test
    public void should_parse_multi_options() {
        Multioptions options = Args.parse(Multioptions.class, "-l", "-p", "8080", "-d", "/usr/logs");
        assertTrue(options.logging());
        assertEquals(8080, options.port());
        assertEquals("/usr/logs", options.directory());
    }

    static record Multioptions(@Option("l") boolean logging, @Option("p") int port, @Option("d") String directory) {
    }
    // sed path:
    // - Bool -l t / -l t f
    // - Intger -p/ -p 8080 8081
    // - String -d/ -d /usr/logs /usr/logs
    // default value
    // - Bool :false
    // - Intger: 0
    // - String: ""

    // -g this is a list -d 1 2 -3 5
    @Test
    @Disabled
    public void should_example_2() {
        ListOptions options = Args.parse(ListOptions.class, "-g", "this", "is", "a", "list", "-d", "1", "2", "-3", "5");
        assertEquals(new String[] { "this", "is", "a", "list" }, options.group());
        assertEquals(new int[] { 1, 2, -3, 5 }, options.decimals());
    }

    static record ListOptions(@Option("g") String[] group, @Option("d") int[] decimals) {
    }
}
