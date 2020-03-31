package com.onlinejudge.examservice;

import com.onlinejudge.daemonservice.DaemonServiceRunnable;
import com.onlinejudge.examservice.ExamServiceGetRating.Participant;
import com.onlinejudge.userservice.TimelineItem;
import com.onlinejudge.userservice.UserServiceSetTimeline;
import com.onlinejudge.util.BooleanEvent;
import com.onlinejudge.util.InternalException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.List;

import static com.onlinejudge.examservice.ExamServiceGetRating.getItem;
import static com.onlinejudge.userservice.UserServiceGetExamName.getExamName;

@Log4j2
@Getter
@Setter
public class ExamServiceUpdateRating implements BooleanEvent {
    private final String examID;
    private List<Participant> pans = null;
    private boolean isRatingCalcuated = false;

    public ExamServiceUpdateRating(@NotNull String examID) {
        this.examID = examID;
    }

    @Override
    public boolean go() throws InternalException {
        if (isRatingCalcuated) return false;
        pans = ExamServiceGetParticipants.getItem(examID);
        getItem(pans);
        ExamServiceSetRating.setItem(pans, examID);
        return true;
    }

    @Override
    public void beforeGo() {
        isRatingCalcuated = DaemonServiceRunnable.isRatingCalculated.getOrDefault(examID, false);
    }

    @Override
    public void afterGo() {
        if (pans != null) {
            for (var pan : pans) {
                UserServiceSetTimeline.setItem(
                        new TimelineItem(
                                getExamName(examID), "", 1, pan.userID,
                                new Timestamp(System.currentTimeMillis()
                                )
                        )
                );
            }
            DaemonServiceRunnable.isRatingCalculated.put(examID, true);
        }
    }
}
