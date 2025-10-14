package ru.baldenna.unleashagent.core.common;

import com.cedarsoftware.util.DeepEquals;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.baldenna.unleashagent.core.utils.CompareUtils.notEquals;

@Slf4j
public class UniversalComparator {

    @SneakyThrows // TODO убрать
    public boolean compareObjects(Object first, Object second) {
        var firstFields = Arrays.stream(first.getClass().getFields())
                .peek(field -> field.setAccessible(true))
                .collect(Collectors.toSet());

        var secondFields = Arrays.stream(second.getClass().getFields())
                .peek(field -> field.setAccessible(true))
                .collect(Collectors.toSet());

        for (Field firstField : firstFields) {
            var secondFieldOpt = secondFields.stream()
                    .filter(secondField -> secondField.getName().equals(firstField.getName()))
                    .findFirst();
            if (secondFieldOpt.isEmpty()) {
                log.warn("Field '{}' not found in class {}", firstField.getName(), second.getClass());
                return false;
            }
            Object localValue = firstField.get(first);
            Object remoteValue = secondFieldOpt.get().get(second);

            if (firstField.getDeclaringClass().isAssignableFrom(Collection.class)) {
                var firstFieldCollection = (Collection<Object>)localValue;
                var secondFieldCollection = (Collection<Object>)remoteValue;
                // TODO развилка на примитивы и не примитивы

               var missedInSecondCount = firstFieldCollection.stream()
                        .filter(firstFieldItem -> !secondFieldCollection.contains(firstFieldItem))
                        .peek(missedInSecond -> log.info("Remote field '{}' collection not contains value {}", firstField.getName(), missedInSecond))
                        .collect(Collectors.toSet()).size();

               var missedInFirstCount = secondFieldCollection.stream()
                        .filter(secondFieldItem -> !firstFieldCollection.contains(secondFieldItem))
                        .peek(missedInSecond -> log.info("Local field '{}' collection not contains value {}", firstField.getName(), missedInSecond))
                        .collect(Collectors.toSet()).size();

               if (missedInSecondCount > 0 || missedInFirstCount > 0) {
                   return false;
               }

            } else  {
                // TODO развилка на примитивы и не примитивы
                if (notEquals(localValue, remoteValue)) {
                    log.info("{} differ in field '{}': local={}, remote={}",
                            first.getClass(), firstField.getDeclaringClass(), localValue, remoteValue);
                    return false;
                }
            }
        }

        for (Field secondField : secondFields) {
            var missedFieldInFirst = firstFields.stream()
                    .noneMatch(firstField -> firstField.getName().equals(secondField.getName()));
            if (missedFieldInFirst) {
                log.warn("Field '{}' not found in class {}", secondField.getName(), first.getClass());
                return false;
            }
        }

        return true;
    }


    public boolean compareWithLib(Object first, Object second) {
        Map<String, Object> options = new HashMap<>();
        boolean same = DeepEquals.deepEquals(first, second, options);
        if (!same) {
            String diff = (String) options.get(DeepEquals.DIFF);
            log.info("{} difference: {}", first.getClass().getSimpleName(), diff);
        }

        return same;
    }

    public Optional<Object> compareWithDiffResult(Object first, Object second) {
        Map<String, Object> options = new HashMap<>();
        options.put(DeepEquals.INCLUDE_DIFF_ITEM, true);
        boolean same = DeepEquals.deepEquals(first, second, options);
        if (!same) {
            String diff = (String) options.get(DeepEquals.DIFF);
            log.info("{} difference: {}", first.getClass().getSimpleName(), diff);
            return Optional.of(options.get(DeepEquals.DIFF_ITEM));
        }

        return Optional.empty();
    }
}
