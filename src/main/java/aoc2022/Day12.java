package aoc2022;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day12 {
    public static void main(String[] args) throws IOException {
        final String inputDir = "src/main/resources/day12/";
        final String filename = args[0];
        final Path inputFilePath = Path.of(inputDir + filename);

        final List<String> inputLines = Files.readAllLines(inputFilePath);
        final int length = inputLines.size();
        final int width = inputLines.get(0).length();
        final List<Point> allPoints = IntStream.range(0, length * width)
                .mapToObj(value -> new Point(value / width, value % width)).toList();
        final Map<Point, Character> heightmap = allPoints.stream()
                .collect(Collectors.toMap(p -> p, p -> inputLines.get(p.x).charAt(p.y)));
        final Map<Point, Integer> distances = allPoints.stream()
                .collect(Collectors.toMap(p -> p, p -> Integer.MAX_VALUE));

        final Point start = IntStream.range(0, length)
                .filter(i -> inputLines.get(i).contains("S"))
                .mapToObj(i -> new Point(i, inputLines.get(i).indexOf("S")))
                .findFirst()
                .orElseThrow();
        final Point end = IntStream.range(0, length)
                .filter(i -> inputLines.get(i).contains("E"))
                .mapToObj(i -> new Point(i, inputLines.get(i).indexOf("E")))
                .findFirst()
                .orElseThrow();

        heightmap.put(start, 'a');
        heightmap.put(end, 'z');
        distances.put(start, 0);
        final Queue<Point> nextPoints = new LinkedList<>();

        nextPoints.add(start);
        updateDistances(heightmap, distances, nextPoints, length, width);
        System.out.println(distances.get(end));

        heightmap.forEach((point, height) -> {
            if (height.equals('a')) {
                distances.put(point, 0);
                nextPoints.add(point);
            }
        });
        updateDistances(heightmap, distances, nextPoints, length, width);
        System.out.println(distances.get(end));
    }

    private static void updateDistances(final Map<Point, Character> heightmap,
                                        final Map<Point, Integer> distances,
                                        final Queue<Point> nextPoints,
                                        final int length,
                                        final int width) {
        while (!nextPoints.isEmpty()) {
            final Point point = nextPoints.poll();
            getNeighbours(point, width, length).stream()
                    .forEach(nextPoint -> {
                        if (heightmap.get(point) - heightmap.get(nextPoint) >= -1 &&
                                distances.get(nextPoint) > distances.get(point) + 1) {
                            distances.put(nextPoint, distances.get(point) + 1);
                            if (!nextPoints.contains(nextPoint)) {
                                nextPoints.add(nextPoint);
                            }
                        }
                    });
        }
    }

    private static List<Point> getNeighbours(final Point p, final int width, final int length) {
        List<Point> result = new ArrayList<>();
        if (p.x > 0) result.add(new Point(p.x - 1, p.y));
        if (p.x < length - 1) result.add(new Point(p.x + 1, p.y));
        if (p.y > 0) result.add(new Point(p.x, p.y - 1));
        if (p.y < width - 1) result.add(new Point(p.x, p.y + 1));
        return result;
    }
}
