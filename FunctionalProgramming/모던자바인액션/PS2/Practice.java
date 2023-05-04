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
