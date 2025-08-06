# Java 17 기반 이미지 사용
FROM eclipse-temurin:17-jdk-jammy

# 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper 파일 복사 (필요한 경우)
COPY gradlew .
COPY gradle gradle

# Gradle 빌드 파일 복사
COPY build.gradle settings.gradle ./

# 소스 코드 복사 (빌드 시점에 필요한 모든 파일)
COPY src src

# 애플리케이션 빌드
RUN ./gradlew bootJar

# 실행 가능한 JAR 파일 복사
COPY build/libs/*.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java","-jar","/app/app.jar"]