######################################################
=+= aldarEconomyModule by camelCase#6543(디스코드) =+=
######################################################


#플러그인 주요 기능: 전체 경제 시스템 관리 및 돈의 흐름 추적

* 돈의 발행과 소모를 편리하게.
 - 기존 이코노미 시스템의 편리성을 그대로 가져왔습니다.
 - 돈 생성을 위한 아이템 정보를 플러그인마다 관리하지 않아도 됩니다.
 - 거래시스템을 따로 구현하지 않아도 됩니다.
 - 서버 내에서 돈이 발행될때와 소멸할때 추적이 가능하도록 돈을 지급해야 하는 상점 모듈 등에서는 EconomyModule을 활용해야 합니다.
 - EconomyModule을 사용하지 않고 돈을 생성할 경우 위조 화폐로 감지하여 사용할 수 없습니다.
  
* 돈이 어디서 발급되었는지 추적 가능.
 - 유저들의 컨텐츠 이용 패턴을 파악할 수 있음.

* 유저들이 돈을 얼마나 소유했는지 알 수 있음
 - 이코노미 플러그인을 쓰지 않고 실물 돈을 사용해도 유저들이 돈을 얼마나 가졌는지 알 수 있음.
 
* 모든 거래 추적 가능
 - 거래량이 많은 지역(섬)이나 거래량이 많은 특정 유저, 유저 그룹, 유저 레벨 등을 추적 가능.
 - 이를 이용할 경우 유저의 플레이 유형에 따른 거래 패턴을 파악하여 맞춤형 컨텐츠를 제작할 수 있음.
 - 인공지능 분석을 활용할 경우 특정 유저의 과거 컨텐츠 이용 패턴에 따른 서버 접속률, 미래 활동 패턴(접는 날짜 등)을 예측 가능.
 - 웹 상에서 유저들의 거래 연결망을 그래프(자료구조) 형태로 보여주는 기능을 제작할 수 있음.
 
* 거래 API제공.
 - 플레이어A가 플레이어B와 거래할 때 자동 잔돈 지급 등의 돈 관리를 해주는 API제공.
 
* 복사 버그 방지
 - 플레이어가 정상적으로 발행되지 않은 돈을 획득할 경우 감지 가능.
 - 플레이어가 정상적으로 발행되지 않은 돈을 인벤토리에서 건드릴 경우 감지 가능.
 - 추적되지 않은 돈을 플레이어가 소유할 경우 일단 메인 시스템에서 발행된 돈이라고 간주. (기록: 시스템에서 발행됨 블록:창고, 좌표:xyz)(기록: 시스템에서 발행됨 좌표xyz)
 - 메인 시스템이 특정한 플레이어에게 돈을 비정상적으로 많이 발행해주었을 경우 그 플레이어는 버그 악용 유저로 판단
 
 
