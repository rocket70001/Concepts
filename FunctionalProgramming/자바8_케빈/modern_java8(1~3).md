## 모던자바8 - 케빈TV(1~3)
01. 함수형 프로그래밍을 왜 배워야 하나?<br>
스트림 사용 시 이점 -> 코드를 명료화하고 중복되는 연산을 피할 수 있다. 읽기에도 직관적이다.<br>
로버트.C.마틴 -> Single Resposibility Principle의 오해 중 하나는 함수가 한 가지 기능만 해야 한다는 오해<br>
엄밀히 말하면 "한 번에 한 가지 일을 해야만 한다." 보다 "한 가지 이유로만 수정되어야 한다."이다.<br>
함수형 프로그래밍 언어는 일등 시민(First Class Citizen)으로 함수 자체를 변수나 배열, 매개변수로<br>
사용 가능한 언어를 말한다. 객체 지향 언어인 자바의 일등 시민은 원래 오브젝트 뿐이었다.<br>
그러나 자바8부터 의존성 주입을 통한 함수 활용 외에도 람다식을 통해 함수를<br>
매개변수로 쓸 수 있게 됐다. 이전에 메서드의 매개변수로 메서드를 넣는 방식은 매개변수가 해당 메서드의<br>
로직에 관여하지 않고 이벨류에이션된 리턴 값만을 반환했을 뿐이다. 그러나 함수의 매개변수로<br>
함수 자체를 넣어줌으로써 매개변수가 메서드의 로직에 관여할 수 있는 환경이 마련되었다.<br>
만약 람다식이 조금 복잡해지거나 직관적이지 않다면 변수에 람다식을 할당해 사용하는 방법도 고려할만 하다.<br>

02. Function, The Transformer<br>
펑션 인터페이스, 인터페이스 안에 메서드가 하나 뿐이면 따로 구현하지 않고 바로 오버라이딩해서 사용한다.<br>
펑션 인터페이스는 람다 표현식으로 사용할 수도 있다.<br>
아이덴티티 펑션은 입력값과 출력값의 타입과 값 자체가 모두 동일한 펑션을 말한다.<br>
입출력값과 타입에 변함이 없는 펑션을 어디에 쓸까? -> 매개변수 단계에서 연산을 필요로 하지 않을 때<br>
아이덴티티 함수를 사용하면 함수가 메서드 내부 로직에서만 연산되도록 사용할 수 있다.<br>

03. Consumer, The Spartan, Give Them Nothing but Take from Them Everything<br>
컨슈머는 반환값이 void일 때 사용 가능하다. 위의 펑션의 반환값을 보이드로 하고 사용하면 에러가 난다.<br>
``` java
final Consumer<String> printValue = value -> System.out.println(value);
final Consumer<String> greetVisitor = value -> System.out.println("Hello " + value);

printValue.accept(Paul);
printValue.accept(Paul);
```
실행결과<br>
Paul<br>
Hello Paul<br>
