package com.sparta.springboard.service;

import com.sparta.springboard.dto.BoardRequestDto;
import com.sparta.springboard.dto.BoardResponseDto;
import com.sparta.springboard.dto.MsgResponseDto;
import com.sparta.springboard.entity.Board;
import com.sparta.springboard.entity.User;
import com.sparta.springboard.jwt.JwtUtil;
import com.sparta.springboard.repository.BoardRepository;
import com.sparta.springboard.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor

public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    //게시글 작성
    @Transactional
    public BoardResponseDto createBoard(BoardRequestDto requestDto, HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("Token Error");
            }

            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );

            Board board = boardRepository.saveAndFlush(new Board(requestDto, user.getId()));

            return new BoardResponseDto(board);

        } else {
            return null;
        }
    }


    //전체 게시글 목록 조회
    @Transactional(readOnly = true)
    public List<BoardResponseDto> getListBoards() {
        List<Board> boardList =  boardRepository.findAllByOrderByModifiedAtDesc();
        List<BoardResponseDto> boardResponseDto = new ArrayList<>();

        for (Board board : boardList) {
            boardResponseDto.add(new BoardResponseDto(board));
        }

        return boardResponseDto;
    }


    @Transactional

    public BoardResponseDto getBoard(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("아이디가 존재하지 않습니다.")
        );

        return new BoardResponseDto(board);
    }


    @Transactional
    public BoardResponseDto update(Long id, BoardRequestDto requestDto, HttpServletRequest request) {

        String token = jwtUtil.resolveToken(request);
        Claims claims;

        if (token != null) {

            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("Token Error");
            }


            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );

            Board board = boardRepository.findByIdAndUserId(id, user.getId()).orElseThrow(
                    () -> new NullPointerException("게시글이 존재하지 않습니다.")
            );

            board.update(requestDto);


            return new BoardResponseDto(board);

        } else {
            return null;
        }
    }


    @Transactional
    public MsgResponseDto deleteBoard (Long id, HttpServletRequest request) {

        String token = jwtUtil.resolveToken(request);
        Claims claims;

        if (token != null) {

            if (jwtUtil.validateToken(token)) {

                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("Token Error");
            }


            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );

            Board board = boardRepository.findByIdAndUserId(id, user.getId()).orElseThrow(
                    () -> new NullPointerException("게시글이 존재하지 않습니다.")
            );

            boardRepository.delete(board);

            return new MsgResponseDto("게시글 삭제 성공", HttpStatus.OK.value());

        } else {
            return new MsgResponseDto("게시글 작성자만 삭제 가능", HttpStatus.OK.value());

        }
    }
}