package com.pdp.threads;

import com.pdp.threads.models.Board;

import java.util.Arrays;

/**
 * Created by victor on 1/4/17.
 */
public class Main {

    public static void main(String[] args) {
        final Board board = new Board(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 16, 12, 13, 14, 15, 11));
        System.out.println(board.move());
    }
}
