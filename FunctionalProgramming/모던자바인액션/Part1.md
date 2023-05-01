# 1부 - 기초

## 1장 - 자바 8, 9, 10, 11 : 무슨 일이 일어나고 있는가?
자바 8은 다중 코어 CPU가 상용화되는 하드웨어 시장 상황에 맞춰<br>
병렬성 처리/관리에 유용한 스트림 기능을 추가했다.<br>
  
스트림은 데이터베이스 질의어 처럼 고수준으로 쓰고 저수준에서 동작하는 기능을 지원한다.<br>
저자는 결국 이 스트림API 덕에 메서드 참조와 람다 표현, 인터페이스의 디폴트 메서드가 가능해졌다고 말한다.<br>
객체지향형 언어인 자바에서 함수형 프로그래밍 기법을 사용할 수 있게 된 것이다.<br>
  
**스트림** - 한 번에 한 개씩 만들어지는 연속적인 데이터 항목들의 모임<br>
자바 8의 java.util.stream 패키지에 추가된 스트림API는 유닉스 명령어로 파이프라인을 구축하듯<br>
파이프라인을 만드는 데 필요한 다양한 메서드를 제공한다. 스트림 메서드를 사용하면 스레드를 사용하지<br> 
않으면서도 병렬성을 얻을 수 있다.<br>
  
**동작 파라미터화** - 코드를 매개변수로 전달함. 달리 말하면 연산의 동작을 매개변수로 넘겨줌.  
  
순수함수, 부작용 없는 함수, 상태 없는 함수(Pure, Side-Effect-Free, Statless)<br>
synchronized를 이용해 공유된 가변 데이터를 보호하며 병렬처리할 수 있지만 일반적으로<br>
synchronized는 시스템 성능에 악영향을 미친다. 함수적 프로그래밍의 순수함수는 데이터를<br>
이런 때 안정성을 보장한다.
  
책에서는 메서드와 함수를 구분한다. 함수는 수학적 함수처럼 사용된 뒤에 부작용을 일으키지<br>
않아야 한다.

``` java
File[] hiddenFiles = new File(".").listFiles(new FileFilter() {
		pulbic boolean accept(File file) {
			return file.isHidden();
		}
	}
```
위 코드는 숨겨진 파일을 필터링하는데 쓰이는 코드이다. file.isHidden()을 리턴해<br>
불리언 값을 반환하기 위해 FileFilter를 인스턴스화하는 절차가 필요하다. 자바 8에서는 이를 다음과 같이 간소화한다.<br>
``` java
File[] hiddenFiles = new File(".").listFiles(File::isHidden);
```
::는 메서드 참조 기능으로 '이 메서드를 값으로 사용하라'는 의미를 가진다.  
메서드를 일급값처럼 사용할 수 있게 된 것이다.  
  
**프레디케이트(predicate)**  
수학에서 인수로 값을 받아 트루나 폴스를 반환하는 함수를 프레디케이트라고 한다.<br>
Function(Apple, Boolean)과 같은 식으로 불리언을 반환할 수도 있지만 Predicate\<Apple\>를<br>
사용하는 게 더 표준적이다.(boolean을 Boolean으로 변환하는 과정이 없어 더 효율적이다.)<br>
  
특정 항목을 선택해 반환하는 동작을 **필터(Filter)** 라고 한다. 자바 8이전에는 다음과 같이<br>
Filter를 구현했다.<br>
``` java
public static List<Apple> filterGreenApples(List<Apple> inventory) {
	List<Apple> result = new ArrayList<>(); //점점 녹색 사과로 채워진다.

	for (Apple apple : inventory) {
		if(GREEN.equals(apple.getColor())) {
			result.add(apple);
		}
	}
	return result;
}
```
위 코드와 같이 녹색 사과를 인벤토리 리스트에서 골라 result 리스트에 담아 반환하는 함수가 있다.<br>
만약 이 함수를 녹색 사과가 아닌 무게가 150g 이상인 사과를 고르는 함수로 필터링하고 싶다면 <br>
if문을 바꾸기 위해 코드를 수정하거나 if문을 바꾼 새 메서드를 만들어야 한다.<br>

자바 8에서 추가된 기능을 활용하면 위 코드를 다음과 같이 바꿀 수 있다.
``` java
public static boolean isGreenApple(Apple apple) {
	return GREEN.equals(apple.getColor());
}

public static boolean isHeavyApple(Apple apple) {
	return apple.getWeight() > 150;
}

public interface Predicate<T> {
	boolean test(T t);
}

static List<Apple> filterApples(List<Apple> inventory, Predicate<Apple> p) {
	List<Apple> result = new ArrayList<>();
	for(Apple apple : inventory) {
		if(p.test(apple)) {
			result.add(apple);
		}
	}
	return result;
}

filterApples(inventory, Apple::isGreenApple);
filterApples(inventory, Apple::isHeavyApple);

```
위 코드는 이전 코드보다 재사용과 유지보수에 유리하다.  
코드의 동작 원리에 대해서는 2장과 3장에서 자세히 설명한다고 한다.  
일단 객체로 감싼 메서드를 사용해야 했던 이전 코드와는 달리 메서드를 직접 매개변수로 넘겨  
프레디케이트라는 인터페이스의 파라미터로 전달했고, 그렇게 하니 코드가 간결해지고 유지보수성이  
늘어났다는 사실에 주목하자.  
만약 사과의 조건을 판별하는 메서드를 별도로 구현하지 않고 람다식으로 표현했다면  
아래와 같이 구현할 수도 있다.
```java
filterApples(inventory, (Apple a) -> GREEN.equals(a.getColor()));
filterApples(inventory, (Apple a) -> a.getWeight() > 150);
filterApples(inventory, (Apple a) -> a.getWeight() < 80 || RED.equals(a.getColor()));
```
한 번만 사용할 메서드는 구현할 필요없이 이렇게 람다식으로 간편하게 사용할 수 있다.  
하지만 람다의 연산이 길어지면 연산을 모두 읽고 이해해야 전체적인 함수의 내용을 이해할 수 있다.(람다는 익명이기때문)  
이런 람다식이 많아지면 코드가 난해해질 수 있으니 이런 상황에는 이름을 가진 메서드를 구현해 활용하는 게 바람직하다.  

### 스트림
```java
Map<Currency, List<Transaction>> transactionByCurrencies = new HashMap<>();
for (Transaction transaction : transactions) {
	if(transaction.getPrice() > 1000) {
		Currency currency = transaction.getCurrency();
		List<Transaction> transactionsForCurrency = transactionByCurrencies.get(currency);
		if(transactionsForCurrency == null) {
			transactionsForCurrency = new ArrayList<>();
			transactionByCurrencies.put(currency, transactionsForCurrency);
		}
		transactionsForCurrency.add(transaction);
	}
}
```

위 코드는 1000 이상의 트랜잭션을 필터링해 통화로 그룹화한다.  
중첩된 제어 흐름이 많아 코드가 직관적이지 않다.  
스트림 API를 활용해 다음과 같이 할 수 있다.  
```java
import static java.util.stream.Collectors.groupingBy;
Map<Currency, List<Transaction>> transactionsForCurrency = transaction.stream()
							.filter((Transaction t) -> t.getPrice() > 1000)
							.collect(groupingBy(Transaction::getCurrency));
```
스트림 API를 활용하면 이처럼 컬렉션 API와는 다른 방식으로 데이터를 처리할 수 있다.  
컬렉션에서는 for-each루프를 이용했는데 이런 반복을 외부 반복이라고 한다.  
스트림API에서 사용한 방식은 루프를 쓰지 않고 라이브러리 내부에서 모든 데이터가 처리된다.  
이는 내부반복을 사용한 것이며 여기에 대해서는 4장에서 설명한다.  

데이터 filtering, extraction, grouping과 같은 작업에 있어 반복되는 작업을 효율적으로 처리하기 위해  
스트림을 사용할 수도 있다. 

### 디폴트 메서드와 자바 모듈
인터페이스에 새 메서드를 추가하면 인터페이스를 구현하는 모든 클래스는 새로 추가된 메서드를  
구현해야만 한다. 어떻게 하면 기존의 구현을 고치지 않고 이미 공개된 인터페이스를 변경할 수 있을까?  
이러한 문제의식에 대한 해답이 디폴트 메서드이다. 
```java
default void sort(Comparator<? super E> c) {
	Collections.sort(this, c);
}
```
자바 8 이전까지는 모든 List를 구현하는 클래스가 sort를 구현해야 했지만 8 이후부터는  
디폴트 sort를 구현하지 않아도 된다.

### 정리
자바 8에 적용된 함수형 프로그래밍 패러다임은 기존의 자바에 두 가지 핵심적인 기능을 더했다.  
1. 함수를 일급값으로 사용할 수 있다.
2. 가변 공유 상태가 없는 병렬 실행을 이용할 수 있다.
  
-> Stream API 

