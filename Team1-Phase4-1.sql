CREATE TABLE DEPARTMENT(
    Department_code         VARCHAR(10) NOT NULL ,
    Total_credits_required  NUMBER(3)   NOT NULL ,
    Name                    VARCHAR(100) NOT NULL ,
    PRIMARY KEY (Department_code)
);

CREATE TABLE STUDENT(
    Student_id              CHAR(10)        NOT NULL ,
    Name                    VARCHAR(100)     NOT NULL ,
    Login_id                VARCHAR(100)     NOT NULL ,
    Login_password          VARCHAR(256)    NOT NULL ,
    Grade                   NUMBER(1)       NOT NULL ,
    Email                   VARCHAR(100),
    Phone_number            VARCHAR(15),
    Enrollment              VARCHAR(30)     NOT NULL ,
    Total_credits_required  NUMBER(3)       NOT NULL ,
    Department_code         VARCHAR(10)     NOT NULL ,
    PRIMARY KEY (Student_id),
    UNIQUE (Login_id),
    FOREIGN KEY (Department_code) REFERENCES DEPARTMENT(Department_code) ON DELETE SET NULL
);

CREATE TABLE PROFESSOR(
    Professor_id            CHAR(10)        NOT NULL ,
    Name                    VARCHAR(100)    NOT NULL ,
    Login_id                VARCHAR(100)     NOT NULL ,
    Login_password          VARCHAR(256)    NOT NULL ,
    Email                   VARCHAR(100),
    Phone_number            VARCHAR(15),
    Lab_location            VARCHAR(30),
    Department_code         VARCHAR(10)     NOT NULL ,
    PRIMARY KEY (Professor_id),
    UNIQUE (Login_id),
    FOREIGN KEY (Department_code) REFERENCES DEPARTMENT(Department_code) ON DELETE SET NULL
);

CREATE TABLE LECTURE(
    Lecture_code        VARCHAR(10)     NOT NULL ,
    Name                VARCHAR(200)     NOT NULL ,
    Description         VARCHAR(1000),
    Credits             NUMBER(2)       NOT NULL ,
    PRIMARY KEY (Lecture_code)
);

CREATE TABLE CLASSROOM(
    Building_number NUMBER(3)       NOT NULL ,
    Room_code       VARCHAR(10)     NOT NULL ,
    Name            VARCHAR(100)     NOT NULL ,
    PRIMARY KEY (Building_number, Room_code)
);

CREATE TABLE CLASS(
    Lecture_code        VARCHAR(10)     NOT NULL ,
    Section_code        VARCHAR(10)     NOT NULL ,
    Semester            VARCHAR(10)     NOT NULL ,
    Year                NUMBER(4)       NOT NULL ,
    Professor_id        CHAR(10)        NOT NULL ,
    Department_code     VARCHAR(10)     NOT NULL ,
    Max_student_number  NUMBER(3)       NOT NULL ,
    Cur_student_number  NUMBER(3)       DEFAULT 0 NOT NULL ,
    Building_number     NUMBER(3),
    Room_code           VARCHAR(10),
    PRIMARY KEY (Lecture_code, Section_code),
    CHECK       (Cur_student_number <= Max_student_number),
    FOREIGN KEY (Lecture_code) REFERENCES LECTURE(Lecture_code),
    FOREIGN KEY (Professor_id) REFERENCES PROFESSOR(Professor_id) ON DELETE SET NULL ,
    FOREIGN KEY (Department_code) REFERENCES DEPARTMENT(Department_code) ON DELETE SET NULL,
    FOREIGN KEY (Building_number, Room_code) REFERENCES CLASSROOM(Building_number, Room_code) ON DELETE SET NULL
);

CREATE TABLE CLASS_TIME(
    Lecture_code        VARCHAR(10)     NOT NULL ,
    Section_code        VARCHAR(10)     NOT NULL ,
    DayOfWeek           NUMBER(1)       NOT NULL ,
    Hour                NUMBER(3, 1)    NOT NULL ,
    PRIMARY KEY (Lecture_code, Section_code, DayOfWeek, Hour),
    FOREIGN KEY (Lecture_code, Section_code) REFERENCES CLASS(Lecture_code, Section_code) ON DELETE CASCADE
);

CREATE TABLE POST(
    Post_id         NUMBER(9)       NOT NULL ,
    Type            VARCHAR(20)     NOT NULL ,
    Created_at      Timestamp       NOT NULL ,
    Title           VARCHAR(200)    NOT NULL ,
    Content         VARCHAR(1000)   NOT NULL ,
    Lecture_code    VARCHAR(10)     NOT NULL ,
    Section_code    VARCHAR(10)     NOT NULL ,
    Publisher_id    CHAR(10),
    PRIMARY KEY (Post_id),
    FOREIGN KEY (Lecture_code, Section_code) REFERENCES CLASS(Lecture_code, Section_code) ON DELETE CASCADE,
    FOREIGN KEY (Publisher_id) REFERENCES PROFESSOR(Professor_id) ON DELETE SET NULL
);

CREATE TABLE POST_COMMENT(
    Comment_id              NUMBER(9)       NOT NULL ,
    Created_at              Timestamp       NOT NULL ,
    Content                 VARCHAR(1000)   NOT NULL ,
    Post_id                 NUMBER(9)       NOT NULL ,
    Publisher_student_id    CHAR(10),
    Publisher_Professor_id  CHAR(10),
    PRIMARY KEY (Comment_id),
    FOREIGN KEY (Post_id) REFERENCES POST(Post_id) ON DELETE CASCADE,
    FOREIGN KEY (Publisher_student_id) REFERENCES STUDENT(Student_id) ON DELETE SET NULL,
    FOREIGN KEY (Publisher_Professor_id) REFERENCES PROFESSOR(Professor_id) ON DELETE SET NULL
);

CREATE TABLE TAKE_CLASS(
    Student_id          CHAR(10)        NOT NULL ,
    Lecture_code        VARCHAR(10)     NOT NULL ,
    Section_code        VARCHAR(10)     NOT NULL ,
    PRIMARY KEY (Student_id, Lecture_code, Section_code),
    FOREIGN KEY (Lecture_code, Section_code) REFERENCES CLASS(Lecture_code, Section_code) ON DELETE CASCADE,
    FOREIGN KEY (Student_id) REFERENCES STUDENT(Student_id) ON DELETE CASCADE
);

CREATE TABLE GRADE_POINT(
    Student_id          CHAR(10)        NOT NULL ,
    Lecture_code        VARCHAR(10)     NOT NULL ,
    Grade_point         NUMBER(2, 1)    NOT NULL ,
    PRIMARY KEY (Student_id, Lecture_code),
    FOREIGN KEY (Lecture_code) REFERENCES LECTURE(Lecture_code),
    FOREIGN KEY (Student_id) REFERENCES STUDENT(Student_id) ON DELETE CASCADE
);

CREATE TABLE RESERVED_CLASSROOM(
    Reserved_id     NUMBER(10)      NOT NULL ,
    Student_id      CHAR(10)        NOT NULL ,
    Building_number NUMBER(3)       NOT NULL ,
    Room_code       VARCHAR(10)     NOT NULL ,
    Start_timestamp Timestamp       NOT NULL ,
    End_timestamp   Timestamp       NOT NULL ,
    PRIMARY KEY (Reserved_id),
    FOREIGN KEY (Student_id) REFERENCES STUDENT(Student_id) ON DELETE CASCADE,
    FOREIGN KEY (Building_number, Room_code) REFERENCES CLASSROOM(Building_number, Room_code) ON DELETE SET NULL
);

CREATE OR REPLACE TRIGGER TRI_TAKE_CLASS_INSERT
              AFTER INSERT ON TAKE_CLASS
                        FOR EACH ROW
BEGIN
UPDATE CLASS
SET CUR_STUDENT_NUMBER = CLASS.CUR_STUDENT_NUMBER + 1
WHERE LECTURE_CODE = :NEW.LECTURE_CODE AND SECTION_CODE = :NEW.SECTION_CODE;
END;
/

CREATE OR REPLACE TRIGGER TRI_TAKE_CLASS_DELETE
              AFTER DELETE ON TAKE_CLASS
                        FOR EACH ROW
BEGIN
UPDATE CLASS
SET CUR_STUDENT_NUMBER = CLASS.CUR_STUDENT_NUMBER - 1
WHERE LECTURE_CODE = :OLD.LECTURE_CODE AND SECTION_CODE = :OLD.SECTION_CODE;
END;
/


CREATE SEQUENCE post_seq
       INCREMENT BY 1
       START WITH 50
       MINVALUE 1
       MAXVALUE 999999999
       NOCYCLE
       NOCACHE
       NOORDER;

CREATE SEQUENCE post_comment_seq
       INCREMENT BY 1
       START WITH 53
       MINVALUE 1
       MAXVALUE 999999999
       NOCYCLE
       NOCACHE
       NOORDER;

CREATE SEQUENCE reserved_classroom_seq
       INCREMENT BY 1
       START WITH 104
       MINVALUE 1
       MAXVALUE 999999999
       NOCYCLE
       NOCACHE
       NOORDER;


commit;
