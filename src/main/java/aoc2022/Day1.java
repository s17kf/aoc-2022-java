package aoc2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;

public class Day1 {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");
        final String inputDir = "src/main/resources/day1/";
        final String filename = args[0];
        final Path inputFilePath = Path.of(inputDir + filename);

        String input = Files.readString(inputFilePath);
        var elvesSnacks = input.replaceAll(System.lineSeparator() + System.lineSeparator(), ";")
                .split(";");

        var result = Arrays.stream(elvesSnacks)
                .map(s -> s.lines().mapToInt(Integer::parseInt).sum())
                .max(Integer::compare)
                .orElseThrow();

        var result2 = Arrays.stream(elvesSnacks)
                .map(s -> s.lines().mapToInt(Integer::parseInt).sum())
                .sorted(Comparator.reverseOrder())
                .limit(3)
                .mapToInt(Integer::intValue)
                .sum();

        System.out.println(result);
        System.out.println(result2);
    }
}
