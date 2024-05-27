package com.se.demo.controller;

import com.se.demo.dto.AddCommentRequest;
import com.se.demo.dto.CommentResponse;
import com.se.demo.entity.Issue;
import com.se.demo.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api")
@Controller
public class CommentController {

    private final CommentService commentService;



    // 특정 이슈의 댓글 목록 및 댓글 생성 페이지
    @GetMapping("/issue/{id}/comments")
    public String showCommentsForIssue(@PathVariable Long id, Model model) {
        try {
            List<CommentResponse> comments = commentService.findAllByIssueId(id);
            model.addAttribute("comments", comments);
            model.addAttribute("issueId", id);
            model.addAttribute("addCommentRequest", new AddCommentRequest());
            return "issue_comments";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }
    // 댓글 생성
    @PostMapping("/comments/create")
    public String saveComment(@ModelAttribute AddCommentRequest request, Principal principal, Model model) {
        if (principal == null) {
            model.addAttribute("errorMessage", "사용자가 인증되지 않았습니다.");
            return "error";
        }

        try {
            Long issueId = Long.valueOf(request.getIssueId());
            if (issueId == null) {
                throw new IllegalArgumentException("Issue ID is null");
            }
            String nickName = principal.getName();
            CommentResponse savedComment = commentService.save(request, nickName, issueId);
            return "redirect:/api/issue/" + issueId + "/comments";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }


    // 특정 이슈의 댓글 목록 페이지
    /*@GetMapping("/issue/{id}/comments")
    public String getCommentsByIssueId(@PathVariable Long id, Model model) {
        try {
            List<CommentResponse> comments = commentService.findAllByIssueId(id);
            model.addAttribute("comments", comments);
            model.addAttribute("issueId", id);
            return "issue_comments";
        } catch (Exception e) {
            // 로그 출력
            e.printStackTrace();
            // 오류 메시지를 모델에 추가하여 오류 페이지에 전달
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }*/
// 이슈 검색 페이지 , state로 검색
    @GetMapping("/comments/search")
    public String showSearchPage() {
        return "issue_search";
    }


    // 이슈 검색 결과
    @GetMapping("/comments/search/results")
    public String searchIssues(@RequestParam String Istate, Model model) {
        try {
            List<Issue> searchList = commentService.search(Istate);
            model.addAttribute("searchList", searchList);
            return "issue_search";
        } catch (Exception e) {
            // 로그 출력
            e.printStackTrace();
            // 오류 메시지를 모델에 추가하여 오류 페이지에 전달
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }
}