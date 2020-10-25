import java.util.*;

public class MockInterface2 {

    public static void main(String[] args) throws Exception {
        Program program = new Program("program1", false);

        if (program != null) {
            Process process = new Process(1, program, 0, 0, program.memory.size());
            Process process2 = new Process(2, program, 2, 50, program.memory.size());

            List<Process> processes = new ArrayList<>();

            processes.add(process);
            processes.add(process2);

            new ScaleByRoundRobinCPU(processes, 2).start();
        }
    }
}
