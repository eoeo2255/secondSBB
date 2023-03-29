package com.mysite.sbb2.answer;

import com.mysite.sbb2.question.Question;
import com.mysite.sbb2.question.QuestionService;
import com.mysite.sbb2.user.SiteUser;
import com.mysite.sbb2.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

        this.answerService.create(q, answerForm.getContent(), siteUser);
        return String.format("redirect:/question/detail/%s", id);
    }


}
