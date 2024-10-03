package minhyeok.taskschedulingpractice.domain.vote;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class VoteEventHandler {

    private final VoteRepository voteRepository;

    @Transactional
    @EventListener
    public void onClosedVote(RegisteredVoteEvent event) {
        log.info("투표 종료 이벤트가 발생했습니다. 투표 ID: {}", event.getId());
        Vote vote = voteRepository.findById(event.getId()).orElseThrow(
            () -> new IllegalArgumentException("해당하는 투표가 없습니다.")
        );
        vote.updateVoteStatusToClose();
        log.info("투표가 종료되었습니다. 투표 ID: {}", vote.getId());
        log.info("투표 결과: {}", vote.getVoteStatus());
    }

}
