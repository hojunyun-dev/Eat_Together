# 1단계: 빌더 (빌드 환경)
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /app

# Gradle Wrapper 파일 복사
COPY gradlew .
COPY gradle gradle

# Gradle 빌드 파일만 먼저 복사하여 종속성 다운로드 캐싱
COPY build.gradle settings.gradle ./

# 종속성 다운로드 (소스 코드 변경과 관계없이 캐싱)
RUN ./gradlew dependencies

# 소스 코드 복사
COPY src src

# 애플리케이션 빌드
RUN ./gradlew bootJar --no-daemon

# 2단계: 실행 환경 (최종 이미지)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# 빌드 단계에서 생성된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java","-jar","/app/app.jar"]