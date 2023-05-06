# Part2-4 병렬 데이터 처리와 성능
자바 7 이전까지는 데이터 컬렉션을 병렬 처리하기 어려웠다.  
사용자는 데이터를 부분집합으로 분해해 각각 다른 스레드에 할당해야 했다. 그리고 레이스 컨디션이 발생하지  
않도록 적절히 동기화를 추가한 뒤 최종적으로 나눴던 부분집합을 하나의 결과로 합쳐야 했다.  
자바 7은 이보다 나은 포크/조인 프레임워크를 제공해 쉬운 병렬화와 에러의 최소화를 내세웠다.  
그러나 스트림을 통한 병렬화는 위의 두 과정보다 간단하다. 이 장에서는 스트림을 통한 병렬 처리가 어떻게 되는지 배운다.  
병렬 스트림이 내부적으로 어떻게 처리되는지 알아보고, 이를 통해 스트림을 바르게 사용하는 법을 배울 것이다.
  
## 병렬 스트림
병렬 스트림은 컬렉션의 parallelStream을 호출하면 생성된다. 병렬 스트림은 스트림 요소를 서로 다른 스레드에서  
처리할 수 있도록 여러 청크로 분할한 스트림이다.  
앞서 책과 영상을 통해 무한한 길이의 스트림을 만들 수 있음을 배웠다.  
다음은 숫자 n을 인수로 받아 1부터 n까지 모든 숫자의 합을 반환하는 메서드이다.  
```java
pulbic long iterativeSum(long n) {
	long result = 0;
	for(int i = 1L; i <= n; i++)
		result += i;
	return result;
}
//전통적 방식

pulbic long sequentialSum(long n) {
	return Stream.iterate(1L, i -> i + 1)
		.limit(n)
		.reduce(0L, Long::sum);
}
//스트림 사용
```
n이 커질수록 병렬 연산의 효율성이 커질 것이다. 그렇다면 어떻게 해야 병렬 스트림으로 위 코드를 다시 작성할 수 있을까?  

#### 순차 스트림을 병렬 스트림으로 변환하기
간단하다. 순차 스트림에 parallel 메서드를 호출해 기존의 함수형 리듀싱 연산을 병렬로 처리할 수 있다.  
```java
public long parallel(long n) {
	return Stream.iterate(1L, i -> i + 1)
		.limit(n)
		.parallel()
		.reduce(0L, Long::sum);
}
```
parallel() 메서드가 스트림을 여러 청크로 분할해 리듀싱 연산에 전달한다. 리듀싱 연산은 각기 다른 청크 별로   
연산을 끝낸 뒤 이를 하나로 합쳐 반환한다.  
parallel()이 호출되면 스트림 내부적으로 연산을 병렬로 수행해야 함을 알리는 불리언 플래그가 설정된다.  
이에 반대되는 메서드도 있다. sequential()을 호출하면 병렬 스트림을 순차 스트림으로 바꿀 수 있다.  
두 연산을 활용해 어떤 연산을 병렬로 실행하고 어떤 연산을 순차로 실행할지 제어할 수 있다.  
```java
stream.parallel()
	.filter(...)
	.sequential()
	.map(...)
	.parallel()
	.reduce();
```

#### 효과적으로 사용하기
- 벤치마크로 직접 성능을 측정해라
- 박싱을 주의하라. 성능이 중요하다면 되도록 기본형 특화 스트림을 사용해라.  
- 무조건 병렬이 답은 아니다. limit이나 findFirst처럼 순서에 의존하는 연산은 병렬에서 오히려 비효울적이다.  
- 전체 파이프라인의 연산 비용을 고려해라.
- 자료구조의 병렬화 친밀도를 고려해라. 
- ArrayList, IntStream.range - 훌륭함
- HashSet, TreeSet - 좋음
- LinkedList, Stream.iterate - 나쁨
다음으로 병렬 스트림의 내부 구조를 잘 파악해야 잘 활용할 수 있다.  
병렬 스트림은 자바 7에 추가된 포크/조인 프레임워크로 처리된다.  
지금부터는 포크/조인 프레임워크를 살펴보자.  

## 포크/조인 프레임워크
포크/조인 프레임워크는 병렬화할 수 있는 작업을 재귀적으로 작은 작업으로 분할한다.  
이후 각각의 서브태스크를 합쳐 전체 결과를 만들도록 설계된다.  
포크/조인 프레임워크는 서브태스크를 스레드 풀(ForkJoinPool)의 작업자 스레드에 분산 할당하는  
ExecutorService 인터페이스를 구현한다.  

#### RecursiveTask 활용
스레드 풀을 이용하기 위해서 RecursiveTask\<R\>의 서브클래스를 만들어야 한다.  
R은 병렬화된 태스크의 결과 타입 또는 결과가 없을 때 RecursiveAction 형식이다.  
RecursiveTask를 정의하려면 추상 메서드 compute를 구현해야 한다.  
```java
protected abstract R compute();
```
compute()는 태스크를 서브태스크로 분할하는 로직과 더이상 분할할 수 없을 때 개별 서브태스크ㄹ의 결과를  
생산할 알고리즘을 정의한다. 일반적으로 이는 다음과 같다.  
if(태스크가 충분히 작거나 더이상 분할할 수 없으면) {  
	순차적으로 태스크 계산  
} else {  
	태스크를 서브태스크로 분할  
	이 전체 조건문을 재귀호출  
	모든 서브태스크 연산이 완료될 때까지 기다림  
	각 서브태스크의 결과를 합침  
}  
이는 분할 후 정복(Divide And Conquer)알고리즘의 병렬화 버전이다.  
포크/조인 프레임워크를 이용해 병렬 합계를 수행하는 예시 코드를 보자.  
```java
public class ForkJoinSumCalculator
extends java.util.concurrent.RecursiveTask<Long> { // RecursiveTask를 상속
	private final long[] numbers;
	private final int start;
	private final int end;
	public static final long THRESHOLD = 10_000; // 이 이하 서브태스크는 더 이상 분할할 수 없다.  

	public ForkJoinSumCalculator(long[] numbers) {
		this(numbers, 0, numbers.length);
	}
	private ForkJoinSumCalculator(long[] numbers, int start, int end) {
		this.numbers = numbers;
		this.start = start;
		this.end = end;
	}
	@Override
	protected Long compute() { // RecursiveTask의 추상 메서드 오버라이드
		int length = end - start; // 이 태스크에서 더할 배열의 길이
		if(length <= THRESHOLD) {
			return computeSequentially();
		}
		ForkJoinSumCalculator leftTask = 
			new ForkJoinSumCalculator(numbers, start, start + length / 2);
		leftTask.fork(); // ForkJoinPool의 다른 스레드로 새 태스크를 비동기로 실행한다.
		ForkJoinSumCalculator rightTask = 
			new ForkJoinSumCalculator(numbers, start + length / 2, end);
		Long rightResult = rightTask.compute();
		Long leftResult = leftTask.join(); // 첫 번째 서브태스크의 결과를 읽거나 결과가 없으면 기다린다. 
		return leftResult + rightResult;
	}
	
	private long computeSequentially() {
		long sum = 0;
		for(int i = start; i < end; i++) {
			sum += numbers[i];
		}
		return sum;
	}
}
```

## 정리
스트림의 병렬처리와 순차처리는 각각 parallel()과 sequential()로 가능하다.  
parallel()은 내부적으로 자바7에 추가된 포크/조인 프레임워크를 기반으로 동작한다.  
포크/조인은 큰 태스크를 최소 단위의 서브태스크로 분할해 각각 다른 스레드로 계산한 뒤 결과값을 합치는 형태로  
동작한다. 여기에는 분할 후 정복 알고리즘이 사용된다.  

## 개선
포크/조인 프레임워크와 Spliterator에 대해 자세히 보지 않았다. Spliterator는 탐색하려는 데이터를 포함하는  
스트림을 어떻게 병렬화할 것인지 정의하는 인터페이스라고 한다.  
그러나 프로젝트를 몇 차례 진행한다해도 스레드의 병렬처리를 고려해야 할만큼 메모리 사용량이 높은 프로젝트를 진행할 것 같지는 않다.  
병렬처리가 요구될 것 같다고 생각되면 parallel()로 처리한 뒤 순차 처리와 성능 비교를 해보고 parallel()로도 부족한 때가 오면 커스텀은 그때 봐도 될 것 같다. 


## 2장 이후 학습
사실상 Part2까지가 람다와 스트림에 관한 기본 내용이고 Part3부터는 활용과 개선에 대해 다룬다.  
디자인패턴과 애플리케이션의 코드가 예시로 등장하는 걸로 봐서 더 이상 프로젝트를 진행하지 않고 책만  
보는 건 큰 의미가 없을 것 같다. 현 단원의 Spliterator나 앞선 단원의 커스텀 처리 등도 실제 개발을 하지 않고서는  
당장의 필요성을 느끼기 힘든 부분이다. 2장을 끝냈으니 자바 스크립트 학습을 빨리 끝내고 실제 프로젝트를 진행  
하는 게 맞을 것 같다. 2장 이후부터는 앞장의 내용을 복습하며 책은 필요한 부분 위주로 보자.
