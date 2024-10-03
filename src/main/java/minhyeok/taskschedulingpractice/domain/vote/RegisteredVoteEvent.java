package minhyeok.taskschedulingpractice.domain.vote;

import lombok.Getter;

@Getter
public class RegisteredVoteEvent {
    private Long id;

    public RegisteredVoteEvent(Long id) {
        this.id = id;
    }
}
