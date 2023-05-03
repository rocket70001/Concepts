# 06 직접 만드는 Functional Interface

함수형 인터페이스는 추상 메서드 하나만을 가지는 인터페이스이다.  
람다 표현식을 활용한 동작 파라미터화가 익명 클래스를 대체  
@FunctionalInterface 어노테이션이 갖는 의미  
- 위 어노테이션이 있으면 컴파일 시에 함수형 인터페이스를 확실히 체크한다.  
만약 추상 메서드가 하나가 아닌 여럿이 있으면 컴파일 에러를 내서 예기치못한 상황을 예방할 수 있다.  
스태틱과 디폴트 메서드는 여럿이 있어도 상관없다.  

```java
@FunctionalInterface
interface printTriple< A1, A2, A3, R> () {
	R apply(A1 a1, A2 a2, A3 a3);
}
```
위 함수형 인터페이스를 만들어서
```java
private static <A1, A2, A3> void print(A1 a1, A2 a2, A3 a3, printTriple<A1, A2, A3, String> triple) {
	System.out.println(triple.apply(a1, a2, a3));
}
```
와 같이 구현한 뒤 메인함수에서 print로 사용할 수 있다. 이때 print의 네 번째 매개변수로 동작 파라미터화한  
람다식을 넣어주면 원하는 문자열에 a1, a2, a3를 출력하는 유연한 함수를 작성할 수 있다.

```java
public static void main(String args[]) {
	print("abc0103", "Green", "idlemoments@email.com", 
			(id, name, email) -> "User id is : " + id, Name is : " + name, "Email : " + email);
}
```

