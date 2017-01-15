package com.pdp.opencl;

import com.pdp.opencl.controllers.BoardController;
import com.pdp.opencl.models.Board;

/**
 * Created by dana on 1/8/17.
 */
public class Main {
    public static void main(String[] args) {
        Board b = new Board(new int[]{1, 2, 3, 16, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 4});
        b.moveList().forEach(Board::toStr);
    }
}
