# Part1-3 람다 표현식

## 람다란 무엇인가?
람다 표현식은 클래스에 종속된 메서드가 아닌 별도의 함수를 만들어 활용하는 식이다.  
익명 클래스처럼 이름은 없지만 파라미터, 바디, 반환형식, 예외 리스트를 가질 수 있다.  
람다는 간결하게 표현되며 변수에 할당할 수 있다는 특징이 있다.  

예시를 보자.
```java
Comparator<Apple> byWeight = new Comparator<Apple>() {
	public int compare(Apple a1, Apple a2) {
		return a1.getWeight().compareTo(a2.getWeight());
	}
};
```
람다식을 통해 표현하면 아래와 같다.
```java
Comparator<Apple> byWeight = 
	(Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());
```
람다 표현식은 위와 같이 파라미터 리스트 -> 바디(표현식)으로 구성된다.  
자바 8에서 유효한 람다 표현식은 다음과 같다.
```java
(String s) -> s.length()
(Apple a) -> a.getWeight() > 150
//표현식에 중괄호가 없는 경우 세미콜론을 생략한다.
(int x, int y) -> {
	System.out.println("Result:");
	System.out.println(x + y);
}
//람다식은 위와 같이 중괄호 안에 여러 문장을 둘 수도 있다.
//이 경우 문장 끝에 세미콜론을 붙여야 한다. 
() -> 42
// 파라미터가 없으며 화살표가 가리키는 42를 반환한다.
(Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight())
// 파라미터 두 개를 가지며 int를 반환하는 람다식
```
화살표가 가리키는 대상이 리턴되므로 람다식은 return 타입을 생략할 수 있다.  

(parmeters) -> expression  
  
위와 같은 표현은 표현식 스타일(expression style)이라고 알려진 람다의 기본 문법이다.  
  
(parmeters) -> {statements;}
  
위와 같은 표현은 블록스타일이라고 한다.  

## 람다의 활용
람다는 함수형 인터페이스에서 사용한다.  
함수형 인터페이스란 무엇인가?  
### 함수형 인터페이스
-> 함수형 인터페이스란 정확히 하나의 추상 메서드를 지정하는 인터페이스다.  
지금까지 살펴본 것들 중 Predicate, Comparator, Runnable 등이 함수형 인터페이스였다.  
자바 API의 함수형 인터페이스에는 다음과 같은 것들이 있다.  
```java
public interface Predicate<T> {
	boolean test (T t);
}
public interface Comparator<T> {
	int compare (T o1, T o2);
}
public interface Runnable {
	void run();
}
public interface ActionListener extends EventListener {
	void actionPerforemd(ActionEvent e);
}
public interface Callable<V> {
	V call() throws Exception;
}
public interface PrivilegedAction<T> {
	T run();
}
```
추가적으로 디폴트 메서드가 있더라도 추상 메서드가 오직 하나이면 함수형 인터페이스이다.  
이에 관해서는 9장에서 공부한다.  
이제 이걸로 뭘 할 수 있을까? 람다식으로 함수형 인터페이스의 추상 메서드 구현을 직접 전달할 수 있으므로  
전체 표현식을 함수형 인터페이스의 인스턴스로 취급할 수 있다.  
```java
Runnable r1 = () -> System.out.println("Hello World 1");

Runnable r2 = new Runnable() {
	public void run() {
		System.out.println("Hello World 2");
	}
};

public static void process(Runnable r) {
	r.run();
}
process(r1); // -> Hello World 1 출력
process(r2); // -> Hello World 2 출력
process(() -> System.out.println("Hello World 3")); // -> Hello World 3 출력
```
위 코드에서 r1은 람다식으로 추상 메서드를 구현하고 r2는 익명 클래스를 활용한다.  
마지막으로 r3는 함수형 인터페이스를 인스턴스로 받는 메서드에 직접 람다 표현식을 전달한다.  
당연하지만 함수형 인터페이스의 시그니처(입력, 반환 타입)이 일치하지 않는 람다식은 사용할 수 없다.

### 함수형 인터페이스 사용
함수형 인터페이스의 추상 메서드는 람다 표현식의 시그니처를 묘사한다.  
함수형 인터페이스의 추상 메서드 시그니처를 함수 디스크럽터(function descriptor)라고 한다.  

#### *Predicate*
java.util.function.Predicate\<T\> 인터페이스를 말한다.
test라는 추상메서드를 정의해 제네릭 T의 객체를 인수로 받아 불리언을 반환한다.  
T형식의 객체를 사용하는 불리언 표현식이 필요한 경우 Predicate를 사용한다.  
다음 예제는 String 객체를 인수로 받는 람다를 정의한다.
```java
@FunctionalInterface
public interface Predicate<T> {
	boolean test(T t);
}
public <T> List<T> filter(List<T> list, Predicate<T> p) {
	List<T> results = new ArrayList<>();
	for(T t : list) {
		if(p.test(t)) {
			results.add(t);
		}
	}
	return results;
}
Predicate<String> nonEmptyStringPredicate = (String s) -> !s.isEmpty();
List<String> nonEmpty = filter(listOfStrings, nonEmptyStringPredicate);
```
람다식을 통해 프레디케이트를 구현, 정의하고 필터를 통해 사용한 코드이다.  
문자열이 비어있지 않으면 참값을 반환하는 test메서드를 통해 해당 문자를 리스트에 더하는 코드이다.  

#### *Consumer*
java.util.function.Consumer<T>
예제
```java
@FunctionalInterface
public interface Consumer<T> {
	void accept(T t);
}
public <T> void forEach(List<T> list, Consumer<T> c) {
	for(T t : list) {
		c.accept(t);
	}
}
forEach(
		Arrays.asList(1,2,3,4,5),
		(Integer i) -> System.out.println(i)
);
```
Consumer는 void 반환형 추상 메서드를 가지는 함수형 인터페이스이다.  
따라서 반환없이 특정 이벤트를 수행하도록 구현해서 사용한다.  
위 코드에서는 \<T\> 타입으로 리스트에 Integer가 들어갔고 람다식이 forEach메서드의 동작 파라미터로  
들어가 accept 메서드가 Integer i값을 출력하게 한다.  

#### *Function*
java.util.function.Function\<T, R\>
예제
```java
@FunctionalInterface
public interface Function<T, R> {
	R apply(T t);
}
public <T, R> List<R> map(List<T> list, Function<T, R> f) {
	List<R> result = new ArrayList<>();
	for(T t : list) {
		result.add(f.apply(t));
	}
	return result;
}

// [7, 2, 6]
List<Integer> l = map(
		Arrays.asList("lambdas", "in", "action"),
		(String s) -> s.length()
);
```
Function은 입력\<T\>를 출력 \<R\>로 매핑하는 함수형 인터페이스이다.  
위 예제에서는 람다식을 통해 String을 인수로 받아 String의 길이를 int로 반환한다.

#### 참조형과 기본형이 중요한가?
자바의 모든 타입은 참조형(Byte, Integer, Object, List...)과 기본형(int, double, byte, char...)로 나뉜다.  
하지만 제네릭 파라미터\<T\>에는 참조형만 쓸 수 있다. 이는 제네릭 내부 구현의 한계에서 비롯된다.  
자바는 기본형을 참조형으로 변환하는 기능을 제공한다. -> 박싱(boxing)  
반대로 참조형을 기본형으로 변환할 수도 있다. -> 언박싱(unboxing)  
편리한 구현을 위해 박싱과 언박싱이 자동으로 이뤄지는 오토박싱 기능도 제공한다.
```java
List<Integer> list = new ArrayList<>();
for(int i = 300; i < 400; i++) {
	list.add(i);
}
```
위 코드에서 리스트는 참조형 타입인 Integer을 인자로 가지지만 기본형인  
int i가 들어와도 자동으로 이를 Integer로 박싱해 리스트에 추가한다. 하지만 이런 변환에는 비용이 소모된다.  
박싱값은 기본형을 감싸는 래퍼이며 힙에 저장된다. 따라서 추가적인 메모리를 소비하고 기본형을 가져올 때도  
메모리를 탐색하는 과정이 필요하다.  
  
위와 같은 이유로 자바8은 기본형을 입출력으로 사용하는 상황에서 오토박싱을 피할 수 있는 특별한  
함수형 인터페이스를 제공한다. 이렇듯 특정한 형식을 입력으로 받는 함수형 인터페이스는 헝가리안 표기법처럼  
이름 앞에 자료형을 붙여서 사용한다. -> DoublePredicate, IntConsumer, IntFunction...  
또한 Function인터페이스는 ToIntFunction\<T\>나 IntToDoubleFunction과 같은 다양한 출력 파라미터를 제공한다.  
  
*트레이드 오프 - 메모리 효율성 <-> 제네릭 파라미터 제공을 통한 유연성*
  
### 컴파일러는 람다를 어떻게 이해하는가?
람다로 함수형 인터페이스의 인스턴스를 만들 수는 있지만 람다식은 람다가 어떤 함수형 인터페이스를 구현하는지  
드러내지 않는다. 람다의 실제 형식이 어떻게 되기에 이게 가능한 걸까?

#### 형식 검사
컴파일러는 람다가 사용되는 문맥을 파악해 람다의 형식을 추론할 수 있다.  
어떤 콘텍스트에서 기대되는 람다식의 형식을 대상 형식(target type)이라고 부른다.  
```java
List<Apple> heavierThan150g =
filter(inventory, (Apple apple) -> apple.getWeight() > 150);
```
위 코드의 형식 추론 과정을 살펴보자.
1. filter 메서드의 선언을 확인한다.
2. filter 메서드는 두 번째 파라미터로 Predicate\<Apple\> 형식을 기대한다.
3. Predicate\<Apple\>은 test를 정의하는 함수형 인터페이스이다.
4. test 메서드는 Apple을 받아 boolean을 반환하는 함수 디스크립터를 묘사한다.
5. filter 메서드로 전달된 인수는 이러한 요구사항을 만족해야 한다.

콘텍스트를 이용한 형식 추론의 대표적인 예는 다이아몬드 연산자를 통한 제네릭 형식 추론이다.
```java
List<String> listOfStrings = new ArrayList<>();
List<Integer> listOfIntegers = new ArrayList<>();
```
추가적으로 람다식에 캐스팅을 하는 것 또한 가능하다는 사실을 가볍게 알아두고 넘어가자.  

#### 추가 - 람다식 내 지역변수 사용
람다식에서 인수로 지역변수를 사용할 수도 있다.  
그러나 해당 지역변수는 final 선언이 되었거나 할당 뒤 final처럼 값 변경이 없는 변수여야 한다.  
생각해보자. 람다식 내에서 사용하는 인스턴스 변수는 힙에 저장되고 지역 변수는 스택에 위치한다.  
람다에서 지역변수로 바로 접근할 수 있다는 가정 하에 람다가 스레드에서 실행된다면  
변수를 할당한 스레드가 사라져서 변수 할당이 해제되었는데도 람다를 실행하는 스레드에서 해당 변수에  
접근하려 할 수 있다. 따라서 자바에서는 원래 변수에 접근을 허용하지는 않되 자유 지역 변수의 복사본을  
제공한다. 이에 따라 복사본의 값이 바뀌지 않아야 하므로 지역 변수에는 한 번만 값을 할당해야 한다는 제약이 생겼다.  

### 메서드 참조
메서드 참조란 기존의 메서드 정의를 재활용해 람다처럼 전달하는 것을 말한다.
```java
inventory.sort((Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()));
// 위 코드를 메서드 참조로 바꾸면
inventory.sort(comparing(Apple::getWeight));
```
위와 같이 메서드 참조를 사용하면 훨씬 간결해진다. 메서드 참조는 메서드 이름 앞에 ::를 붙임으로 가능하다.  
실제 메서드를 호출하는 것은 아니므로 괄호가 필요없다.  
메서드 참조는 세 유형으로 나누어진다.  
1. 정적 메서드 참조
Integer의 parseInt 메서드는 Integer::parseInt로 표현된다.
2. 다양한 형식의 인스턴스 메서드 참조
String의 length는 String::length로 표현된다.
3. 기존 객체의 인스턴스 메서드 참조
Transaction 객체를 할당받은 expensiveTransaction 지역 변수가 있고, Transaction 객체에는  
getValue 메서드가 있으면 이를 expensiveTransaction::getValue라고 표현할 수 있다.

또한 Classname::new 처럼 클래스명과 new 키워드를 이용해 기존 생성자의 참조를 만들 수도 있다.  
예를 들어 Supplier의 () -> Apple과 같은 인수가 없는 생성자가 있다고 하면 간단하게
```java
Supplier<Apple> c1 = Apple::new;
```
로 표현할 수 있다.

Predicate 인터페이스의 negate(), and(), or() 메서드와 람다식를 조합해 복잡하면서도 표현적으로는  
명료한 코드를 작성할 수 있다.

### 정리
함수형 인터페이스는 하나의 추상 메서드만을 정의하는 인터페이스이다.  
함수형 인터페이스를 기대하는 곳에서만 람다 표현식을 사용할 수 있으며 이때 제공하는 람다 표현식은  
그 자체로 함수형 인터페이스의 인스턴스가 된다.  
또 Comparator, Predicate, Function과 같은 함수형 인터페이스는 람다 표현식을 조합할 수 있는 다양한  
디폴트 메서드를 제공한다.  

### 개선
실제 프로그램 구현에서 함수형 인터페이스를 활용할 때 코드의 명료성, 오류 발생 확률 감소 외에  
실질적으로 어떤 장점이 있는지 제대로 이해하지 못 함. 특히 코드에서 데이터베이스로 데이터 입출력,  
GUI 관련 이벤트 발생 등 직접 써보지 않은 기능들에 람다식을 적용할 때 이해도가 떨어졌음.  
이 부분은 직접 프로젝트 진행 후 다시 봐야 제대로 된 이해가 가능할 것 같다.  
6월에는 ps와 알고리즘 공부에 전념하려 했는데 그것보다 실제 프로젝트 진행을 우선시해야 한다.  
6월에 아주 기초적인 프로젝트라도 진행할 수 있도록 5월에 자바스크립트 공부와 관련 배경 지식을 갖춰놓자.
