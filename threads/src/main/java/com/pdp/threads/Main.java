package com.pdp.threads;

import com.pdp.threads.controller.BoardController;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * Created by victor on 1/4/17.
 */
public class Main {

    public static void main(String[] args) {
        final BoardController controller = new BoardController(Arrays.asList(13, 12, 7, 11, 14, 5, 3, 15, 2, 6, 9, 1, 8, 4, 10, 16));
        System.out.println(StringUtils.join(controller.solve(), "\n\n\n"));
    }
}
