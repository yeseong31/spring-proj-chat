package proj.chat.domain.repository.comment;

import proj.chat.domain.entity.Comment;

public interface CommentRepositoryCustom {
    
    void updateVoters(Comment comment, boolean b);
}
