package proj.chat.domain.dto.message;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    
    private String channelUuid;
    private String memberUuid;
    private String memberName;
    private String message;
    
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdDate;
}

/*

[데이터 전송 형식(JSON)]

{
"memberName": "test1234",
"channelId": "a4c2eb10-0a4d-4880-adb4-24704d0d61f2",
"memberUuid": "a4c2eb10-0a4d-4880-adb4-24704d0d61f2",
"content": "hello",
"createdDate": [2023, 8, 25, 15, 24, 8, 805344500]
}

 */