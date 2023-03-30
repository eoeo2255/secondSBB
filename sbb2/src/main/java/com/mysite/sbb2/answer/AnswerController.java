package com.mysite.sbb2.answer;

import com.mysite.sbb2.DataNotFoundException;
import com.mysite.sbb2.question.Question;
import com.mysite.sbb2.question.QuestionService;
import com.mysite.sbb2.user.SiteUser;
import com.mysite.sbb2.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/answer")
public class AnswerController {
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createA(Model model, @PathVariable("id") Integer id, @Valid AnswerForm answerForm, BindingResult bindingResult, Principal principal) {
        Question q = this.questionService.getQ(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());

        if(bindingResult.hasErrors()) {
            model.addAttribute("question", q);
            return "question_detail";
        }

        Answer a = this.answerService.create(q, answerForm.getContent(), siteUser);

        return String.format("redirect:/question/detail/%s#answer_%s", a.getQuestion().getId(), a.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modifyA(AnswerForm answerForm, @PathVariable("id") Integer id, Principal principal) {
        Answer a = this.answerService.getAnswer(id);
        if(!a.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"수정권한이 없습니다.");
        }
        answerForm.setContent(a.getContent());
        return "answer_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modifyA(@Valid AnswerForm answerForm, BindingResult bindingResult, @PathVariable("id") Integer id, Principal principal) {
        if(bindingResult.hasErrors()) {
            return "answer_form";
        }
        Answer a = this.answerService.getAnswer(id);
        if(!a.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"수정권한이 없습니다.");
        }
        this.answerService.modify(a,answerForm.getContent());
        return String.format("redirect:/question/detail/%s#answer_%s", a.getQuestion().getId(), a.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String deleteA(Principal principal, @PathVariable("id") Integer id) {
        Answer a = this.answerService.getAnswer(id);
        if (!a.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.answerService.delete(a);
        return String.format("redirect:/question/detail/%s", a.getQuestion().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String voteA(Principal principal, @PathVariable("id") Integer id) {
        Answer a = this.answerService.getAnswer(id);
        SiteUser user = this.userService.getUser(principal.getName());
        this.answerService.vote(a, user);
        return String.format("redirect:/question/detail/%s#answer_%s", a.getQuestion().getId(), a.getId());
    }

}
