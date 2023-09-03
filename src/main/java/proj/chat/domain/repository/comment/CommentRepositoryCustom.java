package proj.chat.domain.repository.comment;

import proj.chat.domain.entity.Comment;

public interface CommentRepositoryCustom {
    
    void addVoter(Comment comment);
    
    void subtractVoter(Comment comment);
}
