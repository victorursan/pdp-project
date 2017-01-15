package com.pdp.threads;

import com.pdp.threads.controller.BoardController;
import com.pdp.threads.models.Board;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by victor on 1/4/17.
 */
public class Main {

    public static void main(String[] args) {
        final BoardController controller = new BoardController(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 16, 11, 12, 13, 14, 15, 10));
        final long before = System.currentTimeMillis();
        final List<Board> solution = controller.solve();
        final long after = System.currentTimeMillis();

        System.out.println(StringUtils.join(solution, "\n\n\n"));
        System.out.println("Delta time: " + (after - before));
    }
}
