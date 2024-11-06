package com.baiying.x.tdd.reconstruction_cli;

import java.util.List;

class BooleanPatser implements OptionParser {
    @Override
    public Object parse(List<String> arguments, Option option) {
        return arguments.contains("-" + option.value());
    }
}