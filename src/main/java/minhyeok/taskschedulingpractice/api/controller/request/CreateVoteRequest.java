package minhyeok.taskschedulingpractice.api.controller.request;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateVoteRequest {
    private LocalDateTime endAt;
}
