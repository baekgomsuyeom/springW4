package com.sparta.springboard.repository;

import com.sparta.springboard.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

//게시글 작성
public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findAllByOrderByModifiedAtDesc();
    Optional<Board> findByIdAndUserId(Long id, Long userId);
}

