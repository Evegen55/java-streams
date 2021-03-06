package lesson5.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

public class CollectorsExercise {

    @Test
    public void getTheCoolestOne() {
        final Map<String, Person> coolestByPosition = getCoolestByPosition(getEmployees());

        coolestByPosition.forEach((position, person) -> System.out.println(position + " -> " + person));
    }

    private static class PersonPositionDuration {
        private final Person person;
        private final String position;
        private final int duration;

        public PersonPositionDuration(Person person, String position, int duration) {
            this.person = person;
            this.position = position;
            this.duration = duration;
        }

        public Person getPerson() {
            return person;
        }

        public String getPosition() {
            return position;
        }

        public int getDuration() {
            return duration;
        }

        @Override
        public String toString() {
            return "PersonPositionDuration{" +
                    "person=" + person +
                    ", position='" + position + '\'' +
                    ", duration=" + duration +
                    '}';
        }
    }

    // With the longest duration on single job
    private Map<String, Person> getCoolestByPosition(List<Employee> employees) {
        //employees.stream().forEach(System.out::println);
        // First option
        // Collectors.maxBy
        // Collectors.collectingAndThen
        // Collectors.groupingBy

        //make a stream with static possibilities
        Stream<PersonPositionDuration> personPositionDurationStream = employees.stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .map(ppd -> new PersonPositionDuration(e.getPerson(), ppd.getPosition(), ppd.getDuration())));
        //make a map that first groupping by position, then collecting to a list, find max duration and return person
        Map<String, Person> collection1 = personPositionDurationStream
                .collect(groupingBy(
                        PersonPositionDuration::getPosition,
                        collectingAndThen(maxBy(comparing(PersonPositionDuration::getDuration)),
                                personPositionDuration -> personPositionDuration.get().getPerson())
                        )
                );
        //print result
        //System.out.println("===first option");
        //collection1.entrySet().stream().forEach(System.out::println);

        // Second option
        // Collectors.toMap
        // iterate twice: stream...collect(...).stream()...
        //System.out.println("===second option");
        //make a stream with static possibilities
        Stream<PersonPositionDuration> personPositionDurationStream2 = employees.stream()
                .flatMap(e -> e.getJobHistory().stream()
                        .map(ppd -> new PersonPositionDuration(e.getPerson(), ppd.getPosition(), ppd.getDuration())));
        //make a middleware map
        Map<String, PersonPositionDuration> collection2 = personPositionDurationStream2
                .collect(toMap(
                        PersonPositionDuration::getPosition,
                        Function.identity(),
                        (pp1, pp2) -> pp1.getDuration() > pp2.getDuration() ? pp1 : pp2));
        //print it
        //collection2.entrySet().stream().forEach(System.out::println);
        //make end map
        Map<String, Person> collection3 = collection2.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> e.getValue().getPerson()));
        //print it
        //collection3.entrySet().stream().forEach(System.out::println);

        return collection3;
    }

    @Test
    public void getTheCoolestOne2() {
        final Map<String, Person> coolestByPosition = getCoolestByPosition2(getEmployees());

        coolestByPosition.forEach((position, person) -> System.out.println(position + " -> " + person));
    }

    // With the longest sum duration on this position
    // { John Doe, [{dev, google, 4}, {dev, epam, 4}] } предпочтительнее, чем { A B, [{dev, google, 6}, {QA, epam, 100}]}
    private Map<String, Person> getCoolestByPosition2(List<Employee> employees) {
        // TODO
        //map position -> sum of durations for position
        final Stream<PersonPositionDuration> stream = employees.stream()
                .flatMap(e -> {
                            Map<String, Integer> collect = e.getJobHistory().stream()
                                    .collect(toMap(
                                            JobHistoryEntry::getPosition,
                                            JobHistoryEntry::getDuration,
                                            (d1, d2) -> d1 + d2
                                    ));
                            return collect.entrySet().stream()
                                    .map(e1 -> new PersonPositionDuration(e.getPerson(), e1.getKey(), e1.getValue()));
                        }
                );
        final Map<String, Person> collectAsMap = stream
                .collect(groupingBy(
                        PersonPositionDuration::getPosition,
                        collectingAndThen(
                                (maxBy(comparing(PersonPositionDuration::getDuration))),
                                personPositionDuration -> personPositionDuration.get().getPerson())));
        //print it
        collectAsMap.entrySet().stream().forEach(System.out::println);

        //throw new UnsupportedOperationException();
        return collectAsMap;
    }

    private static String generateString() {
        final String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final int maxLength = 10;
        final int length = ThreadLocalRandom.current().nextInt(maxLength) + 1;

        return IntStream.range(0, length)
                .mapToObj(letters::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
    }

    private static String[] generateStringArray(int length) {
        return Stream.generate(CollectorsExercise::generateString)
                .limit(length)
                .toArray(String[]::new);
    }

    public static String pickString(String[] array) {
        return array[ThreadLocalRandom.current().nextInt(array.length)];
    }

    private static class Key {
        private final String id;

        public Key(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            return id.equals(key.id);

        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }

    private static class Value {
        private final String keyId;

        public Value(String keyId) {
            this.keyId = keyId;
        }

        public String getKeyId() {
            return keyId;
        }
    }

    private static class Pair {
        private final Key key;
        private final Value value;

        public Pair(Key key, Value value) {
            this.key = key;
            this.value = value;
        }

        public Key getKey() {
            return key;
        }

        public Value getValue() {
            return value;
        }
    }

    private static List<Pair> generatePairs(int idCount, int length) {
        final String[] ids = generateStringArray(idCount);

        return Stream.generate(() -> new Pair(new Key(pickString(ids)), new Value(pickString(ids))))
                .limit(length)
                .collect(toList());
    }

    private static class SubResult {
        private final Map<Key, List<Value>> subResult;
        private final Map<String, List<Key>> knownKeys;
        private final Map<String, List<Value>> valuesWithoutKeys;

        public SubResult(Map<Key, List<Value>> subResult, Map<String, List<Key>> knownKeys, Map<String, List<Value>> valuesWithoutKeys) {
            this.subResult = subResult;
            this.knownKeys = knownKeys;
            this.valuesWithoutKeys = valuesWithoutKeys;
        }

        public Map<Key, List<Value>> getSubResult() {
            return subResult;
        }

        public Map<String, List<Value>> getValuesWithoutKeys() {
            return valuesWithoutKeys;
        }

        public Map<String, List<Key>> getKnownKeys() {
            return knownKeys;
        }
    }

    private static class MapPair {
        private final Map<String, Key> keyById;
        private final Map<String, List<Value>> valueById;

        public MapPair(Map<String, Key> keyById, Map<String, List<Value>> valueById) {
            this.keyById = keyById;
            this.valueById = valueById;
        }

        public Map<String, Key> getKeyById() {
            return keyById;
        }

        public Map<String, List<Value>> getValueById() {
            return valueById;
        }
    }

    private static <K, V, M extends Map<K, V>>
    BinaryOperator<M> mapMerger(BinaryOperator<V> mergeFunction) {
        return (m1, m2) -> {
            for (Map.Entry<K, V> e : m2.entrySet())
                m1.merge(e.getKey(), e.getValue(), mergeFunction);
            return m1;
        };
    }

    @Test
    public void collectKeyValueMap() {
        final List<Pair> pairs = generatePairs(10, 100);

        // В два прохода
        // final Map<String, Key> keyMap1 = pairs.stream()...

        // final Map<String, List<Value>> valuesMap1 = pairs.stream()...

        // В каждом Map.Entry id ключа должно совпадать с keyId для каждого значения в списке
        // final Map<Key, List<Value>> keyValuesMap1 = valueMap1.entrySet().stream()...

        // В 1 проход в 2 Map с использованием MapPair и mapMerger
        final MapPair res2 = pairs.stream()
                .collect(new Collector<Pair, MapPair, MapPair>() {
                    @Override
                    public Supplier<MapPair> supplier() {
                        // TODO
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public BiConsumer<MapPair, Pair> accumulator() {
                        // TODO add key and value to maps
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public BinaryOperator<MapPair> combiner() {
                        // TODO use mapMerger
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public Function<MapPair, MapPair> finisher() {
                        return Function.identity();
                    }

                    @Override
                    public Set<Characteristics> characteristics() {
                        return Collections.unmodifiableSet(EnumSet.of(
                                Characteristics.CONCURRENT,
                                Characteristics.UNORDERED,
                                Characteristics.IDENTITY_FINISH));
                    }
                });

        final Map<String, Key> keyMap2 = res2.getKeyById();
        final Map<String, List<Value>> valuesMap2 = res2.getValueById();

        // final Map<Key, List<Value>> keyValuesMap2 = valueMap2.entrySet().stream()...

        // Получение результата сразу:

        final SubResult res3 = pairs.stream()
                .collect(new Collector<Pair, SubResult, SubResult>() {
                    @Override
                    public Supplier<SubResult> supplier() {
                        // TODO
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public BiConsumer<SubResult, Pair> accumulator() {
                        // TODO add key to map, then check value.keyId and add it to one of maps
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public BinaryOperator<SubResult> combiner() {
                        // TODO use mapMerger, then check all valuesWithoutKeys
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public Function<SubResult, SubResult> finisher() {
                        return Function.identity();
                    }

                    @Override
                    public Set<Characteristics> characteristics() {
                        return Collections.unmodifiableSet(EnumSet.of(
                                Characteristics.CONCURRENT,
                                Characteristics.UNORDERED,
                                Characteristics.IDENTITY_FINISH));
                    }
                });

        final Map<Key, List<Value>> keyValuesMap3 = res3.getSubResult();

        // compare results
    }

    private List<Employee> getEmployees() {
        return Arrays.asList(
                new Employee(
                        new Person("John", "Galt", 20),
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(2, "dev", "google")
                        )),
                new Employee(
                        new Person("John", "Doe", 21),
                        Arrays.asList(
                                new JobHistoryEntry(4, "BA", "yandex"),
                                new JobHistoryEntry(2, "QA", "epam"),
                                new JobHistoryEntry(2, "dev", "abc")
                        )),
                new Employee(
                        new Person("John", "White", 22),
                        Collections.singletonList(
                                new JobHistoryEntry(6, "QA", "epam")
                        )),
                new Employee(
                        new Person("John", "Galt", 23),
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(2, "dev", "google")
                        )),
                new Employee(
                        new Person("John", "Doe", 24),
                        Arrays.asList(
                                new JobHistoryEntry(4, "QA", "yandex"),
                                new JobHistoryEntry(2, "BA", "epam"),
                                new JobHistoryEntry(2, "dev", "abc")
                        )),
                new Employee(
                        new Person("John", "White", 25),
                        Collections.singletonList(
                                new JobHistoryEntry(6, "QA", "epam")
                        )),
                new Employee(
                        new Person("John", "Galt", 26),
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(1, "dev", "google")
                        )),
                new Employee(
                        new Person("Bob", "Doe", 27),
                        Arrays.asList(
                                new JobHistoryEntry(4, "QA", "yandex"),
                                new JobHistoryEntry(2, "QA", "epam"),
                                new JobHistoryEntry(2, "dev", "abc")
                        )),
                new Employee(
                        new Person("John", "White", 28),
                        Collections.singletonList(
                                new JobHistoryEntry(6, "BA", "epam")
                        )),
                new Employee(
                        new Person("John", "Galt", 29),
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(1, "dev", "google")
                        )),
                new Employee(
                        new Person("John", "Doe", 30),
                        Arrays.asList(
                                new JobHistoryEntry(4, "QA", "yandex"),
                                new JobHistoryEntry(2, "QA", "epam"),
                                new JobHistoryEntry(5, "dev", "abc")
                        )),
                new Employee(
                        new Person("Bob", "White", 31),
                        Collections.singletonList(
                                new JobHistoryEntry(6, "QA", "epam")
                        ))
        );
    }

}
