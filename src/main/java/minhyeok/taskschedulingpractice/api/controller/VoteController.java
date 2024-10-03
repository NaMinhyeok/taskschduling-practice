package minhyeok.taskschedulingpractice.api.controller;

import lombok.RequiredArgsConstructor;
import minhyeok.taskschedulingpractice.api.controller.request.CreateVoteRequest;
import minhyeok.taskschedulingpractice.api.service.VoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/vote")
    public ResponseEntity<Void> createVote(@RequestBody CreateVoteRequest request) {
        voteService.createVote(request.getEndAt());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/vote/{id}")
    public ResponseEntity<Void> getVote(@PathVariable Long id) {
        voteService.endVote(id);
        return ResponseEntity.ok().build();
    }
}
