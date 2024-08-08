<img width="169" alt="image" src="https://github.com/user-attachments/assets/55b298e8-4392-4448-abe2-e739484ddcc4"># 🌰 Univoice-Server 🌰
### 유니보이스 (UniVoice)
<img src="https://github.com/user-attachments/assets/d205e261-da14-4c22-adb4-7b795c54132e" width="100%" height="450"/> </br>
> 34st NOW SOPT APP JAM </br>
> 프로젝트 기간 : 2024.06.15 ~ </br>

“다양한 목소리를 통해 더 나은 학생 사회, 더 나은 대학 생활을 만들기 위해서” </br>
학생회와 학생들의 목소리로 함께 만들어가는 대학 생활 필수 앱, **유니보이스** 입니다.⏰ 
</br>


## 🌰Contributors
|<img src="https://github.com/Team-UniVoice/UniVoice_Server/assets/137388764/d0d09b19-3948-4559-911a-902e54d0ff82" width="250" /> | <img src="https://github.com/SOPT-33RD-APP-NAVERPAY/NaverPay-Server/assets/80024278/f38b7a70-1bf3-48b6-ad8a-05a1deb165be" width="250"/> |
|:---------:|:---------:|
|[최영철](https://github.com/softmoca)|[현예진](https://github.com/eeddiinn)|
| **[👑 Team Leader]** </br> 인프라 세팅(EC2 , RDS, CI/CD)</br>ERD 설계 </br> API 개발 | **[🤖 Team Member]** </br>API 개발 |
</br>


## 📷 Screenshot
|<img src="https://github.com/user-attachments/assets/e4f576df-565e-47bf-b235-f929e5ea1527" width=70% />|<img src="https://github.com/user-attachments/assets/ffc9104d-3c25-49d9-b2e1-b306ead2405b" width=70% />|<img src="https://github.com/user-attachments/assets/44874438-40f9-445c-885b-65ceda26f16e" width=70% />|
|:---------:|:---------:|:---------:|
|스플래시|초기|로그인|

|<img src="https://github.com/user-attachments/assets/7972a1fc-91c5-45bb-a5e2-31ed65230537" width=70% />|<img src="https://github.com/user-attachments/assets/4eb82928-125b-441e-8275-e65dd75133d9" width=70% />|<img src="https://github.com/user-attachments/assets/f9fa9a04-8b89-409a-aa1e-48bda61719a0" width=70% />|
|:---------:|:---------:|:---------:|
|회원가입|회원가입_개인정보입력|회원가입_학생증 인증|

|<img src="https://github.com/user-attachments/assets/7445659f-67ec-4a23-85e7-f34ae978a13b" width=70% />|<img src="https://github.com/user-attachments/assets/911402c5-8183-48ed-9299-12c733ca38e9" width=70% />|<img src="https://github.com/user-attachments/assets/c0bb8d7a-0ad5-48a0-8f4e-2a0b07dc99c0" width=70% />|
|:---------:|:---------:|:---------:|
|회원가입_계정 생성|회원가입_약관동의|회원가입_학생증 확인|

|<img src="https://github.com/user-attachments/assets/f8bdb6cd-d340-437e-889d-eaf2d8df2c8a" width=70% />|<img src="https://github.com/user-attachments/assets/e457b0a7-0f4c-48e2-8b67-a127f003c2ff" width=70% />|<img src="https://github.com/user-attachments/assets/b6388e26-ce2b-41ea-96fd-de59edf211f9" width=70% />|
|:---------:|:---------:|:---------:|
|메인 홈|퀵스캔|공지사항 세부화면|

|<img src="https://github.com/user-attachments/assets/cdc6954d-09f7-4da7-b84b-b6275a28c41e" width=70% />|<img src="https://github.com/user-attachments/assets/f1b963f8-061e-45ad-91b8-71d3fea9bdd7" width=70% />|<img src="https://github.com/user-attachments/assets/776a864b-31c4-4314-a5b8-ef3967b5b9df" width=70% />|
|:---------:|:---------:|:---------:|
|저장 홈|공지사항 등록화면|마이페이지|

</br>
</br>


## 🏫 개발 환경

| 통합 개발 환경 | IntelliJ |
| --- | --- |
| Spring Version | 3.3.1 |
| Java version | Java 17 |
| Database | AWS RDS(Postgres) |
| Deploy | AWS(EC2, S3,CodeDeploy), Nginx |
| CI/CD | Github Actions |
| ERD Tool | ERDCloud |


## 🏫 ERD
![Untitled (8)](https://github.com/Team-UniVoice/UniVoice_Server/assets/137388764/4f717348-404e-4a94-9da4-121f2166bf17)

## 🌠Architecture 
![image](https://github.com/user-attachments/assets/8d09c71f-0244-49d2-b809-7f7ebb4d4178)



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
