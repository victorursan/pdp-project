package co.pdp.opencl.controller;

import co.pdp.opencl.models.Board;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Created by victor on 1/8/17.
 */
public final class BoardController {
    private final ExecutorService executor;
    private final Board initialBoard;
    private final Set<Board> visited;

    public BoardController(final Board initialBoard) {
        executor = Executors.newFixedThreadPool(16);
        this.initialBoard = initialBoard;
        visited = new TreeSet<>();
    }

    public List<Board> solve() {
        final Optional<Board> boardOptional = getSolutions(initialBoard);
        executor.shutdown();
        if (boardOptional.isPresent())
            return reconstructBoardPath(boardOptional.get());
        return Collections.emptyList();
    }

    private Optional<Board> getSolutions(final Board board) {
        List<Board> moves = Collections.singletonList(board);
        while (moves.stream().noneMatch(Board::isSolution) && !moves.isEmpty()) {
            visited.addAll(moves);

            final List<CompletableFuture<List<Board>>> completableFutures = moves.stream()
                    .map(b -> CompletableFuture.supplyAsync(b::move, executor))
                    .collect(toList());

            moves = completableFutures.parallelStream()
                    .flatMap(e -> {
                        try {
                            return e.get().stream();
                        } catch (Exception ignore) {
                            return Stream.empty();
                        }
                    })
                    .filter(b -> !visited.contains(b))
                    .collect(toList());

        }
        return moves.stream().filter(Board::isSolution).findFirst();
    }

    private List<Board> reconstructBoardPath(final Board board) {
        return reconstructBoardPath(Stream.of(board).collect(toList()));
    }

    private List<Board> reconstructBoardPath(final List<Board> boards) {
        if (boards.get(0).getParent().isPresent()) {
            boards.add(0, boards.get(0).getParent().get());
            return reconstructBoardPath(boards);
        }
        return boards;
    }
}
