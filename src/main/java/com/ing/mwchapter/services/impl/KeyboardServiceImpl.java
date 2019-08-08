package com.ing.mwchapter.services.impl;

import com.ing.mwchapter.services.IKeyboardService;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;

import static java.lang.Math.*;

public class KeyboardServiceImpl implements IKeyboardService {

    private static final String KEY_REGEX = "[1-9]";

    private static final int KEYBOARD_WIDTH = 3;
    private static final int KEYBOARD_HEIGHT = 3;

    private static final int MOVE_TO_FIRST_KEY_TIME = 0;
    private static final int MOVE_TO_ADJACENT_KEY_TIME = 1;
    private static final int PRESS_KEY_TIME = 0;

    @Override
    public int entryTime(@NotNull String code, @NotNull String keypad) {
        if (!isValidCode(code) || !isValidKeypad(keypad)) {
            throw new IllegalArgumentException();
        }

        List<Integer> codeKeysSequence = toList(code);
        Map<Integer, Integer> indexByKey = getKeypadIndexesByKey(keypad);

        return IntStream.range(0, codeKeysSequence.size() - 1)
            .reduce(MOVE_TO_FIRST_KEY_TIME, (time, codeIndex) ->
                time + PRESS_KEY_TIME + MOVE_TO_ADJACENT_KEY_TIME *
                    distance(
                        indexByKey.get(codeKeysSequence.get(codeIndex)),
                        indexByKey.get(codeKeysSequence.get(codeIndex + 1))));
    }

    private static List<Integer> toList(String keys) {
        return keys.codePoints()
            .boxed()
            .collect(Collectors.toList());
    }

    private static Map<Integer, Integer> getKeypadIndexesByKey(String keypad) {
        AtomicInteger index = new AtomicInteger();
        return toList(keypad).stream()
            .sequential()
            .collect(Collectors.toMap(
                key -> key,
                key -> index.getAndIncrement()));
    }

    private int distance(int currentKeyIndex, int nextKeyIndex) {
        int xIncrement = getX(nextKeyIndex) - getX(currentKeyIndex);
        int yIncrement = getY(nextKeyIndex) - getY(currentKeyIndex);
        return max(abs(xIncrement), abs(yIncrement));
    }

    private static int getX(int index) {
        return index % KEYBOARD_WIDTH;
    }

    private static int getY(int index) {
        return index / KEYBOARD_HEIGHT;
    }

    private static boolean isValidCode(String code) {
        return !code.isEmpty()
            && allKeysMatch(code);
    }

    private static boolean isValidKeypad(String keypad) {
        return getUniqueKeysCount(keypad) == KEYBOARD_HEIGHT * KEYBOARD_WIDTH
            && allKeysMatch(keypad);
    }

    private static boolean allKeysMatch(String keySequence) {
        return streamKeySequence(keySequence)
            .allMatch(key -> key.matches(KEY_REGEX));
    }

    private static long getUniqueKeysCount(String keySequence) {
        return streamKeySequence(keySequence)
            .distinct()
            .count();
    }

    private static Stream<String> streamKeySequence(String keySequence) {
        return Arrays.stream(keySequence.split(""));
    }

}
