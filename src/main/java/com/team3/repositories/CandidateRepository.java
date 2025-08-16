package com.team3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.team3.entities.Candidate;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long>, JpaSpecificationExecutor<Candidate> {
    boolean existsByEmail(String email);

    List<Candidate> getCandidateByStatus(String status);

    @Query("SELECT c FROM Candidate c WHERE c.status IN :statuses ORDER BY c.updatedAt DESC")
    List<Candidate> findTopCandidatesByStatusIn(@Param("statuses") List<String> statuses, Pageable pageable);
}
