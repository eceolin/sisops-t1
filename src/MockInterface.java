import java.util.*;

public class MockInterface {

    public static void main(String[] args) throws Exception {
        Program program = new Program("program1", false);

        if (program != null) {
            Process process = new Process(1, program, 0, 500, program.memory.size());
            Process process2 = new Process(2, program, 2, 0, program.memory.size());
            Process process3 = new Process(3, program, 1, 500, program.memory.size());

            Comparator<Process> comparatorPriority = Comparator.comparingInt(c -> c.priority);
            Comparator<Process> comparatorCode = Comparator.comparingInt(c -> c.code);

            SortedSet<Process> processes = new TreeSet<>(comparatorPriority.thenComparing(comparatorCode));

            processes.add(process);
            processes.add(process2);
            processes.add(process3);

            new ScaleByPriorityCPU(processes).start();
        }
    }
}
