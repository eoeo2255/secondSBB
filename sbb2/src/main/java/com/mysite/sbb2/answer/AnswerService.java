package com.mysite.sbb2.answer;

import com.mysite.sbb2.DataNotFoundException;
import com.mysite.sbb2.question.Question;
import com.mysite.sbb2.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;

    public Answer create(Question question, String content, SiteUser author) {
        Answer a = new Answer();
        a.setContent(content);
        a.setCreateDate(LocalDateTime.now());
        a.setQuestion(question);
        a.setAuthor(author);
        this.answerRepository.save(a);
        return a;
    }

    public Answer getAnswer(Integer id) {
        Optional<Answer> oa = this.answerRepository.findById(id);
        if (oa.isPresent()) {
            return oa.get();
        } else {
            throw new DataNotFoundException("answer not found");
        }
    }

    public void modify(Answer answer, String content) {
        answer.setContent(content);
        answer.setModifyDate(LocalDateTime.now());
        this.answerRepository.save(answer);
    }

    public void delete(Answer answer) {
        this.answerRepository.delete(answer);
    }

    public void vote(Answer answer, SiteUser siteUser) {
        answer.getVoter().add(siteUser);
        this.answerRepository.save(answer);
    }

}
