# Part2-1 스트림 소개

스트림은 명료화 작업이다. 우선은 데이터 컬렉션 반복을 명료하게 처리하는 기능이라고 생각하고 자바7의 코드를 보자.  
```java
List<Dish> lowCaloricDishes = new ArrayList<>();
for(Dish dish : menu) {
	if(dish.getCalories() < 400) {
		lowCaloricDishes.add(dish);
	}
}
Collections.sort(lowCaloricDishes, new Comparator<Dish>() {
	public int compare(Dish dish, Dish dish2) {
	return Integer.compare(dish1, getCalories(), dish2, getCalories());
	}
});
List<String> lowCaloricDishesName = new ArrayList<>();
for(Dish dish : lowCaloricDishes) {
	lowCaloricDishesName.add(dish.getName()); // 정렬된 리스트를 처리하면서 요리 선택
}
```
위 코드는 400 칼로리 미만의 음식을 lowCaloricDishes 리스트에 추가하고 다시 이를 칼로리 순서에 따라  
정렬한다. 위에서는 lowCaloricDishes라는 가비지 변수를 사용한다. 최종 결과는 lowCaloricDishesName이므로  
lowCaloricDishes는 컨테이너 역할만 하는 중간 변수다. 자바8에서 이런 세부 구현은 라이브러리 내에서 처리할 수 있다.  
자바8의 코드를 보자
```java
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
List<String> lowCaloricDishesName = menu.stream()
				.filter(d -> d.getCalories() < 400)
				.sorted(comparing(Dishes::getCalories))
				.map(Dish::getName)
				.collect(toList());
```
여기서 stream()을 parellelStream()으로 바꾸면 멀티코어 아키텍처에서 병렬로 실행할 수 있다.  
```java
List<String> lowCaloricDishesName = menu.parallelStream() // stream -> parallelStream
				.filter(d -> d.getCalories() < 400)
				.sorted(comparing(Dishes::getCalories))
				.map(Dish::getName)
				.collect(toList());
```
이때 얼마나 많은 스레드가 사용되고 성능은 어떻게 되는지는 7장에서 설명한다.  
위에서 본 스트림의 장점은 다음과 같다.  
1. 선언형으로 코드를 구현한다. 위에서는 스트림을 통해 루프와 조건문 없이 단순히 선언 만으로 "저칼로리 요리를 선택하라"  
가 가능했다. -> *선언형*
2. 파이프라이닝을 통해 복잡한 데이터 처리 연산을 하면서도 가독성과 명료함을 유지한다. -> *조립형*
3. 뒷장에서 살펴보겠지만 특정 스레딩 모델에 제한되지 않고 싱글이든 멀티든 스레드와 락을 걱정할 필요가 없다. -> *병렬화*  
  
기억하자. 스트림은 *선언형*, *조립형*, *병렬화*의 특징을 가졌다.  
스트림은 일련의 데이터 흐름을 조립된 연산을 통해 병렬화한다.  
  
```java
import static java.util.stream.Collectors.toList;
List<String> threeHighCaloricDishNames = menu.stream() //메뉴(요리 리스트)에서 스트림을 얻는다.
					.filter(dish -> dish.getCalories() > 300)
					.map(Dish::getName)
					.limit(3)
					.collect(toList());
System.out.println(threeHighCaloricDishNames);
```
menu.stream()을 통해 일련의 데이터 흐름(요리 리스트의 인자)을 얻은 뒤 데이터 소스는 연속된 요소를 스트림에 제공한다.  
collect를 제외한 filter, map, limit 등의 연산은 값을 반환하며 파이프라이닝한다. 마지막으로 collect는 스트림이 아닌  
리스트를 반환하므로 파이프라인의 결과값을 반환한다. SQL을 떠올려보자.  
  
컬렉션과 구별되는 스트림의 특징은 현행성이다. 스트림은 사용자가 요구하는 시점(현재)에서 행해진다.  
반면에 컬렉션은 모든 자료구조의 값을 저장해두고 사용자에게 제공한다. 책에서는 컬렉션을 DVD, 스트림을 스트리밍 서비스에  
비유한다. 따라서 컬렉션은 생산자 중심(suppliler-driven)이지만 스트림은 요청 중심(demand-driven)이라고 표현한다.  
  
스트림의 데이터 사용은 일회적이다. 생각해보면 당연하다. 컬렉션에는 데이터가 모두 저장되어 있으니 인덱스를 초기로 돌리면  
루프를 통해 데이터를 재순회할 수 있다. 하지만 스트림은 일회적으로 입력되는 데이터를 연산한 뒤 이전 값을  
따로 저장하지 않는다. 따라서 인덱스를 돌려 루프를 재순회할 수 없다.   
만약 스트림의 데이터 소스가 컬렉션에서 비롯됐다면 스트림을 재생성해 컬렉션의 데이터를 가져와 다시 순회할 수 있기는 하다.  
그러나 데이터 소스가 I/O 채널이라면 소스를 반복 사용할 수 없으므로 새로운 스트림을 만들 수도 없다.  

#### 컬렉션의 외부 반복과 스트림의 내부 반복
외부 반복은 for이나 Iterator를 돌리는 걸 말한다. 데이터를 개별적으로 하나씩 꺼내 사용함으로 병렬화하기 위해서  
하나씩 개별적으로 사용자가 관리해줘야 한다(synchronized). 생각해보면 데이터를 하나씩 꺼내 사용한다는 것을  
for(int i = 0; i < end; i++)와 같은 식으로, 또는 Iterator로 명시해놨으니 병렬성 또한 사용자 스스로 처리하는 게 당연하다.  
하지만 스트림은 언어 자체가 제공하는 메커니즘에 순회와 병렬성 처리를 맡긴다. 자료를 반드시 하나씩 반복하라고  
명시하지도 않는다. 사용자가 스트림을 이용할 때 기대하는 것은 특정 입력에 대한 특정 결과값이기 때문이다.  
이런 편의를 위해 스트림은 filter나 map처럼 반복을 숨겨주는 연산 리스트를 미리 정의하고 람다 표현식을 인수로 받아  
이를 처리한다. 스트림이 어떤 추가 연산을 제공하는지는 5장에서 확인한다.  

#### 스트림 연산
위에서 확인한 바와 같이 스트림은 파이프라이닝 하는 중간 연산과 스트림을 닫는 연산인 최종 연산으로 구분된다.  
중간 연산의 핵심적인 특징은 게으름(Laziness)이다. 이는 단말 연산을 스트림 파이프라인에 실행하기 전까지는  
중간연산이 아무 연산도 수행하지 않는 성질을 말한다. 중간 연산은 중간 연산을 최종 연산으로 한 번에 처리한다.  
  
스트림 연산은 다음과 같은 세 가지 요소로 이루어진다.
1. 데이터 소스(일련의 데이터 흐름)
2. 중간연산(파이프라인)
3. 최종연산(파이프라인을 실행하고 결과를 만들 최종 연산)  
빌더패턴에서 호출을 연결해 설정을 만들고 build 메서드를 호출해 최종 연산을 하는 것과 비슷하다.  

## 정리
스트림은 일련의 데이터 흐름에 조립된 연산을 수행하는 활동이다.  
스트림의 특징은 선언형, 조립형, 병렬화로 요약될 수 있다.  
스트림은 컬렉션과 자료구조의 저장 형태나 데이터 처리 방식에 있어 차이를 보인다.  
컬렉션은 메모리 상에 자료구조의 모든 데이터를 저장한 뒤 사용하고 스트림은 필요에 따라 가져다 소비한다.  
이와 같은 데이터 처리 방식의 차이로 인해 스트림은 비가역적이다. 연산을 되돌리거나 재수행 하기 위해서는 새 스트림을  
생성해야 한다. (스트림에서 제공하는 peek()메서드를 사용하면 중간 연산을 볼 수 있다고 한다. 그러나 peek() 메서드 또한  
실제로는 최종 연산을 통해 처리된다.)
스트림의 연산은 지연 평가(Lazy Evaluation) 형태를 띠고 있으며 연산의 단계는  
데이터 소스 공급 -> 최종 연산의 수행 명령 -> 중간 연산의 일괄 수행과 최종 연산을 통한 결괏값 도출로 이어진다.  

