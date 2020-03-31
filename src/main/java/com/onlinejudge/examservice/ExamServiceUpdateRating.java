package com.onlinejudge.examservice;

import com.onlinejudge.examservice.ExamServiceGetRating.Participant;
import com.onlinejudge.userservice.TimelineItem;
import com.onlinejudge.userservice.UserServiceSetTimeline;
import com.onlinejudge.util.BooleanEvent;
import com.onlinejudge.util.InternalException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.sql.Timestamp;
import java.util.List;

import static com.onlinejudge.examservice.ExamServiceGetRating.getItem;
import static com.onlinejudge.userservice.UserServiceGetExamName.getExamName;

@Log4j2
@Getter
@Setter
public class ExamServiceUpdateRating implements BooleanEvent {
    List<Participant> pans = null;
    private String examID;

    @Override
    public boolean go() throws InternalException {
        pans = ExamServiceGetParticipants.getItem(examID);
        getItem(pans);
        ExamServiceSetRating.setItem(pans, examID);
        return true;
    }

    @Override
    public void beforeGo() {
        // do nothing
    }

    @Override
    public void afterGo() {
        assert pans != null;
        for (var pan : pans) {
            UserServiceSetTimeline.setItem(
                    new TimelineItem(
                            getExamName(examID), "", 1, pan.userID,
                            new Timestamp(System.currentTimeMillis()
                            )
                    )
            );
        }
    }
}
