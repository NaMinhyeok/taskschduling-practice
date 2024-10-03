package minhyeok.taskschedulingpractice.domain.vote;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    private VoteStatus voteStatus;

    @Builder
    private Vote(LocalDateTime endAt) {
        this.endAt = endAt;
        this.voteStatus = VoteStatus.OPENED;
    }

    public static Vote create(LocalDateTime endAt) {
        return Vote.builder()
            .endAt(endAt)
            .build();
    }

    public Instant getInstantEndAt() {
        return endAt.atZone(ZoneId.systemDefault()).toInstant();
    }

    public void updateVoteStatusToClose() {
        this.voteStatus = VoteStatus.CLOSED;
    }
}
