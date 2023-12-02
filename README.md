# 개발 환경
- Intellij IDE를 사용하여 개발하였습니다.
- M1 Mac 환경이라 Docker를 사용해 Oracle 컨테이너를 띄워 진행하였습니다.
- Java 17 (Jdk 17)
- OJDBC 11 
  - `build.gradle`에 명시되어 있어 별도로 추가할 필요는 없습니다.
  - 참고: `implementation group: 'com.oracle.database.jdbc', name: 'ojdbc6', version: '11.2.0.4'`

# 실행 방법
1. 같이 제출된 `Team1-Phase4-1.sql` 실행 - DDL
2. 같이 제출된 `Team1-Phase4-2.sql` 실행 - Insert 구문
3. 환경 변수 세팅
   - `/src/main/resources` 폴더에 `secrets.yml` 파일을 추가하고, `secrets.yml.sample` 파일의 내용을 붙여넣음
   - `url` : 데이터베이스 접속 URL
   - `username` : 데이터베이스 접속 유저네임
   - `password` : 데이터베이스 접속 패스워드
4. Gradle 빌드 후 실행 

# 기능 설명
- 간단한 LMS 기능을 구현하였습니다.
- 웹 페이지 환경을 통해 회원 기능, 수강 신청, 수업, 강의실 등의 기능을 이용할 수 있습니다.

# 데모 링크
추후 추가

# DDL 및 Insert 변경점.
`Team1-Additional_task1.txt`에 더 자세한 설명이 있습니다.

- 1씩 증가되는 값의 무결성을 지키기 위해 `post_seq`, `post_comment_seq`, `reserved_classroom_seq` 시퀀스를 추가했습니다.
- 수강 정원을 넘기지 않기 위해 속성 및 제약 조건, 트리거 추가.
  - Class 테이블에 `Cur_student_number` 속성을 추가
    - 그에 따른 Insert 구문 수정
  - `TRI_TAKE_CLASS_INSERT`, `TRI_TAKE_CLASS_DELETE` 트리거 추가
  - Class 테이블에 `CHECK (Cur_student_number <= Max_student_number)` 제약조건 추가
