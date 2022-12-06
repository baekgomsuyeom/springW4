package com.sparta.springboard.controller;

import com.sparta.springboard.dto.BoardRequestDto;
import com.sparta.springboard.dto.BoardResponseDto;
import com.sparta.springboard.dto.MsgResponseDto;
import com.sparta.springboard.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")

public class BoardController {

    private final BoardService boardService;

    @PostMapping("/post")
    public BoardResponseDto createBoard(@RequestBody BoardRequestDto requestDto, HttpServletRequest request) {
        return boardService.createBoard(requestDto, request);
    }

    @GetMapping("/posts")
    public List<BoardResponseDto> getListBoards() {
        return boardService.getListBoards();
    }


    @GetMapping("/post/{id}")
    public BoardResponseDto getBoards(@PathVariable Long id) {
        return boardService.getBoard(id);
    }


    @PutMapping("/post/{id}")
    public BoardResponseDto updateBoard(@PathVariable Long id, @RequestBody BoardRequestDto requestDto, HttpServletRequest request) {
        return boardService.update(id, requestDto, request);
    }


    @DeleteMapping("/post/{id}")
    public MsgResponseDto deleteBoard(@PathVariable Long id, HttpServletRequest request) {
        return boardService.deleteBoard(id, request);
    }
}
