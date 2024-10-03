package minhyeok.taskschedulingpractice.domain.vote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query("select v from Vote v where v.voteStatus = 'OPENED'")
    List<Vote> findOpenVotes();
}
