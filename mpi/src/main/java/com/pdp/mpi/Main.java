package com.pdp.mpi;

import com.pdp.mpi.controller.BoardController;
import com.pdp.mpi.models.Board;
import mpi.MPI;
import mpi.MPIException;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by victor on 1/8/17.
 */
public class Main {
    public static void main(String[] args) {
        try {
            MPI.Init(args);
            if (MPI.COMM_WORLD.getRank() == 0) {
                final BoardController controller = new BoardController(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 16, 11, 12, 13, 14, 15, 10));

                final long before = System.currentTimeMillis();
                final List<Board> solution = controller.solve();
                final long after = System.currentTimeMillis();

                System.out.println(StringUtils.join(solution, "\n\n\n"));
                System.out.println("Delta time: " + (after - before));
            } else {
                BoardController.consumeMPI();
            }
            MPI.Finalize();
        } catch (MPIException e) {
            e.printStackTrace();
        }
    }
}
//mpirun -np 5 java -cp "/Users/victor/.gradle/caches/modules-2/files-2.1/com.google.guava/guava/20.0/89507701249388e1ed5ddcf8c41f4ce1be7831ef/guava-20.0.jar:/Users/victor/.gradle/caches/modules-2/files-2.1/org.apache.commons/commons-lang3/3.5/6c6c702c89bfff3cd9e80b04d668c5e190d588c6/commons-lang3-3.5.jar:." com/pdp/mpi/Main
