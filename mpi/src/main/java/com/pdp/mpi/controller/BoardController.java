package com.pdp.mpi.controller;

import com.google.common.primitives.Ints;
import com.pdp.mpi.models.Board;
import mpi.MPI;
import mpi.MPIException;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Created by victor on 1/8/17.
 */
public final class BoardController {
    private final Board initialBoard;

    public BoardController(final List<Integer> initialList) {
        initialBoard = new Board(initialList);
    }

    private static List<Board> reconstructBoardPath(final Board board) {
        return reconstructBoardPath(Stream.of(board).collect(toList()));
    }

    private static List<Board> reconstructBoardPath(final List<Board> boards) {
        if (boards.get(0).getParent().isPresent()) {
            boards.add(0, boards.get(0).getParent().get());
            return reconstructBoardPath(boards);
        }
        return boards;
    }

    public static void consumeMPI() {
        try {
            final int[] initialPieces = new int[16];
            MPI.COMM_WORLD.recv(initialPieces, 16, MPI.INT, 0, 50);

            final Board currentB = new Board(Ints.asList(initialPieces));
            List<Board> newBoards = Collections.singletonList(currentB);

            final Set<Board> visited = new TreeSet<>();

            while (true) {
                visited.addAll(newBoards);

                newBoards = newBoards.stream()
                        .flatMap(Board::move)
                        .filter(e -> !visited.contains(e))
                        .collect(toList());

                final Optional<Board> optBoard = newBoards.stream()
                        .filter(Board::isSolution)
                        .findFirst();

                if (optBoard.isPresent()) {
                    final List<Board> solution = reconstructBoardPath(optBoard.get());

                    final int solutionSize = solution.size();

                    MPI.COMM_WORLD.send(new int[]{solutionSize}, 1, MPI.INT, 0, 50);

                    final int[] toSend = new int[solutionSize * 16];
                    for (int i = 0; i < solutionSize; ++i) {
                        final Board b = solution.get(i);
                        for (int j = 0; j < 16; ++j) {
                            toSend[i * 16 + j] = b.getPieces().get(j);
                        }
                    }
                    MPI.COMM_WORLD.send(toSend, solutionSize * 16, MPI.INT, 0, 50);
                } else {
                    MPI.COMM_WORLD.send(new int[]{-1}, 1, MPI.INT, 0, 50);
                }
                final int[] toDo = new int[1];
                MPI.COMM_WORLD.recv(toDo, 1, MPI.INT, 0, 50);
                if (toDo[0] == -1) {
                    break;
                }
            }
        } catch (MPIException e) {
            e.printStackTrace();
        }
    }

    public List<Board> solve() {
        try {
            final List<Board> solution = new LinkedList<>();
            solution.add(initialBoard);

            if (initialBoard.isSolution())
                return solution;

            final List<Board> boards = initialBoard.move().collect(toList());

            for (int i = 0; i < boards.size(); ++i) {
                final int[] toSend = new int[16];
                final Board b = boards.get(i);
                for (int j = 0; j < 16; ++j) {
                    toSend[j] = b.getPieces().get(j);
                }
                MPI.COMM_WORLD.send(toSend, 16, MPI.INT, i + 1, 50);
            }

            boolean isSolution = false;
            while (!isSolution) {
                for (int i = 0; i < boards.size(); ++i) {
                    final int[] toDo = new int[1];
                    MPI.COMM_WORLD.recv(toDo, 1, MPI.INT, i + 1, 50);

                    if (toDo[0] != -1) {
                        final int mpiSize = toDo[0];
                        final int[] response = new int[mpiSize * 16];
                        MPI.COMM_WORLD.recv(response, mpiSize * 16, MPI.INT, i + 1, 50);
                        if (!isSolution) {
                            for (int j = 0; j < mpiSize; ++j) {
                                final List<Integer> subArr = Ints.asList(ArrayUtils.subarray(response, j * 16, (j + 1) * 16));
                                solution.add(new Board(subArr));
                            }
                        }
                        isSolution = true;
                    }
                }

                if (!isSolution) {
                    for (int i = 0; i < boards.size(); ++i) {
                        MPI.COMM_WORLD.send(new int[]{0}, 1, MPI.INT, i + 1, 50);
                    }
                }
            }

            if (solution.size() > 1) {
                for (int i = 0; i < 4; ++i) {
                    MPI.COMM_WORLD.send(new int[]{-1}, 1, MPI.INT, i + 1, 50);
                }
            }
            return solution;
        } catch (MPIException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

}
