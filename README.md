# 같이먹자 프로젝트 
- 같이먹자 프로젝트는 같이먹자의 핵심 기능인 음식 주문 및 배달 시스템에 실시간 채팅 기능을 통합하여 사용자 경험을 혁신하는 웹 애플리케이션입니다. 
- 고객끼리의 실시간 채팅으로 같은 동네 사람끼리 배달팁을 덜거나, 양이 많을 경우 함께 먹을 수 있게 서비스를 제공합니다.
![Banner.png](img%2FBanner.png)  
![Logo.png](img%2FLogo.png)
 
## 📌 주요 기능
- 배달팁이 부담스럽거나, 혼자 밥을 먹기엔 양이 많을 때 실시간 채팅으로 주변 사람들과 같이 시켜먹으면 부담을 줄일 수 있는 서비스를 제공합니다.

## 💡 핵심 가치 및 차별점
- 실시간 그룹 채팅 기능을 제공합니다.
- 함께 주문 할 배달파티 모집 기능을 제공합니다.

## 🏗️ 프로젝트 구조
<details>
<summary>클릭하여 프로젝트 파일 구조를 확인하세요.</summary>

📦 project-root/   
┣ 📂 data/   
┣ 📂 gradle/  
┣ 📂 grafana/  
┣ 📂 grafana_data/  
┣ 📂 logs/  
┣ 📂 mysql_exporter/  
┣ 📂 prometheus/  
┣ 📂 src/  
┃ ┣ 📂 main/  
┃ ┃ ┣ 📂 java/  
┃ ┃ ┃ ┗ 📂 com/  
┃ ┃ ┃ ┃ ┗ 📂 example/  
┃ ┃ ┃ ┃ ┃ ┗ 📂 eat_together/  
┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂 domain/  
┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂 cart/  
┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂 controller/  
┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂 dto/  
┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂 entity/  
┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂 repository/  
┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂 service/  
┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂 chat/  
┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂 menu/  
┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂 notification/  
┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂 order/  
┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂 payment/  
┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂 rider/  
┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂 social/  
┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂 store/  
┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂 users/  
┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂 global/  
┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂 common/  
┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂 config/  
┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂 dto/  
┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂 entity/  
┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂 exception/  
┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂 redis/  
┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂 util/  
┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂 websocket/  
┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜 EatTogetherApplication.java  
┃ ┃ ┗ 📂 resources/  
┃ ┃ ┃ ┣ 📂 static/  
┃ ┃ ┃ ┗ 📜 application.yml  
┃ ┣ 📂 test/  
┃ ┗ 📜 .env  
┣ 📜 .gitattributes  
┣ 📜 .gitignore  
┣ 📜 build.gradle  
┣ 📜 docker-compose.yml  
┣ 📜 Dockerfile  
┣ 📜 Dockerfile.elasticsearch  
┣ 📜 gradlew  
┣ 📜 gradlew.bat  
┣ 📜 README.md  
┣ 📜 settings.gradle  
┗ 📂 외부 라이브러리  

</details> 

## 💻 기술 스택
- Backend: Java, Spring Boot, Spring Security, JWT, Query DSL, WebSocket, Kafka
- Database: Mysql, Redis
- Monitoring & Logging : ELK stack, Prometheus, Grafana
- Deployment: Docker, AWS EC2

## 🔧 설치 및 실행 방법
1. 위의 프로젝트를 clone 받아주세요
2. clone 받은 파일로 가서 `doker-compose up -d --build`를 입력해주세요
3. 만약 오류가 발생한다면 `docker-compose donw -v`을 입력해 서비스에 연결된 volume을 삭제해주세요
4. Docker에 해당 프로젝트가 올라와 실행이 되고 있는지 잘 확인해주세요

## ⚡ 사전 준비
- JDK 17 버전을 준비해주세요.
- Docker를 설치해주세요 ( 최신버전으로 준비해주세요 )

### 1. 레포지토리 클론
   터미널 또는 명령 프롬프트에서 다음 명령어를 실행하여 프로젝트 레포지토리를 클론합니다.

### 2. 환경 변수 설정
- backend 디렉토리로 이동하여 .env.example 파일을 참고해 .env 파일을 생성하고 필요한 환경 변수들을 설정합니다.
- cd backend

```
.env` 파일 내용 예시:
# Google
GOOGLE_OAUTH_CLIENT_ID = { Google_CLIENT_ID }
GOOGLE_OAUTH_CLIENT_SECRET = { Google_SECRET_KEY } 

# Kakao
KAKAO_OAUTH_CLIENT_ID = { Kakao_CLIENT_ID }
KAKAO_OAUTH_CLIENT_SECRET = { Kakao_SECRET_KEY } 

# naver
NAVER_OAUTH_CLIENT_ID = { Naver_CLIENT_ID }
NAVER_OAUTH_CLIENT_SECRET = { Naver_SECRET_KEY } 

# Mysql
MYSQL_PORT = { MYSQL_PORT }
MYSQL_USERNAME = { DB_USERNAME }
MYSQL_PASSWORD = { DB_PASSWORD }
MYSQL_DBNAME = { DBNAME }

# Redis
REDIS_PORT = { REDIS_PORT }

{ 이 부분을 각자 설정에 맞게 설정해주세요 }
```

### 🤝 기여 방법
- 이 프로젝트에 대한 여러분의 기여를 환영합니다! 프로젝트를 함께 발전시키기 위한 가이드라인을 따릅니다.

### 🌳 기여 절차
- 이슈 생성: 새로운 기능 제안, 버그 리포트, 개선 사항 등에 대해 먼저 이슈를 생성하여 논의합니다.
- 포크 (Fork): 본 레포지토리를 개인 GitHub 계정으로 포크합니다.
- 클론 (Clone): 포크한 레포지토리를 로컬 환경으로 클론합니다.   
`git clone https://github.com/hojunyun-dev/Eat_Together.git`
---
```
브랜치 생성: 작업할 내용에 맞는 새 브랜치를 생성합니다.

새로운 기능: git checkout -b feature/OOO-기능명

버그 수정: git checkout -b fix/OOO-버그명

개선 사항: git checkout -b refactor/OOO-개선내용

코드 작성 및 테스트: 해당 브랜치에서 코드를 작성하고 충분한 테스트를 진행합니다.

커밋 (Commit): 커밋 컨벤션에 따라 명확한 커밋 메시지를 작성합니다.

git commit -m "feat: OOO 기능 추가"

푸시 (Push): 변경사항을 본인의 원격 저장소에 푸시합니다.

git push origin feature/OOO-기능명

풀 리퀘스트 (Pull Request) 생성: 본 레포지토리의 main 브랜치로 풀 리퀘스트를 생성합니다. 변경 사항에 대한 상세한 설명을 포함해주세요.

```
---
## 🎨 코드 스타일 및 컨벤션
- 커밋 메시지: Conventional Commits 규격을 따릅니다. (예: feat: add user authentication, fix: resolve login bug)
- 브랜치 전략: main 브랜치는 항상 안정적인 버전을 유지하며, 개발은 피처 브랜치에서 진행됩니다.

## ✉️ 문의하기
프로젝트에 대한 질문, 피드백, 또는 협업 제안이 있으시면 언제든지 다음 채널로 연락 주세요!

이메일: OOO@email.com

GitHub Issues: OOO/issues
