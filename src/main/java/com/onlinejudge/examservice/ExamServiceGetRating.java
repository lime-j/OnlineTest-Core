package com.onlinejudge.examservice;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

import static java.lang.Integer.max;
import static java.lang.Integer.min;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.util.Collections.sort;


/*
    按照Codeforces的方式计算Rating
 */

public class ExamServiceGetRating {
    private static final int MAX_PANS = 10000;
    private static Comparator<Participant> c = new Comparator<Participant>() {
        @Override
        public int compare(Participant participant, Participant t1) {
            return Integer.compare(participant.newRating, t1.newRating);
        }
    };
    private List<Participant> pans;

    @Contract(pure = true)
    private static double calculateProbability(@NotNull Participant a, @NotNull Participant b) {
        return 1.0 / (1 + pow(10, (a.rank - b.rank) / 400));
    }

    private static double calculateSeed(int idx, int rating, @NotNull List<Participant> pans) {
        Participant exUser = new Participant();
        exUser.oldRating = rating;
        double res = 1.0;
        for (int i = 0; i < pans.size(); ++i) {
            if (i != idx) res += calculateProbability(pans.get(i), exUser);
        }
        return res;
    }

    private static int calculateExceptedRank(int idx, double rank, @NotNull List<Participant> pans) {
        int l = 1;
        int r = MAX_PANS;
        while (r - l > 1) {
            int mid = (l + r) / 2;
            if (calculateSeed(idx, mid, pans) < rank) r = mid;
            else l = mid;
        }
        return l;
    }

    public List<Participant> getRating() {
        for (int i = 0; i < pans.size(); ++i) {
            for (int j = 0; j < pans.size(); ++j) {
                if (i != j) {
                    pans.get(i).setSeed(pans.get(i).getSeed() + calculateProbability(pans.get(i), pans.get(j)));
                }
            }
        }
        int sumDelta = 0;
        int index = 0;
        for (var pan : pans) {
            double geometricAvg = sqrt(pan.rank * pan.seed);
            int R = calculateExceptedRank(index, geometricAvg, pans);
            pan.delta = (R - pan.oldRating) / 2;
            sumDelta += pan.delta;
            index++;
        }
        // restrict the affect step 1
        int inc = -(sumDelta / pans.size()) - 1;
        for (var pan : pans) pan.delta += inc;
        // restrict the affect step 2
        int s = Math.max(pans.size(), 4 * (int) sqrt(pans.size()));
        sort(pans);
        int sumS = 0;
        for (int i = 0; i < s; ++i) sumS += pans.get(i).delta;
        inc = min(max(-(sumS / s), -10), 0);
        for (var pan : pans) pan.delta += inc;
        for (var pan : pans) pan.newRating = pan.delta + pan.oldRating;
        sort(pans, c);
        return pans;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @EqualsAndHashCode
    private static final class Participant implements Comparable {
        double rank;
        String userName;
        int oldRating;
        int newRating = 0;
        double seed = 1.0;
        int delta = 0;
        String validation;

        @Contract(pure = true)
        private Participant() {
            this.newRating = 0;
            this.delta = 0;
        }

        public int compareTo(@NotNull Object o) {
            return -Double.compare(oldRating, ((Participant) o).oldRating);
        }
    }
}
