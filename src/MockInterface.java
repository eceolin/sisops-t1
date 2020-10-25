import java.util.*;

public class MockInterface {

    public static void main(String[] args) {
        Program program = new Program("program1", false);

        if (program != null) {
            Process process = new Process(1, program, 2, 0, program.memory.size());

            Comparator<Process> comparatorPriority = Comparator.comparingInt(c -> c.priority);
            Comparator<Process> comparatorCode = Comparator.comparingInt(c -> c.code);

            SortedSet<Process> processes = new TreeSet<>(comparatorPriority.thenComparing(comparatorCode));

            processes.add(process);

            new ScaleByPriorityCPU(processes).start();
        }
    }
}
