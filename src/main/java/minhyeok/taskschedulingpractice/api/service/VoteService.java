package minhyeok.taskschedulingpractice.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import minhyeok.taskschedulingpractice.domain.vote.RegisteredVoteEvent;
import minhyeok.taskschedulingpractice.domain.vote.Vote;
import minhyeok.taskschedulingpractice.domain.vote.VoteRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final TaskScheduler taskScheduler;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void createVote(LocalDateTime endAt) {
        Vote vote = Vote.create(endAt);
        Vote savedVote = voteRepository.save(vote);
        addVoteTaskToTaskSchedule(savedVote);
        log.info("투표가 생성되었습니다. 투표 종료 시간: {}", endAt);
    }

    private void addVoteTaskToTaskSchedule(Vote vote) {
        taskScheduler.schedule(publishClosedVoteTask(vote.getId()), vote.getInstantEndAt());
        log.info("투표 종료 이벤트가 등록되었습니다. 투표 ID: {}", vote.getId());
    }

    private Runnable publishClosedVoteTask(Long voteId) {
        return () -> eventPublisher.publishEvent(new RegisteredVoteEvent(voteId));
    }

    @Transactional
    public void endVote(Long id) {
        log.info("투표 종료 이벤트가 발생했습니다.");
        Vote vote = voteRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("해당하는 투표가 없습니다.")
        );
        vote.updateVoteStatusToClose();
    }

    public void checkTaskSchedule() {

    }

}
