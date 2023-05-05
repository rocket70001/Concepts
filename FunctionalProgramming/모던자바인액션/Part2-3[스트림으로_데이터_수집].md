# Part2-3 스트림으로 데이터 수집

이전까지는 최종 연산인 collect를 collect(toList()); 형태로 사용했었다.  
이 장에서는 collect 역시 다양한 요소 누적 방식을 인수로 받을 수 있음을 확인할 것이다.  
다양한 요소 누적 방식은 Collect 인터페이스에 정의되어 있다.  
주의하자! Collection, Collector, collect 는 서로 다른 개념이다.  

이번 장은 데이터베이스 트랜잭션에 대한 사전 지식을 필요로 한다.  
트랜잭션은 데이터베이스에서 특정 정보를 추출하고 조작하기 위한 명령과 절차의 집합이다.  
다양한 나라의 화폐를 다루는 애플리케이션 만든다고 가정해보자.  
우선은 통화별로 각국의 화폐를 그룹화할 필요가 있다. 자바 8 이전의 코드로 이를 구현하면 다음과 같다.  
```java
Map<Currency, List<Transaction>> transactionByCurrencies = new HashMap<>();
//그룹화한 트랜잭션을 저장할 맵을 생성했다.

for(Transaction transaction : transactions) { //트랜잭션 리스트를 반복한다.
	Currency currency = transaction.getCurrency();
	List<Transaction> transactionsForCurrency =
		transactionByCurrencies.get(currency);
	if(transactionsForCurrency == null) { //현재 통화를 그룹화하는 맵에 항목이 없으면 항목 생성
		transactionsForCurrency = new ArrayList<>();
		transactionByCurrencies.put(currency, transactionsForCurrency);
	}
	transactionsForCurrency.add(transaction); // 같은 통화를 가진 트랜잭션 리스트에 현재 트랜잭션 추가
}
```

이를 자바 8의 코드로 바꾸면 다음과 같다.  
```java
Map<Currency, List<Transaction>> transactionByCurrencies =
	transactions.stream().collect(groupingBy(Transaction::getCurrency));
```
훨씬 간결하다!  

## 컬렉터
위의 두 코드는 명령형 프로그래밍과 함수형 프로그래밍의 차이를 보여준다.  
명령형 프로그래밍은 요구하는 바를 찾기 위해 명령(루프와 조건문...) 집합을 직접 구성한다.  
반면에 함수형 프로그래밍은 무엇을 요구하는지를 명시적으로 드러내 절차를 추상화한 채 트랜잭션을 수행한다.  

이렇듯 훌륭하게 설계된 함수형 프로그래밍은 높은 수준의 조합성과 재사용성을 가진다.  

이제 컬렉터가 무엇인지 생각해보자. 컬렉터는 간단하게 생각해서 그룹화하는 인터페이스이다.  
  
명령형 프로그래밍  
-> 루프와 조건을 통해 데이터 집합에서 요소를 구분하고 이를 모아 컬렉션을 만든다.(리스트, 맵 등 자료구조)  
  
함수형 프로그래밍의 컬렉터  
-> 추상화된 리듀스 연산 + 요소 그룹화 + 요소 분할을 통해 컬렉션을 만든다.  
따라서 함수형 프로그래밍을 통해 컬렉션을 만드려면 Collectors 클래스가 제공하는 메소드를 잘 이해하고  
필요에 따라 직접 (리듀싱 연산 + 요소 그룹화 + 요소 분할)을 작성할 줄 알아야 한다.  

#### 스트림값에서 최댓값과 최솟값 검색
Collectors.maxBy와 Collectors.minBy 두 메서드를 통해 스트림의 최솟값과 최댓값을 계산할 수 있다.  
두 컬렉터는 스트림의 요소를 비교하는 데 사용할 Comparator를 인수로 받는다.
```java
Comparator<Dish> dishCaloriesComparator =
	Comparator.comparingInt(Dish::getCalories);

Optional<Dish> mostCalorieDish =
	menu.stream()
		.collect(maxBy(dishCaloriesComparator));
```
칼로리로 요리를 비교하는 Comparator를 구현한 다음 Collectors.maxBy로 전달하는 코드이다.  
Optional\<Dish\>는 menu가 비어있는 경우 디폴트 값을 내놓기 위해 사용한다.  
이와 관련된 연산은 11장에서 자세하게 설명한다.  
스트림에 있는 객체의 숫자 필드의 합계나 평균을 반환하는 연산에도 이러한 리듀싱 기능이 자주 사용된다.  
이런 연산을 요약(summarization) 연산이라고 부른다.

#### 요약연산

! 요약 연산에 대해 알기 전에는 팩토리 메서드가 무엇인지부터 알아야 한다.  
팩토리 메서드는 객체의 생성을 서브 클래스에게 위임해 객체 생성의 유연성을 높이는 메서드이다.  
생성자가 아닌 팩토리 메서드로 객체를 생성하게 되면 객체의 생성 조건을 추상화해 더 높은 캡슐화 정도를  
가질 수 있다.  
팩토리 메서드는 일반적으로 인터페이스나 추상 클래스에 선언된다. 예를 들어 Dish 인터페이스를 구현한  
KoreanFood와 AmericanFood 클래스가 각각 존재한다고 가정하자. 이때 DishFactory라는 새 클래스를 만든다.  
DishFactory는 makeDish(String dish)라는 객체 생성 메서드를 가진다. makeDish는 인자로 들어온 문자열을  
파악해 문자열이 "korea"면 KoreanFood의 객체를 생성하고 "america"면 AmericanFood의 객체를 생성한다.  
이 과정을 통해 KoreanFood와 AmericanFood는 모두 객체의 생성 과정을 드러내지 않고도 객체를 생성할 수 있다.  
  
Collectors 클래스는 Collectors.summingInt라는 요약 팩토리 메서드를 제공한다.  
summingInt는 객체를 Int로 매핑하는 함수를 인수로 받는다.  
summingInt의 인수로 전달된 함수는 객체를 Int로 매핑한 컬렉터를 반환한다.  
summingInt가 collect의 메서드로 전달되면 요약 작업을 수행한다.  
```java
int totalCalories = menu.stream().collect(summingInt(Dish::getCalories));
```
메뉴 리스트의 칼로리를 계산하는 코드를 위와 같이 간결하게 나타낼 수 있다.  

합계 외에도 Collectors.averagingInt, Collectors.averagingDouble, Collectors.averagingLong 등  
다양한 연산의 요약 기능이 제공된다.  

이러한 요약 연산 중 두 개 이상의 연산이 동시에 수행되어야 할 때도 있다. 이럴 때는  
팩토리 메서드 summarizingInt가 반환하는 컬렉터를 사용할 수 있다.
```java
IntSummaryStatistics menuStatistics = 
	menu.stream().collect(summarizingInt(Dish::getCalories));
```
위 연산은 Dish에 포함된 모든 메뉴의 요소 수, 칼로리 합계, 평균, 최댓값, 최솟값 등을 계산한다.  
위 코드를 실행하면 IntSummaryStatistics 클래스로 모든 정보가 수집된다.  
menuStatistics 객체를 출력하면 다음과 같은 정보를 확인할 수 있다.
```java
IntSummaryStatistics{count=9, sum=4300, min=120, average=477.777778, max=800}  
```
  
정리. Collectors 클래스는 요약 연산을 하는 팩토리 메서드를 제공한다.  
이 팩토리 메서드를 통해 스트림의 각 값에 대응하는 객체나 값의 요약에 대응하는 객체를 생성할 수 있다.  

#### 문자열 연결
다음은 메뉴의 모든 요리명을 연결하는 코드이다.  
```java
String shortMenu = menu.stream()
		.map(Dish::getName)
		.collect(joining());
```
joining 메서드는 StringBuilder를 이용해 문자열을 하나로 만든다.  
Dish 클래스가 요리명을 반환하는 toString메서드를 포함하고 있다면 다음 코드처럼 map으로 각 요리의  
이름을 추출하는 과정을 생략할 수도 있다. 
```java
String shortMenu = menu.stream().collect(joining());
```
Dish 클래스가 toString 메서드를 포함하면 위 두 코드는 같은 값을 반환한다.
-> porkbeefchickenfrench friesriceseason fruitpizzaprawnssalmon  
순수한 joining은 구분자가 없다. 따라서 구분 문자열을 넣을 수 있도록 오버라이딩된 joining 팩토리 메서드를  
사용하기도 한다. 
```java
String shortMenu = menu.stream().map(Dish::getName).collect(joining(", "));
```
다음과 같은 결과를 반환한다.
-> pork, beef, chicken, french fries, rice, season fruit, pizza, prawns, salmon  

#### 범용 리듀싱 요약 연산
```java
int totalCalories = menu.stream()
	.collect(reducing(0, Dish::getCalories, (i, j) -> i + j));
```
위와 같이 reducing 메서드 만으로도 모든 메뉴의 칼로리 합계를 계산할 수 있다.  
첫 번째 인수는 연산의 시작값이자 스트림에 인수가 없을 때 반환할 반환값이다.  
두 번째 인수는 객체 데이터를 int값으로 매핑할 때 사용한 변환 함수다.  
세 번째 인수는 람다식으로 들어갔지만 원래 BinaryOperator가 들어간다.  
  
다음과 같이 한 개의 인수를 가진 reducing을 이용해 가장 칼로리가 높은 음식을 찾을 수도 있다.  
```java
Optional<Dish> mostCalorieDish = menu.stream()
		.collect(reducing((d1, d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2);
```

## 그룹화
처음에 봤듯이 명령형 프로그래밍을 통한 그룹화는 거추장스러운 코드로 에러가 발생할 가능성을 높인다.  
함수형 그룹화를 진행하면 한 줄의 코드로 그룹화를 구현할 수 있다.  
```java
Map<Dish, Type, List<Dish>> dishesByType = menu.stream().collect(groupingBy(Dish::getType));
```
Map에 포함된 결과는 다음과 같다.  
-> {Fish=[prawns, salmon], OTHER=[french fries, rice, season fruit, pizza]},  
MEAR=[pork, beef, chicken]}  
스트림의 각 Dish에서 타입이 일치하는 Dish.Type을 추출해 groupingBy 메서드로 전달했다.  
이 함수를 기준으로 스트림이 그룹화되므로 이를 분류 함수(Classification Function)이라고 부른다.  

#### 그룹화된 조작 연산
그룹화된 조작 연산은 그룹화 전에 전처리를 하고 그룹화를 하든지 그룹화 후에 조작을 하든지로 나뉜다.  
하지만 앞의 방식은 엄밀히 말해 그룹화된 조작 연산이라고는 할 수 없다.  
앞의 방식을 먼저 보자. filter에 predicate를 파라미터로 넣어 전처리 후 그룹화를 진행하는 코드이다.  
```java
Map<Dish, Type, List<Dish>> caloricDishesByType = menu.stream()
			.filter(dish -> dish.getCalories() > 500)
			.collect(groupingBy(Dish::getType));
```
filter에 500 칼로리를 초과하는 음식만 거르도록 람다식을 넣었고 이를 groupingBy로 그룹화한다.  
위 코드는 한 가지 단점을 가진다. 그룹화의 결과가 결국 Map의 객체가 되므로 요소값이 없다면  
키값까지 사라져 버린다. 예를 들어 소고기, 닭고기, 해산물 타입의 키값이 있고 소고기국, 프라이드 치킨의  
밸류만 있다면 Map에서 해산물 타입은 값이 없으므로 아예 없어져 버리는 것이다.  
SQL에 비유하면 이는 DML인 DELETE 연산을 통해 값만 삭제해야 하는데 DDL인 DROP 연산을 통해 아예 테이블의  
속성까지 삭제해버린 것과 비슷하다.  
  
Collectors 클래스는 일반적인 분류 함수에 Collector 형식의 두 번째 인수를 갖도록 groupingBy 팩토리 메서드를  
오버로드해 이 문제를 해결한다. 다음 코드처럼 groupingBy의 두 번째 Collector 안으로 필터 프레디케이트를  
이동시켜 이 문제를 해결할 수 있다.  
```java
Map<Dish, Type, List<Dish>> caloricDishesByType = menu.stream()
						.collect(groupingBy(Dish::getType),
						filtering(dish -> dish.getCalories() > 500, 
						toList()));
```
이렇게 추가된 두 번째 인수는 코드 그대로 프레디케이트를 사용해 필터링하고 필터링한 요소를 재그룹화 한다.  
이런식으로 그룹화한 뒤 조작 연산을 수행하면 해산물의 값이 없어도 해산물 키를 살릴 수 있다.  

#### 다수준 그룹화와 서브그룹으로 데이터 수집
그룹 내부에 여러 기준을 두어 한 그룹 내에서 그룹을 세분화하거나 서브그룹에서 특정 그룹을 찾을 수도 있다.
그룹을 세분화하는 데는 제한이 없다. 그러나 아직 프로그래밍을 하면서 다수준 그룹화를 사용하게 될 것 같지는 않다.  
그룹화와 그룹화한 뒤 조작연산을 사용하다가 세분화가 필요해지면 복습하기로 하자.  
일단은 groupingBy로 다수준 그룹화와 서브그룹 내에서 데이터 수집이 가능하다는 정도로 알고 넘어가자.  

## 분할
분할은 분할 함수(Partitioning Function)이라 불리는 프레디케이트를 분류 함수로 사용하는 특수한 그룹화 기능이다.  
분할 함수는 불리언을 반환하므로 맵의 키 형식은 Boolean이다. 이에 대한 결과로 그룹화 맵은 최대 2개(참, 거짓)  
으로 분류된다. 채식요리와 채식이 아닌 요리를 구분해보자.
```java
Map<Boolean, List<Dish>> partitionedMenu = menu.stream()
					.collect(partitioningBy(Dish::isVegetarian));
```
partitionedMenu 메뉴는 true와 false를 키로 가지는 두 그룹으로 분할되었다. 여기서 채식 요리만 리스트로  
뽑으려면 다음과 같이 작성하면 된다.  
```java
List<Dish> vegetarianDishes = partitionedMenu.get(true);
```
앞서 소개된 필터링 방식을 통해 분류할 수도 있었다.
```java
List<Dish> vegetarianDishes = menu.stream()
			.filter(Dish::isVegetarian)
			.collect(toList());
```

지금까지 몇 가지 Collectors 클래스의 팩토리 메서드들을 살펴봤다. 요약하지 않은 많은 팩토리 메서드들이 더 있다.  
그 외 사용법과 용례는 직접 구현해보며 필요할 때마다 찾아보자.  
중요한 것은 이 모든 컬렉터들이 Collector 인터페이스를 구현한다는 사실이다. 지금부터는 Collector 인터페이스를  
살펴볼 것이다. 그리고 자신만의 커스텀 컬렉터를 구현하는 방법을 알아본다.  

## Collector 인터페이스  
Collector 인터페이스는 리듀싱 연산(= 컬렉터)를 어떻게 구현할지 제공하는 메서드 집합으로 구성된다.  
toList나 groupingBy 등이 컬렉터였다. 먼저 Collector 인터페이스의 시그니처와 다섯 개의 메서드 정의를 보자.  
```java
public interface Collector<T, A, B> {
	Supplier<A> supplier();
	BiConsumer<A, T> accumulator();
	Function<A, R> finisher();
	BinaryOperator<A> combiner();
	Set<Characteristics> characteristics();
}
```
- T는 수집될 스트림 항목의 제네릭 형식이다. 
- A는 누적자, 수집 과정에서 중간 결과를 누적하는 객체의 형식이다. 
- R은 수집 연산 결과 객체의 형식(항상 그런 것은 아니지만 거의 대부분)이다.

Stream\<T\>의 모든 요소를 List\<T\>로 수집하는 ToListCollector\<T\>라는 클래스를 구현하면 다음과 같다.  
```java
public class ToListCollector<T> implements Collector<T, List<T>, List<T>>
```
개인적인 생각이지만 인풋은 T 중간 연산 결과는 A 최종 연산 결과는 R이라고 하는 것 같다.  

#### supplier() -> 새로운 결과 컨테이너 만들기  
supplier는 수집 과정에서 빈 누적자 Supplier 인스턴스를 만드는 파라미터가 없는 함수다.  
쉽게 생각해서 Collector 클래스를 위해 중간값을 저장할 수 있는 빈 mutable 객체를 반환하는 메서드라고  
생각하면 된다. ToListCollector처럼 누적자를 반환하는 컬렉터에서는 빈 누적자가 비어있는 스트림의 수집  
과정의 결과가 될 수 있다. ToListCollector에서 supplier는 다음처럼 빈 리스트를 반환한다.
```java
public Supplier<List<T>> supplier() {
	return () -> new ArrayList<T>();
}
```
생성자 참조를 전달하는 방법은 다음과 같다.  
```java
public Supplier<List<T>> supplier() {
	return ArrayList::new;
}
```

#### accumulator() -> 결과 컨테이너에 요소 추가하기
연산이 진행되고 있는 중간 연산에 다음 요소를 추가해 연산하게 하는 메서드이다.  
add()는 특정 데이터 셋에만 적용된다. 또 accumulator는 스레드 안정성이 있다. 
```java
public BiConsumer<List<T>, T> accumulator() {
	return (list, item) -> list.add(item);
}

public BiConsumer<List<T>, T> accumulator() {
	return List::add;
}
```
위는 람다식 반환이고 아래는 메서드 참조형 반환이다.  

#### finisher() -> 최종 변환값을 결과 컨테이너로 적용하기
finisher는 스트림 탐색을 끝낸다. 누적자 객체를 최종 결과로 변환하면서 누적과정을 끝낼 때 호출할 함수를  
반환해야 한다. 변환 과정이 필요하지 않으면 항등 함수를 반환하고 아니면 변환해서 반환한다.  
```java
public Function<List<T>, List<T>> finisher() {
	return Function.identity();
}
```

#### combiner() -> 두 결과 컨테이너 병합
combiner는 서로 다른 서브파트를 병렬로 처리할 때 누적자가 이 결과를 어떻게 처리할지 정의한다.  
```java
public BinaryOperator<List<T>> combiner() {
	return (list1, list2) -> {
		list1.addAll(list2);
		return list1;
	}
}
```
위에서는 간단하게 list1에 list2를 concat 처리해 반환했는데 실제로는 더 복잡할 수 있다.  
스트림의 리듀싱을 병렬 처리할 때 자바7의 포크/조인 프레임워크와 7장에서 배울 Spliterator를 사용한다.
  
#### characteristics()
컬렉터의 연산을 정의하는 Characteristics 형식의 immutable set을 반환한다.  
Characteristics는 스트림을 병렬로 리듀스할 것인지, 병렬로 리듀스한다면 어떤 최적화를 선택해야 할지  
흰트를 제공한다. Characteristics는 다음 세 항목을 포함하는 열거형이다.  
- UNORDERED: Set이므로 리듀싱 결과는 스트림 요소 방문 순서나 누적 순서에 영향을 받지 않는다.
- CONCURRENT: 다중 스레드에서 accumulator 함수를 동시에 호출할 수 있으며 이 컬렉터는 스트림의 병렬 리듀싱을  
수행할 수 있다. 컬렉터의 플래그에 UNORDERED를 함께 설정하지 않았다면 데이터 소스가 정렬되지 않은 상황에서만  
병렬 리듀싱을 수행할 수 있다.  
- IDENTITY\_FINISH: finisher()가 반환하는 함수는 단순히 identity를 적용할 뿐이므로 이를 생략할 수 있다.  
따라서 최종 결과인 누적자 객체를 바로 사용하거나 형변환할 수 있다.  

#### 커스텀 컬렉터 ToListCollector의 구현
```java
import java.util.*;
import java.util.function.*;
import java.stream.Collector;
import static java.util.stream.Collector.Characteristics.*;

public class ToListCollector<T> implements Collector<T, List<T>, List<T>> {
	@Override
	public Supplier<List<T>> supplier() {
		return ArrayList::new; // 수집 연산의 시발점, 빈 객체 공급
	}

	@Override
	public BiConsumer<List<T>, T> accumulator() {
		return List::add; // 탐색한 항목을 누적하고 누적자를 업데이팅.
	}

	@Override
	public Function<List<T>, List<T>> finisher() {
		return Function.identity(); // 항등 함수
	}

	@Override
	public BinaryOperator<List<T>> combiner() {
		return (list1, list2) -> {
			list1.addAll(list2);
			return list1;
		}
	}

	@Override
	public Set<Characteristics> characteristics() {
		return Collections.unmodifiableSet(EnumSet.of(IDENTITY_FINISH,
					CONCURRENT)); // 컬렉터의 플래그를IDENTITY_FINISH, CONCURRENT로 설정한다. 
	}
}
```
위 구현은 Collectors.toList 메서드가 반환하는 결과와 거의 일치한다. 기존의 코드를 다음과 같이 바꿀 수 있다.  
```java
List<Dish> dishes = menuStream.collect(toList()); // 기존 코드
List<Dish> dishes = menuStream.collect(new ToListCollector<Dish>()); // 커스텀리스트
```
IDENTITY\_FINISH 연산에서는 Collector 인터페이스를 새로 구현하지 않고도 같은 결과를 얻을 수 있다.  
```java
List<Dish> dishes = menuStream.collect(
		ArrayList::new, // 발행
		List::add, // 누적
		List::addAll); // 합침
```
collect(발행, 누적, 합침)을 오버로드해 이런 식으로 사용 가능하다. 
collect 자체에 오버로딩된 기능이 제공되는 건지 따로 구현해서 사용한다는 건지는 책에 분명히 나와있지 않아  
모르겠다. 필요할 때 사용해보고 확인하면 된다.  

## 커스텀 컬렉터를 구현해 성능 개선하기
collect의 기존 기능을 제대로 활용해보고 익숙해지면 그때 보자.

## 정리
collect는 다양한 Collector를 활용해 스트림을 누산하는 최종 연산이다.  
최대, 최소, 평균, 누적합 등등 자료구조에 대해 일반적으로 사용되는 연산은 미리 정의되어 있다.  
groupingBy로 요소를 그룹화하거나 partitioningBy로 요소를 분할할 수 있다.  
명령형으로 그룹화, 분할, 리듀싱할 것인가 함수형으로 할 것인가? 함수형으로 해도 네 다섯 줄이 나오는데  
명령형으로 하면 20줄이 넘게 나올 수도 있다. 그룹화, 분할, 리듀싱이 필요하면 함수형을 떠올리자.  
Collector인터페이스에 정의된 메서드를 구현해서 커스텀도 가능하다.  

## 개선
커스텀이나 그룹화 후 세분화 등은 자세히 보지 않았다.  
객체지향도 서툴고 함수형도 제대로 사용해보지 않았는데 디테일까지 배우는 건 욕심같다.  
이제 스트림이 데이터 플로우에 대한 연산을 미리 정의해놓고 간소화, 재사용성 높이기, 캡슐화하는 과정이라는  
생각이 든다. 명령형이 노출하는 루프와 조건 형식 등을 스트림은 추상화한다.  
메서드의 이름을 통해 어느 정도 유추가 되기는 하지만 커스텀으로 그것마저 숨기면(그럴 필요가 있을까 싶지만)  
스트림의 가공 절차를 완전히 캡슐화하는 것도 가능할 것 같다.  
병렬, 세분화에도 유리하다고 하는데 아직 거기까지는 어떤 식으로 그렇게 되는 건지 와닿지 않는다.   
또 함수형 인터페이스를 활용하는 게 재사용성면에서 좋다고는 하는데 supplier가 빈 객체를 생성해서 반환하는 거나  
accumulator를 이용해서 누산하는 과정이 아직까지는 어떻게 재사용성 면에서 기존 방식보다 나은 건지 직접적으로 와닿지는 않는다. 이 부분은 직접 사용하면서 프로그래밍 하지 않으면 느끼기 힘들 것 같다.
