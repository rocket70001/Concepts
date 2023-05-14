<h1> 과목1 - 2. 데이터 모델과 성능 </h1> (5/14, -ing)<br>

## 1절. 정규화
정규화는 데이터 모델링에서 가장 기초적이면서 필수적인 작업이다. 이번 장에서는 정규화의 필요성을 반드시 알아야 한다.   

1. 제1정규형 : 하나의 속성에 하나의 값
2. 제2정규형 : 부분 종속성 제거
함수 종속성은 어떤 기준값에 의해 데이터가 종속되는 현상을 지칭한다. 이때 기준값을 결정자라고 하고 종속되는 값을  
종속자라고 한다. 만약 특정 속성이 식별자 전체가 아닌 일부에만 종속적이면 이를 부분 종속이라고 한다. 일반속성이 주식별자가 아닌 다른 식별자에만 종속적인 부분 종속 상황이 생기면 제2정규형이 위배된다. 당장에는 큰 문제가 없을 수 있지만 가까운 미래에 주식별자가 아닌 타 식별자에 부분 종속적인 개체의 이름이 바뀔 경우, 이름이 바뀌기 이전 개체를 모두 찾아 바뀐 이름으로 변경해야 한다.  
이 경우 엔터티를 분리해 제2정규형을 만족시킬 수 있다.  
3. 제3정규형 : 이행 종속성 제거  
이행 종속성도 데이터 변경을 위해 제거한다. 데이터 변경으로 발생하는 트랜잭션은 도메인 로직과 관련없는 트랜잭션이기에 엔터티를 분리해 이행 종속성을 제거해야 한다.  
  
정규화는 기초적이지만 무조건적이지는 않다. 경우에 따라 성능 향상을 위해 반정규화를 진행하기도 한다.  
반정규화를 할 때는 성능향상도 좋지만 트레이드오프를 고려해야 한다.  
일례로 배송관련 시스템을 구축할 때 주문과 배송을 별개의 엔터티로 구성해 모델링을 진행했다고 가정해보자.  
고객의 주문 번호(주식별자)를 가지는 엔터티는 주문이며 배송 정보를 확인할 송장번호는 배송 엔터티에 들어있다. 
이런 경우에 고객이 매번 배송정보를 확인할 때마다 주문 번호가 있는 주문 엔터티와 주문번호를 외래키로 가지는 배송 엔터티를 조인해  
배송 엔터티 내의 송장번호를 확인해야 한다. 이를 해결하기 위해 송장 번호를 주문 엔터티에도 포함시켜 반정규화를 진행할 수 있다.  
그러나 송장 번호는 배송이 출발할 때 입력되는 정보이기에 고객이 주문을 한 시점에는 항상 NULL값이거나 디폴트 값으로 존재하게 된다.  
이 경우 주문 엔터티에는 이전에는 필요없던 UPDATE(송장 번호 갱신을 위한) 로직이 추가되어야 한다. 따라서 조회(SELECT) 성능향상을 위해 UPDATE 로직을 추가해야 하는 상황에 놓이게 된다. 이런 경우는 반정규화를 진행하지 않아야 한다.  
  
## 2절. 관계와 조인의 이해  
DB에서 관계를 맺는다는 건 식별자를 상속하고, 상속된 속성을 매핑키로 활용해 데이터를 결합해 볼 수 있다는 의미이다.  
SQL에서는 이를 조인이라고 한다.  
  
#### 조인
관계가 형성된 두 엔터티에서 관계를 잇는 키를 매핑키로 설정해 원하는 정보를 얻는 행위이다.  
  
#### 계층형 데이터 모델(셀프 조인)
자가 참조하는 모델. 스스로의 키를 계층 관계 내의 엔터티 모델에 재참조시켜 셀프 조인하는 모델이다.  
예로 엔지니어, 마케터, 매니저가 공통으로 존재하는 직원 엔터티가 있을 때 엔지니어와 마케터의 관리자를 찾는 쿼리를 구성하려 한다.  
이때 같은 엔터티 내의 매니저 코드를 재참조해 엔지니어와 마케터의 매니저를 특정할 수 있다.  
계층형 데이터 모델은 일반적으로 사용되며 계층형 쿼리(Connect By절)을 이해하기 위해 반드시 알고 있어야 한다.  
  
#### 상호배타적(Exclusive-OR 관계)
1:N 관계를 맺는 엔터티(1)이지만 N 중 하나로부터만 상속하는 관계를 말한다.  
ex)회원 엔터티는 개인 엔터티와 법인 엔터티와 동시에 관계를 맺지만 동시 상속은 안 된다.

## 3절. 모델이 표현하는 트랜잭션의 이해

