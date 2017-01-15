package com.pdp.opencl.controllers;

import com.pdp.opencl.models.Board;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by dana on 1/8/17.
 */
public class BoardController {
    private final Board initialBoard;
    private final Set<Board> visited;

    public BoardController(final Board bc) {
        visited = new TreeSet<>();
        initialBoard = bc;
    }

    public List<Board> play() {
        Board board = initialBoard;
        List<Board> moves = Collections.singletonList(board);
        while (moves.stream().noneMatch(Board::isSolution) && !moves.isEmpty()) {
            visited.addAll(moves);
            moves = moves.stream().flatMap(b -> b.moveList().stream())
                    .filter(b -> !visited.contains(b))
                    .collect(Collectors.toList());
        }
        Board b = moves.stream().filter(Board::isSolution).findFirst().get();
        return reconstructBoardPath(b);
    }

    private List<Board> reconstructBoardPath(final Board board) {
        return reconstructBoardPath(Stream.of(board).collect(Collectors.toList()));
    }

    private List<Board> reconstructBoardPath(final List<Board> boards) {
        if (boards.get(0).getParent() != null) {
            boards.add(0, boards.get(0).getParent());
            return reconstructBoardPath(boards);
        }
        return boards;
    }
}