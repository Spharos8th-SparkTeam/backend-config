# backend-config

ReSpark의 설정 서버와 공통 Java 라이브러리를 둔다. Config Server는 Git에 있는 중앙 설정을 제공하고, 공통 코드는 실행 애플리케이션과 분리한다.

## 주요 기능

- Spring Cloud Config Server
- Git 저장소 `config-repo` 경로의 중앙 설정
- `local`, `dev`, `prod` 환경 분리
- 공통 설정과 서비스별 설정 분리
- Actuator `health`, `info`

Spring Cloud Bus와 동적 설정 갱신은 포함하지 않는다. 결제 설정에는 Kafka를 넣지 않는다.

## 기술 스택

- Java 17
- Spring Boot 4.0.10
- Spring Cloud 2025.1.5
- Gradle 9.9.2

## 디렉터리 구조

```
backend-config/
├── config-server/     # Config Server 실행 모듈
├── common/            # 기존 공통 Java 코드. 실행 모듈이 아님
└── config-repo/       # Git backend가 제공하는 설정
```

`config-server`는 `common`에 의존하지 않는다. 이전 프로젝트의 Security, JWT, 예외 처리 코드는 `common`에 그대로 있다.

## 환경변수

실제 값은 `.env`에 두고 Git에는 올리지 않는다. 예시는 `config-server/.env.example`에 있다.

| 이름 | 용도 |
|---|---|
| `SERVER_PORT` | Config Server 포트. 없으면 8888 |
| `CONFIG_GIT_URI` | 설정이 있는 Git 저장소 주소. 필수 |
| `CONFIG_GIT_LABEL` | 읽을 브랜치. 없으면 `develop` |
| `SPRING_PROFILES_ACTIVE` | 서버 프로세스 프로필 예시 값 |

비공개 저장소를 클론할 때만 프로세스 환경변수로 Git 자격 증명을 준다. 값 자체는 저장소에 적지 않는다.

- `SPRING_CLOUD_CONFIG_SERVER_GIT_USERNAME`
- `SPRING_CLOUD_CONFIG_SERVER_GIT_PASSWORD`

이 저장소는 공개 저장소이므로 기본 예시 URI에는 자격 증명이 필요 없다.

## 실행 방법

1. `config-server/.env.example`을 `config-server/.env`로 복사하고 `CONFIG_GIT_URI`를 확인한다.
2. `config-repo`가 들어 있는 커밋이 `CONFIG_GIT_LABEL` 브랜치에 있어야 한다. 아직 원격 브랜치에 없으면 서버가 해당 경로를 읽지 못한다.
3. 저장소 루트에서 실행한다.

```
gradlew.bat :config-server:bootRun
```

상태 확인:

```
curl http://localhost:8888/actuator/health
```

설정 조회 예:

```
curl http://localhost:8888/backend-payment/local
curl http://localhost:8888/eureka-server/dev
curl http://localhost:8888/Gateway/prod
```

클라이언트 연동은 각 서비스 저장소의 후속 작업이다. 이 저장소에서는 다른 마이크로서비스를 수정하지 않는다.

## 서비스 간 통신

Config Server는 설정을 HTTP로 제공한다. 서비스 간 업무 API 호출은 하지 않는다.

## 협업

브랜치는 `main`, `develop`, `feature/{이슈번호}-작업내용`을 사용한다.
