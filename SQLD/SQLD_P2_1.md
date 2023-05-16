<h1>Part2 - 1. SQL 기본</h1><br>

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

## SELECT
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
  

 

