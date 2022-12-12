package aoc2022;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day11 {
    public static void main(String[] args) throws IOException {
        final String inputDir = "src/main/resources/day11/";
        final String filename = args[0];
        final Path inputFilePath = Path.of(inputDir + filename);

        final List<String> input = Files.readAllLines(inputFilePath);

        doTask(input, 1);
        doTask(input, 2);
    }

    static void doTask(List<String> inputLines, int task) {
        Monkey.clearMonkeyList();
        final List<Monkey> monkeys = Monkey.parseMonkeysFromLines(inputLines);
        final int rounds = task == 1 ? 20 : 10000;
        final Long reduceValue = monkeys.stream()
                .map(Monkey::getModuloValue)
                .reduce((m1, m2) -> m1 * m2)
                .orElseThrow();
        final Function<Long, Long> reduceOperation = task == 1 ? l -> l / 3 : l -> l % reduceValue;
        monkeys.forEach(monkey -> monkey.setReduceOperation(reduceOperation));

        IntStream.range(0, rounds).forEach(round -> Monkey.doRoundForAllMonkeys());

        Long result = monkeys.stream()
                .map(Monkey::getInspectedItems)
                .sorted(Comparator.reverseOrder())
                .limit(2)
                .reduce((i1, i2) -> i1 * i2).orElseThrow();
        System.out.println(result);
    }
}

@Getter
@Setter
final class Monkey {
    private static final List<Monkey> ALL_MONKEYS = new ArrayList<>();
    private List<Long> items;
    private Function<Long, Long> operation;
    private long moduloValue;
    private int nextOnTrue;
    private int nextOnFalse;
    private Function<Long, Long> reduceOperation;
    @Setter(AccessLevel.NONE)
    private long inspectedItems = 0;

    Monkey() {
        ALL_MONKEYS.add(this);
    }

    private void addItem(Long item) {
        items.add(item);
    }

    @Override
    public String toString() {
        return inspectedItems + items.toString();
    }

    static void doRoundForAllMonkeys() {
        ALL_MONKEYS.forEach(Monkey::doRound);
    }

    void doRound() {
        items.stream()
                .map(operation)
                .map(reduceOperation)
                .forEach(item -> {
                    ++inspectedItems;
                    var receiver = item % moduloValue == 0 ? nextOnTrue : nextOnFalse;
                    ALL_MONKEYS.get(receiver).addItem(item);
                });
        items.clear();
    }

    static void clearMonkeyList() {
        ALL_MONKEYS.clear();
    }

    static List<Monkey> parseMonkeysFromLines(List<String> lines) {
        List<Monkey> monkeys = new ArrayList<>();
        lines.stream().filter(s -> !s.isEmpty()).forEach(s -> {
            String[] lineSplitted = s.split(": ");
            int lastMonkeyIndex = monkeys.size() - 1;
            switch (lineSplitted[0].strip()) {
                case "Starting items" -> monkeys.get(lastMonkeyIndex)
                        .setItems(Arrays.stream(lineSplitted[1].split(", "))
                                .mapToLong(Integer::parseInt)
                                .boxed()
                                .collect(Collectors.toList()));
                case "Test" -> monkeys.get(lastMonkeyIndex)
                        .setModuloValue(Long.parseLong(lineSplitted[1].split("by ")[1]));
                case "If true" -> monkeys.get(lastMonkeyIndex)
                        .setNextOnTrue(Integer.parseInt(lineSplitted[1].split("monkey ")[1]));
                case "If false" -> monkeys.get(lastMonkeyIndex)
                        .setNextOnFalse(Integer.parseInt(lineSplitted[1].split("monkey ")[1]));
                case "Operation" -> {
                    String[] operationWords = lineSplitted[1].split(" ");
                    monkeys.get(lastMonkeyIndex).setOperation(
                            operationWords[3].equals("+") ? l -> l + Long.parseLong(operationWords[4]) :
                                    operationWords[4].equals("old") ? l -> l * l :
                                            l -> l * Long.parseLong(operationWords[4])
                    );
                }
                default -> monkeys.add(new Monkey());
            }
        });
        return monkeys;
    }
}
