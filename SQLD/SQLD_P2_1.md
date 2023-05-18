<h1>Part2 - 1. SQL 기본</h1>(5/16, 5/17, 5/18 -ing)<br>

## 1절. 관계형 데이터베이스 개요
1970년 영국의 수학자 E.F.Codd 박사의 논문에서 처음으로 관계형 데이터베이스가 소개 -> 이후 상용화되며 파일 시스템과  
계층형, 망형 데이터베이스를 대체하며 주요 DB로 발전 -> 관계형 데이터베이스를 제어하기 위해 SQL 발전  
  
1. SQL(Structured Query Language)  
관계형 데이터베이스에서 데이터 정의, 조작, 제어를 위해 사용한다. 처음 배우기는 쉽지만 프로그램 성능 향상을 위한  
고급 SQL이나 튜닝은 어렵다. SQL은 시스템에 미치는 영향이 크므로 효과적인 활용(응답시간, 자원 활용 최소화)이 중요하다.    
  
분류.  
1. DML(데이터 조작어) - SELECT, INSERT, UPDATE, DELETE  
2. DDL(데이터 정의어) - CREATE, ALTER, DROP, RENAME  
3. DCL(데이터 제어어) - GRANT, REVOKE  
4. TCL(데이터 제어어) - COMMIT, ROLLBACK  
TCL을 DCL의 정의에 포함시키기도 한다.  
  
2. 표준 SQL  
SQL은 ANSI/ISO SQL 표준을 통해 발전해왔다. 대표적인 표준은 다음 내용을 포함한다.  
- STANDARD JOIN(CROSS, OUTER JOIN 등 새로운 FROM절 JOIN 기능들)   
- SCALAR SUBQUERY, TOP-N QUERY 등의 SUBQUERY 기능들  
- ROLLUP, CUBE, GROUPING SETS 등의 리포팅 기능  
- WINDOW FUNCTION 같은 개념 분석 기능들  
  
일반 집합 연산자(UNION, INTERSECT, EXCEPT(Oracle은 MINUS), CROSS JOIN과 순수 관계 연산자(SELECT, PROJECT, (NATURAL) JOIN, DIVIDE(현재 사용 X))도 있다.  
이와 같은 연산자들은 SQL 문장에서 직, 간접적으로 구현되어있다. SELECT는 WHERE절의 조건절 기능으로(SELECT 연산과 SELECT절의 의미가 다름에 유의), PROJECT는 SELECT절의 칼럼 선택 기능으로 구현했다.  
또 JOIN 연산은 WHERE 절의 INNER JOIN 조건과 함께 FROM절의 NATURAL JOIN, INNER JOIN, OUTER JOIN, USING 조건절, ON 조건절 등으로 발전했다.  
일반적으로 제3 정규형이나 보이스코드 정규형까지 진행하고 나면 엔터티가 상당부분 분해되는데 이 엔터티들이 주로 테이블이 된다.  
이렇게 흩어진 데이터를 연결해 원하는 데이터를 가져오는 작업이 JOIN이다. 관계형 데이터베이스에서 JOIN은 따라서 가장 중요한 기능 중 하나이다.  
  
3. 테이블  
관계형 데이터베이스의 기본 단위는 테이블이다.  
테이블은 특정 주제와 목적으로 만들어지는 일종의 집합이다. 테이블은 반드시 하나 이상의 칼럼을 가진다.  
자료를 입력하지 않아도 테이블은 원래 만들어졌던 속성을 유지하면서 존재한다.  
테이블은 쉽게 말하면 2차원 행렬이다. 엔터티와의 차이는 엔터티는 논리 모델의 데이터 단위이고 테이블은 물리 모델의 데이터  
단위라는 것이다. 엔터티에 인스턴스가 있듯이 테이블도 행과 열로 구성된 하나의 개체를 가지는데 이를 필드라고 한다.   
정규화, 기본키와 외래키는 모두 테이블을 단위로 진행한다.  
  
4. ERD  
E-R 다이어그램을 말한다.  
엔터티, 관계, 속성의 세 구성요소로 현실 세계의 데이터를 표현하는 방식이다.  
IE(Information Engineering)과 Barker(CaseMethod)를 ERD로도 표현할 수 있다.  
  
5. 데이터 유형(타입)  
CHAR(Character), VARCHAR(Character Varying)(Limit 개념), NUMERIC, DATETIME 등이 있다.  
CHAR은 사실상 최대 바이트를 제한하는 타입이라고 생각하자. 또 CHAR은 문자 끝 공백을 무시하지만 VARCHAR은 문자 끝 공백도  
문자로 해석하고 strcmp한다.  
추후에 ALTER 명령으로 속성의 타입을 변환할 수도 있긴 하지만 해당 도메인 값을 가지는 이전의 데이터를 일일이 변경해야 하므로  
사실상 초기에 제대로 지정해놔야 한다.  

## SELECT 문제
이하 SQL문은 여러 번 정리했으므로 문제풀이 + 복습으로 대체한다.  

```sql
SELECT FACTORY_ID, FACTORY_NAME, ADDRESS
FROM FOOD_FACTORY
WHERE ADDRESS LIKE '강원도%'
ORDER BY FACTORY_ID ASC
```
WHERE 속성 LIKE '문자열%'  
ORDER BY ASC, DESC  
문제  

```sql
SELECT BOOK_ID, DATE_FORMAT(PUBLISHED_DATE, '%Y-%m-%d') AS PUBLISHED_DATE
FROM BOOK
WHERE PUBLISHED_DATE LIKE '2021%'
AND CATEGORY LIKE '인문'
ORDER BY PUBLISHED_DATE ASC
```
DATE\_FORMAT(PUBLISHED\_DATE, '%Y-%m-%d') AS PUBLISHED\_DATE  
데이트 포맷 맞추기 문제

```sql
SELECT USER_ID, PRODUCT_ID
FROM ONLINE_SALE
GROUP BY USER_ID, PRODUCT_ID
HAVING COUNT(USER_ID) > 1
ORDER BY USER_ID ASC,
PRODUCT_ID DESC;
```
GROUP BY, HAVING 문제  
SELECT에서 집계함수를 선택하고 싶을 때 칼럼을 기준으로 정렬하는 GROUP BY를 많이 쓴다.  
HAVING 절에 조건을 둬 WHERE과 같이 사용할 수 있다.  
  
```sql
SELECT DATE_FORMAT(SALES_DATE, '%Y-%m-%d') AS SALES_DATE, 
 PRODUCT_ID, USER_ID, SALES_AMOUNT 
FROM ONLINE_SALE
WHERE SALES_DATE LIKE '2022-03%'

UNION ALL

SELECT DATE_FORMAT(SALES_DATE, '%Y-%m-%d') AS SALES_DATE, 
 PRODUCT_ID, NULL AS USER_ID, SALES_AMOUNT 
FROM OFFLINE_SALE
WHERE SALES_DATE LIKE '2022-03%'
ORDER BY SALES_DATE ASC,
PRODUCT_ID ASC,
USER_ID ASC
```
UNION ALL 문제
UNION ALL 이후 ORDER BY는 어느 테이블에서 써도 영향 없는 것 같음

```sql
SELECT NAME, DATETIME
FROM ANIMAL_INS
ORDER BY ANIMAL_ID DESC
```
단순 조회 문제

```sql
SELECT ANIMAL_ID, NAME
FROM ANIMAL_INS
WHERE INTAKE_CONDITION LIKE 'Sick'
```

```sql
SELECT ANIMAL_ID, NAME
FROM ANIMAL_INS
WHERE INTAKE_CONDITION NOT LIKE 'Aged'
```
NOT LIKE도 가능

```sql
SELECT ANIMAL_ID, NAME
FROM ANIMAL_INS
ORDER BY ANIMAL_ID
```

```sql
SELECT ANIMAL_ID, NAME, DATETIME
FROM ANIMAL_INS
ORDER BY NAME,
DATETIME DESC
```

```sql
SELECT NAME 
FROM ANIMAL_INS
ORDER BY DATETIME ASC
LIMIT 1
```
LIMIT은 맨 마지막에

```sql
SELECT COUNT(DISTINCT USER_ID) AS USERS
FROM USER_INFO
WHERE JOINED LIKE '2021%' 
AND AGE >= 20
AND AGE <= 29
```
2021년에 가입한 회원 중 나이가 20세 이상 29세 이하인 회원의 수 조회

## 집계함수 문제

```sql
SELECT MAX(PRICE) AS MAX_PRICE
FROM PRODUCT
```
MAX()

```sql
//정답
SELECT DATETIME
FROM ANIMAL_INS
GROUP BY DATETIME
ORDER BY DATETIME DESC
LIMIT 1

//정답
SELECT MAX(DATETIME)
FROM ANIMAL_INS

//오답
SELECT MAX(DATE_FORMAT(DATETIME, '%Y-%m-%d %T')
FROM ANIMAL_INS
```
DATE\_FORMAT 공백 + 시간 표기 오답 처리  
포멧 기본값으로 되어 있으면 건드리지 말자  
  
```sql
SELECT PRODUCT_ID, PRODUCT_NAME, PRODUCT_CD, CATEGORY, PRICE
FROM FOOD_PRODUCT
GROUP BY PRICE
ORDER BY PRICE DESC
LIMIT 1

SELECT PRODUCT_ID, PRODUCT_NAME, PRODUCT_CD, CATEGORY, PRICE
FROM FOOD_PRODUCT
GROUP BY PRICE
HAVING PRICE = (SELECT MAX(PRICE) FROM FOOD_PRODUCT)
```

```sql
SELECT COUNT(ANIMAL_ID)
FROM ANIMAL_INS
```

```sql
SELECT COUNT(DISTINCT(NAME))
FROM ANIMAL_INS
WHERE NAME IS NOT NULL
```

## GROUP BY 문제
```sql
SELECT CAR_ID, 
MAX(IF ('2022-10-16' BETWEEN START_DATE AND END_DATE
, '대여중', '대여 가능'))
AS AVAILABILITY
FROM CAR_RENTAL_COMPANY_RENTAL_HISTORY
GROUP BY CAR_ID
ORDER BY CAR_ID DESC
```
삼항 연산자처럼 if 사용 가능  
IF(조건식, 'true', 'false);
  
```sql
SELECT CATEGORY, 
MAX(PRICE) AS MAX_PRICE, 
(SELECT PRODUCT_NAME 
 FROM FOOD_PRODUCT
 WHERE CATEGORY = FP.CATEGORY
 ORDER BY PRICE DESC
 LIMIT 1) 
 AS PRODUCT_NAME
FROM FOOD_PRODUCT AS FP
WHERE CATEGORY IN ('과자', '국', '김치', '식용유')
GROUP BY CATEGORY
ORDER BY PRICE DESC
```
서브쿼리를 두지 않고 풀었다가 PRODUCT\_NAME이 PRICE와 일치하지 않아서 틀림  
카테고리로 분류된 이름 아래 PRICE가 분류돼서 맞은 줄 알았지만 MAX(PRICE)가 아닌 랜덤 PRICE에 대당하는 CATEGORY-PRODUCT\_NAME이었음  

```sql
# 틀림
#SELECT BK.AUTHOR_ID,  
#A.AUTHOR_NAME,
#BK.CATEGORY,
#(SUM(BS.SALES) * SUM(DISTINCT BK.PRICE)) AS TOTAL_SALES
#FROM BOOK AS BK
#JOIN AUTHOR AS A 
#ON BK.AUTHOR_ID = A.AUTHOR_ID
#JOIN BOOK_SALES AS BS
#ON BK.BOOK_ID = BS.BOOK_ID
#WHERE BS.SALES_DATE LIKE "2022-01%"
#GROUP BY BK.CATEGORY
#ORDER BY BK.AUTHOR_ID ASC,
#BK.CATEGORY DESC

# 테스트
# SELECT *
# FROM BOOK B
# JOIN BOOK_SALES BS
# ON B.BOOK_ID = BS.BOOK_ID
# WHERE BS.SALES_DATE LIKE "2022-01%"
# GROUP BY BS.BOOK_ID

# GROUP BY 이하 여러 절 사용 가능
SELECT BK.AUTHOR_ID,  
A.AUTHOR_NAME,
BK.CATEGORY,
SUM(BS.SALES * BK.PRICE) AS TOTAL_SALES
FROM BOOK AS BK
JOIN AUTHOR AS A 
ON BK.AUTHOR_ID = A.AUTHOR_ID
JOIN BOOK_SALES AS BS
ON BK.BOOK_ID = BS.BOOK_ID
WHERE BS.SALES_DATE LIKE "2022-01%"
GROUP BY BK.CATEGORY, A.AUTHOR_ID
ORDER BY BK.AUTHOR_ID ASC,
BK.CATEGORY DESC
```
GROUP BY에 여러 속성을 둘 수 있다는 걸 모르고 SELECT에 서브 쿼리를 둬 풀려고 시도  
-> SELECT 이하 서브 쿼리는 반드시 하나의 행만 반환해야 함  
이후 #테스트 이하 항목에서 테이블의 하나씩 테스트 해본 결과 같은 AUTHOR\_NAME, CATEGORY인 경우에도 가격이 다른 책이 있었다.  
(AUTHOR\_ID(1)-홍길동-경제-9000원, AUTHOR\_ID(1)-홍길동-경제-12000원) SUM과 GROUP BY CATEGORY만 이용해 풀 때 가격이 다른 두 책이 하나의 PRICE로만 반영됨.
SQL에서 어려운 부분  
-> 집계를 원할 때 GROUP BY 이하 요소나 조건절을 간과하면 원하는 속성 아래 의도하지 않은 값이 들어갈 수 있다.  
-> 의도하지 않은 값이 해당 필드의 값으로 잡혀도 출력에는 문제가 없다.   
엄청나게 삽질했지만 GROUP BY와 JOIN, 서브 쿼리에 대한 이해도가 높아졌다.  
  
```sql
SELECT I.INGREDIENT_TYPE, SUM(F.TOTAL_ORDER) AS TOTAL_ORDER
FROM FIRST_HALF F
JOIN ICECREAM_INFO I 
ON F.FLAVOR = I.FLAVOR
GROUP BY I.INGREDIENT_TYPE
ORDER BY F.TOTAL_ORDER ASC
```

```sql
SELECT MCDP_CD AS 진료과코드, COUNT(APNT_YMD) AS 5월예약건수
FROM APPOINTMENT
WHERE APNT_YMD LIKE '2022-05%'
GROUP BY MCDP_CD
ORDER BY COUNT(APNT_YMD) ASC,
MCDP_CD ASC
```

