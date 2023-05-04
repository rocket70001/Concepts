# Thread (0504~ing)

## 개념
프로세스를 담당하는 한 단위. 병렬 처리를 위해 다중 스레드를 활용하기도 한다. 
스레드 등장 이전에는 프로세스가 CPU 연산의 한 단위였으나 스레드 등장 이후 스레드가 한 단위가 됐다.  
또한 메모리 구조상 싱글 코어 프로세스는 스택 영역이 단일했으나 멀티 스레딩 프로세스는  
스택 영역을 분할해 각 스레드마다 할당한다(각각의 스택포인터). 하지만 힙 영역은 멀티 스레드라도 공유한다.   
멀티 태스킹, 멀티 스레딩, 멀티 프로세싱은 각각 다르다. 확실히 구별하자.

## 스레드의 구현과 실행
Thread를 상속받거나 Runnable 인터페이스를 구현하거나 두 가지로 나뉜다.  
일반적으로 상속은 하나만 받을 수 있고 인터페이스는 여러 개 구현할 수 있기에 인터페이스 구현이 권장된다.  
```java
//1.Thread 상속
class MyThread extends Thread {
	public void run() {/* 작업 내용 */};
}

//2.Runnable 인터페이스 구현
class MyThread implements Runnable {
	public void run() {/* 작업 내용 */};
}
```
```java
import java.util.*;

class ThreadEx1 {
	public static void main(String args[]) {
		ThreadEx1_1 t1 = new ThreadEx1_1();

		Runnable r = new ThreadEx1_2();
		Thread t2 = new Thread(r);

		t1.start();
		t2.start();
	}
}

class ThreadEx1_1 extends Thread {
	public void run() {
		for(int i = 0; i < 5; i++)
			System.out.println(getName());
	}
}

class ThreadEx1_2 implements Runnable {
	public void run() {
		for(int i = 0; i < 5; i++)
			System.out.println(Thread.currentThread().getName());
	}
}
```
위와 같이 스레드를 상속받은 클래스를 객체로 사용하는 경우와 Runnable을 구현한 클래스를 사용하는 방법이 다르다.   
스레드를 구현한 클래스는 바로 객체화해서 사용할 수 있지만 Runnable을 구현한 경우 Runnable 객체를 만든 뒤  
이를 다시 스레드 클래스 생성자의 매개변수로 제공해야 한다.  
다음 코드에서 이유를 알 수 있다. 
```java
public class Thread {
	private Runnable r;

	public Thread(Runnable r) {
		this.r = r;
	}

	public void run() {
		if(r != null)
			r.run();
	}
	//...생략
}
```
위 코드는 스레드 클래스를 이해하기 쉽게 작성한 것이다. 
스레드 클레스의 내부에는 자체적으로 Runnable을 생성자의 매개변수로 받기 위한 변수가 선언되어 있다.  
만약 Runnable 객체를 받아 만들어진 Thread 객체를 이용한다면 r이 null이 아니므로 run() 메서드를 사용할 수 있다.  
  
이제 첫번째 코드로 돌아가 코드를 다시 보면 
```java
class ThreadEx1_1 extends Thread {
	public void run() {
		for(int i = 0; i < 5; i++)
			System.out.println(getName());
	}
}

class ThreadEx1_2 implements Runnable {
	public void run() {
		for(int i = 0; i < 5; i++)
			System.out.println(Thread.currentThread().getName());
```
두 부분에서 출력에 차이가 나는 이유를 알 수 있다. ThreadEx1\_1는 상속받은 슈퍼 Thread의 메서드인 getName()을   
그대로 가져다 쓸 수 있지만, Runnable을 구현한 ThreadEx1\_2의 멤버는 run() 뿐이므로 부모 클래스를 불러와  
Thread.currentThread().getName()와 같은 방식으로 스레드의 이름을 호출해야 한다.  
첫번째 코드를 컴파일하고 실행해보면 Thread-0, Thread-1과 같은 이름이 출력된다.  
이는 스레드의 디폴트 네임이다. 스레드를 좀 더 의미있게 구분하고 싶다면 다음과 같은 생성자나 메서드를 이용해 스레드에 이름을 지을 수 있다.  
```java
Thread(Runnable target, String name)
Thread(String name)
void setName(String name)
```

#### 스레드의 실행 - start()
스레드는 실행 대기 상태에 있다가 자기 차례가 되어야 실행된다. 실행순서는 OS의 스케줄러에 의해 결정된다.  
또한 하나의 스레드에 대해서는 한 번의 호출만 가능하다. 같은 스레드를 재생성하지 않고 두 번 호출하면 에러가 발생한다.  
```java
ThreadEx1_1 t1 = new ThreadEx1_1();
t1.start();
// t1.start(); 호출 시 IllegalThreadStateException!
// 재호출하려면 재생성한다.
t1 = new ThreadEx1_1();
t1.start(); //OK
```

## start()와 run()

