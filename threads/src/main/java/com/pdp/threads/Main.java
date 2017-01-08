package com.pdp.threads;

import com.pdp.threads.controller.BoardController;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * Created by victor on 1/4/17.
 */
public class Main {

    public static void main(String[] args) {
        final BoardController controller = new BoardController(Arrays.asList(1, 2, 3, 4, 5, 6, 16, 8, 9, 10, 11, 12, 13, 14, 15, 7));
        System.out.println(StringUtils.join(controller.solve(), "\n\n\n"));
    }
}
