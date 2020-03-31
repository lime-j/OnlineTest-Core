package com.onlinejudge.examservice;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class RankedUser implements Comparable<RankedUser> {
    private String userID;
    private int penalty;
    private int solved;
    private int rank;

    @Override
    public int compareTo(@NotNull RankedUser o) {
        if (solved != o.solved) {
            return -Integer.compare(solved, o.solved);
        } else return Integer.compare(penalty, o.penalty);
    }
}