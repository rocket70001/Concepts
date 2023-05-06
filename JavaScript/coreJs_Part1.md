# 코어 자바스크립트 Part1 - 데이터 타입

## 데이터 타입
타입은 크게 기본, 참조형으로 나뉨  
기본형은 불변형(식별자, 참조 데이터 주소 저장), 참조형(자료구조임, 식별자, 데이터 배열 주소 저장)  
  
자바스크립트는 변수에 값을 할당하면 변수명과 해당 값의 주소를 저장한다.  
중복성을 활용하기 위해 이런 식으로 설계되었다고 한다.    
또 이런 특성으로 문자열 변수를 선언하고 수정하면 해당 포인터가 가리키는 데이터를 수정하는 게 아니라  
새 문자열 데이터를 만들어 해당 데이터의 주소를 저장한다.  
사용하지 않는 데이터는 가비지 콜렉터에 의해 처리된다.  
  
변수가 식별자(변수명)과 포인터(데이터 주소)를 저장하는 줄 알았는데 챗 쥐피티는 데이터 주소를 참조하는 거지  
C의 포인터 개념과는 다르다고 한다.  
  
I can't understand this sentence "This reference is not a memory address, but rather a reference to the location in memory where the value is stored.". What is the thing that is not memory address but rather a reference to the location in memory. What is this concept called?
  
I apologize for the confusion in my previous message. In JavaScript, variables are references to values stored in memory, and these references can be thought of as memory addresses in a sense.

When I said that the reference is not a memory address, I meant that it is not a raw, numeric memory address like you might find in lower-level languages such as C or C++. In JavaScript, the reference is an abstract concept that refers to the location in memory where the value is stored, but the exact mechanism for how this reference is implemented is not specified by the language itself.

In other words, while the reference in JavaScript is conceptually similar to a memory address in other languages, it is not exposed as a raw number or pointer that can be manipulated directly by the programmer. Instead, the reference is an abstract concept that is used to represent the value stored in memory and can be accessed and manipulated using the language's built-in operators and functions.

I hope this clears up any confusion, and please let me know if you have any further questions.  
  
타 언어의 메모리 주소와는 비슷하지만 조금 더 추상적인 개념이라 C처럼 숫자 형태로 즉각적으로 노출되어서   
프로그래머가 조작 가능한 주소 개념은 아니라고 한다. 따라서 포인터 다루듯 하지는 못하고 내장된 연산자와 함수들로 기능을 다뤄야 한다.  
? 자바스크립트 변수가 저장하는 데이터 주소와 포인터가 가리키는 메모리 주소의 차이 ?  
  
#### 기본형 변수
자바스크립트의 변수는 불변값이다.  
불변값이라는 말은 한 식별자에 대한 데이터 할당에는 제한이 없지만 한 번 할당된 데이터 값 자체는 바꿀 수 없다는 말이다.  
이는 상수 개념과 다르다. 상수는 아예 할당 자체가 한 번 밖에 안 된다.  
따라서 자바스크립트에서 변수와 상수의 구분 기준은 데이터 재할당 여부이다.  

#### 참조형 변수
자바스크립트의 주소 개념은 Raw number로 조작 가능한 포인터와는 다르게 추상화되어서 내장 함수와 연산자로만 다룰 수 있는 무엇이라고 생각하되 기술에 있어서는 편의를 위해 포인터라고 하겠다.  
  
참조형은 구조체나 배열 등의 자료구조로 포인터 배열의 주소값을 가진다고 생각하자.  
여러 자료형의 주솟값을 가지는 변수이다.  
각 자료형은 다시 다른 주솟값을 가리킨다.  
이런 식으로 한 식별자가 가리키는 메모리 주소는 참조 카운트가 올라가게 된다.  
그러나 참조 카운터가 0이 되면 해당 메모리는 가비지 콜렉터에 의해 수거(collecting)되어 빈 공간이 된다.   

#### 기본형과 참조형의 가장 큰 차이
```javascript
var a = 10;
vat b = a;

var obj = {c:10, d:'aaa'};
var obj2 = obj;

b = 15;
obj.c = 15;
```
여기서 기본형 a와 b는 서로 다른 값을 참조하게 됨으로 a = 10,  b = 15가 되지만  
참조형 obj와 ob2는 obj.c = 15, obj2 = 15가 된다.  
위에서 참조형이 포인터 배열의 주솟값을 가진다고 했는데 연속된 포인터 배열의 주솟값 중 중간에 있는 어떤  
개별 주솟값만을 바꿀 수는 없다. 그렇기 때문에 참조형의 포인터 배열을 피상적으로 복제해 사본을 만들면  
사본의 프로퍼티 값을 바꿀 때 원본이 변경되고 원본의 프로퍼티 값을 바꿀 때 사본도 변경된다.  

## 얕은 복사와 깊은 복사
여러 언어에서 함수형 패러다임을 채택하면서 자바스크립트에서도 불변형 객체를 지원하고 있다.  
immutable.js나 baobab.js 등의 라이브러리등이 그렇다.  
다음 코드를 보자  
```javascript
var user = {
	name: 'Jaenam',
	urls: {
		protfolio: 'http://github.com/abc',
		blog: 'http://blog.com',
		facebook: 'http://facebook.com/abc'
	}
};
var user2 = copyObject(user); // copyObject는 user의 프로퍼티를 복사해 반환하는 객체생성 메서드  

user2.name = 'Jung';
console.log(user.name === user2.name); // false

user.urls.protfolio = 'http://protfolio.com';
console.log(user.urls.protfolio === user2.urls.protfolio); // true

user2.urls.blog = '';
console.log(user.urls.blog === user2.urls.blog); // true
```
위 코드는 중첩된 객체를 얕게 복사한다. 따라서 name 프로퍼티는 바꿀 때는 서로 다른 값을 참조하지만  
name 보다 깊은 urls은 같은 데이터를 참조한다. 그래서 원본 객체의 값을 바꾸면 사본 개체의 값도 바뀌고  
사본 객체의 값을 바꾸면 원본 객체의 값도 바뀐다.  
이런 현상이 발생하지 않게 하기 위해 변경을 원하지 않는 원본 객체는 불변 객체로 만들 필요가 있다.  
```javascript
var user2 = copyObject(user);
user2.urls = copyObject(user.urls);

user.urls.protfolio = 'http://protfolio.com';
console.log(user.urls.protfolio === user2.urls.protfolio); // false

user2.urls.blog = '';
console.log(user.urls.blog === user2.urls.blog); // false
```
위 코드는 깊은 복사를 한다.  
값이 기본형일 경우 그대로 복사하면 되지만 참조형 데이터는 다시 그 내부의 프로퍼티를 복사해야 깊은 복사가 가능하다.  
따라서 깊은 복사를 하기 위해서는 객체 내부에 참조형 데이터가 있을 때마다 다시 그 데이터를 복사하는 재귀함수가  
수행되어야 한다.  
이런 개념을 바탕으로 copyObject를 깊은 복사 방식으로 작성하면 다음과 같다.  
```javascript
var copyObjectDeep = function(target) {
	var result = {};
	if(typeof target === 'object' && target !== null) {
		for(var prop in target) {
			result[prop] = copyObjectDeep(target[prop]);
		}
	}
	else {
		result = target;
	}
	return result;
};
```
target이 객체이면 다시 함수로 반환하고 객체가 아닌 프로퍼티면 그대로 result에 넣어주는 식으로 복사한다.  
이렇게 복사하면 원본과 사본이 서로 완전히 다른 객체를 참조하게 된다.  

JSON을 활용해 깊은 복사를 할 수도 있지만 아직 JSON은 안 배웠으므로 일단은 스킵한다.  

## undefined와 null
undefined를 반환하는 세 가지 경우  
1. 값없는 식별자에 접근할 때
2. 객체 내부에 존재하지 않는 프로퍼티에 접근할 때
3. return 문이 없거나 호출되지 않는 함수 실행 결과

undefined는 혼란을 빚을 수 있는데 이는 위 세 가지 경우에 undefined가 할당되기도 하지만  
사용자가 값이 없음을 표현할 때 직접 undefined를 할당할 수도 있기 때문이다.  
그럴 경우 값이 없어서 할당된 undefined와 사용자가 임의로 지정한 undefined가 섞여서 혼란스럽다.  
비어있음을 명시적으로 표현하기 위해서는 null을 사용하자.  
다만 typeof null이 object로 반환되는 자바스크립트 자체의 버그가 있음에 주의하자.  
어떤 변수가 null인지 확인하기 위해서는 typeof가 아닌 다른 방식으로 접근해야 한다.  
일치 연산자를 사용하자
```javascript
var n = null;
console.log(typeof n) // object

console.log(n == undefined) // true
console.log(n == null) // true

console.log(n === undefined) // false
console.log(n === null) // true
```

## 정리
자바스크립트의 데이터 타입에는 크게 기본형과 참조형이 있다.  
기본형은 불변값이고 참조형은 가변값이다.  
var a;시 a에 undefined가 할당된다.  
이후 a = 10; 과 같이 데이터를 할당하면 별도의 공간에 10을 저장한 뒤 10의 주소를 a에 할당한다.  
  
자바스크립트 진영에서 불변객체는 가장 중요한 개념 중 하나이다.  
참조형 데이터의 경우 얕은 복사와 깊은 복사로 나뉜다.  
얕은 복사는 포인터 배열의 주솟값을 참조해 객체를 생성해 사본과 원본에서 중복 프로퍼티가 생길 수 있다.  
깊은 복사는 재귀적으로 프로퍼티를 일일이 복사한 뒤 객체를 생성해 객체의 독립성을 보장한다.  
  
없음에 대한 표현은 디폴트값이 undefined, 사용자가 명시적으로 지정할 때는 null이다.  
사용자가 undefined로 없음을 표현할 수도 있지만 디폴트 undefined와 섞여 헷갈림으로 지양하자.  

## 개선
C의 포인터 개념과 자바의 변수가 참조하는 메모리의 주소가 정확히 어떤 차이를 가지는지 모르겠다.  
얕은 복사를 원본 객체와 사본 객체가 같은 포인터 배열을 참조한다.  
깊은 복사를 사본 객체가 프로퍼티의 개별 포인터를 일일이 참조해 프로퍼티 독립성을 보장한다.  
로 이해했는데 정확한 이해인 줄은 모르겠다.  
일치 연산자(===)와 동등 연산자(==)의 차이  
일치 연산자는 피연산자의 값과 타입을 모두 비교하는데 반해 동등 연산자는 값만 비교한다.  


