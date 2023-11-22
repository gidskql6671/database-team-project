# 동작 환경 및 실행 방법
- Intellij IDE를 사용하여 개발하였습니다.
- M1 Mac 환경이라 Docker를 사용해 Oracle 컨테이너를 띄워 진행하였습니다.
- 같이 제출된 `additional_ddl.sql`을 실행하여, 시퀀스를 생성
  - 1씩 증가되는 값의 무결성을 지키기 위해 추가했습니다.
- 변수 세팅
  - `/src/main/resources` 폴더에 `secrets.yml` 파일을 추가하고, `secrets.yml.sample` 파일의 내용을 붙여넣음
  - `url` : 데이터베이스 접속 URL
  - `username` : 데이터베이스 접속 유저네임
  - `password` : 데이터베이스 접속 패스워드

# 기능 설명
- 간단한 LMS 기능을 구현하였습니다.
- 웹 페이지 환경을 통해 회원 기능, 수강 신청, 수업, 강의실 등의 기능을 이용할 수 있습니다.

# 데모 링크
추후 추가
