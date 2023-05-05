# Part2-2 스트림 활용

앞 장에서 스트림의 구조와 성격을 알아보았다면 이번 장에서는 스트림의 활용에 관해 알아본다.  
스트림 API는 내부적으로 다양한 최적화를 지원한다.  
언어가 지원하는 내부 반복을 사용하기 때문에 가능한 일이다.  
이와 같은 내부 작업에는 필터링, 슬라이싱, 매핑, 검색, 매칭, 리듀싱과 병렬화 등이 있다.  
하나씩 살펴보자  
#### 필터링
스트림에서 요소를 선택할 때 필터링을 사용한다. 필터링은 크게 프레디 케이트 필터링과 고유 요소 필터링으로 나뉜다.  
filter메서드에 프레디케이트 함수를 인수로 받아 프레디 케티르와 일치하는 모든 요소를 반환한다.  
  
프레디케이트 필터링
```java
List<Dish> vegetarianMenu = menu.stream()
			.filter(Dish::isVegetarian)
			.collect(toList());
```
  
distinct()로 고유요소 필터링(중복제거 필터링)
```java
List<Integer> numbers = Arrays.asList(1, 2, 1, 3, 2, 4);
numbers.stream()
	.filter(i -> i % 2 == 0)
	.distinct()
	.forEach(System.out::println);
```
위 코드에서 2가 2번 반복되지만 distinct가 중복을 필터링해 결과는 "2\n4"가 나온다.

#### 슬라이싱
슬라이싱은 스트림의 요소를 선택하거나 스킵하는 연산이다.

*takeWhile*  
```java
List<Dish> specialMenu = Arrays.asList(
		new Dish("seasonal fruit", true, 120, Dish.Type.OTHER0),
		new Dish("prawns", false, 300, Dish.Type.FISH),
		new Dish("rice", true, 350, Dish.Type.OTHER),
		new Dish("chicken", false, 400, Dish.Type.MEAT),
		new Dish("french fries", true, 530, Dish.Type.OTHER));

List<Dish> filteredMenu = specialMenu.stream()
				.filter(dish -> dish.getCalories() < 320)
				.collect(toList());
```
만일 위와 같이 칼로리가 오름차순으로 정렬된 리스트가 있고 거기에서 320 칼로리 미만의 음식들을 골라  
새 리스트를 구성하고자 한다면 filter를 사용해 위와 같이 작성할 수 있다. 그러나 이렇게 한정된 데이터셋이 아니라  
엄청나게 많은(무한도 포함) 데이터를 처리하고자 할시, 특정값(여기서는 320)이상의 데이터를 모두 처리하는 건 낭비이다.  
이때 takeWhile()을 사용하면 조건식에서 벗어나는 값이 나오자마자 연산을 중단하고 값을 최종 연산에 파이프라이닝  
할 수 있다. 

```java
List<Dish> slicedMenu1 = specialMenu.stream()
			.takeWhile(dish -> dish.getCalories() < 320)
			.collect(toList());
```
위 코드는 seasonal fruit, prawns를 지나 칼로리가 350인 rice를 만나는 즉시 연산을 중단하고 collect에 값을 전달한다.

*dropWhile()*  
dropWhile()은 프레디케이트가 처음으로 거짓이 되는 시점까지 발견된 요소를 버린 뒤 남은 요소를 반환한다.  
```java
List<Dish> slicedMenu1 = specialMenu.stream()
			.dropWhile(dish -> dish.getCalories() < 320)
			.collect(toList());
```
rice, chicken, french fries	반환

*limit*  
limit()은 주어진 값 이하의 크기를 갖는 새 스트림을 반환한다. limit에 정렬 기능이 있는 것은 아니므로  
정렬되어있지 않은 리스트라면 정렬되지 않은 상태를 반환한다.
```java
List<Dish> slicedMenu1 = specialMenu.stream()
			.filter(dish -> dish.getCalories() > 300)
			.limit(2)
			.collect(toList());
```
rice, chicken만 반환 french fries 는 limit 초과

*skip*  
skip은 말그대로 skip이다.
```java
List<Dish> slicedMenu1 = specialMenu.stream()
			.filter(dish -> dish.getCalories() > 300)
			.skip(2)
			.collect(toList());
```
rice, chicken 스킵하고 french fries만 반환

#### 매핑
특정 객체에서 특정 데이터를 선택하는 기능. SQL을 떠올리자.  
특정 데이터를 선별하는데 왜 셀렉션이 아니라 매핑인가?  
 -> 스트림은 저장된 데이터를 있는 그대로 가져다 쓰는 게 아니라 해당 데이터 버전을 가지고 새 데이터 흐름을 만든다는  
 개념에 가깝기 때문에 변환(transforming)에 가까운 매핑(mapping)이라는 단어를 사용한다.  
 코드를 보자
 ```java
 List<String> dishNames = menu.stream()
				.filter(dish -> getCalories() > 300)
				.map(Dish::getName)
				.map(String::length)
				.collect(toList());
```
```sql
SELECT LEN(dishNames)  
FROM menu  
WHERE calories > 300  
```
와 비슷하지 않은가? 
  
flatMap()은 스트림의 각 값을 다른 스트림으로 만든 뒤 모든 스트림을 하나의 스트림으로 연결한다.  
```List<String> uniqueCharacters = words.stream()
			.map(word -> word.split("")) // 각 단어를 개별 문자를 포함하는 배열로 변환
			.flatMap(Arrays::stream) // 생성된 스트림을 하나의 스트림으로 평면화
			.distinct()
			.collect(toList());
```
만약에 words가 ["Hello", "World"]인 리스트이면 map 수행 결과는 Stream\<String\[\]\>가 된다.  
문자열 Hello와 World가 다르므로 결과는 Hello World가 된다. 플랫맵은 구분된 두 문자열을 합쳐  
Stream\<String\>으로 만들어준다. 따라서 HelloWorld가 되고 이는 distinct 수행을 통해 HeloWrd가 된다.  

#### 검색과 매칭

*anyMatch()*
```java
if(menu.stream().anyMatch(Dish::isVegetarian)) {
	System.out.println("The menu is (somewhat) vegetarian friendly!!");
}
```
프레디케이트가 주어진 스트림에서 적어도 한 요소와 일치하는지 확인한다.
  
*allMatch()*  
```java
boolean isHealthy = menu.stream()
			.allMatch(dish -> dish.getCalories() < 1000);
```
모든 요소가 주어진 프레디케이트와 일치하는지 확인한다.

*noneMatch()*
```java
boolean isHealthy = menu.stream()
			.noneMatch(d -> d.getCalories() >= 1000);
```
모든 요소가 주어진 프레디케이트와 일치하지 않는지 확인한다.  

*findAny*
```java
Optional<Dish> dish = menu.stream()
			.filter(Dish::isVegetarian)
			.findAny();
```
스트림에서 임의의 요소를 반환한다.
  
Optional은 값이 존재하는지 확인하고 값이 없을 때 어떻게 처리하는지 강제하는 기능을 제공한다.  
null 관련 버그를 피하기 위해 자주 사용된다.  

*findFirst*
```java
List<Integer> someNumbers = Arrays.asList(1,2,3,4,5);
Optional<Integer> firstSquareDivisiblebyThree = someNumbers.stream()
					.map(n -> n * n)
					.filter(n -> n % 3 == 0)
					.findFirst(); // 9
```
요소에서 찾은 첫번째 값을 반환한다. findAny는 순서가 관련 없을 때 병렬성을 위해 사용한다.  

-> allMatch, noneMatch, findFirst, findAny 등의 연산은 쇼트 서킷이라고 부른다.  
쇼트 서킷이란 스트림에 주어진 모든 데이터를 확인하지 않아도 결과를 즉시 반환할 수 있는 연산을 말한다.  
예를 들러 여러 and 연산으로 연결된 커다란 불리언 표현식이 있을 때 첫 결과가 거짓이라면 다음 결과를  
확인할 필요가 없다. 이런 경우 스트림의 길이가 무한하더라도 이를 유한한 크기로 줄일 수 있게 된다.  

#### 폴드 또는 리듀싱 연산
함수형 프로그래밍에서는 스트림을 작은 조각이 될 때 까지 접는 것 같다고 해서 폴드,   
자바8에서는 리듀싱 연산이라고 한다. 리듀싱 연산은 스트림 값을 전부 처리해 값으로 도출하는 연산이다.  

*reduce()*
```java
int sum = numbers.stream().reduce(0, Integer::sum);
```
Integer::sum은 람다식으로 바꾸면 (0,(A, B) -> A + B)이다.  

```java
Optional<Integer> sum = numbers.stream().reduce((a, b) -> (a + b));
```
스트림에 아무 것도 없는 상태라면 초깃값이 없어 reduce를 반환할 수 없으므로 합계가 없음을 가리킬 수 있도록  
Optional객체로 감싼 결과를 반환한다.  

```java
Optional<Integer> max = numbers.stream().reduce(Integer::max);
Optional<Integer> min = numbers.stream().reduce(Integer::min);
```
위와 같은 방식으로 reduce()를 이용해 최댓값과 최솟값을 찾을 수도 있다.  

## 정리
문제를 해결하며 이 장에서 소개된 기능들을 익히자.


## 개선 / 문제풀이
```java
package PS2;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import PS2.Trader;
import PS2.Transaction;

public class Practice {
	public static void main(String args[]) {
		Trader raoul = new Trader("Raoul", "Cambridge");
		Trader mario = new Trader("Mario", "Milan");
		Trader alan = new Trader("Alan", "Cambridge");
		Trader brian = new Trader("Brian", "Cambridge");

		List<Transaction> transaction = Arrays.asList(
				new Transaction(brian, 2011, 300),
				new Transaction(raoul, 2012, 1000),
				new Transaction(raoul, 2011, 400),
				new Transaction(mario, 2012, 710),
				new Transaction(mario, 2012, 700),
				new Transaction(alan, 2012, 950)
				);

		// 1. 2011년에 일어난 모든 트랜잭션을 찾아 값을 오름차순으로 정리하시오.
		List<Transaction> inEleven = transaction.stream()
					.filter(eleven -> transaction.getYear() == 2011)
					.sorted(Transaction::getValue)
					.collect(Collectors.toList());
		System.out.println(inEleven);

		//제네릭으로 트랜잭션 타입이 선언되어 있으므로 컨텍스트에 eleven을 넣고 2011과 연도가 일치하는 대상을
		//값에 따라 정렬하게 했다.
		//처음에는 eleven에 transaction과 같은 이름을 주었는데 스트림 내 람다식에서 한 번 사용된 파라미터는
		//재선언될 수 없다는 에러가 떠서 eleven으로 바꿨다.
		//정답
		List<Transaction> tr2011 = transaction.stream()
					.filter(eleven -> transaction.getYear() == 2011)
					.sorted(comapring(Transaction::getValue))
					.collect(toList());
		System.out.println("정답 -> " + tr0211);


		// 2. 거래자가 근무하는 모든 도시를 중복 없이 나열하시오.
		List<String> cities = transaction.stream()
					.map(city -> transaction.getTrader().getCity())
					.distinct()
					.collect(Collectors.toList());
		System.out.println(cities);

		//거래자의 도시를 요소로 뽑아 distinct()로 중복을 제거한 뒤 최종 연산인 collect로 마무리했다.
		//정답
		List<String> citiesRight = transaction.stream()
					.map(rightCity -> transaction.getTrader().getCity())
					.distinct()
					.collect(toList());

		System.out.println("정답 -> " + citiesRight);
		// 3. 케임브리지에서 근무하는 모든 거래자를 찾아서 이름순으로 정렬하시오.
		List<String> names = transaction.stream()
					.filter(peopleInCambridge -> transaction.getTrader().getCity() == "Cambridge")
					.map(trader -> transaction.getTrader().getName())
					.sorted(Transaction::getName)
					.collect(Collectors.toList());
		System.out.println(names);

		//sorted(comapring(Trader::getName))?
		//equals() 메서드를 잘 활용하자.
		//정답
		List<Trader> nameRight = transaction.stream()
					.map(Transaction::getTrader)
					.filter(trader -> trader.getCity().equals("Cambridge"))
					.distinct()
					.sorted(comparing(Trader::getName))
					.collect(toList());

		System.out.println("정답 -> " + nameRight);
		// 4. 모든 거래자의 이름을 알파벳 순으로 정렬해서 반환하시오.
		List<String> namesAlphaOrder = transaction.stream()
					.map(nameAlpha -> transaction.getTrader().getName())
					.sorted()
					.collect(toList());
		System.out.println(namesAlphaOrder);

		//sorted()의 디폴트값이 알파벳 오름차순 정렬이라고 한다.
		//정답
		String nameAlphaRight = transaction.stream()
					.map(alpharight -> transaction.getTrader().getName()) //모든 거래자명을 문자열로 추출
					.distinct()
					.sorted()
					.reduce("", (n1, n2) -> n1 + n2); //이름을 알파벳 순으로 정렬


		// 5. 밀라노에 거래자가 있는가?
		Optional<String> isInMilan = transaction.stream()
					.filter(tr -> tr.getTrader().getCity() == "Milan")
					.findAny()
					.ifPresent(milan -> System.out.println(milan.getTrader.getName()));

		//값이 없을 경우에 대비하려면 Optional을 쓰라고 해서 썼다.
		//그런데 역시 불리언이 맞았다.
		//정답
		boolean milanBased = transaction.stream()
				.anyMatch(milanRight -> transaction.getTrader().getCity().equals("Milan"));
		//anyMatch에 프레디케이트를 전달해서 밀라노에 거래자가 있는지 확인하는 코드라고 한다.

		// 6. 케임브리지에 거주하는 거래자의 모든 트랜잭션값을 출력하시오
		List<Transaction> transactionInCamb = transaction.stream()
					.filter(cambMen -> cambMen.getTrader().getCity() == "Cambridge")
					.collect(toList());
		System.out.println(transactionInCamb);

		//정답
		transaction.stream()
		.filter( t -> "Cambridge".equals(t.getTrader().getCity()))
		.map(Transaction::getValue)
		.forEach(System.out::println);


		// 7. 전체 트랜잭션 중 최댓값은 얼마인가?
		int max = transaction.stream()
			.map(Transaction::getValue)
			.filter( (a, b) -> a > b ? a : b)
			.max();
		System.out.println("max -> " + max);

		//정답
		Optional<Integer> highestValue = transaction.stream()
						.map(Transaction::getValue)
						.reduce(Integer::max);

		// 8. 전체 트랜잭션 중 최솟값은 얼마인가?
		int min = transaction.stream()
			.map(Transaction::getValue)
			.filter((c, d) -> c < d ? c : d)
			.min();
		System.out.println("min -> " + min);

		//정답
		Optional<Transaction> smallTransaction = transaction.stream()
												.reduce((t1, t2) -> t1.getValue() < t2.getValue() ? t1 : t2);

	}
}
```
전반적으로 초반 문제는 맞은 것 같지만 뒤로 갈 수록 정답과 코드가 조금씩 다르다.  
같은 폴더 안에 폴더 명으로 패키지를 써주고 import 로 참조 클래스들도 넣어주는데 터미널에서 컴파일시  
에러가 발생했다.    
복습할 때 다시 풀어보고 컴파일까지 제대로 해보자.  

## 기본형 특화 스트림
기본형 특화 스트림은 박싱 비용을 최소화할 수 있는 스트림을 말한다. 예를 들어 reduece()를 통해  
전체의 합을 구하는 스트림을 작성하면 해당 스트림은 Integer를 기본형으로 언박싱하게 된다.  
이런 언박싱 비용을 없애기 위해 스트림 API는 IntStream, DoubleStream, LongStream 등 기본형에 특화된  
스트림을 제공한다. 또한 각각의 인터페이스는 합계를 위한 sum, 최댓값을 위한 max와 같이 숫자 관련  
리듀싱 연산 메서드를 제공한다. 또 필요할 때 다시 객체 스트림으로 복원하는 기능도 제공한다.  
특화 스트림은 오로지 박싱 과정에서 일어나는 효율성에만 관련있으며 그 밖의 특별한 기능은 지원하지 않는다.    

#### 숫자 스트림으로 매핑과 객체 스트림으로의 복원
```java
int calories = menu.stream() // Stream<Dish> 반환
		.mapToInt(Dish::getCalories) // IntStream 반환
		.sum();
```
  
어떤 이유로 기본값으로 매핑한 스트림을 다시 매핑해 숫자가 아닌 다른 값을 반환하게 하고 싶으면 다음과  
같이 할 수 있다.
  
```java
IntStream intStream = menu.stream().mapToInt(Dish::getCalories);
Stream<Integer> stream = intStream.boxed();
```

또한 Optional에도 이런 기본값 특화 스트림 조건을 달 수 있다.

```java
OptionalInt maxCalories = menu.stream()
			.mapToInt(Dish::getCalories)
			.max();

int max = maxCalories.orElse(1);
```
이렇게 하면 값이 없을 때 기본 최댓값을 명시적으로 정해줄 수 있다.

#### 숫자 범위

for문을 통한 순회는 순회의 범위를 직접 정해줄수 있다. 스트림에도 이와 같은 기능이 있다.  
IntStream과 LongStream은 range와 rangeClosed라는 정적 메서드를 제공한다.  
range(a, b)는a초과 b미만, rangeClosed(a, b) 는 a 이상 b 이하이다.
```java
IntStream evenNumbers = IntStream.rangeClosed(1, 100) // [1, 100]
			.filter(n -> n % 2 == 0);
System.out.println(evenNumbers.count());
```

#### 스트림 만들기
다양한 방식과 값으로 스트림을 만들 수 있다.

*값으로 스트림 만들기*  
```java
Stream<String> stream = Stream.of("Modern", "Java", "In", "Action"); stream
				.map(String::toUpperCase)
				.forEach(System.out::println);

Stream<String> stream = Stream.empty();
```
위 코드는 임의의 수를 인수로 받는 정적 메서드 Stream.of를 이용해 문자열 스트림을 만든다.  
이후 empty()메서드를 통해 스트림을 비운다.  

*nullable 객체로 스트림 만들기*  
null값을 가질 수 있는 객체를 스트림으로 만들어야 할 수도 있다. 이 때는 널 체크를 해줘야 하는데  
System.getProperty가 제공된 키에 대응하는 속성이 없으면 null을 반환하게 한다.  
이런 메소드를 사용하려면 항상 null을 명시적으로 확인해야 한다.  
```java
String homeValue = System.getProperty("home");
Stream<String> homeValueStream = homeValue == null ? Stream.empty() : Stream.of(value);

Stream<String> homeValueStream = Stream.ofNullable(System.getProperty("home"));

Stream<String> values = Stream.of("config", "home", "user")
			.flatMap(key -> Stream.ofNullable(System.getProperty(key)));
```

*배열로 스트림 만들기*  
배열을 인수로 받는 정적 메서드 Arrays.stream이 있다.
```java
int[] numbers = {2,3,4,5,6,7};
int sum = Arrays.stream(numbers).sum();
```
sum은 27이 된다.

*파일로 스트림 만들기*  
파일에서 특정 문자열을 뽑아내거나 파일을 대상으로 액션을 취할 때 이를 활용할 수 있다.  
파일을 스트림으로 만들어 활용할 수 있다는 정도로만 기억했다가 필요할 때 다시 보자.  

*함수로 무한 스트림 만들기*    
Stream.iterate와 Stream.generate를 이용해 무한 스트림을 만들 수 있다.  
고정되지 않은 크기의 스트림을 만들어 요청할 때마다 무제한으로 값을 계산할 수 있다.  
일반적으로 limit(n)을 연결해 제한을 둔다.  
```java
Stream.iterate(0, n -> n + 2)
	.limit(10)
	.forEach(System.out::println)
```
iterate와 limit을 이용해 0부터 10개의 짝수를 출력하는 예시이다. 
  
```java
Stream.generate(Math::random)
	.limit(5)
	.forEach(System.out::println);
```
iterate와 generate의 차이는 요구한 값을 연속적으로 계산하는지 개별적으로 계산하는지의 차이다.  
위의 코드에서 generate는 0~1 사이의 개별적인 소수를 생산한다.


### 정리
스트림 API에 대한 활용과 예시의 분량이 상당한 장이었다.  
반복학습도 중요하지만 직접 사용하면서 익히자.
