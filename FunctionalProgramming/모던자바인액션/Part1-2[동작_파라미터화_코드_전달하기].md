# Part1-2 동작 파라미터화 코드 전달하기

동작 파라미터화란 말 그대로 동작을 매개변수로 전달하는 것이다.<br>
사용자의 요구가 자주 변화하는 메서드에 사용하면 좋다.<br>

```java
public static List<Apple> filterGreenApples(List<Apple> inventory) {
	List<Apple> result = new ArrayList<>();
	for(Apple apple: inventory) {
		if(GREEN.equals(apple.getColor()) {
			result.add(apple)
		}
	}
	return result;
}
```
위 코드에서 사용자가 녹색 사과 뿐만 아니라 붉은 색, 노란 색, 150그램 이상의 사과 등을<br>
선별해 재고에 넣길 원한다면 여러 방법을 고려해볼 수 있다.  
동작 파라미터화를 쓸 수 없다면 조건문의 조건식을 바꾼 메서드를 추가하거나 메서드를 수정해  
if-else을 반복해야 한다. 이런 식의 수정에는 복사-붙여넣기로 인한 오류 발생 가능성과 가독성 저하,  
 길고 지저분한 코드, 불필요한 연산 등이 수반된다. 따라서 이런 상황에는 다음과 같은 규칙을 따른다.  
  
 **거의 비슷한 코드가 반복된다면 그 코드를 추상화한다.**
   
동작 파라미터화와 전략 디자인 패턴은 좋은 짝이다. 전략 디자인패턴은 알고리즘 묶음을  
캡슐화해 런타임에 알고리즘을 선택하는 기법이다. 참 또는 거짓을 반환하는 함수인 프레디케이트로  
선택 조건을 결정하는 인터페이스를 정의하고 filterApples에서 프레디케이트의 객체를 받아  
사과의 조건을 검사하도록 메서드를 수정할 수 있다. 이와 같이 filterApples 메서드가 ApplePredicate 객체를  
인수로 받도록 고치면 filterApples 내부에서 컬렉션을 반복하는 로직과 컬렉션의 각 요소에 적용할 동작을  
분리할 수 있어서 소프트웨어 공학적으로 큰 이득을 얻는다.  
```java
public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {
	List<Apple> result = new ArrayList<>();
	for(Apple apple : inventory) {
		if(p.test(apple)) {
			result.add(apple);
		}
	}
	return result;
}
```
불리언 값 자체를 매개변수로 전달하는 일은 지양해야 한다. 코드가 길어지면 불리언 값 자체만으로는 값이 무슨  
일을 하는지 알 수 없으며 이는 다른 사람이 코드를 수정하기 어렵게 만든다.  
위와 같이 코드를 수정하면 다양한 ApplePredicate를 만들어 filterApples 메서드로 전달할 수 있다.
```java
public interface ApplePredicate {
	boolean test(Apple apple);
}

public class AppleRedAndHeavyPredicate implements ApplePredicate {
	public boolean test(Apple apple) {
		return RED.equlas(apple.getColor()) && apple.getWeight() > 150;
	}
}

List<Apple> redAndHeavyApples = filterApples(inventory, new AppleRedAndHeavyPredicate());
```
위 코드는 불리언 표현식을 캡슐화해 사과를 필터링한다. 좋은 방법이지만 한편으로는  
메서드의 몸통을 객체화해 파라미터로 전달하기 위해 클래스와 메서드의 선언부를 모두 작성한 것이  
불필요하게 느껴질 수 있다. 이때 람다 표현식을 사용할 수 있다. 이는 3장에서 자세히 다룬다.  

프레디케이트 인터페이스를 만들어 구체 프레디케이트 클래스로 만든 뒤 파라미터로 넘겨주는  
과정을 살펴보았다. 복사-붙여넣기로 여러 메서드를 만드는 방식보다는 분명 나은 방식이지만 그럼에도  
로직과 관련 없는 코드가 많이 추가됐다. 이때 람다식 사용 전에 익명 클래스의 사용을 고려해볼 수도 있다.  
  
익명 클래스는 지역 클래스와 비슷하다. 즉석에서 필요한 구현을 만들어 사용할 수 있으며  
 클래스 선언과 인스턴스화를 동시에 할 수 있다.
```java 
List<Apple> redApples = filterApples(inventory, new ApplePredicate() {
		public boolean test(Apple apple) {
			return RED.equals(apple.getColor());
		}
	});
```
이렇게 파라미터에 직접적으로 익명 클래스를 선언할 수 있다.  
  
다음은 람다식의 사용이다.
```java
List<Apple> result = filterApples(inventory, (Apple apple) -> RED.equals(apple.getColor()));
```
훨씬 간결해졌다.  
  
지금까지 구체 클래스 -> 익명 클래스 -> 람다식의 사용 순서로 동작 파라미터화를 알아보았다.  
동작 파라미터화는 값 파라미터화에 비해 유연하게 활용할 수 있다.  

리스트로 추상화하는 방법  
```java
public interface Predicate<T> {
	boolean test(T t);
}

public static <T> List<T> filter(List<T> list, Predicate<T> p) {
	List<T> result = new ArrayList<>();
	for(T e: list) {
		if(p.test(e)) {
			result.add(e);
		}
	}
	return result;
}

List<Apple> redApples = filter(inventory, (Apple apple) -> RED.equals(apple.getColor()));
List<Integer> evenNumbers = filter(numbers, (Integer i) -> i % 2 == 0);
```
지네릭스와 람다를 활용해 다양한 타입에 필터 메서드를 적용할 수 있다.  
매우 유연하고 간결하다.  
  
## 동작 파라미터화의 실제 활용
1. Comparator로 정렬하기
```java
// java.util.Comparator

public interface Comparator<T> {
	int compare(T o1, T o2);
}
```
위와 같은 인터페이스를 갖는 java.util.Comparator 객체를 이용하면 sort의 동작을 파라미터화  
할 수 있다. 다음 예시에서는 익명 클래스를 사용해 무게가 적은 순서대로 사과를 정렬한다.  
```java
inventory.sort(new Comparator<Apple>() {
		public int compare(Apple a1, Apple a2) {
			return a1.getWeight().compareTo(a2.getWeight());
		}
	});
```
만약 사용자가 사과를 무게 기준이 아닌 원산지를 기준으로(서울-대전-대구-부산 순으로) 정렬하길 원한다면  
다음과 같은 람다식으로 수정할 수 있다.  
```java
inventory.sort((Apple a1, Apple a2) -> a1.getOrigin().compareTo(a2.getOrigin()));
```
정렬에 대한 세부 사항을 추상화되어 있으므로 신경 쓸 필요없다.

2. Runnable로 코드 블록 실행하기
  
이 부분은 스레드를 통해 코드를 병렬처리하는 방식을 다룬다.  
람다 표현식을 통해 다음과 같이 스레드 코드를 구현할 수 있다.
```java
Thread t = new Thread(() -> System.out.println("Hello World"));
```
자바 스레드와 병렬 처리에 대한 개념이 부족하므로 해당 개념 학습 후 돌아와 다시 정리하자.

3. Callable을 결과로 반환하기

상동

4. GUI 이벤트 처리하기

상동

### 정리
동작 파라미터화의 핵심 아이디어는 코드를 메서드의 인수로 전달한다는 것.  
변화하는 요구사항에 잘 대응하는 코드는 엔지니어링 비용 감소로 이어진다.  
방법은 크게 세 가지, 인터페이스 선언 후 구체 클래스화-익명 클래스 선언-람다식  
자바 기본 API의 많은 메서드가 정렬, 스레드, GUI 처리 등에 대한 동작 파라미터화를 지원한다.  

### 개선
스레드와 GUI 이벤트 처리, 병렬성에 관해서는 모르기 때문에 이해할 수 없었다.  
해당 개념에 대한 학습이 필요하다.  
병렬성을 제대로 이해하고 활용하려면 컴퓨터 구조 - 운영체제 - 스레드 - 자바 스레드 처리로 이어지는 공부가 필요할 듯 하다.  
또 OOP나 함수형 프로그래밍은 단순한 PS로 연습하기 힘들겠다는 생각이 들었다.  
객체지향과 함수형 프로그래밍을 연습하려면 알고리즘 PS가 아니라 작은 프로젝트를 진행해봐야 할 것 같다.  

