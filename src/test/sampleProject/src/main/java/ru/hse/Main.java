package ru.hse;

import com.google.common.collect.Streams;

import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        var first = Stream.of(1, 2);
        var second = Stream.of(2, 3);
        Streams.zip(first, second, Integer::sum).forEach(System.out::println);
    }
}