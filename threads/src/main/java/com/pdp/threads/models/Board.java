package com.pdp.threads.models;

import com.google.common.collect.Ordering;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

/**
 * Created by victor on 1/7/17.
 */
public final class Board {
    final private List<Integer> pieces;

    public Board(final List<Integer> pieces) {
        this.pieces = pieces;
    }

    @NotNull
    private Integer getEmptyPiece() {
        return pieces.indexOf(16);
    }

    public List<Board> move() {
        final Integer emptyPosition = getEmptyPiece();
        return concat(concat(concat(concat(Stream.empty(),
                isValidInteger(emptyPosition - 1, integer -> integer >= (emptyPosition / 4) * 4)),
                isValidInteger(emptyPosition + 1, integer -> integer < ((emptyPosition / 4) + 1) * 4)),
                isValidInteger(emptyPosition - 4, integer -> integer >= 0)),
                isValidInteger(emptyPosition + 4, integer -> integer < 16))
                .map(newPos -> swap(emptyPosition, newPos))
                .map(Board::new)
                .collect(toList());
    }

    private Stream<Integer> isValidInteger(final Integer number, final Predicate<Integer> predicate) {
        return Stream.of(number).filter(predicate);
    }

    private List<Integer> swap(final int firstIndex, final int secondIndex) {
        final List<Integer> copiedList = pieces.stream().collect(toList());
        Collections.swap(copiedList, firstIndex, secondIndex);
        return copiedList;
    }

    public boolean isSolution() {
        return Ordering.natural().isOrdered(pieces);
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
        return "Board{" +
                "pieces=" + pieces +
                '}';
    }
}
