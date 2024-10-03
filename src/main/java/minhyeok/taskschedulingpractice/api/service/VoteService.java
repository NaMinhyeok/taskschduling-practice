package minhyeok.taskschedulingpractice.api.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import minhyeok.taskschedulingpractice.domain.vote.RegisteredVoteEvent;
import minhyeok.taskschedulingpractice.domain.vote.Vote;
import minhyeok.taskschedulingpractice.domain.vote.VoteRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final TaskScheduler taskScheduler;
    private final ApplicationEventPublisher eventPublisher;
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    @Transactional
    public void createVote(LocalDateTime endAt) {
        Vote vote = Vote.create(endAt);
        Vote savedVote = voteRepository.save(vote);
        addVoteTaskToTaskSchedule(savedVote);
        log.info("투표가 생성되었습니다. 투표 종료 시간: {}", endAt);
    }

    private void addVoteTaskToTaskSchedule(Vote vote) {
        ScheduledFuture<?> schedule = taskScheduler.schedule(publishClosedVoteTask(vote.getId()), vote.getInstantEndAt());
        scheduledTasks.put(vote.getId(), schedule);
        log.info("투표 종료 이벤트가 등록되었습니다. 투표 ID: {}", vote.getId());
    }

    private Runnable publishClosedVoteTask(Long voteId) {
        return () -> eventPublisher.publishEvent(new RegisteredVoteEvent(voteId));
    }

    @Transactional
    public void endVote(Long id) {
        log.info("투표를 종료합니다. 투표 ID: {}", id);
        Vote vote = voteRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("해당하는 투표가 없습니다.")
        );
        vote.updateVoteStatusToClose();
        log.info("스케줄된 작업을 통해 투표를 종료합니다. 투표 ID: {}", id);
        ScheduledFuture<?> scheduledTask = scheduledTasks.get(id);
        if (scheduledTask == null) {
            log.error("해당하는 투표 ID로 등록된 작업이 없습니다.");
            return;
        }
        scheduledTask.cancel(false);
        scheduledTasks.remove(id);
    }

    @PostConstruct
    public void initializeScheduledTasks() {
        List<Vote> activeVotes = voteRepository.findOpenVotes();
        for (Vote vote : activeVotes) {
            addVoteTaskToTaskSchedule(vote);
        }
    }

}
