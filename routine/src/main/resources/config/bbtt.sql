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

CREATE SEQUENCE SEQ_LOCATION START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE SEQ_WEATHER START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE SEQ_RECOMMEND START WITH 1 INCREMENT BY 1;



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


-- 위치
CREATE TABLE LOCATION (
    LOCATION_NO NUMBER PRIMARY KEY,
    LOCATION_NAME VARCHAR2(100) NOT NULL,
    ADDRESS VARCHAR2(200),
    LATITUDE NUMBER,
    LONGITUDE NUMBER,
    NX NUMBER,
    NY NUMBER
);	

COMMENT ON COLUMN LOCATION.LOCATION_NO IS '위치 번호';
COMMENT ON COLUMN LOCATION.LOCATION_NAME IS '사용자 지역명';
COMMENT ON COLUMN LOCATION.ADDRESS IS '카카오 API 상세주소';
COMMENT ON COLUMN LOCATION.LATITUDE IS '위도';
COMMENT ON COLUMN LOCATION.LONGITUDE IS '경도';
COMMENT ON COLUMN LOCATION.NX IS '기상청 격자 X좌표';
COMMENT ON COLUMN LOCATION.NY IS '기상청 격자 Y좌표';


-- 날씨
CREATE TABLE WEATHER (
    WEATHER_NO NUMBER PRIMARY KEY,
    LOCATION_NO NUMBER NOT NULL,
    TEMPERATURE NUMBER,
    HUMIDITY NUMBER,
    RAIN_PROBABILITY NUMBER,
    WEATHER_CONDITION VARCHAR2(50),
    PM10 NUMBER,
    AIR_GRADE VARCHAR2(20),
    CONSTRAINT FK_WEATHER_LOCATION FOREIGN KEY (LOCATION_NO) REFERENCES LOCATION(LOCATION_NO)
);

COMMENT ON COLUMN WEATHER.WEATHER_NO IS '날씨 번호';
COMMENT ON COLUMN WEATHER.LOCATION_NO IS '위치 번호';
COMMENT ON COLUMN WEATHER.TEMPERATURE IS '온도 (°C)';
COMMENT ON COLUMN WEATHER.HUMIDITY IS '습도 (%)';
COMMENT ON COLUMN WEATHER.RAIN_PROBABILITY IS '강수확률 (%)';
COMMENT ON COLUMN WEATHER.WEATHER_CONDITION IS '날씨 상태';
COMMENT ON COLUMN WEATHER.PM10 IS '미세먼지 농도 (㎍/㎥)';
COMMENT ON COLUMN WEATHER.AIR_GRADE IS '대기질 등급 (좋음, 보통, 나쁨, 매우나쁨)';





-- 추천 항목
CREATE TABLE RECOMMEND (
    RECOMMEND_NO NUMBER PRIMARY KEY,
    WEATHER_NO NUMBER NOT NULL,
    EXERCISE_TYPE VARCHAR2(50),
    LOCATION_TYPE VARCHAR2(20),
    CONSTRAINT FK_RECOMMEND_WEATHER FOREIGN KEY (WEATHER_NO) REFERENCES WEATHER(WEATHER_NO)
);

COMMENT ON COLUMN RECOMMEND.RECOMMEND_NO IS '추천 번호';
COMMENT ON COLUMN RECOMMEND.WEATHER_NO IS '날씨 번호';
COMMENT ON COLUMN RECOMMEND.EXERCISE_TYPE IS '운동 종류';
COMMENT ON COLUMN RECOMMEND.LOCATION_TYPE IS '장소 타입 (INDOOR, OUTDOOR)';














