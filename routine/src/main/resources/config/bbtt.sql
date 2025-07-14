--시퀀스 
CREATE SEQUENCE SEQ_MEETING_NO
START WITH 1
INCREMENT BY 1
NOMAXVALUE
NOCYCLE
NOCACHE;

CREATE SEQUENCE SEQ_MESSAGE_NO
START WITH 1
INCREMENT BY 1
NOMAXVALUE
NOCYCLE
NOCACHE;


--모임 
CREATE TABLE meetings (
    meeting_no NUMBER PRIMARY KEY,                
    user_no NUMBER NOT NULL,                    
    title VARCHAR2(200) NOT NULL,                
    description CLOB,                            
    location VARCHAR2(200),                                               
 	meeting_date DATE DEFAULT SYSDATE,            
    CONSTRAINT fk_meetings_user FOREIGN KEY (user_no) REFERENCES users(user_no) ON DELETE CASCADE
);


COMMENT ON COLUMN meetings.meeting_no IS '모임 고유 번호';
COMMENT ON COLUMN meetings.user_no IS '모임 생성자 사용자 식별번호';
COMMENT ON COLUMN meetings.title IS '모임 제목';
COMMENT ON COLUMN meetings.description IS '모임 상세 설명';
COMMENT ON COLUMN meetings.location IS '모임 장소';
COMMENT ON COLUMN meetings.meeting_date IS '모임 생성일시';

--모임 참가자
CREATE TABLE meeting_part (
    meeting_no NUMBER NOT NULL,                   
    user_no NUMBER NOT NULL,                     
    joined_at DATE DEFAULT SYSDATE,
    last_read_at DATE DEFAULT NULL,            
    PRIMARY KEY (meeting_no, user_no),          
    CONSTRAINT fk_participants_meeting FOREIGN KEY (meeting_no) REFERENCES meetings(meeting_no) ON DELETE CASCADE,
    CONSTRAINT fk_participants_user FOREIGN KEY (user_no) REFERENCES users(user_no) ON DELETE CASCADE
);

COMMENT ON COLUMN meeting_part.meeting_no IS '참가한 모임 번호';
COMMENT ON COLUMN meeting_part.user_no IS '참가자 식별번호';
COMMENT ON COLUMN meeting_part.joined_at IS '모임 참가 일시';
COMMENT ON COLUMN meeting_part.last_read_at IS '마지막 채팅 읽은 시간';

--채팅 메시지
CREATE TABLE chat_messages (
    message_no NUMBER PRIMARY KEY,              
    meeting_no NUMBER NOT NULL,                 
    user_no NUMBER,                           
    message_content CLOB,                       
    message_type VARCHAR2(20) DEFAULT 'text' CHECK (message_type IN ('text', 'image', 'file', 'system')), 
    file_url VARCHAR2(500),                     
    file_name VARCHAR2(200),                   
    sent_at DATE DEFAULT SYSDATE,              
    CONSTRAINT fk_messages_meeting FOREIGN KEY (meeting_no) REFERENCES meetings(meeting_no) ON DELETE CASCADE,
    CONSTRAINT fk_messages_user FOREIGN KEY (user_no) REFERENCES users(user_no) ON DELETE SET NULL
);

COMMENT ON COLUMN chat_messages.message_no IS '메시지 고유 번호';
COMMENT ON COLUMN chat_messages.meeting_no IS '해당 메시지 모임 번호';
COMMENT ON COLUMN chat_messages.user_no IS '메시지 발송자 번호 (users 테이블 참조, NULL이면 시스템 메시지)';
COMMENT ON COLUMN chat_messages.message_content IS '메시지 텍스트 내용';
COMMENT ON COLUMN chat_messages.message_type IS '메시지 타입 (text: 텍스트, image: 이미지, file: 파일, system: 시스템 메시지)';
COMMENT ON COLUMN chat_messages.file_url IS '첨부파일 저장 경로';
COMMENT ON COLUMN chat_messages.file_name IS '첨부파일 원본 파일명';







