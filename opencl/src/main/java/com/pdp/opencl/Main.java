package com.pdp.opencl;

import co.pdp.opencl.controller.BoardController;
import co.pdp.opencl.models.Board;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by dana on 1/8/17.
 */
public class Main {
    public static void main(String[] args) {
        Board b = new Board(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 16, 11, 12, 13, 14, 15, 10});
        final BoardController controller = new BoardController(b);
        final long before = System.currentTimeMillis();
        final List<Board> solution = controller.solve();
        final long after = System.currentTimeMillis();

        System.out.println(StringUtils.join(solution, "\n\n\n"));
        System.out.println("Delta time: " + (after - before));
    }
}
