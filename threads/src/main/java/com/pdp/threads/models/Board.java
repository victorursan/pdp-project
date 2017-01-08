package com.pdp.threads.models;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

/**
 * Created by victor on 1/7/17.
 */
public final class Board implements Comparable {
    final private List<Integer> pieces;
    final private Board parent;
    final private boolean solution;

    public Board(final List<Integer> pieces) {
        this.pieces = pieces;
        this.parent = null;
        this.solution = checkSolution();
    }

    private Board(final List<Integer> pieces, final Board parent) {
        this.pieces = pieces;
        this.parent = parent;
        this.solution = checkSolution();
    }

    public Optional<Board> getParent() {
        return Optional.ofNullable(parent);
    }


    public List<Board> move() {
        final Integer emptyPosition = getEmptyPiece();
        return concat(concat(concat(concat(Stream.empty(),
                isValidInteger(emptyPosition - 1, integer -> (integer + 1) % 4 != 0)),
                isValidInteger(emptyPosition + 1, integer -> (integer - 1) % 4 != 3)),
                isValidInteger(emptyPosition - 4, integer -> integer >= 0)),
                isValidInteger(emptyPosition + 4, integer -> integer < 16))
                .map(newPos -> new Board(swap(emptyPosition, newPos), this))
                .collect(toList());
    }

    private boolean checkSolution() {
        return Ordering.natural().isOrdered(pieces);
    }

    public boolean isSolution() {
       return solution;
    }

    @NotNull
    private Integer getEmptyPiece() {
        return pieces.indexOf(16);
    }

    private Stream<Integer> isValidInteger(final Integer number, final Predicate<Integer> predicate) {
        return Stream.of(number).filter(predicate);
    }

    private List<Integer> swap(final int firstIndex, final int secondIndex) {
        final List<Integer> copiedList = pieces.stream().collect(toList());
        Collections.swap(copiedList, firstIndex, secondIndex);
        return copiedList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Board board = (Board) o;

        return new EqualsBuilder()
                .append(pieces, board.pieces)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new org.apache.commons.lang3.builder.HashCodeBuilder(17, 37)
                .append(pieces)
                .toHashCode();
    }

    @Override
    public String toString() {
        List<String> replacedPieces = pieces.stream().map(e -> {
            if (e == 16) return "x";
            return e.toString();
        }).collect(toList());
        return StringUtils.join(Lists.partition(replacedPieces, 4).stream()
                .map(elements -> StringUtils.join(elements, "|\t"))
                .collect(toList()), "\n--------------\n");
    }

    @Override
    public int compareTo(final Object o) {
        if (this == o) return 0;

        if (o == null || getClass() != o.getClass()) return -1;

        Board board = (Board) o;
        if (board.pieces.equals(this.pieces)) {
            return 0;
        }
        return 1;
    }
}
