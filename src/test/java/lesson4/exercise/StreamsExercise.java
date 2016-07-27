package lesson4.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class StreamsExercise {
    // https://youtu.be/kxgo7Y4cdA8 Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 1
    // https://youtu.be/JRBWBJ6S4aU Сергей Куксенко и Алексей Шипилёв — Через тернии к лямбдам, часть 2

    // https://youtu.be/O8oN4KSZEXE Сергей Куксенко — Stream API, часть 1
    // https://youtu.be/i0Jr2l3jrDA Сергей Куксенко — Stream API, часть 2


    private static String generateString() {
        final String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final int maxLength = 10;
        final int length = ThreadLocalRandom.current().nextInt(maxLength) + 1;

        return IntStream.range(0, length)
                .mapToObj(letters::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
    }

    private static Person generatePerson() {
        return new Person(generateString(), generateString(), 18 + ThreadLocalRandom.current().nextInt(50));
    }

    private static JobHistoryEntry generateJobHistoryEntry() {
        final int maxDuration = 10;
        final int duration = ThreadLocalRandom.current().nextInt(maxDuration) + 1;
        return new JobHistoryEntry(duration, generatePosition(), generateEmployer());
    }

    private static String generateEmployer() {
        final String[] employers = {"epam", "google", "yandex", "abc"};

        return employers[ThreadLocalRandom.current().nextInt(employers.length)];
    }

    private static String generatePosition() {
        final String[] positions = {"dev", "QA", "BA"};

        return positions[ThreadLocalRandom.current().nextInt(positions.length)];
    }

    private static List<JobHistoryEntry> generateJobHistory() {
        int maxLength = 10;
        final int length = ThreadLocalRandom.current().nextInt(maxLength) + 1;

        return Stream.generate(StreamsExercise::generateJobHistoryEntry)
                .limit(length - 1)
                .collect(toList());
    }

    /**
     * was doing
     * @return
     */
    private static Employee generateEmployee() {
        return new Employee(
        		generatePerson(),
        		generateJobHistory()
        		);
    }
    
    /**
     * was doing
     * @return
     */
    private static List<Employee> generateEmployeeList() {
        int maxLength = 10;
        final int length = ThreadLocalRandom.current().nextInt(maxLength) + 1;
        
        return Stream.generate(StreamsExercise::generateEmployee)
                .limit(length - 1)
                .collect(toList());
    }

    /**
     * was doing
     */
    @Test
    public void sumEpamDurations() {
        final List<Employee> employees = generateEmployeeList();

        //for expected
        int expected = 0;

        for (Employee e: employees) {
            for (JobHistoryEntry j : e.getJobHistory()) {
                if (j.getEmployer().equals("epam")) {
                    expected += j.getDuration();
                }
            }
        }

        // TODO for result
        int result = employees.stream()
        		.flatMap(e -> e.getJobHistory().stream()
        				.filter(j -> j.getEmployer().equals("epam")))
        		.mapToInt(e -> e.getDuration()).sum();
        assertEquals(expected, result);
    }

    /**
     * was doing
     */
    @Test
    public void getAllEpamEmployees() {
    	
    	// TODO all persons with experience in epam
    	final List<Employee> employees = generateEmployeeList();
    	//create list with only persons with experience in epam
        List<Person> epamEmployees = new LinkedList<>();
        for (Employee e: employees) {
            for (JobHistoryEntry j : e.getJobHistory()) {
                if (j.getEmployer().equals("epam") && !epamEmployees.contains(e.getPerson())) {
                	epamEmployees.add(e.getPerson());
                }
            }
        }
        //TODO find use stream
        List<Person> epamEmployeesLambda = employees.stream()
        		.filter(e -> e.getJobHistory().stream()
        				.map(JobHistoryEntry::getEmployer)
        				.anyMatch("epam"::equals)
        				)
        		.map(e -> e.getPerson())
        		.collect(toList());
        assertThat(epamEmployees, is(epamEmployeesLambda));
    }

    @Test
    public void getEmployeesStartedFromEpam() {
        // TODO all persons with first experience in epam
        //final List<Employee> employees = generateEmployeeList();
        final List<Employee> employees = getEmployees();
        //a standart way
        List<Person> epamEmployees = new LinkedList<>();
        for (Employee e: employees) {
            final JobHistoryEntry j = e.getJobHistory().get(0);
            if (j.getEmployer().equals("epam") && !epamEmployees.contains(e.getPerson())) {
                epamEmployees.add(e.getPerson());
            }
        }
        //test
        //System.out.println("epamEmployees" + epamEmployees);
        List<Person> epamEmployeesLambda = employees.stream()
                .filter(e -> e.getJobHistory()
                        .get(0)
                        .getEmployer()
                        .equals("epam"))
                .map(Employee::getPerson)
                .collect(toList());
        //test
        //System.out.println("epamEmployeesLambda " + epamEmployeesLambda);
        assertThat(epamEmployees, is(epamEmployeesLambda));
    }

    // https://github.com/senia-psm/java-streams

    @Test
    public void indexByFirstEmployer() {
        //TODO
        final List<Employee> employees = getEmployees();
        Map<String, List<Person>> employeesIndex = new HashMap<>();
        for (Employee e: employees) {
            for (JobHistoryEntry j : e.getJobHistory()) {
                if (!employeesIndex.containsKey(j.getEmployer())) {
                    List<Person> persons = new LinkedList<>();
                    persons.add(e.getPerson());
                    employeesIndex.put(j.getEmployer(), persons);
                } else {
                    Person p = e.getPerson();
                    String employer = j.getEmployer();
                    List<Person> persons = employeesIndex.get(employer);
                    if (!persons.contains(p)) {
                        persons.add(p);
                    }
                    employeesIndex.put(employer, persons);
                }
            }
        }
    //print it for test use streams
    employeesIndex.entrySet().forEach(System.out::println);
    
    
    //TODO it use streams API
    //цель получить список пар первый работодатель/список лиц с первым работодателем
    Stream<PersonEmployerPair> flatMap = employees.stream()
            .flatMap(e ->
                    e.getJobHistory().stream()
                            .findFirst()
                            .map(Stream::of)
                            .orElseGet(Stream::empty)
                            .map(er -> new PersonEmployerPair(e.getPerson(), er.getEmployer()))
                    );
    //Collectors.mapping(mapper, downstream)
    Map<String, List<PersonEmployerPair>> collect = flatMap
            .collect(Collectors.groupingBy(
                    PersonEmployerPair::getEmployer, toList()));
    
    
            
    }

    // TODO class PersonEmployerPair
    private static class PersonEmployerPair {
        private Person p;
        private String employer;
        
        public PersonEmployerPair(Person p, String employer) {
            this.p = p;
            this.employer = employer;
        }
        
        public Person getPerson() {
            return p;
        }
        
        public String getEmployer() {
            return employer;
        }
    }


    @Test
    public void employersStuffLists() {
        Map<String, List<Person>> employersStuffLists = null;// TODO
        throw new UnsupportedOperationException();
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
