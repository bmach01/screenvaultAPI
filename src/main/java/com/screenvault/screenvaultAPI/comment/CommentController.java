package com.screenvault.screenvaultAPI.comment;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @GetMapping("/getCommentsUnderPost")
    public ResponseEntity<Page<Comment>> getCommentsUnderPost(
            @RequestBody GetCommentsRequestBody requestBody
    ) {
        return null;
    }

}
