# UniVoice_Server
> 학생회와 일반 학생들의 목소리를 들으며 함께 만들어가는 대학 생활 필수 앱.
![image](https://github.com/Team-UniVoice/UniVoice_Server/assets/137388764/7b8e3956-5059-4f6b-8db3-442341558f02)



## 🏫 유니보이스 팀원
|                             최영철                         |                                                                현예진                                                                 |
| :----------------------------------------------------------: |:----------------------------------------------------------------------------------------------------------------------------------:|
| ![image](https://github.com/Team-UniVoice/UniVoice_Server/assets/137388764/d0d09b19-3948-4559-911a-902e54d0ff82) | <img src="https://github.com/SOPT-33RD-APP-NAVERPAY/NaverPay-Server/assets/80024278/f38b7a70-1bf3-48b6-ad8a-05a1deb165be" width="300"/> |
|              [softmoca](https://github.com/softmoca)               |                                             [eeddiinn](https://github.com/eeddiinn)


| 담당 역할              |   Role   |
|:-------------------|:--------:|
| 프로젝트 초기 세팅(EC2 , RDS)       |   최영철   |
| ERD 설계              |   최영철, 현예진    |
| API 개발              |   최영철, 현예진    |
| 배포 (CI/CD)                |   최영철   |

## 🏫 개발 환경

| 통합 개발 환경 | IntelliJ |
| --- | --- |
| Spring 버전 | 3.3.1 |
| 데이터베이스 | AWS RDS(Postgres) |
| 배포 | AWS EC2(Ubuntu) |
| Project 빌드 관리 도구 | Gradle |
| ERD 다이어그램 툴 | ERDCloud |
| Java version | Java 17 |
| 패키지 구조 | 도메인 패키지 구조 |

## 🏫 ERD
![Untitled (8)](https://github.com/Team-UniVoice/UniVoice_Server/assets/137388764/4f717348-404e-4a94-9da4-121f2166bf17)

## 🌠Architecture 
![image](https://github.com/user-attachments/assets/3f92cb24-accc-43be-b3e3-c2d50b5dd9a1)


## 🏫 Structure

```yaml
📂 Univoice

🗂 src
    🗂 main
        🗂 java/sopt/univoice
            🗂 domain
                🗂 affiliation
                🗂 auth
                🗂 firstcome
                🗂 notice
                🗂 universityData
                🗂 user
            🗂 infra
                🗂 common
                🗂 config
                🗂 external
                🗂 persistence
        🗂 resources
            application.yml
    🗂 test 
    
```


## 🏫 Convention
- https://massive-maple-b53.notion.site/Convention-7e36098665d743bda62bf5e84838bf63?pvs=4
