package com.onlinejudge.predictservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static java.lang.Math.sqrt;

@Getter
@Setter
@AllArgsConstructor
class UserStar {
    protected static final int MAXN = 128;
    private static final double MAX_RATING = 1500;
    private static final double MIN_RATING = 0;
    private String userID;
    private String examID;
    private int userRating;
    private int isInteresting;
    private int isChallenging;
    private boolean[] studiedCources;

    @Contract(pure = true)
    private static Triple<Double, Double, Double> getCourseSimilarity(@NotNull UserStar a, @NotNull UserStar b) {
        // Calculate cosine similarity of studiedCourses
        assert a.studiedCources.length == MAXN;
        assert b.studiedCources.length == MAXN;
        double result = 0.0;
        double acnt = 0;
        double bcnt = 0;
        for (int i = 0; i < MAXN; ++i) {
            if (a.studiedCources[i] == b.studiedCources[i]) {
                result += 1.0;
            }
            if (a.studiedCources[i]) acnt += 1;
            if (b.studiedCources[i]) bcnt += 1;
        }
        return new ImmutableTriple<>(acnt, bcnt, result);
    }

    @Contract(pure = true)
    private static Pair<Double, Double> getNormalizedRating(@NotNull UserStar a, @NotNull UserStar b) {
        // Turn domain from [MIN_RATING, MAX_RATING] to [0,1]
        double retA = (a.userRating - MIN_RATING) / (MAX_RATING - MIN_RATING);
        double retB = (b.userRating - MIN_RATING) / (MAX_RATING - MIN_RATING);
        return new ImmutablePair<>(retA, retB);
    }

    @Contract(pure = true)
    public static double getSimilarity(@NotNull UserStar a, @NotNull UserStar b) {
        var ratingPair = getNormalizedRating(a, b);
        var courseTuple = getCourseSimilarity(a, b);
        double result = ratingPair.getLeft() * ratingPair.getRight() + courseTuple.getRight();
        result = result / sqrt(courseTuple.getLeft() + ratingPair.getLeft())
                / sqrt(courseTuple.getMiddle() + ratingPair.getRight());
        return result;
    }

}
