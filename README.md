<div align="center">
<h1> 러닝할 땐 욜로가! 🏃🏻‍♀️🏃🏻 </h1>
</div>

<p align="center">
<img src="https://user-images.githubusercontent.com/92148749/229272189-a08ec8a9-6ccf-433a-a15a-0782822a2d48.png" alt="dobugs-logo" width="220" height="220">
</p>

## 프로젝트 소개
가까이에 있는 사람들과 함께 러닝을 즐길 수 있는 욜로가 서비스입니다!

<br>

## 주요 기능
- 내 주변의 러닝크루들을 탐색할 수 있어요. 🧐
- 직접 주최하거나 참여했던 러닝 히스토리를 기록해두었어요. 📚
- 러닝크루를 소규모로 진행하고 싶다면, 원하는 사람만 승인하도록 허가할 수 있어요. 🤫
- 목표했던 시간과 실제로 러닝했던 시간들을 기록하여 러닝 이력들을 관리하세요! 🏃🏻‍♀️🏃🏻

<br>

## 기술 스택
![기술스택](https://user-images.githubusercontent.com/92148749/229279299-203a9097-11f3-4438-a2f5-544ea379392c.png)

<br>

## 인프라 아키텍처
<p align="center">
<img src="https://user-images.githubusercontent.com/92148749/229276598-98f8b3ac-efd4-495c-95f5-304e60317d0d.png" alt="architecture" width="900">
</p>

<br>

- AWS Load Balancer 를 이용하여 HTTPS 적용하였습니다.
- 제한된 인스턴스 내에 여러 서비스를 제공하기 위해 포트를 분리하였습니다.
- 더욱 빠른 응답을 위해 JWT 는 Redis 로 관리하였습니다.
- SPOF 를 줄이기 위해 로드 밸런싱을 도입하였습니다.

<br>

## 욜로가 서비스 이용해보기

- [✨ 욜로가 방문하기](https://dev.yologa.dobugs.co.kr/)

- [📘 활동 기록 보러가기](https://github.com/dobugs/yologa-api/wiki/%ED%99%9C%EB%8F%99-%EA%B8%B0%EB%A1%9D)

- [🙋🏻‍♀️ 두벅스 사용자 프로젝트 구경하기](https://github.com/dobugs/yologa-authentication-api)
